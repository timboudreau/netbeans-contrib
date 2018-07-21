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
 * IntList.java
 *
 * Created on March 21, 2004, 12:18 AM
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

/** A list of primitive integers.  Entries may be added only
 * in ascending order.
 *
 * @author  Tim Boudreau
 */
public final class IntList {
    private int initialCapacity;
    private int[] array;
    private int used = 0;
    private int lastAdded = Integer.MIN_VALUE;
    private boolean modified = false;
    
    /** Creates a new instance with a preallocated array of <code>capacity</code>
     * size. */
    public IntList(int capacity) {
        initialCapacity = capacity;
        array = allocArray (initialCapacity);
    }
    
    /**
     * Create an IntList by loading it from a file saved by another IntList.
     */
    public IntList (File f) throws IOException {
        this(0);
        load (f);
    }
    
    
    /** Add an integer to the lineStartList.  Must be greater than the preceding value
     * or an exception is thrown. */
    public synchronized void add (int value) {
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
    
    private int[] allocArray (int size) {
        int[] result = new int[size];
        //Fill it with Integer.MAX_VALUE so binarySearch works properly (must
        //be sorted, cannot have 0's after the actual data
        Arrays.fill(result, Integer.MAX_VALUE);
        return result;
    }
    
    /**
     * Get the integer stored at the specified index.
     */
    public synchronized int get(int index) {
        if (index >= used) {
            throw new ArrayIndexOutOfBoundsException("List contains " + used 
                + " items, but tried to fetch item " + index);
        }
        return array[index];
    }
    
    /**
     * Determine if the list contains the specified value.
     */
    public boolean contains (int val) {
        return Arrays.binarySearch(array, val) >= 0;
    }
    
    /** Return the <strong>index</strong> of the value closest to but lower than
     * the passed value */
    public int findNearest (int val) {
        if (size() == 0) {
            return -1;
        }
        return findInRange (val, 0, size());
    }
    
    /** Recursive binary search */
    private int findInRange (int val, int start, int end) {
        if (end - start <= 1) {
            return start;
        }
        int midPoint = start + ((end - start) / 2);
        int valAtMidpoint = get (midPoint);
        if (valAtMidpoint > val) {
            return findInRange (val, start, start + ((end - start) / 2));
        } else {
            return findInRange (val, start + ((end - start) / 2), end);
        }
    }
    
    /**
     * Get the index of the passed value.
     */
    public int indexOf (int val) {
        int result = Arrays.binarySearch(array, val);
        if (result < 0) {
            result = -1;
        }
        if (result >= used) {
            result = -1;
        }
        return result;
    }
    
    /**
     * Set the value at the specified index.
     * @throws IllegalArgumentException if the passed value would cause the
     *  array to become out-of-sequence.
     */
    public void set (int idx, int val) {
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
        assert sequential(array) : "Array out of sequence: " + this;
        modified = true;
    }
    
    /**
     * Get the number of entries in this IntList.
     */
    public synchronized int size() {
        return used;
    }
    
    /**
     * Get an array of integers representing the data in this IntList.
     */
    public int[] toArray() {
        if (used == 0) {
            return new int[0];
        }
        int[] result = new int[used];
        System.arraycopy (array, 0, result, 0, result.length);
        return result;
    }
    
    private void growArray() {
        int[] old = array;
        array = allocArray(Math.round(array.length * 1.5f));
        System.arraycopy(old, 0, array, 0, old.length);
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer ("IntList [");
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
                lastAdded = ib.get();
                ib.get(); //1 int pad
                array = new int[ib.limit() - ib.position()];
                ib.get(array);
                assert sequential(array) : "Cache data out of sequence: " + this;
            } finally {
                fis.close();
                ch.close();
                ch = null;
            }
        }
    }
    
    private static boolean sequential (int[] a) {
        int last = Integer.MIN_VALUE;
        for (int i=0; i < a.length; i++) {
            if (a[i] == Integer.MAX_VALUE) {
                break;
            }
            if (a[i] <= last) {
                return false;
            }
            last = a[i];
        }
        return true;
    }
    
    private static final int VERSION = 0;
    /**
     * Save this IntList to a file.
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

            ByteBuffer buf = ByteBuffer.allocate(20 + (4 * used));

            ByteBuffer b1 = ByteBuffer.allocate(20);
            IntBuffer ib = b1.asIntBuffer();
            ib.put(VERSION);
            ib.put(initialCapacity);
            ib.put(used);
            ib.put(lastAdded);
            buf.put(b1);

            b1 = ByteBuffer.allocate(4 * used);
            b1.asIntBuffer().put(array, 0, used);
            buf.put(b1);
            fos.write(buf.array());
            
            fos.flush();
        } finally {
            lock.release();
            fos.close();
        }
    }
    
    public int hashCode() {
        int result = 0;
        for (int i=0; i < used; i++) {
            result ^= (array[i] * 31) + i;
        }
        return result;
    }
    
    public boolean equals (Object o) {
        if (o instanceof IntList) {
            IntList il = (IntList) o;
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
     * Delete an entry from this IntList.
     */
    public synchronized void delete (int index) {
        //XXX this can be made more efficient if we don't mind doing a *lot*
        //of bookkeeping - keep an array of deleted indices and mask things
        //that have been deleted
        if (index < 0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException ("Out of range: " + index);
        }
        HashSet deleted = new HashSet(size());
        deleted.add(new Integer(index));
        compact(deleted);
    }
    
    /**
     * Delete a range of entries from this IntList.
     */
    public synchronized void delete (int first, int last) {
        HashSet deleted = new HashSet(); //XXX inefficient
        for (int i=first; i <= last; i++) {
            deleted.add (new Integer(i));
        }
        compact(deleted);
    }
    
    private void compact(HashSet deleted) {
        if (deleted == null) {
            return;
        }
        int sz = size();
        int[] nue = new int[sz];
        int idx = 0;
        for (int i=0; i < used; i++) {
            if (!deleted.contains(new Integer(i))) {
                nue[idx] = array[i];
                idx++;
            }
        }
        used = sz - deleted.size();
        array = nue;
        lastAdded = sz == 0 ? Integer.MIN_VALUE : array[sz-1];
        deleted = null;
    }
}
