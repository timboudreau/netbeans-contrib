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
/*
 * Cache.java
 *
 * Created on October 19, 2004, 5:30 PM
 */

package org.netbeans.lib.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import org.netbeans.lib.storage.FileMapStorage;
import org.netbeans.api.storage.Storage;
import org.netbeans.collections.numeric.*;
import org.netbeans.lib.storage.HeapStorage;
import org.netbeans.api.cache.Cache;
import org.netbeans.api.cache.PersistentCache;

/**
 * A cache for indexed wads of data - rather like a big List of binary data,
 * but backed by a memory mapped file.  Contains specific support for 
 * caching text, via some String based entries.
 * <p>
 * On disk, a Cache consists of two files - the data file, and an indices
 * file which lists the start offsets of entries in the data file.
 * <p>
 * Cache objects are most highly optimized for the case where most data
 * is static, and new entries will simply be appended to the cache.  There
 * are methods to delete entries and replace entries, but they are somewhat
 * less efficient (though not prohibitively slow).
 * <p>
 * The stored, memory mapped data may be written to disk at any time;  the
 * <code>close()</code> method must be called to update the indices data, when
 * a cache object is disposed (and also to ensure that any cached data in the
 * memory mapped file is indeed flushed to disk).  Automation of some of this
 * may be done in the future.
 * <p>
 * <b>Threading:</b> At this point, little attempt at thread safety is made.  
 * Clients should ensure they do not access a cache concurrently from two
 * threads.  Read operations and read while appending should be safe, but
 * are not guaranteed to be.
 *
 * @author Tim Boudreau
 */
public final class CacheImpl extends PersistentCache {
    Storage storage;
    private String name;
    private final File dir;
    
    //TODO:  Allow constructor argument RepairFacility that can try to re-index
    //corrupted caches
    
    //TODO:  Support Matcher for searches
        
    /** Creates a new instance of Cache with the specified name, in the passed
     * directory.  If the name contains File.separator, subdirectories will
     * be created on first write in order to create the cache file.
     */
    CacheImpl(String name, File dir) {
        this.name = name;
        this.dir = dir;
        assert dir.isDirectory() || !dir.exists();
        if (name.startsWith(File.separator) || name.endsWith(File.separator)) {
            throw new IllegalArgumentException (name + " starts or ends with" +
                    "the system file separator");
        }
    }
    
    private static Map cacheCache = null;
    /**
     * Get a cache instance with the passed name in the passed directory.
     * Since caches use memory mapped files which can grow on write, it is
     * important that only one instance of a Cache ever exist for any 
     * file or directory.
     */
    public static synchronized CacheImpl cache (String name, File dir) {
        File f = new File (dir, name);
        if (cacheCache != null) {
            cacheCache = new HashMap();
        }
        CacheImpl result = null;
        Reference ref = (Reference) cacheCache.get(f.getPath());
        if (ref != null) {
            result = (CacheImpl) ref.get();
        }
        if (result == null) {
            result = new CacheImpl (name, dir);
            cacheCache.put (f.getPath(), new WeakReference(result));
        }
        return result;
    }
    
    /**
     * Get the Storage object which actually contains the CharSequence data.
     */
    private Storage getStorage () {
        if (storage == null) {
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new Error ("Could not create dir " + dir);
                }
            }
            try {
                storage = new FileMapStorage (getStorageFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return storage;
    }
    
    /**
     * Get the parent directory for the root directory of the cache, creating
     * intermediate directories if the name contains path separators.
     */
    File getParentDir() {
        File parent = dir;
        if (name.indexOf(File.separator) != -1) {
            StringTokenizer tok = new StringTokenizer(name, File.separator);
            while (tok.hasMoreTokens()) {
                if (!parent.exists()) {
                    parent.mkdir();
                } else if (!parent.isDirectory()) {
                    throw new IllegalStateException ("Exists but isn't a directory: " + parent);
                }
                String nm = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    parent = new File (parent, nm);
                } else {
                    //Last is the file
                    break;
                }
            }
        }
        return parent;
    }
    
    /**
     * Get the file used for data storage.
     */
    File getStorageFile() {
        File result = new File (getParentDir(), strip(name));
        return result;
    }
    
    /**
     * Get the file used for index storage. 
     */
    File getIndicesFile() {
        return new File (getParentDir(), strip(name) + ".idxs");
    }
    
    /**
     * Strip the path out of a file name.
     */
    private static String strip (String name) {
        int idx = name.lastIndexOf(File.separator);
        if (idx != -1) {
            return name.substring (idx + 1);
        } else {
            return name;
        }
    }
    
    LongList indices = null;
    /**
     * Get the pseudo-collection which holds the start position for each 
     * index into the storage file.  This is resolved from the indices file
     * if that exists.
     */
    LongList getIndices() {
        if (indices == null) {
            File indicesFile = getIndicesFile();
            if (indicesFile.exists()) {
                try {
                    indices = new LongList(indicesFile);
                } catch (IOException ioe) {
                    //XXX handle
                    ioe.printStackTrace();
                }
            } else {
                indices = new LongList(50);
            }
        }
        return indices;
    }
    
    private long getStartOffset (int index) {
        return getIndices().get(index);
    }
    
    private long getEndOffset (int index) {
        LongList indices = getIndices();
        if (index == indices.size() - 1) {
            return getStorage().size();
        } else {
            return indices.get(index + 1);
        }
    }
    
    /**
     * Get a ByteBuffer representing data at a given index.
     */
    public ByteBuffer getBuffer (int index) {
        LongList indices = getIndices();
        long start = indices.get(index);
        long end;
        if (index == indices.size() - 1) {
            end = getStorage().size();
        } else {
            end = indices.get(index + 1);
        }
        
        try {
            ByteBuffer result = getStorage().getReadBuffer(start, end - start);
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException (ioe); //XXX
        }
    }
    
    /**
     * Get a character sequence representing the data at a given index.
     * <i><b>Important</b>: Do not assume that <code>"foo".equals(get(n)) ==
     * "foo".equals(get(n).toString())</code> - the CharSequence returned is
     * not necessarily an instance of String.  To compare Strings, always 
     * use <code>get(n).toString()</code>.</i>
     */
    public CharSequence get(int index) {
        return getBuffer(index).asCharBuffer();
    }
    
    /**
     * Get the number of elements in this cache.
     */
    public int size() {
        return getIndices().size();
    }
    
    /**
     * Get the last modified time of this cache, indicating the last time
     * data was written.  If this is a new cache instance whose file on disk
     * does not exist, this will return <code>Long.MIN_VALUE</code>.
     * <p>
     * Note that changes in the return value of this method subsequent to a write
     * are not guaranteed to be immediate - this depends on the JVM's implementation
     * of mapped byte buffers.
     */
    public long getLastModified() {
        if (storage != null) {
            try {
                storage.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return storage == null ? Long.MIN_VALUE : storage.lastWrite();
    }
    
    /**
     * Append a character sequence to this cache, incrementing its size by
     * one and changing the last modified date.
     */
    public void add (CharSequence seq) throws IOException {
        Storage storage = getStorage();
        ByteBuffer buf = storage.getWriteBuffer(seq.length() * 2);
        LongList indices = getIndices();
        getIndices().add (storage.size());
        
        CharBuffer cbuf = buf.asCharBuffer();
        cbuf.put (seq.toString());
        buf.position (seq.length() * 2);
        storage.write(buf);
    }
    
    /**
     * Append a ByteBuffer to this cache, incrementing its size by one and 
     * changing the last modified date.
     * @param bb A byte buffer whose current <code>position</code> is at the
     *  end of the data that should be written, such that a call to <code>bb.flip()</code>
     *  prepares it to be drained.
     */
    public void add (ByteBuffer bb) throws IOException {
        bb.flip();
        getStorage().write(bb);
        getIndices().add (storage.size());
    }
    
    void save() throws IOException {
        if (indices != null) {
            indices.save (getIndicesFile());
        }
        if (storage != null) {
            storage.flush();
            storage.close();
        }
    }
    
    /**
     * Close the cache, saving any changes.
     * <i>Always call this method if you have written to a cache, before
     * disposing it</i>.
     */
    public void close() throws IOException {
        save();
        indices = null;
        if (storage != null) {
            storage.dispose();
            storage = null;
        }
    }
    
    /**
     * Rename the cache, possibly moving the files in question.  The name
     * may contain path separator characters, and must be relative to the
     * root directory passed to the constructor.
     * <p>
     * If the rename fails, an IOException may be thrown.  This can happen,
     * for example, if other code is holding a FileChannel open over the 
     * cache file, has the file locked, etc.
     */
    public void rename (String name) throws IOException {
        save();
        if (storage != null) {
            storage.dispose();
            storage = null;
        }
        File store = getStorageFile();
        File indices = getIndicesFile();
        String oldName = this.name;
        this.name = name;
        File nueStore = getStorageFile();
        File nueIndices = getIndicesFile();
        if (nueStore.exists() || nueIndices.exists()) {
            this.name = oldName;
            throw new IOException ("Renamed cache files already exist " + nueStore + "," + nueIndices);
        }
        File[] orig = new File[] {store, indices};
        File[] nue = new File[] {nueStore, nueIndices};
        try {
            boolean result = renameFiles (orig, nue, true);
            if (!result) {
                throw new IOException ("Could not rename " + oldName + " to " + name);
            }
        } catch (IOException ioe) {
            this.name = oldName;
            throw ioe;
        }
        if (cacheCache != null) {
            //Update the cache
            cacheCache.remove(store.getPath());
            cacheCache.put (nueStore.getPath(), this);
        }
    }
    
    /**
     * Delete this cache, including its backing data files.
     * A cache object that has been deleted should not be written to or
     * read from after a call to delete() - the results of doing so are
     * undefined, but may include recreating the cache's directory tree
     * and files (sans the deleted data).
     */
    public void delete() throws IOException {
        if (storage != null) {
            storage.close();
            storage.dispose();
            storage = null;
        }
        indices = null;
        File store = getStorageFile();
        if (store.exists()) {
            store.delete();
        }
        File idxs = getIndicesFile();
        if (idxs.exists()) {
            idxs.delete();
        }
        delEmptyParents (store, dir);
        if (cacheCache != null) {
            cacheCache.remove(store.getPath());
        }
    }
    
    /**
     * Determine if backing storage for this cache exists on disk.
     */
    public boolean exists() {
        return getStorageFile().exists();
    }
    
    public int hashCode() {
        return getIndicesFile().hashCode() + (getStorageFile().hashCode() * 31);
    }
    
    public boolean equals (Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CacheImpl) {
            CacheImpl c = (CacheImpl) o;
            boolean result = c.getIndicesFile().equals(getIndicesFile()) &&
                    c.getStorageFile().equals(getStorageFile());
                    
            if (result) {
                if (c.indices != null && indices != null) {
                    result = c.indices.equals(indices);
                }
            }
            return result;
        } else {
            return false;
        }
    }
    
    /**
     * Delete an indexed entry from the cache and its backing storage.
     * <p>
     * Throws an IOException if a stream is open with pending data for
     * the entry being deleted.
     */
    public void deleteEntry (int index) throws IOException {
        long start = getStartOffset(index);
        long end = getEndOffset(index);
        getStorage().excise (start, end);
        indices.delete(index);
        if (index != indices.size() - 1 && indices.size() != 0) {
            indices.adjust (index, -1 * (end - start));
        }
        synchronized (this) {
            if (streams != null) {
                for (Iterator i=streams.iterator(); i.hasNext();) {
                    CacheOutputStream str = (CacheOutputStream) i.next();
                    int idx = str.getIndex();
                    if (idx == index) {
                        str.destroy();
                        throw new IllegalStateException ("Entry " + index +
                            " was deleted with its output stream open");
                    }
                    if (idx > index) {
                        str.setIndex(str.getIndex()-1);
                    }
                }
            }
        }
    }
    
    /**
     * Delete a range of entries from the cache, removing them from
     * the backing storage.
     * <p>
     * Will throw an IOException if a stream is open with data written to
     * replace the data at an index being deleted.
     */
    public void deleteEntries (int first, int last) throws IOException {
        long start = getStartOffset(first);
        long end = getEndOffset(last);
        getStorage().excise (start, end);
        int sz = indices.size();
        indices.delete (first, last);
        if (indices.size() != 0) {
            indices.adjust (first, -1 * (end - start));
        }
        synchronized (this) {
            if (streams != null) {
                for (Iterator i=streams.iterator(); i.hasNext();) {
                    CacheOutputStream str = (CacheOutputStream) i.next();
                    int idx = str.getIndex();
                    if (idx <= last && idx >= first) {
                        throw new IOException ("Entry " + idx + 
                                " was deleted with its output stream open");
                    } else if (idx > last) {
                        str.setIndex (str.getIndex() - ((last - first) + 1));
                    }
                }
            }
        }
    }
    
    private Set streams = null;
    synchronized void unregisterOutputStream (CacheOutputStream stream) {
        if (streams != null) {
            streams.remove(stream);
            if (streams.isEmpty()) {
                streams = null;
            }
        }
    }
    
    synchronized void registerOutputStream (CacheOutputStream stream) {
        if (streams == null) {
            streams = new HashSet();
        }
        streams.add(stream);
    }
    
    /** Index to pass to getOutputStream indicating that the output stream
     * should append to the end of the cache, rather than replacing data
     * at the specified index */
    public static final int APPEND = -2;
    synchronized void write (CacheOutputStream stream) throws IOException {
        int idx = stream.getIndex();
        if (idx == APPEND) {
            add (stream.buffer());
            return;
        }
        long start = getStartOffset(idx);
        long oldEnd = getEndOffset(idx);

        ByteBuffer buf = stream.buffer();
        
        int len = buf.limit();
        long offset = storage.replace(buf, start, oldEnd);
        
        if (idx+1 < indices.size()) {
            indices.adjust (idx+1, offset);
        }
        
        stream.destroy();
    }
    
    /**
     * Fetch an output stream which can replace the data at a specified index,
     * or append to the end of the cache, if the APPEND argument is specified.
     * <p>
     * Calling <code>close()</code> on the output stream will update the data.
     * <p>
     * Note that no caching is done here - multiple calls will produce multiple
     * output streams.
     */
    public OutputStream getOutputStream (int index) {
        if ((index < 0 && index != APPEND) || index >= size()) {
            throw new IllegalArgumentException ("Out of range: " + index);
        }
        return new CacheOutputStream (this, index);
    }
    
    /**
     * Get a file corresponding to a combination of parent dir and path
     */
    static File pathToFile (File parent, String path) {
        StringTokenizer tok = new StringTokenizer (path, File.separator);
        File f = parent;
        while (tok.hasMoreTokens()) {
            f = new File (f, tok.nextToken());
        }
        return f;
    }
    
    /**
     * Atomically rename an array of files to an array of new files, creating
     * any nonexistent directories for the new files.  All the files in each
     * passed array must have the same parent directory.  If the rename of any
     * passed file fails, no files will be renamed and no directories will be
     * created, and false will be returned.
     * 
     * @param orig An array of files to be renamed/moved, all of which are in the 
     *  same parent dir
     * @param nue An array of files that the orig array will become, the same length
     *  as the orig array, and all sharing a parent dir
     * @param deltree If true, the directory tree to the orig files should be
     *  deleted down to the first parent in common with the nue array or the
     *  first non-empty directory
     */
    static boolean renameFiles (File orig[], File nue[], boolean deltree) throws IOException {
        assert sameParentDir(orig) : "Not same parent dir: " + Arrays.asList(orig);
        assert sameParentDir(nue) : "Not same parent dir: " + Arrays.asList(nue);
        assert orig.length == nue.length : "Unequal array lengths: " + orig.length + "," + nue.length;
        assert orig.length > 0 : "No files passed";
        for (int i=0; i < orig.length; i++) {
            File origParent = orig[i].getParentFile();
            File nueParent = nue[i].getParentFile();
            List createdDirs = null;
            if (!nueParent.exists()) {
                createdDirs = createIntermediateDirs (nueParent);
            }
            if (!orig[i].renameTo(nue[i])) {
                rollback (orig, nue, i);
                if (createdDirs != null) {
                    for (Iterator it=createdDirs.iterator(); it.hasNext();) {
                        File f = (File) it.next();
                        f.delete();
                    }
                }
                return false;
            }
        }
        if (deltree) {
            delEmptyParents (orig[0], nue[0]);
        }
        return true;
    }
    
    /**
     * Used by some assertions.  Determine that all files in an array share
     * the same parent directory.
     */
    static boolean sameParentDir (File[] f) {
        if (f.length == 1) return true;
        File par = f[0].getParentFile();
        for (int i=0; i < f.length; i++) {
            if (!f[i].getParent().equals(par)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Rollback renaming of any files, stopping before the passed index.
     */
    static void rollback (File[] orig, File[] nue, int stop) throws IOException {
        for (int i=0; i < stop; i++) {
            if (!nue[i].renameTo (orig[i])) {
                throw new IOException ("Could not restore name of " + nue[i] + " to " + orig[i] + ".  Cache corrupted.");
            }
        }
    }
    
    /**
     * Create the passed file as a directory, creating any intermediate
     * directories needed.  
     *
     * @param file the directory to create
     * @return A List containing all of the directories created, from deepest
     *   to shallowest
     */
    static List createIntermediateDirs (File file) throws IOException {
        if (file.exists()) {
            return null;
        }
        File f = file;
        Stack stack = new Stack();
        List result = null;
        while (!f.exists()) {
            stack.push(f);
            String s = f.getParent();
            f = new File(f.getParent());
        }
        while (!stack.isEmpty()) {
            f = (File) stack.pop();
            f.mkdir();
            if (result == null) {
                result = new ArrayList(stack.size());
                result.add(f);
            }
        }
        return result;
    }
    
    /**
     * Delete all parents of a (which must not exist - it must already be
     * deleted), back to the first non-empty directory or the first directory
     * shared by b.
     */
    static void delEmptyParents (File a, File b) throws IOException {
        File del = a.getParentFile();
        File check = findFirstSharedParent (a, b);
        while (!del.equals(check) && del.listFiles() == null || del.listFiles().length == 0) {
            if (!del.delete()) {
                throw new IOException ("Could not delete " + del);
            }
            del = del.getParentFile();
        }
    }
    
    /**
     * Find the first parent in common between two files.
     */
    static File findFirstSharedParent (File a, File b) {
        System.err.println("First shared: " + a.getPath() + " & " + b.getPath());
        char[] aPath = a.getPath().toCharArray();
        char[] bPath = b.getPath().toCharArray();
        int max = Math.min (aPath.length, bPath.length);
        char sep = File.separatorChar;
        StringBuffer common = new StringBuffer();
        StringBuffer curr = new StringBuffer();
        for (int i=0; i < max; i++) {
            if (aPath[i] == bPath[i]) {
                curr.append (aPath[i]);
                if (aPath[i] == sep || i == max - 1) {
                    common.append (curr.toString());
                    curr = new StringBuffer();
                }
            } else {
                break;
            }
        }
        if (common.length() != 0) {
            return new File(common.toString());
        } else {
            return null;
        }
    }
}
