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
 * SubCache.java
 *
 * Created on October 31, 2004, 1:41 PM
 */

package org.netbeans.api.cache;
import java.io.*;
import java.nio.*;

/**
 * A sub cache that provides indexed access to a single entry in another
 * cache.
 *
 * @author Tim Boudreau
 */
final class SubCache extends Cache {
    private final CacheParser parser;
    private final Cache cache;
    private final int base;

    /** Creates a new instance of SubCache */
    public SubCache(Cache cache, CacheParser parser, int index) {
        this.cache = cache;
        this.parser = parser;
        this.base = index;
    }

    public ByteBuffer getBuffer(int index) {
        ByteBuffer master = cache.getBuffer(base);
        System.err.println("getBuffer " + index + " capacity " + master.capacity() + " currlimit " + master.limit());
        parser.setBuffer (master);
        int start = parser.indexOf(index);
        master.position (start);
        int end = parser.indexOf(index+1);
        System.err.println("getBuffer " + index + " limit to " + end + " start " + start + " capacity " + master.capacity() + " currlimit " + master.limit());
        master.limit (end);
            
        ByteBuffer result = master.slice();
        return result;
    }
    public int size() {
        parser.setBuffer (cache.getBuffer(base));
        return parser.size();
    }
    
    public int hashCode() {
        return (parser.hashCode() * 31) + (cache.hashCode() * 11) + base;
    }
    
    public boolean equals (Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubCache) {
            SubCache other = (SubCache) o;
            return other.parser.equals(parser) && other.base == base &&
                    other.cache.equals(cache);
        } else {
            return false;
        }
    }
    

    public CacheInputStream getInputStream(int index) {
        throw new UnsupportedOperationException ("not implemented"); //XXX
    }

    public void add(ByteBuffer bb) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public void add(CharSequence seq) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public void deleteEntry(int index) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }

    public OutputStream getOutputStream(int index) throws IOException {
        throw new IOException ("Sub-caches are read only");
    }
}
