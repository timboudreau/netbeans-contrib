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
package org.netbeans.modules.ada.editor.lexer;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.ada.editor.AdaVersion;
import org.netbeans.modules.ada.editor.parser.Ada95ASTSymbols;
import org.netbeans.modules.ada.editor.ast.nodes.*;
import java_cup.runtime.*;

%%
// Options adn declarations section

%class Ada95ASTLexer
%implements Scanner
%type Symbol
%function next_token
%public

%eofval{
    return createSymbol(Ada95ASTSymbols.EOF);
%eofval}
%eofclose

%unicode
%caseless

//Turns character counting on
%char
//Turns line counting on
%line
//Turns column counting on
%column


%state ST_LOOKING_FOR_PROPERTY
%state ST_COMMENT

%{
    private final List commentList = new LinkedList();
    private String comment = null;
    private StateStack stack = new StateStack();
    private char yy_old_buffer[] = new char[ZZ_BUFFERSIZE];
    private int yy_old_pushbackPos;
    protected int commentStartPosition;

    public AdaVersion getAdaVersion() {
            return AdaVersion.ADA_95;
    }

    public void reset(java.io.Reader reader) {
        yyreset(reader);
    }

    public void setState(int state) {
        yybegin(state);
    }

    public int getState() {
        return yystate();
    }

    public void resetCommentList() {
            commentList.clear();
    }

    public List getCommentList() {
            return commentList;
    }
	
    protected void addComment(Comment.Type type) {
            int leftPosition = getTokenStartPosition();
            Comment comm;
            comm = new Comment(commentStartPosition, leftPosition + getTokenLength());
            commentList.add(comm);
    }
	
    private void pushState(int state) {
        stack.pushStack(zzLexicalState);
        yybegin(state);
    }

    private void popState() {
        yybegin(stack.popStack());
    }

    public int getCurrentLine() {
        return yyline;
    }

    protected int getTokenStartPosition() {
        return zzStartRead - zzPushbackPos;
    }

    protected int getTokenLength() {
        return zzMarkedPos - zzStartRead;
    }

    public int getLength() {
        return zzEndRead - zzPushbackPos;
    }
    
    private void handleCommentStart() {
        commentStartPosition = getTokenStartPosition();
    }
	
    private void handleLineCommentEnd() {
         addComment(Comment.Type.TYPE_SINGLE_LINE);
    }
        
    private Symbol createFullSymbol(int symbolNumber) {
        Symbol symbol = createSymbol(symbolNumber);
        symbol.value = yytext();
        return symbol;
    }

    private Symbol createSymbol(int symbolNumber) {
        int leftPosition = getTokenStartPosition();
        Symbol symbol = new Symbol(symbolNumber, leftPosition, leftPosition + getTokenLength());
        return symbol;
    }

    public int[] getParamenters(){
    	return new int[]{zzMarkedPos, zzPushbackPos, zzCurrentPos, zzStartRead, zzEndRead, yyline};
    }
    
    public void reset(java.io.Reader  reader, char[] buffer, int[] parameters){
            this.zzReader = reader;
            this.zzBuffer = buffer;
            this.zzMarkedPos = parameters[0];
            this.zzPushbackPos = parameters[1];
            this.zzCurrentPos = parameters[2];
            this.zzStartRead = parameters[3];
            this.zzEndRead = parameters[4];
            this.yyline = parameters[5];
            this.yychar = this.zzStartRead - this.zzPushbackPos;
    }

%}

/*********************************************************
 *                                                       *
 * Ada 95 AST Lexer, based on:                           *
 *                                                       *
 * 1. Ada Reference Manual                               *
 *    ISO/IEC 8652:1995(E)                               *
 *    with Technical Corrigendum 1                       *
 *    Language and Standard Libraries                    *
 *    Copyright © 1992,1993,1994,1995 Intermetrics, Inc. *
 *    Copyright © 2000 The MITRE Corporation, Inc.       *
 * 2. http://www.adaic.com/standards/95lrm/lexer9x.l     *
 *                                                       *
 * Modified for "Ada for Netbeans" and for using with    *
 * JFlex by Andrea Lucarelli.                            *
 *                                                       *
 *********************************************************/

DIGIT=[0-9]
EXTENDED_DIGIT=[0-9a-fA-F]
INTEGER=({DIGIT}(_?{DIGIT})*)
EXPONENT=([eE](\+?|-){INTEGER})
DECIMAL_LITERAL={INTEGER}(\.?{INTEGER})?{EXPONENT}?
BASE={INTEGER}
BASED_INTEGER={EXTENDED_DIGIT}(_?{EXTENDED_DIGIT})*
BASED_LITERAL={BASE}#{BASED_INTEGER}(\.{BASED_INTEGER})?#{EXPONENT}?
IDENTIFIER=[a-zA-Z]("_"?[a-zA-Z0-9])*
WHITESPACE=[ \n\r\t]+
STRING_LITERAL=(\\\"|[^\n\r\"]|\\{WHITESPACE}+\\)*
CHAR_LITERAL=\'[^\n]\'
WHITESPACE=[ \n\r\t]+
NEWLINE=("\r"|"\n"|"\r\n")
ANY_CHAR=(.|[\n])

%%

// Ada 95 keywords

<YYINITIAL> {

    "abort"         { return createSymbol(Ada95ASTSymbols.ABORT); }
    "abs"           { return createSymbol(Ada95ASTSymbols.ABS); }
    "abstract"      { return createSymbol(Ada95ASTSymbols.ABSTRACT); }
    "access"        { return createSymbol(Ada95ASTSymbols.ACCESS); }
    "accept"        { return createSymbol(Ada95ASTSymbols.ACCEPT); }
    "aliased"       { return createSymbol(Ada95ASTSymbols.ALIASED); }
    "all"           { return createSymbol(Ada95ASTSymbols.ALL); }
    "and"           { return createSymbol(Ada95ASTSymbols.AND); }
    "array"         { return createSymbol(Ada95ASTSymbols.ARRAY); }
    "at"            { return createSymbol(Ada95ASTSymbols.AT); }
    "begin"         { return createSymbol(Ada95ASTSymbols.BEGIN); }
    "body"          { return createSymbol(Ada95ASTSymbols.BODY); }
    "constant"      { return createSymbol(Ada95ASTSymbols.CONSTANT); }
    "case"          { return createSymbol(Ada95ASTSymbols.CASE); }
    "declare"       { return createSymbol(Ada95ASTSymbols.DECLARE); }
    "delay"         { return createSymbol(Ada95ASTSymbols.DELAY); }
    "do"            { return createSymbol(Ada95ASTSymbols.DO); }
    "delta"         { return createSymbol(Ada95ASTSymbols.DELTA); }
    "else"          { return createSymbol(Ada95ASTSymbols.ELSE); }
    "elsif"         { return createSymbol(Ada95ASTSymbols.ELSIF); }
    "end"           { return createSymbol(Ada95ASTSymbols.END); }
    "entry"         { return createSymbol(Ada95ASTSymbols.ENTRY); }
    "exception"     { return createSymbol(Ada95ASTSymbols.EXCEPTION); }
    "exit"          { return createSymbol(Ada95ASTSymbols.EXIT); }
    "for"           { return createSymbol(Ada95ASTSymbols.FOR); }
    "function"      { return createSymbol(Ada95ASTSymbols.FUNCTION); }
    "generic"       { return createSymbol(Ada95ASTSymbols.GENERIC); }
    "goto"          { return createSymbol(Ada95ASTSymbols.GOTO); }
    "if"            { return createSymbol(Ada95ASTSymbols.IF); }
    "in"            { return createSymbol(Ada95ASTSymbols.IN); }
    "is"            { return createSymbol(Ada95ASTSymbols.IS); }
    "limited"       { return createSymbol(Ada95ASTSymbols.LIMITED); }
    "loop"          { return createSymbol(Ada95ASTSymbols.LOOP); }
    "mod"           { return createSymbol(Ada95ASTSymbols.MOD); }
    "new"           { return createSymbol(Ada95ASTSymbols.NEW); }
    "not"           { return createSymbol(Ada95ASTSymbols.NOT); }
    "null"          { return createSymbol(Ada95ASTSymbols.NULL); }
    "of"            { return createSymbol(Ada95ASTSymbols.OF); }
    "or"            { return createSymbol(Ada95ASTSymbols.OR); }
    "others"        { return createSymbol(Ada95ASTSymbols.OTHERS); }
    "out"           { return createSymbol(Ada95ASTSymbols.OUT); }
    "package"       { return createSymbol(Ada95ASTSymbols.PACKAGE); }
    "pragma"        { return createSymbol(Ada95ASTSymbols.PRAGMA); }
    "private"       { return createSymbol(Ada95ASTSymbols.PRIVATE); }
    "procedure"     { return createSymbol(Ada95ASTSymbols.PROCEDURE); }
    "protected"     { return createSymbol(Ada95ASTSymbols.PROTECTED); }
    "return"        { return createSymbol(Ada95ASTSymbols.RETURN); }
    "reverse"       { return createSymbol(Ada95ASTSymbols.REVERSE); }
    "raise"         { return createSymbol(Ada95ASTSymbols.RAISE); }
    "range"         { return createSymbol(Ada95ASTSymbols.RANGE); }
    "record"        { return createSymbol(Ada95ASTSymbols.RECORD); }
    "rem"           { return createSymbol(Ada95ASTSymbols.REM); }
    "renames"       { return createSymbol(Ada95ASTSymbols.RENAMES); }
    "requeue"       { return createSymbol(Ada95ASTSymbols.REQUEUE); }
    "select"        { return createSymbol(Ada95ASTSymbols.SELECT); }
    "separate"      { return createSymbol(Ada95ASTSymbols.SEPARATE); }
    "subtype"       { return createSymbol(Ada95ASTSymbols.SUBTYPE); }
    "tagged"        { return createSymbol(Ada95ASTSymbols.TAGGED); }
    "task"          { return createSymbol(Ada95ASTSymbols.TASK); }
    "terminate"     { return createSymbol(Ada95ASTSymbols.TERMINATE); }
    "then"          { return createSymbol(Ada95ASTSymbols.THEN); }
    "type"          { return createSymbol(Ada95ASTSymbols.TYPE); }
    "until"         { return createSymbol(Ada95ASTSymbols.UNTIL); }
    "use"           { return createSymbol(Ada95ASTSymbols.USE); }
    "when"          { return createSymbol(Ada95ASTSymbols.WHEN); }
    "while"         { return createSymbol(Ada95ASTSymbols.WHILE); }
    "with"          { return createSymbol(Ada95ASTSymbols.WITH); }
    "xor"           { return createSymbol(Ada95ASTSymbols.XOR); }

}

// delimiters

<YYINITIAL> {

    "&"             { return createSymbol(Ada95ASTSymbols.AMP); }
    "'"             { return createSymbol(Ada95ASTSymbols.TICK); }
    "("             { return createSymbol(Ada95ASTSymbols.LPAREN); }
    ")"             { return createSymbol(Ada95ASTSymbols.RPAREN); }
    "*"             { return createSymbol(Ada95ASTSymbols.STAR); }
    "+"             { return createSymbol(Ada95ASTSymbols.PLUS); }
    ","             { return createSymbol(Ada95ASTSymbols.COMMA); }
    "-"             { return createSymbol(Ada95ASTSymbols.MINUS); }
    "."             {
                        pushState(ST_LOOKING_FOR_PROPERTY);
                        return createSymbol(Ada95ASTSymbols.DOT);
                    }
    "/"             { return createSymbol(Ada95ASTSymbols.SLASH); }
    ":"             { return createSymbol(Ada95ASTSymbols.COLON); }
    ";"             { return createSymbol(Ada95ASTSymbols.SEMICOLON); }
    "<"             { return createSymbol(Ada95ASTSymbols.GT); }
    "="             { return createSymbol(Ada95ASTSymbols.EQ); }
    ">"             { return createSymbol(Ada95ASTSymbols.LT); }
    "|"             { return createSymbol(Ada95ASTSymbols.BAR); }
    "!"             { return createSymbol(Ada95ASTSymbols.BAR); }

}

// compound delimiters

<YYINITIAL> {

    "=>"             { return createSymbol(Ada95ASTSymbols.ARROW); }
    ".."             { return createSymbol(Ada95ASTSymbols.DOT_DOT); }
    "**"             { return createSymbol(Ada95ASTSymbols.EXPONENT); }
    ":="             { return createSymbol(Ada95ASTSymbols.ASSIGNMENT); }
    "/="             { return createSymbol(Ada95ASTSymbols.INEQ); }
    ">="             { return createSymbol(Ada95ASTSymbols.GTEQ); }
    "<="             { return createSymbol(Ada95ASTSymbols.LTEQ); }
    "<<"             { return createSymbol(Ada95ASTSymbols.LTLT); }
    ">>"             { return createSymbol(Ada95ASTSymbols.GTGT); }
    "<>"             { return createSymbol(Ada95ASTSymbols.BOX); }

}

<ST_LOOKING_FOR_PROPERTY>"." {
	return createSymbol(Ada95ASTSymbols.DOT);
}

<ST_LOOKING_FOR_PROPERTY>{IDENTIFIER} {
    popState();
    return createFullSymbol(Ada95ASTSymbols.IDENTIFIER);
}

<ST_LOOKING_FOR_PROPERTY>{CHAR_LITERAL} {
    yypushback(yylength());
    popState();
}

<YYINITIAL>\'{CHAR_LITERAL}\' {
    return createFullSymbol(Ada95ASTSymbols.CHAR_LITERAL);
}

<YYINITIAL>\"{STRING_LITERAL}\" {
    return createFullSymbol(Ada95ASTSymbols.STRING_LITERAL);
}

<YYINITIAL>{BASED_LITERAL} {
    return createFullSymbol(Ada95ASTSymbols.BASED_LITERAL);
}

<YYINITIAL>{DECIMAL_LITERAL} {
    return createFullSymbol(Ada95ASTSymbols.DECIMAL_LITERAL);
}

<YYINITIAL>{IDENTIFIER} {
    return createFullSymbol(Ada95ASTSymbols.IDENTIFIER);
}

<YYINITIAL>{WHITESPACE} {
}

<YYINITIAL>"--" {
	handleCommentStart();
	yybegin(ST_COMMENT);
}

<ST_COMMENT>[^\n\r]*(.|{NEWLINE}) {
        handleLineCommentEnd();
        yybegin(YYINITIAL);
}

<YYINITIAL> <<EOF>> {
              if (yytext().length() > 0) {
                yypushback(1);  // backup eof
                comment = yytext();
              }
              else {
                return createSymbol(Ada95ASTSymbols.EOF);
              }
              
}

<YYINITIAL>{ANY_CHAR} {
	// do nothing
}