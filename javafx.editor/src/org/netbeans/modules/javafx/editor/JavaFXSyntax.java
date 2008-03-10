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

package org.netbeans.modules.javafx.editor;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
* Syntax analyzes for Java source files.
* Tokens and internal states are given below.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class JavaFXSyntax extends Syntax {

    // Internal states
    private static final int ISI_WHITESPACE = 2; // inside white space
    private static final int ISI_LINE_COMMENT = 4; // inside line comment //
    private static final int ISI_BLOCK_COMMENT = 5; // inside block comment /* ... */
    private static final int ISI_STRING = 6; // inside string constant
    private static final int ISI_STRING_A_BSLASH = 7; // inside string constant after backslash
    private static final int ISI_CHAR = 8; // inside char constant
    private static final int ISI_CHAR_A_BSLASH = 9; // inside char constant after backslash
    private static final int ISI_IDENTIFIER = 10; // inside identifier
    private static final int ISA_SLASH = 11; // slash char
    private static final int ISA_EQ = 12; // after '='
    private static final int ISA_GT = 13; // after '>'
    private static final int ISA_GTGT = 14; // after '>>'
    private static final int ISA_GTGTGT = 15; // after '>>>'
    private static final int ISA_LT = 16; // after '<'
    private static final int ISA_LTLT = 17; // after '<<'
    private static final int ISA_PLUS = 18; // after '+'
    private static final int ISA_MINUS = 19; // after '-'
    private static final int ISA_STAR = 20; // after '*'
    private static final int ISA_STAR_I_BLOCK_COMMENT = 21; // after '*'
    private static final int ISA_PIPE = 22; // after '|'
    private static final int ISA_PERCENT = 23; // after '%'
    private static final int ISA_AND = 24; // after '&'
    private static final int ISA_XOR = 25; // after '^'
    private static final int ISA_EXCLAMATION = 26; // after '!'
    private static final int ISA_ZERO = 27; // after '0'
    private static final int ISI_INT = 28; // integer number
    private static final int ISI_OCTAL = 29; // octal number
    private static final int ISI_DOUBLE = 30; // double number
    private static final int ISI_DOUBLE_EXP = 31; // double number
    private static final int ISI_HEX = 32; // hex number
    private static final int ISA_DOT = 33; // after '.'

    private boolean isJava15 = true;

    //when set to true, the parser divides java block comment by lines (a performance fix of #55628 for JSPs)
    private boolean useInJsp = false; 
    
    public JavaFXSyntax() {
        tokenContextPath = JavaFXTokenContext.contextPath;
    }

    public JavaFXSyntax(String sourceLevel) {
        this();
        if (sourceLevel != null) {
            try {
                isJava15 = Float.parseFloat(sourceLevel) >= 1.5;
            } catch (NumberFormatException e) {
                // leave the default
            }
        }
    }
     
    public JavaFXSyntax(String sourceLevel, boolean useInJsp) {
        this(sourceLevel);
        this.useInJsp = useInJsp;
    }

    protected TokenID parseToken() {
        char actChar;

        while(offset < stopOffset) {
            actChar = buffer[offset];

            switch (state) {
            case INIT:
                switch (actChar) {
                case '"': // NOI18N
                    state = ISI_STRING;
                    break;
                case '\'':
                    state = ISI_CHAR;
                    break;
                case '/':
                    state = ISA_SLASH;
                    break;
                case '=':
                    state = ISA_EQ;
                    break;
                case '>':
                    state = ISA_GT;
                    break;
                case '<':
                    state = ISA_LT;
                    break;
                case '+':
                    state = ISA_PLUS;
                    break;
                case '-':
                    state = ISA_MINUS;
                    break;
                case '*':
                    state = ISA_STAR;
                    break;
                case '|':
                    state = ISA_PIPE;
                    break;
                case '%':
                    state = ISA_PERCENT;
                    break;
                case '&':
                    state = ISA_AND;
                    break;
                case '^':
                    state = ISA_XOR;
                    break;
                case '~':
                    offset++;
                    return JavaFXTokenContext.NEG;
                case '!':
                    state = ISA_EXCLAMATION;
                    break;
                case '0':
                    state = ISA_ZERO;
                    break;
                case '.':
                    state = ISA_DOT;
                    break;
                case ',':
                    offset++;
                    return JavaFXTokenContext.COMMA;
                case ';':
                    offset++;
                    return JavaFXTokenContext.SEMICOLON;
                case ':':
                    offset++;
                    return JavaFXTokenContext.COLON;
                case '?':
                    offset++;
                    return JavaFXTokenContext.QUESTION;
                case '(':
                    offset++;
                    return JavaFXTokenContext.LPAREN;
                case ')':
                    offset++;
                    return JavaFXTokenContext.RPAREN;
                case '[':
                    offset++;
                    return JavaFXTokenContext.LBRACKET;
                case ']':
                    offset++;
                    return JavaFXTokenContext.RBRACKET;
                case '{':
                    offset++;
                    return JavaFXTokenContext.LBRACE;
                case '}':
                    offset++;
                    return JavaFXTokenContext.RBRACE;
                case '@': // 1.5 "@ident" annotation // NOI18N
                    offset++;
                    return JavaFXTokenContext.ANNOTATION;

                default:
                    // Check for whitespace
                    if (Character.isWhitespace(actChar)) {
                        state = ISI_WHITESPACE;
                        break;
                    }

                    // Check for digit
                    if (Character.isDigit(actChar)) {
                        state = ISI_INT;
                        break;
                    }

                    // Check for identifier
                    if (Character.isJavaIdentifierStart(actChar)) {
                        state = ISI_IDENTIFIER;
                        break;
                    }

                    offset++;
                    return JavaFXTokenContext.INVALID_CHAR;
                }
                break;

            case ISI_WHITESPACE: // white space
                if (!Character.isWhitespace(actChar)) {
                    state = INIT;
                    return JavaFXTokenContext.WHITESPACE;
                }
                break;

            case ISI_LINE_COMMENT:
                switch (actChar) {
                case '\n':
                    state = INIT;
                    return JavaFXTokenContext.LINE_COMMENT;
                }
                break;

            case ISI_BLOCK_COMMENT:
                switch (actChar) {
                case '*':
                    state = ISA_STAR_I_BLOCK_COMMENT;
                    break;
                //create a block comment token for each line of the comment - a performance fix for #55628
                case '\n':
                    if(useInJsp) {
                        //leave the some state - we are still in the block comment,
                        //we just need to create a token for each line.
                        offset++;
                        return JavaFXTokenContext.BLOCK_COMMENT;
                    }
                }
                break;

            case ISI_STRING:
                switch (actChar) {
                case '\\':
                    state = ISI_STRING_A_BSLASH;
                    break;
                case '\n':
                    state = INIT;
                    supposedTokenID = JavaFXTokenContext.STRING_LITERAL;
//!!!                    return JavaTokenContext.INCOMPLETE_STRING_LITERAL;
                    return supposedTokenID;
                case '"': // NOI18N
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.STRING_LITERAL;
                }
                break;

            case ISI_STRING_A_BSLASH:
                switch (actChar) {
                case '"': // NOI18N
                case '\\':
                    break;
                default:
                    offset--;
                    break;
                }
                state = ISI_STRING;
                break;

            case ISI_CHAR:
                switch (actChar) {
                case '\\':
                    state = ISI_CHAR_A_BSLASH;
                    break;
                case '\n':
                    state = INIT;
                    supposedTokenID = JavaFXTokenContext.CHAR_LITERAL;
// !!!                    return JavaTokenContext.INCOMPLETE_CHAR_LITERAL;
                    return supposedTokenID;
                case '\'':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.CHAR_LITERAL;
                }
                break;

            case ISI_CHAR_A_BSLASH:
                switch (actChar) {
                case '\'':
                case '\\':
                    break;
                default:
                    offset--;
                    break;
                }
                state = ISI_CHAR;
                break;

            case ISI_IDENTIFIER:
                if (!(Character.isJavaIdentifierPart(actChar))) {
                    state = INIT;
                    TokenID tid = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                    return (tid != null) ? tid : JavaFXTokenContext.IDENTIFIER;
                }
                break;
                
            case ISA_SLASH:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.DIV_EQ;
                case '/':
                    state = ISI_LINE_COMMENT;
                    break;
                case '*':
                    state = ISI_BLOCK_COMMENT;
                    break;
                default:
                    state = INIT;
                    return JavaFXTokenContext.DIV;
                }
                break;

            case ISA_EQ:
                switch (actChar) {
                case '=':
                    offset++;
                    return  JavaFXTokenContext.EQ_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.EQ;
                }
                // break;

            case ISA_GT:
                switch (actChar) {
                case '>':
                    state = ISA_GTGT;
                    break;
                case '=':
                    offset++;
                    return JavaFXTokenContext.GT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.GT;
                }
                break;

            case ISA_GTGT:
                switch (actChar) {
                case '>':
                    state = ISA_GTGTGT;
                    break;
                case '=':
                    offset++;
                    return JavaFXTokenContext.RSSHIFT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.RSSHIFT;
                }
                break;

            case ISA_GTGTGT:
                switch (actChar) {
                case '=':
                    offset++;
                    return JavaFXTokenContext.RUSHIFT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.RUSHIFT;
                }
                // break;


            case ISA_LT:
                switch (actChar) {
                case '<':
                    state = ISA_LTLT;
                    break;
                case '=':
                    offset++;
                    return JavaFXTokenContext.LT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.LT;
                }
                break;

            case ISA_LTLT:
                switch (actChar) {
                case '<':
                    state = INIT;
                    offset++;
                    return JavaFXTokenContext.INVALID_OPERATOR;
                case '=':
                    offset++;
                    return JavaFXTokenContext.LSHIFT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.LSHIFT;
                }

            case ISA_PLUS:
                switch (actChar) {
                case '+':
                    offset++;
                    return JavaFXTokenContext.PLUS_PLUS;
                case '=':
                    offset++;
                    return JavaFXTokenContext.PLUS_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.PLUS;
                }

            case ISA_MINUS:
                switch (actChar) {
                case '-':
                    offset++;
                    return JavaFXTokenContext.MINUS_MINUS;
                case '=':
                    offset++;
                    return JavaFXTokenContext.MINUS_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.MINUS;
                }

            case ISA_STAR:
                switch (actChar) {
                case '=':
                    offset++;
                    return JavaFXTokenContext.MUL_EQ;
                case '/':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.INVALID_COMMENT_END; // '*/' outside comment
                default:
                    state = INIT;
                    return JavaFXTokenContext.MUL;
                }

            case ISA_STAR_I_BLOCK_COMMENT:
                switch (actChar) {
                case '/':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.BLOCK_COMMENT;
                default:
                    offset--;
                    state = ISI_BLOCK_COMMENT;
                    break;
                }
                break;

            case ISA_PIPE:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.OR_EQ;
                case '|':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.OR_OR;
                default:
                    state = INIT;
                    return JavaFXTokenContext.OR;
                }
                // break;

            case ISA_PERCENT:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.MOD_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.MOD;
                }
                // break;

            case ISA_AND:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.AND_EQ;
                case '&':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.AND_AND;
                default:
                    state = INIT;
                    return JavaFXTokenContext.AND;
                }
                // break;

            case ISA_XOR:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.XOR_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.XOR;
                }
                // break;

            case ISA_EXCLAMATION:
                switch (actChar) {
                case '=':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.NOT_EQ;
                default:
                    state = INIT;
                    return JavaFXTokenContext.NOT;
                }
                // break;

            case ISA_ZERO:
                switch (actChar) {
                case '.':
                    state = ISI_DOUBLE;
                    break;
                case 'x':
                case 'X':
                    state = ISI_HEX;
                    break;
                case 'l':
                case 'L':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.LONG_LITERAL;
                case 'f':
                case 'F':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.FLOAT_LITERAL;
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.DOUBLE_LITERAL;
                case '8': // it's error to have '8' and '9' in octal number
                case '9':
                    state = INIT;
                    offset++;
                    return JavaFXTokenContext.INVALID_OCTAL_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_DOUBLE_EXP;
                    break;
                default:
                    if (Character.isDigit(actChar)) { // '8' and '9' already handled
                        state = ISI_OCTAL;
                        break;
                    }
                    state = INIT;
                    return JavaFXTokenContext.INT_LITERAL;
                }
                break;

            case ISI_INT:
                switch (actChar) {
                case 'l':
                case 'L':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.LONG_LITERAL;
                case '.':
                    state = ISI_DOUBLE;
                    break;
                case 'f':
                case 'F':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.FLOAT_LITERAL;
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.DOUBLE_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_DOUBLE_EXP;
                    break;
                default:
                    if (!(actChar >= '0' && actChar <= '9')) {
                        state = INIT;
                        return JavaFXTokenContext.INT_LITERAL;
                    }
                }
                break;

            case ISI_OCTAL:
                if (!(actChar >= '0' && actChar <= '7')) {

                    state = INIT;
                    return JavaFXTokenContext.OCTAL_LITERAL;
                }
                break;

            case ISI_DOUBLE:
                switch (actChar) {
                case 'f':
                case 'F':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.FLOAT_LITERAL;
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.DOUBLE_LITERAL;
                case 'e':
                case 'E':
                    state = ISI_DOUBLE_EXP;
                    break;
                default:
                    if (!((actChar >= '0' && actChar <= '9')
                            || actChar == '.')) {

                        state = INIT;
                        return JavaFXTokenContext.DOUBLE_LITERAL;
                    }
                }
                break;

            case ISI_DOUBLE_EXP:
                switch (actChar) {
                case 'f':
                case 'F':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.FLOAT_LITERAL;
                case 'd':
                case 'D':
                    offset++;
                    state = INIT;
                    return JavaFXTokenContext.DOUBLE_LITERAL;
                default:
                    if (!(Character.isDigit(actChar)
                            || actChar == '-' || actChar == '+')) {
                        state = INIT;
                        return JavaFXTokenContext.DOUBLE_LITERAL;
                    }
                }
                break;

            case ISI_HEX:
                if (!((actChar >= 'a' && actChar <= 'f')
                        || (actChar >= 'A' && actChar <= 'F')
                        || Character.isDigit(actChar))
                   ) {

                    state = INIT;
                    return JavaFXTokenContext.HEX_LITERAL;
                }
                break;

            case ISA_DOT:
                if (Character.isDigit(actChar)) {
                    state = ISI_DOUBLE;
                } else if (actChar == '.' && offset + 1 < stopOffset && buffer[offset + 1] == '.') {
                    offset += 2;
                    state = INIT;
                    return JavaFXTokenContext.ELLIPSIS;
                } else { // only single dot
                    state = INIT;
                    return JavaFXTokenContext.DOT;
                }
                break;

            } // end of switch(state)

            offset++;
        } // end of while(offset...)

        /** At this stage there's no more text in the scanned buffer.
        * Scanner first checks whether this is completely the last
        * available buffer.
        */

        if (lastBuffer) {
            switch(state) {
            case ISI_WHITESPACE:
                state = INIT;
                return JavaFXTokenContext.WHITESPACE;
            case ISI_IDENTIFIER:
                state = INIT;
                TokenID kwd = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                return (kwd != null) ? kwd : JavaFXTokenContext.IDENTIFIER;
            case ISI_LINE_COMMENT:
                return JavaFXTokenContext.LINE_COMMENT; // stay in line-comment state
            case ISI_BLOCK_COMMENT:
            case ISA_STAR_I_BLOCK_COMMENT:
                return JavaFXTokenContext.BLOCK_COMMENT; // stay in block-comment state
            case ISI_STRING:
            case ISI_STRING_A_BSLASH:
                return JavaFXTokenContext.STRING_LITERAL; // hold the state
            case ISI_CHAR:
            case ISI_CHAR_A_BSLASH:
                return JavaFXTokenContext.CHAR_LITERAL; // hold the state
            case ISA_ZERO:
            case ISI_INT:
                state = INIT;
                return JavaFXTokenContext.INT_LITERAL;
            case ISI_OCTAL:
                state = INIT;
                return JavaFXTokenContext.OCTAL_LITERAL;
            case ISI_DOUBLE:
            case ISI_DOUBLE_EXP:
                state = INIT;
                return JavaFXTokenContext.DOUBLE_LITERAL;
            case ISI_HEX:
                state = INIT;
                return JavaFXTokenContext.HEX_LITERAL;
            case ISA_DOT:
                state = INIT;
                return JavaFXTokenContext.DOT;
            case ISA_SLASH:
                state = INIT;
                return JavaFXTokenContext.DIV;
            case ISA_EQ:
                state = INIT;
                return JavaFXTokenContext.EQ;
            case ISA_GT:
                state = INIT;
                return JavaFXTokenContext.GT;
            case ISA_GTGT:
                state = INIT;
                return JavaFXTokenContext.RSSHIFT;
            case ISA_GTGTGT:
                state = INIT;
                return JavaFXTokenContext.RUSHIFT;
            case ISA_LT:
                state = INIT;
                return JavaFXTokenContext.LT;
            case ISA_LTLT:
                state = INIT;
                return JavaFXTokenContext.LSHIFT;
            case ISA_PLUS:
                state = INIT;
                return JavaFXTokenContext.PLUS;
            case ISA_MINUS:
                state = INIT;
                return JavaFXTokenContext.MINUS;
            case ISA_STAR:
                state = INIT;
                return JavaFXTokenContext.MUL;
            case ISA_PIPE:
                state = INIT;
                return JavaFXTokenContext.OR;
            case ISA_PERCENT:
                state = INIT;
                return JavaFXTokenContext.MOD;
            case ISA_AND:
                state = INIT;
                return JavaFXTokenContext.AND;
            case ISA_XOR:
                state = INIT;
                return JavaFXTokenContext.XOR;
            case ISA_EXCLAMATION:
                state = INIT;
                return JavaFXTokenContext.NOT;
            }
        }

        /* At this stage there's no more text in the scanned buffer, but
        * this buffer is not the last so the scan will continue on another buffer.
        * The scanner tries to minimize the amount of characters
        * that will be prescanned in the next buffer by returning the token
        * where possible.
        */

        switch (state) {
        case ISI_WHITESPACE:
            return JavaFXTokenContext.WHITESPACE;
        }

        return null; // nothing found
    }

    public String getStateName(int stateNumber) {
        switch(stateNumber) {
        case ISI_WHITESPACE:
            return "ISI_WHITESPACE"; // NOI18N
        case ISI_LINE_COMMENT:
            return "ISI_LINE_COMMENT"; // NOI18N
        case ISI_BLOCK_COMMENT:
            return "ISI_BLOCK_COMMENT"; // NOI18N
        case ISI_STRING:
            return "ISI_STRING"; // NOI18N
        case ISI_STRING_A_BSLASH:
            return "ISI_STRING_A_BSLASH"; // NOI18N
        case ISI_CHAR:
            return "ISI_CHAR"; // NOI18N
        case ISI_CHAR_A_BSLASH:
            return "ISI_CHAR_A_BSLASH"; // NOI18N
        case ISI_IDENTIFIER:
            return "ISI_IDENTIFIER"; // NOI18N
        case ISA_SLASH:
            return "ISA_SLASH"; // NOI18N
        case ISA_EQ:
            return "ISA_EQ"; // NOI18N
        case ISA_GT:
            return "ISA_GT"; // NOI18N
        case ISA_GTGT:
            return "ISA_GTGT"; // NOI18N
        case ISA_GTGTGT:
            return "ISA_GTGTGT"; // NOI18N
        case ISA_LT:
            return "ISA_LT"; // NOI18N
        case ISA_LTLT:
            return "ISA_LTLT"; // NOI18N
        case ISA_PLUS:
            return "ISA_PLUS"; // NOI18N
        case ISA_MINUS:
            return "ISA_MINUS"; // NOI18N
        case ISA_STAR:
            return "ISA_STAR"; // NOI18N
        case ISA_STAR_I_BLOCK_COMMENT:
            return "ISA_STAR_I_BLOCK_COMMENT"; // NOI18N
        case ISA_PIPE:
            return "ISA_PIPE"; // NOI18N
        case ISA_PERCENT:
            return "ISA_PERCENT"; // NOI18N
        case ISA_AND:
            return "ISA_AND"; // NOI18N
        case ISA_XOR:
            return "ISA_XOR"; // NOI18N
        case ISA_EXCLAMATION:
            return "ISA_EXCLAMATION"; // NOI18N
        case ISA_ZERO:
            return "ISA_ZERO"; // NOI18N
        case ISI_INT:
            return "ISI_INT"; // NOI18N
        case ISI_OCTAL:
            return "ISI_OCTAL"; // NOI18N
        case ISI_DOUBLE:
            return "ISI_DOUBLE"; // NOI18N
        case ISI_DOUBLE_EXP:
            return "ISI_DOUBLE_EXP"; // NOI18N
        case ISI_HEX:
            return "ISI_HEX"; // NOI18N
        case ISA_DOT:
            return "ISA_DOT"; // NOI18N

        default:
            return super.getStateName(stateNumber);
        }
    }

    public TokenID matchKeyword(char[] buffer, int offset, int len) {
        if (len > 12)
            return null;
        if (len <= 1)
            return null;
        switch (buffer[offset++]) {
            case 'a':
                if (len <= 5)
                    return null;
                switch (buffer[offset++]) {
/*                    case 'b':
                        return (len == 8
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 't')
                        ? JavaFXTokenContext.ABSTRACT : null;*/
                    case 's':
                        return (len == 6
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 't')
                        ? JavaFXTokenContext.ASSERT : null;
                    case 't':
                        return (len == 9
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.ATTRIBUTE : null;
                    default:
                        return null;
                }
            case 'b':
                if (len <= 3)
                    return null;
                switch (buffer[offset++]) {
                    /*case 'o':
                        return (len == 7
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'n')
                        ? JavaFXTokenContext.BOOLEAN : null;*/
                    case 'r':
                        return (len == 5
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'k')
                        ? JavaFXTokenContext.BREAK : null;
                    /*case 'y':
                        return (len == 4
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.BYTE : null;*/
                    default:
                        return null;
                }
            case 'c':
                if (len <= 3)
                    return null;
                switch (buffer[offset++]) {
                    case 'a':
                        switch (buffer[offset++]) {
                            /*case 's':
                                return (len == 4
                                && buffer[offset++] == 'e')
                                ? JavaFXTokenContext.CASE : null;*/
                            case 't':
                                return (len == 5
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 'h')
                                ? JavaFXTokenContext.CATCH : null;
                            default:
                                return null;
                        }
                    /*case 'h':
                        return (len == 4
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'r')
                        ? JavaFXTokenContext.CHAR : null;*/
                    case 'l':
                        return (len == 5
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 's')
                        ? JavaFXTokenContext.CLASS : null;
                    case 'o':
                        if (len <= 4)
                            return null;
                        if (buffer[offset++] != 'n')
                            return null;
                        switch (buffer[offset++]) {
                            /*case 's':
                                return (len == 5
                                && buffer[offset++] == 't')
                                ? JavaFXTokenContext.CONST : null;*/
                            case 't':
                                return (len == 8
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'u'
                                && buffer[offset++] == 'e')
                                ? JavaFXTokenContext.CONTINUE : null;
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            case 'd':
                switch (buffer[offset++]) {
                    /*case 'e':
                        return (len == 7
                        && buffer[offset++] == 'f'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 't')
                        ? JavaFXTokenContext.DEFAULT : null;*/
                    case 'o':
                        if (len == 2)
                            return JavaFXTokenContext.DO;
                        switch (buffer[offset++]) {
                            /*case 'u':
                                return (len == 6
                                && buffer[offset++] == 'b'
                                && buffer[offset++] == 'l'
                                && buffer[offset++] == 'e')
                                ? JavaFXTokenContext.DOUBLE : null;*/
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            case 'e':
                if (len <= 3)
                    return null;
                switch (buffer[offset++]) {
                    case 'l':
                        return (len == 4
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.ELSE : null;
                    /*case 'n':
                        return (len == 4
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'm')
                        ? isJava15 ? JavaFXTokenContext.ENUM : null : null;*/
                    case 'x':
                        return (len == 7
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'd'
                        && buffer[offset++] == 's')
                        ? JavaFXTokenContext.EXTENDS : null;
                    default:
                        return null;
                }
            case 'f':
                if (len <= 2)
                    return null;
                switch (buffer[offset++]) {
                    /*case 'a':
                        return (len == 5
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 's'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.FALSE : null;*/
                    case 'i':
                        if (len <= 4)
                            return null;
                        if (buffer[offset++] != 'n'
                        || buffer[offset++] != 'a'
                        || buffer[offset++] != 'l')
                            return null;
                        // if (len == 5)
                        //     return JavaFXTokenContext.FINAL;
                        if (len <= 6)
                            return null;
                        if (buffer[offset++] != 'l'
                        || buffer[offset++] != 'y')
                            return null;
                        if (len == 7)
                            return JavaFXTokenContext.FINALLY;
                        return null;
                    /*case 'l':
                        return (len == 5
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 't')
                        ? JavaFXTokenContext.FLOAT : null;*/
                    case 'o':
                        return (len == 3
                        && buffer[offset++] == 'r')
                        ? JavaFXTokenContext.FOR : null;
                    case 'u':
                        return (len == 8
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n')
                        ? JavaFXTokenContext.FUNCTION : null;
                    default:
                        return null;
                }
            /*case 'g':
                return (len == 4
                && buffer[offset++] == 'o'
                && buffer[offset++] == 't'
                && buffer[offset++] == 'o')
                ? JavaFXTokenContext.GOTO : null;*/
            case 'i':
                switch (buffer[offset++]) {
                    case 'f':
                        return (len == 2)
                        ? JavaFXTokenContext.IF : null;
                    case 'm':
                        if (len <= 5)
                            return null;
                        if (buffer[offset++] != 'p')
                            return null;
                        switch (buffer[offset++]) {
                            /*case 'l':
                                return (len == 10
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'm'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 's')
                                ? JavaFXTokenContext.IMPLEMENTS : null;*/
                            case 'o':
                                return (len == 6
                                && buffer[offset++] == 'r'
                                && buffer[offset++] == 't')
                                ? JavaFXTokenContext.IMPORT : null;
                            default:
                                return null;
                        }
                    case 'n':
                        if (len <= 2)
                            return null;
                        switch (buffer[offset++]) {
                            case 's':
                                return (len == 10
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'a'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'o'
                                && buffer[offset++] == 'f')
                                ? JavaFXTokenContext.INSTANCEOF : null;
                            /*case 't':
                                if (len == 3)
                                    return JavaFXTokenContext.INT;
                                switch (buffer[offset++]) {
                                    case 'e':
                                        return (len == 9
                                        && buffer[offset++] == 'r'
                                        && buffer[offset++] == 'f'
                                        && buffer[offset++] == 'a'
                                        && buffer[offset++] == 'c'
                                        && buffer[offset++] == 'e')
                                        ? JavaFXTokenContext.INTERFACE : null;
                                    default:
                                        return null;
                                }*/
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            /*case 'l':
                return (len == 4
                && buffer[offset++] == 'o'
                && buffer[offset++] == 'n'
                && buffer[offset++] == 'g')
                ? JavaFXTokenContext.LONG : null;*/
            case 'n':
                if (len <= 2)
                    return null;
                switch (buffer[offset++]) {
                    /*case 'a':
                        return (len == 6
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'v'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.NATIVE : null;*/
                    case 'e':
                        return (len == 3
                        && buffer[offset++] == 'w')
                        ? JavaFXTokenContext.NEW : null;
                    case 'u':
                        return (len == 4
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'l')
                        ? JavaFXTokenContext.NULL : null;
                    default:
                        return null;
                }
            case 'o':
                return (len == 9
                && buffer[offset++] == 'p'
                && buffer[offset++] == 'e'
                && buffer[offset++] == 'r'
                && buffer[offset++] == 'a'
                && buffer[offset++] == 't'
                && buffer[offset++] == 'i'
                && buffer[offset++] == 'o'
                && buffer[offset++] == 'n')
                ? JavaFXTokenContext.OPERATION : null;
            case 'p':
                if (len <= 5)
                    return null;
                switch (buffer[offset++]) {
                    case 'a':
                        return (len == 7
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'k'
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 'g'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.PACKAGE : null;
                    case 'r':
                        if (len <= 6)
                            return null;
                        switch (buffer[offset++]) {
                            case 'i':
                                return (len == 7
                                && buffer[offset++] == 'v'
                                && buffer[offset++] == 'a'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'e')
                                ? JavaFXTokenContext.PRIVATE : null;
                            case 'o':
                                return (len == 9
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'd')
                                ? JavaFXTokenContext.PROTECTED : null;
                            default:
                                return null;
                        }
                    case 'u':
                        return (len == 6
                        && buffer[offset++] == 'b'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'c')
                        ? JavaFXTokenContext.PUBLIC : null;
                    default:
                        return null;
                }
            case 'r':
                if (len <= 5)
                    return null;
                if (buffer[offset++] != 'e')
                    return null;
                switch (buffer[offset++]) {
                    case 't':
                        return (len == 6
                        && buffer[offset++] == 'u'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'n')
                        ? JavaFXTokenContext.RETURN : null;
                    case 'a':
                        return (len == 8
                        && buffer[offset++] == 'd'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'y')
                        ? JavaFXTokenContext.READONLY : null;
                    default:
                        return null;
                }
            case 's':
                if (len <= 4)
                    return null;
                switch (buffer[offset++]) {
                    /*case 'h':
                        return (len == 5
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 't')
                        ? JavaFXTokenContext.SHORT : null;*/
                    case 't':
                        if (len <= 5)
                            return null;
                        switch (buffer[offset++]) {
                            /*case 'a':
                                return (len == 6
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'c')
                                ? JavaFXTokenContext.STATIC : null;
                            case 'r':
                                return (len == 8
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'c'
                                && buffer[offset++] == 't'
                                && buffer[offset++] == 'f'
                                && buffer[offset++] == 'p')
                                ? JavaFXTokenContext.STRICTFP : null;*/
                            default:
                                return null;
                        }
                    case 'u':
                        return (len == 5
                        && buffer[offset++] == 'p'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'r')
                        ? JavaFXTokenContext.SUPER : null;
                    /*case 'w':
                        return (len == 6
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'h')
                        ? JavaFXTokenContext.SWITCH : null;
                    case 'y':
                        return (len == 12
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'c'
                        && buffer[offset++] == 'h'
                        && buffer[offset++] == 'r'
                        && buffer[offset++] == 'o'
                        && buffer[offset++] == 'n'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'z'
                        && buffer[offset++] == 'e'
                        && buffer[offset++] == 'd')
                        ? JavaFXTokenContext.SYNCHRONIZED : null;*/
                    default:
                        return null;
                }
            case 't':
                if (len <= 2)
                    return null;
                switch (buffer[offset++]) {
                    case 'h':
                        if (len <= 3)
                            return null;
                        switch (buffer[offset++]) {
                            case 'i':
                                return (len == 4
                                && buffer[offset++] == 's')
                                ? JavaFXTokenContext.THIS : null;
                            case 'r':
                                if (len <= 4)
                                    return null;
                                if (buffer[offset++] != 'o'
                                || buffer[offset++] != 'w')
                                    return null;
                                /*if (len == 5)
                                    return JavaFXTokenContext.THROW;*/
                                if (buffer[offset++] != 's')
                                    return null;
                                /*if (len == 6)
                                    return JavaFXTokenContext.THROWS;*/
                                return null;
                            default:
                                return null;
                        }
                    case 'r':
                        switch (buffer[offset++]) {
                            /*case 'a':
                                return (len == 9
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 's'
                                && buffer[offset++] == 'i'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'n'
                                && buffer[offset++] == 't')
                                ? JavaFXTokenContext.TRANSIENT : null;*/
                            case 'i':
                                return (len == 7
                                && buffer[offset++] == 'g'
                                && buffer[offset++] == 'g'
                                && buffer[offset++] == 'e'
                                && buffer[offset++] == 'r')
                                ? JavaFXTokenContext.TRIGGER : null;
                            case 'u':
                                return (len == 4
                                && buffer[offset++] == 'e')
                                ? JavaFXTokenContext.TRUE : null;
                            case 'y':
                                return (len == 3)
                                ? JavaFXTokenContext.TRY : null;
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            case 'v':
                return (len == 3
                && buffer[offset++] == 'a'
                && buffer[offset++] == 'r')
                ? JavaFXTokenContext.VAR : null;
                /*if (len <= 3)
                    return null;
                if (buffer[offset++] != 'o')
                    return null;
                return null;
                switch (buffer[offset++]) {
                    case 'i':
                        return (len == 4
                        && buffer[offset++] == 'd')
                        ? JavaFXTokenContext.VOID : null;
                    case 'l':
                        return (len == 8
                        && buffer[offset++] == 'a'
                        && buffer[offset++] == 't'
                        && buffer[offset++] == 'i'
                        && buffer[offset++] == 'l'
                        && buffer[offset++] == 'e')
                        ? JavaFXTokenContext.VOLATILE : null;
                    default:
                        return null;
                }*/
            case 'w':
                return (len == 5
                && buffer[offset++] == 'h'
                && buffer[offset++] == 'i'
                && buffer[offset++] == 'l'
                && buffer[offset++] == 'e')
                ? JavaFXTokenContext.WHILE : null;
            default:
                return null;
        }
    }
}