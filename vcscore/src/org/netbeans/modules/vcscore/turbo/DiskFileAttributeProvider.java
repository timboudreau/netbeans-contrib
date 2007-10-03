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
import org.openide.ErrorManager;
import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeProvider;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.turbo.log.Statistics;

import java.io.*;
import java.util.*;

/**
 * Represents disk cache layer. These operation can take while,
 * consider typical disk assess time about 10ms per random seek.
 * <p>
 * Because FileObject.set/getAttribute does not scale it
 * uses it's own storage format in dedicated text files.
 * The format is desribed in StatusFormat class and encoded
 * using UTf-8 and entries separated by UNIX new lines.
 * <p>
 * It's temporary solution that will be obsoleted by
 * DefaultFileAttributeProvider implementation. As temporary
 * solution is uses private contract with FileAttributeQuery
 * being registered under privileged <code>Services/FileAttributeProviders/Turbo</code>.
 *
 * @author Petr Kuzel
 */
final class DiskFileAttributeProvider implements FileAttributeProvider {

    private static final int BUFFER_SIZE = 1024 * 16;  // average cache file size is about 5kB

    private static FileAttributeProvider instance;

    /** T9Y entry point */
    private static File testCacheFile;

    /** T9Y entry point. Forces rewrite mode. */
    private static boolean testRewrite;

    /**
     * Accessible using efault lookup only. This method is
     * used for filesystem based registration.
     */
    public static synchronized FileAttributeProvider getDefault() {
        if (instance == null) {
            instance = new DiskFileAttributeProvider();
        }
        return instance;
    }

    private DiskFileAttributeProvider() {
        // use getDefault
    }

    public boolean recognizesAttribute(String name) {
        return FileProperties.ID.equals(name) || FolderProperties.ID.equals(name);
    }

    public boolean recognizesFileObject(FileObject fo) {
        return true;
    }

    public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
        try {
            if (FileProperties.ID.equals(name)) {
                return readFileProperties(fo, memoryCache);
            } else if (FolderProperties.ID.equals(name)) {
                if (fo.isFolder()) {
                    return readFolderProperties(fo);
                }
            }
        } catch (IOException e) {
            // notify at lowest level we run at background
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
    }

    public boolean writeAttribute(FileObject fo, String name, Object value) {
        try {
            if (FileProperties.ID.equals(name)) {
                writeFileProperties(fo, (FileProperties) value);
            } else if (FolderProperties.ID.equals(name)) {
                writeFolderProperties(fo, (FolderProperties) value);
            } else {
                return false;
            }
        } catch (IOException e) {
            // notify at lowest level we run at background
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return true;
    }

    // Disk I/O ~~~~~~~~
    // The implementation stores statuses of all files in single directory in one file.
    // It's because profiles provide cache file per directory.

    private static final Object IO_LOCK = new IOLock();

    /** FileProperties list */
    private static final List sharedList = new ArrayList(327);

    /** 1.1 version added support for FileProperties.getFolderListing(). */
    private static final String FILE_HEADER_1_1 = "#vcs.turbo.version=1.1";  // NOI18N

    private static void writeFileProperties(FileObject fileObject, FileProperties fprops) throws IOException {
        File file = FileUtil.toFile(fileObject);
        File cacheFile = filePropertiesCache(file);
        if (cacheFile == null) return;

        assert nameMustMatchFileInvariant(fileObject, fprops);

        // TODO null fprops means remove the attribute

        synchronized(IO_LOCK) {

            // for effeciency reasons append data to file end without merging
            // content on each write, once after 5 min do full writeFromScratch

            final long FIVE_MIN = 1000 * 60 * 5;
            boolean writeFromScratch = false;
            boolean complete = false;

            sharedList.clear();

            if (cacheFile.exists() && checkVersion(cacheFile) == false) {
                cacheFile.delete();  // XXX conver from previous versions
            }

            if (cacheFile.exists()) {
                long now = System.currentTimeMillis();
                long updatedOn = cacheFile.lastModified();
                if (now < updatedOn || (now - updatedOn) > FIVE_MIN) {
                    complete = readCacheFile(cacheFile, sharedList);
                    writeFromScratch = true;
                }
            } else {
                File cacheDir = cacheFile.getParentFile();
                if (cacheDir.exists() == false) cacheDir.mkdirs();
                cacheFile.createNewFile();
                writeFromScratch = true;
            }

            if (writeFromScratch == false && testRewrite == false) {
                appendToCacheFile(cacheFile, fprops);
                return;
            }

            // merge list content with written value and remove duplicated data
            // note that entries at file end are more current than at the begining

            Statistics.diskRewrite();
            final String writtenName = fprops != null ? fprops.getName() : "";

            Set duplicates = new HashSet(3);
            Map previousValues = new HashMap(sharedList.size());  // filename -> properties
            Iterator it = sharedList.iterator();
            while (it.hasNext()) {
                FileProperties next = (FileProperties) it.next();
                String fileName = next.getName();
                if (writtenName.equals(fileName)) {
                    duplicates.add(next);
                } else if (previousValues.keySet().contains(fileName)) {
                    Object stale = previousValues.get(fileName);
                    duplicates.add(stale);
                }
                previousValues.put(fileName, next);
            }
            sharedList.removeAll(duplicates);

            if (fprops != null) sharedList.add(fprops);

            rewriteCacheFile(cacheFile, complete);
        }
    }

    private static boolean nameMustMatchFileInvariant(FileObject fileObject, FileProperties fprops) {
        if (fprops == null) return true;
        if (fileObject.isFolder()) {
            return (fileObject.getNameExt() + "/").equals(fprops.getName()); // NOI18N
        } else {
            return fileObject.getNameExt().equals(fprops.getName());
        }
    }

    private static void writeFolderProperties(FileObject fileObject, FolderProperties fprops) throws IOException {

        // it's not needed in VCS usecases. Theoretical problem unveild by general attribute tests
        assert fprops != null : "Semantics of writing null FolderProperties attribute is not yet defined!";  // NOI18N

        File file = FileUtil.toFile(fileObject);
        File cacheFile = folderPropertiesCache(file);
        if (cacheFile == null) return;

        if (fprops != null && fprops.getFolderListing() == null) return;
        synchronized(IO_LOCK) {

            // read original values

            sharedList.clear();
            if (fprops == null) {
                // delete the complete flag it virtualy removes FolderProperties attribute
                readCacheFile(cacheFile, sharedList);
                rewriteCacheFile(cacheFile, false);
                return;
            }

            if (cacheFile.exists()) {
                readCacheFile(cacheFile, sharedList);
            } else {
                File cacheDir = cacheFile.getParentFile();
                if (cacheDir.exists() == false) cacheDir.mkdirs();
                cacheFile.createNewFile();
            }

            // merge list content with written value and remove duplicated data
            // note that entries at file end are more current than at the begining

            final Set entries = fprops.getFolderListing();
            final boolean complete = fprops.isComplete();
            final Set folderListing = new HashSet(entries.size());
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                FolderEntry next = (FolderEntry) it.next();
                String fileName = next.isFolder() ? next.getName() + '/' : next.getName();
                folderListing.add(fileName);
            }

            Set duplicates = new HashSet(3);
            Set missing = new HashSet(3);
            Map previousValues = new HashMap(folderListing.size());  // filename -> properties
            it = sharedList.iterator();
            while (it.hasNext()) {
                FileProperties next = (FileProperties) it.next();
                String fileName = next.getName();
                if (folderListing.contains(fileName) == false) {
                    missing.add(next);
                } else if (previousValues.keySet().contains(fileName)) {
                    Object stale = previousValues.get(fileName);
                    duplicates.add(stale);
                }
                previousValues.put(fileName, next);
            }
            sharedList.removeAll(duplicates);
            if (complete) sharedList.removeAll(missing);

            it = entries.iterator();
            while (it.hasNext()) {
                FolderEntry next = (FolderEntry) it.next();
                String name = next.isFolder() ? next.getName() + '/' : next.getName();
                if (previousValues.keySet().contains(name) == false) {
                    FileProperties mprops = null;
                    FileAttributeQuery faq = FileAttributeQuery.getDefault();
                    if (faq.isPrepared(fileObject, FileProperties.ID)) {
                        mprops = (FileProperties) faq.readAttribute(fileObject, FileProperties.ID);
                    }

                    if (mprops == null) {
                        mprops = new FileProperties();
                        mprops.setName(name);
                    }
                    sharedList.add(mprops);
                }
            }

            assert (!complete) || sharedList.size() <= folderListing.size() : "cache " + cacheFile + " data " + sharedList;  // there are probably merging error
            rewriteCacheFile(cacheFile, true);
        }
    }

    /**
     * @param fo for which to load the FileProperties
     */
    private static FileProperties readFileProperties(FileObject fo, MemoryCache cache) throws IOException {
        File file = FileUtil.toFile(fo);
        File cacheFile = filePropertiesCache(file);
        if (cacheFile == null) return null;
        FileObject folder = fo.getParent();

        String fileName = file.getName();
        synchronized(IO_LOCK) {
            sharedList.clear();
            readCacheFile(cacheFile, sharedList);
            Iterator it = sharedList.iterator();
            Map entries = new HashMap(sharedList.size()*2);
            while (it.hasNext()) {
                FileProperties next = (FileProperties) it.next();
                String name = next.getFileName();
                entries.put(name, next);  // possibly overwrites previous value
            }

            // here we can silently (without firing event)
            // because there is high probability that request for siblings will arrive soon
            it = entries.values().iterator();
            while (it.hasNext()) {
                FileProperties next = (FileProperties) it.next();
                String name = next.getFileName();
                FileObject speculative;
                if (folder == null) {
                    // When the fo is root of some filesystem, it will not have a parent,
                    // even though the parent exists on disk
                    if (name.equals(fo.getNameExt())) {
                        speculative = fo;
                    } else {
                        speculative = null;
                    }
                } else {
                    speculative = folder.getFileObject(name);
                }
                if (speculative == null) continue; // The file is lost
                cache.cacheAttribute(speculative, FileProperties.ID, next);  // fill Memory.cache map
            }
            FileProperties value = (FileProperties) entries.get(fileName);
            if (value == null) {
                // traverse to parent until found known properties
                // it these have local status local inherit them
                if (folder.isRoot() == false) {
                    value = readFileProperties(folder, cache);  // RECURSION
                    if (value != null && value.isLocal()) {
                        FileProperties inheritedValue = new FileProperties();
                        inheritedValue.setName(fo.isFolder() ? fo.getNameExt() + "/" : fo.getName());  // NOI18N
                        inheritedValue.setStatus(value.getStatus());
                        value = inheritedValue;
                    } else {
                        value = null;
                    }
                }
            }
            return value;
        }
    }

    private FolderProperties readFolderProperties(FileObject folder) throws IOException {
        File file = FileUtil.toFile(folder);
        File cacheFile = folderPropertiesCache(file);
        if (cacheFile == null) return null;

        synchronized(IO_LOCK) {
            sharedList.clear();
            boolean complete = readCacheFile(cacheFile, sharedList);

            Set folderListing = new HashSet(sharedList.size());
            Iterator it = sharedList.iterator();
            while (it.hasNext()) {
                FileProperties next = (FileProperties) it.next();
                String  name = next.getName();
                boolean isFolder = false;
                if (name.endsWith("/")) { // NOI18N
                    name = name.substring(0, name.length() -1);
                    isFolder = true;
                }
                folderListing.add(new FolderEntry(name, isFolder));
            }

            // merge value with recent one to avoid loosing hidden state

            FolderProperties fprops;
            FileAttributeQuery faq = FileAttributeQuery.getDefault();
            if (faq.isPrepared(folder, FolderProperties.ID)) {
                fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
                assert fprops != null;
                fprops = new FolderProperties(fprops);
                fprops.setFolderListing(folderListing);
            } else {
                fprops = new FolderProperties();
                fprops.setFolderListing(folderListing);
            }
            fprops.setComplete(complete);

            return fprops;
        }
    }

    /** T9Y entry point */
    static void setTestCacheFile(File cacheFile) {
        testCacheFile = cacheFile;
    }

    /** T9Y entry point */
    static void setTestRewrite(boolean rewrite) {
        testRewrite = rewrite;
    }

    /**
     * Locates cache file for given file.
     * All files in dir shares came cache file.
     * Folder FileProperties are stored in parent folder cache file.
     */
    private static File filePropertiesCache(File file) {
        if (testCacheFile != null) {
            return testCacheFile;
        } else {
            File cacheFolder = file.getParentFile();
            File ret = Profiles.cacheForFolder(cacheFolder);
            return ret;
        }
    }

    /** Locates cache file for files in given folder. */
    private static File folderPropertiesCache(File folder) {
        assert folder.isDirectory() || folder.exists() == false;
        if (testCacheFile != null) {
            return testCacheFile;
        } else {
            File ret = Profiles.cacheForFolder(folder);
            return ret;
        }
    }

    // stream operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    private static void appendToCacheFile(File cacheFile, FileProperties fprops) throws IOException {
        // append to file end
        Statistics.diskAppend();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile, true), BUFFER_SIZE);
        Writer w = new OutputStreamWriter(out, "UTF-8"); // NOI18N
        try {
            try {
                String[] elements = fprops.toElements();
                String line = StatusFormat.getLineFromElements(elements);
                w.write(line);
                w.write("\n");
            } catch (IOException ex) {
                cacheFile.delete();
                throw ex;
            }
        } finally {
            try {
                w.close();
            } catch (IOException ex) {
                // already closed
            }
        }

    }
    private static void rewriteCacheFile(File cacheFile, boolean complete) throws IOException {

        // write down in precisely defined format, UTF-8 encoding and \n newlines
        // HEADER
        // metanum = number of metadate lines in textual form
        // [metadata line]*metanum
        // entry lines
        // <EOF>

        OutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile), BUFFER_SIZE);
        Writer w = new OutputStreamWriter(out, "UTF-8"); // NOI18N
        try {
            try {
                w.write(FILE_HEADER_1_1 + "\n");
                if (complete) {
                    w.write("1\n"); // NOI18N
                    w.write("complete=true\n");  // NOI18N
                } else {
                    w.write("0\n"); // NOI18N
                }
                Iterator it = sharedList.iterator();
                while (it.hasNext()) {
                    FileProperties next = (FileProperties) it.next();
                    if (next == null) continue; // do not write invalidated entries
                    String[] elements = next.toElements();
                    String line = StatusFormat.getLineFromElements(elements);
                    w.write(line);
                    w.write("\n");
                }
                w.flush();
            } catch (IOException ex) {
                cacheFile.delete();  // eliminate broken folderListing
                throw ex;
            }
        } finally {
            try {
                w.close();
            } catch (IOException ex) {
                // already closed
            }
        }

    }

    private static boolean checkVersion(File cacheFile) {
        BufferedReader r = null;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(cacheFile), BUFFER_SIZE);
            r = new BufferedReader(new InputStreamReader(in, "UTF-8"), 1024);  // NOI18N
            String header = r.readLine();
            return FILE_HEADER_1_1.equals(header);
        } catch (FileNotFoundException ex) {
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (r != null)
                try {
                    r.close();
                } catch (IOException e) {
                    // closed
                }
        }
    }

    /**
     * Reads data from file into passed list. Must be called under IO_LOCK.
     *
     * @return true it the cache file is complete and can be used for definig virtuals
     */
    private static boolean readCacheFile(File cacheFile, List fill) throws IOException {

        // uses precisely defined format, UTF-8 encoding and \n newlines

        BufferedReader r;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(cacheFile), BUFFER_SIZE);
            r = new BufferedReader(new InputStreamReader(in, "UTF-8"), 1024);  // NOI18N
        } catch (FileNotFoundException ex) {
            return false;
        }

        boolean definesVirtuals = false;
        try {
            try {
                String header = r.readLine();
                if (FILE_HEADER_1_1.equals(header) == false) {
                    // do not care about backward compatability we can compute all data again
                    return false;
                }

                String metaCount = r.readLine();
                int metaLines = Integer.decode(metaCount).intValue();
                for (int i = 0; i<metaLines; i++) {
                    String line = r.readLine();
                    if ("complete=true".equals(line)) {
                        definesVirtuals = true;
                    }
                }

                while (true) {
                    String line = r.readLine();
                    if (line == null) break;
                    String[] elements = StatusFormat.getElementsFromLine(line);
                    fill.add(new FileProperties(elements));
                }
            } catch (IOException ex) {
                fill.clear();  // ignore content of broken files
                throw ex;
            }
        } finally {
            try {
                r.close();
            } catch (IOException ex) {
                // already closed
            }
        }

        return definesVirtuals;
    }

    // to get prettier thread dumps 'name' the lock
    private static final class IOLock {
    }

}
