/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
