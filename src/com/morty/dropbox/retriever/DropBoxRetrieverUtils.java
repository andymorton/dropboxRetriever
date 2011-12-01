// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for dropbox retriever.
 * @author amorton
 */
public class DropBoxRetrieverUtils
{
    private static final Log m_logger = LogFactory.getLog(DropBoxRetriever.class);


    /*
     * Print out keys and values from the map.
     */
    public static void printProperties(Map props)
    {
        if(m_logger.isDebugEnabled())
        {
            m_logger.debug("All props returned as::");
            Set keys = props.keySet();
            Iterator it = keys.iterator();
            while(it.hasNext())
            {
                String key = (String) it.next();
                m_logger.debug("Key ["+key+"] has value ["+props.get(key).toString()+"]");
            }
            m_logger.debug("All props Finished");
        }
    }
    
    
    
    
    /*
     * Print out list of files.
     */
    public static void printFileList(Map fileList)
    {
        if(m_logger.isDebugEnabled())
        {
            m_logger.debug("File List:");
            Set keys = fileList.keySet();
            Iterator it = keys.iterator();
            while(it.hasNext())
            {
                String key = (String) it.next();
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) fileList.get(key);
                if(key == null)
                    m_logger.error("Key is NULL!!!! PANIC!!");
                else if( file == null)
                    m_logger.error("File is NULL!!!! PANIC!!");
                
                m_logger.debug("FileKey ["+key+"] has value ["+file.getFilename()+"]");
            }
            m_logger.debug("End file List");
        }
    }

    /*
     * Converts a file to a byte array!
     * (not just a clever name!)
     */
    public static byte[] toByteArray(File file) throws Exception
    {
        int length = (int) file.length();
        byte[] array = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        while (offset < length) {
            int count = in.read(array, offset, (length - offset));
            offset += length;
        }
        in.close();
        return array;
    }


}
