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

import junit.framework.*;

import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;

import org.openide.windows.TopComponent;

/**
 * Test the broker
 *
 * @author Petr Kuzel
 */
public class SuggestionsBrokerTest extends TestCase {

    public SuggestionsBrokerTest(java.lang.String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SuggestionsBrokerTest.class);
        return suite;
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
