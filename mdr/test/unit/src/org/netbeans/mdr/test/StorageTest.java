/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.mdr.test;

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
            doSingleTest(storage, "btree");
            
            // memory storage, singlevalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("memory storage, singlevalued index");
            getLog().println("-------------------------------------------------");
            factory = new StorageFactoryImpl ();
            storage = factory.createStorage(new HashMap());
            storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doSingleTest(storage, "memory");
            
            // btree storage, multivalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("btree storage, multivalued test");
            getLog().println("-------------------------------------------------");
            factory = new BtreeFactory();
            storage = factory.createStorage(new HashMap());
            storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doMultiTest(storage, "btree");
            
            /*
            // memory storage, multivalued index
            getLog().println();
            getLog().println("*************************************************");
            getLog().println("memory storage, multivalued test");
            getLog().println("-------------------------------------------------");
            factory = new StorageFactoryImpl ();
            storage = factory.createStorage(new HashMap());
            storage.create (true, new Resolver());
            random = new Random(RAND_VAL);
            doMultiTest(storage, "memory");
             */
        } catch (Exception e) {
            e.printStackTrace ();
            fail (e.getMessage ());
        }
    }
    
    public void doSingleTest(Storage storage, String info) throws StorageException {
        final int KEYS_NUM = 10000;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 1000000;
        
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        SinglevaluedIndex index = storage.createSinglevaluedIndex("singleIndex", entryType, entryType);
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
            m.print();
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
            m.print();
        }
        time = System.currentTimeMillis();
        storage.close();
        
        totalTime += System.currentTimeMillis() - time;
        getLog().println();
        getLog().println(info + ", test time: " + totalTime);
        getLog().println("#insertions: " + insertions);
        getLog().println("#deletions: " + deletions);
    }
    
    public void doMultiTest(Storage storage, String info) throws StorageException {
        final int KEYS_NUM = 10000;
        final int VALUES_NUM = 2000;
        final long OPS_NUM = 1000000;
        
        Storage.EntryType entryType = Storage.EntryType.MOFID;
        MultivaluedIndex index = storage.createMultivaluedIndex("multiIndex", entryType, entryType, false);
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
            m.print();
        }
        time = System.currentTimeMillis();
        storage.close();
        
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
        storage.close();
        
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
        storage.close();
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
    
}

