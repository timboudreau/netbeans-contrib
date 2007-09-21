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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.spellchecker.plain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.spellchecker.spi.language.TokenList;

/**
 *
 * @author Jan Lahoda
 */
public class PlainTokenListTest extends NbTestCase {
    
    public PlainTokenListTest(String testName) {
        super(testName);
    }

    public void testSimpleWordBroker() throws Exception {
        tokenListTest(
            "aaaaa bbbbb ccccc",
            "aaaaa", "bbbbb", "ccccc"
        );
    }

    public void testSimpleWordBroker2() throws Exception {
        tokenListTest(
            "aaaaa bbbbb ccccc  ddddd",
            "aaaaa", "bbbbb", "ccccc", "ddddd"
        );
    }
    
    private void tokenListTest(String documentContent, String... golden) throws Exception {
        Document doc = new PlainDocument();
        
        doc.insertString(0, documentContent, null);
        
        List<String> words = new ArrayList<String>();
        TokenList l = new PlainTokenList(doc);
        
        l.setStartOffset(0);
        
        while (l.nextWord()) {
            words.add(l.getCurrentWordText().toString());
        }
        
        assertEquals(Arrays.asList(golden), words);
    }

}
