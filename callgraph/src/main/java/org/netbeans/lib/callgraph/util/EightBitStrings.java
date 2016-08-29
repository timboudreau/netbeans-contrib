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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

    public ComparableCharSequence create(CharSequence string) {
        if (disabled) {
            return string instanceof ComparableCharSequence ? (ComparableCharSequence) string
                    : new StringWrapper(string.toString());
        }
        return INTERN_TABLE.intern(string);
    }

    public ComparableCharSequence concat(CharSequence... seqs) {
        if (seqs.length == 1 && seqs[0] instanceof ComparableCharSequence) {
            return (ComparableCharSequence) seqs[0];
        }
        if (disabled) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence c : seqs) {
                sb.append(c);
            }
            return new StringWrapper(sb.toString());
        }
        return new Concatenation(seqs);
    }

    public ComparableCharSequence concatQuoted(Collection<CharSequence> seqs) {
        if (disabled) {
            StringBuilder sb = new StringBuilder("\"");
            for (Iterator<CharSequence> it = seqs.iterator(); it.hasNext();) {
                CharSequence c = it.next();
                sb.append(c);
                if (it.hasNext()) {
                    sb.append("\" \"");
                } else {
                    sb.append("\"");
                }
            }
            return new StringWrapper(sb.toString());
        } else {
            List<CharSequence> quoted = new ArrayList<>((seqs.size() * 2) + 1);
            quoted.add(this.QUOTE);
            for (Iterator<CharSequence> it = seqs.iterator(); it.hasNext();) {
                quoted.add(it.next());
                if (it.hasNext()) {
                    quoted.add(CLOSE_OPEN_QUOTE);
                } else {
                    quoted.add(QUOTE);
                }
            }
            return new Concatenation(quoted.toArray(new CharSequence[quoted.size()]));
        }
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

        private static final class Entry implements ComparableCharSequence {

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

            @Override
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
                return compareCharSequences(this, o);
            }
        }
    }

    static int compareCharSequences(CharSequence a, CharSequence b) {
        if (a == b) {
            return 0;
        }
        int aLength = a.length();
        int bLength = b.length();
        int max = Math.min(aLength, bLength);
        for (int i = 0; i < max; i++) {
            if (a.charAt(i) > b.charAt(i)) {
                return 1;
            } else if (a.charAt(i) < b.charAt(i)) {
                return -1;
            }
        }
        if (aLength == bLength) {
            return 0;
        } else if (aLength > bLength) {
            return 1;
        } else {
            return -1;
        }
    }

    static boolean debug;
    class Concatenation implements ComparableCharSequence, Comparable<CharSequence> {

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
            if (entries.length == 0) {
                throw new IndexOutOfBoundsException("0 length but asked for " + index);
            }
            for (int i = 0; i < entries.length; i++) {
                InternTable.Entry e = entries[i];
                if (index >= e.length) {
                    index -= e.length;
                } else {
                    return e.charAt(index);
                }
            }
            throw new IndexOutOfBoundsException(index + " of " + length() + " in " + this + " with entries " + Arrays.asList(entries));
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
            if (debug) {
                sb.append (" - " + Arrays.asList(entries));
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
            if (true) {
                if (o instanceof CharSequence) {
                    return charSequencesEqual(this, (CharSequence) o);
                }
            }
            if (o == this) {
                return true;
            } else if (o == null) {
                return false;
            } else if (o instanceof Concatenation) {
                Concatenation c = (Concatenation) o;
                if (c.entries.length != entries.length) {
                    return charSequencesEqual(this, c);
                }
                for (int i = 0; i < entries.length; i++) {
                    if (entries[i].length() != entries[i].length) {
                        return charSequencesEqual(this, c);
                    }
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

        @Override
        public int compareTo(CharSequence o) {
            return compareCharSequences(this, o);
        }
    }

    private static boolean charSequencesEqual(CharSequence a, CharSequence b) {
        int maxA = a.length();
        int maxB = b.length();
        if (maxA != maxB) {
            return false;
        }
        for (int i = 0; i < maxA; i++) {
            char aa = a.charAt(i);
            char bb = b.charAt(i);
            if (aa != bb) {
                return false;
            }
        }
        return true;
    }

    static class StringWrapper implements ComparableCharSequence {

        private final String s;

        public StringWrapper(String s) {
            this.s = s;
        }

        public int length() {
            return s.length();
        }

        public boolean isEmpty() {
            return s.isEmpty();
        }

        public char charAt(int index) {
            return s.charAt(index);
        }

        public int codePointAt(int index) {
            return s.codePointAt(index);
        }

        public int codePointBefore(int index) {
            return s.codePointBefore(index);
        }

        public int codePointCount(int beginIndex, int endIndex) {
            return s.codePointCount(beginIndex, endIndex);
        }

        public int offsetByCodePoints(int index, int codePointOffset) {
            return s.offsetByCodePoints(index, codePointOffset);
        }

        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            s.getChars(srcBegin, srcEnd, dst, dstBegin);
        }

        public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
            s.getBytes(srcBegin, srcEnd, dst, dstBegin);
        }

        public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
            return s.getBytes(charsetName);
        }

        public byte[] getBytes(Charset charset) {
            return s.getBytes(charset);
        }

        public byte[] getBytes() {
            return s.getBytes();
        }

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (anObject == null) {
                return false;
            }
            if (!(anObject instanceof CharSequence)) {
                return false;
            }
            return charSequencesEqual(this, (CharSequence) anObject);
        }

        public boolean contentEquals(StringBuffer sb) {
            return s.contentEquals(sb);
        }

        public boolean contentEquals(CharSequence cs) {
            return s.contentEquals(cs);
        }

        public boolean equalsIgnoreCase(String anotherString) {
            return s.equalsIgnoreCase(anotherString);
        }

        public int compareTo(CharSequence anotherString) {
            return compareCharSequences(this, anotherString);
        }

        public int compareToIgnoreCase(String str) {
            return s.compareToIgnoreCase(str);
        }

        public boolean regionMatches(int toffset, String other, int ooffset, int len) {
            return s.regionMatches(toffset, other, ooffset, len);
        }

        public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
            return s.regionMatches(ignoreCase, toffset, other, ooffset, len);
        }

        public boolean startsWith(String prefix, int toffset) {
            return s.startsWith(prefix, toffset);
        }

        public boolean startsWith(String prefix) {
            return s.startsWith(prefix);
        }

        public boolean endsWith(String suffix) {
            return s.endsWith(suffix);
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }

        public int indexOf(int ch) {
            return s.indexOf(ch);
        }

        public int indexOf(int ch, int fromIndex) {
            return s.indexOf(ch, fromIndex);
        }

        public int lastIndexOf(int ch) {
            return s.lastIndexOf(ch);
        }

        public int lastIndexOf(int ch, int fromIndex) {
            return s.lastIndexOf(ch, fromIndex);
        }

        public int indexOf(String str) {
            return s.indexOf(str);
        }

        public int indexOf(String str, int fromIndex) {
            return s.indexOf(str, fromIndex);
        }

        public int lastIndexOf(String str) {
            return s.lastIndexOf(str);
        }

        public int lastIndexOf(String str, int fromIndex) {
            return s.lastIndexOf(str, fromIndex);
        }

        public String substring(int beginIndex) {
            return s.substring(beginIndex);
        }

        public String substring(int beginIndex, int endIndex) {
            return s.substring(beginIndex, endIndex);
        }

        public CharSequence subSequence(int beginIndex, int endIndex) {
            return s.subSequence(beginIndex, endIndex);
        }

        public String concat(String str) {
            return s.concat(str);
        }

        public String replace(char oldChar, char newChar) {
            return s.replace(oldChar, newChar);
        }

        public boolean matches(String regex) {
            return s.matches(regex);
        }

        public boolean contains(CharSequence s) {
            return this.s.contains(s);
        }

        public String replaceFirst(String regex, String replacement) {
            return s.replaceFirst(regex, replacement);
        }

        public String replaceAll(String regex, String replacement) {
            return s.replaceAll(regex, replacement);
        }

        public String replace(CharSequence target, CharSequence replacement) {
            return s.replace(target, replacement);
        }

        public String[] split(String regex, int limit) {
            return s.split(regex, limit);
        }

        public String[] split(String regex) {
            return s.split(regex);
        }

        public static String join(CharSequence delimiter, CharSequence... elements) {
            return String.join(delimiter, elements);
        }

        public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
            return String.join(delimiter, elements);
        }

        public String toLowerCase(Locale locale) {
            return s.toLowerCase(locale);
        }

        public String toLowerCase() {
            return s.toLowerCase();
        }

        public String toUpperCase(Locale locale) {
            return s.toUpperCase(locale);
        }

        public String toUpperCase() {
            return s.toUpperCase();
        }

        public String trim() {
            return s.trim();
        }

        public String toString() {
            return s.toString();
        }

        public char[] toCharArray() {
            return s.toCharArray();
        }

        public static String format(String format, Object... args) {
            return String.format(format, args);
        }

        public static String format(Locale l, String format, Object... args) {
            return String.format(l, format, args);
        }

        public static String valueOf(Object obj) {
            return String.valueOf(obj);
        }

        public static String valueOf(char[] data) {
            return String.valueOf(data);
        }

        public static String valueOf(char[] data, int offset, int count) {
            return String.valueOf(data, offset, count);
        }

        public static String copyValueOf(char[] data, int offset, int count) {
            return String.copyValueOf(data, offset, count);
        }

        public static String copyValueOf(char[] data) {
            return String.copyValueOf(data);
        }

        public static String valueOf(boolean b) {
            return String.valueOf(b);
        }

        public static String valueOf(char c) {
            return String.valueOf(c);
        }

        public static String valueOf(int i) {
            return String.valueOf(i);
        }

        public static String valueOf(long l) {
            return String.valueOf(l);
        }

        public static String valueOf(float f) {
            return String.valueOf(f);
        }

        public static String valueOf(double d) {
            return String.valueOf(d);
        }

        public String intern() {
            return s.intern();
        }

    }
}
