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

/*
 * NAME_SUBSTITUTION.java -- synopsis.
 *
 *
 * Date: 15.6.1998 12:22:29$
 * <<Revision>>
 *
 * SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 *
 * Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */
//package com.netbeans.enterprise.modules.corba.idl.parser;
package com.netbeans.enterprise.modules.corba.idl.src;

public interface IDLParserConstants {

  int EOF = 0;
  int ID = 67;
  int OCTALINT = 68;
  int DECIMALINT = 69;
  int HEXADECIMALINT = 70;
  int FLOATONE = 71;
  int FLOATTWO = 72;
  int CHARACTER = 73;
  int STRING = 74;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<token of kind 5>",
    "<token of kind 6>",
    "<token of kind 7>",
    "<token of kind 8>",
    "\";\"",
    "\"module\"",
    "\"{\"",
    "\"}\"",
    "\"interface\"",
    "\":\"",
    "\",\"",
    "\"::\"",
    "\"const\"",
    "\"=\"",
    "\"|\"",
    "\"^\"",
    "\"&\"",
    "\">>\"",
    "\"<<\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "\"~\"",
    "\"(\"",
    "\")\"",
    "\"TRUE\"",
    "\"FALSE\"",
    "\"typedef\"",
    "\"float\"",
    "\"double\"",
    "\"long\"",
    "\"short\"",
    "\"unsigned\"",
    "\"char\"",
    "\"boolean\"",
    "\"octet\"",
    "\"any\"",
    "\"Object\"",
    "\"struct\"",
    "\"union\"",
    "\"switch\"",
    "\"case\"",
    "\"default\"",
    "\"enum\"",
    "\"sequence\"",
    "\"<\"",
    "\">\"",
    "\"string\"",
    "\"[\"",
    "\"]\"",
    "\"readonly\"",
    "\"attribute\"",
    "\"exception\"",
    "\"oneway\"",
    "\"void\"",
    "\"in\"",
    "\"out\"",
    "\"inout\"",
    "\"raises\"",
    "\"context\"",
    "<ID>",
    "<OCTALINT>",
    "<DECIMALINT>",
    "<HEXADECIMALINT>",
    "<FLOATONE>",
    "<FLOATTWO>",
    "<CHARACTER>",
    "<STRING>",
  };

}

/*
 * <<Log>>
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */
