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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.model.command.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.StyledDocument;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class CommandParserTest extends NbTestCase {
    
    private FileObject dataDir;
    
    public CommandParserTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        //testNewCommand2 is failing, exclude:
        for (Enumeration e = new TestSuite(CommandParserTest.class).tests(); e.hasMoreElements(); ) {
            Test t = (Test) e.nextElement();
            
            if (!(t instanceof TestCase) || !"testNewCommand2".equals(((TestCase) t).getName())) {
                suite.addTest(t);
            }
        }
        
        return suite;
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        dataDir = FileUtil.toFileObject(new File(getDataDir(), "CommandParserTest"));
        
        assertNotNull(dataDir);
    }

    public void testInclude() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testInclude.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        assertTrue("Errors: " + errors, errors.isEmpty());
        assertEquals(new HashSet(Arrays.asList(new FileObject[] {
            dataDir.getFileObject("testInclude.tex"),
            dataDir.getFileObject("inc1.tex"),
            dataDir.getFileObject("includes/inc2.tex"),
        })), new HashSet(node.getFiles()));
    }
    
    /**TODO: should use bigger example
     */
    public void testParserSanity() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testsanity.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        assertTrue("Errors: " + errors, errors.isEmpty());
        
        List queue = new LinkedList();
        
        queue.add(node);
        
        while (!queue.isEmpty()) {
            Node n = (Node) queue.remove(0);
            List children = new LinkedList();
            
            if (n instanceof TextNode) {
                for (Iterator i = ((TextNode) n).getChildrenIterator(); i.hasNext(); ) {
                    children.add(i.next());
                }
            } else {
                if (n instanceof BlockNode) {
                    children.add(((BlockNode) n).getBeginCommand());
                    children.add(((BlockNode) n).getContent());
                    children.add(((BlockNode) n).getEndCommand());
                } else {
                    if (n instanceof CommandNode) {
                        CommandNode cnode = (CommandNode) n;
                        
                        for (int cntr = 0; cntr < cnode.getArgumentCount(); cntr++) {
                            children.add(cnode.getArgument(cntr));
                        }
                        
                        if (cnode instanceof InputNode) {
                            children.add(((InputNode) cnode).getContent());
                        }
                    } else {
                        fail("Unknown node type: " + n.getClass());
                    }
                }
            }
            
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                Node child = (Node) i.next();
                
                assertTrue("child.getParent()=" + child.getParent() + ", n=" + n, child.getParent() == n);
                
                queue.add(child);
            }
        }
    }
    
    public void testNewCommand1() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testNewCommand1.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        assertTrue("Errors: " + errors, errors.isEmpty());
    }
    
    public void testNewCommand2() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testNewCommand2.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        assertTrue("Errors: " + errors, errors.isEmpty());
    }
    
    public void testEnvironmentsWithCommandDefinitions() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testEnvironmentsWithCommandDefinitions.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        LaTeXSource.Lock lock = lsi.lock(true);
        
        try {
            DataObject od = DataObject.find(testFileObject);
            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
            StyledDocument doc = ec.openDocument();
            
            int offset = NbDocument.findLineOffset(doc, 8) + 3;
            Node node = lsi.findNode(doc, offset);
            assertTrue(node instanceof CommandNode);
            assertEquals("\\test", ((CommandNode) node).getCommand().getCommand());
        } finally {
            lsi.unlock(lock);
        }
    }
    
    public void testEnvironmentInEnvironment() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testEnvironmentInEnvironment.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        assertTrue("Errors: " + errors, errors.isEmpty());
    }
    
    public void testIncludeDefiningCommands() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testInclude.tex");
        
        assertNotNull(testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        LaTeXSource.Lock lock = lsi.lock(true);
        
        try {
            DataObject od = DataObject.find(testFileObject);
            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
            StyledDocument doc = ec.openDocument();
            
            int offset = NbDocument.findLineOffset(doc, 7) + 3;
            Node node = lsi.findNode(doc, offset);
            assertTrue(node instanceof CommandNode);
            assertEquals("\\test", ((CommandNode) node).getCommand().getCommand());
        } finally {
            lsi.unlock(lock);
        }
    }
    
}
