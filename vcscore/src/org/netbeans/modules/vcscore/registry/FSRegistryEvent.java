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

import java.util.EventObject;

/**
 *
 * @author  Martin Entlicher
 */
public final class FSRegistryEvent extends EventObject {
    
    private FSInfo fsInfo;
    private boolean added;
    
    /**
     * Creates a new instance of FSRegistryEvent.
     * @param registry The filesystem registry, that was changed.
     * @param fsInfo The filesystem info, that was added or removed.
     * @param added <code>true</code> if the info was added,
     *              <code>false</code> if removed.
     */
    public FSRegistryEvent(FSRegistry registry, FSInfo fsInfo, boolean added) {
        super(registry);
        this.fsInfo = fsInfo;
        this.added = added;
    }
    
    /**
     * Get the filesystem info.
     */
    public FSInfo getInfo() {
        return fsInfo;
    }
    
    /**
     * Find whether the filesystem info was added or removed.
     */
    public boolean isAdded() {
        return added;
    }
    
}
