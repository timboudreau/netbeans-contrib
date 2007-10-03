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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakSet;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
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
import java.util.LinkedHashSet;

/**
 * Connects to repository to get fresh status. Supports
 * (timeouted) synchronous operations and asynchronous
 * operation.
 *
 * @author Petr Kuzel
 */
final class Repository {

    /**
     * Get fresh properties of given file/folder.
     *
     * @param fileObject file or folder to refresh. For folder it refreshs
     * folder status not folder content statuses.
     * @return fresh properties or <code>null</code> if cannot be retrieved
     * (e.g. timeout or invalid request).
     */
    public static FileProperties get(FileObject fileObject) {

        FileObject parent = normalize(fileObject);
        if (parent == null) return null;

        if (refreshFolderContent(parent)) {
            // it was loaded by dirReaderListener
            FileProperties fprops = (FileProperties) FileAttributeQuery.getDefault().readAttribute(fileObject, FileProperties.ID);
            
            assert attributeMatchesFile(fprops, fileObject) : "Bad properties for FileObject "+fileObject+": "+fprops;  // NOI18N
            return fprops;
        } else {
            return null;
        }
    }

    private static boolean attributeMatchesFile(FileProperties fprops, FileObject fileObject) {
        if (fprops == null) return true;
        String fname = fprops.getName();
        if (fname.endsWith("/")) fname = fname.substring(0, fname.length() - 1);  // NOI18N
        return fileObject.getNameExt().equals(fname);
    }

    /**
     * Refresh properties of all files in repository folder.
     * <p>
     * It delegates to profile provider. It can block for
     * several seconds. Results if any are inserted into
     * Memory layer (using DirReaderListener).
     *
     * @param fileObject folder which content needs refresh
     * @return true if command for sure reported some results to listeners
     */
    public static boolean refreshFolderContent(FileObject fileObject) {

        assert fileObject.isFolder();

        IgnoreList.invalidate(fileObject);

        VcsFileSystem fsystem = (VcsFileSystem) fileObject.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (fsystem != null) {
            CommandSupport engine;
            boolean offLine = fsystem.isOffLine();
            if (offLine) {
                engine = fsystem.getCommandSupport(VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE);
                if (engine == null) {
                    engine = fsystem.getCommandSupport(VcsCommand.NAME_REFRESH);
                }
            } else {
                engine = fsystem.getCommandSupport(VcsCommand.NAME_REFRESH);
            }
            
            if (engine == null) {
                throw new IllegalStateException("Command "+VcsCommand.NAME_REFRESH+" is not defined in "+fsystem);
            }

            Command cmd = engine.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                FileObject[] applicable = cmd.getApplicableFiles(new FileObject[] {fileObject});
                if (applicable == null || applicable.length == 0) return true;
                cmd.setFiles(applicable);
                cmd.setGUIMode(false);


                ((VcsDescribedCommand) cmd).addDirReaderListener(TurboUtil.dirReaderListener(fsystem));
                if (VcsManager.getDefault().showCustomizer(cmd)) {
                    CommandTask task = cmd.execute();
                    try {
                        task.waitFinished(0);
                        int exitStatus = task.getExitStatus();
                        return exitStatus == CommandTask.STATUS_SUCCEEDED;
                    } catch (InterruptedException e) {
                        return false;
                    } finally {
                        if (task.isRunning()) task.stop();
                    }
                }
            }
        }

        return false;
    }

    /**
     * REcursively refresh properties of all files in repository folder.
     * <p>
     * It delegates to profile provider. It can block for
     * several seconds. Results if any are inserted into
     * Memory layer (using DirReaderListener).
     *
     * @param fileObject folder which content needs refresh
     * @return true if command for sure reported some results to listeners
     */
    public static boolean refreshFolderContentRecusively(FileObject fileObject) {

        if (fileObject.isData()) {
            fileObject = fileObject.getParent();
        }

        IgnoreList.invalidate(fileObject);

        VcsFileSystem fsystem = (VcsFileSystem) fileObject.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (fsystem != null) {
            CommandSupport engine;
            boolean offLine = ((VcsFileSystem)fsystem).isOffLine();
            if (offLine) {
                engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH_RECURSIVELY + VcsCommand.NAME_SUFFIX_OFFLINE);
            } else {
                engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH_RECURSIVELY);
            }

            if (engine == null) {
                // profile does not support recursive refresh, emulate it
                return refreshFolderContentDeeply(fileObject);
            } else {
                Command cmd = engine.createCommand();
                if (cmd instanceof VcsDescribedCommand) {
                    FileObject[] applicable = cmd.getApplicableFiles(new FileObject[] {fileObject});
                    if (applicable == null || applicable.length == 0) return true;
                    cmd.setFiles(applicable);
                    cmd.setGUIMode(false);

                    ((VcsDescribedCommand) cmd).addDirReaderListener(TurboUtil.dirReaderListener(fsystem));
                    if (VcsManager.getDefault().showCustomizer(cmd)) {
                        CommandTask task = cmd.execute();
                        try {
                            task.waitFinished(0);
                            int exitStatus = task.getExitStatus();
                            return exitStatus == CommandTask.STATUS_SUCCEEDED;
                        } catch (InterruptedException e) {
                            return false;
                        } finally {
                            if (task.isRunning()) task.stop();
                        }
                    }
                }
            }
        }

        return false;
    }

    /** Emulates recursine folder refreshing for dump profiles. */
    private static boolean refreshFolderContentDeeply(FileObject folder) {
        if (refreshFolderContent(folder) == false) return false;

        IgnoreList.invalidate(folder);
        FileObject[] content = folder.getChildren();
        for (int i = 0; i < content.length; i++) {
            FileObject fileObject = content[i];
            if (fileObject.isFolder()) {
                boolean ret = refreshFolderContentDeeply(fileObject); // recursion
                if (ret == false) return false;
            }
        }
        return true;
    }

    /**
     * profiles does not define command for refreshing single file
     * there is only support for directory refresh and recursive directory refresh
     * if I want to refresh single file or folder status one must use parent folder refresh
     * XXX FileReaderListener means that there exists single file refresh
     * but in reality it exists just for some profiles LIST_FILE
     */
    private static FileObject normalize(FileObject fileObject) {

        if (fileObject.isFolder()) {
            // single folder status WORKAROUND
            // it seems to come from CVS that supports statuses for files only
            // folder has only [local]/[versioned] status that cannot be directly retrieved
            // and is guessed in dirReaderListener implementation
            fileObject = fileObject.getParent();

        } else {
            // single file status WORKAROUND
            // ask for all statuses in parent folder, one of them is the right one
            // On the other hand for most cases it's ineffecient
            // to ask for single file status because most clients
            // iterate over folder content anyway
            fileObject = fileObject.getParent();
        }
        return fileObject;
    }

    /** Holds fileobject that were requested for background status retrieval. */
    private static final Set prepareRequests = Collections.synchronizedSet(new LinkedHashSet(43));

    private static PreparationTask preparationTask;

    /**
     * Asynchronously refresh properties of given file/folder.
     *
     * @param fileObject file or folder to refresh. For folder it refreshs
     * folder status not folder content statuses.
     * @return fresh properties or <code>null</code> if cannot be retrieved
     * (e.g. timeout or invalid request).
     */
    public static void prepareMeta(FileObject fileObject) {

        fileObject = normalize(fileObject);
        if (fileObject == null) return;

        synchronized(prepareRequests) {
            if (prepareRequests.add(fileObject)) {
                if (preparationTask == null) {
                    preparationTask = new PreparationTask(prepareRequests);
                    RequestProcessor.getDefault().post(preparationTask);
                }
                preparationTask.notifyNewRequest();
            }
        }
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
                while (waitForRequests()) {
                    FileObject fo;
                    synchronized (requests) {
                        fo = (FileObject) requests.iterator().next();
                        requests.remove(fo);
                    }
                    if (fo == null) continue;
                    Repository.refreshFolderContent(fo);
                }
            } catch (InterruptedException ex) {
                synchronized(requests) {
                    // forget about recent requests
                    requests.clear();
                }
            } finally {
                synchronized(requests) {
                    preparationTask = null;
                }
            }
        }

        private boolean waitForRequests() throws InterruptedException {
            synchronized(requests) {
                if (requests.size() == 0) requests.wait(1000 * 271 ); // 4,5 sec
                return requests.size() > 0;
            }
        }

        public void notifyNewRequest() {
            synchronized (requests) {
                requests.notify();
            }
        }
    }

}
