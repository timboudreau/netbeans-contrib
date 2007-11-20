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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package threadmanagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>This class manages multiple <code>Thread</code> instances, permitting one
 * thread at a time to continue its normal execution while the others wait.
 * In web applications, an instance of this class can be held in session scope and 
 * used to permit only one thread associated with the session to execute at at time.
 * In web applications (particularly JavaServer Faces applications) that implement <code>init</code> and
 * <code>destroy</code> (or equivalent) lifecycle methods in request scope, this class can be used like so:</p>
<pre>public void init() {
    //...normal init logic goes here...
    ThreadSynchronizer synchronizer = getSessionBean1().getSynchronizer();
    ThreadPriority currentThreadPriority = ... ; //determine current thread priority
    this.currentThreadSynchronized = synchronizer.synchronizeCurrentThread(currentThreadPriority);
}

public void destroy() {
    if (this.currentThreadSynchronized) {
        ThreadSynchronizer synchronizer = getSessionBean1().getSynchronizer();
        synchronizer.releaseCurrentThread();
    }
}
</pre>
 * <p>The order in which threads are permitted to continue
 * normal execution is based on their priority level, and, in the case of threads bearing a priority
 * level for which the <code>serviceInOrderReceived</code> property is <code>true</code>,
 * on the order in which such threads are received.</p>
 *
 * <p>Internally, this class maintains a <code>java.util.List</code> instance
 * for each of its known priority levels. Invoking the <code>synchronizeCurrentThread</code>
 * method either permits the current thread to continue normal execution or places it in
 * the appropriate <code>java.util.List</code> instance and makes it wait. 
 * Invoking the <code>releaseCurrentThread</code>
 * method notifies any waiting threads that the current thread has completed 
 * its normal execution, thereby causing the waiting threads to test whether they are next to 
 * be permitted to continue executing normally.</p>
 * 
 * @author mbohm
 */
public class ThreadSynchronizer {
    
    /**
     * <p>An array of <code>ThreadPriority</code> instances representing
     * this object's known priority levels.</p>
     */
    private ThreadPriority[] priorities;
    
    /**
     * <p>An array of <code>java.util.List</code> instances, where each instance 
     * represents a "bucket" in which threads of a particular priority level wait.
     * Because one bucket is established for each known priority level, 
     * this array has the same length as <code>this.priorities</code>.</p>
     */
    private List[] bucketArr;
    
    /**
     * <p>The thread currently completing its normal execution. Any other threads
     * associated with the session wait until its execution is complete.</p>
     */
    private Thread threadInProgress;
    
    /**
     * <p>The maximum time in milliseconds a thread may wait. If a thread's wait time exceeds this interval, 
     * <code>synchronizeCurrentThread</code> returns <code>false</code> and the thread continues its normal execution.
     * In such a case, that thread is not assigned to <code>this.threadInProgress</code>.</p>
     */
    private long waitTimeout;
    
    /**
     * <p>Whether we are debugging this class.</p>
     */
    private boolean debug;
    
    /**
     * <p>A counter that lets us number the threads for debugging purposes.</p>
     */
    private long debugThreadCounter;
    
    /**
     * <p>A <code>java.util.Map</code> whose keys are <code>Thread</code>
     * instances and whose values are <code>Long</code> instances indicating
     * the debug number assigned to the thread.</p>
     */
    private Map debugThreadMap;

    /**
     * <p>Construct a <code>ThreadSynchronizer</code> instance.
     * We assign the <code>priorities</code>, <code>waitTimeout</code>, and
     * <code>debug</code> parameters to their corresponding member variables.
     * If <code>debug</code> is <code>true</code>, we initialize
     * <code>this.debugThreadMap</code>. We also initialize <code>this.bucketArr</code>
     * with the same length as <code>priorities</code>, and initialize
     * each of the members of <code>this.bucketArr</code> by instantiating a <code>java.util.LinkedList</code>.</p>
     * @param priorities An array of <code>ThreadPriority</code> instances representing
     * this object's known priority levels.
     * @param waitTimeout The maximum time in milliseconds a thread may wait.
     * @param debug Whether we are debugging this class.
     * @throws <code>NullPointerException</code> if <code>priorities</code> is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if <code>priorities</code> is zero-length.
     * @throws <code>IllegalArgumentException</code> if <code>priorities</code> contains duplicate instances of <code>ThreadPriority</code>.
     */
    public ThreadSynchronizer(ThreadPriority[] priorities, long waitTimeout, boolean debug) {
        if (priorities == null) {
            throw new NullPointerException();
        }
        if (priorities.length == 0) {
            throw new IllegalArgumentException("priorities was length 0");
        }
        //prevent duplicate ThreadPriority instances
        for (int i = 0; i < priorities.length - 1; i++) {   //don't test the last one
            for (int j = i + 1; j < priorities.length; j++) {
                if (priorities[i] == priorities[j]) {
                    throw new IllegalArgumentException("priorities[" + i + "] and  [" + j + "] were duplicates");
                }
            }
        }
        this.priorities = priorities;
        this.waitTimeout = waitTimeout;
        this.debug = debug;
        if (this.debug) {
            this.debugThreadMap = new HashMap();
        }
        this.bucketArr = new List[this.priorities.length];
        for (int i = 0; i < this.bucketArr.length; i++) {
            this.bucketArr[i] = new LinkedList();
        }
    }
    
    /**
     * <p>Construct a <code>ThreadSynchronizer</code> instance by 
     * invoking <code>this(priorities, waitTimeout, false)</code>, thus defaulting
     * <code>debug</code> to <code>false</code>.</p>
     * @param priorities An array of <code>ThreadPriority</code> instances representing
     * this object's known priority levels.
     * @param waitTimeout The maximum time in milliseconds a thread may wait.
     * @throws <code>NullPointerException</code> if <code>priorities</code> is <code>null</code>.
     * @throws <code>IllegalArgumentException</code> if <code>priorities</code> is zero-length.
     * @throws <code>IllegalArgumentException</code> if <code>priorities</code> contains duplicate instances of <code>ThreadPriority</code>.
     */
    public ThreadSynchronizer(ThreadPriority[] priorities, long waitTimeout) {
        this(priorities, waitTimeout, false);
    }
    
    /**
     * <p>Get an array of <code>ThreadPriority</code> instances representing
     * this object's known priority levels.</p>
     * @return This object's known priority levels.
     */
    public ThreadPriority[] getPriorities() {
        return this.priorities;
    }
    
    /**
     * <p>Notify any waiting threads that the current thread has completed its 
     * normal execution. This method should only (and must) be invoked after
     * previously invoking <code>synchronizeCurrentThread</code> and receiving a return
     * value of <code>true</code>. We set <code>this.threadInProgress</code> to
     * <code>null</code> and call <code>notify</code> on <code>this.bucketArr</code>.
     * The latter causes any waiting threads to test whether they are next to 
     * be permitted to continue executing normally.</p> 
     */
    public void releaseCurrentThread() {
        synchronized (bucketArr) {
            this.threadInProgress = null;
            if (this.debug) {
                log("Thread " + getDebugCurrentThreadNumber() + " is now released");
            }
            this.bucketArr.notify();
        }
    }
    
    /**
     * <p>Either assign the current thread to <code>this.threadInProgress</code> and
     * let it continue its normal execution or
     * add it to the appropriate "bucket" and make it wait. In the latter case,
     * if the current thread finishes waiting its turn without exceeding the 
     * <code>this.waitTimeout</code> interval, assign the current thread to <code>this.threadInProgress</code> and
     * let it continue its normal execution.</p>
     * @param priority The priority level of the current thread.
     * @throws <code>IllegalArgumentException</code> if the supplied priority is unknown to this <code>ThreadSynchronizer</code> instance.
     * @return <code>true</code> if the current thread has been assigned to <code>this.threadInProgress</code> and is thus
     * considered the thread of record completing its normal execution, <code>false</code> otherwise.
     */
    public boolean synchronizeCurrentThread(ThreadPriority priority) {
        synchronized (bucketArr) {
            try {
                Thread currentThread = Thread.currentThread();
                assignDebugCurrentThreadNumber();
                int ourPriority = getPriorityIndex(priority);
                if (canCurrentThreadProceed(ourPriority, false)) {
                    if (this.debug) {
                        String priorityMessage = priority.getName() == null ? (" with priorityIndex " + ourPriority) : (" with priority '" + priority.getName() + "'");
                        log("Thread " + getDebugCurrentThreadNumber() + priorityMessage + " is new and is proceeding");
                    }
                    this.threadInProgress = currentThread;
                    return true;
                }
                else {
                    return queueCurrentThreadAndWait(priority);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    
    /**
     * <p>Determine whether the current thread can continue its normal execution
     * now. If it cannot, then it must wait. If <code>this.threadInProgress</code>
     * is not <code>null</code>, then another thread is the thread of record completing
     * its normal execution, in which case return <code>false</code>. Otherwise,
     * loop through the buckets in <code>this.bucketArr</code>, proceeding from the bucket for
     * threads of the highest priority to the bucket for threads of the lowest priority.
     * If we encounter a non-empty bucket that is for threads of a higher 
     * priority than the current thread, then that means there are threads waiting of a higher
     * priority than the current thread, in which case
     * return <code>false</code>. If we encounter the bucket for threads of the 
     * same priority as the current thread, return <code>true</code> if 
     * one of the following is true (otherwise return <code>false</code>):</p>
     * <ul>
     * <li>the bucket for threads of the 
     * same priority as the current thread is empty (in which case there are no other threads 
     * waiting with the same priority
     * as the current thread)</li>
     * <li>this thread has already
     * been queued (and thus is in the bucket in question) and
     * the <code>serviceInOrderReceived</code> property of that bucket's
     * corresponding <code>ThreadPriority</code> is <code>false</code></li>
     * <li>this thread has already
     * been queued (and thus is in the bucket in question) and
     * the <code>serviceInOrderReceived</code> property of that bucket's
     * corresponding <code>ThreadPriority</code> is <code>true</code> <strong>and</strong> 
     * the current thread is the thread in that bucket that has been waiting the longest.</li>
     * </ul>
     * @param ourPriority The zero-based index of the current thread's priority level.
     * @param alreadyQueued <code>true</code> if this method is being invoked from <code>queueCurrentThreadAndWait</code>, 
     * <code>false</code> if it is being invoked from <code>synchronizeCurrentThread</code>.
     * @return Whether the current thread can continue its normal execution
     * now.
     */
    private boolean canCurrentThreadProceed(int ourPriority, boolean alreadyQueued) {
        synchronized (this.bucketArr) {
            if (this.threadInProgress != null) {
                return false;
            }
            //examine each bucket
            //go from "highest" priority to "lowest"
            for (int i = 0; i < this.bucketArr.length; i++) {
                List bucket = this.bucketArr[i];
                if (ourPriority > i)  { //ours is a lower priority than the current bucket
                    if (bucket.size() > 0) {
                        return false;
                    }
                    //else continue on to examine next bucket
                }
                else if (ourPriority == i) {
                    if (bucket.size() > 0) {
                        if (!alreadyQueued) {
                            return false;
                        }
                        else {
                            if (this.priorities[i].isServiceInOrderReceived()) {
                                if (bucket.get(0) == Thread.currentThread()) {
                                    return true;
                                }
                                else {
                                    return false;
                                }
                            }
                            else {
                                return true;
                            }
                        }
                    }
                    else {
                        return true;
                    }
                }
                else {
                    return true;    //should not be reachable
                }
            }
        }
        return true;    //should not be reachable
    }
    
    /**
     * <p>Add the current thread to the appropriate "bucket" and make it wait.
     * If the current thread finishes waiting its turn without exceeding the 
     * <code>this.waitTimeout</code> interval, remove it from the bucket, 
     * assign it to <code>this.threadInProgress</code> (thus making it
     * the thread of record completing its normal execution), and
     * return <code>true</code>, thereby
     * letting it continue its normal execution. If the current thread's wait 
     * time exceeds the <code>this.waitTimeout</code> interval, 
     * remove it from the bucket and 
     * return <code>false</code>, thereby letting it continue its normal 
     * execution, but not as the thread of record completing its normal execution.</p>
     * @param priority The priority level of the current thread.
     * @throws <code>IllegalArgumentException</code> if the supplied priority is unknown to this <code>ThreadSynchronizer</code> instance.
     * @return <code>true</code> if the current thread has been assigned to <code>this.threadInProgress</code> and is thus
     * considered the thread of record completing its normal execution, <code>false</code> otherwise.
     */
    private boolean queueCurrentThreadAndWait(ThreadPriority priority) throws InterruptedException {
        synchronized (this.bucketArr) {
            int ourPriority = getPriorityIndex(priority);
            Thread currentThread = Thread.currentThread();
            
            //add currentThread to correct bucket
            this.bucketArr[ourPriority].add(currentThread);

            while(true) {
                if (this.debug) {
                    String priorityMessage = priority.getName() == null ? (" with priorityIndex " + ourPriority) : (" with priority '" + priority.getName() + "'");
                    log("Thread " + getDebugCurrentThreadNumber() + priorityMessage + " in queue will now wait");
                }
                long time1 = System.currentTimeMillis();
                this.bucketArr.wait(this.waitTimeout);
                long time2 = System.currentTimeMillis();
                if (time2 - time1 >= waitTimeout) {
                    //remove current thread from appropriate bucket
                    this.bucketArr[ourPriority].remove(currentThread);
                    
                    //return false without assigning currentThread to this.threadInProgress
                    if (this.debug) {
                        log("Thread " + getDebugCurrentThreadNumber() + " in queue waited too long. waited for: " + (time2 - time1));
                    }
                    return false;
                }
                if (canCurrentThreadProceed(ourPriority, true)) {
                    //remove current thread from appropriate bucket
                    this.bucketArr[ourPriority].remove(currentThread);

                    //set current thread as thread in progress
                    if (this.debug) {
                        log("Thread " + getDebugCurrentThreadNumber() + " in queue will now proceed");
                    }
                    this.threadInProgress = currentThread;
                    return true;
                }
            }
        }
    }

    /**
     * <p>Get the zero-based index of the supplied <code>priority</code> in 
     * <code>this.priorities</code>.</p>
     * @param priority The <code>ThreadPriority</code> instance whose index we want to find in <code>this.priorities</code>.
     * @throws <code>IllegalArgumentException</code> if the supplied priority is unknown to this <code>ThreadSynchronizer</code> instance.
     * @return The zero-based index of the supplied <code>priority</code> in 
     * <code>this.priorities</code>.
     */
    private int getPriorityIndex(ThreadPriority priority) {
        for (int i = 0; i < this.priorities.length; i++) {
            if (this.priorities[i] == priority) {
                return i;
            }
        }
        throw new IllegalArgumentException("unknown ThreadPriority: " + priority);
    }
    
    /**
     * <p>If we are debugging this class, use <code>this.debugThreadCounter</code> to place an entry for the current
     * thread in <code>this.debugThreadMap</code>.</p>
     */
    private void assignDebugCurrentThreadNumber() {
        if (this.debug) {
            Long threadNumber = new Long(this.debugThreadCounter++);
            this.debugThreadMap.put(Thread.currentThread(), threadNumber);
        }
    }
    
    /**
     * <p>If we are debugging this class, return the debugging number
     * assigned to the current thread as a <code>Long</code> instance; otherwise return <code>null</code>.</p>
     * @return The debugging number assigned to the current thread if we are debugging this class or <code>null</code> otherwise.
     */
    private Long getDebugCurrentThreadNumber() {
        if (this.debug) {
            return (Long)this.debugThreadMap.get(Thread.currentThread());
        }
        return null;
    }
    
    /**
     * <p>Log the supplied <code>message</code>. We use <code>System.out.println</code>
     * here for this purpose.</p>
     * @param message The message to log.
     */
    private void log(String message) {
        System.out.println(message);
    }
}
