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

package org.netbeans.modules.aspects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adlookup.AdaptableLookup;
import org.netbeans.modules.adlookup.SingletonizerLookupImpl;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

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
        Adaptor provider = Adaptors.singletonizer(supported, runImpl);
        Object representedObject = "sampleRO";
        Lookup lookup = AdaptableLookup.getLookup(provider, representedObject);
        
        assertNotNull ("Lookup created", lookup);
        // initialized at 40, increased to 48 when added byte[] with cached results
        // 72 is now needed as we have also a reference to ourselves
        ArrayList<Object> ignore = new ArrayList<Object>();
        ignore.add(runImpl);
        ignore.add(provider);
        ignore.add(representedObject);
        ignore.add(Utilities.activeReferenceQueue());
        assertSize ("It is small", Collections.singleton (lookup), 72, ignore.toArray());
        
        Runnable r = (Runnable)lookup.lookup(Runnable.class);
        assertNotNull ("Runnable provided", r);
        r.run ();
        
        assertEquals ("One call to invoke method", 1, runImpl.cnt);
        assertEquals ("Called on RO", representedObject, runImpl.representedObject);
        assertNotNull ("Method provided", runImpl.method);
        assertEquals ("Method of the interface", Runnable.class, runImpl.method.getDeclaringClass ());
        assertEquals ("Method name is run", "run", runImpl.method.getName ());
        
        runImpl.isEnabled = false;
        assertNotNull ("Runnable still available as we have not fired yet", lookup.lookup (Runnable.class));

        // this shall still succeed as the change in isEnabled state has not been fired
        r.run ();
        runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
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
            Adaptor provider = Adaptors.singletonizer(classes, new Singletonizer() {
                public boolean isEnabled (Object o, Class c) {
                    return false;
                }

                public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
                    return null;
                }
                public void addSingletonizerListener(SingletonizerListener listener) throws TooManyListenersException {
                }
                
                public void removeSingletonizerListener(SingletonizerListener listener) {
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
        Adaptor provider = Adaptors.singletonizer(supported, runImpl);
        Object representedObject = new String ("sampleRO");
        Lookup lookup = AdaptableLookup.getLookup(provider, representedObject);
        
        assertSame ("Next time the same lookup is returned", lookup, AdaptableLookup.getLookup(provider, representedObject));
        Object representedObject2 = new String ("sampleRO2");
        Lookup lookup2 = AdaptableLookup.getLookup(provider, representedObject2);
        
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
        if (fireChangeOnAllObjects) {
            runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
        }
        
        assertEquals ("Runnable is not there anymore", 0, resultRunnable.allInstances ().size ()); 
        if (fireChangeOnAllObjects) {
            assertEquals ("Runnable2 is not there anomore", 0, resultRunnable2.allInstances ().size ());
        } else {
            assertEquals ("Runnable2 is still there as nobody fired a change", 1, resultRunnable2.allInstances ().size ()); 
        }
        assertEquals ("ActionListener is not there still", 0, resultListener.allInstances ().size ()); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerRunnable2.assertCount ("No change in run2 or 1", fireChangeOnAllObjects ? 1 : 0);
        listenerListener.assertCount ("This one have not", 0);

        runImpl.isEnabled = true;
        if (fireChangeOnAllObjects) {
            runImpl.listener.stateChanged (SingletonizerEvent.allObjectsChanged(runImpl)); // change in all
        } else {
            runImpl.listener.stateChanged (SingletonizerEvent.anObjectChanged(runImpl, representedObject));
        }
        
        assertEquals ("Runnable reappeared", 1, resultRunnable.allInstances ().size ()); 
        assertEquals ("Runnable2 is still there as nobody fired a change", 1, resultRunnable.allInstances ().size ()); 
        assertEquals ("ActionListener is not there still", 0, resultListener.allInstances ().size ()); 
        listenerRunnable.assertCount ("This one changed", 1);
        listenerListener.assertCount ("This one have not", 0);
        listenerRunnable2.assertCount ("No change in run2 again or 1", fireChangeOnAllObjects ? 1 : 0);
        
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
        assertGC ("Lookup shall disappear as well", refLookup2);
        
        java.lang.ref.WeakReference refRepresented2 = new java.lang.ref.WeakReference (representedObject2);
        representedObject2 = null;
        assertGC ("Represeted object shall disappear as well", refRepresented2);
        
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
    private static class Implementation implements Singletonizer {
        public boolean isEnabled = true;
        public int cnt;
        public Object representedObject;
        public java.lang.reflect.Method method;
        public SingletonizerListener listener;

        public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) {
            this.cnt++;
            this.representedObject = obj;
            this.method = method;
            return null;
        }

        public void addSingletonizerListener(SingletonizerListener listener) throws TooManyListenersException {
            if (this.listener != null) throw new TooManyListenersException ();
            this.listener = listener;
        }

        public void removeSingletonizerListener(SingletonizerListener listener) {
            if (this.listener == listener) {
                this.listener = null;
            }
        }

        public boolean isEnabled(Object obj, Class c) {
            return isEnabled;
        }
    } // end of Implementation
}
