package org.netbeans.collections.numeric;
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
 * LongListTest.java
 * JUnit based test
 *
 * Created on March 22, 2004, 12:24 AM
 */

import java.io.File;
import junit.framework.*;

/**
 *
 * @author tim
 */
public class LongListTest extends TestCase {

    public LongListTest(java.lang.String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(LongListTest.class);
        return suite;
    }

    /**
     * Test of add method, of class org.netbeans.core.output2.LongList.
    */
    public void testAdd() {
        System.out.println("testAdd");
        
        LongList il = new LongList (100);
        il.add (0);
        il.add (1);
        il.add (2);
        il.add (23);
        il.add (Integer.MAX_VALUE);
        
        assertTrue (il.get(0) == 0);
        assertTrue (il.get(1) == 1);
        assertTrue (il.get(2) == 2);
        assertTrue (il.get(3) == 23);
        assertTrue (il.get(4) == Integer.MAX_VALUE);
    }
    
    /**
     * Test of get method, of class org.netbeans.core.output2.LongList.
     */
    public void testGet() {
        System.out.println("testGet");
        
    }
    
    /**
     * Test of findNearest method, of class org.netbeans.core.output2.LongList.
     */
    public void testFindNearest() {
        System.out.println("testFindNearest");

        LongList il = new LongList (1000);
        
        for (int i=0; i < 100; i++) {
            il.add (i * 10);
        }
        
        int near475 = il.findNearest (475);
        assertTrue ("Nearest entry to 475 should be 470, not " + near475, 
            near475 == 47);
        
        int near470 = il.findNearest (470);
        assertTrue ("List contains an entry 470 at index 47, but returned " 
            + near470 + " as the index with the value closest to 470",
            near470 == 47);
        
        int near505 = il.findNearest (505);
        assertTrue ("Nearest entry to 505 should be 500, not " + near505, 
            near505 == 50);
        
        int near515 = il.findNearest (515);
        assertTrue ("Nearest entry to 515 should be 510, not " + near515, 
            near515 == 51);
        
        int near5 = il.findNearest (5);
        assertTrue ("Nearest entry to 5 should be 0, not " + near5, 
            near5 == 0);
        
        int near995 = il.findNearest (995);
        assertTrue ("Nearest entry to 995 should be 990, not " + near995, 
            near995 == 99);
        
        int near21000 = il.findNearest (21000);
        assertTrue ("Nearest entry to 21000 should be 990, not " + near21000, 
            near21000 == 99);
        
        int nearNeg475 = il.findNearest (-475);
        assertTrue ("Nearest entry to -475 should be 0 not " + nearNeg475, 
            nearNeg475 == 0);
    
    }
    
    /**
     * Test of indexOf method, of class org.netbeans.core.output2.LongList.
     */
    public void testIndexOf() {
        System.out.println("testIndexOf");
        
        LongList il = new LongList (1000);
        
        int[] vals = new int[] {
            1, 4, 23, 31, 47, 2350, 5727, 32323
        };
        
        for (int i=0; i < vals.length; i++) {
            il.add (vals[i]);
        }
        
        for (int i=0; i < vals.length; i++) {
            assertTrue (vals[i] + " was added at index " + i + " but found it" +
            "(or didn't find it) at index " + il.indexOf(vals[i]), 
            il.indexOf(vals[i]) == i);
        }
        
    }
    
    /**
     * Test of size method, of class org.netbeans.core.output2.LongList.
     */
    public void testSize() {
        System.out.println("testSize");
        LongList il = new LongList (1000);
        
        int[] vals = new int[] {
            1, 4, 23, 31, 47, 2350, 5727, 32323
        };
        
        for (int i=0; i < vals.length; i++) {
            il.add (vals[i]);
        }

        assertTrue (il.size() == vals.length);
    }
    
    public void testEqauls() {
        System.out.println("testEquals");
        LongList il = new LongList (1000);
        LongList il2 = new LongList (2000);
        for (int i=0; i < 20; i++) {
            il.add (i * 2);
            il2.add (i * 2);
        }
        
        assertEquals(il, il2);
        LongList il3 = new LongList (1000);
        for (int i=0; i < 20; i++) {
            il3.add (i);
        }
        assertFalse (il.equals(il3));
        
        LongList il4 = new LongList (1000);
        for (int i=0; i < 21; i++) {
            il4.add (i * 2);
        }
        assertFalse (il.equals(il4));
        
    }
    
    public void testHashCode() {
        System.out.println("testHashCode");
        LongList il = new LongList (1000);
        LongList il2 = new LongList (2000);
        for (int i=0; i < 20; i++) {
            il.add (i * 2);
            il2.add (i * 2);
        }
        assertEquals(il.hashCode(), il2.hashCode());

        LongList il3 = new LongList (1000);
        for (int i=0; i < 20; i++) {
            il3.add (i);
        }
        assertFalse (il.hashCode() == il3.hashCode());
        
        LongList il4 = new LongList (1000);
        for (int i=0; i < 21; i++) {
            il4.add (i * 2);
        }
        assertFalse (il.hashCode() == il3.hashCode());
    }
    
    public void testReadWrite() throws Exception {
        System.out.println("testReadWrite");
        LongList il = new LongList (1000);
        for (int i=0; i < 20; i++) {
            il.add (i * 2);
        }
        
        File tmpdir = new File (System.getProperty("java.io.tmpdir"));
        File store = new File (tmpdir, "LongListTest");
        
        il.save (store);
        LongList nue = new LongList(store);
        assertEquals (il, nue);
    }
    
    public void testDelete() throws Exception {
        System.out.println("testDelete");
        LongList il = new LongList (1000);
        for (int i=0; i < 20; i++) {
            il.add (i * 2);
        }
        
        assertEquals (20, il.size());
        assertTrue (il.contains(20));
        assertTrue (il.contains(0));
        il.delete (10);
        assertFalse (il.contains(20));
        assertEquals (19, il.size());
        
        il.delete (0);
        assertFalse (il.contains(0));
    }    
    
    public void testDeleteRange() throws Exception {
        System.out.println("testDeleteRange");
        LongList il = new LongList (1000);
        for (int i=0; i < 20; i++) {
            il.add (i * 2);
        }
        
        assertEquals (20, il.size());
        assertTrue (il.contains(20));
        assertTrue (il.contains(0));
        
        il.delete (10, 12);
        assertFalse (il.contains(20));
        assertFalse (il.contains(22));
        assertFalse (il.contains(24));

        int[] ix = new int[17];
        int pos = 0;
        for (int i=0; i < 20; i++) {
            if (i != 10 && i != 11 && i != 12) {
                ix[pos] = i * 2;
                pos++;
            }
        }
        
        long[] xx = il.toArray();
        assertEquals (ix.length, xx.length);
        for (int i=0; i < ix.length; i++) {
            assertEquals("At " + i + " should be " + ix[i] + " not " + xx[i], ix[i], xx[i]);
        }
    }
   
}
