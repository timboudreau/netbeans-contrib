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

import java.util.Collection;
import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableListener;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.modules.adaptable.Accessor;
import org.netbeans.modules.adaptable.ProviderImpl;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;

/** A bunch of utility methods that support making functionality
 * "singletonized". There is a more instances, but only one object
 * that does the functionality (like represnted object vs. look).
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerLookupImpl extends Object 
implements SingletonizerListener, ProviderImpl {
    private Class[] classes;
    /** Keeps track of existing lookups. Type of Object to LkpReference<Lkp> */
    private java.util.HashMap<Object,LkpReference> lookups = new java.util.HashMap<Object,LkpReference>();
    
    
    /** We control the life cycle */
    SingletonizerLookupImpl (Class[] classes) {
        this.classes = classes;
    }
    
    /** Creates an AspectProvider based on that support sinletonization.
     * @param classes the interfaces that we support
     * @param impl provider of the functionality
     */
    public static Adaptor create (Class[] classes, Singletonizer impl) {
        return Adaptors.singletonizer(classes, impl);
    }
    
    //
    // Implementation of ProviderImpl
    //
   
    public Adaptable createLookup(Object obj, Adaptor adaptor) {
        LkpReference ref = (LkpReference)lookups.get (obj);
        Lkp lkp = ref == null ? null : (Lkp)ref.get ();
        if (lkp == null) {
            lkp = new Lkp (obj, (Singletonizer) Accessor.API.getData(adaptor), classes);
            ref = new LkpReference (obj, lkp);
            lookups.put (obj, ref);
        }
        return lkp;
    }
    
    public void stateChanged(SingletonizerEvent e) {
        Object affected = Accessor.SPI.getAffectedObject(e);
        if (affected == null) {
            // refresh all of them
            java.util.Iterator it = lookups.values ().iterator ();
            while (it.hasNext ()) {
                LkpReference ref = (LkpReference)it.next ();
                Lkp lkp = (Lkp)ref.get ();
                if (lkp != null) {
                    lkp.update ();
                }
            }
        } else {
            LkpReference ref = lookups.get (affected);
            if (ref == null) {
                return;
            }
            Lkp lkp = (Lkp)ref.get ();
            if (lkp != null) {
                lkp.update ();
            }
        }
    }


    //
    // The Lookup that points to a represented object
    //
    
    private static final class Lkp extends Lookup 
    implements java.lang.reflect.InvocationHandler, Adaptable {
        final Object obj;
        final Singletonizer impl;
        final Object proxy; 
        /** array of 0/1 for each class in impl.classes to identify the state 
         * whether it should be enabled or not */
        private byte[] enabled;
        private LkpReferenceToResult first;
        
        public Lkp (Object obj, Singletonizer impl, Class[] classes) {
            this.obj = obj;
            this.impl = impl;
            this.proxy = java.lang.reflect.Proxy.newProxyInstance(
                getClass ().getClassLoader (), 
                classes,
                this
            );
        }
        
        public <T> T lookup(Class<T> clazz) {
            if (impl.isEnabled(obj, clazz) && clazz.isInstance(proxy)) {
                return clazz.cast(proxy);
            }
            return null;
        }
        
        public <T> Lookup.Result<T> lookup(Lookup.Template<T> template) {
            LkpResult<T> r = new LkpResult<T>();
            LkpReferenceToResult newRef = new LkpReferenceToResult (r, this, template);
            newRef.next = first;
            first = newRef;
            return r;
        }
        
        /** Called when one wants an interface provided by this singletonizer
         */
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            if (isEnabled (method.getDeclaringClass ())) {
                return impl.invoke(obj, method, args);
            } else {
                throw new IllegalStateException ("Method " + method + " cannot be invoked when " + method.getDeclaringClass ());
            }
        }
        
        /** Checks whether a class is enabled or not */
        final boolean isEnabled (Class<?> clazz) {
            Class<?>[] allSupportedClasses = proxy.getClass().getInterfaces ();
            if (enabled == null) {
                enabled = new byte[(allSupportedClasses.length + 7) / 8];
                int offset = 1;
                int index = 0;
                for (int i = 0; i < allSupportedClasses.length; i++) {
                    if (impl.isEnabled (obj, allSupportedClasses[i])) {
                        enabled[index] |= offset;
                    }
                    if (offset == 128) {
                        index++;
                        offset = 1;
                    } else {
                        offset = offset << 1;
                    }
                }
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
        
        final void update () {
            if (enabled == null) {
                return;
            }
            
            java.util.HashSet<Class> changedClasses = new java.util.HashSet<Class>();
            Class<?>[] allSupportedClasses = proxy.getClass().getInterfaces ();
            int offset = 1;
            int index = 0;
            for (int i = 0; i < allSupportedClasses.length; i++) {
                boolean newValue = impl.isEnabled (obj, allSupportedClasses[i]);
                boolean oldValue = (enabled[index] & offset) != 0;
                
                if (newValue != oldValue) {
                    changedClasses.add (allSupportedClasses[i]);
                    if (newValue) {
                        enabled[index] |= offset;
                    } else {
                        enabled[index] &= ~offset;
                    }
                }
                
                if (offset == 128) {
                    offset = 1;
                    index++;
                } else {
                    offset = offset << 1;
                }
            }            
            
            ReferenceIterator it = new ReferenceIterator (this.first);
            while (it.next ()) {
                it.current ().getResult ().check (changedClasses);
             }
            this.first = it.first (); 
        }

        /** When a result is GCed, this clean ups the references to it
         * from the chain of results we have to maintain */
        final void cleanUpResult (Lookup.Template t) {
            ReferenceIterator it = new ReferenceIterator (this.first);
            while (it.next());
            this.first = it.first ();
        }

        public void addAdaptableListener(AdaptableListener l) {
        }

        public void removeAdaptableListener(AdaptableListener l) {
        }

    } // end of Lkp
    
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

    /** Reference to Lkps and to object the Lkp is assigned to with
     * appropriate equals and hash methods and search capability.
     */
    private final class LkpReference extends java.lang.ref.SoftReference<Lookup>
    implements Runnable {
        private Object key;
        
        public LkpReference (Object obj, Lkp lookup) {
            super (lookup, org.openide.util.Utilities.activeReferenceQueue ());
            this.key = obj;
        }
        
        public boolean equals (Object obj) {
            return key.equals (obj);
        }
        
        public int hashCode () {
            return key.hashCode ();
        }
        
        public void run () {
            if (lookups.get (key) == this) {
                lookups.remove (key);
                key = null;
            }
        }
        
    }
}
