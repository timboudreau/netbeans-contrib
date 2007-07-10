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

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;

import org.openide.nodes.*;
import org.netbeans.junit.*;
import org.openide.util.Lookup;

import org.netbeans.spi.looks.*;

/** Tests whether all vales returned from a Node are identical with
 * the values server by associated look
 */
public class NodeLookValuesTest extends TestBaseValues {

    Node delegate;

    // Methods of testCase -----------------------------------------------------

    public NodeLookValuesTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite( NodeLookValuesTest.class );
        return suite;
    }    
    
    protected void setUp() throws Exception {
        
        Look look = new SampleLook( "NodeLookValuesTestLook" );
        LookSelector selector = Selectors.selector( new SampleProvider( look ) );
        GoldenValue[] goldenValues = GoldenValue.createGoldenValues();
        SampleRepObject ro = new SampleRepObject( goldenValues );
        delegate = Nodes.node( ro, look, selector );
        Node node = Nodes.node( delegate, Nodes.nodeLook(), selector );

        setTarget( node, ro, 1 );                
        setGoldenValues( goldenValues );

        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    ///////////////////////////////////////////////////////
    //                                                   //
    //  Most methods are inherited from: TestBaseValues  //
    //                                                   //
    ///////////////////////////////////////////////////////
    
    // Overriden test methods --------------------------------------------------
    
    public void testGetLookupValues() {
        
        Lookup lookup = node.getLookup();
        Lookup.Result result = lookup.lookup( new Lookup.Template( Object.class ) );
        Collection items = new ArrayList( result.allItems() ); // Make it modifyable

        // We need to remove the node itself TWICE

        for( int i = 0; i < 2; i++ ) {            
            Object nodeItself = null;

            for( Iterator it = items.iterator(); it.hasNext(); ) {
                Lookup.Item item = (Lookup.Item)it.next();
                
                if ( item.getInstance() == ( i == 0 ? node : delegate ) ) {
                    nodeItself = item;
                    break;
                }
            }

            assertNotNull( "Lookup should contain the node itself. Run " + i +".", nodeItself );
            items.remove( nodeItself );            
        }
        
        assertTrue ( MSSG_UNEXPECTED_VALUE_RETURNED,  
                        GoldenValue.isOK(
                            ProxyLook.GET_LOOKUP_ITEMS,
                            items,
                            goldenValues ) );
    }
    
    public void testGetChildObjects() {
        Node[] nodes = node.getChildren().getNodes();
        List gv = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );

        if ( gv == null ) {
            fail( "Golden value is invalid" );
        }
        if ( gv.size() != nodes.length ) {
            fail( MSSG_UNEXPECTED_VALUE_RETURNED );
        }

        for( int i = 0; i < nodes.length; i++ ) {

            Node n = nodes[i];
                      
            Object o = TestUtil.getRepresentedObject( n );
            assertTrue("o is a Node: " + o.getClass().getName(), o instanceof Node);
            Node delegate = (Node)o;
            
            if ( TestUtil.getRepresentedObject( delegate ) != gv.get(i) &&
                !TestUtil.getRepresentedObject( delegate ).equals( gv.get(i) ) ) {
                fail( MSSG_UNEXPECTED_VALUE_RETURNED + "on index : " + i );
            }
        }                        
    }
    
    // Additional test methods -------------------------------------------------
    
    public void testGetRepresentedObject() {
        Object ro = TestUtil.getRepresentedObject( node );
        if ( ro != delegate ) {
            fail("Bad represented object.");
        }
        if ( TestUtil.getRepresentedObject( (Node)ro ) != representedObject ) {
            fail("Bad represented delegate.");
        }
    }
    
    public void testGetLook() {        
        Look look = ((LookNode)node).getLook();
        if ( look != Nodes.nodeLook() ) {
            fail("Bad or no look on node");
        }
    }
    
    public void testGetCookie() {
        Class c = org.openide.cookies.SaveCookie.class;
        Node.Cookie cookie = node.getCookie( c );
        
        Collection items = (Collection)representedObject.getValue( ProxyLook.GET_LOOKUP_ITEMS );
        Node.Cookie gc = null;
        
        for( Iterator it = items.iterator(); it.hasNext(); ) {
            Lookup.Item item = (Lookup.Item)it.next();
            if ( c.isInstance( item.getInstance() ) ) {
                gc = (Node.Cookie)item.getInstance();
            }
        }
        
        if ( cookie != gc ) {
            fail("Bad cookie." + cookie + " instad of " + gc );
        }
    }    
    
}


