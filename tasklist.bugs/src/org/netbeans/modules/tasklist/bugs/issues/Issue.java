/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs.issues;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An issue
 */
public class Issue {
    /** Parent list of issues. */
    private IssuesList list; 
    
    /** List of comments. <String> */
    public List comments = new ArrayList();
    
    /** List of issues this one depends on. <Issue> */
    public List dependsOn = new ArrayList();
    
    /** ID of this issue. It is unique in a list. */
    public int id;
    
    /** Component. -1 means unknown */
    public int component = -1;
    
    /** Subcomponent. -1 means unknown*/
    public int subcomponent;
    
    /** status of this issue */
    public int status;
    
    /** Platform. -1 means all*/
    public int platform;
    
    /** Operating system. -1 means all */
    public int os;
    
    /** Version */
    public int version;
    
    /** priority */
    public int priority;
    
    /** Type of this issue (bug, enhancement etc) */
    public int type;
    
    /** Target version */
    public int targetVersion;
    
    /** Person this issue is assigned to. */
    public int assignedTo;
    
    /** Associated URL. May be null.*/
    public URL url;
    
    /** Summary. */
    public String summary = "";
    
    /** Resolution */
    public int resolution;
    
    /**
     * Creates a new instance of Issue
     * Don't use this constructor. Use IssuesList.createIssue() instead
     *
     * @param list parent list for this issue
     * @param id id of this issue
     */
    Issue(IssuesList list, int id) {
        this.list = list;
        this.id = id;
    }
}
