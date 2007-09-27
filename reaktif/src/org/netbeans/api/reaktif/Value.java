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

package org.netbeans.api.reaktif;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

// XXX public method to recalculate w/ all upstream from scratch? or discard caches?

/**
 * Reactive value cell with caching and change propagation abilities.
 * A cell may be a "leaf", polling or idiosyncratically listening to some
 * legacy data source and refiring changes. Or it may be a computation
 * based on the value of other cells. A directed acyclic graph of value
 * objects may be built up; changes in leaves are propagated through the
 * graph back to the root(s), stopping when a computation yields the same
 * value as it previously had. Interrogating a cell normally uses the
 * cached value. Exceptions thrown during a computation are trapped and
 * cached just like a regular return value. "Upstream" cells (e.g. leaves)
 * do not hold strong references to "downstream" cells (e.g. roots); downstream
 * cells hold strong references to those upstream cells whose value was last
 * used in their computation.
 * @param T the type of value computed or produced
 * @param X a checked exception type thrown by the computation; use {@link Error} for none
 * @author Jesse Glick
 */
public abstract class Value<T,X extends Throwable> {
    private static final Object UNKNOWN = new String("Value.UNKNOWN");
    private static class Exc {
        final Exception exception;
        Exc(Exception x) {
            exception = x;
        }
        @Override
        public String toString() {
            return exception.toString();
        }
    }
    private Object/*T|UNKNOWN|Exc*/ cache = UNKNOWN;
    private List<ValueListener> listeners;
    private List<Reference<Value<?,?>>> downstream;
    private final TrackingReference ref = new TrackingReference(this);
    private static final Map<Object,Set<Value<?,?>>> calculating;
    static {
        boolean asserts = false;
        assert asserts = true;
        if (asserts) {
            calculating = new WeakHashMap<Object,Set<Value<?,?>>>();
        } else {
            calculating = null;
        }
    }
    protected Value() {}
    /**
     * Called when necessary to calculate the value of this cell.
     * The implementation should do no caching as this is handled by the infrastructure.
     * Leaf cells will normally just retrieve a value from some foreign data model.
     * Computation cells should ensure that all variable inputs to the computation
     * are retrieved from other cells by calling {@link #resolve}.
     */
    protected abstract T calculate() throws X;
    /**
     * Called when a cell gets a direct listener or a downstream cell depending on it.
     * Typically leaf cells should override this to attach listeners to some data model.
     * Call {@link #recalculate} when that data model changes.
     * The default implementation does nothing so direct subclasses need not call the super
     * method when overriding.
     */
    protected void addNotify() {}
    /**
     * Called when the last direct listener or downstream cell is removed from this cell.
     * Cells which added a foreign listener in {@link #addNotify} should now remove it.
     * The default implementation does nothing so direct subclasses need not call the super
     * method when overriding.
     */
    protected void removeNotify() {}
    /**
     * A Java monitor to use for all operations on this cell.
     * All cells by default use the same lock but different "forests" of cells
     * may wish to all use a different lock in order to avoid contention.
     * It is illegal for a cell to depend on a cell with a different lock.
     * This method is called just once per instance, from the superclass constructor,
     * so fields initialized in a subclass constructor will not yet be available.
     */
    protected Object lock() {
        return UNKNOWN;
    }
    /**
     * Recalculate the value of this cell.
     * Normally would be called only in response to changing external data.
     * If this cell's value changes (according to {link Object#equals}) as a result,
     * any cells whose last computation depended on this value will also be recalculated,
     * and so on transitively. Change events will be fired from all modified cells
     * (in an unspecified order), after all new values have been calculated
     * but before returning from this method, without holding the lock.
     * You may not call this method from inside the {@link #calculate} method of this
     * cell, or any other cell using the same lock.
     */
    protected final void recalculate() {
        assert calculating == null || !calculating.containsKey(ref.lock) || calculating.get(ref.lock).isEmpty();
        Map<ValueEvent,ValueListener[]> changes = new LinkedHashMap<ValueEvent,ValueListener[]>();
        synchronized (ref.lock) {
            Collection<Value<?,?>> queue = new LinkedHashSet<Value<?,?>>();
            Set<Value<?,?>> processed = new HashSet<Value<?,?>>();
            queue.add(this);
            while (true) {
                Iterator<Value<?,?>> poll = queue.iterator();
                if (!poll.hasNext()) {
                    break;
                }
                Value<?,?> other = poll.next();
                poll.remove();
                if (!processed.add(other)) {
                    continue;
                }
                assert ref.lock == other.ref.lock;
                Object old = other.cache;
                boolean enqueue;
                if (old == UNKNOWN) {
                    enqueue = true;
                } else {
                    other.runCalculate();
                    if (other.cache == null ? old != null : !other.cache.equals(old)) {
                        if (other.listeners != null) {
                            changes.put(new ValueEvent(other), other.listeners.toArray(new ValueListener[other.listeners.size()]));
                        }
                        enqueue = true;
                    } else {
                        enqueue = false;
                    }
                }
                if (enqueue && other.downstream != null) {
                    Iterator<Reference<Value<?,?>>> it = other.downstream.iterator();
                    while (it.hasNext()) {
                        Value<?,?> next = it.next().get();
                        if (next != null) {
                            queue.add(next);
                        } else {
                            it.remove();
                            other.maybeRemoveNotify();
                        }
                    }
                }
            }
        }
        for (Map.Entry<ValueEvent,ValueListener[]> entry : changes.entrySet()) {
            for (ValueListener l : entry.getValue()) {
                try {
                    l.valueChanged(entry.getKey());
                } catch (Exception x) {
                    Exceptions.printStackTrace(x);
                }
            }
        }
    }
    private void maybeRemoveNotify() {
        boolean wasLive = downstream != null || listeners != null;
        if (downstream != null && downstream.isEmpty()) {
            downstream = null;
        }
        if (listeners != null && listeners.isEmpty()) {
            listeners = null;
        }
        if (wasLive && downstream == null && listeners == null) {
            removeNotify();
        }
    }
    /**
     * Obtain the value of another cell, and remember the dependency on it.
     * May only be called within the {@link #calculate} method of this cell.
     */
    protected final <U,Y extends Throwable> U resolve(Value<U,Y> other) throws Y {
        assert calculating == null || (calculating.containsKey(ref.lock) && calculating.get(ref.lock).contains(this) && !calculating.get(ref.lock).contains(other));
        assert ref.lock == other.ref.lock;
        assert Thread.holdsLock(ref.lock);
        if (ref.upstream != null) {
            ref.upstream.add(other);
        }
        return other.getNoLock();
    }
    private void runCalculate() {
        assert Thread.holdsLock(ref.lock);
        if (ref.upstream == null) {
            ref.upstream = new HashSet<Value<?,?>>();
        } else {
            for (Value<?,?> other : ref.upstream) {
                if (other.downstream != null) {
                    Iterator<Reference<Value<?,?>>> it = other.downstream.iterator();
                    while (it.hasNext()) {
                        Value<?,?> next = it.next().get();
                        if (next == null || next == this) {
                            it.remove();
                            other.maybeRemoveNotify();
                        }
                    }
                }
            }
            ref.upstream.clear();
        }
        if (calculating != null) {
            Set<Value<?,?>> s = calculating.get(ref.lock);
            if (s == null) {
                s = new HashSet<Value<?,?>>();
                calculating.put(ref.lock, s);
            }
            assert s.add(this);
        }
        try {
            cache = calculate();
        } catch (Throwable t) {
            if (t instanceof Error) {
                throw (Error) t;
            } else {
                cache = new Exc((Exception) t);
            }
        } finally {
            if (calculating != null) {
                assert calculating.get(ref.lock).remove(this);
            }
        }
        for (Value<?,?> other : ref.upstream) {
            if (other.downstream == null) {
                other.downstream = new LinkedList<Reference<Value<?,?>>>();
                if (other.listeners == null) {
                    other.addNotify();
                }
            }
            other.downstream.add(ref);
        }
        assert cache != UNKNOWN;
    }
    /**
     * Obtain the (possibly cached) value of this cell.
     * Any result is cached for future use.
     */
    public final T get() throws X {
        synchronized (ref.lock) {
            return getNoLock();
        }
    }
    @SuppressWarnings("unchecked")
    private T getCache() throws X {
        assert cache != UNKNOWN;
        if (cache instanceof Exc) {
            throw (X) ((Exc) cache).exception;
        } else {
            return (T) cache;
        }
    }
    private T getNoLock() throws X {
        assert Thread.holdsLock(ref.lock);
        if (cache == UNKNOWN) {
            runCalculate();
        }
        return getCache();
    }
    /**
     * Obtain the (possibly cached) value of this cell.
     * Any newly computed result is not cached.
     */
    public T getUncached() throws X {
        synchronized (ref.lock) {
            if (cache == UNKNOWN) {
                ref.upstream = null;
                if (calculating != null) {
                    Set<Value<?,?>> s = calculating.get(ref.lock);
                    if (s == null) {
                        s = new HashSet<Value<?,?>>();
                        calculating.put(ref.lock, s);
                    }
                    assert s.add(this);
                }
                try {
                    return calculate();
                } finally {
                    if (calculating != null) {
                        assert calculating.get(ref.lock).remove(this);
                    }
                }
            } else {
                return getCache();
            }
        }
    }
    /**
     * Listen to changes in the value of this cell.
     */
    public final void addValueListener(ValueListener l) {
        synchronized (ref.lock) {
            if (listeners == null) {
                listeners = new LinkedList<ValueListener>();
                if (downstream == null) {
                    addNotify();
                }
            }
            listeners.add(l);
        }
    }
    /**
     * Stop listening to changes in the value of this cell.
     */
    public final void removeValueListener(ValueListener l) {
        synchronized (ref.lock) {
            if (listeners != null) {
                listeners.remove(l);
                maybeRemoveNotify();
            }
        }
    }
    /**
     * Check for object identity.
     */
    @Override
    public final boolean equals(Object o) {
        return this == o;
    }
    /**
     * Object identity hash code.
     */
    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }
    /**
     * By default, shows cached value.
     */
    @Override
    public String toString() {
        return String.valueOf(cache);
    }
    private static class TrackingReference extends WeakReference<Value<?,?>> implements Runnable {
        Set<Value<?,?>> upstream;
        final Object lock;
        TrackingReference(Value<?,?> referent) {
            super(referent, Utilities.activeReferenceQueue());
            this.lock = referent.lock();
        }
        public void run() {
            synchronized (lock) {
                if (upstream != null) {
                    for (Value<?,?> other : upstream) {
                        if (other.downstream != null) {
                            Iterator<Reference<Value<?,?>>> it = other.downstream.iterator();
                            while (it.hasNext()) {
                                Value<?,?> next = it.next().get();
                                if (next == null) {
                                    it.remove();
                                    other.maybeRemoveNotify();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
