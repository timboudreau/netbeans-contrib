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
package org.netbeans.modules.editor.hints.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.modules.editorhints.*;
import org.netbeans.spi.editor.hints.ProvidersList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectsListener implements PropertyChangeListener {
    
    private List<Project> wereOpened;
    
    private List<List<Project>> queue;
    
    private static ProjectsListener INSTANCE = new ProjectsListener();
    
    public static ProjectsListener getDefault() {
        return INSTANCE;
    }
    
    /** Creates a new instance of ProjectsListener */
    private ProjectsListener() {
        if (true/*ProvidersList.getEagerness() != ProvidersList.EAGER_ON_PROJECT*/)
            return ;
        
        queue = new ArrayList<List<Project>>();
        wereOpened = Arrays.asList(OpenProjects.getDefault().getOpenProjects());
        
        enqueue(wereOpened);
        
        OpenProjects.getDefault().addPropertyChangeListener(this);
        
        new RequestProcessor("XXX").post(new Runnable() {
            public void run() {
                while (true) {
                    List<Project> projects = deqeue();
                    
                    for (Project p : projects) {
                        Sources s = ProjectUtils.getSources(p);
                        
                        SourceGroup[] sgs = s.getSourceGroups(Sources.TYPE_GENERIC);
                        
                        for (int cntr = 0; cntr < sgs.length; cntr++) {
                            scanFolders(sgs[cntr], sgs[cntr].getRootFolder());
                        }
                    }
                    
                    HintsOperator.getDefault().startProgress();
                }
            }
        });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        List<Project> l = new ArrayList<Project>(Arrays.asList(OpenProjects.getDefault().getOpenProjects()));
        
        l.removeAll(wereOpened);
        
        enqueue(l);
        
        wereOpened = Arrays.asList(OpenProjects.getDefault().getOpenProjects());
    }
    
    private synchronized void enqueue(List<Project> projects) {
        queue.add(projects);
        notifyAll();
    }
    
    private synchronized List<Project> deqeue() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        
        return queue.remove(0);
    }
    
    private void scanFolders(SourceGroup sg, FileObject file) {
        if (!VisibilityQuery.getDefault().isVisible(file))
            return ;
        
        if (!sg.contains(file))
            return ;
        
        if (file.isData()) {
            if (!PersistentCache.getDefault().isKnown(file))
                HintsOperator.getDefault().enqueue(file);
        } else {
            FileObject[] c = file.getChildren();
            
            for (int cntr = 0; cntr < c.length; cntr++) {
                scanFolders(sg, c[cntr]);
            }
        }
    }
    
}
