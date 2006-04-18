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

package org.netbeans.modules.spellchecker;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import junit.framework.TestCase;
import java.util.TreeSet;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 *
 * @author lahvac
 */
public class TrieDictionaryTest extends TestCase {
    
    public TrieDictionaryTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    public void testValidateWord() throws Exception {
        SortedSet<String> data = new TreeSet<String>();
        
        data.add("add");
        data.add("remove");
        data.add("data");
        data.add("test");
                
        TrieDictionary d = TrieDictionary.constructTrie(data);
        
        assertEquals(ValidityType.VALID, d.validateWord("remove"));
        assertEquals(ValidityType.VALID, d.validateWord("add"));
        assertEquals(ValidityType.VALID, d.validateWord("data"));
        assertEquals(ValidityType.VALID, d.validateWord("test"));
        
        assertEquals(ValidityType.INVALID, d.validateWord("sdfgh"));
        assertEquals(ValidityType.INVALID, d.validateWord("sd"));
        assertEquals(ValidityType.INVALID, d.validateWord("s"));
        assertEquals(ValidityType.INVALID, d.validateWord("datax"));
        
        assertEquals(ValidityType.PREFIX_OF_VALID, d.validateWord("d"));
        assertEquals(ValidityType.PREFIX_OF_VALID, d.validateWord("da"));
        assertEquals(ValidityType.PREFIX_OF_VALID, d.validateWord("dat"));
        assertEquals(ValidityType.PREFIX_OF_VALID, d.validateWord("t"));
    }

//    public void testFindValidWordsForPrefix() {
//    }
//
    
    public void testFindProposals() throws Exception {
        SortedSet<String> data = new TreeSet<String>();
        
        data.add("add");
        data.add("remove");
        data.add("data");
        data.add("test");
        data.add("hello");
        data.add("saida");
        
        TrieDictionary d = TrieDictionary.constructTrie(data);
        
        assertEquals(Collections.singletonList("hello"), d.findProposals("hfllo"));
        assertEquals(Collections.singletonList("saida"), d.findProposals("safda"));
    }
    
//
//    public void testGetDictionary() throws Exception {
//    }
//
//    public void testConstructTrie() throws Exception {
//    }
    
}
