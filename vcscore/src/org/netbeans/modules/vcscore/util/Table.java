/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.util.*;

/**
 * Table class implements the Map interface. It guarantees that the order of keys will be the same
 * all the time Table exists. The order is the same in which the pairs were inserted into the table.
 *
 * @author  Martin Entlicher
 */
public class Table extends AbstractMap {

    SimpleSet entries = null;
    /** Creates new Table */
    public Table() {
        entries = new SimpleSet();
    }
    
    public synchronized Set entrySet() {
        return entries;
    }
    
    public synchronized Object put(Object key, Object value) {
        boolean set = false;
        Object old = null;
        for(Iterator it = entries.iterator(); it.hasNext(); ) {
            Entry e = (Entry) it.next();
            if (e.getKey() == key) {
                old = e.getValue();
                e.setValue(value);
                set = true;
                break;
            }
        }
        if (!set) {
            Entry entry = new Entry(key, value);
            entries.add(entry);
        }
        return old;
    }
    
    public synchronized void putFirst(Object key, Object value) {
        Entry entry = new Entry(key, value);
        entries.addFirst(entry);
    }
    
    public synchronized Object remove(Object key) {
        Object old = null;
        int i = 0;
        for(Iterator it = entries.iterator(); it.hasNext(); i++) {
            Entry e = (Entry) it.next();
            if (e.getKey().equals(key)) {
                old = e.getValue();
                entries.remove(i);
                break;
            }
        }
        return old;
    }
    
    public synchronized Enumeration keys() {
        return new EnumKeys();
    }
    
    public synchronized Object get(Object key) {
        Object value = null;
        for(Iterator it = entries.iterator(); it.hasNext(); ) {
            Entry e = (Entry) it.next();
            if (e.getKey() == key) {
                value = e.getValue();
                break;
            }
        }
        return value;
    }

    private class Entry implements Map.Entry {
        private Object key;
        private Object value;
        
        public Entry(Object key) {
            this.key = key;
            this.value = null;
        }
        
        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
        
        public boolean equals(Object obj) {
            return false;
        }
        
        public Object getKey() {
            return key;
        }
        
        public Object getValue() {
            return value;
        }
        
        public Object setValue(Object value) {
            Object old = this.value;
            this.value = value;
            return old;
        }
    }
    
    private class SimpleSet extends AbstractSet {
        private LinkedList list = null;

        public SimpleSet() {
            list = new LinkedList();
        }
        
        public Iterator iterator() {
            return list.iterator();
        }
        
        public boolean add(Object obj) {
            if (list.contains(obj)) return false;
            list.add(obj);
            return true;
        }
        
        public void addFirst(Object obj) {
            list.addFirst(obj);
        }
        
        // Do NOT use this method, it behaves strange !!
        public boolean remove(Object o) {
            System.out.println("Table.SimpleSet.remove("+o+"): list = "+list+"\n, size = "+list.size());
            System.out.println("o = ("+((Entry) o).getKey()+", "+((Entry) o).getValue()+")");
            System.out.println("list.get(0) = ("+((Entry) list.get(0)).getKey()+", "+((Entry) list.get(0)).getValue()+")");
            boolean removed = list.remove(o);
            System.out.println("Table.SimpleSet.removed = "+removed+", size = "+list.size());
            return removed;
        }
        
        public Object remove(int i) {
            return list.remove(i);
        }
        
        public int size() {
            return list.size();
        }
    }
    
    private class EnumKeys implements Enumeration {
        private Iterator iterator;
        
        public EnumKeys() {
            iterator = entries.iterator();
        }
        
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }
        
        public Object nextElement() {
            Entry entry = (Entry) iterator.next();
            if (entry == null) return null;
            return entry.getKey();
        }
    }
}