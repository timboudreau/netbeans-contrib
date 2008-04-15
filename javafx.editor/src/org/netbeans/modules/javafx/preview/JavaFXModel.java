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
import java.io.IOException;
import java.util.Collections;
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
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.javafx.project.JavaFXProject;
import org.openide.filesystems.FileObject;

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
    
    static public void addClassBytes(Project project, Map<String, byte[]> classBytes) {
        synchronized (projectsClassBytes) {
            Map <String, byte[]> classBytesForProj = projectsClassBytes.get(project);
            if (classBytesForProj == null) {
                classBytesForProj = new HashMap <String, byte[]>();
                OpenProjects.getDefault().addPropertyChangeListener(new ProjectListener());
            }
            if (classBytes != null)
                classBytesForProj.putAll(classBytes);
        }
    }
    
    static public void putClassBytes(Project project, Map<String, byte[]> classBytes) {
        synchronized (projectsClassBytes) {
            Map <String, byte[]> classBytesForProj = projectsClassBytes.get(project);
            if (classBytesForProj == null) {
                classBytesForProj = new HashMap <String, byte[]>();
                OpenProjects.getDefault().addPropertyChangeListener(new ProjectListener());
            }
            else 
                classBytesForProj.clear();
            if (classBytes != null)
                classBytesForProj.putAll(classBytes);
        }
    }
    
    static public Map<String, byte[]> getClassBytes(Project project) {
        synchronized (projectsClassBytes) {
            Map<String, byte[]> classBytes = projectsClassBytes.get(project);
            if (classBytes != null)
                return projectsClassBytes.get(project);
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
        
        comps.get(doc).sourceChanged();
        lastVisitTime = System.currentTimeMillis();
        changeThread.setDocument(doc);
    }

    public static void sourceDependencyChanged(FXDocument doc){
//        System.out.println("[JavaFXModel] sourceDependencyChanged for document: " + doc);
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
//        System.out.println("Show preview for doc: " + document);
        if (document != null && document.executionAllowed()){
            JComponent resultComponent = getResultComponent(document);
            if ((requiredNew) || (resultComponent == null)){
                
                boolean isAdded = documents.add(document);
//                System.out.println("Added to Set (" + documents.size() +"): " + document);
                //if (isAdded){
                    renderPreview(document);
                //}
            }else{
                document.renderPreview(resultComponent);
            }
        }
    }

    public static void addDocument(FXDocument doc){
//        System.out.println("[JavaFXModel] add document: " + doc);
        synchronized(comps){
            JavaFXRuntimeInfo ri = new JavaFXRuntimeInfo(doc);
            comps.put(doc, ri);
        }
    }

/*    public static Compilation getPreviewCompilation(FXDocument doc){
//        System.out.println("[JavaFXModel] getPreviewCompilation for document: " + doc);
        return comps.get(doc).getPreviewCompilation();
    }
    
    public static CompilationUnit getPreviewCompilationUnit(FXDocument doc){
//        System.out.println("[JavaFXModel] getPreviewCompilationUnit for document: " + doc);
        JavaFXRuntimeInfo ri = comps.get(doc);
        return ri.getPreviewUnit();
    }
    
    public static Compilation getCompilation(FXDocument doc){
//        System.out.println("[JavaFXModel] getCompilation for document: " + doc);
        return comps.get(doc).getCompilation();
    }
    
    public static Compilation getCompilation(FileObject fileObject){
        FXDocument doc = null;
        try{
            DataObject dataObject = DataObject.find(fileObject);
            EditorCookie editorCookie =  dataObject.getCookie(EditorCookie.class);
            doc = (FXDocument)editorCookie.getDocument();
        }catch(DataObjectNotFoundException e){
        }
        
        return getCompilation(doc);
    }
    
    public static CompilationUnit getCompilationUnit(FXDocument doc){
//        System.out.println("[JavaFXModel] getCompilationUnit for document: " + doc);
        JavaFXRuntimeInfo ri = comps.get(doc);
        return ri.getUnit();
    }
*/
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
    
    
// private methods    
    
    private static void initFX(){
        if (changeThread == null){
            changeThread = new ChangeThread();
            new Thread(new ChangeThread()).start();
        }
        projectsClassBytes = new HashMap<Project, Map<String, byte[]>>();
    }
    
    private static void renderPreview(final FXDocument doc) {
        try{
            PreviewThread tPreview = new PreviewThread(doc);
            if(!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(tPreview);
            } else {
                tPreview.start();
            }
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
