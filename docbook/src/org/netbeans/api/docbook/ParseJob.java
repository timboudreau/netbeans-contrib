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
 * ParseJob.java
 *
 * Created on October 16, 2006, 7:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.docbook;

import java.util.Collection;
import org.netbeans.modules.docbook.ParseJobFactory;
import org.openide.filesystems.FileObject;

/**
 * A job that will run asynchronously, parsing a file's XML and calling back
 * the provided Callback with results.
 * 
 * @author Tim Boudreau
 */
public abstract class ParseJob <T extends Callback> {
    /**
     * Throws exception if not the one legal subclass allowed.
     */ 
    protected ParseJob() {
        if (getClass() != ParseJobFactory.ParseJobImpl.class) {
            throw new IllegalStateException ("Subclassing not allowed");
        }
    }

    /**
     * Attach some work that should be done the next time the passed
     * file is parsed, causing a parse to be enqueued if necessary.
     * Once the job is created, call its enqueue() method to prepare it
     * to run.
     */
    public static ParseJob createJob (FileObject file, Callback callback) {
        if (file == null) {
            throw new NullPointerException ("File null");
        }
        if (callback == null) {
            throw new NullPointerException ("Callback null");
        }
        return ParseJobFactory.createJob (file, callback);
    }
    
    /**
     * Create a collection of jobs from a collection of callbacks.
     */ 
    public static Collection <ParseJob> createJobs (FileObject file, Collection <Callback> callbacks) {
        if (file == null) {
            throw new NullPointerException ("File null");
        }
        if (callbacks == null) {
            throw new NullPointerException ("Callback null");
        }
        if (!callbacks.isEmpty()) {
            return ParseJobFactory.createJobs (file, callbacks);
        } else {
            return null;
        }
    }
    

    public final ParseJob enqueue() {
        ParseJobFactory.enqueue(this);
        return this;
    }
    
    public abstract boolean isEnqueued();
    
    public abstract boolean isRunning();
    
    public FileObject getFile() {
        return ParseJobFactory.getFile(this);
    }

    /**
     * Blocks until this job has been run.  If it is not enqueued,
     * returns immediately.
     */ 
    public abstract void waitFinished() throws InterruptedException;

    public void cancel() {
        ParseJobFactory.cancelled (this);
    }

    protected final void done (Callback callback, FileObject ob, ParseJob job) {
        callback.done(ob, job);
    }

    protected final void start (Callback callback, FileObject ob, ParseJob job) {
        callback.start(ob, job);
    }

    protected final void failed (Callback callback, Exception e, FileObject ob, ParseJob job) {
        callback.failed (e, ob, job);
    }



}
