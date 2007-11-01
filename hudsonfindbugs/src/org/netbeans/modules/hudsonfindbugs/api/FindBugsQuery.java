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

package org.netbeans.modules.hudsonfindbugs.api;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.hudsonfindbugs.spi.FindBugsQueryImplementation;

/**
 * Find the Hudson FindBugs scan result files for projects. Files may be located either on 
 * remote server or built locally. IDE-wide proxy settings are used to fetch the remote resource.
 *  
 * @author Martin Grebac
 */
public class FindBugsQuery {
    
    private static final Logger LOG = Logger.getLogger(FindBugsQuery.class.getName());
            
    private FindBugsQuery() {}

    /**
     * Find the URL for FindBugs Hudson plugin
     * @param project The project for which FindBugs URL is requested
     * @param remote Whether remote server URL is requested, or URL for local copy
     * @return URL to findbugs file for project 'project' or null if the url is not defined. Note the physical resource 
     * itself might not exist.
     */
    public static URL getFindBugsUrl(Project project, boolean remote) {
        if (project == null) {
            throw new NullPointerException("Passed null to FindBugsQuery.getFindBugsUrl(Project, boolean)"); // NOI18N
        }
        FindBugsQueryImplementation query = project.getLookup().lookup(FindBugsQueryImplementation.class);
        if (query != null) {
            URL url = query.getFindBugsUrl(project, remote);
            LOG.log(Level.FINE, "getFindBugsUrl({0}, {1}) -> {2}", new Object[] {project.getProjectDirectory(), remote, url});
            return url;
        }
        LOG.log(Level.FINE, "No FBugs query implementation found, returning null for: ", project.getProjectDirectory());
        return null;
    }

    /**
     * Find the fastest access URL for FindBugs results. 
     * @param project The project for which FindBugs URL is requested
     * @return Local file if present, otherwise remote file. Null if the url is not defined. Note the physical resource 
     * itself might not exist.
     */
    public static URL getFindBugsUrl(Project project) {
        if (project == null) {
            throw new NullPointerException("Passed null to FindBugsQuery.getFindBugsUrl(Project)"); // NOI18N
        }
        FindBugsQueryImplementation query = project.getLookup().lookup(FindBugsQueryImplementation.class);
        if (query != null) {
            URL url = query.getFindBugsUrl(project, false);
            if (url == null) {
                url = query.getFindBugsUrl(project, true);
            }
            LOG.log(Level.FINE, "getFindBugsUrl({0}) -> {1}", new Object[] {project.getProjectDirectory(), url});
            return url;
        }
        LOG.log(Level.FINE, "No FBugs query implementation found, returning null for: ", project.getProjectDirectory());
        return null;
    }
    
}
