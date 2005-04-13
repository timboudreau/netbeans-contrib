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

package org.netbeans.modules.adaptable;

import java.lang.ref.Reference;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.Deinitializer;
import org.netbeans.spi.adaptable.Singletonizer;

/** A bunch of utility methods that support making functionality
 * "singletonized". There is a more instances, but only one object
 * that does the functionality (like represnted object vs. look).
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerImpl extends Object 
implements ProviderImpl, javax.swing.event.ChangeListener {
    private Class[] classes;
    /**
     * Keeps track of existing lookups. 
     */
    private java.util.Map<Object, Reference<AdaptableImpl>> lookups = new java.util.WeakHashMap<Object,Reference<AdaptableImpl>> ();
    
    /** singletonizer we delegate to */
    private final Singletonizer single;
    /** fields for life cycle control */
    final Initializer initCall;
    final Initializer initListener;
    final Deinitializer noListener;
    final Deinitializer gc;
    
    /** We control the life cycle */
    private SingletonizerImpl (
        Class[] classes,
        Singletonizer single,
        Initializer initCall,
        Initializer initListener,
        Deinitializer noListener,
        Deinitializer gc
    ) {
        this.classes = classes;
        this.single = single;
        this.initCall = initCall;
        this.initListener = initListener;
        this.noListener = noListener;
        this.gc = gc;
    }
    
    /**
     * Creates an Adaptor based on that support sinletonization.
     * @param classes the interfaces that we support
     * @param impl provider of the functionality
     * @param initCall initializer (or null) to be notified when a first call
     *    is made to an object's adaptable method
     * @param initListener initializer (or null) to be notified when a first
     *    listener is added to the Adaptable 
     * @param noListener deinitilizer (or null) that is supposed to be called
     *    when the last listener is removed from an adaptable
     * @param gc deinitilizer (or null) to be notified when an Adaptable is GCed and
     *    no longer in use 
     */
    public static Adaptor create (
        Class[] classes, 
        org.netbeans.spi.adaptable.Singletonizer impl,
        Initializer initCall,
        Initializer initListener,
        Deinitializer noListener,
        Deinitializer gc
    ) {
        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].isInterface()) {
                throw new IllegalArgumentException ("Works only on interfaces: " + classes[i].getName ()); // NOI18N
            }
        }
        SingletonizerImpl single = new SingletonizerImpl (classes, impl, initCall, initListener, noListener, gc);
        try {
            impl.addChangeListener (single);
        } catch (java.util.TooManyListenersException ex) {
            throw new IllegalStateException ("addChangeListener should not throw exception: " + impl); // NOI18N
        }
        return Accessor.API.createAspectProvider(single, impl);
    }
    
    public Singletonizer getSingletonizer () {
        return single;
    }
    
    //
    // Implementation of ProviderImpl
    //
   
    public Adaptable createLookup(Object obj, Object data) {
        java.lang.ref.Reference<AdaptableImpl> ref = lookups.get (obj);
        AdaptableImpl lkp = ref == null ? null : (AdaptableImpl)ref.get ();
        if (lkp == null) {
            lkp = new AdaptableImpl (obj, this, classes);
            lookups.put (obj, lkp.ref);
        }
        return lkp;
    }
    
    public void stateChanged (javax.swing.event.ChangeEvent e) {
        if (e.getSource () instanceof org.netbeans.spi.adaptable.Singletonizer) {
            // refresh all of them
            java.util.Iterator it = lookups.values ().iterator ();
            while (it.hasNext ()) {
                Reference ref = (Reference)it.next ();
                AdaptableImpl lkp = (AdaptableImpl)ref.get ();
                if (lkp != null) {
                    lkp.update ();
                }
            }
        } else {
            Reference ref = (Reference)lookups.get (e.getSource ());
            if (ref == null) {
                return;
            }
            AdaptableImpl lkp = (AdaptableImpl)ref.get ();
            if (lkp != null) {
                lkp.update ();
            }
        }
    }
    
    //
    // The Lookup that points to a represented object
    //
    
    private static final class AdaptableImpl 
    implements Adaptable, java.lang.reflect.InvocationHandler {
        final AdaptableRef ref;
        final Object proxy; 
        /** array of 0/1 for each class in impl.classes to identify the state 
         * whether it should be enabled or not */
        private byte[] enabled;
        /** Change listener associated with this adaptable object either ChangeListener or List<ChangeListener>*/
        private List<ChangeListener> listener;
        /** first call has been made */
        private boolean firstCallDone;
        
        public AdaptableImpl (Object obj, SingletonizerImpl impl, Class[] classes) {
            this.proxy = java.lang.reflect.Proxy.newProxyInstance(
                getClass ().getClassLoader (), 
                classes,
                this
            );
            this.ref = new AdaptableRef (this, obj, impl);
        }
        
        public <T> T lookup(Class<T> clazz) {
            if (isEnabled (clazz)) {
                return clazz.cast (proxy);
            }
            return null;
        }
        
        public synchronized void addChangeListener (ChangeListener l) {
            boolean callAdded = false;
            synchronized (this) {
                if (this.listener == null) {
                    this.listener = Collections.singletonList (l);
                    callAdded = true;
                } else {
                    if (this.listener instanceof java.util.ArrayList) {
                        this.listener.add (l);
                    } else {
                        java.util.ArrayList<ChangeListener> arr = new java.util.ArrayList<ChangeListener> ();
                        arr.addAll (this.listener);
                        arr.add (l);
                        this.listener = arr;
                    }
                }
            }
            
            
            if (callAdded && ref.impl.initListener != null) {
                ref.impl.initListener.initialize (ref.obj);
            }
        }
        
        public void removeChangeListener (ChangeListener l) {
            boolean callRemoved = true;
            synchronized (this) {
                if (this.listener instanceof java.util.ArrayList) {
                    List<ChangeListener> arr = this.listener;
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
            
            if (callRemoved && ref.impl.noListener != null) {
                ref.impl.noListener.deinitialize (ref.obj);
            }
        }
        
        /** Called when one wants an interface provided by this singletonizer
         */
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            if (isEnabled (method.getDeclaringClass ())) {
                if (!firstCallDone) {
                    firstCallDone = true;
                    if (ref.impl.initCall != null) {
                        ref.impl.initCall.initialize (ref.obj);
                    }
                }
                
                return ref.impl.getSingletonizer ().invoke(ref.obj, method, args);
            } else {
                throw new IllegalStateException ("Method " + method + " cannot be invoked when " + method.getDeclaringClass ());
            }
        }
        
        /** Updates its state. */
        final void update () {
            enabled = null;
            
            List<ChangeListener> arr = null;
            
            synchronized (this) {
                if (this.listener == null) {
                    return;
                }

                arr = this.listener;
            }
            
            
            for (ChangeListener listener : arr) {
                listener.stateChanged (new ChangeEvent (this)); 
            }
        }
        
        /** Checks whether a class is enabled or not */
        final boolean isEnabled (Class<?> clazz) {
            Class[] allSupportedClasses = proxy.getClass().getInterfaces ();
            if (enabled == null) {
                enabled = new byte[(allSupportedClasses.length + 7) / 8];
                int offset = 1;
                int index = 0;
                for (int i = 0; i < allSupportedClasses.length; i++) {
                    if (ref.impl.getSingletonizer ().isEnabled (allSupportedClasses[i])) {
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
    } // end of AdaptableImpl

    /** A reference to adaptable that gets cleared when it is gone.
     */
    private static final class AdaptableRef extends java.lang.ref.WeakReference<AdaptableImpl> {
        /** queue we need to clear */
        private static final java.lang.ref.ReferenceQueue<AdaptableImpl> QUEUE = new java.lang.ref.ReferenceQueue<AdaptableImpl> ();
        /** we need to know the object we work with */
        final Object obj;
        /** implementation we work with */
        final SingletonizerImpl impl;
        
        public AdaptableRef (AdaptableImpl adapt, Object obj, SingletonizerImpl impl) {
            super (adapt, QUEUE);
            this.obj = obj;
            this.impl = impl;
        }
        
        
        public static final void cleanUpQueue () {
            for (;;) {
                Reference<? extends AdaptableImpl> x = QUEUE.poll ();
                if (x == null) {
                    break;
                }
                AdaptableRef ref = (AdaptableRef)x;
                
                if (ref.impl.gc != null) {
                    ref.impl.gc.deinitialize (ref.obj);
                }
            }
        }
    }
}
