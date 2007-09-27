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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
package org.netbeans.modules.adnode;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.Action;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.Facets.*;
import org.netbeans.api.adnode.NodeFacets.Customizable;
import org.netbeans.api.adnode.NodeFacets.Drop;
import org.netbeans.api.adnode.NodeFacets.NewTypes;
import org.netbeans.api.adnode.NodeFacets.PasteTypes;
import org.netbeans.api.adnode.NodeFacets.SetOfProperties;
import org.netbeans.junit.NbTestCase;

import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;


import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Jaroslav Tulach
 */
public class ANodeTest extends NbTestCase implements Singletonizer {
    private ANode instance;

    private SingletonizerListener listener;
    private Object obj = new Object();

    private Object invokeObject;
    private Method invokeMethod;
    private Object[] invokeArgs;
    private Object invokeReturn;


    private Class isEnabledClass = SubHierarchy.class;
    private boolean isEnabled = false;

    private PListener pListener;
    private NListener nListener;

    private Adaptable a;
    private Adaptor adapt;

    public ANodeTest(String testName) {
        super(testName);


        adapt = Adaptors.singletonizer(allClasses(), this);
        a = adapt.getAdaptable(obj);
        instance = new ANode(a, adapt);

        pListener = new PListener();
        nListener = new NListener();

        instance.addNodeListener(nListener);
        instance.addPropertyChangeListener(pListener);
    }

    protected void setUp() throws Exception {
        
    }

    protected void tearDown() throws Exception {
        WeakReference<Object> ref = new WeakReference<Object>(instance);
        instance = null;
        assertGC("Allow node to disappear", ref);

        nListener.assertEvents("during tear down no unexpected messages shall be seen");
        pListener.assertEvents("during tear down no unexpected messages shall be seen");
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
        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

        Node n = instance;
        if (n.getChildren() == Children.LEAF) {
            fail("We should have children now");
        }

        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(Integer.valueOf(3));
        al.add(Integer.valueOf(7));
        invokeReturn = al;
        invokeMethod = SubHierarchy.class.getDeclaredMethod("getChildren");
        invokeObject = obj;

        Node[] arr = n.getChildren().getNodes(true);
        assertEquals("two", 2, arr.length);


        isEnabled = false;
        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

        Node n2 = n;

        assertEquals("No ch.", Children.LEAF, n2.getChildren());

        nListener.assertEvents("Msgs", Node.PROP_LEAF, Node.PROP_LEAF, "childrenRemoved");
    }

    public void testGetIcon() throws Exception {
        invokeMethod = Icon.class.getDeclaredMethod("paintIcon", Component.class, Graphics.class, int.class, int.class);
        invokeObject = obj;

        Image img = instance.getIcon(BeanInfo.ICON_COLOR_16x16);

        assertNotNull("paintIcon method called", invokeArgs);

        assertTrue("is component", invokeArgs[0] instanceof Component);
        assertFalse("is not container", invokeArgs[0] instanceof Container);
    }

    public void testGetOpenedIcon() throws Exception {
        invokeMethod = Icon.class.getDeclaredMethod("paintIcon", Component.class, Graphics.class, int.class, int.class);
        invokeObject = obj;

        Image img = instance.getOpenedIcon(BeanInfo.ICON_COLOR_16x16);

        assertNotNull("paintIcon method called", invokeArgs);

        assertTrue("is component", invokeArgs[0] instanceof Component);
        assertTrue("is container", invokeArgs[0] instanceof Container);
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

        listener.stateChanged(SingletonizerEvent.anObjectChanged(this, obj));


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

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

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

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

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

    public void testCanDestroy() throws Exception {
        assertTrue("deleteable", instance.canDestroy());

        isEnabledClass = Delete.class;
        isEnabled = false;

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

        assertFalse("not deleteable", instance.canDestroy());

        try {
            invokeObject = obj;
            instance.destroy();
            fail("Destroy shall not succeed as it is disabled");
        } catch (IOException ex) {
            // ok
        }

        isEnabled = true;

        listener.stateChanged(SingletonizerEvent.anObjectChanged(this, obj));

        boolean result = instance.canDestroy();
        assertTrue("Now Enabled", result);

        invokeArgs = new Object[2];

        invokeMethod = Delete.class.getDeclaredMethods()[0];
        instance.destroy();

        assertNull("No argument passed", invokeArgs);

        invokeArgs = null;

        IOException e = new IOException("Wrong");
        invokeReturn = e;
        try {
            instance.destroy();
            fail("Destroy throws exception");
        } catch (IOException ex) {
            assertEquals("Right localized message", e.getLocalizedMessage(), ex.getLocalizedMessage());
        }

        Exception e2 = new IllegalStateException("Wrong");
        invokeReturn = e2;
        try {
            instance.destroy();
            fail("Destroy throws exception");
        } catch (IOException ex) {
            assertEquals("Right localized message", e2.getLocalizedMessage(), ex.getLocalizedMessage());
        }


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

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));


        assertEquals("Empty array", 0, instance.getPropertySets().length);
        nListener.assertEvents("We changed property sets", Node.PROP_PROPERTY_SETS);
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

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

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

        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

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

        listener.stateChanged(SingletonizerEvent.anObjectChanged(this, obj));

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

        listener.stateChanged(SingletonizerEvent.anObjectChanged(this, obj));

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
        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

        invokeMethod = Identity.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = "idmyne";

        assertEquals("Now the display name is taken from identity", "idmyne", instance.getDisplayName());
        nListener.assertEvents("display name changed", Node.PROP_DISPLAY_NAME);

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
        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

        invokeMethod = DisplayName.class.getDeclaredMethods()[0];
        invokeObject = obj;
        invokeReturn = "dispmyne";

        assertEquals("Now the short d. is taken from display name", "dispmyne", instance.getShortDescription());

        nListener.assertEvents("shrt msg is ok", Node.PROP_SHORT_DESCRIPTION);
    }

    public void testToString() {
        String result = instance.toString();

        if (result.indexOf("ANode") == -1) {
            fail("There should be name of the class: " + result);
        }
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
        listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));

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
        return c != SubHierarchy.class;
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

    public synchronized void addSingletonizerListener(SingletonizerListener listener) throws TooManyListenersException {
        if (this.listener != null) {
            throw new TooManyListenersException();
        }
        this.listener = listener;
    }

    public synchronized void removeSingletonizerListener(SingletonizerListener listener) {
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
            Icon.class, Delete.class,
        };
    }

    private static class PListener implements PropertyChangeListener {
        final StringBuffer events = new StringBuffer();

        final void append(String s) {
            if (events.length() > 0) {
                events.append(',');
            }
            events.append(s);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            append(evt.getPropertyName());
        }

        public void assertEvents(String msg, String... eventNames) {
            StringBuffer sb = new StringBuffer();
            String pref = "";
            for (String s : eventNames) {
                sb.append(pref);
                sb.append(s);
                pref=",";
            }
            assertEquals(msg, sb.toString(), events.toString());
            events.setLength(0);
        }
    }

    private static class NListener extends PListener implements NodeListener {
        public void childrenAdded(NodeMemberEvent ev) {
            append("childrenAdded");
        }

        public void childrenRemoved(NodeMemberEvent ev) {
            append("childrenRemoved");
        }

        public void childrenReordered(NodeReorderEvent ev) {
            append("childrenReordered");
        }

        public void nodeDestroyed(NodeEvent ev) {
            append("nodeDestroyed");
        }
    }
}
