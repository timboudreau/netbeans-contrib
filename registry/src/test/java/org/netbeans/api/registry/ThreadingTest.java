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
