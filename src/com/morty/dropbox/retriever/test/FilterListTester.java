// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.test;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.FileFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Takes a filter name, looks it up and then runs it against a file
 * @author FMBC AV
 */
public class FilterListTester 
{

    protected static final Log m_logger = LogFactory.getLog(FilterListTester.class);
    public static void main(String[] args)
    {
        String contextFile = args[0];
        String filter = args[1];
        String fileToProcess = args[2];
        String outputDir = args[3];
                
        try
        {
            //Load file into map.
            Map fileMap = new HashMap();
            File fileref = new File(fileToProcess);
            
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
                    fileMap.put("TestFile"+fileInDir.getName(), file);
                }
                
            }
            else
            {
                byte[] data = FileUtils.readFileToByteArray(fileref);
                DropBoxRetrieverFile file = new DropBoxRetrieverFile(fileref.getName(), data);
                fileMap.put("TestFile", file);
            }

            //Load context
            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(contextFile);
            final List ff = (List) ctx.getBean(filter);

            Iterator itf = ff.iterator();
            
            while(itf.hasNext())
            {
                FileFilter ffs = (FileFilter) itf.next();
                fileMap = ffs.process(fileMap);
            }
            
            //Run the filter
            Map returnFile = fileMap;
            
            //Output the result.
            m_logger.info("Outputting files");
            
            File outputDirectory = new File(outputDir);
            if(!outputDirectory.exists())
                outputDirectory.mkdir();
            
            Iterator it = returnFile.keySet().iterator();
            while(it.hasNext())
            {
                String fileKey = (String) it.next();
                DropBoxRetrieverFile rfile = (DropBoxRetrieverFile) returnFile.get(fileKey);
                String filename = rfile.getFilename();
                File fileToWrite = new File(outputDir+File.separator+filename);
                FileUtils.writeByteArrayToFile(fileToWrite, rfile.getFileData());
                m_logger.info("File outputted as ["+filename+"] to ["+outputDir+"]");
            }
            
            m_logger.info("Test Finished");
        }
        catch(Exception e)
        {
            m_logger.error("Filter test failure ["+e.getMessage()+"]",e);
        }
        
    }
    
}
