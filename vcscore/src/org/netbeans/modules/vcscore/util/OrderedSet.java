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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import org.openide.util.enum.AlterEnumeration;
import org.openide.util.enum.QueueEnumeration;
import org.openide.util.enum.RemoveDuplicatesEnumeration;
import org.openide.util.enum.SequenceEnumeration;

/**
 *
 * @author  Martin Entlicher
 *
 * The set, that keeps the order of elements in the same order they were added.
 * Only add of collections of elements is supported in this set.
 */
public final class OrderedSet extends AbstractSet {

    /** Queue of collections of elements. */
    private QueueEnumeration queue = new QueueEnumeration();
    /** Objects stored in this set. */
    Object[] objects = null;

    /** Creates new OrderedSet */
    public OrderedSet() {
    }
    
    public boolean add(Object obj) {
        queue.put(Collections.singleton(obj));
        return true;
    }

    /**
     * Adds all of the elements in the specified collection to this collection.
     */
    public boolean addAll(Collection coll) {
        queue.put(coll);
        return true;
    }
    
    
    private Object[] getObjects() {
        if (objects == null) {
            AlterEnumeration altered = new AlterEnumeration(queue) {
                public Object alter(Object obj) {
                    return Collections.enumeration((Collection) obj);
                }
            };
            SequenceEnumeration sequenced = new SequenceEnumeration(altered);
            Enumeration result = new RemoveDuplicatesEnumeration(sequenced);
            ArrayList objectList = new ArrayList();
            for (int i = 0; result.hasMoreElements(); i++) {
                objectList.add(result.nextElement());
            }
            objects = objectList.toArray();
        }
        return objects;
    }
    
    /**
     * Returns an iterator over the elements contained in this collection.
     */
    public Iterator iterator() {
        final int size = getObjects().length;
        return new Iterator() {
            int i = 0;
            public boolean hasNext() {
                return i < size;
            }
            
            public Object next() {
                return objects[i++];
            }
            
            public void remove() {
                throw new UnsupportedOperationException("Remove is not supported.");
            }
        };
    }
    
    /**
     * Returns the number of elements in this collection.
     */
    public int size() {
        return getObjects().length;
    }
}
