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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.util;

import java.io.IOException;
import java.io.OutputStream;

public class UU {

    /** Decodes a line of uuencoded data to an output stream */
    public static void decode(String sdata, OutputStream out) throws IOException {
        if (sdata == null || sdata.length() < 3) {
            return;
        }
        try {
            char[] data = sdata.toCharArray();
            int byteLength = data[0] - 32;
            int length = 1 + (byteLength / 3) * 4;
            if ((byteLength % 3) > 0) {
                length += 1 + byteLength % 3;
            }
            for (int i = 1; i < length - 1;) {
                while (data[i] >= 96 || data[i] < 32) {
                    i ++;
                }
                if (i >= length - 1) {
                    break;
                }
                int char0 = data[i++];
                int char1 = data[i++];
                if (char0 == '\u0001' || char1 == '\u0001') {
                    break;
                }
                out.write((char0 - 32) << 2 | (char1 - 32) >> 4);
                if (i >= length) {
                    // only one byte
                } else {
                    int char2 = data[i++];
                    int value = (char1 - 32) << 4 | (char2 - 32) >> 2;
                    out.write(value & 0xff);
                    if (i >= length) {
                        // only 2 bytes
                    } else {
                        int char3 = data[i++];
                        value = (char2 - 32) << 6 | (char3 - 32);
                        out.write(value & 0xff);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ArrayIndexOutOfBoundsException e2 =
                new ArrayIndexOutOfBoundsException("Error decoding '" + sdata + "': " + e.getMessage());
            e2.initCause(e);
            throw e2;
        }
    }
    
    /** uuencodes a chunk of data.
     */
    public static String encode(byte[] data, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > data.length) {
            throw new IndexOutOfBoundsException();
        }
        StringBuffer sb = new StringBuffer();
        StringBuffer line = new StringBuffer();
        int bytesInLine = 0;
        for (int i = offset; i < offset + length;) {
            int byte0 = ((int) data[i++]) & 0xff;
            int byte1 = (i < data.length) ? ((int) data[i++]) & 0xff : 0x100;
            int byte2 = (i < data.length) ?  ((int) data[i++]) & 0xff : 0x100;

            line.append((char) (32 + (byte0 >> 2)));
            bytesInLine  ++;
            if (byte1 == 0x100) {
                line.append((char) (32 + ((byte0 << 4) & 0x30)));
            } else {
                line.append((char) (32 + ((byte0 << 4 | byte1 >> 4) & 0x3f)));
                bytesInLine ++;
                if (byte2 == 0x100) {
                    line.append((char) (32 + ((byte1 << 2) & 0x3f)));
                } else {
                    line.append((char) (32 + ((byte1 << 2 | byte2 >> 6) & 0x3f)));
                    line.append((char) (32 + ((byte2 & 0x3f))));
                    bytesInLine ++;
                }
            }
            if (line.length() > 56) {
                sb.append((char) (32 + bytesInLine));
                sb.append(line);
                sb.append('\n');
                bytesInLine = 0;
                line.setLength(0);
            }
        }
        if (bytesInLine > 0) {
            sb.append((char) (32 + bytesInLine));
            sb.append(line);
            sb.append('\n');
        }
        return sb.toString();
    }

}
