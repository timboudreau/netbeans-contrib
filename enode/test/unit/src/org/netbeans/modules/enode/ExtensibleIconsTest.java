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
 * This class should test the setting icons in the
 * ExtensibleNode.
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
    
    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
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
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
        root.getParent().delete();
    }

    /**
     * This test verifies that the code in ExtensibleNode calls method 
     * <code>AbstractNode.setIconBase()</code> with correct arguments in
     * correct time. However it does not test whether the icon specified
     * is displayed or not - that is job of AbstractNode.<p>
     * Also there is an accessible method setIconBase in AbstractNode but there
     * is no way to get the value of the iconBase. So this test uses
     * reflection to access the private field from AbstractNode.<p>
     * The test does following:
     * <OL><LI> Creates an ExtensibleNode with patch "a/b/c"
     *     <LI> Because the configuration folders are empty (or not present) at this
     *     moment the getIconBase should be null
     *     <LI> A String object is created in folder "a/b" containing the value base1
     *     <LI> "base1" should be the value of the iconBase since the ExtensibleNode
     *     was created to use the hierarchical search and so the file in folder "a/b"
     *     should be taken into account
     *     <LI> A String object with value "base2" is created in folder "a/b/c"
     *     <LI> Now the value of the iconBase should be "base2" since the value in folder
     *     "a/b/c" should override the value in "a/b"
     *  </OL>
     */
    public void testSettingTheIconBase() throws Exception {
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
