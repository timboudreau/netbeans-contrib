package org.netbeans.lib.callgraph.util;

import org.netbeans.lib.callgraph.util.EightBitStrings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tim
 */
public class SmallStringTest {

    @Test
    public void testCreate() {
        EightBitStrings strings = new EightBitStrings(false);
        CharSequence testOne = strings.create("A first test one");
        CharSequence testTwo = strings.create("A second test one");
        assertNotEquals(testOne, testTwo);

        CharSequence hello = strings.create("Hello world");
        CharSequence hello2 = strings.create("Hello world");
        assertEquals(hello, hello2);
        assertSame(hello, hello2);
        assertEquals(hello.hashCode(), "Hello world".hashCode());
        assertEquals("Hello world", hello.toString());
        assertEquals("Hello world".length(), hello.length());

        CharSequence worlds = strings.create("Hello worlds");
        assertNotEquals(hello2, worlds);

        assertEquals(hello, "Hello world");
//        assertEquals("Hello world", hello);
    }

    @Test
    public void testInterning() {
        EightBitStrings strings = new EightBitStrings(false);
        List<String> all = new ArrayList<>(500);
        List<CharSequence> seqs = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            String s = randomString(5 + r.nextInt(20));
            all.add(s);
            seqs.add(strings.create(s));
        }
        int size = strings.internTableSize();
        Set<CharSequence> xseqs = new HashSet<>(seqs);
        int oldSize = xseqs.size();
        for (String again : all) {
            CharSequence ss = strings.create(again);
            xseqs.add(ss);
            assertEquals(size, strings.internTableSize());
            assertEquals(oldSize, xseqs.size());
        }
    }

    @Test
    public void testConcatenations() {
        EightBitStrings strings = new EightBitStrings(false);
        CharSequence concat = strings.concat("Hello ", "there ", "how ", "are ", "you?");
        CharSequence concat2 = strings.concat("Hello ", "there ", "how ", "are ", "you?");
        assertEquals("Hello there how are you?", concat.toString());
        assertEquals("Hello there how are you?".hashCode(), concat.toString().hashCode());
        assertEquals(concat, concat2);

    }

    private static String randomString(int len) {
        char[] c = new char[len];
        for (int i = 0; i < c.length; i++) {
            c[i] = randomChar();
        }
        return new String(c);
    }

    private static char randomChar() {
        return alpha[r.nextInt(alpha.length)];
    }

    static final Random r = new Random(320392);
    private static final char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();

}
