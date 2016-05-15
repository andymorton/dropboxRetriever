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
     * Add an optional repeat function, so that it will continually loop.
     * Components can disable themselves, so they can be configured to run once.
     * 
     */
    private boolean m_repeat = false;
    public void setRepeat(boolean rep)
    {
        m_repeat = rep;
    }
    
    
    /*
     * Because of the repeatable function, we set a sleep, so that 
     * a) we dont have to rely on Windows Scheduler
     * b) We can control the amount of running.
     * 
     * Default is 1 sec. (this is in milli seconds)
     * 
     */
    private long m_delay = 1000;
    public void setDelay(long delay)
    {
        m_delay = delay;
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
            boolean runOnce = true;
            while(runOnce || m_repeat)
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
                if(m_repeat) 
                {
                    try
                    {
                        Thread.sleep(m_delay);    
                    } 
                    catch (InterruptedException ex)
                    {
                        m_logger.error("Unable to sleep thread.",ex);
                    }
                }
                
                //If we are not repeating, this will allow the stop
                //If we are repeating the other variable overrides the logic gate.
                runOnce= false;
            }
        }
        else m_logger.info("No components specified");

        //Just log a finish
        m_logger.info("Finished Dropbox Retriever");
    }


}
