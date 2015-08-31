/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.lib.callgraph.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * byte[] backed string to cut memory consumption from Strings in half for most
 * codebases, to reduce memory footprint to process larger codebases by using 8
 * bits per character instead of 16.
 *
 * @author Tim Boudreau
 */
public class EightBitStrings {

    private static final Charset UTF = Charset.forName("UTF-8");

    private final InternTable INTERN_TABLE = new InternTable();
    public final CharSequence DOT = create(".");
    public final CharSequence QUOTE = create("\"");
    public final CharSequence CLOSE_OPEN_QUOTE = create("\" \"");

    private boolean disabled;

    public EightBitStrings(boolean disabled) {
        this.disabled = disabled;
    }

    public void clear() {
        INTERN_TABLE.dispose();
    }

    public CharSequence create(CharSequence string) {
        if (disabled) {
            return string;
        }
        return INTERN_TABLE.intern(string);
    }

    public CharSequence concat(CharSequence... seqs) {
        if (disabled) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence c : seqs) {
                sb.append(c);
            }
            return sb.toString();
        }
        return new Concatenation(seqs);
    }

    private static byte[] toBytes(CharSequence seq) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(seq.toString().getBytes(UTF));
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static CharSequence toCharSequence(byte[] bytes) {
        try {
            return UTF.newDecoder().decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    int internTableSize() {
        return INTERN_TABLE.last + 1;
    }

    static class InternTable {

        private static final int SIZE_INCREMENT = 50;

        private int last = -1;
        private Entry[] entries = new Entry[SIZE_INCREMENT];

        void dispose() {
            entries = new Entry[SIZE_INCREMENT];
            last = -1;
        }

        Entry intern(CharSequence seq) {
            if (seq instanceof Entry) {
                return (Entry) seq;
            }
            // We are using an array and binary search to conserve memory
            // here.  This is slower than a HashMap (we sort on insert so
            // we can binary search later), but involves far fewer allocations
            Entry entry = new Entry(toBytes(seq), (short) seq.length());
            int offset = last == -1 ? -1 : Arrays.binarySearch(entries, 0, last + 1, entry);
            if (offset > 0) {
                return entries[offset];
            }
            if (last == entries.length - 1) {
                Entry[] nue = new Entry[entries.length + SIZE_INCREMENT];
                System.arraycopy(entries, 0, nue, 0, entries.length);
                entries = nue;
            }
            entries[++last] = entry;
            Arrays.sort(entries, 0, last + 1);
            return entry;
        }

        private static final class Entry implements Comparable<CharSequence>, CharSequence {

            private final byte[] bytes;
            private final short length;

            public Entry(byte[] bytes, short length) {
                if (length < 0) {
                    throw new Error("String too large");
                }
                this.bytes = bytes;
                this.length = length;
            }

            public int hashCode() {
                int h = 0;
                if (h == 0 && bytes.length > 0) {
                    CharSequence val = toChars();
                    int max = val.length();
                    for (int i = 0; i < max; i++) {
                        h = 31 * h + val.charAt(i);
                    }
                }
                return h;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null) {
                    return false;
                } else if (o == this) {
                    return true;
                } else if (o instanceof Entry) {
                    Entry other = (Entry) o;
                    if (other.bytes.length < bytes.length) {
                        return false;
                    }
                    return Arrays.equals(bytes, other.bytes);
                } else if (o instanceof CharSequence) {
                    return charSequencesEqual(this, (CharSequence) o);
                } else {
                    return false;
                }
            }

            public int compare(Entry o) {
                if (o == this) {
                    return 0;
                }
                int max = Math.min(bytes.length, o.bytes.length);
                for (int i = 0; i < max; i++) {
                    if (bytes[i] > o.bytes[i]) {
                        return 1;
                    } else if (bytes[i] < o.bytes[i]) {
                        return -1;
                    }
                }
                if (bytes.length == o.bytes.length) {
                    return 0;
                } else if (bytes.length > o.bytes.length) {
                    return 1;
                } else {
                    return -1;
                }
            }

            public String toString() {
                return new String(bytes, UTF);
            }

            @Override
            public int length() {
                return length;
            }

            CharSequence toChars() {
                return toCharSequence(bytes);
            }

            @Override
            public char charAt(int index) {
                return toCharSequence(bytes).charAt(index);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return toCharSequence(bytes).subSequence(start, end);
            }

            @Override
            public int compareTo(CharSequence o) {
                if (o instanceof Entry) {
                    return compare((Entry) o);
                }
                return toString().compareTo(o.toString());
            }
        }
    }

    class Concatenation implements CharSequence {

        private final InternTable.Entry[] entries;

        Concatenation(CharSequence... entries) {
            this.entries = new InternTable.Entry[entries.length];
            for (int i = 0; i < entries.length; i++) {
                this.entries[i] = INTERN_TABLE.intern(entries[i]);
            }
        }

        @Override
        public int length() {
            int result = 0;
            for (int i = 0; i < entries.length; i++) {
                result += entries[i].length;
            }
            return result;
        }

        @Override
        public char charAt(int index) {
            int offset = 0;
            for (int i = 0; i < entries.length; i++) {
                if (index < offset + entries[i].length) {
                    offset += entries[i].length;
                    continue;
                }
                return entries[i].charAt(index - offset);
            }
            throw new IndexOutOfBoundsException(index + " of " + length() + " in " + this);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return INTERN_TABLE.intern(toString().subSequence(start, end));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (InternTable.Entry e : entries) {
                sb.append(e);
            }
            return sb.toString();
        }

        private int hash = 0;

        public int hashCode() {
            int h = hash;
            if (h == 0 && length() > 0) {
                for (InternTable.Entry e : entries) {
                    CharSequence chars = e.toChars();
                    int max = chars.length();
                    for (int i = 0; i < max; i++) {
                        h = 31 * h + chars.charAt(i);
                    }
                }
                hash = h;
            }
            return h;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o == null) {
                return false;
            } else if (o instanceof Concatenation) {
                Concatenation c = (Concatenation) o;
                if (c.entries.length != entries.length) {
                    return false;
                }
                for (int i = 0; i < entries.length; i++) {
                    if (!entries[i].equals(c.entries[i])) {
                        return false;
                    }
                }
                return true;
            } else if (o instanceof CharSequence) {
                return charSequencesEqual(this, (CharSequence) o);
            } else {
                return false;
            }
        }
    }

    private static boolean charSequencesEqual(CharSequence a, CharSequence b) {
        int maxA = a.length();
        int maxB = b.length();
        if (maxA != maxB) {
            return false;
        }
        for (int i = 0; i < maxA; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
