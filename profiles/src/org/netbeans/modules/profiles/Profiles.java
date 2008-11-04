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

package org.netbeans.modules.profiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.*;
import java.util.jar.*;
import org.openide.filesystems.*;
import org.xml.sax.SAXException;


/** Utility methods to work with profiles.
*
* @author  Jaroslav Tulach
*/
final class Profiles extends Object {
    /** URL of previously activated profile */
    private static java.net.URL previousProfile;
    
    
    private Profiles () {
    }

    /** Folder for profiles.
     */
    private static FileObject getProfilesFolder() {
        try {
            return FileUtil.createFolder (Repository.getDefault().getDefaultFileSystem().getRoot(), "Profiles"); // NOI18N
        } catch (java.io.IOException ex) {
            return Repository.getDefault().getDefaultFileSystem(). getRoot();
        }
    }
    
    /** Finds the profiles node.
     */
    public static org.openide.nodes.Node getProfilesNode () {
        try {
            FileObject folder = getProfilesFolder();
            return org.openide.loaders.DataObject.find (folder).getNodeDelegate ();
        } catch (java.io.IOException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            return org.openide.nodes.Node.EMPTY;
        }
    }
    
    /** Saves a profile of current settings under given name.
     */
    public static void saveAs (String name) {
        class Accept extends HashSet {
            public boolean contains (Object o) {
                if (o instanceof FileObject) {
                    FileObject f = (FileObject)o;
                    if (f.getPath().startsWith ("Editor") || f.getPath().startsWith ("Shortcuts")) {
                        // only user modifications on disk
                        return FileUtil.toFile(f) != null;
                    }
                }
                return false;
            }
        }
        
        FileObject o = getProfilesFolder();
        try {
            FileObject r = generateProfile(o, name, Repository.getDefault().getDefaultFileSystem().getRoot(), new Accept ());
            activateProfile (r);
        } catch (java.io.IOException ex) {
            org.openide.ErrorManager.getDefault().notify (ex);
        }
    }
    
    /** Activates given profile in the current session.
     */
    public static void activateProfile (FileObject profile) {
        try {
            activateProfile (profile.getURL ());
        } catch (Exception ex) {
            org.openide.ErrorManager.getDefault().notify (ex);
        }
    }
    
    private static FS INSTANCE;
    // public for Lookup
    @org.openide.util.lookup.ServiceProvider(service=org.openide.filesystems.FileSystem.class)
    public static final class FS extends MultiFileSystem {
        public FS() {
            INSTANCE = this;
        }
        void activate(URL u) throws SAXException {
            setDelegates(new XMLFileSystem(u));
        }
    }
    
    /** Activates given profile in the current session.
     */
    public static void activateProfile(URL profile) throws Exception {
        if (profile.equals (previousProfile)) {
            return;
        }
        INSTANCE.activate(profile);
    }
    
    /** generates profile.
     */
    public static FileObject generateProfile (
        FileObject targetDir, String profileName, 
        FileObject root, java.util.Set acceptor
    ) throws java.io.IOException {
        FileObject res = FileUtil.createData(targetDir, profileName + ".profile"); // NOI18N
        FileObject resDir = FileUtil.createFolder(targetDir, profileName);
        FileLock lock = res.lock ();
        try {
            OutputStream os = res.getOutputStream(lock);
            try {
                writeOut(root, acceptor, os, resDir);
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
        
        return res; 
    }

    /** explorts profile as a module JAR file.
     * @param profile file containing the description of the profile (usually .profile extension)
     * @param jar file to generate the module to
     */
    public static void exportProfile (
        FileObject profile, java.io.File jar
    ) throws java.io.IOException {
        if (!profile.hasExt ("profile")) {
            throw new IOException("Wrong profile name" + profile); // NOI18N
        }
        
        Manifest mf = new Manifest();
        Attributes attr = mf.getMainAttributes();
        attr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attr.putValue("OpenIDE-Module", "org.netbeans.modules.profiles.generated." + profile.getName()); // NOI18N
        String layer = "org/netbeans/modules/profiles/generated/" + profile.getName() + "/layer.xml"; // NOI18N
        attr.putValue("OpenIDE-Module-Layer", layer);
        
        final String dir = "org/netbeans/modules/profiles/generated/" + profile.getName() + "/"; // NOI18N
        String prof = dir + profile.getNameExt(); // NOI18N
        
        final JarOutputStream os = new JarOutputStream(new java.io.FileOutputStream(jar), mf);

        {
            // writing the layer file
            os.putNextEntry(new JarEntry(layer));
            Writer wr = new OutputStreamWriter(os, "UTF-8"); // NOI18N
            wr.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N
            wr.write("<?xml-stylesheet type=\"text/xml\" href=\"http://openide.netbeans.org/fs/filesystem.xsl\"?>\n"); // NOI18N
            wr.write("<!DOCTYPE filesystem PUBLIC \"-//NetBeans//DTD Filesystem 1.1//EN\" \"http://www.netbeans.org/dtds/filesystem-1_1.dtd\">\n"); // NOI18N
            wr.write("<filesystem>\n"); // NOI18N
            wr.write("  <folder name='Profiles'>\n"); // NOI18N
            wr.write("    <file name='" + profile.getNameExt() + "' url='nbres:/" + prof + "' />\n"); // NOI18N
            wr.write("  </folder>\n"); // NOI18N
            wr.write("</filesystem>\n"); // NOI18N
            wr.flush();
        }

        class JarC implements Creator {
            public Object[] create(String name) throws IOException {
                os.putNextEntry(new JarEntry(dir + name));
                String url = "nbres:/" + dir + name; 
                return new Object[] { os, url };
            }
            
            public void close(Object[] arr) throws IOException {
            }
            
            public boolean accept (FileObject fo) {
                return true;
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            XMLFileSystem fs = new XMLFileSystem(profile.getURL());
            writeOut(fs.getRoot(), bytes, new JarC());
        } catch (org.xml.sax.SAXException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
        
        // write the profile file
        os.putNextEntry(new JarEntry(prof));
        os.write(bytes.toByteArray());
        
        os.close();
    }

    private static void writeOut(final FileObject root, final Set acceptor, OutputStream out, final FileObject datadir) throws IOException {
        class FSC implements Creator {
            public Object[] create(String name) throws IOException {
                FileObject data = FileUtil.createData (datadir, name);
                String url = datadir.getNameExt() + '/' + data.getNameExt();
                FileLock lock = data.lock ();
                OutputStream os = data.getOutputStream(lock);
                return new Object[] { os, url, lock };
            }
            
            public void close(Object[] arr) throws IOException {
                OutputStream os = (OutputStream)arr[0];
                os.close();
                FileLock l = (FileLock)arr[2];
                l.releaseLock();
            }
            
            public boolean accept (FileObject fo) {
                return acceptor.contains(fo);
            }
        }
        writeOut(root, out, new FSC());
    }
    
    /** Write a complete layer to a stream.
     */
    private static void writeOut(FileObject root, OutputStream out, Creator c) throws IOException {
        Writer wr = new OutputStreamWriter(out, "UTF-8"); // NOI18N
        wr.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N
        // Simplify debugging a bit:
        wr.write("<?xml-stylesheet type=\"text/xml\" href=\"http://openide.netbeans.org/fs/filesystem.xsl\"?>\n"); // NOI18N
        wr.write("<!DOCTYPE filesystem PUBLIC \"-//NetBeans//DTD Filesystem 1.1//EN\" \"http://www.netbeans.org/dtds/filesystem-1_1.dtd\">\n"); // NOI18N
        wr.write("<filesystem"); // NOI18N
        if(writeAttrs(wr, root, 1)) {
            wr.write(">"); // NOI18N
            wr.write('\n'); // NOI18N
        }
        writeFolder(wr, root, 1, c);
        wr.write("</filesystem>\n"); // NOI18N
        wr.flush();
    }
    
    private static final String SPACES = "                                                                                        "; // NOI18N
    private static final int SPACES_LENGTH = SPACES.length();
    private static final int INDENT = 4;
    private static String space(int n) {
        int size = n * INDENT;
        if (size <= SPACES_LENGTH) {
            return SPACES.substring(0, size);
        } else {
            StringBuffer buf = new StringBuffer(size);
            for (int i = 0; i < size; i++) buf.append(' '); // NOI18N
            return buf.toString();
        }
    }

    /** Write one <folder>.
     */
    private static void writeFolder(Writer wr, FileObject elem, int depth, Creator creator) throws IOException {
        FileObject[] chArr = elem.getChildren();
        if (chArr == null || chArr.length == 0) return;
        Iterator it = Arrays.asList(chArr).iterator();
        
        while (it.hasNext()) {
            FileObject child = (FileObject)it.next();
            if (!creator.accept(child)) {
                continue;
            }
            
            if (child.isFolder()) {
                wr.write(space(depth));
                wr.write("<folder name=\""); // NOI18N
                wr.write(org.openide.xml.XMLUtil.toAttributeValue(child.getNameExt()));
                wr.write('"'); // NOI18N
                if(writeAttrs(wr, child, depth + 1)) {
                    wr.write('>'); // NOI18N
                    wr.write('\n'); // NOI18N
                }
                writeFolder(wr, child, depth + 1, creator);
                wr.write(space(depth));
                wr.write("</folder>"); // NOI18N
                wr.write('\n'); // NOI18N
            } else {
                FileObject file = child;
                wr.write(space(depth));
                wr.write("<file name=\""); // NOI18N
                wr.write(org.openide.xml.XMLUtil.toAttributeValue(child.getNameExt()));
                String url = null;
                if (file.getSize () > 0) {
                    int s = (int)file.getSize();
                    byte[] contents = new byte[s];
                    InputStream is = file.getInputStream();
                    int r = is.read (contents);
                    is.close ();
                    if (r != s) throw new IOException ("Should read " + s + " bytes but was only " + r + " for " + file);

                    Object[] arr = creator.create(file.getPath().replace('/', '-'));
                    url = (String)arr[1];
                    OutputStream os = (OutputStream)arr[0];
                    os.write(contents);
                    creator.close(arr);
                    // else it is already there, presumably with the right contents, unless
                    // there is a hash collision - unlikely, SHA-1 would make it safer
                    // XXX should old data_* files be deleted? possible but probably not important
                }
                if (url != null) {
                    wr.write("\" url=\""); // NOI18N
                    wr.write(org.openide.xml.XMLUtil.toAttributeValue(url));
                }
                wr.write('"'); // NOI18N
                if (writeAttrs(wr, child, depth + 1)) {
                    wr.write("/>"); // NOI18N
                    wr.write('\n'); // NOI18N
                } else {
                    wr.write(space(depth));
                    wr.write("</file>"); // NOI18N
                    wr.write('\n'); // NOI18N
                }
            }
        }
        
    }

    /** Write attributes of a <file> or <folder>.
     */
    private static boolean writeAttrs(Writer wr, FileObject file, int depth) throws IOException {
        Enumeration attri = file.getAttributes();
        
        if (!attri.hasMoreElements()) return true; // empty attrs

        wr.write('>'); // NOI18N
        wr.write('\n'); // NOI18N
        while (attri.hasMoreElements()) {
            String a = (String)attri.nextElement();
            wr.write(space(depth));
            wr.write("<attr name=\""); // NOI18N
            wr.write(org.openide.xml.XMLUtil.toAttributeValue(a));
            wr.write("\" "); // NOI18N
            
            Object value = file.getAttribute(a);
            wr.write(typeAndData (value, true));
            wr.write("=\""); // NOI18N
            wr.write(org.openide.xml.XMLUtil.toAttributeValue(typeAndData (value, false)));
            wr.write("\"/>"); // NOI18N
            wr.write('\n'); // NOI18N
        }
        
        return false;
    }
    
    private static String typeAndData (Object value, boolean type) throws IOException {
        if (value instanceof Byte) {
            return type ? "bytevalue" : value.toString();
        }
        if (value instanceof Short) {
            return type ? "shortvalue" : value.toString();
        }
        if (value instanceof Integer) {
            return type ? "intvalue" : value.toString();
        }
        if (value instanceof Long) {
            return type ? "longvalue" : value.toString();
        }
        if (value instanceof Float) {
            return type ? "floatvalue" : value.toString();
        }
        if (value instanceof Double) {
            return type ? "doublevalue" : value.toString();
        }
        if (value instanceof Boolean) {
            return type ? "boolvalue" : value.toString();
        }
        if (value instanceof Character) {
            return type ? "charvalue" : value.toString();
        }
        if (value instanceof String) {
            return type ? "stringvalue" : value.toString();
        }
        if (value instanceof java.net.URL) {
            return type ? "urlvalue" : value.toString();
        }

        if (type) {
            return "serialvalue";
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream ();
        java.io.ObjectOutputStream oo = new java.io.ObjectOutputStream (os);
        oo.writeObject(value);
        oo.close();
        
        
        return org.openide.xml.XMLUtil.toHex(os.toByteArray(), 0, os.toByteArray().length);
    }
    
    private interface Creator {
        /** Returns [0] = output stream, [1] url = to refer to it */
        public Object[] create(String name) throws IOException;
        /** release what ever was allocated with create.
         */
        public void close(Object[] arr) throws IOException;
        
        /** accept this object?
         */
        public boolean accept(FileObject fo);
    }
}

