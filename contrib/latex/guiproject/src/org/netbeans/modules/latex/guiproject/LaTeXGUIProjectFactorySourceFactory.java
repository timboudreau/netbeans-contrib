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

import java.io.IOException;
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
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectFactorySourceFactory extends LaTeXSourceFactory {
    
//    private LaTeXGUIProjectFactory factory;
    /*package private*/ Map mainFile2Project;
    
    /** Creates a new instance of LaTeXGUIProjectFactory */
    public LaTeXGUIProjectFactorySourceFactory() {
        mainFile2Project = new WeakHashMap();
    }
    
    private static LaTeXGUIProjectFactorySourceFactory instance = null;
    
    public synchronized static LaTeXGUIProjectFactorySourceFactory instanceCreate() {
//        Thread.dumpStack();
        if (instance == null) {
            instance = new LaTeXGUIProjectFactorySourceFactory();
        }
        
        return instance;
    }
    
    public boolean supports(Object file) {
        return file instanceof FileObject;
    }
    
    public LaTeXSource get(Object file) {
        LaTeXGUIProject p = findProject(file);
        
        if (p != null)
            return p.getSource();
        else
            return null;
    }
    
    private LaTeXGUIProject findProject(Object file) {
//        System.err.println("findProject");
//        System.err.println("file = " + file );
        LaTeXGUIProject mainProj = (LaTeXGUIProject) mainFile2Project.get(file);

//        System.err.println("mainProj = " + mainProj );
        if (mainProj != null)
            return mainProj;
        
        //TODO: is this fast enough?:
        for (Iterator i = mainFile2Project.values().iterator(); i.hasNext(); ) {
            LaTeXGUIProject p = (LaTeXGUIProject) i.next();
            
//            System.err.println("p = " + p );
            DocumentNode dn = p.getSource().getDocument(); //TODO:no locking? (currently intentionally)
            
//            System.err.println("dn = " + dn );
            if (dn != null) {
                if (dn.getFiles().contains(file))
                    return p;
            }
        }
        return null;
    }
    
    public boolean isKnownFile(Object file) {
        return findProject(file) != null;
    }
    
    public boolean isMainFile(Object file) {
        return mainFile2Project.get(file) != null;
    }
    
    /*package private*/ void projectLoad(LaTeXGUIProject project, FileObject master) {
        mainFile2Project.put(master, project);
//        System.err.println("adding project: " + project + ", master file: " + master);
    }

}
