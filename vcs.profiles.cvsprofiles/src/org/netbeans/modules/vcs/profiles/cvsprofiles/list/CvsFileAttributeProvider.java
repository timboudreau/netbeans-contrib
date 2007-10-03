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

package org.netbeans.modules.vcs.profiles.cvsprofiles.list;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeProvider;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeProvider.MemoryCache;
import org.netbeans.modules.vcscore.turbo.*;
import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.CvsCreateFolderIgnoreList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.io.*;
import java.util.*;

/**
 * Reads attributes from <tt>CVS/Entries</tt> file. Supports
 * {@link FileProperties#ID}, {@link FolderProperties#ID} and
 * {@link IgnoreList#ID} attributes only.
 * <p>
 * It catches well working directory changes but
 * it cannot detect repository side changes
 * that are typically reported as <i>Needs Update</i>
 * status. But this status is reported for virtual FileObjects.
 * <p>
 * It does not listen on <tt>CVS/Entries</tt> file
 * external modifications. After all it's OK because
 * if IDE user needs fresh data it must call hard
 * refresh invalidating caches (here stale memory
 * layer that keeps invalid data).
 *
 * @author Petr Kuzel
 */
public final class CvsFileAttributeProvider implements FileAttributeProvider {

    // see also cvs.xml: POSSIBLE_FILE_STAUSES*
    private static final String VERSIONED_FOLDER_STATUS = "";  // NOI18N

    private static final String ENTRIES_LOG = "Entries.Log"; // NOI18N


    /** Must not be called directly exept from layer. */
    public CvsFileAttributeProvider() {
    }

    public boolean recognizesAttribute(String name) {
        return FileProperties.ID.equals(name) || FolderProperties.ID.equals(name) || IgnoreList.ID.equals(name);
    }

    /**
     * Does the file lies in CVS working directory.
     * The test is based on testing native FS (wrapped by MasterFS) profile name.
     */
    public boolean recognizesFileObject(FileObject fo) {
        Object vfs = fo.getAttribute("VcsFileSystemAttributeIdentifier");   // NOI18N
        if (vfs instanceof CommandLineVcsFileSystem) {
            CommandLineVcsFileSystem clifs = (CommandLineVcsFileSystem) vfs;
            Profile profile = clifs.getProfile();
            String name = profile.getType();
            if (name != null) {
                return "netbeans.cvsprofile".equals(name); // NOI18N
            } else {
                return isCvs(clifs); // When the type is not defined, try to check it for older profiles (see #55340).
            }
        }
        return false;
    }
    
    private static boolean isCvs(CommandLineVcsFileSystem clifs) {
        VcsCommand cmd = clifs.getCommand("CREATE_FOLDER_IGNORE_LIST");
        if (cmd == null) return false;
        String exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null && exec.indexOf("CvsCreateFolderIgnoreList.class") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {

        // fast recognition check

        if (recognizesAttribute(name) == false) return null;

        // load attribute
        // XXX see standard admin handler for structured info

        String targetName = fo.getNameExt();
        Object value = null;

        if (fo.isData()) {

            if (FolderProperties.ID.equals(name)) return null;
            if (IgnoreList.ID.equals(name)) return null;

            File parent = null;
            File f = FileUtil.toFile(fo);
            if (f != null) {
                parent = f.getParentFile();
            }
            if (parent == null) {
                return FileProperties.createLocal(fo.getNameExt()); // [local] not versioned
            }

            File entries = getCVSEntriesFile(parent);
            if (entries == null) {
                // [local] or ignored, missing Entries
                FileProperties fprops = new FileProperties();
                FileObject folder = fo.getParent();
                boolean ignored = IgnoreList.forFolder(folder).isIgnored(targetName);
                String status = ignored ? Statuses.STATUS_IGNORED : Statuses.getLocalStatus();
                status = applyParentStatus(fo, status);
                fprops.setStatus(status);
                return fprops;
            }

            // returns files only
            List lines = CvsListOffline.loadEntries(entries);
            Iterator it = lines.iterator();
            while (it.hasNext()) {
                String entry = (String) it.next();
                String[] elements = CvsListOffline.parseEntry(entry);
                String filename = elements[0];
                FileProperties fprops = new FileProperties();
                fprops.setName(filename);
                String revision = elements[1];
                if (revision.length() <= 4) revision.intern();
                fprops.setRevision(revision);
                fprops.setDate(elements[2]); // XXX parce to date and time fields
                File file = new File(parent, filename);
                String status;
                if (revision.startsWith("-")) {
                    status = "Locally Removed"; // NOI18N
                } else if (revision.equals("0")) {
                    status = "Locally Added"; // NOI18N
                } else {
                    status = CvsListOffline.getStatusFromTime(elements[2], file);
                }
                status = applyParentStatus(fo, status);
                fprops.setStatus(status);
                fprops.setAttr(elements[3]);
                String sticky = elements[4];
                if (sticky != null && sticky.length() > 0) {
                    sticky = sticky.substring(1, sticky.length());
                }
                fprops.setSticky(sticky);
                if (targetName.equals(filename)) {  // XXX on Win ignore case?
                    value = fprops;
                } else {
                    FileObject speculative = fo.getParent().getFileObject(filename);
                    if (speculative != null) {
                        memoryCache.cacheAttribute(speculative, FileProperties.ID, fprops);  // it'll polulate MEM cache
                    } else {
                        // not yet known virtual
                    }
                }
            }

            // no CVS/Entry exists => local or ignored file
            if (value == null) {
                value = FileProperties.createLocal(fo);
            }

        } else {

            // handle ignore list attribute

            File folder = FileUtil.toFile(fo);
            if (IgnoreList.ID.equals(name)) {
                ArrayList ignoreList = new ArrayList(5);
                File cvsignore = new File(folder, ".cvsignore");  // NOI18N
                if (cvsignore.exists() && cvsignore.canRead()) {
                    CvsCreateFolderIgnoreList.addFileIgnoreList(cvsignore, ignoreList);
                }
                return ignoreList;
            }

            File entries = getCVSEntriesFile(folder);
            if (entries != null) {
                if (FileProperties.ID.equals(name)) {
                    FileProperties fprops = new FileProperties();

                    // folder can have 4 statuses: Missing, Versioned, Local or Ignored

                    fprops.setName(targetName + '/');
                    if (folder.exists()) {
                        FileObject parent = fo.getParent();
                        if (parent == null) {
                            // FS root
                            fprops.setStatus(VERSIONED_FOLDER_STATUS);
                        } else {
                            // Regardless of the ignored state, the folder is versioned.
                            fprops.setStatus(VERSIONED_FOLDER_STATUS);
                        }
                    } else {
                        fprops.setStatus("Needs Checkout"); // NOI18N
                    }
                    value = fprops;
                } else if (FolderProperties.ID.equals(name)) {
                    FolderProperties fprops = new FolderProperties();
                    Set folderListing = folderListing(entries);
                    fprops.setFolderListing(folderListing);
                    fprops.setComplete(true);
                    value = fprops;
                }
            } else {
                if (FolderProperties.ID.equals(name)) {
                    return null;
                } else if (FileProperties.ID.equals(name)) {
                    // folder without CVS/Entries can have 3 statuses: Missing, Local or Ignored
                    FileProperties fprops = new FileProperties();
                    fprops.setName(targetName + '/');
                    if (folder.exists()) {
                        FileObject parent = fo.getParent();
                        if (parent == null) {
                            // FS root
                            fprops.setStatus(VERSIONED_FOLDER_STATUS);
                        } else {
                            boolean ignored = IgnoreList.forFolder(parent).isIgnored(targetName);
                            String status = ignored ? Statuses.STATUS_IGNORED : Statuses.getLocalStatus();
                            status = applyParentStatus(fo, status);
                            fprops.setStatus(status);
                        }
                    } else {
                        fprops.setStatus("Needs Checkout"); // NOI18N
                    }
                    value = fprops;
                }
            }
        }

        return value;

    }

    /**
     * Look up to workdir root and check actual subtree status.
     * Local in ignored statuses are inherited.
     * @param fo file object that parents are in question
     * @param status initial status of file object
     */
    private String applyParentStatus(FileObject fo, String status) {

        if (status == VERSIONED_FOLDER_STATUS) return status;  // inheritance exception
        if (status == Statuses.STATUS_IGNORED) return status;

        FileObject parent = fo.getParent();
        if (parent == null) {
            // working dir root
            return VERSIONED_FOLDER_STATUS;
        }

        FileProperties fprops = (FileProperties) readAttribute(parent, FileProperties.ID, null);
        String up = FileProperties.getStatus(fprops);

        if (up == Statuses.STATUS_IGNORED) return up;
        if (up == Statuses.getLocalStatus()) return up;
        return status;
    }

    /** Read Entries and Entries.log file and return set of FolderEntries*/
    private Set folderListing(File entries) {
        Set ret = new HashSet();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(entries));
            String line;
            while ((line = reader.readLine()) != null) {
                FolderEntry entry = createEntry(line);
                if (entry == null) continue;
                ret.add(entry);
            }
        } catch (FileNotFoundException fnfExc) {
            return null;
        } catch (IOException ioExc) {
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException exc) {
                // already closed
            }
        }

        try {
            File folder = entries.getParentFile();
            if (folder != null) {
                File log = new File(folder, ENTRIES_LOG);
                if (log.exists() && log.canRead()) {
                    reader = new BufferedReader(new FileReader(log));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("A ")) {  // NOI18N
                            String s = line.substring(2);
                            FolderEntry entry = createEntry(s);
                            if (entry == null) continue;
                            ret.add(entry);
                        } else if (line.startsWith("R ")) { // NOI18N
                            String s = line.substring(2);
                            FolderEntry entry = createEntry(s);
                            if (entry == null) continue;
                            ret.remove(entry);
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfExc) {
            return null;
        } catch (IOException ioExc) {
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException exc) {
                // already closed
            }
        }
        return ret;
    }

    /** convert Entries line syntax to FolderEntry. */
    private static FolderEntry createEntry(String line) {
        if (line.startsWith("/")) {       // NOI18N
            int end = line.indexOf('/', 1); // NOI18N
            if (end > 0) {
                String file = line.substring(1, end);
                return new FolderEntry(file, false);
            }
        } else if (line.startsWith("D/")) { // NOI18N
            int end = line.indexOf('/', 2); // NOI18N
            if (end > 0) {
                String file = line.substring(2, end);
                return new FolderEntry(file, true);
            }
        }
        return null;
    }

    /** No value is actually written here. */
    public synchronized boolean writeAttribute(FileObject fo, String name, Object value) {
        return recognizesFileObject(fo) && (FileProperties.ID.equals(name) || FolderProperties.ID.equals(name) || IgnoreList.ID.equals(name));
    }

    /** Get the CVS/Entries file below given folder.
     * @return the Entries file or <code>null</code> when the Entries file does not exist. */
    private static File getCVSEntriesFile(File folder) {
        File entries = new File(folder, "CVS" + File.separator + "Entries"); // NOI18N
        if (entries.exists() == false || entries.canRead() == false) return null;
        return entries;
    }
}
