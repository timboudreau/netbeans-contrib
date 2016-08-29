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
}
