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

package org.netbeans.modules.adaptable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TooManyListenersException;

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
    
    /** Subclassable method to create an Adaptors.singletonizer
     */
    protected Adaptor createSingletonizer (Class[] supported, Singletonizer impl) {
        return Adaptors.singletonizer (supported, impl);
    }

    public void testProvidesImplementationOfRunnable () {
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = createSingletonizer (supported, runImpl);
        Object representedObject = "sampleRO";
        Adaptable lookup = provider.getAdaptable (representedObject);
        
        assertNotNull ("Lookup created", lookup);
        // initialized at 40, increased to 48 when added byte[] with cached results
        // now 72 as it also includes the Reference object: 
        //   There is even a better way how to lower this and that 
        //   is to implement the Adaptable interface by the proxy as well, 
        //   but it can wait until really needed.

        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add(provider);
        arr.add(runImpl);
        arr.add(representedObject);
        arr.addAll(Suite.excludeFromSize);

        assertSize ("It is small", Collections.singleton (lookup), 72, arr.toArray());
        
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
        runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
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
            Adaptor provider = createSingletonizer (classes, new Singletonizer () {
                public boolean isEnabled (Object obj, Class c) {
                    return false;
                }

                public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
                    return null;
                }
                public void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
                }
                
                public void removeSingletonizerListener (SingletonizerListener listener) {
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
        Adaptor provider = createSingletonizer (supported, runImpl);
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
            runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
        }
        
        assertNull ("Runnable is not there anymore", lookup.lookup (Runnable.class)); 
        if (fireChangeOnAllObjects) {
            assertNull ("Runnable2 is not there anomore", lookup2.lookup (Runnable.class));
        } else {
            assertNotNull ("Runnable2 is still there as nobody fired a change", lookup2.lookup (Runnable.class));
        }
        assertNull ("ActionListener is not there still", lookup.lookup (java.awt.event.ActionListener.class)); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerRunnable.assertAffected("Runnable has been changed", Runnable.class);
        listenerListener.assertCount ("This one as well", 1);
        listenerListener.assertAffected("Runnable has been changed", Runnable.class);
        if (fireChangeOnAllObjects) {
            listenerRunnable2.assertCount ("No change in run2 or 1", 1);
            listenerRunnable2.assertAffected("Runnable has been changed", Runnable.class);
        } else {
            listenerRunnable2.assertCount ("No change in run2 or 1", 0);
            listenerRunnable2.assertAffected("No Runnable has been changed");
        }

        runImpl.isEnabled = true;
        if (fireChangeOnAllObjects) {
            runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
        }
        
        assertNotNull ("Runnable reappeared", lookup.lookup (Runnable.class)); 
        assertNotNull ("Runnable2 is still there as nobody fired a change", lookup2.lookup (Runnable.class));
        assertNull ("ActionListener is not there still", lookup.lookup (java.awt.event.ActionListener.class));
        listenerRunnable.assertCount ("This one changed", 1);
        listenerRunnable.assertAffected("again change in run", Runnable.class);
        listenerListener.assertCount ("This as well", 1);
        listenerListener.assertAffected("again change in run", Runnable.class);
        if (fireChangeOnAllObjects) {
            listenerRunnable2.assertCount ("No change in run2 or 1", 1);
            listenerRunnable2.assertAffected("Runnable has been changed", Runnable.class);
        } else {
            listenerRunnable2.assertCount ("No change in run2 or 1", 0);
            listenerRunnable2.assertAffected("No Runnable has been changed");
        }
        
        java.lang.ref.WeakReference<Object> refLookup2 = new java.lang.ref.WeakReference<Object>(lookup2);
        lookup2 = null;
        assertGC ("Lookup shall disappear as well", refLookup2);
        
        java.lang.ref.WeakReference<Object> refRepresented2 = new java.lang.ref.WeakReference<Object>(representedObject2);
        representedObject2 = null;
        assertGC ("Represeted object shall disappear as well", refRepresented2);
    }
    
    public void testMoreListeners () throws Exception {
        final int cnt = 5;
        
        
        Class[] supported = { Runnable.class };
        
        Implementation runImpl = new Implementation ();
        Adaptor provider = createSingletonizer (supported, runImpl);
        
        Adaptable adaptable = provider.getAdaptable (this);
        
        Listener[] arr = new Listener[cnt];
        for (int i = 0; i < cnt; i++) {
            arr[i] = new Listener (adaptable);
        }
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, new Object ()));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - no changes as the fire was on other object", 0);
        }
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - one change", 1);
        }
        
        
        for (int i = 0; i < cnt; i++) {
            adaptable.removeAdaptableListener (arr[i]);
        }
        
        
        runImpl.isEnabled = false;
        runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl));
        
        for (int i = 0; i < cnt; i++) {
            arr[i].assertCount (i + " - no change listener removed", 0);
        }
    }

    /** Counting listener */
    protected static final class Listener implements AdaptableListener {
        public int cnt;
        public Set<Class> affected = new HashSet<Class>();
        
        public Listener (Adaptable res) {
            res.addAdaptableListener (this);
        }
        
        public void stateChanged (AdaptableEvent ev) {
            cnt++;
            affected.addAll(ev.getAffectedClasses());

            try {
                Iterator<?> it = ev.getAffectedClasses().iterator();
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                    fail("Modifications to getAffectedClasses() must be prevented");
                }
            } catch (UnsupportedOperationException ex) {
                // ok
            }
        }
        
        public void assertCount (String msg, int cnt) {
            assertEquals (msg, cnt, this.cnt);
            this.cnt = 0;
        }

        public void assertAffected(String msg, Class... what) {
            Set<Class> a = new HashSet<Class>();
            for (Class c : what) {
                a.add(c);
            }
            assertEquals(msg, affected, a);
            affected.clear();
        }

    } // end of Listener
    
    /** Implementation of singletonizer */
    protected static final class Implementation implements org.netbeans.spi.adaptable.Singletonizer {
        private boolean isEnabled = true;
        public int cnt;
        public Object representedObject;
        public java.lang.reflect.Method method;
        public SingletonizerListener listener;

        public boolean isEnabled (Object obj, Class c) {
            return isEnabled;
        }

        public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
            this.cnt++;
            this.representedObject = obj;
            this.method = method;
            return null;
        }

        public void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
            if (this.listener != null) throw new TooManyListenersException ();
            this.listener = listener;
        }

        public void removeSingletonizerListener (SingletonizerListener listener) {
            if (this.listener == listener) {
                this.listener = null;
            }
        }
        
        public void setEnabled (boolean e) {
            if (e == isEnabled) return;
            
            isEnabled = e;
            if (listener != null) {
                listener.stateChanged(SingletonizerEvent.allObjectsChanged(this));
            }
        }
    } // end of Implementation
}
