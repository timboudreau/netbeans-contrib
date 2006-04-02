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
        /** Change listener associated with this adaptable object either SingletonizerListener or List<SingletonizerListener>*/
        private List<AdaptableListener> listener;

        private LkpReferenceToResult first;

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
            boolean callAdded = false;
            synchronized (this) {
                if (this.listener == null) {
                    this.listener = Collections.singletonList (l);
                    callAdded = true;
                } else {
                    if (this.listener instanceof java.util.ArrayList) {
                        this.listener.add (l);
                    } else {
                        java.util.ArrayList<AdaptableListener> arr = new java.util.ArrayList<AdaptableListener>();
                        arr.addAll (this.listener);
                        arr.add (l);
                        this.listener = arr;
                    }
                }
            }
            
            
            if (callAdded && ref.getImpl().initListener != null) {
                ref.getImpl().initListener.initialize (ref.getRepresentedObject());
            }
        }
        
        public void removeAdaptableListener (AdaptableListener l) {
            boolean callRemoved = true;
            synchronized (this) {
                if (this.listener instanceof java.util.ArrayList) {
                    List<AdaptableListener> arr = this.listener;
                    arr.remove (l);
                    if (arr.size () == 1) {
                        this.listener = Collections.singletonList (arr.get (0));
                    }
                } else {
                    if (this.listener != null && this.listener.contains (l)) {
                        this.listener = null;
                        callRemoved = true;
                    }
                }
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


            List<AdaptableListener> arr;
            synchronized (this) {
                arr = this.listener;
            }

            if (arr != null) {
                AdaptableEvent ev = Accessor.API.createEvent(this, af);
                for (AdaptableListener listener : arr) {
                    listener.stateChanged(ev);
                }
            }

            ReferenceIterator it = new ReferenceIterator (this.first);
            while (it.next ()) {
                it.current ().getResult ().check (af);
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

        public <T> Lookup.Result<T> lookup(Lookup.Template<T> template) {
            LkpResult<T> r = new LkpResult<T>();
            LkpReferenceToResult newRef = new LkpReferenceToResult (r, this, template);
            newRef.next = first;
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
            boolean enabled = reference.lookup.isEnabled (reference.template.getType ());
            return enabled ? Collections.nCopies (1, (T)reference.lookup.proxy) : Collections.<T>emptySet();
        }
        
        /** Notification from lookup that there were some changes.
         * @param changedClasses the set of <Class> objects that changed
         */
        final void check (java.util.Set changedClasses) {
            if (changedClasses.contains (reference.template.getType ())) {
                if (listener != null) {
                    listener.resultChanged (new org.openide.util.LookupEvent (this));
                }
            }
        }
    } // end of LkpResult
    
    /** Reference to a result  
     */
    static final class LkpReferenceToResult extends java.lang.ref.WeakReference<LkpResult>
    implements Runnable {
        /** next refernece in chain, modified only from AbstractLookup or this */
        private LkpReferenceToResult next;
        /** the template for the result */
        public final Lkp.Template template;
        /** the lookup we are attached to */
        public final Lkp lookup;
        /** caches for results */
        //public Object caches;
        
        /** Creates a weak refernece to a new result R in context of lookup
         * for given template
         */
        LkpReferenceToResult (LkpResult result, Lkp lookup, Lkp.Template template) {
            super (result, org.openide.util.Utilities.activeReferenceQueue ());
            this.template = template;
            this.lookup = lookup;
            getResult ().reference = this;
        }
        
        /** Returns the result or null
         */
        LkpResult getResult () {
            return get ();
        }
        
        /** Cleans the reference. Implements Runnable interface, do not call
         * directly.
         */
        public void run() {
            lookup.cleanUpResult (this.template);
        }
    } // end of LkpReferenceToResult
    
    
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
        private LkpReferenceToResult first;
        private LkpReferenceToResult current;
        /** hard reference to current result, so it is not GCed meanwhile */
        private LkpResult currentResult;
        
        /** Initializes the iterator with first reference.
         */
        public ReferenceIterator (LkpReferenceToResult first) {
            this.first = first;
        }
        
        /** Moves the current to next possition */
        public boolean next () {
            LkpReferenceToResult prev;
            LkpReferenceToResult ref;
            if (current == null) {
                ref = first;
                prev = null;
            } else {
                prev = current;
                ref = current.next;
            }
                
            while (ref != null) {
                LkpResult result = (LkpResult)ref.get ();
                if (result == null) {
                    if (prev == null) {
                        // move the head
                        first = ref.next;
                    } else {
                        // skip over this reference
                        prev.next = ref.next;
                    }
                    prev = ref;
                    ref = ref.next;
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
        
        /** Access to current reference.
         */
        public LkpReferenceToResult current () {
            return current;
        }
        
        /** Access to reference that is supposed to be the first one.
         */
        public LkpReferenceToResult first () {
            return first;
        }
    } // end of ReferenceIterator
}
