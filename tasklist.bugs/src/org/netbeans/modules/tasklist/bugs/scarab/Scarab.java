/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
            throw new IllegalStateException ("Cannot initialize parser"); //NOI18N
        }
    }
    
    public void setProxyPool( final String proxyPool ) {
        java.util.StringTokenizer tokens = new java.util.StringTokenizer( proxyPool, "," ); //NOI18N
        
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
  
        System.out.println("Rotating http proxy server to " + host + ":" + port); //NOI18N
        
        if (!port.equals("")) {
            System.getProperties ().put ("http.proxyPort", port); //NOI18N
        }
        if (!host.equals("")) {
            System.getProperties ().put ("http.proxyHost", host); //NOI18N
        }
    }
    
    /** Executes a query and returns array of issue numbers that fullfils the query.
     * @param query the query string that should be appended to the URL after question mark part
     * @return array of integers
     */
    public List query (final String query) throws SAXException, IOException {
    
        final String search = "curmodule/0/tqk/0/template/admin%2CViewXMLExportIssues.vm?"; //NOI18N
        final URL u = new URL (urlBase,search+query);
        IOException lastEx = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;

        for (int iterate = 0; iterate < maxIOFailures; iterate++) {
            try {
                isr = new InputStreamReader (u.openStream (), "UTF-8"); //NOI18N
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
            else throw new IOException("Can't get connection to " + u.toString() + " for " + maxIOFailures + "times.");  //NOI18N
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
