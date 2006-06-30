/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
