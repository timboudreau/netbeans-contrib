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
