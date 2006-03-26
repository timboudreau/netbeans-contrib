/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adaptable;

import java.io.Serializable;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Uninitializer;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.Singletonizer;

/** Tests for adaptables with lifecycle management. Inherits the basic test
 * to make sure everything works and add few new test methods.
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerLifeCycleTest extends SingletonizerTest {
    public SingletonizerLifeCycleTest(java.lang.String testName) {
        super(testName);
    }
    
    /** Subclassable method to create an Adaptors.singletonizer
     */
    protected Adaptor createSingletonizer (Class[] supported, Singletonizer impl) {
        return Adaptors.singletonizer (supported, impl, null, null, null, null);
    }

    //
    // Noninherited life cycle methods
    //
    
    public void testLifecycleNotifications () throws Exception {
        Implementation impl = new Implementation ();
        InitDeinit firstCall = new InitDeinit ();
        InitDeinit firstListener = new InitDeinit ();
        InitDeinit gone = new InitDeinit ();
        InitDeinit goneListener = new InitDeinit ();
        
        Adaptor a = Adaptors.singletonizer (
            new Class[] { Runnable.class }, 
            impl,
            firstCall,
            firstListener,
            gone,
            goneListener
        );
        
        String ro = new String ("Ahoj");
        
        Adaptable adapt = a.getAdaptable (ro);
        
        firstCall.assertCalls ("None yet", 0, null);
        firstListener.assertCalls ("None yet", 0, null);
        gone.assertCalls ("None yet", 0, null);
        goneListener.assertCalls ("None yet", 0, null);
        
        {   
            Serializable serial = adapt.lookup (Serializable.class);
            assertNull ("We do not provide such interface", serial);

            firstCall.assertCalls ("Still None", 0, null);
            firstListener.assertCalls ("Still None", 0, null);
            gone.assertCalls ("Still None", 0, null);
            goneListener.assertCalls ("Still None", 0, null);
        }
        
        {
            impl.setEnabled (false);
            Runnable run = adapt.lookup (Runnable.class);
            assertNull ("We do not provide such interface", run);

            firstCall.assertCalls ("Still None2", 0, null);
            firstListener.assertCalls ("Still None2", 0, null);
            gone.assertCalls ("Still None2", 0, null);
            goneListener.assertCalls ("Still None2", 0, null);
        }
        
        {
            impl.setEnabled (true);
            Runnable run = adapt.lookup (Runnable.class);
            assertNotNull ("Now we do provide it", run);

            firstCall.assertCalls ("Still None3", 0, null);
            firstListener.assertCalls ("Still None3", 0, null);
            gone.assertCalls ("Still None3", 0, null);
            
            run.run ();
            
            firstCall.assertCalls ("A call has been made", 1, ro);
            firstListener.assertCalls ("Still None4", 0, null);
            gone.assertCalls ("Still None4", 0, null);
            
            Listener listener = new Listener (adapt);
            firstCall.assertCalls ("No further notifications", 0, null);
            firstListener.assertCalls ("first listener added", 1, ro);
            gone.assertCalls ("Still nothing", 0, null);
            goneListener.assertCalls ("Still nothing", 0, null);

            adapt.removeAdaptableListener (listener);
            firstCall.assertCalls ("No further notifications", 0, null);
            firstListener.assertCalls ("no changes", 0, null);
            gone.assertCalls ("no changes", 0, null);
            goneListener.assertCalls ("Last listener is gone", 1, ro);
            
            Listener listener2 = new Listener (adapt);
            firstCall.assertCalls ("no, no no", 0, null);
            firstListener.assertCalls ("yet another first listener", 1, ro);
            gone.assertCalls ("Still nothing4", 0, null);
            goneListener.assertCalls ("Still nothing4", 0, null);
            
        }
        
        java.lang.ref.WeakReference ref = new java.lang.ref.WeakReference (adapt);
        adapt = null;
        assertGC ("Addaptable can disappear", ref);
        System.runFinalization();
        
        firstCall.assertCalls ("no other call made", 0, null);
        firstListener.assertCalls ("no first listener attached", 0, null);
        gone.assertCalls ("The adaptable is gone", 1, ro);
        goneListener.assertCalls ("Listeners may exist, but effectively is gone as well", 1, ro);
    }
    
    
    protected static final class InitDeinit implements Initializer, Uninitializer {
        private int called = 0;
        private Object lastCalledOn = null;
        
        public void initialize(Object representedObject) {
            called++;
            lastCalledOn = representedObject;
        }

        public void uninitialize(Object representedObject) {
            called++;
            lastCalledOn = representedObject;
        }

        public void assertCalls (String msg, int cnt, Object obj) {
            if (cnt != -1) {
                assertEquals (msg, cnt, called);
            }
            if (obj != this) {
                assertEquals (msg, obj, this.lastCalledOn);
            }
            
            this.called = 0;
            this.lastCalledOn = null;
        }
    }
}
