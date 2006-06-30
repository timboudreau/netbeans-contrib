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

package org.netbeans.modules.spellchecker.bindings.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.ext.java.JCFinder;
import org.netbeans.editor.ext.java.JavaSyntax;
import org.netbeans.editor.ext.java.JavaSyntaxSupport;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.spellchecker.spi.language.TokenList;

/**
 *
 * @author Jan Lahoda
 */
public class JavaTokenListTest extends NbTestCase {
    
    public JavaTokenListTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    public void testSimpleWordBroker() throws Exception {
        tokenListTest(
            "/**tes test*/ testt testtt /*testttt*//**testtttt*//**testttttt*/",
            "tes", "test", "testtttt", "testttttt"
        );
    }

    public void testPairTags() throws Exception {
        tokenListTest(
            "/**tes <code>test</code> <pre>testt</pre> <a href='testtt'>testttt</a> testttttt*/",
            "tes", "testttttt"
        );
    }

    public void testSimplewriting() throws Exception {
        tokenListTestWithWriting(
            "/**tes test*/ testt testtt /*testttt*//**testtttt*//**testttttt*/",
            14, "bflmpsvz", 13,
            "testtttt", "testttttt"
        );
    }

    public void testDotDoesNotSeparateWords() throws Exception {
        tokenListTest(
                "/**tes.test*/",
                "tes", "test"
        );
    }
    
    public void testTagHandling() throws Exception {
        tokenListTest(
                "/**@see aba.abb.abc.abd abe @param abf abg abh @author abi abj abk abl\n abm abn @throws abo.abp abq*/",
                "abe", "abg", "abh", "abm", "abn", "abq"
        );
    }
    
    public void testLinkHandling() throws Exception {
        tokenListTest(
                "/**{@link aba abb abc} {abd }abe*/",
                "abd", "abe"
        );
    }
    
    public void testIsIdentifierLike() throws Exception {
        assertTrue(JavaTokenList.isIdentifierLike("JTable"));
        assertTrue(JavaTokenList.isIdentifierLike("getData"));
        assertTrue(JavaTokenList.isIdentifierLike("setTestingData"));

        assertFalse(JavaTokenList.isIdentifierLike("test"));
        assertFalse(JavaTokenList.isIdentifierLike("code"));
        assertFalse(JavaTokenList.isIdentifierLike("data"));
    }

    private void tokenListTest(String documentContent, String... golden) throws Exception {
        BaseDocument doc = new BaseDocument(TestJavaKit.class, true);
        
        doc.insertString(0, documentContent, null);
        
        List<String> words = new ArrayList<String>();
        TokenList l = new JavaTokenList(doc);
        
        l.setStartOffset(0);
        
        while (l.nextWord()) {
            words.add(l.getCurrentWordText().toString());
        }
        
        assertEquals(Arrays.asList(golden), words);
    }

    private void tokenListTestWithWriting(String documentContent, int offset, String text, int startOffset, String... golden) throws Exception {
        BaseDocument doc = new BaseDocument(TestJavaKit.class, true);
        
        doc.insertString(0, documentContent, null);
        
        List<String> words = new ArrayList<String>();
        TokenList l = new JavaTokenList(doc);
        
        while (l.nextWord()) {
        }

        doc.insertString(offset, text, null);
        
        l.setStartOffset(startOffset);
        
        while (l.nextWord()) {
            words.add(l.getCurrentWordText().toString());
        }

        assertEquals(Arrays.asList(golden), words);
    }

    public static final class TestJavaKit extends BaseKit {

        public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
            return new JavaSyntaxSupport(doc) {
                protected JCFinder getFinder() {
                    return null;
                }
            };
        }

        public Syntax createSyntax(Document doc) {
            return new JavaSyntax("1.5");
        }

    }

}
