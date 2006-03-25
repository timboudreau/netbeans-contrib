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

import java.lang.reflect.Method;
import java.util.TooManyListenersException;
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


    private Class isEnabledClass;
    private boolean isEnabled;

    public ANodeTest(String testName) {
        super(testName);


        Adaptor adapt = Adaptors.singletonizer(allClasses(), this);
        Adaptable a = adapt.getAdaptable(obj);
        instance = new ANode(a);
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

    public void testGetActions() {
        boolean context = true;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(context);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    public boolean isEnabled(Class c) {
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
            ShortDescription.class, Customizable.class, HelpCtx.Provider.class
        };
    }
    
}
