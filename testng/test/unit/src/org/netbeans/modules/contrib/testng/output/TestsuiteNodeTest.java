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

import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.contrib.testng.output.Report.Testcase;
import org.netbeans.modules.contrib.testng.output.Report.Trouble;

/**
 *
 * @author lukas
 */
public class TestsuiteNodeTest extends NbTestCase {

    public TestsuiteNodeTest(String name) {
        super(name);
    }

    public void testGetHtmlDisplayName() {
        Report report = new Report("MySuite");
        TestsuiteNode n = new TestsuiteNode(ResultDisplayHandler.ANONYMOUS_SUITE, false);
        assertEquals("Test suite&nbsp;&nbsp;running...", n.getHtmlDisplayName());
        n = new TestsuiteNode("MySuite", false);
        assertEquals("MySuite&nbsp;&nbsp;running...", n.getHtmlDisplayName());
        Testcase testcase = new Testcase("TestCase");
        report.reportTest(testcase);
        n.displayReport(report);
        assertEquals("MySuite&nbsp;&nbsp;<font color='#CE7B00'>INTERRUPTED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
        report.markSuiteFinished();
        //passed test
        assertEquals("MySuite&nbsp;&nbsp;<font color='#00CC00'>PASSED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
        //failed test
        Trouble t = new Trouble(true);
        t.exceptionClsName = "SomeEx";
        t.message = "message";
        t.stackTrace = new String[]{"SomeEx", "AnotherEx"};
        testcase.trouble = t;
        report = new Report("TC");
        report.reportTest(testcase);
        report.markSuiteFinished();
        report.totalTests = 1;
        report.failures = 1;
        n = new TestsuiteNode(report, false);
        assertEquals("TC&nbsp;&nbsp;<font color='#FF0000'>FAILED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
        report.totalTests = 1;
        report.confFailures = 1;
        n = new TestsuiteNode(report, false);
        assertEquals("TC&nbsp;&nbsp;<font color='#FF0000'>FAILED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
        //skipped
        report.totalTests = 1;
        report.failures = 0;
        report.confFailures = 0;
        report.skips = 1;
        n = new TestsuiteNode(report, false);
        assertEquals("TC&nbsp;&nbsp;<font color='#808080'>SKIPPED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
        report.confSkips = 1;
        n = new TestsuiteNode(report, false);
        assertEquals("TC&nbsp;&nbsp;<font color='#808080'>SKIPPED</font>", n.getHtmlDisplayName());
        assertEquals(1, n.getChildren().getNodesCount(true));
    }

}