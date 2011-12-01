// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.impl;

import com.morty.dropbox.retriever.excpt.DropBoxRetrieverEmailException;
import com.morty.dropbox.retriever.DropBoxRetrieverComponent;
import com.morty.dropbox.retriever.DropBoxRetrieverConstants;
import com.morty.dropbox.retriever.DropBoxRetrieverUtils;
import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.sun.mail.pop3.POP3SSLStore;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * An implementation of the Component that does emails...
 * This version does POP3 only!
 *
 * TODO:     pop3 unread marking - not sure if possible.
 *
 * @author amorton
 */
public class DropBoxRetrieverEmailComponent extends DropBoxRetrieverComponent
{

    
    //Variables to be injected by spring.
    private String m_host;
    private int m_port;
    private String m_user;
    private String m_pass;
    private boolean m_ssl;


    //Number to retrieve in one go. (default to 100)
    private int m_maxRetrieval = 100;

    //whitelists and blacklists.
    private List m_blackListDomains = new ArrayList();
    private List m_whiteListDomains = new ArrayList();
    private List m_whiteListAddresses = new ArrayList();


    private Map m_folderMappings;
    
    
    public void setSubjectMappings(Map mappings)
    {
        m_folderMappings = mappings;
    }

    public void setBlackListDomains(List m_blackListDomains)
    {
        this.m_blackListDomains = m_blackListDomains;
    }

  

    public void setHost(String m_host)
    {
        this.m_host = m_host;
    }

    public void setMaxRetrieval(int m_maxRetrieval)
    {
        this.m_maxRetrieval = m_maxRetrieval;
    }

    public void setPass(String m_pass)
    {
        this.m_pass = m_pass;
    }

    public void setPort(int m_port)
    {
        this.m_port = m_port;
    }

    
    public void setUser(String m_user)
    {
        this.m_user = m_user;
    }

    public void setWhiteListAddresses(List m_whiteListAddresses)
    {
        this.m_whiteListAddresses = m_whiteListAddresses;
    }

    public void setWhiteListDomains(List m_whiteListDomains)
    {
        this.m_whiteListDomains = m_whiteListDomains;
    }

    
    public void setSsl(boolean m_ssl)
    {
        this.m_ssl = m_ssl;
    }

    @Override
    public String getName()
    {
        return "EmailRetrieverComponent";
    }

    public Map processComponent() throws Exception
    {
        m_logger.info("Starting component [EmailRetriever]");
        Map returnFiles = new HashMap();
        //Get the emails from the pop3 store
        Store store=null;
        Folder folder=null;
        try
        {
          //Get a session
          Properties props = System.getProperties();

          props.setProperty("mail.pop3.socketFactory.class", DropBoxRetrieverConstants.SSL_FACTORY);
          props.setProperty("mail.pop3.socketFactory.fallback", "false");
          props.setProperty("mail.pop3.port",  ""+m_port);
          props.setProperty("mail.pop3.socketFactory.port", ""+m_port);
          props.setProperty("mail.mime.decodefilename", "true");

          URLName url = new URLName(DropBoxRetrieverConstants.DEFAULT_STORE, m_host, m_port, "",m_user, m_pass);
          Session session = Session.getDefaultInstance(props, null);


          if(m_ssl)
          {
            m_logger.info("Connecting(SSL) to ["+m_host+"] on port ["+m_port+"] with username ["+m_user+"]");
            store = new POP3SSLStore(session, url);
            store.connect();
            m_logger.info("Connected to ["+m_host+"]");
          }
          else
          {
              //Connect to the default pop3 store
              store = session.getStore(DropBoxRetrieverConstants.DEFAULT_STORE);
              m_logger.info("Connecting to ["+m_host+"] on port ["+m_port+"] with username ["+m_user+"]");
              store.connect(m_host, m_port,m_user, m_pass);
              m_logger.info("Connected to ["+m_host+"]");
          }


          //Can we get the default folder?
          folder = store.getDefaultFolder();
          if (folder == null) throw new Exception("No default folder");
          
          //Now get the inbox
          folder = folder.getFolder("INBOX");
          if (folder == null) throw new Exception("No POP3 INBOX");
          
          m_logger.info("Opening INBOX");
          //Open in read_write mode to allow the setting of 'read'
          folder.open(Folder.READ_ONLY);
          

          //process mails up to the limit!
          Message[] emails = folder.getMessages();

          m_logger.info("Processing ["+emails.length+"] messages");

          for (int msgNum = 0; msgNum < emails.length && msgNum < m_maxRetrieval; msgNum++)
          {
              m_logger.info("Processing Message Number ["+msgNum+"]");
              returnFiles = processMessage(emails[msgNum], returnFiles);
              emails[msgNum].setFlag(Flag.SEEN, false);
          }
          return returnFiles;

        }
        catch (Exception ex)
        {
          m_logger.error("Unable to process Email",ex);
          returnFiles = new HashMap();

        }
        finally
        {
            // Tidy up all the connections!
            try
            {
                if (folder!=null)
                    folder.close(false);
            
                if (store!=null)
                    store.close();
            }
            catch (Exception ex2)
            {
                m_logger.error("Unable to clean up:",ex2);
            }
            return returnFiles;

        }



    }

    private Map processMessage(Message message, Map currentFiles) throws Exception
    {
        try
        {
            //Take the message, check against the filename format to ensure supported format.
            //Then once it
            Map messageProps = processMessageProps(message);

            if(m_logger.isDebugEnabled())DropBoxRetrieverUtils.printProperties(messageProps);

            //Only proceed if from is on the whitelist,etc..
            validEmail(messageProps,message);
            
            //Get the files from the message
            Map files = getAttachments(message, messageProps);

            //Check to see if there are files. If not, then just return values as they are passed in.
            if(!files.isEmpty())
                currentFiles.putAll(files);
            else
                throw new DropBoxRetrieverEmailException("No attachments found in email.",message);

            return currentFiles;
            
        }
        catch (Exception ex)
        {
            //If we have a problem retrieving the files, then this is an issue.
            //We reply or we forward to the admin!
            if(ex instanceof DropBoxRetrieverEmailException && m_exceptionHandler != null)
                m_exceptionHandler.handleException(ex);
            else
            {
                m_logger.error("Unknown error",ex);
                throw ex;
            }
        }

        //Return files.
        return currentFiles;

    }

    
    private Map processMessageProps(Message message) throws Exception
    {
        Map props = new HashMap();

        //look at the configuration
        props.put(DropBoxRetrieverConstants.MAIL_SUBJECT,message.getSubject().trim());
        Address[] addresses = message.getFrom();

        if(addresses != null)
        {
            InternetAddress address = (InternetAddress) addresses[0];
            props.put(DropBoxRetrieverConstants.MAIL_SENDER,address.getAddress());
            props.put(DropBoxRetrieverConstants.MAIL_SENDER_DOMAIN,address.getAddress().split("@")[1]);

        }
        
        //If we have a mapping for this subject, then use the dictated subdirectory...
        if(m_folderMappings != null && m_folderMappings.containsKey(message.getSubject().trim()))
            props.put(DropBoxRetrieverConstants.SUB_DIRECTORY,(String) m_folderMappings.get(message.getSubject().trim()));
        
        
        return props;

    }

    private Map getAttachments(Message message, Map messageProps) throws Exception
    {

        m_logger.info("Getting attachments");
        Map files = new HashMap();
        if(message.getContent() instanceof Multipart)
        {
            Multipart mp = (Multipart) message.getContent();
            for (int i = 0, n = mp.getCount(); i < n; i++)
            {
                Part part = mp.getBodyPart(i);
                String disposition = part.getDisposition();
                if ((disposition != null) &&
                        ((disposition.equals(Part.ATTACHMENT) || (disposition.equals(Part.INLINE)))))
                {
                    m_logger.debug("Obtained attachement ["+part.getFileName()+"]");
                    byte[] fileData = IOUtils.toByteArray(part.getInputStream());
                    String fileKey = message.getMessageNumber()+"_"+i+"_"+part.getFileName();
                    DropBoxRetrieverFile file = new DropBoxRetrieverFile(part.getFileName(), fileData);
                    file.setProperties(messageProps);
                    files.put(fileKey,file);
                }
            }
        }
        else m_logger.info("No attachments found in this message");
        return files;

    }

   
    /*
     * Check the black and whitelists.
     */
    private void validEmail(Map messageProps,Message message) throws DropBoxRetrieverEmailException
    {
        
        m_logger.info("Checking valid email.");
        DropBoxRetrieverUtils.printProperties(messageProps);
        
        if(m_whiteListAddresses == null && m_whiteListDomains == null && m_blackListDomains == null)
            return;
        
        boolean valid = true;
        String errorMessage = "";
        // Are we valid?
        if(m_whiteListAddresses.contains(messageProps.get(DropBoxRetrieverConstants.MAIL_SENDER)) )
        {
            m_logger.info("Valid sender ");
            valid = true;
        }
        else if( m_whiteListDomains.contains(messageProps.get(DropBoxRetrieverConstants.MAIL_SENDER_DOMAIN)) )
        {
             m_logger.info("Valid domain ");
             valid=true;
        }    
        else
        {
            m_logger.info("Not on whitelists");
            valid = false;
            errorMessage = "Not in email whitelist";
        }
            

        m_logger.info("Valid Address ["+valid+"]");
        
        
        if(m_blackListDomains.contains(messageProps.get(DropBoxRetrieverConstants.MAIL_SENDER_DOMAIN)))            
        {
            m_logger.info("On the black list");
            valid = false;
            errorMessage = "Sender/Domain is on black list";
        }
        else
        {
            //If its not on the whitelist, and the whitelists are configured....
            if(     
                    (!valid) && 
                    (m_whiteListAddresses != null && m_whiteListAddresses.isEmpty()) &&
                    (m_whiteListDomains != null && m_whiteListDomains.isEmpty())
              )
            {
                m_logger.info("Not on blacklist, but not on whitelist either...please add to list.");
                valid = false;
                errorMessage = "The email is not on the whitelists.";
            }
            else
            {
                //Not on the blacklist, but the whitelist are not configured.
                valid = true;
                m_logger.info("This email was valid after whitelist check. So allow");
            }
        }

        if(!valid)
            throw new DropBoxRetrieverEmailException(errorMessage,message);
        else return;
        

    }



    @Override
    protected void deliverFiles(Map mapOfFiles) throws Exception
    {

        m_logger.info("Deliver files....");
        DropBoxRetrieverUtils.printFileList(mapOfFiles);
        
        Iterator it = mapOfFiles.keySet().iterator();
        while(it.hasNext())
        {
            String fileKey = (String) it.next();
            DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
            
            //Print out the props of the file.
            DropBoxRetrieverUtils.printProperties(file.getProperties());
            
            m_logger.info("Delivering ["+file.getFilename()+"]....");
            
            //Add the directory from the subject... 
            String destination = m_dropboxFolder+File.separator;
            if(file.hasProperty(DropBoxRetrieverConstants.SUB_DIRECTORY))
                destination+=file.getProperty(DropBoxRetrieverConstants.SUB_DIRECTORY)+File.separator;            
            else if(file.hasProperty(DropBoxRetrieverConstants.MAIL_SUBJECT))
                destination= m_dropboxFolder+File.separator +file.getProperty(DropBoxRetrieverConstants.MAIL_SUBJECT)+File.separator;
            
            
            m_logger.debug("Destination after properties is ["+destination+"]");
            
            //Create the folder... 
            File destinationCheck = new File(destination);
            if(!destinationCheck.exists())
            {
                m_logger.info("Creating subfolder ["+destinationCheck.getAbsolutePath()+"]");
                destinationCheck.mkdirs();
            }
            
            
            destination+=file.getFilename();
            m_logger.info("File ["+destination+"] being written");
            FileUtils.writeByteArrayToFile(new File(destination), file.getFileData());        
        }
        
        
        m_logger.info("Files Delivered....");
    }



}

