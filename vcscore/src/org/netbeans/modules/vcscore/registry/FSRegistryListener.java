/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.util.EventListener;

/**
 * Listener, that can be attached to FSRegistry. It is then called for every
 * added or removed filesystem information.
 *
 * @author  Martin Entlicher
 */
public interface FSRegistryListener extends EventListener {
    
    /**
     * Called when a new filesystem information is added.
     */
    public void fsAdded(FSRegistryEvent ev);
    
    /**
     * Called when a filesystem information is removed.
     */
    public void fsRemoved(FSRegistryEvent ev);
    
}
