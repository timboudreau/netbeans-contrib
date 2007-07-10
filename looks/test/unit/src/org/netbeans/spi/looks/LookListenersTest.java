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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import org.netbeans.junit.*;
import org.netbeans.modules.looks.LookEvent;

import org.netbeans.modules.looks.LookListener;

/** Tests modifications in the Look listeners cache
 */
public class LookListenersTest extends NbTestCase {

    // Methods of testCase -----------------------------------------------------

    public LookListenersTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static NbTest suite() {
        NbTestSuite suite = new NbTestSuite(LookListenersTest.class);
        return suite;
    }    
    
    protected void setUp() throws Exception {        
        super.setUp();    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    // Test methods ------------------------------------------------------------
    
    public void testAddObject() {
    
        // Crate test looks
        TestFiringLook look = new TestFiringLook();        
        TestLookListener listener = new TestLookListener( look );
        
        reset();
        
        // Add some listeners
        look.addLookListener( createObject(), listener );
        look.addLookListener( createObject(), listener );
        
        try {
            // Fire change on all objects which will add new listener        
            look.fire( null, ADD_OBJECT );
        }
        catch ( ConcurrentModificationException e ) {
            fail( "CME thrown " + e );
        }
                
    }
    
    public void testRemoveObject() {
    
        // Crate test looks
        TestFiringLook look = new TestFiringLook();        
        TestLookListener listener = new TestLookListener( look );
        
        reset();
        
      
        // Add some listeners
        look.addLookListener( createObject(), listener );
        look.addLookListener( createObject(), listener );
        look.addLookListener( createObject(), listener );
        
        try {
            // Fire change on all objects which will add new listener        
            look.fire( null, REMOVE_OBJECT );
        }
        catch ( ConcurrentModificationException e ) {
            fail( "CME thrown " + e );
        }
                
    }
    
    public void testAddListener() {
    
        // Crate test looks
        TestFiringLook look = new TestFiringLook();        
        
        reset();
        createObject();
        // Add some listeners
        look.addLookListener( getObject( counter ), new TestLookListener( look ) );
        look.addLookListener( getObject( counter ), new TestLookListener( look ) );
        
        try {
            // Fire change on all objects which will add new listener        
            look.fire( getObject( counter ), ADD_LISTENER );
        }
        catch ( ConcurrentModificationException e ) {
            fail( "CME thrown " + e );
        }
                
    }
    
    public void testRemoveListener() {
    
        // Crate test looks
        TestFiringLook look = new TestFiringLook();        
        
        reset();
        createObject();
        // Add some listeners
        look.addLookListener( getObject( counter ), new TestLookListener( look ) );
        look.addLookListener( getObject( counter ), new TestLookListener( look ) );
        
        try {
            // Fire change on all objects which will add new listener        
            look.fire( getObject( counter ), REMOVE_LISTENER );
        }
        catch ( ConcurrentModificationException e ) {
            fail( "CME thrown " + e );
        }
                
    }
    
    // Private helper classes --------------------------------------------------
    
    private static int counter = -1;
    private static ArrayList objects = new ArrayList();
    
    private static void reset() {
        counter = -1;
        objects = new ArrayList();
        TestLookListener.listeners = new ArrayList();
    }
    
    private static Integer createObject() {
        objects.add( new Integer( counter++ ) );
        return (Integer)objects.get( counter );
    }
    
    private static Integer getObject( int index ) {
        return (Integer)objects.get( index );
    }
    
    // Abusing event mask for controlling Look's listeners cache
    private static final long ADD_OBJECT = Look.GET_NAME;
    private static final long REMOVE_OBJECT = Look.GET_DISPLAY_NAME;
    private static final long ADD_LISTENER = Look.GET_ICON;
    private static final long REMOVE_LISTENER = Look.GET_OPENED_ICON;
    
    private static class TestFiringLook extends Look {
        
        TestFiringLook() {
            super( "TEST_FIRING_LOOK" );
        }
        
        public void fire( Object object, long mask ) {
            fireChange( object, mask );
        }
        
    }
    
    private static class TestLookListener implements LookListener {

        private static ArrayList listeners = new ArrayList();
        
        Look look;
        
        TestLookListener( Look look ) {
            this.look = look;
            listeners.add( this );
        }
                
        public void change( LookEvent evt ) {
            if ( evt.getMask() == ADD_OBJECT ) {
                look.addLookListener( createObject(), this );
            }
            else if ( evt.getMask() == REMOVE_OBJECT ) {
                look.removeLookListener( getObject( counter ), this );
            }
            else if ( evt.getMask() == ADD_LISTENER ) {
                look.addLookListener( getObject( counter ), this );
            }
            else if ( evt.getMask() == REMOVE_LISTENER ) {
                look.removeLookListener( getObject( counter ), (LookListener)listeners.get( counter ) );
            }
        }
        
        public void propertyChange( LookEvent evt ) {
                
        }
        
    }
    
    
}
