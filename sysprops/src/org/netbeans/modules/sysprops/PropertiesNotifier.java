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
 *
 * Contributor(s): Jesse Glick
 */
package org.netbeans.modules.sysprops;

import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Notifier-Object for all Listeners on the System Properties. 
 *
 * @author Jesse Glick
 */
public class PropertiesNotifier {

    /** Default instance. */
    private static PropertiesNotifier DEFAULT = null;
    /** Get default instance of the notifier.
     * @return the default instance
     */
    public static synchronized PropertiesNotifier getDefault () {
        if (DEFAULT == null)
            DEFAULT = new PropertiesNotifier ();
        return DEFAULT;
    }
    
    /** Set of all Listeners on this Notifier. */
    private Set listeners = new HashSet ();
    
    
    /** Adds a ChangeListener to this Notifier.
     * 
     * @param listener the listener to add.
     */
    public synchronized void addChangeListener (ChangeListener listener) {
        listeners.add (listener);
    }
    
    /** Removes a ChangeListener to this Notifier.
     * 
     * @param listener the listener to remove.
     */
    public synchronized void removeChangeListener (ChangeListener listener) {
        listeners.remove (listener);
    }
    
    /**
     * Sends a ChangeEvent to all Listeners.
     */
    public void changed () {
        ChangeEvent ev = new ChangeEvent (PropertiesNotifier.class);
        Collection listeners_;
        synchronized (this) {
            listeners_ = new ArrayList (listeners);
        }
        Iterator it = listeners_.iterator ();
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }
}
