/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.searchandreplace.model;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import junit.framework.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.searchandreplace.Cancel;
import org.netbeans.modules.searchandreplace.model.ItemComparator;

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
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SearchTest.class);
        
        return suite;
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

        items[2].setShouldReplace(false);

        for (int i=0; i < items.length; i++) {
            if (items[i].isShouldReplace()) {
                items[i].replace();
                items[i].setReplaced(true);
            }
        }

        File f = null;
        CharSequence seq = null;
        boolean fileChanged = true;
        for (int i=0; i < items.length; i++) {
            if (i != 2) {
                assertTrue ("Not replaced: " + i + ":" + items[i], items[i].isReplaced());
                File curr = items[i].getFile();
                if (!curr.equals(f)) {
                    f = curr;
                    seq = readFile (f);
                    fileChanged = true;
                } else {
                    //offsets may be wrong so only test the first item for now
                    fileChanged = false;
                }
                if (fileChanged) {
                    Point p = items[i].getLocation();
                    if (p.y < seq.length() - 1) {
                        String test = seq.subSequence(p.x, p.y).toString();
                        assertEquals ("At " + i + " in " + f.getPath() +
                                " string not replaced by " + items[i], "fwaddle", test);
                    }
                }
            } else {
                assertFalse (items[i].isReplaced());
            }
        }

        SearchDescriptor descriptor2 = new SearchDescriptor("fwaddle",
                "swing", true, true, false, true, true);

        Search s2 = new Search (Collections.singleton(test0dir), descriptor2,
                new Cancel());

        ProgressHandle handle2 = ProgressHandleFactory.createHandle("Do " +
                "more stuff");

        handle2.start();
        Item[] items2 = s2.search(handle2);
        Arrays.sort (items2, new ItemComparator());

        assertEquals (items.length - 2, items2.length);

        for (int i = 0; i < items2.length; i++) {
            items2[i].replace();
        }

        assertIdentical (test0goldenDir, test0dir);
    }

    private void assertIdentical (File a, File b) throws IOException {
        assertTrue ("One is a dir one isn't " + a.getPath() + " and " +
                b.getPath(), a.isDirectory() == b.isDirectory());
        assertTrue (a.exists());
        assertTrue (b.exists());

        if (a.isFile()) {
            assertTrue (a.getPath() + " is " +
                    a.length() + " bytes but " + b.getPath() + " is " +
                    b.length() + " bytes:\n A:\n" + readFile(a) +
                    "\n\nB:\n" + readFile(b), a.length() == b.length());

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

            int len = (int) a.length();
            for (int i=0; i < len; i++) {
                byte aByte = aBuffer.get();
                byte bByte = bBuffer.get();
                assertEquals ("Files " + a.getPath() + " and " +
                        b.getPath() + " differ at byte " + i, aByte, bByte);
            }
        } else if (a.isDirectory()) {
            File[] aFiles = a.listFiles();
            File[] bFiles = b.listFiles();
            Arrays.sort (aFiles, new FC());
            Arrays.sort (bFiles, new FC());
            assertEquals (aFiles.length, bFiles.length);
            for (int i=0; i < aFiles.length; i++) {
                assertEquals (aFiles[i].getName(), bFiles[i].getName());
                assertIdentical (aFiles[i], bFiles[i]);
            }
        }
    }

    private CharSequence readFile(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        FileChannel channel = fis.getChannel();
        ByteBuffer buf = ByteBuffer.allocate((int) f.length());
        channel.read(buf);
        channel.close();
        return Charset.defaultCharset().decode(buf);
    }

    private static final class FC implements Comparator {
        public int compare(Object o1, Object o2) {
            File a = (File) o1;
            File b = (File) o2;
            return a.getPath().compareTo(b.getPath());
        }
    }
}
