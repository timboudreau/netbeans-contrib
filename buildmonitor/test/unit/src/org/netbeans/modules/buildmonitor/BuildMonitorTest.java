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
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

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

    public void testHudsonSuccessParsing() throws Exception {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("foo.instance");
        fo.setAttribute("url", BuildMonitorTest.class.getResource("hudson-success.xml"));
        fo.setAttribute("name", "Hudson Success");
        BuildMonitor m = BuildMonitor.create(fo);
        waitBM();
        assertEquals(Status.SUCCESS, m.getStatus());
        assertEquals(new URL("http://deadlock.nbextras.org:80/job/kukaczka/14/"), m.getStatusLink());
        assertEquals("kukaczka all builds", m.getTitle());
        assertEquals("kukaczka #14 (SUCCESS)", m.getStatusDescription());
    }

    public void testHudsonFailureParsing() throws Exception {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("foo.instance");
        fo.setAttribute("url", BuildMonitorTest.class.getResource("hudson-failure.xml"));
        fo.setAttribute("name", "Hudson Failure");
        fo.setAttribute("minutes", new Integer(15));
        BuildMonitor m = BuildMonitor.create(fo);
        waitBM();
        assertEquals(15, m.getPollMinutes());
        assertEquals("Hudson Failure", m.getName());
        assertEquals(Status.FAILED, m.getStatus());
        assertEquals(new URL("http://deadlock.nbextras.org:80/job/trunk/7/"), m.getStatusLink());
        assertEquals("trunk #7 (FAILURE)", m.getStatusDescription());
    }

    public void testHudsonProgressParsing() throws Exception {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("foo.instance");
        fo.setAttribute("url", BuildMonitorTest.class.getResource("hudson-progress.xml"));
        fo.setAttribute("name", "Hudson Progres");
        BuildMonitor m = BuildMonitor.create(fo);
        waitBM();
        assertEquals(Status.NO_STATUS_AVAIL, m.getStatus());
        assertEquals(new URL("http://deadlock.nbextras.org:80/job/trunk/8/"), m.getStatusLink());
        assertEquals("trunk #8 ()", m.getStatusDescription());
    }

}
