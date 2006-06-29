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
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber;

import org.jivesoftware.smack.packet.Packet;

/**
 * Result is a future-value pattern or status interface.
 * Allows both active and passive waiting for acknowledge of an operation
 * and acts as a container for response packet, if applicable.
 *
 * It is used as a return value from some methods, thus can't be constructed
 * directly.
 *
 * @author  nenik
 */
public final class Result {
    private Object lock = new Object();

    private Packet result;
    private Callback callback;
    private boolean finished;
    private boolean cancelled;
    
    /** Creates a new instance of Request */
    Result(Callback callback) {
        this.callback = callback;
    }
    
    public Packet getResult() {
        synchronized (lock) {
            return result;
        }
    }
    
    public boolean isFinished() {
        synchronized (lock) {
            return finished;
        }
    }
    
    public void waitFinished() throws InterruptedException {
        synchronized(lock) {
            while (!finished) lock.wait();
        }
    }
    
    public boolean isCancelled() {
        synchronized (lock) {
            return cancelled;
        }
    }
    
    public void cancel() {
        synchronized(lock) {
            cancelled = true;
            markFinished(null);
        }
    }
    
    void markFinished(Packet result) {
        synchronized (lock) {
            finished = true;
            this.result = result;
            lock.notifyAll();
        }
        if (callback != null) callback.resultFinished(this);
    }
    
    
    
    
    public static interface Callback {
        public void resultFinished(Result result);
    }
    
}
