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

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

public class ThreadingTest extends NbTestCase {
    private boolean ok = false;

    public ThreadingTest(String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ThreadingTest.class));
    }

    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testThreading() throws Exception {
        Context subctx = getContext().createSubcontext ("threading");
        Context subctx2 = subctx.createSubcontext ("hoy");
        
        // - this thread will grab read mutex and will read for a few seconds
        // - inside ReadThread1 is started writing thread which must wait
        //   till the ReadThread1 is finished
        // - inside WriteThread the ReadThread2 is started which must wait till
        //   the WriteThread is finished
        ReadThread1 rt1 = new ReadThread1();
        rt1.start();

        // wait till the threads are done.
        try {
            Thread.sleep(4000);
        } catch(InterruptedException ie) {
            assertTrue("Some error"+ ie.toString(), false);
        }

        assertTrue("Something failed", ok);
        
        getContext().destroySubcontext("threading");
    }

    protected Context getContext() {
        return Context.getDefault();    
    }

    private class ReadThread1 extends Thread {
        public void run() {
            final Context ctx = Context.getDefault().getSubcontext("threading");
            ctx.getMutex().readAccess(new Runnable() {
                public void run() {
                    
                    // this thread will try to delete the hoy context
                    WriteThread wt = new WriteThread();
                    wt.start();
                    
                    try {
                        sleep(1500);
                    } catch(InterruptedException ie) {
                        assertTrue("Some error"+ ie.toString(), false);
                    }
                    
                    Context c = ctx.getSubcontext("hoy");
                    assertTrue("The 'hoy' context must exist.", c != null);
                }
            });
        }
    }
    
    private class WriteThread extends Thread {
        public void run() {
            final Context ctx = Context.getDefault();
            ctx.getMutex().writeAccess(new Runnable() {
                public void run() {
                    
                    // this thread will try to the hoy context which should be deleted by write thread
                    ReadThread2 rt2 = new ReadThread2();
                    rt2.start();
                    
                    try {
                        sleep(1500);
                    } catch(InterruptedException ie) {
                        assertTrue("Some error"+ ie.toString(), false);
                    }
                    
                    try {
                        Context ctx2 = ctx.getSubcontext("threading");
                        ctx2.destroySubcontext("hoy");
                    } catch (ContextException ce) {
                        assertTrue("Cannot delete subcontext "+ ce.toString(), false);
                    }
                }
            });
        }
    }
    
    private class ReadThread2 extends Thread {
        public void run() {
            final Context ctx = Context.getDefault().getSubcontext("threading");
            ctx.getMutex().readAccess(new Runnable() {
                public void run() {
                    Context c = ctx.getSubcontext("hoy");
                    assertTrue("The 'hoy' context cannot exist.", c == null);
                    ThreadingTest.this.ok = true;
                }
            });
        }
    }
    
    
}
