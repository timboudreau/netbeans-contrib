/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;
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
    
    public synchronized static LaTeXGUIProjectFactorySourceFactory get() {
        for (Iterator i = Lookup.getDefault().lookup(new Lookup.Template(LaTeXSourceFactory.class)).allInstances().iterator(); i.hasNext(); ) {
            LaTeXSourceFactory fact = (LaTeXSourceFactory) i.next();
            
            if (fact instanceof LaTeXGUIProjectFactorySourceFactory) {
                return (LaTeXGUIProjectFactorySourceFactory) fact;
            }
        }
        
        return null;
    }
    
    public boolean supports(Object file) {
        return file instanceof FileObject;
    }
    
    public FileObject findMainFile(FileObject file) {
        LaTeXGUIProject p = findProject(file);
        
        if (p != null)
            return p.getMainFile();
        else
            return null;
    }
    
    private LaTeXGUIProject findProject(Object file) {
//        System.err.println("findProject");
//        System.err.println("file = " + file );
//        System.err.println("mainFile2Project=" + mainFile2Project);
        LaTeXGUIProject mainProj = (LaTeXGUIProject) mainFile2Project.get(file);

//        System.err.println("mainProj = " + mainProj );
        if (mainProj != null)
            return mainProj;
        
        //TODO: is this fast enough?:
        for (Iterator i = mainFile2Project.values().iterator(); i.hasNext(); ) {
            LaTeXGUIProject p = (LaTeXGUIProject) i.next();
            
//            System.err.println("p = " + p );
            if (p.contains((FileObject) file))
                return p;
            
        }
        return null;
    }
    
    public boolean isKnownFile(Object file) {
        return findProject(file) != null;
    }
    
    public boolean isMainFile(Object file) {
        return mainFile2Project.get(file) != null;
    }
    
    public Collection getAllKnownFiles() {
        Collection result = new ArrayList();
        
        //TODO: is this fast enough?:
        for (Iterator i = mainFile2Project.values().iterator(); i.hasNext(); ) {
            LaTeXGUIProject p = (LaTeXGUIProject) i.next();
            
            result.addAll(p.getContainedFiles());
        }
        
        return result;
    }
    
    /*package private*/ void projectLoad(LaTeXGUIProject project, FileObject master) {
        mainFile2Project.put(master, project);
//        System.err.println("adding project: " + project + ", master file: " + master);
    }

}
