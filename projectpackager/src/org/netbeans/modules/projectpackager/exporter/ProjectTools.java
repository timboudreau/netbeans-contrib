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

package org.netbeans.modules.projectpackager.exporter;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;

/**
 * Tools class for working with projects
 * @author Roman "Roumen" Strobl
 */
public class ProjectTools {

    /** No constructor */
    private ProjectTools() {
    }

    /**
     * Read project info from Projects API and set the ExportPackageInfo according to it
     */
    public static void readProjectInfo() {
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        
        for (int i = 0; i < openProjects.length; i++) {
            Sources sources = ProjectUtils.getSources(openProjects[i]);
            SourceGroup[] sourceGroups
                    = sources.getSourceGroups(Sources.TYPE_GENERIC);
            ProjectInfo.setName(i, ProjectUtils.getInformation(openProjects[i]).getName());            
            FileObject[] sourceDirs = new FileObject[sourceGroups.length];
            Boolean[] isExternal = new Boolean[sourceGroups.length];
            for (int j = 0; j < sourceGroups.length; j++) {
                sourceDirs[j] = sourceGroups[j].getRootFolder();
                String projectPath = openProjects[i].getProjectDirectory().getPath();
                if (projectPath.indexOf(sourceGroups[j].getRootFolder().getPath()) == -1) {
                    isExternal[j] = Boolean.TRUE;
                } else {
                    isExternal[j] = Boolean.FALSE;                    
                }
            }
            ProjectInfo.setSourceRootPaths(i, sourceDirs);
            ProjectInfo.setIsExternal(i, isExternal);
        }
    }    
    
}
