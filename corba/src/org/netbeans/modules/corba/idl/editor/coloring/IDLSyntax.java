/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba.idl.editor.coloring;

import com.netbeans.editor.Syntax;
import com.netbeans.editor.BaseSyntax;

/**
* Syntax analyzes for IDL source files.
* Tokens and internal states are given below. 
*
* @author Miloslav Metelka
* @version 1.00
*/

public class IDLSyntax extends BaseSyntax {

  // Token names
  public static final String TN_KEYWORD = "keyword";
  public static final String TN_IDENTIFIER = "identifier";
  public static final String TN_METHOD = "method";
  public static final String TN_OPERATOR = "operator";
  public static final String TN_LINE_COMMENT = "line-comment";
  public static final String TN_BLOCK_COMMENT = "block-comment";
  public static final String TN_CHAR = "char";
  public static final String TN_STRING = "string";
  public static final String TN_INT = "int";
  public static final String TN_HEX = "hex";
  public static final String TN_OCTAL = "octal";
  public static final String TN_LONG = "long";
  public static final String TN_FLOAT = "float";
  public static final String TN_DIRECTIVE = "directive";

  // Token IDs
  public static final int KEYWORD = 2; // keyword
  public static final int IDENTIFIER = 3; // identifier
  public static final int METHOD = 4; // method call i.e. name()
  public static final int OPERATOR = 5; // operators like '+', '*=' etc.
  public static final int LINE_COMMENT = 6; // comment till end of line
  public static final int BLOCK_COMMENT = 7; // block comment
  public static final int CHAR = 8; // char constant e.g. 'c'
  public static final int STRING = 9; // string constant e.g. "string"
  public static final int INT = 10; // integer constant e.g. 1234
  public static final int HEX = 11; // hex constant e.g. 0x5a
  public static final int OCTAL = 12; // octal constant e.g. 0123
  public static final int LONG = 13; // long constant e.g. 12L
  public static final int FLOAT = 14; // float constant e.g. 1.5e+43
  public static final int DIRECTIVE = 15;  // CPP derective e.g. #include <...>

  // Internal states
  private static final int ISI_ERROR = 1; // after carriage return
  private static final int ISI_TEXT = 2; // inside white space
  private static final int ISI_WS_P_IDENTIFIER = 3; // inside WS past identifier
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
  private static final int ISI_FLOAT = 30; // float number
  private static final int ISI_FLOAT_EXP = 31; // float number
  private static final int ISI_HEX = 32; // hex number
  private static final int ISA_DOT = 33; // after '.'
  private static final int ISA_HASH = 34; // right after '#'
  private static final int ISA_DIRECTIVE = 36; // after directive
  private static final int ISI_HERROR = 37; // after hash got error

  /** Helper index used for method coloring */
  int hlpInd = -1; // -1 means invalid value

  public IDLSyntax() {
  }

  public boolean isIdentifierPart(char ch) {
    return Character.isJavaIdentifierPart(ch);
  }

  protected int parseToken() {
    char actChar;

    while(curInd < stopInd) {
      actChar = buffer[curInd];

      switch (state) {
        case INIT:
          switch (actChar) {
            case '\n':
              curInd++;
              return EOL;
            case ' ':
            case '\t':
              state = ISI_TEXT;
              break;
            case '"':
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
            case '!':
              state = ISA_EXCLAMATION;
              break;
            case '0':
              state = ISA_ZERO;
              break;
            case '.':
              state = ISA_DOT;
              break;
            case '#':
              state = ISA_HASH;
              break;
            default:
              if (actChar >= '1' && actChar <= '9') { // '0' already handled
                state = ISI_INT;
                break;
              }

              if (Character.isJavaIdentifierStart(actChar)) { // identifier
                state = ISI_IDENTIFIER;
                break;
              }

              // everything else is an operator
              curInd++;
              return OPERATOR;
          }
          break;

        case ISI_ERROR:
          switch (actChar) {
            case ' ':
            case '\t':
            case '\n':
              state = INIT;
              return ERROR;
          }
          break;

        case ISI_TEXT: // white space
          if (actChar != ' ' && actChar != '\t') {
            state = INIT;
            return TEXT;
          }
          break;

        case ISI_WS_P_IDENTIFIER:
          switch (actChar) {
            case ' ':
            case '\t':
              break;
            default:
              curInd = hlpInd;
              hlpInd = -1; // make hlpInd invalid
              state = INIT;
              return (actChar == '(') ? METHOD : IDENTIFIER;
          }
          break;

        case ISI_LINE_COMMENT:
          switch (actChar) {
            case '\n':
              state = INIT;
              return LINE_COMMENT;
          }
          break;

        case ISI_BLOCK_COMMENT:
          switch (actChar) {
            case '\n':
              if (curInd == begInd) { // only '\n'
                curInd++;
                return EOL; // stay in ISI_BLOCK_COMMENT state for next line
              } else { // return comment token to qualify for previous if()
                return BLOCK_COMMENT;
              }
            case '*':
              state = ISA_STAR_I_BLOCK_COMMENT;
              break;
          }
          break;

        case ISI_STRING:
          switch (actChar) {
            case '\\':
              state = ISI_STRING_A_BSLASH;
              break;
            case '\n':
              state = INIT;
              return STRING;
            case '"':
              curInd++;
              state = INIT;
              return STRING;
          }
          break;

        case ISI_STRING_A_BSLASH:
          switch (actChar) {
            case '"':
            case '\\':
              break;
            default:
              curInd--;
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
              return CHAR;
            case '\'':
              curInd++;
              state = INIT;
              return CHAR;
          }
          break;

        case ISI_CHAR_A_BSLASH:
          switch (actChar) {
            case '\'':
            case '\\':
              break;
            default:
              curInd--;
              break;
          }
          state = ISI_CHAR;
          break;

        case ISI_IDENTIFIER:
          if (!(Character.isJavaIdentifierPart(actChar))) {
            if (matchKeywords()) { // it's keyword
              state = INIT;
              return KEYWORD;
            } else {
              switch (actChar) {
                case '(': // it's method
                  state = INIT;
                  return METHOD;
                case ' ':
                case '\t':
                  state = ISI_WS_P_IDENTIFIER;
                  hlpInd = curInd; // end of identifier
                  break;
                default:
                  state = INIT;
                  return IDENTIFIER;
              }
            }
          }
          break;

        case ISA_SLASH:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            case '/':
              state = ISI_LINE_COMMENT;
              break;
            case '*':
              state = ISI_BLOCK_COMMENT;
              break;
            default:
              state = INIT;
              return OPERATOR;
          }
          break;

        case ISA_EQ:
          switch (actChar) {
            case '=':
              curInd++;
              return  OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_GT:
          switch (actChar) {
            case '>':
              state = ISA_GTGT;
              break;
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          break;

        case ISA_GTGT:
          switch (actChar) {
            case '>':
              state = ISA_GTGTGT;
              break;
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          break;

        case ISA_GTGTGT:
          switch (actChar) {
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;


        case ISA_LT:
          switch (actChar) {
            case '<':
              state = ISA_LTLT;
              break;
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          break;

        case ISA_LTLT:
          switch (actChar) {
            case '<':
              state = ISI_ERROR;
              break;
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          break;

        case ISA_PLUS:
          switch (actChar) {
            case '+':
              // let it flow to '='
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_MINUS:
          switch (actChar) {
            case '-':
              // let it flow to '='
            case '=':
              curInd++;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_STAR:
          switch (actChar) {
            case '=':
              curInd++;
              return OPERATOR;
            case '/':
              curInd++;
              state = INIT;
              return ERROR; // '*/' outside comment
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_STAR_I_BLOCK_COMMENT:
          switch (actChar) {
            case '/':
              curInd++;
              state = INIT;
              return BLOCK_COMMENT;
            default:
              curInd--;
              state = ISI_BLOCK_COMMENT;
              break;
          }
          break;

        case ISA_PIPE:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_PERCENT:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_AND:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_XOR:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_EXCLAMATION:
          switch (actChar) {
            case '=':
              curInd++;
              state = INIT;
              return OPERATOR;
            default:
              state = INIT;
              return OPERATOR;
          }
          // break;

        case ISA_ZERO:
          switch (actChar) {
            case '.':
              state = ISI_FLOAT;
              break;
            case 'x':
            case 'X':
              state = ISI_HEX;
              break;
            case 'l':
            case 'L':
              curInd++;
              state = INIT;
              return LONG;
            case 'f':
            case 'F':
            case 'd':
            case 'D':
              curInd++;
              state = INIT;
              return FLOAT;
            case '8': // it's error to have '8' and '9' in octal number
            case '9':
              state = ISI_ERROR;
              break;
            default:
              if (actChar >= '0' && actChar <= '7') {
                state = ISI_OCTAL;
                break;
              }
              state = INIT;
              return INT;
          }
          break;

        case ISI_INT:
          switch (actChar) {
            case 'l':
            case 'L':
              curInd++;
              state = INIT;
              return LONG;
            case '.':
              state = ISI_FLOAT;
              break;
            default:
              if (!(actChar >= '0' && actChar <= '9')) {
                state = INIT;
                return INT;
              }
          }
          break;

        case ISI_OCTAL:
          if (!(actChar >= '0' && actChar <= '7')) {

            state = INIT;
            return OCTAL;
          }
          break;

        case ISI_FLOAT:
          switch (actChar) {
            case 'f':
            case 'F':
            case 'd':
            case 'D':
              curInd++;
              state = INIT;
              return FLOAT;
            case 'e':
            case 'E':
              state = ISI_FLOAT_EXP;
              break;
            default:
              if (!((actChar >= '0' && actChar <= '9')
                || actChar == '.')) {

                state = INIT;
                return FLOAT;
              }
          }
          break;

        case ISI_FLOAT_EXP:
          switch (actChar) {
            case 'f':
            case 'F':
            case 'd':
            case 'D':
              curInd++;
              state = INIT;
              return FLOAT;
            default:
              if (!((actChar >= '0' && actChar <= '9')
                || actChar == '-' || actChar == '+')) {
                state = INIT;
                return FLOAT;
              }
          }
          break;

        case ISI_HEX:
          if (!((actChar >= 'a' && actChar <= 'f')
            || (actChar >= 'A' && actChar <= 'F')
            || (actChar >= '0' && actChar <= '9'))) {

            state = INIT;
            return HEX;
          }
          break;

        case ISA_DOT:
          if (actChar >= '0' && actChar <= '9') {
            state = ISI_FLOAT;
            break;
          }
          state = INIT;
          return OPERATOR;

        case ISA_HASH:
          if (Character.isJavaIdentifierPart(actChar)) {
             break; // continue possible directive string
          }
          if (matchDirective()) { // directive found
            state = ISA_DIRECTIVE;
            return DIRECTIVE;
          }
          switch (actChar) {
            case '\n':
              state = INIT;
              return TEXT;
          }
          state = ISI_HERROR; // directive error
          return ERROR;
	    
        case ISA_DIRECTIVE:
          switch (actChar) {
            case '\n':
              state = INIT;
              return DIRECTIVE;
          }
          break;
          
	 case ISI_HERROR:
	    switch (actChar) {
	    case '\n':
	       state = INIT;
	       return ERROR;
	    }
	    break;
	    
      } // end of switch(state)
      
      curInd = ++curInd;
    } // end of while(curInd...)

    /** At this stage there's no more text in the scanned buffer.
    * Scanner first checks whether this is completely the last
    * available buffer.
    */

    if (lastBuffer) {
      switch(state) {
        case ISI_IDENTIFIER:
          return matchKeywords() ? KEYWORD : IDENTIFIER;
        case ISA_HASH:
          return matchDirective() ? DIRECTIVE : TEXT;
        case ISI_WS_P_IDENTIFIER:
          curInd = hlpInd;
          hlpInd = -1;
          state = INIT;
          return IDENTIFIER;
        case ISA_STAR_I_BLOCK_COMMENT:
          return BLOCK_COMMENT;
        case ISA_ZERO:
          return INT;
        case ISA_DOT:
        case ISA_SLASH:
        case ISA_EQ:
        case ISA_GT:
        case ISA_GTGT:
        case ISA_GTGTGT:
        case ISA_LT:
        case ISA_LTLT:
        case ISA_PLUS:
        case ISA_MINUS:
        case ISA_STAR:
        case ISA_PIPE:
        case ISA_PERCENT:
        case ISA_AND:
        case ISA_XOR:
        case ISA_EXCLAMATION:
          return OPERATOR;
        case ISI_STRING_A_BSLASH:
          return STRING;
        case ISI_CHAR_A_BSLASH:
          return CHAR;
      }
    }

    /* At this stage there's no more text in the scanned buffer, but
    * this buffer is not the last so the scan will continue on another buffer.
    * The scanner tries to minimize the amount of characters
    * that will be prescanned in the next buffer.
    */

    switch (state) {
      case ISI_ERROR:
        return ERROR;
      case ISI_TEXT:
        return TEXT;
      case ISI_WS_P_IDENTIFIER: // white space past identifier
        return EOT; // rescan till begining of ?identifier/keyword?
      case ISI_IDENTIFIER:
        return EOT; // rescan till begining of ?identifier/keyword?
      case ISA_HASH:
        return EOT; // rescan till begining of ?identifier/keyword?
      case ISA_DIRECTIVE:
        return DIRECTIVE;
      case ISI_HERROR:
        return ERROR;
      case ISI_LINE_COMMENT:
        return LINE_COMMENT;
      case ISI_BLOCK_COMMENT:
        return BLOCK_COMMENT;
      case ISI_STRING:
        return STRING;
      case ISI_STRING_A_BSLASH:
        if (curInd - begInd > 1) {
          curInd--; // go to backslash char
          state = ISI_STRING;
          return STRING;
        } else {
          return EOT; // only one (backslash) char
        }
      case ISI_CHAR:
        return CHAR;
      case ISI_CHAR_A_BSLASH:
        if (curInd - begInd > 1) {
          curInd--; // go to backslash char
          state = ISI_CHAR;
          return CHAR;
        } else {
          return EOT; // only one (backslash) char
        }
      case ISA_DOT:
      case ISA_SLASH:
      case ISA_EQ:
      case ISA_GT:
      case ISA_GTGT:
      case ISA_GTGTGT:
      case ISA_LT:
      case ISA_LTLT:
      case ISA_PLUS:
      case ISA_MINUS:
      case ISA_STAR:
      case ISA_PIPE:
      case ISA_PERCENT:
      case ISA_AND:
      case ISA_XOR:
      case ISA_EXCLAMATION:
        return EOT; // only short ones
      case ISA_STAR_I_BLOCK_COMMENT:
        return BLOCK_COMMENT;
      case ISI_INT:
        return INT;
      case ISI_OCTAL:
        return OCTAL;
      case ISI_FLOAT:
        return FLOAT;
      case ISI_FLOAT_EXP:
        return FLOAT;
      case ISI_HEX:
        return HEX;
    }

    return EOT;

  }


   /** match IDL keywords */
   private boolean matchKeywords() {
      if (curInd - begInd > 9)
	 return false;
      if (curInd - begInd <= 0)
	 return false;
      switch (buffer[begInd + 0]) {
      case 'F':
	 return curInd - begInd == 5
	    && buffer[begInd + 1] == 'A'
	    && buffer[begInd + 2] == 'L'
	    && buffer[begInd + 3] == 'S'
	    && buffer[begInd + 4] == 'E';
      case 'O':
	 return curInd - begInd == 6
	    && buffer[begInd + 1] == 'b'
	    && buffer[begInd + 2] == 'j'
	    && buffer[begInd + 3] == 'e'
	    && buffer[begInd + 4] == 'c'
	    && buffer[begInd + 5] == 't';
      case 'T':
	 return curInd - begInd == 4
	    && buffer[begInd + 1] == 'R'
	    && buffer[begInd + 2] == 'U'
	    && buffer[begInd + 3] == 'E';
      case 'a':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'n':
	    return curInd - begInd == 3
	       && buffer[begInd + 2] == 'y';
	 case 't':
	    return curInd - begInd == 9
	       && buffer[begInd + 2] == 't'
	       && buffer[begInd + 3] == 'r'
	       && buffer[begInd + 4] == 'i'
	       && buffer[begInd + 5] == 'b'
	       && buffer[begInd + 6] == 'u'
	       && buffer[begInd + 7] == 't'
	       && buffer[begInd + 8] == 'e';
	 default:
	    return false;
	 }
      case 'b':
	 return curInd - begInd == 7
	    && buffer[begInd + 1] == 'o'
	    && buffer[begInd + 2] == 'o'
	    && buffer[begInd + 3] == 'l'
	    && buffer[begInd + 4] == 'e'
	    && buffer[begInd + 5] == 'a'
	    && buffer[begInd + 6] == 'n';
      case 'c':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'a':
	    return curInd - begInd == 4
	       && buffer[begInd + 2] == 's'
	       && buffer[begInd + 3] == 'e';
	 case 'h':
	    return curInd - begInd == 4
	       && buffer[begInd + 2] == 'a'
	       && buffer[begInd + 3] == 'r';
	 case 'o':
	    if (curInd - begInd <= 2)
	       return false;
	    switch (buffer[begInd + 2]) {
	    case 'n':
	       if (curInd - begInd <= 3)
		  return false;
	       switch (buffer[begInd + 3]) {
	       case 's':
		  return curInd - begInd == 5
		     && buffer[begInd + 4] == 't';
	       case 't':
		  return curInd - begInd == 7
		     && buffer[begInd + 4] == 'e'
		     && buffer[begInd + 5] == 'x'
		     && buffer[begInd + 6] == 't';
	       default:
		  return false;
	       }
	    default:
	       return false;
	    }
	 default:
	    return false;
	 }
      case 'd':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'e':
	    return curInd - begInd == 7
	       && buffer[begInd + 2] == 'f'
	       && buffer[begInd + 3] == 'a'
	       && buffer[begInd + 4] == 'u'
	       && buffer[begInd + 5] == 'l'
	       && buffer[begInd + 6] == 't';
	 case 'o':
	    return curInd - begInd == 6
	       && buffer[begInd + 2] == 'u'
	       && buffer[begInd + 3] == 'b'
	       && buffer[begInd + 4] == 'l'
	       && buffer[begInd + 5] == 'e';
	 default:
	    return false;
	 }
      case 'e':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'n':
	    return curInd - begInd == 4
	       && buffer[begInd + 2] == 'u'
	       && buffer[begInd + 3] == 'm';
	 case 'x':
	    return curInd - begInd == 9
	       && buffer[begInd + 2] == 'c'
	       && buffer[begInd + 3] == 'e'
	       && buffer[begInd + 4] == 'p'
	       && buffer[begInd + 5] == 't'
	       && buffer[begInd + 6] == 'i'
	       && buffer[begInd + 7] == 'o'
	       && buffer[begInd + 8] == 'n';
	 default:
	    return false;
	 }
      case 'f':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'i':
	    return curInd - begInd == 5
	       && buffer[begInd + 2] == 'x'
	       && buffer[begInd + 3] == 'e'
	       && buffer[begInd + 4] == 'd';
	 case 'l':
	    return curInd - begInd == 5
	       && buffer[begInd + 2] == 'o'
	       && buffer[begInd + 3] == 'a'
	       && buffer[begInd + 4] == 't';
	 default:
	    return false;
	 }
      case 'i':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'n':
	    if (curInd - begInd == 2)
	       return true;
	    switch (buffer[begInd + 2]) {
	    case 'o':
	       return curInd - begInd == 5
		  && buffer[begInd + 3] == 'u'
		  && buffer[begInd + 4] == 't';
	    case 't':
	       return curInd - begInd == 9
		  && buffer[begInd + 3] == 'e'
		  && buffer[begInd + 4] == 'r'
		  && buffer[begInd + 5] == 'f'
		  && buffer[begInd + 6] == 'a'
		  && buffer[begInd + 7] == 'c'
		  && buffer[begInd + 8] == 'e';
	    default:
	       return false;
	    }
	 default:
	    return false;
	 }
      case 'l':
	 return curInd - begInd == 4
	    && buffer[begInd + 1] == 'o'
	    && buffer[begInd + 2] == 'n'
	    && buffer[begInd + 3] == 'g';
      case 'm':
	 return curInd - begInd == 6
	    && buffer[begInd + 1] == 'o'
	    && buffer[begInd + 2] == 'd'
	    && buffer[begInd + 3] == 'u'
	    && buffer[begInd + 4] == 'l'
	    && buffer[begInd + 5] == 'e';
      case 'o':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'c':
	    return curInd - begInd == 5
	       && buffer[begInd + 2] == 't'
	       && buffer[begInd + 3] == 'e'
	       && buffer[begInd + 4] == 't';
	 case 'n':
	    return curInd - begInd == 6
	       && buffer[begInd + 2] == 'e'
	       && buffer[begInd + 3] == 'w'
	       && buffer[begInd + 4] == 'a'
	       && buffer[begInd + 5] == 'y';
	 case 'u':
	    return curInd - begInd == 3
	       && buffer[begInd + 2] == 't';
	 default:
	    return false;
	 }
      case 'r':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'a':
	    return curInd - begInd == 6
	       && buffer[begInd + 2] == 'i'
	       && buffer[begInd + 3] == 's'
	       && buffer[begInd + 4] == 'e'
	       && buffer[begInd + 5] == 's';
	 case 'e':
	    return curInd - begInd == 8
	       && buffer[begInd + 2] == 'a'
	       && buffer[begInd + 3] == 'd'
	       && buffer[begInd + 4] == 'o'
	       && buffer[begInd + 5] == 'n'
	       && buffer[begInd + 6] == 'l'
	       && buffer[begInd + 7] == 'y';
	 default:
	    return false;
	 }
      case 's':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'e':
	    return curInd - begInd == 8
	       && buffer[begInd + 2] == 'q'
	       && buffer[begInd + 3] == 'u'
	       && buffer[begInd + 4] == 'e'
	       && buffer[begInd + 5] == 'n'
	       && buffer[begInd + 6] == 'c'
	       && buffer[begInd + 7] == 'e';
	 case 'h':
	    return curInd - begInd == 5
	       && buffer[begInd + 2] == 'o'
	       && buffer[begInd + 3] == 'r'
	       && buffer[begInd + 4] == 't';
	 case 't':
	    if (curInd - begInd <= 2)
	       return false;
	    switch (buffer[begInd + 2]) {
	    case 'r':
	       if (curInd - begInd <= 3)
		  return false;
	       switch (buffer[begInd + 3]) {
	       case 'i':
		  return curInd - begInd == 6
		     && buffer[begInd + 4] == 'n'
		     && buffer[begInd + 5] == 'g';
	       case 'u':
		  return curInd - begInd == 6
		     && buffer[begInd + 4] == 'c'
		     && buffer[begInd + 5] == 't';
	       default:
		  return false;
	       }
	    default:
	       return false;
	    }
	 case 'w':
	    return curInd - begInd == 6
	       && buffer[begInd + 2] == 'i'
	       && buffer[begInd + 3] == 't'
	       && buffer[begInd + 4] == 'c'
	       && buffer[begInd + 5] == 'h';
	 default:
	    return false;
	 }
      case 't':
	 return curInd - begInd == 7
	    && buffer[begInd + 1] == 'y'
	    && buffer[begInd + 2] == 'p'
	    && buffer[begInd + 3] == 'e'
	    && buffer[begInd + 4] == 'd'
	    && buffer[begInd + 5] == 'e'
	    && buffer[begInd + 6] == 'f';
      case 'u':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'n':
	    if (curInd - begInd <= 2)
	       return false;
	    switch (buffer[begInd + 2]) {
	    case 'i':
	       return curInd - begInd == 5
		  && buffer[begInd + 3] == 'o'
		  && buffer[begInd + 4] == 'n';
	    case 's':
	       return curInd - begInd == 8
		  && buffer[begInd + 3] == 'i'
		  && buffer[begInd + 4] == 'g'
		  && buffer[begInd + 5] == 'n'
		  && buffer[begInd + 6] == 'e'
		  && buffer[begInd + 7] == 'd';
	    default:
	       return false;
	    }
	 default:
	    return false;
	 }
      case 'v':
	 return curInd - begInd == 4
	    && buffer[begInd + 1] == 'o'
	    && buffer[begInd + 2] == 'i'
	    && buffer[begInd + 3] == 'd';
      case 'w':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'c':
	    return curInd - begInd == 5
	       && buffer[begInd + 2] == 'h'
	       && buffer[begInd + 3] == 'a'
	       && buffer[begInd + 4] == 'r';
	 case 's':
	    return curInd - begInd == 7
	       && buffer[begInd + 2] == 't'
	       && buffer[begInd + 3] == 'r'
	       && buffer[begInd + 4] == 'i'
	       && buffer[begInd + 5] == 'n'
	       && buffer[begInd + 6] == 'g';
	 default:
	    return false;
	 }
      default:
	 return false;
      }
   }

   private boolean matchDirective () {
      if (curInd - begInd > 8)
	 return false;
      if (curInd - begInd <= 0)
	 return false;
      switch (buffer[begInd + 0]) {
      case '#':
	 if (curInd - begInd <= 1)
	    return false;
	 switch (buffer[begInd + 1]) {
	 case 'd':
	    return curInd - begInd == 7
	       && buffer[begInd + 2] == 'e'
	       && buffer[begInd + 3] == 'f'
	       && buffer[begInd + 4] == 'i'
	       && buffer[begInd + 5] == 'n'
	       && buffer[begInd + 6] == 'e';
	 case 'e':
	    return curInd - begInd == 6
	       && buffer[begInd + 2] == 'n'
	       && buffer[begInd + 3] == 'd'
	       && buffer[begInd + 4] == 'i'
	       && buffer[begInd + 5] == 'f';
	 case 'i':
	    if (curInd - begInd <= 2)
	       return false;
	    switch (buffer[begInd + 2]) {
	    case 'f':
	       if (curInd - begInd <= 3)
		  return false;
	       switch (buffer[begInd + 3]) {
	       case 'd':
		  return curInd - begInd == 6
		     && buffer[begInd + 4] == 'e'
		     && buffer[begInd + 5] == 'f';
	       case 'n':
		  return curInd - begInd == 7
		     && buffer[begInd + 4] == 'd'
		     && buffer[begInd + 5] == 'e'
		     && buffer[begInd + 6] == 'f';
	       default:
		  return false;
	       }
	    case 'n':
	       return curInd - begInd == 8
		  && buffer[begInd + 3] == 'c'
		  && buffer[begInd + 4] == 'l'
		  && buffer[begInd + 5] == 'u'
		  && buffer[begInd + 6] == 'd'
		  && buffer[begInd + 7] == 'e';
	    default:
	       return false;
	    }
	 case 'p':
	    return curInd - begInd == 7
	       && buffer[begInd + 2] == 'r'
	       && buffer[begInd + 3] == 'a'
	       && buffer[begInd + 4] == 'g'
	       && buffer[begInd + 5] == 'm'
	       && buffer[begInd + 6] == 'a';
	 default:
	    return false;
	 }
      default:
	 return false;
      }
   }

  public void relocate(char buffer[], int offset, int len) {
    if (hlpInd >= 0) { // relocate hlpInd before calling super.relocScan()
      hlpInd += (offset - curInd);
    }
    super.relocate(buffer, offset, len);
  }

  /** Create scan state appropriate for particular scanner */
  protected MarkState createMarkState() {
    return new IDLMarkState();
  }

  /** Store state of this scanner into given scan state. */
  protected void storeState(MarkState markState) {
    super.storeState(markState);
    ((IDLMarkState)markState).hlpPreScan = (hlpInd >= 0) ? (curInd - hlpInd) : -1;
  }

  /** Load state into scanner. Indexes are already initialized
  * when this function is called.
  */
  protected void loadState(MarkState markState) {
    super.loadState(markState);
    int hi = ((IDLMarkState)markState).hlpPreScan;
    hlpInd = (hi >= 0) ? (curInd - hi) : -1;
  }

  /** Initialize scanner in case the state stored in syntax mark
  * is null.
  */
  protected void loadInitState() {
    super.loadInitState();
    hlpInd = -1;
  }

  public String getStateName(int stateNumber) {
    switch(stateNumber) {
      case ISI_ERROR:
        return "ISI_ERROR";
      case ISI_TEXT:
        return "ISI_TEXT";
      case ISI_WS_P_IDENTIFIER:
        return "ISI_WS_P_IDENTIFIER";
      case ISI_LINE_COMMENT:
        return "ISI_LINE_COMMENT";
      case ISI_BLOCK_COMMENT:
        return "ISI_BLOCK_COMMENT";
      case ISI_STRING:
        return "ISI_STRING";
      case ISI_STRING_A_BSLASH:
        return "ISI_STRING_A_BSLASH";
      case ISI_CHAR:
        return "ISI_CHAR";
      case ISI_CHAR_A_BSLASH:
        return "ISI_CHAR_A_BSLASH";
      case ISI_IDENTIFIER:
        return "ISI_IDENTIFIER";
      case ISA_SLASH:
        return "ISA_SLASH";
      case ISA_EQ:
        return "ISA_EQ";
      case ISA_GT:
        return "ISA_GT";
      case ISA_GTGT:
        return "ISA_GTGT";
      case ISA_GTGTGT:
        return "ISA_GTGTGT";
      case ISA_LT:
        return "ISA_LT";
      case ISA_LTLT:
        return "ISA_LTLT";
      case ISA_PLUS:
        return "ISA_PLUS";
      case ISA_MINUS:
        return "ISA_MINUS";
      case ISA_STAR:
        return "ISA_STAR";
      case ISA_STAR_I_BLOCK_COMMENT:
        return "ISA_STAR_I_BLOCK_COMMENT";
      case ISA_PIPE:
        return "ISA_PIPE";
      case ISA_PERCENT:
        return "ISA_PERCENT";
      case ISA_AND:
        return "ISA_AND";
      case ISA_XOR:
        return "ISA_XOR";
      case ISA_EXCLAMATION:
        return "ISA_EXCLAMATION";
      case ISA_ZERO:
        return "ISA_ZERO";
      case ISI_INT:
        return "ISI_INT";
      case ISI_OCTAL:
        return "ISI_OCTAL";
      case ISI_FLOAT:
        return "ISI_FLOAT";
      case ISI_FLOAT_EXP:
        return "ISI_FLOAT_EXP";
      case ISI_HEX:
        return "ISI_HEX";
      case ISA_DOT:
        return "ISA_DOT";

      default:
        return super.getStateName(stateNumber);
    }
  }

  public String getTokenName(int tokenID) {
    switch (tokenID) {
      case KEYWORD:
        return TN_KEYWORD;
      case IDENTIFIER:
        return TN_IDENTIFIER;
      case METHOD:
        return TN_METHOD;
      case OPERATOR:
        return TN_OPERATOR;
      case LINE_COMMENT:
        return TN_LINE_COMMENT;
      case BLOCK_COMMENT:
        return TN_BLOCK_COMMENT;
      case CHAR:
        return TN_CHAR;
      case STRING:
        return TN_STRING;
      case INT:
        return TN_INT;
      case HEX:
        return TN_HEX;
      case OCTAL:
        return TN_OCTAL;
      case LONG:
        return TN_LONG;
      case FLOAT:
          return TN_FLOAT;
      case DIRECTIVE:
        return TN_DIRECTIVE;
      default:
        return super.getTokenName(tokenID);
    }
  }

  /** Create array of arrays of chars containing wrong characters */
  protected char[][] createWrongCharsArray() {
    return new char[][] {
      WRONG_NL, // wrong chars for text
      WRONG_NL, // error
      WRONG_NL_TAB_SPC, // keyword
      WRONG_NL_TAB_SPC, // identifier
      WRONG_NL_TAB_SPC, // method
      WRONG_NL_TAB_SPC, // operator
      WRONG_NL, // line comment
      WRONG_NL, // block comment
      WRONG_NL, // char
      WRONG_NL, // string
      WRONG_NL_TAB_SPC, // int
      WRONG_NL_TAB_SPC, // hex
      WRONG_NL_TAB_SPC, // octal
      WRONG_NL_TAB_SPC, // long
      WRONG_NL_TAB_SPC // float
    };
  }

  public String toString() {
    String s = super.toString();
    s += ", hlpInd=" + hlpInd;
    return s;
  }
  
  class IDLMarkState extends Syntax.MarkState {

    /** Helper prescan for method coloring */
    int hlpPreScan;

  }

}

/*
 * <<Log>>
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */

