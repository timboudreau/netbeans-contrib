/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * DelayQueueQueue.java
 *
 * Created on October 21, 2006, 6:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */ 
package org.netbeans.api.workqueues;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Specialized queueing for processing work to be done on a file (or whatever).  
 * A delayed
 * blocking queue of per-file (file == Target) non-blocking queues.  When
 * put() is called, if one is not found, a new non-blocking Queue is
 * created for that file (a new instance of Entry keyed to the file and
 * owning the queue of things that want to process the file).
 * <p>
 * Adding work via put() is non-blocking.
 * <p>
 * The new queue will not be removable until its delay has expired.  Since
 * work will be posted when a file changes, when work is queued for a given
 * class we can expect some more to be enqueued in short order, and it should
 * all run against the same copy of the file's data loaded once.  So the delay
 * allows other running code to enqueue their own work to run against that
 * file.
 * <p>
 * Work will be enqueued in batches, and run on a background thread by
 * passing it to the QueueWorkProcessor passed to the constructor of this
 * object.
 * <h2>Usage</h2>
 * Simply implement the two methods of QueueWorkProcessor, construct an
 * instance of this class and call put (obj1, obj2) to enqueue work to run in the
 * background.  After a timeout your QueueWorkProcessor will be called on
 * a background thread and passed obj1 and all objects that were passed
 * as obj2 in a collection that can be drained by type.
 *
 * @author Tim Boudreau
 */
public final class Dispatcher <Target, WorkType> {
    private final DelayQueue<PerKeyWork<Target,WorkType>> delayed =
            new DelayQueue<PerKeyWork<Target,WorkType>>();

    private final ExecutorService serv = Executors.newFixedThreadPool(1);
    private final QueueWorkProcessor<Target,WorkType> processor;
    private Future future;
    private volatile boolean started = false;
    static final Logger log = Logger.getLogger(Dispatcher.class.getName());

    /** Creates a new instance of DelayQueueQueue */
    public Dispatcher(QueueWorkProcessor<Target,WorkType> p) {
        this.processor = p;
    }

    /**
     * Remove all enqueued work related to the passed key.  This call is
     * non-blocking but does *not* guarantee that the work will not run
     * before the call completes (pending work against invalid objects must
     * be disabled some other way).
     */
    public Drainable<WorkType> remove (Target key) {
        for (Iterator <PerKeyWork<Target,WorkType>> i=delayed.iterator(); i.hasNext();) {
            PerKeyWork<Target,WorkType> e = i.next();
            if (key.equals(e.getKey())) {
                i.remove();
                return e;
            }
        }
        return null;
    }

    /**
     * Enqueue some work to be done against the key object.  This call is
     * non-blocking.
     */
    public void put(Target key, WorkType content) {
        log.log(Level.FINE, "put (" + key + ", " + content + ")");

        if (started && !isRunning()) {
            throw new IllegalStateException("Thread already exited");
        }
        List<PerKeyWork<Target,WorkType>>  l =
                new ArrayList<PerKeyWork<Target,WorkType>>(delayed);

        for (PerKeyWork<Target,WorkType> e : l) {
            log.log(Level.FINE, "CHECK " + e);
            if (key.equals(e.getKey()) && !e.isExpired()) {
                log.log (Level.FINE, "GOT ONE: " + e);
                if (e.add(content)) {
                    log.log(Level.FINE, "Put " + content + " into existing queue " + e);
                    return;
                }
            } else if (e.isExpired()) {
                log.log(Level.FINE, "Entry was already expired, new Entry " +
                        "will be created");
            }
        }
        PerKeyWork<Target,WorkType> entry = new PerKeyWork<Target,WorkType>(key, content);
        delayed.offer(entry);
        log.log(Level.FINE, "Created a new entry for " + key + ": " + entry);
        if (!started) {
            log.log(Level.FINE, "Starting queue thread");
            start();
        }
    }

    PerKeyWork<Target,WorkType> take() throws InterruptedException {
        return delayed.take();
    }

    /**
     * Determine if there is no pending work for any object.
     */
    public boolean isEmpty() {
        return delayed.isEmpty();
    }

    public String toString() {
        return "DelayQueueQueue containing " + delayed;
    }

    private void start() {
        started = true;
        assert future == null;
        future = serv.submit(new Runner <Target, WorkType> (this, processor));
    }

    /**
     * Determine if the queue is running.  Will be false if either it has not
     * yet started or has been stopped due to Processor.handleException() returning
     * true, or by the processor thread being interrupted.
     */
    public boolean isRunning() {
        return future != null && !future.isDone();
    }

    /**
     * Stop this queue.  It cannot be restarted.  Pending work may or may not
     * be completed.
     */
    public void stop() {
        serv.shutdownNow();
    }

    /**
     * Wait for the next work cycle to complete, unless the queue is empty.
     */
    public void waitFor() throws InterruptedException {
        if (isEmpty()) {
            return;
        }
        waitNext();
    }

    /**
     * Wait for the next work cycle to complete, unless the queue is empty.
     */
    public void waitFor(long timeout) throws InterruptedException {
        if (isEmpty()) {
            return;
        }
        waitNext(timeout);
    }

    /**
     * Wait for the next work cycle to complete, blocking until the next
     * work cycle has completed.
     */
    public void waitNext() throws InterruptedException {
        if (EventQueue.isDispatchThread()) {
            throw new IllegalStateException ("Cannot wait on the dispatch " +
                    "thread");
        }
        synchronized (this) {
            wait();
        }
    }

    /**
     * Wait for the next work cycle to complete, blocking until the next
     * work cycle has completed or the timeout has elapsed.
     */
    public void waitNext(long timeout) throws InterruptedException {
        if (EventQueue.isDispatchThread()) {
            throw new IllegalStateException ("Cannot wait on the dispatch " +
                    "thread");
        }
        synchronized (this) {
            wait(timeout);
        }
    }

    private static final class Runner <Target, WorkType> implements Runnable {
        private Dispatcher<Target, WorkType> master;
        private QueueWorkProcessor<Target,WorkType> p;
        Runner(Dispatcher<Target, WorkType> master, QueueWorkProcessor<Target,WorkType> p) {
            this.master = master;
            this.p = p;
        }

        public void run() {
            for (;;) {
                try {
                    log.log(Level.FINE, "Enter wait on DelayQueueQueue");
                    PerKeyWork<Target,WorkType> entry = master.take();
                    log.log(Level.FINE, "Got an entry to process");
                    try {
                        p.process(entry.getKey(), entry);
                    } catch (Exception e) {
                        try {
                            boolean cont = p.handleException(e, entry.getKey(),
                                    entry);

                            log.log(Level.FINE, "Handle exception " + e + " " +
                                    " continue " + cont);
                            if (!cont) {
                                return;
                            }
                        } catch (RuntimeException e1) {
                            Logger.getLogger(Dispatcher.class.getName()).log(
                                    Level.WARNING, "Handling exception " +
                                    e.getMessage(), e1);
                        }
                    } finally {
                        if (!entry.isEmpty()) {
                            //Other code could have had a reference to this
                            //entry's queue when it was taken off the master
                            //queue, and could have added data to it
                            master.delayed.offer (entry);
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } finally {
                    if (master.isEmpty()) {
                        synchronized (master) {
                            master.notifyAll();
                        }
                    }
                }
                if (Thread.interrupted()) {
                    return;
                }
            }
        }
    }
}
