/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.io.File;
import java.io.IOException;


import java.io.InputStream;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;


import org.openide.filesystems.FileUtil;

import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class CreateNewLaTeXProject {
    
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
        System.err.println("createProject(" + metadataDir + ", " + mainFile + ")");
        System.err.println("mainFile.exists()=" + mainFile.exists());
        if (!mainFile.exists()) {
            File parent = mainFile.getParentFile();
            
            parent.mkdirs();
            
            /*refresh:*/
            
            FileObject toRefresh = null;
            System.err.println("parent = " + parent );
            while (parent != null && !parent.equals(parent.getParentFile()) && (toRefresh = FileUtil.toFileObject(parent)) == null) {
                System.err.println("parent = " + parent );
                System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
                parent = parent.getParentFile();
                System.err.println("parent = " + parent );
                System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
            }
            
            if (toRefresh != null) {
                System.err.println("refreshing:" + toRefresh);
                System.out.println("refreshing:" + toRefresh);
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
        
        FileObject buildXml = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/build.xml");
        
        FileObject targetBuildXml = FileUtil.copyFile(buildXml, metadataDirFO, "build", "xml");
        
        FileObject buildSettings = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/build-settings.properties");
        
        FileObject targetBuildSettings = FileUtil.copyFile(buildSettings, metadataDirFO, "build-settings", "properties");
        
        EditableProperties ep = new EditableProperties();
        
        FileLock lock = null;
        
        try {
            ep.load(targetBuildSettings.getInputStream());
            ep.setProperty("mainfile", mainFile.getAbsolutePath());
            lock = targetBuildSettings.lock();
            ep.store(targetBuildSettings.getOutputStream(lock));
        } finally {
            if (lock != null)
                lock.releaseLock();
        }
        
        return metadataDirFO;
    }
}
