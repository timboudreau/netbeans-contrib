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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
 */

package org.netbeans.modules.propertiestool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import junit.framework.*;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author David Strupl
 */
public class PropertySheetPanelTest extends TestCase {
    
    public PropertySheetPanelTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PropertySheetPanelTest.class);
        return suite;
    }

    /**
     * Test of getNodes method, of class org.netbeans.modules.propertiestool.PropertySheetPanel.
     */
    public void testSetGetNodes() {
        PropertySheetPanel instance = new PropertySheetPanel();
        final PropertyChangeEvent[] propChange = new PropertyChangeEvent[1];
        instance.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                propChange[0] = evt;
            }
        });
        instance.setNodes(new Node[] { new TestNode(Lookup.EMPTY)});
        assertNotNull("there should be prop change", propChange[0]);
        assertEquals("save enabled should have been changed", propChange[0].getPropertyName(), PropertySheetPanel.SAVE_ENABLED);
        assertFalse("save should be disabled", instance.isSaveCancelEnabled());
        Node[] editedNodes = instance.getEditedNodes();
        assertNotNull("the edited nodes should have been created", editedNodes);
        assertTrue("the edited nodes should be our private inner class ", editedNodes[0] instanceof PropertySheetPanel.BatchUpdateNode);
    }

    /**
     * Test of save method, of class org.netbeans.modules.propertiestool.PropertySheetPanel.
     */
    public void testSave() throws Exception {
        Node n = new TestNode(Lookup.EMPTY);
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertEquals("Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        psp.save();
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[1].getValue(), "sss");
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(100));
    }

    /**
     * Test of enableSaveCancel method, of class org.netbeans.modules.propertiestool.PropertySheetPanel.
     */
    public void testEnableDisableSaveCancel() throws Exception {
        Node n = new TestNode(Lookup.EMPTY);
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        assertFalse("save should be disabled", psp.isSaveCancelEnabled());
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        assertTrue("save should be enabled", psp.isSaveCancelEnabled());
        assertEquals("Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        psp.save();
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertFalse("save should be disabled", psp.isSaveCancelEnabled());
    }
    /**
     * Test of save method, of class org.netbeans.modules.propertiestool.PropertySheetPanel.
     */
    public void testCancel() throws Exception {
        Node n = new TestNode(Lookup.EMPTY);
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        assertFalse("save should be disabled", psp.isSaveCancelEnabled());
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        assertTrue("save should be enabled", psp.isSaveCancelEnabled());
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        assertTrue("save should be enabled", psp.isSaveCancelEnabled());
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertTrue("save should be enabled", psp.isSaveCancelEnabled());
        assertEquals("Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        psp.cancel();
        assertFalse("save should be disabled", psp.isSaveCancelEnabled());
        assertEquals("After cancel Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("After cancel Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("After cancel Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
    }
    /**
     * Test of save method, of class org.netbeans.modules.propertiestool.PropertySheetPanel.
     */
    public void testRestoreDefaultValue() throws Exception {
        Node n = new TestNode(Lookup.EMPTY);
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[0].getPropertySets()[0].getProperties()[1].restoreDefaultValue();
        assertEquals("Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("edited should have default ", edited[0].getPropertySets()[0].getProperties()[1].getValue(), "default");
        assertTrue("the default should be indicated", edited[0].getPropertySets()[0].getProperties()[1].isDefaultValue());
    }
}
