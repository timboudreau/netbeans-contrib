/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.jmiutils;

import org.openide.filesystems.*;
import java.io.*;
import java.util.*;
import org.netbeans.api.mdr.JMIMapper;
import org.netbeans.api.mdr.JMIStreamFactory;

/**
 *
 * @author  mmatula
 * @version 
 */
public class FSSFImpl extends JMIStreamFactory {
    private final org.openide.filesystems.FileSystem fileSystem;
    private final java.util.Date modelLastModified;
    
    /** Creates a new instance of FSSImpl. Using this constructor is
     *  equivalent to using <code>FSSImpl(fs, null)</code>.
     *  @see #FSSFImpl(org.openide.filesystems.FileSystem, java.util.Date)
     *  @param fs The target filesystem
     *  @throws IllegalArgumentException If <code>fs == null</code>
     */
     public FSSFImpl(org.openide.filesystems.FileSystem fs) {
        this(fs, null);
    }
    
    /** Creates a new instance of FSSFImpl. FSSFImpl will compare the last
     *  modified date of any existing target files against the value of the
     *  <code>modelLastModified</code> paramter. If the target file's last
     *  modified date is later than <code>modelLastModified/code>
     *  then {@link #createStream(List, String, String)} will return
     *  <code>null</code> to prevent the {@link JMIMapper} from overwriting
     *  the file. If <code>modelLastModified == null</code>, then
     *  <code>createStream</code> will never return <code>null</code>.
     *  @param fs The target filesystem
     *  @throws IllegalArgumentException If <code>fs == null</code>
     */
    public FSSFImpl(org.openide.filesystems.FileSystem fs, java.util.Date modelLastModified) {
        if (fs == null)
            throw new IllegalArgumentException("ERROR: fs is null");
        fileSystem = fs;
        this.modelLastModified = modelLastModified;
    }

    public OutputStream createStream(List pkg, String className, String extension) throws IOException {
        // if the interface should be generated into a filesystem, do so...
        FileObject folder = fileSystem.getRoot();

        // first create folders for the destination package
        String folderName;
        FileObject tempFolder;
        for (Iterator it = pkg.iterator(); it.hasNext();) {
            folderName = (String) it.next();
            // create the folder only if it doesn't exist
            if ((tempFolder = folder.getFileObject(folderName)) == null) {
                tempFolder = folder.createFolder(folderName);
            }
            folder = tempFolder;
        }

        // create file for a class
        FileObject file = folder.getFileObject(className, extension);
        if (file != null) {
            // don't touch the file if it is up to date
            if (modelLastModified != null && modelLastModified.before(file.lastModified()))
                return null;
            
            // if the file already exists, delete it
            FileLock lock = file.lock();
            file.delete(lock);
            lock.releaseLock();
        }
        file = folder.createData(className, extension);

        // lock the new file and get the output stream for it
        FileLock fileLock = file.lock();
        return new FSStream(file.getOutputStream(fileLock), fileLock);
    }
    
    private static class FSStream extends FilterOutputStream {
        private final FileLock fileLock;
        
        private FSStream(OutputStream stream, FileLock fileLock) {
            super(stream);
            this.fileLock = fileLock;
        }
        
        public void close() throws IOException {
            super.close();
            fileLock.releaseLock();
        }
    }
}
