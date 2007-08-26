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

package org.netbeans.modules.spellchecker.bindings.ruby;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.api.lexer.Language;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.ruby.lexer.RubyTokenId;
import org.netbeans.modules.spellchecker.spi.language.TokenList;

public class RubyTokenListTest extends TokenListTestBase {
    public RubyTokenListTest(String testName) {
        super(testName, RubyTokenId.language(), "text/x-ruby");
    }

    public void testSimpleWordBroker() throws Exception {
        tokenListTest("# tes test\ntestt(testtt)\n#testttt, testttttt.\n", "tes", "test", "testttt", "testttttt");
    }

    public void testSkipSymbols() throws Exception {
        tokenListTest("# tes :skip test\n", "tes", "test");
    }

    public void testPairTags() throws Exception {
        tokenListTest("#tes <code>test</code> <pre>testt</pre> <a href='testtt'>testttt</a> testttttt", "tes", "testttttt");
    }

    public void testSimplewriting() throws Exception {
        tokenListTestWithWriting("#  tes test\n  testt testtt # testttt\n #  testtttt\n #  testttttt\n", 14, "bflmpsvz", 13, "testtttt", "testttttt");
    }

    public void testDotDoesNotSeparateWords() throws Exception {
        tokenListTest("#tes.test", "tes", "test");
    }

    public void testRDocMarkers() throws Exception {
        tokenListTest("#*tes*.+test+ _underline_", "tes", "test", "underline");
    }

    public void testTagHandling() throws Exception {
        tokenListTest("# :nodoc: aba.abb.abc.abd abe :yield: abf abg abh abi abj abk abl\n abm abn abo.abp abq*/", "abe", "abg", "abh", "abm", "abn", "abq");
    }

    public void testLinkHandling() throws Exception {
        tokenListTest("# http://netbeans.org {abd }abe*/", "abd", "abe");
    }

    public void testPositions() throws Exception {
        BaseDocument doc = new BaseDocument(null, false);

        doc.putProperty(Language.class, RubyTokenId.language());

        doc.insertString(0, "#  tes test <pre>testt</pre> <a href='testtt'>testttt</a> testttttt*/", null);

        TokenList l = new RubyTokenList(doc);

        l.setStartOffset(9);
        assertTrue(l.nextWord());
        assertEquals(7, l.getCurrentWordStartOffset());
        assertTrue("test".equals(l.getCurrentWordText().toString()));
    }
}