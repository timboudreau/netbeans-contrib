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
package org.netbeans.modules.vcscore.turbo.local;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Utilities;
import org.openide.ErrorManager;
import org.netbeans.modules.vcscore.turbo.log.Statistics;

import java.util.*;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;

/**
 * Caches FileObject => attributes pair without actually
 * holding FileObject's reference. It monitors live
 * FileObjects and frees stale entries for dead ones
 * (it keeps few speculative entries for inborn Fileobjetcs).
 * <p>
 * It's synchronized for safety because at least
 * <code>TrackingRef.run</code> comes from private OpenAPI
 * thread.
 *
 * @author Petr Kuzel
 */
final class Memory {

    /** Limited size Map&lt;absolutePath, Map>. It keeps few ahead properties for FileObject embryos. */
    private static Map speculativeCache = new LRU(47);

    /** Map&lt;FileObjectKey, Map&lt;attributeName, attributeValue>>. Key identifies live FileObject. */
    private static Map liveFileObjectsMap = new HashMap(3571);

    /** Special value, known null that does not invalidate but caches. */
    public static final Object NULL = new Object();

    /**
     * Makes best efford to store file object attributes obtainable by next {@link #get}.
     * @param value updated value, <code>null</code> that kills memory entry
     * or <code>Memory.NULL</code> stores <code>null</code> prepared for {@link #get}.
     */
    public static void put(FileObject fileObject, String name, Object value) {

        // XXX unwrap from MasterFileSystem, hidden dependency on "VCS-Native-FileObject" attribute knowledge
        // Unfortunately MasterFileSystem API does not support generic unwrapping.
        FileObject nativeFileObject = (FileObject) fileObject.getAttribute("VCS-Native-FileObject");  // NOI18N
        if (nativeFileObject == null) nativeFileObject = fileObject;

        FileObjectKey key = new FileObjectKey(nativeFileObject);
        Map[] speculative = new Map[1];
        putEntry(key, name, value, speculative);
        if (speculative[0] != null) {
            storeSpeculative(nativeFileObject, speculative[0]);
        }        
    }

    /** Silently adds speculative entry from providers layer. */
    public static synchronized void putSpeculativeEntry(String key, String name, Object value) {
        Map attributes = (Map) speculativeCache.get(key);
        if (attributes == null) {
            attributes = new HashMap(5);
            speculativeCache.put(key, attributes);
        }
        if (value != null) {
            attributes.put(name, normalizeValue(value));
        } else {
            attributes.remove(name);
        }
    }

    /** Entry 'format' does not keep any strong ref to source file object. */
    private static void putEntry(FileObjectKey key, String name, Object value, Map[] speculativePtr) {

        FileObject fo = key.fileObject;
        File f = FileUtil.toFile(fo);
        
        synchronized (Memory.class) {
            
            // update existing values
            Map attributes;
            if (liveFileObjectsMap.containsKey(key)) {
                attributes = (Map) liveFileObjectsMap.get(key);
                if (value != null) {
                    attributes.put(name, normalizeValue(value));
                } else {
                    attributes.remove(name);
                }
            } else {
                // merge with speculative values

                String absolutePath = f.getAbsolutePath();
                attributes = (Map) speculativeCache.get(absolutePath);
                boolean hadSpeculative = false;
                if (attributes == null) {
                    attributes = new HashMap(5);
                } else {
                    hadSpeculative = true;
                }
                if (value != null) {
                    attributes.put(name, normalizeValue(value));
                } else {
                    attributes.remove(name);
                }
                liveFileObjectsMap.put(key, attributes);
                if (hadSpeculative) {
                    speculativeCache.remove(absolutePath);
                    speculativePtr[0] = new HashMap(attributes);
                }
                key.makeWeak();
            }
        }

    }

    private static Object normalizeValue(Object value) {
        if (value == NULL) return null;
        return value;
    }

    /**
     * Looks for cached file atribute of given name.
     * Return stored attributes or <code>null</code>.
     */
    public static Object get(FileObject fileObject, String name) {

        // XXX unwrap from MasterFileSystem, hidden dependency on "VCS-Native-FileObject" attribute knowledge
        // Unfortunately MasterFileSystem API does not support generic unwrapping.
        FileObject nativeFileObject = (FileObject) fileObject.getAttribute("VCS-Native-FileObject");  // NOI18N
        if (nativeFileObject == null) nativeFileObject = fileObject;

        Map[] speculative = new Map[1];
        Object value = getImpl(nativeFileObject, name, speculative);
        if (speculative[0] != null) {
            storeSpeculative(nativeFileObject, speculative[0]);
        }
        return value;
    }

    public static Object getImpl(FileObject fo, String name, Map[] speculativePtr) {
        File file = FileUtil.toFile(fo);
        synchronized (Memory.class) {
            Object key = new FileObjectKey(fo);
            Map attributes = (Map) liveFileObjectsMap.get(key);
            if (attributes != null) {
                return attributes.get(name);
            } else {
                // try speculative results
                String skey = file.getAbsolutePath();
                attributes = (Map) speculativeCache.get(skey);
                if (attributes != null) {
                    liveFileObjectsMap.put(key, attributes);
                    speculativeCache.remove(skey);
                    speculativePtr[0] = new HashMap(attributes);
                    return attributes.get(name);
                }
            }
        }
        return null;
    }

    /** store speculative data with disk providers if they just become real */
    private static void storeSpeculative(FileObject fo, Map attributes) {

        // #52508 deadlock
        assert Thread.holdsLock(Memory.class) == false;

        Iterator it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            FileAttributeQuery faq = FileAttributeQuery.getDefault();
            String attName = (String) entry.getKey();
            Object attValue = entry.getValue();
            faq.storeAttribute(fo, attName, attValue);
        }
    }

    /**
     * Determines if given attribute has a cache entry.
     * Note that the entry can contain info that attribute
     * does not exist!
     */
    public static boolean existsEntry(FileObject fo, String name) {
        File file = FileUtil.toFile(fo);
        synchronized (Memory.class) {
            Object key = new FileObjectKey(fo);
            Map attributes = (Map) liveFileObjectsMap.get(key);
            if (attributes != null) {
                return attributes.keySet().contains(name);
            } else {
                key = file.getAbsolutePath();
                attributes = (Map) speculativeCache.get(key);
                if (attributes != null) {
                    return attributes.keySet().contains(name);
                }
            }
        }
        return false;
    }

    /** Tests whether given fileobjects exist in live map i.e. put() or get() was called. */
    public static synchronized boolean isLiveEntry(FileObject fo) {
        Object key = new FileObjectKey(fo);
        return liveFileObjectsMap.containsKey(key);
    }


    /** Cooperates with GC to track FileObject lifetime in Filesystem indepenent way. */
    private static final class TrackingRef extends WeakReference implements Runnable {

        private final FileObjectKey key;

        /** For statictics purposes only. */
        private final String statisticsPath;

        public TrackingRef(FileObject referent, FileObjectKey key) {
            super(referent, Utilities.activeReferenceQueue());
            this.key = key;

            if (Statistics.logPerformance()) {
                File f = FileUtil.toFile(key.fileObject);
                statisticsPath = f.getAbsolutePath();
            } else {
                statisticsPath = null;
            }
            Statistics.fileObjectAdded(statisticsPath);
        }

        /** GC-collected.*/
        public void run() {
            liveFileObjectsMap.remove(key);
            Statistics.fileObjectRemoved(statisticsPath);
        }

        public String toString() {
            return "TrackingRef referentKey=" + key;  // NOI18N
        }
    }

    /**
     * GC mutable key holding equals, hashCode
     */
    /*T9Y*/ final static class FileObjectKey {
        private FileObject fileObject;
        private Reference fileObjectRef;
        private final int hashCode;
        private String absolutePath;

        public FileObjectKey(FileObject fo) {
            fileObject = fo;
            hashCode = fo.getNameExt().hashCode();
        }

        public void makeWeak() {
            fileObjectRef = new TrackingRef(fileObject, this);
            fileObject = null;
        }

        public boolean equals(Object o) {
            if (this == o) return true;

            if (o instanceof FileObjectKey) {
                FileObjectKey key = (FileObjectKey) o;
                FileObject fo2 = key.fileObject;
                if (fo2 == null) {
                    fo2 = (FileObject) key.fileObjectRef.get();
                }
                if (fo2 != null) {
                    FileObject fo = fileObject;
                    if (fo == null) {
                        fo = (FileObject) fileObjectRef.get();
                    }
                    if (fo != null) {

                        if (fo == fo2) return true;

                        try {
                            FileSystem fs = fo.getFileSystem();
                            FileSystem fs2 = fo2.getFileSystem();
                            if (fs.equals(fs2)) {
                                return fo.equals(fo2);
                            } else {
                                // fallback use absolute paths (cache them)
                                if (absolutePath == null) {
                                    File f = FileUtil.toFile(fo);
                                    absolutePath = f.getAbsolutePath();
                                    if (fileObject == null) {
                                        Statistics.absolutePathKey(absolutePath);
                                    }
                                }
                                if (key.absolutePath == null) {
                                    File f2 = FileUtil.toFile(fo2);
                                    key.absolutePath = f2.getAbsolutePath();
                                    if (fileObject == null) {
                                        Statistics.absolutePathKey(absolutePath);
                                    }
                                }
                                return absolutePath.equals(key.absolutePath);
                            }
                        } catch (FileStateInvalidException e) {
                            ErrorManager err = ErrorManager.getDefault();
                            err.notify(e);
                        }
                    }
                }
            }
            return false;
        }

        public int hashCode() {
            return hashCode;
        }

        public String toString() {
            if (absolutePath != null) {
                return absolutePath;
            } else if (fileObject != null) {
                return fileObject.toString();
            }

            FileObject fo = (FileObject) fileObjectRef.get();
            if (fo != null) {
                return fo.toString();
            }

            return super.toString();
        }
    }

    /** Limited size LRU map implementation. */
    private final static class LRU extends LinkedHashMap {

        private final int maxSize;

        public LRU(int maxSize) {
            super(maxSize *2, 0.5f, true);
            this.maxSize = maxSize;
        }

        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > maxSize;
        }
    }

}
