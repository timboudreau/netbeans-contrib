/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import java.util.EventObject;

/**
 *
 * @author  Martin Entlicher
 */
public class VersioningRepositoryEvent extends EventObject {

    private VersioningRepository vr;
    private VersioningSystem vfs;
    private boolean added;
    
    /** Creates new VersioningRepositoryEvent */
    public VersioningRepositoryEvent(VersioningRepository vr, VersioningSystem vfs, boolean added) {
        super(vr);
        this.vr = vr;
        this.vfs = vfs;
        this.added = added;
    }
    
    public VersioningRepository getRepository() {
        return vr;
    }
    
    public VersioningSystem getVersioningSystem() {
        return vfs;
    }
    
    public boolean isAdded() {
        return added;
    }

}
