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

import org.xml.sax.*;

import java.util.*;

import java.io.*;
import java.text.SimpleDateFormat;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The handler for parsing files containing XML representations of Scarab
 * bugs.
 *
 * @author mick
 * @version 1.0
 */
final class ScarabXMLHandler extends DefaultHandler {

    /** The DTD version this parser is capable to work with. */
    private static final String DTD_VERSION = "$Revision$";

    /** Name of the dtd_version attribute of the issuezilla tag */
    private static final String DTD_VERSION_NAME = "dtd_version";
    
    private static final String SCARAB_ISSUES = "scarab-issues";
    private static final String ISSUE = "issue";
    private static final String ACTIVITY_SET = "activity-set";
    private static final String ACTIVITY = "activity";
    private static final String ATTACHMENT = "attachment";    

    private boolean inIssue = false;
    private boolean inActivitySet = false;
    private boolean inActivity = false;
    private boolean inActivitySetForCreateIssue = false;
    
    private StringBuffer buffer = new StringBuffer();
    
    /** 
     * List of bugs created from the XML file. Items of the list are Maps
     * containing the name/value pairs for the bug attributes.
     */
    private List issues;
    
    /** The name/value pairs for the currently parsed bug */
    private Issue issue;
    
    /** Structure of currently opened tags */
    private ArrayList openedTags;

    private String dateFormat;
    private String attribute;

    /** Creates new IssuezillaXMLHandler */
    public ScarabXMLHandler() {
    }

    
    public void setDocumentLocator (org.xml.sax.Locator locator) {
    }

    
    public void startDocument ()
            throws org.xml.sax.SAXException {
        
        issues = new ArrayList();
        openedTags = new ArrayList();
    }

    
    public void endDocument ()
            throws org.xml.sax.SAXException {
    }
    
    public void startElement (final String n, 
                final String l,
                final String q,
                final org.xml.sax.Attributes atts)
            throws org.xml.sax.SAXException {
        
        openedTags.add(q);

        if (q.equals(SCARAB_ISSUES)) {
            checkDTDVersion(atts);
            
        } else if ( q.equals(ISSUE)) {
            inIssue = true;
            issue = new Issue();
            issues.add(issue);
            
        } else if ( inIssue && q.equals(ACTIVITY_SET)) {
            inActivitySet = true;
            
        } else if ( inActivitySet && q.equals(ACTIVITY)) {
            inActivity = true;
        }
    }
    
    public void endElement (final String n,
                final String l,
                final String q)
            throws org.xml.sax.SAXException {
        
        if (!currentTag().equals(q)) {
            throw new SAXException(
                "An error while parsing the XML file near the closing "+q+" tag");
        }
        openedTags.remove(openedTags.size() - 1);
        
        if (q.equals(SCARAB_ISSUES)) {
            
        } else if (q.equals(ISSUE)) {
            inIssue = false;
            
        } else if ( inIssue && !inActivitySet && q.equals("id")    ){
            issue.setAttribute(Issue.ISSUE_ID,buffer.toString().trim());
         
        } else if ( inIssue && q.equals("artifact-type")    ){
            issue.setAttribute(Issue.ISSUE_TYPE,buffer.toString().trim());
          
        } else if ( inActivitySet && q.equals("type")) {
            final String type = buffer.toString().trim();
            if( "Create Issue".equalsIgnoreCase(type) ){
                inActivitySetForCreateIssue = true;
            }
            
        } else if ( inActivitySetForCreateIssue && q.equals("created-by")    ){
            issue.setAttribute(Issue.REPORTER, buffer.toString().trim());
            
        } else if ( inActivitySetForCreateIssue && q.equals("format")    ){
            dateFormat = buffer.toString().trim();
            
        } else if ( inActivitySetForCreateIssue && q.equals("timestamp")    ){
            issue.setAttribute(Issue.CREATED,toDate(buffer.toString().trim(),dateFormat));
            inActivitySetForCreateIssue = false;
            
        } else if ( inActivity && q.equals("attribute")    ){
            attribute = buffer.toString().trim();
           
        } else if ( inActivity && q.equals("new-value")    ){
            if( buffer.toString() != null ){
                issue.setAttribute(attribute,buffer.toString().trim());
            }
        
        } else if ( inActivity && q.equals("new-option")    ){
            if( buffer.toString() != null ){
                issue.setAttribute(attribute,buffer.toString().trim());
            }
            
        } else if ( inIssue && q.equals(ACTIVITY_SET)) {
            inActivitySet = false;
            
        } else if ( inActivitySet && q.equals(ACTIVITY)) {
            inActivity = false;
  
        }
        buffer.setLength(0);
    }
    
    /** Gets the current tag */
    private String currentTag() {
        if (openedTags.size() == 0) {
            return null;
        }
        return (String) openedTags.get(openedTags.size()-1);
    }
    
    /**
     * Gets the List of the bugs which is created during the parsing process.
     * Must be called after the parsing (othervise returns null)
     *
     * @return a List containing the Maps of name/value pairs for the bugs
     */
    public List getIssueList() {
        return issues;
    }
    
    
    /** 
     * Checks whether the right DTD version is used. If not a SAXException
     * to that effect is thrown.
     */
    private void checkDTDVersion(final Attributes atts) throws SAXException {
        
        final String dtdVersion = atts.getValue(DTD_VERSION_NAME);
        if ( (dtdVersion == null) || (!dtdVersion.equals(DTD_VERSION))) {
            //throw new SAXException("Wrong DTD version " + dtdVersion 
              //                     + "; expected " + DTD_VERSION);
            System.out.println("Warning: Wrong DTD version: " + dtdVersion 
                                + "; expected " + DTD_VERSION);
        }
    }
    
    /** Converts a string to date
     */
    public java.util.Date toDate (final String date, final String format) {
        
        final SimpleDateFormat dateFormat = new SimpleDateFormat (format);
        try {
            return dateFormat.parse (date);
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    public void characters (char ch[], int start, int length)
            throws org.xml.sax.SAXException {
        
        final String s = new String(ch, start, length);
        buffer.append(s);
    }

    
    public void ignorableWhitespace (char ch[], int start, int length)
    throws org.xml.sax.SAXException {
    }

    
    public void processingInstruction (String target, String data)
    throws org.xml.sax.SAXException {
    }
        
}
