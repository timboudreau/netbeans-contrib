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

package org.netbeans.modules.tasklist.usertasks.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 * A list of objects that allows listening for changes.
 * 
 * @author tl
 */
public class ObjectList<E> extends AbstractList<E> {    
    private EventListenerList listeners = null;
    
    /** internal representation */
    private List<E> objects = null;
    
    /**
     * Creates a new instance of ObjectList with an ArrayList as underlying 
     * implementation class.
     */
    public ObjectList() {
    }
    
    /**
     * Creates a new instance of ObjectList.
     * 
     * @param objects observed list
     */
    public ObjectList(List<E> objects) {
        this.objects = objects;
    }
    
    public int size() {
        int sz;
        if (objects == null)
            sz = 0;
        else 
            sz = objects.size();
        return sz;
    }
    
    public E set(int index, E obj) {
        if (objects == null)
            objects = new ArrayList<E>();
        
        E old = objects.set(index, obj);
        if (hasListeners()) {
            fireEvent(new ObjectListEvent(this, ObjectListEvent.EVENT_REMOVED, 
                new int[] {index}, new Object[]{old}));
            fireEvent(new ObjectListEvent(this, ObjectListEvent.EVENT_ADDED, 
                new int[] {index}, new Object[]{obj}));
        }
        return old;
    }
    
    /**
     * Adds a listener to this list
     *
     * @param listener a listener
     */
    public void addListener(ObjectListListener listener) {
        if (listeners == null)
            listeners = new EventListenerList();
        listeners.add(ObjectListListener.class, listener);
    }
    
    /**
     * Removes a listener
     *
     * @param listener a listener
     */
    public void removeListener(ObjectListListener listener) {
        if (listeners != null)
            listeners.remove(ObjectListListener.class, listener);
        if (listeners.getListenerCount() == 0)
            this.listeners = null;
    }
    
    /**
     * Are there any listeners?
     * 
     * @return true if the number of listeners > 0
     */
    protected boolean hasListeners() {
        return listeners != null && listeners.getListenerCount() != 0;
    }
    
    /**
     * Fires an event
     *
     * @param e an event
     */
    protected void fireEvent(ObjectListEvent e) {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = l.length - 2; i >= 0; i -= 2) {
            // Lazily create the event:
            ((ObjectListListener) l[i+1]).listChanged(e);
        }
    }
    
    public E get(int index) {
        if (objects == null)
            throw new IndexOutOfBoundsException("Empty list"); // NOI18N
        
        return objects.get(index);
    }
    
    @Override
    public void add(int index, E element) {
	if (objects == null)
            objects = new ArrayList();
        
        objects.add(index, element);
        
        if (hasListeners() || element instanceof ObjectListListener) {
            ObjectListEvent e = new ObjectListEvent(this, 
                    ObjectListEvent.EVENT_ADDED, new int[] {index},
                    new Object[] {element});
            if (hasListeners())
                fireEvent(e);
            if (element instanceof ObjectListListener)
                ((ObjectListListener) element).listChanged(e);
        }
    }
    
    public E remove(int index) {
	if (objects == null)
            throw new IndexOutOfBoundsException("Empty list"); // NOI18N
        E obj = objects.remove(index);
        if (hasListeners() || obj instanceof ObjectListListener) {
            ObjectListEvent e = new ObjectListEvent(this, 
                    ObjectListEvent.EVENT_REMOVED, 
                    new int[] {index}, new Object[] {obj});
            if (hasListeners())
                fireEvent(e);
            if (obj instanceof ObjectListListener)
                ((ObjectListListener) obj).listChanged(e);
        }
        if (objects.size() == 0)
            objects = null;
        return obj;
    }
    
    /**
     * Moves an element
     *
     * @param index old index of the element
     * @param newIndex new index of the element
     */
    public void move(int index, int newIndex) {
	if (objects == null)
            throw new IndexOutOfBoundsException("Empty list"); // NOI18N
        objects.add(newIndex, objects.remove(index));
        if (hasListeners()) {
            ObjectListEvent e = new ObjectListEvent(this, 
                    ObjectListEvent.EVENT_REORDERED, null, null);
            fireEvent(e);
        }
    }
}
