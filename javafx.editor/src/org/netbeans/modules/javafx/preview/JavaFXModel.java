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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.javafx.preview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.javafx.editor.*;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import javax.swing.SwingUtilities;
//import net.java.javafx.type.expr.CompilationUnit;
//import net.java.javafx.typeImpl.Compilation;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.javafx.project.JavaFXProject;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;


/**
 *
 * @author answer
 */

public class JavaFXModel {
    
    private static Map<FXDocument, JavaFXRuntimeInfo> comps = Collections.synchronizedMap(new HashMap<FXDocument, JavaFXRuntimeInfo>());
    private static Set<FXDocument> documents = Collections.synchronizedSet(new HashSet<FXDocument>());
    
    private static final long PREVIEW_SHOW_DELAY = 1000;
    private static final long PREVIEW_CHECK_DELAY = 200;
    private static ChangeThread changeThread = null;
    private static long lastVisitTime = 0;
    private static Map <Project, Map <String, byte[]>> projectsClassBytes = null;
    
    static{
        initFX();
    }
    
    static class ProjectListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().contentEquals(OpenProjects.PROPERTY_OPEN_PROJECTS)) {
                Project projects[] = OpenProjects.getDefault().getOpenProjects();
                ArrayList<Project> projectArray = new ArrayList<Project>();
                Collections.addAll(projectArray, projects);
                synchronized (projectsClassBytes) {
                    for (Object project : projectsClassBytes.keySet()) {
                        if (!projectArray.contains((Project)project)) {
                            projectsClassBytes.remove(project);
                        }
                    }
                }
            }
        }
    }
    
    static class ClassPathListener implements PropertyChangeListener {
        private Project project = null;
        public ClassPathListener(Project project) {
            this.project = project;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().contentEquals("platform.active")) {                             // NOI18N
                projectsClassBytes.remove(project);
                if (project instanceof JavaFXProject) {
                    PropertyEvaluator evaluator =((JavaFXProject)project).evaluator();
                    evaluator.removePropertyChangeListener(this);
                }
            }
        }
    }
    
    
    static public void addClassBytes(Project project, Map<String, byte[]> classBytes) {
        synchronized (projectsClassBytes) {
            Map <String, byte[]> classBytesForProj = projectsClassBytes.get(project);
            if (classBytesForProj == null) {
                classBytesForProj = new HashMap <String, byte[]>();
                if (project instanceof JavaFXProject) {
                    PropertyEvaluator evaluator =((JavaFXProject)project).evaluator();
                    evaluator.addPropertyChangeListener(new ClassPathListener(project));
                }
            }
            if (classBytes != null)
                classBytesForProj.putAll(classBytes);
            projectsClassBytes.put(project,classBytesForProj);
        }
    }
    
    static public void putClassBytes(Project project, Map<String, byte[]> classBytes) {
        synchronized (projectsClassBytes) {
            Map <String, byte[]> classBytesForProj = projectsClassBytes.get(project);
            if (classBytesForProj == null) {
                classBytesForProj = new HashMap <String, byte[]>();
                if (project instanceof JavaFXProject) {
                    PropertyEvaluator evaluator =((JavaFXProject)project).evaluator();
                    evaluator.addPropertyChangeListener(new ClassPathListener(project));
                }
            }
            else 
                classBytesForProj.clear();
            if (classBytes != null)
                classBytesForProj.putAll(classBytes);
            projectsClassBytes.put(project,classBytesForProj);
        }
    }
    
    static public Map<String, byte[]> getClassBytes(Project project) {
        synchronized (projectsClassBytes) {
            Map<String, byte[]> classBytes = projectsClassBytes.get(project);
            if (classBytes != null)
                return classBytes;
            else
                return new HashMap<String, byte[]>();
        }
    }
    
    public static FXDocument getNextDocument() {
        FXDocument result = documents.iterator().next();
        documents.remove(result);
        return(result);
    }
    
    public static boolean hasMoreDocuments() {
        return(documents.iterator().hasNext());
    }
    
    public static void sourceChanged(FXDocument doc){
        
        CodeManager.cut(doc);
        lastVisitTime = System.currentTimeMillis();
        previewReq(doc, true);
    }

    public static void previewReq(FXDocument doc, boolean requiredNew){
        comps.get(doc).sourceChanged();
        changeThread.setDocument(doc);
    }
    
    public static void sourceDependencyChanged(FXDocument doc){
        comps.get(doc).sourceDependencyChanged();
        sourceChanged(doc);
    }
    
    public static void projectChanged(Project project){
        if (project != null){
            for(JavaFXRuntimeInfo ri: comps.values()){
                if (getProject(ri.getFileObject()) == project){
                    sourceDependencyChanged(ri.getDocument());
                }
            }
        }
    }
    
    public static void fireDependenciesChange(FXDocument doc){
        for(JavaFXRuntimeInfo ri: comps.values()){
            ri.fireDependenciesUpdate(doc);
        }
    }
    
    public static void showPreview(FXDocument document, boolean requiredNew) {
        if (document != null && document.executionAllowed()){
            JComponent resultComponent = getResultComponent(document);
            if ((requiredNew) || (resultComponent == null)){
                
                //boolean isAdded = documents.add(document);
                //if (isAdded){
                    renderPreview(document);
                //}
            }else{
                document.renderPreview(resultComponent);
            }
        }
    }

    public static void addDocument(FXDocument doc){
        synchronized(comps){
            JavaFXRuntimeInfo ri = new JavaFXRuntimeInfo(doc);
            comps.put(doc, ri);
        }
    }

    public static void setResultComponent(FXDocument doc, JComponent comp){
        comps.get(doc).setResultComponent(comp);
    }

    public static JComponent getResultComponent(FXDocument doc){
        return comps.get(doc).getResultComponent();
    }
    
    public static Reader getPreviewWidgetSource() {
        ClassLoader loader = JavaFXModel.class.getClassLoader();
        return new InputStreamReader(loader.getResourceAsStream("org/netbeans/modules/javafx/model/impl/JavaFXWidget.fx"));
    }
    
    private static void initFX(){
        if (changeThread == null){
            changeThread = new ChangeThread();
            new Thread(new ChangeThread()).start();
        }
        projectsClassBytes = new HashMap<Project, Map<String, byte[]>>();
        OpenProjects.getDefault().addPropertyChangeListener(new ProjectListener());
    }
    
    static PreviewThread tPreview = null;
    
    synchronized private static void renderPreview(final FXDocument doc) {
        try {
            if (tPreview != null)
                tPreview.joinTask();
            tPreview = new PreviewThread(doc);
        
            tPreview.start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static Project getProject(FXDocument doc){
        return getProject(NbEditorUtilities.getFileObject(doc));
    }

    public static Project getProject(FileObject fileObject){
        return FileOwnerQuery.getOwner(fileObject);
    }

    private static class ChangeThread implements Runnable{
        private static FXDocument doc;
        public void run(){
            while(true){
                if (doc != null && doc.executionAllowed() && (getResultComponent(doc) == null) && (System.currentTimeMillis() - lastVisitTime > PREVIEW_SHOW_DELAY)){
                    FXDocument document = doc;
                    doc = null;
                    showPreview(document, false);
                }
                try{
                    Thread.sleep(PREVIEW_CHECK_DELAY);
                }catch(InterruptedException e){}
            }
        }
        public void setDocument(FXDocument doc){
            ChangeThread.doc = doc;
        }
    }
}
