/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SubStorage.java
 *
 * Created on October 31, 2004, 12:45 PM
 */

package org.netbeans.lib.storage;
import org.netbeans.api.storage.Storage;
import java.nio.*;
import java.io.*;

/**
 * A read-only Storage over a single ByteBuffer, used to create sub-storages
 * of a master storage.
 *
 * @author Tim Boudreau
 */
public class SubStorage extends Storage {
    private final ByteBuffer master;
    private final int size;
    
    /** Creates a new instance of ByteBufferStorag */
    public SubStorage(ByteBuffer buf) {
        this.master = buf;
        size = buf.limit();
    }

    public java.nio.ByteBuffer getReadBuffer(long start, long length) throws java.io.IOException {
        master.position((int) start);
        master.limit((int) length);
        return master.slice();
    }

    public boolean isClosed() {
        return false;
    }

    public long lastWrite() {
        return 0;
    }

    public int size() {
        return size;
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void truncate(long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public int write(java.nio.ByteBuffer buf) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public java.nio.ByteBuffer getWriteBuffer(int length) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public long replace(java.nio.ByteBuffer buf, long start, long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws an UnsupportedOperationException.
     */
    public void excise(long start, long end) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void flush() throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Does nothing
     */
    public void close() throws java.io.IOException {
        //do nothing
    }

    /**
     * Does nothing
     */
    public void dispose() {
        //do nothing
    }
}
