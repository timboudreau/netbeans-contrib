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

//package org.netbeans.modules.corba.idl.parser;
package org.netbeans.modules.corba.idl.src;

public interface IDLParserConstants {

  int EOF = 0;
  int ID = 84;
  int OCTALINT = 85;
  int DECIMALINT = 86;
  int HEXADECIMALINT = 87;
  int FLOATONE = 88;
  int FLOATTWO = 89;
  int CHARACTER = 90;
  int WCHARACTER = 91;
  int STRING = 92;
  int WSTRING = 93;
  int FIXED = 94;
  int VERSION = 95;
  int FILE = 96;

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
    "\"#\"",
    "\"pragma\"",
    "\"ID\"",
    "\"prefix\"",
    "\"version\"",
    "\";\"",
    "\"module\"",
    "\"{\"",
    "\"}\"",
    "\"abstract\"",
    "\"interface\"",
    "\":\"",
    "\",\"",
    "\"::\"",
    "\"valuetype\"",
    "\"custom\"",
    "\"truncatable\"",
    "\"supports\"",
    "\"public\"",
    "\"private\"",
    "\"factory\"",
    "\"(\"",
    "\")\"",
    "\"in\"",
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
    "\"TRUE\"",
    "\"FALSE\"",
    "\"typedef\"",
    "\"native\"",
    "\"float\"",
    "\"double\"",
    "\"long\"",
    "\"short\"",
    "\"unsigned\"",
    "\"char\"",
    "\"wchar\"",
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
    "\"wstring\"",
    "\"[\"",
    "\"]\"",
    "\"readonly\"",
    "\"attribute\"",
    "\"exception\"",
    "\"oneway\"",
    "\"void\"",
    "\"out\"",
    "\"inout\"",
    "\"raises\"",
    "\"context\"",
    "\"fixed\"",
    "\"ValueBase\"",
    "<ID>",
    "<OCTALINT>",
    "<DECIMALINT>",
    "<HEXADECIMALINT>",
    "<FLOATONE>",
    "<FLOATTWO>",
    "<CHARACTER>",
    "<WCHARACTER>",
    "<STRING>",
    "<WSTRING>",
    "<FIXED>",
    "<VERSION>",
    "<FILE>",
  };

}
