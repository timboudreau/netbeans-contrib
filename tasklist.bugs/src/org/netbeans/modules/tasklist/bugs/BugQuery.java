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

package org.netbeans.modules.tasklist.bugs;

/**
 * This is a generic bug query.  it contains all the info about the query
 * that is going to be done.  There will probably be specific querys that extend
 * from this class.
 *  
 * @todo Figure out common things to go in here.
 */
public class BugQuery {
    private String mBugEngine;
    private String mBaseUrl;
    private String mQueryString;
    
    /** Creates a new instance of BugQuery */
    public BugQuery() {
    }
    
    public String getSummary() { return "Summary"; }
    public Integer getBugId() { return new Integer(0); }
    
    /** Getter for property mBugEngine.
     * @return Value of property mBugEngine.
     *
     */
    public java.lang.String getBugEngine() {
        return mBugEngine;
    }
    
    /** Setter for property mBugEngine.
     * @param mBugEngine New value of property mBugEngine.
     *
     */
    public void setBugEngine(java.lang.String mBugEngine) {
        this.mBugEngine = mBugEngine;
    }
    
    /** Getter for property mQueryString.
     * @return Value of property mQueryString.
     *
     */
    public java.lang.String getQueryString() {
        return mQueryString;
    }
    
    /** Setter for property mQueryString.
     * @param mQueryString New value of property mQueryString.
     *
     */
    public void setQueryString(java.lang.String mQueryString) {
        this.mQueryString = mQueryString;
    }
    
    /** Getter for property mBaseUrl.
     * @return Value of property mBaseUrl.
     *
     */
    public java.lang.String getBaseUrl() {
        return mBaseUrl;
    }
    
    /** Setter for property mBaseUrl.
     * @param mBaseUrl New value of property mBaseUrl.
     *
     */
    public void setBaseUrl(java.lang.String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }
    
}
