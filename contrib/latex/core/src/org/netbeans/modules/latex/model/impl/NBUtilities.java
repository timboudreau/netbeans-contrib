/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.parser.MainStructuralElement;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.CloneableEditor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class NBUtilities extends Utilities {
    
    private static final boolean debug = true;
    
    /** Creates a new instance of NBUtilities */
    public NBUtilities() {
    }
    
    public boolean compareFiles(Object file1, Object file2) {
        return file1 == file2;
    }
    
    public Object getFile(Document doc) {
        //TODO: NetBeans only:
        if (doc == null)
            throw new NullPointerException();
        
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        
        if (od == null)
            return null;
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
    
    public void removeErrors(Collection/*<ParseError>*/ errors) {
        Iterator err = errors.iterator();
        
        while (err.hasNext()) {
            ((ParseErrorImpl) err.next()).removeError();
        }
    }
    
    public void showErrors(Collection/*<ParseError>*/ errors) {
        Iterator err = errors.iterator();
        
        while (err.hasNext()) {
            ((ParseErrorImpl) err.next()).showError();
        }
    }

    public ParseError createError(String message, SourcePosition pos) {
        return new ParseErrorImpl(message, pos);
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
    
    public List getLabels(LaTeXSource source) {
        FileObject mainFile = (FileObject) source.getMainFile();
        
        return ((MainStructuralElement) Model.getDefault().getModel(mainFile)).getLabels();
    }
    
    public String getHumanReadableDescription(SourcePosition position) {
        FileObject file = (FileObject) position.getFile();
        int        line = position.getLine();
        
        return file.getNameExt() + ":" + line;
    }
    
    public JEditorPane getLastActiveEditorPane() {
        TopComponent comp = TopComponent.getRegistry().getActivated();
        
        if (comp == null || !(comp instanceof CloneableEditor))
            return null;//Nothing to do.
        
        CloneableEditor editor = (CloneableEditor) comp;
        
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
    
}
