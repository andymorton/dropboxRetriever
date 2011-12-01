// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.excpt;

/**
 * Default Exception Handler.
 * Will do the same thing for everything. just log and throw...(maybe!)
 * @author amorton
 */
public interface DropBoxRetrieverExceptionHandler
{
    public void handleException(Exception e) throws Exception;  
}
