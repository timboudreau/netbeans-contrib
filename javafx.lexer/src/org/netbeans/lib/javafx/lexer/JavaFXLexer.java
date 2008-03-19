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

package org.netbeans.lib.javafx.lexer;

import org.netbeans.api.javafx.lexer.JavaFXTokenId;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Lexical analyzer for the JavaFX language.
 * <br/>
 * It recognizes "version" attribute and expects <code>java.lang.Integer</code>
 * value for it. The default value is Integer.valueOf(1). 
 * <p><b>Note:</b>This version doesn't rely on the version value 
 * and doesn't change its behavior.</p>
 * 
 * <p>
 * This implementation is based on the JavaFX ANTLR gramar 
 * <a href="https://openjfx-compiler.dev.java.net/source/browse/openjfx-compiler/trunk/src/share/classes/com/sun/tools/javafx/antlr/v3.g?rev=1927&view=markup">
 * v3.g rev 1927</a>.
 * </p>
 * 
 * <p>The implementation of this class is adoption for JavaFX of the
 * <code>org.netbeans.lib.java.lexer.JavaLexer</code> developed by
 * Miloslav Metelka.
 * </p>
 * <p>
 * Unlike the original implementation this implementation is consistent with 
 * the Unit test approach. The implementation extends the 
 * <code>JavaFXTestableLexer<code> that encapsulates all the Lexer API entities 
 * which can't be instantiated outside of the infrastructure.
 * It lets to design a test of the <code>JavaFXLexer</code> unit with using an 
 * Adaptor that will overload methods of the <code>JavaFXTestableLexer</code>, 
 * but it won't hide <code>Lexer</code> API (i.e. a subject of unit testing).
 * </p>
 *
 * @author Miloslav Metelka
 * @author Victor G. Vasilyev
 * @version 1.00
 * 
 * @todo convertUnicode - Implement a Scaner according to the JavaFX specification.
 * @todo NextIsPercent
 */

public class JavaFXLexer extends JavaFXTestableLexer implements Lexer<JavaFXTokenId> {
    
    private static final int SINGLE_QUOTE = '\'';
    private static final int DOUBLE_QUOTE = '"';
    private static final int RBRACE = JavaFXTokenId.RBRACE.fixedText().charAt(0);
            
    private static final int EOF = LexerInput.EOF;

    private final int version;
    
    private final JavaFXLexerStateController stateController;

    public JavaFXLexer(LexerRestartInfo<JavaFXTokenId> info) {
        super(info.input(), info.tokenFactory());
        this.version = getLanguageMajorVersion(info);
        this.stateController = new JavaFXLexerStateController(info.state());
    }

    /**
     * For test purpose only.
     * @param state
     * @param version
     */
    JavaFXLexer(Object state, int version) {
        this.stateController = new JavaFXLexerStateController(state);
        this.version = version;
    } 
    
    private static int getLanguageMajorVersion(LexerRestartInfo<JavaFXTokenId> info) {
        Integer ver = (Integer)info.getAttributeValue("version");
        return (ver != null) ? ver.intValue() : 1; // Use JavaFX 1.0 by default
    }
    
    public Object state() {
        return stateController.getState();
    }
    
    public void release() {
        stateController.release();
    }

    public Token<JavaFXTokenId> nextToken() {
        while(true) {
            int c = inputRead();
            switch (c) {
                case DOUBLE_QUOTE: // string literal
                case SINGLE_QUOTE: // string literal
                    return processString(c, 0);
                case '#':
                    if(inputRead() == '#') {               
                        if(inputRead() == '[') {
                            while (true)
                                switch (inputRead()) {
                                    case ']':
                                    case EOF:
                                        return token(JavaFXTokenId.TRANSLATION_KEY);
                                }
                        } else {
                            inputBackup(1);                            
                        }
                        return token(JavaFXTokenId.TRANSLATION_KEY);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.POUND);
                case '-':
                    switch (inputRead()) {
                        case '-':
                            return token(JavaFXTokenId.MINUSMINUS);
                        case '=':
                            return token(JavaFXTokenId.MINUSEQ);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.MINUS);
                case ',':
                    return token(JavaFXTokenId.COMMA);
                case ';':
                    return token(JavaFXTokenId.SEMICOLON);
                case ':':
                    return token(JavaFXTokenId.COLON);
                case '?':
                    return token(JavaFXTokenId.QUESTION);
                case '.':
                    if ((c = inputRead()) == '.') {
                        return token(JavaFXTokenId.DOTDOT);
                    } else if ('0' <= c && c <= '9') { // float literal
                        return finishNumberLiteral(inputRead(), true);
                    } else {
                        inputBackup(1);
                    }
                    return token(JavaFXTokenId.DOT);
                case '(':
                    return token(JavaFXTokenId.LPAREN);
                case ')':
                    return token(JavaFXTokenId.RPAREN);
                case '[':
                    return token(JavaFXTokenId.LBRACKET);
                case ']':
                    return token(JavaFXTokenId.RBRACKET);
                case '{':
                    stateController.enterBrace(0, false);
                    return token(JavaFXTokenId.LBRACE);
                case '}':
                    if(!stateController.rightBraceLikeQuote(0)) {
                        // case 1: end of usual block
                        stateController.leaveBrace();
                        return token(JavaFXTokenId.RBRACE);
                    }
                    if(stateController.rightBraceLikeQuote(DOUBLE_QUOTE)) {
                       // case 2: end of a block inside "string literal"
                       return processString(DOUBLE_QUOTE, RBRACE); 
                    }
                    if(stateController.rightBraceLikeQuote(SINGLE_QUOTE)) {
                       // case 2: end of a block inside 'string literal'
                       return processString(SINGLE_QUOTE,RBRACE); 
                    }
                    break;
                case '*':
                    switch (inputRead()) {
                        case '/': // invalid comment end - */
                            return token(JavaFXTokenId.INVALID_COMMENT_END);
                        case '=':
                            return token(JavaFXTokenId.STAREQ);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.STAR);
                case '/':
                    switch (inputRead()) {
                        case '=': // found /=
                            return token(JavaFXTokenId.SLASHEQ);
                        case '/': // in single-line comment
                            while (true)
                                switch (inputRead()) {
                                    case '\r': inputConsumeNewline();
                                    case '\n':
                                    case EOF:
                                        return token(JavaFXTokenId.LINE_COMMENT);
                                }
                        case '*': // in multi-line or javadoc comment
                            c = inputRead();
                            if (c == '*') { // either javadoc comment or empty multi-line comment /**/
                                    c = inputRead();
                                    if (c == '/')
                                        return token(JavaFXTokenId.BLOCK_COMMENT);
                                    while (true) { // in javadoc comment
                                        while (c == '*') {
                                            c = inputRead();
                                            if (c == '/')
                                                return token(JavaFXTokenId.JAVADOC_COMMENT);
                                            else if (c == EOF)
                                                return tokenFactoryCreateToken(JavaFXTokenId.JAVADOC_COMMENT,
                                                        inputReadLength(), PartType.START);
                                        }
                                        if (c == EOF)
                                            return tokenFactoryCreateToken(JavaFXTokenId.JAVADOC_COMMENT,
                                                        inputReadLength(), PartType.START);
                                        c = inputRead();
                                    }

                            } else { // in multi-line comment (and not after '*')
                                while (true) {
                                    c = inputRead();
                                    while (c == '*') {
                                        c = inputRead();
                                        if (c == '/')
                                            return token(JavaFXTokenId.BLOCK_COMMENT);
                                        else if (c == EOF)
                                            return tokenFactoryCreateToken(JavaFXTokenId.BLOCK_COMMENT,
                                                    inputReadLength(), PartType.START);
                                    }
                                    if (c == EOF)
                                        return tokenFactoryCreateToken(JavaFXTokenId.BLOCK_COMMENT,
                                                inputReadLength(), PartType.START);
                                }
                            }
                    } // end of switch()
                    inputBackup(1);
                    return token(JavaFXTokenId.SLASH);
                case '%':
                    if(stateController.percentIsFormat()) {
                        while(true) {
                            c = inputRead();
                            switch(c) {
                                case ' ': // May be all whitespaces should be listed?
                                case EOF:
                                    stateController.resetPercentIsFormat();
                                    return token(JavaFXTokenId.FORMAT_STRING_LITERAL);                        
                                default:
                                    continue;
                            }
                        }
                    }
                    if (inputRead() == '=')
                        return token(JavaFXTokenId.PERCENTEQ);
                    inputBackup(1);
                    return token(JavaFXTokenId.PERCENT);
                case '+':
                    switch (inputRead()) {
                        case '+':
                            return token(JavaFXTokenId.PLUSPLUS);
                        case '=':
                            return token(JavaFXTokenId.PLUSEQ);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.PLUS);
                case '<':
                    switch (inputRead()) {
                        case '=': // <=
                            return token(JavaFXTokenId.LTEQ);
                        case '>': // <>
                            return token(JavaFXTokenId.LTGT);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.LT);
                case '=':
                    switch (inputRead()) {
                        case '=': // ==
                            return token(JavaFXTokenId.EQEQ);
                        case '>': // =>
                            return token(JavaFXTokenId.SUCHTHAT);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.EQ);
                case '>':
                    switch (inputRead()) {
                        case '=': // >=
                            return token(JavaFXTokenId.GTEQ);
                    }
                    inputBackup(1);
                    return token(JavaFXTokenId.GT);
                case '|':
                    return token(JavaFXTokenId.PIPE);

                case '0': // "0" in a number literal or a time literal
		    c = inputRead();
                    if (c == 'x' || c == 'X') { // in hexadecimal (possibly floating-point) literal
                        boolean inFraction = false;
                        while (true) {
                            switch (inputRead()) {
                                case '0': case '1': case '2': case '3': case '4':
                                case '5': case '6': case '7': case '8': case '9':
                                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                    break;
                                case '.': // hex float literal
                                    if (!inFraction) {
                                        inFraction = true;
                                    } else { // two dots in the float literal
                                        return token(JavaFXTokenId.FLOATING_POINT_LITERAL_INVALID);
                                    }
                                    break;
//                                case 'p': case 'P': // binary exponent
//                                    return finishFloatExponent();
                                default:
                                    inputBackup(1);
                                    // if float then before mandatory binary exponent => invalid
                                    return token(inFraction ? JavaFXTokenId.FLOATING_POINT_LITERAL_INVALID
                                            : JavaFXTokenId.DECIMAL_LITERAL);
                            }
                        } // end of while(true)
                    }
                    return finishNumberLiteral(c, false); 
                    
                case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9': 
                    // "1"..."9" in a number literal or a time literal
                    return finishNumberLiteral(inputRead(), false);

                    
                // Keywords lexing    
                case 'a':
                    switch (c = inputRead()) {
                        case 'b':
                            if ((c = inputRead()) == 's'
                             && (c = inputRead()) == 't'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 'a'
                             && (c = inputRead()) == 'c'
                             && (c = inputRead()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.ABSTRACT);
                            break;
                        case 'f':
                            if ((c = inputRead()) == 't'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.AFTER);
                            break;
                        case 'n':
                            if ((c = inputRead()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.AND);
                            break;
                        case 's': 
                            if(Character.isWhitespace(c = inputRead())) {
                                inputBackup(1);
                                return keywordOrIdentifier(JavaFXTokenId.AS);
                            }
                            if (c == 's'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.ASSERT);
                            break;
                        case 't':
                            if ((c = inputRead()) == 't'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 'b'
                             && (c = inputRead()) == 'u'
                             && (c = inputRead()) == 't'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.ATTRIBUTE);
                            break;
                    }
                    return finishIdentifier(c);

                case 'b':
                    switch (c = inputRead()) {
                        case 'e':
                            if ((c = inputRead()) == 'f'
                             && (c = inputRead()) == 'o'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.BEFORE);
                            break;
                        case 'i':
                            if ((c = inputRead()) == 'n'
                             && (c = inputRead()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.BIND);
                            break;
                        case 'o':
                            if ((c = inputRead()) == 'u'
                             && (c = inputRead()) == 'n'
                             && (c = inputRead()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.BOUND);
                            break;
                        case 'r':
                            if ((c = inputRead()) == 'e'
                             && (c = inputRead()) == 'a'
                             && (c = inputRead()) == 'k')
                                return keywordOrIdentifier(JavaFXTokenId.BREAK);
                            break;
                    }
                    return finishIdentifier(c);

                case 'c':
                    switch (c = inputRead()) {
                        case 'a':
                            switch (c = inputRead()) {
                                case 't':
                                    if ((c = inputRead()) == 'c'
                                     && (c = inputRead()) == 'h')
                                        return keywordOrIdentifier(JavaFXTokenId.CATCH);
                                    break;
                            }
                            break;
                        case 'l':
                            if ((c = inputRead()) == 'a'
                             && (c = inputRead()) == 's'
                             && (c = inputRead()) == 's')
                                return keywordOrIdentifier(JavaFXTokenId.CLASS);
                            break;
                        case 'o':
                            if ((c = inputRead()) == 'n' 
                             && (c = inputRead()) == 't' 
                             && (c = inputRead()) == 'i' 
                             && (c = inputRead()) == 'n' 
                             && (c = inputRead()) == 'u' 
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.CONTINUE);
                    }
                    return finishIdentifier(c);

                case 'd':
                    if ((c = inputRead()) == 'e' 
                     && (c = inputRead()) == 'l' 
                     && (c = inputRead()) == 'e' 
                     && (c = inputRead()) == 't' 
                     && (c = inputRead()) == 'e')
                        return keywordOrIdentifier(JavaFXTokenId.DELETE);
                    return finishIdentifier(c);

                case 'e':
                    switch (c = inputRead()) {
                        case 'l':
                            if ((c = inputRead()) == 's'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.ELSE);
                            break;
                        case 'x':
                            switch (c = inputRead()) {
                                case 'c':
                                    if ((c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'u'
                                     && (c = inputRead()) == 's'
                                     && (c = inputRead()) == 'i'
                                     && (c = inputRead()) == 'v'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.EXCLUSIVE);
                                    break;
                                case 't':
                                    if ((c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'n'
                                     && (c = inputRead()) == 'd'
                                     && (c = inputRead()) == 's')
                                        return keywordOrIdentifier(JavaFXTokenId.EXTENDS);
                                    break;
                            }
                    }
                    return finishIdentifier(c);

                case 'f':
                    switch (c = inputRead()) {
                        case 'a': // "fa"
                            if ((c = inputRead()) == 'l'
                             && (c = inputRead()) == 's'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.FALSE);
                            break;
                        case 'i': // "fi"
                            switch (c = inputRead()) {
                                case 'n':
                                    if ((c = inputRead()) == 'a'
                                     && (c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.FINALLY);
                                    break;
                                case 'r':
                                    if ((c = inputRead()) == 's'
                                     && (c = inputRead()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.FIRST);
                                    break;
                            }
                            break;
                        case 'o':
                            if ((c = inputRead()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.FOR);
                            break;
                        case 'r':
                            if ((c = inputRead()) == 'o'
                             && (c = inputRead()) == 'm')
                                return keywordOrIdentifier(JavaFXTokenId.FROM);
                            break;
                        case 'u':
                            if ((c = inputRead()) == 'n'
                             && (c = inputRead()) == 'c'
                             && (c = inputRead()) == 't'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 'o'
                             && (c = inputRead()) == 'n')
                                return keywordOrIdentifier(JavaFXTokenId.FUNCTION);
                            break;
                    }
                    return finishIdentifier(c);
                case 'i': // "i"
                    switch (c = inputRead()) {
                        case 'f':
                            return keywordOrIdentifier(JavaFXTokenId.IF);
                        case 'm':
                            if ((c = inputRead()) == 'p'
                             && (c = inputRead()) == 'o'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 't') {
                                return keywordOrIdentifier(JavaFXTokenId.IMPORT);
                            }
                            break;
                        case 'n': // "in"
                            if(Character.isWhitespace(c = inputRead())) {
                                inputBackup(1);
                                return keywordOrIdentifier(JavaFXTokenId.IN);
                            }
                            switch (c) {
                                case 'd':  // "ind"
                                    if ((c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'x'
                                     && (c = inputRead()) == 'o'
                                     && (c = inputRead()) == 'f')
                                        return keywordOrIdentifier(JavaFXTokenId.INDEXOF);
                                    break;
                                case 'i': // "ini"
                                    if ((c = inputRead()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.INIT);
                                    break;
                                case 's': // "ins"
                                    switch(c = inputRead()) {
                                        case 'e': // "inse"
                                            if ((c = inputRead()) == 'r'
                                             && (c = inputRead()) == 't')
                                                return keywordOrIdentifier(JavaFXTokenId.INSERT);
                                            break;
                                        case 't': // "inst"
                                            if ((c = inputRead()) == 'a'
                                             && (c = inputRead()) == 'n'
                                             && (c = inputRead()) == 'c'
                                             && (c = inputRead()) == 'e'
                                             && (c = inputRead()) == 'o'
                                             && (c = inputRead()) == 'f')
                                                return keywordOrIdentifier(JavaFXTokenId.INSTANCEOF);
                                            break;
                                    }
                                    break;
                                case 't': // "int"
                                    if ((c = inputRead()) == 'o')
                                        return keywordOrIdentifier(JavaFXTokenId.INTO);
                                    break;
                                case 'v': // "inv"
                                    if ((c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'r'
                                     && (c = inputRead()) == 's'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.INVERSE);
                                    break;
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'l':
                    switch (c = inputRead()) {
                        case 'a':
                            switch (c = inputRead()) {
                                case 's':
                                    if ((c = inputRead()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.LAST);
                                    break;
                                case 'z':
                                    if ((c = inputRead()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.LAZY);
                                    break;
                            }
                            break;
                        case 'e':
                            if ((c = inputRead()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.LET);
                            break;
                    }
                    return finishIdentifier(c);

                case 'n':
                    switch (c = inputRead()) {
                        case 'o':
                            if ((c = inputRead()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.NOT);
                            break;
                        case 'e':
                            if ((c = inputRead()) == 'w')
                                return keywordOrIdentifier(JavaFXTokenId.NEW);
                            break;
                        case 'u':
                            if ((c = inputRead()) == 'l'
                             && (c = inputRead()) == 'l')
                                return keywordOrIdentifier(JavaFXTokenId.NULL);
                            break;
                    }
                    return finishIdentifier(c);

                case 'o':
                    switch (c = inputRead()) {
                        case 'n':
                            return keywordOrIdentifier(JavaFXTokenId.ON);
                        case 'r':
                            return keywordOrIdentifier(JavaFXTokenId.OR);
                        case 'v':
                            if ((c = inputRead()) == 'e'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 'r'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 'd'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.OVERRIDE);
                            break;
                    }
                    return finishIdentifier(c);

                case 'p':
                    switch (c = inputRead()) {
                        case 'a':
                            if ((c = inputRead()) == 'c'
                             && (c = inputRead()) == 'k'
                             && (c = inputRead()) == 'a'
                             && (c = inputRead()) == 'g'
                             && (c = inputRead()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.PACKAGE);
                            break;
                        case 'o':
                            if ((c = inputRead()) == 's'
                             && (c = inputRead()) == 't'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 'n'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.POSTINIT);
                            break;
                        case 'r':
                            switch (c = inputRead()) {
                                case 'i':
                                    if ((c = inputRead()) == 'v'
                                     && (c = inputRead()) == 'a'
                                     && (c = inputRead()) == 't'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.PRIVATE);
                                    break;
                                case 'o':
                                    if ((c = inputRead()) == 't'
                                     && (c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'c'
                                     && (c = inputRead()) == 't'
                                     && (c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'd')
                                        return keywordOrIdentifier(JavaFXTokenId.PROTECTED);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = inputRead()) == 'b'
                             && (c = inputRead()) == 'l'
                             && (c = inputRead()) == 'i'
                             && (c = inputRead()) == 'c')
                                return keywordOrIdentifier(JavaFXTokenId.PUBLIC);
                            break;
                    }
                    return finishIdentifier(c);

                case 'r': // "r"
                    switch (c = inputRead()) {
                        case 'e': // "re"
                            switch (c = inputRead()) {
                                case 'a': // "rea"
                                    if ((c = inputRead()) == 'd'
                                     && (c = inputRead()) == 'o'
                                     && (c = inputRead()) == 'n'
                                     && (c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.READONLY);
                                    break;
                                case 'p': // "rep"
                                    if ((c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'a'
                                     && (c = inputRead()) == 'c'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.REPLACE);
                                    break;
                                case 't': // "ret"
                                    if ((c = inputRead()) == 'u'
                                     && (c = inputRead()) == 'r'
                                     && (c = inputRead()) == 'n')
                                        return keywordOrIdentifier(JavaFXTokenId.RETURN);
                                    break;
                                case 'v': // "rev"
                                    if ((c = inputRead()) == 'e'
                                     && (c = inputRead()) == 'r'
                                     && (c = inputRead()) == 's'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.REVERSE);
                                    break;
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 's':
                    switch (c = inputRead()) {
                        case 'i':
                            if ((c = inputRead()) == 'z'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'o'
                             && (c = inputRead()) == 'f')
                                return keywordOrIdentifier(JavaFXTokenId.SIZEOF);
                            break;
                        case 't':
                            switch (c = inputRead()) {
                                case 'a':
                                    if ((c = inputRead()) == 't'
                                     && (c = inputRead()) == 'i'
                                     && (c = inputRead()) == 'c')
                                        return keywordOrIdentifier(JavaFXTokenId.STATIC);
                                    break;
                                case 'e': // "ste"
                                    if ((c = inputRead()) == 'p')
                                        return keywordOrIdentifier(JavaFXTokenId.STEP);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = inputRead()) == 'p'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.SUPER);
                            break;
                    }
                    return finishIdentifier(c);

                case 't': // "t"
                    switch (c = inputRead()) {
                        case 'h': // "th"
                            switch (c = inputRead()) {
                                case 'e': // "the"
                                    if ((c = inputRead()) == 'n')
                                        return keywordOrIdentifier(JavaFXTokenId.THEN);
                                    break;
                                case 'i': // "thi"
                                    if ((c = inputRead()) == 's')
                                        return keywordOrIdentifier(JavaFXTokenId.THIS);
                                    break;
                                case 'r': // "thr"
                                    if ((c = inputRead()) == 'o'
                                     && (c = inputRead()) == 'w')
                                        return keywordOrIdentifier(JavaFXTokenId.THROW);
                                    break;
                            }
                            break;
                        case 'r': // "tr"
                            switch (c = inputRead()) {
                                case 'u': // "tru"
                                    if ((c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.TRUE);
                                    break;
                                case 'y': // "try"
                                    return keywordOrIdentifier(JavaFXTokenId.TRY);
                            }
                            break;
                        case 'w': // "tw"
                            if ((c = inputRead()) == 'e'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'n')
                                return keywordOrIdentifier(JavaFXTokenId.TWEEN);
                            break;
                        case 'y': // "ty"
                            if ((c = inputRead()) == 'p'
                             && (c = inputRead()) == 'e'
                             && (c = inputRead()) == 'o'
                             && (c = inputRead()) == 'f')
                                return keywordOrIdentifier(JavaFXTokenId.TYPEOF);
                            break;
                    }
                    return finishIdentifier(c);

                case 'v':
                    if ((c = inputRead()) == 'a'
                     && (c = inputRead()) == 'r')
                        return keywordOrIdentifier(JavaFXTokenId.VAR);
                    return finishIdentifier(c);

                case 'w':
                    switch (c = inputRead()) {
                        case 'h': // "wh"
                            switch (c = inputRead()) {
                                case 'e': // "whe"
                                    if ((c = inputRead()) == 'r'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.WHERE);
                                case 'i': // "whi"
                                    if ((c = inputRead()) == 'l'
                                     && (c = inputRead()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.WHILE);
                                    break;
                            }
                            break;
                        case 'i': // "wi"
                            if ((c = inputRead()) == 't'
                             && (c = inputRead()) == 'h')
                                return keywordOrIdentifier(JavaFXTokenId.WITH);
                            break;
                    }
                    return finishIdentifier(c);

                // Rest of lowercase letters starting identifiers
                case 'g': case 'h': case 'j': case 'k': case 'm': 
                case 'q': case 'u': case 'x': case 'y': case 'z':
                // Uppercase letters starting identifiers
                case 'A': case 'B': case 'C': case 'D': case 'E':
                case 'F': case 'G': case 'H': case 'I': case 'J':
                case 'K': case 'L': case 'M': case 'N': case 'O':
                case 'P': case 'Q': case 'R': case 'S': case 'T':
                case 'U': case 'V': case 'W': case 'X': case 'Y':
                case 'Z':
                case '$': case '_':
                    return finishIdentifier();
                    
                // All Character.isWhitespace(c) below 0x80 follow
                // ['\t' - '\r'] and [0x1c - ' ']
                case '\t':
                case '\n':
                case 0x0b:
                case '\f':
                case '\r':
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                    return finishWhitespace();
                case ' ':
                    c = inputRead();
                    if (c == EOF || !Character.isWhitespace(c)) { // Return single space as flyweight token
                        inputBackup(1);
                        return tokenFactoryGetFlyweightToken(JavaFXTokenId.WHITESPACE, " ");
                    }
                    return finishWhitespace();

                case EOF:
                    return null;

                default:
                    if (c >= 0x80) { // lowSurr ones already handled above
                        c = translateSurrogates(c);
                        if (Character.isJavaIdentifierStart(c))
                            return finishIdentifier();
                        if (Character.isWhitespace(c))
                            return finishWhitespace();
                    }

                    // Invalid char
                    return token(JavaFXTokenId.ERROR);
            } // end of switch (c)
        } // end of while(true)
    }
    
    private Token<JavaFXTokenId> processString(int quote, int startedWith) {
        assert (quote == '\'' || quote == '"');
        assert( startedWith == RBRACE || startedWith == 0);
        while (true) {
            int c = inputRead();
            switch (c) {
                case '\'': // NOI18N
                case '"': // NOI18N
                    if (quote == c) {
                        if(startedWith == RBRACE) {
                            stateController.leaveBrace();
                            if(stateController.inBraceQuote()) stateController.leaveQuote();
                            return token(JavaFXTokenId.RBRACE_QUOTE_STRING_LITERAL);
                        }
                        return token(JavaFXTokenId.STRING_LITERAL);
                    }
                    break;
                case '\\':
                    inputRead();
                    break;
                case '\r':
                    inputConsumeNewline();
                case '\n':
                    inputRead(); // enable the multi-line string literals.
                    break;
                case EOF: // incompleted string literal, i.e. under development.
                    return tokenFactoryCreateToken(JavaFXTokenId.STRING_LITERAL,
                            inputReadLength(), PartType.START);
                case '{':
                    if(startedWith == '}') {
                        return token(JavaFXTokenId.RBRACE_LBRACE_STRING_LITERAL);
                    }
                    stateController.enterBrace(quote, isThereFormatClause());
                    return token(JavaFXTokenId.QUOTE_LBRACE_STRING_LITERAL);
            }
        }
    }
    
    private boolean isThereFormatClause() {
        // See the fragment NextIsPercent[int quoteContext] definition in 
        // the v3.g ANTLR grammar file.
        int charCounter = 0; 
        while (true) {
            int c = inputRead();
            charCounter++;
            switch (c) {
                case ' ':
                case '\r':
                case '\t':
                case '\u000C':
                case '\n':
                    continue;
                case '%':
                    inputBackup(charCounter);
                    return true;
                case EOF:
                default:
                    inputBackup(charCounter);
                    return false;
            }
        }
    }
    
    private int translateSurrogates(int c) {
        if (Character.isHighSurrogate((char)c)) {
            int lowSurr = inputRead();
            if (lowSurr != EOF && Character.isLowSurrogate((char)lowSurr)) {
                // c and lowSurr form the integer unicode char.
                c = Character.toCodePoint((char)c, (char)lowSurr);
            } else {
                // Otherwise it's error: Low surrogate does not follow the high one.
                // Leave the original character unchanged.
                // As the surrogates do not belong to any
                // specific unicode category the lexer should finally
                // categorize them as a lexical error.
                inputBackup(1);
            }
        }
        return c;
    }

    private Token<JavaFXTokenId> finishWhitespace() {
        while (true) {
            int c = inputRead();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (c == EOF || !Character.isWhitespace(c)) {
                inputBackup(1);
                return tokenFactoryCreateToken(JavaFXTokenId.WHITESPACE);
            }
        }
    }
    
    private Token<JavaFXTokenId> finishIdentifier() {
        return finishIdentifier(inputRead());
    }
    
    private Token<JavaFXTokenId> finishIdentifier(int c) {
        while (true) {
            if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
                // For surrogate 2 chars must be backed up
                inputBackup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactoryCreateToken(JavaFXTokenId.IDENTIFIER);
            }
            c = inputRead();
        }
    }

    private Token<JavaFXTokenId> keywordOrIdentifier(JavaFXTokenId keywordId) {
        return keywordOrIdentifier(keywordId, inputRead());
    }

    private Token<JavaFXTokenId> keywordOrIdentifier(JavaFXTokenId keywordId, int c) {
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
            // For surrogate 2 chars must be backed up
            inputBackup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
            return token(keywordId);
        } else // c is identifier part
            return finishIdentifier();
    }
    
    private Token<JavaFXTokenId> finishNumberLiteral(int c, boolean inFraction) {
        while (true) {
            switch (c) {
                case '.':
                    if (!inFraction) {
                        inFraction = true;
                    } else { // two dots in the literal
                        return token(JavaFXTokenId.FLOATING_POINT_LITERAL_INVALID);
                    }
                    break;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                case 'e': case 'E': // exponent part
                    return finishFloatExponent();
                case 'h': 
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>h"
                case 'm':
                    if((c = inputRead()) == 's') 
                        return token(JavaFXTokenId.TIME_LITERAL); // "<time>ms"
                    inputBackup(1);
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>m"
                case 's': 
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>s"
                default:
                    inputBackup(1);
//                    return token(inFraction ? JavaFXTokenId.DOUBLE_LITERAL
//                            : JavaFXTokenId.INT_LITERAL);
                    return token(JavaFXTokenId.FLOATING_POINT_LITERAL);
            }
            c = inputRead();
        }
    }
    
    private Token<JavaFXTokenId> finishFloatExponent() {
        int c = inputRead();
        if (c == '+' || c == '-') {
            c = inputRead();
        }
        if (c < '0' || '9' < c)
            return token(JavaFXTokenId.FLOATING_POINT_LITERAL_INVALID);
        do {
            c = inputRead();
        } while ('0' <= c && c <= '9'); // reading exponent
        switch (c) {
            case 'h': 
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>h"
            case 'm':
                if((c = inputRead()) == 's') 
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>ms"
                inputBackup(1);
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>m"
            case 's': 
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>s"
            default:
                inputBackup(1);
                return token(JavaFXTokenId.FLOATING_POINT_LITERAL);
        }
    }
    
}
