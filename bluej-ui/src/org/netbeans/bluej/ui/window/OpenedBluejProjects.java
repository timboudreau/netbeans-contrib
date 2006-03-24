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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.bluej.api.BluejLogicalViewProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Class holding opened BlueJ projects and providing ComboBoxModel
 *
 * @author Milan Kubec
 */
class OpenedBluejProjects implements PropertyChangeListener {
    
    private DefaultComboBoxModel model;
    private PropChange topComponentChanger;
    
    /** Creates a new instance of OpenedBluejProjects */
    OpenedBluejProjects() {
        model = new DefaultComboBoxModel();
        topComponentChanger = new PropChange();
    }
    
    public void addNotify() {
        OpenProjects.getDefault().addPropertyChangeListener(this);
//        doUpdate(false);
    }
    
    public void removeNotify() {
        OpenProjects.getDefault().removePropertyChangeListener(this);
    }
    
    public ComboBoxModel getComboModel() {
        return model;
    }
    
    public Project getSelectedProject() {
        Object sel = model.getSelectedItem();
        if (sel != null) {
            return ((ComboWrapper)sel).getProject();
        }
        return null;
    }
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (OpenProjects.PROPERTY_OPEN_PROJECTS.equals(evt.getPropertyName())) {
            if (SwingUtilities.isEventDispatchThread()) {
                doUpdate(true);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        doUpdate(true);
                    }
                });
            }
        }
    }
    
    private void doUpdate(boolean trapProjectsView) {
        Collection existing = new ArrayList();
        for (int i = 0; i < model.getSize(); i++) {
            existing.add(((ComboWrapper)model.getElementAt(i)).getProject());
        }
        Collection newones = new ArrayList();
        Project[] prjs = OpenProjects.getDefault().getOpenProjects();
        for (int i = 0; i < prjs.length; i++) {
            if (prjs[i].getLookup().lookup(BluejLogicalViewProvider.class) != null) {
                if (existing.contains(prjs[i])) {
                    existing.remove(prjs[i]);
                } else {
                    newones.add(prjs[i]);
                }
            }
        }
        Iterator it = existing.iterator();
        while (it.hasNext()) {
            Project elem = (Project) it.next();
            for (int i = 0; i < model.getSize(); i++) {
                if (elem == ((ComboWrapper)model.getElementAt(i)).getProject()) {
                    model.removeElementAt(i);
                    break;
                }
            }
        }
        if (newones.size() > 0) {
            it = newones.iterator();
            ComboWrapper wr = null;
            while (it.hasNext()) {
                Project elem = (Project) it.next();
                wr = new ComboWrapper(elem);
                model.addElement(wr);
            }
            if (trapProjectsView) {
                model.setSelectedItem(wr);
                topComponentChanger.projectWasOpened();
            }
        }
        if (model.getSelectedItem() == null && model.getSize() > 0) {
            model.setSelectedItem(model.getElementAt(0));
        }
        if (model.getSize() == 0 && model.getSelectedItem() != null) {
            model.setSelectedItem(null);
        }
    }
    
    private static class ComboWrapper {
        private Project project;
        ComboWrapper(Project proj) {
            project = proj;
        }
        
        public String toString() {
            return ProjectUtils.getInformation(project).getDisplayName();
        }
        
        public Project getProject() {
            return project;
        }
        
    }
    
    private static class PropChange implements PropertyChangeListener{
        private boolean listenerAdded = false;
        PropChange() {
        }
        
        void projectWasOpened() {
            if (listenerAdded) {
                return;
            }
            listenerAdded = true;
            assert SwingUtilities.isEventDispatchThread();
            TopComponent active = TopComponent.getRegistry().getActivated();
            String id = WindowManager.getDefault().findTopComponentID(active);
            if ("projectTabLogical_tc".equals(id)) {
                BluejViewTopComponent.findInstance().open();
                BluejViewTopComponent.findInstance().requestActive();
            }
            TopComponent.getRegistry().addPropertyChangeListener(this);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
                TopComponent active = TopComponent.getRegistry().getActivated();
                String id = WindowManager.getDefault().findTopComponentID(active);
                if ("projectTabLogical_tc".equals(id)) {
                    TopComponent.getRegistry().removePropertyChangeListener(this);
                    listenerAdded = false;
                    BluejViewTopComponent.findInstance().open();
                    BluejViewTopComponent.findInstance().requestActive();
                }
            }
        }
    }
    
}
