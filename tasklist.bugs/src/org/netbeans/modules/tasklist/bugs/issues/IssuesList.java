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

import java.util.ArrayList;
import java.util.List;

/**
 * List of issues
 */
public class IssuesList {
    private int freeId;
    
    /** List of issues. <Issue> */
    public List issues = new ArrayList();
    
    /** List of components. <ProductComponent> */
    public List components = new ArrayList();
    
    /** List of issue statuses. <String> */
    public List statuses = new ArrayList();
    
    /** List of issue resolutions. <String> */
    public List resolutions = new ArrayList();
    
    /** List of platforms. <String> */
    public List platforms = new ArrayList();
    
    /** List of operating systems. <String> */
    public List operatingSystems = new ArrayList();
    
    /** List of available product versions. <String> */
    public List versions = new ArrayList();
    
    /** List of priorities for the issues. <String> */
    public List priorities = new ArrayList();
    
    /** List of issue types (bug, enhancement etc.). <String> */
    public List issueTypes = new ArrayList();
    
    /** List of persons. <Person> */
    public List persons = new ArrayList();
    
    /**
     * Creates a new instance of IssuesList
     */
    public IssuesList() {
    }
    
    /**
     * Returns the number of issues
     *
     * @return number of issues
     */
    public int getIssueCount() {
        return issues.size();
    }
    
    /**
     * Returns an issue
     * 
     * @return issue with the given index
     */
    public Issue getIssue(int index) {
        return (Issue) issues.get(index);
    }
    
    /**
     * Creates an issue
     *
     * @return created issue
     */
    public Issue createIssue() {
        Issue issue = new Issue(this, freeId++);
        issues.add(issue);
        return issue;
    }
}
