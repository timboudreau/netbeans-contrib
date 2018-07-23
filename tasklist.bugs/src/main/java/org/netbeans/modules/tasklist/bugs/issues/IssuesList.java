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
