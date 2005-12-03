/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs.scarab;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.MessageFormat;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 * A connection to Scarab. Connects to the database and provides
 * descriptions of bugs. Is not thread safe, each thread should use
 * its own instance of Scarab.
 * 
 * tor@netbeans.org:
 * This class is virtually identical to
 *  nbbuild/antsrc/org/netbeans/nbbuild/Issuezilla.java
 * At first, I inclouded its class file directly as part of
 * the build. However, treating Issuezilla as a black box
 * didn't work well because when connections fail (and are
 * retried), or even during a query, there is no feedback - and
 * since issuezilla is so slow, it's hard to know in the GUI
 * that things are working. Therefore, I've modified the java
 * file to give us a little bit more feedback.
 * In CVS I stored the original file as the first revision,
 * so you can easily diff to see what has changed - and generate
 * a patch which you can then apply to an updated version
 * of nbbuild/antsrc/ to keep the two in sync.
 * 
 * serff@netbeans.org:
 * This class is almost exactally the same as Issuezilla, but modified to 
 * work with bugzilla.
 * 
 * @author Ivan Bradac, refactored by Jaroslav Tulach, modified by serff
 */
public final class Scarab{
    /** url base of issuezilla. For netbeans it is 
     * "http://openide.netbeans.org/issues/"
     */
    private java.net.URL urlBase;
    /** sax parser to use */
    private SAXParser saxParser;

    /** maximum IO failures during connection to IZ */
    private int maxIOFailures = 15;
    
    private Vector proxyServer = null;
    
    private int lastProxy = -1;
   
   
    /** Creates new connection to issuezilla. The urlBase should
     * point to URL where issuezilla produces its XML results.
     * In case of NetBeans the URL is
     * <B>"http://openide.netbeans.org/issues/xml.cgi"</B>
     * @param urlBase a URI to issuezilla's XML service
     */
    public Scarab(final java.net.URL urlBase) {
        this.urlBase = urlBase;
        
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance(); 
            factory.setValidating (false);
            saxParser = factory.newSAXParser();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException ("Cannot initialize parser");
        }
    }
    
    public void setProxyPool( final String proxyPool ) {
        java.util.StringTokenizer tokens = new java.util.StringTokenizer( proxyPool, "," );
        
        proxyServer = new Vector();
        
        while ( tokens.hasMoreTokens() ) {
            proxyServer.add( tokens.nextToken() );         
        }
        rotateProxy();
    }
    
    private void rotateProxy() {
        if (proxyServer == null) return;
        
        if (proxyServer.size() == 0) return;
        
        if (lastProxy + 2 > proxyServer.size()) lastProxy = 0;
        else lastProxy++;
        
        final String proxyString = (String) proxyServer.get( lastProxy );
        final String host = proxyString.substring(0, proxyString.indexOf(':'));
        final String port = proxyString.substring(proxyString.indexOf(':')+1);
  
        System.out.println("Rotating http proxy server to " + host + ":" + port);
        
        if (!port.equals("")) {
            System.getProperties ().put ("http.proxyPort", port);
        }
        if (!host.equals("")) {
            System.getProperties ().put ("http.proxyHost", host);
        }
    }
    
    /** Executes a query and returns array of issue numbers that fullfils the query.
     * @param query the query string that should be appended to the URL after question mark part
     * @return array of integers
     */
    public List query (final String query) throws SAXException, IOException {
    
        final String search = "curmodule/0/tqk/0/template/admin%2CViewXMLExportIssues.vm?";
        final URL u = new URL (urlBase,search+query);
        IOException lastEx = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;

        for (int iterate = 0; iterate < maxIOFailures; iterate++) {
            try {
                isr = new InputStreamReader (u.openStream (), "UTF-8");
                reader = new BufferedReader (isr);
                if( reader != null ){ break; }
            }catch (IOException ex) {
                synchronized ( this ) {
                    try {
                        StatusDisplayer.getDefault().setStatusText(
                                   MessageFormat.format(
                                    NbBundle.getMessage(Scarab.class, 
					     "CantConnect"), // NOI18N
				    new String[] { 
                                       new Date().toString(),
                                       urlBase.getHost()
                                   }));
                        rotateProxy();
                        this.wait( 5000 );
                    }
                    catch (InterruptedException ex1) {}
                }
                lastEx = ex;
                if( reader != null ){ reader.close(); }
                if( isr != null ){ isr.close(); }
            }
        }
        if (reader == null) {
            if (lastEx != null) throw lastEx;
            else throw new IOException("Can't get connection to " + u.toString() + " for " + maxIOFailures + "times.");
        }
        
        final List list = getBugs(reader,u);
        reader.close();
        return list;
    }
        
        
    
    /**
     * Gets the Issuezilla bugs from the InputStream.
     *
     * @return Issue[] objects from the InputStream containing
     * their XML representation.
     */
    private List getBugs(final Reader in, final URL source)
            throws SAXException, IOException  {
        
        final ScarabXMLHandler handler = new ScarabXMLHandler();
        final InputSource input = new InputSource(in);
        input.setSystemId(source.toExternalForm());
        saxParser.parse(input, handler);
        return handler.getIssueList();
    }

}
