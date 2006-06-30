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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectTestCase extends NbTestCase {
    
    /** Creates a new instance of TestProjectCreator */
    public ProjectTestCase(String name) {
        super(name);
    }
    
    private static FileObject copyFile(String resource, FileObject dest) throws IOException {
        FileLock lock = null;
        InputStream ins = null;
        OutputStream out = null;
        
        try {
            lock = dest.lock();
            ins  = ProjectTestCase.class.getResourceAsStream(resource);
            out  = dest.getOutputStream(lock);
            FileUtil.copy(ins, out);
	    
	    return dest;
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
    }
    
    private static FileObject copyFile(String resource, FileObject dir, String name, String ext) throws IOException {
        return copyFile(resource, dir.createData(name, ext));
    }
    
    protected Collection/*<FileObject>*/ project1Files;
    protected Collection/*<FileObject>*/ project2Files;
    protected FileObject   notIncluded;
    protected FileObject   prj1;
    protected FileObject   main1;
    protected FileObject   prj2;
    protected FileObject   main2;
    protected FileObject   prj1Impl;
    protected FileObject   prj2Impl;
    
    public void setUp() throws IOException, SAXException, PropertyVetoException {
        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/guiproject/resources/mf-layer.xml", "/org/netbeans/modules/latex/resources/mf-layer.xml"}, new Object[] {
            new LaTeXGUIProjectFactory(),
            new LaTeXGUIProjectFactorySourceFactory(),
	    new LaTeXFileOwnerQuery(),
        });
        
        FileObject testDir = UnitUtilities.makeScratchDir(this);
        File workdir = FileUtil.toFile(testDir);
        File project1 = new File(workdir, "1");
        File project2 = new File(workdir, "2");
        
        prj1Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project1, "tex-project-1"), new File(project1, "main1.tex"));
        
        prj1 = prj1Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj1Impl);
        
        prj2Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project2, "tex-project-2"), new File(project2, "main2.tex"));
        
        prj2 = prj2Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj2Impl);
        
        project1Files = new ArrayList();
        project2Files = new ArrayList();
        
        project1Files.add(main1 = copyFile("data/main1.tex", prj1.getFileObject("main1", "tex")));
        project1Files.add(copyFile("data/included1a.tex", prj1, "included1a", "tex"));
        project1Files.add(copyFile("data/included1b.tex", prj1, "included1b", "tex"));
        project1Files.add(copyFile("data/bibdatabase1a.bib", prj1, "bibdatabase1a", "bib"));
        project1Files.add(copyFile("data/bibdatabase1b.bib", prj1, "bibdatabase1b", "bib"));
        project2Files.add(main2 = copyFile("data/main2.tex", prj2.getFileObject("main2", "tex")));
        project2Files.add(copyFile("data/included2a.tex", prj2, "included2a", "tex"));
        project2Files.add(copyFile("data/included2b.tex", prj2, "included2b", "tex"));
        project2Files.add(copyFile("data/bibdatabase2a.bib", prj2, "bibdatabase2a", "bib"));
        project2Files.add(copyFile("data/bibdatabase2b.bib", prj2, "bibdatabase2b", "bib"));
        
        /*project1Files.add(*/copyFile("data/public1.xml", prj1Impl, "public", "xml")/*)*/;
        /*project1Files.add(*/copyFile("data/private1.xml", prj1Impl, "private", "xml")/*)*/;
	
	notIncluded = copyFile("data/notIncluded.tex", testDir, "noIncluded", "tex");
        
        parseProject(prj1Impl);
        parseProject(prj2Impl);
    }
    
    private static void parseProject(FileObject prj) throws IOException {
        LaTeXSource source = ((LaTeXSource) ProjectManager.getDefault().findProject(prj).getLookup().lookup(LaTeXSource.class));
        LaTeXSource.Lock lock = null;
        
//        try {
//            lock = source.lock(true);
//        } finally  {
//            if (lock != null)
//                source.unlock(lock);
//        }
        
//        System.err.println("source=" + source);
        try {
//            System.err.println("ProjectTestCase.parseProject trying to obtain lock");
            lock = source.lock(true);
//            System.err.println("ProjectTestCase.parseProject lock obtained=" + lock);
        } finally {
            if (lock != null) {
//                System.err.println("ProjectTestCase.parseProject unlock the lock");
                source.unlock(lock);
//                System.err.println("ProjectTestCase.parseProject unlocking done");
            } else {
//                System.err.println("ProjectTestCase.parseProject no unlocking (lock == null)");
            }
        }
    }
}
