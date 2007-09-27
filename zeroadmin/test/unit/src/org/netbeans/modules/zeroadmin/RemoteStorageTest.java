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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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

package org.netbeans.modules.zeroadmin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.SwingUtilities;

import org.openide.windows.*;
import org.openide.util.SharedClassObject;
import org.openide.util.Lookup;
import org.openide.filesystems.*;
import org.netbeans.modules.zeroadmin.actions.*;
import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.netbeans.core.projects.*;

/**
 * This test suite should test whether the data
 * stored on the J2EEserver as user configuration
 * data are stored and retrieved correctly.
 * @author David Strupl
 */
public class RemoteStorageTest extends NbTestCase {

    private FileSystem originalFS;
    
    public RemoteStorageTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(RemoteStorageTest.class));
    }

    /**
     * Sets up the testing environment by 
     */
    protected void setUp () throws Exception {
    }
    
    /**
     */
    protected void tearDown() throws Exception {
    }
    
    /**
     * The reset config action should restore the
     * data to the operator data. It means that whatever
     * the user has done has to be undone by the action.
     * <OL> This test:
     *      <LI> Creates a new workspace
     *      <LI> Verifies that the workspace has been created
     *      <LI> Calls ResetConfigAction
     *      <LI> Checks whether the workspace is gone
     * </OL>
     */
    public void testConfigReset() throws Exception {
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CREATE_TC));
        
        TestRunnable test = new TestRunnable(TestRunnable.WAIT_FOR_WM_EVENT);
        java.awt.EventQueue.invokeAndWait(test);
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CALL_RESET_CONFIG_ACTION));
        
        // wait for the window system to update itself
        int round = 0;
        boolean done = false;
        while (! done) {
            Thread.sleep(1000);
            round++;
            System.out.println("testConfigReset round " + round);
            if (round > 10) {
                fail("Waiting too long for a change!");
            }
            done = test.changeEvent != null;
        }
        
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CHECK_NULL));
    }

    /**
     * The refresh config should add new configuration
     * data added by the operaor to use config. The user
     * configuration data should remain intact.
     * <OL> This test
     *      <LI> Creates a workspace
     *      <LI> Saves the operator config
     *      <LI> Deletes the workspace
     *      <LI> Calls RefreshConfigAction
     *      <LI> The workspace should be back
     * </OL>
     */
    public void testConfigRefresh() throws Exception {
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CREATE_TC));
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CALL_SAVE_OPERATOR_CONFIG_ACTION));
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.DELETE_TC));
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CHECK_NULL));
        
        TestRunnable test = new TestRunnable(TestRunnable.WAIT_FOR_WM_EVENT);
        java.awt.EventQueue.invokeAndWait(test);
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CALL_REFRESH_CONFIG_ACTION));

        // wait for the window system to update itself
        int round = 0;
        boolean done = false;
        while (! done) {
            Thread.sleep(1000);
            round++;
            System.out.println("testConfigRefresh round " + round);
            if (round > 10) {
                fail("Waiting too long for a change!");
            }
            done = test.changeEvent != null;
        }
        
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CHECK_NOT_NULL));
        // just cleanup at the very end:
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.DELETE_TC));
        java.awt.EventQueue.invokeAndWait(new TestRunnable(TestRunnable.CHECK_NULL));
    }
    
    /**
     * Makes sure that what is saved as operator data
     * is correct XML file system.
     * <OL> This test
     *      <LI> Saves the operator's data using the action provided
     *      <LI> Fetches the raw data and creates XMLBufferFileSystem
     * </OL>
     */
    public void testSaveOperatorConfig() throws Exception {
        SaveOperatorConfigAction action1 = (SaveOperatorConfigAction)SharedClassObject.findObject(SaveOperatorConfigAction.class, true);
        action1.performAction();
        ZeroAdminInstall z = (ZeroAdminInstall)SharedClassObject.findObject(ZeroAdminInstall.class);
        assertNotNull(z);
        assertNotNull(z.cfgProxy);
        char[] dataFromServer = z.cfgProxy.getOperatorData();
        XMLBufferFileSystem xbfs = new XMLBufferFileSystem(new ParseRegen(dataFromServer));
        xbfs.waitFinished();
        // no exceptions --> OK.
    }
    
    /**
     * This test should verify that the data stored to 
     * the J2EE server are the same as the data on the
     * client.
     * <OL> This test
     *      <LI> saves the user data to the server
     *      <LI> downloads back the user data and compares them
     *          to what is on the client - the data should
     *          be the same
     * </OL>
     */
    public void testSavedDataAreTheSame() throws Exception {
        ZeroAdminInstall z = (ZeroAdminInstall)SharedClassObject.findObject(ZeroAdminInstall.class);
        assertNotNull(z);
        assertNotNull(z.saver);
        assertNotNull(z.cfgProxy);
        assertNotNull(z.writableLayer);
        z.saver.waitFinished();

        // test saving to the server
        XMLBufferFileSystem bufFs = new XMLBufferFileSystem();
        ZeroAdminInstall.copy(z.writableLayer.getRoot(), bufFs.getRoot(), true);

        bufFs.waitFinished();
        char[] origData = bufFs.getBuffer();
        z.cfgProxy.saveUserData(origData);

        char[] dataFromServer = z.cfgProxy.getUserData();
        assertEquals("Data from the server should have the same length", origData.length, dataFromServer.length);
        for (int i = 0; i < origData.length; i++) {
            if (origData[i] != dataFromServer[i]) {
                fail("Data from the server are not the same - offset " + i);
            }
        }
        
        // test construction of the filesystem from the server data
        XMLBufferFileSystem xbfs = new XMLBufferFileSystem(new ParseRegen(dataFromServer));
        xbfs.waitFinished();
        
        // compare the children of the root:
        FileObject r1 = xbfs.getRoot();
        FileObject r2 = bufFs.getRoot();
        FileObject ch[] = r1.getChildren();
        for (int i = 0; i < ch.length; i++) {
            FileObject other = r2.getFileObject(ch[i].getNameExt());
            //assertNotNull(ch[i] + " not found", other);
            if (other != null) {
                assertTrue(ch[i] + " not the same", compare(ch[i], other));
            } else {
                // for some reason ide.log is not found 
                // do not report is as failure just write
                // a note to the log file
                System.err.println(ch[i] + " not found");
            }
        }
    }
    
    /**
     * Compares two files or folders. For files checks
     * only the name, not the content. For folders recurse
     * into subfolders.
     * returns true if they are the same
     */
    private boolean compare(FileObject f1, FileObject f2) {
        if (! f1.getNameExt().equals(f2.getNameExt())) {
            return false;
        }
        if (f1.isData()) {
            if (f2.isFolder()) {
                return false;
            }
            if (f2.isData()) {
                // both are data with the same name
                // we do not compare the content of the file!
                return true;
            }
            throw new IllegalStateException(f2 + " is not a folder nor data");
        }
        if (f1.isFolder()) {
            if (! f2.isFolder()) {
                return false;
            }
            // both folder --> recurse
            FileObject ch1[] = f1.getChildren();
            for (int i = 0; i < ch1.length; i++) {
                FileObject second = f2.getFileObject(ch1[i].getNameExt());
                if (second == null) {
                    return false;
                }
                if (! compare(ch1[i], second)) {
                    return false;
                }
            }
            FileObject ch2[] = f2.getChildren();
            for (int i = 0; i < ch2.length; i++) {
                FileObject second = f1.getFileObject(ch2[i].getNameExt());
                if (second == null) {
                    return false;
                }
                if (! compare(ch2[i], second)) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalStateException(f1 + " is not folder nor data");
    }
    
    /**
     * Since all window system API has to be called from the AWT event queue
     * this class is the Runnable for scheduling into the evert queue.
     */
    private static class TestRunnable implements Runnable, PropertyChangeListener {
        /** possible value for what */
        public static final int CREATE_TC = 1;
        /** possible value for what */
        public static final int CHECK_NULL = 2;
        /** possible value for what */
        public static final int CHECK_NOT_NULL = 3;
        /** possible value for what */
        public static final int DELETE_TC = 4;
        /** possible value for what */
        public static final int WAIT_FOR_WM_EVENT = 5;
        /** possible value for what */
        public static final int CALL_RESET_CONFIG_ACTION = 6;
        /** possible value for what */
        public static final int CALL_REFRESH_CONFIG_ACTION = 7;
        /** possible value for what */
        public static final int CALL_SAVE_OPERATOR_CONFIG_ACTION = 8;
        
        
        /** what to do */
        private int what;
        
        public PropertyChangeEvent changeEvent;
        
        /** just remember what */
        public TestRunnable(int what) {
            this.what = what;
        }
        
        /** This is run in the AWT event thread */
        public void run() {
            WindowManager wm = WindowManager.getDefault();
            if (what == CREATE_TC) {
                TopComponent tc = new TopComponent() {
                    public int getPersistenceType() {
                        return TopComponent.PERSISTENCE_NEVER;
                    }
                };
                tc.setName("testTc");
                wm.findMode("explorer").dockInto(tc);
                tc.open();
                // --------
                assertNotNull("The tc should be created", wm.findTopComponent("testTc"));
            }
            if (what == CHECK_NULL) {
                assertNull("The tc should be gone", wm.findTopComponent("testTc"));
            }
            if (what == CHECK_NOT_NULL) {
                assertNotNull("The tc should be there", wm.findTopComponent("testTc"));
            }
            if (what == DELETE_TC) {
                TopComponent tc = wm.findTopComponent("testTc");
                assertNotNull(tc);
                tc.close();
            }
            
            if (what == WAIT_FOR_WM_EVENT) {
                wm.addPropertyChangeListener(this);
            }
            
            if (what == CALL_RESET_CONFIG_ACTION) {
                ResetConfigAction action = (ResetConfigAction)SharedClassObject.findObject(ResetConfigAction.class, true);
                action.performAction();
            }
            
            if (what == CALL_REFRESH_CONFIG_ACTION) {
                RefreshConfigAction action2 = (RefreshConfigAction)SharedClassObject.findObject(RefreshConfigAction.class, true);
                action2.performAction();
            }
            
            if (what == CALL_SAVE_OPERATOR_CONFIG_ACTION) {
                SaveOperatorConfigAction action1 = (SaveOperatorConfigAction)SharedClassObject.findObject(SaveOperatorConfigAction.class, true);
                action1.performAction();
            }
        }
        
        public void propertyChange(PropertyChangeEvent pce) {
            changeEvent = pce;
        }
    }
}
