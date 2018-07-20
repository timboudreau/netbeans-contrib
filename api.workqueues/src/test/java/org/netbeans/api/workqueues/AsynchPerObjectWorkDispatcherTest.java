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
