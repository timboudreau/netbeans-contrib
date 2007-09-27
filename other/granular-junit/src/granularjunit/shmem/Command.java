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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Data which was received from an Shmem.
 */
public final class Command  {
    private final long uid;
    private final int cmd;
    private final String content;

    /**
     * Create a new Command with the passed source unique id, command code
     * and string content.  A command has the unique id of the Shmem that 
     * created it, an integer command ID whose meaning is a contract between
     * the code using Shmems, and ad-hoc String content.
     */ 
    Command(long uid, int cmd, String content) {
        if (uid == 0) throw new IllegalArgumentException ("UID 0 not allowed");
        this.uid = uid;
        this.cmd = cmd;
        this.content = content;
        if (content.length() > 2048) {
            throw new IllegalArgumentException ("Command too long");
        }
    }
    
    /**
     * Get the unique ID of the Shmem that created this command.
     */ 
    public long getSourceUid() {
        return uid;
    }
    
    /**
     * Get the integer command value (0 and -1 are reserved).
     */ 
    public int getCommand() {
        return cmd;
    }
    
    /**
     * Get the string content received for this command.
     */ 
    public String getContent() {
        return content;
    }
    
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Command) {
            Command c = (Command) o;
            result = c.getSourceUid() == getSourceUid() && c.getContent().equals(
                    getContent()) && c.getCommand() == getCommand();
        }
        return result;
    }
    
    public int hashCode() {
        return getContent().hashCode() * getCommand() + (int) getSourceUid();
    }
    
    public String toString() {
        return "Command " + getCommand() + " from " + getSourceUid() + ":" +
                getContent();
    }
    
    static final int TIMEOUT = 30000;
    
    /**
     * Wait until the data in the passed buffer becomes acknowledge.
     */ 
    public boolean waitForAcknowledgement (ByteBuffer buf) throws InterruptedException {
        return waitForAcknowledgement(getSourceUid(), TIMEOUT, buf);
    }
    
    /**
     * Write this command to the passed ByteBuffer and optionally wait
     * until it is acknowledged.
     * @param ByteBuffer the bytebuffer representing the memory mapped file
     * @param waitForAcknowledgement Whether or not to block until the
     *  buffer data is set to acknowledged
     */ 
    public boolean write (ByteBuffer buf, boolean waitForAcknowledgement) {
        boolean result = !waitForAcknowledgement;
        write (getSourceUid(), getCommand(), getContent(), buf);
        if (waitForAcknowledgement) {
            try {
                waitForAcknowledgement (getSourceUid(), TIMEOUT, buf);
                result = true;
            } catch (InterruptedException ex) {
            }
        }
        return result;
    }
    
    static boolean waitForAcknowledgement (long uid, long timeout, ByteBuffer buf) throws InterruptedException {
        if (timeout == -1) timeout = Long.MAX_VALUE;
        long time = System.currentTimeMillis();
        boolean result;
        while (!(result = isAcknowledge(uid, buf))) {
            if (result) {
                break;
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime - time > timeout) {
                    result = isAcknowledge(uid, buf);
                    break;
                }
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        return result;
    }
    
    static int ACK = 0;
    static boolean waitForCommand (long uid, long timeout, ByteBuffer buf) throws InterruptedException {
        if (timeout == -1) timeout = Long.MAX_VALUE;
        long time = System.currentTimeMillis();
        boolean result;
        while (!(result = (isUID(uid, buf) && isCommand(ACK, buf)))) {
            if (result) {
                break;
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime - time > timeout) {
                    break;
                }
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        return result;
    }
    
    static boolean isAcknowledge (long uid, ByteBuffer buf) {
        return isUID(uid, buf) && isCommand (0, buf);
    }
    
    static boolean isCommand(int cmd, ByteBuffer buf) {
        buf.rewind();
        buf.position (Long.SIZE);
        int commandInBuffer = buf.asIntBuffer().get();
        boolean result = commandInBuffer == cmd;
//        assert read(buf).getCommand() == cmd : " expected " + cmd + " not " + read(buf);
        return result;
    }
    
    static boolean isUID(long uid, ByteBuffer buf) {
        buf.position (0);
        long id = buf.asLongBuffer().get();
        return id == uid;
    }
    
    static boolean isDataWaiting(long uid, ByteBuffer buf) {
        return !isCommand(ACK, buf) && !isUID(uid, buf);
    }
    
    static void write (long uid, int cmdId, String content, ByteBuffer dest) {
        dest.position (0);
        dest.asLongBuffer().put(uid);
        dest.position (Long.SIZE);
        dest.asIntBuffer().put(cmdId).put(content.length());
        dest.position(Long.SIZE + Integer.SIZE + Integer.SIZE);
        System.err.println("WRITE CONTENT " + content + " uid " + uid);
        byte[] b = content.getBytes(UTF8);
        dest.put (b);
    }
    
    static Charset UTF8 = Charset.forName("UTF-8");
    static Command read (ByteBuffer buf) {
        buf.position (0);
        long uid = buf.asLongBuffer().get();
        if (uid == 0) {
            throw new IllegalArgumentException ("UID 0 not allowed");
        }
        buf.position(Long.SIZE);
        int[] ivals = new int[2];
        buf.asIntBuffer().get(ivals);
        int cmdId = ivals[0];
        int length = ivals[1];
        buf.position(Long.SIZE + Integer.SIZE + Integer.SIZE);
        ByteBuffer sub = buf.slice();
        CharBuffer cb = UTF8.decode(sub);
        char[] chars = new char[length];
        cb.get(chars);
        return new Command (uid, cmdId, new String(chars));
    }
    
    static void setAcknowledged (ByteBuffer buf) {
        buf.position(Long.SIZE);
        buf.asIntBuffer().put(0);
        assert isAck (buf) : "Command should be 0 " + read(buf);
    }
    
    static boolean isAck (ByteBuffer buf) {
        long l = ((ByteBuffer) buf.position(0)).asLongBuffer().get();
        boolean ack = isAcknowledge(l, buf);
        return ack;
    }
}