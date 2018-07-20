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

package org.netbeans.modules.tasklist.core;

import org.openide.util.RequestProcessor;
import org.openide.util.Cancellable;
import org.openide.ErrorManager;

/**
 * Workarounds RequestProcessor (and JVM) background threads behaviour
 * on Linux systems that map thread to OS scheduled processes.
 * <p>
 * Do not forget to run JVM with -Djava.library.path=/home/pk97937/prj/academy/linux
 * property or put into NetBeans search path (modules/bin).
 *
 * @author  Petr Kuzel
 */
public final class Background {

    private static boolean loaded = false;
    private static boolean loadfailed = false;

    private Thread peer;
    private Cancellable cancel;

    private Background(Thread peer, Cancellable c) {
        this.peer = peer;
        cancel = c;
    }

    public static Background execute(Runnable run) {
        Cancellable cancel = (Cancellable) (run instanceof Cancellable ? run : null);
        if (useHack()) {
            Thread t = new Thread(new Wrapper(run), "Background");  // NOI18N
            t.setPriority(Thread.MIN_PRIORITY);
            t.setDaemon(true);
            t.start();

            return new Background(t, cancel);
        } else {
            ThreadExtractor extractor = new ThreadExtractor(run);
            RequestProcessor.getDefault().post(extractor, 0, Thread.MIN_PRIORITY);
            return new Background(extractor.getThread(), cancel);
        }
    }

    public final void interrupt() {
        if (peer != null) {
            peer.interrupt();  // it's not enough see #38399
            peer.interrupt();
        }
        if (cancel != null) cancel.cancel();
    }

    // use hack on linux JVM with successfuly loaded library
    // it works with Sun provided Linux 1.4 series JVMs on i386
    // feel free to weaken vendor and version rules if you find
    // other JVM that maps Java threads to linux processes
    private static boolean useHack() {

        String os = System.getProperty("os.name"); // NOI18N
        if ("Linux".equals(os) == false) return false; // NOI18N

        // jlahoda thinks that JVM threading is correct on 2.6.x kernels, he'll investigate
        String osversion = "" + System.getProperty("os.version"); // NOI18N
        if (osversion.startsWith("2.4") == false) return false; // NOI18N

        String vendor = "" + System.getProperty("java.vm.vendor"); // NOI18N
        if (vendor.startsWith("Sun") == false) return false; // NOI18N

        String version = "" + System.getProperty("java.vm.version"); // NOI18N
        if (version.startsWith("1.4") == false) return false; // NOI18N

        String hw = System.getProperty("os.arch"); // NOI18N
        if ("i386".equals(hw) == false) return false; // NOI18N

        loadLibrary();
        return loaded;
    }

    private static class Wrapper implements Runnable {

        private final Runnable peer;

        public Wrapper(Runnable run) {
            this.peer = run;
        }

        public void run() {
            native_nice();
            peer.run();
        }

    }

    /**
     * Get actual RP thread for given Runnable.
     */
    private static class ThreadExtractor implements Runnable {

        private final Runnable peer;
        private Thread thread;

        ThreadExtractor(Runnable run) {
            peer = run;
        }

        public void run() {
            Thread.currentThread().interrupted(); // consume/clear the flag
            synchronized (this) {
                thread = Thread.currentThread();
                notifyAll();
            }
            peer.run();
        }

        public synchronized Thread getThread() {
            while (thread == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // null thread
                }
            }
            return thread;
        }
    }

    // JNI related section ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static void loadLibrary() {
        if (loadfailed) return;
        if (false == loaded) {
            try {
                // XXX be aware of #32080, that changes location of native libraries
                System.loadLibrary("tasklist_bgthreads"); // NOI18N
                loaded = true;
            } catch (Throwable t) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, t);
                loadfailed = true;
            }
        }
    }


    /**
     * Nice current process (on some JVM implementations Java thread). 
     */
    private static native void native_nice();
    
    /**
     * Simple self test.
     */
    public static void main(String[] args) throws Exception {
        if (useHack() == true) {
            native_nice();
            System.out.println("I'm niced for 1 minute. Check it by top utility."); // NOI18N
            Thread.sleep(60*1000);
        }
    }
}
