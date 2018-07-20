/*
 * ScarabBugQuery.java
 *
 * Created on November 29, 2005, 5:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.bugs.scarab;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.tasklist.bugs.BugQuery;

/**
 *
 * @author mick
 * @version $Id$
 */
public final class ScarabBugQuery extends BugQuery
{
    private final Map attributes = new HashMap();
    private final BugQuery bugQuery;
    
    /** Creates a new instance of ScarabBugQuery */
    public ScarabBugQuery(final BugQuery query)
    {
        bugQuery = query;
    }
    
    public String getAttributeName(final String attribute)
    {
        return (String)attributes.get(attribute);
    }
    
    public void setAttributeName(final String tasklistAttribute, 
            final String scarabAttribute)
    {
        attributes.put(tasklistAttribute, scarabAttribute);
    }
    
    public String getSummary() { 
        return bugQuery.getSummary();
    }
    
    public Integer getBugId() {
        return bugQuery.getBugId();
    }
    
    /** Getter for property mBugEngine.
     * @return Value of property mBugEngine.
     *
     */
    public java.lang.String getBugEngine() {
        return bugQuery.getBugEngine();
    }
    
    /** Setter for property mBugEngine.
     * @param mBugEngine New value of property mBugEngine.
     *
     */
    public void setBugEngine(java.lang.String mBugEngine) {
        bugQuery.setBugEngine(mBugEngine);
    }
    
    /** Getter for property mQueryString.
     * @return Value of property mQueryString.
     *
     */
    public java.lang.String getQueryString() {
        return bugQuery.getQueryString();
    }
    
    /** Setter for property mQueryString.
     * @param mQueryString New value of property mQueryString.
     *
     */
    public void setQueryString(java.lang.String mQueryString) {
        bugQuery.setQueryString(mQueryString);
    }
    
    /** Getter for property mBaseUrl.
     * @return Value of property mBaseUrl.
     *
     */
    public java.lang.String getBaseUrl() {
        return bugQuery.getBaseUrl();
    }
    
    /** Setter for property mBaseUrl.
     * @param mBaseUrl New value of property mBaseUrl.
     *
     */
    public void setBaseUrl(java.lang.String mBaseUrl) {
        bugQuery.setBaseUrl(mBaseUrl);
    }    
}
