/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.aspects;

import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.aspects.*;
import org.openide.util.Lookup;

/** Tests Singletonizer behaviour.`
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerTest extends org.netbeans.junit.NbTestCase {
    public SingletonizerTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new org.netbeans.junit.NbTestSuite(SingletonizerTest.class);
        
        return suite;
    }
    
    protected void setUp () throws Exception {
        super.setUp ();
    }

    public void testProvidesImplementationOfRunnable () {
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        AspectProvider provider = Singletonizer.create (supported, runImpl);
        Object representedObject = "sampleRO";
        Lookup lookup = Aspects.getLookup(representedObject, provider);
        
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
        
        assertNull ("Runnable not available any longer", lookup.lookup (Runnable.class));

        // this shall still succeed as the change in isEnabled state has not been fired
        r.run ();
        runImpl.listener.stateChanged (new ChangeEvent (representedObject));
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
            AspectProvider provider = Singletonizer.create (classes, new Singletonizer.Impl () {
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
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        AspectProvider provider = Singletonizer.create (supported, runImpl);
        Object representedObject = "sampleRO";
        Lookup lookup = Aspects.getLookup(representedObject, provider);
        
        assertSame ("Next time the same lookup is returned", lookup, Aspects.getLookup(representedObject, provider));
        Object representedObject2 = "sampleRO2";
        Lookup lookup2 = Aspects.getLookup(representedObject2, provider);
        
        Lookup.Result resultListener = lookup.lookup (new Lookup.Template (java.awt.event.ActionListener.class));
        assertNotNull (resultListener);
        Lookup.Result resultRunnable = lookup.lookup (new Lookup.Template (Runnable.class));
        assertNotNull (resultRunnable);
        Lookup.Result resultRunnable2 = lookup2.lookup (new Lookup.Template (Runnable.class));
        assertNotNull (resultRunnable2);
        
        Listener listenerListener = new Listener (resultListener);
        Listener listenerRunnable = new Listener (resultRunnable);
        Listener listenerRunnable2 = new Listener (resultRunnable2);
        
        assertEquals ("Runnable is there once", 1, resultRunnable.allInstances ().size ()); 
        assertEquals ("ActionListener is not there", 0, resultListener.allInstances ().size ()); 
        assertEquals ("Runnable2 is there once", 1, resultRunnable2.allInstances ().size ()); 
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (new ChangeEvent (representedObject));
        
        assertEquals ("Runnable is not there anymore", 0, resultRunnable.allInstances ().size ()); 
        assertEquals ("Runnable2 is still there as nobody fired a change", 1, resultRunnable2.allInstances ().size ()); 
        assertEquals ("ActionListener is not there still", 0, resultListener.allInstances ().size ()); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerRunnable2.assertCount ("No change in run2", 0);
        listenerListener.assertCount ("This one have not", 0);

        runImpl.isEnabled = true;
        runImpl.listener.stateChanged (new ChangeEvent (representedObject));
        
        assertEquals ("Runnable reappeared", 1, resultRunnable.allInstances ().size ()); 
        assertEquals ("Runnable2 is still there as nobody fired a change", 1, resultRunnable.allInstances ().size ()); 
        assertEquals ("ActionListener is not there still", 0, resultListener.allInstances ().size ()); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerListener.assertCount ("This one have not", 0);
        listenerRunnable2.assertCount ("No change in run2 again", 0);
        
        java.lang.ref.WeakReference refRunnable = new java.lang.ref.WeakReference (resultRunnable);
        java.lang.ref.WeakReference refRunnable2 = new java.lang.ref.WeakReference (resultRunnable2);
        java.lang.ref.WeakReference refListener = new java.lang.ref.WeakReference (resultListener);
        
        resultRunnable2 = null;
        assertGC ("result 2 shall GC", refRunnable2);
        
        resultListener = null;
        assertGC ("result for Listener shall GC", refListener);
        
        resultRunnable = null;
        assertGC ("All results shall GC", refRunnable);
        
        java.lang.ref.WeakReference refLookup2 = new java.lang.ref.WeakReference (lookup2);
        lookup2 = null;
//        assertGC ("Lookup shall disappear as well", refLookup2);
    }
    
    /** Counting listener */
    private static class Listener implements org.openide.util.LookupListener {
        public int cnt;
        
        public Listener (Lookup.Result res) {
            res.addLookupListener (this);
        }
        
        public void resultChanged (org.openide.util.LookupEvent ev) {
            cnt++;
        }
        
        public void assertCount (String msg, int cnt) {
            assertEquals (msg, cnt, this.cnt);
            this.cnt = 0;
        }
    } // end of Listener
    
    /** Implementation of singletonizer */
    private static class Implementation implements Singletonizer.Impl {
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
