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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Tim Boudreau
 */
public class JarToCopyModel implements FileModel {
    private final String pathToJarOnDisk;
    private final String destPath;

    /** Creates a new instance of JarToCopyModel */
    public JarToCopyModel(String destPath, String pathToJarOnDisk) {
        this.destPath = destPath;
        this.pathToJarOnDisk = pathToJarOnDisk;
        if (pathToJarOnDisk.equals("")) {
            throw new IllegalArgumentException ("Empty path name");
        }
    }

    public String getPath() {
        return destPath;
    }

    public void write(OutputStream stream) throws IOException {
        File f = new File (pathToJarOnDisk);
        if (!f.exists() || !f.isFile()) {
            throw new IOException (f.getPath() + " missing or not a file");
        }
        InputStream is = new BufferedInputStream (new FileInputStream (f));
        copy (is, stream);
        is.close();
    }
    
    public static void copy(InputStream is, OutputStream os)
    throws IOException {
        final byte[] BUFFER = new byte[4096];
        int len;
        for (;;) {
            len = is.read(BUFFER);

            if (len == -1) {
                return;
            }
            os.write(BUFFER, 0, len);
        }
    }    
}
