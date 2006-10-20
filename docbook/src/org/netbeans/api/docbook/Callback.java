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