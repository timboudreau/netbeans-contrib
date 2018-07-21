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
/*
 * Cache.java
 *
 * Created on October 31, 2004, 1:02 PM
 */

package org.netbeans.api.cache;
import java.io.*;
import java.nio.*;

import org.netbeans.lib.cache.CacheImpl;

/**
 * A cache for indexed wads of data - rather like a big List of binary data,
 * but backed by a memory mapped file.  Contains specific support for
 * caching text, via some String based entries.
 * <p>
 * On disk, a Cache consists of two files - the data file, and an indices
 * file which lists the start offsets of entries in the data file.
 * <p>
 * Cache objects are most highly optimized for the case where most data
 * is static, and new entries will simply be appended to the cache.  There
 * are methods to delete entries and replace entries, but they are somewhat
 * less efficient (though not prohibitively slow).
 * <p>
 * The stored, memory mapped data may be written to disk at any time;  the
 * <code>close()</code> method must be called to update the indices data, when
 * a cache object is disposed (and also to ensure that any cached data in the
 * memory mapped file is indeed flushed to disk).  Automation of some of this
 * may be done in the future.
 * <p>
 * <b>Threading:</b> At this point, little attempt at thread safety is made.  
 * Clients should ensure they do not access a cache concurrently from two
 * threads.  Read operations and read while appending should be safe, but
 * are not guaranteed to be.
 *
 * @see PersistentCache
 * @author Tim Boudreau
 */
public abstract class Cache {
    
    protected Cache() {
    }
    
    public static PersistentCache getCache (String name, File dir) {
        return CacheImpl.getCache(name, dir);
    }
    
    
    /**
     * Append the contents of the passed ByteBuffer to the cache, incrementing
     * the return value of <code>size()</code> by 1.
     */
    public abstract void add (ByteBuffer bb) throws IOException;
    
    /**
     * Append the passed ByteBuffer to the cache, incrementing
     * the return value of <code>size()</code> by 1.
     */
    public abstract void add (CharSequence seq) throws IOException;

    /**
     * Delete one entry from the cache.
     */
    public abstract void deleteEntry (int index) throws IOException;
    
    /**
     * Delete a range of entries from the cache, up to and including
     * the passed end value.
     * <p>
     * The default implementation simply iterates and calls
     * <code>deleteEntry</code>.  It is preferable to override this
     * with a more efficient implementation where possible.
     */
    public void deleteEntries (int start, int end) throws IOException {
        for (int i=start; i <= end; i++) {
            deleteEntry (i);
        }
    }
    
    /**
     * Get a cache entry as a CharSequence.  
     * <i><b>Important:</b> Do not assume that 
     * <code>"foo".equals(get(n)) == "foo".equals(get(n).toString())</code>
     * - the CharSequence returned is not necessarily an instance of String. 
     * To compare Strings, always use <code>get(n).toString()</code>.</i>
     */
    public CharSequence get(int index) {
        return getBuffer(index).asCharBuffer();
    }
    
    /**
     * Get a cached data entry as a ByteBuffer.
     */
    public abstract ByteBuffer getBuffer (int index);
    
    /**
     * Get an input stream representing the cached data at a given index.
     */
    public CacheInputStream getInputStream (int index) {
        if (index < 0 || index >= size()) {
            throw new IllegalArgumentException ("Out of range: " + index);
        }
        return new BBStream (getBuffer(index));
    }    
    /**
     * Get an output stream representing the cached data at a given index.
     */
    public abstract OutputStream getOutputStream (int index) throws IOException;
    
    /**
     * Get the number of entries this cache contains
     */
    public abstract int size();
    
    /**
     * Get a new Cache object representing a single element from this 
     * cache.  If elements in a cache have some sub-structure which should
     * be parsed into individual objects, the CacheParser passed is used
     * to determine the indices of the data for those objects.
     */
    private final Cache getSubCache (int index, CacheParser parser) {
        //XXX needs some work before making public
        return new SubCache (this, parser, index);
    }
    
    /**
     * A class used by sub caches to parse contents on the fly.  
     */
     static abstract class CacheParser  {
        private ByteBuffer buffer;
        /**
         * Sets the byte buffer this parser will parse.
         */
        final void setBuffer (ByteBuffer buf) {
            this.buffer = buf;
        }
        
        /**
         * Get the byte buffer to parse.
         */
        protected final ByteBuffer getBuffer() {
            return buffer;
        }
        
        public abstract int indexOf (int cacheIndex);
        
        /**
         * Count the number of elements in the subcache
         */
        public abstract int size();
    }
}
