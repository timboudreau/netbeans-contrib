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

package org.netbeans.spi.looks;

import org.openide.nodes.*;
import org.netbeans.junit.*;
import org.netbeans.spi.looks.*;

/** The LookSwitcherLook should allow access to the LookNode in all Looks
 * which are plugged into the same CompositeLook. The access should be
 * provided using Lookup. This class tests this feature.
 */
public class LookSwitcherLookLookupTest extends NbTestCase {

    private GoldenValue[] gvLeaf1, gvLeaf2, gvLeaf3, gvMid1, gvMid2, gvTop;
    private SampleRepObject srLeaf1, srLeaf2, srLeaf3, srMid1, srMid2, srTop;
    private Node node, nodeNonSwitch;
    /* private MySampleLook sampleLook; */
    
    // Methods of testCase -----------------------------------------------------
    
    public LookSwitcherLookLookupTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( LookSwitcherLookLookupTest.class );
        return suite;
    }    
    
    protected void setUp() throws Exception {
        
        super.setUp();    
        /*
        // 1. Create hierarchy of SampleRepObjects
        
        // LEAF LEVEL
        gvLeaf1 = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "LEAF_1" )
        };        
        srLeaf1 = new SampleRepObject( gvLeaf1 );
        
        gvLeaf2 = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "LEAF_2" )
        };        
        srLeaf2 = new SampleRepObject( gvLeaf2 );
        
        gvLeaf3 = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "LEAF_3" )
        };
        srLeaf3 = new SampleRepObject( gvLeaf3 );
        
        // MID LEVEL
        gvMid1 = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "MID_1" ),
            new GoldenValue( Look.GET_CHILD_OBJECTS, 
                Arrays.asList( new Object[] { srLeaf1, srLeaf2 } ) )
        };     
        srMid1 = new SampleRepObject( gvMid1 );
        
        gvMid2 = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "MID_2" ),
            new GoldenValue( Look.GET_CHILD_OBJECTS, 
                Arrays.asList( new Object[] { srLeaf3 } ) )
        };
        srMid2 = new SampleRepObject( gvMid2 );
        
        // TOP LEVEL
        gvTop = new GoldenValue[] {
            new GoldenValue( Look.GET_NAME, "TOP" ),
            new GoldenValue( Look.GET_CHILD_OBJECTS, 
                Arrays.asList( new Object[] { srMid1, srMid2 } ) )
        };        
        srTop = new SampleRepObject( gvTop );
                
        
        // Create composite look and look node for testing
        //
        sampleLook = new MySampleLook( "MySample" );
        Look composite = Looks.composite( "TestComposite",
            new Look[] { Looks.lookSwitcherLook(), sampleLook } );        
        LookSelector selector = new SampleSelector( composite );
        node = Looks.node( srTop, composite, selector );

        LookSelector selectorNonSwitch = new SampleSelector( sampleLook );                
        nodeNonSwitch = Looks.node( srTop, sampleLook, selectorNonSwitch );
         */
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------

    public void testLookup() {
        fail( "Needs to be modified for the new version" );
        /*
        testTree( node );
         */
    }

    public void testNonSwitchLookup() {
        fail( "Needs to be modified for the new version" );
        /*
        assertNull ("LookNode isn't found in Lookup if LookNode don't contain LookSwitcherLook.",
                nodeNonSwitch.getLookup ().lookup (org.netbeans.modules.looks.LookNode.class));

        // Test children
        Node children[] = nodeNonSwitch.getChildren().getNodes();
        
        for( int i = 0; i < children.length; i++ ) {
            assertNull ("LookNode isn't found in Lookup if LookNode don't contain LookSwitcherLook.",
                    children[i].getLookup ().lookup (org.netbeans.modules.looks.LookNode.class));
        } 
        */       
    }
        
    // Innerclasses ------------------------------------------------------------
    
    private void testTree( Node node ) {
        fail( "Needs to be modified for the new version" );
        /*
        // Test the node
        String name = node.getName();
        Lookup.Result result = sampleLook.getResult();        
        Collection c = result.allInstances();
        assertTrue( "Bad number of instances.", c.size() == 1 );        
        Node n = (Node)c.iterator().next();
        assertTrue( "Bad Node.", node == n );
        assertNotNull ("LookNode is found Lookup if LookNode contains LookSwitcherLook",
                node.getLookup ().lookup (org.netbeans.modules.looks.LookNode.class));

        // Test children
        Node children[] = node.getChildren().getNodes();
        
        for( int i = 0; i < children.length; i++ ) {
            testTree( children[i] );
        }        
        */
    }
    
    /*
    class MySampleLook extends SampleLook {
        
        private Lookup.Result result;
        
        MySampleLook( String name ) {
            super( name );
        }
                
        public String getName( Look.NodeSubstitute subst ) {
            result = subst.getLookup().lookup( new Lookup.Template( org.netbeans.modules.looks.LookNode.class ) );
            return super.getName( subst );
        }
        
        Lookup.Result getResult() {
            Lookup.Result lastResult = result;
            result = null;
            return lastResult;
        }
        
    }
    */
    
}


