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

package org.netbeans.modules.javafx.model.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.SwingUtilities;
import javax.swing.SwingUtilities;
import net.java.javafx.type.expr.CompilationUnit;
import net.java.javafx.typeImpl.Compilation;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.javafx.model.FXDocument;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

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
    
    static{
        initFX();
    }
    
    public static FXDocument getNextDocument() {
        FXDocument result = documents.iterator().next();
        documents.remove(result);
//        System.out.println("Removed from Set (" + documents.size() + "): " + result);
        return(result);
    }
    
    public static boolean hasMoreDocuments() {
        return(documents.iterator().hasNext());
    }
    
    public static void sourceChanged(FXDocument doc){
//        System.out.println("[JavaFXModel] sourceChanged for document: " + doc);
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
                if (isAdded){
                    renderPreview(document);
                }
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

    public static Compilation getPreviewCompilation(FXDocument doc){
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
    }
    
    private static void renderPreview(final FXDocument doc) {
//        System.out.println("[JavaFXModel] renderPreview for document: " + doc);
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
    
    private static Project getProject(FXDocument doc){
        return getProject(NbEditorUtilities.getFileObject(doc));
    }

    private static Project getProject(FileObject fileObject){
        Project result = null;
        try {
            ProjectManager pm = ProjectManager.getDefault();
            FileObject projDir = fileObject.getParent();
            while (!pm.isProject(projDir)) {
                projDir = projDir.getParent();
            }
            result = pm.findProject(projDir);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (IllegalArgumentException iae) {
        }
        return result;
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
