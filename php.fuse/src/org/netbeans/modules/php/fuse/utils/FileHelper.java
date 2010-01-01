package org.netbeans.modules.php.fuse.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.util.Exceptions;

/**
 * Helper for easier work with files.
 * @author Martin Fousek
 */
public class FileHelper {

    /**
     * Copy directory from some location to another one.
     * @param srcDir source directory
     * @param dstDir destination directory
     */
    public static void copyDirectory(File srcDir, File dstDir) {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }

            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),
                                     new File(dstDir, children[i]));
            }
        } else {
            try {
                copyFile(srcDir, dstDir);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Copy file from one location to another one.
     * @param src source file
     * @param dst destination file
     * @throws IOException the source file can be read or the destination file is read-only
     */
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}