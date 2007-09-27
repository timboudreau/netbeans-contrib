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
package org.netbeans.modules.searchandreplace;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import javax.swing.text.StyledDocument;
import junit.framework.TestCase;
import junit.framework.*;
import javax.swing.JEditorPane;
import org.openide.cookies.EditorCookie;
import org.openide.text.Line;
import org.openide.util.Lookup;
import org.openide.util.Task;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author TimBoudreau
 */
public class TimedEditorCookieListenerTest extends TestCase {
    
    public TimedEditorCookieListenerTest(String testName) {
        super(testName);
    }

    InstanceContent ic;
    Lookup lkp;
    protected void setUp() throws Exception {
        ic = new InstanceContent();
        lkp = new AbstractLookup (ic);
    }

    public void testImmediatelyRunsAndDisappears() throws Exception {
        EC ec = new EC();
        ic.set(Collections.singleton(ec), null);
        TimedEditorCookieListener l = new TimedEditorCookieListener (lkp, 0, 0);
        ec.assertCalled();
        Reference ref = new WeakReference (l);
        l = null;

        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            Thread.currentThread().sleep(100);
        }
        assertNull (ref.get());
    }

    public void testCalledIfEditorCookieImmediatelyAppears() throws Exception {
        TimedEditorCookieListener l = new TimedEditorCookieListener (lkp, 0, 0);
        EC ec = new EC();
        ec.assertNotCalled();
        ic.set(Collections.singleton(ec), null);
        //Make time for the EQ part to run
        Thread.currentThread().yield();
        Thread.currentThread().sleep(300);

        ec.assertCalled();
        Reference ref = new WeakReference (l);
        l = null;

        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            Thread.currentThread().sleep(100);
        }
        assertNull (ref.get());
    }

    public void testCalledIfEditorCookieAppearsBeforeTimeout() throws Exception {
        TimedEditorCookieListener l = new TimedEditorCookieListener (lkp, 0, 0);
        EC ec = new EC();
        ec.assertNotCalled();

        //let half of the 7 second timeout expire
        Thread.currentThread().sleep(TimedEditorCookieListener.TIMEOUT / 2);
        ic.set(Collections.singleton(ec), null);

        //Make time for the EQ part to run
        Thread.currentThread().yield();
        Thread.currentThread().sleep(300);

        ec.assertCalled();
        Reference ref = new WeakReference (l);
        l = null;

        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            Thread.currentThread().sleep(100);
        }
        assertNull (ref.get());
    }

    public void testNotCalledIfEditorCookieAppearsAfterTimeout() throws Exception {
        TimedEditorCookieListener l = new TimedEditorCookieListener (lkp, 0, 0);
        EC ec = new EC();
        ec.assertNotCalled();

        //let double the 7 second timeout expire
        Thread.currentThread().sleep(TimedEditorCookieListener.TIMEOUT * 2);
        ic.set(Collections.singleton(ec), null);

        //Make time for the EQ part to run
        Thread.currentThread().yield();
        Thread.currentThread().sleep(300);

        ec.assertNotCalled();
        Reference ref = new WeakReference (l);
        l = null;

        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            Thread.currentThread().sleep(100);
        }
        assertNull (ref.get());
    }



    public static Test suite() {
        TestSuite suite = new TestSuite(TimedEditorCookieListenerTest.class);
        
        return suite;
    }


    private static final class EC implements EditorCookie {
        public void open() {
        }

        public boolean close() {
            return true;
        }

        public Task prepareDocument() {
            return null;
        }

        public StyledDocument openDocument() throws IOException {
            return null;
        }

        public StyledDocument getDocument() {
            return null;
        }

        public void saveDocument() throws IOException {
        }

        public boolean isModified() {
            return false;
        }

        boolean called = false;
        public JEditorPane[] getOpenedPanes() {
            called = true;
            return null;
        }

        public Line.Set getLineSet() {
            return null;
        }

        public void assertCalled() {
            assertTrue (called);
        }

        public void assertNotCalled() {
            assertFalse (called);
        }

    }
    
}
