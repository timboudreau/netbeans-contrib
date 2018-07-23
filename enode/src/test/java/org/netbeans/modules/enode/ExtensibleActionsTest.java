/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.enode;

import java.util.Arrays;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.modules.ModuleInfo;
import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.api.registry.*;

/**
 * This test should verify that the functionality of methods
 * <code>ExtensibleNode.getActions(...)</code> is correct.
 * @author David Strupl
 */
public class ExtensibleActionsTest extends NbTestCase {
    /** root Context */
    private Context root;
    /** root Context for the lookup tests*/
    private Context rootLookup;
    /** submenu Context for the lookup tests*/
    private Context submenu;
    
    private int i = 0;

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
        String baseFolder = ExtensibleNode.E_NODE_ACTIONS.substring(1, ExtensibleNode.E_NODE_ACTIONS.length()-1);
        root = Context.getDefault().createSubcontext(baseFolder);
        String baseFolderLookup = ExtensibleNode.E_NODE_LOOKUP.substring(1, ExtensibleNode.E_NODE_LOOKUP.length()-1);
        rootLookup = Context.getDefault().createSubcontext(baseFolderLookup);
        String submenuFolder = ExtensibleNode.E_NODE_SUBMENUS.substring(1, ExtensibleNode.E_NODE_SUBMENUS.length()-1);
        submenu = Context.getDefault().createSubcontext(submenuFolder);
    }
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
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
        try {
            ExtensibleNode en1 = new ExtensibleNode("test", false);
            assertEquals("No actions at the start " + i, 0, en1.getActions(false).length);
            Context test = root.createSubcontext("test");

            SystemAction sa = SystemAction.get(org.openide.actions.PropertiesAction.class);
            test.putObject("ttt", sa);
            Action [] res = en1.getActions(false);
            assertEquals("There should be exactly one action. " + i , 1, res.length);
            test.putObject("ttt", null);
            assertEquals("No actions after deleting " + i, 0, en1.getActions(false).length);
        } finally {
            root.destroySubcontext("test");
        }
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
        try {
            ExtensibleNode en1 = new ExtensibleNode("test/t1", true);
            assertEquals("No actions at the start", 0, en1.getActions(false).length);
            Context test = root.createSubcontext("test");

            SystemAction sa = SystemAction.get(org.openide.actions.PropertiesAction.class);
            test.putObject("ttt", sa);
            Action [] res = en1.getActions(false);
            assertEquals("There should be exactly one action.", 1, res.length);
            Context t1 = test.createSubcontext("t1");
            SystemAction a2 = SystemAction.get(org.openide.actions.CutAction.class);
            t1.putObject("t2", sa);
            assertEquals("There should 2 actions.", 2, en1.getActions(false).length);

            test.putObject("ttt", null);
            assertEquals("There should be one after first delete.", 1, en1.getActions(false).length);

            t1.putObject("t2", null);
            assertEquals("No actions after deleting both", 0, en1.getActions(false).length);
        } finally {
            root.destroySubcontext("test");
        }
    }
    
    /**
     * An attempt to create a simple stress test. Just calls
     * the <code>testCreateAndDeleteAction</code> 100 times.
     */
    public void testRepetitiveDeleting() throws Exception {
        for (i = 0; i < 100; i++) {
            testCreateAndDeleteAction();
            Thread.sleep(100);
        }
    }
    
    /**
     * This test should test behaviour of the getActions method when
     * there is some alien object specified in the configuration folder.
     * The testing object is of type Integer (instead of javax.swing.Action).
     */
    public void testWrongActionObjectInConfig() throws Exception {
        try {
            ExtensibleNode en1 = new ExtensibleNode("test", false);
            assertEquals("No actions at the start", 0, en1.getActions(false).length);
            Context test = root.createSubcontext("test");
            test.putObject("ttt", "foobar");
            Action [] res = en1.getActions(false);
            assertEquals("There should be zero actions.", 0, res.length);        
        } finally {
            root.destroySubcontext("test");
        }
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
        try {
            ExtensibleNode en1 = new ExtensibleNode("test", false);
            assertEquals("No actions at the start", 0, en1.getActions(false).length);
            Context test = root.createSubcontext("test");

            SystemAction a1 = SystemAction.get(org.openide.actions.PropertiesAction.class);
            Object sep = new javax.swing.JSeparator();
            SystemAction a2 = SystemAction.get(org.openide.actions.CutAction.class);
            test.putObject("a1", a1);
            test.putObject("sep", sep);
            test.putObject("a2", a2);
            test.orderContext(Arrays.asList(new String[] { "a1", "sep", "a2" } ));

            javax.swing.Action[] actions = en1.getActions(false);
            assertEquals("Actions array should contain 3 elements", 3, actions.length);
            assertNull("separator should create null element in the array but created 1." + actions[0] + " 2. " + actions[1] + " 3. " + actions[2], actions[1]);
            JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
            assertEquals("Popup should contain 3 components", 3, jp.getComponentCount());
            assertTrue("Separator should be second", jp.getComponent(1) instanceof javax.swing.JSeparator);
        } finally {
            root.destroySubcontext("test");
        }
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
            ExtensibleNode en1 = new ExtensibleNode("test1", false);
            Action[] actions = en1.getActions(false);
            if (actions.length == 1) {
                fail("actions only contains " + actions[0]);
            }
            assertEquals("Actions array should contain 2 elements ", 2, actions.length);
            JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
            assertEquals("Popup should contain 2 components", 2, jp.getComponentCount());
            assertTrue("The first component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
            javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
            assertEquals("Submenu should contain two elements", 2, jm.getMenuComponentCount());
            assertEquals("Submenu should have correct name", "Sub Menu1", jm.getText());
    }

    /**
     * This test checks whether the JSeparator added from the configuration
     * file is reflected in the resulting popup.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create two actions in the testing folder separated by JSeparator
     *     <LI> getActions should return 1 elements - the submenu
     *     <LI> Popup is created from the actions array - the separator should
     *          come second according to the order.
     * </OL>
     */
    public void testAddingSeparatorsToSubMenu() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test2", false);
        javax.swing.Action[] actions = en1.getActions(false);
        assertEquals("Actions array should contain 1 element", 1, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 1 component", 1, jp.getComponentCount());
        assertTrue("The component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
        assertEquals("Submenu should contain 3 elements", 3, jm.getMenuComponentCount());
        assertTrue("Separator should be second", jm.getMenuComponent(1) instanceof javax.swing.JSeparator);
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
        try {
            ExtensibleNode en1 = new ExtensibleNode("test", false);
            assertEquals("No actions at the start", 0, en1.getActions(false).length);
            Context test = rootLookup.createSubcontext("test");
            Context cFolder = root.createSubcontext("Cookie");

            SystemAction a1 = SystemAction.get(org.openide.actions.PropertiesAction.class);
            cFolder.putObject("a1", a1);
            test.putObject("Cookie", "brumbrum");

            Action [] res = en1.getActions(false);
            assertEquals("There should be exactly one action.", 1, res.length);
            test.putObject("Cookie", null);
            assertEquals("No actions after deleting cookie", 0, en1.getActions(false).length);
        } finally {
            rootLookup.destroySubcontext("test");
            root.destroySubcontext("Cookie");
        }
    }
    
    /**
     * This test should ensure that when the user selects more nodes in the explorer
     * that share common submenu the resulting submenu is really shown.
     * The tests performs following steps:
     * <OL><LI> Create two instances of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create a subfolder of the config folder containing two acitons files
     *     <LI> Check whether the folder is represented by an action when both nodes
     *          are selected
     *     <LI> Convert the action to popup
     *     <LI> The popup should contain one element (JMenu)
     * </OL>
     */
    public void testSubMenuOnMoreSelectedNodes() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test2", false);
        ExtensibleNode en2 = new ExtensibleNode("test2", false);

        Action[] actions = NodeOp.findActions(new Node[] { en1, en2 });
        assertEquals("Actions array should contain 1 element", 1, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.fixed(new Node[] { en1, en2 }));
        assertEquals("Popup should contain 1 component", 1, jp.getComponentCount());
        assertTrue("The component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
        assertEquals("Submenu should contain 3 elements", 3, jm.getMenuComponentCount());
    }
    
    /**
     * This test should ensure that when the user selects more nodes in the explorer
     * that share common submenu the resulting submenu is really shown.
     * The tests performs following steps:
     * <OL><LI> Create two instances of ExtensibleNode with folder set to "test"
     *     <LI> No actions should be returned by getActions since the "test" folder
     *          is not there
     *     <LI> Create a subfolder of the config folder containing two acitons files
     *     <LI> Check whether the folder is represented by an action when both nodes
     *          are selected
     *     <LI> Convert the action to popup
     *     <LI> The popup should contain one element (JMenu)
     * </OL>
     */
    public void testSubMenuShadow() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test5", false);
        ExtensibleNode en2 = new ExtensibleNode("test5", false);

        Action[] actions = NodeOp.findActions(new Node[] { en1, en2 });
        assertEquals("Actions array should contain 1 element", 1, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.fixed(new Node[] { en1, en2 }));
        assertEquals("Popup should contain 1 component", 1, jp.getComponentCount());
        assertTrue("The component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
        assertEquals("Submenu should contain 1 element", 1, jm.getMenuComponentCount());
        JMenuItem jmi = (JMenuItem)jm.getMenuComponent(0);
        assertTrue(jmi.isEnabled());
    }
    
    /**
     * This test checks whether adding folder from the configuration
     * file is reflected in the resulting popup as a submenu.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "Foo"
     *     <LI> Check whether the folder is represented by an action
     *     <LI> Convert the action to popup
     *     <LI> The popup should contain one element (JMenu)
     *     <LI> The nested JMenu should have one subelements
     * </OL>
     */
    public void testOrderOfActions() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode(Children.LEAF,"TPFVWC_View/View", true);
        javax.swing.Action[] actions = NodeOp.findActions(new Node[] { en1 });
        assertEquals("Actions array should contain 2 elements ", 2, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 2 components", 2, jp.getComponentCount());
        assertEquals("The second component should be Delete", "Delete", ((JMenuItem)jp.getComponent(1)).getText());
        
        ExtensibleNode en2 = new ExtensibleNode(Children.LEAF,"TPFVWC_View/ViewFolder", true);
        javax.swing.Action[] actions2 = NodeOp.findActions(new Node[] { en2 });
        assertEquals("Actions array should contain 2 elements ", 2, actions2.length);
        JPopupMenu jp2 = Utilities.actionsToPopup(actions2, org.openide.util.lookup.Lookups.singleton(en2));
        assertEquals("Popup should contain 2 components", 2, jp2.getComponentCount());
        assertEquals("The second component should be Delete", "Delete", ((JMenuItem)jp2.getComponent(1)).getText());
    }
    /**
     * This test checks whether the JSeparator added from the configuration
     * file is reflected in the resulting popup.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test3"
     *     <LI> Create two actions in the testing folder separated by JSeparator
     *     <LI> getActions should return 1 elements - the submenu
     *     <LI> Popup is created from the actions array - the separator should
     *          come second according to the order.
     * </OL>
     */
    public void testSubSubMenu() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test3", false);
        javax.swing.Action[] actions = en1.getActions(false);
        assertEquals("Actions array should contain 1 element", 1, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 1 component", 1, jp.getComponentCount());
        assertTrue("The component should be menu", jp.getComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(0);
        assertEquals("Menu should have display name Nice", "Nice", jm.getText());
        
        assertEquals("Menu should contain 1 component", 1, jm.getMenuComponentCount());
        assertTrue("The component should be menu", jm.getMenuComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm2 = (javax.swing.JMenu)jm.getMenuComponent(0);
        assertEquals("Menu should have display name Localized", "Localized", jm2.getText());
        
        assertEquals("Menu2 should contain 1 component", 1, jm2.getMenuComponentCount());
        assertTrue("The component should be menu", jm2.getMenuComponent(0) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm3 = (javax.swing.JMenu)jm2.getMenuComponent(0);
        assertEquals("Menu should have display name Menu", "Menu", jm3.getText());
        
        assertEquals("Submenu should contain 3 elements", 3, jm3.getMenuComponentCount());
        assertTrue("Separator should be second", jm3.getMenuComponent(1) instanceof javax.swing.JSeparator);
    }

    /**
     * This test checks whether the JSeparator added from the configuration
     * file is reflected in the resulting popup.
     * The tests performs following steps:
     * <OL><LI> Create an instance of ExtensibleNode with folder set to "test3"
     *     <LI> Create two actions in the testing folder separated by JSeparator
     *     <LI> getActions should return 1 elements - the submenu
     *     <LI> Popup is created from the actions array - the separator should
     *          come second according to the order.
     * </OL>
     */
    public void testMoreSeparators() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test4", false);
        javax.swing.Action[] actions = en1.getActions(false);
        assertEquals("Actions array should contain 6 elements", 6, actions.length);
        JPopupMenu jp = Utilities.actionsToPopup(actions, org.openide.util.lookup.Lookups.singleton(en1));
        assertEquals("Popup should contain 6 components", 6, jp.getComponentCount());
        assertTrue("The 2nd component should be separator", jp.getComponent(1) instanceof javax.swing.JSeparator);
        assertTrue("The 5nd component should be separator", jp.getComponent(4) instanceof javax.swing.JSeparator);
        
        assertTrue("The 3rd component should be menu", jp.getComponent(2) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm = (javax.swing.JMenu)jp.getComponent(2);
        
        assertEquals("Menu should contain 6 components", 6, jm.getMenuComponentCount());
        assertTrue("The 2nd component should be separator", jm.getMenuComponent(1) instanceof javax.swing.JSeparator);
        assertTrue("The 5nd component should be separator", jm.getMenuComponent(4) instanceof javax.swing.JSeparator);
        assertTrue("The 3rd component should be menu", jm.getMenuComponent(2) instanceof javax.swing.JMenu);
        javax.swing.JMenu jm2 = (javax.swing.JMenu)jm.getMenuComponent(2);
        
        assertEquals("Menu2 should contain 5 components", 5, jm2.getMenuComponentCount());
        assertTrue("The 2nd component should be separator", jm2.getMenuComponent(1) instanceof javax.swing.JSeparator);
        assertTrue("The 4nd component should be separator", jm2.getMenuComponent(3) instanceof javax.swing.JSeparator);
    }
    
    /**
     *
     */
    public void testfindExistingContext() throws Exception {
        try {
            String baseFolder = ExtensibleNode.E_NODE_ACTIONS.substring(1, ExtensibleNode.E_NODE_ACTIONS.length()-1);
            Context test = root.createSubcontext("test");
            Context res = ExtensibleLookupImpl.findExistingContext(baseFolder + "/test/ahoj");
            String s = res.getAbsoluteContextName();
            assertEquals("Should find the test folder ", ExtensibleNode.E_NODE_ACTIONS+"test", s);
            Context res2 = ExtensibleLookupImpl.findExistingContext(baseFolder + "/test/ahoj/booooom/ba");
            String s2 = res2.getAbsoluteContextName();
            assertEquals("Should find the test folder ", ExtensibleNode.E_NODE_ACTIONS+"test", s2);
        } finally {
            root.destroySubcontext("test");
        }
    }
}
    

