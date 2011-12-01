// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Restricts file based on suffix
 * @author amorton
 */
public class ValidFileTypeFilter extends AbstractFilter
{

    private List m_validTypes = new ArrayList();

    public void setValidTypes(List m_validSuffix)
    {
        this.m_validTypes = m_validSuffix;
    }




    @Override
    public Map processFilter(Map mapOfFiles)
    {
        Map resultantFiles = new HashMap();
        Iterator it = mapOfFiles.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
            String filename = file.getFilename();
            String[] parts = filename.split("\\.");
            if(parts.length == 1) m_logger.info("File ["+filename+"] Has no file type");
            else m_logger.info("File ["+filename+"] has filetype of ["+parts[parts.length-1]+"]");
            String suffix = parts[parts.length-1];
            if(m_validTypes.contains(suffix))
            {
                m_logger.info("File ["+filename+"] is valid file and will be returned");
                resultantFiles.put(fileKey, file);
            }
            else m_logger.info("File ["+filename+"] is invalid.");
        }
        return resultantFiles;
    }

    @Override
    public String getName()
    {
        return "ValidFileFilter";
    }

}
