// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.file;

import java.util.HashMap;
import java.util.Map;

/**
 * This allows a file and a hashmap of properties to be stored.
 * @author amorton
 */
public class DropBoxRetrieverFile 
{
    private String m_filename;
    private byte[] m_fileData;
    private Map m_properties = new HashMap();

    public DropBoxRetrieverFile(String name,byte[] data)
    {
        m_fileData = data;
        m_filename = name;
    }
    
    public void setFilename(String name)
    {
        this.m_filename = name;
    }
    
    public String getFilename()
    {
        return m_filename;
    }
            
    
    public byte[] getFileData() {
        return m_fileData;
    }

    public void setFileData(byte[] m_fileData) {
        this.m_fileData = m_fileData;
    }

    public Map getProperties() {
        return m_properties;
    }

    public void setProperties(Map m_properties) {
        this.m_properties = m_properties;
    }
    
    public boolean hasProperty(String key)
    {
        return m_properties.containsKey(key);
    }
            
    public Object getProperty(String key)
    {
        if(m_properties.containsKey(key)) return m_properties.get(key);
        else return "";
    }
    
    public String getFileNameWithoutSuffix()
    {
        //look for last .
        int index = this.m_filename.lastIndexOf('.');
        if (index >0&& index <= m_filename.length() - 2 ) 
        return m_filename.substring(0, index);
        else return m_filename;
    }
    

}
