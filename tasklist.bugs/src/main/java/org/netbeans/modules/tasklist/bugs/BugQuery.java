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
