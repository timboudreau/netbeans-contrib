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

package org.netbeans.modules.adlookup;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableEvent;
import org.netbeans.api.adaptable.AdaptableListener;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.modules.adaptable.Accessor;
import org.netbeans.modules.adaptable.ProviderImpl;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;
import org.netbeans.spi.adaptable.Uninitializer;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/** A bunch of utility methods that support making functionality
 * "singletonized". There is a more instances, but only one object
 * that does the functionality (like represnted object vs. look).
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerLookupImpl extends Object 
implements SingletonizerListener, ProviderImpl {
    /** Keeps track of existing lookups. Type of Object to LkpReference<Lkp> */
    private java.util.HashMap<Object,LkpRef> lookups = new java.util.HashMap<Object,LkpRef>();
    

    private Singletonizer single;
    private Class[] classes;
    /** fields for life cycle control */
    final Initializer initCall;
    final Initializer initListener;
    final Uninitializer noListener;
    final Uninitializer gc;
    
    /** We control the life cycle */
    SingletonizerLookupImpl (
        Class[] classes,
        Singletonizer single,
        Initializer initCall,
        Initializer initListener,
        Uninitializer noListener,
        Uninitializer gc
    ) {
        this.classes = classes;
        this.single = single;
        this.initCall = initCall;
        this.initListener = initListener;
        this.noListener = noListener;
        this.gc = gc;
    }
    
    //
    // Implementation of ProviderImpl
    //
   
    public Adaptable createLookup(Object obj, Adaptor adaptor) {
        LkpRef ref = lookups.get (obj);
        Lkp lkp = ref == null ? null : ref.get ();
        if (lkp == null) {
            lkp = new Lkp (obj, adaptor, classes);
            lookups.put (obj, lkp.ref);
        }
        return lkp;
    }
    
    public void stateChanged(SingletonizerEvent e) {
        Object affected = Accessor.SPI.getAffectedObject(e);
        Set<Class> types = Accessor.SPI.getAffectedClasses(e);

        if (affected == null) {
            // refresh all of them
            java.util.Iterator<LkpRef> it = lookups.values ().iterator ();
            while (it.hasNext ()) {
                LkpRef ref = it.next ();
                Lkp lkp = ref.get ();
                if (lkp != null) {
                    lkp.update (types);
                }
            }
        } else {
            LkpRef ref = lookups.get(affected);
            if (ref == null) {
                return;
            }
            Lkp lkp = ref.get ();
            if (lkp != null) {
                lkp.update(types);
            }
        }
    }

    final Singletonizer getSingletonizer() {
        return single;
    }

    private void removeObject(Object object) {
        lookups.remove(object);
    }


    //
    // The Lookup that points to a represented object
    //
    
    private static final class Lkp extends Lookup 
    implements java.lang.reflect.InvocationHandler, Adaptable {
        final Object proxy; 
        /** array of 0/1 for each class in impl.classes to identify the state 
         * whether it should be enabled or not */
        private byte[] enabled;
        /** reference to results and listeners */
        private LkpChainItem first;
        /** reference to me */
        private LkpRef ref;
        
        public Lkp (Object obj, Adaptor impl, Class[] classes) {
            this.proxy = java.lang.reflect.Proxy.newProxyInstance(
                getClass ().getClassLoader (), 
                classes,
                this
            );
            this.ref = new LkpRef(this, obj, impl);
        }
        
        public <T> T lookup(Class<T> clazz) {
            if (isEnabled (clazz)) {
                return clazz.cast (proxy);
            }
            return null;
        }
        
        public synchronized void addAdaptableListener (AdaptableListener l) {
            boolean callAdded;
            synchronized (this) {
                callAdded = this.first == null;

                ReferenceIterator it = new ReferenceIterator (this.first);
                while (it.next()) {
                    AdaptableListener now = it.current().getListener();
                    if (now == l) {
                        return;
                    }
                }
                this.first = it.first ();

                LkpChainItem newRef = new LkpReferenceToListener (l, this);
                newRef.setNext(first);
                first = newRef;
            }
            
            
            if (callAdded && ref.getImpl().initListener != null) {
                ref.getImpl().initListener.initialize (ref.getRepresentedObject());
            }
        }
        
        public void removeAdaptableListener (AdaptableListener l) {
            boolean callRemoved = true;
            synchronized (this) {
                ReferenceIterator it = new ReferenceIterator (this.first);
                while (it.next()) {
                    if (it.current().getListener() == l) {
                        it.remove();
                    }
                }
                this.first = it.first ();

                callRemoved = this.first == null;
            }
            
            if (callRemoved && ref.getImpl().noListener != null) {
                ref.getImpl().noListener.uninitialize (ref.getRepresentedObject());
            }
        }
        
        /** Called when one wants an interface provided by this singletonizer
         */
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            if (isEnabled (method.getDeclaringClass ())) {
                if (ref.madeFirstCall ()) {
                    if (ref.getImpl().initCall != null) {
                        ref.getImpl().initCall.initialize (ref.getRepresentedObject());
                    }
                }
                
                return ref.getImpl().getSingletonizer ().invoke(ref.getRepresentedObject(), method, args);
            } else {
                throw new IllegalStateException ("Method " + method + " cannot be invoked when " + method.getDeclaringClass ());
            }
        }
        
        /** Updates its state. */
        final void update (Set<Class> affectedByDefault) {
            Class<?>[] allSupportedClasses = proxy.getClass().getInterfaces ();

            byte[] prev = enabled;
            byte[] now = null;
            Set<Class> af;
            if (prev != null) {
                now = computeEnabledState(allSupportedClasses);
                HashSet<Class> haf = new HashSet<Class>();
                if (affectedByDefault != null) {
                    haf.addAll(affectedByDefault);
                }
                for (int i = 0; i < allSupportedClasses.length; i++) {
                    int index = i / 8;
                    int offset = 1 << (i % 8);
                    if ((prev[index] & offset) != (now[index] & offset)) {
                        haf.add(allSupportedClasses[i]);
                    }
                }
                af = Collections.unmodifiableSet(haf);
            } else {
                af = affectedByDefault == null ? Collections.<Class>emptySet() : Collections.unmodifiableSet(affectedByDefault);
            }
            enabled = now;

            AdaptableEvent ev = null;
            ReferenceIterator it = new ReferenceIterator (this.first);
            while (it.next()) {
                LkpResult res = it.current().getResult();
                if (res != null) {
                    res.check(af);
                    continue;
                }

                AdaptableListener l = it.current().getListener();
                if (l != null) {
                    if (ev == null) {
                        ev = Accessor.API.createEvent(this, af);
                    }
                    l.stateChanged(ev);
                    continue;
                }
             }
            this.first = it.first (); 

        }
        
        /** Checks whether a class is enabled or not */
        final boolean isEnabled (Class<?> clazz) {
            Class[] allSupportedClasses = proxy.getClass().getInterfaces ();
            if (enabled == null) {
                enabled = computeEnabledState(allSupportedClasses);
            }

            for (int i = 0; i < allSupportedClasses.length; i++) {
                if (clazz.isAssignableFrom (allSupportedClasses[i])) {
                    int index = i / 8;
                    int offset = 1 << (i % 8);
                    if ((enabled[index] & offset) != 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        private byte[] computeEnabledState(final Class[] allSupportedClasses) {
            byte[] arr = new byte[(allSupportedClasses.length + 7) / 8];
            int offset = 1;
            int index = 0;
            for (int i = 0; i < allSupportedClasses.length; i++) {
                if (ref.getImpl().getSingletonizer ().isEnabled(ref.getRepresentedObject(), allSupportedClasses[i])) {
                    arr[index] |= offset;
                }
                if (offset == 128) {
                    index++;
                    offset = 1;
                } else {
                    offset = offset << 1;
                }
            }
            return arr;
        }

        public synchronized <T> Lookup.Result<T> lookup(Lookup.Template<T> template) {
            LkpResult<T> r = new LkpResult<T>();
            LkpReferenceToResult newRef = new LkpReferenceToResult (r, this, template);
            newRef.setNext(first);
            first = newRef;
            return r;
        }

        /** When a result is GCed, this clean ups the references to it
         * from the chain of results we have to maintain */
        final void cleanUpResult (Lookup.Template t) {
            ReferenceIterator it = new ReferenceIterator (this.first);
            while (it.next());
            this.first = it.first ();
        }
    } // end of Lkp

    /** A reference to adaptable that gets cleared when it is gone.
     */
    private static final class LkpRef extends java.lang.ref.WeakReference<Lkp>
    implements Runnable {
        /** we need to know the object we work with */
        private final Object obj;
        /** implementation we work with */
        private volatile Object impl;

        public LkpRef (Lkp adapt, Object obj, Adaptor impl) {
            super (adapt, Utilities.activeReferenceQueue());
            this.obj = obj;
            this.impl = impl;
        }

        final Object getRepresentedObject() {
            return obj;
        }

        final SingletonizerLookupImpl getImpl() {
            Object i = impl;
            if (i instanceof SingletonizerLookupImpl) {
                return (SingletonizerLookupImpl)i;
            } else {
                return (SingletonizerLookupImpl)Accessor.API.getProviderImpl ((Adaptor)i);
            }
        }
        
        /** Marks this reference as already done first call.
         * @return true if the first call has not been done yet.
         */
        final boolean madeFirstCall () {
            Object i = impl;
            if (this.impl instanceof SingletonizerLookupImpl) {
                return false;
            }
            this.impl = getImpl ();
            return true;
        }
        
        public void run() {
            if (getImpl().gc != null) {
                getImpl().gc.uninitialize (getRepresentedObject());
            }
            if (getImpl().noListener != null) {
                getImpl().noListener.uninitialize (getRepresentedObject());
            }
            getImpl().removeObject(getRepresentedObject());
        }
    } // end of AdaptableRef
    
    /** Result for the lookup.
     */
    private static final class LkpResult<T> extends Lookup.Result<T> {
        private LkpReferenceToResult reference;
        private org.openide.util.LookupListener listener;
        
        public LkpResult () {
        }
        
        public void addLookupListener (LookupListener l) {
            this.listener = l;
        }
        public void removeLookupListener (LookupListener l) {
            this.listener = null;
        }

        @SuppressWarnings("unchecked")
        public Collection<? extends T> allInstances () {
            boolean enabled = reference.getLookup().isEnabled (reference.getTemplate().getType ());
            return enabled ? Collections.nCopies (1, (T)reference.getLookup().proxy) : Collections.<T>emptySet();
        }
        
        /** Notification from lookup that there were some changes.
         * @param changedClasses the set of <Class> objects that changed
         */
        final void check (java.util.Set changedClasses) {
            if (changedClasses.contains (reference.getTemplate().getType ())) {
                if (listener != null) {
                    listener.resultChanged (new org.openide.util.LookupEvent (this));
                }
            }
        }
    } // end of LkpResult

    /** Chain of objects attached as listeners.
     */
    static interface LkpChainItem {
        public LkpResult getResult();
        public AdaptableListener getListener();
        public LkpChainItem getNext();
        public void setNext(LkpChainItem next);
        public Lookup.Template getTemplate();
        public Lkp getLookup();
    }
    
    /** Reference to a result  
     */
    static final class LkpReferenceToResult extends java.lang.ref.WeakReference<LkpResult>
    implements Runnable, LkpChainItem {
        /** next refernece in chain, modified only from AbstractLookup or this */
        private LkpChainItem next;
        /** the template for the result */
        private final Lkp.Template template;
        /** the lookup we are attached to */
        private final Lkp lookup;
        
        /** Creates a weak refernece to a new result R in context of lookup
         * for given template
         */
        LkpReferenceToResult (LkpResult result, Lkp lookup, Lkp.Template template) {
            super (result, org.openide.util.Utilities.activeReferenceQueue ());
            this.template = template;
            this.lookup = lookup;
            result.reference = this;
        }
        
        /** Returns the result or null
         */
        public LkpResult getResult () {
            return get ();
        }

        /** Returns the listener or null
         */
        public AdaptableListener getListener() {
            return null;
        }
        
        /** Cleans the reference. Implements Runnable interface, do not call
         * directly.
         */
        public void run() {
            getLookup().cleanUpResult (this.getTemplate());
        }

        public LkpChainItem getNext() {
            return next;
        }

        public void setNext(LkpChainItem next) {
            this.next = next;
        }

        public Lookup.Template getTemplate() {
            return template;
        }

        public Lkp getLookup() {
            return lookup;
        }
    } // end of LkpReferenceToResult

    /** Pointer to a listener.
     */
    static final class LkpReferenceToListener extends Object implements LkpChainItem {
        private AdaptableListener l;
        private Lkp lookup;
        private LkpChainItem next;

        LkpReferenceToListener(AdaptableListener l, Lkp lookup) {
            this.l = l;
            this.lookup = lookup;
        }

        public LkpResult getResult() {
            return null;
        }

        public AdaptableListener getListener() {
            return l;
        }

        public LkpChainItem getNext() {
            return next;
        }

        public void setNext(LkpChainItem next) {
            this.next = next;
        }

        public Lookup.Template getTemplate() {
            return null;
        }

        public Lkp getLookup() {
            return lookup;
        }
    } // end of LkpReferenceToListener
    
    
    /** Supporting class to iterate over linked list of ReferenceToResult
     * Use:
     * <PRE>
     *  ReferenceIterator it = new ReferenceIterator (this.ref);
     *  while (it.next ()) {
     *    it.current (): // do some work
     *  }
     *  this.ref = it.first (); // remember the first one
     */
    static final class ReferenceIterator extends Object {
        private LkpChainItem first;
        private LkpChainItem current;
        private LkpChainItem previous;
        /** hard reference to current result, so it is not GCed meanwhile */
        private Object currentResult;
        
        /** Initializes the iterator with first reference.
         */
        public ReferenceIterator (LkpChainItem first) {
            this.first = first;
        }
        
        /** Moves the current to next possition */
        public boolean next () {
            LkpChainItem prev;
            LkpChainItem ref;
            if (current == null) {
                ref = first;
                prev = null;
            } else {
                prev = current;
                ref = current.getNext();
            }
            this.previous = prev;
                
            while (ref != null) {
                Object result = ref.getResult();
                if (result == null) {
                    result = ref.getListener();
                }
                if (result == null) {
                    if (prev == null) {
                        // move the head
                        first = ref.getNext();
                    } else {
                        // skip over this reference
                        prev.setNext(ref.getNext());
                    }
                    prev = ref;
                    ref = ref.getNext();
                } else {
                    // we have found next item
                    currentResult = result;
                    current = ref;
                    return true;
                }
            }
            
            currentResult = null;
            current = null;
            return false;
        }

        /** Removes the current reference from the list.
         */
        public void remove() {
            if (previous == null) {
                first = current.getNext();
                current = null;
            } else {
                previous.setNext(current.getNext());
                current = previous;
            }
        }
        
        /** Access to current reference.
         */
        public LkpChainItem current () {
            return current;
        }
        
        /** Access to reference that is supposed to be the first one.
         */
        public LkpChainItem first () {
            return first;
        }

    } // end of ReferenceIterator
}
