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
package org.netbeans.modules.vcscore.turbo.log;

import org.netbeans.modules.vcscore.turbo.Turbo;

import java.io.*;
import java.util.Date;

/**
 * Supports {@link org.netbeans.modules.vcscore.turbo.Turbo} and
 * {@link org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery} statistics.
 * <p>
 * Looking at results one can see that FileObjects are GCed
 * really rarely(!). Therefore disk layer takes effect
 * mainly after IDE restart. Later on almost all
 * request get resolved at memory layer. Fast disk
 * writes consisst mayority of writes. We could reconsider
 * droping it for low disk reads / disk write ratio
 * because it slows down disk reads a little bit.
 *
 * @author Petr Kuzel
 */
public final class Statistics {

    private static PrintWriter out;
    private static long requests = 0;
    private static long faqRequests = 0;
    private static long memoryHits = 0;
    private static long diskHits = 0;
    private static long gcCounter = 0;
    private static long newCounter = 0;
    private static long fastDiskWrites = 0;
    private static long slowDiskWrites = 0;
    private static long threads = 0;
    private static long duplicates = 0;
    private static long maxQueueSize = 0;
    private static long absoluteKeys = 0;
    private static long maxMemoryEntries = 0;
    private static boolean outToBeClosed;

    private Statistics() {
        // only static methods
    }

    /**
     * Checks if additional logging required for detailed performance evaluation is required.
     */
    public static boolean logPerformance() {
        return System.getProperty("netbeans.experimental.vcsTurboStatistics", "mini").equalsIgnoreCase("performance");  // NOI18N
    }

    /** FileObject  created adding permision to store it in memory layer. */
    public static void fileObjectAdded(String path) {
        if (path != null) println("FS+ " + path);
        newCounter++;
        long allocated = newCounter - gcCounter;
        if (allocated > maxMemoryEntries) {
            maxMemoryEntries = allocated;
        }
    }

    /** FileObject GC notifiled, removing permision to store it in memory layer. */
    public static void fileObjectRemoved(String path) {
        if (path != null) println("FS- " + path);
        gcCounter++;
    }

    /** Absolute path key (a fallback) was created for weak key */
    public static void absolutePathKey(String absolutePath) {
        println("KEY " + absolutePath);
        absoluteKeys++;
    }

    /** Turbo request arrived */
    public static void request() {
        requests++;
        if (requests % 1000 == 0) {
            printCacheStatistics();
        }
    }

    public static void attributeRequest() {
        faqRequests++;
    }

    /** The client request was resolved by memory layer */
    public static void memoryHit() {
        memoryHits++;
    }

    /** new background thread spawned */
    public static void backgroundThread() {
        threads++;
    }

    /** Duplicate request eliminated from queue. */
    public static void duplicate() {
        duplicates++;
    }

    public static void queueSize(int size) {
        if (size > maxQueueSize) {
            maxQueueSize = size;
        }
    }

    /** The client request was resolved by disk layer */
    public static void diskHit() {
        diskHits++;
    }

    /** New data were appended to end of disk cache */
    public static void diskAppend() {
        fastDiskWrites++;
    }

    /** Disk file was optimalized */
    public static void diskRewrite() {
        slowDiskWrites++;
    }

    public static void shutdown() {
        printCacheStatistics();
        if (outToBeClosed) {
            out.close();
        }
//        System.out.println("  Statistics goes to " + Statistics.logPath()); // NOI18N        
    }

    public static String logPath() {
        return System.getProperty("java.io.tmpdir") + File.separator + "netbeans-vcs-turbo.log"; // NOI18N
    }
    
    private static void printCacheStatistics() {
        println("CS  turbo.requests=" + requests + " faq.requests=" + faqRequests);
        println("CS  memory.hits=" + memoryHits + " " + (((float)memoryHits/(float)faqRequests) * 100) + "% disk.hits=" + diskHits + " " + (((float)diskHits/(float)faqRequests) * 100) + "%");  // NOI18N
        println("CS  memory.max=" + maxMemoryEntries + " memory.entries=" + (newCounter - gcCounter) + " memory.entiresReclaimingRatio=" + (((float)gcCounter/(float)newCounter) * 100) + "%");  // NOI18N
        println("CS  memory.fsMismatchs=" + absoluteKeys);  // NOI18N
        println("CS  queue.threads=" + threads + " queue.duplicates=" + duplicates + " queue.maxSize=" + maxQueueSize);  // NOI18N
        println("CS  disk.fastWrites=" + fastDiskWrites + " disk.slowWrites=" + slowDiskWrites + " disk.fastRatio=" + (((float)fastDiskWrites/(float)(fastDiskWrites + slowDiskWrites)) * 100) + "%");  // NOI18N
        println("--"); // NOI18N
        println("turbo.log.Statistics on " + new Date().toString()); // NOI18N
    }

    private static synchronized void println(String s) {
        
        if (System.getProperty("netbeans.experimental.vcsTurboStatistics", "off").equalsIgnoreCase("off")) return;  // NOI18N
        
        if (out == null) {
            String filePath = logPath();
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(filePath), 512));
                outToBeClosed = true;
            } catch (IOException e) {
                out = new PrintWriter(new OutputStreamWriter(System.out), true);
                outToBeClosed = false;
            }
            out.println("FS followed by +/- denotes new memory cache entry/releasing it"); // NOI18N
            out.println("CS describes summary statistics of memory and disk caches"); // NOI18N
            out.println();
        }
        out.println(s);
    }

}
