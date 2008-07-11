/*
 * Util.java
 *
 * Created on January 11, 2007, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.commons.palette.jsp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Peter Williams
 */
public class Util {
    
    private Util() {
    }
        
    /** Creates a new file containing the specified string, creating and writing
     *  everything during an atomic action so that the dataloaders properly
     *  recognize the file.
     *
     * @param targetFolder folder where file will be created.
     * @param fileName name of file to be created, including extension if any.
     * @param data the data to be written.
     * @param encoding encoding to use for interpreting the data string.
     *
     * @return FileObject of the created file
     *
     * @throws IOException Any IOExceptions during the operation are the
     *   responsibility of the caller.
     */
    public static FileObject atomicWriteString(final FileObject targetFolder, final String fileName, 
            final String data, final String encoding) throws IOException {
        return atomicWriteBytes(targetFolder, fileName, data.getBytes(encoding));
    }
            
    /** Creates a new file containing the specified bytes, creating and writing
     *  everything during an atomic action so that the dataloaders properly
     *  recognize the file.
     *
     * @param targetFolder folder where file will be created.
     * @param fileName name of file to be created, including extension if any.
     * @param data the data to be written.
     *
     * @return FileObject of the created file
     *
     * @throws IOException Any IOExceptions during the operation are the
     *   responsibility of the caller.
     */
    public static FileObject atomicWriteBytes(final FileObject targetFolder, final String fileName, 
            final byte [] data) throws IOException {
        FileSystem targetFS = targetFolder.getFileSystem();
        targetFS.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileLock lock = null;
                OutputStream out = null;
                try {
                    FileObject targetFO = targetFolder.createData(fileName);
                    lock = targetFO.lock();
                    out = new BufferedOutputStream(targetFO.getOutputStream(lock));
                    out.write(data);
                } finally {
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                        }
                    }
                    if(lock != null) {
                        lock.releaseLock();
                    }
                }
            }
        });
        
        return targetFolder.getFileObject(fileName);
    }
    
    public static FileObject fastCopy(final FileObject srcFO, final FileObject targetFolder) throws IOException {
        File srcFile = FileUtil.toFile(srcFO);
        File targetFile = new File(FileUtil.toFile(targetFolder), srcFile.getName());
        copyFile(srcFile, targetFile);
        return FileUtil.toFileObject(targetFile);
    }
    
    public static boolean fastDelete(final FileObject targetFO) throws IOException {
        boolean result = true;
        if(targetFO.isValid()) {
            if(!targetFO.isFolder()) {
                targetFO.delete();
            } else {
                File targetDir = FileUtil.toFile(targetFO);
                result = deleteFolderImpl(targetDir);
            }
        }
        return result;
    }
    
    private static boolean deleteFolderImpl(File targetDir) throws IOException {
        boolean result = true;
        File [] children = targetDir.listFiles();
        if(children != null) {
            for(int i = 0; i < children.length && result; i++) {
                File target = new File(targetDir, children[i].getName());
                if(children[i].isDirectory()) {
                    result &= deleteFolderImpl(children[i]);
                } else {
                    result &= target.delete();
                }
            }
        }
        result &= targetDir.delete();
        return result;
    }
    
    public static void copyFile(final File srcFile, final File targetFile) throws IOException {
        if(isXml(srcFile)) {
            FileObject srcFO = FileUtil.toFileObject(srcFile);
            FileSystem fs = srcFO.getFileSystem();
            fs.runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    copyFileImpl(srcFile, targetFile);
                }
            });
        } else {
            copyFileImpl(srcFile, targetFile);
        }
    }

    private static boolean isXml(File f) {
        boolean result = false;
        String name = f.getName();
        int l = name.length();
        if(l > 3) {
            result = name.substring(l-4).compareToIgnoreCase(".xml") == 0;
        }
        return result;
    }
    
    public static void copyFileImpl(final File srcFile, final File targetFile) throws IOException {
        FileInputStream is = null;
        FileOutputStream os = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(targetFile);
            in = is.getChannel();
            out = os.getChannel();
            
            long fileSize = srcFile.length();
            long bufsize = Math.min(65536, fileSize);
            long offset = 0;
            
            do {
              offset += in.transferTo(offset, bufsize, out);
            } while(offset < fileSize);
            
        } finally {
            if(is != null) { try { is.close(); } catch(IOException ex) { } }
            if(os != null) { try { os.close(); } catch(IOException ex) { } }
            if(in != null) { try { in.close(); } catch(IOException ex) { } }
            if(out != null) { try { out.close(); } catch(IOException ex) { } }
        }
    }


    /** Handle finding fileobject via relative paths that include '..', as the
     *  standard implementation of getFileObject() does not handle this.
     *
     * @param folder The folder from which to start the search.
     * @param path Relative path name for which to locate the fileobject.
     *
     * @return FileObject of object specified by relative path, or null if no
     *  such object could be found.
     */
    public static FileObject getRelativeFileObject(final FileObject folder, final String path) {
        File target = FileUtil.normalizeFile(new File(FileUtil.toFile(folder), path));
        return FileUtil.toFileObject(target);
    }
     
    
}
