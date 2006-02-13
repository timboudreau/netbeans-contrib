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

package org.netbeans.bluej.ui.window;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

/**
 * Class holding opened BlueJ projects and providing ComboBoxModel
 *
 * @author Milan Kubec
 */
public class OpenedBluejProjects {
    
    private ArrayList openedProjects;
    private DefaultComboBoxModel model;
    private HashMap projectIndexMap;
    
    /** Creates a new instance of OpenedBluejProjects */
    public OpenedBluejProjects() {
        openedProjects = new ArrayList();
        model = new DefaultComboBoxModel();
        projectIndexMap = new HashMap();
    }
    
    public void addProject(Project prj) {
        openedProjects.add(prj);
        updateMap();
        updateModel(prj);
    }
    
    public void removeProject(Project prj) {
        openedProjects.remove(prj);
        updateMap();
        updateModel(null);
    }
    
    public Project getProject(int index) {
        return (Project) openedProjects.get(index);
    }
    
    public Project getProject(String prjName) {
        return (Project) openedProjects.get(((Integer) projectIndexMap.get(prjName)).intValue());
    }
    
    public ComboBoxModel getComboModel() {
        return model;
    }
    
    public boolean isEmpty() {
        return openedProjects.isEmpty();
    }
    
    private void updateModel(final Project prj) {
        final Project[] op = (Project[]) openedProjects.toArray(new Project[] {});
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
                for (int i = 0; i < op.length; i++) {
                    model.addElement(getProjectDisplayName(op[i]));
                }
                if (prj != null) {
                    model.setSelectedItem(getProjectDisplayName(prj));
                }
            }
        });
    }
    
    private void updateMap() {
        projectIndexMap.clear();
        Iterator iter = openedProjects.iterator();
        while (iter.hasNext()) {
            Project p = (Project) iter.next();
            projectIndexMap.put(getProjectDisplayName(p), new Integer(openedProjects.indexOf(p)));
        }
    }
    
    private String getProjectDisplayName(Project prj) {
        return ProjectUtils.getInformation(prj).getDisplayName();
    }
    
}
