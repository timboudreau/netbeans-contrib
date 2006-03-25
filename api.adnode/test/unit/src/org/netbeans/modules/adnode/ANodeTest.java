/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.adnode;

import junit.framework.TestCase;
import junit.framework.*;
import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.info.Identity;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Jaroslav Tulach
 */
public class ANodeTest extends TestCase {
    private ANode instance;

    public ANodeTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetName() {
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cloneNode method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCloneNode() {
        Node expResult = null;
        Node result = instance.cloneNode();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIcon method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetIcon() {
        int type = 0;
        
        Image expResult = null;
        Image result = instance.getIcon(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOpenedIcon method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetOpenedIcon() {
        int type = 0;
        
        Image expResult = null;
        Image result = instance.getOpenedIcon(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetHelpCtx() {
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canRename method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCanRename() {
        boolean expResult = true;
        boolean result = instance.canRename();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canDestroy method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCanDestroy() {
        boolean expResult = true;
        boolean result = instance.canDestroy();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPropertySets method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetPropertySets() {
        Node.PropertySet[] expResult = null;
        Node.PropertySet[] result = instance.getPropertySets();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clipboardCopy method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testClipboardCopy() throws Exception {
        Transferable expResult = null;
        Transferable result = instance.clipboardCopy();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clipboardCut method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testClipboardCut() throws Exception {
        Transferable expResult = null;
        Transferable result = instance.clipboardCut();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of drag method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testDrag() throws Exception {
        Transferable expResult = null;
        Transferable result = instance.drag();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canCopy method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCanCopy() {
        boolean expResult = true;
        boolean result = instance.canCopy();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canCut method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCanCut() {
        boolean expResult = true;
        boolean result = instance.canCut();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPasteTypes method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetPasteTypes() {
        Transferable t = null;
        
        PasteType[] expResult = null;
        PasteType[] result = instance.getPasteTypes(t);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDropType method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetDropType() {
        Transferable t = null;
        int action = 0;
        int index = 0;
        
        PasteType expResult = null;
        PasteType result = instance.getDropType(t, action, index);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNewTypes method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetNewTypes() {
        NewType[] expResult = null;
        NewType[] result = instance.getNewTypes();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasCustomizer method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testHasCustomizer() {
        boolean expResult = true;
        boolean result = instance.hasCustomizer();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCustomizer method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetCustomizer() {
        Component expResult = null;
        Component result = instance.getCustomizer();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHandle method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetHandle() {
        Node.Handle expResult = null;
        Node.Handle result = instance.getHandle();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDisplayName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetDisplayName() {
        String s = "";
        
        instance.setDisplayName(s);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetName() {
        String s = "";
        
        instance.setName(s);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setShortDescription method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetShortDescription() {
        String s = "";
        
        instance.setShortDescription(s);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCookie method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetCookie() {
        Class type = null;
        
        Node.Cookie expResult = null;
        Node.Cookie result = instance.getCookie(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActions method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetActions() {
        boolean context = true;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(context);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetDisplayName() {
        String expResult = "";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShortDescription method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetShortDescription() {
        String expResult = "";
        String result = instance.getShortDescription();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testToString() {
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of destroy method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testDestroy() throws Exception {
        instance.destroy();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlDisplayName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetHtmlDisplayName() {
        String expResult = "";
        String result = instance.getHtmlDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreferredAction method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetPreferredAction() {
        Action expResult = null;
        Action result = instance.getPreferredAction();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValue method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetValue() {
        String attributeName = "";
        
        Object expResult = null;
        Object result = instance.getValue(attributeName);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValue method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetValue() {
        String attributeName = "";
        Object value = null;
        
        instance.setValue(attributeName, value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPreferred method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetPreferred() {
        boolean preferred = true;
        
        instance.setPreferred(preferred);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHidden method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetHidden() {
        boolean hidden = true;
        
        instance.setHidden(hidden);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExpert method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetExpert() {
        boolean expert = true;
        
        instance.setExpert(expert);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPreferred method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testIsPreferred() {
        boolean expResult = true;
        boolean result = instance.isPreferred();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isHidden method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testIsHidden() {
        boolean expResult = true;
        boolean result = instance.isHidden();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isExpert method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testIsExpert() {
        boolean expResult = true;
        boolean result = instance.isExpert();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of attributeNames method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testAttributeNames() {
        Enumeration<String> expResult = null;
        Enumeration<String> result = instance.attributeNames();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
