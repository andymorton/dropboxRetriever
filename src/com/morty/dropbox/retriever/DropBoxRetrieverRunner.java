// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Gets the dropbox retriever with anything that is required from the spring
 * and runs it.
 *
 * @author amorton
 */
public class DropBoxRetrieverRunner
{

    private static final Log m_logger = LogFactory.getLog(DropBoxRetrieverRunner.class);

    /**
     * This is the main runner for this application.
     * It takes the parameter of the spring context file to use,
     * then looks up the generator (which should be populated with the right
     * parameter values and in the classpath) and runs it.
     * @param spring context file to use
     */
    public static void main(String[] args)
    {
        try
        {
            //Get the spring bean, and run
            m_logger.info("Starting Dropbox Retriever");
            String springFile = args[0];
            m_logger.info("Using Spring File ["+springFile+"]");

            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(springFile);
            final DropBoxRetriever dbr = (DropBoxRetriever) ctx.getBean(DropBoxRetrieverConstants.DROPBOX_RETRIEVER);

            try
            {
                dbr.process();
            }
            catch(Exception procEx)
            {
                m_logger.error("Unable to process the files....",procEx);
            }
        }
        catch(Exception e)
        {
            m_logger.error("Unable to get bean from spring context. Please confirm file and bean details.",e);
        }

    }


}
