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
 */

package org.netbeans.modules.vcscore.util;

/**
 * This listener is used to listen to the closing of a TopComponent.
 * This is supposed to be a temporary solution till a mechanism for
 * listening on TopComponents closing will be added to the OpenAPI
 *
 * @author  Martin Entlicher
 */
public interface TopComponentCloseListener {
    
    /**
     * Called when the TopComponent is being to close.
     */
    public void closing();

}

