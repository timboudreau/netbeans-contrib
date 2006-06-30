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
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import junit.framework.Assert;

/**
 * Looks like real Suggestion provider but has
 * side channels that monitor events from framework.
 *
 * @author Petr Kuzel
 */
public final class TrackingProvider extends SuggestionProvider {

    public static void installSuggestionProviders() {
        InstanceContent content = new InstanceContent();
        content.add(new TrackingProvider());
        content.add(new Lookup.Provider() {
            public Lookup getLookup() {
                return Lookup.getDefault();
            }
        });
        AbstractLookup testLookup = new AbstractLookup(content);
        SuggestionProviders.lookup = testLookup;
    }

    public String getType() {
        return "test";
    }

    static final int PREPARED = 1;
    static final int RUN = 10;
    static final int STOPPED = 30;
    static final int FINISHED = 50;

    private int state = FINISHED;

    public void notifyFinish() {
        if (state != STOPPED && state != PREPARED)  Assert.fail("Unexpected state: " + state);
        state = FINISHED;
    }

    public void notifyPrepare() {
        if (state != FINISHED) Assert.fail("Unexpected state: " + state);
        state = PREPARED;
    }

    public void notifyRun() {
        if (state != PREPARED && state != STOPPED) Assert.fail("Unexpected state: " + state);
        state = RUN;
    }

    public void notifyStop() {
        if (state != RUN) Assert.fail("Unexpected state: " + state);
        state = STOPPED;
    }
}
