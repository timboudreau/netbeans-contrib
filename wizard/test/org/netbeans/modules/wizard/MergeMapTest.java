/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * MergeMapTest.java
 * JUnit based test
 *
 * Created on March 4, 2005, 1:26 PM
 */

package org.netbeans.modules.wizard;

import java.util.Arrays;
import junit.framework.*;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author tim
 */
public class MergeMapTest extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite(MergeMapTest.class);
        
        return suite;
    }

    MergeMap map = null;
    
    public MergeMapTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        map = new MergeMap ("a");
    }

    /**
     * Test of isEmpty method, of class org.netbeans.modules.wizard.MergeMap.
     */
    public void testFunctionality() {
        System.out.println("testFunctionality");
        assertTrue (map.isEmpty());
        map.put ("key", "val");
        assertFalse (map.isEmpty());
        assertEquals ("val", map.remove ("key"));
        assertNull (map.get("key"));
        assertTrue (map.isEmpty());
        map.put ("key", "val");
        assertFalse (map.isEmpty());
        
        map.push("next");
        assertFalse (map.isEmpty());
        assertEquals ("val", map.get("key"));
        map.put ("key1", "val1");
        assertFalse (map.isEmpty());
        assertEquals (2, map.size());
        assertEquals("next", map.popAndCalve());
        assertEquals (1, map.size());
        assertNull (map.get("key1"));
        
        map.push ("next");
        assertEquals (2, map.size());
        assertEquals ("val1", map.get("key1"));
        assertFalse (map.isEmpty());
        
        map.push ("farther");
        assertEquals (2, map.size());
        assertEquals ("val", map.get("key"));
        assertEquals ("val1", map.get("key1"));
        assertNull (map.get("key2"));
        assertFalse (map.isEmpty());
        
        map.put ("key2", "val2");
        assertEquals (3, map.size());
        assertEquals ("farther", map.popAndCalve());
        assertEquals (2, map.size());
        assertEquals ("next", map.popAndCalve());
        assertEquals (1, map.size());
        
        Set s = map.keySet();
        HashSet hs = new HashSet (Arrays.asList (new String[] { "key" }));
        assertEquals (s, hs);
        
        map.push ("farther");
        assertEquals (2, map.size());
        assertEquals ("val2", map.get("key2"));
        assertNull (map.get("key1"));
        map.push ("next");
        assertEquals ("val1", map.get("key1"));
        assertEquals (3, map.size());
        
        hs = new HashSet (Arrays.asList (new String[] { "key", "key1", "key2" }));
        assertEquals (map.keySet(), hs);
        
        map.put ("key", "other");
        assertEquals (3, map.size());
        assertEquals ("other", map.get("key"));
        assertEquals ("other", map.remove ("key"));
        assertNull (map.get("key"));
        assertEquals (2, map.size());
        
        try {
           map.clear(); 
           fail ("Exception should have been thrown");
        } catch (UnsupportedOperationException uoe2) {
            //do nothing
        } catch (RuntimeException e) {
            fail ("Wrong exception " + e);
        }
        
        
    }
}
