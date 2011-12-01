// Copyright (c) 2011, Andrew Morton. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package com.morty.dropbox.retriever;

/**
 * Constants used.
 * @author amorton
 */
public class DropBoxRetrieverConstants
{

    /*
     * Bean name to pick up.
     */
    public static final String DROPBOX_RETRIEVER="dropboxRetriever";

    /*
     * POP3 store name
     */
    public static final String DEFAULT_STORE="pop3";


    /*
     * Property Keys to use in descriptions...
     */
    public static final String SUB_DIRECTORY="${directory}";


    /*
     * Class for the ssl connection
     */
    public static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    /*
     * Property for the subject
     */
    public static final String MAIL_SUBJECT="${mail.subject}";
     
    /*
     * Property for holding the mail sender.
     */
    public static final String MAIL_SENDER="${mail.sender}";
     
    /*
     * Property for holding the domain of the sender
     */
    public static final String MAIL_SENDER_DOMAIN="${mail.sender.domain}";
     
   


}
