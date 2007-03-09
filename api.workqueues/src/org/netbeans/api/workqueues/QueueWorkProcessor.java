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

/**
 * Object which actually processes the work enqueued on an Dispatcher.
 * Work will be scheduled on a background thread, batched and fed to the
 * process method.
 */ 
public interface QueueWorkProcessor <Target, WorkType> {
    /**
     * Process a batch of work.  All of the pending work may be fetched from
     * the passed Drainable.  Any work left in the Drainable will be re-queued
     * and re-run after the owning Dispatcher's timeout has expired.
     * <p>
     * RuntimeExceptions thrown in this method will be passed to handleException().
     * If that returns true, the queue will die and no more work may be 
     * performed by it.
     * @param key The object that the work should be done on
     * @param work An object containing one or more items that should be 
     *  run against the passed Target
     */ 
    public void process(Target key, Drainable <WorkType> work) throws Exception;
    /**
     * Handle a runtime exception thrown by the process() method.  If false is
     * returned, the owning work queue will die and exit.
     * @return true If the queue should continue functioning
     */ 
    public boolean handleException(Exception e, Target key, 
            Drainable <WorkType> work);
}