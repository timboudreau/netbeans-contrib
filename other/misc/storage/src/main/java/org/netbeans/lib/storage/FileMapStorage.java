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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.lib.storage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import org.netbeans.api.storage.Storage;

/**
 * An implementation of the Storage interface over a memory mapped file.
 *
 * <i>TODO: This implementation is not 64 bits clean, and cannot handle
 * sizes above Integer.MAX_VALUE.</i>
 *
 */
public class FileMapStorage extends Storage {
    /** A file channel for writing the mapped file */
    private FileChannel writeChannel;
    /** A file channel for reading the mapped file */
    private FileChannel readChannel;
    /** The base number of bytes to allocate when a getWriteBuffer for writing is
     * needed. */
    private static final int BASE_BUFFER_SIZE = 8196;
    /**
     * The byte getWriteBuffer that write operations write into.  Actual buffers are
     * provided for writing by calling master.slice(); this getWriteBuffer simply
     * pre-allocates a fairly large chunk of memory to reduce repeated
     * allocations.
     */
    private ByteBuffer master;
    /** A byte getWriteBuffer mapped to the contents of the output file, from which
     * content is read. */
    private ByteBuffer contents;
    /** The number of bytes from the file that have been are currently mapped
     * into the contents ByteBuffer.  This will be checked on calls that read,
     * and if more than the currently mapped bytes are requested, the
     * contents bufffer will be replaced by a larger one */
    private long mappedRange;
    /**
     * The currently in use buffer.
     */
    private ByteBuffer buffer = null;
    /**
     * The number of bytes that have been written.
     */
    protected int bytesWritten = 0;
    /**
     * The file we are writing to.
     */
    private File outfile = null;
    
    public FileMapStorage() {
        init();
    }
    
    public FileMapStorage(File f) {
        this();
        outfile = f;
        if (f.exists()) {
            bytesWritten = (int) f.length();
        }
    }

    private void init() {
        contents = null;
        mappedRange = -1;
        master = ByteBuffer.allocateDirect (BASE_BUFFER_SIZE);
        readChannel = null;
        writeChannel = null;
        buffer = null;
        bytesWritten = 0;
    }

    /**
     * Ensure that the output file exists.
     */
    private void ensureFileExists() throws IOException {
        if (outfile == null || !outfile.exists()) {
            if (outfile == null) {
                String outdir = System.getProperty("java.io.tmpdir"); //NOI18N
                if (!outdir.endsWith(File.separator)) {
                    outdir += File.separator;
                }
                File dir = new File (outdir);
                if (!dir.exists() || !dir.canWrite()) {
                    //Handle the (unlikely) case we cannot write to the system
                    //temporary directory
                    IOException ise = new IOException ("Cannot" + //NOI18N
                    " write to " + outdir); //NOI18N
    //                ErrorManager.getDefault().annotate (ise,
    //                    NbBundle.getMessage (OutWriter.class,
    //                    "FMT_CannotWrite", //NOI18N
    //                    outdir));
                    throw ise;
                }
                //#47196 - if user holds down F9, many threads can enter this method
                //simultaneously and all try to create the same file
                synchronized (FileMapStorage.class) {
                    String fname = outdir + "cache" + Long.toString(System.currentTimeMillis()); //NOI18N
                    outfile = new File (fname);
                    while (outfile.exists()) {
                        fname += "x"; //NOI18N
                        outfile = new File(fname);
                    }
                }
            }
            System.err.println("Creating " + outfile);
            outfile.createNewFile();
            bytesWritten = 0;
        } else if (outfile.exists()) {
            bytesWritten = (int) outfile.length();
        }
    }
    
    public String toString() {
        return outfile == null ? "[unused or disposed FileMapStorage]" : outfile.getPath();
    }

    /**
     * Get a FileChannel opened for writing against the output file.
     */
    private FileChannel writeChannel() {
        try {
            if (writeChannel == null) {
                ensureFileExists();
                FileOutputStream fos = new FileOutputStream(outfile, true);
                writeChannel = fos.getChannel();
            }
            return writeChannel;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace(); //XXX
        } catch (IOException ioe) {
            ioe.printStackTrace(); //XXX
        }
        return null;
    }

    /**
     * Fetch a FileChannel for readin the file.
     */
    private FileChannel readChannel() {
        //TODO may be better to use RandomAccessFile and a single bidirectional
        //FileChannel rather than maintaining two separate ones.
        if (readChannel == null) {
            try {
                ensureFileExists();
                FileInputStream fis = new FileInputStream (outfile);
                readChannel = fis.getChannel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return readChannel;
    }

    /**
     * Fetch a getWriteBuffer of a specified size to use for appending new data to the
     * end of the file.
     */
    public synchronized ByteBuffer getWriteBuffer (int size) throws IOException {
        if (master == null || master.capacity() - master.position() < size) {
            int newSize = Math.max (BASE_BUFFER_SIZE * 2, 
                size + BASE_BUFFER_SIZE);
            
            master = ByteBuffer.allocateDirect (newSize);
        }

        if (buffer == null) {
            buffer = master.slice();
        } else {
            int charsRemaining = (buffer.capacity() - buffer.position()) / 2;

            if (charsRemaining < size) {
                buffer.flip();
                buffer = master.slice();
            }
        }
        outstandingBufferCount++;
        return buffer;
    }
    private int outstandingBufferCount = 0;

    /**
     * Dispose of a ByteBuffer which has been acquired for writing by one of
     * the write methods, writing its contents to the file.
     */
    public int write (ByteBuffer bb) throws IOException {
        synchronized (this) {
            if (bb == buffer) {
                buffer = null;
            }
        }
        int position = size();
        int byteCount = bb.position();
        bb.flip();
        if (writeChannel().isOpen()) { //If a thread was terminated while writing, it will be closed
            writeChannel().write (bb);
            synchronized (this) {
                bytesWritten += byteCount;
                outstandingBufferCount--;
            }
        }
        return position;
    }

    public synchronized void dispose() {
        if (writeChannel != null && writeChannel.isOpen()) {
            try {
                writeChannel.close();
                writeChannel = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (readChannel != null && readChannel.isOpen()) {
            try {
                readChannel.close();
                readChannel = null;
            } catch (Exception e) {
//                ErrorManager.getDefault().notify(e);
                e.printStackTrace();
            }
        }
        buffer = null;
        contents = null;
    }

    /**
     * Get a byte buffer representing the a getText of the contents of the
     * output file.  This is optimized to possibly map more of the output file
     * into memory if it is not already mapped.
     */
    public ByteBuffer getReadBuffer(long start, long byteCount) throws IOException {
        ByteBuffer contents;
        synchronized (this) {
            //XXX Some optimizations possible here:
            // - Don't map the entire file, just what is requested (perhaps if the mapped
            //    start - currentlyMappedStart > someThreshold
            // - Use RandomAccessFile and use one buffer for reading and writing (this may
            //    cause contention problems blocking repaints)
            contents = this.contents;
            if (byteCount == -1) {
                byteCount = ((int) readChannel().size()) - start;
            }
            
            if (contents == null || start + byteCount > mappedRange) {
                FileChannel ch = readChannel();
                long prevMappedRange = mappedRange;
                mappedRange = ch.size();
                try {
                    try {
                        contents = ch.position(0).map(FileChannel.MapMode.READ_ONLY,
                            0, mappedRange);
                        this.contents = contents;
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        if (log) log("Failed to memory map output file for " + //NOI18N
                                "reading.  Trying to read it normally."); //NOI18N
                        //If a lot of processes have crashed with mapped files (generally when testing),
                        //this exception may simply be that the memory cannot be allocated for mapping.
                        //Try to do it non-mapped
                        contents = ByteBuffer.allocate((int) mappedRange);
                        ch.position(0).read(contents);
                    }
                } catch (IOException ioe) {
                    /*ErrorManager.getDefault().log*/System.err.println("Failed to read output file. Start:" + start + " bytes reqd=" + //NOI18N
                        byteCount + " mapped range=" + mappedRange + //NOI18N
                        " previous mapped range=" + prevMappedRange + //NOI18N
                        " channel size: " + ch.size()); //NOI18N
                    throw ioe;
                }
            }
            try {
                contents.position ((int) start); //XXX
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException ("Bad position: " + start +
                        " contents capacity " + contents.capacity() + 
                        " limit " + contents.limit() + " position " +
                        contents.position());
            }
        }
        int limit = Math.min(contents.limit(), (int) byteCount); //XXX
        try {
            ByteBuffer result;
            if (limit != contents.limit()) {
                result = (ByteBuffer) contents.slice().limit(limit);
            } else {
                result = contents.slice();
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException ("Error setting limit to " + limit //NOI18N
            + " contents size = " + contents.limit() + " requested: read " + //NOI18N
            "buffer from " + start + " to be " + byteCount + " bytes"); //NOI18N
        }
    }

    public synchronized int size() {
        return bytesWritten;
    }

    public void flush() throws IOException {
        if (buffer != null) {
            if (log) log("FILEMAP STORAGE flush(): " + outstandingBufferCount);
            write (buffer);
            writeChannel.force(false);
            buffer = null;
        }
    }

    public void close() throws IOException {
        if (writeChannel != null) {
            flush();
            writeChannel.close();
            writeChannel = null;
            if (log) log("FILEMAP STORAGE CLOSE.  Outstanding buffer count: " + outstandingBufferCount);
        }
    }

    public boolean isClosed() {
        return writeChannel == null || !writeChannel.isOpen();
    }
    
    private static boolean log = false;
    private void log (String s) {
        System.err.println(s);
    }
    
    public long lastWrite() {
        return outfile != null ? outfile.lastModified() : Long.MIN_VALUE;
    }
    
    public void excise (long start, long end) throws IOException {
        flush();
        FileChannel rc = readChannel();
        ByteBuffer after = null;
        long size = rc.size();
        if (end < size) {
            after = ByteBuffer.allocateDirect ((int) (rc.size() - end));
            rc.read (after, end);
            after.flip();
        }
        
        FileChannel wc = writeChannel();
        wc.truncate(start);
        if (after != null) {
            wc.write (after);
        }
        bytesWritten = (int) wc.size();
    }
    
    public long replace (ByteBuffer bb, long start, long end) throws IOException {
        flush();
        bb.flip();
        int len = bb.limit();
        long oldLen = end - start;
        long delta = len - oldLen;
        
        FileChannel rc = readChannel();
        ByteBuffer after = null;
        long size = rc.size();
        if (end < size) {
            after = ByteBuffer.allocateDirect ((int) (rc.size() - end));
            rc.read (after, end);
            after.flip();
        }
        
        FileChannel wc = writeChannel();
        wc.truncate(start);
        wc.write(bb);
        if (after != null) {
            wc.write (after);
        }
        bytesWritten = (int) wc.size();
        return delta;
    }
    
    public void truncate (long end) throws IOException {
        if (end > size()) {
            throw new IllegalArgumentException ("Requested size > current size: " + end);
        }
        if (end < 0) {
            throw new IllegalArgumentException ("Negative size: " + end);
        }
        writeChannel().truncate(end);
        bytesWritten = (int) end;
    }
}
