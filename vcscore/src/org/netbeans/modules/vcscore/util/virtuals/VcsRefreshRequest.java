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
import java.util.LinkedList;
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
* @author Jaroslav Tulach
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
    
    private LinkedList preffered;

    /** Constructor
    * @param fs file system to refresh
    * @param ms refresh time
    */
    public VcsRefreshRequest(AbstractFileSystem fs, int ms, VirtualsRefreshing refresher) {
        system = new WeakReference (fs);
        this.refresher  = new WeakReference(refresher);
        refreshTime = ms;
        task = RequestProcessor.postRequest (this, ms, Thread.MIN_PRIORITY);
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

    public synchronized void addPrefferedFolder(FileObject folder) {
        if (preffered == null) {
            preffered = new LinkedList();
        }
        preffered.add(folder);
    }
    
    public synchronized FileObject getPrefferedFolder() {
        if (preffered == null || preffered.size() == 0) {
            return null;
        }
        FileObject toReturn = (FileObject)preffered.get(0);
        preffered.remove(0);
        return toReturn;
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
                 
                 notifyAll ();
                 
             }
             // plan the task for next execution
             t.schedule (ms);
        }
    }
    
    
    private void doLoop (int ms) {
        AbstractFileSystem system = (AbstractFileSystem)this.system.get ();
        if (system == null) {
            // end for ever the fs does not exist no more
            return;
        }

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
            prefFo = getPrefferedFolder();
        }

        for (int i = 0; i < REFRESH_COUNT && en.hasMoreElements (); i++) {
            FileObject fo = (FileObject)en.nextElement ();
            if (fo != null) {
                refreshing.doVirtualsRefresh(fo);
            }
            
            if (refreshTime <= 0) {
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
