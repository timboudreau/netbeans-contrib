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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * Parser for Makefile expressions
 *
 *   File: makefile.g
 * Author: Arkady Galyash, SPbSU,   <my_another@mail.ru>
 *   Date: 27-Mar-2007
 */
 
header{
  package org.netbeans.modules.cnd.make2netbeans.impl.generated;
  import antlr.LexerSharedInputState;
  import antlr.CharStreamException;
  import antlr.TokenStreamException;
  import java.io.FileReader;
  import org.netbeans.modules.cnd.make2netbeans.impl.Global;
  import org.netbeans.modules.cnd.make2netbeans.impl.IncludeMakefileException;
  import org.netbeans.modules.cnd.make2netbeans.impl.NeedWSException;
}

options
{
  language = "Java";
}

class MakefileParser extends Parser;

options
{
  k = 1;
}

// global variables
{
  // contains first word in every ("directive", "rule" 
  // or "variable definition") line
  String name;
  // counts nested conditional directives
  private int makelevel = 0;
}

/* Makefiles contain five kinds of things: "explicit rules", 
 * "implicit rules", "variable definitions", "directives", 
 * and "comments". 
 * (but "comments" was handled in scanner(lexer))
 */
makefile
//  throws IncludeMakefileException, NeedWSException
  :
   // It's impossible to recognize "rule", "variable definitions". 
   // that's why we have special rule(`item')
   (NEWLINE)*
   ( (  name=firstWord item 
      | directive
     )
     // `NEWLINE' and `TAB' (commandLine starts with TAB) indicates
     // EOL
     (  // empty string
        NEWLINE 
        // The command lines. 
      | (commandLine)+ NEWLINE
     )+
   )*
;

/* A variable name may be any sequence of characters not containing 
 * `:', `#', `=', or leading or trailing whitespace. However, variable
 * names containing characters other than letters, numbers, 
 * and underscores should be avoided.
 * But when we recognize this, we don't know is it variable name or
 * target of some rule. So, it may contain 
 * letters, numbers, underscores      - because of variables
 * `.', `(', `)', `\', `/', `-', `$'  - because of explicit rules
 * `%'                                - because of implicit rules
 */
firstWord returns [String result="";]:
       (y:LITERA{result += y.getText();})
       (x: LITERA {result += x.getText();}
       | x1: MINUS {result += x1.getText();})*
;

// All these strings are "rules" or "variable definitions".
item:
     rule
   | varDefinition 
   | {name.startsWith("$(error ") || name.startsWith("${error ")}? 
     {
       // Function `error' generates a fatal error. 
       // So, lets end parsing...
       throw new /*Recognition*/IncludeMakefileException();
     }
   | // This function works similarly to the `error' function, 
     // but processing of the makefile continues.
     // So, do nothing...
     {name.startsWith("$(warning ") || name.startsWith("${warning ")}? 
     
;

//----------------------------------------------------------
// "directive" handling
//----------------------------------------------------------
directive
  // `IncludeMakefileException' indicates (`include' directive)
  // Lexer to change InputState
  // `NeedWSException' indicates (`define' directive)
  // Lexer that whitespaces are important 
//  throws IncludeMakefileException, NeedWSException
{
  // for `ifeq' and `ifneq' directives
  boolean equals;
}
   : ("ifdef" defVar:LITERA 
       {
         if (makelevel > 0)
           makelevel++;
         else 
           if (null == Global.globalVars.get(defVar.getText()))
             makelevel++;
       }
   | "ifndef" ndefVar:LITERA 
       {   
         if (makelevel > 0)
           makelevel++;
         else    
           if (null != Global.globalVars.get(ndefVar.getText()))
             makelevel++;
       }
   | "ifeq" equals=eq_direct
       {
         if (makelevel > 0)
           makelevel++;
         else    
           if (!equals)
             makelevel++;
       }
   | "ifneq" equals=eq_direct
       {
         if (makelevel > 0)
           makelevel++;
         else    
           if (equals)
             makelevel++;
       }
   | ("include" | MINUS "include")
       (incFile :LITERA
         {
           if (makelevel == 0)
           {
             try{
               Global.makefiles.addFirst(new LexerSharedInputState(
                 new FileReader(Global.pwd + 
                   Global.eval(incFile.getText()))));
             }catch(Exception e){
               System.err.println("makefile.g: unexpected Exception"
                       + "in MakefileParser.directive():\n" + e);
             }
           }
         }
       )+ unreachable_break_Include
   | "endif" 
       {
         makelevel--;
         if (makelevel < 0)
           makelevel = 0;
       }
   | "define" unreachable_break_NeedWS
   | "else" 
       {
         if (makelevel == 1)
           makelevel--;
         if (makelevel == 0)
           makelevel++;
       }
   ) 
;

// The special state to start with when directive `define' appeared
// in makefile
define:
  {
    String var = "";
  }
    (WS)+
    (z:~(NEWLINE | TAB | WS) {var += z.getText();})+ // var name
    (WS)* (NEWLINE | TAB)
    {
      String value ="";
    }
    // var value
    (
      (x:~(NEWLINE | TAB | "endef"){value += ";"+x.getText();}
        (y:~(NEWLINE | TAB) {value += y.getText();})+
      )? 
      (NEWLINE | TAB)
    )*
    "endef"
    {
      value = value.substring(1,value.length());
      Global.globalVars.put(var, value);
    }
;

// for `if[n]eq' directives 
// tests condition(returns true - if value are equal)
eq_direct returns [boolean result = true]
{
  String firstExpr, secondExpr;
}
  :  var1:LITERA COMMA var2:LITERA 
       {
         /* this conditional directive is like 
          * `if[n]eq (ARG1, ARG2)'
          *          ^          ^
          *          |          |
          * let remove this `(' and `)'
          */
         String first = var1.getText().substring(1);
         String second = var2.getText();
         second.substring(0, second.length()-1);
         result = Global.eval(first).equals(Global.eval(second));
       }
  |  firstExpr=condExpr secondExpr=condExpr
       {
         /* this is other conditional directive
          * like 'ARG1' "ARG2" or
          *      "ARG1" "ARG2" etc.
          */
         result = Global.eval(firstExpr).equals
                  (Global.eval(secondExpr));
       }
;

// what kind could be arguments in conditional directives
condExpr returns [String result = ""]:
     // this is like 'EXPR'
     QUOTE quoExpr:LITERA QUOTE
     {
       result = quoExpr.getText();
     }
  |  // this is like "EXPR"
     DQUOTE dquoExpr:LITERA DQUOTE
     {
       result = dquoExpr.getText();
     }
;

/* We have this rule, because we cannot throw exception 
 * when we test literals (unreachable break)???
 * But we need to throw IncludeMakefileException
 */
unreachable_break_Include
//  throws IncludeMakefileException
 options
 {
   defaultErrorHandler = false;
 }
 : {
     throw new IncludeMakefileException();
   }
;

/* We have this rule, because we cannot throw exception 
 * when we test literals (unreachable break)???
 * But we need to throw NeedWSException
 */
unreachable_break_NeedWS
//  throws NeedWSException
 options
 {
   defaultErrorHandler = false;
 }
 : {
     throw new NeedWSException(true);
   }
;

//----------------------------------------------------------
// "variable definitions" handling
//----------------------------------------------------------

varDefinition
{
 // value of Makefile's variable
 String value;
}:
     /* To set a variable from the makefile, write a line starting 
      * with the variable name followed by `=' or `:='.
      * Whitespace around the variable name and immediately 
      * after the `=' is ignored.
      * Positive closure of `BLANK' was applied because of 
      * concatenating of strings ending on `\' in lexer 
      */
       BECOMES (WS)* value=varValue
       {
         /* "Simply expanded variables" are defined by lines using `:='
          * (`BECOMES'). The value of a simply expanded variable
          * is scanned once and for all, expanding any references 
          * to other variables and functions, when the variable 
          * is defined.  The actual value of the simply expanded 
          * variable is the result of expanding the text that you write. 
          */
         if (makelevel == 0){
           Global.globalVars.put(name, Global.eval(value));
         }
       }
     | EQUALS (WS)* value=varValue  
       {
        /* This flavor of variable is a "recursively expanded" 
         * variable. Variables of this sort are defined 
         * by lines using `='.
         */
         if (makelevel == 0){
           Global.globalVars.put(name, value);
         }
       }
     | ADDEQ (WS)* value=varValue
       {
        /* add more text to the value of a variable already defined 
         * with a line containing `+=' (`ADDEQ')
         */
         if (makelevel == 0){
           String tmp = Global.globalVars.remove(name);
           if (tmp == null)
             tmp = new String();
           Global.globalVars.put(name, tmp + " " + value);
         }
       }
;

/* Whatever follows the `=' or `:=' on the line becomes the value.
 * Whitespace immediately after the `=' is ignored.(That's why 
 * first char could not be blank).
 * @return String value of defining variable
 */

varValue returns [String result=""]:
{
  String option;
}
       (
         token:~( NEWLINE | INCL | SYSINCL | DEF | WS | TAB)
         {result += token.getText();}
           (token2:~( NEWLINE | INCL | SYSINCL | DEF | TAB)
              {result += token2.getText();}
           )*
       )?
       (option=minusOption {result += option;})?
       {
         result = result.trim();
       }
;

/* Anything starts with `-I' or `-D'
 * The most interesting for us(include directory or define macros)
 */
minusOption returns [String result=""]
{
  String var, token2, token3, token;
  token2="";
  token3="";
  token = "";
}
     : // include directory
       INCL { result += "-I" ; }
       (WS {result += " ";})*
       (
         inctoken:~( WS | NEWLINE | TAB ){token += inctoken.getText();}
       )+
       {
         result += token;
         Global.includeDirs.add(token);
       }
       ((WS {result += " ";})+
       token2=varValue
       {
         result += token2;
       })?
     | // system include directory
       SYSINCL { result += "-Y" ; }
       (WS {result += " ";}  WS {result += "I,";})*
       (
         sysinctoken:~( WS | NEWLINE | TAB ){token += sysinctoken.getText();}
       )+
       {
         result += token;
         Global.includeDirs.add(token);
       }
       ((WS {result += " ";})+
       token2=varValue
       {
         result += token2;
       })?
     | // define macros
       DEF {result += "-D";}
       (WS {result += " ";})*
       (
        err:~( WS | NEWLINE | TAB | EQUALS ){token3 += err.getText();}
       )+
       ( EQUALS
        (
          err2:~( WS | NEWLINE | TAB | EQUALS ){token += err2.getText();}
        )*
       )?
       {result += token3; Global.macros.put(token3, token);}
       ((WS {result += " ";})+
       token2=varValue
       {
         result += token2;
       })?
; 

//----------------------------------------------------------
// "rule" handling
//----------------------------------------------------------

/* describe part of "rule" which starts with `:'(COLON) 
 */
rule:
     COLON allPrerequisites commands
;

commands:
     // TARGETS : PREREQUISITES ; COMMAND
     //      COMMAND
     SEMI command 
     // TARGETS : PREREQUISITES
     //         COMMAND 
   | 
;

/* There are actually two different types of prerequisites 
 * understood by GNU `make': normal prerequisites and 
 * "order-only" prerequisites.
 */
allPrerequisites: 
     prerequisites orderOnlyPrerequisites
   | // there are maybe no prerequisites
;

/* all the rest string, except `|'(PIPE) and `;'(SEMI)
 * was decided to be filenames of prerequisites BADCOMMENT
 */
prerequisites:
     (x:~( NEWLINE | PIPE | SEMI | TAB))+
;

orderOnlyPrerequisites:
     PIPE (prerequisites)?
   | // there are maybe no order-only prerequisites BADCOMMENT
;

//----------------------------------------------------------
// "command" handling
//----------------------------------------------------------

// every command line starts with TAB
commandLine:
      TAB command 
;

// command could contain any symbol 
command:
{
  String option = "";
  String comm = "";
}
       (
         token:~( NEWLINE | INCL | SYSINCL | DEF | TAB)
	 {
	   comm += token.getText();
	 }
       )*
       // but we want to find options `-I' and `-D' here too
       (
         option=minusOption
	 {
	   comm += option;
	 }
       )?
;

//----------------------------------------------------------
// "command"
//----------------------------------------------------------

//----------------------------------------------------------
// Scanner(Lexer) for Makefile expressions
//----------------------------------------------------------
class MakefileLexer extends Lexer;

options
{
  k = 2; // two characters of lookahead
  charVocabulary = '\u0000'..'\ufffe';
  testLiterals = false;
}
{
  // should we skip whitespaces or not???
  private boolean needWS = false;

  public void needWS(boolean b){
    needWS = b;
  }
  
    protected Token createToken(int type) throws InstantiationException, IllegalAccessException {
        return new antlr.CommonToken();
    }

  /* when the lexer has hit EOF condition.
   */
  public void uponEOF() 
    throws TokenStreamException/*, CharStreamException*/{
    if (Global.makefiles.size() > 0){
      setInputState(Global.makefiles.getFirst());
      //throw new antlr.CharStreamException("INCLUDE MAKEFILE");
    }
  }
}

/* `#' in a line of a makefile starts a "comment".
 * It and the rest of the line are ignored, 
 * except that a trailing backslash not
 * escaped by another backslash will continue the comment across
 * multiple lines.
 */
COMMENT
options
{
  checkSkip = true;
}
       : '#'(~('\n'|'\r')|('\\' NEWLINE ))*
         // skip comments  
          {$setType(Token.SKIP); needWS = false;};

SPLIT
options
{
  checkSkip = true;
}
      : '\\' ('\n' | '\r' | "\r\n")
         {$setType(Token.SKIP); newline();$setText("");};

LITERA
options
{
  testLiterals = true;
}
/* LITERA cannot starts with `-', because we want `-I' and 
 * `-D' to be special tokens
 */
: ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '.' | '/' | '%' |
   '+' | '(' | ')' | VAR | '@')
  ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '.' | '/' | '%' |
   '+' | '(' | ')' | VAR | '@' | '-')*
;

// anything like `$(...)', `${...}' or $x
protected
VAR 
: '$' ( ( '(' | '{' ) (~( '$' | ')' | '}' | '\\' | '\r' | '\n') 
        | '\\' ~('\r' | '\n') | SPLIT | VAR )* ( ')' | '}' )
        | ~('(' | '{' | '=')
      )
;

SL : '\\' ;

COMMA: ',';

QUOTE: '\'';

DQUOTE: '\"';

WS
options
{
  checkSkip = true;
}
: (' '| '\t')+ { if (!needWS) $setType(Token.SKIP);};

EQUALS: '=' {needWS = true;};

BECOMES : ":=" {needWS = true;};

QUESTEQ: "?=" {needWS = true;};

ADDEQ: "+=" {needWS = true;};

INCL: "-I";

SYSINCL: "-Y";

MINUS : '-';

DEF: "-D";

TAB: ('\r' | '\n' )'\t'{needWS = true;};
   
PIPE: '|';

SEMI: ';';

COLON: ':';

OTHER: ~( ':' | ';' | '\t' | '|' | '\n' | '\r' | 'a'..'z' | '/' | 'A'..'Z' | '_' | ' ' | '0' .. '9' | '=' | '#' | '.' | '$' | ')' | '(' | '-' | '\\' | '%' | '+' | ',' | '\'' | '"' | '@');

NEWLINE
{newline(); needWS = false;}
    :   '\n'
    |   '\r'
    ;
