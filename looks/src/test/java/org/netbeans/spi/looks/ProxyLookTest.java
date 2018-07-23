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

import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.Node;
import org.openide.util.Enumerations;

/** Test additional features of ProxyLook.
 *
 * @author  Petr Hrebejk
 */
public class ProxyLookTest extends NbTestCase {

    // Methods of testCase -----------------------------------------------------

    public ProxyLookTest( String testName ) {
        super(testName);
    }

    // Test methods ------------------------------------------------------------
    
    public void testThrowNoException() {
        
        Look composite = Looks.composite( "TC", new Look[] {
              new ExceptionLook( "sl1", null ),
              new ExceptionLook( "sl2", null )
        } );
        
        Look filter = Looks.filter( "TF", new ExceptionLook( "fd1", null ), ProxyLook.ALL_METHODS );
        
        try {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( composite, "RO", null );
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( filter, "RO", null );
        }
        catch( Exception e  ) {
            fail( "No exception should be thrown" );
        }
        
    }
    
    public void testThrowClassCastException() {
        
        Look composite = Looks.composite( "TC", new Look[] {
              new ExceptionLook( "sl1", null ),
              new ExceptionLook( "sl2", ClassCastException.class )
        } );
        
        Look filter = Looks.filter( "TF", new ExceptionLook( "fd1", ClassCastException.class ), ProxyLook.ALL_METHODS );
        
        try {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( composite, "RO", null );
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( filter, "RO", null );
            fail( "ClassCastException should be thrown, but it was not" );
        }
        catch( Exception e  ) {
            assertTrue( "ClassCastException should be thrown. Was : " + e.getClass(), e instanceof ClassCastException );             
        }
        
    }
    
    public void testThrowIllegalArgumentException() {
        Look composite = Looks.composite( "TC", new Look[] {
              new ExceptionLook( "sl1", null ),
              new ExceptionLook( "sl2", IllegalArgumentException.class )
        } );
        
        Look filter = Looks.filter( "TF", new ExceptionLook( "fd1", IllegalArgumentException.class ), ProxyLook.ALL_METHODS );
        
        try {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( composite, "RO", null );
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( filter, "RO", null );
            fail( "IllegalArgumentException should be thrown, but it was not" );
        }
        catch( Exception e  ) {
            assertTrue( "IllegalArgumentException should be thrown. Was : " + e.getClass(), e instanceof IllegalArgumentException );             
        }
        
    }

    public void testDetachWhenExceptionWasThrown() {
        
        ExceptionLook sl1 = new ExceptionLook( "sl1", null );
        ExceptionLook sl2 = new ExceptionLook( "sl2", null );
        ExceptionLook sl3 = new ExceptionLook( "sl2", IllegalArgumentException.class );
        ExceptionLook sl4 = new ExceptionLook( "sl2", ClassCastException.class );
        
        Look composite = Looks.composite( "TC", new Look[] { sl1, sl2, sl3, sl4 } );
        
        try {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( composite, "RO", null );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "Sl1 attachTo", 1, sl1.getAttachToCount() );
            assertEquals( "Sl1 detachFrom", 1, sl1.getDetachFromCount() );
            assertEquals( "Sl2 attachTo", 1, sl2.getAttachToCount() );
            assertEquals( "Sl2 detachFrom", 1, sl2.getDetachFromCount() );
            assertEquals( "Sl3 attachTo", 0, sl3.getAttachToCount() );
            assertEquals( "Sl3 detachFrom", 0, sl3.getDetachFromCount() );
            assertEquals( "Sl4 attachTo", 0, sl4.getAttachToCount() );
            assertEquals( "Sl4 detachFrom", 0, sl4.getDetachFromCount() );
            return;
        }
        
        fail( "Exception not thrown" );
    }

    public void testAttachDetachAfterSeletorChange( ) {
        
        AttachDetachProvider provider = new AttachDetachProvider();
        LookSelector selector = Selectors.selector( provider );
        Look proxy = new AttachDetachProxyLook( "TEST_AD", selector );
        
        Node n = Nodes.node( "KAREL", null, Selectors.singleton( proxy ) );
        
        assertEquals( "AD_LOOK_1 attachTo", 1, provider.AD_LOOK[0].getAttachCount() );
        assertEquals( "AD_LOOK_1 detachFrom", 0, provider.AD_LOOK[0].getDetachCount() );
        assertEquals( "AD_LOOK_2 attachTo", 0, provider.AD_LOOK[1].getAttachCount() );
        assertEquals( "AD_LOOK_2 detachFrom", 0, provider.AD_LOOK[1].getDetachCount() );
        
        provider.switchLook();
        
        assertEquals( "AD_LOOK_1 attachTo", 0, provider.AD_LOOK[0].getAttachCount() );
        assertEquals( "AD_LOOK_1 detachFrom", 1, provider.AD_LOOK[0].getDetachCount() );
        assertEquals( "AD_LOOK_2 attachTo", 1, provider.AD_LOOK[1].getAttachCount() );
        assertEquals( "AD_LOOK_2 detachFrom", 0, provider.AD_LOOK[1].getDetachCount() );
    }
    
    // Innerclasses ------------------------------------------------------------


    public static class ExceptionLook extends Look {
        
        private int attachTo;
        private int detachFrom;
        
        private Class exClass;
                
        public ExceptionLook( String name, Class exClass ) {
            super( name );
            
            if ( exClass != null && !( RuntimeException.class.isAssignableFrom( exClass ) ) ) {
                fail( "Bad usage of the test " + exClass + " is not runtimeException" );
            }
            
            this.exClass = exClass;
        }
        
        public void attachTo( Object representedObject ) { 
            if ( exClass != null) {
                try { 
                    System.err.println("exClass=" + exClass);//XXX
                    throw (RuntimeException)exClass.newInstance();
                }
                catch ( InstantiationException e ) {
                    fail( "Bad usage of the test " + e );
                }
                catch ( IllegalAccessException e ) {
                    fail( "Bad usage of the test " + e );
                }
            }
            
            attachTo++;
        }
        
        public void detachFrom( Object representedObject ) {
            detachFrom++;
        }
        
        public int getAttachToCount() {
            return attachTo;
        }
        
        public int getDetachFromCount() {
            return detachFrom;
        }
        
    }

    private static class AttachDetachProvider implements ChangeableLookProvider {
        
        private final AttachDetachLook AD_LOOK[] = new AttachDetachLook[] { 
            new AttachDetachLook( "AD_LOOK_1" ),
            new AttachDetachLook( "AD_LOOK_2" ) 
        };
        
        private int current = 0;
        
        private ChangeListener listener;
        
        public Enumeration getLooksForObject( Object representedObject ) {
            return Enumerations.singleton(AD_LOOK[current]);
        }        
                
        public void switchLook() {
            current = current == 0 ? 1 : 0;
            
            if ( listener != null ) {
                listener.stateChanged( null );
            }            
        }
        
        public synchronized void addChangeListener(ChangeListener listener) throws TooManyListenersException {
            if ( this.listener != null ) {
                throw new TooManyListenersException();
            }
            else {
                this.listener = listener;
            }
        }        
        
        public Object getKeyForObject(Object representedObject) {
            return String.class;
        }
        
        public Enumeration getLooksForKey(Object key) {
            return Enumerations.singleton(AD_LOOK[current]);
        }
        
    }
    
    private static class AttachDetachProxyLook extends ProxyLook {
        
        public AttachDetachProxyLook( String name, LookSelector selector ) {
            super( name, selector );
        }
        
        
    }
    
    private static class AttachDetachLook extends Look {
        
        private int attachCount;
        private int detachCount;
        
        public AttachDetachLook( String name ) {
            super( name );
        }

        protected void attachTo(Object representedObject) {
            attachCount ++;
        }        
        

        
        protected void detachFrom(Object representedObject) {
            detachCount ++;
        }        
        
        
        public int getAttachCount() {
            int c = attachCount;
            attachCount = 0;
            return c;
        }
    
        public int getDetachCount() {
            int c = detachCount;
            detachCount = 0;
            return c;
        }
        
    }
    
    
}
