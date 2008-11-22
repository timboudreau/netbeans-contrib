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
public class TestMethodNodeTest extends NbTestCase {

    public TestMethodNodeTest(String name) {
        super(name);
    }

    public void testGetHtmlDisplayName() {
        Testcase testcase = new Testcase("TestCase");
        //passed test
        TestMethodNode n = new TestMethodNode(testcase);
        assertEquals("TestCase&nbsp;&nbsp;<font color='#00CC00'>passed  (0.0 s)</font>", n.getHtmlDisplayName());
        //failed test
        Trouble t = new Trouble(true);
        t.exceptionClsName = "SomeEx";
        t.message = "message";
        t.stackTrace = new String[]{"SomeEx", "AnotherEx"};
        testcase.trouble = t;
        n = new TestMethodNode(testcase);
        assertEquals("TestCase&nbsp;&nbsp;<font color='#FF0000'>failed  (0.0 s)</font>", n.getHtmlDisplayName());
        assertEquals(4, n.getChildren().getNodesCount(true));
        //skipped test
        t.failure = false;
        t.nestedTrouble = new Trouble(false);
        t.nestedTrouble.exceptionClsName = "SomeEx2";
        t.nestedTrouble.message = "message2";
        t.nestedTrouble.stackTrace = new String[]{"SomeEx2", "AnotherEx2"};
        testcase.trouble = t;
        n = new TestMethodNode(testcase);
        assertEquals("TestCase&nbsp;&nbsp;<font color='#808080'>skipped  (0.0 s)</font>", n.getHtmlDisplayName());
        assertEquals(7, n.getChildren().getNodesCount(true));
        //interrupted test
        testcase.timeMillis = Testcase.NOT_FINISHED_YET;
        n = new TestMethodNode(testcase);
        assertEquals("TestCase&nbsp;&nbsp;<font color='#CE7B00'>interrupted</font>", n.getHtmlDisplayName());
        //test with unknown time
        testcase.timeMillis = Testcase.TIME_UNKNOWN;
        n = new TestMethodNode(testcase);
        assertEquals("TestCase&nbsp;&nbsp;<font color='#808080'>skipped</font>", n.getHtmlDisplayName());

        //passed conf method (not shown by default in UI)
        testcase.confMethod = true;
        testcase.trouble = null;
        testcase.timeMillis = 0;
        n = new TestMethodNode(testcase);
        assertEquals("<i>TestCase</i>&nbsp;&nbsp;<font color='#00CC00'>passed  (0.0 s)</font>", n.getHtmlDisplayName());
        //failed conf method
        testcase.trouble = new Trouble(true);
        n = new TestMethodNode(testcase);
        assertEquals("<i>TestCase</i>&nbsp;&nbsp;<font color='#FF0000'>failed  (0.0 s)</font>", n.getHtmlDisplayName());
        assertEquals(0, n.getChildren().getNodesCount(true));
        //skipped conf method
        testcase.trouble = new Trouble(false);
        n = new TestMethodNode(testcase);
        assertEquals("<i>TestCase</i>&nbsp;&nbsp;<font color='#808080'>skipped  (0.0 s)</font>", n.getHtmlDisplayName());
        //interrupted test
        testcase.timeMillis = Testcase.NOT_FINISHED_YET;
        n = new TestMethodNode(testcase);
        assertEquals("<i>TestCase</i>&nbsp;&nbsp;<font color='#CE7B00'>interrupted</font>", n.getHtmlDisplayName());
        //test with unknown time
        testcase.timeMillis = Testcase.TIME_UNKNOWN;
        n = new TestMethodNode(testcase);
        assertEquals("<i>TestCase</i>&nbsp;&nbsp;<font color='#808080'>skipped</font>", n.getHtmlDisplayName());
    }
}