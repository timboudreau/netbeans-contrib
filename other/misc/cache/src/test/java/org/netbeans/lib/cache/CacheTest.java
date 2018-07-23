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
/*
 * CacheTest.java
 * JUnit based test
 *
 * Created on October 25, 2004, 2:02 PM
 */

package org.netbeans.lib.cache;

import java.util.StringTokenizer;
import junit.framework.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.netbeans.lib.storage.FileMapStorage;
import org.netbeans.api.storage.Storage;
import org.netbeans.api.cache.*;
import java.io.*;
import java.lang.ref.WeakReference;

/**
 * Tests the functionality of Cache and BBStream.
 *
 * @author Tim Boudreau
 */
public class CacheTest extends TestCase {
    
    public CacheTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(CacheTest.class);
        
        return suite;
    }

    private CacheImpl cache = null;
    File td = null;
    File cacheDir = null;
    String name = "test";
    protected void setUp() throws java.lang.Exception {
        td = new File (System.getProperty("java.io.tmpdir"));
        cacheDir = new File (td, "cacheTest");
        
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        
        cache = new CacheImpl (name, cacheDir);
        File f = cache.getStorageFile();
        boolean recreate = false;
        if (f.exists()) {
            f.delete();
            recreate = true;
        }
        f = cache.getIndicesFile();
        if (f.exists()) {
            f.delete();
            recreate = true;
        }
        if (recreate) {
            cache = new CacheImpl (name, cacheDir);
        }
    }
    
    protected void tearDown() throws Exception {
        if (cache != null) {
            cache.delete();
        }
    }

    
    public void testAdd() throws Exception {
        System.out.println("testAdd");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        
        assertTrue (cache.size() == 3);
        
        String s1t = cache.get(0).toString();
        assertEquals (s1, s1t);
        CharSequence seq = cache.get(1);
        String s2t = seq.toString();
        assertEquals (s2.length(), seq.length());
        assertEquals ("Should be \"" + s2 + "\" + but was \"" + s2t + "\"", s2, s2t);
        String s3t = cache.get(2).toString();
        assertEquals (s3, s3t);
    }

    public void testSize() throws Exception {
        System.out.println("testSize");
        assertTrue (cache.size() == 0);
        cache.add (s1);
        assertTrue (cache.size() == 1);
        cache.delete();
        assertTrue (cache.size() == 0);
    }


    public void testGetBuffer() throws Exception {
        System.err.println("testGetBuffer");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        
        ByteBuffer bb = cache.getBuffer(0);
        assertEquals (s1, bb.asCharBuffer().toString());

        ByteBuffer bb2 = cache.getBuffer(1);
        assertEquals (s2, bb2.asCharBuffer().toString());
        
        ByteBuffer bb3 = cache.getBuffer(2);
        assertEquals (s3, bb3.asCharBuffer().toString());
        
    }

    public void testGet() throws Exception {
        System.err.println("testGet");
        String s1 = "This is the first entry";
        String s2 = "This is the second entry";
        cache.add (s1);
        cache.add (s2);
        
        CharSequence cb = cache.get(0);
        assertEquals (s1, cb.toString());

        CharSequence cb2 = cache.get(1);
        assertEquals (s2, cb2.toString());
    }

    public void testGetLastModified() throws Exception {
        System.out.println("testGetLastModified");
        long l1 = cache.getLastModified();
        System.out.println("testAdd");
        String s1 = "This is the first entry";
        String s2 = "This is the second entry";
        Thread.currentThread().sleep (1000);
        cache.add (s1);
        long l2 = cache.getLastModified();
        Thread.currentThread().sleep (1000);
        cache.add (s2);
        long l3 = cache.getLastModified();
        
        assertTrue (l2 > l1);
        assertTrue ("Last modified time did not change - was " + l2 + " now " + l3, l3 > l2);
    }

    public void testClose() throws Exception {
        System.out.println("testClose");
        String s1 = "This is the first entry";
        String s2 = "This is the second entry";
        cache.add (s1);
        cache.add (s2);
        
        cache.close();
        Thread.currentThread().sleep (500);
        File f = cache.getStorageFile();
        assertTrue (f.exists());
        f = cache.getIndicesFile();
        assertTrue (f.exists());
    }
    
    public void testReadWrite() throws Exception {
        System.out.println("testReadWrite");
        String s1 = "This is the first entry";
        String s2 = "This is the second entry";
        String s3 = "This is another entry.  It is a bit\n longer than the others, just for \n fun.  Isn't that interesting?";
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        
        cache.save();
        
        assertTrue (cache.getIndicesFile().exists());
        
        CacheImpl nue = new CacheImpl (name, cacheDir);
        
        assertTrue (cache.indices.equals(nue.getIndices()));
        
        assertEquals (cache.size(), nue.size());
        
        assertEquals (s1, nue.get(0).toString());
        assertEquals (s3, nue.get(2).toString());
        assertEquals ("Should be \"" + s2 + "\" + but was \"" + nue.get(1).toString() + "\"", s2, nue.get(1).toString());
    }
    
    public void testRename() throws Exception {
        System.out.println("testRename");
        String s1 = "This is the first entry";
        String s2 = "This is the second entry";
        String s3 = "This is another entry.  It is a bit\n longer than the others, just for \n fun.  Isn't that interesting?";
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        
        File oldStorage = cache.getStorageFile();
        File oldIndices = cache.getIndicesFile();
        
        String newName = "another";
        cache.rename (newName);
        
        Thread.currentThread().sleep(500);
        assertFalse (oldStorage.exists());
        assertFalse (oldIndices.exists());
        
        assertEquals (3, cache.size());
        assertEquals (s1, cache.get(0).toString());
        assertEquals (s2, cache.get(1).toString());
        assertEquals (s3, cache.get(2).toString());
        
        cache.save();

        CacheImpl nue = new CacheImpl (newName, cacheDir);
        assertEquals (3, nue.size());
        assertEquals (s1, nue.get(0).toString());
        assertEquals (s2, nue.get(1).toString());
        assertEquals (s3, nue.get(2).toString());
    }

    public void testDelete() throws Exception{
        System.out.println("testDelete");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        
        File oldStorage = cache.getStorageFile();
        File oldIndices = cache.getIndicesFile();
        cache.close();
       
        assertTrue (oldStorage.exists());
        assertTrue (oldIndices.exists());
        
        cache.delete();
        
        assertFalse (oldStorage.exists());
        assertFalse (oldIndices.exists());
        assertEquals (0, cache.size());
        
        CacheImpl nue = new CacheImpl (name, cacheDir);
        assertTrue (nue.size() == 0);
    }
 
    public void testExists() throws Exception {
        System.out.println("testExists");
        assertFalse (cache.exists());
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.close();
        assertTrue (cache.exists());
        cache.delete();
        assertFalse (cache.exists());
    }
    
    public void testDeleteEntry() throws Exception {
        System.out.println("testDeleteEntry");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.add (s4);
        
        assertEquals (4, cache.size());
        cache.deleteEntry (1);
        
        assertEquals (3, cache.size());
        assertEquals (s1, cache.get(0).toString());
        assertEquals (s3, cache.get(1).toString());
        assertEquals (s4, cache.get(2).toString());
        
        cache.deleteEntry (0);
        assertEquals (2, cache.size());
        assertEquals (s3, cache.get(0).toString());
        assertEquals (s4, cache.get(1).toString());
        
        cache.deleteEntry (1);
        assertEquals (1, cache.size());
        assertEquals (s3, cache.get(0).toString());
        
        cache.deleteEntry (0);
        assertEquals (0, cache.size());
        
        Exception e = null;
        try {
            cache.get(0);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
    }
    
    
    public void testDeleteEntries() throws Exception {
        System.out.println("testDeleteEntries");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.add (s4);
        
        assertEquals (4, cache.size());
        cache.deleteEntries (1, 2);
        
        assertEquals (2, cache.size());
        String s1t = cache.get(0).toString();
        assertEquals ("Expected \"" + s1 + "\" not \"" + s1t + "\"", s1, s1t);
        String s4t = cache.get(1).toString();
        assertEquals ("Expected \"" + s4 + "\" not \"" + s4t + "\"", s4, s4t);
        
        cache.deleteEntries (0, 1);
        assertEquals (0, cache.size());
        Exception e = null;
        try {
            cache.get(0);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
    }    

    public void testDeleteEntriesFromStart() throws Exception {
        System.out.println("testDeleteEntriesFromStart");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.add (s4);
        
        assertEquals (4, cache.size());
        cache.deleteEntries (0, 2);
        assertEquals (1, cache.size());
        String s4t = cache.get(0).toString();
        assertEquals ("Expected \"" + s4 + "\" not \"" + s4t + "\"", s4, s4t);
    }
    
    public void testDeleteEntriesFromEnd() throws Exception {
        System.out.println("testDeleteEntriesFromEnd");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.add (s4);
        
        assertEquals (4, cache.size());
        cache.deleteEntries (1, 3);
        assertEquals (1, cache.size());
        String s1t = cache.get(0).toString();
        assertEquals ("Expected \"" + s1 + "\" not \"" + s1t + "\"", s1, s1t);
    }
    
    public void testDeleteAllEntries() throws Exception {
        System.out.println("testDeleteAllEntries");
        cache.add (s1);
        cache.add (s2);
        cache.add (s3);
        cache.add (s4);
        
        assertEquals (4, cache.size());
        cache.deleteEntries (0, 3);
        assertEquals (0, cache.size());
    }
    
    public void testStreamReads() throws Exception {
        System.out.println("testStreamReads");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        
        for (int i=0; i < strings.length; i++) {
//            checkStream (new BBStream(cache.getBuffer(i)), strings[i]);
            checkStream (cache.getInputStream(i), strings[i]);
        }
    }
    
    private void checkStream (CacheInputStream stream, String s) throws Exception {
        System.out.println("  checkStream for " + s);
        int avail = stream.available();
        assertEquals (avail, s.length() * 2);
        byte[] bytes = new byte[avail];
        stream.read (bytes);
        String tst = new String(bytes, "UTF-16");
        assertEquals (s.length(), tst.length());
        assertEquals ("Expected \"" + s + "\" not \"" + tst + "\"", s, tst);
        assertEquals (0, stream.available());
        stream.close();
    }
    
    
    public void testStreamWrites() throws Exception {
        System.out.println("testStreamWrites");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        
        CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(1);
        String s = "Something completely different";
        
        out.write (utfBytes(s));
        out.close();
        cache.storage.flush();
        
        for (int i=0; i < strings.length; i++) {
            if (i != 1) {
                try {
                    String got = cache.get(i).toString();
//                    assertEquals (strings[i].length(), got.length());
                    assertEquals ("Expected \"" + strings[i] + "\" not \"" + got +"\"", strings[i], got);
                } catch (Exception e) {
                    System.err.println("Failure on index " + i + " cache size " +
                       cache.size() + " cache indices " + cache.indices);
                    e.printStackTrace();
                    fail (e.getMessage());
                }
            } else {
                String got = cache.get(i).toString();
                assertEquals ("Expected \"" + s + "\" not \"" + got +"\"", s, got);
            }
        }
    }
    
    public void testOverwriteFirst() throws Exception {
        System.out.println("testOverwriteFirst");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        
        CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(0);
        String s = "Something completely different";
        
        out.write (utfBytes(s));
        out.close();
        cache.storage.flush();
        
        for (int i=0; i < strings.length; i++) {
            if (i != 0) {
                try {
                    String got = cache.get(i).toString();
                    assertEquals (strings[i].length(), got.length());
                    assertEquals ("Expected \"" + strings[i] + "\" not \"" + got +"\"", strings[i], got);
                } catch (Exception e) {
                    System.err.println("Failure on index " + i + " cache size " +
                       cache.size() + " cache indices " + cache.indices);
                    e.printStackTrace();
                    fail (e.getMessage());
                }
            } else {
                String got = cache.get(i).toString();
                assertEquals ("Expected \"" + s + "\" not \"" + got +"\"", s, got);
            }
        }
    }    
    
    public void testOverwriteLast() throws Exception {
        System.out.println("testOverwriteLast");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        
        CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(3);
        String s = "Something completely different";
        
        out.write (utfBytes(s));
        out.close();
        cache.storage.flush();
        
        for (int i=0; i < strings.length; i++) {
            if (i != 3) {
                try {
                    String got = cache.get(i).toString();
                    assertEquals (strings[i].length(), got.length());
                    assertEquals ("Expected \"" + strings[i] + "\" not \"" + got +"\"", strings[i], got);
                } catch (Exception e) {
                    System.err.println("Failure on index " + i + " cache size " +
                       cache.size() + " cache indices " + cache.indices);
                    e.printStackTrace();
                    fail (e.getMessage());
                }
            } else {
                String got = cache.get(i).toString();
                assertEquals ("Expected \"" + s + "\" not \"" + got +"\"", s, got);
            }
        }
    }        
    
    public void testOverwriteEverything() throws Exception {
        System.out.println("testOverwriteEverything");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        
        String[] reps = new String[] {
            "Something completely different",
            "In outer space, they don't dress like us",
            "In outer space, they dress different",
            "I walk through walls"
        };
        
        for (int i=0; i < reps.length; i++) {
            CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(i);
            out.write (utfBytes(reps[i]));
            out.close();
        }
        cache.storage.flush();
        
        for (int i=0; i < strings.length; i++) {
            try {
                assertEquals (reps[i], cache.get(i).toString());
            } catch (Exception e) {
                System.err.println("EXCEPTION ON INDEX " + i + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    } 
        
    
    private static byte[] utfBytes (String s) throws Exception {
        byte[] b = s.getBytes("UTF-16");
        byte[] result = new byte[b.length-2];
        System.arraycopy (b, 2, result, 0, result.length);
        return result;
    }
    
    
    public void testDeleteWhileWritingThrowsException() throws Exception {
        System.out.println("testDeleteWhileWritingThrowsException");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        String s = "Hello world";
        CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(2);
        out.write (utfBytes(s));
        
        Exception e = null;
        try {
            cache.deleteEntry(2);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull("Exception should have been thrown", e);
    }
    
    public void testDeleteWhileWritingStillWritesToCorrectIndex() throws Exception {
        System.out.println("testDeleteWhileWritingStillWritesToCorrectIndex");
        for (int i=0; i < strings.length; i++) {
            cache.add (strings[i]);
        }
        String s = "Hello world";
        CacheOutputStream out = (CacheOutputStream) cache.getOutputStream(2);
        out.write (utfBytes(s));
        
        cache.deleteEntry(0);
        out.close();
        
        assertEquals (s, cache.get(1).toString());
    }
    
    private static boolean deltree (File f) throws Exception {
        boolean result = true;
        if (f.exists()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (int i=0; i < kids.length; i++) {
                    result &= deltree(kids[i]);
                }
            }
            result &= f.delete();
        }
        return result;
    }
    
    public void testNoHeldReferences() throws Exception {
        System.out.println("testNoHeldReferences");
        File file = new File (cacheDir, "refs");
        assertTrue ("Could not delete cache file " + file, deltree(file));
        CacheImpl c = new CacheImpl ("refs/test/folder", cacheDir);
        
        assertFalse (c.getStorageFile().exists());
        
        for (int i=0; i < strings.length; i++) {
            c.add (strings[i]);
        }
        c.close();
        
        WeakReference ref = new WeakReference (c);
        c = null;
        for (int i=0; i < 5; i++) {
            System.gc();
        }
        assertNull ("Cache still referenced", ref.get());
        
        assertTrue ("Could not delete cache file " + file, deltree(file));
    }
    
    public void testNestedDirsCache() throws Exception {
        File file = new File (cacheDir, "some");
        assertTrue ("Could not delete cache file " + file, deltree(file));
        assertFalse (file.exists());
        
        CacheImpl c = new CacheImpl ("some/other/folder", cacheDir);
        
        assertFalse (c.getStorageFile().exists());
        
        for (int i=0; i < strings.length; i++) {
            c.add (strings[i]);
        }
        c.close();
        
        File f = c.getStorageFile();
        assertTrue (f + " should exist", f.exists());
        File f1 = c.getIndicesFile();
        assertTrue (f1 + " should exist", f.exists());
        
        assertTrue (f + " and " + f1 + " should have the same parent dir",
                f1.getParent().equals(f.getParent()));
        
        c = new CacheImpl ("some/other/folder", cacheDir);
        for (int i=0; i < strings.length; i++) {
            assertEquals (strings[i], c.get(i).toString());
        }
        File nu = new File (cacheDir, "a");
        assertTrue (deltree(nu));
        
        c.rename ("a/new/folder/thats/deeper");
        c.add ("I'm a little teapot");

        for (int i=0; i < strings.length; i++) {
            assertEquals (strings[i], c.get(i).toString());
        }
    }
    
    public void testIllegalNamesFail() throws Exception {
        Exception e = null;
        try {
            Cache c = new CacheImpl ("/bad", cacheDir);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
        e = null;
        
        try {
            Cache c = new CacheImpl ("also/bad/", cacheDir);
        } catch (Exception e2) {
            e = e2;
        }
        assertNotNull(e);
        e = null;

        try {
            Cache c = new CacheImpl ("/really/bad/", cacheDir);
        } catch (Exception e2) {
            e = e2;
        }
        assertNotNull(e);
    }

    public void testPathToFile() throws Exception {
        System.out.println("testPathToFile");
        File f = new File ("/food/bar");
        File f2 = CacheImpl.pathToFile (f, "baz/boo/bam");
        assertEquals("/food/bar/baz/boo/bam", f2.getPath());
    }
    
    public void testCreateIntermediateDirs() throws Exception {
        System.out.println("testCreateIntermediateDirs");
        File f = new File (td.getPath() + File.separator + "idTest" + System.currentTimeMillis());
        if (f.exists()) {
            f.delete();
        }
        File f2 = new File (f, "testDir");
        File f3 = new File (f2, "test2");
        assertFalse (f2.exists());
        assertFalse (f.exists());
        assertFalse (f3.exists());
        CacheImpl.createIntermediateDirs(f3);
        assertTrue (f.exists());
        assertTrue (f2.exists());
        assertTrue (f3.exists());
        
        deltree(f);
    }
    
    public void testRenameFiles() throws Exception {
        System.out.println("testRenameFiles");
        File f = new File (td.getPath() + File.separator + "renameTest" + System.currentTimeMillis());
        File f1 = new File (f, "subdir1");
        File f2 = new File (f1, "subdir2");
        File f3 = new File (f2, "subdir3");
        CacheImpl.createIntermediateDirs(f3);
        File data1 = new File (f3, "data1");
        File data2 = new File (f3, "data2");
        FileOutputStream fos1 = new FileOutputStream(data1);
        FileOutputStream fos2 = new FileOutputStream(data2);
        fos1.write ("one".getBytes());
        fos2.write ("two".getBytes());
        fos1.flush();
        fos2.flush();
        fos1.close();
        fos2.close();
        fos1 = null;
        fos2 = null;
        System.gc();
        assertTrue (data1.exists());
        assertTrue (data2.exists());
        
        File r = new File (td, "renamedDir" + System.currentTimeMillis());
        File r1 = new File (r, "renameSub1");        
        File r2 = new File (r1, "renameSub2");
        
        File rdata1 = new File (r2, "rdata1");
        File rdata2 = new File (r2, "rdata2");
        assertFalse (rdata2.exists());
        assertFalse (rdata1.exists());
        
        File[] orig = new File[] {data1, data2};
        File[] nue = new File[] {rdata1, rdata2};
        
        CacheImpl.renameFiles (orig, nue, false);
        
        assertTrue (r.exists());
        assertTrue (r1.exists());
        assertTrue (r2.exists());
        assertTrue (rdata1.exists());
        assertTrue (rdata2.exists());
        assertFalse (rdata1.isDirectory());
        assertFalse (rdata2.isDirectory());
        
        assertFalse (data1.exists());
        assertFalse (data2.exists());
        assertTrue (f.exists());
        assertTrue (f1.exists());
        assertTrue (f2.exists());
        assertTrue (f3.exists());
        
        CacheImpl.renameFiles (nue, orig, true);
        assertTrue (data1.exists());
        assertTrue (data2.exists());
        assertFalse (data1.isDirectory());
        assertFalse (data2.isDirectory());
        assertTrue (f.exists());
        assertTrue (f1.exists());
        assertTrue (f2.exists());
        assertTrue (f3.exists());
        
        assertFalse (r.exists());
        assertFalse (r1.exists());
        assertFalse (r2.exists());
        assertFalse (rdata1.exists());
        assertFalse (rdata2.exists());
        
        deltree(f);
    }
    
    public void testRenameFilesFailsGracefully() throws Exception {
        System.out.println("testRenameFilesFailsGracefully");
        File f = new File (td.getPath() + File.separator + "renameTest" + System.currentTimeMillis());
        if (f.exists()) {
            deltree(f);
        }
        File f1 = new File (f, "subdir1");
        File f2 = new File (f1, "subdir2");
        File f3 = new File (f2, "subdir3");
        CacheImpl.createIntermediateDirs(f3);
        File data1 = new FakeFile (f3, "data1");
        File data2 = new FakeFile (f3, "data2");
        FileOutputStream fos1 = new FileOutputStream(data1);
        FileOutputStream fos2 = new FileOutputStream(data2);
        
        java.nio.channels.FileLock lock = fos1.getChannel().lock();
        java.nio.channels.FileLock lock2 = fos2.getChannel().lock();
        fos1.write ("one".getBytes());
        fos2.write ("two".getBytes());
        
        System.gc();
        assertTrue (data1.exists());
        assertTrue (data2.exists());
        
        File r = new File (td, "renamedDir" + System.currentTimeMillis());
        File r1 = new File (r, "renameSub1");        
        File r2 = new File (r1, "renameSub2");
        
        File rdata1 = new FakeFile (r2, "rdata1");
        File rdata2 = new FakeFile (r2, "rdata2");
        assertFalse (rdata2.exists());
        assertFalse (rdata1.exists());
        
        File[] orig = new File[] {data1, data2};
        File[] nue = new File[] {rdata1, rdata2};
        
        assertFalse(CacheImpl.renameFiles (orig, nue, false));
        
        for (int i=0; i < nue.length; i++) {
            assertFalse ("Failed rename - " + nue[i] + " should not exist", nue[i].exists());
        }
        
        lock.release();
        lock2.release();
        
        deltree(f);
    }    
    
    public void testFindFirstSharedParent() {
        System.out.println("testFindFirstSharedParent");
        File a = new File ("/hey/there/how/are/you");
        File b = new File ("/hey/there/I/Am/Fine/Arent/You");
        File c = CacheImpl.findFirstSharedParent(a, b);
        assertEquals (new File ("/hey/there"), c);
        
        a = new File ("/foo/goo/boo");
        b = new File ("/woo/noo/hoo");
        assertEquals (new File("/"), CacheImpl.findFirstSharedParent(a, b));
    }
    
/*    public void testSubcache() throws Exception {
        System.out.println("testSubcache");
        
        byte[] bytes = new byte [100];
        for (int i=0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.position (bytes.length);
        
        cache.add (buf);
        assertEquals (1, cache.size());
        
        Cache sub = cache.getSubCache(0, new SubParser());
        assertEquals (10, sub.size());
        for (int i=0; i < 10; i++) {
            ByteBuffer bb = sub.getBuffer(i);
            int predicted = i * 10;
            for (int j=0; j < 10; j++) {
                try {
                    assertEquals (j + predicted, bb.get());
                } catch (Exception e) {
                    System.err.println("EXCEPTION AT SUB-BUFFER " + i + " INDEX " + j);
                    throw e;
                }
            }
        }
    }
    
    private static final class SubParser extends Cache.CacheParser {
        public int indexOf (int idx) {
            return idx * 10;
        }
        
        public int size() {
            return 10;
        }
    }
 */
    
    private static final class FakeFile extends File {
        public FakeFile (File dir, String path) {
            super (dir, path);
        }
        
        public boolean renameTo (File f) {
            return false;
        }
    }
    
    String s1 = "This is the first entry";
    String s2 = "This is the second entry";
    String s3 = "This is another entry.  It is a bit\n longer than the others, just for \n fun.  Isn't that interesting?";
    String s4 = "And yet another entry.  What fun!";
    
    String[] strings = new String[] {
        s1, s2, s3, s4
    };
    
}
