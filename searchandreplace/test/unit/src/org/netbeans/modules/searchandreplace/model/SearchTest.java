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

package org.netbeans.modules.searchandreplace.model;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import junit.framework.TestCase;
import junit.framework.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.searchandreplace.Cancel;

/**
 *
 * @author Tim Boudreau
 */
public class SearchTest extends TestCase {
    
    public SearchTest(String testName) {
        super(testName);
    }

    private File testRoot = null;
    private File goldenRoot = null;
    private File resultsRoot = null;

    private File test0dir = null;
    private File test0goldenDir = null;

    protected void setUp() throws Exception {
        Search.UNIT_TESTING = true;
        File tmpdir = new File (System.getProperty("java.io.tmpdir"));
        if (!tmpdir.exists()) {
            fail ("java.io.tmpdir not set");
        }
        File f = new File (tmpdir, "searchAndReplaceTestData");
        if (!f.exists() || !f.isDirectory()) {
            fail ("Test data not unpacked to " + f.getPath() + " - check the " +
                    "ant task test-preinit");
        }
        testRoot = f;

        test0dir = new File (testRoot, "GUIFormExamples");
        if (!test0dir.exists() || !test0dir.isDirectory()) {
            fail ("Test0 test data not unpacked to " + test0dir.getPath()
                    + " - check the " +
                    "ant task test-preinit");
        }

        f = new File (tmpdir, "searchAndReplaceTestDataGolden");
        if (!f.exists() || !f.isDirectory()) {
            fail ("Test golden data not unpacked to " + f.getPath() + " - check the " +
                    "ant task test-preinit");
        }
        goldenRoot = f;

        test0goldenDir = new File (goldenRoot, "GUIFormExamples");
        if (!test0dir.exists() || !test0dir.isDirectory()) {
            fail ("Test0 test golden data not unpacked to " + test0dir.getPath()
                    + " - check the " +
                    "ant task test-preinit");
        }

        resultsRoot = new File (tmpdir, "searchAndReplaceTestDataExpectedResults");
        if (!f.exists() || !f.isDirectory()) {
            fail ("Test results data not unpacked to " + f.getPath() + " - check the " +
                    "ant task test-preinit");
        }

        testSingleRoot = new File (testRoot, "newpackage");
        testSingleResultsRoot = new File (resultsRoot, "newpackage");
        test0resultsRoot = new File (resultsRoot, "GUIFormExamples");
        assertTrue (test0resultsRoot.exists());
        assertTrue (testSingleRoot.exists());
        assertTrue (testSingleResultsRoot.exists());

        binAndSelectiveRoot = new File (testRoot, "binAndSelective");
        assertTrue (binAndSelectiveRoot + " is missing",
                binAndSelectiveRoot.exists());

        binAndSelectiveGoldenRoot = new File (testRoot, "binAndSelective");
        assertTrue (binAndSelectiveGoldenRoot + " is missing",
                binAndSelectiveGoldenRoot.exists());

        binaryFile = new File (binAndSelectiveRoot, "nb.exe");
        assertTrue (binaryFile + " is missing", binaryFile.exists());

        selectiveFile = new File (binAndSelectiveRoot, "foo.txt");
        assertTrue (selectiveFile + " is missing ", selectiveFile.exists());

        goldenSelectiveFile = new File (binAndSelectiveGoldenRoot, "foo.txt");
        assertTrue (goldenSelectiveFile + " is missing ", goldenSelectiveFile .exists());

        goldenBinaryFile = new File (binAndSelectiveGoldenRoot, "nb.exe");
        assertTrue (goldenBinaryFile + " is missing", goldenBinaryFile.exists());
    }

    public void testSelectiveReplacement() throws Exception {
        System.out.println("testSelectiveReplacement");
        SearchDescriptor descriptor = new SearchDescriptor("hello",
                "goodbye", true, true, false, true, true);

        Search s = new Search (Collections.singleton(selectiveFile),
                descriptor, new Cancel());

        ProgressHandle handle = ProgressHandleFactory.createHandle("Do stuff");
        handle.start();
        Item[] items = s.search(handle);
        Arrays.sort(items, new ItemComparator());

        for (int i=0; i < items.length; i++) {
            items[i].setShouldReplace(i % 2 == 0);
        }
        for (int i=0; i < items.length; i++) {
            if (items[i].isShouldReplace()) {
                items[i].replace();
            }
        }

        assertTrue (readFile(selectiveFile).indexOf("goodbye") >= 0);
        System.out.println("FILE:\n\n" + readFile (selectiveFile) + "\n\n");

        boolean even = true;
        int ct = 0;
        for (StringTokenizer tok = new StringTokenizer (readFile(selectiveFile), "\n"); tok.hasMoreElements();) {
            String s1 = tok.nextToken().trim();
            if ("hello".equals(s) || "goodbye".equals(s1)) {
                if (even) {
                    assertEquals ("hello", s1);
                } else {
                    assertEquals ("goodbye", s1);
                    ct++;
                }
            }
            even = !even;
        }

        SearchDescriptor descriptor1 = new SearchDescriptor("goodbye",
                "hello", true, true, false, true, true);

        Search s1 = new Search (Collections.singleton(selectiveFile),
                descriptor1, new Cancel());

        handle = ProgressHandleFactory.createHandle("Do stuff");
        handle.start();
        Item[] items1 = s1.search(handle);
        Arrays.sort (items1, new ItemComparator());
        assertEquals (ct, items1.length);
        assertEquals (items.length / 2, items1.length);
        for (int i=0; i < items1.length; i++) {
            items1[i].replace();
        }
        assertFalse (readFile(selectiveFile).indexOf("goodbye") >= 0);

        assertIdentical (goldenSelectiveFile, selectiveFile);
    }

    public void testReplaceOverBinaryFilesIsReversible() throws Exception {
        System.out.println("testReplaceOverBinaryFilesIsReversible");
        SearchDescriptor descriptor = new SearchDescriptor("harness",
                "poodlehoover", true, true, true, true, true);

        Search s = new Search (Collections.singleton(binaryFile),
                descriptor, new Cancel());

        System.out.println("\n\nFILE:\n" + readFile (binaryFile) + "\n\n");

        ProgressHandle handle = ProgressHandleFactory.createHandle("Do stuff");
        handle.start();
        Item[] items = s.search(handle);
        Arrays.sort(items, new ItemComparator());
        assertTrue (items.length > 0);
        for (int i=0; i < items.length; i++) {
            items[i].replace();
        }

        assertTrue (readFile(binaryFile).indexOf("poodlehoover") >= 0);

        SearchDescriptor descriptor1 = new SearchDescriptor("poodlehoover",
                "harness", true, true, true, true, true);

        Search s1 = new Search (Collections.singleton(binaryFile),
                descriptor1, new Cancel());

        handle = ProgressHandleFactory.createHandle("Do more stuff");
        handle.start();

        Item[] items1 = s1.search (handle);
        Arrays.sort (items1, new ItemComparator());
        assertTrue (items1.length > 0);
        for (int i=0; i < items1.length; i++) {
            items1[i].replace();
        }
        assertFalse (readFile(binaryFile).indexOf("poodlehoover") >= 0);
        assertTrue (readFile(binaryFile).indexOf("harness") >= 0);

        assertIdentical (goldenBinaryFile, binaryFile);
    }

    private File goldenSelectiveFile = null;
    private File binAndSelectiveGoldenRoot = null;
    private File goldenBinaryFile = null;
    private File selectiveFile = null;
    private File binaryFile = null;
    private File binAndSelectiveRoot = null;
    private File test0resultsRoot = null;
    private File testSingleRoot = null;
    private File testSingleResultsRoot = null;

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SearchTest.class);
        return suite;
    }

    public void testSingleItemsAreReplaced() throws Exception {
        System.out.println("testSingleItemsAreReplaced");
        SearchDescriptor descriptor = new SearchDescriptor("\"OK\"",
                "Utils.OK", true, true, false, true, true);

        Search s = new Search (Collections.singleton(testSingleRoot),
                descriptor, new Cancel());

        ProgressHandle handle1 = ProgressHandleFactory.createHandle("Do stuff");
        handle1.start();
        Item[] items = s.search(handle1);

        Arrays.sort (items, new ItemComparator());

        for (int i=0; i < items.length; i++) {
            if (items[i].getName().startsWith("Util")) {
                items[i].setShouldReplace(false);
                System.err.println("Marking " + items[i] + " as non-replace");
            }
        }
        for (int i=0; i < items.length; i++) {
            if (items[i].isShouldReplace()) {
                items[i].replace();
                System.err.println("Replaced item " + items[i]);
                items[i].setReplaced(true);
            }
        }
        assertIdentical (testSingleResultsRoot, testSingleRoot);
    }

    public void testReversableSearch() throws Exception {
        System.out.println("testReversableSearch");

        SearchDescriptor descriptor = new SearchDescriptor("swing",
                "fwaddle", true, true, false, true, true);

        Search s = new Search (Collections.singleton(test0dir), descriptor,
                new Cancel());

        Set files = s.getAllFiles();
        for (Iterator i = files.iterator(); i.hasNext();) {
            File f = (File) i.next();
            assertFalse (f.getName().endsWith(".class"));
            assertFalse (f.getName().endsWith(".jar"));
        }

        ProgressHandle handle1 = ProgressHandleFactory.createHandle("Do stuff");
        handle1.start();
        Item[] items = s.search(handle1);

        Arrays.sort (items, new ItemComparator());

        assertTrue (items.length > 0);

        for (int i=0; i < items.length; i++) {
            assertTrue (items[i].isShouldReplace());
//            System.err.println("Performing replace on " + items[i]);
            items[i].replace();
        }

//        System.err.println("Enter assert identical");
        assertIdentical (test0dir, test0resultsRoot);

        SearchDescriptor descriptor2 = new SearchDescriptor("fwaddle",
                "swing", true, true, false, true, true);

        Search s2 = new Search (Collections.singleton(test0dir), descriptor2,
                new Cancel());

        ProgressHandle handle2 = ProgressHandleFactory.createHandle("Do " +
                "more stuff");

        handle2.start();
        Item[] items2 = s2.search(handle2);
        Arrays.sort (items2, new ItemComparator());

        assertEquals (items.length, items2.length);

        for (int i = 0; i < items2.length; i++) {
            items2[i].replace();
        }

        assertIdentical (test0goldenDir, test0dir);
    }

    public void testAssertIdenticalSanity() throws Exception {
        System.out.println("testAssertIdenticalSanity");
        File a = new File (System.getProperty("java.io.tmpdir") + File.separator + "a.txt");
        File b = new File (System.getProperty("java.io.tmpdir") + File.separator + "a.txt");
        if (a.exists()) {
            assertTrue ("file locked", a.delete());
            Thread.currentThread().sleep(400);
        }
        if (b.exists()) {
            assertTrue ("file locked", b.delete());
            Thread.currentThread().sleep(400);
        }
        a.createNewFile();
        b.createNewFile();
        a.deleteOnExit();
        b.deleteOnExit();
        FileOutputStream oa = new FileOutputStream (a);
        FileOutputStream ob = new FileOutputStream (b);
        String content = "This is some useless content\n\n murble wurble\n";
        FileChannel ac = oa.getChannel();
        FileChannel bc = ob.getChannel();
        ByteBuffer buf = ByteBuffer.wrap (content.getBytes());
        ac.write(buf);
        bc.write(buf);
        ac.force(true);
        bc.force(true);
        ac.close();
        bc.close();
        assertIdentical (a, b);
    }

    public void testReadFileSanity() throws Exception {
        System.out.println("testReadFileSanity");
        File a = new File (System.getProperty("java.io.tmpdir") + File.separator + "c.txt");
        if (a.exists()) {
            assertTrue ("file locked", a.delete());
            Thread.currentThread().sleep(400);
        }
        assertTrue ("Tmpdir is broken", a.createNewFile());
        a.deleteOnExit();
        FileOutputStream oa = new FileOutputStream (a);
        String content = "This is some useless content\n\n murble wurble\n";
        FileChannel ac = oa.getChannel();
        ByteBuffer buf = ByteBuffer.wrap (content.getBytes());
        ac.write(buf);
        ac.force(true);
        ac.close();
        assertEquals (content, readFile(a));
    }

    private void assertIdentical (File a, File b) throws IOException {
        assertTrue ("One is a dir one isn't " + a.getPath() + " and " +
                b.getPath(), a.isDirectory() == b.isDirectory());
        assertTrue (a.exists());
        assertTrue (b.exists());
        assertTrue (a.getName().equals(b.getName()));

        if (a.isFile()) {
//            System.err.println("Comparing files " + a.getName());
            assertTrue (a.getPath() + " is " +
                    a.length() + " bytes but " + b.getPath() + " is " +
                    b.length() + " bytes:\n EXPECTED:\n" + readFile(a) +
                    "\n\nACTUAL:\n" + readFile(b), a.length() == b.length());

            ByteBuffer aBuffer = ByteBuffer.allocate((int) a.length());
            ByteBuffer bBuffer = ByteBuffer.allocate((int) b.length());

            FileInputStream as = new FileInputStream (a);
            FileInputStream bs = new FileInputStream (b);

            FileChannel aChannel = as.getChannel();
            FileChannel bChannel = bs.getChannel();

            as.getChannel().read(aBuffer);
            bs.getChannel().read(bBuffer);

            aChannel.close();
            bChannel.close();

            aBuffer.rewind();
            bBuffer.rewind();

            int len = (int) Math.min(a.length(), b.length());
            assertEquals (aBuffer, bBuffer);
//            for (int i=0; i < len; i++) {
//                byte aByte = aBuffer.get();
//                byte bByte = bBuffer.get();
//                assertEquals ("Files " + a.getPath() + " and " +
//                        b.getPath() + " differ at byte " + i + "\nEXPECTED:\n" 
//                        + readFile(a) + "\n\nACTUAL:\n" + readFile(b), aByte,
//                        bByte);
//            }
        } else if (a.isDirectory()) {
            File[] aFiles = a.listFiles();
            File[] bFiles = b.listFiles();
//            System.err.println("Comparing directories " + a.getName() + " and " + b.getName());
            Arrays.sort (aFiles, new FC());
            Arrays.sort (bFiles, new FC());
            assertEquals ("Mismatched number of files in directory - " + a.getPath() +
                    " contains " + aFiles.length + ", " + b.getPath() + 
                    " contains " + bFiles.length + "\nA-Files:" + list(aFiles) +
                    "\nB-Files:" + list(bFiles), aFiles.length, bFiles.length);

            for (int i=0; i < aFiles.length; i++) {
                assertEquals (aFiles[i].getName(), bFiles[i].getName());
                assertIdentical (aFiles[i], bFiles[i]);
            }
        }
    }

    private String list(File[] files) {
        StringBuffer sb = new StringBuffer(" [");
        for (int i=0; i < files.length; i++) {
            sb.append (files[i].getName());
            if (i != files.length-1) {
                sb.append (", ");
            }
        }
        sb.append("] ");
        return sb.toString();
    }

    private static final int PADDING = 40;
    private String context (Item item) throws IOException {
        String all = readFile (item.getFile());
        Point p = item.getLocation();
        if (p.x < all.length() && p.y < all.length()) {
            int end = Math.min (all.length() - 1, p.y + PADDING);
            int start = Math.max (0, p.x - PADDING);
            StringBuffer sb = new StringBuffer(all.substring(start, end));
            sb.insert(PADDING, "[");
            sb.insert((sb.length() + 1) - PADDING, "]");
            return sb.toString();
        }
        return all;
    }

    private String readFile(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        FileChannel channel = fis.getChannel();
        ByteBuffer buf = ByteBuffer.allocate((int) f.length());
        channel.read(buf);
        channel.close();
        buf.rewind();
        return Charset.defaultCharset().decode(buf).rewind().toString();
    }

    private static final class FC implements Comparator {
        public int compare(Object o1, Object o2) {
            File a = (File) o1;
            File b = (File) o2;
            return a.getPath().compareTo(b.getPath());
        }
    }
}
