/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakSet;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.api.vcs.VcsManager;

import java.io.File;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;

/**
 * Connects to repository to get fresh status.
 *
 * @author Petr Kuzel
 */
final class Repository {

    /**
     * Can return null on failure = unknown status.
     */
    public static FileProperties get(FileObject fileObject) {
        if (refresh(fileObject)) {
            // it was loaded by dirReaderListener
            FileProperties fprops = Memory.get(fileObject);
            return fprops;
        } else {
            return null;
        }
    }

    /**
     * Run REFRESH on single repository file. It's highly ineffecient
     * to ask for single file status because implementaion solves this
     * by asking for statuses of all files inr parent dir.
     * <p>
     * It delegates to profile provider. It can block for
     * several seconds. Results if any are inserted into
     * Memory layer (using DirReaderListener).
     *
     * @return true if command for sure reported some results to listeners
     */
    private static boolean refresh(FileObject fileObject) {

        System.out.println("[turbo] Repository.refresh(" + fileObject + ")");

        // TODO profiles does not define command for refreshing single file
        // there is only support for directory refresh and recursive directory refresh
        // if I want to refresh single file or folder status one must use parent folder refresh

        // single folder status WORKAROUND
        // it seems to come from CVS that supports stauses for files only
        // folder has only [local]/[versioned] status that cannot be directly retrieved
        // and is guessed in dirReaderListener implementation

        // single file status WORKAROUND
        // ask for all statuses in parent folder, one of them is the right one
        if (fileObject.isData()) {
            fileObject = fileObject.getParent();
        }

        VcsFileSystem fsystem = (VcsFileSystem) fileObject.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (fsystem != null) {
            CommandSupport engine;
            boolean offLine = ((VcsFileSystem)fsystem).isOffLine();
            if (offLine) {
                // VcsCommand.NAME_REFRESH_RECURSIVELY for recursive impl
                engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE);
            } else {
                engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH);
            }

            Command cmd = engine.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                File f = (File) fileObject.getAttribute(VcsAttributes.FILE_ATTRIBUTE);
                assert f != null;  // what if request comes for virtual file?
                ((VcsDescribedCommand) cmd).setDiskFiles(new File[] { f });

                ((VcsDescribedCommand) cmd).addDirReaderListener(TurboUtil.dirReaderListener(fsystem));
                // TODO this one is never called however it can mean that there exists single file refresh
                //((VcsDescribedCommand) cmd).addFileReaderListener(TurboUtil.fileReaderListener(fsystem));
                VcsManager.getDefault().showCustomizer(cmd);
                CommandTask task = cmd.execute();
                try {
                    task.waitFinished(10000); // give it 10s
                    // the command is not killed, it continues and will probably asynchronously report results
                    // I think it's OK, anyway it could be prevented by special dirReaderListener that would ignore results
                    return task.isFinished();
                } catch (InterruptedException e) {
                    return false;
                }
            }
        }

        return false;
    }

    /** Holds fileobject that were requested for background status retrieval. */
    private static final Set prepareRequests = Collections.synchronizedSet(new WeakSet());

    private static PreparationTask preparationTask;

    /** Tries to locate meta on disk or in repository */
    public static void prepareMeta(FileObject fileObject) {
        synchronized(Repository.class) {
            if (preparationTask == null) {
                preparationTask = new PreparationTask(prepareRequests);
                RequestProcessor.getDefault().post(preparationTask);
            }
        }

        prepareRequests.add(fileObject);
        preparationTask.notifyNewRequest();
    }

    /**
     * On background fetches data from repository.
     */
    private static class PreparationTask implements Runnable {

        private final Set requests;

        public PreparationTask(Set requests) {
            this.requests = requests;
        }

        public void run() {
            try {
                Thread.currentThread().setName("VCS Repository Fetcher");  // NOI18N
                while (true) {
                    waitForRequests();
                    FileObject[] fos;
                    synchronized(requests) {
                        fos = (FileObject[]) requests.toArray(new FileObject[0]);
                        requests.clear();
                    }
                    for (int i = 0; i<fos.length; i++) {
                        FileObject fo = fos[i];
                        if (fo == null) continue;
                        Repository.refresh(fo);
                    }
                }
            } catch (InterruptedException ex) {
                synchronized(Repository.class) {
                    // forget about recent requests
                    prepareRequests.clear();
                    preparationTask = null;
                }
            }
        }

        private synchronized void waitForRequests() throws InterruptedException {
            while (requests.size() == 0) wait();
        }

        public synchronized void notifyNewRequest() {
            notify();
        }
    }


}
