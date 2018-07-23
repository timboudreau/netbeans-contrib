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
    private final long expTime;

    private final ConcurrentLinkedQueue<WorkType> q = new ConcurrentLinkedQueue<WorkType>();

    private final Target key;

    public PerKeyWork(Target key, WorkType firstItem, long delay) {
        expTime = System.currentTimeMillis()  + delay;
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

    @SuppressWarnings("unchecked")
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