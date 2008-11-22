/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.output;

import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author lukas
 */
public class RootNodeTest extends NbTestCase {

    public RootNodeTest(String name) {
        super(name);
    }

    public void testNode() throws Exception {
        //check the root node
        final RootNode rn = new RootNode(false);
        R r = new R(new File(getDataDir(), "results/testng-results.xml"), rn);
        SwingUtilities.invokeAndWait(r);
        assertEquals("5 tests passed, 3 tests failed, 4 tests skipped.", rn.getDisplayName());
        filterNode(rn, true);
        assertEquals(6, rn.getChildren().getNodesCount(true));
        filterNode(rn, false);
        assertEquals(7, rn.getChildren().getNodesCount(true));

        //check testsuite nodes
        Node[] suites = rn.getChildren().getNodes(true);
        //failed suite node
        Node tsn = suites[0];
        assertEquals(3, tsn.getChildren().getNodesCount(true));
        assertEquals("test.FailPassSkipTest&nbsp;&nbsp;<font color='#FF0000'>FAILED</font>",
                tsn.getHtmlDisplayName());
        Node[] cases = tsn.getChildren().getNodes(true);
        assertEquals(3, cases.length);

        //skipped suite node
        //6-test.SkippedExceptionTest
        tsn = suites[6];
        assertEquals(1, tsn.getChildren().getNodesCount(true));
        assertEquals("test.SkippedExceptionTest&nbsp;&nbsp;<font color='#808080'>SKIPPED</font>", tsn.getHtmlDisplayName());

        //passed suite node
        //4-test.NewTestNGTest
        tsn = suites[4];
        assertEquals(1, tsn.getChildren().getNodesCount(true));
        assertEquals("test.NewTestNGTest&nbsp;&nbsp;<font color='#00CC00'>PASSED</font>", tsn.getHtmlDisplayName());
    }

    public void testNode2() throws Exception {
        final RootNode rn = new RootNode(false);
        rn.getChildren().getNodes(true);
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                try {
                    List<Report> reports = XmlOutputParserTest.parseResultXML(new File(getDataDir(), "results/testng-results_1.xml"));
                    rn.displayReports(reports);
                    for (Report r : reports) {
                        r.markSuiteFinished();
                    }
                    assertEquals(reports.size(), rn.getChildren().getNodesCount(true));
                } catch (Throwable ex) {
                    AssertionError t = new AssertionError(ex.getMessage());
                    t.initCause(ex);
                    throw t;

                }
            }
        });
        assertEquals("174 tests passed, 1 test failed.", rn.getDisplayName());

        filterNode(rn, true);
        assertEquals(1, rn.getChildren().getNodesCount(true));
        filterNode(rn, false);
        assertEquals(71, rn.getChildren().getNodesCount(true));
    }

    public void testNode3() throws Exception {
        final RootNode rn = new RootNode(true);
        rn.getChildren().getNodes(true);
        R r = new R(new File(getDataDir(), "results/testng-results_2.xml"), rn);
        SwingUtilities.invokeAndWait(r);
        assertEquals("2 tests passed, 1 test failed, 1 test skipped.", rn.getDisplayName());
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                rn.displayMessageSessionFinished("[Done.]");
            }
        });
        assertEquals("2 tests passed, 1 test failed, 1 test skipped. [Done.]", rn.getDisplayName());
        filterNode(rn, false);
        assertEquals(1, rn.getChildren().getNodesCount(true));
        filterNode(rn, true);
        assertEquals(1, rn.getChildren().getNodesCount(true));
    }

    private void filterNode(final RootNode rn, final boolean filter) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {

            public void run() {
                Children.MUTEX.writeAccess(new Runnable() {

                    public void run() {
                        rn.setFiltered(filter);
                    }
                });

            }
        });
    }

    private static class R implements Runnable {

        private final File resource;
        private final RootNode rn;
        static int z = 0;

        R(final File resource, final RootNode rn) {
            this.rn = rn;
            this.resource = resource;
        }

        public void run() {
            try {
                List<Report> reports = XmlOutputParserTest.parseResultXML(resource);
                int i = 0;
                z++;
                for (Report r : reports) {
                    assertEquals(i++, rn.getChildren().getNodesCount(true));
                    rn.displaySuiteRunning(( z == 2) ? ResultDisplayHandler.ANONYMOUS_SUITE : r.suiteClassName);
                    assertEquals(i, rn.getChildren().getNodesCount(true));
                    r.markSuiteFinished();
                    assertEquals(i, rn.getChildren().getNodesCount(true));
                    TestsuiteNode tn = rn.displayReport(r);
                    assertEquals(i, rn.getChildren().getNodesCount(true));
                    assertNotNull(tn);
                }
                assertEquals(i, reports.size());
            } catch (Throwable ex) {
                AssertionError t = new AssertionError(ex.getMessage());
                t.initCause(ex);
                throw t;
            }
        }
    }
}