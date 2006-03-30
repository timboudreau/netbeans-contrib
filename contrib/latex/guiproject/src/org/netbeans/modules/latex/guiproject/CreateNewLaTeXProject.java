/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;


import org.openide.filesystems.FileUtil;

import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class CreateNewLaTeXProject {
    
    private boolean debug = Boolean.getBoolean("netbeans.latex.guiproject.create.new.project");
    
    /** Creates a new instance of CreateNewLaTeXProject */
    private CreateNewLaTeXProject() {
    }
    
    private static CreateNewLaTeXProject instance = null;
    
    public static synchronized CreateNewLaTeXProject getDefault() {
        if (instance == null) {
            instance = new CreateNewLaTeXProject();
        }
        
        return instance;
    }
    
    public FileObject createProject(File metadataDir, File mainFile) throws IOException {
        if (debug) {
            System.err.println("createProject(" + metadataDir + ", " + mainFile + ")");
            System.err.println("mainFile.exists()=" + mainFile.exists());
        }
        
        if (!mainFile.exists()) {
            File parent = mainFile.getParentFile();
            
            parent.mkdirs();
            
            /*refresh:*/
            
            FileObject toRefresh = null;
            
            if (debug)
                System.err.println("parent = " + parent );
            
            while (parent != null && !parent.equals(parent.getParentFile()) && (toRefresh = FileUtil.toFileObject(parent)) == null) {
                if (debug) {
                    System.err.println("parent = " + parent );
                    System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
                }
                
                parent = parent.getParentFile();
                
                if (debug) {
                    System.err.println("parent = " + parent );
                    System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
                }
            }
            
            if (toRefresh != null) {
                if (debug) {
                    System.err.println("refreshing:" + toRefresh);
                    System.out.println("refreshing:" + toRefresh);
                }
                toRefresh.refresh();
            }
            
            System.err.flush();
            
            mainFile.createNewFile();
            FileUtil.toFileObject(parent).refresh();
            
            FileObject mainFileFO = FileUtil.toFileObject(mainFile);
            
            if (mainFileFO == null)
                throw new IOException("Created mainfile not found on filesystems!");
            
//           mainFileFO.getParent().refresh();
        }
        
        metadataDir.mkdirs();
        
        FileUtil.toFileObject(metadataDir.getParentFile()).refresh();
        
        FileObject metadataDirFO = FileUtil.toFileObject(metadataDir);
        
        FileObject buildSettings = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/build-settings.properties");
        
        FileObject targetBuildSettings = FileUtil.copyFile(buildSettings, metadataDirFO, "build-settings", "properties");
        
        EditableProperties ep = new EditableProperties();
        
        FileLock lock = null;
        
        OutputStream out = null;
        InputStream  ins = null;
        
        try {
            ins = targetBuildSettings.getInputStream();
            ep.load(ins);
            ins.close();
            ep.setProperty("mainfile", Utilities.findShortestName(metadataDir, mainFile));
            lock = targetBuildSettings.lock();
            out = targetBuildSettings.getOutputStream(lock);
            ep.store(out);
        } finally {
            if (lock != null)
                lock.releaseLock();
            
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
        
        return metadataDirFO;
    }
}
