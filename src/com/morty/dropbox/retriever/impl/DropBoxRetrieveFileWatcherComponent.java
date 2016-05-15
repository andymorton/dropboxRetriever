package com.morty.dropbox.retriever.impl;

import com.morty.dropbox.retriever.DropBoxRetrieverComponent;
import java.util.Map;

/**
 * 
 * @author amorton
 */
public class DropBoxRetrieveFileWatcherComponent extends DropBoxRetrieverComponent 
{
    
    /*
     * This bad boy is a whole thread in itself...
     * Therefore, upon calling process(), it will return to the main thread initially
     * and then the Watcher will fire and continually poll.
     */
    
    
    @Override
    public void process() throws Exception
    {
        //get the initial files
        Map files = processComponent();

        if(files.isEmpty())
        {
            m_logger.info("No files obtained from component");
            return;
        }
        else m_masterFiles = files;

        //save the files
        saveFiles(files);
        //filter the files
        files = filterFiles(files);
        //finally delivery
        deliverFiles(files);
        //cleanup
        cleanup();
        //Printout the files that have been removed
        printReport(files);
        
        
    }
    

    @Override
    public String getName()
    {
        return "FileWatcherComponent";
    }

    @Override
    protected Map processComponent() throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void deliverFiles(Map mapOfFiles) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
