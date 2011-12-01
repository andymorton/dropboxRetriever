// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.impl;

import com.morty.dropbox.retriever.DropBoxRetrieverComponent;
import com.morty.dropbox.retriever.DropBoxRetrieverUtils;
import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * This is a file component, which basically means it loads up all files in a 
 * directory or a single file, 
 * @author amorton
 */
public class DropBoxRetrieverFileComponent extends DropBoxRetrieverComponent 
{
    
    private String m_targetFile;
    
    public void setTargetFile(String tf)
    {
        m_targetFile = tf;
    }

    @Override
    public String getName() {
        return "FileRetrievalComponent";
    }

    @Override
    protected Map processComponent() throws Exception 
    {
        Map returnFiles = new HashMap();
        
        File fileref = new File(m_targetFile);
            
        if(!fileref.exists())
            throw new Exception("File/Directory does not exist...");

        if(fileref.isDirectory()) 
        {
            File[] filesToProcess = fileref.listFiles();
            for(int i=0; i<filesToProcess.length; i++)
            {
                File fileInDir = filesToProcess[i];
                byte[] data = FileUtils.readFileToByteArray(fileInDir);
                DropBoxRetrieverFile file = new DropBoxRetrieverFile(fileInDir.getName(), data);
                returnFiles.put("File_"+fileInDir.getName(), file);
            }

        }
        else
        {
            byte[] data = FileUtils.readFileToByteArray(fileref);
            DropBoxRetrieverFile file = new DropBoxRetrieverFile(fileref.getName(), data);
            returnFiles.put("File_", file);
        }
        
        return returnFiles;
    }

    @Override
    protected void deliverFiles(Map mapOfFiles) throws Exception 
    {
    
         m_logger.info("Deliver files....");
        DropBoxRetrieverUtils.printFileList(mapOfFiles);
        
        Iterator it = mapOfFiles.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
            
            //Print out the props of the file.
            DropBoxRetrieverUtils.printProperties(file.getProperties());
            
            m_logger.info("Delivering ["+file.getFilename()+"]....");
            
            //Add the directory from the subject... 
            String destination = m_dropboxFolder+File.separator+file.getFilename();
           
            m_logger.info("File ["+destination+"] being written");
            FileUtils.writeByteArrayToFile(new File(destination), file.getFileData());        
        }
        
        
        m_logger.info("Files Delivered....");
        
    }
    
}
