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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.javafx.source.JavaFXSource.Priority;

/**
 * Only encapsulates current request. May be trasformed into
 * JavaFXSource private static methods, but it may be less readable.
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */
public final class CurrentRequestReference {
    private final static SingleThreadFactory factory = new SingleThreadFactory ();
    static {
        Executors.newSingleThreadExecutor(factory).submit (new CompilationJob());
    }  

    private static Request DUMMY_RQ = new Request(new CancellableTask<CompilationInfo>() {

        public void cancel() {
        }
        {
        }

        public void run(CompilationInfo info) {
        }
    }, null, null, null, false);
    
    private Request reference;
    private Request canceledReference;
    private long cancelTime;
    private final AtomicBoolean canceled;
    private boolean mayCancelJavac;

    public CurrentRequestReference() {
        super();
        this.canceled = new AtomicBoolean();
    }

    public boolean setCurrentTask(Request reference) throws InterruptedException {
        boolean result = false;
        synchronized (CompilationJob.INTERNAL_LOCK) {
            while (this.canceledReference != null) {
                CompilationJob.INTERNAL_LOCK.wait();
            }
            result = this.canceled.getAndSet(false);
            this.mayCancelJavac = false;
            this.cancelTime = 0;
            this.reference = reference;
        }
        return result;
    }

    /**
     * Prevents race-condition in runWhenScanFinished. This method may be called only from
     * the Java-Source-Worker-Thread right after the initial scan finished. The problem was
     * that the task was added into the todo after the todo was drained into the list of pending
     * tasks but the getTaskToCancel thought that the task is still the RepositoryUpdater. So the
     * Java-Source-Worker-Thread has to clean the task after calling RU.run but before draining the
     * pending tasks into the array, it cannot use setCurrentTaks (null) since it is under javac lock
     * and the setCurrentTaks methods may block the caller thread => deadlock.
     */
    public void clearCurrentTask() {
        synchronized (CompilationJob.INTERNAL_LOCK) {
            this.reference = null;
        }
    }

    public Request getTaskToCancel(final Priority priority) {
        Request request = null;
        if (!factory.isDispatchThread(Thread.currentThread())) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                if (this.reference != null && priority.compareTo(this.reference.priority) < 0) {
                    assert this.canceledReference == null;
                    request = this.reference;
                    this.canceledReference = request;
                    this.reference = null;
                    this.canceled.set(true);
                    this.cancelTime = System.currentTimeMillis();
                }
            }
        }
        return request;
    }

    public Request getTaskToCancel(final boolean mayCancelJavac) {
        Request request = null;
        if (!factory.isDispatchThread(Thread.currentThread())) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                if (this.reference != null) {
                    assert this.canceledReference == null;
                    request = this.reference;
                    this.canceledReference = request;
                    this.reference = null;
                    this.canceled.set(true);
                    this.mayCancelJavac = mayCancelJavac;
                    this.cancelTime = System.currentTimeMillis();
                } else if (canceledReference == null) {
                    request = DUMMY_RQ;
                    this.canceledReference = request;
                    this.mayCancelJavac = mayCancelJavac;
                    this.cancelTime = System.currentTimeMillis();
                }
            }
        }
        return request;
    }

    public Request getTaskToCancel(final CancellableTask task) {
        Request request = null;
        if (!factory.isDispatchThread(Thread.currentThread())) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                if (this.reference != null && task == this.reference.task) {
                    assert this.canceledReference == null;
                    request = this.reference;
                    this.canceledReference = request;
                    this.reference = null;
                    this.canceled.set(true);
                }
            }
        }
        return request;
    }

    public Request getTaskToCancel() {
        Request request = null;
        if (!factory.isDispatchThread(Thread.currentThread())) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                request = this.reference;
                if (request != null) {
                    assert this.canceledReference == null;
                    this.canceledReference = request;
                    this.reference = null;
                    this.canceled.set(true);
                    this.cancelTime = System.currentTimeMillis();
                }
            }
        }
        return request;
    }

    public boolean getUserTaskToCancel(Request[] request) {
        assert request != null;
        assert request.length == 1;
        boolean result = false;
        if (!factory.isDispatchThread(Thread.currentThread())) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                request[0] = this.reference;
                if (request[0] != null) {
                    result = request[0].phase == null;
                    assert this.canceledReference == null;
                    if (!result) {
                        this.canceledReference = request[0];
                        this.reference = null;
                    }
                    this.canceled.set(result);
                    this.cancelTime = System.currentTimeMillis();
                }
            }
        }
        return result;
    }

    public boolean isCanceled() {
        synchronized (CompilationJob.INTERNAL_LOCK) {
            return this.canceled.get();
        }
    }

    AtomicBoolean getCanceledRef() {
        return this.canceled;
    }

    boolean isInterruptJavac() {
        synchronized (CompilationJob.INTERNAL_LOCK) {
            boolean ret = mayCancelJavac && 
                    canceledReference != null &&
                    canceledReference.source != null &&
                    (canceledReference.source.flags & JavaFXSource.INVALID) != 0;
            return ret;
        }
    }

    public long getCancelTime() {
        synchronized (CompilationJob.INTERNAL_LOCK) {
            return this.cancelTime;
        }
    }

    public void cancelCompleted(final Request request) {
        if (request != null) {
            synchronized (CompilationJob.INTERNAL_LOCK) {
                assert request == this.canceledReference;
                this.canceledReference = null;
                CompilationJob.INTERNAL_LOCK.notify();
            }
        }
    }
    
    private static class SingleThreadFactory implements ThreadFactory {

        private Thread t;

        public Thread newThread(Runnable r) {
            assert this.t == null;
            this.t = new Thread(r, "JavaFX Source Worker Thread");
            //NOI18N
            return this.t;
        }

        public boolean isDispatchThread(Thread t) {
            assert t != null;
            return this.t == t;
        }
    }

}
