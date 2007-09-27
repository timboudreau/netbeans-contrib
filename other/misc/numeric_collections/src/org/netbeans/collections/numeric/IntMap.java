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
 * IntMap.java
 *
 * Created on March 29, 2004, 6:40 PM
 */

package org.netbeans.collections.numeric;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Sparse array integer keyed map.  Similar to a standard Collections map,
 * but considerably more efficient for this purpose, it simply an array
 * if integer indices that have values and an array of objects mapped to
 * those indices.
 * <p>
 * It is preferable to add entries in ascending order if at all possible;
 * otherwise the first call to a method that reads data, after a put has been
 * performed will force the entire map to be sorted.
 * <p>
 * Supports secondary mapping of objects, such that it is possible to construct
 * trees of IntMaps (IntMaps with IntMap values) which also contain user data
 * of some sort.
 * <p>
 * Note, this class is generally designed to be populated and used;  adding
 * and removing multiple elements is relatively efficient, but sequences of
 * add, read, remove, read, remove, read, add, read will not be fast - internally
 * the map depends for performance on its contents being sorted - so each read
 * after a modification will force a re-sorting.
 * 
 *
 * @author  Tim Boudreau
 */
public final class IntMap {
    private int[] keys = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        
    private Object[] vals = new Object[5];
    private int last = -1;
    
    /** Creates a new instance of IntMap */
    public IntMap() {
    }
    
    /**
     * Get the first key in this map.
     */
    public int first() {
        checkSort();
        return isEmpty() ? -1 : keys[0];
    }
    
    public boolean containsKey (int key) {
        checkSort();
        int result = Arrays.binarySearch(keys, key);
        return result > -1;
    }
    
    /**
     * An iterator-like class.  If you need to read a series of consecutive
     * values from a map, call iter() or nearestIter().
     */
    public static final class Iter {
        private int pos;
        private final IntMap map;
        Iter (final IntMap map, final int startIdx) {
            pos = startIdx;
            this.map = map;
        }
        
        public boolean hasNext() {
            return pos <= map.last;
        }
        
        public boolean hasPrev() {
            return pos >= 0;
        }
        
        public int prev() {
            pos--;
            int result = map.keys[pos];
            return result;
        }
        
        public int next() {
            int result = map.keys[pos];
            pos++;
            return result;
        }
        
        public Object current() {
            return map.vals[pos];
        }
    }
    
    public final Iter iter() {
        checkSort();
        return new Iter(this, 0);
    }
    
    /**
     * Get an iterator anchored on the closest value to the passed integer,
     * @param key The desired value the Iter will be anchored on
     * @param below If true, will find the nearest value to key which is <= key.
     */
    public final Iter nearestIter (int key, boolean below) {
        checkSort();
        int index = -1;
        if (isEmpty()) {
            return iter();
        } else if (last == 0) {
            return new Iter(this, last);
        }
        if (key < keys[0]) {
            return below ? new Iter(this, last) : iter();
        }
        if (key > keys[last]) {
            return below ? new Iter(this, last) : iter();
        }
        int idx = Arrays.binarySearch (keys, key);
        if (idx < 0 && last > 0) {
            for (int i=1; i <= last; i++) {
                if (keys[i-1] < key && keys[i] > key) {
                    idx = i;
                    break;
                }
            }
            return below ? new Iter(this, idx-1) : new Iter(this, idx);
        } else {
            if (below) {
                idx = idx == 0 ? last : idx - 1;
            } else {
                idx = idx == last ? 0 : idx + 1;
            }
            return new Iter(this, idx);
        }
    }
    
    /**
     * Find the key nearest to this value.  Wraps around the highest entry
     * to the lowest.
     * @param key A value which is near a key in the map
     * @param below If true, will return the nearest stored value less than
     *  the passed key
     */
    public int nearest (int key, boolean below) {
        checkSort();
        if (isEmpty()) {
            return -1;
        }
        if (last == 0) {
            return keys[last];
        }
        if (key < keys[0]) {
            return below ? keys[last] : keys[0];
        }
        if (key > keys[last]) {
            return below ? keys[last] : keys[0];
        }
        int idx = Arrays.binarySearch (keys, key);
        if (idx < 0 && last > 0) {
            for (int i=1; i <= last; i++) {
                if (keys[i-1] < key && keys[i] > key) {
                    idx = i;
                    break;
                }
            }
            return below ? keys[idx-1] : keys[idx];
        } else {
            if (below) {
                idx = idx == 0 ? last : idx - 1;
            } else {
                idx = idx == last ? 0 : idx + 1;
            }
            return keys[idx];
        }
    }

    /**
     * Get all of the integer keys in this map
     */
    public int[] getKeys () {
        if (last == -1) {
            return new int[0];
        }
        if (last == keys.length -1) {
            growArrays();
        }
        int[] result = new int[last+1];
        System.arraycopy (keys, 0, result, 0, last+1);
        return result;
    }

    /** Some temporary diagnostics re issue 48608 */
    private static String i2s (int[] arr) {
        StringBuffer sb = new StringBuffer(arr.length * 3);
        sb.append ('[');
        for (int i=0; i < arr.length; i++) {
            if (arr[i] != Integer.MAX_VALUE) {
                sb.append (arr[i]);
                sb.append (',');
            }
        }
        sb.append (']');
        return sb.toString();
    }
    
    /**
     * Get the object associated with this integer.
     */
    public Object get (int key) {
        checkSort();
        int idx = Arrays.binarySearch (keys, key);
        if (idx > -1 && idx <= last) {
            return vals[idx];
        }
        return null;
    }
    
    /**
     * Add an object to this map.  The integer key must be larger than
     * the last key in this map.
     */
    public void put (int key, Object val) {
        doPut (key, val);
    }
    
    private boolean needsSort = false;

    private int doPut (int key, Object val) {
        if (last > 0) {
            if (key <= keys[last]) {
//                throw new IllegalArgumentException ("Keys must be sorted!");
                needsSort = true;
            }
        }
        if (last == keys.length - 1) {
            growArrays();
        }
        if (last != -1 && keys[last] == key) {
            throw new IllegalArgumentException ("Duplicate key " + key + " already set to " + vals[last] + " attempt to set it to " + val);
        }
        last++;
        keys[last] = key;
        vals[last] = val;
        return last;
    }
    
    /** Put an object keyed to an int into this map, and also associate a
     * secondary object with that key.  Allows building a tree of IntMaps,
     * where each IntMap's values are other IntMaps, but some outside data
     * needs to be associated with the map.
     */
    public void put (int key, Object val, Object secondaryVal) {
        doPut (key, val);
        setSecondaryValue (key, secondaryVal);
    }

    /** Get the secondary object associated with an int key */
    public Object getSecondaryValue (int key) {
        return secondaryValues == null ? null : secondaryValues.get(key);
    }
    
    /** Set the secondary object associated with an int key */
    public void setSecondaryValue (int key, Object o) {
        if (secondaryValues == null) {
            secondaryValues = new IntMap();
        }
        secondaryValues.put (key, o);
    }
    
    private IntMap secondaryValues = null;
    
    private void growArrays() {
        int newSize = last * 2;
        int[] newKeys = new int[newSize];
        Object[] newVals = new Object[newSize];
        Arrays.fill (newKeys, Integer.MAX_VALUE); //So binarySearch works
        System.arraycopy (keys, 0, newKeys, 0, keys.length);
        System.arraycopy (vals, 0, newVals, 0, vals.length);
        keys = newKeys;
        vals = newVals;
    }
    
    /**
     * Get the key which follows the passed key, or -1.  Will wrap around 0.
     */
    public int nextEntry (int entry) {
        checkSort();
        int result = -1;
        if (!isEmpty()) {
            int idx = Arrays.binarySearch (keys, entry);
            if (idx >= 0) {
                result = idx == keys.length -1 ? keys[0] : keys[idx+1];
            }
        }
        return result;
    }
    
    /**
     * Get the key which precedes the passed key, or -1.  Will wrap around 0.
     */
    public int prevEntry (int entry) {
        checkSort();
        int result = -1;
        if (!isEmpty()) {
            int idx = Arrays.binarySearch (keys, entry);
            if (idx >= 0) {
                result = idx == 0 -1 ? keys[keys.length-1] : keys[idx-1];
            }
        }
        return result;
    }
    
    
    public boolean isEmpty() {
        return last == -1;
    }
    
    public int size() {
        return last + 1;
    }
    
    public String toString() {
        checkSort();
        StringBuffer sb = new StringBuffer("IntMap@" + 
            System.identityHashCode(this) + "\n"); //NOI18N
        
        for (int i=0; i < size(); i++) {
            sb.append (" ["); //NOI18N
            sb.append (Integer.toHexString(keys[i]));
            sb.append (":"); //NOI18N
            sb.append (vals[i]);
            sb.append ("]\n"); //NOI18N
        }
        if (size() == 0) {
            sb.append (" empty"); //NOI18N
        }
        return sb.toString();
    }
    
    private static final Object REMOVED = new Object();
    private boolean removeDirty = false;
    
    public void remove (int key) {
        if (!removeDirty) {
            checkSort();
        }
        int idx = Arrays.binarySearch (keys, key);
        if (idx > -1) {
            vals[idx] = REMOVED;
            removeDirty = true;
            if (secondaryValues != null && secondaryValues.containsKey(key)) {
                secondaryValues.remove(key);
            }
        }
    }
    
    private void checkSort() {
        if (needsSort) {
            sort();
            needsSort = false;
        }
        if (removeDirty) {
            remove();
            removeDirty = false;
        }
    }
    
    private void remove() {
        int remcount = 0;
        for (int i=0; i <= last; i++) {
            boolean offEnd = i + remcount > last;
            
            Object val = offEnd ? null : vals[i + remcount];
            int key = keys[i];
            boolean rem = val == REMOVED;
            while (val == REMOVED) {
                System.err.println("FOUND ONE AT " + i + " key " + key);
                remcount++;
                val = vals[i + remcount];
                key = keys[i + remcount];
            }
            if (remcount > 0) {
                offEnd = i + remcount > last;
                System.err.println("Set values from " + (i + remcount) + " in " + i);
                keys[i] = offEnd ? Integer.MAX_VALUE : keys[i + remcount];
                vals[i] = offEnd ? null : vals[i + remcount];
            }
        }
        last -= remcount;
        for (int i=0; i < remcount && i < keys.length-1; i++) {
            vals[last + i + 1] = null;
            keys[last + i + 1] = Integer.MAX_VALUE;
        }
    }
    
    private void sort() {
        E[] e = new E[last+1];
        for (int i=0; i <= last; i++) {
            e[i] = new E(keys[i], vals[i]);
        }
        Arrays.sort (e);
        for (int i=0; i < last; i++) {
            keys[i] = e[i].key;
            vals[i] = e[i].val;
        }
    }
    
    private static final class E implements Comparable {
        public int key;
        public Object val;
        public E (int key, Object val) {
            this.key = key;
            this.val = val;
        }

        public int compareTo(Object o) {
            return key - ((E) o).key;
        }
    }    
}
