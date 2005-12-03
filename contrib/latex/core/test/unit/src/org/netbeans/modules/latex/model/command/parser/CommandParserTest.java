/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.model.command.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class CommandParserTest extends NbTestCase {
    
    public CommandParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
    }

    public void testInclude() throws Exception {
        Collection errors = performTest("testInclude.tex");
        
        assertTrue("Errors: " + errors, errors.isEmpty());
    }
    
    private Collection performTest(String testFileName) throws Exception {
        File testFile = new File(new File(getDataDir(), "CommandParserTest"), testFileName);
        FileObject testFileObject = FileUtil.toFileObject(testFile);
        
        assertNotNull("The test file " + testFileName + " translated to " + testFile.getPath() + " was not found on the filesystems.", testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        return errors;
    }
    
}
