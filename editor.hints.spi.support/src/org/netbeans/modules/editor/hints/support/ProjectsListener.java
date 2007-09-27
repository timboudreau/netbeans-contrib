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
