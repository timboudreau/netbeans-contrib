/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * @author Tim Boudreau
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
