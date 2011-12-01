// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The dropbox retriever uses the components that are setup to
 * connect to interfaces and put them in the dropbox folder.
 * Simples.
 * @author amorton
 */
public class DropBoxRetriever
{

    List m_components;
    private static final Log m_logger = LogFactory.getLog(DropBoxRetriever.class);

    public void setComponents(List m_components)
    {
        this.m_components = m_components;
    }

    /*
     * Main entry point - calls each component in order.
     */
    public void process()
    {
        m_logger.info("Started Dropbox Retriever");
        //Look at the list of components and process each.
        if(m_components != null && !m_components.isEmpty())
        {
            Iterator it = m_components.iterator();
            DropBoxRetrieverComponent component = null;
            while(it.hasNext())
            {
                try
                {
                    component = (DropBoxRetrieverComponent) it.next();
                    component.process();
                }
                catch (Exception ex)
                {
                    if(component != null)
                        m_logger.error("Unable to call component ["+component.getName()+"]",ex);
                    else
                        m_logger.error("Unable to call unknown component");
                }
            }
        }
        else m_logger.info("No components specified");

        //Just log a finish
        m_logger.info("Finished Dropbox Retriever");
    }


}
