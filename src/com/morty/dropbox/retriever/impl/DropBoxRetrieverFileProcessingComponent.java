// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.impl;

import com.morty.dropbox.retriever.DropBoxRetrieverComponent;
import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * This is a file component, which basically means it loads up all files in a 
 * directory or a single file, 
 * @author amorton
 */

public class DropBoxRetrieverFileProcessingComponent extends DropBoxRetrieverComponent 
{
    public DropBoxRetrieverFileProcessingComponent()
    {
        //Dont deliver files.
        m_deliver = false;
    }
    
    private String m_targetFile;
    
    public void setTargetFile(String tf)
    {
        m_targetFile = tf;
    }

    @Override
    public String getName() {
        return "FileProcessingComponent";
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
                returnFiles.put(fileInDir.getName(), file);
            }

        }
        else
        {
            byte[] data = FileUtils.readFileToByteArray(fileref);
            DropBoxRetrieverFile file = new DropBoxRetrieverFile(fileref.getName(), data);
            returnFiles.put(fileref.getName(), file);
        }
        
        return returnFiles;
    }

    @Override
    protected void deliverFiles(Map mapOfFiles) throws Exception 
    {
        
        //We dont, we just process...
        return;
    }
    
}
