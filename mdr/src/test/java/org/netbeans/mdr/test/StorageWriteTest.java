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
package org.netbeans.mdr.test;

import java.io.IOException;
import java.util.*;
import junit.extensions.*;
import junit.framework.*;
import org.netbeans.mdr.persistence.*;
import org.netbeans.mdr.persistence.MOFID;
import org.netbeans.mdr.persistence.btreeimpl.btreeindex.Btree;
import org.netbeans.mdr.persistence.btreeimpl.btreeindex.TreeMetrics;
import org.netbeans.mdr.persistence.btreeimpl.btreestorage.*;
import org.netbeans.mdr.persistence.memoryimpl.*;


public class StorageWriteTest extends MDRTestCase {

    static final int ROWS = 50000;
    static long RAND_VAL = 666; // satan's number
    
    static Random random = new Random(RAND_VAL);
    
    private Set keys;
    private Set values;

    public StorageWriteTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(StorageWriteTest.class);

        TestSetup setup = new TestSetup(suite) {
            
            public void setUp() {}
            
            public void tearDown() {}
        };        
        return setup;        
    }
    
    protected void setUp() {
        // generate keys/values pairs
        keys = new HashSet(ROWS);
        values = new HashSet(ROWS);

        long time = System.currentTimeMillis();
        for (int x = 0; x < ROWS; ) {
            MOFID key = generateMOFID();
            if (keys.add(key))
                x++;
        }
        for (int x = 0; x < ROWS; ) {
           MOFID value = generateMOFID();
            if (values.add(value))
                x++;
        }
        long generatorTime = System.currentTimeMillis() - time;
        getLog().println("Keys and values pair generator takes " + generatorTime + " ms.");
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // tests
    public void testSequentialWrite() {
        StorageFactory factory;
        Storage storage;
        for (int i = 0; i < 5; i++) {
            try {
                factory = new BtreeFactory();
                storage = factory.createStorage(new HashMap());
                storage.create (true, new Resolver());
                sequentialWrite(storage, "btree", "seqWrite" + i);
                storage.close();
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }
    
    public void testSequentialRemove() {
        StorageFactory factory;
        Storage storage;
        for (int i = 0; i < 5; i++) {
            try {
                factory = new BtreeFactory();
                storage = factory.createStorage(new HashMap());
                storage.create (true, new Resolver());
                random = new Random(RAND_VAL);
                sequentialWrite(storage, "btree", "seqRemove" + i);
                sequentialRemove(storage, "btree", "seqRemove" + i);
                storage.close();
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }
    // end tests
    ////////////////////////////////////////////////////////////////////////////
    
    private void sequentialWrite(Storage storage, String info, String prefix) throws StorageException {
        // index
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        SinglevaluedIndex index = storage.createSinglevaluedIndex(prefix + "singleIndex", entryType, entryType);
        // insertations
        Iterator k = keys.iterator();
        Iterator v = values.iterator();
        long time = System.currentTimeMillis();
        for (int x = 0; x < ROWS; x++) {
            index.put(k.next(), v.next());
        }
        long insertionsTime = System.currentTimeMillis() - time;
        getLog().println("Insertions time: " + insertionsTime);
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
    }
    
    private void sequentialRemove(Storage storage, String info, String prefix) throws StorageException {
        // index
        SinglevaluedIndex index = storage.getSinglevaluedIndex(prefix + "singleIndex");
        // deletions
        Iterator k = keys.iterator();
        long time = System.currentTimeMillis();
        for (int x = 0; x < ROWS; x++) {
            index.remove(k.next());
        }
        long deletionsTime = System.currentTimeMillis() - time;
        getLog().println("Deletions time: " + deletionsTime);        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
    }
    
    public static String generateString(int maxLength) {
        return randomString("", 10, Math.max(10, maxLength));
    }
    
    public static MOFID generateMOFID() {
        long serialNumber = Math.abs(random.nextLong());
        String storageId = randomString("", 16, 16);
        return new MOFID(serialNumber, storageId);
    }
    
    public static String randomString(String prefix) {
        final int minLength = 10;
        final int maxLength = 20;
        return randomString (prefix, minLength, maxLength);
    }
    
    public static String randomString(String prefix, int minLength, int maxLength) {        
        String res = "";
        int length = Math.max (minLength, random.nextInt (maxLength + 1));
        for (int x = prefix.length (); x <= length; x++) {
            res = res + (char) (random.nextInt ('z' - 'a' + 1) + 'a');
        }
        return prefix + res;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    // ..........................................................................
    // INNER CLASSES
    private class Resolver implements ObjectResolver {
        public Object resolve(String storageID, Object key) {
            getLog().println("resolve object called");
            return new Object();
        }
    }
    
    private static class PrimaryItem implements Streamable {
        
        private byte[] data;
        
        PrimaryItem() {
            int length = StorageTest.random.nextInt(256);
            data = new byte[length];
            for (int x = 0; x < length; x++) {
                data[x] = (byte)StorageTest.random.nextInt(256);
            }
        }
        
        public void read(java.io.InputStream is) throws StorageException {
            try {
                int length = is.read();
                data = new byte[length];
                for (int x = 0; x < length; x++) {
                    data[x] = (byte)is.read();
                }
            } catch (IOException e) {
                throw new StorageIOException(e);
            }
        }

        public void write(java.io.OutputStream os) throws StorageException {
            try {
                os.write(data.length);
                for (int x = 0; x < data.length; x++) {
                    os.write(data[x]);
                }
            } catch (IOException e) {
                throw new StorageIOException(e);
            }
        }
        
    }
}
