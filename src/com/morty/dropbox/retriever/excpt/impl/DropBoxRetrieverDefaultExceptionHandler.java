// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.excpt.impl;

import com.morty.dropbox.retriever.excpt.DropBoxRetrieverExceptionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default - log and throw
 * @author amorton
 */
public class DropBoxRetrieverDefaultExceptionHandler implements DropBoxRetrieverExceptionHandler
{
    protected static final Log m_logger = LogFactory.getLog(DropBoxRetrieverDefaultExceptionHandler.class);

    @Override
    public void handleException(Exception e) throws Exception
    {
        m_logger.error("Exception caught",e);
        throw e;
    }

}
