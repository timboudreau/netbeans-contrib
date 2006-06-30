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
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXFileOwnerQueryTest extends ProjectTestCase {
    
    /** Creates a new instance of LaTeXFileOwnerQueryTest */
    public LaTeXFileOwnerQueryTest(String name) {
        super(name);
    }
    
//    static {
//        Class c = UnitUtilities.class;
//    }
    
//        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/guiproject/resources/mf-layer.xml"}, new Object[] {
//            new LaTeXGUIProjectFactory(),
//            new LaTeXGUIProjectFactorySourceFactory(),
//	    new LaTeXFileOwnerQuery(),
//        });
        
    private void checkFile(FileObject prj, FileObject mainFile, String name) {
        FileObject file = prj.getFileObject(name);
        
        if (file == null)
            fail("The test file was not found.");
        
        Project p = FileOwnerQuery.getOwner(file);
        
        if (p == null)
            fail("No project corresponding to file: " + name + " found.");
        
        LaTeXSource source = (LaTeXSource) p.getLookup().lookup(LaTeXSource.class);
        
        if (source == null)
            fail("Found project is not LaTeXGUIProject (does not have LaTeXSource in lookup).");
        
        assertTrue("Incorrect project found!", source.getMainFile() /*!!*/ == /*!!*/ mainFile);
    }
    
    public void test_main1_tex_File() {
        checkFile(prj1, main1, "main1.tex");
    }
    
    public void test_included1a_tex_File() {
        checkFile(prj1, main1, "included1a.tex");
    }
    
    public void test_main2_tex_File() {
        checkFile(prj2, main2, "main2.tex");
    }
    
    public void test_included2a_tex_File() {
        checkFile(prj2, main2, "included2a.tex");
    }
    
    public void test_included1b_tex_File() {
        checkFile(prj1, main1, "included1b.tex");
    }
    
    public void test_included2b_tex_File() {
        checkFile(prj2, main2, "included2b.tex");
    }

    public void test_bibdatabase1a_bib_File() {
        checkFile(prj1, main1, "bibdatabase1a.bib");
    }

    public void test_bibdatabase1b_bib_File() {
        checkFile(prj1, main1, "bibdatabase1b.bib");
    }

    public void test_bibdatabase2a_bib_File() {
        checkFile(prj2, main2, "bibdatabase2a.bib");
    }

    public void test_bibdatabase2b_bib_File() {
        checkFile(prj2, main2, "bibdatabase2b.bib");
    }
    
    public void testShouldNotBeFound() {
        assertNull("Some project found for file outside of all projects.", FileOwnerQuery.getOwner(notIncluded));
    }
    
}
