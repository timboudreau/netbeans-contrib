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
public class ExtensibleIconsTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;

    public ExtensibleIconsTest(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleIconsTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem();
        String baseFolder = ExtensibleNode.E_NODE_ICONS.substring(1, ExtensibleNode.E_NODE_ICONS.length()-1);
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
    
    public void testFindIcon() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("a/b/c", true);
        java.lang.reflect.Method getIconManagerMethod = ExtensibleNode.class.getDeclaredMethod("getIconManager", new Class[0]);
        getIconManagerMethod.setAccessible(true);
        Object iconMan = getIconManagerMethod.invoke(en1, new Object[0]);
        java.lang.reflect.Method getIconBaseMethod = iconMan.getClass().getMethod("getIconBase", new Class[0]);
        assertNull("No files - no dirs", getIconBaseMethod.invoke(iconMan, new Object[0]));
        FileObject a = root.createFolder("a");
        FileObject b = a.createFolder("b");
        FileObject c = b.createFolder("c");
        String base1 = "base1";
        org.openide.loaders.InstanceDataObject.create(
            org.openide.loaders.DataFolder.findFolder(b), 
            "i1", base1, null);
        assertEquals("i1 in dir b", base1, getIconBaseMethod.invoke(iconMan, new Object[0]));
        String base2 = "base2";
        org.openide.loaders.InstanceDataObject.create(
            org.openide.loaders.DataFolder.findFolder(c),
            "i2", base2, null);
        assertEquals("i2 in dir c", base2, getIconBaseMethod.invoke(iconMan, new Object[0]));
    }
}
