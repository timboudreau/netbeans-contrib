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

package org.netbeans.apihole.tasklist;

import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 * Friendly implementation entry point.
 *
 * @author Petr Kuzel
 * @since 1.4
 */
public abstract class SPIHole {

    /** Creates suggestion context backed by dataobject or <code>null</code> */
    public static SuggestionContext createSuggestionContext(DataObject dobj) {
        SPIHole hole = (SPIHole) Lookup.getDefault().lookup(SPIHole.class);
        assert hole != null : "SPIHole Lookup registration failure!";  // NOI18N
        return hole.createSuggestionContextImpl(dobj);
    }

    protected abstract SuggestionContext createSuggestionContextImpl(DataObject dobj);
}
