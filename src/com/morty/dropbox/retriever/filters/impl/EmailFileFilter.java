package com.morty.dropbox.retriever.filters.impl;

import com.morty.dropbox.retriever.file.DropBoxRetrieverFile;
import com.morty.dropbox.retriever.filters.AbstractFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This emails the file to a particular address/list of addresses
 * @author amorton
 */
public class EmailFileFilter extends AbstractFilter
{

    private String m_host;
    private int m_port = 465;
    private String m_username;
    private String m_password;
    private boolean m_sslEnabled;
    
    
    
    
    
    @Override
    public Map processFilter(Map mapOfFiles)
    {
        Map resultantFiles = new HashMap();
        try
        {
            m_logger.info("Starting.");
           
            m_logger.info("Processing files");
            Iterator it = mapOfFiles.keySet().iterator();

            while(it.hasNext())
            {
                String fileKey = (String) it.next();
                DropBoxRetrieverFile file = (DropBoxRetrieverFile) mapOfFiles.get(fileKey);
                String filename = file.getFilename();

                processFile(file);
               
               //return it.
               resultantFiles.put(fileKey, file);
                
            }

        }
        catch(Exception e)
        {
            m_logger.error("Problem in filter",e);
        }
        finally
        {
            return resultantFiles;
        }
    }

    @Override
    public String getName()
    {
        return "EmailFileFilter";
    }

    private void processFile(DropBoxRetrieverFile file)
    {
        //Take each file, and email it.
        Properties props = new Properties();
        props.put("mail.smtp.host", m_host);
        
        //Secure settings
        if(m_sslEnabled)
        {
            props.put("mail.smtp.socketFactory.port", m_port);
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", m_port);
        }
        else
        {
            //TLS (ie non ssl)
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");
        }
        
 
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("username","password");
                        }
                });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from@no-spam.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("to@no-spam.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");
 
			Transport.send(message);
 
			
 
		}
                catch (MessagingException e) 
                {
                    throw new RuntimeException(e);
		}
        return;
        
    }
    

}
