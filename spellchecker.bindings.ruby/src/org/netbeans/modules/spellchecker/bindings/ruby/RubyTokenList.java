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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.ruby.lexer.RubyCommentTokenId;
import org.netbeans.modules.ruby.lexer.RubyTokenId;
import org.netbeans.modules.ruby.lexer.RubyTokenId;

/**
 * Tokenize Ruby text for spell checking
 *
 * @todo Check spelling in documentation sections
 * @todo Suppress spelling checks on :rdoc: modifiers
 * @todo Remove surrounding +, _, * on spelling words
 * @todo Spell check string literals?
 * @todo Spell check constant names and method names?
 *
 *
 *
 * @author Tor Norbye
 */
public class RubyTokenList extends AbstractRubyTokenList {

    /** Creates a new instance of RubyTokenList */
    public RubyTokenList(Document doc) {
        super(doc);
    }

    /** Given a sequence of Ruby tokens, return the next span of eligible comments */
    @Override
    protected int[] findNextSpellSpan(TokenSequence<? extends TokenId> ts, int offset) throws BadLocationException {
        if (ts == null) {
            return new int[]{-1, -1};
        }

        int diff = ts.move(offset);

        while (ts.moveNext()) {
            TokenId id = ts.token().id();
            if (id == RubyTokenId.LINE_COMMENT || id == RubyTokenId.DOCUMENTATION) {
                TokenSequence<? extends TokenId> t = ts.embedded(RubyCommentTokenId.language());
                if (t == null) {
                    return new int[]{ts.offset(), ts.offset() + ts.token().length()};
                } else {
                    t.move(offset);
                    while (t.moveNext()) {
                        id = t.token().id();
                        if (id == RubyCommentTokenId.COMMENT_TEXT || id == RubyCommentTokenId.COMMENT_BOLD || id == RubyCommentTokenId.COMMENT_ITALIC) {
                            return new int[]{t.offset(), t.offset() + t.token().length()};
                        }
                    }
                }
            }
        }

        return new int[]{-1, -1};
    }
}