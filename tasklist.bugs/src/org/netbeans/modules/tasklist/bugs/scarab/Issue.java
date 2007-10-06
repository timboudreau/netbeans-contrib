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

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Date;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


/** Represents on issue in issuezilla.
 * Created by {@link Issuezilla#getBug}
 *
 *
 * tor@netbeans.org:
 * This class is virtually identical to
 *  nbbuild/antsrc/org/netbeans/nbbuild/Issue.java
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
 * serff@netbeans.org:
 * This class is almost exactally the same as issuezilla.Issue, but modified to 
 * work with bugzilla. I didn't want to call this class Bug because of the 
 * higher level Bug class.  If you can think of a better name, please let me know
 *
 * @todo think of a better name.
 *
 * @author Ivan Bradac, refactored by Jaroslav Tulach
 */
public final class Issue implements Comparable {

    private HashMap attributes = new HashMap (49);
    
    static final String ISSUE_TYPE = "issue_type";
    static final String ISSUE_ID = "issue_id";
    static final String REPORTER = "created_by";
    static final String ASSIGNED_TO = "assigned_to";
    static final String CREATED = "created";
    static final String SUMMARY = "Summary";
    static final String STATUS = "Status";
    static final String PRIORITY = "Priority";
    static final String COMPONENT = "Component";
    static final String SUBCOMPONENT = "Subomponent";
    static final String KEYWORDS = "Keywords";
    static final String TARGET = "Target";
    static final String VOTES = "Votes";

    /**
     * Gets the id as an Integer.
     *
     * @return the issue_id as 
     */
    public String getId() {
        return string(ISSUE_ID);
    }

    /** Who is assigned to this bug.
     * @return name of person assigned to this bug
     */
    public String getAssignedTo () {
        return string (ASSIGNED_TO);
    }

    /** Who reported the bug.
     * @return name of the reporter
     */
    public String getReportedBy () {
        return string (REPORTER);
    }


    /** Type of the issue: Bug, Enhancement, Task, etc...
     * @return textual name of issue type
     */
    public String getType () {
        return string (ISSUE_TYPE);
    }

    
    /** A time when this issue has been created.
     * @return the date or begining of epoch if wrongly defined
     */
    public Date getCreated () {
        Date d = (Date)getAttribute (CREATED);
        return d == null ? new Date (0) : d;
    }

    /** Getter to return string for given attribute.
     */
    private String string (String name) {
        Object o = getAttribute (name);
        return o instanceof String ? (String)o : "";
    }
    
    /** Getter for array of integers.
     */
    private int[] ints (String name) {
        List l = (List)getAttribute (name);
        if (l == null) {
            return new int[0];
        }
        
        int[] arr = new int[l.size ()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt ((String)l.get (i));
        }
        return arr;
    }

    /** Package private getter, it is expected to add getter for useful
     * issues.
     */
    Object getAttribute(String name) {
        return attributes.get(name);
    }


    /** Setter of values, package private. */
    void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    /** Converts the object to textual representation.
     * @return a text description of the issue
     */
    public String toString() {   
        StringBuffer buffer;
        if (attributes == null) {
            return java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("Empty_BugBase");
        }
        Iterator it = attributes.entrySet().iterator();
        buffer = new StringBuffer();
        buffer.append(this.getClass().getName() 
                      + java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("_containing_these_name/value_attribute_pairs:\n"));
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            buffer.append(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("NAME__:_") + entry.getKey() + "\n");
            buffer.append(java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/bugs/scarab/Bundle").getString("VALUE_:_") + entry.getValue() + "\n");      
        }
        return buffer.toString();
    }

    /** Compares issues by their ID
     */
    public int compareTo (final Object o) {
        final Issue i = (Issue)o;
        return getId ().compareTo(i.getId ());
    }

    public String getSummary()
    {
        return string(SUMMARY);
    }

    String getStatus()
    {
        return string(STATUS);
    }

}
