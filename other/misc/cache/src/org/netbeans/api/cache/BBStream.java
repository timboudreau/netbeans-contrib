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
 * BBStream.java
 *
 * Created on October 20, 2004, 12:28 PM
 */

package org.netbeans.api.cache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * Trivial stream wrapper over a byte buffer (I have a feeling there probably
 * is some JDK class that does this).
 *
 * @author Tim Boudreau
 */
final class BBStream extends CacheInputStream {
    private final ByteBuffer buf;
    /** Creates a new instance of BBStream */
    BBStream(ByteBuffer bb) {
        this.buf = bb;
    }
    
    public int read() {
        return (int) buf.get();
    }
    
    public int read(byte[] b) {
        int count = Math.min(b.length, buf.limit() - buf.position());
        if (count > 0) {
            buf.get(b, 0, count);
        }
        return count;
    }
    
    public int read(byte[] b, int off, int len) {
        assert len <= b.length : "Bad length: " + len;
        int count = Math.min (len, buf.limit() - buf.position());
        if (count > 0) {
            buf.get(b, off, count);
        }
        return count;
    }
    
    public int available() {
        return buf.limit() - buf.position();
    }
    
    public boolean markSupported() {
        return true;
    }
    
    public void mark (int readlimit) {
        buf.mark();
    }
    
    public void reset() throws IOException {
        buf.reset();
    }
    
    public long getPosition() {
        return buf.position();
    }
    
    public InputStream newStream(long start, long end) {
        int pos = buf.position();
        buf.position ((int) start);
        ByteBuffer nue = buf.slice();
        nue.limit ((int) (end - start));
        buf.position(pos);
        return new BBStream(nue);
    }
    
    public ByteBuffer getBuffer() {
        return buf.asReadOnlyBuffer();
    }
    
    public String toString() {
        return "[BBStream]" + buf.asCharBuffer();
    }
}
