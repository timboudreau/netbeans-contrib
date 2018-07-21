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
 * BBStream.java
 *
 * Created on October 20, 2004, 12:28 PM
 */

package org.netbeans.api.cache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * Trivial stream wrapper over a byte buffer (I have a feeling there probably
 * is some JDK class that does this).
 *
 * @author Tim Boudreau
 */
final class BBStream extends CacheInputStream {
    private final ByteBuffer buf;
    /** Creates a new instance of BBStream */
    BBStream(ByteBuffer bb) {
        this.buf = bb;
    }

    public int read() {
        return (int) buf.get();
    }
    
    public int read(byte[] b) {
        int count = Math.min(b.length, buf.limit() - buf.position());
        if (count > 0) {
            buf.get(b, 0, count);
        }
        return count;
    }
    
    public int read(byte[] b, int off, int len) {
        assert len <= b.length : "Bad length: " + len;
        int count = Math.min (len, buf.limit() - buf.position());
        if (count > 0) {
            buf.get(b, off, count);
        }
        return count;
    }
    
    public int available() {
        return buf.limit() - buf.position();
    }
    
    public boolean markSupported() {
        return true;
    }
    
    public void mark (int readlimit) {
        buf.mark();
    }
    
    public void reset() throws IOException {
        buf.reset();
    }
    
    public long getPosition() {
        return buf.position();
    }
    
    public InputStream newStream(long start, long end) {
        int pos = buf.position();
        buf.position ((int) start);
        ByteBuffer nue = buf.slice();
        nue.limit ((int) (end - start));
        buf.position(pos);
        return new BBStream(nue);
    }
    
    public ByteBuffer getBuffer() {
        return buf.asReadOnlyBuffer();
    }
    
    public String toString() {
        return "[BBStream]" + buf.asCharBuffer();
    }
}
