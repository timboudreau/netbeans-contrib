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
package org.netbeans.api.docbook;

import org.openide.filesystems.FileObject;


/**
 * Parent class for parse visitors.
 */
public abstract class Callback<T> {
    private volatile boolean cancelled;
    private final T t;
    Callback(T t) {
        this.t = t;
    }

    /**
     *  Get the regexp Pattern or SAX ContentHandler this callback provides
     * to collect parse info.
     */ 
    public final T getProcessor() {
        return t;
    }

    /**
     * Cancel this callback so it will not be run.
     */ 
    public final void cancel() {
        cancelled = true;
        cancelled();
    }

    /**
     * Determine if this callback has been cancelled.
     */ 
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Called when a parse is started.  Default impl does nothing.
     */ 
    protected void start(FileObject f, ParseJob job) {
        //do nothing
    }

    /**
     * Called when a parse is cancelled.  Default impl does nothing.
     */ 
    protected void cancelled() {
        //do nothing
    }

    /**
     * Called when a parse is completed, either with failure or success.  
     * Default impl does nothing.
     */ 
    protected void done(FileObject f, ParseJob job) {
        //do nothing
    }

    /**
     * Called when a parse is has failed.  Default impl does nothing.
     * Failure can be throwing a SAXException, or any runtime exception
     * from any method on this class.
     */ 
    protected void failed(Exception e, FileObject ob, ParseJob job) {
        //do nothing
    }
}