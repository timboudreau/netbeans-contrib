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
    int ID = 80;
    int OCTALINT = 81;
    int DECIMALINT = 82;
    int HEXADECIMALINT = 83;
    int FLOATONE = 84;
    int FLOATTWO = 85;
    int CHARACTER = 86;
    int WCHARACTER = 87;
    int STRING = 88;
    int WSTRING = 89;
    int FIXED = 90;

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
    };

}
