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

package org.netbeans.modules.tasklist.providers;

import org.netbeans.apihole.tasklist.SPIHole;
import org.openide.loaders.DataObject;

/**
 * Creates SuggestionContext. It's accessed using
 * (@link SPIHole} class.
 *
 * @author Petr Kuzel
 * @since 1.4
 */
final class SPIHoleImpl extends SPIHole {

    private SPIHoleImpl() {
    }

    public static SPIHole layerEntryPoint() {
        // called from layer via reflection (InstanceSupport)
        return new SPIHoleImpl();
    }

    protected SuggestionContext createSuggestionContextImpl(DataObject dobj) {
        return new SuggestionContext(dobj);
    }
}
