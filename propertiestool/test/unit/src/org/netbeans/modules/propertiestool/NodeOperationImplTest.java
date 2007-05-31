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
import java.awt.Component;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.nodes.NodeOperation;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David Strupl
 */
public class NodeOperationImplTest extends TestCase {
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
    }
    
    public NodeOperationImplTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(NodeOperationImplTest.class);
        
        return suite;
    }

    /**
     * Test of customize method, of class org.netbeans.modules.propertiestool.NodeOperationImpl.
     */
    public void testGetDelegate() throws Exception {
        NodeOperation no = NodeOperationImpl.getDelegate();
        assertTrue("The other impl should be returned ... ", no instanceof NodeOperationImpl2);
    }

    /**
     * Test of explore method, of class org.netbeans.modules.propertiestool.NodeOperationImpl.
     */
    public void testReadOnlyNode() throws Exception {
        NodeOperationImpl instance = new NodeOperationImpl();
        Node orig = new TestNode(Lookup.EMPTY);
        Node readOnly = instance.new ReadOnlyNode(orig);
        assertFalse("cannot write the read only copy 0", readOnly.getPropertySets()[0].getProperties()[0].canWrite());
        assertFalse("cannot write the read only copy 1", readOnly.getPropertySets()[0].getProperties()[1].canWrite());
        assertFalse("cannot write the read only copy 2", readOnly.getPropertySets()[0].getProperties()[2].canWrite());
        assertEquals("the type of the property should remain 0", readOnly.getPropertySets()[0].getProperties()[0].getValueType(), Boolean.TYPE);
        assertEquals("the type of the property should remain 1", readOnly.getPropertySets()[0].getProperties()[1].getValueType(), String.class);
        assertEquals("the type of the property should remain 2", readOnly.getPropertySets()[0].getProperties()[2].getValueType(), Integer.TYPE);
        // TODO: check all the other aspects of the read only copy
    }
    
    public static final class Lkp extends org.openide.util.lookup.AbstractLookup {
        public Lkp() {
            this(new org.openide.util.lookup.InstanceContent());
        }
        private Lkp(org.openide.util.lookup.InstanceContent ic) {
            super(ic);
            ic.add(new NodeOperationImpl2());
            ic.add(new NodeOperationImpl());
        }
    }

    private static final class NodeOperationImpl2 extends NodeOperation {
        public boolean customize(Node n) { return false; }
        public void explore(Node n) { }
        public void showProperties(Node n) { }
        public void showProperties(Node[] n) { }
        public Node[] select(String title, String rootTitle, Node root, NodeAcceptor acceptor, Component top) throws UserCancelException {
            return null;
        }
    }
}
