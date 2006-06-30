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
