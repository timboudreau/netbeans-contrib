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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.persistence.util;

import  java.util.*;

/**
 * Maps long keys to long values
 * The code is mostly the same as in the standard java.util.HashMap
 * 
 * @author Vladimir Kvashin
 * @see HashMap
 */

public class LongLongHashMap//<K> // K is long 
    //extends AbstractMap<K>
    //implements Map<K>, Cloneable, Serializable
    // unfortunately it's no way to use built-in types in Java gener
{

    public static final long NO_VALUE = Long.MIN_VALUE;
    
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     **/
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry[] table;

    /**
     * The number of key-value mappings contained in this identity hash map.
     */
    transient int size;
  
    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    int threshold;
  
    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;

    /**
	 * The number of times this LongHashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the LongHashMap or otherwise modify its internal structure (e.g.,
	 * rehash).  This field is used to make iterators on Collection-views of
	 * the LongHashMap fail-fast.  (See ConcurrentModificationException).
	 */
    transient volatile int modCount;

    /**
	 * Constructs an empty <tt>LongHashMap</tt> with the specified initial
	 * capacity and load factor.
	 * 
	 * @param initialCapacity The initial capacity.
	 * @param loadFactor      The load factor.
	 * @throws IllegalArgumentException if the initial capacity is negative
	 *         or the load factor is nonpositive.
	 */
    public LongLongHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + // NOI18N
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + // NOI18N
                                               loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) 
            capacity <<= 1;
    
        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = new Entry[capacity];
        init();
    }
  
    /**
	 * Constructs an empty <tt>LongHashMap</tt> with the specified initial
	 * capacity and the default load factor (0.75).
	 * 
	 * @param initialCapacity the initial capacity.
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
    public LongLongHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
	 * Constructs an empty <tt>LongHashMap</tt> with the default initial capacity
	 * (16) and the default load factor (0.75).
	 */
    public LongLongHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    // internal utilities

    /**
	 * Initialization hook for subclasses. This method is called
	 * in all constructors and pseudo-constructors (clone, readObject)
	 * after LongHashMap has been initialized but before any entries have
	 * been inserted.  (In the absence of this method, readObject would
	 * require explicit knowledge of subclasses.)
	 */
    void init() {
    }

    /**
     * Whether to prefer the old supplemental hash function, for
     * compatibility with broken applications that rely on the
     * internal hashing order.
     *
     * Set to true only by hotspot when invoked via
     * -XX:+UseNewHashFunction or -XX:+AggressiveOpts
     */
    private static final boolean useNewHash;
    static { useNewHash = false; }

    private static int oldHash(int h) {
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    private static int newHash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because LongHashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits.
     */
    static int hash(int h) {
	return useNewHash ? newHash(h) : oldHash(h);
    }

    static int hash(long key) {
	return (int) (key & 0x00000000FFFFFFFF);
    }

    /**
     * Returns index for hash code h. 
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }
 
    /**
     * Returns the number of key-value mappings in this map.
     * @return the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }
  
    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped in this identity
     * hash map, or <tt>null</tt> if the map contains no mapping for this key.
     * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
     * that the map contains no mapping for the key; it is also possible that
     * the map explicitly maps the key to <tt>null</tt>. The
     * <tt>containsKey</tt> method may be used to distinguish these two cases.
     *
     * @param   key the key whose associated value is to be returned.
     * @return  the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key.
     * @see #put(long, long)
     */
    public long get(long key) {
        int hash = hash(key);
        for (Entry e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            long k;
            if (e.key == key )
                return e.value;
        }
        return NO_VALUE;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(long key) {
        long k = key;
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        Entry e = table[i]; 
        while (e != null) {
            if (/*e.hash == hash &&*/ k == e.key) 
                return true;
            e = e.next;
        }
        return false;
    }

    /**
	 * Returns the entry associated with the specified key in the
	 * LongHashMap.  Returns null if the LongHashMap contains no mapping
	 * for this key.
	 */
    public Entry getEntry(long key) {
        long k = key;
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        Entry e = table[i]; 
        while (e != null && !(/*e.hash == hash &&*/ k == e.key))
            e = e.next;
        return e;
    }
  
    /**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @return previous value associated with specified key, or <tt>null</tt>
	 * 	       if there was no mapping for key.  A <tt>null</tt> return can
	 * 	       also indicate that the LongHashMap previously associated
	 * 	       <tt>null</tt> with the specified key.
	 */
    public long put(long key, long value) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry e = table[i]; e != null; e = e.next) {
            //if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
	    if (e.key == key) {
                long oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return NO_VALUE;
    }

    /**
     * This method is used instead of put by constructors and
     * pseudoconstructors (clone, readObject).  It does not resize the table,
     * check for comodification, etc.  It calls createEntry rather than
     * addEntry.
     */
//    private void putForCreate(K key, long value) {
//        K k = maskNull(key);
//        int hash = hash(k.hashCode());
//        int i = indexFor(hash, table.length);
//
//        /**
//         * Look for preexisting entry for key.  This will never happen for
//         * clone or deserialize.  It will only happen for construction if the
//         * input Map is a sorted map whose ordering is inconsistent w/ equals.
//         */
//        for (Entry e = table[i]; e != null; e = e.next) {
//            if (e.hash == hash && eq(k, e.key)) {
//                e.value = value;
//                return;
//            }
//        }
//
//        createEntry(hash, k, value, i);
//    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two;
     *        must be greater than current capacity unless current
     *        capacity is MAXIMUM_CAPACITY (in which case value
     *        is irrelevant).
     */
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    /** 
     * Transfer all entries from current table to newTable.
     */
    void transfer(Entry[] newTable) {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e.next;
                    int i = indexFor(e.hash, newCapacity);  
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

 
    public long remove(long key) {
        Entry e = removeEntryForKey(key);
        return (e == null ? NO_VALUE : e.value);
    }

    /**
	 * Removes and returns the entry associated with the specified key
	 * in the LongHashMap.  Returns null if the LongHashMap contains no mapping
	 * for this key.
	 */
    Entry removeEntryForKey(long key) {
        long k = key;
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        Entry prev = table[i];
        Entry e = prev;

        while (e != null) {
            Entry next = e.next;
            if (e.key == key) {
                modCount++;
                size--;
                if (prev == e) 
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
   
        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    Entry removeMapping(Object o) {
        if (!(o instanceof Map.Entry))
            return null;

        LongLongHashMap.Entry entry = (LongLongHashMap.Entry) o;
        long k = entry.getKey();
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        Entry prev = table[i];
        Entry e = prev;

        while (e != null) {
            Entry next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e) 
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
   
        return e;
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        modCount++;
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) 
            tab[i] = null;
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(long value) {
	Entry[] tab = table;
        for (int i = 0; i < tab.length ; i++)
            for (Entry e = tab[i] ; e != null ; e = e.next)
                if (value == e.value)
                    return true;
	return false;
    }

    public static class Entry /*implements Map.Entry*/ {
        final long key;
        long value;
        final int hash;
        Entry next;

        /**
         * Create new entry.
         */
        Entry(int h, long k, long v, Entry n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public long getKey() {
            return key;
        }

        public long getValue() {
            return value;
        }
    
        public long setValue(long newValue) {
	    long oldValue = value;
            value = newValue;
            return oldValue;
        }
    
	@Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry e = (Entry)o;
            long k1 = getKey();
            long k2 = e.getKey();
            if (k1 == k2) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2))) 
                    return true;
            }
            return false;
        }
    
	@Override
        public int hashCode() {
	    return (int)(value ^ (value >>> 32));
        }
    
	@Override
        public String toString() {
            return getKey() + "=" + getValue(); // NOI18N
        }

        /**
		 * This method is invoked whenever the value in an entry is
		 * overwritten by an invocation of put(k,v) for a key k that's already
		 * in the LongHashMap.
		 */
        void recordAccess(LongLongHashMap m) {
        }

        /**
         * This method is invoked whenever the entry is
         * removed from the table.
         */
        void recordRemoval(LongLongHashMap m) {
        }
    }

    /**
     * Add a new entry with the specified key, value and hash code to
     * the specified bucket.  It is the responsibility of this 
     * method to resize the table if appropriate.
     *
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(int hash, long key, long value, int bucketIndex) {
	Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    /**
	 * Like addEntry except that this version is used when creating entries
	 * as part of Map construction or "pseudo-construction" (cloning,
	 * deserialization).  This version needn't worry about resizing the table.
	 * 
	 * Subclass overrides this to alter the behavior of LongHashMap(Map),
	 * clone, and readObject.
	 */
    void createEntry(int hash, long key, long value, int bucketIndex) {
	Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E> {
        Entry next;	// next entry to return
        int expectedModCount;	// For fast-fail 
        int index;		// current slot 
        Entry current;	// current entry

	@SuppressWarnings("empty-statement")
        HashIterator() {
            expectedModCount = modCount;
            Entry[] t = table;
            int i = t.length;
            Entry n = null;
            if (size != 0) { // advance to first entry
                while (i > 0 && (n = t[--i]) == null)
                    ;
            }
            next = n;
            index = i;
        }

        public boolean hasNext() {
            return next != null;
        }

        Entry nextEntry() { 
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry e = next;
            if (e == null) 
                throw new NoSuchElementException();
                
            Entry n = e.next;
            Entry[] t = table;
            int i = index;
            while (n == null && i > 0)
                n = t[--i];
            index = i;
            next = n;
            return current = e;
        }

        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            long k = current.key;
            current = null;
            LongLongHashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }


    private class EntryIterator extends HashIterator<LongLongHashMap.Entry> {
        public LongLongHashMap.Entry next() {
            return nextEntry();
        }
    }

    private Iterator<LongLongHashMap.Entry> newEntryIterator()   {
        return new EntryIterator();
    }
    
    private class KeyIterator implements LongIterator {

	private EntryIterator delegate = new EntryIterator();
	
	public boolean hasNext() {
	    return delegate.hasNext();
	}

	public long next() {
	    Entry next = delegate.next;
	    return (next == null) ? NO_VALUE : next.value;
	}
	
    }

    public LongIterator keyIterator() {
	return new KeyIterator();
    }

    // Views

    private transient Set<LongLongHashMap.Entry> entrySet = null;

//    /**
//     * Each of these fields are initialized to contain an instance of the
//     * appropriate view the first time this view is requested.  The views are
//     * stateless, so there's no reason to create more than one of each.
//     */
//    transient volatile Set<K>        keySet = null;
//
//    /**
//     * Returns a set view of the keys contained in this map.  The set is
//     * backed by the map, so changes to the map are reflected in the set, and
//     * vice-versa.  The set supports element removal, which removes the
//     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
//     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
//     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
//     * <tt>addAll</tt> operations.
//     *
//     * @return a set view of the keys contained in this map.
//     */
//    public Set<K> keySet() {
//        Set<K> ks = keySet;
//        return (ks != null ? ks : (keySet = new KeySet()));
//    }
//
//    private class KeySet extends AbstractSet<K> {
//        public Iterator<K> iterator() {
//            return newKeyIterator();
//        }
//        public int size() {
//            return size;
//        }
//        public boolean contains(Object o) {
//            return containsKey(o);
//        }
//        public boolean remove(Object o) {
//            return LongLongHashMap.this.removeEntryForKey(o) != null;
//        }
//        public void clear() {
//            LongLongHashMap.this.clear();
//        }
//    }

    /**
     * Returns a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this map.
     * @see Map.Entry
     */
    public Set<LongLongHashMap.Entry> entrySet() {
        Set<LongLongHashMap.Entry> es = entrySet;
        return (es != null ? es : (entrySet = (Set<LongLongHashMap.Entry>) (Set) new EntrySet()));
    }

    private class EntrySet extends AbstractSet/*<Map.Entry>*/ {
        public Iterator/*<Map.Entry>*/ iterator() {
            return newEntryIterator();
        }
	@Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            LongLongHashMap.Entry e = (LongLongHashMap.Entry) o;
            Entry candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
	
	@Override
        public boolean remove(Object o) {
            return removeMapping(o) != null;
        }
	
        public int size() {
            return size;
        }
	@Override
        public void clear() {
            LongLongHashMap.this.clear();
        }
    }


    // These methods are used when serializing HashSets
    int   capacity()     { return table.length; }
    float loadFactor()   { return loadFactor;   }
}
