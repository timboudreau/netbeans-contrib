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
 */
package granularjunit.shmem;

import granularjunit.Commands;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Shared memory inter-process I/O.  An instance of Shmem owns a file, which
 * it reads and writes to.  In practice, a second Shmem instance in another
 * JVM reads and writes to the same file to pass messages between processes.
 * <p>
 * The current buffer size is 2048.  Into that file
 * are written "commands" - the meaning of these is up to the client.  Upon
 * a write, the thread that calls write() will block until the command has 
 * been "acknowledged" by another process also writing into the same file.
 * <p>
 * The file format is simple:  
 * <ul>
 * <li>One long which is a unique ID.  Each shmem has a unique ID.</li>
 * <li>An integer indicating the command code - may be any number except 0.</li>
 * <li>An integer indicating the following content length.  Must be less than
 *     MAX_BUFFER_SIZE (2048) - Long.SIZE + (Integer.SIZE * 2).</li>
 * <li>UTF-8 encoded string content of zero or more bytes up to the limit of the
 *     buffer.</li>
 * </ul>
 * Implementation notes:  Acknowledging is setting the command id in the buffer
 * to zero. An instance of Shmem does not see commands from itself - hasDataWaiting()
 * will never be true if the data currently in the buffer was produced by
 * (has the same UID as) this Shmem.
 *
 * @author Tim Boudreau
 */
public class Shmem {
    private File f;
    /**
     * Character sequence the constructor that takes an input stream will look
     * for.  This should be immediately followed by the file name this 
     * Shmem should read from, which should be immediately followed by
     * a newline.
     */
    public static final String MAGIC_SEQ = "#shmem-filespec:";
    private long uid;
    /** Creates a new instance of Shmem, which will operate over the 
     * passed file.  If it already exits, its contents will be lost.
     */
    public Shmem(String path) throws IOException {
        initUid();
        f = new File (path);
        if (!f.exists() || f.length() != BUFFER_LENGTH) {
            if (f.exists()) {
                if (!f.delete()) {
                    throw new IOException ("Could not delete " + f);
                }
            }
            if (!f.createNewFile()) {
                throw new IOException ("Could not create " + f);
            }
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
            stream.write(new byte[BUFFER_LENGTH]);
            stream.close();
            f = new File(path);
        }
        if (f.length() != BUFFER_LENGTH) {
            throw new IOException ("File length should be exactly " + 
                    BUFFER_LENGTH + " not " + f.length());
        }
        synchronized (Shmem.class) {
            icount++;
        }
        System.err.println("STREAM FILE DATA " + path);
        startWatchdog();
        instances.add (new WeakReference(this));
    }
    
    static Random rand = new Random(System.currentTimeMillis() + System.identityHashCode(Shmem.class));
    static int ix = rand.nextInt();
    
    private void initUid() {
        long uuid = 0;
        while (uuid == 0) {
            uuid = Double.doubleToLongBits(
                Math.pow(System.currentTimeMillis(),
                System.currentTimeMillis()) + rand.nextGaussian()) + 
                (ix++);
        }
        uid = uuid;
    }

    /**
     *  Unit test constructor - create a second Shmem over the same file as
     *  the passed one.
     */
    Shmem (Shmem mem) throws IOException {
        this (mem.getFile().getPath());
    }
    
    /**
     *  Create a new Shmem over a new temporary file.
     */
    Shmem() throws IOException {
        this ((OutputStream) null);
    }
    
    /**
     *  Create a new Shmem over a new temporary file and write the path
     *  of that file to the passed output stream.
     */
    public Shmem(OutputStream str) throws IOException {
        f = findUnusedFile();
        initUid();
        startWatchdog();
        icount++;
        if (str != null) {
            String pth = MAGIC_SEQ + f.getPath() + '\n';
            str.write(pth.getBytes(Command.UTF8));
            str.flush();
        }
        instances.add (new WeakReference(this));
    }
    
    /**
     * Get the file this Shmem reads/writes over.
     */
    public File getFile() {
        return f;
    }
    
    /**
     * Create a new Shmem which will read from the passed input stream
     * the path to the file it should use for I/O.  The file string should
     * be preceded by MAGIC_SEQ and followed immediately by a newline.
     * This constructor will block until a file path is written to the
     * input stream.
     */ 
    public Shmem(InputStream stream) throws IOException {
        this (readFilePathFromStream(stream));
    }
    
    public void finalize() throws Throwable {
        //XXX find a better way to do this...
        super.finalize();
        if (channel != null) {
            channel.close();
            raf.close();
        }
        synchronized (Shmem.class) {
            icount--;
            if (icount == 0) {
                watchdog.stop();
                watchdog = null;
            }
        }
    }
    
    private static int icount = 0;
    private static volatile Thread watchdog;;
    
    private static synchronized void startWatchdog() {
        if (watchdog == null) {
            watchdog = new Thread (new R());
            watchdog.start();
        }
    }
    
    private volatile Thread waitThread;
    private volatile long waitTime;
    
    /** Timeout after which we should stop waiting for acknowledgement or 
     * data */
    private static final long MAXWAIT = 60000;
    private void maybeTimeout() {
        if (f != null) {
            synchronized (f) {
                if (waitThread != null && System.currentTimeMillis() > waitTime + MAXWAIT) {
                    waitThread.interrupt();
                    try {
                        //Okay, no response for a very long time.  
                        //Self-acknowledge?
                        Command.setAcknowledged (buf());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    f.notifyAll();
                }
            }
        }
    }
    
    private void enterWait() {
        synchronized (f) {
            waitThread = Thread.currentThread();
            waitTime = System.currentTimeMillis();
        }
    }
    
    private void exitWait() {
        synchronized (f) {
            waitThread = null;
            waitTime = Long.MAX_VALUE;
        }
    }
    
    private static class R implements Runnable {
        public void run() {
            while (true) {
                for (Iterator <WeakReference<Shmem>> it=instances.iterator(); it.hasNext();) {
                    WeakReference ref = it.next();
                    Shmem mem = (Shmem) ref.get();
                    if (mem != null) {
                        mem.maybeTimeout();
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    
    private static Set <WeakReference<Shmem>> instances = new HashSet();
    
    private static File findUnusedFile() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir == null) {
            throw new IOException ("System property java.io.tmpdir not set");
        }
        File parent = new File (tmpDir);
        if (!parent.isDirectory()) {
            throw new IOException ("Can't access tempdir " + parent);
        }
        int ix = 0;
        File result;
        do {
            result = new File (parent, "junit-shmem" + (ix++));
        } while (result.exists());
        if (!result.createNewFile()) {
            throw new IOException ("Could not create " + result);
        }
        if (result.length() != 1024) {
            OutputStream stream = new BufferedOutputStream (new FileOutputStream(result));
            stream.write(new byte[BUFFER_LENGTH]);
            stream.close();
        }
        result.deleteOnExit();
        return result;
    }
    
    /**
     * Acknowledge the command currently in the buffer.
     */
    public void acknowledge() throws IOException {
        FileLock lock = lock();
        try {
            Command.setAcknowledged(buf());
        } finally {
            lock.release();
        }
    }

    /**
     * Determine if the last command sent has been acknowledged.
     */ 
    public boolean isAcknowledge() throws IOException {
        return Command.isAcknowledge(uid, buf());
    }
    
    private static String readFilePathFromStream (InputStream stream) throws IOException {
        int read = 0;
        BufferedInputStream buf = new BufferedInputStream (stream);
        StringWriter sb = new StringWriter();
        byte[] b = new byte [1024];
        System.err.println("wait for filename from " + stream);
        while ((read = buf.read(b)) > 0) {
            sb.append (new String (b, 0, read, "UTF-8"));
            String test = sb.toString();
            int tidx;
            int ridx;
            System.err.println("GOT " + test);
            if ((tidx = test.indexOf(MAGIC_SEQ)) < (ridx = test.lastIndexOf('\n'))) {
                int pos = tidx + MAGIC_SEQ.length();
                int len = ridx - pos;
                String result = test.substring(pos, pos + len);
                return result;
            }
        }
        throw new IOException ("Failed to get filespec");
    }
    
    private FileChannel channel;
    RandomAccessFile raf;
    private FileChannel getChannel() throws IOException {
        synchronized (f) {
            if (channel == null) {
                if (raf == null) {
                    raf = new RandomAccessFile (f, "rws");
                }
                channel = raf.getChannel();
            }
        }
        return channel;
    }
    
    private static final int BUFFER_LENGTH = 2048;
    private static MappedByteBuffer buf;
    MappedByteBuffer buf() throws IOException {
        synchronized (f) {
            if (buf == null) {
                FileChannel fc = getChannel();
                buf = fc.map(MapMode.READ_WRITE, 0, BUFFER_LENGTH);
            }
        }
        buf.rewind();
        return buf;
    }

    private FileLock lock() throws IOException {
        return getChannel().lock(0, BUFFER_LENGTH, true);
    }
    
    /**
     * Send the close command to the other end of the channel.
     */
    public void close() throws IOException, InterruptedException {
        send (Commands.CLOSED, "close");
    }
        
    /**
     * Send the passed command and content and block until it has been
     * acknowledged or the timeout has occurred.
     * @return Whether or not the command was acknowledged.
     */
    public boolean send (int command, String s) throws IOException, InterruptedException {
        FileLock lock = lock();
        ByteBuffer buf = buf();
        try {
            Command.write (uid, command, s, buf);
            System.err.println("WROTE COMMAND ID " + command + " " + s + " to file " + f);
        } finally {
            lock.release();
        }
        enterWait();
        boolean result = Command.waitForAcknowledgement(uid, Command.TIMEOUT, 
                buf);
        exitWait();
        return result;
    }
    
    public String toString() {
        return "SHMEM:" + uid; 
    }
    
    /**
     * Block until either the timeout has been passed or a command has been
     * placed on the queue by an Shmem other than this one.
     * @return A command from another Shmem instance over this file in this or
     *   another process
     */
    public Command getNextCommand(long timeout) throws InterruptedException, IOException {
        System.err.println("get next command");
        ByteBuffer buf = buf();
        Command result;
        if (Command.isDataWaiting(uid, buf)) {
            result = read();
        } else {
            if (Command.waitForCommand(uid, timeout, buf)) {
                result = read();
            } else {
                result = null;
            }
        }
        if (result != null) {
            acknowledge();
        }
        return result;
    }
    
    /**
     * Read a pending command without blocking if none is present.
     * This call does <b>not</b> acknowledge the command, so the remote
     * Shmem that posted it will remain blocked.
     */
    public Command peek() throws IOException {
        if (hasDataWaiting()) {
            return read();
        }
        return null;
    }
    
    private boolean waitForData(int timeout) throws InterruptedException, IOException {
        boolean result;
        enterWait();
        try {
            result = Command.waitForCommand(uid, timeout, buf());
        } finally {
            exitWait();
        }
        return result;
    }
    
    /**
     * Read the currently available command.  If the current command has
     * already been acknowledged, throws an exception.
     */
    public Command read () throws IOException {
        FileLock lock = lock();
        try {
            return Command.read (buf());
        } finally {
            lock.release();
        }
    }
    
    boolean ownsBufferData() throws IOException {
        return Command.isUID(uid, buf());
    }
    
    /**
     * Determine if there is a command in the buffer which was not 
     * written there by this instance of Shmem.
     */
    public boolean hasDataWaiting() throws IOException {
        return Command.isDataWaiting(uid, buf());
    }
    
    long getUID() {
        return uid;
    }
}
