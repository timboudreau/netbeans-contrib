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
/*
 * DelayQueueQueueTest.java
 * JUnit based test
 *
 * Created on October 21, 2006, 8:32 PM
 */ 
 
package org.netbeans.api.workqueues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author Tim Boudreau
 */
public class AsynchPerObjectWorkDispatcherTest extends NbTestCase {

    public AsynchPerObjectWorkDispatcherTest(String testName) {
        super(testName);
    }

    ProcessorImpl proc;
    Dispatcher<String, Integer> q;
    protected void setUp() throws Exception {
        proc = new ProcessorImpl();
        q = new Dispatcher <String, Integer> (proc);
        Logger.getLogger(Dispatcher.class.getName()).setLevel(Level.ALL);
    }

    /**
     * Test of put method, of class org.netbeans.modules.docbook.DelayQueueQueue.
     */
    public void testPut() throws Exception {
        System.out.println("testPut");
        assertFalse (q.isRunning());
        assertTrue (q.isEmpty());
        q.put("Hello", 1);
        assertFalse (q.isEmpty());
        q.put ("Hello", 2);
        assertFalse (q.isEmpty());

        q.waitNext();

        assertTrue (q.isRunning());
        List l = proc.assertProcessed("Hello");
        assertEquals ("Should have two elements " + l, 2, l.size());
        assertEquals (l.get(0), new Integer(1));
        assertEquals (l.get(1), new Integer(2));

        q.put ("Goodbye", 3);
        q.put ("Goodbye", 4);
        proc.beEvil = true;
        assertFalse (q.isEmpty());

        q.waitNext(10000);
        proc.assertException(NumberFormatException.class);

        assertFalse (q.isRunning());
        q.stop();
    }

    protected Level logLevel() {
        return Level.FINE;
    }

    protected int timeOut() {
        return 120000;
    }

    private static class ProcessorImpl implements QueueWorkProcessor <String, Integer> {
        private Map <String, List <Integer>> m = new HashMap <String, List <Integer>>();

        public List<Integer> assertProcessed (String key) {
            List result = m.remove(key);
            assertNotNull (result);
            return result;
        }

        public void assertNotProcessed (String key) {
            assertFalse (m.containsKey(key));
        }

        public <T extends Exception> void assertException (Class <T> c) {
            assertNotNull (e);
            assertTrue (c.isInstance(e));
        }

        boolean beEvil = false;
        public void process(String key, Drainable contents) {
            System.err.println("PROCESS " + key + " contents " + contents);
            if (beEvil) {
                throw new NumberFormatException ("GO AWAY!");
            }
            m.put (key, contents.drain (Integer.class));
        }

        Exception e;
        public boolean handleException(Exception e) {
            this.e = e;
            return false;
        }
    
        public boolean handleException(Exception e, String key,
                                       Drainable<Integer> work) {
                this.e = e;
                return false;
        }
    }

}
