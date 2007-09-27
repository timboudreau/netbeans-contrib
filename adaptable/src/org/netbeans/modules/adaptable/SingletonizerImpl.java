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

import java.lang.ref.Reference;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;
import org.netbeans.spi.adaptable.Uninitializer;
import org.netbeans.spi.adaptable.Singletonizer;

/** A bunch of utility methods that support making functionality
 * "singletonized".There is a more instances, but only one object
 * that does the functionality (like represnted object vs. look).
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerImpl extends Object 
implements ProviderImpl, SingletonizerListener {
    private Class[] classes;
    /**
     * Keeps track of existing lookups. 
     */
    private java.util.Map<Object, Reference<AdaptableImpl>> lookups = new java.util.HashMap<Object,Reference<AdaptableImpl>> ();
    
    /** singletonizer we delegate to */
    private final Singletonizer single;
    /** fields for life cycle control */
    final Initializer initCall;
    final Initializer initListener;
    final Uninitializer noListener;
    final Uninitializer gc;
    
    /** We control the life cycle */
    private SingletonizerImpl (
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
    static Adaptor create (
        Class[] classes, 
        org.netbeans.spi.adaptable.Singletonizer impl,
        Initializer initCall,
        Initializer initListener,
        Uninitializer noListener,
        Uninitializer gc
    ) {
        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].isInterface()) {
                throw new IllegalArgumentException ("Works only on interfaces: " + classes[i].getName ()); // NOI18N
            }
        }
        SingletonizerImpl single = new SingletonizerImpl (classes, impl, initCall, initListener, noListener, gc);
        try {
            impl.addSingletonizerListener (single);
        } catch (java.util.TooManyListenersException ex) {
            throw new IllegalStateException ("addSingletonizerListener should not throw exception: " + impl); // NOI18N
        }
        return Accessor.API.createAspectProvider(single, impl);
    }
    
    public Singletonizer getSingletonizer () {
        return single;
    }
    
    //
    // Implementation of ProviderImpl
    //
   
    public Adaptable createLookup(Object obj, Adaptor adaptor) {
        java.lang.ref.Reference<AdaptableImpl> ref = lookups.get (obj);
        AdaptableImpl lkp = ref == null ? null : (AdaptableImpl)ref.get ();
        if (lkp == null) {
            lkp = new AdaptableImpl (obj, adaptor, classes);
            lookups.put (obj, lkp.ref);
        }
        return lkp;
    }
    
    final void removeObject (Object obj) {
        lookups.remove (obj);
    }
    
    public void stateChanged (SingletonizerEvent e) {
        Object affected = Accessor.SPI.getAffectedObject(e);
        Set<Class> types = Accessor.SPI.getAffectedClasses(e);

        if (affected == null) {
            // refresh all of them
            java.util.Iterator it = lookups.values ().iterator ();
            while (it.hasNext ()) {
                Reference ref = (Reference)it.next ();
                AdaptableImpl lkp = (AdaptableImpl)ref.get ();
                if (lkp != null) {
                    lkp.update (types);
                }
            }
        } else {
            Reference ref = (Reference)lookups.get(affected);
            if (ref == null) {
                return;
            }
            AdaptableImpl lkp = (AdaptableImpl)ref.get ();
            if (lkp != null) {
                lkp.update(types);
            }
        }
    }
    
    /** Method just for testing purposes. Returns objects, that
     * shall be excluding from assertSize computations as they are static
     */
    static Object excludeFromAssertSize () {
        return AdaptableRef.QUEUE;
    }

    static SingletonizerFactory defaultFactory() {
        return new SingletonizerFactory() {
            public Adaptor create(
                Class[] classes, Singletonizer impl,
                Initializer initCall, Initializer initListener,
                Uninitializer noListener, Uninitializer gc
            ) {
                return SingletonizerImpl.create(
                    classes, impl,
                    initCall, initListener,
                    noListener, gc
                );
            }
        };
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
        /** Change listener associated with this adaptable object either SingletonizerListener or List<SingletonizerListener>*/
        private List<AdaptableListener> listener;
        
        public AdaptableImpl (Object obj, Adaptor impl, Class[] classes) {
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
            
            List<AdaptableListener> arr = null;

            synchronized (this) {
                if (this.listener == null) {
                    return;
                }

                arr = this.listener;
            }


            AdaptableEvent ev = Accessor.API.createEvent(this, af);
            for (AdaptableListener listener : arr) {
                listener.stateChanged(ev);
            }
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
    } // end of AdaptableImpl

    /** A reference to adaptable that gets cleared when it is gone.
     */
    private static final class AdaptableRef extends java.lang.ref.WeakReference<AdaptableImpl> {
        /** queue we need to clear */
        private static final java.lang.ref.ReferenceQueue<AdaptableImpl> QUEUE = new java.lang.ref.ReferenceQueue<AdaptableImpl>();
        static {
            new AdaptableCleaner ();
        }
        /** we need to know the object we work with */
        private final Object obj;
        /** implementation we work with */
        private volatile Object impl;
        
        public AdaptableRef (AdaptableImpl adapt, Object obj, Adaptor impl) {
            super (adapt, QUEUE);
            this.obj = obj;
            this.impl = impl;
        }

        final Object getRepresentedObject() {
            return obj;
        }

        final SingletonizerImpl getImpl() {
            Object i = impl;
            if (i instanceof SingletonizerImpl) {
                return (SingletonizerImpl)i;
            } else {
                return (SingletonizerImpl)Accessor.API.getProviderImpl ((Adaptor)i);
            }
        }
        
        /** Marks this reference as already done first call.
         * @return true if the first call has not been done yet.
         */
        final boolean madeFirstCall () {
            Object i = impl;
            if (this.impl instanceof SingletonizerImpl) {
                return false;
            }
            this.impl = getImpl ();
            return true;
        }
        
        public static final void cleanUpQueue (AdaptableRef ref) {
            if (ref == null) {
                ref = (AdaptableRef)(Object)QUEUE.poll ();
            }
            for (;;) {
                if (ref == null) {
                    break;
                }
                if (ref.getImpl().gc != null) {
                    ref.getImpl().gc.uninitialize (ref.getRepresentedObject());
                }
                if (ref.getImpl().noListener != null) {
                    ref.getImpl().noListener.uninitialize (ref.getRepresentedObject());
                }
                ref.getImpl().removeObject(ref.getRepresentedObject());
                ref = (AdaptableRef)(Object)QUEUE.poll ();
            }
        }
    } // end of AdaptableRef
    
    
    /** A thread to cleanup the adaptable.
     */
    private static final class AdaptableCleaner extends Thread {
        public AdaptableCleaner () {
            super ("Adaptable References Cleaner");
            setDaemon(true);
            setPriority(MIN_PRIORITY);
            start ();
        }
        
        public void run () {
            AdaptableRef ref;
            for (;;) {
                // to allow GC
                ref = null;
                try {
                    ref = (AdaptableRef)(Object)AdaptableRef.QUEUE.remove ();
                    AdaptableRef.cleanUpQueue(ref);
                } catch (InterruptedException ex) {
                    // go on
                }
                
            }
        }
    } // end of AdaptableCleaner
}
