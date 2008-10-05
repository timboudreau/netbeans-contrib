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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import org.netbeans.modules.gsf.api.Error;
import org.netbeans.modules.gsf.api.Severity;
import org.netbeans.modules.ada.editor.parser.AdaParser.Context;
import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.openide.util.NbBundle;

/**
 * Based on org.netbeans.modules.php.editor.parser.PHP5ErrorHandler
 *
 * @author Andrea Lucarelli
 */
public class Ada95ErrorHandler implements ParserErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(Ada95ErrorHandler.class.getName());
    
    public static class SyntaxError {
        private final short[] expectedTokens;
        private final Symbol currentToken;
        private final Symbol previousToken;
        
        public SyntaxError(short[] expectedTokens, Symbol currentToken, Symbol previousToken) {
            this.expectedTokens = expectedTokens;
            this.currentToken = currentToken;
            this.previousToken = previousToken;
        }

        public Symbol getCurrentToken() {
            return currentToken;
        }

        public Symbol getPreviousToken() {
            return previousToken;
        }

        public short[] getExpectedTokens() {
            return expectedTokens;
        }
    }
    
    private final List<SyntaxError> syntaxErrors;

    private final Context context;
    AdaParser outer;

    public Ada95ErrorHandler(Context context, AdaParser outer) {
        super();
        this.outer = outer;
        this.context = context;
        syntaxErrors = new ArrayList<SyntaxError>();
        //LOGGER.setLevel(Level.FINEST);
    }

    public void handleError(Type type, short[] expectedtokens, Symbol current, Symbol previous) {
        Error error;
        if (type == ParserErrorHandler.Type.SYNTAX_ERROR) {
            // logging syntax error
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("Syntax error:"); //NOI18N
                LOGGER.finest("Current [" + current.left + ", " + current.right + "](" + Utils.getASTScannerTokenName(current.sym) + "): " + current.value); //NOI18N
                LOGGER.finest("Previous [" + previous.left + ", " + previous.right + "] (" + Utils.getASTScannerTokenName(previous.sym) + "):" + previous.value); //NOI18N
                StringBuffer message = new StringBuffer();
                message.append("Expected tokens:"); //NOI18N
                for (int i = 0; i < expectedtokens.length; i += 2) {
                    message.append(" ").append( Utils.getASTScannerTokenName(expectedtokens[i])); //NOI18N
                }
                LOGGER.finest(message.toString());
            }
            syntaxErrors.add(new SyntaxError(expectedtokens, current, previous));
        } else if (type == ParserErrorHandler.Type.FATAL_PARSER_ERROR) {
            String message = null;
            if (current != null) {
                String tagText = getTokenTextForm(current.sym);
                if (tagText != null) {
                    message = NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Unexpected") + " " + tagText;
                }
                else {
                    message = NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Unexpected") + " " + Utils.getASTScannerTokenName(current.sym);
                }
            }
            if (message == null) {
                message = "Parser error"; // NOI18N
            }
            error = new AdaError(message, context.getFile().getFileObject(), current.left, current.right, Severity.ERROR, null);
            context.getListener().error(error);
        }
    }

    public void handleError(Type type, Symbol symbol, String message) {
        Error error;
        if (symbol != null) {
            if (message == null) {
                message = "Parser error";
            }
            error = new AdaError(message, context.getFile().getFileObject(), symbol.left, symbol.right, Severity.ERROR, null);
            context.getListener().error(error);
        }
    }
    
    public void displaySyntaxErrors(Program program) {
        for (SyntaxError syntaxError : syntaxErrors) {
            ASTNode astError = null;
            if (program != null) {
                astError = org.netbeans.modules.ada.editor.ast.ASTUtils.getNodeAtOffset(program, syntaxError.currentToken.left);
                if (!(astError instanceof ASTError)) {
                    astError = org.netbeans.modules.ada.editor.ast.ASTUtils.getNodeAtOffset(program, syntaxError.previousToken.right);
                    if (!(astError instanceof ASTError)) {
                        astError = null;
                    }
                }
                if (astError != null) {
                    LOGGER.finest("ASTError [" + astError.getStartOffset() + ", " + astError.getEndOffset() + "]"); //NOI18N
                } else {
                    LOGGER.finest("ASTError was not found");  //NOI18N
                }
            }
            Error error = defaultSyntaxErrorHandling(syntaxError, astError);
            context.getListener().error(error);
        }
    }
    
    // This is just defualt handling. We can do a logic, which will find metter 
    private Error defaultSyntaxErrorHandling(SyntaxError syntaxError, ASTNode astError) {
        Error error = null;
        String unexpectedText = "";     //NOI18N
        StringBuffer message = new StringBuffer();
        boolean isUnexpected = false;
        int start  = syntaxError.getCurrentToken().left;
        int end = syntaxError.getCurrentToken().right;
        
        if (syntaxError.getCurrentToken().sym == Ada95ASTSymbols.EOF) {
            isUnexpected = true;
            unexpectedText = NbBundle.getMessage(Ada95ErrorHandler.class, "SE_EOF");
            start = end - 1;
        }
        else {
            String currentText = (String)syntaxError.getCurrentToken().value;
            isUnexpected = currentText != null && currentText.trim().length() > 0;
            if (isUnexpected) {
                unexpectedText = currentText.trim();
                end = start + unexpectedText.length();
            }
        }
        
        List<String> possibleTags = new ArrayList<String>();
        for (int i = 0; i < syntaxError.getExpectedTokens().length; i += 2) {
            String text = getTokenTextForm(syntaxError.getExpectedTokens()[i]);
            if (text != null) {
                possibleTags.add(text);
            }
        }

        message.append(NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Message"));
        message.append(':'); //NOI18N
        if (isUnexpected) {
            message.append(' ').append(NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Unexpected"));
            message.append(": "); //NOI18N
            message.append(unexpectedText);
        }
        if (possibleTags.size() > 0) {
            message.append('\n').append(NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Expected"));
            message.append(": "); //NOI18N
            boolean addOR = false;
            for (String tag : possibleTags) {
                if (addOR) {
                    message.append(" " + NbBundle.getMessage(Ada95ErrorHandler.class, "SE_Or") + " ");
                }
                else {
                    addOR = true;
                }
                
                message.append(tag);
            }
        }

        if (astError != null){
            start = astError.getStartOffset();
            end = astError.getEndOffset();
            // if the asterror is trough two lines, the problem is ussually at the end
            String text = context.getSource().substring(start, end);
            int lastNewLine = text.length()-1;
            while (text.charAt(lastNewLine) == '\n' || text.charAt(lastNewLine) == '\r'
                    || text.charAt(lastNewLine) == '\t' || text.charAt(lastNewLine) == ' ') {
                lastNewLine--;
                if (lastNewLine < 0) {
                    break;
                }
            }
            lastNewLine = text.lastIndexOf('\n', lastNewLine);   //NOI18N
            if (lastNewLine > 0) {
                start = start + lastNewLine + 1;
            }
        }
        error = new AdaError(message.toString(), context.getFile().getFileObject(), start, end, Severity.ERROR, new Object[]{syntaxError});
        return error;
    }

    public List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    private String getTokenTextForm (int token) {
        String text = null;
        switch (token) {
            case Ada95ASTSymbols.BASED_LITERAL : text = "number"; break; //NOI18N
            case Ada95ASTSymbols.DECIMAL_LITERAL : text = "number"; break; //NOI18N
            case Ada95ASTSymbols.IDENTIFIER : text = "identifier"; break; //NOI18N
            case Ada95ASTSymbols.ABORT : text = "abort"; break; //NOI18N
            case Ada95ASTSymbols.ABS : text = "abs"; break; //NOI18N
            case Ada95ASTSymbols.ABSTRACT : text = "abstract"; break; //NOI18N
            case Ada95ASTSymbols.ACCESS : text = "access"; break; //NOI18N
            case Ada95ASTSymbols.ACCEPT : text = "access"; break; //NOI18N
            case Ada95ASTSymbols.ALIASED : text = "aliased"; break; //NOI18N
            case Ada95ASTSymbols.ALL : text = "all"; break; //NOI18N
            case Ada95ASTSymbols.AND : text = "and"; break; //NOI18N
            case Ada95ASTSymbols.ARRAY : text = "array"; break; //NOI18N
            case Ada95ASTSymbols.AT : text = "at"; break; //NOI18N
            case Ada95ASTSymbols.BEGIN : text = "begin"; break; //NOI18N
            case Ada95ASTSymbols.BODY : text = "body"; break; //NOI18N
            case Ada95ASTSymbols.CONSTANT : text = "constant"; break; //NOI18N
            case Ada95ASTSymbols.CASE : text = "case"; break; //NOI18N
            case Ada95ASTSymbols.DECLARE : text = "declare"; break; //NOI18N
            case Ada95ASTSymbols.DELAY : text = "delay"; break; //NOI18N
            case Ada95ASTSymbols.DELTA : text = "delta"; break; //NOI18N
            case Ada95ASTSymbols.DIGITS : text = "digits"; break; //NOI18N
            case Ada95ASTSymbols.DO : text = "do"; break; //NOI18N
            case Ada95ASTSymbols.ELSE : text = "else"; break; //NOI18N
            case Ada95ASTSymbols.ELSIF : text = "elsif"; break; //NOI18N
            case Ada95ASTSymbols.END : text = "end"; break; //NOI18N
            case Ada95ASTSymbols.ENTRY : text = "entry"; break; //NOI18N
            case Ada95ASTSymbols.EXCEPTION : text = "exception"; break; //NOI18N
            case Ada95ASTSymbols.EXIT : text = "exit"; break; //NOI18N
            case Ada95ASTSymbols.FOR : text = "for"; break; //NOI18N
            case Ada95ASTSymbols.FUNCTION : text = "function"; break; //NOI18N
            case Ada95ASTSymbols.GENERIC : text = "generic"; break; //NOI18N
            case Ada95ASTSymbols.GOTO : text = "goto"; break; //NOI18N
            case Ada95ASTSymbols.IF : text = "if"; break; //NOI18N
            case Ada95ASTSymbols.IN : text = "in"; break; //NOI18N
            case Ada95ASTSymbols.IS : text = "is"; break; //NOI18N
            case Ada95ASTSymbols.LIMITED : text = "limited"; break; //NOI18N
            case Ada95ASTSymbols.LOOP : text = "loop"; break; //NOI18N
            case Ada95ASTSymbols.MOD : text = "mod"; break; //NOI18N
            case Ada95ASTSymbols.NEW : text = "new"; break; //NOI18N
            case Ada95ASTSymbols.NOT : text = "not"; break; //NOI18N
            case Ada95ASTSymbols.NULL : text = "null"; break; //NOI18N
            case Ada95ASTSymbols.OF : text = "of"; break; //NOI18N
            case Ada95ASTSymbols.OR : text = "or"; break; //NOI18N
            case Ada95ASTSymbols.OTHERS : text = "others"; break; //NOI18N
            case Ada95ASTSymbols.OUT : text = "out"; break; //NOI18N
            case Ada95ASTSymbols.PACKAGE : text = "package"; break; //NOI18N
            case Ada95ASTSymbols.PRAGMA : text = "pragma"; break; //NOI18N
            case Ada95ASTSymbols.PRIVATE : text = "private"; break; //NOI18N
            case Ada95ASTSymbols.PROCEDURE : text = "procedure"; break; //NOI18N
            case Ada95ASTSymbols.PROTECTED : text = "protected"; break; //NOI18N
            case Ada95ASTSymbols.RETURN : text = "return"; break; //NOI18N
            case Ada95ASTSymbols.REVERSE : text = "reverse"; break; //NOI18N
            case Ada95ASTSymbols.RAISE : text = "raise"; break; //NOI18N
            case Ada95ASTSymbols.RANGE : text = "range"; break; //NOI18N
            case Ada95ASTSymbols.RECORD : text = "record"; break; //NOI18N
            case Ada95ASTSymbols.REM : text = "rem"; break; //NOI18N
            case Ada95ASTSymbols.RENAMES : text = "renames"; break; //NOI18N
            case Ada95ASTSymbols.REQUEUE : text = "requeue"; break; //NOI18N
            case Ada95ASTSymbols.SELECT : text = "select"; break; //NOI18N
            case Ada95ASTSymbols.SEPARATE : text = "separate"; break; //NOI18N
            case Ada95ASTSymbols.SUBTYPE : text = "subtype"; break; //NOI18N
            case Ada95ASTSymbols.TAGGED : text = "tagged"; break; //NOI18N
            case Ada95ASTSymbols.TASK : text = "task"; break; //NOI18N
            case Ada95ASTSymbols.TERMINATE : text = "terminate"; break; //NOI18N
            case Ada95ASTSymbols.THEN : text = "then"; break; //NOI18N
            case Ada95ASTSymbols.TYPE : text = "type"; break; //NOI18N
            case Ada95ASTSymbols.UNTIL : text = "until"; break; //NOI18N
            case Ada95ASTSymbols.USE : text = "use"; break; //NOI18N
            case Ada95ASTSymbols.WHEN : text = "when"; break; //NOI18N
            case Ada95ASTSymbols.WHILE : text = "while"; break; //NOI18N
            case Ada95ASTSymbols.WITH : text = "with"; break; //NOI18N
            case Ada95ASTSymbols.XOR : text = "xor"; break; //NOI18N
            case Ada95ASTSymbols.AMP : text = "&"; break; //NOI18N
            case Ada95ASTSymbols.TICK : text = "'"; break; //NOI18N
            case Ada95ASTSymbols.LPAREN : text = "("; break; //NOI18N
            case Ada95ASTSymbols.RPAREN : text = ")"; break; //NOI18N
            case Ada95ASTSymbols.STAR : text = "*"; break; //NOI18N
            case Ada95ASTSymbols.PLUS : text = "+"; break; //NOI18N
            case Ada95ASTSymbols.COMMA : text = ","; break; //NOI18N
            case Ada95ASTSymbols.MINUS : text = "-"; break; //NOI18N
            case Ada95ASTSymbols.DOT : text = "."; break; //NOI18N
            case Ada95ASTSymbols.SLASH : text = "/"; break; //NOI18N
            case Ada95ASTSymbols.COLON : text = ":"; break; //NOI18N
            case Ada95ASTSymbols.SEMICOLON : text = ";"; break; //NOI18N
            case Ada95ASTSymbols.GT : text = "<"; break; //NOI18N
            case Ada95ASTSymbols.EQ : text = "="; break; //NOI18N
            case Ada95ASTSymbols.LT : text = ">"; break; //NOI18N
            case Ada95ASTSymbols.BAR : text = "|"; break; //NOI18N
            case Ada95ASTSymbols.ARROW : text = "=>"; break; //NOI18N
            case Ada95ASTSymbols.DOT_DOT : text = ".."; break; //NOI18N
            case Ada95ASTSymbols.EXPONENT : text = "**"; break; //NOI18N
            case Ada95ASTSymbols.ASSIGNMENT : text = ":="; break; //NOI18N
            case Ada95ASTSymbols.INEQ : text = "/="; break; //NOI18N
            case Ada95ASTSymbols.GTEQ : text = ">="; break; //NOI18N
            case Ada95ASTSymbols.LTEQ : text = "<="; break; //NOI18N
            case Ada95ASTSymbols.LTLT : text = "<<"; break; //NOI18N
            case Ada95ASTSymbols.GTGT : text = ">>"; break; //NOI18N
            case Ada95ASTSymbols.BOX : text = "<>"; break; //NOI18N
        }
        return text;
    }
}
