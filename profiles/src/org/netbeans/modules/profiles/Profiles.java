/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.profiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import org.openide.filesystems.*;


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
    
    /** Activates given profile in the current session.
     */
    public static void activateProfile (java.net.URL profile) throws Exception {
        org.netbeans.core.startup.layers.SystemFileSystem sfs;
        sfs = (org.netbeans.core.startup.layers.SystemFileSystem)Repository.getDefault().getDefaultFileSystem();
        org.netbeans.core.startup.layers.ModuleLayeredFileSystem layer = sfs.getUserLayer();
        
        if (previousProfile != null) {
            layer.removeURLs (Collections.singleton (previousProfile));
        }
        
        previousProfile = profile;
        
        if (profile != null) {
            layer.addURLs (Collections.singleton(profile));
        }
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
        java.io.OutputStream os = res.getOutputStream(lock);
        writeOut (root, acceptor, os, resDir);
        os.close ();
        
        return res; 
    }
    
    /** Write a complete layer to a stream.
     */
    private static void writeOut(FileObject root, Set acceptor, OutputStream out, FileObject datadir) throws IOException {
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
        writeFolder(wr, root, 1, datadir, acceptor);
        wr.write("</filesystem>\n"); // NOI18N
        wr.close();
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
    private static void writeFolder(Writer wr, FileObject elem, int depth, FileObject datadir, Set acceptor) throws IOException {
        FileObject[] chArr = elem.getChildren();
        if (chArr == null || chArr.length == 0) return;
        Iterator it = Arrays.asList(chArr).iterator();
        
        while (it.hasNext()) {
            FileObject child = (FileObject)it.next();
            if (!acceptor.contains (child)) {
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
                writeFolder(wr, child, depth + 1, datadir, acceptor);
                wr.write(space(depth));
                wr.write("</folder>"); // NOI18N
                wr.write('\n'); // NOI18N
            } else {
                FileObject file = (FileObject)child;
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
                    java.util.zip.CRC32 crc = new java.util.zip.CRC32();
                    crc.update(contents);
                    String hex = Long.toHexString(crc.getValue());
                    // Note that it is really an int, not a long, so hex.length() <= 8.
                    while (hex.length() < 8) {
                        hex = "0" + hex; // NOI18N
                    }
                    FileObject data = FileUtil.createData (datadir, file.getName () + hex + "." + file.getExt());
                    url = datadir.getNameExt() + '/' + data.getNameExt();
                    FileLock lock = data.lock ();
                    OutputStream os = data.getOutputStream(lock);
                    os.write (contents);
                    os.close ();
                    lock.releaseLock();
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
}

