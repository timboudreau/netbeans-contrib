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
