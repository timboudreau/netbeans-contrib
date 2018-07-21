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
 * CacheOutputStream.java
 *
 * Created on October 25, 2004, 7:14 PM
 */

package org.netbeans.lib.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.netbeans.lib.storage.HeapStorage;
import org.netbeans.api.storage.Storage;

/**
 *
 * @author tim
 */
final class CacheOutputStream extends OutputStream {
    private Storage storage = null;
    private final CacheImpl cache;
    private int index;
    private boolean init = false;

    /** Creates a new instance of CacheOutputStream */
    public CacheOutputStream(CacheImpl cache, int index) {
        assert index >= 0 && index < cache.size() : "Bad index " + index;
        this.index = index;
        this.cache = cache;
    }
    
    public void write (int i) throws IOException {
        write (new byte[] { (byte) i });
    }
    
    public void write (byte[] b, int off, int len) throws IOException {
        if (storage == null) {
            init();
        }
        ByteBuffer bb = storage.getWriteBuffer(len);
        bb.put (b, off, len);
        bb.position (len);
        storage.write(bb);
    }
    
    public void write (byte[] b) throws IOException {
        if (storage == null) {
            init();
        }
        ByteBuffer bb = storage.getWriteBuffer(b.length);
        bb.put (b);
        bb.position (b.length);
        storage.write(bb);
    }
    
    private void init() throws IOException {
        if (init) {
            throw new IOException ("Closed");
        } else {
            storage = new HeapStorage();
        }
        cache.registerOutputStream(this);
        init = true;
    }
    
    public void flush() throws IOException {
        //do nothing
    }
    
    void setIndex (int i) {
        this.index = i;
    }
    
    public void close() throws IOException {
        cache.write(this);
        storage = null;
        cache.unregisterOutputStream(this);
    }
    
    public int getIndex() {
        return index;
    }
    
    public void destroy() {
        cache.unregisterOutputStream(this);
        index = -1;
    }
    
    public ByteBuffer buffer() {
        try {
            int sz = (int) storage.size();
            ByteBuffer result = storage.getReadBuffer(0, sz).slice();
            result.position(sz);
            return result;
        } catch (IOException ioe) {
            //Never thrown by HeapStorage
            return null;
        }
    }
    
}
