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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package beans2nbm.gen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 *
 * @author Tim Boudreau
 */
public class NbmFileModel implements FileModel {
    private final String path;

    private List entries = new ArrayList();
    public NbmFileModel (String path) {
        this.path = path;
    }

    static String genClassnameDots (String classname) {
        assert classname.endsWith(".class");
        StringBuffer sb = new StringBuffer (classname.substring(0, classname.length() - ".class".length()));
        for (int i=0; i < sb.length(); i++) {
            if ('/' == sb.charAt(i)) {
                sb.setCharAt(i, '.');
            }
        }
        return sb.toString();
    }
    
    static String genSimpleClassname (String classname) {
        int start = classname.lastIndexOf('/');
        int end = classname.lastIndexOf('.');
        if (start == -1 || end == -1) {
            throw new IllegalArgumentException (classname);
        }
        return classname.substring(start, end);
    }

    public String getPath() {
        return path;
    }

    public void write(OutputStream stream) throws IOException {
        List l = buildEntries();
        JarOutputStream jos = new JarOutputStream (stream);
        jos.setLevel(9);
        try {
            for (Iterator i = l.iterator(); i.hasNext();) {
                FileModel file = (FileModel) i.next();
                JarEntry entry = new JarEntry (file.getPath());
                jos.putNextEntry(entry);
                file.write(jos);
                jos.closeEntry();
            }
        } finally {
            jos.close();
        }
    }
    
    public void add (FileModel mdl) {
        entries.add (mdl);
    }

    private List buildEntries() {
        List l = new ArrayList (entries);
        return l;
    }
    
}
