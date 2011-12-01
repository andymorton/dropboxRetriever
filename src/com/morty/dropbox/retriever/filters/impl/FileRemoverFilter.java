// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This removes the files from the file map.
 *
 * @author amorton
 */
public class FileRemoverFilter extends AbstractFilter
{

    private String m_validFilePattern;

    public void setValidTypes(String pattern)
    {
        m_validFilePattern = pattern;
    }

    @Override
    public Map processFilter(Map mapOfFiles)
    {
        Pattern pattern = Pattern.compile(m_filePattern);
        Map resultantFiles = new HashMap();
        Iterator it = mapOfFiles.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
            String filename = file.getFilename();
            Matcher matcher = pattern.matcher(filename);
            if(matcher.matches())
            {
                m_logger.info("File ["+filename+"] is valid file and will be returned");
                resultantFiles.put(filename, mapOfFiles.get(filename));
            }
            else m_logger.info("File ["+filename+"] has been removed.");
        }
        return resultantFiles;
    }

    @Override
    public String getName()
    {
        return "FileRemover";
    }

}
