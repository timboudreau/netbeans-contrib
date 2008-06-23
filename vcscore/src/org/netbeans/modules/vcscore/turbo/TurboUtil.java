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
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.VcsProvider;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import javax.swing.*;
import java.util.*;
import java.io.File;
import org.openide.ErrorManager;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;

/**
 * Various utility methods eliminating boilerplate constructions
 * over {@link Turbo} API goes here.
 *
 * @author Petr Kuzel
 */
public final class TurboUtil {

    /** A synchronization object for refresh requests. It helps to synchronize
        only refreshes of hierarchically dependent folders. */
    private static final Object SYNCHRON_REFRESH = new Object();

    /** The FileObjects currently being refreshed. */
    private static Set refreshingFileObjects = new HashSet();

    private TurboUtil() {
        // only static methods
    }

    /** Return all filder in given folder (inluding virtuals) never <code>null</code>.*/
    public static FileObject[] listFolders(FileObject fileObject) {
        FileObject fo[] = fileObject.getChildren();
        List ret = new ArrayList(fo.length);
        for (int i = 0; i<fo.length; i++) {
            if (fo[i].isFolder()) {
                ret.add(fo[i]);
            }
        }
        return (FileObject[]) ret.toArray(new FileObject[ret.size()]);
    }

    /** Return all files in given folder (inluding virtuals) never <code>null</code>.*/
    public static FileObject[] listFiles(FileObject fileObject) {
        FileObject fo[] = fileObject.getChildren();
        List ret = new ArrayList(fo.length);
        for (int i = 0; i<fo.length; i++) {
            if (fo[i].isData()) {
                ret.add(fo[i]);
            }
        }
        return (FileObject[]) ret.toArray(new FileObject[ret.size()]);
    }

    /** MUST be called under a lock on SYNCHRON_REFRESH object */
    private static void waitForParentRefreshes(FileObject folder) {
        boolean needsWait;
        do {
            needsWait = false;
            for (Iterator it = refreshingFileObjects.iterator(); it.hasNext(); ) {
                FileObject rfo = (FileObject) it.next();
                if (rfo.equals(folder) || FileUtil.isParentOf(rfo, folder)) {
                    needsWait = true;
                    break;
                }
            }
            if (needsWait) {
                try {
                    SYNCHRON_REFRESH.wait();
                } catch (InterruptedException iex) {
                    // Continue, next round will block again
                }
            }
        } while (needsWait);
    }

    /**
     * Refreshes folder content's metadata.
     * @return true If the folder was successfully refreshed.
     */
    public static boolean refreshFolder(FileObject folder) {
        if (folder == null) return false;
        if (folder.isFolder() == false) return false;

        assert SwingUtilities.isEventDispatchThread() == false;

        synchronized (SYNCHRON_REFRESH) {
            waitForParentRefreshes(folder);
            refreshingFileObjects.add(folder);
        }
        try {
            return Repository.refreshFolderContent(folder);
        } finally {
            synchronized (SYNCHRON_REFRESH) {
                refreshingFileObjects.remove(folder);
                SYNCHRON_REFRESH.notifyAll();
            }
        }
    }

    /**
     * Recursively refreshes folder content's metadata.
     * @return true If the folder was successfully refreshed.
     */
    public static boolean refreshRecursively(FileObject folder) {
        if (folder == null) return false;
        if (folder.isFolder() == false) return false;

        assert SwingUtilities.isEventDispatchThread() == false;

        synchronized (SYNCHRON_REFRESH) {
            waitForParentRefreshes(folder);
        }
        // Wait for any parent refreshes, but do not block execution
        // of simple refresh. It's necessary because:
        // 1) rec. refresh can spawn non-rec. refreshes recursively
        // 2) rec. refresh can take a long time, simple refresh should not
        //    be required to wait for it.
        return Repository.refreshFolderContentRecusively(folder);
    }

    /**
     * Populates cache by command output ({@link VcsCache#readDirFinished}).
     *
     * @param provider provider that allows to properly match command
     *   output to fileobjects
     * @param path directory that was read by VcsDirReader relative to the provider root
     * @param rawData vector of <CODE>String[]</CODE> that describes files and subdirectories excluding local files
     * @param success whether the refresh command succeeded.
     * @param complete true means that all folder files on repository were reported and it's safe to guess local files
     */
    private static void populateCache(VcsProvider provider, String path, Collection rawData, boolean success, boolean complete) {

        if (success == false) return;

        // path is folder relative to FS root then raw data contains children
        FileObject fileObject = provider.findResource(path);    // "" denotes root

        if (fileObject == null) {

            // try to locate first live parent fileobject then refresh fs children

            String[] atoms = path.split("/"); // NOI18N
            FileObject lastLive = provider.findResource(""); // NOI18N
            for (int i = 0; i<atoms.length; i++) {
                String atom = atoms[i];
                if ("".equals(atom)) continue; // NOI18N
                FileObject newLive = lastLive.getFileObject(atom);
                if (newLive == null) {
                    lastLive.refresh(true);
                    newLive = lastLive.getFileObject(atom);
                }

                // here we relax assert bellow for windows systems
                if (newLive == null && Utilities.isWindows()) {
                    // #52596 XXX probably Windows case sensitivity problem
                    IllegalStateException ex = new IllegalStateException();
                    ErrorManager.getDefault().annotate(ex, NbBundle.getMessage(Statuses.class, "ise52596", path));
                    ErrorManager.getDefault().notify(ErrorManager.USER, ex);
                    // Try to find the correct file
                    String latom = atom.toLowerCase();
                    FileObject[] children = lastLive.getChildren();
                    for (int j = 0; j < children.length; j++) {
                        if (children[j].getNameExt().toLowerCase().equals(latom)) {
                            newLive = children[j];
                            break;
                        }
                    }
                }
                lastLive = newLive;
                assert lastLive != null : "Path pointing to nowhere: " + path + " at: " + atom;  // NOI18N
            }
            fileObject = lastLive;
        }

        if (fileObject == null) {
            assert false : "Unexpected FS root path pointing to nowhere: " + path;  // NOI18N
            populateVirtuals(path, provider, fileObject);
        } else if (fileObject.isFolder()) {
            populateFolder(fileObject, rawData, complete);

        } else {  // data

            assert false : "Unexpected refresh format: " + path;  // NOI18N
        }
    }

    private static void populateFolder(FileObject folder, Collection rawData, boolean complete) {
        FileProperties fprops;
        List localCandidates;
        Set folderListing;
        List extraFiles;
        boolean wereRemovedFiles = false;
        if (complete) {
            File file = FileUtil.toFile(folder);
            String[] kids = file.list();
            if (kids == null) {
                localCandidates = Collections.EMPTY_LIST;
            } else {
                localCandidates = new ArrayList(Arrays.asList(kids));
            }
            folderListing = new HashSet(rawData.size());
            FileObject[] children = folder.getChildren();
            extraFiles = new ArrayList(children.length);
            for (int i = 0; i < children.length; i++) {
                extraFiles.add(children[i].getNameExt());
            }
        } else {
            localCandidates = Collections.EMPTY_LIST;
            folderListing = null;
            extraFiles = Collections.EMPTY_LIST;
        }

        Iterator it = rawData.iterator();

        // update virtual files if listing is complete
        if (folderListing != null) {
            while (it.hasNext()) {
                String[] next = (String[]) it.next();
                String fileName = next[StatusFormat.ELEMENT_INDEX_FILE_NAME];
                if (fileName == null) {
                    String msg = "Bad data produced by refresh: "+VcsUtilities.array2string(next)+", file name = "+fileName;
                    ErrorManager.getDefault().notify(ErrorManager.USER,
                            ErrorManager.getDefault().annotate(
                            new IllegalArgumentException(msg), msg));
                    ErrorManager.getDefault().log(ErrorManager.getDefault().ERROR, msg);
                    continue;
                }

                String  name = fileName;
                boolean isFolder = false;
                if (fileName.endsWith("/")) { // NOI18N
                    name = fileName.substring(0, fileName.length() -1);
                    isFolder = true;
                }

                assert name.indexOf('/') == -1 && name.indexOf(File.separatorChar) == -1 : "Unexpected file name: " + name;  // NOI18N
                folderListing.add(new FolderEntry(name, isFolder));
            }
            FileAttributeQuery faq = FileAttributeQuery.getDefault();
            FolderProperties folderProps = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
            if (folderProps == null) {
                folderProps = new FolderProperties();
                folderProps.setFolderListing(folderListing);
            } else {
                folderProps = new FolderProperties(folderProps);
                folderProps.setFolderListing(folderListing);
            }
            folderProps.setComplete(complete);
            faq.writeAttribute(folder, FolderProperties.ID, folderProps);

            // Initialize the iterator again
            it = rawData.iterator();
        }

        while (it.hasNext()) {
            String[] next = (String[]) it.next();
            String fileName = next[StatusFormat.ELEMENT_INDEX_FILE_NAME];
            if (fileName == null) {
                String msg = "Bad data produced by refresh: "+VcsUtilities.array2string(next)+", file name = "+fileName;
                ErrorManager.getDefault().notify(ErrorManager.USER,
                        ErrorManager.getDefault().annotate(
                        new IllegalArgumentException(msg), msg));
                ErrorManager.getDefault().log(ErrorManager.getDefault().ERROR, msg);
                continue;
            }

            String  name = fileName;
            boolean isFolder = false;
            if (fileName.endsWith("/")) { // NOI18N
                name = fileName.substring(0, fileName.length() -1);
                isFolder = true;
            }

            assert name.indexOf('/') == -1 && name.indexOf(File.separatorChar) == -1 : "Unexpected file name: " + name;  // NOI18N

            extraFiles.remove(name);

            String status;
            if (next.length == 1) {
                // commit deleted file command
                // invalidate values
                FileObject fo = folder.getFileObject(name);
                RepositoryFiles.forFolder(folder).removeFileObject(name);
                if (fo != null) {
                    // If the file happens to exist, mark it as [local], otherwise do not care - the file is gone
                    FileProperties deleted = null;
                    if (fo.isVirtual() == false) {
                        deleted = FileProperties.createLocal(fo);;
                    }
                    Turbo.setMeta(fo, deleted);
                    wereRemovedFiles = true;
                }
                continue;
            } else {
                status = next[StatusFormat.ELEMENT_INDEX_STATUS];
                assert status != null;
            }

            FileObject fo = folder.getFileObject(name);

            if (fo == null) {
                // it's new virtual not yet known by FS
                int mask = isFolder ? RepositoryFiles.FOLDER_MASK : 0;
                RepositoryFiles.forFolder(folder).addFileObject(name, mask);
                Throwable thr = null;
                try {
                    folder.refresh(true);
                    fo = folder.getFileObject(name);
                } catch (ThreadDeath td) {
                    throw td;
                } catch (Throwable t) {
                    // Something went wrong, a bad name was likely provided
                    thr = t;
                }
                if (fo == null) {
                    // Can be a backup file or anything else that is filtered...
                    // We will not add anything that is not presented anywhere to the cache
                    if (name.endsWith("~")) { // ignore backups
                        continue;
                    }
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Bad resource '"+name+"' at "+folder);
                    if (thr != null) ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, thr);
                    RepositoryFiles.forFolder(folder).removeFileObject(name);
                    continue;
                }
            }
            fprops = new FileProperties(next);
            Turbo.setMeta(fo, fprops);
            localCandidates.remove(fo.getNameExt());

        }  // ~while

        // set [local] and [ignored] statuses overhere

        it = localCandidates.iterator();
        while (it.hasNext()) {
            String next = (String) it.next();
            FileObject fo = folder.getFileObject(next);
            if (fo != null) {
                fprops = FileProperties.createLocal(fo);
                Turbo.setMeta(fo, fprops);
            }
        }

        it = extraFiles.iterator();
        while (it.hasNext()) {
            String next = (String) it.next();
            RepositoryFiles.forFolder(folder).removeFileObject(next);
            wereRemovedFiles = true;
        }
        if (wereRemovedFiles) {
            // Refresh the folder to release the removed files
            folder.refresh(true);
        }
    }

    private static void populateVirtuals(String path, VcsProvider provider, FileObject fileObject) {
//        Stack fileNames = new Stack();
//        String fileName = null;
//        while (path.length() > 0) {
//            int index = path.lastIndexOf('/');
//            if (index < 0) {
//                fileName = path;
//                path = "";
//            } else {
//                fileName = path.substring(index + 1);
//                path = path.substring(0, index);
//            }
//            fileObject = fileSystem.findResource(path);
//            if (fileObject == null) {
//                fileNames.push(fileName);
//            } else {
//                break;
//            }
//        }
//        assert fileObject != null: "Resource does not exist: "+path; // At least the root must exist
//        if (fileObject != null) {
//            fileObject.refresh();
//            if (fileObject.getFileObject(fileName) == null) {
//                RepositoryFiles.forFolder(fileObject).addFileObject(fileName, RepositoryFiles.FOLDER_MASK);//mask);
//                fileObject.refresh();
//            }
//            fileObject = fileObject.getFileObject(fileName);
//            while (!fileNames.empty()) {
//                fileName = (String) fileNames.pop();
//                fileObject.refresh(); // The parent file object
//                if (fileObject.getFileObject(fileName) == null) {
//                    RepositoryFiles.forFolder(fileObject).addFileObject(fileName, RepositoryFiles.FOLDER_MASK);//mask);
//                    fileObject.refresh();
//                }
//                fileObject = fileObject.getFileObject(fileName);
//            }
//        }
    }


    /**
     * Returns FileReaderListener implementation that populates
     * the cache from the command data execuded over given FS.
     * <p>
     * It's used by e.g. LIST command.
     *
     * @param fs provider that allows to properly match command
     *   output to fileobjects
     */
    public static FileReaderListener fileReaderListener(VcsProvider fs) {
        return new FileReaderListenerImpl(fs);
    }


    /**
     * Returns DirReaderListener implementation that populates
     * the cache from the command data execuded over given FS.
     *
     * @param fs provider that allows to properly match command
     *   output to fileobjects
     */
    public static DirReaderListener dirReaderListener(VcsProvider fs) {
        return new DirReaderListenerImpl(fs);
    }

    /**
     * Populates the cache from command output data.
     */
    static final class FileReaderListenerImpl implements FileReaderListener {

        private final VcsProvider provider;

        private FileReaderListenerImpl(VcsProvider provider) {
            this.provider = provider;
        }

        public void readFileFinished(String path, Collection rawData) {
            populateCache(provider, path, rawData, true, false);
        }

    }

    static final class DirReaderListenerImpl implements DirReaderListener {

        private final VcsProvider provider;

        private DirReaderListenerImpl(VcsProvider provider) {
            this.provider = provider;
        }

        public void readDirFinished(String path, Collection rawData, boolean success) {
            populateCache(provider, path, rawData, success, true);
        }

        // it's typically comming from refresh recursively command all other commands
        // use sequence of readDirFinished or readFileFinished
        public void readDirFinishedRecursive(String cmdPath, VcsDirContainer rawData, boolean success) {

            if (success == false) return;

            // path is folder relative to FS root then raw data contains children
            String path = rawData.getPath();
            /*
            FileObject folder = provider.findResource(path);    // "" denotes root
            assert folder != null: "Non-existing path : '"+path+"'";
            assert folder.isFolder();
             *
             * The above should succeed in most cases. However, someone might
             * remove the files in between. (like issue #56046).
             */

            Map fileToRawData = (Map) rawData.getElement(); // it's a hashmap<name, rawData.element>
            if (fileToRawData == null) return;
            Collection extractedRawData = fileToRawData.values();
            populateCache(provider, path, extractedRawData, success, true);

            VcsDirContainer subdirs[] = rawData.getSubdirContainers();
            Set processedNames = new HashSet();
            for (int i = 0; i < subdirs.length; i++) {
                VcsDirContainer container = subdirs[i];
                readDirFinishedRecursive(cmdPath, container, success);  // recursion
                processedNames.add(container.getName());
            }
            populateUnreportedLocals(path, processedNames, success);
        }

        /** Mark as local files that exist, but were not reported by refresh. */
        private void populateUnreportedLocals(String path, Collection processedNames, boolean success) {
            FileObject fo = provider.findResource(path);
            if (fo == null) return ;
            FileObject[] kids = fo.getChildren();
            for (int i = 0; i < kids.length; i++) {
                if (!processedNames.contains(kids[i].getNameExt()) && kids[i].isFolder()) {
                    String kidPath = (path.length() > 0) ? path + "/" + kids[i].getNameExt() : kids[i].getNameExt();
                    populateCache(provider, kidPath, Collections.EMPTY_SET, success, true);
                    populateUnreportedLocals(kidPath, Collections.EMPTY_SET, success);
                }
            }
        }
    }
}
