/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util.virtuals;

import java.beans.*;
import java.io.*;
import java.lang.ref.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.openide.filesystems.*;
import org.openide.ErrorManager;
import org.openide.TopManager;

import org.openide.util.RequestProcessor;
import org.openide.util.enum.SequenceEnumeration;
import org.openide.util.enum.SingletonEnumeration;
import org.openide.util.enum.QueueEnumeration;

/** Request for parsing of an filesystem. Can be stoped.
* Copied from openide by Milos Kleint.. :) because the class is final and I need to rewrite it..
*
* @author Milos Kleint
*/
public final class VcsRefreshRequest extends Object implements Runnable {
    /** how much folders refresh at one request */
    private static final int REFRESH_COUNT = 30;

    /** fs to work on */
    private Reference system;
    
    private Reference refresher;

    /** enumeration of folders Reference (FileObjects) to process */
    private Enumeration en;

    /** how often invoke itself */
    private int refreshTime;

    /** task to call us */
    private RequestProcessor.Task task;
    
    private HashSet preffered;
    
private boolean interrupt;

    /** Constructor
    * @param fs file system to refresh
    * @param ms refresh time
    */
    public VcsRefreshRequest(AbstractFileSystem fs, int ms, VirtualsRefreshing refresher) {
        system = new WeakReference (fs);
        this.refresher  = new WeakReference(refresher);
        refreshTime = ms;
        // will generate a random seed for starting the refreshing.. that way we have the
        // different filesystems refresh at random times
        int randSeed = (int)Math.round(Math.random() * 15000) + 15000;
//        System.out.println("seed=" + randSeed + " for=" + fs.getDisplayName());
        task = RequestProcessor.postRequest (this, randSeed, Thread.MIN_PRIORITY);
    }

    /** Getter for the time.
    */
    public int getRefreshTime () {
        return refreshTime;
    }

    /** Stops the task.
    */
    public synchronized void stop () {
        refreshTime = 0;

        if (task == null) {        
            // null task means that the request processor is running =>
            // wait for end of task execution
            try {
                wait ();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * allows the filesystem to add a specific folder that should be refreshed with priority
     * @param folderPath the packageNameExt path to the folder
     */
    public  void addPrefferedFolder(String folderPath) {
        synchronized (this) {
            if (preffered == null) {
                preffered = new HashSet();
            }
            preffered.add(folderPath);
            if (task != null) {
                if (task.getDelay() > 1000 || task.getDelay() < 600) {
//                    System.out.println("rescheduling..");
                    task.schedule(1000);
                }
            } else {
                //task is running - attempt to interrupt..
                interrupt = true;
            }
        }
    }
    
    synchronized boolean hasPrefferedFolder() {
        if (preffered == null || preffered.size() == 0) {
            return false;
        }
        return true;
    }
    
    private boolean shouldBeInterrupted() {
        return interrupt;
    }
    
    FileObject getPrefferedFolder() {
        boolean repeat = false;
        FileObject fo = null;
        while (!repeat) {
            repeat = true;
            String toReturn;
            synchronized (this) {
                if (preffered == null || preffered.size() == 0) {
//                    System.out.println("pref is null");
                    return null;
                }
                toReturn = (String)preffered.iterator().next();
                preffered.remove(toReturn);
            }
            if (toReturn == null) return null;
            AbstractFileSystem fs = (AbstractFileSystem)this.system.get();
            if (fs == null) {
//                System.out.println("fs is null");
                return null;
            }
            fo = fs.findResource(toReturn);
            if (fo == null) {
//                System.out.println("repeating.." +toReturn);
                repeat = false;
            }
        }
//        System.out.println("ok=" + fo.getName());
        return fo;
    }

    /** Refreshes the system.
    */
    public void run () {
        // this code is executed only in RequestProcessor thread
        int ms;
        RequestProcessor.Task t;
        synchronized (this) {
            // the synchronization is here to be sure
            // that 
            ms = refreshTime;
            
            if (ms <= 0) {
                // finish silently if already stopped
                return;
            }
            
            t = task;
            interrupt = false;
        }
        
        try {
          // by setting task to null we indicate that we are currently processing
          // files and that any stop should wait till the processing is over
          task = null;
          
          doLoop (ms);
        } finally {
             synchronized (this) {
                 // reseting task variable back to indicate that 
                 // the processing is over
                 task = t;
                 interrupt = false;
                 
                 notifyAll ();
                 
             }
             // if there's any prefferer folders, shcedule earlier
             if (hasPrefferedFolder()) {
                t.schedule(1000);
             } else {
             // plan the task for next execution
                t.schedule (ms);
             }
        }
    }
    
    
    private void doLoop (int ms) {
        AbstractFileSystem system = (AbstractFileSystem)this.system.get ();
        if (system == null) {
            // end for ever the fs does not exist no more
            return;
        }
//        System.out.println("executing for =" + system.getDisplayName());
        VirtualsRefreshing refreshing = (VirtualsRefreshing)this.refresher.get();
        if (refreshing == null) {
            TopManager.getDefault().getErrorManager().log(ErrorManager.WARNING, "VcsRefreshRequest: Missing refresher. Please file a bug against vcscore module");
            return;
        }
        
        if (en == null || !en.hasMoreElements ()) {
            // start again from root
            en = existingFolders (refreshing);
        }

        FileObject prefFo = getPrefferedFolder();
        while (prefFo != null) {
            refreshing.doVirtualsRefresh(prefFo);
            if (shouldBeInterrupted()) {
                return;
            }
//            System.out.println("pref refre.." + prefFo.getName());
            prefFo = getPrefferedFolder();
        }

        for (int i = 0; i < REFRESH_COUNT && en.hasMoreElements (); i++) {
            FileObject fo = (FileObject)en.nextElement ();
            if (fo != null) {
                refreshing.doVirtualsRefresh(fo);
            }
            
            if (refreshTime <= 0 || shouldBeInterrupted()) {
                // after each refresh check the current value of refreshTime
                // again and if it goes to zero exit as fast a you can
                return;
            }
        }

        // clear the queue
        if (!en.hasMoreElements ()) {
            en = null;
        }
    }

    /** Existing folders for abstract file objects.
    */
    private static Enumeration existingFolders (VirtualsRefreshing fs) {
        return fs.getExistingFolders();
    }
}
