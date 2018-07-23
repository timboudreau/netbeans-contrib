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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import junit.framework.*;

/**
 *
 * @author tim
 */
public class TrieImplTest extends TestCase {

    public TrieImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of depthHash method, of class trie.TrieImpl.
     */
    public void testDepthHash() {
        System.out.println("depthHash");
        
        CharSequence[] cs = new CharSequence[] {
            "a", "b", "aa", "ab", "ac", "ad", "bd", "ba",
                    "bb", "bbb", "bad", "aaa",
                    "bbb", "bbc", "bbd", "bba",
                    "aaaa", "aaaaa", "z", "zz",
                    "zzza", "zzzb", "zzzc", "zzzz"
        };
        
        int[] results = new int[cs.length];
        E[] e = new E[results.length];
        
        for (int i=0; i < cs.length; i++) {
            results[i] = CharUtil.depthHashBytes (cs[i], 0);
            e[i] = new E(results[i], cs[i]);
        }
        
        Arrays.sort (e);
        for (int i=0; i < e.length; i++) {
//            System.out.println(e[i]);
            assertTrue ((CharUtil.depthHashBytes(e[i].cs, e[i].cs.length()) | CharUtil.depthHashBytes(new String(new char[] { e[i].cs.charAt(0) }), 0)) == CharUtil.depthHashBytes(new String(new char[] { e[i].cs.charAt(0) }), 0));
            String s = CharUtil.byteHashToString(CharUtil.depthHashBytes(e[i].cs, 0));
//            System.err.println(e[i].cs + "->" + s);
        }
    }
    
    public void testLength() {
        CharSequence[] cs = new CharSequence[] {
            "a", "b", "aa", "ab", "ac", "ad", "bd", "ba",
                    "bb", "bbb", "bad", "aaa",
                    "bbb", "bbc", "bbd", "bba",
                    "aaaa", "z", "zz",
                    "zzza", "zzzb", "zzzc", "zzzz"
        };        
        for (int i=0; i < cs.length; i++) {
            assertEquals (cs[i].length(), CharUtil.length(CharUtil.depthHashBytes(cs[i],0)));
        }
    }
    

    
    public void testMatch() {
        String abcd = "abcd";
        String abcf = "abcf";
        String abbd = "abbd";
        String aacd = "aacd";
        
        int abcdHash = CharUtil.depthHashBytes(abcd, 0);
        
        assertEquals (3, CharUtil.countConsecutiveMatchingChars(abcdHash, 
                CharUtil.depthHashBytes(abcf, 0)));
        assertEquals (2, CharUtil.countConsecutiveMatchingChars(abcdHash, 
                CharUtil.depthHashBytes(abbd, 0)));
        assertEquals (1, CharUtil.countConsecutiveMatchingChars(abcdHash, 
                CharUtil.depthHashBytes(aacd, 0)));
        
        assertEquals (0, CharUtil.countConsecutiveMatchingChars(abcdHash, 
                CharUtil.depthHashBytes("zbcd", 0)));
        
        assertEquals (4, CharUtil.countConsecutiveMatchingChars(abcdHash, abcdHash)); //sanity check
        
        
        assertTrue (CharUtil.match(abcdHash, CharUtil.depthHashBytes("a",0)));
        assertTrue (CharUtil.match(abcdHash, CharUtil.depthHashBytes("ab",0)));
        assertTrue (CharUtil.match(abcdHash, CharUtil.depthHashBytes("abc",0)));
        assertTrue (CharUtil.match(abcdHash, CharUtil.depthHashBytes("abcd",0)));
        assertFalse (CharUtil.match(CharUtil.depthHashBytes("abbd",0), abcdHash));
        assertFalse (CharUtil.match(CharUtil.depthHashBytes("zbcd",0), abcdHash));
    }
    
    
    public void testPartialMatch() {
        String abcd = "abcd";
        String abcf = "abcf";
        String abbd = "abbd";
        String aacd = "aacd";
        
        int abcdHash = CharUtil.depthHashBytes(abcd, 0);
//        assertTrue (CharUtil.partialMatch(dh("ab"), abcdHash));
//        assertFalse (CharUtil.partialMatch(dh("abcd"), abcdHash));
        
    }
    
    private static int dh (String s) {
        return CharUtil.depthHashBytes(s, 0);
    }
    
    public void testCharAt() {
        String s = "abcd";
        int hash = CharUtil.depthHashBytes(s, 0);
        for (int i=0; i < s.length(); i++) {
            assertEquals("Wrong char at " + i + " should be '" + s.charAt(i) + 
                "' not '" + ((char) CharUtil.charAt(i, hash)) + "'", 
                CharUtil.charAt(i, hash), (byte) s.charAt(i));
        }
    }
    
    public void testTrie() {
        String[] cs = new String[] {
            "A big dog",
            "A bigger dog",
                    "A large dog",
                    "A funky dog",
                    "Some trees",
                    "Some trees are green",
                    "Some trees have teeth",
                    "A funky tree has teeth",
                    "My monkey can fly",
                    "A big wall of fudge",
                    "A bigger wall of fudge",
                    "Some big dog"
        };
        Arrays.sort (cs);
        
        assertTrue (CharUtil.match(dh("A funky dog"), dh("A funk")));
//        assertTrue (CharUtil.match(dh("A funk"), dh("A funky dog")));

        String[] tests = new String[] {
                    "A la",
                    "A funk",
                            "A big",
                            "A",
                            "Some",
                            "Some t",
                            "Some b",
                            "My",
                            "My m",
                            "A bigg"
                            
        };
        for (int i=0; i < tests.length; i++) {
            assertCorrectMatching (cs, tests[i], i);
        }
        assertCorrectMatching (cs, "A big", 0);
    }
    
    public void testStress() {
        String[] cs = new String[] {
            "A big dog",
            "A bigger dog",
                    "A large dog",
                    "A funky dog",
                    "Some trees",
                    "Some trees are green",
                    "Some trees have teeth",
                    "A funky tree has teeth",
                    "My monkey can fly",
                    "A big wall of fudge",
                    "A bigger wall of fudge",
                    "Some big dog"
        };
        
        Random r = new Random (System.currentTimeMillis());
        String[] strings = new String[40000];
        
        for (int i=0; i < strings.length; i++) {
            strings[i] = genRandomString (cs, r);
//            System.err.println(strings[i]);
        }

        Set s = new HashSet (Arrays.asList(strings));
        strings = (String[]) s.toArray(new String[s.size()]);
//        System.err.println("STRINGS SIZE " + strings.length);
        
        Arrays.sort (strings);
        for (int i=0; i < cs.length; i++) {
            try {
//                assertCorrectMatching (strings, cs[i], i);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                throw new NullPointerException ("NPE on " + i + " - " + cs[i]);
            }
        }
        
        for (int q=0; q < 20; q++) {
            long tm = System.currentTimeMillis();
            for (int i=0; i < cs.length; i++) {
                bruteForceFindCompletions (strings, cs[i]);
            }
            long elapsedBrute = System.currentTimeMillis() - tm;

            Trie trie = new ByteTrieImpl (strings);

            tm = System.currentTimeMillis();
            for (int i=0; i < cs.length; i++) {
                trie.match(cs[i]);
            }
            long elapsedTrie = System.currentTimeMillis() - tm;

            System.err.println(q + ": Brute Elapsed: " + elapsedBrute);
            System.err.println(q + ": Trie Elapsed : " + elapsedTrie);
        }
    }
    
    private String genRandomString (String[] roots, Random r) {
        boolean useRoot = r.nextBoolean();
        StringBuffer sb = new StringBuffer();
        if (useRoot) {
            String use = roots[r.nextInt(roots.length)];
            use = use.substring(0, r.nextInt(use.length()));
            sb.append (use);
        }
        int ct = r.nextInt(50);
        //Append random characters
        for (int i=0; i < ct; i++) {
            char c = (char) (r.nextInt('z' - 'A') + 'A');
            if (c != '\\') {
                sb.append (c);
            }
        }
        return sb.toString();
    }
    
    public void testPermutations() {
        System.out.println("testPermutations");
        String[] cs = new String[] {
            "A big dog",
            "A bigger dog",
                    "A large dog",
                    "A funky dog",
                    "Some trees",
                    "Some trees are green",
                    "Some trees have teeth",
                    "A funky tree has teeth",
                    "A big wall of funky dogs",
                    "My monkey can fly",
                    "A big wall of fudge",
                    "A bigger wall of fudge",
                    "Some big dog"
        };    
        Arrays.sort(cs);
        Trie trie = new ByteTrieImpl (cs);
        for (int i=0; i < cs.length; i++) {
            String s = cs[i];
            StringBuffer sb = new StringBuffer (s.length());
            for (int j = 0; j < s.length(); j++) {
                sb.append (s.charAt(j));
                CharSequence[] res = trie.match(sb.toString());
                String[] got = strings(res);
                String[] expect = bruteForceFindCompletions (cs, sb.toString());
                assertTrue ("Search for '"+ sb + "'\n Expected " + Arrays.asList(expect) + "\n GOT      " + Arrays.asList(got) + "\nTRIE:\n" + trie, Arrays.equals(got, expect));
            }
        }
        
        StringBuffer sb = new StringBuffer ("Some trees");
        CharSequence[] res = trie.match(sb.toString());
        String[] got = strings(res);
        String[] expect = bruteForceFindCompletions (cs, sb.toString());
        assertTrue ("Search for '"+ sb + "'\n Expected " + Arrays.asList(expect) + "\n GOT      " + Arrays.asList(got) + "\nTRIE:\n" + trie, Arrays.equals(got, expect));
        
    }
    
    
    public void testKnownProblematic() {
        System.out.println("testKnownProblematic");
        String[] knownBadDataSet = new String[] {
            "Some trees aq",
            "Some trees arLSLS]BUP_vU_BP",
            "Some trees are ",
            "Some trees are greeAAubvgFF[vQ`]woYeyDXnGexgjsTj",
            "Some trees areiWo^AbQElHV_]BqrjPdZQmbh\\MkZ\\MTTX`I_[YpxRRlY",
            "Some trees haqu_ODTgvsZPRCPnaQnIyKsu\\b^",
            "Some trees harhjHmlu\\hkh[_OuiXqxldNcPGF\\BieXxTWyBXlq",
            "Some trees hyBV^NpBgAZqK_",
            "Some treesHHK`mr[vxc_aZtg]F\\hCP\\VrEUv[\\pc",
            "Some treesKaTsbmWGBe]aJH",
        };

        Arrays.sort (knownBadDataSet);
        assertCorrectMatching (knownBadDataSet, "Some trees", 0);
    }
    

    
    public void testKnownProblematic2() {
        System.out.println("testKnownProblematic2");
        String[] knownBadDataSet = new String[] {
            "Some trees ",
            "Some trees are",
            "Some trees aglobbop"
        };
        
        assertCorrectMatching (knownBadDataSet, "Some trees", 0);
    }
    
    public void testMultiIntCharSequence() {
        System.out.println("testMultiIntCharSequence");
        String[] ss = new String[] {
            "testMultiIntCharSequence",
            "a",
            "ab",
            "abc",
            "abcd",
            "abcde",
            "abcdef",
            "abcdefg",
            "abcdefgh",
            "abcdefghi"
        };
        
        int[] els = CharUtil.depthHashesBytes("a");
        assertEquals (1, els.length);
        int theValue = els[0];
        String ret = CharUtil.byteHashToString(theValue);
        assertEquals ("Length is wrong ", 1, ret.length());
        assertEquals ("Reencoding 'a' gets '" + ret + "' not a","a", ret);
        
        
        els = CharUtil.depthHashesBytes("Why not test this string?");
        assertEquals ("Why not test this string?", CharUtil.byteHashToString(els));
        
        
        for (int j=0; j < ss.length; j++) {
           String s = ss[j];
            int[] ints = CharUtil.depthHashesBytes(s);
            ByteTrieImpl.MultiIntCharSequence cs = new ByteTrieImpl.MultiIntCharSequence(ints, false);

            assertEquals (s.length(), cs.length());
            assertEquals (s, cs.toString());
            assertEquals (s.hashCode(), cs.hashCode());
//            assertEquals (s, cs);

            for (int i=0; i < s.length(); i++) {
                assertEquals (s.charAt(i), cs.charAt(i));
            }
        }
    }
    
    public void testRemove() {
        System.out.println ("testRemove");    
        CharSequence[] cs = new CharSequence[] {
            "A big dog",
            "A bigger dog",
            "A big",
                    "abcd",
                    "efgh",
                    "efghijk",
                    "efghijkl",
//                    "efghijklm",
            "The song",
            "A monkey",
            "A money",
            "Seven snorkels",
            "Seven snorkels swim",
            "Seven snorkels swim the seashore",
        };     
        Arrays.sort (cs);
        ByteTrieImpl[] tries = new ByteTrieImpl[cs.length];
        for (int i=0; i < tries.length; i++) {
            tries[i] = new ByteTrieImpl(cs);
        }
        for (int i=0; i < cs.length; i++) {
            assertTrue ("Before removing " + cs[i] + " trie should contain it", tries[i].contains(cs[i]));
            System.err.println("TRIE BEFORE REMOVE " + cs[i] + "\n" + tries[i]);
            tries[i].remove (cs[i]);
            System.err.println("TRIE AFTER REMOVE " + cs[i] + "\n" + tries[i]);
            if (tries[i].match(cs[i]).length == 0) {
//                assertFalse ("After removing '" + cs[i] + "' trie should not contain " + cs[i], tries[i].contains(cs[i]));
            }
            for (int j=0; j < cs.length; j++) {
                if (cs[j] != cs[i]) {
                    assertTrue ("After removing '" + cs[i] + "' trie should still contain '" + cs[j] +"'", tries[i].contains(cs[j]));
                }
            }
        }
    }    
    
    /*
     Commented out until I figure a good way to slove it
    public void testRemoveOnBoundary() {
        System.err.println("TestRemoveOnBoundary");
        String[] cs = new String[] {
                    "abcd",
                    "efgh",
                    "efghijk",
                    "efghijkl",
                    "efghijklm", //This is the problem child - efghijkl sits perfectly at a boundary,
                                 //but nothing marks the fact that its map represents both it and
                                 //its contents, 'm', so when we remove m, it becomes empty and
                                 //we remove it too.  Need either a dummy map for things like this,
                                 //or some way of marking an explicit entry.
        };
        Arrays.sort (cs);
        TrieImpl[] tries = new TrieImpl[cs.length];
        for (int i=0; i < tries.length; i++) {
            tries[i] = new TrieImpl(cs);
        }
        for (int i=0; i < cs.length; i++) {
            assertTrue ("Before removing " + cs[i] + " trie should contain it", tries[i].contains(cs[i]));
            System.err.println("TRIE BEFORE REMOVE " + cs[i] + "\n" + tries[i]);
            tries[i].remove (cs[i]);
            System.err.println("TRIE AFTER REMOVE " + cs[i] + "\n" + tries[i]);
            if (tries[i].match(cs[i]).length == 0) {
//                assertFalse ("After removing '" + cs[i] + "' trie should not contain " + cs[i], tries[i].contains(cs[i]));
            }
            for (int j=0; j < cs.length; j++) {
                if (cs[j] != cs[i]) {
                    assertTrue ("After removing '" + cs[i] + "' trie should still contain '" + cs[j] +"'", tries[i].contains(cs[j]));
                }
            }
        }
    }
     */
    
    public void testContains() {
        System.out.println("testContains");
        CharSequence[] cs = new CharSequence[] {
            "A big dog",
            "A bigger dog",
            "A big",
                    "abcd",
                    "efgh",
                    "efghijk",
                    "efghijkl",
            "The song",
            "A monkey",
            "A money",
            "Seven snorkels",
            "Seven snorkels swim",
            "Seven snorkels swim the seashore",
        };   
        Arrays.sort (cs);
        ByteTrieImpl trie = new ByteTrieImpl(cs);
        for (int i=0; i < cs.length; i++) {
            assertTrue ("Trie contains " + cs[i] + " but contains() returns false", trie.contains(cs[i]));
//            assertFalse ("Trie should not say it contains a subsequence ", trie.contains(cs[i].subSequence(0, cs[i].length()-2)));
        }
        
        assertFalse (trie.contains("A monkey is large"));
        assertFalse (trie.contains ("Seven big thingies"));
    }        
    
    private void assertCorrectMatching (String[] strings, String search, int ix) {
        String[] exp = bruteForceFindCompletions(strings, search);
        List expected = Arrays.asList(exp);
        String[] gt = strings(new ByteTrieImpl(strings).match(search));
        List got = Arrays.asList(gt);
//        System.err.println("E len " + expected.size());
//        System.err.println("G len " + got.size());
        
        boolean good = Arrays.equals(exp, gt);
        
//        if (exp.length == gt.length && !good) {
//            for (int i=0; i < exp.length; i++) {
//                System.err.println(i + " exp " + exp[i].getClass().getName() + ":" + exp[i]);
//                System.err.println(i + " gt  " + gt[i].getClass().getName() + ":" + gt[i]);
//                System.err.println(" EQUAL? " + exp[i].equals(gt[i]) + " <->" + gt[i].equals(exp[i]));
//                System.err.println("len gt " + gt[i].length() + " <-> exp " + exp[i].length());
//                for (int j=0; j < gt[i].length(); j++) {
//                    char ee = j < exp[i].length() ? exp[i].charAt(j) : '-';
//                    char gg = gt[i].charAt(j);
//                    System.err.println(i + " " + ee + ':' + gg);
////                    System.err.println(" " + j  + " " + ((byte) gt[i].charAt(j)));
//                }
//            }
//        } else if (exp.length != gt.length) {
//            System.err.println("LENGTHS DON'T MATCH: ' " + exp.length + "!=" + gt.length);
//            for (int i=0; i < Math.max(exp.length, gt.length); i++) {
//                String e = i < exp.length ? exp[i].intern() : null;
//                String g = i < gt.length ? gt[i].intern() : null;
//                
//                if (e != g)
//                    System.err.println(i + ": Expected '" + (e == null ? "" : e) + "' Got '" + (g == null ? "" : g)  +"'");
//            }
//            HashSet s1 = new HashSet (Arrays.asList(exp));
//            s1.removeAll (Arrays.asList(gt));
//            System.err.println("\nMISSING:\n  " + s1 + "\n");
//        }
        
        if (!good) {
            StringBuffer sb = new StringBuffer ("\nString[] knownBadDataSet = new String[] {");
            for (int i=0; i < exp.length; i++) {
                sb.append ('"' + exp[i] + '"' + '\n');
            }
            sb.append ("\n};\n");
            System.err.println(sb);
        }
        
        assertTrue ("Matching '" + search + "', expected " + expected + " got " + got + " iter " + ix, Arrays.equals(exp, gt));
    }
    
    private static String[] strings (CharSequence[] sq) {
        String[] result = new String[sq.length];
        for (int i=0; i < sq.length; i++) {
            result[i] = sq[i].toString().intern();
        }
        return result;
    }

    private static String[] last = null;
    private static List lastL = null;
    private String[] bruteForceFindCompletions(String[] search, String match) {
        List al;
        al = new ArrayList();
        for (int i=0; i < search.length; i++) {
            if (search[i].startsWith(match)) {
                al.add (search[i]);
            }
        }
        return ((String[]) al.toArray(new String[al.size()]));
    }
//    
//    private String[] reasonablyEfficentFindCompletions (String[] search, String[] match) {
//        
//    }
//    

    private static String string (CharSequence cs) {
        if (cs instanceof String) {
            return (String) cs;
        } else {
            char[] c = new char[cs.length()];
            for (int i=0; i < cs.length(); i++) {
                c[i] = cs.charAt(i);
            }
            return new String(c);
        }
    }
    
    private static class E implements Comparable {
        private final int hash;
        final CharSequence cs;
        public E (final int hash, final CharSequence cs) {
            this.hash = hash;
            this.cs = cs;
        }
        
        public String toString() {
            return string (cs) + ":" + Integer.toHexString(hash) + " : " + hash;
        }

        public int compareTo(Object o) {
            return hash - ((E) o).hash;
        }
    }
}
