// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Filters are the way that we allow for external calls or anything else.
 * The filters are spring loaded.
 * Passed in as list.
 * Takes a map of filename->bytes
 * Matches the filename pattern - if not, just return it
 * Then does something with it.
 * Spits out the hashmap with the resultant files, ready to go to the next filter
 *
 * Clever,eh?
 *
 * @author amorton
 */
public abstract class AbstractFilter implements FileFilter
{

    protected final Log m_logger = LogFactory.getLog(getClass());
    public abstract Map processFilter(Map mapOfFiles);
    @Override
    public abstract String getName();
    
    //File pattern that can be used to include or exclude files.
    protected String m_filePattern;
    protected Pattern m_regexPattern;
    
    public void setFilePattern(String pattern)
    {
        m_filePattern = pattern;
        m_regexPattern = Pattern.compile(m_filePattern);
    }        
           
    
    //This calls the processMethod.
    public Map process(Map mapOfFiles)
    {
        if(m_filePattern == null || m_filePattern.equals(""))
            return processFilter(mapOfFiles);
        else
        {
            //Remove the files that dont match the pattern!
            HashMap limitedFiles = new HashMap();
            HashMap ignoredFiles = new HashMap();
            limitedFiles.putAll(mapOfFiles);
            Set keys = mapOfFiles.keySet();
            Iterator it = keys.iterator();
            while(it.hasNext())
            {
                String fileKey = (String) it.next();
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
                Matcher matcher = m_regexPattern.matcher(file.getFilename());
                if(!matcher.matches())
                {
                    m_logger.info("File ["+file.getFilename()+"] does not match the filter pattern. Will be ignored");
                    limitedFiles.remove(fileKey);
                    ignoredFiles.put(fileKey, file);
                }
                
            }
            
            
            //Process them all, and then add in the ignored ones.
            Map resultantFiles = processFilter(limitedFiles);
            resultantFiles.putAll(ignoredFiles);
            return resultantFiles;
            
            
        }
    }
    
    

}
