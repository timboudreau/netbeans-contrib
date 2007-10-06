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
package org.netbeans.modules.tasklist.usertasks.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * The TimeoutProvider class implements a way to let object that implements
 * the Timeout interface to be notified at a given time.
 *
 * @author Trond Norbye
 */
public class TimeoutProvider implements Runnable {
    /** An inner class used to stash away all of the data */
    private class Entry {
        /** Create a new Entry instance 
         * @param owner the owner of the timeout
         * @param userRef should be sent back with the timeout
         * @param timeout when the timeout will expire
         */
        public Entry(Timeout owner, Object userRef, long timeout) {
            this.owner = owner;
            this.userRef = userRef;
            this.timeout = timeout;
        }
        /** The object to notify */
        public Timeout owner;
        /** An optional user reference */
        public Object  userRef;
        /** When this entry should time out */
        public long    timeout;
    }
       
    /**
     * Private constructor for this Singleton class...
     * Create the linked list, and start the thread...
     */
    private TimeoutProvider() { 
	alarms = new LinkedList<Entry>();
        alarms.add(new Entry(null, null, Long.MAX_VALUE));

	thread = new Thread(this, "TimeoutProvider"); // NOI18N
        thread.start();
    }
    
    /**
     * Get the one and only instance of the TimeoutProvider
     * @return the handle to the TimeoutProvider
     */
    public static TimeoutProvider getInstance() { 
	if (instance == null) {
	    instance = new TimeoutProvider();
	}
	return instance; 
    }

    /**
     * Cancel a timeout
     * @param owner the owner of the timeout
     * @param userRef the owners reference (null == all timeouts belonging to
     *                to this object)
     */
    public void cancel(Timeout owner, Object userRef) {
	synchronized (alarms) {
	    ListIterator i = alarms.listIterator(0);

	    while (i.hasNext()) {
                Entry e = (Entry)i.next();
                
		if (owner.equals(e.owner)) {
                    if (userRef == null || e.userRef.equals(userRef)) {
                        i.remove();
                    }
		}
	    }
	}
	    
	thread.interrupt();
    }

    /**
     * Add a new object to supervision
     * @param owner the object that owns the timeout
     * @param userRef an object the user would like to get back with the timeout
     * @param timeout the time this object should time out
     */
    public void add(Timeout owner, Object userRef, long timeout) {
	synchronized (alarms) {
	    ListIterator i = alarms.listIterator(0);
	    boolean done = false;
	    int idx = 0;

	    while (!done && i.hasNext()) {
                Entry e = (Entry)i.next();

		if (timeout < e.timeout) {
		    done = true;
		} else {
		    ++idx;
		}
	    }
	    
	    alarms.add(idx, new Entry(owner, userRef, timeout));
	}

	thread.interrupt();
    }

    /**
     * The "message loop" of the TimeoutProvider. 
     * Wait for the next timeout and signal all of the items...
     */
    public void run() {

        while (true) {
            // Find the next timeout
	    long nextTimeout;
	    synchronized (alarms) {
		nextTimeout = ((Entry)alarms.get(0)).timeout;
	    }
	    long currentTime = System.currentTimeMillis();
	    try {
                long sleeptime = nextTimeout - currentTime;
                if (sleeptime > 0) {
                    Thread.sleep(sleeptime);
                }
                
                Vector<Entry> vec = new Vector<Entry>();
                synchronized (alarms) {
                    ListIterator i = alarms.listIterator(0);
                    boolean done = false;
                    
                    while (!done && i.hasNext()) {
                        Entry e = (Entry)i.next();
                        
                        if (e.timeout <= nextTimeout) {
                            // I cannot notify the object from this synchronized
                            // block since the callback might want to register
                            // a new callback...
                            vec.add(e);
                            i.remove(); // Remove this timeout
                        } else {
                            done = true;
                        }
                    }
                } // synchronized

                Iterator i = vec.iterator();
                while (i.hasNext()) {
                    // I should perhaps do this in it's own thread so I don't
                    // block the TimeoutProvider....
                    Entry e = (Entry)i.next();
                    e.owner.timeoutExpired(e.userRef);
                }
            
            } catch (InterruptedException e) {
		// Do nothing, reevaluate next timeout!
	    }
	}
    }

    /** The internal thread used for waiting and signaling the objects */
    private Thread thread;
    /** The one and only instance of the timeout provider */
    private static TimeoutProvider instance;
    /** A "sorted" linked list of objects to wait for */
    private LinkedList<Entry> alarms;
}
