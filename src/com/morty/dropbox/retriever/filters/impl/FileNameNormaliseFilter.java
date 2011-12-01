// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * This will remove spaces from the filenames
 * @author amorton
 */
public class FileNameNormaliseFilter extends AbstractFilter
{

    @Override
    public Map processFilter(Map mapOfFiles) 
    {
        Map resultantFiles = new HashMap();
        try
        {
            m_logger.info("Starting.");
           
            m_logger.info("Processing files");
            Iterator it = mapOfFiles.keySet().iterator();

            while(it.hasNext())
            {
                String fileKey = (String) it.next();
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
                String filename = file.getFilename();

                file.setFilename(StringUtils.replace(filename, " ", ""));
               
               //return it.
               resultantFiles.put(fileKey, file);
                
            }

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
    public String getName() {
        return "FileNameNormaliser";
    }
    
}
