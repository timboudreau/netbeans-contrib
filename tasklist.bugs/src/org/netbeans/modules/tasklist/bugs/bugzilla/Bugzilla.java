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

package org.netbeans.modules.tasklist.bugs.bugzilla;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.MessageFormat;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.openide.TopManager;
import org.openide.util.NbBundle;

/**
 * A connection to Bugzilla. Connects to the database and provides
 * descriptions of bugs. Is not thread safe, each thread should use
 * its own instance of Bugzilla.
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
public final class Bugzilla extends java.lang.Object {
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
    public Bugzilla(java.net.URL urlBase) {
        this.urlBase = urlBase;
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
            factory.setValidating (false);
            saxParser = factory.newSAXParser();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException ("Cannot initialize parser");
        }
    }
    
    public void setProxyPool( String proxyPool ) {
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
        
        String proxyString = (String) proxyServer.get( lastProxy );
        String host = proxyString.substring(0, proxyString.indexOf(':'));
        String port = proxyString.substring(proxyString.indexOf(':')+1);
  
        System.out.println("Rotating http proxy server to " + host + ":" + port);
        
        if (!port.equals("")) {
            System.getProperties ().put ("http.proxyPort", port);
        }
        if (!host.equals("")) {
            System.getProperties ().put ("http.proxyHost", host);
        }
    }
    
    /** Getter of an issue for given number.
     * @param number number of the issue
     * @return the issue 
     * @exception IOException if connection fails
     * @exception SAXException if parsing fails
     */
    public Issue getBug (int number) throws SAXException, IOException {
        Issue[] arr = getBugs (new int[] { number });        
        if (arr.length != 1) {
            throw new java.io.InvalidObjectException ("Issue not read");
        }
        
        return arr[0];
    }
    
    /** Getter of more issues at once.
     * @param numbers array of integers with numbers of bugs to retrieve
     * @return the issue array
     * @exception IOException if connection fails
     * @exception SAXException if parsing fails
     */
    public Issue[] getBugs (int[] numbers) throws SAXException, IOException {
        int maxIssuesAtOnce = 10;
        
        Issue[] result = new Issue[numbers.length];
        
        GLOBAL: for (int issueToProcess = 0; issueToProcess < numbers.length; ) {
            int lastIssueRightNow = Math.min (numbers.length, issueToProcess + maxIssuesAtOnce);
        
            StringBuffer sb = new StringBuffer (numbers.length * 8);
            String sep = "xml.cgi?id=";
            IOException lastEx = null;
            for (int i = issueToProcess; i < lastIssueRightNow; i++) {
                sb.append (sep);
                sb.append (numbers[i]);
                sep = ",";
            }
            sb.append ("&show_attachments=false");
            for (int iterate = 0; iterate < maxIOFailures; iterate++) {
                URL u = null;
                try {
                    u = new URL(urlBase, sb.toString());
                    InputStream is = u.openStream();
                    
                    Issue[] arr;
                    try {
                        arr = getBugs(is);
                    } finally {
                        is.close();
                    }
                    
                    // copy the results and go on
                    for (int i = 0; i < arr.length; ) {
                        result[issueToProcess++] = arr[i++];
                    }
                    

                    continue GLOBAL;
                }
                catch (IOException ex) {
                    synchronized ( this ) {
                        try {
                            TopManager.getDefault().setStatusText(
                                   MessageFormat.format(
                                    NbBundle.getMessage(Bugzilla.class, 
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
                }
            }
        
            throw lastEx;
        } // end of GLOBAL
        
        return result;
    }
    
    /** Executes a query and returns array of issue numbers that fullfils the query.
     * @param query the query string that should be appended to the URL after question mark part
     * @return array of integers
     */
    public int[] query (String query) throws SAXException, IOException {
        URL u = new URL (urlBase, "buglist.cgi?" + query);
        IOException lastEx = null;
        BufferedReader reader = null;

        for (int iterate = 0; iterate < maxIOFailures; iterate++) {
            try {
                reader = new BufferedReader (
                    new InputStreamReader (u.openStream (), "UTF-8")
                );
            }
            catch (IOException ex) {
                synchronized ( this ) {
                    try {
                        TopManager.getDefault().setStatusText(
                                   MessageFormat.format(
                                    NbBundle.getMessage(Bugzilla.class, 
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
            }
        }
        if (reader == null) {
            if (lastEx != null) throw lastEx;
            else throw new IOException("Can't get connection to " + u.toString() + " for " + maxIOFailures + "times.");
        }
        
        ArrayList result = new ArrayList ();
        
        String magic = "show_bug.cgi?id=";
        for (;;) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            
            int index = line.indexOf (magic);
            if (index == -1) {
                continue;
            }
            
            index += magic.length ();
            
            int end = line.indexOf ('"', index);
            if (end == -1) {
                throw new IOException ("No ending \" from index " + index + " in " + line);
            }
        
            String number = line.substring (index, end);
            TopManager.getDefault().setStatusText(
                       MessageFormat.format(
                                    NbBundle.getMessage(Bugzilla.class, 
					     "QueryBug"), // NOI18N
				    new String[] { 
                                       number
            }));

            
            result.add (Integer.valueOf (number));
        }
        
        int[] arr = new int[result.size ()];
        
        Iterator it = result.iterator ();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = ((Integer)it.next ()).intValue();
        }
        
        return arr;
    }
        
        
    
    /**
     * Gets the Issuezilla bugs from the InputStream.
     *
     * @return Issue[] objects from the InputStream containing
     * their XML representation.
     */
    private Issue[] getBugs(InputStream in)
    throws SAXException, IOException  {
        BugzillaXMLHandler handler = new BugzillaXMLHandler();
        saxParser.parse(in, handler);
        return getBugsFromHandler(handler);
    }
    
    /**
     * Gets the bugs form the handler. This must be called once the handler
     * finished its work.
     */
    private Issue[] getBugsFromHandler(BugzillaXMLHandler handler) {
        List bugList = handler.getBugList();
        if (bugList == null) {
            return null;
        }
        Issue[] bugs = new Issue[bugList.size()];
        for (int i = 0; i < bugList.size(); i++) {
            Issue bug = new Issue();
            Map atts = (Map) bugList.get(i);
            Iterator it = atts.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next(); 
                bug.setAttribute((String) entry.getKey(), entry.getValue());
            }
            bugs[i] = bug;
        }
        return bugs;
    }
/*    
    public static void main (String[] args) throws Exception {
        Bugzilla iz = new Bugzilla (new URL ("http://www.netbeans.org/issues/"));
        
        
        //Issue[] arr = new Issue[] { iz.getBug (16000) };
        Issue[] arr = iz.getBugs (new int[] { 10001, 10000 });
        System.out.println("arr: " + arr.length);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(i + " = " + arr[i]);
        }
    }
*/

/*    Query *
    public static void main (String[] args) throws Exception {
        Bugzilla iz = new Bugzilla (new URL ("http://www.netbeans.org/issues/"));
        iz.setProxyPool("webcache.czech.sun.com:8080,webczech.uk.sun.com:8080,webcache.holland.sun.com:8080");
        

        int[] res = iz.query ("issue_status=NEW&issue_status=ASSIGNED&issue_status=STARTED&issue_status=REOPENED&email1=tulach&emailtype1=substring&emailassigned_to1=1&email2=&emailtype2=substring&emailreporter2=1&issueidtype=include&issue_id=&changedin=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&short_desc=&short_desc_type=substring&long_desc=&long_desc_type=substring&issue_file_loc=&issue_file_loc_type=substring&status_whiteboard=&status_whiteboard_type=substring&field0-0-0=noop&type0-0-0=noop&value0-0-0=&cmdtype=doit&newqueryname=&order=%27Importance%27");
        
        String sep = "";
        for (int i = 0; i < res.length; i++) {
            System.out.print(sep);
            System.out.print(res[i]);
            sep = ", ";
        }
        System.out.println();
    }
/**/
    
}
