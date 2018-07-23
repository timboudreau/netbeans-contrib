/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.genericnavigator;

import java.io.File;
import java.io.FileOutputStream;
import junit.framework.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;

/**
 *
 * @author Tim Boudreau
 */
public class PatternItemTest extends TestCase {

    public PatternItemTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PatternItemTest.class);
        return suite;
    }

    LocalFileSystem lfs;
    PatternItem listitems;
    PatternItem headers;
    PatternItem flagsundefined;
    PatternItem misorderedgroups;
    protected void setUp() throws Exception {
        cleanUpAfterOldTests();
        createBaseData();
        lfs = new LocalFileSystem ();
        lfs.setRootDirectory(dataDir);
        PatternItem.rootfolder = lfs.getRoot();
        listitems = new PatternItem (DataObject.find (lfs.getRoot().getFileObject("text/html/listitems")));
        headers = new PatternItem (DataObject.find (lfs.getRoot().getFileObject("text/html/headers")));
        flagsundefined = new PatternItem (DataObject.find (lfs.getRoot().getFileObject("text/html/other")));
        misorderedgroups = new PatternItem (DataObject.find (lfs.getRoot().getFileObject("text/html/misorderedgroups")));
    }

    protected void tearDown() throws Exception {
        cleanUpAfterOldTests();
    }

    File dataDir = new File (new File(System.getProperty("java.io.tmpdir")), "patternItemTest");
    private void cleanUpAfterOldTests() {
        if (dataDir.exists()) {
            List <File> l = new ArrayList <File>();
            recurseDelete (dataDir,l);
            for (Iterator <File> i = l.iterator(); i.hasNext();) {
                File file = i.next();
                file.delete();
            }
        }
    }

    private void recurseDelete (File dataDir, List <File> l) {
        l.add (0, dataDir);
        if (dataDir.isDirectory()) {
            File[] kids = dataDir.listFiles();
            for (int i = 0; i < kids.length; i++) {
                File file = kids[i];
                recurseDelete (file, l);
            }
        }
    }

    private void createBaseData() throws IOException {
        dataDir.mkdir();
        File f = new File (dataDir, "text");
        f.mkdir();
        f = new File (f, "html");
        f.mkdir();
        String toWrite = "<\\s*[Ll][Ii]\\s*>(.*?)<\\s*/[lL][iI]\\s*>\n" +
                         "CASE_INSENSITIVE, DOTALL, MULTILINE,\n" +
                         "0\n" +
                         "stripMarkup\n";

        writeFile (f, "listitems", toWrite);

        toWrite = "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)\n" +
                "CASE_INSENSITIVE, DOTALL,MULTILINE,\n" +
                 "1,3\n" +
                 "\n";

        writeFile (f, "headers", toWrite);

        toWrite = "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)\n";
        writeFile (f, "other", toWrite);

        toWrite = "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)\n" +
                "CASE_INSENSITIVE, DOTALL,MULTILINE,\n" +
                 "9, 5,1,3,  2,\n" +
                 "\n";
        writeFile (f, "misorderedgroups", toWrite);
    }

    private void writeFile (File dir, String name, String data) throws IOException {
        File f = new File (dir, name);
        f.createNewFile();
        f.deleteOnExit();
        FileOutputStream fos = new FileOutputStream (f);
        FileChannel ch = fos.getChannel();
        ch.write (ByteBuffer.wrap(data.getBytes()));
        ch.close();
    }

    private void assertFlag (int flag, int toMatch) {
        assertTrue ((toMatch & flag) != 0);
    }

    private void assertNotFlag (int flag, int toMatch) {
        assertTrue ((toMatch & flag) == 0);
    }

    private void assertContains (String test, String in) {
        assertTrue (in.indexOf(test) >= 0);
    }

    private void assertDoesntContain (String test, String in) {
        assertFalse (in.indexOf(test) >= 0);
    }

    private String str(int[] x) {
        StringBuffer result = new StringBuffer (x.length * 3);
        result.append ('[');
        for (int i = 0; i < x.length; i++) {
            result.append (x[i]);
            if (i != x.length -1) {
                result.append (',');
            }
        }
        result.append (']');
        return result.toString();
    }

    private void assertArraysEqual (int[] a, int[] b) {
        assertTrue ("Expected " + str(a) + " got " + str(b), Arrays.equals (a, b));
    }

    private String detailedDiff(String a, String b) {
        char[] ac = a.toCharArray();
        char[] bc = b.toCharArray();
        StringBuffer result = new StringBuffer();
        if (ac.length != bc.length) {
            result.append ("Lengths differ:\n'" + a + "'\n'" + b + "'\n");
        }
        int max = Math.max (ac.length, bc.length);
        for (int i = 0; i < max; i++) {
            String s;
            if (i < ac.length) {
               result.append ('[');
               result.append (ac[i]);
            } else {
                result.append ("[-");
            }
            if (i < bc.length) {
                result.append (bc[i]);
                result.append (']');
            } else {
                result.append ("-]");
            }
        }
        return result.toString();
    }

    private String toString (PatternItem p) {
        StringBuffer sb = new StringBuffer();
        sb.append ("ITEM: ");
        sb.append (p.getDisplayName());
        sb.append ("\nFLAGS: " + p.getFlagsString(p.getFlags()));
        sb.append ("\nSTRIP: " + p.isStripHtml());
        sb.append ("\nGROUPS: " + str(p.getIncludeGroups()));
        sb.append('\n');
        return sb.toString();
    }


    public void testGroups() {
        System.out.println("testGroups");
        int[] grps = headers.getIncludeGroups();
        assertEquals ("Expected " + str(new int[] { 1, 3}) + " got " + str(grps), 2, grps.length);
        assertEquals ("Expected " + str(new int[] { 1, 3}) + " got " + str(grps), 1, grps[0]);
        assertEquals ("Expected " + str(new int[] { 1, 3}) + " got " + str(grps), 3, grps[1]);
    }

    public void testParseIncludeGroups() {
        System.out.println("testParseIncludeGroups");
        int[] expect = new int[] { 1, 2, 5, 11, 12, 22 };

        String s = "12, 1, 11, 2, 22, 5";
        int[] r = PatternItem.parseIncludeGroups(s);
        assertArraysEqual (expect, r);

        s = "12, 1, 11, 2, 22, 5\n";
        r = PatternItem.parseIncludeGroups(s);
        assertArraysEqual (expect, r);

        s = "12, 1,11, 2,22, 5  \n\n";
        r = PatternItem.parseIncludeGroups(s);
        assertArraysEqual (expect, r);

        s = "12, 1, 11, 2, 22, 5";
        r = PatternItem.parseIncludeGroups(s);
        assertArraysEqual (expect, r);
    }

    public void testReads() {
        System.out.println("testReads");
        assertEquals ("<\\s*[Ll][Ii]\\s*>(.*?)<\\s*/[lL][iI]\\s*>", listitems.getPatternString());
        int flags = listitems.getFlags();
        assertFlag (flags, Pattern.CASE_INSENSITIVE);
        assertFlag (flags, Pattern.DOTALL);
        assertFlag (flags, Pattern.MULTILINE);
        assertNotFlag (flags, Pattern.CANON_EQ);
        assertNotFlag (flags, Pattern.COMMENTS);
        assertNotFlag (flags, Pattern.LITERAL);
        assertNotFlag (flags, Pattern.UNICODE_CASE);
        assertNotFlag (flags, Pattern.UNIX_LINES);

        assertFalse (headers.isStripHtml());
        assertTrue (listitems.isStripHtml());
    }

    public void testDiffers() {
        System.out.println("testDiffers");
        PatternItem test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);

        assertFalse (test.differs(headers));
        assertFalse (headers.differs(test));

        test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3, 5 },
                false);
        assertTrue (test.differs(headers));
        assertTrue (headers.differs(test));

        test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        assertTrue (test.differs(headers));
        assertTrue (headers.differs(test));

        test = new PatternItem ("headers",
                "x(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        assertTrue (test.differs(headers));
        assertTrue (headers.differs(test));

        test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                true);
        assertTrue (test.differs(headers));
        assertTrue (headers.differs(test));
    }

    public void testEquals() {
        System.out.println("testEquals");
        assertTrue (listitems.equals(listitems));
        assertFalse (listitems.equals(headers));
        PatternItem test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        assertTrue (test.equals(headers));
        assertTrue (headers.equals(test));
    }

    public void testHashCode() {
        System.out.println("testHashCode");
        PatternItem test = new PatternItem ("headers",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        assertEquals (test.hashCode(), headers.hashCode());
    }

    public void testGetFlags() {
        System.out.println("testGetFlags");
        PatternItem test = new PatternItem ("monkeys",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        int flags = test.getFlags();
        assertFlag (flags, Pattern.CASE_INSENSITIVE);
        assertFlag (flags, Pattern.DOTALL);
        assertFlag (flags, Pattern.MULTILINE);
        assertNotFlag (flags, Pattern.CANON_EQ);
        assertNotFlag (flags, Pattern.COMMENTS);
        assertNotFlag (flags, Pattern.LITERAL);
        assertNotFlag (flags, Pattern.UNICODE_CASE);
        assertNotFlag (flags, Pattern.UNIX_LINES);

        String s = test.getFlagsString(flags);
        assertContains ("CASE_INSENSITIVE", s);
        assertContains ("DOTALL", s);
        assertContains ("MULTILINE", s);
        assertDoesntContain ("CANON_EQ", s);
        assertDoesntContain ("COMMENTS", s);
        assertDoesntContain ("LITERAL", s);
        assertDoesntContain ("UNIX_CASE", s);
        assertDoesntContain ("UNIX_LINES", s);
    }

    public void testGetDisplayName() {
        System.out.println("testGetDisplayName");
        assertEquals ("headers", headers.getDisplayName());
        assertEquals ("listitems", listitems.getDisplayName());
        PatternItem test = new PatternItem ("monkeys",
                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
                Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL |
                Pattern.MULTILINE,
                new int[] { 1, 3 },
                false);
        assertEquals ("monkeys", test.getDisplayName());
    }

//    public void testGetPattern() {
//        System.out.println("testGetPattern");
//        Pattern p = Pattern.compile("(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
//                Pattern.CASE_INSENSITIVE |
//                Pattern.DOTALL |
//                Pattern.MULTILINE);
//        assertEquals (p.toString(), headers.getPatternString());
//        assertEquals (p.toString(), headers.getPattern().toString());
//    }
//
//    public void testAbsentLinesMatchEmptyArgs() {
//        System.out.println("testAbsentLinesMatchEmptyArgs");
//        PatternItem test = new PatternItem ("other",
//                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
//                0,
//                new int[] { 0 },
//                false);
//        assertEquals (test, flagsundefined);
//        assertFalse (test.differs(flagsundefined));
//        assertFalse (flagsundefined.differs(test));
//    }
//
//    public void testGetPatternString() {
//        assertEquals ("(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)", headers.getPatternString());
//    }
//
//    public void testGetIncludeGroups() {
//        int[] check = new int[] { 1, 2, 3, 5, 9 };
//        assertArraysEqual (
//                check, misorderedgroups.getIncludeGroups());
//    }
//
//    public void testDelete() throws Exception {
//        PatternItem test = new PatternItem ("other",
//                "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)",
//                0,
//                new int[] { 0 },
//                false);
//
//        Exception e = null;
//        try {
//            test.delete("text/plain");
//        } catch (Exception ex) {
//            e = ex;
//        }
//        assertNotNull ("Deleting a pattern item not backed by a file should " +
//                "throw an exception but didn't", e);
//
//        misorderedgroups.delete("text/html");
//        assertNull (lfs.getRoot().getFileObject("text/html/misorderedgroups"));
//    }
//
//    public void testSave() throws Exception {
//        PatternItem test = new PatternItem ("muck",
//                "hello",
//                Pattern.MULTILINE | Pattern.CANON_EQ,
//                new int[] { 3, 5, 1 },
//                false);
//
//        test.save("moo/goo", "muck");
//
//        assertNotNull (lfs.getRoot().getFileObject("moo/goo/muck"));
//        PatternItem item = new PatternItem (DataObject.find(lfs.getRoot().getFileObject("moo/goo/muck")));
//
//        FileObject fob = lfs.getRoot().getFileObject("moo/goo/muck");
//        File f = FileUtil.toFile (fob);
//        ByteBuffer buf = ByteBuffer.allocate((int) f.length());
//        FileInputStream fis = new FileInputStream (f);
//        FileChannel ch = fis.getChannel();
//        ch.read(buf);
//        CharBuffer cb = Charset.defaultCharset().decode(buf);
//        System.err.println("FILE WRITTEN:\n" + cb + "---\n");
//
//        assertEquals (test, item);
//        assertFalse ("Loaded item " + toString(item) + " differs from " + toString(test), item.differs(test));
//        assertFalse (test.differs(item));
//        item.getPatternString();
//        assertFalse ("Loaded item " + toString(item) + " differs from " + toString(test), item.differs(test));
//        assertFalse (test.differs(item));
//    }
//
//
//    public void testNoPatternIfBadPattern() {
//        PatternItem test = new PatternItem ("other",
//                "***)))",
//                0,
//                new int[] { 42 },
//                false);
//        assertNull (test.getPattern());
//    }
//
//    public void testIsStripHtml() {
//        assertFalse (flagsundefined.isStripHtml());
//        assertTrue (listitems.isStripHtml());
//    }
//
//    public void testParseIncludeGroups() {
//        int[] expect = new int[] { 1, 2, 5, 11, 12, 22 };
//
//        String s = "12, 1, 11, 2, 22, 5";
//        int[] r = PatternItem.parseIncludeGroups(s);
//        assertArraysEqual (expect, r);
//
//        s = "12, 1, 11, 2, 22, 5\n";
//        r = PatternItem.parseIncludeGroups(s);
//        assertArraysEqual (expect, r);
//
//        s = "12, 1,11, 2,22, 5  \n\n";
//        r = PatternItem.parseIncludeGroups(s);
//        assertArraysEqual (expect, r);
//
//        s = "12, 1, 11, 2, 22, 5";
//        r = PatternItem.parseIncludeGroups(s);
//        assertArraysEqual (expect, r);
//    }
//
//    public void testParseFlags() {
//        int expect = Pattern.CASE_INSENSITIVE | Pattern.LITERAL | Pattern.CANON_EQ |
//                Pattern.MULTILINE;
//
//        String s = "CASE_INSENSITIVE, LITERAL, CANON_EQ, MULTILINE,\n";
//        int f = PatternItem.parseFlags(s);
//        assertEquals (expect, f);
//
//        s = "CASE_INSENSITIVE, MONKEYPOODLES, LITERAL, HOGWASH, CANON_EQ, MULTILINE,\n";
//        f = PatternItem.parseFlags(s);
//        assertEquals (expect, f);
//
//        s = "CASE_INSENSITIVE, LITERAL, CANON_EQ, MULTILINE";
//        f = PatternItem.parseFlags(s);
//        assertEquals (expect, f);
//
//        s = "    CASE_INSENSITIVE,LITERAL,CANON_EQ,               MULTILINE,\n";
//        f = PatternItem.parseFlags(s);
//        assertEquals (expect, f);
//    }
    public void testGetConfigRoot() {
        assertEquals (lfs.getRoot(), PatternItem.getConfigRoot());
    }

    public void testGetSupportedMimeTypes() {
        Set <String> s = PatternItem.getSupportedMimeTypes();
        assertEquals (1, s.size());
        assertEquals ("text/html", s.iterator().next());
    }
}
