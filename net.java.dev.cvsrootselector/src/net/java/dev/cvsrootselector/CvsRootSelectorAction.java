/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the CVSROOT Selector (RFE #65366).
 * The Initial Developer of the Original Code is Michael Nascimento Santos.
 * Portions created by Michael Nascimento Santos are Copyright (C) 2005.
 * All Rights Reserved.
 */

package net.java.dev.cvsrootselector;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.NodeAction;

public final class CvsRootSelectorAction extends NodeAction {
    public String getName() {
        return "Change CVSROOT...";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected boolean enable(Node[] node) {
        if (node.length != 1) {
            return false;
        }
        
        try {
            File file = getFile(node[0]);
            
            if (file == null) {
                return false;
            }
                        
            return CvsRootRewriter.getCvsRootFile(file) != null;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return false;
        }
    }
    
    protected void performAction(Node[] node) {
        try {
            new CvsRootSelectorPanel(getFile(node[0])).display();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.USER, ex);
        }
    }
    
    private File getFile(Node node) throws IOException {
        File file = null;
        
        FileObject fo = null;
        Collection fileObjects = node.getLookup().lookup(
                new Lookup.Template(FileObject.class)).allInstances();
        
        if (fileObjects.size() > 0) {
            fo = (FileObject) fileObjects.iterator().next();
        } else {
            DataObject dataObject = (DataObject) node.getCookie(DataObject.class);
            if (dataObject instanceof DataShadow) {
                dataObject = ((DataShadow) dataObject).getOriginal();
            }
            if (dataObject != null) {
                fo = dataObject.getPrimaryFile();
            }
        }
        
        if (fo != null) {
            File f = FileUtil.toFile(fo);
            if (f != null && f.isDirectory()) {
                file = f;
            }
        } else {
            Project project = (Project)node.getLookup().lookup(Project.class);
            
            if (project != null) {
                Sources sources = ProjectUtils.getSources(project);
                SourceGroup[] groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
                
                if (groups.length == 1) {
                    FileObject root = groups[0].getRootFolder();
                    file = FileUtil.toFile(root);
                } else {
                    File versioned = null;
                    boolean multiple = false;
                    
                    for (int i = 0; i < groups.length; i++) {
                        FileObject root = groups[0].getRootFolder();
                        File f = FileUtil.toFile(root);
                        
                        System.out.println(f);
                        if (f != null && CvsRootRewriter.getCvsRootFile(f) != null) {
                            if (versioned != null && !versioned.equals(f)) {
                                multiple = true;
                            }
                            
                            versioned = f;
                        }
                    }
                    
                    file = (multiple || versioned == null) ?
                        FileUtil.toFile(project.getProjectDirectory()) :
                        versioned;
                }
            }
        }
        
        return file;
    }
}