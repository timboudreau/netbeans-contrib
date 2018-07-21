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

import java.io.IOException;
import java.nio.ByteBuffer;
import org.netbeans.api.storage.Storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Heap based implementation of the Storage interface, over a byte array.
 * <i>TODO: This implementation is not 64 bits clean, and cannot handle
 * sizes above Integer.MAX_VALUE.</i>
 *
 */
public class HeapStorage extends Storage {
    private boolean closed = true;
    private byte[] bytes = new byte[2048];
    private int size = 0;
    private long lastWrite = System.currentTimeMillis();
    private File file = null;

    public Storage toFileMapStorage() throws IOException {
        FileMapStorage result = new FileMapStorage();
        result.write(getReadBuffer(0, size));
        return result;
    }
    
    public HeapStorage() {
        
    }
    
    public HeapStorage(File f) throws IOException {
        load(f);
        this.file = f;
    }
    
    private void load(File f) throws IOException {
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            if (f.length() > Integer.MAX_VALUE) {
                throw new IOException ("File too large: " + f.length());
            }
            bytes = new byte[(int) f.length()];
            size = (int) f.length();
            fis.close();
            lastWrite = f.lastModified();
        }
    }

    public ByteBuffer getReadBuffer(long start, long length) throws IOException {
        return ByteBuffer.wrap(bytes, (int)start, (int) length);
    }

    public ByteBuffer getWriteBuffer(int length) throws IOException {
        return ByteBuffer.allocate(length);
    }

    public synchronized int write(ByteBuffer buf) throws IOException {
        closed = false;
        int oldSize = size;
        size += buf.limit();
        if (size > bytes.length) {
            byte[] oldBytes = bytes;
            bytes = new byte[Math.max (oldSize * 2, (buf.limit() * 2) + oldSize)]; 
            System.arraycopy (oldBytes, 0, bytes, 0, oldSize);
        }
        buf.flip();
        buf.get(bytes, oldSize, buf.limit());
        
        stamp();
        return oldSize;
    }

    public synchronized void dispose() {
        bytes = new byte[0];
        size = 0;
    }

    public synchronized int size() {
        return size;
    }

    public void flush() throws IOException {
        //N/A
    }

    public void close() throws IOException {
        closed = true;
        if (file != null) {
            save (file);
        }
    }
    
    private void save (File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        if (size() > 0) {
            ByteBuffer bb = getReadBuffer (0, size);
            fos.getChannel().write (bb);
        }
        fos.close();
    }

    public boolean isClosed() {
        return closed;  
    }
    
    public long lastWrite() {
        return lastWrite;
    }
    
    public void excise (long start, long end) {
        byte[] nue = new byte[bytes.length - ((int)end - (int)start)];
        for (int i=0; i < start; i++) {
            nue[i] = bytes[i];
        }
        int ix = (int)start;
        for (int i=(int)end; i < bytes.length; i++) {
            nue[ix] = bytes[i];
            ix++;
        }
        size -= (int) (end - start);
        this.bytes = nue;
        stamp();
    }
    
    public long replace (ByteBuffer bb, long start, long end) throws IOException {
        bb.flip();
        int len = bb.limit();
        long oldLen = end - start;
        long delta = len - oldLen;
        
        byte[] nue = new byte[bytes.length + (int)delta];
        for (int i=0; i < start; i++) {
            nue[i] = bytes[i];
        }
        bb.get (nue, (int) start, len);
        
        int ix = (int)start + len;
        for (int i=(int)end; i < bytes.length; i++) {
            nue[ix] = bytes[i];
            ix++;
        }
        bytes = nue;
        size += delta;
        return delta;
    }
    
    public void truncate (long end) throws IOException {
        if (end > size()) {
            throw new IllegalArgumentException ("Requested size > current size: " + end);
        }
        if (end < 0) {
            throw new IllegalArgumentException ("Negative size: " + end);
        }
        size = (int) end;
    }
    
    public String toString() {
        return bytes == null ? "Empty" : new String(bytes);
    }
    
    private void stamp() {
        lastWrite = System.currentTimeMillis();
    }
}
