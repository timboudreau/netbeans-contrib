/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
public class Table extends LinkedHashMap {

    public Table() {
        super();
    }

    public Table(int initialCapacity) {
        super(initialCapacity);
    }

    public synchronized void clear() {
        super.clear();
    }

    public synchronized Object clone() {
        return super.clone();
    }

    public synchronized boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    public synchronized boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    public synchronized Set entrySet() {
        return super.entrySet();
    }

    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }

    public synchronized Object get(Object key) {
        return super.get(key);
    }

    public synchronized int hashCode() {
        return super.hashCode();
    }

    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    public synchronized Set keySet() {
        return super.keySet();
    }

    public synchronized Object put(Object key, Object value) {
        return super.put(key, value);
    }

    public synchronized void putAll(Map m) {
        super.putAll(m);
    }

    public synchronized Object remove(Object key) {
        return super.remove(key);
    }

    public synchronized int size() {
        return super.size();
    }

    public synchronized String toString() {
        return super.toString();
    }

    public synchronized Collection values() {
        return super.values();
    }

    /** @deprecated use keyset().iterator() instead */
    public Enumeration keys() {
        return new Enumeration() {

            private Iterator it = keySet().iterator();

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }
        };
    }
}
