// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.filters;

import java.util.Map;

/**
 * Interface for file filters - everything must have these..
 * @author amorton
 */
public interface FileFilter
{
    //Main hook
    public Map process(Map mapOfFiles);

    //So we can tell what it is.
    public String getName();
    
}
