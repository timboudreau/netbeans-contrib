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

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.providers.SuggestionProvider;

/**
 * Allows to test providers acceptability for
 * particular task (scanner or broker).
 *
 * @author Petr Kuzel
 */
public interface ProviderAcceptor {

    /** Accepts all providers. */
    public static ProviderAcceptor ALL = new ProviderAcceptor() {
        public boolean accept(SuggestionProvider provider) {
            return true;
        }
    };

    /**
     * Tests whether given provider is acceptable.
     * Results may vary on each call.
     *
     * @return true is proceed with that provider
     */
    boolean accept(SuggestionProvider provider);
}
