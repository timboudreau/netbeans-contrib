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

import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.*;


/** Tests Singletonizer behaviour.`
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerTest extends org.netbeans.junit.NbTestCase {
    public SingletonizerTest(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp () throws Exception {
        super.setUp ();
    }

    public void testProvidesImplementationOfRunnable () {
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = Adaptors.singletonizer (supported, runImpl);
        Object representedObject = "sampleRO";
        Adaptable lookup = provider.getAdaptable (representedObject);
        
        assertNotNull ("Lookup created", lookup);
        // initialized at 40, increased to 48 when added byte[] with cached results
        assertSize ("It is small", Collections.singleton (lookup), 48, new Object[] { runImpl, representedObject });
        
        Runnable r = (Runnable)lookup.lookup(Runnable.class);
        assertNotNull ("Runnable provided", r);
        r.run ();
        
        assertEquals ("One call to invoke method", 1, runImpl.cnt);
        assertEquals ("Called on RO", representedObject, runImpl.representedObject);
        assertNotNull ("Method provided", runImpl.method);
        assertEquals ("Method of the interface", Runnable.class, runImpl.method.getDeclaringClass ());
        assertEquals ("Method name is run", "run", runImpl.method.getName ());
        
        runImpl.isEnabled = false;
        
        assertNotNull ("Runnable is there as nobody fired a change", lookup.lookup (Runnable.class));

        // this shall still succeed as the change in isEnabled state has not been fired
        r.run ();
        runImpl.listener.stateChanged (new ChangeEvent (representedObject));
        assertNull ("Runnable of course is no longer there", lookup.lookup (Runnable.class));
        try {
            r.run ();
            fail ("Should throw IllegalStateException");
        } catch (IllegalStateException ex) {
            // ok, we are not enabled anymore
        }
    }

    public void testSingletonizerCanDelegateJustToInterfaces () {
        Class[] classes = { Integer.class };
        
        try {
            Adaptor provider = Adaptors.singletonizer (classes, new Singletonizer () {
                public boolean isEnabled (Class c) {
                    return false;
                }

                public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
                    return null;
                }
                public void addChangeListener (ChangeListener listener) throws TooManyListenersException {
                }
                
                public void removeChangeListener (ChangeListener listener) {
                }
            });
            fail ("Should fail, as non interface classes cannot be supported");
        } catch (IllegalArgumentException ex) {
            // ok, that is what we expect
        }
    }

    public void testFiringOfChangesGeneratesEvents () throws Exception {
        doFiringOfChanges (false);
    }
    public void testFiringOfChangesOnAllObjectsGeneratesEvents () throws Exception {
        doFiringOfChanges (true);
    }
    
    private void doFiringOfChanges (boolean fireChangeOnAllObjects) {
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = Adaptors.singletonizer (supported, runImpl);
        Object representedObject = new String ("sampleRO");
        Adaptable lookup = provider.getAdaptable (representedObject);
        
        assertSame ("Next time the same lookup is returned", lookup, provider.getAdaptable (representedObject));
        Object representedObject2 = new String ("sampleRO2");
        Adaptable lookup2 = provider.getAdaptable (representedObject2);
        
        assertNotNull ("Runnable is there A", lookup.lookup (Runnable.class)); 
        assertNotNull ("Runnable is there B", lookup2.lookup (Runnable.class)); 
        
        
        Listener listenerListener = new Listener (lookup);
        Listener listenerRunnable = new Listener (lookup);
        Listener listenerRunnable2 = new Listener (lookup2);
        
        runImpl.isEnabled = false;
        if (fireChangeOnAllObjects) {
            runImpl.listener.stateChanged (new ChangeEvent (runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (new ChangeEvent (representedObject));
        }
        
        assertNull ("Runnable is not there anymore", lookup.lookup (Runnable.class)); 
        if (fireChangeOnAllObjects) {
            assertNull ("Runnable2 is not there anomore", lookup2.lookup (Runnable.class));
        } else {
            assertNotNull ("Runnable2 is still there as nobody fired a change", lookup2.lookup (Runnable.class));
        }
        assertNull ("ActionListener is not there still", lookup.lookup (java.awt.event.ActionListener.class)); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerListener.assertCount ("This one as well", 1);
        listenerRunnable2.assertCount ("No change in run2 or 1", fireChangeOnAllObjects ? 1 : 0);

        runImpl.isEnabled = true;
        if (fireChangeOnAllObjects) {
            runImpl.listener.stateChanged (new ChangeEvent (runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (new ChangeEvent (representedObject));
        }
        
        assertNotNull ("Runnable reappeared", lookup.lookup (Runnable.class)); 
        assertNotNull ("Runnable2 is still there as nobody fired a change", lookup2.lookup (Runnable.class));
        assertNull ("ActionListener is not there still", lookup.lookup (java.awt.event.ActionListener.class));
        listenerRunnable.assertCount ("This one changed", 1);
        listenerListener.assertCount ("This as well", 1);
        listenerRunnable2.assertCount ("No change in run2 again or 1", fireChangeOnAllObjects ? 1 : 0);
        
        java.lang.ref.WeakReference refLookup2 = new java.lang.ref.WeakReference (lookup2);
        lookup2 = null;
        assertGC ("Lookup shall disappear as well", refLookup2);
        
        java.lang.ref.WeakReference refRepresented2 = new java.lang.ref.WeakReference (representedObject2);
        representedObject2 = null;
        assertGC ("Represeted object shall disappear as well", refRepresented2);
    }
    
    public void testMoreListeners () throws Exception {
        final int cnt = 5;
        
        
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = Adaptors.singletonizer (supported, runImpl);
        
        Adaptable adaptable = provider.getAdaptable (this);
        
        Listener[] arr = new Listener[cnt];
        for (int i = 0; i < cnt; i++) {
            arr[i] = new Listener (adaptable);
        }
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (new ChangeEvent (new Object ()));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - no changes as the fire was on other object", 0);
        }
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (new ChangeEvent (this));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - one change", 1);
        }
        
        
        for (int i = 0; i < cnt; i++) {
            adaptable.removeChangeListener (arr[i]);
        }
        
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (new ChangeEvent (this));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - no change listener removed", 0);
        }
    }

    /** Counting listener */
    private static class Listener implements javax.swing.event.ChangeListener {
        public int cnt;
        
        public Listener (Adaptable res) {
            res.addChangeListener (this);
        }
        
        public void stateChanged (javax.swing.event.ChangeEvent ev) {
            cnt++;
        }
        
        public void assertCount (String msg, int cnt) {
            assertEquals (msg, cnt, this.cnt);
            this.cnt = 0;
        }
    } // end of Listener
    
    /** Implementation of singletonizer */
    private static class Implementation implements org.netbeans.spi.adaptable.Singletonizer {
        public boolean isEnabled = true;
        public int cnt;
        public Object representedObject;
        public java.lang.reflect.Method method;
        public ChangeListener listener;

        public boolean isEnabled (Class c) {
            return isEnabled;
        }

        public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
            this.cnt++;
            this.representedObject = obj;
            this.method = method;
            return null;
        }

        public void addChangeListener (ChangeListener listener) throws TooManyListenersException {
            if (this.listener != null) throw new TooManyListenersException ();
            this.listener = listener;
        }

        public void removeChangeListener (ChangeListener listener) {
            if (this.listener == listener) {
                this.listener = null;
            }
        }
    } // end of Implementation
}
