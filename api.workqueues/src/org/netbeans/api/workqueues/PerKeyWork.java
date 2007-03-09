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
package org.netbeans.api.workqueues;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * A collection of jobs which belong to a particular key in a
 * DelayQueueQueue.  Adds and removes from the collection are thread-safe
 * non-blocking.
 *
 * @author Tim Boudreau
 */
final class PerKeyWork<Target, WorkType> implements Delayed, Drainable <WorkType> {
    private final long expTime = System.currentTimeMillis()  + DELAY;
    static long DELAY = 250;

    private final ConcurrentLinkedQueue<WorkType> q = new ConcurrentLinkedQueue<WorkType>();

    private final Target key;

    public PerKeyWork(Target key, WorkType firstItem) {
        this.key = key;
        q.offer(firstItem);
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    private volatile boolean expired = false;
    /**
     * Determine if this work collection has already been processed (*not*
     * whether its timeout has expired).
     */
    boolean isExpired() {
        return expired;
    }

    void expire() {
        expired = true;
    }

    public long getDelay(TimeUnit unit) {
        long result = unit.convert(expTime -
                    System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        Dispatcher.log.log(Level.FINE, "Delay on " + this + " now " + result);
        return result;
    }

    public int compareTo(Delayed o) {
        return (int) (getDelay(TimeUnit.MILLISECONDS)  - o.getDelay(
                    TimeUnit.MILLISECONDS));
    }

    public boolean equals(Object o) {
        return o instanceof PerKeyWork && key.equals (((PerKeyWork) o).key);
    }

    public int hashCode() {
        return key.hashCode() * 11;
    }

    public Queue getChildQueue() {
        return q;
    }

    public Target getKey() {
        return key;
    }

    public boolean add(WorkType content) {
        return !expired && q.offer(content);
    }

    public <T extends WorkType> List<T> drain(Class<T> clazz) {
        Dispatcher.log.log(Level.FINE, "Drain work for " + key);
        List<T> result = new LinkedList <T> ();
        for (Iterator<WorkType> i = q.iterator(); i.hasNext(); ) {
            WorkType c = i.next();
            if (clazz.isInstance(c)) {
                result.add((T) c);
                i.remove();
            }
        }
        if (isEmpty()) {
            expire();
        }
        return result;
    }

    public String toString() {
        return "Queue Entry for " + key + " with remaining " + q;
    }
}