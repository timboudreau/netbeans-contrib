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
package org.netbeans.modules.spellchecker.bindings.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.lexer.Language;
import org.netbeans.editor.BaseDocument;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.spellchecker.spi.language.TokenList;

/**
 *
 * @author Tor Norbye
 */
public abstract class TokenListTestBase extends NbTestCase {
    private Language language;
    private String mimeType;

    public TokenListTestBase(String testName, Language language, String mimeType) {
        super(testName);
        this.language = language;
        this.mimeType = mimeType;
    }

    protected void tokenListTest(String documentContent, String... golden) throws Exception {
        BaseDocument doc = new BaseDocument(null, false);

        doc.putProperty(Language.class, language);
        doc.putProperty("mimeType", mimeType);

        doc.insertString(0, documentContent, null);

        List<String> words = new ArrayList<String>();

        //TokenList l = new RubyTokenList(doc);
        TokenList l = new RubyTokenListProvider().findTokenList(doc);
        assertNotNull(l);

        l.setStartOffset(0);

        while (l.nextWord()) {
            words.add(l.getCurrentWordText().toString());
        }

        assertEquals(Arrays.asList(golden), words);
    }

    protected void tokenListTestWithWriting(String documentContent, int offset, String text, int startOffset, String... golden) throws Exception {
        BaseDocument doc = new BaseDocument(null, false);

        doc.putProperty(Language.class, language);
        doc.putProperty("mimeType", mimeType);

        doc.insertString(0, documentContent, null);

        List<String> words = new ArrayList<String>();
        TokenList l = new RubyTokenList(doc);

        while (l.nextWord()) {
        }

        doc.insertString(offset, text, null);

        l.setStartOffset(startOffset);

        while (l.nextWord()) {
            words.add(l.getCurrentWordText().toString());
        }

        assertEquals(Arrays.asList(golden), words);
    }
}