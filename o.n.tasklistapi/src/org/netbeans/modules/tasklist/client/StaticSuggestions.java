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

package org.netbeans.modules.tasklist.client;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A list of "static" suggestions.
 * Those suggestions normally will be updated after a file was
 * written to the hard disk.
 * The class is thread-safe.
 *
 * @author tl
 */
public class StaticSuggestions {
    private static StaticSuggestions instance = new StaticSuggestions();
    
    /**
     * Returns the default registry.
     *
     * @return registry with statis suggestions
     */
    public static StaticSuggestions getDefault() {
        return instance;
    }
            
    private List all = new ArrayList();
    private EventListenerList listenerList = new EventListenerList();
    
    /** 
     * Creates a new instance of StaticSuggestions 
     */
    private StaticSuggestions() {
    }
    
    /**
     * Registers a suggestion
     *
     * @param s a suggestion
     */
    public synchronized void add(Suggestion s) {
        all.add(s);
        fireIntervalAdded(all.size() - 1, all.size() - 1);
    }
    
    /**
     * Removes a suggestion.
     *
     * @param s suggestion to be removed
     */
    public synchronized void remove(Suggestion s) {
        int index = all.indexOf(s);
        if (index >= 0) {
            all.remove(s);
            fireIntervalRemoved(index, index);
        }
    }
    
    /**
     * Returns all registered suggestions.
     *
     * @return all registered suggestions.
     */
    public synchronized Suggestion[] getAll() {
        return (Suggestion[]) all.toArray(new Suggestion[all.size()]);
    }
    
    /**
     * Fires a ListDataEvent
     *
     * @param index0 the one end of the intervall
     * @param index1 the other end of the intervall
     */
    private void fireIntervalAdded(int index0, int index1) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent changeEvent = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ListDataListener)listeners[i+1]).intervalAdded(
                        new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 
                        index0, index1));
            }
        }
    }
    
    /**
     * Fires a ListDataEvent
     *
     * @param index0 the one end of the intervall
     * @param index1 the other end of the intervall
     */
    private void fireIntervalRemoved(int index0, int index1) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent changeEvent = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ListDataListener)listeners[i+1]).intervalAdded(
                        new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 
                        index0, index1));
            }
        }
    }
    
    /**
     * Removes a listener.
     *
     * @param l the listener that will be removed
     */
    public void removeListener(ListDataListener l) {
        this.listenerList.remove(ListDataListener.class, l);
    }
    
    /**
     * Adds a listener. The listener will be notified each time
     * new suggestions were registered or removed from the registry.
     *
     * @param l a listener
     */
    public void addListener(ListDataListener l) {
        this.listenerList.add(ListDataListener.class, l);
    }
}
