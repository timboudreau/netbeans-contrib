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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.collections.numeric.IntMap;
import org.netbeans.collections.numeric.IntMap.Iter;

/**
 * Non 16bit clean implementation of a Trie.  Implementation notes:
 * Each passed String/CharSequence is converted into an array of ints,
 * each representing four characters.  We use IntMap, from numeric collections,
 * which is a very fast int->object map (it prefers data to be presorted
 * but will lazily sort when needed).
 * <p>
 * Essentially we have a tree of IntMaps.  Each int (representing 4 chars)
 * becomes a key in a map.  So, if we put the string "abcdefghijkl", that
 * would map to three ints representing "abcd", "efgh", "ijkl".  We 
 * ask the root map if it has an IntMap for "abcd".  If not, we put one there.
 * Then we take that map, and (if we didn't just create it) ask it if it
 * has an IntMap for "efgh".  If not, we put one there.  Then with that map,
 * we ask it if it has a map for "ijkl"... you get the idea.
 * <p>
 * When we match a prefix string, we do the same decomposition into ints.
 * For each int except the last, we try to find a corresponding map.  If there
 * is an exact match, we continue with the next int.  With the last int, if
 * there is no exact match, and if there is, we will check if it is representing
 * less than 4 characters.  If so, we will ask the IntMap for the nearest key
 * to our last int, and see if its high bits match all of our it's set bits.
 * If so, then we will iterate until we run out of partial completions.
 * <p>
 * The upshot of this all is that, compared with a Collections based approach,
 * this class is very, very fast - if microbenchmarks can be believed, on the
 * order of 600 times faster for large data sets (~40000 items).  For smaller
 * data sets, the difference will be less pronounced.
 * <p>
 * One of the upshots of using IntMap is that, whenever you put() a new 
 * String, only those IntMaps that are receiving new data need to be sorted - 
 * so even though some sorting will be needed, for all strings that share 
 * four or more leading characters in common, the overhead will be reduced
 * somewhat.
 * <p>
 * BUT I CAN'T PUT ANYTHING BUT ASCII IN THIS!  WHAT GOOD IS IT?
 * Lots.  But, the plan of action is to have a 16 bit clean implementation
 * of Trie, and a wrapper Trie which will transparently switch to using it
 * the first time a > 8 bit string is added.  The 16 bit version won't be as
 * fast, but it should be a transparent (if somewhat expensive) switch over.
 *
 * @author Tim Boudreau
 */
public class ByteTrieImpl implements Trie {
    private final IntMap map = new IntMap();
    
    static boolean log = false; //XXX temp
    
    public ByteTrieImpl() {
    }
    
    public ByteTrieImpl(CharSequence[] initialData) {
        if (initialData == null) throw new NullPointerException ("Arg is null");
        for (int i=0; i < initialData.length; i++) {
            if (initialData[i] == null) {
                throw new NullPointerException ("Null at " + i);
            }
            add (initialData[i]);
        }
    }

    public CharSequence[] match(CharSequence seq) {
        final int[] data = CharUtil.depthHashesBytes(seq);
        final int[] base = new int[data.length];

        IntMap map = this.map;
        final List l = new LinkedList();
        for (int i=0; i < base.length; i++) {
            int currData = data[i];
            if (map == null) {
                break;
            }
            IntMap nextMap = (IntMap) map.get(data[i]);
            boolean perfectMatch = nextMap != null;
            
            if (perfectMatch) {
                base[i] = data[i];
                if (i == base.length - 1) {
                    getCompletions(nextMap, base, l);
                }
            } else { 
                int next = map.nearest(data[i], false);
                if (next == data[i]) {
                    //if (log) System.err.println("SKIP " + new IntCharSequence(data[i]));
                    next = map.nextEntry(next);
                }
                if (!CharUtil.match(next, data[i])) {
                    //if (log) System.err.println("FLIP " + new IntCharSequence(data[i]));
                    map = nextMap;
                    continue;
                }
             } 
                
            IntMap.Iter iter = map.nearestIter(data[i], false);//perfectMatch);

            //XXX the situation where we have a pure match and also append
            //matches is quite rare;  would be nice to not run the loop
            //below for most cases

            int hash;
            IntMap comps;
            while (iter.hasNext()) {
                comps = (IntMap) iter.current();
                hash = iter.next();
                if (hash == data[i]) {
                    continue;
                }
                boolean match = CharUtil.match(hash, data[i]);
                if (!match || comps == null) {
                    break;
                }
                
                if (match) {
                    base[i] = hash;
                    getCompletions (comps, base, l);
                } else {
                    break;
                }
            }
            map = nextMap;
        }
        return (CharSequence[]) l.toArray(new CharSequence[l.size()]);
    }
    
   private static final boolean getCompletions (final IntMap map, final int[] base, final List dest) {
        if (map.isEmpty()) {
            dest.add (new MultiIntCharSequence(base, true));
            return true;
        }
        Iter iter = map.iter();

        boolean added = false;
        int ix = 0;
        while (iter.hasNext()) {
            IntMap next = (IntMap) iter.current();
            int key = iter.next();
            if (next != null) {
                int[] newBase = new int[base.length + 1];
                System.arraycopy(base, 0, newBase, 0, base.length);
                newBase[base.length] = key;
                added |= getCompletions (next, newBase, dest);
                if (!added) {
                    MultiIntCharSequence comp = new MultiIntCharSequence (newBase, false);
                    dest.add (comp);
                    added = true;
                }
            }
        }
        return added;
    }    
    
    private static void outMap (IntMap map) {
        if (log) {
            System.err.println("*************");
            StringBuffer sb = new StringBuffer();
            outMap (map, 0, sb);
            System.err.println(sb);
            System.err.println("*************");
        }
    }
    
    private static void outMap (IntMap map, int depth, StringBuffer buf) {
        char[] x = new char[depth * 4];
        Arrays.fill (x, ' ');
        String ws = new String(x);
        if (map == null) {
            buf.append(ws + "[EMPTY]");
            return;
        }
        Iter iter = map.iter();
        while (iter.hasNext()) {
            IntMap m = (IntMap) iter.current();
            int key = iter.next();
            buf.append(ws + "'" + new IntCharSequence(key) + "'\n");
            outMap (m, depth+1, buf);
        }
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        outMap (map, 0, buf);
        return buf.toString();
    }
    

    public void add(CharSequence seq) {
        int[] data = CharUtil.depthHashesBytes(seq);
        IntMap currMap = map;
        for (int i=0; i < data.length; i++) {
            if (data[i] == 0) {
                break;
            }
            IntMap next = (IntMap) currMap.get(data[i]);
            if (next == null) {
                next = new IntMap();
                currMap.put (data[i], next);
            }
            currMap = next;
        }
    }
    
    public boolean removeAll (CharSequence seq) {
        return false; //XXX
    }
    
    private static void om (IntMap map) {
        if (map.isEmpty()) {
            System.err.println("  [EMPTY]");
        }
        for (IntMap.Iter iter = map.iter(); iter.hasNext();) {
            Object o = iter.current();
            int key = iter.next();
            System.err.println("  " + new IntCharSequence(key) + " = map of " + ((IntMap) o).size());
        }
    }
    
    public boolean remove(CharSequence seq) {
        System.err.println("REMOVE '" + seq + "'");
        int[] data = CharUtil.depthHashesBytes(seq);
        IntMap[] maps = new IntMap[data.length + 1];
        IntMap curr = map;
        maps[0] = map;
        boolean result = false;
        for (int i=0; i < data.length; i++) {
            if (curr == null) {
//                System.err.println("Break at " + i);
                break;
            }
            maps[i+1] = curr;
            curr = (IntMap) curr.get(data[i]);;
        }

        for (int i=maps.length-1; i >= 1; i--) {
            if (maps[i] == null) {
                continue;
            }
            
//            System.err.println(i + " Remove " + " '" + new IntCharSequence(data[i-1]) + "' from map size " + maps[i].size());
//            om (maps[i]);
            
            IntMap mp = (IntMap) maps[i].get(data[i-1]);
//            System.err.println("FOUND INSIDE:");
//            om (mp);
            
            if (mp.isEmpty()) {
                maps[i].remove (data[i-1]);
                result = true;
            }
        }

        return result;
    }

//    public boolean remove(CharSequence seq) {
//        int[] data = CharUtil.depthHashesBytes(seq);
//        IntMap[] maps = new IntMap[data.length + 1];
//        
//        maps[0] = map;
//        boolean result = false;
//        for (int i=0; i < data.length; i++) {
//            if (data[i] == 0 || maps[i] == null) {
//                break;
//            }
//            maps[i+1] = (IntMap) maps[i].get(data[i]);
//            if (maps[i] == null && i != data.length -1) {
//                //We don't have this string
//                return false;
//            }
//        }
//        int start = maps.length-1;
//        while (maps[start] == null) {
//            start--;
//        }
//        for (int i=maps.length-1; i >= 1; i--) {
//            if (maps[i] != null) {
//                if (maps[i].isEmpty()) {
//                    System.err.println("Removing " + data[i-1]);
//                    maps[i-1].remove (data[i-1]);
//                    result = true;
//                }
//            }
//        }
//        return result;
//    }
    
    public boolean contains (CharSequence seq) {
        int[] data = CharUtil.depthHashesBytes(seq);
        IntMap currMap = map;
        boolean result = false;
        for (int i=0; i < data.length; i++) {
            if (data[i] == 0) {
                break;
            }
            if (currMap == null) {
                return i==data.length-1;
            }
            IntMap next = (IntMap) currMap.get(data[i]);
            currMap = next;
            result = next != null;
        }
        return result;
    }
    
    static final class MultiIntCharSequence implements CharSequence {
        private final int[] values;
        MultiIntCharSequence (int[] values, boolean copy) {
            if (copy) {
                this.values = new int[values.length];
                System.arraycopy (values, 0, this.values, 0, values.length);
            } else {
                this.values = values;
            }
        }
        
        public int length() {
            return ((values.length - 1) * 4) + CharUtil.length(values[values.length-1]);
        }

        public char charAt(int index) {
            int pos = index / 4;
            int off = index % 4;
            return (char) CharUtil.charAt(off, values[pos]);
        }

        public CharSequence subSequence(int start, int end) {
            //Not currently used much;  could optimize...
            StringBuffer sb = new StringBuffer (end - start);
            for (int i=start; i < end; i++) {
                sb.append (charAt(i));
            }
            return sb.toString();
        }
        
        public boolean equals (Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof MultiIntCharSequence) {
                MultiIntCharSequence m = (MultiIntCharSequence) o;
                return Arrays.equals (values, m.values);
            } else if (o instanceof IntCharSequence) {
                return values.length == 1 && ((IntCharSequence) o).value == values[0];
            } else if (o instanceof CompositeCharSequence) {
                return o.toString().equals (toString());
            } else if (o instanceof CharSequence) {
                return o.toString().equals (toString());
            } else {
                return false;
            }
        }
        
        int hash = 0;
        public int hashCode() {
            if (hash == 0) {
                int h = hash;
                int len = length();
                //Same alg as java.lang.String
                for (int i = 0; i < len; i++) {
                    h = 31*h + charAt(i);
                }
                hash = h;
            }
            return hash;
        }
        
        public String toString() {
            return CharUtil.byteHashToString(values);
        }
    }
    
    private static final class IntCharSequence implements CharSequence {
        private final int value;
        IntCharSequence (int value) {
            this.value = value;
        }
        
        public int length() {
            return CharUtil.length(value);
        }

        public char charAt(int index) {
            return (char) CharUtil.charAt (index, value);
        }

        public CharSequence subSequence(int start, int end) {
            if (start == 0 && end == length()) {
                return this;
            } else {
                return new Sub (start, end);
            }
        }
        
        public int hashCode() {
            return value;
        }
        
        public String toString() {
            return CharUtil.byteHashToString(value);
        }
        
        public boolean equals(Object o) {
            if (o instanceof IntCharSequence) {
                return ((IntCharSequence) o).value == value;
            } else if (o instanceof CharSequence) {
                return ((CharSequence) o).toString().equals(toString());
            } else {
                return false;
            }
        }
        
        private final class Sub implements CharSequence {
            private final int start, end;
            public Sub (int start, int end) {
                this.start = start;
                this.end = end;
            }
            
            public IntCharSequence owner() {
                return IntCharSequence.this;
            }

            public int length() {
                return end - start;
            }

            public char charAt(int index) {
                assert index <= length();
                return IntCharSequence.this.charAt(start + index);
            }

            public CharSequence subSequence(int start, int end) {
                return IntCharSequence.this.subSequence (this.start + start, end);
            }
            
            public String toString() {
                char[] c = new char[length()];
                for (int i=0; i < c.length; i++) {
                    c[i] = charAt(i);
                }
                return new String(c);
            }
            
            public int hashCode() {
                int val = owner().value;
                val >>= 8 * start;
                return val;
            }
            
            public boolean equals (Object o) {
                if (o instanceof Sub) {
                    if (((Sub)o).owner() == owner() || ((Sub)o).owner().value == owner().value) {
                        return ((Sub) o).start == start && ((Sub) o).end == end;
                    } else {
                        return o.toString().equals(toString());
                    }
                } else if (o instanceof IntCharSequence) {
                    return o.hashCode() == hashCode();
                } else if (o instanceof CharSequence) {
                    return o.toString().equals(toString());
                } else {
                    return false;
                }
            }
        }
    }
    
    
    private static class CompositeCharSequence implements CharSequence {
        private final CharSequence[] seq;
        public CompositeCharSequence (CharSequence[] seq) {
            assert seq.length > 0;
            this.seq = seq;
            if (charAt(length() -1) == 0) {
                throw new IllegalStateException ("I AM BAD: " + outIt(seq));
            }
            if (toString().indexOf("funkdog") != -1) {
                throw new Error ("CULPRIT " + this);
            }
            for (int i=0; i < seq.length; i++) {
                if (seq[i] == null) {
                    throw new NullPointerException ("CULPRIT: " +i);
                }
            }
        }
        
        private String outIt (CharSequence[] seq) {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i < seq.length; i++) {
                sb.append ("[" + seq[i] + "-" + seq[i].getClass().getName() + "]");
                if (seq[i].charAt(seq[i].length()-1) == 0) {
                    sb.append (" !!CULPRIT:" + seq[i] + "!!  " + " length==" + seq[i].length());
                }
            }
            return sb.toString();
        }
        
        int len = -1;
        public int length() {
            if (len == -1) {
                len = 0;
                for (int i=0; i < seq.length; i++) {
                    if (seq[i] == null) break;
                    len += seq[i].length();
                }
            }
            return len;
        }

        public char charAt(final int index) {
            int l = 0;
            for (int i=0; i < seq.length; i++) {
                int currLen = seq[i].length();
                if (l + currLen > index) {
                    return seq[i].charAt(index - l);
                }
                l += currLen;
            }
            throw new ArrayIndexOutOfBoundsException (toString() + " index " + index);
        }

        public CharSequence subSequence(int start, int end) {
            //XXX special-case & optimize
            return toString().subSequence(start, end);
        }
        
        public String toString() {
            StringBuffer sba = new StringBuffer();
            for (int i=0; i < seq.length; i++) {
                sba.append (seq[i]);
            }
            return sba.toString();
        }
    }
    
  
}
