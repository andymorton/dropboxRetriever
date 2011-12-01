// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.excpt.impl;

import com.morty.dropbox.retriever.excpt.DropBoxRetrieverEmailException;
import com.morty.dropbox.retriever.excpt.DropBoxRetrieverExceptionHandler;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This will take the email and send it back, cc'ing in the admin, 
 * or maybe just to admin!
 *
 * May not stop processing - dependant on flag!
 *
 * @author amorton
 */
public class DropBoxRetrieverEmailExceptionHandler implements DropBoxRetrieverExceptionHandler
{

    protected static final Log m_logger = LogFactory.getLog(DropBoxRetrieverEmailExceptionHandler.class);
    
    private boolean replyTo = false;
    private String m_adminEmail;
    private String m_from;
    private String m_host;
    private String m_user;
    private String m_pass;
    private int m_port = 465;
    private boolean stopProcessing = false;
    
    public void setStopProcessing(boolean sp)
    {
        stopProcessing = sp;
    }
    
    public void setAdminEmail(String adminEmail)
    {
        this.m_adminEmail = adminEmail;
    }

    public void setReplyTo(boolean replyTo)
    {
        this.replyTo = replyTo;
    }

    public void setHost(String m_host)
    {
        this.m_host = m_host;
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
    
    public void setFrom(String from)
    {
        m_from = from;
    }



    public void handleException(Exception e) throws Exception
    {
        //TODO: Should this not all be spring based?
        if(e instanceof DropBoxRetrieverEmailException)
        {
             m_logger.info("Found Email Exception");
             
            Message currentMessage = ((DropBoxRetrieverEmailException )e).getEmail();
            if(currentMessage == null)
            {
                m_logger.error("Unable to reply to problem. No message attached",e);
                throw e;
            }
           
            // Get system properties
            Properties props = System.getProperties();

            // Setup mail server
            props.put("mail.smtp.host", m_host);
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                            "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            
            // Get session
            
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(m_user,m_pass);
				}
			});

            m_logger.info("Mail Session retrieved");
            // Define message
         
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("admin@fmbc.ac"));
            
            msg.setSubject("FMBC System: Error on processing attachments");
            
            
            if(replyTo)
            {
                m_logger.info("Reply to");
                //Try to email the person back.
                msg.setText("We were unable to process some/all of your attachments that were sent in.\n"+
                            "Please check the error message and try again.\n\nError["+e.getMessage()+"]");
                m_logger.info("Replying to the email from ["+currentMessage.getFrom()[0]+"]");
                msg.addRecipient(Message.RecipientType.TO, currentMessage.getFrom()[0]);
                
            }
            else
            {
                m_logger.info("Admin email");
                //Forward to admin email.
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_adminEmail));
                
                 // Create a multi-part to combine the parts
                Multipart multipart = new MimeMultipart();
                
                // Create your new message part
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dropbox retrieval system failed with the following error:\n\n["+e.getMessage()+"]\n\n");

                MimeBodyPart mbp = new MimeBodyPart();
                mbp.setContent(currentMessage, "message/rfc822");
                multipart.addBodyPart(mbp);
                multipart.addBodyPart(messageBodyPart);
                

                if(m_logger.isDebugEnabled())m_logger.debug("Number of email parts ["+multipart.getCount()+"]");
                
                //Set the content
                msg.setContent(multipart);
            }
            m_logger.info("sending email");
            //Send the message to the right person!
            Transport.send(msg);
            
            m_logger.info("sent email");
            
            if(stopProcessing)
                throw e;
        }
        else throw e;

    }




}
