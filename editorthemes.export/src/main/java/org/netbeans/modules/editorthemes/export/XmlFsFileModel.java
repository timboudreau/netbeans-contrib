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
        return path + "/layer.xml"; //NOI18N
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
        PrintWriter w = new PrintWriter (stream);
        try {
            w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //NOI18N
            w.println("<!DOCTYPE filesystem PUBLIC " + //NOI18N
                    "\"-//NetBeans//DTD Filesystem 1.1//EN\" " + //NOI18N
                    "\"http://www.netbeans.org/dtds/filesystem-1_1.dtd\">"); //NOI18N
            w.println ("<filesystem>"); //NOI18N
            recurseWrite (fs.getRoot(), w, 0);
        } finally {
            w.println ("</filesystem>"); //NOI18N
            w.flush();
        }
    }

    private String getValString(Object o) {
        if (o instanceof String) {
            return "stringvalue=\"" + o.toString() + "\""; //NOI18N
        } else if (o instanceof Class) {
            Class clazz = (Class) o;
            return "stringvalue=\"" + clazz.getName() + "\""; //NOI18N
        } else if (o instanceof Method) {
            Method m = (Method) o;
            Class clazz = m.getDeclaringClass();
            return "methodvalue=\"" + clazz.getCanonicalName() + m.getName() + "\""; //NOI18N
        } else if (o instanceof Integer) {
            Integer in = (Integer) o;
            return "intvalue=\"" + in.intValue() + "\""; //NOI18N
        } else if (o instanceof Boolean) {
            return "boolvalue=\"" + o.toString() + "\""; //NOI18N
        } else {
            throw new IllegalArgumentException ("What is " + o.getClass() + " " + o); //NOI18N
        }
    }

    private void recurseWrite(FileObject f, PrintWriter w, int depth) throws IOException {
        String s = depthString(depth);
        if (f.isData()) {
            AdHocFileModel fm = AdHocFileModel.create(f, path);
            String decl = "<file name=\"" + f.getNameExt() + "\""; //NOI18N
            if (fm != null) {
                decl += " url=\"" + fm.getName() + "\""; //NOI18N
                embeddedFiles.add (fm);
            }
            String ss = depthString (depth + 1);
            Enumeration <String> e = f.getAttributes();
            boolean hasAttrs = e.hasMoreElements();
            if (hasAttrs) {
                decl += ">"; //NOI18N
                w.println (s + decl);
                for (; e.hasMoreElements();) {
                    String key = e.nextElement();
                    Object o = f.getAttribute(key);
                    w.println (ss + "<attr name=\"" + key + "\" " + getValString(o) + "/>"); //NOI18N
                }
                w.println (s + "</file>"); //NOI18N
            } else {
                decl += "/>"; //NOI18N
                w.println (s + decl);
            }
            System.err.println("WRITE: " + decl); //NOI18N
        } else if (f.isFolder()) {
            if (!"".equals(f.getName()))
                w.println (s + "<folder name=\"" + f.getName() + "\">"); //NOI18N
            FileObject[] kids = f.getChildren();
            for (FileObject fob : kids) {
                recurseWrite (fob, w, depth + 1);
            }
            if (!"".equals(f.getName())) {//NOI18N
                w.println (s + "</folder>"); //NOI18N
            }
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

