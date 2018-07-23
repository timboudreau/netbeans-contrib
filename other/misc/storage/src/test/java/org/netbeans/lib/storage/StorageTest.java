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
 * OutputDocumentTest.java
 * JUnit based test
 *
 * Created on March 23, 2004, 5:34 PM
 */

package org.netbeans.lib.storage;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import junit.framework.*;
import org.netbeans.api.storage.Storage;

/**
 *
 * @author tim
 */
public class StorageTest extends TestCase {
    
    public StorageTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(StorageTest.class);
        return suite;
    }

    Storage filemap = null;
    Storage heap = null;
    protected void setUp() throws java.lang.Exception {
        filemap = new FileMapStorage();
        heap = new HeapStorage();
    }
    
    protected void tearDown() throws Exception {
        filemap.dispose();
        heap.dispose();
    }
    
    public void testIsClosed() throws Exception {
        doTestIsClosed(heap);
        doTestIsClosed(filemap);
    }
    
    private void doTestIsClosed (Storage storage) throws Exception {
        System.out.println("testIsClosed - " + storage.getClass());
        assertTrue (storage.isClosed());
        
        String test = "Hello world";
        storage.write(ByteBuffer.wrap(test.getBytes()));
        
        assertFalse (storage.isClosed());
        
        storage.close();
        assertTrue (storage.isClosed());
        
        write (storage, test);
        assertFalse (storage.isClosed());
        
        storage.close();
        assertTrue (storage.isClosed());
    }
    
    private int toByteIndex (int i) {
        return i*2;
    }
    
    private int write (Storage storage, String s) throws Exception {
        ByteBuffer buf = storage.getWriteBuffer(toByteIndex(s.length()));
        buf.asCharBuffer().put(s);
        buf.position (buf.position() + toByteIndex(s.length()));
        int result = storage.write(buf);
        storage.flush();
        return result;
    }
    
    public void testWrites () throws Exception {
        System.out.println("testWrites");
        
        ByteBuffer bb1 = ByteBuffer.wrap (data);
        ByteBuffer bb2 = ByteBuffer.wrap (data);
        bb1.position (data.length);
        bb2.position (data.length);
        heap.write (bb1);
        filemap.write (bb2);

        assertStoragesIdentical();
        
        ByteBuffer f = filemap.getReadBuffer(0, filemap.size());
        ByteBuffer h = heap.getReadBuffer(0, heap.size());
        for (int i=0; i < data.length; i++) {
            assertEquals (data[i], f.get());
            assertEquals (data[i], h.get());
        }
        
    }
    
    public void testIdenticalBehaviors() throws Exception {
        System.out.println("testIdenticalBehaviors");
        String[] s = new String[10];
        String a = "abcd";
        String b = a;
        for (int i=0; i < s.length; i++) {
            s[i] = b;
            b += a;
            int hwrite = write (heap, s[i]);
            int fwrite = write (filemap, s[i]);
            assertEquals (hwrite, fwrite);
            assertEquals(heap.isClosed(), filemap.isClosed());
            assertEquals(heap.size(), filemap.size());
            ByteBuffer hbuf = heap.getReadBuffer(hwrite, heap.size() - hwrite);
            ByteBuffer fbuf = filemap.getReadBuffer(hwrite, filemap.size() - fwrite);
        }
    }
    
    public void testFileMapStorageCanBeAsLargeAsIntegerMaxValue() {
        System.out.println("testFileMapStorageCanBeAsLargeAsIntegerMaxValue - THIS TEST WILL CREATE A 2 GIGABYTE TEMP FILE!!!!");
        if (true) {
            System.out.println("   Wisely skipping this test");
            return;
        }
        char[] c = new char[16384];
        Arrays.fill (c, 'a');
        String s = new String(c);
        try {
            while (filemap.size() < Integer.MAX_VALUE) {
                 write (filemap, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail ("Could not create a large file - " + e.getMessage());
        }
    }
    
    public void testExcise() throws Exception {
        System.out.println("testExcise");
        byte[] b = new byte[256];
        for (byte i=-128; i < 127; i++) {
            b[i + 128] = i;
        }
        
        for (int i=0; i < 9; i++) {
            b[0] = (byte) i;
            
            ByteBuffer wb = heap.getWriteBuffer(b.length);
            wb.put (b);
            heap.write (wb);
            
            wb = filemap.getWriteBuffer(b.length);
            wb.put(b);
            filemap.write(wb);
            assertEquals (filemap.size(), heap.size());
        }
        
        assertStoragesIdentical();
        
        int sz = heap.size();
        
        long start = 512;
        long end = 1024;
        
        filemap.excise (start, end);
        heap.excise (start, end);
        
        assertEquals (sz - 512, filemap.size());
        assertEquals (sz - 512, heap.size());
        
        assertStoragesIdentical();
        
        ByteBuffer f = filemap.getReadBuffer(0, filemap.size());
        ByteBuffer h = heap.getReadBuffer(0, heap.size());
        for (int i=0; i < data.length; i++) {
            assertEquals (data[i], f.get());
            assertEquals (data[i], h.get());
        }
    }
    
    public void testReplace() throws Exception {
        System.out.println("testReplace");
        
        String[] s = new String[] {
            "one ", "two ", "three ", "four ", "five ", "six ", "seven ",
            "eight ", "nine ", "ten ", "eleven ", "12 ", "thirteen ", "go away",
            "Four score and seven years ago something happened"
        };
        
        ArrayList hsizes = new ArrayList (s.length);
        ArrayList fsizes = new ArrayList (s.length);
        for (int i=0; i < s.length; i++) {
            ByteBuffer f = filemap.getWriteBuffer (s[i].length() * 2);
            f.asCharBuffer().put(s[i].toCharArray());
            f.position (s[i].length() * 2);
            filemap.write(f);
            ByteBuffer h = heap.getWriteBuffer (s[i].length() * 2);
            h.asCharBuffer().put(s[i].toCharArray());
            h.position (s[i].length() * 2);
            heap.write(h);
            assertStoragesIdentical();
            hsizes.add (new Long(heap.size()));
            fsizes.add (new Long(filemap.size()));
        }
        
        assertEquals (fsizes, hsizes);
        
        long itemOneStart = ((Long) fsizes.get(1)).longValue();
        long itemOneEnd = ((Long) fsizes.get(2)).longValue();
        
        String nue = "This is the replacement";
        ByteBuffer bb = ByteBuffer.allocate (nue.length() * 2);
        CharBuffer cb = bb.asCharBuffer();
        cb.put (nue.toCharArray());
        bb.position (nue.toCharArray().length * 2);
        ByteBuffer bb2 = bb.duplicate();
        bb2.position (nue.toCharArray().length * 2);
        
        long hs = heap.size();
        long fs = filemap.size();
        
        heap.replace (bb, itemOneStart, itemOneEnd);
        filemap.replace (bb2, itemOneStart, itemOneEnd);
        
        assertFalse ("Heap size should have changed", hs == heap.size());
        assertFalse ("File map size should have changed", fs == filemap.size());
        
        assertStoragesIdentical();
    }
    
    public void testTruncate() throws Exception {
        System.out.println("testTruncate");
        File td = new File (System.getProperty("java.io.tmpdir"));
        File ff = new File (td, "trunctest");
        filemap = new FileMapStorage (ff);
        
        String[] s = new String[] {
            "one ", "two ", "three ", "four ", "five ", "six ", "seven ",
            "eight ", "nine ", "ten ", "eleven ", "12 ", "thirteen ", "go away",
            "Four score and seven years ago something happened"
        };
        
        ArrayList hsizes = new ArrayList (s.length);
        ArrayList fsizes = new ArrayList (s.length);
        for (int i=0; i < s.length; i++) {
            ByteBuffer f = filemap.getWriteBuffer (s[i].length() * 2);
            f.asCharBuffer().put(s[i].toCharArray());
            f.position (s[i].length() * 2);
            filemap.write(f);
            ByteBuffer h = heap.getWriteBuffer (s[i].length() * 2);
            h.asCharBuffer().put(s[i].toCharArray());
            h.position (s[i].length() * 2);
            heap.write(h);
            assertStoragesIdentical();
            hsizes.add (new Long(heap.size()));
            fsizes.add (new Long(filemap.size()));
        }
        
        int sz = heap.size();
        heap.truncate (40);
        filemap.truncate(40);
        
        assertEquals (40, heap.size());
        assertEquals (40, filemap.size());
        
        filemap.close();
        
        assertTrue (ff.exists());
        assertEquals (40, ff.length());
        
        Exception e = null;
        try {
            filemap.truncate (-100);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull ("Exception should have been thrown");

        e = null;
        try {
            heap.truncate (-100);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull ("Exception should have been thrown");

        e = null;
        try {
            heap.truncate (200);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull ("Exception should have been thrown");
        
        e = null;
        try {
            filemap.truncate (200);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull ("Exception should have been thrown");
        
        ff.delete();
    }
    
    
    public void testHeapStorageFromFile() throws Exception {
        System.out.println("testHeapStorageFromFile");
        File td = new File (System.getProperty("java.io.tmpdir"));
        File f = new File (td, "hstest");
        filemap = new FileMapStorage (f);
        ByteBuffer buf = filemap.getWriteBuffer(20000 * 4);
        IntBuffer ib = buf.asIntBuffer();
        for (int i=0; i < 20000; i++) {
            ib.put (i);
        }
        filemap.write(buf);
        filemap.flush();
        
        heap = new HeapStorage(f);
        
        assertStoragesIdentical();
        
        heap.close();
        filemap.close();
        filemap.dispose();
        if (f.exists()) {
            f.delete();
        }
    }
    
    private void assertStoragesIdentical() throws Exception {
        assertEquals (filemap.size(), heap.size());
        ByteBuffer f = filemap.getReadBuffer (0, filemap.size());
        ByteBuffer h = heap.getReadBuffer (0, heap.size());
        
        for (long l=0; l < filemap.size(); l++) {
            byte hh = h.get();
            byte ff = f.get();
            
            assertEquals ("Storages differ at " + l + ": heap:" + hh + " filemap: " + ff + 
                " \nheapContents: " + heap.getReadBuffer(0, heap.size()).asCharBuffer() 
                + " \nfilemapContents: " + filemap.getReadBuffer(0, filemap.size()).asCharBuffer(), 
                    hh, ff);
        }
    }
    
    private static final byte[] data = new byte[] {
        0,
        -127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,-111,-110,-109,-108,
        -107,-106,-105,-104,-103,-102,-101,-100,-99,-98,-97,-96,-95,-94,-93,-92,-91,-90,-89,-88,
        -87,-86,-85,-84,-83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,
        -67,-66,-65,-64,-63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,
        -47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,
        -27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,
        -7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,
        13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,
        33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,
        53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,
        73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,
        93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,
        113,114,115,116,117,118,119,120,121,122,123,124,125,126,0,1,-127,-126,-125,-124,
        -123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,-111,-110,-109,-108,-107,-106,-105,-104,
        -103,-102,-101,-100,-99,-98,-97,-96,-95,-94,-93,-92,-91,-90,-89,-88,-87,-86,-85,-84,
        -83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-64,
        -63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,-47,-46,-45,-44,
        -43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,-27,-26,-25,-24,
        -23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,
        -3,-2,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,
        17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,
        37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,
        57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,
        77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,
        97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,
        117,118,119,120,121,122,123,124,125,126,0,4,-127,-126,-125,-124,-123,-122,-121,-120,
        -119,-118,-117,-116,-115,-114,-113,-112,-111,-110,-109,-108,-107,-106,-105,-104,-103,-102,-101,-100,
        -99,-98,-97,-96,-95,-94,-93,-92,-91,-90,-89,-88,-87,-86,-85,-84,-83,-82,-81,-80,
        -79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-64,-63,-62,-61,-60,
        -59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,-47,-46,-45,-44,-43,-42,-41,-40,
        -39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-21,-20,
        -19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0,
        1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
        21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
        41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,
        61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,
        81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,
        101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,
        121,122,123,124,125,126,0,5,-127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-117,-116,
        -115,-114,-113,-112,-111,-110,-109,-108,-107,-106,-105,-104,-103,-102,-101,-100,-99,-98,-97,-96,
        -95,-94,-93,-92,-91,-90,-89,-88,-87,-86,-85,-84,-83,-82,-81,-80,-79,-78,-77,-76,
        -75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-64,-63,-62,-61,-60,-59,-58,-57,-56,
        -55,-54,-53,-52,-51,-50,-49,-48,-47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,
        -35,-34,-33,-32,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,
        -15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,
        5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,
        25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,
        45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,
        65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,
        85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,
        105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,
        125,126,0,6,-127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,
        -111,-110,-109,-108,-107,-106,-105,-104,-103,-102,-101,-100,-99,-98,-97,-96,-95,-94,-93,-92,
        -91,-90,-89,-88,-87,-86,-85,-84,-83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,
        -71,-70,-69,-68,-67,-66,-65,-64,-63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,
        -51,-50,-49,-48,-47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,
        -31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,
        -11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,
        9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,
        29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,
        49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,
        69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
        89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,
        109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,0,7,
        -127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,-111,-110,-109,-108,
        -107,-106,-105,-104,-103,-102,-101,-100,-99,-98,-97,-96,-95,-94,-93,-92,-91,-90,-89,-88,
        -87,-86,-85,-84,-83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,
        -67,-66,-65,-64,-63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,
        -47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,
        -27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,
        -7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,
        13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,
        33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,
        53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,
        73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,
        93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,
        113,114,115,116,117,118,119,120,121,122,123,124,125,126,0,8,-127,-126,-125,-124,
        -123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,-111,-110,-109,-108,-107,-106,-105,-104,
        -103,-102,-101,-100,-99,-98,-97,-96,-95,-94,-93,-92,-91,-90,-89,-88,-87,-86,-85,-84,
        -83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-64,
        -63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,-47,-46,-45,-44,
        -43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,-31,-30,-29,-28,-27,-26,-25,-24,
        -23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,
        -3,-2,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,
        17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,
        37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,
        57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,
        77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,
        97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,
        117,118,119,120,121,122,123,124,125,126,0,        
    };
    
}
