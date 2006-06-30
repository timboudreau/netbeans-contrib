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

import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import junit.framework.TestCase;
import org.openide.windows.TopComponent;

/**
 * Test the broker
 *
 * @author Petr Kuzel
 */
public class SuggestionsBrokerTest extends TestCase {

    public SuggestionsBrokerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TrackingProvider.installSuggestionProviders();
    }

    public void testLifecycle() {
        SuggestionsBroker broker = SuggestionsBroker.getDefault();
        TestEnv env = new TestEnv();
        broker.env = env;
        SuggestionsBroker.Job job = broker.startBroker(ProviderAcceptor.ALL);
        SuggestionsBroker.Job job2 = broker.startBroker(ProviderAcceptor.ALL);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // wake up
        }
        job2.stopBroker();
        job.stopBroker();

        // broker must not be listening anywhere
        assertTrue(env.tclCount == 0);
        assertTrue(env.dslCount == 0);
    }

    static class TestEnv extends SuggestionsBroker.Env {

        int tclCount = 0;
        int dslCount = 0;

        void addDORegistryListener(ChangeListener cl) {
            tclCount++;
        }

        void addTCRegistryListener(PropertyChangeListener pcl) {
            dslCount++;
        }

        public TopComponent findActiveEditor() {
            return null;
        }

        void removeDORegistryListener(ChangeListener cl) {
            dslCount--;
        }

        void removeTCRegistryListener(PropertyChangeListener pcl) {
            tclCount--;
        }
    }

}
