/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.suggestions.ProviderAcceptor;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;

/**
 * Accepts only SourceTasksProvider.
 *
 * @author Petr Kuzel
 */
public class SourceTasksProviderAcceptor implements ProviderAcceptor {
    public boolean accept(SuggestionProvider provider) {
        return provider.getClass() == SourceTaskProvider.class;
    }
}
