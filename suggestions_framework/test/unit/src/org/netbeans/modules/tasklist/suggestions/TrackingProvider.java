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

    public String[] getTypes() {
        return new String[] {"test"};
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
