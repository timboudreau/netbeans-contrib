/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * John Platts
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 * Portions Copyrighted 2009 John Platts
 */

package org.netbeans.modules.portalpack.servers.core.util;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class containing routines for encoding and decoding Base64 strings.
 * @author John Platts
 */
public final class Base64Routines {
    private Base64Routines() {}

    private static final char BASE64_ALPHABET[] =
            ("ABCDEFGH" +
             "IJKLMNOP" +
             "QRSTUVWX" +
             "YZabcdef" +
             "ghijklmn" +
             "opqrstuv" +
             "wxyz0123" +
             "456789+/").toCharArray();
    private static final char BASE64_PAD_CHAR = '=';
    private static final SortedMap<Character, Integer>
            BASE64_ALPHABET_CHAR_TO_INDEX_MAP =
            createCharToIndexMap(BASE64_ALPHABET, BASE64_PAD_CHAR);

    private static final SortedMap<Character, Integer> createCharToIndexMap(
            char[] alphabet, char padChar) {
        TreeMap<Character, Integer> treeMap =
                new TreeMap<Character, Integer>();

        for(int i = 0; i < 64; i++) {
            treeMap.put(alphabet[i], i);
        }

        treeMap.put(padChar, 64);

        return Collections.unmodifiableSortedMap(treeMap);
    }

    private static final int getBase64AlphabetIndex(char ch) {
        Integer index =
                BASE64_ALPHABET_CHAR_TO_INDEX_MAP.get(ch);

        if(index == null) {
            return -1;
        } else {
            int idx = index.intValue();

            if(idx >= 0 && idx <= 64) {
                return idx;
            } else {
                return -1;
            }
        }
    }

    public static CharBuffer base64Encode(ByteBuffer byteBuf) {
        if(byteBuf == null) {
            return null;
        }

        CharBuffer charBuf =
                CharBuffer.allocate(((byteBuf.remaining() + 2) / 3) << 2);

        while(byteBuf.hasRemaining()) {
            int b1 = byteBuf.get() & 0x000000FF;

            int b2, b3, n;
            
            if(byteBuf.hasRemaining()) {
                b2 = byteBuf.get() & 0x000000FF;
                
                if(byteBuf.hasRemaining()) {
                    b3 = byteBuf.get() & 0x000000FF;
                    n = 3;
                } else {
                    b3 = 0;
                    n = 2;
                }
            } else {
                b2 = 0;
                b3 = 0;
                n = 1;
            }

            /* +--first octet--+-second octet--+--third octet--+ */
            /* |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0| */
            /* +-----------+---+-------+-------+---+-----------+ */
            /* |5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0| */
            /* +--1.index--+--2.index--+--3.index--+--4.index--+ */

            /* +--first octet--+ */
            /* |7 6 5 4 3 2 1 0| */
            /* +-----------+---+ */
            /* |5 4 3 2 1 0|     */
            /* +--1.index--+     */
            charBuf.put(BASE64_ALPHABET[(b1 >>> 2) & 0x3F]);

            /* +--first octet--+-second octet--+ */
            /* |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0| */
            /* +-----------+---+-------+-------+ */
            /*             |5 4 3 2 1 0|         */
            /*             +--2.index--+         */
            charBuf.put(BASE64_ALPHABET[((b1 << 4) & 0x30) |
                    ((b2 >>> 4) & 0x0F)]);

            if(n >= 2) {
                /* +-second octet--+--third octet--+ */
                /* |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0| */
                /* +-------+-------+---+-----------+ */
                /*         |5 4 3 2 1 0|             */
                /*         +--3.index--+             */

                charBuf.put(BASE64_ALPHABET[((b2 << 2) & 0x3C) |
                        ((b3 >>> 6) & 0x03)]);

                if(n >= 3) {
                    /* +--third octet--+ */
                    /* |7 6 5 4 3 2 1 0| */
                    /* +---+-----------+ */
                    /*     |5 4 3 2 1 0| */
                    /*     +--4.index--+ */

                    charBuf.put(BASE64_ALPHABET[b3 & 0x3F]);
                    continue;
                } else {
                    charBuf.put('=');
                    break;
                }
            } else {
                charBuf.put("==");
                break;
            }
        }

        charBuf.flip();
        return charBuf;
    }
    public static CharBuffer base64EncodeUnpadded(ByteBuffer byteBuf) {
        CharBuffer paddedByteBuffer = base64Encode(byteBuf);

        if(paddedByteBuffer != null) {
            while(paddedByteBuffer.hasRemaining() &&
                    paddedByteBuffer.get(paddedByteBuffer.limit() - 1) ==
                    BASE64_PAD_CHAR) {
                paddedByteBuffer.limit(paddedByteBuffer.limit() - 1);
            }
        }
        
        return paddedByteBuffer;
    }

    public static final class UnpaddedBase64CharSequenceWrapper
            implements CharSequence, Serializable {
        private static final long serialVersionUID = -7050670133860565990L;
        public UnpaddedBase64CharSequenceWrapper(CharSequence baseSequence) {
            if(baseSequence == null || baseSequence.length() == 0) {
                this.baseSequence = "";
                this.totalLength = 0;
            } else {
                int baseSequenceLength = baseSequence.length();
                int baseSequenceRemainder = baseSequenceLength & 0x03;

                for(int i = baseSequenceLength - 1; i >= 0; i--) {
                    if(baseSequence.charAt(i) == BASE64_PAD_CHAR) {
                        throw new IllegalArgumentException("baseSequence " +
                                "must not contain any padding characters.");
                    }
                }

                this.baseSequence = baseSequence;
                switch(baseSequenceRemainder) {
                    case 0:
                        this.totalLength = baseSequenceLength;
                        break;
                    case 2:
                        this.totalLength = baseSequenceLength + 2;
                        break;
                    case 3:
                        this.totalLength = baseSequenceLength + 1;
                        break;
                    default:
                        throw new IllegalArgumentException("The baseSequence " +
                                "must have (baseSequence.length() mod 4) equal " +
                                "to 0, 2, or 3.");
                }
            }
        }
        private UnpaddedBase64CharSequenceWrapper(
                UnpaddedBase64CharSequenceWrapper wrapper,
                int start, int end) {
            this.baseSequence = wrapper.baseSequence.subSequence(start,
                    Math.min(end, wrapper.baseSequence.length()));
            this.totalLength = end - start;
        }

        public int length() {
            return totalLength;
        }

        public char charAt(int index) {
            if(index < 0) {
                throw new IndexOutOfBoundsException();
            } else if(index >= 0 && index < baseSequence.length()) {
                return baseSequence.charAt(index);
            } else if(index < totalLength) {
                return BASE64_PAD_CHAR;
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public CharSequence subSequence(int start, int end) {
            if(start < 0 || end < 0 || end > totalLength || start > end) {
                throw new IndexOutOfBoundsException();
            }

            if(start == end) {
                return "";
            } else if(end <= baseSequence.length()) {
                return baseSequence.subSequence(start, end);
            } else if(start < baseSequence.length()) {
                return new UnpaddedBase64CharSequenceWrapper(this,
                        start, end);
            } else {
                int length = end - start;
                char[] chars = (subsequenceBufferWeakRef != null) ?
                    subsequenceBufferWeakRef.get() : null;
                if(chars == null || chars.length < length) {
                    chars = new char[length];

                    subsequenceBufferWeakRef =
                            new WeakReference<char[]>(chars);
                }

                Arrays.fill(chars, 0, length, BASE64_PAD_CHAR);

                return new String(chars, 0, length);
            }
        }

        @Override
        public String toString() {
            int baseSequenceLength = baseSequence.length();

            if(baseSequenceLength == totalLength) {
                return baseSequence.toString();
            }

            char[] chars =
                    (sequenceCharsWeakRef != null) ?
                        sequenceCharsWeakRef.get() : null;
            
            if(chars == null) {
                chars = new char[totalLength];
                for(int i = 0; i < baseSequenceLength; i++) {
                    chars[i] = baseSequence.charAt(i);
                }

                Arrays.fill(chars, baseSequenceLength,
                        totalLength, BASE64_PAD_CHAR);

                sequenceCharsWeakRef =
                        new WeakReference<char[]>(chars);
            }

            return new String(chars);
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        @Override
        public boolean equals(Object o) {
            if(!(o instanceof UnpaddedBase64CharSequenceWrapper)) {
                return false;
            }

            if(this == o) {
                return true;
            }

            UnpaddedBase64CharSequenceWrapper other =
                    (UnpaddedBase64CharSequenceWrapper)o;

            if(this.baseSequence != other.baseSequence) {
                int len1 = this.baseSequence.length();
                int len2 = other.baseSequence.length();

                if(len1 != len2) {
                    return false;
                }

                for(int i = 0; i < len1; i++) {
                    if(this.baseSequence.charAt(i) !=
                            other.baseSequence.charAt(i)) {
                        return false;
                    }
                }
            }

            if(this.totalLength != other.totalLength) {
                return false;
            }

            return true;
        }

        private CharSequence baseSequence;
        private int totalLength;
        private transient WeakReference<char[]> sequenceCharsWeakRef;
        private transient WeakReference<char[]> subsequenceBufferWeakRef;
    }
    public static ByteBuffer base64DecodeUnpadded(CharSequence charSeq) {
        if(charSeq == null) {
            return null;
        } else {
            return base64Decode(new UnpaddedBase64CharSequenceWrapper(charSeq));
        }
    }
    public static ByteBuffer base64Decode(CharSequence charSeq) {
        if(charSeq == null) {
            return null;
        }

        int len = charSeq.length();

        if((len & 0x03) != 0) {
            throw new IllegalArgumentException("charSeq must have a length " +
                    "that is a multiple of 4.");
        }

        ByteBuffer byteBuf = ByteBuffer.allocate((len >>> 2) * 3);

        for(int i = 0; i < len; i += 4) {
            int idx1 =
                    getBase64AlphabetIndex(charSeq.charAt(i));
            int idx2 =
                    getBase64AlphabetIndex(charSeq.charAt(i + 1));
            int idx3 =
                    getBase64AlphabetIndex(charSeq.charAt(i + 2));
            int idx4 =
                    getBase64AlphabetIndex(charSeq.charAt(i + 3));

            if(idx1 == -1 || idx1 == 64 || idx2 == -1 || idx2 == 64 ||
                    idx3 == -1 ||
                    (idx3 == 64 && idx4 != 64) || idx4 == -1) {
            throw new IllegalArgumentException("charSeq is a malformed " +
                    "Base64 sequence.");
            }

            int n;
            if(idx3 == 64) {
                n = 1;
            } else if(idx4 == 64) {
                n = 2;
            } else {
                n = 3;
            }

            if(n < 3 && ((i + 4) < len)) {
                throw new IllegalArgumentException("Padding characters are " +
                        "only valid at the end of the Base64-encoded sequence.");
            }

            if(n < 1) {
                break;
            } else {
                /* +--first octet--+         */
                /* |7 6 5 4 3 2 1 0|         */
                /* +-----------+---+-------+ */
                /* |5 4 3 2 1 0|5 4 3 2 1 0| */
                /* +--1.index--+--2.index--+ */

                byteBuf.put((byte)((idx1 << 2) | (idx2 >>> 4)));

                if(n >= 2) {
                    /*     +-second octet--+     */
                    /*     |7 6 5 4 3 2 1 0|     */
                    /* +---+-------+-------+---+ */
                    /* |5 4 3 2 1 0|5 4 3 2 1 0| */
                    /* +--2.index--+--3.index--+ */

                    byteBuf.put((byte)((idx2 << 4) | (idx3 >>> 2)));

                    if(n >= 3) {
                        /*         +--third octet--+ */
                        /*         |7 6 5 4 3 2 1 0| */
                        /* +-------+---+-----------+ */
                        /* |5 4 3 2 1 0|5 4 3 2 1 0| */
                        /* +--3.index--+--4.index--+ */

                        byteBuf.put((byte)((idx3 << 6) | idx4));
                        continue;
                    } else {
                        /*         +--third octet--+ */
                        /*         |7 6 5 4 3 2 1 0| */
                        /* +-------+---+-----------+ */
                        /* |5 4 3 2 1 0|5 4 3 2 1 0| */
                        /* +--3.index--+--4.index--+ */
                        if((idx3 & 0x03) != 0) {
                            throw new IllegalArgumentException("charSeq is a " +
                                "malformed Base64 sequence.");
                        }

                        break;
                    }
                } else {
                    /*     +-second octet--+     */
                    /*     |7 6 5 4 3 2 1 0|     */
                    /* +---+-------+-------+---+ */
                    /* |5 4 3 2 1 0|5 4 3 2 1 0| */
                    /* +--2.index--+--3.index--+ */
                    if((idx2 & 0x0F) != 0) {
                        throw new IllegalArgumentException("charSeq is a " +
                                "malformed Base64 sequence.");
                    }

                    break;
                }
            }
        }

        byteBuf.flip();
        return byteBuf;
    }
}
