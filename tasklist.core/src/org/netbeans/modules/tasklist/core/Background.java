/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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
                System.loadLibrary("tasklist_bgthreads");
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
            System.out.println("I'm niced for 1 minute. Check it by top utility.");
            Thread.sleep(60*1000);
        }
    }
}
