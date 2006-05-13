/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
