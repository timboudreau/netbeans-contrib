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
import java.io.FileOutputStream;


import java.io.IOException;
import java.io.PrintStream;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;





import org.netbeans.junit.NbTestCase;
import org.netbeans.api.project.TestUtil;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;


import org.openide.filesystems.FileLock;


import org.openide.filesystems.FileObject;

import org.openide.filesystems.FileUtil;

import org.openide.filesystems.Repository;

import org.openide.filesystems.XMLFileSystem;

import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXFileOwnerQueryTest extends NbTestCase {
    
    /** Creates a new instance of LaTeXFileOwnerQueryTest */
    public LaTeXFileOwnerQueryTest(String name) {
        super(name);
    }
    
    private void copyFile(String resource, FileObject dest) throws IOException {
        FileLock lock = null;
        
        try {
            lock = dest.lock();
            FileUtil.copy(this.getClass().getResourceAsStream(resource), dest.getOutputStream(lock));
        } finally {
            if (lock != null)
                lock.releaseLock();
        }
    }
    
    private void copyFile(String resource, FileObject dir, String name, String ext) throws IOException {
        copyFile(resource, dir.createData(name, ext));
    }
    
    private FileObject main1;
    private FileObject main2;
    private FileObject prj1;
    private FileObject prj2;
    
    public void setUp() throws IOException, SAXException {
        TestUtil.setLookup(new Object[] {
            new Repository(new XMLFileSystem(this.getClass().getResource("/org/netbeans/modules/latex/guiproject/resources/mf-layer.xml"))),
            new LaTeXGUIProjectFactory(),
            new LaTeXGUIProjectFactorySourceFactory(),
        }, this.getClass().getClassLoader());
        System.setErr(new PrintStream(new FileOutputStream("/tmp/unit-test-log.txt")));
        getRef().println("asdfasdf");
	getLog().println("setUp");
        getLog().flush();
//        clearWorkDir();
        
        FileObject testDir = TestUtil.makeScratchDir(this);
        File workdir = FileUtil.toFile(testDir);
        File project1 = new File(workdir, "1");
        File project2 = new File(workdir, "2");
        
        FileObject prj1Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project1, "tex-project-1"), new File(project1, "main1.tex"));
        
        prj1 = prj1Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj1Impl);
        
        FileObject prj2Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project2, "tex-project-2"), new File(project2, "main2.tex"));
        
        prj2 = prj2Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj2Impl);
        
        copyFile("data/main1.tex", main1 = prj1.getFileObject("main1", "tex"));
        copyFile("data/included1a.tex", prj1, "included1a", "tex");
        copyFile("data/included1b.tex", prj1, "included1b", "tex");
        copyFile("data/bibdatabase1a.bib", prj1, "bibdatabase1a", "bib");
        copyFile("data/bibdatabase1b.bib", prj1, "bibdatabase1b", "bib");
        copyFile("data/main2.tex", main2 = prj2.getFileObject("main2", "tex"));
        copyFile("data/included2a.tex", prj2, "included2a", "tex");
        copyFile("data/included2b.tex", prj2, "included2b", "tex");
        copyFile("data/bibdatabase2a.bib", prj2, "bibdatabase2a", "bib");
        copyFile("data/bibdatabase2b.bib", prj2, "bibdatabase2b", "bib");
        
        parseProject(prj1Impl);
        parseProject(prj2Impl);
    }
    
    private void checkFile(FileObject prj, FileObject mainFile, String name) {
        FileObject file = prj.getFileObject(name);
        
        if (file == null)
            fail("The test file was not found.");
        
        Project p = FileOwnerQuery.getOwner(file);
        
        if (p == null)
            fail("No project corresponding to file: " + name + " not found.");
        
        LaTeXSource source = (LaTeXSource) p.getLookup().lookup(LaTeXSource.class);
        
        if (source == null)
            fail("Found project is not LaTeXGUIProject (does not have LaTeXSource in lookup).");
        
        assertTrue("Incorrect project found!", source.getMainFile() /*!!*/ == /*!!*/ mainFile);
    }
    
    public void testLaTeXFileAreCorrect() {
        checkFile(prj1, main1, "included1a.tex");
        checkFile(prj2, main2, "included1a.tex");
        checkFile(prj1, main1, "included1b.tex");
        checkFile(prj2, main2, "included1b.tex");
    }
    
    private void parseProject(FileObject prj) throws IOException {
        LaTeXSource source = ((LaTeXSource) ProjectManager.getDefault().findProject(prj).getLookup().lookup(LaTeXSource.class));
        LaTeXSource.Lock lock = null;
        
        try {
            lock = source.lock(true);
        } finally  {
            if (lock != null)
                source.unlock(lock);
        }
        
    }
}
