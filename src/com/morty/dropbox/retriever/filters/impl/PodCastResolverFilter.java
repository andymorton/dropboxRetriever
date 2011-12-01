// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import com.morty.podcast.writer.file.PodCastFileNameResolver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This one ensures that a file matches one of the formats...
 * @author amorton
 */
public class PodCastResolverFilter extends AbstractFilter
{
    //FilenameResolver
    private PodCastFileNameResolver m_resolver = new PodCastFileNameResolver();

    public void setResolver(PodCastFileNameResolver m_resolver)
    {
        this.m_resolver = m_resolver;
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
            if(m_resolver.fileIsResolvable(filename))
                resultantFiles.put(fileKey, file);
            else
                m_logger.info("File ["+filename+"] is not resolvable - removing.");
        }
        return resultantFiles;
    }

    @Override
    public String getName()
    {
        return "PodCastResolver";
    }





}
