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

import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
%%

%public
%class Ada95SyntaxLexer
%type AdaTokenId
%function nextToken
%unicode
%caseless
%char

%state ST_LOOKING_FOR_PROPERTY
%state ST_COMMENT
%state ST_HIGHLIGHTING_ERROR

%eofval{
       if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return AdaTokenId.UNKNOWN_TOKEN;
        } else {
            return null;
        }
%eofval}

%{

    private StateStack stack = new StateStack();

    private boolean short_tags_allowed = true;

    private LexerInput input;
    
    public Ada95SyntaxLexer(LexerRestartInfo info) {
        this.input = info.input();

        if(info.state() != null) {
            //reset state
            setState((LexerState)info.state());
        } else {
            //initial state
            zzState = zzLexicalState = YYINITIAL;
            stack.clear();
        }

    }

    public static final class LexerState  {
        final StateStack stack;
        /** the current state of the DFA */
        final int zzState;
        /** the current lexical state */
        final int zzLexicalState;

        LexerState (StateStack stack, int zzState, int zzLexicalState) {
            this.stack = stack;
            this.zzState = zzState;
            this.zzLexicalState = zzLexicalState;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                    return true;
            }

            if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
            }

            LexerState state = (LexerState) obj;
            return (this.stack.equals(state.stack)
                && (this.zzState == state.zzState)
                && (this.zzLexicalState == state.zzLexicalState));
        }

        @Override
        public int hashCode() {
            int hash = 11;
            hash = 31 * hash + this.zzState;
            hash = 31 * hash + this.zzLexicalState;
            hash = 31 * hash + this.stack.hashCode();
            return hash;
        }
    }
        
    public LexerState getState() {
        return new LexerState(stack.createClone(), zzState, zzLexicalState);
    }

    public void setState(LexerState state) {
        this.stack.copyFrom(state.stack);
        this.zzState = state.zzState;
        this.zzLexicalState = state.zzLexicalState;
    }
    
    public int[] getParamenters(){
    	return new int[]{zzMarkedPos, zzPushbackPos, zzCurrentPos, zzStartRead, zzEndRead, yyline, zzLexicalState};
    }

    protected int getZZLexicalState() {
        return zzLexicalState;
    }

    protected int getZZMarkedPos() {
        return zzMarkedPos;
    }

    protected int getZZEndRead() {
        return zzEndRead;
    }

    public char[] getZZBuffer() {
        return zzBuffer;
    }
    
    protected int getZZStartRead() {
    	return this.zzStartRead;
    }

    protected int getZZPushBackPosition() {
    	return this.zzPushbackPos;
    }

        protected void pushBack(int i) {
		yypushback(i);
	}

        protected void popState() {
		yybegin(stack.popStack());
	}

	protected void pushState(final int state) {
		stack.pushStack(getZZLexicalState());
		yybegin(state);
	}

    
 // End user code

%}

/*********************************************************
 *                                                       *
 * Ada 95 Lexer, based on:                               *
 *                                                       *
 * 1. Ada Reference Manual                               *
 *    ISO/IEC 8652:1995(E)                               *
 *    with Technical Corrigendum 1                       *
 *    Language and Standard Libraries                    *
 *    Copyright © 1992,1993,1994,1995 Intermetrics, Inc. *
 *    Copyright © 2000 The MITRE Corporation, Inc.       *
 * 2. http://www.adaic.com/standards/95lrm/lexer9x.l     *
 *                                                       *
 * Modified for "Ada for Netbeans" and for using it with *
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

    "abort"         { return AdaTokenId.ABORT; }
    "abs"           { return AdaTokenId.ABS; }
    "abstract"      { return AdaTokenId.ABSTRACT; }
    "access"        { return AdaTokenId.ACCESS; }
    "accept"        { return AdaTokenId.ACCEPT; }
    "aliased"       { return AdaTokenId.ALIASED; }
    "all"           { return AdaTokenId.ALL; }
    "and"           { return AdaTokenId.AND; }
    "array"         { return AdaTokenId.ARRAY; }
    "at"            { return AdaTokenId.AT; }
    "begin"         { return AdaTokenId.BEGIN; }
    "body"          { return AdaTokenId.BODY; }
    "constant"      { return AdaTokenId.CONSTANT; }
    "case"          { return AdaTokenId.CASE; }
    "declare"       { return AdaTokenId.DECLARE; }
    "delay"         { return AdaTokenId.DELAY; }
    "delta"         { return AdaTokenId.DELTA; }
    "digits"        { return AdaTokenId.DIGITS; }
    "do"            { return AdaTokenId.DO; }
    "else"          { return AdaTokenId.ELSE; }
    "elsif"         { return AdaTokenId.ELSIF; }
    "end"           { return AdaTokenId.END; }
    "entry"         { return AdaTokenId.ENTRY; }
    "exception"     { return AdaTokenId.EXCEPTION; }
    "exit"          { return AdaTokenId.EXIT; }
    "for"           { return AdaTokenId.FOR; }
    "function"      { return AdaTokenId.FUNCTION; }
    "generic"       { return AdaTokenId.GENERIC; }
    "goto"          { return AdaTokenId.GOTO; }
    "if"            { return AdaTokenId.IF; }
    "in"            { return AdaTokenId.IN; }
    "is"            { return AdaTokenId.IS; }
    "limited"       { return AdaTokenId.LIMITED; }
    "loop"          { return AdaTokenId.LOOP; }
    "mod"           { return AdaTokenId.MOD; }
    "new"           { return AdaTokenId.NEW; }
    "not"           { return AdaTokenId.NOT; }
    "null"          { return AdaTokenId.NULL; }
    "of"            { return AdaTokenId.OF; }
    "or"            { return AdaTokenId.OR; }
    "others"        { return AdaTokenId.OTHERS; }
    "out"           { return AdaTokenId.OUT; }
    "package"       { return AdaTokenId.PACKAGE; }
    "pragma"        { return AdaTokenId.PRAGMA; }
    "private"       { return AdaTokenId.PRIVATE; }
    "procedure"     { return AdaTokenId.PROCEDURE; }
    "protected"     { return AdaTokenId.PROTECTED; }
    "return"        { return AdaTokenId.RETURN; }
    "reverse"       { return AdaTokenId.REVERSE; }
    "raise"         { return AdaTokenId.RAISE; }
    "range"         { return AdaTokenId.RANGE; }
    "record"        { return AdaTokenId.RECORD; }
    "rem"           { return AdaTokenId.REM; }
    "renames"       { return AdaTokenId.RENAMES; }
    "requeue"       { return AdaTokenId.REQUEUE; }
    "select"        { return AdaTokenId.SELECT; }
    "separate"      { return AdaTokenId.SEPARATE; }
    "subtype"       { return AdaTokenId.SUBTYPE; }
    "tagged"        { return AdaTokenId.TAGGED; }
    "task"          { return AdaTokenId.TASK; }
    "terminate"     { return AdaTokenId.TERMINATE; }
    "then"          { return AdaTokenId.THEN; }
    "type"          { return AdaTokenId.TYPE; }
    "until"         { return AdaTokenId.UNTIL; }
    "use"           { return AdaTokenId.USE; }
    "when"          { return AdaTokenId.WHEN; }
    "while"         { return AdaTokenId.WHILE; }
    "with"          { return AdaTokenId.WITH; }
    "xor"           { return AdaTokenId.XOR; }
}

// Ada 95 compound keywords

<YYINITIAL> {
    "end case"      { return AdaTokenId.END_CASE; }
    "end if"        { return AdaTokenId.END_IF; }
    "end loop"      { return AdaTokenId.END_LOOP; }
}

// attributes

<YYINITIAL> {

    \'"access"                          { return AdaTokenId.ATTRIBUTE; }
    \'"address"                         { return AdaTokenId.ATTRIBUTE; }
    \'"adjacent"                        { return AdaTokenId.ATTRIBUTE; }
    \'"aft"                             { return AdaTokenId.ATTRIBUTE; }
    \'"alignment"                       { return AdaTokenId.ATTRIBUTE; }
    \'"base"                            { return AdaTokenId.ATTRIBUTE; }
    \'"bit_order"                       { return AdaTokenId.ATTRIBUTE; }
    \'"body_version"                    { return AdaTokenId.ATTRIBUTE; }
    \'"callable"                        { return AdaTokenId.ATTRIBUTE; }
    \'"caller"                          { return AdaTokenId.ATTRIBUTE; }
    \'"ceiling"                         { return AdaTokenId.ATTRIBUTE; }
    \'"class"                           { return AdaTokenId.ATTRIBUTE; }
    \'"component_size"                  { return AdaTokenId.ATTRIBUTE; }
    \'"compose"                         { return AdaTokenId.ATTRIBUTE; }
    \'"constrained"                     { return AdaTokenId.ATTRIBUTE; }
    \'"copy_sign"                       { return AdaTokenId.ATTRIBUTE; }
    \'"count"                           { return AdaTokenId.ATTRIBUTE; }
    \'"delta"                           { return AdaTokenId.ATTRIBUTE; }
    \'"denorm"                          { return AdaTokenId.ATTRIBUTE; }
    \'"digits"                          { return AdaTokenId.ATTRIBUTE; }
    \'"exponent"                        { return AdaTokenId.ATTRIBUTE; }
    \'"external_tag"                    { return AdaTokenId.ATTRIBUTE; }
    \'"first"                           { return AdaTokenId.ATTRIBUTE; }
    \'"first_bit"                       { return AdaTokenId.ATTRIBUTE; }
    \'"floor"                           { return AdaTokenId.ATTRIBUTE; }
    \'"fore"                            { return AdaTokenId.ATTRIBUTE; }
    \'"fraction"                        { return AdaTokenId.ATTRIBUTE; }
    \'"identity"                        { return AdaTokenId.ATTRIBUTE; }
    \'"image"                           { return AdaTokenId.ATTRIBUTE; }
    \'"input"                           { return AdaTokenId.ATTRIBUTE; }
    \'"last"                            { return AdaTokenId.ATTRIBUTE; }
    \'"last_bit"                        { return AdaTokenId.ATTRIBUTE; }
    \'"leading_part"                    { return AdaTokenId.ATTRIBUTE; }
    \'"length"                          { return AdaTokenId.ATTRIBUTE; }
    \'"machine"                         { return AdaTokenId.ATTRIBUTE; }
    \'"machine_emax"                    { return AdaTokenId.ATTRIBUTE; }
    \'"machine_emin"                    { return AdaTokenId.ATTRIBUTE; }
    \'"machine_mantissa"                { return AdaTokenId.ATTRIBUTE; }
    \'"machine_overflows"               { return AdaTokenId.ATTRIBUTE; }
    \'"machine_radix"                   { return AdaTokenId.ATTRIBUTE; }
    \'"machine_rounds"                  { return AdaTokenId.ATTRIBUTE; }
    \'"max"                             { return AdaTokenId.ATTRIBUTE; }
    \'"max_size_in_storage_elements"    { return AdaTokenId.ATTRIBUTE; }
    \'"min"                             { return AdaTokenId.ATTRIBUTE; }
    \'"model"                           { return AdaTokenId.ATTRIBUTE; }
    \'"model_emin"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Model_Epsilon"                   { return AdaTokenId.ATTRIBUTE; }
    \'"Model_Mantissa"                  { return AdaTokenId.ATTRIBUTE; }
    \'"Model_Small"                     { return AdaTokenId.ATTRIBUTE; }
    \'"Modulus"                         { return AdaTokenId.ATTRIBUTE; }
    \'"output"                          { return AdaTokenId.ATTRIBUTE; }
    \'"Partition_ID"                    { return AdaTokenId.ATTRIBUTE; }
    \'"Pos"                             { return AdaTokenId.ATTRIBUTE; }
    \'"Position"                        { return AdaTokenId.ATTRIBUTE; }
    \'"Range"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Read"                            { return AdaTokenId.ATTRIBUTE; }
    \'"Remainder"                       { return AdaTokenId.ATTRIBUTE; }
    \'"Round"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Rounding"                        { return AdaTokenId.ATTRIBUTE; }
    \'"Safe_First"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Safe_Last"                       { return AdaTokenId.ATTRIBUTE; }
    \'"Scale"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Scaling"                         { return AdaTokenId.ATTRIBUTE; }
    \'"Signed_Zeros"                    { return AdaTokenId.ATTRIBUTE; }
    \'"Size"                            { return AdaTokenId.ATTRIBUTE; }
    \'"Small"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Storage_Pool"                    { return AdaTokenId.ATTRIBUTE; }
    \'"Storage_Size"                    { return AdaTokenId.ATTRIBUTE; }
    \'"Succ"                            { return AdaTokenId.ATTRIBUTE; }
    \'"Tag"                             { return AdaTokenId.ATTRIBUTE; }
    \'"Terminated"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Truncation"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Unbiased_Rounding"               { return AdaTokenId.ATTRIBUTE; }
    \'"Unchecked_Access"                { return AdaTokenId.ATTRIBUTE; }
    \'"Val"                             { return AdaTokenId.ATTRIBUTE; }
    \'"Valid"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Value"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Version"                         { return AdaTokenId.ATTRIBUTE; }
    \'"Wide_Image"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Wide_Value"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Wide_Width"                      { return AdaTokenId.ATTRIBUTE; }
    \'"Width"                           { return AdaTokenId.ATTRIBUTE; }
    \'"Write"                           { return AdaTokenId.ATTRIBUTE; }

}

// delimiters

<YYINITIAL> {

    "&"             { return AdaTokenId.AMP; }
    "'"             { return AdaTokenId.TICK; }
    "("             { return AdaTokenId.LPAREN; }
    ")"             { return AdaTokenId.RPAREN; }
    "*"             { return AdaTokenId.STAR; }
    "+"             { return AdaTokenId.PLUS; }
    ","             { return AdaTokenId.COMMA; }
    "-"             { return AdaTokenId.MINUS; }
    "."             {
                        pushState(ST_LOOKING_FOR_PROPERTY);
                        return AdaTokenId.DOT;
                    }
    "/"             { return AdaTokenId.SLASH; }
    ":"             { return AdaTokenId.COLON; }
    ";"             { return AdaTokenId.SEMICOLON; }
    "<"             { return AdaTokenId.GT; }
    "="             { return AdaTokenId.EQ; }
    ">"             { return AdaTokenId.LT; }
    "|"             { return AdaTokenId.BAR; }
    "!"             { return AdaTokenId.BAR; }

}

// compound delimiters

<YYINITIAL> {

    "=>"             { return AdaTokenId.ARROW; }
    ".."             { return AdaTokenId.DOT_DOT; }
    "**"             { return AdaTokenId.EXPON; }
    ":="             { return AdaTokenId.ASSIGNMENT; }
    "/="             { return AdaTokenId.INEQ; }
    ">="             { return AdaTokenId.GTEQ; }
    "<="             { return AdaTokenId.LTEQ; }
    "<<"             { return AdaTokenId.LTLT; }
    ">>"             { return AdaTokenId.GTGT; }
    "<>"             { return AdaTokenId.BOX; }

}

<YYINITIAL> {

    "boolean"           { return AdaTokenId.BOOLEAN; }
    "character"         { return AdaTokenId.CHARACTER; }
    "float"             { return AdaTokenId.FLOAT; }
    "integer"           { return AdaTokenId.INTEGER; }
    "wide_character"    { return AdaTokenId.WIDE_CHARECTER; }
    "true"              { return AdaTokenId.TRUE; }
    "false"             { return AdaTokenId.FALSE; }

}

<ST_LOOKING_FOR_PROPERTY>"." {
    return AdaTokenId.DOT;
}

<ST_LOOKING_FOR_PROPERTY>{IDENTIFIER} {
    popState();
    return AdaTokenId.IDENTIFIER;
}

<YYINITIAL>{IDENTIFIER} {
    return  AdaTokenId.IDENTIFIER;
}

<ST_HIGHLIGHTING_ERROR> {
    {WHITESPACE} {
        popState();
        return AdaTokenId.WHITESPACE;
    }
    . {
        return AdaTokenId.UNKNOWN_TOKEN;
    }
}

<YYINITIAL>{WHITESPACE} {
    return  AdaTokenId.WHITESPACE;
}

<ST_LOOKING_FOR_PROPERTY>{CHAR_LITERAL} {
    yypushback(1);
    popState();
}

<YYINITIAL>{DECIMAL_LITERAL} {
    return AdaTokenId.DECIMAL_LITERAL;
}

<YYINITIAL>{BASED_LITERAL} {
    return AdaTokenId.BASED_LITERAL;
}

<YYINITIAL>\"{STRING_LITERAL}\" {
    return AdaTokenId.STRING_LITERAL;
}

<YYINITIAL>\"{STRING_LITERAL} {
    pushState(ST_HIGHLIGHTING_ERROR);
    return  AdaTokenId.UNKNOWN_TOKEN;
}

<YYINITIAL>\'{CHAR_LITERAL}\' {
    return AdaTokenId.CHAR_LITERAL;
}

<YYINITIAL>\'{CHAR_LITERAL} {
    pushState(ST_HIGHLIGHTING_ERROR);
    return  AdaTokenId.UNKNOWN_TOKEN;
}

<YYINITIAL>\'{CHAR_LITERAL} {
    pushState(ST_HIGHLIGHTING_ERROR);
    return  AdaTokenId.UNKNOWN_TOKEN;
}

<YYINITIAL>"--" {
    pushState(ST_COMMENT);
    return AdaTokenId.COMMENT;
}

<ST_COMMENT>[^\n\r]*{ANY_CHAR} {
    popState();
    return AdaTokenId.COMMENT;
}

<ST_COMMENT>{NEWLINE} {
    popState();
    return AdaTokenId.COMMENT;
}

<YYINITIAL>. {
    yypushback(1);
    pushState(ST_HIGHLIGHTING_ERROR);
}
