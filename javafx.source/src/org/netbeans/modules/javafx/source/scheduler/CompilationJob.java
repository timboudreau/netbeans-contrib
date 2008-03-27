/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx.source.scheduler;

import org.netbeans.api.javafx.source.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.openide.util.Exceptions;
import org.netbeans.modules.javafx.source.scheduler.Request.RequestComparator;

/**
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */
public class CompilationJob implements Runnable {
    static final Logger LOGGER = Logger.getLogger(CompilationJob.class.getName());
    private final static List<DeferredTask> todo = Collections.synchronizedList(new LinkedList<DeferredTask>());    
//    //Only single thread can operate on the single javac
    private final static ReentrantLock javacLock = new ReentrantLock (true);
    public static final Object INTERNAL_LOCK = new Object();
    public final static PriorityBlockingQueue<Request> requests = new PriorityBlockingQueue<Request> (10, new RequestComparator());
    public final static Map<JavaFXSource,Collection<Request>> finishedRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    public final static Map<JavaFXSource,Collection<Request>> waitingRequests = new WeakHashMap<JavaFXSource,Collection<Request>>();
    public final static Collection<CancellableTask> toRemove = new LinkedList<CancellableTask> ();
    public final static CurrentRequestReference currentRequest = new CurrentRequestReference ();

    CompilationJob() {
        super();
    }

    public void run() {
        try {
            while (true) {
                try {
                    synchronized (INTERNAL_LOCK) {
                        if (!toRemove.isEmpty()) {
                            for (Iterator<Collection<Request>> it = finishedRequests.values().iterator(); it.hasNext();) {
                                Collection<Request> cr = it.next();
                                for (Iterator<Request> it2 = cr.iterator(); it2.hasNext();) {
                                    Request fr = it2.next();
                                    if (toRemove.remove(fr.task)) {
                                        it2.remove();
                                    }
                                }
                                if (cr.size() == 0) {
                                    it.remove();
                                }
                            }
                        }
                    }
                    Request r = requests.poll(2, TimeUnit.SECONDS);
                    if (r != null) {
                        currentRequest.setCurrentTask(r);
                        try {
                            JavaFXSource js = r.source;
                            if (js == null) {
                                assert r.phase == null;
                                assert r.reschedule == false;
                                javacLock.lock();
                                try {
                                    try {
                                        r.task.run(null);
                                    } finally {
                                        currentRequest.clearCurrentTask();
                                        boolean cancelled = requests.contains(r);
                                        if (!cancelled) {
                                            DeferredTask[] _todo;
                                            synchronized (todo) {
                                                _todo = todo.toArray(new DeferredTask[todo.size()]);
                                                todo.clear();
                                            }
                                            for (DeferredTask rq : _todo) {
                                                try {
                                                    js.runUserActionTask(rq.task, rq.shared);
                                                } finally {
                                                    rq.sync.taskFinished();
                                                }
                                            }
                                        }
                                    }
                                } catch (RuntimeException re) {
                                    Exceptions.printStackTrace(re);
                                } finally {
                                    javacLock.unlock();
                                }
                            } else {
                                assert js.files.size() <= 1;
                                boolean jsInvalid;
                                CompilationController ci;
                                synchronized (INTERNAL_LOCK) {
                                    //jl:what does this comment mean?
                                    //Not only the finishedRequests for the current request.JavaFXSource should be cleaned,
                                    //it will cause a starvation
                                    if (toRemove.remove(r.task)) {
                                        continue;
                                    }
                                    synchronized (js) {
                                        boolean changeExpected = (js.flags & JavaFXSource.CHANGE_EXPECTED) != 0;
                                        if (changeExpected) {
                                            Collection<Request> rc = waitingRequests.get(r.source);
                                            if (rc == null) {
                                                rc = new LinkedList<Request>();
                                                waitingRequests.put(r.source, rc);
                                            }
                                            rc.add(r);
                                            continue;
                                        }
                                        jsInvalid = js.currentInfo == null || (js.flags & JavaFXSource.INVALID) != 0;
                                        ci = js.currentInfo;
                                    }
                                }
                                try {
                                    //createCurrentInfo has to be out of synchronized block, it aquires an editor lock
                                    if (jsInvalid) {
                                        ci = JavaFXSource.createCurrentInfo(js, null);
                                        synchronized (js) {
                                            if (js.currentInfo == null || (js.flags & JavaFXSource.INVALID) != 0) {
                                                js.currentInfo = ci;
                                                js.flags &= ~JavaFXSource.INVALID;
                                            } else {
                                                ci = js.currentInfo;
                                            }
                                        }
                                    }
                                    assert ci != null;
                                    javacLock.lock();
                                    try {
                                        boolean shouldCall;
                                        try {
                                            final Phase phase = js.moveToPhase(r.phase, ci, true);
                                            shouldCall = phase.compareTo(r.phase) >= 0;
                                        } finally {
                                        }
                                        if (shouldCall) {
                                            synchronized (js) {
                                                shouldCall &= (js.flags & JavaFXSource.INVALID) == 0;
                                            }
                                            if (shouldCall) {
                                                try {
                                                    final long startTime = System.currentTimeMillis();
                                                    r.task.run(ci);
                                                    final long endTime = System.currentTimeMillis();
                                                    if (LOGGER.isLoggable(Level.FINEST)) {
                                                        LOGGER.finest(String.format("executed task: %s in %d ms.", r.task.getClass().toString(), endTime - startTime));
                                                    }
                                                } catch (Exception re) {
                                                    Exceptions.printStackTrace(re);
                                                }
                                            }
                                        }
                                    } finally {
                                        javacLock.unlock();
                                    }
                                    if (r.reschedule) {
                                        synchronized (INTERNAL_LOCK) {
                                            boolean canceled = currentRequest.setCurrentTask(null);
                                            synchronized (js) {
                                                if ((js.flags & JavaFXSource.INVALID) != 0 || canceled) {
                                                    requests.add(r);
                                                } else {
                                                    Collection<Request> rc = finishedRequests.get(r.source);
                                                    if (rc == null) {
                                                        rc = new LinkedList<Request>();
                                                        finishedRequests.put(r.source, rc);
                                                    }
                                                    rc.add(r);
                                                }
                                            }
                                        }
                                    }
                                } catch (IOException invalidFile) {
                                }
                            }
                        } finally {
                            currentRequest.setCurrentTask(null);
                        }
                    }
                } catch (Throwable e) {
                    if (e instanceof InterruptedException) {
                        throw (InterruptedException) e;
                    } else if (e instanceof ThreadDeath) {
                        throw (ThreadDeath) e;
                    } else {
                        Exceptions.printStackTrace(e);
                    }
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    static final class DeferredTask {
        final JavaFXSource js;
        final Task<CompilationController> task;
        final boolean shared;
        final ScanSync sync;
        
        public DeferredTask (final JavaFXSource js, final Task<CompilationController> task, final boolean shared, final ScanSync sync) {
            assert js != null;
            assert task != null;
            assert sync != null;
            
            this.js = js;
            this.task = task;
            this.shared = shared;
            this.sync = sync;
        }
    }
    static final class ScanSync implements Future<Void> {
        
        private Task<CompilationController> task;
        private final CountDownLatch sync;
        private final AtomicBoolean canceled;
        
        public ScanSync (final Task<CompilationController> task) {
            assert task != null;
            this.task = task;
            this.sync = new CountDownLatch (1);
            this.canceled = new AtomicBoolean (false);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.sync.getCount() == 0) {
                return false;
            }
            synchronized (todo) {
                boolean _canceled = canceled.getAndSet(true);
                if (!_canceled) {
                    for (Iterator<DeferredTask> it = todo.iterator(); it.hasNext();) {
                        DeferredTask dt = it.next();
                        if (dt.task == this.task) {
                            it.remove();
                            return true;
                        }
                    }
                }
            }            
            return false;
        }

        public boolean isCancelled() {
            return this.canceled.get();
        }

        public synchronized boolean isDone() {
            return this.sync.getCount() == 0;
        }

        public Void get() throws InterruptedException, ExecutionException {
            this.sync.await();
            return null;
        }

        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            this.sync.await(timeout, unit);
            return null;
        }
        
        private void taskFinished () {
            this.sync.countDown();
        }            
    }

}
