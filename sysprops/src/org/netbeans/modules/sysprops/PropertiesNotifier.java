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
 *
 * Contributor(s): Jesse Glick
 */

package org.netbeans.modules.sysprops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
    public static synchronized PropertiesNotifier getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new PropertiesNotifier();
        }
        return DEFAULT;
    }
    
    /** Set of all Listeners on this Notifier. */
    private final Set listeners = new HashSet();
    
    /** Adds a ChangeListener to this Notifier.
     *
     * @param listener the listener to add.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    /** Removes a ChangeListener to this Notifier.
     *
     * @param listener the listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Sends a ChangeEvent to all Listeners.
     */
    public void changed() {
        ChangeEvent ev = new ChangeEvent(PropertiesNotifier.class);
        Collection listeners_;
        synchronized (this) {
            listeners_ = new ArrayList(listeners);
        }
        Iterator it = listeners_.iterator();
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
}
