/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.selenium.server;

import java.io.File;
import org.openide.modules.InstalledFileLocator;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.modules.selenium.server.SeleniumServerNode;
import org.netbeans.modules.selenium.server.SeleniumServerRunner;
import org.openide.util.test.MockLookup;
import static org.junit.Assert.*;

/**
 *
 * @author Jindrich Sedek
 */
public class SeleniumServerNodeTest {

    static SeleniumServerNode node;

    @BeforeClass
    public static void startClass(){
         node = new SeleniumServerNode();
         MockLookup.setInstances(new IFL());
    }

    @After
    public void tearDown(){
        SeleniumServerRunner.stopServer().waitFinished();
        node.taskFinished(null);
    }

    @Test
    public void testGetActions() {
        Action[] actions = node.getActions(true);
        assertEquals(6, actions.length);
        assertNull(actions[3]);
        assertNotNull(actions[4]);//Tools?
        assertNotNull(actions[5]);//Properties
    }

    @Test
    public void testStartAction() throws Exception {
        final Action startAction = node.getActions(true)[0];
        assertFalse(SeleniumServerRunner.isRunning());
        assertTrue(startAction.isEnabled());

        startAction.actionPerformed(null);
        SeleniumServerRunner.waitAllTasksFinished();
        node.taskFinished(null);
        assertTrue(SeleniumServerRunner.isRunning());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertFalse(startAction.isEnabled());
            }
        });
    }

    public void testStopAction() throws Exception {
        final Action startAction = node.getActions(true)[0];
        Action stopAction = node.getActions(true)[1];
        assertFalse(SeleniumServerRunner.isRunning());
        assertFalse(stopAction.isEnabled());

        startAction.actionPerformed(null);
        SeleniumServerRunner.waitAllTasksFinished();
        node.taskFinished(null);
        assertTrue(SeleniumServerRunner.isRunning());
        assertTrue(stopAction.isEnabled());

        stopAction.actionPerformed(null);
        SeleniumServerRunner.waitAllTasksFinished();
        node.taskFinished(null);
        assertFalse(SeleniumServerRunner.isRunning());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertFalse(startAction.isEnabled());
            }
        });
    }

    public void testRestartAction() throws Exception {
        final Action restartAction = node.getActions(true)[2];
        assertFalse(SeleniumServerRunner.isRunning());
        assertTrue(restartAction.isEnabled());

        restartAction.actionPerformed(null);
        SeleniumServerRunner.waitAllTasksFinished();
        node.taskFinished(null);
        assertTrue(SeleniumServerRunner.isRunning());
        assertTrue(restartAction.isEnabled());

        restartAction.actionPerformed(null);
        SeleniumServerRunner.waitAllTasksFinished();
        node.taskFinished(null);
        assertTrue(SeleniumServerRunner.isRunning());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertTrue(restartAction.isEnabled());
            }
        });
    }

    /** Copied from AntLoggerTest. */
    public static final class IFL extends InstalledFileLocator {

        public IFL() {}

        @Override
        public File locate(String relativePath, String codeNameBase, boolean localized) {
            if (relativePath.equals("modules/ext/selenium/selenium-server-2.0.jar")) {
                String path = System.getProperty("test.selenium.server.jar");
                System.err.println(path);
                assertNotNull("must set test.selenium.server.jar", path);
                return new File(path);
            }

            return null;
        }
    }

}

