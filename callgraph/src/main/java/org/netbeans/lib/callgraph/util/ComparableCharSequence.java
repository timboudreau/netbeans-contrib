package org.netbeans.lib.callgraph.util;

/**
 *
 * @author Tim Boudreau
 */
public interface ComparableCharSequence extends CharSequence, Comparable<CharSequence> {

    @Override
    public default int compareTo(CharSequence o) {
        return EightBitStrings.compareCharSequences(this, o);
    }
    
    public default boolean startsWith(CharSequence seq) {
        int myLen = length();
        int seqLength = seq.length();
        if (seqLength > myLen) {
            return false;
        }
        for (int i = seqLength-1; i >= 0; i--) {
            if (charAt(i) != seq.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public default int indexOf(char c) {
        int len = length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                if (c == charAt(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public default int lastIndexOf(char c) {
        int len = length();
        if (len > 0) {
            for (int i = len - 1; i >= 0; i--) {
                if (c == charAt(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    static final ComparableCharSequence EMPTY = new ComparableCharSequence() {
        @Override
        public int compareTo(CharSequence o) {
            return o.length() == 0 ? 0 : -1;
        }

        @Override
        public int indexOf(char c) {
            return -1;
        }

        @Override
        public int lastIndexOf(char c) {
            return -1;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            throw new ArrayIndexOutOfBoundsException("0 length string but"
                    + " requested char " + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            if (start == 0 && end == 0) {
                return this;
            }
            throw new ArrayIndexOutOfBoundsException("0 length string but"
                    + " requested substring " + start + " -> " + end);

        }

        public boolean equals(Object o) {
            return o instanceof CharSequence && ((CharSequence) o).length() == 0;
        }

        public int hashCode() {
            return 0;
        }
        
        public String toString() {
            return "";
        }
    };
}
