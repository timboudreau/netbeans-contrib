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
import javax.naming.*;
import javax.swing.JPopupMenu;

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
import org.openide.util.Utilities;

import org.netbeans.api.enode.ExtensibleNode;

/** 
 * This test should verify that the functionality of methods
 * <code>ExtensibleNode.getActions(...)</code> is correct.
 * @author David Strupl
 */
public class ExtensibleActionsTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;
    /** root folder FileObject for the lookup tests*/
    private FileObject rootLookup;

    public ExtensibleActionsTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleActionsTest.class));
    }

    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
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
        
        String baseFolderLookup = ExtensibleNode.E_NODE_LOOKUP.substring(1, ExtensibleNode.E_NODE_LOOKUP.length()-1);
        rootLookup = Repository.getDefault().getDefaultFileSystem().findResource(baseFolderLookup);
        if (rootLookup == null) {
            rootLookup = root.getParent().createFolder(baseFolderLookup.substring(baseFolderLookup.lastIndexOf('/')+1));
        }
    }
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
        root.getParent().delete();
        rootLookup.getParent().delete();
    }
    
    /**
     * This test tests the presence of declarative actions from
     * system file system without the hierarchical flag set (the ExtensibleNode
     * instance is created with constructor ExtensibleNode("test", false).
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create one action in the testing folder
     *     <LI> The action should be visible in the result of getActions
     *     <LI> After deleting the action from the folder the action should
     *          not be returned from getActions().
     * </OL>
     */
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
    
    /**
     * This test tests the presence of declarative actions from
     * system file system with the hierarchical flag set (the ExtensibleNode
     * instance is created with constructor ExtensibleNode("test/t1", true).
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set as "test/t1"
     *     <LI> No actions should be returned from getActions now since the
     *          testing folders are not there
     *     <LI> Create one action in the folder "test"
     *     <LI> The action should be visible in the result of getActions because
     *          although the node has the folder set to "test/t1" with the hierarchical
     *          behaviour the content of folder "test" should be also returned
     *          from this ExtensibleNode
     *     <LI> Create another action in folder "test/t1"
     *     <LI> The second action should be also visible as in getActions together
     *          with the first one - now there should be total of two actions
     *     <LI> After deleting the actions from theirs folders the actions should
     *          not be returned from getActions().
     * </OL>
     */
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
    
    /**
     * An attempt to create a simple stress test. Just calls
     * the <code>testCreateAndDeleteAction</code> 100 times.
     */
    public void testRepetitiveDeleting() throws Exception {
        for (int i = 0; i < 100; i++) {
            testCreateAndDeleteAction();
        }
    }
    
    /**
     * This test should test behaviour of the getActions method when
     * there is some alien object specified in the configuration folder.
     * The testing object is of type Integer (instead of javax.swing.Action).
     */
    public void testWrongActionObjectInConfig() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        FileObject a1 = test.createData("java-lang-String.instance");
        Action [] res = en1.getActions(false);
        assertEquals("There should be zero actions.", 0, res.length);        
    }
    
    /**
     * This test checks whether the JSeparator added from the configuration
     * file is reflected in the resulting popup.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create two actions in the testing folder separated by JSeparator
     *     <LI> getActions should return 3 elements - null element for the separator
     *     <LI> Popup is created from the actions array - the null element
     *              should be replaced by a JSeparator again
     * </OL>
     */
    public void testAddingSeparators() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        FileObject a1 = test.createData("1[org-openide-actions-PropertiesAction].instance");
        FileObject sep = test.createData("2[javax-swing-JSeparator].instance");
        FileObject a2 = test.createData("3[org-openide-actions-CutAction].instance");
        javax.swing.Action[] actions = en1.getActions(false);
        assertEquals("Actions array should contain 3 elements", 3, actions.length);
        assertNull("separator should create null element in the array", actions[1]);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 3 components", 3, jp.getComponentCount());
        assertTrue("Separator should be second", jp.getComponent(1) instanceof javax.swing.JSeparator);
    }
    
    /**
     * This test checks whether adding folder from the configuration
     * file is reflected in the resulting popup as a submenu.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create a subfolder of the config folder containing two acitons files
     *     <LI> Check whether the folder is represented by an action
     *     <LI> Convert the action to popup
     *     <LI> The popup should contain one element (JMenu)
     *     <LI> The nested JMenu should have two subelements
     *     <LI> The text on the menu should be the name of the submenu folder
     * </OL>
     */
    public void testSubMenuBehaviour() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        FileObject sub = test.getFileObject("SubMenuSub Menu");
        if (sub == null) {
            sub = test.createFolder("SubMenuSub Menu");
        }
        FileObject a1 = sub.createData("org-openide-actions-PropertiesAction.instance");
        FileObject a2 = sub.createData("org-openide-actions-CutAction.instance");
        javax.swing.Action[] actions = en1.getActions(false);
        assertEquals("Actions array should contain 1 element", 1, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 1 component", 1, jp.getComponentCount());
        assertTrue("The component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
        assertEquals("Submenu should contain two elements", 2, jm.getMenuComponentCount());
        assertEquals("Submenu should have correct name", "Sub Menu", jm.getText());
    }
    
    /**
     * This test tests the presence of declarative actions from
     * system file system configured for declarative cookie instances.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Cookie.instance is added to the lookup folder
     *          and Cookie folder is added to the actions folder
     *     <LI> Create one action in the Cookie folder
     *     <LI> The action should be visible in the result of getActions
     *     <LI> After deleting the cookie the action should
     *          not be returned from getActions().
     * </OL>
     */
    public void testCreateAndDeleteActionForCookie() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertEquals("No actions at the start", 0, en1.getActions(false).length);
        FileObject test = rootLookup.getFileObject("test");
        if (test == null) {
            test = rootLookup.createFolder("test");
        }
        FileObject cFolder = root.getFileObject("Cookie");
        if (cFolder == null) {
            cFolder = root.createFolder("Cookie");
        }
        FileObject ck = test.createData("Cookie.instance");
        FileObject a1 = cFolder.createData("org-openide-actions-PropertiesAction.instance");
        Action [] res = en1.getActions(false);
        assertEquals("There should be exactly one action.", 1, res.length);
        ck.delete();
        assertEquals("No actions after deleting cookie", 0, en1.getActions(false).length);
    }
}
