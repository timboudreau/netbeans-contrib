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
