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
 * 
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.objectloader;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.openide.util.Lookup;

/**
 * Type-safe loading of an object on a background thread.
 * 
 * Loads an object on a background thread, and passes it to a receiver 
 * object once it is loaded.
 *
 * @author Tim Boudreau
 */
public abstract class ObjectLoader<T> {
    private Reference<T> reference;
    private final Class<T> type;
    private States state = States.UNLOADED;
    private final CacheStrategy strategy;
    
    //PENDING:  Make thread limit settable?
    private static final ExecutorService POOL = 
            Executors.newFixedThreadPool(20);
    
    private Set <ObjectReceiver<T>> receivers = 
            new HashSet<ObjectReceiver<T>>();

    /**
     * Create a new ObjectLoader of the specified type.
     * 
     * @param type The type of the object that will be 
     * @param strategy The caching strategy that should determine how this
     * loader retains a reference to the loaded object
     */
    protected ObjectLoader (Class<T> type, CacheStrategy strategy) {
        this.type = type;
        this.strategy = strategy;
    }

    /**
     * Load the object, performing whatever long-running operation is
     * necessary.  Implementations should regularly check 
     * <code>Thread.interrupted()</code>, and if true, abort loading
     * and return null.
     * @return The object to be loaded
     * @throws java.io.IOException if an error occurs while loading - in that
     * case the state will be set to States.NOT_LOADABLE and the failed()
     * method will be called on all ObjectReceivers interested in the 
     * object that would have been loaded.
     */
    protected abstract T load() throws IOException;
    
    /**
     * Get the type of object this loader will load.
     * @return The type
     */
    public final Class<T> type() {
        return type;
    }
    
    /**
     * Get the currently cached instance of the object, if any.
     * 
     * @return The object, if loaded
     */
    public synchronized T getCachedInstance() {
        return reference == null ? null : reference.get();
    }
    
    /**
     * Synchrously fetch the object, loading it if necessary.  Do not call
     * this method on the event thread or it will throw an exception.
     * 
     * @return The object
     * @throws java.io.IOException
     */
    public T getSynchronous() throws IOException {
        T result = getCachedInstance();
        if (result == null) {
            if (EventQueue.isDispatchThread()) {
                throw new IllegalStateException("Cannot load on event thread");
            }
            setState (States.LOADING);
            boolean ok = false;
            try {
                result = load();
                ok = true;
                set(result);
            } finally {
                setState (ok ? States.LOADED : States.NOT_LOADABLE);
            }
            
        }
        return result;
    }
    
    /**
     * Clear the cached object and reset the state to States.UNLOADED.
     * Call this method if a previous attempt to load threw an exception and
     * you believe it is safe to attempt loading again for some reason.
     */
    public final synchronized void reset() {
        reference = null;
        setState(States.UNLOADED);
    }
    
    private synchronized void setState(States state) {
        this.state = state;
    }
    
    /**
     * Get the state of this ObjectLoader:  LOADED
     * @return
     */
    public final synchronized States getState() {
        if (state == States.LOADED && strategy != CacheStrategies.HARD) {
            T t = reference == null ? null : reference.get();
            if (t == null) {
                setState (States.UNLOADED);
            }
        }
        //XXX could use a timed reference to make the return value more
        //reliable in the case of States.LOADED + WeakReference
        return state;
    }
    
    /**
     * Get the object wrapped by this loader.  If the object is cached,
     * the receiver will be called synchronously with the value.  If not,
     * the object will be loaded and the receiver will be called on the
     * event thread with the result once it is loaded.
     * <p/>
     * If an attempt to load the object has already happened and failed,
     * the receiver's failed() method will be called immediately.
     * 
     * @param receiver
     */
    public final void get(ObjectReceiver<T> receiver) {
        T result = null;
        synchronized (this) {
            if (state == States.NOT_LOADABLE) {
                receiver.failed(new IOException("Previous load failed"));
                return;
            }
            if (reference != null) {
                result = reference.get();
                if (result == null) {
                    reference = null;
                }
            }
        }
        if (result != null) {
            receiver.setSynchronous(true);
            receiver.received(result);
        } else {
            receiver.setSynchronous(false);
            receivers.add (receiver);
            beginLoad ();
        }
    }
    
    /**
     * Cancel loading.  This method expresses that the passed ObjectReceiver
     * is no longer interested in the object being loaded.  Loading may 
     * continue if other loaders have been passed to the get() method and
     * are still interested in the object.  Otherwise an attempt to cancel
     * loading will be made.
     * 
     * @param receiver
     */
    public synchronized void cancel (ObjectReceiver<T> receiver) {
        receivers.remove(receiver);
        if (receivers.isEmpty()) {
            cancelLoad();
        }
    }
    
    private Future<T> future;
    private synchronized void beginLoad() {
//        if (future == null && !receivers.isEmpty()) {
            setState (States.LOADING);
            future = POOL.submit(new Loader());
    }
    
    private synchronized void cancelLoad() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
            future = null;
            setState(States.UNLOADED);
        }
    }

    private synchronized void set (T t) {
        future = null;
        assert t != null;
        setState (States.LOADED);
        reference = strategy.createReference(t);
    }
    
    
    /**
     * Notification method called after all ObjectReceivers have been passed
     * the object and the Strategy is caching the object. 
     * @param t The object loaded, or null in the event of failure
     */
    protected void postDelivery(T t) {
        
    }
    
    private final class Loader implements Callable<T> {
        List <ObjectReceiver<T>> toCall;
        T result;
        public T call() throws Exception {
            try {
                result = load();
                setState(States.LOADED);
                synchronized (ObjectLoader.this) {
                    toCall = new LinkedList <ObjectReceiver<T>>(receivers);
                    receivers.clear();
                    future = null;
                    if (result != null) {
                        set (result);
                    }
                }
                if (result != null) {
                    EventQueue.invokeLater(new Deliverer());
                }
                return result;
            } catch (Exception e) {
                setState(States.NOT_LOADABLE);
                synchronized (ObjectLoader.this) {
                    toCall = new LinkedList <ObjectReceiver<T>>(receivers);
                    receivers.clear();
                    future = null;
                }
                if (!toCall.isEmpty()) {
                    EventQueue.invokeLater(new Failer(e));
                }
                throw e;
            }
        }

        private final class Deliverer implements Runnable {
            public void run() {
                for (ObjectReceiver<T> receiver : toCall) {
//                    ClassLoader ldr = Lookup.getDefault().lookup(ClassLoader.class);
//                    ClassLoader current = Thread.currentThread().getContextClassLoader();
//                    try {
//                        Thread.currentThread().setContextClassLoader(ldr);
                        receiver.received(result);
//                    } finally {
//                        Thread.currentThread().setContextClassLoader(current);
//                    }
                }
                System.err.println("Invoking postDeleivery");
                postDelivery(result);
            }
        }
        
        private final class Failer implements Runnable {
            private final Exception ex;
            Failer (Exception ex) {
                this.ex = ex;
            }

            public void run() {
                for (ObjectReceiver<T> receiver : toCall) {
                    receiver.failed(ex);
                }
                postDelivery (null);
            }
        }
    }
}
