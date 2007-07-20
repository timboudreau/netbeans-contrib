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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tim Boudreau
 */
final class AdHocFileModel implements FileModel {
    private final String path;
    private final String name;
    private final byte[] bytes;
    private AdHocFileModel(String path, String name, byte[] bytes) {
        this.path = path;
        this.bytes = bytes;
        this.name = name;
    }
    
    public String getPath() {
        return path + "/" + name;
    }

    public void write(OutputStream stream) throws IOException {
        System.err.println("Write " + getPath());
        stream.write(bytes);
        stream.flush();
    }

    String getName() {
        return name;
    }

    public String toString() {
        return new String (bytes);
    }
    
    static AdHocFileModel create (FileObject src, String path) throws IOException {
        byte[] b = getBytes(src);
        if (b.length > 0) {
            String nm = src.getPath().replace("/", "-");
            return new AdHocFileModel (path, nm + ".content", b);
        }
        return null;
    }
    
    private static byte[] getBytes (FileObject fob) throws IOException {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        InputStream in  = fob.getInputStream();
        try {
            FileUtil.copy(in, str);
        } finally {
            str.close();
            in.close();
        }
        return str.toByteArray();
    }
}
