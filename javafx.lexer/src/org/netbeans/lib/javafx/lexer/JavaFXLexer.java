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

import java.util.Stack;
import org.netbeans.api.javafx.lexer.JavaFXTokenId;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexical analyzer for the JavaFX language.
 * <br/>
 * It recognizes "version" attribute and expects <code>java.lang.Integer</code>
 * value for it. The default value is Integer.valueOf(1). 
 * <p><b>Note:</b>This version doesn't rely on the version value 
 * and doesn't change its behavior.</p>
 *
 * @author Miloslav Metelka
 * @author Victor G. Vasilyev
 * @version 1.00
 * 
 * @todo convertUnicode
 * @todo NextIsPercent
 */

public class JavaFXLexer implements Lexer<JavaFXTokenId> {
    
    private static final int SINGLE_QUOTE = '\'';
    private static final int DOUBLE_QUOTE = '"';
            
    private static final int EOF = LexerInput.EOF;

    private final LexerInput input;
    
    private final TokenFactory<JavaFXTokenId> tokenFactory;
    
    private final int version;

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private BraceQuoteTracker quoteStack;
    
    /** Track "He{"l{"l"}o"} world" quotes
     */
    private static class BraceQuoteTracker {
        private int braceDepth;
        private char quote;
        private boolean percentIsFormat;
        private BraceQuoteTracker next;
        private BraceQuoteTracker(BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
            this.quote = quote;
            this.percentIsFormat = percentIsFormat;
            this.braceDepth = 1;
            this.next = prev;
        }
    }
    
    private void enterBrace(int quote, boolean percentIsFormat) {
        if (quote == 0) {  // exisiting string expression or non string expression
            if (quoteStack != null) {
                ++quoteStack.braceDepth;
                quoteStack.percentIsFormat = percentIsFormat;
            }
        } else {
            quoteStack = new BraceQuoteTracker(quoteStack, (char) quote, percentIsFormat); // push
        }
    }

    /** Return quote kind if we are reentering a quote
     * */
    private char leaveBrace() {
        if (quoteStack != null && --quoteStack.braceDepth == 0) {
            return quoteStack.quote;
        }
        return 0;
    }

    private boolean rightBraceLikeQuote(int quote) {
        return quoteStack != null && quoteStack.braceDepth == 1 && 
                    (quote == 0 || quoteStack.quote == (char) quote);
    }

    private void leaveQuote() {
        assert (quoteStack != null && quoteStack.braceDepth == 0);
        quoteStack = quoteStack.next; // pop
    }

    private boolean percentIsFormat() {
        return quoteStack != null && quoteStack.percentIsFormat;
    }

    private void resetPercentIsFormat() {
        quoteStack.percentIsFormat = false;
    }

    private boolean inBraceQuote() {
        return quoteStack != null;
    }

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public JavaFXLexer(LexerRestartInfo<JavaFXTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();

//        assert (info.state() == null); // never set to non-null value in state()
        this.quoteStack = (BraceQuoteTracker)info.state();
        
        Integer ver = (Integer)info.getAttributeValue("version");
        this.version = (ver != null) ? ver.intValue() : 1; // Use JavaFX 1.0 by default
    }
    
    public Object state() {
//        return null; // always in default state after token recognition
        return (Object)quoteStack;
    }
    
    public Token<JavaFXTokenId> nextToken() {
        while(true) {
            int c = input.read();
            switch (c) {
                case DOUBLE_QUOTE: // string literal
                case SINGLE_QUOTE: // string literal
                    return processString(c);
                case '#':
                    if(input.read() == '#') {               
                        if(input.read() == '[') {
                            while (true)
                                switch (input.read()) {
                                    case ']':
                                    case EOF:
                                        return token(JavaFXTokenId.TRANSLATION_KEY);
                                }
                        } else {
                            input.backup(1);                            
                        }
                        return token(JavaFXTokenId.TRANSLATION_KEY);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.POUND);
                case '-':
                    switch (input.read()) {
                        case '-':
                            return token(JavaFXTokenId.MINUSMINUS);
                        case '=':
                            return token(JavaFXTokenId.MINUSEQ);
                    }
                    input.backup(1);
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
                    if ((c = input.read()) == '.') {
                        return token(JavaFXTokenId.DOTDOT);
                    } else if ('0' <= c && c <= '9') { // float literal
                        return finishNumberLiteral(input.read(), true);
                    } else {
                        input.backup(1);
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
                    enterBrace(0, false);
                    return token(JavaFXTokenId.LBRACE);
                case '}':
                    if(!rightBraceLikeQuote(0)) {
                        // case 1: end of usual block
                        leaveBrace();
                        return token(JavaFXTokenId.RBRACE);
                    }
                    if(rightBraceLikeQuote(DOUBLE_QUOTE)) {
                       // case 2: end of a block inside "string literal"
                       return processString(DOUBLE_QUOTE); 
                    }
                    if(rightBraceLikeQuote(SINGLE_QUOTE)) {
                       // case 2: end of a block inside 'string literal'
                       return processString(SINGLE_QUOTE); 
                    }
                case '*':
                    switch (input.read()) {
                        case '/': // invalid comment end - */
                            return token(JavaFXTokenId.INVALID_COMMENT_END);
                        case '=':
                            return token(JavaFXTokenId.STAREQ);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.STAR);
                case '/':
                    switch (input.read()) {
                        case '=': // found /=
                            return token(JavaFXTokenId.SLASHEQ);
                        case '/': // in single-line comment
                            while (true)
                                switch (input.read()) {
                                    case '\r': input.consumeNewline();
                                    case '\n':
                                    case EOF:
                                        return token(JavaFXTokenId.LINE_COMMENT);
                                }
                        case '*': // in multi-line or javadoc comment
                            c = input.read();
                            if (c == '*') { // either javadoc comment or empty multi-line comment /**/
                                    c = input.read();
                                    if (c == '/')
                                        return token(JavaFXTokenId.BLOCK_COMMENT);
                                    while (true) { // in javadoc comment
                                        while (c == '*') {
                                            c = input.read();
                                            if (c == '/')
                                                return token(JavaFXTokenId.JAVADOC_COMMENT);
                                            else if (c == EOF)
                                                return tokenFactory.createToken(JavaFXTokenId.JAVADOC_COMMENT,
                                                        input.readLength(), PartType.START);
                                        }
                                        if (c == EOF)
                                            return tokenFactory.createToken(JavaFXTokenId.JAVADOC_COMMENT,
                                                        input.readLength(), PartType.START);
                                        c = input.read();
                                    }

                            } else { // in multi-line comment (and not after '*')
                                while (true) {
                                    c = input.read();
                                    while (c == '*') {
                                        c = input.read();
                                        if (c == '/')
                                            return token(JavaFXTokenId.BLOCK_COMMENT);
                                        else if (c == EOF)
                                            return tokenFactory.createToken(JavaFXTokenId.BLOCK_COMMENT,
                                                    input.readLength(), PartType.START);
                                    }
                                    if (c == EOF)
                                        return tokenFactory.createToken(JavaFXTokenId.BLOCK_COMMENT,
                                                input.readLength(), PartType.START);
                                }
                            }
                    } // end of switch()
                    input.backup(1);
                    return token(JavaFXTokenId.SLASH);
                case '%':
                    if (input.read() == '=')
                        return token(JavaFXTokenId.PERCENTEQ);
                    input.backup(1);
                    return token(JavaFXTokenId.PERCENT);
                case '+':
                    switch (input.read()) {
                        case '+':
                            return token(JavaFXTokenId.PLUSPLUS);
                        case '=':
                            return token(JavaFXTokenId.PLUSEQ);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.PLUS);
                case '<':
                    switch (input.read()) {
                        case '=': // <=
                            return token(JavaFXTokenId.LTEQ);
                        case '>': // <>
                            return token(JavaFXTokenId.LTGT);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.LT);
                case '=':
                    switch (input.read()) {
                        case '=': // ==
                            return token(JavaFXTokenId.EQEQ);
                        case '>': // =>
                            return token(JavaFXTokenId.SUCHTHAT);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.EQ);
                case '>':
                    switch (input.read()) {
                        case '=': // >=
                            return token(JavaFXTokenId.GTEQ);
                    }
                    input.backup(1);
                    return token(JavaFXTokenId.GT);
                case '|':
                    return token(JavaFXTokenId.PIPE);

                case '0': // "0" in a number literal or a time literal
		    c = input.read();
                    if (c == 'x' || c == 'X') { // in hexadecimal (possibly floating-point) literal
                        boolean inFraction = false;
                        while (true) {
                            switch (input.read()) {
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
                                    input.backup(1);
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
                    return finishNumberLiteral(input.read(), false);

                    
                // Keywords lexing    
                case 'a':
                    switch (c = input.read()) {
                        case 'b':
                            if ((c = input.read()) == 's'
                             && (c = input.read()) == 't'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 'a'
                             && (c = input.read()) == 'c'
                             && (c = input.read()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.ABSTRACT);
                            break;
                        case 'f':
                            if ((c = input.read()) == 't'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.AFTER);
                            break;
                        case 'n':
                            if ((c = input.read()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.AND);
                            break;
                        case 's': 
                            if(Character.isWhitespace(c = input.read())) {
                                input.backup(1);
                                return keywordOrIdentifier(JavaFXTokenId.AS);
                            }
                            if (c == 's'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.ASSERT);
                            break;
                        case 't':
                            if ((c = input.read()) == 't'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 'b'
                             && (c = input.read()) == 'u'
                             && (c = input.read()) == 't'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.ATTRIBUTE);
                            break;
                    }
                    return finishIdentifier(c);

                case 'b':
                    switch (c = input.read()) {
                        case 'e':
                            if ((c = input.read()) == 'f'
                             && (c = input.read()) == 'o'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.BEFORE);
                            break;
                        case 'i':
                            if ((c = input.read()) == 'n'
                             && (c = input.read()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.BIND);
                            break;
                        case 'o':
                            if ((c = input.read()) == 'u'
                             && (c = input.read()) == 'n'
                             && (c = input.read()) == 'd')
                                return keywordOrIdentifier(JavaFXTokenId.BOUND);
                            break;
                        case 'r':
                            if ((c = input.read()) == 'e'
                             && (c = input.read()) == 'a'
                             && (c = input.read()) == 'k')
                                return keywordOrIdentifier(JavaFXTokenId.BREAK);
                            break;
                    }
                    return finishIdentifier(c);

                case 'c':
                    switch (c = input.read()) {
                        case 'a':
                            switch (c = input.read()) {
                                case 't':
                                    if ((c = input.read()) == 'c'
                                     && (c = input.read()) == 'h')
                                        return keywordOrIdentifier(JavaFXTokenId.CATCH);
                                    break;
                            }
                            break;
                        case 'l':
                            if ((c = input.read()) == 'a'
                             && (c = input.read()) == 's'
                             && (c = input.read()) == 's')
                                return keywordOrIdentifier(JavaFXTokenId.CLASS);
                            break;
                        case 'o':
                            if ((c = input.read()) == 'n' 
                             && (c = input.read()) == 't' 
                             && (c = input.read()) == 'i' 
                             && (c = input.read()) == 'n' 
                             && (c = input.read()) == 'u' 
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.CONTINUE);
                    }
                    return finishIdentifier(c);

                case 'd':
                    if ((c = input.read()) == 'e' 
                     && (c = input.read()) == 'l' 
                     && (c = input.read()) == 'e' 
                     && (c = input.read()) == 't' 
                     && (c = input.read()) == 'e')
                        return keywordOrIdentifier(JavaFXTokenId.DELETE);
                    return finishIdentifier(c);

                case 'e':
                    switch (c = input.read()) {
                        case 'l':
                            if ((c = input.read()) == 's'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.ELSE);
                            break;
                        case 'x':
                            switch (c = input.read()) {
                                case 'c':
                                    if ((c = input.read()) == 'l'
                                     && (c = input.read()) == 'u'
                                     && (c = input.read()) == 's'
                                     && (c = input.read()) == 'i'
                                     && (c = input.read()) == 'v'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.EXCLUSIVE);
                                    break;
                                case 't':
                                    if ((c = input.read()) == 'e'
                                     && (c = input.read()) == 'n'
                                     && (c = input.read()) == 'd'
                                     && (c = input.read()) == 's')
                                        return keywordOrIdentifier(JavaFXTokenId.EXTENDS);
                                    break;
                            }
                    }
                    return finishIdentifier(c);

                case 'f':
                    switch (c = input.read()) {
                        case 'a': // "fa"
                            if ((c = input.read()) == 'l'
                             && (c = input.read()) == 's'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.FALSE);
                            break;
                        case 'i': // "fi"
                            switch (c = input.read()) {
                                case 'n':
                                    if ((c = input.read()) == 'a'
                                     && (c = input.read()) == 'l'
                                     && (c = input.read()) == 'l'
                                     && (c = input.read()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.FINALLY);
                                    break;
                                case 'r':
                                    if ((c = input.read()) == 's'
                                     && (c = input.read()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.FIRST);
                                    break;
                            }
                            break;
                        case 'o':
                            if ((c = input.read()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.FOR);
                            break;
                        case 'r':
                            if ((c = input.read()) == 'o'
                             && (c = input.read()) == 'm')
                                return keywordOrIdentifier(JavaFXTokenId.FROM);
                            break;
                        case 'u':
                            if ((c = input.read()) == 'n'
                             && (c = input.read()) == 'c'
                             && (c = input.read()) == 't'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 'o'
                             && (c = input.read()) == 'n')
                                return keywordOrIdentifier(JavaFXTokenId.FUNCTION);
                            break;
                    }
                    return finishIdentifier(c);
                case 'i': // "i"
                    switch (c = input.read()) {
                        case 'f':
                            return keywordOrIdentifier(JavaFXTokenId.IF);
                        case 'm':
                            if ((c = input.read()) == 'p'
                             && (c = input.read()) == 'o'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 't') {
                                return keywordOrIdentifier(JavaFXTokenId.IMPORT);
                            }
                            break;
                        case 'n': // "in"
                            if(Character.isWhitespace(c = input.read())) {
                                input.backup(1);
                                return keywordOrIdentifier(JavaFXTokenId.IN);
                            }
                            switch (c) {
                                case 'd':  // "ind"
                                    if ((c = input.read()) == 'e'
                                     && (c = input.read()) == 'x'
                                     && (c = input.read()) == 'o'
                                     && (c = input.read()) == 'f')
                                        return keywordOrIdentifier(JavaFXTokenId.INDEXOF);
                                    break;
                                case 'i': // "ini"
                                    if ((c = input.read()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.INIT);
                                    break;
                                case 's': // "ins"
                                    switch(c = input.read()) {
                                        case 'e': // "inse"
                                            if ((c = input.read()) == 'r'
                                             && (c = input.read()) == 't')
                                                return keywordOrIdentifier(JavaFXTokenId.INSERT);
                                            break;
                                        case 't': // "inst"
                                            if ((c = input.read()) == 'a'
                                             && (c = input.read()) == 'n'
                                             && (c = input.read()) == 'c'
                                             && (c = input.read()) == 'e'
                                             && (c = input.read()) == 'o'
                                             && (c = input.read()) == 'f')
                                                return keywordOrIdentifier(JavaFXTokenId.INSTANCEOF);
                                            break;
                                    }
                                    break;
                                case 't': // "int"
                                    if ((c = input.read()) == 'o')
                                        return keywordOrIdentifier(JavaFXTokenId.INTO);
                                    break;
                                case 'v': // "inv"
                                    if ((c = input.read()) == 'e'
                                     && (c = input.read()) == 'r'
                                     && (c = input.read()) == 's'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.INVERSE);
                                    break;
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'l':
                    switch (c = input.read()) {
                        case 'a':
                            switch (c = input.read()) {
                                case 's':
                                    if ((c = input.read()) == 't')
                                        return keywordOrIdentifier(JavaFXTokenId.LAST);
                                    break;
                                case 'z':
                                    if ((c = input.read()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.LAZY);
                                    break;
                            }
                            break;
                        case 'e':
                            if ((c = input.read()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.LET);
                            break;
                    }
                    return finishIdentifier(c);

                case 'n':
                    switch (c = input.read()) {
                        case 'o':
                            if ((c = input.read()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.NOT);
                            break;
                        case 'e':
                            if ((c = input.read()) == 'w')
                                return keywordOrIdentifier(JavaFXTokenId.NEW);
                            break;
                        case 'u':
                            if ((c = input.read()) == 'l'
                             && (c = input.read()) == 'l')
                                return keywordOrIdentifier(JavaFXTokenId.NULL);
                            break;
                    }
                    return finishIdentifier(c);

                case 'o':
                    switch (c = input.read()) {
                        case 'n':
                            return keywordOrIdentifier(JavaFXTokenId.ON);
                        case 'r':
                            return keywordOrIdentifier(JavaFXTokenId.OR);
                        case 'v':
                            if ((c = input.read()) == 'e'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 'r'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 'd'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.OVERRIDE);
                            break;
                    }
                    return finishIdentifier(c);

                case 'p':
                    switch (c = input.read()) {
                        case 'a':
                            if ((c = input.read()) == 'c'
                             && (c = input.read()) == 'k'
                             && (c = input.read()) == 'a'
                             && (c = input.read()) == 'g'
                             && (c = input.read()) == 'e')
                                return keywordOrIdentifier(JavaFXTokenId.PACKAGE);
                            break;
                        case 'o':
                            if ((c = input.read()) == 's'
                             && (c = input.read()) == 't'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 'n'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 't')
                                return keywordOrIdentifier(JavaFXTokenId.POSTINIT);
                            break;
                        case 'r':
                            switch (c = input.read()) {
                                case 'i':
                                    if ((c = input.read()) == 'v'
                                     && (c = input.read()) == 'a'
                                     && (c = input.read()) == 't'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.PRIVATE);
                                    break;
                                case 'o':
                                    if ((c = input.read()) == 't'
                                     && (c = input.read()) == 'e'
                                     && (c = input.read()) == 'c'
                                     && (c = input.read()) == 't'
                                     && (c = input.read()) == 'e'
                                     && (c = input.read()) == 'd')
                                        return keywordOrIdentifier(JavaFXTokenId.PROTECTED);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = input.read()) == 'b'
                             && (c = input.read()) == 'l'
                             && (c = input.read()) == 'i'
                             && (c = input.read()) == 'c')
                                return keywordOrIdentifier(JavaFXTokenId.PUBLIC);
                            break;
                    }
                    return finishIdentifier(c);

                case 'r': // "r"
                    switch (c = input.read()) {
                        case 'e': // "re"
                            switch (c = input.read()) {
                                case 'a': // "rea"
                                    if ((c = input.read()) == 'd'
                                     && (c = input.read()) == 'o'
                                     && (c = input.read()) == 'n'
                                     && (c = input.read()) == 'l'
                                     && (c = input.read()) == 'y')
                                        return keywordOrIdentifier(JavaFXTokenId.READONLY);
                                    break;
                                case 'p': // "rep"
                                    if ((c = input.read()) == 'l'
                                     && (c = input.read()) == 'a'
                                     && (c = input.read()) == 'c'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.REPLACE);
                                    break;
                                case 't': // "ret"
                                    if ((c = input.read()) == 'u'
                                     && (c = input.read()) == 'r'
                                     && (c = input.read()) == 'n')
                                        return keywordOrIdentifier(JavaFXTokenId.RETURN);
                                    break;
                                case 'v': // "rev"
                                    if ((c = input.read()) == 'e'
                                     && (c = input.read()) == 'r'
                                     && (c = input.read()) == 's'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.REVERSE);
                                    break;
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 's':
                    switch (c = input.read()) {
                        case 'i':
                            if ((c = input.read()) == 'z'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'o'
                             && (c = input.read()) == 'f')
                                return keywordOrIdentifier(JavaFXTokenId.SIZEOF);
                            break;
                        case 't':
                            switch (c = input.read()) {
                                case 'a':
                                    if ((c = input.read()) == 't'
                                     && (c = input.read()) == 'i'
                                     && (c = input.read()) == 'c')
                                        return keywordOrIdentifier(JavaFXTokenId.STATIC);
                                    break;
                                case 'e': // "ste"
                                    if ((c = input.read()) == 'p')
                                        return keywordOrIdentifier(JavaFXTokenId.STEP);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = input.read()) == 'p'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'r')
                                return keywordOrIdentifier(JavaFXTokenId.SUPER);
                            break;
                    }
                    return finishIdentifier(c);

                case 't': // "t"
                    switch (c = input.read()) {
                        case 'h': // "th"
                            switch (c = input.read()) {
                                case 'e': // "the"
                                    if ((c = input.read()) == 'n')
                                        return keywordOrIdentifier(JavaFXTokenId.THEN);
                                    break;
                                case 'i': // "thi"
                                    if ((c = input.read()) == 's')
                                        return keywordOrIdentifier(JavaFXTokenId.THIS);
                                    break;
                                case 'r': // "thr"
                                    if ((c = input.read()) == 'o'
                                     && (c = input.read()) == 'w')
                                        return keywordOrIdentifier(JavaFXTokenId.THROW);
                                    break;
                            }
                            break;
                        case 'r': // "tr"
                            switch (c = input.read()) {
                                case 'u': // "tru"
                                    if ((c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.TRUE);
                                    break;
                                case 'y': // "try"
                                    return keywordOrIdentifier(JavaFXTokenId.TRY);
                            }
                            break;
                        case 'w': // "tw"
                            if ((c = input.read()) == 'e'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'n')
                                return keywordOrIdentifier(JavaFXTokenId.TWEEN);
                            break;
                        case 'y': // "ty"
                            if ((c = input.read()) == 'p'
                             && (c = input.read()) == 'e'
                             && (c = input.read()) == 'o'
                             && (c = input.read()) == 'f')
                                return keywordOrIdentifier(JavaFXTokenId.TYPEOF);
                            break;
                    }
                    return finishIdentifier(c);

                case 'v':
                    if ((c = input.read()) == 'a'
                     && (c = input.read()) == 'r')
                        return keywordOrIdentifier(JavaFXTokenId.VAR);
                    return finishIdentifier(c);

                case 'w':
                    switch (c = input.read()) {
                        case 'h': // "wh"
                            switch (c = input.read()) {
                                case 'e': // "whe"
                                    if ((c = input.read()) == 'r'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.WHERE);
                                case 'i': // "whi"
                                    if ((c = input.read()) == 'l'
                                     && (c = input.read()) == 'e')
                                        return keywordOrIdentifier(JavaFXTokenId.WHILE);
                                    break;
                            }
                            break;
                        case 'i': // "wi"
                            if ((c = input.read()) == 't'
                             && (c = input.read()) == 'h')
                                return keywordOrIdentifier(JavaFXTokenId.WITH);
                            break;
                    }
                    return finishIdentifier(c);

                // Rest of lowercase letters starting identifiers
                case 'h': case 'j': case 'k': case 'm': 
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
                    c = input.read();
                    if (c == EOF || !Character.isWhitespace(c)) { // Return single space as flyweight token
                        input.backup(1);
                        return tokenFactory.getFlyweightToken(JavaFXTokenId.WHITESPACE, " ");
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
    
    private Token<JavaFXTokenId> processString(int quote) {
        assert (quote == '\'' || quote == '"');
        while (true) {
            int c = input.read();
            switch (c) {
                case '\'': // NOI18N
                case '"': // NOI18N
                    if (quote == c) {
                        return token(JavaFXTokenId.STRING_LITERAL);
                    }
                case '\\':
                    input.read();
                    break;
                case '\r':
                    input.consumeNewline();
                case '\n':
                    input.read(); // enable the multi-line string literals.
                    break;
                case EOF: // incompleted string literal, i.e. under development.
                    return tokenFactory.createToken(JavaFXTokenId.STRING_LITERAL,
                            input.readLength(), PartType.START);
                case '{':
                    enterBrace(quote, false);
                    return tokenFactory.createToken(
                            JavaFXTokenId.QUOTE_LBRACE_STRING_LITERAL,
                            input.readLength(), PartType.START);
            }
        }
    }
    
    private int translateSurrogates(int c) {
        if (Character.isHighSurrogate((char)c)) {
            int lowSurr = input.read();
            if (lowSurr != EOF && Character.isLowSurrogate((char)lowSurr)) {
                // c and lowSurr form the integer unicode char.
                c = Character.toCodePoint((char)c, (char)lowSurr);
            } else {
                // Otherwise it's error: Low surrogate does not follow the high one.
                // Leave the original character unchanged.
                // As the surrogates do not belong to any
                // specific unicode category the lexer should finally
                // categorize them as a lexical error.
                input.backup(1);
            }
        }
        return c;
    }

    private Token<JavaFXTokenId> finishWhitespace() {
        while (true) {
            int c = input.read();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (c == EOF || !Character.isWhitespace(c)) {
                input.backup(1);
                return tokenFactory.createToken(JavaFXTokenId.WHITESPACE);
            }
        }
    }
    
    private Token<JavaFXTokenId> finishIdentifier() {
        return finishIdentifier(input.read());
    }
    
    private Token<JavaFXTokenId> finishIdentifier(int c) {
        while (true) {
            if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
                // For surrogate 2 chars must be backed up
                input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactory.createToken(JavaFXTokenId.IDENTIFIER);
            }
            c = input.read();
        }
    }

    private Token<JavaFXTokenId> keywordOrIdentifier(JavaFXTokenId keywordId) {
        return keywordOrIdentifier(keywordId, input.read());
    }

    private Token<JavaFXTokenId> keywordOrIdentifier(JavaFXTokenId keywordId, int c) {
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
            // For surrogate 2 chars must be backed up
            input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
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
                    if((c = input.read()) == 's') 
                        return token(JavaFXTokenId.TIME_LITERAL); // "<time>ms"
                    input.backup(1);
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>m"
                case 's': 
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>s"
                default:
                    input.backup(1);
//                    return token(inFraction ? JavaFXTokenId.DOUBLE_LITERAL
//                            : JavaFXTokenId.INT_LITERAL);
                    return token(JavaFXTokenId.FLOATING_POINT_LITERAL);
            }
            c = input.read();
        }
    }
    
    private Token<JavaFXTokenId> finishFloatExponent() {
        int c = input.read();
        if (c == '+' || c == '-') {
            c = input.read();
        }
        if (c < '0' || '9' < c)
            return token(JavaFXTokenId.FLOATING_POINT_LITERAL_INVALID);
        do {
            c = input.read();
        } while ('0' <= c && c <= '9'); // reading exponent
        switch (c) {
            case 'h': 
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>h"
            case 'm':
                if((c = input.read()) == 's') 
                    return token(JavaFXTokenId.TIME_LITERAL); // "<time>ms"
                input.backup(1);
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>m"
            case 's': 
                return token(JavaFXTokenId.TIME_LITERAL); // "<time>s"
            default:
                input.backup(1);
                return token(JavaFXTokenId.FLOATING_POINT_LITERAL);
        }
    }
    
    private Token<JavaFXTokenId> token(JavaFXTokenId id) {
        String fixedText = id.fixedText();
        return (fixedText != null)
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }
    
    public void release() {
    }

}
