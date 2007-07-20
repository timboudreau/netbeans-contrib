/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editorthemes.export;

import beans2nbm.gen.FileModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tim Boudreau
 */
final class XmlFsFileModel implements FileModel, Comparator <FileObject> {

    private final FileSystem fs = FileUtil.createMemoryFileSystem();
    private final String path;
    public XmlFsFileModel(String path) {
        this.path = path;
    }

    public String getPath() {
        return path + "/layer.xml";
    }

    public void add (final FileObject obj) {
        try {
            FileObject f = fs.getRoot().getFileObject(obj.getPath());
            if (obj.isFolder()) {
                if (f == null) {
                    FileUtil.createFolder(fs.getRoot(), obj.getPath());
                }
                FileObject[] kids = obj.getChildren();
                for (FileObject k : kids) {
                    add (k);
                }
            } else {
                FileObject nue = f;
                if (nue == null) {
                    nue = FileUtil.createData(fs.getRoot(), obj.getPath());
                }
                System.err.println("Copying data file to " + nue.getPath());
                FileLock a = obj.lock();
                try {
                    FileLock lock = nue.lock();
                    InputStream in = obj.getInputStream();
                    OutputStream out = nue.getOutputStream(lock);
                    try {
                        FileUtil.copyAttributes(obj, nue);
                        FileUtil.copy (in, out);
                    } finally {
                        in.close();
                        out.close();
                        lock.releaseLock();
                    }
                } finally {
                    a.releaseLock();
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    List <FileModel> getEmbeddedFiles() {
        if (embeddedFiles.isEmpty()) {
            try {
                //XXX find these files w/o write
                ByteArrayOutputStream str = new ByteArrayOutputStream();
                write (str);
                List <FileModel> result = new ArrayList <FileModel> (embeddedFiles);
                clear();
                return result;
            } catch (Exception e) {
                throw new IllegalStateException (e);
            }
        } else {
            return new ArrayList <FileModel> (embeddedFiles);
        }
    }

    final List <AdHocFileModel> embeddedFiles = new ArrayList<AdHocFileModel>();
    private void clear() {
        embeddedFiles.clear();
    }

    public void write(OutputStream stream) throws IOException {
        clear();
        try { Thread.sleep (5000); } catch (Exception e) {}
        System.err.println("write " + getPath());
        PrintWriter w = new PrintWriter (stream);
        try {
            w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            w.println("<!DOCTYPE filesystem PUBLIC \"-//NetBeans//DTD Filesystem 1.1//EN\" \"http://www.netbeans.org/dtds/filesystem-1_1.dtd\">");
            w.println ("<filesystem>");
            recurseWrite (fs.getRoot(), w, 0);
        } finally {
            w.println ("</filesystem>");
            w.flush();
        }
    }

    private String getValString(Object o) {
        if (o instanceof String) {
            return "stringvalue=\"" + o.toString() + "\"";
        } else if (o instanceof Class) {
            Class clazz = (Class) o;
            return "stringvalue=\"" + clazz.getName() + "\"";
        } else if (o instanceof Method) {
            Method m = (Method) o;
            Class clazz = m.getDeclaringClass();
            return "methodvalue=\"" + clazz.getCanonicalName() + m.getName() + "\"";
        } else if (o instanceof Integer) {
            Integer in = (Integer) o;
            return "intvalue=\"" + in.intValue() + "\"";
        } else if (o instanceof Boolean) {
            return "boolvalue=\"" + o.toString() + "\"";
        } else {
            throw new IllegalArgumentException ("What is " + o.getClass() + " " + o);
        }
    }

    private void recurseWrite(FileObject f, PrintWriter w, int depth) throws IOException {
        System.err.println("  recurseWrite " + f.getPath());
        String s = depthString(depth);
        if (f.isData()) {
            AdHocFileModel fm = AdHocFileModel.create(f, path);
            String decl = "<file name=\"" + f.getNameExt() + "\"";
            if (fm != null) {
                decl += " url=\"" + fm.getName() + "\"";
                embeddedFiles.add (fm);
            }
            String ss = depthString (depth + 1);
            Enumeration <String> e = f.getAttributes();
            boolean hasAttrs = e.hasMoreElements();
            if (hasAttrs) {
                decl += ">";
                w.println (s + decl);
                for (; e.hasMoreElements();) {
                    String key = e.nextElement();
                    Object o = f.getAttribute(key);
                    w.println (ss + "<attr name=\"" + key + "\" " + getValString(o) + "/>");
                }
                w.println (s + "</file>");
            } else {
                decl += "/>";
                w.println (s + decl);
            }
            System.err.println("WRITE: " + decl);
        } else if (f.isFolder()) {
            if (!"".equals(f.getName()))
                w.println (s + "<folder name=\"" + f.getName() + "\">");
            FileObject[] kids = f.getChildren();
            for (FileObject fob : kids) {
                recurseWrite (fob, w, depth + 1);
            }
            if (!"".equals(f.getName()))
                w.println (s + "</folder>");
        }
    }

    private String depthString (int i) {
        char[] c = new char[i * 4];
        Arrays.fill (c, ' ');
        return new String (c);
    }

    public int compare(FileObject a, FileObject b) {
        return a.getPath().compareTo(b.getPath());
    }
}

