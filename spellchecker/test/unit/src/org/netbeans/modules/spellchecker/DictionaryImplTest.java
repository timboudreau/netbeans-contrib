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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.io.File;
import java.util.Collections;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 *
 * @author Jan Lahoda
 */
public class DictionaryImplTest extends NbTestCase {
    
    public DictionaryImplTest(String testName) {
        super(testName);
    }

    public void testAlmostEmpty() throws Exception {
        clearWorkDir();
        
        File source = new File(getWorkDir(), "dictionary.cache");
        DictionaryImpl d = new DictionaryImpl(source);
        
        assertEquals(ValidityType.INVALID, d.validateWord("dddd"));
        assertEquals(Collections.emptyList(), d.findProposals("dddd"));
        assertEquals(Collections.emptyList(), d.findValidWordsForPrefix("dddd"));
        
        d.addEntry("dddd");
        
        assertEquals(ValidityType.VALID, d.validateWord("dddd"));
        assertEquals(Collections.emptyList(), d.findProposals("dddd"));
        assertEquals(Collections.emptyList(), d.findValidWordsForPrefix("dddd"));
        assertEquals(Collections.singletonList("dddd"), d.findProposals("ddddd"));
        assertEquals(Collections.emptyList(), d.findValidWordsForPrefix("ddddd"));
        
        d.addEntry("ddddd");
        
        assertEquals(ValidityType.VALID, d.validateWord("dddd"));
        assertEquals(ValidityType.VALID, d.validateWord("ddddd"));
        assertEquals(Collections.emptyList(), d.findProposals("dddd"));
        assertEquals(Collections.emptyList(), d.findProposals("ddddd"));
    }
    
}
