/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.python.editor.lexer;

import java.util.ConcurrentModificationException;
import javax.swing.text.BadLocationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.junit.NbTestCase;
import org.netbeans.lib.lexer.test.LexerTestUtilities;
import org.netbeans.lib.lexer.test.ModificationTextDocument;

/**
 *
 * @author Martin Adamek
 */
public class PythonLexerTest extends NbTestCase {

    public PythonLexerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws java.lang.Exception {
        Logger.getLogger(PythonLexer.class.getName()).setLevel(Level.FINEST);
    }

    @Override
    protected void tearDown() throws java.lang.Exception {
    }

    @Override
    protected Level logLevel() {
        // enabling logging
        return Level.INFO; // uncomment this to have logging from PyhonLexer
        // we are only interested in a single logger, so we set its level in setUp(),
        // as returning Level.FINEST here would log from all loggers
    }

    public void test1() {
        String text = 
                "# this is the first comment\n" +
                "SPAM = 1                 # and this is the second comment\n" +
                "                         # ... and now a third!\n" +
                "STRING = \"# This is not a comment.\"";
        TokenHierarchy hi = TokenHierarchy.create(text, PythonTokenId.language());
        TokenSequence ts = hi.tokenSequence();
        // TODO why there is newline attached to this comment?
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "# this is the first comment\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.IDENTIFIER, "SPAM");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_OPERATOR, "=");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.INT_LITERAL, "1");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, "                 ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "# and this is the second comment");
        // TODO and now newline is extra token?
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.NEWLINE, "\n");
        // TODO those whtespaces before actual comment are part of the comment token?
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "                         # ... and now a third!\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.IDENTIFIER, "STRING");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_OPERATOR, "=");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.STRING_LITERAL, "\"# This is not a comment.\"");
    }

    public void test2() {
        String text =
                "#! /usr/bin/python\n" +
                "print \"Hello World!\"\t\n";
        TokenHierarchy hi = TokenHierarchy.create(text, PythonTokenId.language());
        TokenSequence ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "#! /usr/bin/python\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_KEYWORD, "print");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.STRING_LITERAL, "\"Hello World!\"");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, "\t");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.NEWLINE, "\n");
        assertFalse(ts.moveNext());
    }
    
    public void test3() {
        String text =
                "#! /usr/bin/python\n" +
                "print \"Hello World!\"\t";
        TokenHierarchy hi = TokenHierarchy.create(text, PythonTokenId.language());
        TokenSequence ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "#! /usr/bin/python\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_KEYWORD, "print");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.STRING_LITERAL, "\"Hello World!\"");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ERROR, "\t");
        assertFalse(ts.moveNext());
    }
    
    public void test4() throws BadLocationException {
        Document doc = new ModificationTextDocument();
        // Assign a language to the document
        doc.putProperty(Language.class, PythonTokenId.language());
        TokenHierarchy<?> hi = TokenHierarchy.get(doc);
        assertNotNull("Null token hierarchy for document", hi);
        TokenSequence<?> ts = hi.tokenSequence();
        assertFalse(ts.moveNext());
        
        // Insert text into document
        String text =
                "#! /usr/bin/python\n" +
                "print \"Hello World!\"";
        doc.insertString(0, text, null);

        // Last token sequence should throw exception - new must be obtained
        try {
            ts.moveNext();
            fail("TokenSequence.moveNext() did not throw exception as expected.");
        } catch (ConcurrentModificationException e) {
            // Expected exception
        }
        
        ts = hi.tokenSequence();
        
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "#! /usr/bin/python\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_KEYWORD, "print");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.STRING_LITERAL, "\"Hello World!\"");

        assertFalse(ts.moveNext());

        LexerTestUtilities.incCheck(doc, false);
        
        int offset = text.length() - 1;

        doc.remove(offset, 1);
        
        ts = hi.tokenSequence();
        
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.COMMENT, "#! /usr/bin/python\n");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ANY_KEYWORD, "print");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, PythonTokenId.ERROR, "\"Hello World!");

        assertFalse(ts.moveNext());

        LexerTestUtilities.incCheck(doc, false);
    }
    
}
