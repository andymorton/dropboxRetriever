// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This takes each specified filter and passes the map through in sequence.
 * @author amorton
 */
public class FilterProcessor
{

    protected static final Log m_logger = LogFactory.getLog(FilterProcessor.class);
    private List m_filters;

    //Standard constructor
    public FilterProcessor()
    {

    }

    //Create with filters...
    public FilterProcessor(List filters)
    {
        super();
        if(filters == null)
        {
            m_filters = new ArrayList();
        }
        else
            m_filters = filters;

        m_logger.info("Filters Defined as ["+m_filters+"]");
    }


    public Map processFilters(Map mapOfFiles)
    {
        m_logger.info("Starting Filter Process");
        //Pass the resultant map onto the next, and if debug, print it out.
        Iterator it = m_filters.iterator();
        while(it.hasNext())
        {
            FileFilter ff = (FileFilter) it.next();
            mapOfFiles = ff.process(mapOfFiles);
        }
        m_logger.info("Finished Filter Process");
        return mapOfFiles;

    }


}
