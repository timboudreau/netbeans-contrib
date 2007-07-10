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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.nodes2looks;


import org.openide.nodes.*;
import org.openide.cookies.InstanceCookie;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

import org.netbeans.junit.*;

import junit.framework.*;

import org.netbeans.spi.looks.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;


/** Tests methods of the LookNode. Notice that the communication with look
 *  is tested using. LookNodeValuesTest. This class only tests additional
 *  methods.
 */
public class LookNodeTest extends NbTestCase {

    // Sample looks
    SampleLook sampleLook1;
    SampleLook sampleLook2;

    // The lookNode
    Node lookNode;

    private static final String NODE_NAME_1 = "Node name 1";
    private static final String NODE_NAME_2 = "Node name 2";

    // Represented object
    SampleRepObject representedObject;

    // Methods of testCase -----------------------------------------------------

    public LookNodeTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new NbTestSuite(LookNodeTest.class);

        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();

        sampleLook1 = new SampleLook( "First sample look" ) {
            public String getName( Object representedObject, Lookup env ) {
                return NODE_NAME_1;
            }
        };

        sampleLook2 = new SampleLook( "Second sample look" )  {
            public String getName( Object representedObject, Lookup env ) {
                return NODE_NAME_2;
            }
        };
        representedObject = new SampleRepObject( createGoldenValues() );
        lookNode = Nodes.node( representedObject, 
                               sampleLook1, 
                               Selectors.selector( new SampleProvider( sampleLook1 ) ) );

    }

    protected void tearDown() throws Exception {

        sampleLook1 = sampleLook2 = null;

        lookNode = null;
        representedObject = null;

        super.tearDown();

    }

    // Test methods ------------------------------------------------------------

    public void testGetRepresentedObject() {

        SampleRepObject ro = (SampleRepObject)TestUtil.getRepresentedObject( lookNode );
        if ( ro != representedObject ) {
            fail("Bad represented object.");
        }

    }

    public void testGetRepresentedObjectByInstanceCookie() throws Exception {
        InstanceCookie.Of ic = (InstanceCookie.Of) lookNode.getLookup().lookup( InstanceCookie.Of.class );

        assertTrue( "Bad represented object. ", ic.instanceCreate() == representedObject );
    }

    public void testSetLook() {

        TestUtil.setLook( lookNode, sampleLook2 );
        if ( TestUtil.getLook( lookNode ) != sampleLook2 ) {
            fail( "Bad or no look not set." );
        }
        if ( lookNode.getName() != NODE_NAME_2 ) {
            fail( "Bad name from look: " + lookNode.getName() );
        }

        TestUtil.setLook( lookNode, sampleLook1 );
        if ( TestUtil.getLook( lookNode ) != sampleLook1 ) {
            fail( "Bad or no look not set." );
        }
        if ( lookNode.getName() != NODE_NAME_1 ) {
            fail( "Bad name from look: "  + lookNode.getName() );
        }

    }

    public void testGetLook() {

        Look look = TestUtil.getLook( lookNode );
        if ( look != sampleLook1 ) {
            fail("Bad or no look on node");
        }

    }

    public void testCloneNode() {

        Look look = sampleLook2;
        Node lookNode = Nodes.node(new Object(), look, Selectors.selector( new SampleProvider( look )  ) );

        Node clonedNode = lookNode.cloneNode();
        Node secondLevelClone = clonedNode.cloneNode();

        assertTrue ("Cloned node must not be null!", clonedNode != null );
        //assertTrue ("Cloned node must be a chameleon!", clonedNode.getBaseLook() == lookNode.getBaseLook ());
        //assertTrue ("Second level clone has different hard look!", clonedNode.getBaseLook() == secondLevelClone.getBaseLook());
        // assert("Second level clone has different hard look candidate!", clonedNode.getHardLookCandidate() == secondLevelClone.getHardLookCandidate());

        // we do not have any parent node so we do not use chameleon look but rather hard look candidate

        assertTrue ("Both clones must result in same getLook()!",
            ((org.netbeans.api.nodes2looks.LookNode)clonedNode).getLook() ==
            ((org.netbeans.api.nodes2looks.LookNode)secondLevelClone).getLook());

    }

    public void testGetActions() {

        Action[] actions = lookNode.getActions();

        assertEquals( "There should be 3 items", 3, actions.length );
        assertNull( "First item is not SystemAction.", actions[0] );
        assertNull( "Second item is separator", actions[1] );
        assertEquals( "Third item is SystemAction.", GoldenValue.TestingAction1.class, actions[2].getClass() );

        actions = lookNode.getActions( false );

        assertEquals( "There should be 3 items", 3, actions.length );
        assertEquals( "First item is Action.", TestBaseAction.class, actions[0].getClass() );
        assertNull( "Second item is separator", actions[1] );
        assertEquals( "Third item is SystemAction.", GoldenValue.TestingAction1.class, actions[2].getClass() );


    }

    public void testGetContextActions() {

        Action[] actions = lookNode.getContextActions();

        assertEquals( "There should be 3 items", 3, actions.length );
        assertNull( "First item is not SystemAction.", actions[0] );
        assertNull( "Second item is separator", actions[1] );
        assertEquals( "Third item is SystemAction.", GoldenValue.TestingAction1.class, actions[2].getClass() );

        actions = lookNode.getActions( true );

        assertEquals( "There should be 3 items", 3, actions.length );
        assertEquals( "First item is Action.", TestBaseAction.class, actions[0].getClass() );
        assertNull( "Second item is separator", actions[1] );
        assertEquals( "Third item is SystemAction.", GoldenValue.TestingAction1.class, actions[2].getClass() );


    }


    public void testGetDefaultAction() {

        Action action = lookNode.getDefaultAction();

        assertNull( "Default action is not SystemAction.", action );

        action = lookNode.getPreferredAction();

        assertEquals( "Default Action is Action.", TestBaseAction.class, action.getClass() );


    }
    
    public void testProperLookSelected() {
        
        ProxyLookTest.ExceptionLook e1 = 
            new ProxyLookTest.ExceptionLook( "e1", IllegalArgumentException.class );
        ProxyLookTest.ExceptionLook e2 = 
            new ProxyLookTest.ExceptionLook( "e2", ClassCastException.class );
        ProxyLookTest.ExceptionLook e3 = 
            new ProxyLookTest.ExceptionLook( "e3", null );
        
        LookSelector s1 = Selectors.array( new Look[] { e1, e2, e3 } );
        LookSelector s2 = Selectors.array( new Look[] { e1, e2 } );
        
        Node n1 = Nodes.node( "RO1", null, s1 );
        Look l1 = TestUtil.getLook( n1 );
        
        Node n2 = Nodes.node( "RO1", null, s2 );
        Look l2 = TestUtil.getLook( n2 );
        
        assertTrue( "Node shuld have the look e3 set. Was " + l1, l1 == e3 );
        assertTrue( "Node shuld have the beanLook set. Was " + l2, l2 == Looks.bean() );
        
    }
    
    public void testSize() {
        
        Look look = new SampleLook( "Second sample look" );        
        LookSelector selector = Selectors.selector( new SampleProvider( sampleLook1 ) );
        SampleRepObject ro = new SampleRepObject( createGoldenValues() );
        Node n = Nodes.node( ro, look, selector );
        
        
        Object subtract[] = new Object[] {
            look, 
            selector, 
            ro,
            org.openide.util.Utilities.activeReferenceQueue(),
            ((LookNode)n).getCache(),
            Collections.EMPTY_LIST,
            Children.LEAF
        };
        
        assertSize( "Size of the node " , Collections.singleton( n ), 168, subtract );

        n.getIcon( 0 );

        assertSize( "Size of the node " , Collections.singleton( n ), 168, subtract );
                
        n.getLookup ();

        assertSize( "Size of the node " , Collections.singleton( n ), 504, subtract );
    }

    // Private helper methods --------------------------------------------------


    private static GoldenValue[] createGoldenValues() {

        GoldenValue goldenValues[] = new GoldenValue[] {
            new GoldenValue( Look.GET_LOOKUP_ITEMS, GoldenValue.createGoldenLookupItems() ),
            new GoldenValue( Look.GET_ACTIONS,
                new Action[] { new TestBaseAction(), null, SystemAction.get( GoldenValue.TestingAction1.class ) } ),
            new GoldenValue( Look.GET_CONTEXT_ACTIONS,
                new Action[] { new TestBaseAction(), null, SystemAction.get( GoldenValue.TestingAction1.class ) } ),
            new GoldenValue( Look.GET_DEFAULT_ACTION, new TestBaseAction() )
        };

        return goldenValues;

    }


    public static final class TestBaseAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
        }

    }


}

