/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erd.wizard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Andrei Badea
 */
public class ChangeSupport {
    
    private Object source;
    private LinkedList listeners = new LinkedList();
    
    public ChangeSupport(Object source) {
        this.source = source;
    }
    
    public synchronized void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void fireChange() {
        fireChange(new ChangeEvent(source));
    }

    public void fireChange(ChangeEvent event) {
        HashSet listenersCopy = null;
        synchronized (this) {
            listenersCopy = new HashSet(listeners);
        }
        for (Iterator i = listenersCopy.iterator(); i.hasNext();) {
            ((ChangeListener)i.next()).stateChanged(event);
        }
    }
}
