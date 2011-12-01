// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever.excpt;

import javax.mail.Message;

/**
 * Exception used to allow custom exceptions in the email component.
 * @author amorton
 */
public class DropBoxRetrieverEmailException extends Exception
{

    Message m_mail;
    
    public DropBoxRetrieverEmailException()
    {
        super();
    }

    public DropBoxRetrieverEmailException(String string)
    {
        super(string);
    }
    
    
    public DropBoxRetrieverEmailException(String string, Message msg)
    {
        super(string);
        this.m_mail = msg;
    }
    
    public Message getEmail()
    {
        return m_mail;
    }
    

}
