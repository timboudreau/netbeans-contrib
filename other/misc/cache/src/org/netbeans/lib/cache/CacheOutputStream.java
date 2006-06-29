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
 * CacheOutputStream.java
 *
 * Created on October 25, 2004, 7:14 PM
 */

package org.netbeans.lib.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.netbeans.lib.storage.HeapStorage;
import org.netbeans.api.storage.Storage;

/**
 *
 * @author tim
 */
final class CacheOutputStream extends OutputStream {
    private Storage storage = null;
    private final CacheImpl cache;
    private int index;
    private boolean init = false;

    /** Creates a new instance of CacheOutputStream */
    public CacheOutputStream(CacheImpl cache, int index) {
        assert index >= 0 && index < cache.size() : "Bad index " + index;
        this.index = index;
        this.cache = cache;
    }
    
    public void write (int i) throws IOException {
        write (new byte[] { (byte) i });
    }
    
    public void write (byte[] b, int off, int len) throws IOException {
        if (storage == null) {
            init();
        }
        ByteBuffer bb = storage.getWriteBuffer(len);
        bb.put (b, off, len);
        bb.position (len);
        storage.write(bb);
    }
    
    public void write (byte[] b) throws IOException {
        if (storage == null) {
            init();
        }
        ByteBuffer bb = storage.getWriteBuffer(b.length);
        bb.put (b);
        bb.position (b.length);
        storage.write(bb);
    }
    
    private void init() throws IOException {
        if (init) {
            throw new IOException ("Closed");
        } else {
            storage = new HeapStorage();
        }
        cache.registerOutputStream(this);
        init = true;
    }
    
    public void flush() throws IOException {
        //do nothing
    }
    
    void setIndex (int i) {
        this.index = i;
    }
    
    public void close() throws IOException {
        cache.write(this);
        storage = null;
        cache.unregisterOutputStream(this);
    }
    
    public int getIndex() {
        return index;
    }
    
    public void destroy() {
        cache.unregisterOutputStream(this);
        index = -1;
    }
    
    public ByteBuffer buffer() {
        try {
            int sz = (int) storage.size();
            ByteBuffer result = storage.getReadBuffer(0, sz).slice();
            result.position(sz);
            return result;
        } catch (IOException ioe) {
            //Never thrown by HeapStorage
            return null;
        }
    }
    
}
