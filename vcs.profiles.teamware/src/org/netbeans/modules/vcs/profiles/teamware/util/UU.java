/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
            for (int i = 1, offset = 0; i < length - 1;) {
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
        for (int i = offset, charsInLine = 0; i < offset + length;) {
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
