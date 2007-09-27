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

package org.netbeans.api.nodes2looks;

import java.lang.ref.*;
import java.util.*;

import org.netbeans.junit.*;

import org.netbeans.spi.looks.*;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

public class LookNodeGCTest extends NbTestCase {

    private static String RED = "Red";
    private static String GREEN = "Green";
    private static String BLUE = "Blue";

    private static List allObjects = Arrays.asList( new String[] { RED, GREEN, BLUE } );

    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.setUpRegistryToDefault();
    }
    
    public LookNodeGCTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(new NbTestSuite (LookNodeGCTest.class));
    }
    
    /** Tests whether nodes are garbage collecte and detachFrom is called
     */    
    public void testNodeGC() throws Exception {
        
        for (int cnt = 0; cnt < 5; cnt++) {
        
            MyLook look = new MyLook ();
            
            Node nodes[] = { 
                Nodes.node ( RED, look),
                Nodes.node ( GREEN, look),
                Nodes.node ( BLUE, look)
            };
            
            WeakReference nodesWeak[] = {
                new WeakReference( nodes[0] ),
                new WeakReference( nodes[1] ),
                new WeakReference( nodes[2] ),
            };
            
            // Substitutes should still exist            
            assertEquals( "No attached objects", 3, look.attached.size() );
            
            // Now forgot about the nodes            
            nodes = null;
            
            // Nodes should dissapear            
            assertGC( "Reference 0 not cleared", nodesWeak[0] );
            assertGC( "Reference 1 not cleared", nodesWeak[1] );
            assertGC( "Reference 2 not cleared", nodesWeak[2] );
            
            
            waitForTestRef();
            
            assertEquals( "Substitutes not cleared", 0, look.attached.size() ); 
            
        }
        
    }
    
    
    /** Tests whether substitutes are not listed when they change look.
     */
    
    public void testChangeOfLook () throws Exception {
        
        MyLook lookOne = new MyLook ();
        MyLook lookTwo = new MyLook ();
        
        Node ln = Nodes.node ( RED, lookOne );
                
        assertEquals( "Look one should have one assertion", 1, lookOne.attached.size() );
        assertEquals( "Look one should have zero assertions", 0, lookTwo.attached.size() );

        TestUtil.setLook( ln, lookTwo );
        
        waitForTestRef();
        
        assertEquals( "Look one should have zero assertion", 0, lookOne.attached.size() );
        assertEquals( "Look one should have one assertions", 1, lookTwo.attached.size() );
        
        TestUtil.setLook( ln, lookOne );
        
        waitForTestRef();
        
        assertEquals( "Look one should have one assertion", 1, lookOne.attached.size() );
        assertEquals( "Look one should have zero assertions", 0, lookTwo.attached.size() );
        
    }
    
    
    // Private methods ---------------------------------------------------------
        
    /** Waits until clear method on test reference is called
     */
    
    private static void waitForTestRef() throws InterruptedException {
        boolean flag[] = { false };
        Reference tRef = new TestReference( "Ahoj", Utilities.activeReferenceQueue(), flag );

        tRef.enqueue();
        tRef.clear();

        for( long startTime = System.currentTimeMillis(); System.currentTimeMillis() - startTime < 3000; ) {
            System.gc();
            if ( flag[0] ) {
               break;
            }
        }             

        Thread.sleep( 100 );

        if ( !flag[0] ) {
            fail( "Test reference not cleared" );
        }
    }
       
    // Inner classes -----------------------------------------------------------
    
    private static class MyLook extends DefaultLook {
        
        List attached = new ArrayList();
        
        public MyLook() {
            super( "MyLook" );
        }
        
        public void attachTo(Object representedObject) {
            attached.add( this );
        }
        
        public void detachFrom(Object representedObject) {
            assertNotNull( "Represented objec is null.", representedObject );
            attached.remove( this );
        }
        
        public String getDisplayName () {
            return "MyLook";
        }
        
    }
    
        
    private static class TestReference extends WeakReference implements Runnable {
        
        boolean[] flag;
        
        public TestReference( Object object, ReferenceQueue q, boolean flag[] ) {
            super( object, q );
            this.flag = flag;
            this.flag[0] = false;
        }
        
        public void run() {
            flag[0] = true;
        }
        
    }
    
}
