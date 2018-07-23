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


public class StorageTest extends MDRTestCase {

    public static long RAND_VAL = 2123;
    
    public static Random random;
    
    public StorageTest(String testName) {
        super (testName);
    }
    
    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite ());
    }
    
    public static Test suite () {
        TestSuite suite = new TestSuite ();
        suite.addTestSuite (StorageTest.class);
        
        TestSetup setup = new TestSetup (suite) {
            public void setUp () {
            }
            public void tearDown () {
            }
        };        
        return setup;        
    }

    protected void setUp () {
    }
    
    // **************************************************************************
    
    public void test() {
        StorageFactory factory;
        Storage storage;
        try {
            // btree storage, singlevalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("btree storage, singlevalued index");
            getLog().println("-------------------------------------------------");
            factory = new BtreeFactory();
            storage = factory.createStorage(new HashMap());
            storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doSingleTest(storage, "btree", "test1");
            
            System.out.println("single test done");
            
            // memory storage, singlevalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("memory storage, singlevalued index");
            getLog().println("-------------------------------------------------");
            factory = new StorageFactoryImpl ();
            Storage memStorage = factory.createStorage(new HashMap());
            memStorage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doSingleTest(memStorage, "memory", "test2");
            memStorage.close();
            
            System.out.println("single test (memory) done");

            // btree storage, multivalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("btree storage, multivalued test");
            getLog().println("-------------------------------------------------");
            factory = new BtreeFactory();
            // storage = factory.createStorage(new HashMap());
            // storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doMultiTest(storage, "btree", "test3");

            System.out.println("multivalued test done");
            
            // btree storage, several indexes
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("btree storage, several indexes");
            getLog().println("-------------------------------------------------");
            factory = new BtreeFactory();
            // storage = factory.createStorage(new HashMap());
            // storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doSeveralIndexesTest(storage, "btree", "test4");
            
            System.out.println("several indexes test done");
            
            // btree storage, primary index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("btree storage, primary index");
            getLog().println("-------------------------------------------------");
            factory = new BtreeFactory();
            // storage = factory.createStorage(new HashMap());
            // storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doPrimaryIndexTest(storage, "btree", "test5");
            
            System.out.println("primary index test done");
            
            storage.close();
        } catch (Exception e) {
            e.printStackTrace ();
            fail (e.getMessage ());
        }
    }
    
    public void doSingleTest(Storage storage, String info, String prefix) throws StorageException {
        final int KEYS_NUM = 10000;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 1000000;
        
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        SinglevaluedIndex index = storage.createSinglevaluedIndex(prefix + "singleIndex", entryType, entryType);
        MOFID[] keys = new MOFID[KEYS_NUM];
        MOFID[] values = new MOFID[VALUES_NUM];
        for (int x = 0; x < KEYS_NUM; x++) {
            keys[x] = generateMOFID();
        }
        for (int x = 0; x < VALUES_NUM; x++) {
            values[x] = generateMOFID();
        }
        
        long time = System.currentTimeMillis();
        long totalTime = 0;
        long insertions = 0;
        long deletions = 0;
        
        for (int x = 0; x < KEYS_NUM; x++) {
            MOFID value = values[random.nextInt(VALUES_NUM)];
            index.put(keys[x], value);
        }
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println("initial insertions time: " + totalTime);        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
        time = System.currentTimeMillis();
        
        for (long x = 0; x < OPS_NUM; x++) {
            MOFID key = keys[random.nextInt(KEYS_NUM)];
            MOFID value = values[random.nextInt(VALUES_NUM)];
            Object val = index.getIfExists(key);
            if (val == null) {
                insertions++;
                index.put(key, value);
            } else {
                deletions++;
                index.remove(key);
            }
        }
        totalTime += System.currentTimeMillis() - time;        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
        time = System.currentTimeMillis();
        // storage.close();
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
        getLog().println("#insertions: " + insertions);
        getLog().println("#deletions: " + deletions);
    }
    
    public void doMultiTest(Storage storage, String info, String prefix) throws StorageException {
        final int KEYS_NUM = 10000;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 1000000;
        
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        MultivaluedIndex index = storage.createMultivaluedIndex(prefix + "multiIndex", entryType, entryType, false);
        MOFID[] keys = new MOFID[KEYS_NUM];
        MOFID[] values = new MOFID[VALUES_NUM];
        for (int x = 0; x < KEYS_NUM; x++) {
            keys[x] = generateMOFID();
        }
        for (int x = 0; x < VALUES_NUM; x++) {
            values[x] = generateMOFID();
        }
        
        long time = System.currentTimeMillis();
        long totalTime = 0;
        long insertions = 0;
        long deletions = 0;
        
        for (long x = 0; x < OPS_NUM; x++) {
            MOFID key = keys[random.nextInt(KEYS_NUM)];
            MOFID value = values[random.nextInt(VALUES_NUM)];
            List list = (List)index.getItems(key);
            boolean flag = random.nextBoolean();
            int size = list.size();
            if (size == 0 || flag) {
                list.add(value);
                insertions++;
            } else {
                list.remove(random.nextInt(size));
                deletions++;
            }
        }
        totalTime += System.currentTimeMillis() - time;        
        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
        time = System.currentTimeMillis();
        // storage.close();
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
        getLog().println("#insertions: " + insertions);
        getLog().println("#deletions: " + deletions);
    }

    public void doSeveralIndexesTest(Storage storage, String info, String prefix) throws StorageException {
        final int KEYS_NUM = 10000;
        final int VALUES_NUM = 1000;
        final long OPS_NUM = 500000;
        final int INDEXES_NUM = 50;
        
        MOFID[] keys = new MOFID[KEYS_NUM];
        MOFID[] values = new MOFID[VALUES_NUM];
        for (int x = 0; x < KEYS_NUM; x++) {
            keys[x] = generateMOFID();
        }
        for (int x = 0; x < VALUES_NUM; x++) {
            values[x] = generateMOFID();
        }

        Index[] indexes = new Index[INDEXES_NUM];
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        for (int x = 0; x < INDEXES_NUM; x++) {
            if (random.nextBoolean()) {
                // singlevalued index
                indexes[x] = storage.createSinglevaluedIndex(prefix + "index" + x, entryType, entryType);
            } else {
                // multivalued index
                boolean unique = random.nextBoolean();
                boolean ordered = random.nextBoolean();
                if (ordered) {
                    indexes[x] = storage.createMultivaluedOrderedIndex(prefix + "index" + x, entryType, entryType, unique);
                } else {
                    indexes[x] = storage.createMultivaluedIndex(prefix + "index" + x, entryType, entryType, unique);
                }
            }
        } // for

        long time = System.currentTimeMillis();
        long totalTime = 0;
        long insertions = 0;
        long deletions = 0;
        
        for (long x = 0; x < OPS_NUM; x++) {
            MOFID key = keys[random.nextInt(KEYS_NUM)];
            MOFID value = values[random.nextInt(VALUES_NUM)];
            Index index = indexes[random.nextInt(INDEXES_NUM)];
            if (index instanceof MultivaluedIndex) {
                boolean remove = random.nextBoolean();
                MultivaluedIndex multi = (MultivaluedIndex)index;
                List list; 
                if (multi instanceof MultivaluedOrderedIndex) {
                    list = (List)multi.getItems(key);
                } else {
                    list = (List)multi.getItems(key);
                }
                boolean contained = list.contains(value);
                int size = list.size();
                if ((size > 0 && remove) || (multi.isUnique() && contained)) {
                    list.remove(random.nextInt(size));
                    deletions++;
                } else {
                    list.add(random.nextInt(size + 1), value);
                    insertions++;
                }
            } else {
                SinglevaluedIndex single = (SinglevaluedIndex)index;
                if (single.getIfExists(key) != null) {
                    single.remove(key);
                    deletions++;
                } else {
                    single.add(key, value);
                    insertions++;
                }
            }
        }
        // storage.close();
        totalTime = System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
        getLog().println("#insertions: " + insertions);
        getLog().println("#deletions: " + deletions);
    }
    
    public void doPrimaryIndexTest(Storage storage, String info, String prefix) throws StorageException {
        final int ITEMS_NUM = 8000;
        final long OPS_NUM = 500000;
        
        SinglevaluedIndex index = storage.getPrimaryIndex();
        MOFID[] keys = new MOFID[ITEMS_NUM];
        PrimaryItem[] values = new PrimaryItem[ITEMS_NUM];
        for (int x = 0; x < ITEMS_NUM; x++) {
            keys[x] = generateMOFID();
            values[x] = new PrimaryItem();
        }
        
        long time = System.currentTimeMillis();
        long totalTime = 0;
        long insertions = 0;
        long deletions = 0;
        
        for (long x = 0; x < OPS_NUM; x++) {
            int pos = random.nextInt(ITEMS_NUM);
            MOFID key = keys[pos];
            PrimaryItem value = values[pos];
            Object val = index.getIfExists(key);
            if (val == null) {
                insertions++;
                index.put(key, value);
            } else {
                deletions++;
                index.remove(key);
            }
        }
        totalTime = System.currentTimeMillis() - time;        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print(getLog());
        }
        time = System.currentTimeMillis();
        // storage.close();
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
        getLog().println("#insertions: " + insertions);
        getLog().println("#deletions: " + deletions);
    }
    
    /*
    public void doTest3(Storage storage, String info) throws StorageException {
        final int KEYS_NUM = 100;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 100000;
        
        Storage.EntryType entryType = Storage.EntryType.STRING;
        MultivaluedIndex index = storage.createMultivaluedIndex("multiIndex", entryType, entryType, false);
        String[] keys = new String[KEYS_NUM];
        String[] values = new String[VALUES_NUM];
        for (int x = 0; x < KEYS_NUM; x++) {
            keys[x] = generateString(60);
        }
        for (int x = 0; x < VALUES_NUM; x++) {
            values[x] = generateString(200);
        }
        
        long time = System.currentTimeMillis();
        long totalTime = 0;
        for (long x = 0; x < OPS_NUM / 10; x++) {
            index.add(keys[random.nextInt(KEYS_NUM)], values[random.nextInt(VALUES_NUM)]);
        }
        for (long x = 0; x < OPS_NUM; x++) {
            String key = keys[random.nextInt(KEYS_NUM)];
            String value = values[random.nextInt(VALUES_NUM)];
            List coll = (List)index.getItems(key);
            int size = coll.size();
            if (size == 0) {
                coll.add(value);
            } else {
                coll.remove(random.nextInt(size));
            }
        }
        totalTime += System.currentTimeMillis() - time;
        time = System.currentTimeMillis();
        
        if (index instanceof Btree) {
            TreeMetrics m = ((Btree) index).computeMetrics();
            m.print();
        }
        // storage.close();
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
    }
    
    public void doTest2(Storage storage, String info) throws StorageException {
        final int KEYS_NUM = 100;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 150000;
        
        Storage.EntryType entryType = Storage.EntryType.STRING;
        MultivaluedIndex index = storage.createMultivaluedIndex("multiIndex", entryType, entryType, false);
        String[] keys = new String[KEYS_NUM];
        String[] values = new String[VALUES_NUM];
        for (int x = 0; x < KEYS_NUM; x++) {
            keys[x] = generateString(60);
        }
        for (int x = 0; x < VALUES_NUM; x++) {
            values[x] = generateString(200);
        }
        
        long time = System.currentTimeMillis();
        for (long x = 0; x < OPS_NUM / 10; x++) {
            index.add(keys[random.nextInt(KEYS_NUM)], values[random.nextInt(VALUES_NUM)]);
        }
        for (long x = 0; x < OPS_NUM; x++) {
            String key = keys[random.nextInt(KEYS_NUM)];
            String value = values[random.nextInt(VALUES_NUM)];
            List coll = (List)index.getItems(key);
            int size = coll.size();
            if (size == 0) {
                coll.add(value);
            } else {
                coll.remove(random.nextInt(size));
            }
        }
        // storage.close();
        getLog().println(info + ", test time: " + (System.currentTimeMillis() - time));
    }
     */
    
    public String generateString(int maxLength) {
        return randomString("", 10, Math.max(10, maxLength));
    }
    
    public MOFID generateMOFID() {
        long serialNumber = Math.abs(random.nextLong());
        String storageId = randomString("", 16, 16);
        return new MOFID(serialNumber, storageId);
    }
    
    public String randomString (String prefix) {
        final int minLength = 10;
        final int maxLength = 20;
        return randomString (prefix, minLength, maxLength);
    }
    
    public String randomString (String prefix, int minLength, int maxLength) {        
        String res = "";
        int length = Math.max (minLength, random.nextInt (maxLength + 1));
        for (int x = prefix.length (); x <= length; x++) {
            res = res + (char) (random.nextInt ('z' - 'a' + 1) + 'a');
        }
        return prefix + res;
    }
    
    // ..........................................................................
    
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

