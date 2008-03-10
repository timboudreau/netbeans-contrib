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

package org.netbeans.modules.javafx.lexer;

import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.lexer.test.LexerTestUtilities;
import org.netbeans.junit.NbTestCase;

/**
 * Test several simple lexer impls.
 *
 * @author mmetelka
 */
public class JavaLexerBatchTest extends NbTestCase {

    public JavaLexerBatchTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws java.lang.Exception {
        // Set-up testing environment
        LexerTestUtilities.setTesting(true);
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testComments() {
        String text = "/*ml-comment*//**//***//**\n*javadoc-comment*//* a";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BLOCK_COMMENT, "/*ml-comment*/");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BLOCK_COMMENT, "/**/");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.JAVADOC_COMMENT, "/***/");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.JAVADOC_COMMENT, "/**\n*javadoc-comment*/");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BLOCK_COMMENT, "/* a");
        assertEquals(PartType.START, ts.token().partType());
    }
    
    public void testIdentifiers() {
        String text = "a ab aB2 2a x\nyZ\r\nz";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "a");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "ab");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "aB2");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "2");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "a");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "x");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, "\n");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "yZ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, "\r\n");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "z");
    }
    
    public void testCharLiterals() {
        String text = "'' 'a''' '\\'' '\\\\' '\\\\\\'' '\\n' 'a";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "''");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'a'");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "''");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'\\''");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'\\\\'");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'\\\\\\''");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'\\n'");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR_LITERAL, "'a");
        assertEquals(PartType.START, ts.token().partType());
    }
    
    public void testStringLiterals() {
        String text = "\"\" \"a\"\"\" \"\\\"\" \"\\\\\" \"\\\\\\\"\" \"\\n\" \"a";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"a\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\\\"\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\\\\\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\\\\\\\"\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"\\n\"");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"a");
        assertEquals(PartType.START, ts.token().partType());
    }
    
    public void testNumberLiterals() {
        String text = "0 00 09 1 12 0L 1l 12L 0x1 0xf 0XdE 0Xbcy" + 
                " 09.5 1.5f 2.5d 6d 7e3 6.1E-7f 0xa.5dp+12d .3";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "0");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "00");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "09");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "1");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "12");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LONG_LITERAL, "0L");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LONG_LITERAL, "1l");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LONG_LITERAL, "12L");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "0x1");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "0xf");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "0XdE");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT_LITERAL, "0Xbc");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "y");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, "09.5");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FLOAT_LITERAL, "1.5f");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, "2.5d");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, "6d");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, "7e3");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FLOAT_LITERAL, "6.1E-7f");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, "0xa.5dp+12d");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE_LITERAL, ".3");
    }
    
    public void testOperators() {
        String text = "^ ^= % %= * *= / /= = == and or not xor";
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CARET, "^");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CARETEQ, "^=");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PERCENT, "%");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PERCENTEQ, "%=");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STAR, "*");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STAREQ, "*=");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SLASH, "/");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SLASHEQ, "/=");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EQ, "=");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EQEQ, "==");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.AND, "and");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.OR, "or");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.NOT, "not");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.XOR, "xor");
    }

    public void testKeywords() {
        /*String text = "abstract assert boolean break byte case catch char class const continue " +
            "default do dur double else enum extends final finally float for goto if " +
            "implements import instanceof int interface long native new package " +
            "private protected public return short static strictfp super switch " +
            "synchronized this throw throws transient try void volatile while " +
            "null true false " + 
            "after as attribute before bind delete first foreach from function " +
            "indexof insert in into inverse last later lazy nodebug on operation " +
            "reverse select sizeof trigger typeof var";*/
        String text = "assert break by catch class continue " +
            "distinct do dur easeboth easein easeout else extends finally for fps if " +
            "import instanceof new package " +
            "private protected public return super " +
            "this then try while " +
            "null true false " + 
            "after as attribute before bind delete first foreach from function " +
            "indexof insert in into inverse last later lazy linear nodebug on operation " +
            "order reverse select sizeof trigger typeof var";
        
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ABSTRACT, "abstract");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ASSERT, "assert");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BOOLEAN, "boolean");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BREAK, "break");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BYTE, "byte");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CASE, "case");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BY, "by");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CATCH, "catch");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CHAR, "char");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CLASS, "class");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CONST, "const");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.CONTINUE, "continue");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DEFAULT, "default");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DISTINCT, "distinct");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DO, "do");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DURATION, "dur");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DOUBLE, "double");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EASEBOTH, "easeboth");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EASEIN, "easein");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EASEOUT, "easeout");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ELSE, "else");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ENUM, "enum");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.EXTENDS, "extends");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FINAL, "final");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FINALLY, "finally");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FLOAT, "float");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FOR, "for");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FPS, "fps");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.GOTO, "goto");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IF, "if");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IMPLEMENTS, "implements");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IMPORT, "import");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INSTANCEOF, "instanceof");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INT, "int");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INTERFACE, "interface");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LONG, "long");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.NATIVE, "native");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.NEW, "new");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PACKAGE, "package");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PRIVATE, "private");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PROTECTED, "protected");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.PUBLIC, "public");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.RETURN, "return");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SHORT, "short");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STATIC, "static");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRICTFP, "strictfp");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SUPER, "super");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SWITCH, "switch");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SYNCHRONIZED, "synchronized");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.THIS, "this");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.THEN, "then");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.THROW, "throw");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.THROWS, "throws");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.TRANSIENT, "transient");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.TRY, "try");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.VOID, "void");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.VOLATILE, "volatile");
//        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHILE, "while");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.NULL, "null");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.TRUE, "true");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FALSE, "false");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.AFTER, "after"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.AS, "as");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ATTRIBUTE, "attribute"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BEFORE, "before"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.BIND, "bind"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.DELETE, "delete");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FIRST, "first"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FOREACH, "foreach"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FROM, "from"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.FUNCTION, "function"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INDEXOF, "indexof"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INSERT, "insert"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IN, "in"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INTO, "into"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.INVERSE, "inverse"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LAST, "last"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LATER, "later"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LAZY, "lazy"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.LINEAR, "linear");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.NODEBUG, "nodebug"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ON, "on");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.OPERATION, "operation"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.ORDER, "order");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.REVERSE, "reverse");     
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SELECT, "select"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.SIZEOF, "sizeof"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.TRIGGER, "trigger"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.TYPEOF, "typeof"); 
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.VAR, "var"); 
    }

    public void testNonKeywords() {
        String text = "abstracta assertx b br car dou doubl finall im i ifa inti throwsx";

        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "abstracta");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "assertx");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "b");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "br");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "car");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "dou");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "doubl");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "finall");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "im");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "i");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "ifa");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "inti");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "throwsx");
    }
    
    public void testEmbedding() {
        String text = "ddx \"d\\t\\br\" /** @see X */";
        
        TokenHierarchy<?> hi = TokenHierarchy.create(text, JavaFXTokenId.language());
        TokenSequence<? extends TokenId> ts = hi.tokenSequence();
        
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.IDENTIFIER, "ddx");
        assertEquals(0, ts.offset());
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        assertEquals(3, ts.offset());
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.STRING_LITERAL, "\"d\\t\\br\"");
        assertEquals(4, ts.offset());
        
        TokenSequence<? extends TokenId> es = ts.embedded();
        
        LexerTestUtilities.assertNextTokenEquals(es, JavaStringTokenId.TEXT, "d");
        assertEquals(5, es.offset());
        LexerTestUtilities.assertNextTokenEquals(es, JavaStringTokenId.TAB, "\\t");
        assertEquals(6, es.offset());
        LexerTestUtilities.assertNextTokenEquals(es, JavaStringTokenId.BACKSPACE, "\\b");
        assertEquals(8, es.offset());
        LexerTestUtilities.assertNextTokenEquals(es, JavaStringTokenId.TEXT, "r");
        assertEquals(10, es.offset());
        
        assertFalse(es.moveNext());
        
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.WHITESPACE, " ");
        assertEquals(12, ts.offset());
        LexerTestUtilities.assertNextTokenEquals(ts, JavaFXTokenId.JAVADOC_COMMENT, "/** @see X */");
        assertEquals(13, ts.offset());
        
        TokenSequence<? extends TokenId> ds = ts.embedded();
        
        LexerTestUtilities.assertNextTokenEquals(ds, JavadocTokenId.OTHER_TEXT, " ");
        assertEquals(16, ds.offset());
        LexerTestUtilities.assertNextTokenEquals(ds, JavadocTokenId.TAG, "@see");
        assertEquals(17, ds.offset());
        LexerTestUtilities.assertNextTokenEquals(ds, JavadocTokenId.OTHER_TEXT, " ");
        assertEquals(21, ds.offset());
        LexerTestUtilities.assertNextTokenEquals(ds, JavadocTokenId.IDENT, "X");
        assertEquals(22, ds.offset());
        LexerTestUtilities.assertNextTokenEquals(ds, JavadocTokenId.OTHER_TEXT, " ");
        assertEquals(23, ds.offset());
        
        assertFalse(ds.moveNext());
        
        assertFalse(ts.moveNext());
    }
}
