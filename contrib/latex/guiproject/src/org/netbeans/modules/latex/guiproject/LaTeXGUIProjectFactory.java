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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.project.Project;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectFactory implements ProjectFactory {
    
    /** Creates a new instance of LaTeXGUIProjectFactory */
    public LaTeXGUIProjectFactory() {
//        Thread.dumpStack();
//        mainFile2Project = new WeakHashMap(); /*is weak correct?*/
    }
    
    private static final String TAG = "nbproject-";
    
    public boolean isProject(FileObject projectDirectory) {
//        System.err.println("LaTeXGUIProjectFactory.isProject(" + projectDirectory + ")");
        
        return getMasterFile(projectDirectory) != null;
    }
    
    private FileObject getMasterFile(FileObject projectDirectory) {
//        System.err.println("getMasterFile(" + projectDirectory + ")");
        InputStream ins = null;
        
        try {
            FileObject settings = projectDirectory.getFileObject("build-settings.properties");
            
//            System.err.println("settings = " + settings );
            if (settings == null)
                return null;
            
            EditableProperties p = new EditableProperties();
            
            ins = settings.getInputStream();
            p.load(ins); //TODO: close the stream.
            String mainFileName = p.getProperty("mainfile");
            
            if (mainFileName == null)
                return null;
            
//            System.err.println("mainFileName = " + mainFileName );
            
            File projectDirectoryFile = FileUtil.toFile(projectDirectory);
            File mainFile = new File(projectDirectoryFile, mainFileName); //TODO: well, this forces projectDirectory to have File.
            
//            System.err.println("mainFile = " + mainFile );
//            System.err.println("mainFile = " + mainFile.exists() );
            if (!mainFile.exists())
                mainFile = new File(mainFileName);
            
            mainFile = mainFile.getCanonicalFile();
//            System.err.println("mainFile = " + mainFile );
//            System.err.println("mainFile = " + mainFile.exists() );
//            System.err.println("mainFile = " + FileUtil.toFileObject(mainFile) );
            return FileUtil.toFileObject(mainFile);
        } catch (IOException e) {
            return null;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    //ignore?
                }
            }
        }
    }
    
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (!isProject(projectDirectory))
            return null;
        
        FileObject master = getMasterFile(projectDirectory);
        LaTeXGUIProject p = new LaTeXGUIProject(projectDirectory, master);
        
        LaTeXGUIProjectFactorySourceFactory.instanceCreate().projectLoad(p, master);

        return p;
//        throw new IOException("Xxxx");
    }
    
    public void saveProject(Project project) throws IOException, ClassCastException {
//        throw new ClassCastException("xxx");
//        System.err.println("saveProject called, project=" + project);
    }
    
}
