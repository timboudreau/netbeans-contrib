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
        }
    }
    
    /**
     * Fires a ChangeEvent
     */
    private void fireChange() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent changeEvent = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
    
    /**
     * Removes a listener.
     *
     * @param l the listener that will be removed
     */
    public void removeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Adds a listener. The listener will be notified each time
     * new suggestions were registered or removed from the registry.
     *
     * @param l a listener
     */
    public void addListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }
}
