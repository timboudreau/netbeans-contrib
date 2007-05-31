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

import junit.framework.*;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.Node;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author David Strupl
 */
public class PropertiesToolTopComponentTest extends TestCase {
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
    }
    
    public PropertiesToolTopComponentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PropertiesToolTopComponentTest.class);
        return suite;
    }

    /**
     * Test of getDefault method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testGetDefault() {
        PropertiesToolTopComponent expResult = PropertiesToolTopComponent.getDefault();
        PropertiesToolTopComponent result = PropertiesToolTopComponent.getDefault();
        assertEquals("should be singleton", expResult, result);
    }

    /**
     * Test of updateSaveCookie method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testUpdateSaveCookie() throws Exception {
        PropertiesToolTopComponent instance = PropertiesToolTopComponent.findInstance();
        Node n = new TestNode(Lookup.EMPTY);
        assertNull("no save cookie when save disabled", instance.getLookup().lookup(SaveCookie.class));
        instance.getPropertySheetPanel().setNodes(new Node[] { n });
        Node[] edited = instance.getPropertySheetPanel().getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        assertNotNull("save cookie when save enabled", instance.getLookup().lookup(SaveCookie.class));
        instance.getPropertySheetPanel().setNodes(new Node[0]);
        assertNull("no save cookie when save disabled", instance.getLookup().lookup(SaveCookie.class));
    }

    /**
     * Test of findInstance method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testFindInstance() {
        PropertiesToolTopComponent expResult = PropertiesToolTopComponent.findInstance();
        PropertiesToolTopComponent result = PropertiesToolTopComponent.findInstance();
        assertEquals("should be a singleton", expResult, result);
    }

    /**
     * Test of componentOpened method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testComponentOpened() {
        PropertiesToolTopComponent instance = PropertiesToolTopComponent.findInstance();
        Node n = new TestNode(Lookup.EMPTY);
        MyContextGlobalProvider pcgp = (MyContextGlobalProvider)Lookup.getDefault().lookup(ContextGlobalProvider.class);
        pcgp.add(n);
        instance.open();
        assertEquals("the nodes from global context should be explorer after open", instance.getPropertySheetPanel().getNodes()[0], n);
        instance.close();
        pcgp.remove(n);
    }

    /**
     * Test of preferredID method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testPreferredID() {
        PropertiesToolTopComponent instance = PropertiesToolTopComponent.findInstance();
        String expResult = PropertiesToolTopComponent.PREFERRED_ID;
        String result = instance.preferredID();
        assertEquals(expResult, result);
    }

    /**
     * Test of resultChanged method, of class org.netbeans.modules.propertiestool.PropertiesToolTopComponent.
     */
    public void testResultChanged() throws Exception {
        PropertiesToolTopComponent instance = PropertiesToolTopComponent.findInstance();
        instance.open();
        assertEquals("nothing there?", 0, instance.getPropertySheetPanel().getNodes().length);
        Node n = new TestNode(Lookup.EMPTY);
        MyContextGlobalProvider pcgp = (MyContextGlobalProvider)Lookup.getDefault().lookup(ContextGlobalProvider.class);
        pcgp.add(n);
        assertEquals("the nodes from global context should be explored", instance.getPropertySheetPanel().getNodes()[0], n);
        instance.close();
        pcgp.remove(n);
    }
    
    public static final class Lkp extends AbstractLookup {
        public Lkp() {
            this(new InstanceContent());
        }
        private Lkp(InstanceContent i) {
            super(i);
            i.add(new MyContextGlobalProvider());
        }
    }
    
    private static final class MyContextGlobalProvider implements ContextGlobalProvider {
        private InstanceContent ic = new InstanceContent();
        private Lookup globalContext = new AbstractLookup(ic);
        public Lookup createGlobalContext() {
            return globalContext;
        }
        public void add(Object obj) {
            ic.add(obj);
        }
        public void remove(Object obj) {
            ic.remove(obj);
        }
    }
}
