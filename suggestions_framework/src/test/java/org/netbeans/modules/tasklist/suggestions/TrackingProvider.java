/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
