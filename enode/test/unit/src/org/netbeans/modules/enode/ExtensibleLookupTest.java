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

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.cookies.InstanceCookie;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.spi.enode.LookupContentFactory;
import org.netbeans.modules.enode.test.*;

/**
 * Tests contained in this class should cover the content
 * of the lookup obtained as <code>ExtensibleNode.getLookup()</code>.<p>
 * This class uses classes from package test as testing
 * classes for the content of the lookup.
 * @author David Strupl
 */
public class ExtensibleLookupTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;

    public ExtensibleLookupTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleLookupTest.class));
    }
    
    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        String baseFolder = ExtensibleNode.E_NODE_LOOKUP.substring(1, ExtensibleNode.E_NODE_LOOKUP.length()-1);
        root = Repository.getDefault().getDefaultFileSystem().findResource(baseFolder);
        if (root == null) {
            String s = baseFolder.substring(0, baseFolder.lastIndexOf('/'));
            FileObject f1 = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(s);
            if (f1 == null) {
                f1 = Repository.getDefault().getDefaultFileSystem().getRoot().createFolder(s);
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
     * Test the ability to get an object from the declarative specification into
     * the content of the lookup <code>ExtensibleNode.getLookup()</code>.
     * The test performs following steps:
     * <OL> <LI> Create an ExtensibleNode with path "test"
     *      <LI> Check the content of its lookup (should be empty)
     *      <LI> Use Filesystems API to create an object on the system file system
     *      in folder "test".
     *      The configuration file tells the lookup to create an instance of MONodeEnhancer
     *      <LI> MONodeEnhancer should be found in the lookup
     *      <LI> Delete the configuration file
     *      <LI> The lookup should not return the object (it was deleted).
     * </OL>
     */
    public void testFindObjectInLookup() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        FileObject a1 = test.createData("cookie1.instance");
        a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
        a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
        a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer");
        assertNotNull("Object not found", en1.getLookup().lookup(MONodeEnhancer.class));
        a1.delete();
        assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
    }

    /**
     * This test is almost the same as <code>testFindObjectInLookup()</code>. The only
     * difference is that the object is created in parent folder of the folder
     * specified as argument to constructor of the ExtensibleNode. In this setup
     * the hierarchical usage of the the folders should be tested.
     */
    public void testMergingContentOfFolders() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test/t2", true);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        FileObject a1 = test.createData("cookie1.instance");
        a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
        a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
        a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer");
        assertNotNull("Object not found", en1.getLookup().lookup(MONodeEnhancer.class));
        
        a1.delete();
        assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
    }
}
