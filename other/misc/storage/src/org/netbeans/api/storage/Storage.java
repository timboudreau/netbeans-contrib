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
/*
 * Storage.java
 *
 * Created on May 14, 2004, 1:40 PM
 */

package org.netbeans.api.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.netbeans.lib.storage.*;

/**
 * A simple abstraction for byte-based data storage.  May be heap based,
 * memory mapped, or other.
 *
 * @author  Tim Boudreau, Jesse Glick
 */
public abstract class Storage {
    /**
     * Get a ByteBuffer for reading over the storage, starting at the specified byte position and containing the
     * specified number of bytes
     *
     * @param startThe start byte
     * @param length How many bytes
     * @return A byte buffer
     * @throws IOException if there is a problem reading or allocating the buffer
     */
    public abstract ByteBuffer getReadBuffer (long start, long length) throws IOException;
    /**
     * Get a buffer for <strong>appending</strong> <code>length</code> bytes to the stored data.  Note that
     * writing into the returned buffer does not automatically write to the file - the returned buffer should
     * be passed to <code>write(ByteBuffer b)</code> to be saved once it has been filled.
     *
     * @param length The number of bytes to write
     * @return
     * @throws IOException
     */
    public abstract ByteBuffer getWriteBuffer (int length) throws IOException;
    /**
     * Write a ByteBuffer (presumably obtained from getWriteBuffer()) to persistent storage.  The buffer
     * may be underfilled; data will be written to the <code>limit</code> of the ByteBuffer, disregarding any
     * additional capacity.
     *
     * @param buf A ByteBuffer with data to write
     * @return The byte position of the <strong>start</strong> of data written
     * @throws IOException if there is a problem writing the data
     */
    public abstract int write (ByteBuffer buf) throws IOException;

    /**
     * Dispose of this storage, deleting all associated resources, files, data storage, etc.  This should only
     * be called after it is absolutely certain that nothing will try to write further data to the storage.
     *
     */
    public abstract void dispose ();

    /**
     * The number of bytes currently occupied by written data
     *
     * @return A byte count
     */
    public abstract int size();

    /**
     * For storages that implement a lazy writing scheme, force any pending data to be written to the storage.
     *
     * @throws IOException If there is a problem writing the data
     */
    public abstract void flush() throws IOException;

    /**
     * Close the storage for <strong>writing</strong> disposing of any resources used for writing to the storage,
     * but leaving it in a state where it may be read, calling <code>flush()</code> if necessary.  Subsequent calls
     * to write methods may reopen the storage for writing as needed.
     *
     * @throws IOException If there is a problem writing to the persistent storage
     */
    public abstract void close() throws IOException;

    /**
     * Determine if close() has been called on this storage.  Primarily used for cases where the gui should
     * display some status information if a process is still writing to the storage.
     *
     * @return true if the storage has been closed
     */
    public abstract boolean isClosed();

    /**
     * Get the time of the last write to the storage.
     * @return The time of the last write, or Long.MIN_VALUE if the storage is
     *  empty
     */
    public abstract long lastWrite();
    
    /**
     * Remove a section from somewhere in the middle of the storage.
     * @throws IOException if for some reason this cannot be done
     */
    public abstract void excise (long start, long end) throws IOException;
    
    /**
     * Replace a section somewhere in the storage with the contents of the
     * passed byte buffer.
     */
    public abstract long replace (ByteBuffer buf, long start, long end) throws IOException;
    
    /**
     * Truncate the storage to the passed length.
     */
    public abstract void truncate (long end) throws IOException;
    
    /**
     * Get a Storage object representing a subsection of this Storage object,
     * with its 0 position at the passed start position.  The returned
     * Storage object will not be writable.
     */
    public final Storage getSubStorage (long start, long end) throws IOException {
        return new SubStorage (getReadBuffer(start, end - start));
    }
    
    public static final Storage getStorage(Object o) {
        if (o instanceof java.io.File) {
            return new FileMapStorage ((java.io.File) o);
        } else if (Boolean.TRUE.equals(o)) {
            return new HeapStorage();
        } else if (Boolean.FALSE.equals(o)) {
            return new FileMapStorage();
        } else if (o != null) {
            throw new IllegalArgumentException ("Unrecognized hint: " + o);
        } else {
            //XXX use a low disk space flag, and use heap as needed
            return new FileMapStorage();
        }
    }
}
