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
import com.netbeans.editor.TokenID;

/**
* Syntax analyzes for IDL source files.
* Tokens and internal states are given below. 
*
* @author Miloslav Metelka
* @version 1.00
*/

public class IDLSyntax extends Syntax {

  // Token IDs
  public static final int TEXT_ID = 1; // plain text
  public static final int ERROR_ID = 2; // errorneous text
  public static final int KEYWORD_ID = 3; // keyword
  public static final int IDENTIFIER_ID = 4; // identifier
  public static final int METHOD_ID = 5; // method call i.e. name()
  public static final int OPERATOR_ID = 6; // operators like '+', '*=' etc.
  public static final int LINE_COMMENT_ID = 7; // comment till end of line
  public static final int BLOCK_COMMENT_ID = 8; // block comment
  public static final int CHAR_ID = 9; // char constant e.g. 'c'
  public static final int STRING_ID = 10; // string constant e.g. "string"
  public static final int INT_ID = 11; // integer constant e.g. 1234
  public static final int HEX_ID = 12; // hex constant e.g. 0x5a
  public static final int OCTAL_ID = 13; // octal constant e.g. 0123
  public static final int LONG_ID = 14; // long constant e.g. 12L
  public static final int FLOAT_ID = 15; // float constant e.g. 1.5e+43
  public static final int DIRECTIVE_ID = 16;  // CPP derective e.g. #include <...>

  // TokenIDs
  /** Plain text */
  public static final IDLTokenID TEXT = new IDLTokenID("idl-text", TEXT_ID);
  /** Errorneous text */
  public static final IDLTokenID ERROR = new IDLTokenID("idl-error", ERROR_ID);
  /** IDL keyword */
  public static final IDLTokenID KEYWORD = new IDLTokenID("idl-keyword", KEYWORD_ID);
  /** IDL identifier */
  public static final IDLTokenID IDENTIFIER = new IDLTokenID("idl-identifier", IDENTIFIER_ID);
  /** IDL method call i.e. name() */
  public static final IDLTokenID METHOD = new IDLTokenID("idl-method", METHOD_ID);
  /** IDL operators like '+', '*=' etc. */
  public static final IDLTokenID OPERATOR = new IDLTokenID("idl-operator", OPERATOR_ID);
  /** IDL comment till end of line */
  public static final IDLTokenID LINE_COMMENT = new IDLTokenID("idl-line_comment", LINE_COMMENT_ID);
  /** IDL block comment */
  public static final IDLTokenID BLOCK_COMMENT = new IDLTokenID("idl-block_comment", BLOCK_COMMENT_ID);
  /** IDL char constant e.g. 'c' */
  public static final IDLTokenID CHAR = new IDLTokenID("idl-char", CHAR_ID);
  /** IDL string constant e.g. "string" */
  public static final IDLTokenID STRING = new IDLTokenID("idl-string", STRING_ID);
  /** IDL integer constant e.g. 1234 */
  public static final IDLTokenID INT = new IDLTokenID("idl-int", INT_ID);
  /** IDL hex constant e.g. 0x5a */
  public static final IDLTokenID HEX = new IDLTokenID("idl-hex", HEX_ID);
  /** IDL octal constant e.g. 0123 */
  public static final IDLTokenID OCTAL = new IDLTokenID("idl-octal", OCTAL_ID);
  /** IDL long constant e.g. 12L */
  public static final IDLTokenID LONG = new IDLTokenID("idl-long", LONG_ID);
  /** IDL float constant e.g. 1.5e+43 */
  public static final IDLTokenID FLOAT = new IDLTokenID("idl-float", FLOAT_ID);
  /** IDL CPP derective e.g. #include <...> */
  public static final IDLTokenID DIRECTIVE = new IDLTokenID("idl-directive", DIRECTIVE_ID); 


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
  /*
    public int nextToken() {
    int tokenID = super.nextToken();
    System.out.println("tokenID=" + getTokenName(tokenID));
    return tokenID;
    }
  */  
  
  public boolean isIdentifierPart(char ch) {
    return Character.isJavaIdentifierPart(ch);
  }

  protected boolean matchKeywords () {
    return (IDLKeyword.get(buffer, tokenOffset, offset - tokenOffset) != null);
  }

  protected TokenID parseToken() {
    char actChar;

    while(offset < stopOffset) {
      actChar = buffer[offset];

      switch (state) {
      case INIT:
	switch (actChar) {
	case '\n':
	  offset++;
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
	  offset++;
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
	  offset = hlpInd;
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
	  if (offset == tokenOffset) { // only '\n'
	    offset++;
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
	  offset++;
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
	  return CHAR;
	case '\'':
	  offset++;
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
	  offset--;
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
	      hlpInd = offset; // end of identifier
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
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
	  return OPERATOR;
	default:
	  state = INIT;
	  return OPERATOR;
	}
	break;

      case ISA_GTGTGT:
	switch (actChar) {
	case '=':
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
	  return OPERATOR;
	default:
	  state = INIT;
	  return OPERATOR;
	}
	// break;

      case ISA_STAR:
	switch (actChar) {
	case '=':
	  offset++;
	  return OPERATOR;
	case '/':
	  offset++;
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
	  offset++;
	  state = INIT;
	  return BLOCK_COMMENT;
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
	  return OPERATOR;
	default:
	  state = INIT;
	  return OPERATOR;
	}
	// break;

      case ISA_PERCENT:
	switch (actChar) {
	case '=':
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
	  state = INIT;
	  return LONG;
	case 'f':
	case 'F':
	case 'd':
	case 'D':
	  offset++;
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
	  offset++;
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
	  offset++;
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
	  offset++;
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
      
      offset = ++offset;
    } // end of while(offset...)

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
	offset = hlpInd;
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
      if (offset - tokenOffset > 1) {
	offset--; // go to backslash char
	state = ISI_STRING;
	return STRING;
      } else {
	return EOT; // only one (backslash) char
      }
    case ISI_CHAR:
      return CHAR;
    case ISI_CHAR_A_BSLASH:
      if (offset - tokenOffset > 1) {
	offset--; // go to backslash char
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

  private boolean matchDirective () {
    if (offset - tokenOffset > 8)
      return false;
    if (offset - tokenOffset <= 0)
      return false;
    switch (buffer[tokenOffset + 0]) {
    case '#':
      if (offset - tokenOffset <= 1)
	return false;
      switch (buffer[tokenOffset + 1]) {
      case 'd':
	return offset - tokenOffset == 7
	  && buffer[tokenOffset + 2] == 'e'
	  && buffer[tokenOffset + 3] == 'f'
	  && buffer[tokenOffset + 4] == 'i'
	  && buffer[tokenOffset + 5] == 'n'
	  && buffer[tokenOffset + 6] == 'e';
      case 'e':
	return offset - tokenOffset == 6
	  && buffer[tokenOffset + 2] == 'n'
	  && buffer[tokenOffset + 3] == 'd'
	  && buffer[tokenOffset + 4] == 'i'
	  && buffer[tokenOffset + 5] == 'f';
      case 'i':
	if (offset - tokenOffset <= 2)
	  return false;
	switch (buffer[tokenOffset + 2]) {
	case 'f':
	  if (offset - tokenOffset <= 3)
	    return false;
	  switch (buffer[tokenOffset + 3]) {
	  case 'd':
	    return offset - tokenOffset == 6
	      && buffer[tokenOffset + 4] == 'e'
	      && buffer[tokenOffset + 5] == 'f';
	  case 'n':
	    return offset - tokenOffset == 7
	      && buffer[tokenOffset + 4] == 'd'
	      && buffer[tokenOffset + 5] == 'e'
	      && buffer[tokenOffset + 6] == 'f';
	  default:
	    return false;
	  }
	case 'n':
	  return offset - tokenOffset == 8
	    && buffer[tokenOffset + 3] == 'c'
	    && buffer[tokenOffset + 4] == 'l'
	    && buffer[tokenOffset + 5] == 'u'
	    && buffer[tokenOffset + 6] == 'd'
	    && buffer[tokenOffset + 7] == 'e';
	default:
	  return false;
	}
      case 'p':
	return offset - tokenOffset == 7
	  && buffer[tokenOffset + 2] == 'r'
	  && buffer[tokenOffset + 3] == 'a'
	  && buffer[tokenOffset + 4] == 'g'
	  && buffer[tokenOffset + 5] == 'm'
	  && buffer[tokenOffset + 6] == 'a';
      default:
	return false;
      }
    default:
      return false;
    }
  }

  public void relocate(char buffer[], int offset, int len,
  boolean lastBuffer, int stopPosition) {
    if (hlpInd >= 0) { // relocate hlpInd before calling super.relocScan()
      hlpInd += (offset - this.offset);
    }

    super.relocate(buffer, offset, len, lastBuffer, stopPosition);
  }

  /** Create scan state appropriate for particular scanner */
  public Syntax.StateInfo createStateInfo() {
    return new IDLStateInfo();
  }

  /** Store state of this scanner into given scan state. */
  public void storeState(Syntax.StateInfo stateInfo) {
    super.storeState(stateInfo);
    ((IDLStateInfo)stateInfo).hlpPreScan = (hlpInd >= 0) ? (offset - hlpInd) : -1;
  }

  /** Load state into scanner. Indexes are already initialized
   * when this function is called.
   */
  public void loadState(Syntax.StateInfo stateInfo) {
    super.loadState(stateInfo);
    int hi = ((IDLStateInfo)stateInfo).hlpPreScan;
    hlpInd = (hi >= 0) ? (offset - hi) : -1;
  }

  /** Initialize scanner in case the state stored in syntax mark
   * is null.
   */
  public void loadInitState() {
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

  public String toString() {
    String s = super.toString();
    s += ", hlpInd=" + hlpInd;
    return s;
  }
  
  class IDLStateInfo extends Syntax.BaseStateInfo {

    /** Helper prescan for method coloring */
    int hlpPreScan;

  }

}

/*
 * <<Log>>
 *  4    Jaga      1.2.1.0     3/15/00  Miloslav Metelka Structural change
 *  3    Gandalf   1.2         2/8/00   Karel Gardas    
 *  2    Gandalf   1.1         12/28/99 Miloslav Metelka Structural change and 
 *       some renamings
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */

