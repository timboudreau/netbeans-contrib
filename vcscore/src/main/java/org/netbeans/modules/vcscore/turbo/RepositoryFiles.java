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
import org.openide.modules.InstalledFileLocator;
import org.openide.ErrorManager;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;

import java.util.*;
import java.io.*;

/**
 * Keeps track of all recently known repository files.
 * It's primary storage of such information.
 * <p>
 * The information persists, it's held by a FolderPropepties
 * attribute.
 * <p>
 * One prominent client is FileSystem impl that must
 * orchestrate with this class to provide stored
 * files as virtual FileObjects.
 * @author Petr Kuzel
 */
public final class RepositoryFiles {

    private final FileObject folder;

    public static final int FOLDER_MASK = 1;
    public static final int FILE_MASK = 0;

    /** Keeps scheduled removals FileObject(folder), Set&lt;String> */
    public static Map scheduledRemovals = new WeakHashMap();

    private RepositoryFiles(FileObject fo) {
        folder = fo;
    }

    /** Get RepositoryFiles for given folder. */
    public static RepositoryFiles forFolder(FileObject fo) {

        assert fo != null;

        return new RepositoryFiles(fo);
    }

    /**
     * Registers new repository file. Client must be sure
     * that just registered file has appeared in repository only.
     * Reverse case i.e. file disappeared from working
     * directory should be probably cought by another mechanisms
     * (complete folder refresh).
     *
     * @param fileName
     * @param mask union of <code>*_MASK</code> constancs
     */
    public synchronized void addFileObject(String fileName, int mask) {

        assert fileName.endsWith("/") == false;

        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
        if (fprops == null) {
            // Can be null when comes from FileReaderListener
            // Create non-complete folder properties
            fprops = new FolderProperties();
        }
        Set folderListing = fprops.getFolderListing();
        FolderEntry entry = new FolderEntry(fileName, (mask & FOLDER_MASK) != 0);
        Set updated;
        if (folderListing != null) {
            updated = new HashSet(folderListing);
            updated.add(entry);
        } else {
            updated = Collections.singleton(entry);
        }
        fprops = new FolderProperties(fprops);
        fprops.setFolderListing(updated);
        faq.writeAttribute(folder, FolderProperties.ID, fprops);
    }

    /**
     * Unregisters a repository file. The caller must be sure that
     * file really disappered from repository!
     */
    public synchronized void removeFileObject(String fileName) {
        assert fileName.endsWith("/") == false;

        Set scheduled = (Set) scheduledRemovals.get(folder);
        if (scheduled == null) {
            scheduled = new HashSet(2);
            scheduledRemovals.put(folder, scheduled);
        }
        scheduled.add(fileName);
    }

    /**
     * Must be called from FS.children to assure consistency see #53079.
     * @thread call under RepositoryFiles.class lock
     */
    public synchronized void commitRemoved() {

        Set scheduled = (Set) scheduledRemovals.get(folder);
        if (scheduled == null) return;
        scheduledRemovals.remove(folder);

        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
        if (fprops != null) {
            Set folderListing = fprops.getFolderListing();
            if (folderListing != null) {
                Set updated = new HashSet(folderListing.size());
                Iterator it = folderListing.iterator();
                while (it.hasNext()) {
                    FolderEntry next = (FolderEntry) it.next();
                    if (scheduled.contains(next.getName()) == false) {
                        updated.add(next);
                    }
                }
                fprops.setFolderListing(updated);
                faq.writeAttribute(folder, FolderProperties.ID, fprops);
            }
        }
    }

    /**
     * Classify given (registered) file. For unregistered files it fails.
     * @thread call under RepositoryFiles.class lock
     * @throws IllegalStateException if given file is not tracked in virtual files
     * it probbaly means that it's uncatched external removal (e.g. empty folder pruning on cvs checkout)
     */
    public synchronized boolean isFolder(String fileName) throws IllegalStateException {

        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
        if (fprops == null) {
            // Attribute is gone - the file should be gone as well - does not matter what we return
            return false;
        }
        Set folderListing = fprops.getFolderListing();
        if (folderListing == null) {
            // Attribute is gone - the file should be gone as well - does not matter what we return
            return false;
        }
        Iterator it = folderListing.iterator();
        while (it.hasNext()) {
            FolderEntry next = (FolderEntry) it.next();
            if (fileName.equals(next.getName())) {
                return next.isFolder();
            }
        }

        throw new IllegalStateException("Cannot determine folder flag for " + folder.getPath() + "/" + fileName);
    }

    /**
     * @return FolderEntry iterator of all known repository files
     * @thread call under RepositoryFiles.class lock
     */
    public synchronized Iterator virtualsIterator() {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
        if (fprops == null || fprops.getFolderListing() == null) {
            return Collections.EMPTY_SET.iterator();
        } else {

            return fprops.getFolderListing().iterator();
        }
    }


}
