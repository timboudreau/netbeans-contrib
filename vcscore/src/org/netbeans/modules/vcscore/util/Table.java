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
