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
 * SubCache.java
 *
 * Created on October 31, 2004, 1:41 PM
 */

package org.netbeans.api.cache;
import java.io.*;
import java.nio.*;

/**
 * A sub cache that provides indexed access to a single entry in another
 * cache.
 *
 * @author Tim Boudreau
 */
final class SubCache extends Cache {
    private final CacheParser parser;
    private final Cache cache;
    private final int base;
    
    /** Creates a new instance of SubCache */
    public SubCache(Cache cache, CacheParser parser, int index) {
        this.cache = cache;
        this.parser = parser;
        this.base = index;
    }

    public ByteBuffer getBuffer(int index) {
        ByteBuffer master = cache.getBuffer(base);
        System.err.println("getBuffer " + index + " capacity " + master.capacity() + " currlimit " + master.limit());
        parser.setBuffer (master);
        int start = parser.indexOf(index);
        master.position (start);
        int end = parser.indexOf(index+1);
        System.err.println("getBuffer " + index + " limit to " + end + " start " + start + " capacity " + master.capacity() + " currlimit " + master.limit());
        master.limit (end);
            
        ByteBuffer result = master.slice();
        return result;
    }
    public int size() {
        parser.setBuffer (cache.getBuffer(base));
        return parser.size();
    }
    
    public int hashCode() {
        return (parser.hashCode() * 31) + (cache.hashCode() * 11) + base;
    }
    
    public boolean equals (Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubCache) {
            SubCache other = (SubCache) o;
            return other.parser.equals(parser) && other.base == base &&
                    other.cache.equals(cache);
        } else {
            return false;
        }
    }
    

    public CacheInputStream getInputStream(int index) {
        throw new UnsupportedOperationException ("not implemented"); //XXX
    }

    public void add(ByteBuffer bb) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public void add(CharSequence seq) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public void deleteEntry(int index) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public OutputStream getOutputStream(int index) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }
}
