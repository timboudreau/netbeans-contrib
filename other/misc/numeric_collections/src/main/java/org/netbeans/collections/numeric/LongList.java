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

package org.netbeans.collections.numeric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** A collections-like list of primitive longs.  Entries may be added only
 * in ascending order.
 *
 * @author  Tim Boudreau
 */
public final class LongList {
private int initialCapacity;
    private long[] array;
    private int used = 0;
    private long lastAdded = Long.MIN_VALUE;
    private boolean modified = false;
    
    /** Creates a new instance of IntMap */
    public LongList(int capacity) {
        initialCapacity = capacity;
        array = allocArray (initialCapacity);
    }
    
    public LongList (File f) throws IOException {
        this(10);
        load (f);
    }
    
    /** Add an integer to the lineStartList.  Must be greater than the preceding value
     * or an exception is thrown. */
    public synchronized void add (long value) {
        if (value < lastAdded) {
            throw new IllegalArgumentException ("Contents must be presorted - " + //NOI18N
                "added value " + value + " is less than preceding " + //NOI18N
                "value " + lastAdded); //NOI18N
        }
        if (used >= array.length) {
            growArray();
        }
        array[used++] = value;
        lastAdded = value;
        modified = true;
    }
    
    /**
     * Set the value at the specified index.
     * @throws IllegalArgumentException if the passed value would cause the
     *  array to become out-of-sequence.
     */
    public synchronized void set (int idx, long val) {
        if (idx >= used || idx < 0) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(idx));
        }
        if (idx != 0 && array[idx-1] >= val) {
            throw new IllegalArgumentException ("Cannot set element " + idx + 
                " to " + val + ", it is <= the previous element");
        }
        if (idx != size() -1 && array[idx+1] <= val) {
            throw new IllegalArgumentException ("Cannot set element " + idx +
                " to " + val + ", it is >= the element at " + (idx+1));
        }
        array[idx] = val;
    }
    
    private long[] allocArray (int size) {
        long[] result = new long[size];
        //Fill it with Integer.MAX_VALUE so binarySearch works properly (must
        //be sorted, cannot have 0's after the actual data
        Arrays.fill(result, Long.MAX_VALUE);
        return result;
    }
    
    /**
     * Get the value at a given index 
     */
    public synchronized long get(int index) {
        if (index >= used) {
            throw new ArrayIndexOutOfBoundsException("List contains " + used 
                + " items, but tried to fetch item " + index);
        }
        return array[index];
    }
    
    /**
     * Determine if this LongList contains the passed value
     */
    public boolean contains (long val) {
        return Arrays.binarySearch(array, val) >= 0;
    }
    
    /**
     * Returns the index of the passed value, or a value < 0 if it is not
     * present.
     */
    public int indexOf (long val) {
        int result = Arrays.binarySearch(array, val);
        if (result < 0) {
            result = -1;
        }
        if (result >= used) {
            result = -1;
        }
        return result;
    }
    
    /** Return the <strong>index</strong> of the value closest to but lower than
     * the passed value */
    public int findNearest (long val) {
        if (size() == 0) {
            return -1;
        }
        return findInRange (val, 0, size());
    }
    
    /** Recursive binary search */
    private int findInRange (long val, int start, int end) {
        if (end - start <= 1) {
            return start;
        }
        int midPoint = start + ((end - start) / 2);
        long valAtMidpoint = get (midPoint);
        if (valAtMidpoint > val) {
            return findInRange (val, start, start + ((end - start) / 2));
        } else {
            return findInRange (val, start + ((end - start) / 2), end);
        }
    }
    
    public synchronized int size() {
        return used;
    }
    
    private void growArray() {
        long[] old = array;
        array = allocArray(Math.round(array.length * 1.5f));
        System.arraycopy(old, 0, array, 0, old.length);
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer ("LongList [");
        for (int i=0; i < used; i++) {
            result.append (i);
            result.append (':');
            result.append (array[i]);
            if (i != used-1) {
                result.append(',');
            }
        }
        result.append (']');
        return result.toString();
    }
    
    private static boolean sequential (long[] a) {
        long last = Long.MIN_VALUE;
        for (int i=0; i < a.length; i++) {
            if (a[i] == Long.MAX_VALUE) {
                break;
            }
            if (a[i] <= last) {
                return false;
            }
            last = a[i];
        }
        return true;
    }    
    
    private void load (File f) throws IOException {
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            FileChannel ch = fis.getChannel();
            try {
                byte[] b = new byte[(int)ch.size()];
                
                fis.read(b);
                ByteBuffer buf = ByteBuffer.wrap(b);
                IntBuffer ib = buf.asIntBuffer();
                int version = ib.get();
                initialCapacity = ib.get();
                used = ib.get();
                ib.get(); //4 byte pad to align data

                LongBuffer lb = buf.asLongBuffer();
                lb.position (3);
                lastAdded = lb.get();
                
                array = new long[lb.limit() - lb.position()];
                lb.get(array);
                assert sequential(array) : "Cache data out of sequence: " + this;
            } finally {
                fis.close();
                ch.close();
                ch = null;
            }
        }
    }
    
    private static final int VERSION = 1;
    /**
     * Save this LongList to a file.
     */
    public void save (File f) throws IOException {
        if (!modified) {
            return;
        }
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream (f);
        FileChannel ch = fos.getChannel();
        FileLock lock = ch.lock();
        try {

            ByteBuffer buf = ByteBuffer.allocate(24 + (8 * (used + 1)));

            ByteBuffer b1 = ByteBuffer.allocate(24);
            IntBuffer ib = b1.asIntBuffer();
            ib.put(VERSION);
            ib.put(initialCapacity);
            ib.put(used);
            ib.put(0); //4 byte pad to align data
            buf.put(b1);
            

            b1 = ByteBuffer.allocate(8 * (used + 1));
            LongBuffer bl = b1.asLongBuffer();
            bl.put (lastAdded);

            bl.put(array, 0, used);
            buf.put(b1);
            fos.write(buf.array());
            
            fos.flush();
        } finally {
            lock.release();
            fos.close();
        }
    }
    
    /**
     * Get an array of longs representing the contents of this list
     */
    public long[] toArray() {
        if (used == 0) {
            return new long[0];
        }
        long[] result = new long[used];
        System.arraycopy (array, 0, result, 0, result.length);
        return result;
    }    
    
    public int hashCode() {
        int result = 0;
        for (int i=0; i < used; i++) {
            result ^= (int)array[i] * (i + 31);
        }
        return result;
    }
    
    public boolean equals (Object o) {
        if (o instanceof LongList) {
            LongList il = (LongList) o;
            if (il.used == used && il.lastAdded == lastAdded) {
                boolean result = true;
                for (int i=0; i < used; i++) {
                    result &= il.array[i] == array[i];
                    if (!result) {
                        break;
                    }
                }
                return result;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Delete the entry at a given index.
     */
    public synchronized void delete (int index) {
        //XXX this can be made more efficient if we don't mind doing a *lot*
        //of bookkeeping - keep an array of deleted indices and mask things
        //that have been deleted.
        long result = get(index);
        Integer i = new Integer(index);
        HashSet deleted = new HashSet(size());
        deleted.add(i);
        compact(deleted);
    }
    
    /**
     * Delete a range of entries.  Deletes from first, up to and including last.
     */
    public synchronized void delete (int first, int last) {
        assert first <= last;
        HashSet deleted = new HashSet();
        for (int i=first; i <= last; i++) {
            deleted.add (new Integer(i)); //XXX inefficient
        }
        compact(deleted);
    }
    
    /**
     * Add an amount to all entries following the passed index.
     */
    public void adjust (int start, long amount) {
        for (int i=start; i < used; i++) {
            long old = array[i];
            array[i] += amount;
        }
        assert sequential(array) : "Out of sequence";
    }
    
    private void compact(Set deleted) {
        if (deleted == null) {
            return;
        }
        int sz = size() - deleted.size();
        long[] nue = new long[sz];
        int idx = 0;
        for (int i=0; i < used; i++) {
            if (!deleted.contains(new Integer(i))) {
                nue[idx] = array[i];
                idx++;
            }
        }
        used = sz;
        array = nue;
        lastAdded = sz == 0 ? Long.MIN_VALUE : array[sz-1];
        deleted = null;
    }    
}
