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
package org.netbeans.api.dynactions;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tim Boudreau
 */
public class SensorTest extends TestCase {

    public SensorTest(String testName) {
        super(testName);
    }

    InstanceContent content;
    AbstractLookup lkp;
    protected void setUp() throws Exception {
        content = new InstanceContent();
        lkp = new AbstractLookup (content);
    }

    /**
     * Test of getSensor method, of class org.netbeans.paint.api.actions.Sensor.
     */
    public void testGetSensor() {
        System.out.println("testGetSensor");
        N <String> n = new N <String> ();
        Lookup lkp = Utilities.actionsGlobalContext();
        Sensor sensor = Sensor.create(String.class, lkp);
        assertNotNull(sensor);
        sensor.doRegister(n);
        assertEquals (1, sensor.toNotify.size());
        sensor.doUnregister(n);
        WeakReference ref = new WeakReference (sensor);
        sensor = null;
        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            if (ref.get() == null) break;
        }
        assertNull (ref.get());
    }

    /**
     * Test of resultChanged method, of class org.netbeans.paint.api.actions.Sensor.
     */
    public void testResultChanged() throws Exception {
        System.out.println("testResultChanged");
        N <String> n = new N <String> ();
        Sensor sensor = Sensor.create(String.class, lkp);
        sensor.doRegister (n);

        N <String> n1 = new N <String> ();
        sensor.doRegister(n1);
        content.set (Collections.singleton(new Object()), null);
        n.clear();
        n1.clear();
        content.set(Collections.singleton(new Object()), null);
        n.assertNotNotified();
        n1.assertNotNotified();
        content.set (Arrays.asList("Hello", "There"), null);
        wait (n);
        n.assertNotified();
        n1.assertCount(2);
        content.set (Arrays.asList("Goodbye"), null);
        wait (n);
        n.assertNotified();
        n1.assertCount (1);
        content.set (Collections.EMPTY_LIST, null);
        wait (n);
        n1.assertNotified();
        n.assertEmpty();
        content.set (Collections.singleton(new Object()), null);
        wait (n);
        n1.assertNotNotified();
        n.assertNotNotified();
        assertEquals (2, sensor.toNotify.size());
        sensor.doUnregister(n);
        assertEquals (1, sensor.toNotify.size());
        content.set(Arrays.asList("This", "Is", "A", "Test"), null);
        wait (n);
        n1.assertNotified();
        n.assertNotNotified();
        sensor.doRegister (n);
        assertEquals (2, sensor.toNotify.size());
        WeakReference<N> wr = new WeakReference<N> (n);
        n = null;
        for (int i=0; i < 10; i++) {
            System.gc();
            System.runFinalization();
            if (wr.get() == null) break;
        }
        assertNull (wr.get());
        content.set (Collections.singleton("Foo"), null);
        wait (n1);
        assertEquals (1, sensor.toNotify.size());
        sensor.doUnregister(n1);
        n1.clear();
        content.set (Collections.singleton("Goo goo g'joob"), null);
        wait (n1);
        n1.assertNotNotified();
    }
    
    private void wait (Sensor.Notifiable n) throws Exception {
        synchronized (n) {
            n.wait(1000);
        }
    }

    private static final class N <T extends Object> implements Sensor.Notifiable <T> {
        Collection <T> coll;

        public void notify(Collection coll, Class clazz) {
            this.coll = coll;
        }

        void clear() {
            coll = null;
        }

        public Collection <T> assertNotified() {
            Collection <T> c = coll;
            coll = null;
            assertTrue (c!= null);
            return c;
        }

        public void assertNotEmpty() {
            Collection <T> c = assertNotified();
            assertFalse (c.isEmpty());
        }

        public void assertEmpty() {
            Collection <T> c = assertNotified();
            assertTrue (c.isEmpty());
        }

        public void assertNotNotified() {
            assertNull (coll);
        }

        public void assertCount (int count) {
            Collection c = assertNotified();
            assertEquals (count, c.size());
        }
    }
}
