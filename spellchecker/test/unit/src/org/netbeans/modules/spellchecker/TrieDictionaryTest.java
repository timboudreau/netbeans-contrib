/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
