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

package org.netbeans.modules.tasklist.bugs.issuezilla;

import org.xml.sax.*;

import java.util.*;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * The handler for parsing files containing XML representations of Issuezilla
 * bugs.
 *
 * tor@netbeans.org:
 * This class is virtually identical to
 *  nbbuild/antsrc/org/netbeans/nbbuild/IssuezillaXMLHandler.java
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
 * @author ibradac
 * @version 1.0
 */
final class IssuezillaXMLHandler extends HandlerBase {
    
    /** The DTD version this parser is capable to work with. */
    private static final String DTD_VERSION = "$Revision$";
    
    /** Name of the dtd_version attribute of the issuezilla tag */
    private static final String DTD_VERSION_NAME = "dtd_version";
    
    private static final String ISSUE_STATUS = "issue_status";
    
    private static final String RESOLUTION = "resolution";
    
    private static final String PRIORITY = "priority";
    
    private static final String ISSUE = "issue";
    
    private static final String ISSUEZILLA = "issuezilla";
    
    private static final String LONG_DESC = "long_desc";
    
    private static final String WHO = "who";
    
    private static final String ISSUE_WHEN = "issue_when";
    
    private static final String THETEXT = "thetext";
    
    /** 
     * Contains names of all the XML tags which can be inside the
     * &lt;Issuezilla.Issue&gt; tag. Should be probably fetched from the DTD.
     * A constant for now. 
     */
    private static final HashSet tagsInIssue;
    
    private static final List tagsInLongDesc;
    
    /** 
     * List of bugs created from the XML file. Items of the list are Maps
     * containing the name/value pairs for the bug attributes.
     */
    private List bugs;
    
    /** The name/value pairs for the currently parsed bug */
    private Map bug;
    
    /** Structure of currently opened tags */
    private Vector openedTags;
    
    /** A StringBuffer containing the values inside the tags */
    private StringBuffer buffer;
    
    /** A StringBuffer containing the value for the long_desc attribute */
    private StringBuffer longDescBuffer;
    
    /** Contains the Issue.LongDescription object list */
    private List longDescriptionList;
    
    private Issue.Description longDesc;

    /** date format converter */
    private SimpleDateFormat dateFormat;
    /** date format converter for second format of time */
    private SimpleDateFormat dateFormat2;
    
    
    static {
        tagsInIssue = new HashSet();
        tagsInIssue.add("issue_id");
        //tagsInIssue.add("issue_status");
        tagsInIssue.add("component");  //tagsInIssue.add("product");
        //tagsInIssue.add("priority");
        tagsInIssue.add("version");
        tagsInIssue.add("rep_platform");
        tagsInIssue.add(Issue.ASSIGNED_TO);
        tagsInIssue.add("delta_ts");
        tagsInIssue.add("subcomponent"); //tagsInIssue.add("component");
        tagsInIssue.add("reporter");
        tagsInIssue.add("target_milestone");
        tagsInIssue.add("issue_type");
        tagsInIssue.add(Issue.CREATED);
        tagsInIssue.add("qa_contact");
        tagsInIssue.add("status_whiteboard");
        tagsInIssue.add("op_sys");
        //tagsInIssue.add("resolution");
        tagsInIssue.add("short_desc");
        tagsInIssue.add(Issue.BLOCKS);
        tagsInIssue.add(Issue.CC);
        tagsInIssue.add(Issue.DEPENDS_ON);
        tagsInIssue.add(Issue.VOTES);
        tagsInIssue.add(Issue.KEYWORDS);
        //tagsInIssue.add("long_desc");
        
        tagsInLongDesc = new ArrayList();
        tagsInLongDesc.add(Issue.Description.WHO);
        tagsInLongDesc.add(Issue.Description.ISSUE_WHEN);
        tagsInLongDesc.add(Issue.Description.THETEXT);
    }
    
    /** Creates new IssuezillaXMLHandler */
    public IssuezillaXMLHandler() {
    }

    
    public void setDocumentLocator (org.xml.sax.Locator locator) {
    }

    
    public void startDocument ()
    throws org.xml.sax.SAXException {
        bugs = new ArrayList();
        openedTags = new Vector();
    }

    
    public void endDocument ()
    throws org.xml.sax.SAXException {
    }
    
    public void startElement (String name, org.xml.sax.AttributeList atts)
    throws org.xml.sax.SAXException {
        openedTags.addElement(name);
        if (name.equals(ISSUEZILLA)) {
            checkDTDVersion(atts);
        } else if (name.equals(ISSUE)) {
            bug = new HashMap();
            bugs.add(bug);
            longDescriptionList = new ArrayList();
            bug.put(Issue.LONG_DESC_LIST, longDescriptionList);
            // also CC is multivalue
            bug.put(Issue.CC, new ArrayList ());
            bug.put(Issue.DEPENDS_ON, new ArrayList ());
            bug.put(Issue.BLOCKS, new ArrayList ());
            bug.put(Issue.CREATED, new java.util.Date (0));
            //longDescBuffer = new StringBuffer();
            dealIssueAttributes(atts);
        } else if (tagsInIssue.contains(name)) {
            buffer = new StringBuffer();
        } else if (tagsInLongDesc.contains(name)) { 
            longDescBuffer = new StringBuffer();
        } else if (name.equals(LONG_DESC)) {
            longDesc = new Issue.Description();
            longDescriptionList.add(longDesc);
        }
    }

    
    public void endElement (String name)
    throws org.xml.sax.SAXException {
        if (!currentTag().equals(name)) {
            throw new SAXException(
                "An error while parsing the XML file near the closing " + name
                + " tag");
        }
        openedTags.remove(openedTags.size() - 1);
        if (name.equals(ISSUEZILLA)) {
            
        } else if (tagsInIssue.contains(name)) {
            Object prev = bug.get (name);
            if (prev instanceof List) {
                // expecting multivalue
                ((List)prev).add (buffer.toString ());
            } else if (prev instanceof Date) {
                // convert to date
                bug.put (name, toDate (buffer.toString()));
            } else {
                bug.put(name, buffer.toString());
            }
            buffer = null;
        } else if (name.equalsIgnoreCase(LONG_DESC)) { 
            //longDescriptionList.add(longDesc);
        } else if (tagsInLongDesc.contains(name)) {
            String s = longDescBuffer.toString ();
            if (name.equals (Issue.Description.ISSUE_WHEN)) {
                longDesc.setIssueWhen (toDate (s));
            } else {
                longDesc.setAtribute(name, s);
            }
        } 
        
    }

    
    public void characters (char ch[], int start, int length)
    throws org.xml.sax.SAXException {
        String s = new String(ch, start, length);
        if (!s.equals("")) {
            if (tagsInLongDesc.contains(currentTag())) {
                longDescBuffer.append(s);
            } else if (buffer != null) {
                buffer.append(s);
            }
        }
        
    }

    
    public void ignorableWhitespace (char ch[], int start, int length)
    throws org.xml.sax.SAXException {
    }

    
    public void processingInstruction (String target, String data)
    throws org.xml.sax.SAXException {
    }
    
    /** Gets the current tag */
    private String currentTag() {
        if (openedTags.size() == 0) {
            return null;
        }
        return (String) openedTags.lastElement();
    }
    
    /**
     * Gets the List of the bugs which is created during the parsing process.
     * Must be called after the parsing (othervise returns null)
     *
     * @return a List containing the Maps of name/value pairs for the bugs
     */
    public List getBugList() {
        return bugs;
    }
    
    /**
     * Sets the priority and resolution attributes and checks the DTD version */
    private void dealIssueAttributes(AttributeList atts)
    throws SAXException {
        String priority = atts.getValue(PRIORITY);
        String issue_status = atts.getValue(ISSUE_STATUS);
        String resolution = atts.getValue(RESOLUTION);
        if (priority == null) {
            priority = "P0";
        }
        bug.put(PRIORITY, priority);
        bug.put(ISSUE_STATUS, issue_status);
        if (resolution != null) {
            bug.put(RESOLUTION, resolution);
        }
    }
    
    /** 
     * Checks whether the right DTD version is used. If not a SAXException
     * to that effect is thrown.
     */
    private void checkDTDVersion(AttributeList atts) throws SAXException {
        String dtdVersion = atts.getValue(DTD_VERSION_NAME);
        if ( (dtdVersion == null) || (!dtdVersion.equals(DTD_VERSION))) {
            //throw new SAXException("Wrong DTD version " + dtdVersion 
              //                     + "; expected " + DTD_VERSION);
            //System.out.println("Warning: Wrong DTD version: " + dtdVersion 
              //                  + "; expected " + DTD_VERSION);
        }
    }
    
    /** Converts a string to date
     */
    public java.util.Date toDate (String date) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat ("yyyy-mm-dd hh:mm:ss");
        }
        if (dateFormat2 == null) {
            dateFormat2 = new SimpleDateFormat ("yyyy-mm-dd hh:mm");
        }
        try {
            return dateFormat.parse (date);
        } catch (java.text.ParseException ex1) {
            try {
                return dateFormat2.parse (date);
            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
