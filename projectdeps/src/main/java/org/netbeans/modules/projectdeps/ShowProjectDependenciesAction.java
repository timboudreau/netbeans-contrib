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

package org.netbeans.modules.projectdeps;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.lib.graphlayout.Graph;
import org.netbeans.lib.graphlayout.Vertex;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jaroslav Tulach
 */
final class ShowProjectDependenciesAction 
implements ProjectActionPerformer {
    
    /** Creates a new instance of ShowProjectDependenciesAction */
    private ShowProjectDependenciesAction() {
    }
    
    public static Action create() {
        return ProjectSensitiveActions.projectSensitiveAction(new ShowProjectDependenciesAction(), "Show Project Dependencies", null);
    }

    public boolean enable(Project project) {
        return project != null;
    }

    public void perform(Project project) {
        Graph g = Graph.create();
        HashSet<FileObject> direct = new HashSet<FileObject>();
        
        direct.add(project.getProjectDirectory());
        SubprojectProvider prov = project.getLookup().lookup(SubprojectProvider.class);
        if (prov != null) {
            for (Object o : prov.getSubprojects()) {
                Project p = (Project)o;
                
                direct.add(p.getProjectDirectory());
            }
        }
        
        collectProjects(g, new HashSet<FileObject>(), direct, project);

        ProjectInformation info = ProjectUtils.getInformation(project);
        
        TopComponent tc = new TopComponent();
        tc.setName(info.getName());
        tc.setDisplayName(info.getDisplayName());
        tc.setLayout(new BorderLayout());
        tc.add(BorderLayout.CENTER, g.createRenderer());
        tc.open();
        tc.requestActive();
    }
    
    private static void collectProjects(Graph g, Set<FileObject> visited, Set<FileObject> include, Project p) {
        if (!include.contains(p.getProjectDirectory())) {
            return;
        }
        if (!visited.add(p.getProjectDirectory())) {
            return;
        }
        
        ProjectInformation info = ProjectUtils.getInformation(p);
        Vertex v = g.createVertex(info.getName(), info.getDisplayName());

        SubprojectProvider prov = p.getLookup().lookup(SubprojectProvider.class);
        for (Object o : prov.getSubprojects()) {
            Project sub = (Project)o;
            
            if (include.contains(sub.getProjectDirectory())) {
                ProjectInformation info2 = ProjectUtils.getInformation(sub);
                Vertex subVer = g.createVertex(info2.getName(), info2.getDisplayName());

                g.createEdge(v, subVer, 1);
            }
        }
        
        for (Object o : prov.getSubprojects()) {
            Project sub = (Project)o;

            collectProjects(g, visited, include, sub);
        }
    }
    
    
}
