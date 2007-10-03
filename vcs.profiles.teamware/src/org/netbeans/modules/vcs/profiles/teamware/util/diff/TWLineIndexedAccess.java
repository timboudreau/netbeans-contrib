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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcs.profiles.teamware.util.diff;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Random access to an array of lines.
 *
 * @author  Martin Entlicher
 */
public class TWLineIndexedAccess extends TWObjectIndexedAccess {

    private static final int BUFF_LENGTH = 1024;

    private List lines;

    /**
     * Creates a new instance of TWLineIndexedAccess
     *
     * @param r The reader to read lines from.
     */
    public TWLineIndexedAccess(Reader r) throws IOException {
        lines = new ArrayList();
        try {
            initLines(r);
        } finally {
            r.close();
        }
    }
    
    private void initLines(Reader r) throws IOException {
        char[] buffer = new char[BUFF_LENGTH];
        int length;
        StringBuffer lineBuff = new StringBuffer();
        while((length = r.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                if (buffer[i] == '\n') {
                    lines.add(lineBuff.toString());
                    lineBuff.delete(0, lineBuff.length());
                } else {
                    lineBuff.append(buffer[i]);
                }
            }
        }
        if (lineBuff.length() > 0) {
            lines.add(lineBuff.toString());
        }
    }
    
    public long length() {
        return lines.size();
    }
    
    public Object readAt(long pos) throws IOException {
        return lines.get((int) pos);
    }
    
    public Object[] readFullyAt(long pos, long length) throws IOException {
        String[] subLines = new String[(int) length];
        for (int i = 0; i < length; i++, pos++) {
            if (pos < length()) {
                subLines[i] = (String) lines.get((int) pos);
            } else {
                throw new EOFException(pos+" >= "+length());
            }
        }
        return subLines;
    }
    
    public void readFullyAt(long pos, Object[] obj) throws IOException {
        for (int i = 0; i < obj.length; i++, pos++) {
            if (pos < length()) {
                obj[i] = lines.get((int) pos);
            } else {
                throw new EOFException(pos+" >= "+length());
            }
        }
    }
    
}
