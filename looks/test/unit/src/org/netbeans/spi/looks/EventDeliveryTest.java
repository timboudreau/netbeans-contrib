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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.api.nodes2looks.TestUtil;
import org.netbeans.spi.looks.*;

import org.netbeans.junit.*;
import org.netbeans.modules.looks.LookListener;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;

/** Tests the event delivery from looks.
 *
 * @author  Petr Hrebejk
 */
public class EventDeliveryTest extends NbTestCase {
    
    // Methods of testCase -----------------------------------------------------

    public EventDeliveryTest(String testName) {
        super(testName);
    }

    public static void main( String[] args ) {
        junit.textui.TestRunner.run(suite());
    }

    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(EventDeliveryTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.setUpRegistryToDefault();
    }

    protected void tearDown() throws Exception {    
        super.tearDown();
    }
    
    // Test methods ------------------------------------------------------------
    
    /** Tests that the event delivery works well for multiple views
     */
    public void testMultipleViewsDetach() {
        
        BadGuyLook look = new BadGuyLook( "BAD_GUY_LOOK" );
        BadGuy ro = new BadGuy( "First name", new HashSet() );
        
        doTest( look, ro );
        
    }
    
    
    /** Tests that the event delivery works well for multiple views
     */
    
    public void testMultipleViewsEventsCount() {
        
        BadGuyLook look = new BadGuyLook( "BAD_GUY_LOOK" );
        BadGuy ro = new BadGuy( "First name", new ArrayList() );
        
        doTest( look, ro );
        
    }
    
    
    private void doTest( BadGuyLook look, BadGuy ro ) {
        // These nodes represent the two views
        Node n1 = Nodes.node( ro, look );
        Node n2 = Nodes.node( ro, look );
        
        BadGuyNodeListener bgnl1 = new BadGuyNodeListener();
        BadGuyNodeListener bgnl2 = new BadGuyNodeListener();
        
        n1.addNodeListener( bgnl1 );
        n2.addNodeListener( bgnl2 );
        
        ro.setName( "Second name" );
        
        assertEquals( "There should be one prop change", 1, bgnl1.names.size() );
        assertEquals( "There should be one prop change", 1, bgnl2.names.size() );
        
        assertEquals( "Attach should be called once", 1, look.attachTo );
        
        WeakReference ref = new WeakReference ( n1 );
        n1 = null; // Lets forget the node       
        assertGC ("The node must disapear", ref);
        
        ro.setName( "Third name" );
        
        assertEquals( "There should be one prop change", 1, bgnl1.names.size() );
        assertEquals( "There should be one prop change", 2, bgnl2.names.size() );
    }
    
    
    /** Tests that detached look won't fire any events
     */    
    public void testEventAfterDetach() {
        
        BadGuyLook look = new BadGuyLook( "BAD_GUY_LOOK" );
        BadGuy ro = new BadGuy( "First name", new ArrayList() );
        BadGuyLookListener ll = new BadGuyLookListener();
        
        org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, ro, ll );        
        ro.setName( "Second name" );
        
        assertEquals( "There should be one name change", 1, ll.names.size() );
        
        org.netbeans.modules.looks.Accessor.DEFAULT.removeLookListener( look, ro, ll );
        
        ro.setName( "Third name" );
        
        assertEquals( "There should be still only one name change", 1, ll.names.size() );
    }
    

    // Innerclasses ------------------------------------------------------------

    /** This is a bad represented object allows for using different
     * collections for listener list.
     */
    
    public static class BadGuy {
        
        private Collection listeners;
        
        private String name;
        
        public BadGuy( String name, Collection listeners ) {
            this.name = name;
            this.listeners = listeners;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName( String name ) {
            String oldName = this.name;
            this.name = name;
            
            for( Iterator it = listeners.iterator(); it.hasNext(); ) {
                ((PropertyChangeListener)it.next()).propertyChange( new PropertyChangeEvent( this, "NAME", oldName, name ) );
            }
        }
        
        public void addPropertyChangeListener( PropertyChangeListener listener ) {
            listeners.add( listener );
        }
        
        public void removePropertyChangeListener( PropertyChangeListener listener ) {
            listeners.remove( listener );
        }
        
    }
    
    
    public static class BadGuyLook extends Look implements PropertyChangeListener {
        
        private int attachTo;
        private int detachFrom;
        
        private Class exClass;
                
        public BadGuyLook( String name ) {
            super( name );
        }
        
        public void attachTo( Object representedObject ) { 
            ((BadGuy)representedObject).addPropertyChangeListener( this );
            attachTo++;
        }
        
        public void detachFrom( Object representedObject ) {
            ((BadGuy)representedObject).removePropertyChangeListener( this );
            detachFrom++;
        }
        
        public int getAttachToCount() {
            return attachTo;
        }
        
        public int getDetachFromCount() {
            return detachFrom;
        }
        
        public void propertyChange( PropertyChangeEvent evt ) {
            fireChange( evt.getSource(), Look.GET_NAME );
        }
        
    }

    public static class BadGuyNodeListener implements NodeListener {
        
        private List names = new ArrayList();
        
        public void propertyChange(PropertyChangeEvent evt) {
            names.add( evt.getNewValue() );
        }
        
        public void childrenAdded(org.openide.nodes.NodeMemberEvent ev) {
        }
        
        public void childrenRemoved(org.openide.nodes.NodeMemberEvent ev) {
        }
        
        public void childrenReordered(org.openide.nodes.NodeReorderEvent ev) {
        }
        
        public void nodeDestroyed(org.openide.nodes.NodeEvent ev) {
        }
        
    }
    
    public static class BadGuyLookListener implements LookListener {
        
        private List names = new ArrayList();
        
        public void change(org.netbeans.modules.looks.LookEvent evt) {
            long mask = evt.getMask();
            
            if ( ( mask & Look.GET_NAME ) > 0 ) {
                names.add( new Object() );
            }
        }
        
        
        public void propertyChange(org.netbeans.modules.looks.LookEvent evt) {
        }
                
    }

}
