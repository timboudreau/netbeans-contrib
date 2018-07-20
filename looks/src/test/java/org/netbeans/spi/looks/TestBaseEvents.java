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

package org.netbeans.spi.looks;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.openide.nodes.*;

import org.netbeans.junit.*;

import org.netbeans.spi.looks.*;

public class TestBaseEvents extends NbTestCase {

    // Sample look we test against
    protected SampleLook sampleLook;

    // The node to test on
    protected Node node;

    // Represented object of the tested node
    protected SampleRepObject representedObject;

    // The test listener used for the node
    protected GoldenEvent.Listener testNodeListener;

    // Property change listener
    protected GoldenEvent.Listener testPcl;

    // Golden values
    protected GoldenValue[] goldenValues;

    // Methods of testCase -----------------------------------------------------

    public TestBaseEvents(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(TestBaseEvents.class);
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        node = null;
        representedObject = null;

        super.tearDown();
    }


    // Methods for setting up the test case ------------------------------------

    protected void setTarget( Node node, SampleRepObject representedObject ) {
        this.node = node;
        this.representedObject = representedObject;

        testNodeListener = new GoldenEvent.Listener();
        node.addNodeListener( testNodeListener );
        testPcl = new GoldenEvent.Listener();
        node.addPropertyChangeListener( testPcl );

    }

    protected void setGoldenValues( GoldenValue[] goldenValues ) {
        this.goldenValues = goldenValues;
    }

    // Test methods ------------------------------------------------------------


    public void testFirePropertyChange() {

        representedObject.setProperty( "MY_PROP", "something" );
        representedObject.setProperty( "MY_PROP", "something else" );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             "MY_PROP",
                             null, null ),

            new GoldenEvent( node,
                             "MY_PROP",
                             null, null )
        };

        assertTrue( GoldenEvent.compare( testPcl.getEvents(),
                                         goldenEvents,
                                         null ) );

        assertTrue( "Unexpected events in NodeListsner: " + testNodeListener.getEvents().size(),
                    testNodeListener.getEvents().size() == 0 );
    }


    public void testFireNameChange() {

        String oldValue = (String)GoldenValue.get( ProxyLook.GET_NAME, goldenValues );

        representedObject.setValue( ProxyLook.GET_NAME, "New name" );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_NAME,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }

    public void testFireDisplayNameChange() {
        String oldValue = (String)GoldenValue.get( ProxyLook.GET_DISPLAY_NAME, goldenValues );

        representedObject.setValue( ProxyLook.GET_DISPLAY_NAME, "New display name" );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_DISPLAY_NAME,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }

    public void testFireShortDescriptionChange() {
        String oldValue = (String)GoldenValue.get( ProxyLook.GET_SHORT_DESCRIPTION, goldenValues );

        representedObject.setValue( ProxyLook.GET_SHORT_DESCRIPTION, "New short description" );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_SHORT_DESCRIPTION,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }


    public void testFireIconChange() {
        representedObject.setValue( ProxyLook.GET_ICON,
                                    new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB ) );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_ICON,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );

    }

    public void testFireOpenedIconChange() {
        representedObject.setValue( ProxyLook.GET_OPENED_ICON,
                                    new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB ) );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_OPENED_ICON,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }

    public void testFirePropertySetsChange() {
        Node.PropertySet[] oldValue = (Node.PropertySet[])GoldenValue.get( ProxyLook.GET_PROPERTY_SETS, goldenValues );
        Node.PropertySet[] newValue = new Node.PropertySet[] {
                                            new Sheet.Set(),
                                            new Sheet.Set()
                                        };

        representedObject.setValue( ProxyLook.GET_PROPERTY_SETS, newValue );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_PROPERTY_SETS,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );

    }


    public void testFireCookieChange() {

        Collection oldItems = (Collection)GoldenValue.get( ProxyLook.GET_LOOKUP_ITEMS, goldenValues );

        node.getCookie( org.openide.cookies.CloseCookie.class ); // To make lookup fire


        Collection items = new ArrayList( oldItems ); // Needed to make propertySupport to fire

        items.add(
            new GoldenValue.TestLookupItem (
                new org.openide.cookies.ViewCookie() {
                    public void view() {}
                }
            )
        );
        items.add( new GoldenValue.TestLookupItem ( new javax.swing.JPanel() ) );

        representedObject.setValue( ProxyLook.GET_LOOKUP_ITEMS, items );

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             Node.PROP_COOKIE,
                             null, null ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );

    }


    public void testFireNodeDestroyed() {
        representedObject.setProperty( SampleRepObject.DESTROY, "kill" );

        assertTrue( "Bad number of events: " + testNodeListener.getEvents().size(),
                    testNodeListener.getEvents().size() == 1 );

    }


    public void testAddChildren() {
        List oldValue = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );;
        List newValue = new ArrayList( oldValue );
        newValue.add( "Additional child" );

        // Workaround for Children.Keys behavior
        node.removeNodeListener( testNodeListener );
        Node oldNodes[] = node.getChildren().getNodes();
        node.addNodeListener( testNodeListener );

        representedObject.setValue( ProxyLook.GET_CHILD_OBJECTS, newValue );
        Node newNodes[] = node.getChildren().getNodes();

        assertEquals(3, newNodes.length);
        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             true,
                             new Node[] { newNodes[2] },
                             new int[] { 2 } ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }

    public void testRemoveChildren() {
        List oldValue = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );
        List newValue = new ArrayList( oldValue );
        newValue.remove( 0 );

        // Workaround for Children.Keys behavior
        node.removeNodeListener( testNodeListener );
        Node oldNodes[] = node.getChildren().getNodes();
        node.addNodeListener( testNodeListener );

        representedObject.setValue( ProxyLook.GET_CHILD_OBJECTS, newValue );
        Node newNodes[] = node.getChildren().getNodes();

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             false,
                             new Node[] { oldNodes[0] },
                             new int[] { 0 } ),
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );

    }


    public void testReorderChildren() {
        List oldValue = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );
        List newValue = new ArrayList( oldValue );
        Object o0 = newValue.get( 0 );
        Object o1 = newValue.get( 1 );
        newValue.set( 0, o1 );
        newValue.set( 1, o0 );

        // Workaround for Children.Keys behavior
        node.removeNodeListener( testNodeListener );
        Node oldNodes[] = node.getChildren().getNodes();
        node.addNodeListener( testNodeListener );

        representedObject.setValue( ProxyLook.GET_CHILD_OBJECTS, newValue );
        Node newNodes[] = node.getChildren().getNodes();

        GoldenEvent[] goldenEvents = new GoldenEvent[] {
            new GoldenEvent( node,
                             new int[] { 1, 0 } )
        };

        assertTrue( GoldenEvent.compare( testNodeListener.getEvents(),
                                         goldenEvents,
                                         null ) );
    }


    public void testNoChildrenChange() {
        List oldValue = (List)GoldenValue.get( ProxyLook.GET_CHILD_OBJECTS, goldenValues );
        List newValue = new ArrayList( oldValue );

        // Workaround for Children.Keys behavior
        node.removeNodeListener( testNodeListener );
        node.addNodeListener( testNodeListener );

        representedObject.setValue( ProxyLook.GET_CHILD_OBJECTS, newValue );


        assertTrue( "Bad number of events : " + testNodeListener.getEvents().size(),
                    testNodeListener.getEvents().size() == 0 );


    }


}

