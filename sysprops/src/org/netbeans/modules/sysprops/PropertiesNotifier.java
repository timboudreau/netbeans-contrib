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

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Notifier-Object for all Listeners on the System Properties. 
 *
 * @author Jesse Glick
 */
public class PropertiesNotifier {
  
    /** Set of all Listeners on this Notifier. */
    private static Set listeners = new HashSet ();
    
    
    /** Adds a ChangeListener to this Notifier.
     * 
     * @param listener the listener to add.
     */
    public static void addChangeListener (ChangeListener listener) {
        listeners.add (listener);
    }
    
    /** Removes a ChangeListener to this Notifier.
     * 
     * @param listener the listener to remove.
     */
    public static void removeChangeListener (ChangeListener listener) {
        listeners.remove (listener);
    }
    
    /**
     * Sends a ChangeEvent to all Listeners.
     */
    public static void changed () {
        ChangeEvent ev = new ChangeEvent (PropertiesNotifier.class);
        Iterator it = listeners.iterator ();
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }
}