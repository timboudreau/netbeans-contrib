/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import javax.swing.Action;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

import org.netbeans.api.enode.ExtensibleNode;

/** 
 * @author David Strupl
 */
public class ExtensibleActionsTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;

    public ExtensibleActionsTest(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleActionsTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem();
        String baseFolder = ExtensibleNode.E_NODE_ACTIONS.substring(1, ExtensibleNode.E_NODE_ACTIONS.length()-1);
        root = dfs.findResource(baseFolder);
        if (root == null) {
            String s1 = baseFolder.substring(0, baseFolder.lastIndexOf('/'));
            FileObject f1 = dfs.findResource(s1);
            if (f1 == null) {
                f1 = dfs.getRoot().createFolder(s1);
            } 
            root = f1.createFolder(baseFolder.substring(baseFolder.lastIndexOf('/')+1));
        }
    }
    
    protected void tearDown() throws Exception {
        root.getParent().delete();
    }
    
    public void testCreateAndDeleteAction() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        FileObject a1 = test.createData("org-openide-actions-PropertiesAction.instance");
        Action [] res = en1.getActions(false);
        assertEquals("There should be exactly one action.", 1, res.length);
        a1.delete();
        assertEquals("No actions after deleting", 0, en1.getActions(false).length);
    }
    
    public void testHierarchicalBehaviour() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test/t1", true);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = root.createFolder("test");
        FileObject a1 = test.createData("org-openide-actions-PropertiesAction.instance");
        Action [] res = en1.getActions(false);
        assertEquals("There should be exactly one action.", 1, res.length);
        FileObject t1 = test.createFolder("t1");
        FileObject a2 = t1.createData("org-openide-actions-CutAction.instance");
        assertEquals("There should 2 actions.", 2, en1.getActions(false).length);
        
        a1.delete();
        assertEquals("There should be one after first delete.", 1, en1.getActions(false).length);
        
        a2.delete();
        assertEquals("No actions after deleting both", 0, en1.getActions(false).length);
    }
    
    public void testRepetitiveDeleting() throws Exception {
        for (int i = 0; i < 100; i++) {
            testCreateAndDeleteAction();
        }
    }
}
