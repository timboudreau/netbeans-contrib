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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.label.LabelStructuralElement;
import org.netbeans.modules.latex.model.structural.parser.MainStructuralElement;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditor;
import org.openide.text.Line;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class NBUtilities extends Utilities implements PropertyChangeListener {
    
    private static final boolean debug = true;
    
    /** Creates a new instance of NBUtilities */
    public NBUtilities() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
        updateLastUsedCloneableEditor();
    }
    
    public boolean compareFiles(Object file1, Object file2) {
        return file1 == file2;
    }
    
    public Object getFile(Document doc) {
        //TODO: NetBeans only:
        if (doc == null)
            throw new NullPointerException();
        
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        
        if (od == null) {
//            Logger.getLogger("global").log(Level.WARNING, "The document's StreamDescriptionProperty is null.");
            return null;
        }
//            throw new IllegalStateException("The document's StreamDescriptionProperty is null.");
        
        return od.getPrimaryFile();
    }
    
    public Document openDocument(Object obj) throws IOException {
        //TODO: This should work in the IDE, but won't work with File(s)
        DataObject od = DataObject.find((FileObject) obj);
        
        if (od == null)
            return null;
        
        EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
        
        if (ec == null)
            return null;
        
        Document result = ec.openDocument();
        
        return result;
    }
    
    public Object[] getRelativeFileList(Object file, String relativePath) throws IOException {
//        File fileObj = new File("/" + ((FileObject) file).getParent().getPath(), relativePath);
//        String path = fileObj.getCanonicalPath();
//        FileObject relativeFolder = Repository.getDefault().findResource(path);
        
        FileObject relativeFolder = (FileObject) getRelativeFileName(file, relativePath);
        
        if (relativeFolder == null)
            return new Object[0];
        else
            return relativeFolder.getChildren();
    }
    
    private FileObject getChild(FileObject parent, String child) {
        int dot = child.indexOf('.');
        
        if (dot != (-1)) {
            return parent.getFileObject(child.substring(0, dot), child.substring(dot + 1));
        } else {
            return parent.getFileObject(child, null);
        }
    }
    
    public Object getRelativeFileName(Object file, String relativeFile) throws IOException {
        FileObject fileFO = ((FileObject) file).getParent();
        int        sep    = relativeFile.indexOf(File.separatorChar);
        
        while (sep != (-1) && fileFO != null) {
            String dirSpec;

            if (sep >= relativeFile.length() - 1) {
                dirSpec = relativeFile;
                relativeFile = "";
            } else {
                dirSpec = relativeFile.substring(0, sep + 1);
                relativeFile = relativeFile.substring(sep + 1);
            }
            
            sep    = relativeFile.indexOf(File.separatorChar);
            
            if ("../".equals(dirSpec)) {
                fileFO = fileFO.getParent();
                continue;
            }
            
            if ("./".equals(dirSpec)) {
                continue;
            }
            
            fileFO = getChild(fileFO, dirSpec);
        }
        
        if ("".equals(relativeFile))
            return fileFO;
        
        if (fileFO == null)
            return null;
        
        return getChild(fileFO, relativeFile);
    }
    
//    public Object   findFile(File file) throws IOException {
//        return FileUtil.toFileObject(file);
//    }
    
    public List/*<String>*/ findRelativeFilesBegining(Object file, String prefix) throws IOException {
        String dir  = "";
        String name = "";
        int sep = prefix.lastIndexOf(File.separatorChar);
        
        if (sep == (-1)) {
            name = prefix;
        } else {
            if (sep >= prefix.length() - 1) {
                dir  = prefix;
                name = "";
            } else {
                dir = prefix.substring(0, sep + 1);
                name = prefix.substring(sep + 1);
            }
        }
        
        Object[] files = getRelativeFileList(file, dir);
        List     result = new ArrayList();
        
        for (int cntr = 0; cntr < files.length; cntr++) {
            String fileName = ((FileObject) files[cntr]).getNameExt();
            
            if (fileName.indexOf(name) != (-1))
                result.add(dir + fileName);
        }
        
        return result;
    }
    
    public List<? extends LabelInfo> getLabels(LaTeXParserResult root) {
        List<LabelInfo> labels = new LinkedList<LabelInfo>();
        
        for (StructuralElement e : ((MainStructuralElement) root.getStructuralRoot()).getLabels()) {
            if (e instanceof LabelStructuralElement) {
                labels.add((LabelStructuralElement) e);
            }
        }
        
        return labels;
    }
    
    public String getHumanReadableDescription(SourcePosition position) {
        FileObject file = (FileObject) position.getFile();
        int        line = position.getLine();
        
        return file.getNameExt() + ":" + line;
    }
    
    private WeakReference/*<CloneableEditor>*/ lastUsedCloneableEditor = null;
    
    private CloneableEditor getLastCloneableEditor() {
        if (lastUsedCloneableEditor == null)
            return null;
        
        return (CloneableEditor) lastUsedCloneableEditor.get();
    }
    
    public synchronized JEditorPane getLastActiveEditorPane() {
        CloneableEditor editor = getLastCloneableEditor();
        
        if (editor == null)
            return null;
            
        try {
            Field field = CloneableEditor.class.getDeclaredField("pane");
            
            field.setAccessible(true);
            
            JEditorPane pane = (JEditorPane) field.get(editor);
            
            return pane;
        } catch (NoSuchFieldException nsfe) {
            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
            ErrorManager.getDefault().annotate(ex, nsfe);
            throw ex;
        } catch (IllegalArgumentException nsfe) {
            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
            ErrorManager.getDefault().annotate(ex, nsfe);
            throw ex;
        } catch (IllegalAccessException nsfe) {
            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
            ErrorManager.getDefault().annotate(ex, nsfe);
            throw ex;
        }
    }

    private synchronized void updateLastUsedCloneableEditor() {
        TopComponent comp = TopComponent.getRegistry().getActivated();
        
        if (comp == null || !(comp instanceof CloneableEditor) || comp == getLastCloneableEditor())
            return ;//Nothing to do.
        
        lastUsedCloneableEditor = new WeakReference((CloneableEditor) comp);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateLastUsedCloneableEditor();
    }
    
    public String getFileShortName(Object file) {
        FileObject fo = (FileObject) file;
        
        return fo.getNameExt();
    }

    public void openPosition(SourcePosition position) {
        try {
            DataObject od = DataObject.find((FileObject) position.getFile());
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            Line line = lc.getLineSet().getCurrent(position.getLine());
            
            line.show(Line.SHOW_GOTO);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

}
