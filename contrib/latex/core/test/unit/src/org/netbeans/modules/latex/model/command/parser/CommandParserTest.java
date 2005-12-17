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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class CommandParserTest extends NbTestCase {
    
    private FileObject dataDir;
    
    public CommandParserTest(String testName) {
        super(testName);
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
    
}
