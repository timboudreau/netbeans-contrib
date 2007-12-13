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
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.objectloader;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import junit.framework.TestCase;

/**
 * Tests the behavior of ObjectLoader.
 *
 * @author Tim Boudreau
 */
public class ObjectLoaderTest extends TestCase {
    
    public ObjectLoaderTest(String testName) {
        super(testName);
    }            

    public void testLoaderLoadsAndManagesMemoryCorrectly() throws Exception {
        System.out.println("testLoaderLoadsAndManagesMemoryCorrectly");
        final Object lock = new Object();
        Ldr ldr = new Ldr ("Hello", lock, false);
        assertEquals (States.UNLOADED, ldr.getState());
        OR or = new OR();
        ldr.get(or);
        synchronized (lock) {
            lock.wait(10000);
        }
        synchronized (or) {
            or.wait (10000);
        }
        assertEquals ("Hello", or.result.toString());
        assertNull (or.e);
        Reference<OR> orRef = new WeakReference<OR> (or);
        or = null;
        for (int i=0; i < 5; i++) {
            System.gc();
            System.runFinalization();
        }
        assertNull (orRef.get());
        assertEquals(States.UNLOADED, ldr.getState());
    }
    
    public void testLoaderFailsCorrectly() throws Exception {
        System.out.println("testLoaderFailsCorrectly");
        final Object lock = new Object();
        Ldr ldr = new Ldr ("Hello", lock, true);
        assertEquals (States.UNLOADED, ldr.getState());
        OR or = new OR();
        ldr.get(or);
        synchronized (lock) {
            lock.wait(10000);
        }
        synchronized (or) {
            or.wait(5000);
        }
        assertNull (or.result);
        assertNotNull (or.e);
        assertEquals (States.NOT_LOADABLE, ldr.getState());
        Reference orRef = new WeakReference<OR> (or);
        or = null;
        for (int i=0; i < 5; i++) {
            System.gc();
            System.runFinalization();
        }
        assertNull (orRef.get());
        assertEquals(States.NOT_LOADABLE, ldr.getState());
        ldr.reset();
        assertEquals (States.UNLOADED, ldr.getState());
    }
    
    public void testStateChanges() throws Exception {
        System.out.println("testStateChanges");
        Object waitLock = new Object();
        Object notifyLock = new Object();
        Ldr2 ldr = new Ldr2 ("foo", waitLock, notifyLock);
        OR or = new OR();
        ldr.get(or);
        assertEquals (States.LOADING, ldr.getState());
        Thread.sleep(1000);
        assertEquals (States.LOADING, ldr.getState());
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        synchronized (or) {
            or.wait(1000);
        }
        assertEquals ("foo", or.result == null ? null : or.result.toString());
        assertNull (or.e);
        assertEquals (States.LOADED, ldr.getState());
    }
    
    public void testPartialCancellation() throws Exception {
        System.out.println("testPartialCancellation");
        Object waitLock = new Object();
        Object notifyLock = new Object();
        Ldr2 ldr = new Ldr2 ("foo", waitLock, notifyLock);
        OR or1 = new OR();
        OR or2 = new OR();
        ldr.get(or1);
        ldr.get(or2);
        ldr.cancel(or2);
        Thread.sleep (1000);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        synchronized (notifyLock) {
            notifyLock.wait(5000);
        }
        synchronized (or1) {
            or1.wait(10000);
        }
        synchronized (or2) {
            or2.wait(10000);
        }
        assertNotNull (or1.result);
        assertNull (or2.result);
    }
    
    public void testFullCancellation() throws Exception {
        System.out.println("testFullCancellation");
        Object waitLock = new Object();
        Object notifyLock = new Object();
        Ldr2 ldr = new Ldr2 ("foo", waitLock, notifyLock);
        OR or1 = new OR();
        OR or2 = new OR();
        ldr.get(or1);
        ldr.get(or2);
        ldr.cancel(or1);
        ldr.cancel(or2);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        synchronized (notifyLock) {
            notifyLock.wait(10000);
        }
        assertNull (or1.result);
        assertNull (or2.result);
        Thread.yield();
        assertEquals (States.UNLOADED, ldr.getState());
    }
    
    private class OR implements ObjectReceiver<StringBuffer> {
        StringBuffer result;
        Exception e;
        public void received(StringBuffer t) {
            result = t;
            synchronized (this) {
                notifyAll();
            }
        }

        public void failed(Exception e) {
            this.e = e;
            synchronized (this) {
                notifyAll();
            }
        }

        Boolean sync = false;
        public void setSynchronous(boolean val) {
            sync = val ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    private static class Ldr extends ObjectLoader<StringBuffer> {
        private final String s;
        private final Object lock;
        private final boolean fail;
        public Ldr (String s, Object lock, boolean fail) {
            super (StringBuffer.class, CacheStrategies.WEAK);
            this.s = s;
            this.lock = lock;
            this.fail = fail;
        }

        @Override
        protected StringBuffer load() throws IOException {
            try {
                if (Thread.interrupted()) {
                    return null;
                }
//                Thread.sleep(1000);
                if (false) throw new InterruptedException();
                if (Thread.interrupted()) {
                    return null;
                }
                if (fail) {
                    throw new IOException ("Failed");
                }
                StringBuffer result = new StringBuffer(s);
                if (Thread.interrupted()) {
                    return null;
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
                if (Thread.interrupted()) {
                    return null;
                }
                return result;
            } catch (InterruptedException ex) {
                return null;
            }
        }
    }
    
    private static class Ldr2 extends ObjectLoader<StringBuffer> {
        private final String s;
        private final Object lock;
        private final Object waitLock;
        public Ldr2 (String s, Object waitLock, Object lock) {
            super (StringBuffer.class, CacheStrategies.WEAK);
            this.s = s;
            this.lock = lock;
            this.waitLock = waitLock;
        }

        @Override
        protected StringBuffer load() throws IOException {
            try {
                System.err.println("Entered ldr2.load");
                if (Thread.interrupted()) {
                    System.err.println("Abort 1");
                    return null;
                }
                synchronized (waitLock) {
                    waitLock.wait();
                }
                if (Thread.interrupted()) {
                    System.err.println("Abort 2");
                    return null;
                }
                StringBuffer result = new StringBuffer(s);
                if (Thread.interrupted()) {
                    System.err.println("Abort 3");
                    return null;
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
                if (Thread.interrupted()) {
                    System.err.println("Abort 4");
                    return null;
                }
                return result;
            } catch (InterruptedException ex) {
                return null;
            }
        }
    }    
}
