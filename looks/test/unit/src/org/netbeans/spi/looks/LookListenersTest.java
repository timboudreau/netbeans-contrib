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
