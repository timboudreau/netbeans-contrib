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

package org.netbeans.modules.tasklist.core.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 * A list of objects that allows listening for changes
 */
public class ObjectList<E> extends AbstractList<E> {
    /**
     * Owner of an ObjectList
     */
    public static interface Owner {
        /**
         * Returns the ObjectList
         * 
         * @return objects
         */
        public ObjectList getObjectList();
    }
    
    /**
     * This interface could be implemented by elements of an ObjectList.
     */
    public static interface Element {
        /**
         * Returns the ObjectList that contains this element
         *
         * @return ObjectList that contains this element.
         */
        public ObjectList getParentList();
    }
	
    /**
     * An event for changes in an ObjectList
     */
    public static class Event extends EventObject {
        /** a range of items was removed */
        public static final int EVENT_REMOVED = 0;
        
        /** new items added */
        public static final int EVENT_ADDED = 1;
        
        /** the objects changed completely */
        public static final int EVENT_STRUCTURE_CHANGED = 2;
        
        /** objects were just reordered. Not removed and not added. */
        public static final int EVENT_REORDERED = 3;

        private int op;
        private int indices[];
        private Object objects[];
        
        /**
         * Constructor
         *
         * @param source source for this event. Typically an ObjectList
         * @param indices indices of the changed values. May be null if
         *   op == EVENT_REORDERED.
         * @param objects changed values. May be null if
         *   op == EVENT_REORDERED.
         * @param op Operation. One of the EVENT_* constants from this class
         */
        public Event(Object source, int op, int[] indices, Object[] objects) {
            super(source);
            
            assert op == EVENT_ADDED || op == EVENT_REMOVED ||
                op == EVENT_REORDERED || op == EVENT_STRUCTURE_CHANGED : 
                "Wrong operation"; // NOI18N
            
            assert indices != null || op == EVENT_REORDERED;
            assert objects != null || op == EVENT_REORDERED;
            
            this.op = op;
            this.indices = indices;
            this.objects = objects;
        }
        
        /**
         * Returns the type of the event
         *
         * @return one of the EVENT_* constants from this class
         */
        public int getType() {
            return op;
        }
        
        /**
         * Returns changed objects
         * 
         * @return array of changed objects. May be null if
         *   op == EVENT_REORDERED
         */
        public Object[] getObjects() {
            return objects;
        }
        
        /**
         * Returns indices of changed objects
         * 
         * @return indices. May be null if op == EVENT_REORDERED
         */
        public int[] getIndices() {
            return indices;
        }
    }
    
    /**
     * A listener for an ObjectList. Objects from this list could 
     * implement this interface. Such an itemy will not be informed about
     * all changes. It will be informed about changes associated
     * with it.
     */
    public static interface Listener extends EventListener {
        /**
         * A change in the list occured.
         *
         * @param e an event
         */
        public void listChanged(Event e);
    }
    
    private EventListenerList listeners = null;
    
    /** internal representation */
    private List<E> objects = null;
    
    /**
     * Creates a new instance of ObjectList
     */
    public ObjectList() {
    }
    
    /**
     * Returns the owner of this list
     * 
     * @return owner or null
     */
    public ObjectList.Owner getOwner() {
    	return null;
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
            fireEvent(new Event(this, Event.EVENT_REMOVED, 
                new int[] {index}, new Object[]{old}));
            fireEvent(new Event(this, Event.EVENT_ADDED, 
                new int[] {index}, new Object[]{obj}));
        }
        return old;
    }
    
    /**
     * Adds a listener to this list
     *
     * @param listener a listener
     */
    public void addListener(Listener listener) {
        if (listeners == null)
            listeners = new EventListenerList();
        listeners.add(Listener.class, listener);
    }
    
    /**
     * Removes a listener
     *
     * @param listener a listener
     */
    public void removeListener(Listener listener) {
        if (listeners != null)
            listeners.remove(Listener.class, listener);
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
    protected void fireEvent(Event e) {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = l.length - 2; i >= 0; i -= 2) {
            // Lazily create the event:
            ((Listener) l[i+1]).listChanged(e);
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
        
        if (hasListeners() || element instanceof Listener) {
            Event e = new Event(this, Event.EVENT_ADDED, new int[] {index},
                new Object[] {element});
            if (hasListeners())
                fireEvent(e);
            if (element instanceof Listener)
                ((Listener) element).listChanged(e);
        }
    }
    
    public E remove(int index) {
	if (objects == null)
            throw new IndexOutOfBoundsException("Empty list"); // NOI18N
        E obj = objects.remove(index);
        if (hasListeners() || obj instanceof Listener) {
            Event e = new Event(this, Event.EVENT_REMOVED, 
                new int[] {index}, new Object[] {obj});
            if (hasListeners())
                fireEvent(e);
            if (obj instanceof Listener)
                ((Listener) obj).listChanged(e);
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
            Event e = new Event(this, Event.EVENT_REORDERED, null, null);
            fireEvent(e);
        }
    }
}
