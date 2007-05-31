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

import java.lang.reflect.InvocationTargetException;
import junit.framework.*;
import org.netbeans.api.propertiestool.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David Strupl
 */
public class BatchUpdateTest extends TestCase {
    
    public BatchUpdateTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BatchUpdateTest.class);
        
        return suite;
    }

    /**
     * Test of startSaving method, of class org.netbeans.api.propertiestool.BatchUpdate.
     * Test of finishSaving method, of class org.netbeans.api.propertiestool.BatchUpdate.
     */
    public void testStartFinishSaving() throws Exception {
        BatchUpdateImpl instance = new BatchUpdateImpl();
        Node n = new TestNode(Lookups.singleton(instance));
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertEquals("Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        try {
            psp.save();
        } catch (RuntimeException re) {
            fail("Failed with " + re.getMessage() + " and test value == " + instance.getTest());
        }
        assertEquals("Should be 57", instance.getTest(), 57);
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[1].getValue(), "sss");
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(100));
    }

    /**
     * Test of savePropertyValue method, of class org.netbeans.api.propertiestool.BatchUpdate.
     */
    public void testSavePropertyValue() throws Exception {
        BatchUpdateImpl2 instance = new BatchUpdateImpl2();
        Node n = new TestNode(Lookups.singleton(instance));
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertEquals("Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        psp.save();
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(1000));
    }

    /**
     */
    public void testCoalesce() throws Exception {
        BatchUpdateImpl3 instance = new BatchUpdateImpl3();
        Node n1 = new TestNode(Lookups.singleton(instance));
        Node n2 = new TestNode(Lookups.singleton(instance));
        Node n3 = new TestNode(Lookups.singleton(instance));
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n1, n2, n3});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        edited[1].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[1].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[1].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        edited[2].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[2].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[2].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertEquals("Original should not be changed 0 ", n1.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n1.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n1.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        try {
            psp.save();
        } catch (RuntimeException re) {
            fail("Failed with " + re.getMessage() + " and test value == " + instance.getTest());
        }
        assertEquals("Should be 69", 69, instance.getTest());
        assertEquals("Original should be changed ", n1.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertEquals("Original should be changed ", n1.getPropertySets()[0].getProperties()[1].getValue(), "sss");
        assertEquals("Original should be changed ", n1.getPropertySets()[0].getProperties()[2].getValue(), new Integer(100));
    }
    
    /**
     */
    public void testCoalesce2() throws Exception {
        BatchUpdateImpl instance1 = new BatchUpdateImpl();
        Node n = new TestNode(Lookups.singleton(instance1));
        BatchUpdateImpl3 instance2 = new BatchUpdateImpl3();
        Node n1 = new TestNode(Lookups.singleton(instance2));
        Node n2 = new TestNode(Lookups.singleton(instance2));
        Node n3 = new TestNode(Lookups.singleton(instance2));
        PropertySheetPanel psp = new PropertySheetPanel();
        psp.setNodes(new Node[] {n1, n, n2, n3});
        Node[] edited = psp.getEditedNodes();
        edited[0].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[0].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[0].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        edited[1].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[1].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[1].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        edited[2].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[2].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[2].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        edited[3].getPropertySets()[0].getProperties()[0].setValue(Boolean.FALSE);
        edited[3].getPropertySets()[0].getProperties()[1].setValue("sss");
        edited[3].getPropertySets()[0].getProperties()[2].setValue(new Integer(100));
        assertEquals("Original should not be changed 0 ", n1.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n2.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n3.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        assertEquals("Original should not be changed 0 ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.TRUE);
        assertEquals("Original should not be changed 1 ", n.getPropertySets()[0].getProperties()[1].getValue(), "string test");
        assertEquals("Original should not be changed 2 ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(45));
        try {
            psp.save();
        } catch (RuntimeException re) {
            fail("Failed with " + re.getMessage());
        }
        assertEquals("Should be 69", 69, instance2.getTest());
        assertEquals("Should be 57", 57, instance1.getTest());
        assertEquals("Original should be changed ", n1.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertEquals("Original should be changed ", n2.getPropertySets()[0].getProperties()[1].getValue(), "sss");
        assertEquals("Original should be changed ", n3.getPropertySets()[0].getProperties()[2].getValue(), new Integer(100));
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[0].getValue(), Boolean.FALSE);
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[1].getValue(), "sss");
        assertEquals("Original should be changed ", n.getPropertySets()[0].getProperties()[2].getValue(), new Integer(100));
    }
    /**
     * Generated implementation of abstract class org.netbeans.api.propertiestool.BatchUpdate. Please fill dummy bodies of generated methods.
     */
    private class BatchUpdateImpl implements BatchUpdate {
        private int test = 0;
        public void startSaving() {
            if (test !=0) {
                // must be called first
                throw new RuntimeException("startSaving was not called first");
            }
            test++;
        }

        public void savePropertyValue(org.openide.nodes.Node.Property property, java.lang.Object value) {
            test+=2;
            try {
                property.setValue(value);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void finishSaving() {
            if (test != 7) {
                throw new RuntimeException("finishSaving was not called last "+ test);
            }
            test+=50;
        }
        
        public int getTest() {
            return test;
        }

        public boolean coalesce(BatchUpdate next) {
            return false;
        }
    }
    private class BatchUpdateImpl2 implements BatchUpdate {
        public void startSaving() {
        }

        public void savePropertyValue(org.openide.nodes.Node.Property property, java.lang.Object value) {
            try {
                if (value instanceof Integer) {
                    property.setValue(new Integer(1000));
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void finishSaving() {
        }

        public boolean coalesce(BatchUpdate next) {
            return next instanceof BatchUpdateImpl2;
        }
    }
    private class BatchUpdateImpl3 implements BatchUpdate {
        private int test = 0;
        public void startSaving() {
            if (test !=0) {
                // must be called first
                throw new RuntimeException("startSaving was not called first");
            }
            test++;
        }

        public void savePropertyValue(org.openide.nodes.Node.Property property, java.lang.Object value) {
            test+=2;
            try {
                property.setValue(value);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void finishSaving() {
            if (test != 19) {
                throw new RuntimeException("finishSaving was not called last");
            }
            test+=50;
        }
        
        public int getTest() {
            return test;
        }

        public boolean coalesce(BatchUpdate next) {
            return next instanceof BatchUpdateImpl3;
        }
    }
}
