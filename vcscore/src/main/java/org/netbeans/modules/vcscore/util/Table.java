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
