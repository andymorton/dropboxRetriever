// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.util.Iterator;
import java.util.Map;

/**
 * Just a simple pass through - essentially logs.
 * @author amorton
 */
public class SimplePassThroughFilter extends AbstractFilter
{

    @Override
    public Map processFilter(Map mapOfFiles)
    {

        Iterator it = mapOfFiles.keySet().iterator();

        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
            m_logger.info("File ["+file.getFilename()+"] is passing through.");
        }
        return mapOfFiles;
    }

    @Override
    public String getName()
    {
        return "SimplePassThrough";
    }


}
