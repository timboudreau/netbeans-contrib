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

import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import javax.swing.AbstractAction;
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
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.info.*;

import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;

import org.netbeans.api.adnode.*;


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
public class ANodeTest extends TestCase
implements Singletonizer {
    private ANode instance;

    private ChangeListener listener;
    private Object obj = new Object();

    private Object invokeObject;
    private Method invokeMethod;
    private Object[] invokeArgs;
    private Object invokeReturn;


    private Class isEnabledClass = SubHierarchy.class;
    private boolean isEnabled = false;

    public ANodeTest(String testName) {
        super(testName);


        Adaptor adapt = Adaptors.singletonizer(allClasses(), this);
        Adaptable a = adapt.getAdaptable(obj);
        instance = new ANode(a, adapt);
    }

    protected void setUp() throws Exception {
        
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getName method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testGetName() {
        String expResult = "myne";
        invokeMethod = Identity.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = expResult;

        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of cloneNode method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCloneNode() {
        Node result = instance.cloneNode();
        assertNotNull(result);
        assertEquals("Should be equal", result, instance);
        if (result == instance) {
            fail("Should be different: " + result);
        }
        assertEquals(result.hashCode(), instance.hashCode());
    }

    public void testChildren() throws Exception {
        assertEquals("By default we have no children as we do not have SubHierarchy", Children.LEAF, instance.getChildren());

        isEnabled = true;
        listener.stateChanged(new ChangeEvent(this));

        Node n = instance;
        if (n.getChildren() == Children.LEAF) {
            fail("We should have children now");
        }

        ArrayList al = new ArrayList();
        al.add(Integer.valueOf(3));
        al.add(Integer.valueOf(7));
        invokeReturn = al;
        invokeMethod = SubHierarchy.class.getDeclaredMethod("getChildren");
        invokeObject = obj;

        Node[] arr = n.getChildren().getNodes(true);
        assertEquals("two", 2, arr.length);


        isEnabled = false;
        listener.stateChanged(new ChangeEvent(this));

        Node n2 = n;

        assertEquals("No ch.", Children.LEAF, n2.getChildren());
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
        HelpCtx expResult = new HelpCtx(getClass());

        invokeReturn = expResult;
        invokeObject = obj;
        invokeMethod = HelpCtx.Provider.class.getDeclaredMethods()[0];

        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);

        isEnabledClass = HelpCtx.Provider.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(obj));


        assertNull(instance.getHelpCtx());
    }

    /**
     * Test of canRename method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testCanRename() {
        boolean result = instance.canRename();
        assertTrue("Enabled if we do not return false from isEnabled", result);

        isEnabledClass = Rename.class;
        isEnabled = false;

        result = instance.canRename();
        assertTrue("Still Enabled if we do not fire change", result);

        listener.stateChanged(new ChangeEvent(this));

        result = instance.canRename();
        assertFalse("Disabled finally", result);

        try {
            invokeObject = obj;
            instance.setName("SomeStupidName");
            fail("Rename shall not succeed as it is disabled");
        } catch (IllegalArgumentException ex) {
            // ok
        }

        isEnabled = true;

        listener.stateChanged(new ChangeEvent(this));

        result = instance.canRename();
        assertTrue("Now Enabled", result);

        invokeMethod = Rename.class.getDeclaredMethods()[0];
        instance.setName("Kukuc");

        assertEquals("One argument passed", 1, invokeArgs.length);
        assertEquals("Kukuc", invokeArgs[0]);

        invokeArgs = null;

        IOException e = new IOException("Wrong");
        invokeReturn = e;
        try {
            instance.setName("AnotherName");
            fail("Rename throws exception");
        } catch (IllegalArgumentException ex) {
            assertEquals("Right localized message", e.getLocalizedMessage(), ex.getLocalizedMessage());
        }
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
        Node.PropertySet[] expResult = new Node.PropertySet[0];
        invokeReturn = expResult;
        invokeMethod = SetOfProperties.class.getDeclaredMethods()[0];
        invokeObject = obj;

        Node.PropertySet[] result = instance.getPropertySets();
        assertSame(expResult, result);

        isEnabledClass = SetOfProperties.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(this));


        assertEquals("Empty array", 0, instance.getPropertySets().length);
    }

    /**
     * Test of clipboardCopy method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testClipboardCopy() throws Exception {
        assertTrue(instance.canCopy());

        invokeReturn = new StringSelection("Haha");
        invokeObject = obj;
        invokeMethod = Copy.class.getDeclaredMethods()[0];

        Transferable result = instance.clipboardCopy();
        assertEquals(invokeReturn, result);

        isEnabledClass = Copy.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(this));

        assertFalse(instance.canCopy());
        try {
            instance.clipboardCopy();
            fail("Should throw an exception");
        } catch (IOException ex) {
            // ok
        }
    }

    public void testClipboardCut() throws Exception {
        assertTrue(instance.canCut());

        invokeReturn = new StringSelection("Haha");
        invokeObject = obj;
        invokeMethod = Cut.class.getDeclaredMethods()[0];

        Transferable result = instance.clipboardCut();
        assertEquals(invokeReturn, result);

        isEnabledClass = Cut.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(this));

        assertFalse(instance.canCut());
        try {
            instance.clipboardCut();
            fail("Should throw an exception");
        } catch (IOException ex) {
            // ok
        }
    }

    public void testDrag() throws Exception {
        Transferable expResult = new StringSelection("kuk");

        invokeReturn = expResult;
        invokeMethod = Drag.class.getDeclaredMethods()[0];
        invokeObject = obj;

        Transferable result = instance.drag();
        assertEquals(expResult, result);
    }

    public void testGetPasteTypes() {
        PasteType[] expResult = new PasteType[5];

        invokeReturn = expResult;
        invokeMethod = PasteTypes.class.getDeclaredMethods()[0];
        invokeObject = obj;

        PasteType[] result = instance.getPasteTypes(null);
        assertEquals(expResult, result);
    }

    public void testGetDropType() {
        PasteType expResult = null;

        invokeReturn = expResult;
        invokeMethod = Drop.class.getDeclaredMethods()[0];
        invokeObject = obj;

        PasteType result = instance.getDropType(null, 0, -1);
        assertEquals(expResult, result);
    }

    public void testGetNewTypes() {
        NewType[] expResult = new NewType[5];

        invokeReturn = expResult;
        invokeMethod = NewTypes.class.getDeclaredMethods()[0];
        invokeObject = obj;

        NewType[] result = instance.getNewTypes();
        assertEquals(expResult, result);
    }

    public void testGetCustomizer() {
        assertTrue("we support customizer", instance.hasCustomizer());

        invokeReturn = new java.awt.Button();
        invokeMethod = Customizable.class.getDeclaredMethods()[0];
        invokeObject = obj;

        assertEquals("The right customizer", invokeReturn, instance.getCustomizer());

        isEnabledClass = Customizable.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(obj));

        assertFalse("no more customizer", instance.hasCustomizer());
        assertNull("no customizer", instance.getCustomizer());
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

        try {
            instance.setDisplayName(s);
            fail("Should not be supported");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
    }

    /**
     * Test of setShortDescription method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetShortDescription() {
        String s = "";

        try {
            instance.setShortDescription("");
            fail("Should not be supported");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
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

    public void testGetActions() throws Exception {
        doGetActions(true);
    }

    public void testGetActionsNoContext() throws Exception {
        doGetActions(false);
    }

    private void doGetActions(boolean context) throws Exception {
        class MyA extends AbstractAction {
            public void actionPerformed(ActionEvent e) {
            }
        }

        MyA my = new MyA();
        invokeReturn = my;
        invokeObject = obj;
        invokeMethod = ActionProvider.class.getDeclaredMethod("getPreferredAction");

        assertSame("The prefered action is delegated", invokeReturn, instance.getPreferredAction());

        invokeReturn = new Action[] { my };
        invokeObject = obj;
        invokeMethod = ActionProvider.class.getDeclaredMethod("getActions");

        Action[] result = instance.getActions(context);
        assertEquals("One action returned", 1, result.length);
        assertEquals("It is mine", my, result[0]);

        isEnabledClass = ActionProvider.class;
        isEnabled = false;

        listener.stateChanged(new ChangeEvent(obj));

        assertNull(instance.getPreferredAction());
        result = instance.getActions(context);
        assertNotNull(result);
        assertEquals("Empty", 0, result.length);
    }

    public void testGetDisplayName() {
        String expResult = "myne";
        invokeMethod = DisplayName.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = expResult;

        String result = instance.getDisplayName();
        assertEquals(expResult, result);

        isEnabledClass = DisplayName.class;
        isEnabled = false;
        listener.stateChanged(new ChangeEvent(this));

        invokeMethod = Identity.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = "idmyne";

        assertEquals("Now the display name is taken from identity", "idmyne", instance.getDisplayName());
    }

    public void testGetShortDescription() {
        String expResult = "myne";
        invokeMethod = ShortDescription.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = expResult;

        String result = instance.getShortDescription();
        assertEquals(expResult, result);


        isEnabledClass = ShortDescription.class;
        isEnabled = false;
        listener.stateChanged(new ChangeEvent(this));

        invokeMethod = DisplayName.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = "dispmyne";

        assertEquals("Now the short d. is taken from display name", "dispmyne", instance.getShortDescription());

    }

    public void testToString() {
        String result = instance.toString();

        if (result.indexOf("ANode") == -1) {
            fail("There should be name of the class: " + result);
        }
    }

    public void testDestroy() throws Exception {
        instance.destroy();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetHtmlDisplayName() {
        String expResult = "myne";
        invokeMethod = HtmlDisplayName.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = expResult;

        String result = instance.getHtmlDisplayName();
        assertEquals(expResult, result);


        isEnabledClass = HtmlDisplayName.class;
        isEnabled = false;
        listener.stateChanged(new ChangeEvent(this));

        invokeMethod = null;
        invokeObject = null;
        invokeReturn = null;

        assertNull("If disabled this method returns null", instance.getHtmlDisplayName());
    }

    public void testGetValue() {
        String attributeName = "anything";
        
        Object expResult = null;
        Object result = instance.getValue(attributeName);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetValue() {
        String attributeName = "";
        Object value = null;

        try {
            instance.setValue(attributeName, value);
            fail("Should fail");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
    }

    public void testSetPreferred() {
        boolean preferred = true;

        try {
            instance.setPreferred(preferred);
            fail("Should fail");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
    }

    public void testSetHidden() {
        boolean hidden = true;

        try {
            instance.setHidden(hidden);
            fail("Should fail");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
    }

    /**
     * Test of setExpert method, of class org.netbeans.modules.adnode.ANode.
     */
    public void testSetExpert() {
        boolean expert = true;

        try {
            instance.setExpert(expert);
            fail("Should fail");
        } catch (UnsupportedOperationException ex) {
            // ok
        }
    }

    public void testIsPreferred() {
        boolean expResult = false;
        boolean result = instance.isPreferred();
        assertEquals(expResult, result);
    }

    public void testIsHidden() {
        boolean expResult = false;
        boolean result = instance.isHidden();
        assertEquals(expResult, result);
    }

    public void testIsExpert() {
        boolean expResult = false;
        boolean result = instance.isExpert();
        assertEquals(expResult, result);
    }

    public void testAttributeNames() {
        Enumeration<String> result = instance.attributeNames();
        assertFalse("No attributes", result.hasMoreElements());
    }


    //
    // expected calls
    //

    public boolean isEnabled(Object obj, Class c) {
        if (c == isEnabledClass) {
            return isEnabled;
        }
        return true;
    }

    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        assertEquals(invokeMethod, method);
        assertEquals(invokeObject, obj);

        invokeArgs = args;

        if (invokeReturn instanceof Throwable) {
            throw (Throwable)invokeReturn;
        }

        return invokeReturn;
    }

    public synchronized void addChangeListener(ChangeListener listener) throws TooManyListenersException {
        if (this.listener != null) {
            throw new TooManyListenersException();
        }
        this.listener = listener;
    }

    public synchronized void removeChangeListener(ChangeListener listener) {
        if (this.listener == listener) {
            this.listener = null;
        }
    }

    private static Class[] allClasses() {
        return new Class[] {
            Identity.class, Rename.class, DisplayName.class, HtmlDisplayName.class,
            ShortDescription.class, Customizable.class, HelpCtx.Provider.class,
            ActionProvider.class, Copy.class, Cut.class, SetOfProperties.class,
            Drag.class, NewTypes.class, PasteTypes.class, Drop.class, SubHierarchy.class,

        };
    }
    
}
