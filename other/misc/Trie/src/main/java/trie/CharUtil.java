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
package trie;

/**
 *
 * @author tim
 */
final class CharUtil {
    private CharUtil() {}

    static int[] depthHashesCharsInt (CharSequence seq) {
        final int l = seq.length();
        final int ct = l / 2 + (l % 2 == 0 ? 0 : 1);
        int[] result = new int[ct];
        int start = 0;
        for (int i=0; i < result.length; i++) {
            result[i] = (int) depthHashChars (seq, start); //with a max len of 2 per call, cast can't lose data
            start += 16;
        }
        return result;
    }

    static long[] depthHashesChars (CharSequence seq) {
        final int l = seq.length();
        final int ct = l / 4 + (l % 4 == 0 ? 0 : 1);
        long[] result = new long[ct];
        int start = 0;
        for (int i=0; i < result.length; i++) {
            result[i] = depthHashChars (seq, start);
            start += 16;
        }
        return result;
    }
    
    static int[] depthHashesBytes (CharSequence seq) {
        final int l = seq.length();
        final int ct = l / 4 + (l % 4 == 0 ? 0 : 1);
        int[] result = new int[ct];
        int start = 0;
//        System.err.println("HASHING " + seq + " to " + result.length + " ints");
        for (int i=0; i < result.length; i++) {
            result[i] = depthHashBytes (seq, start);
//            System.err.println(" " + i + " " + result[i]);
            start += 4;
        }
        return result;
    }
    
    static long depthHashChars (CharSequence seq, int startpoint) {
        char[] x = chars (seq, startpoint, 4);
        long result = 0;
        for (int i=0; i < x.length; i++) {
            result |= ((short) x[i]) << 8 * (4 - i);
        }
        return result;
    }    
    
    static int depthHashBytes (CharSequence seq, int startpoint) {
        byte[] x = bytes (seq, startpoint, 4);
        int result = 0;
        for (int i=0; i < x.length; i++) {
            int curr = x[i] << 8 * (3-i); 
            result |= curr;
        }
        return result;
    }
    
    static String byteHashToString (int[] hashes) {
        char[] c = new char[hashes.length * 4];
        
        for (int i=0; i < hashes.length; i++) {
            int pos = i * 4;
            decodeByteHash (hashes[i], c, pos);
        }
        int end = c.length-1;
        while (c[end] == 0) {
            end--;
        }
        return new String(c, 0, end + 1);
    }
    
    static String byteHashToString (int hash) {
        char[] c = new char[4];
        decodeByteHash (hash, c, 0);
        int end = c.length-1;
        while (c[end] == 0) {
            end--;
        }
        return new String(c, 0, end + 1);
    }
    
    static void decodeByteHash (int hash, char[] dest, int start) {
        int max = Math.min (3, dest.length-1);
        for (int i=max; i >= 0; i--) {
            int mask = masksByFours[i];
            int curr = hash & mask;
            dest[start + (max-i)] = ((char) (curr >>> 8*i));
        }
    }
    
    static byte charAt (int idx, int value) {
        return (byte) ((value & masksByFours[3 - idx]) >>> 8 * (3 - idx));
    }
    
    static int[] masksByFours = new int[] {
        0x000000FF,
        0x0000FF00,
        0x00FF0000,
        0xFF000000,
    };
    
    static int length (int fourChars) {
        return (fourChars & 0x000000FF) != 0 ? 4 :
            (fourChars & 0x0000FFFF) != 0 ? 3 :
            (fourChars & 0x00FFFFFF) != 0 ? 2 :
            (fourChars & 0xFFFFFFFF) != 0 ? 1 : 0;
    }
    
    static boolean match (int test, int data) {
        if (test == data) return true;
        int ch = countConsecutiveMatchingChars(test, data);
        return ch >= length(data);
    }
    
    static boolean partialMatch (int test, int data) {
        return test != data && match (test, data);
    }
    
    static int countConsecutiveMatchingChars (int test, int val) {
        int result = 0;
        for (int i=masksByFours.length-1; i >=0; i--) {
            if ((test & masksByFours[i]) == (val & masksByFours[i])) {
                result++;
            } else {
                //Don't want matches like &23aa00ff and 23bb00ff == 3, should be 1
                break;
            }
        }
        return result;
    }
    
    
    private static char[] chars (final CharSequence cs, final int start, final int maxlen) {
        if (cs instanceof String) {
            int l = Math.min(cs.length() - start, maxlen);
            char[] c = new char[l];
            ((String) cs).getChars (start, start + l, c, 0);
            return c;
        } else if (cs instanceof StringBuffer) {
            int l = Math.min(cs.length() - start, maxlen);
            char[] c = new char[l];
            ((StringBuffer) cs).getChars (start, start + l, c, 0);
            return c;
        } else {
            char[] c = new char[Math.min(cs.length() - start, maxlen)];
            for (int i=0; i < cs.length(); i++) {
                c[i] = cs.charAt(i);
            }
            return c;
        }
    }    
    
    //NON 8 BIT CLEAN VERSION:
    private static byte[] bytes (final CharSequence cs, final int start, final int maxlen) {
        if (cs instanceof String) {
            int l = Math.min(cs.length() - start, maxlen);
            byte[] b = new byte[l];
            ((String) cs).getBytes(start, start + l, b, 0);
            return b;
        } else if (cs instanceof StringBuffer) {
            int l = Math.min(cs.length() - start, maxlen);
            char[] c = new char[l];
            byte[] b = new byte[l];
            ((StringBuffer) cs).getChars (start, start + l, c, 0);
            for (int i=0; i < c.length; i++) {
                b[i] = (byte) c[i];
            }
            return b;
        } else {
            byte[] b = new byte[Math.min(cs.length() - start, maxlen)];
            for (int i=0; i < cs.length(); i++) {
                b[i] = (byte) cs.charAt(i);
            }
            return b;
        }
    }      
    
}
