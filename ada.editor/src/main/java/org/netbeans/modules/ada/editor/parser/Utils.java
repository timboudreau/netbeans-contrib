/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.editor.parser;

/**
 * Based on org.netbeans.modules.php.editor.parser.Utils
 *
 * @author Andrea Lucarelli
 */
public class Utils {

    /**
     * This method is mainly used for debugging purpose.
     * 
     * @param id token id
     * @return text representation for the token
     */
    public static String getASTScannerTokenName(int id) {
        String name;
        switch (id) {
            case Ada95ASTSymbols.REVERSE:
                name = "REVERSE";
                break;
            case Ada95ASTSymbols.PROCEDURE:
                name = "PROCEDURE";
                break;
            case Ada95ASTSymbols.OF:
                name = "OF";
                break;
            case Ada95ASTSymbols.ABORT:
                name = "ABORT";
                break;
            case Ada95ASTSymbols.AMP:
                name = "AMP";
                break;
            case Ada95ASTSymbols.SEMICOLON:
                name = "SEMICOLON";
                break;
            case Ada95ASTSymbols.CASE:
                name = "CASE";
                break;
            case Ada95ASTSymbols.BASED_LITERAL:
                name = "BASED_LITERAL";
                break;
            case Ada95ASTSymbols.DECIMAL_LITERAL:
                name = "DECIMAL_LITERAL";
                break;
            case Ada95ASTSymbols.MOD:
                name = "MOD";
                break;
            case Ada95ASTSymbols.ARRAY:
                name = "ARRAY";
                break;
            case Ada95ASTSymbols.WITH:
                name = "WITH";
                break;
            case Ada95ASTSymbols.USE:
                name = "USE";
                break;
            case Ada95ASTSymbols.BAR:
                name = "BAR";
                break;
            case Ada95ASTSymbols.GENERIC:
                name = "GENERIC";
                break;
            case Ada95ASTSymbols.EXCEPTION:
                name = "EXCEPTION";
                break;
            case Ada95ASTSymbols.TAGGED:
                name = "TAGGED";
                break;
            case Ada95ASTSymbols.FOR:
                name = "FOR";
                break;
            case Ada95ASTSymbols.IDENTIFIER:
                name = "IDENTIFIER";
                break;
            case Ada95ASTSymbols.SLASH:
                name = "SLASH";
                break;
            case Ada95ASTSymbols.AT:
                name = "AT";
                break;
            case Ada95ASTSymbols.TYPE:
                name = "TYPE";
                break;
            case Ada95ASTSymbols.EQ:
                name = "EQ";
                break;
            case Ada95ASTSymbols.WHILE:
                name = "WHILE";
                break;
            case Ada95ASTSymbols.DELAY:
                name = "DELAY";
                break;
            case Ada95ASTSymbols.ENTRY:
                name = "ENTRY";
                break;
            case Ada95ASTSymbols.DELTA:
                name = "DELTA";
                break;
            case Ada95ASTSymbols.DIGITS:
                name = "DIGITS";
                break;
            case Ada95ASTSymbols.ABSTRACT:
                name = "ABSTRACT";
                break;
            case Ada95ASTSymbols.LOOP:
                name = "LOOP";
                break;
            case Ada95ASTSymbols.ACCESS:
                name = "ACCESS";
                break;
            case Ada95ASTSymbols.REQUEUE:
                name = "REQUEUE";
                break;
            case Ada95ASTSymbols.TASK:
                name = "TASK";
                break;
            case Ada95ASTSymbols.ABS:
                name = "ABS";
                break;
            case Ada95ASTSymbols.END:
                name = "END";
                break;
            case Ada95ASTSymbols.REM:
                name = "REM";
                break;
            case Ada95ASTSymbols.MINUS:
                name = "MINUS";
                break;
            case Ada95ASTSymbols.ASSIGNMENT:
                name = "ASSIGNMENT";
                break;
            case Ada95ASTSymbols.THEN:
                name = "THEN";
                break;
            case Ada95ASTSymbols.GOTO:
                name = "GOTO";
                break;
            case Ada95ASTSymbols.NEW:
                name = "NEW";
                break;
            case Ada95ASTSymbols.WHEN:
                name = "WHEN";
                break;
            case Ada95ASTSymbols.TERMINATE:
                name = "TERMINATE";
                break;
            case Ada95ASTSymbols.COLON:
                name = "COLON";
                break;
            case Ada95ASTSymbols.EOF:
                name = "EOF";
                break;
            case Ada95ASTSymbols.PLUS:
                name = "PLUS";
                break;
            case Ada95ASTSymbols.LTEQ:
                name = "LTEQ";
                break;
            case Ada95ASTSymbols.BEGIN:
                name = "BEGIN";
                break;
            case Ada95ASTSymbols.FUNCTION:
                name = "FUNCTION";
                break;
            case Ada95ASTSymbols.RECORD:
                name = "RECORD";
                break;
            case Ada95ASTSymbols.RANGE:
                name = "RANGE";
                break;
            case Ada95ASTSymbols.PROTECTED:
                name = "PROTECTED";
                break;
            case Ada95ASTSymbols.PRIVATE:
                name = "PRIVATE";
                break;
            case Ada95ASTSymbols.INEQ:
                name = "INEQ";
                break;
            case Ada95ASTSymbols.SEPARATE:
                name = "SEPARATE";
                break;
            case Ada95ASTSymbols.CONSTANT:
                name = "CONSTANT";
                break;
            case Ada95ASTSymbols.SELECT:
                name = "SELECT";
                break;
            case Ada95ASTSymbols.OTHERS:
                name = "OTHERS";
                break;
            case Ada95ASTSymbols.ALIASED:
                name = "ALIASED";
                break;
            case Ada95ASTSymbols.ELSE:
                name = "ELSE";
                break;
            case Ada95ASTSymbols.DO:
                name = "DO";
                break;
            case Ada95ASTSymbols.GT:
                name = "GT";
                break;
            case Ada95ASTSymbols.RENAMES:
                name = "RENAMES";
                break;
            case Ada95ASTSymbols.LIMITED:
                name = "LIMITED";
                break;
            case Ada95ASTSymbols.STAR:
                name = "STAR";
                break;
            case Ada95ASTSymbols.NULL:
                name = "NULL";
                break;
            case Ada95ASTSymbols.SUBTYPE:
                name = "SUBTYPE";
                break;
            case Ada95ASTSymbols.RETURN:
                name = "RETURN";
                break;
            case Ada95ASTSymbols.ALL:
                name = "ALL";
                break;
            case Ada95ASTSymbols.RAISE:
                name = "RAISE";
                break;
            case Ada95ASTSymbols.AND:
                name = "AND";
                break;
            case Ada95ASTSymbols.GTEQ:
                name = "GTEQ";
                break;
            case Ada95ASTSymbols.UNTIL:
                name = "UNTIL";
                break;
            case Ada95ASTSymbols.BODY:
                name = "BODY";
                break;
            case Ada95ASTSymbols.EXIT:
                name = "EXIT";
                break;
            case Ada95ASTSymbols.ACCEPT:
                name = "ACCEPT";
                break;
            case Ada95ASTSymbols.PRAGMA:
                name = "PRAGMA";
                break;
            case Ada95ASTSymbols.IS:
                name = "IS";
                break;
            case Ada95ASTSymbols.OR:
                name = "OR";
                break;
            case Ada95ASTSymbols.OUT:
                name = "OUT";
                break;
            case Ada95ASTSymbols.RPAREN:
                name = "RPAREN";
                break;
            case Ada95ASTSymbols.ELSIF:
                name = "ELSIF";
                break;
            case Ada95ASTSymbols.NOT:
                name = "NOT";
                break;
            case Ada95ASTSymbols.XOR:
                name = "XOR";
                break;
            case Ada95ASTSymbols.LPAREN:
                name = "LPAREN";
                break;
            case Ada95ASTSymbols.IN:
                name = "IN";
                break;
            case Ada95ASTSymbols.COMMA:
                name = "COMMA";
                break;
            case Ada95ASTSymbols.LT:
                name = "LT";
                break;
            case Ada95ASTSymbols.PACKAGE:
                name = "PACKAGE";
                break;
            case Ada95ASTSymbols.DOT:
                name = "DOT";
                break;
            case Ada95ASTSymbols.IF:
                name = "IF";
                break;
            case Ada95ASTSymbols.DECLARE:
                name = "DECLARE";
                break;
            default:
                name = "unknown";
        }
        return name;
    }

    public static String getSpaces(int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int index = 0; index < length; index++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static int getRowStart(String text, int offset) {
        // Search backwards
        for (int i = offset - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i + 1;
            }
        }
        return 0;
    }

    public static int getRowEnd(String text, int offset) {
        int i = offset - 1;
        if (i < 0 ) {
            return 0;
        }
        for (; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i;
            }
        }
        return i;
    }
}
