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

//package com.netbeans.enterprise.modules.corba.idl.parser;
package com.netbeans.enterprise.modules.corba.idl.src;

public interface IDLParserConstants {

  int EOF = 0;
  int ID = 66;
  int OCTALINT = 67;
  int DECIMALINT = 68;
  int HEXADECIMALINT = 69;
  int FLOATONE = 70;
  int FLOATTWO = 71;
  int CHARACTER = 72;
  int STRING = 73;

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
