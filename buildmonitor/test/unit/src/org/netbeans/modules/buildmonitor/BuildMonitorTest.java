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

package org.netbeans.modules.buildmonitor;

import java.net.URL;
import java.util.prefs.Preferences;
import org.netbeans.junit.NbTestCase;

public class BuildMonitorTest extends NbTestCase {

    public BuildMonitorTest(String name) {
        super(name);
    }

    private static void waitBM() throws Exception {
        final Object LOCK = new Object();
        synchronized (LOCK) {
            BuildMonitor.WORKER.post(new Runnable() {
                public void run() {
                    synchronized (LOCK) {
                        LOCK.notify();
                    }
                }
            });
            LOCK.wait();
        }
    }
    
    private BuildMonitor createMonitor(String resource, String name) throws Exception {
        Preferences p = Preferences.userNodeForPackage(BuildMonitorTest.class).node(getName());
        BuildMonitor m = BuildMonitor.create(p);
        m.setURL(BuildMonitorTest.class.getResource(resource));
        m.setName(name);
        waitBM();
        return m;
    }

    public void testHudsonSuccessParsing() throws Exception {
        BuildMonitor m = createMonitor("hudson-success.xml", "Hudson Success");
        assertEquals(Status.SUCCESS, m.getStatus());
        assertEquals(new URL("http://deadlock.nbextras.org:80/job/kukaczka/14/"), m.getStatusLink());
        assertEquals("kukaczka all builds", m.getTitle());
        assertEquals("kukaczka #14 (SUCCESS)", m.getStatusDescription());
    }

    public void testHudsonFailureParsing() throws Exception {
        BuildMonitor m = createMonitor("hudson-failure.xml", "Hudson Failure");
        assertEquals("Hudson Failure", m.getName());
        assertEquals(Status.FAILED, m.getStatus());
        assertEquals(new URL("http://deadlock.nbextras.org:80/job/trunk/7/"), m.getStatusLink());
        assertEquals("trunk #7 (FAILURE)", m.getStatusDescription());
    }

    public void testHudsonProgressParsing() throws Exception {
        BuildMonitor m = createMonitor("hudson-progress.xml", "Hudson Progress");
        assertEquals("picks up old failure status even though a new build is running", Status.FAILED, m.getStatus());
        assertEquals("points to the previous build, not the running one", new URL("http://deadlock.nbextras.org:80/job/trunk/7/"), m.getStatusLink());
        assertEquals("points to the previous build, not the running one", "trunk #7 (FAILURE)", m.getStatusDescription());
    }

}
