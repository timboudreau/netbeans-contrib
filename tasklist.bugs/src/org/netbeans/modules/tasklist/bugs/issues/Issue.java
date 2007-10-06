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
