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

package org.netbeans.modules.javafx.source.classpath;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.ClassPath.Entry;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/** Methods for accessing an archive. Archive represents zip file or
 * folder.
 *
 * @author Petr Hrebejk
 */
public abstract class Archive {
       
    // New implementation Archive Interface ------------------------------------


    /** Gets all files in given folder
     *  @param folderName name of folder to list, path elements separated by / char
     *  @param entry owning ClassPath.Entry to check the excludes or null if everything should be included
     *  @param kinds to list, may be null => all types
     *  @param filter to filter the file content
     *  @return the listend files
     */
    public abstract Iterable<JavaFileObject> getFiles( String folderName, ClassPath.Entry entry, Set<JavaFileObject.Kind> kinds) throws IOException;    
    
    
    /**
     * Cleans cached data
     */
    public abstract void clear ();

    
    private static Map<URL, Archive> archives = new HashMap<URL, Archive>();

    public static Archive get(URL root) {
        Archive archive = archives.get(root);

        if (archive == null) {
            archive = create(root);
            if (archive != null) {
                archives.put(root, archive );
            }
        }
        return archive;
    }
    
    /** Creates proper archive for given file.
     */
    private static Archive create( URL root) {
        String protocol = root.getProtocol();
        if ("file".equals(protocol)) {
            File f = new File (URI.create(root.toExternalForm()));
            if (f.isDirectory()) {
                return new FolderArchive (f);
            } else {
                return null;
            }
        }
        if ("jar".equals(protocol)) {
            URL inner = FileUtil.getArchiveFile(root);
            protocol = inner.getProtocol();
            if ("file".equals(protocol)) {
                File f = new File (URI.create(inner.toExternalForm()));
                if (f.isFile()) {
                    return new CachingArchive(f);
                } else {
                    return null;
                }
            }
        }                
        //Slow
        FileObject fo = URLMapper.findFileObject(root);
        if (fo != null) {
            return new FileObjectArchive (fo);
        } else {
            return null;
        }
    }
    
    private static class FileObjectArchive extends Archive {
        private final FileObject root;
    
        /** Creates a new instance of FileObjectArchive */
        public FileObjectArchive (final FileObject root) {
            this.root = root;
        }
    
        public Iterable<JavaFileObject> getFiles(String folderName, ClassPath.Entry entry, Set<JavaFileObject.Kind> kinds) throws IOException {
            FileObject folder = root.getFileObject(folderName);        
            if (folder == null || !(entry == null || entry.includes(folder))) {
                return Collections.<JavaFileObject>emptySet();
            }
            
            FileObject[] children = folder.getChildren();
            List<JavaFileObject> result = new ArrayList<JavaFileObject>(children.length);
            for (FileObject fo : children) {
                if (fo.isData() && (entry == null || entry.includes(fo))) {
                    if (kinds == null || kinds.contains (FileObjects.getKind(fo.getExt()))) {
                        result.add(FileObjects.nbFileObject(fo, root, false));
                    }
                }
            }
            return result;
        }
    
        public void clear() {
        }
    }

    private static class FolderArchive extends Archive {
    
        final File root;
        final Charset encoding;

    /** Creates a new instance of FolderArchive */
    public FolderArchive (final File root) {
        assert root != null;
        this.root = root;
        
        FileObject file = FileUtil.toFileObject(root);
        
        if (file != null) {
            encoding = FileEncodingQuery.getEncoding(file);
        } else {
            encoding = null;
        }
    }
    
    public Iterable<JavaFileObject> getFiles(String folderName, ClassPath.Entry entry, Set<JavaFileObject.Kind> kinds) throws IOException {
        assert folderName != null;
        if (folderName.length()>0) {
            folderName+='/';                                                                            //NOI18N
        }
        if (entry == null || entry.includes(folderName)) {
            File folder = new File (this.root, folderName.replace('/', File.separatorChar));      //NOI18N
            //Issue: #126392
            //Normalization is slow
            //the problem when File ("A/").listFiles()[0].equals(new File("a/").listFiles[0]) returns
            //false seems to be only on Mac.
            if (org.openide.util.Utilities.isMac()) {
                folder = FileUtil.normalizeFile(folder);
            }
            if (folder.canRead()) {
                File[] content = folder.listFiles();            
                if (content != null) {
                    List<JavaFileObject> result = new ArrayList<JavaFileObject>(content.length);
                    for (File f : content) {
                        if (f.isFile()) {
                            if (entry == null || entry.includes(f.toURI().toURL())) {
                                if (kinds == null || kinds.contains(FileObjects.getKind(FileObjects.getExtension(f.getName())))) {
                                    result.add(FileObjects.fileFileObject(f,this.root,encoding));
                                }
                            }
                        }
                    }
                    return Collections.unmodifiableList(result);
                }
            }
        }
        return Collections.<JavaFileObject>emptyList();
    }               
    
    public void clear () {
    }
    
}

    private static class CachingArchive extends Archive {
        private final File archiveFile;
        private ZipFile zipFile;
        
        byte[] names;// = new byte[16384];
        private int nameOffset = 0;
        final static int[] EMPTY = new int[0];
        private Map<String, Folder> folders; // = new HashMap<String, Folder>();

        /** Creates a new instance of archive from zip file */
        public CachingArchive( File archiveFile) {
            this.archiveFile = archiveFile;
        }
        
        /** Gets all files in given folder */
        public Iterable<JavaFileObject> getFiles( String folderName, ClassPath.Entry entry, Set<JavaFileObject.Kind> kinds) throws IOException {
            doInit();        
            Folder files = folders.get( folderName );        
            if (files == null) {
                return Collections.<JavaFileObject>emptyList();
            } else {
                assert zipFile != null;
                List<JavaFileObject> l = new ArrayList<JavaFileObject>(files.idx / files.delta);
                for (int i = 0; i < files.idx; i += files.delta) {
                    create(folderName, files, i, kinds, l);
                }
                return l;
            }
        }

        private String getString(int off, int len) {
            byte[] name = new byte[len];
            System.arraycopy(names, off, name, 0, len);
            try {
                return new String(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new InternalError("No UTF-8");
            }
        }
    
        static long join(int higher, int lower) {
            return (((long)higher) << 32) | (((long) lower) & 0xFFFFFFFFL);
        }
    
        private void create(String pkg, Folder f, int off, Set<JavaFileObject.Kind> kinds, List<? super JavaFileObject> l) {
            String baseName = getString(f.indices[off], f.indices[off+1]);
            if (kinds == null || kinds.contains(FileObjects.getKind(FileObjects.getExtension(baseName)))) {
                long mtime = join(f.indices[off+3], f.indices[off+2]);
                if (zipFile == null) {
                    assert f.delta == 4;
                    l.add (FileObjects.zipFileObject(archiveFile, pkg, baseName, mtime));
                } else {
                    l.add (FileObjects.zipFileObject( zipFile, pkg, baseName, mtime));
                }
            }
        }
    
        public synchronized void clear () {
            folders = null;
            names = null;
            nameOffset = 0;
        }

        synchronized void doInit() {
            if (folders == null) {
                try {
                    names = new byte[16384];
                    folders = createMap(archiveFile);
                    trunc();
                } catch (IOException e) {
                    names = new byte[0];
                    nameOffset = 0;
                    folders = new HashMap<String, Folder>();

                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException ex) {
    ex.printStackTrace();
                        }
                    }
                }
            }
        }

        private void trunc() {
            // strip the name array:
            byte[] newNames = new byte[nameOffset];
            System.arraycopy(names, 0, newNames, 0, nameOffset);
            names = newNames;

            // strip all the indices arrays:
            for (Iterator it = folders.values().iterator(); it.hasNext();) {
                ((Folder) it.next()).trunc();
            }
        }

        private Map<String,Folder> createMap(File file ) throws IOException {        
            if (!file.canRead()) {
                return Collections.<String, Folder>emptyMap();
            }
            Map<String,Folder> map = new HashMap<String,Folder>();
            ZipFile zip = new ZipFile (file);
            try {
                for ( Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = e.nextElement();
                    String name = entry.getName();
                    int i = name.lastIndexOf('/');
                    String dirname = i == -1 ? "" : name.substring(0, i /* +1 */);
                    String basename = name.substring(i+1);
                    if (basename.length() == 0) {
                        basename = null;
                    }
                    Folder fld = map.get(dirname);
                    if (fld == null) {
                        fld = new Folder();
                        map.put(new String(dirname).intern(), fld);
                    }

                    if ( basename != null ) {
                        fld.appendEntry(this, basename, entry.getTime());
                    }
                }                    
            } finally {
                this.zipFile = zip;
            }            
            return map;
        }
    
        // Innerclasses ------------------------------------------------------------

        int putName(byte[] name) {
            int start = nameOffset;

            if ((start + name.length) > names.length) {
                byte[] newNames = new byte[(names.length * 2) + name.length];
                System.arraycopy(names, 0, newNames, 0, start);
                names = newNames;
            }

            System.arraycopy(name, 0, names, start, name.length);
            nameOffset += name.length;

            return start;
        }


        private static class Folder {
            int[] indices = EMPTY; // off, len, mtimeL, mtimeH
            int idx = 0;
            private final int delta;

            public Folder() {
                delta = 4;
            }

            void appendEntry(CachingArchive outer, String name, long mtime) {
                // ensure enough space
                if ((idx + delta) > indices.length) {
                    int[] newInd = new int[(2 * indices.length) + delta];
                    System.arraycopy(indices, 0, newInd, 0, idx);
                    indices = newInd;
                }

                try {
                    byte[] bytes = name.getBytes("UTF-8");
                    indices[idx++] = outer.putName(bytes);
                    indices[idx++] = bytes.length;
                    indices[idx++] = (int)(mtime & 0xFFFFFFFF);
                    indices[idx++] = (int)(mtime >> 32);
                } catch (UnsupportedEncodingException e) {
                    throw new InternalError("No UTF-8");
                }
            }

            void trunc() {
                if (indices.length > idx) {
                    int[] newInd = new int[idx];
                    System.arraycopy(indices, 0, newInd, 0, idx);
                    indices = newInd;
                }
            }
        }        
    }
}
