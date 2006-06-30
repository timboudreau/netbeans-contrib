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
    private Object propagationId;

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
    
    /**
     * Getter for property propagationId.
     * @return Value of property propagationId.
     */
    public Object getPropagationId() {
        return propagationId;
    }
    
    /**
     * Setter for property propagationId.
     * @param propagationId New value of property propagationId.
     */
    public void setPropagationId(Object propagationId) {
        this.propagationId = propagationId;
    }
    
}
