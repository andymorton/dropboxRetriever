// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever;

import com.morty.dropbox.retriever.excpt.DropBoxRetrieverExceptionHandler;
import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.FilterProcessor;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Component to allow a lot of base stuff to be put in here.
 * Things that are common to all components should be stuffed in here.
 * @author amorton
 */
public abstract class DropBoxRetrieverComponent
{

    //Specify the folder inside the dropbox folder to use as root.
    protected String m_dropboxFolder;

    //Specify the filters on this component
    protected List m_filters;

    //Specify the way that exceptions are handled.
    protected DropBoxRetrieverExceptionHandler m_exceptionHandler;

    //Used for temp storage - its cleared out at the end of every run.
    private String m_workingDirectory;

    //Each component has a name...
    public abstract String getName();

    //Keep record of files retrieved
    protected Map m_masterFiles = new HashMap();


    //Standard logger.
    protected static final Log m_logger = LogFactory.getLog(DropBoxRetrieverComponent.class);

    //Setters and getters
    public String getDropboxFolder()
    {
        return m_dropboxFolder;
    }

    public void setDropboxFolder(String m_dropboxFolder)
    {
        this.m_dropboxFolder = m_dropboxFolder;
    }

    public List getFilters()
    {
        return m_filters;
    }

    public void setFilters(List filters)
    {
        this.m_filters = filters;
    }

    public void setExceptionHandler(DropBoxRetrieverExceptionHandler eh)
    {
        this.m_exceptionHandler = eh;
    }

    public void setWorkingDirectory(String m_workingDirectory)
    {
        this.m_workingDirectory = m_workingDirectory;
    }



    /*
     * The processComponent() should return the files in byte array with the name
     * as the key in a hashmap
     * Save files then saves them locally for safe keeping
     * filter will process them based on the config.
     * deliver finally writes it to the endpoint (which can be anywhere!)
     * cleanup will clean the working Directory of ALL files.
     * Not just the ones it delivers. it must remove temp files as well.
     *
     */
    public void process() throws Exception
    {
        //get the initial files
        Map files = processComponent();

        if(files.isEmpty())
        {
            m_logger.info("No files obtained from component");
            return;
        }
        else m_masterFiles = files;

        //save the files
        saveFiles(files);
        //filter the files
        files = filterFiles(files);
        //finally delivery
        deliverFiles(files);
        //cleanup
        cleanup();
        //Printout the files that have been removed
        printReport(files);
        
        
    }

    //Do the initial file retrieval
    protected abstract Map processComponent() throws Exception;

    //Deliver the files to the file system.
    protected abstract void deliverFiles(Map mapOfFiles) throws Exception;

    //Cleaup the working directory!
    protected void cleanup() throws Exception
    {
        FileUtils.cleanDirectory(new File(m_workingDirectory));
    }


    //Go through the list of filters
    protected Map filterFiles(Map mapOfFiles)
    {
        //Pass everything to the FilterProcessor.
        FilterProcessor fp = new FilterProcessor(m_filters);
        return fp.processFilters(mapOfFiles);
    }


    /*
     * Saves the files in working directory.
     */
    protected void saveFiles(Map files) throws Exception
    {
        if(m_workingDirectory == null || m_workingDirectory.equals(""))
            throw new Exception("Working Directory is not set!");
        
        m_logger.info("Saving files in working directory");
        Iterator it = files.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) files.get(fileKey);
            String filename = file.getFilename();
            File fileToWrite = new File(m_workingDirectory+File.separator+filename);
            FileUtils.writeByteArrayToFile(fileToWrite, file.getFileData());
        }
        m_logger.info("Files saved in working directory");

    }

    /*
     * Prints out which files were removed from the filter...
     */
    private void printReport(Map files)
    {
        m_logger.info("======================================================");
        m_logger.info("Files removed by filters");
        Iterator it = m_masterFiles.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            if(!files.containsKey(fileKey))
            {
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) m_masterFiles.get(fileKey);
                m_logger.info("File ["+file.getFilename()+"] has been removed by the filters");
            }
        }
        m_logger.info("======================================================");
    }


}
