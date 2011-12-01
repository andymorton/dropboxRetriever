// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

/**
 * Convert from one doc type to another!
 * Uses the jod convertor and an open office instance!
 * @author amorton
 */
public class PdfDocumentConversionFilter extends AbstractFilter
{
    
    private String m_openOfficeHome;
    
    public void setOfficeHome(String home)
    {
        this.m_openOfficeHome = home;
    }
    
    private String m_workingDirectory;
    
    public void setWorkingDirectory(String wd)
    {
        this.m_workingDirectory = wd;
    }
    
    @Override
    public Map processFilter(Map mapOfFiles)
    {
        Map resultantFiles = new HashMap();
        try
        {
            m_logger.info("Starting.");
            //Start the oo process to allow conversion!
             OfficeManager officeManager = new DefaultOfficeManagerConfiguration()
              .setOfficeHome(m_openOfficeHome)
              .buildOfficeManager();
            m_logger.info("About to start office manager.");
            officeManager.start();

            m_logger.info("Processing files");
            Iterator it = mapOfFiles.keySet().iterator();

            while(it.hasNext())
            {
                String fileKey = (String) it.next();
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
                String filename = file.getFilename();

                //Write out the file to disk, convert and load it up again!
                File fileToWrite = new File(m_workingDirectory+File.separator+filename);
                FileUtils.writeByteArrayToFile(fileToWrite, file.getFileData());
                OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
                String newFile = m_workingDirectory+File.separator+file.getFileNameWithoutSuffix()+".pdf";
                converter.convert(fileToWrite, new File(newFile) );

                //Load the file up now and place in data...
                byte[] data = FileUtils.readFileToByteArray(new File(newFile));
                DropBoxRetrieverFile returnFile = new DropBoxRetrieverFile(file.getFileNameWithoutSuffix()+".pdf", data);
                returnFile.setProperties(file.getProperties());        
                resultantFiles.put(fileKey, returnFile);
                
            }

            //finally stop the manager.
            officeManager.stop();
        }
        catch(Exception e)
        {
            m_logger.error("Problem in filter",e);
        }
        finally
        {
            return resultantFiles;
        }
            
    }

    @Override
    public String getName()
    {
        return "PdfDocumentConversion";
    }

}
