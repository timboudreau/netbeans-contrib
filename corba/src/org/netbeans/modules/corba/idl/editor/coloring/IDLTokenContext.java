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

package org.netbeans.modules.corba.idl.editor.coloring;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.BaseTokenCategory;
import org.netbeans.editor.BaseImageTokenID;

/**
* Syntax analyzes for IDL source files.
* Tokens and internal states are given below. 
*
* @author Miloslav Metelka
* @version 1.00
*/

public class IDLTokenContext extends TokenContext {

    // Token category-ids
    public static final int KEYWORDS_ID = 1;

    // Token IDs
    public static final int TEXT_ID = KEYWORDS_ID + 1; // plain text
    public static final int ERROR_ID = TEXT_ID + 1; // errorneous text
    public static final int IDENTIFIER_ID = ERROR_ID + 1; // identifier
    public static final int OPERATOR_ID = IDENTIFIER_ID + 1; // operators like '+', '*=' etc.
    public static final int LINE_COMMENT_ID = OPERATOR_ID + 1; // comment till end of line
    public static final int BLOCK_COMMENT_ID = LINE_COMMENT_ID + 1; // block comment
    public static final int CHAR_LITERAL_ID = BLOCK_COMMENT_ID + 1; // char constant e.g. 'c'
    public static final int STRING_LITERAL_ID = CHAR_LITERAL_ID + 1; // string constant e.g. "string"
    public static final int INT_LITERAL_ID = STRING_LITERAL_ID + 1; // integer constant e.g. 1234
    public static final int HEX_LITERAL_ID = INT_LITERAL_ID + 1; // hex constant e.g. 0x5a
    public static final int OCTAL_LITERAL_ID = HEX_LITERAL_ID + 1; // octal constant e.g. 0123
    public static final int LONG_LITERAL_ID = OCTAL_LITERAL_ID + 1; // long constant e.g. 12L
    public static final int FLOAT_LITERAL_ID = LONG_LITERAL_ID + 1; // float constant e.g. 1.5e+43
    public static final int DIRECTIVE_ID = FLOAT_LITERAL_ID + 1;  // CPP derective e.g. #include <...>
    public static final int EOL_ID = DIRECTIVE_ID + 1;  // EOL

    // Keyword's numeric IDs
    public static final int FALSE_ID = EOL_ID + 1;
    public static final int OBJECT_ID = FALSE_ID + 1;
    public static final int TRUE_ID = OBJECT_ID + 1;
    public static final int VALUEBASE_ID = TRUE_ID + 1;
    public static final int ABSTRACT_ID = VALUEBASE_ID + 1;
    public static final int ANY_ID = ABSTRACT_ID + 1;
    public static final int ATTRIBUTE_ID = ANY_ID + 1;
    public static final int BOOLEAN_ID = ATTRIBUTE_ID + 1;
    public static final int CASE_ID = BOOLEAN_ID + 1;
    public static final int CHAR_ID = CASE_ID + 1;
    public static final int CONST_ID = CHAR_ID + 1;
    public static final int CONTEXT_ID = CONST_ID + 1;
    public static final int CUSTOM_ID = CONTEXT_ID + 1;
    public static final int DEFAULT_ID = CUSTOM_ID + 1;
    public static final int DOUBLE_ID = DEFAULT_ID + 1;
    public static final int ENUM_ID = DOUBLE_ID + 1;
    public static final int EXCEPTION_ID = ENUM_ID + 1;
    public static final int FACTORY_ID = EXCEPTION_ID + 1;
    public static final int FIXED_ID = FACTORY_ID + 1;
    public static final int FLOAT_ID = FIXED_ID + 1;
    public static final int IN_ID = FLOAT_ID + 1;
    public static final int INOUT_ID = IN_ID + 1;
    public static final int INTERFACE_ID = INOUT_ID + 1;
    public static final int LONG_ID = INTERFACE_ID + 1;
    public static final int MODULE_ID = LONG_ID + 1;
    public static final int NATIVE_ID = MODULE_ID + 1;
    public static final int OCTET_ID = NATIVE_ID + 1;
    public static final int ONEWAY_ID = OCTET_ID + 1;
    public static final int OUT_ID = ONEWAY_ID + 1;
    public static final int PRIVATE_ID = OUT_ID + 1;
    public static final int PUBLIC_ID = PRIVATE_ID + 1;
    public static final int RAISES_ID = PUBLIC_ID + 1;
    public static final int READONLY_ID = RAISES_ID + 1;
    public static final int SEQUENCE_ID = READONLY_ID + 1;
    public static final int SHORT_ID = SEQUENCE_ID + 1;
    public static final int STRING_ID = SHORT_ID + 1;
    public static final int STRUCT_ID = STRING_ID + 1;
    public static final int SUPPORTS_ID = STRUCT_ID + 1;
    public static final int SWITCH_ID = SUPPORTS_ID + 1;
    public static final int TRUNCATABLE_ID = SWITCH_ID + 1;
    public static final int TYPEDEF_ID = TRUNCATABLE_ID + 1;
    public static final int UNION_ID = TYPEDEF_ID + 1;
    public static final int UNSIGNED_ID = UNION_ID + 1;
    public static final int VALUETYPE_ID = UNSIGNED_ID + 1;
    public static final int VOID_ID = VALUETYPE_ID + 1;
    public static final int WCHAR_ID = VOID_ID + 1;
    public static final int WSTRING_ID = WCHAR_ID + 1;

    // Token-categories
    public static final BaseTokenCategory KEYWORDS
    = new BaseTokenCategory("keywords", KEYWORDS_ID);

    // TokenIDs
    /** Plain text */
    public static final BaseTokenID TEXT = new BaseTokenID("text", TEXT_ID);
    /** Errorneous text */
    public static final BaseTokenID ERROR = new BaseTokenID("error", ERROR_ID);
    /** IDL identifier */
    public static final BaseTokenID IDENTIFIER = new BaseTokenID("identifier", IDENTIFIER_ID);
    /** IDL operators like '+', '*=' etc. */
    public static final BaseTokenID OPERATOR = new BaseTokenID("operator", OPERATOR_ID);
    /** IDL comment till end of line */
    public static final BaseTokenID LINE_COMMENT = new BaseTokenID("line-comment", LINE_COMMENT_ID);
    /** IDL block comment */
    public static final BaseTokenID BLOCK_COMMENT = new BaseTokenID("block-comment", BLOCK_COMMENT_ID);
    /** IDL char constant e.g. 'c' */
    public static final BaseTokenID CHAR_LITERAL = new BaseTokenID("char-literal", CHAR_LITERAL_ID);
    /** IDL string constant e.g. "string" */
    public static final BaseTokenID STRING_LITERAL = new BaseTokenID("string-literal", STRING_LITERAL_ID);
    /** IDL integer constant e.g. 1234 */
    public static final BaseTokenID INT_LITERAL = new BaseTokenID("int-literal", INT_LITERAL_ID);
    /** IDL hex constant e.g. 0x5a */
    public static final BaseTokenID HEX_LITERAL = new BaseTokenID("hex-literal", HEX_LITERAL_ID);
    /** IDL octal constant e.g. 0123 */
    public static final BaseTokenID OCTAL_LITERAL = new BaseTokenID("octal-literal", OCTAL_LITERAL_ID);
    /** IDL long constant e.g. 12L */
    public static final BaseTokenID LONG_LITERAL = new BaseTokenID("long-literal", LONG_LITERAL_ID);
    /** IDL float constant e.g. 1.5e+43 */
    public static final BaseTokenID FLOAT_LITERAL = new BaseTokenID("float-literal", FLOAT_LITERAL_ID);
    /** IDL CPP derective e.g. #include <...> */
    public static final BaseTokenID DIRECTIVE = new BaseTokenID("directive", DIRECTIVE_ID);
    /** End of line */
    public static final BaseTokenID EOL = new BaseTokenID("EOL", EOL_ID);

    // Keywords
    public static final BaseImageTokenID FALSE
        = new BaseImageTokenID("FALSE", FALSE_ID, KEYWORDS);

    public static final BaseImageTokenID OBJECT
        = new BaseImageTokenID("object", OBJECT_ID, KEYWORDS);

    public static final BaseImageTokenID TRUE
        = new BaseImageTokenID("TRUE", TRUE_ID, KEYWORDS);

    public static final BaseImageTokenID VALUEBASE
        = new BaseImageTokenID("valuebase", VALUEBASE_ID, KEYWORDS);

    public static final BaseImageTokenID ABSTRACT
        = new BaseImageTokenID("abstract", ABSTRACT_ID, KEYWORDS);

    public static final BaseImageTokenID ANY
        = new BaseImageTokenID("any", ANY_ID, KEYWORDS);

    public static final BaseImageTokenID ATTRIBUTE
        = new BaseImageTokenID("attribute", ATTRIBUTE_ID, KEYWORDS);

    public static final BaseImageTokenID BOOLEAN
        = new BaseImageTokenID("boolean", BOOLEAN_ID, KEYWORDS);

    public static final BaseImageTokenID CASE
        = new BaseImageTokenID("case", CASE_ID, KEYWORDS);

    public static final BaseImageTokenID CHAR
        = new BaseImageTokenID("char", CHAR_ID, KEYWORDS);

    public static final BaseImageTokenID CONST
        = new BaseImageTokenID("const", CONST_ID, KEYWORDS);

    public static final BaseImageTokenID CONTEXT
        = new BaseImageTokenID("context", CONTEXT_ID, KEYWORDS);

    public static final BaseImageTokenID CUSTOM
        = new BaseImageTokenID("custom", CUSTOM_ID, KEYWORDS);

    public static final BaseImageTokenID DEFAULT
        = new BaseImageTokenID("default", DEFAULT_ID, KEYWORDS);

    public static final BaseImageTokenID DOUBLE
        = new BaseImageTokenID("double", DOUBLE_ID, KEYWORDS);

    public static final BaseImageTokenID ENUM
        = new BaseImageTokenID("enum", ENUM_ID, KEYWORDS);

    public static final BaseImageTokenID EXCEPTION
        = new BaseImageTokenID("exception", EXCEPTION_ID, KEYWORDS);

    public static final BaseImageTokenID FACTORY
        = new BaseImageTokenID("factory", FACTORY_ID, KEYWORDS);

    public static final BaseImageTokenID FIXED
        = new BaseImageTokenID("fixed", FIXED_ID, KEYWORDS);

    public static final BaseImageTokenID FLOAT
        = new BaseImageTokenID("float", FLOAT_ID, KEYWORDS);

    public static final BaseImageTokenID IN
        = new BaseImageTokenID("in", IN_ID, KEYWORDS);

    public static final BaseImageTokenID INOUT
        = new BaseImageTokenID("inout", INOUT_ID, KEYWORDS);

    public static final BaseImageTokenID INTERFACE
        = new BaseImageTokenID("interface", INTERFACE_ID, KEYWORDS);

    public static final BaseImageTokenID LONG
        = new BaseImageTokenID("long", LONG_ID, KEYWORDS);

    public static final BaseImageTokenID MODULE
        = new BaseImageTokenID("module", MODULE_ID, KEYWORDS);

    public static final BaseImageTokenID NATIVE
        = new BaseImageTokenID("native", NATIVE_ID, KEYWORDS);

    public static final BaseImageTokenID OCTET
        = new BaseImageTokenID("octet", OCTET_ID, KEYWORDS);

    public static final BaseImageTokenID ONEWAY
        = new BaseImageTokenID("oneway", ONEWAY_ID, KEYWORDS);

    public static final BaseImageTokenID OUT
        = new BaseImageTokenID("out", OUT_ID, KEYWORDS);

    public static final BaseImageTokenID PRIVATE
        = new BaseImageTokenID("private", PRIVATE_ID, KEYWORDS);

    public static final BaseImageTokenID PUBLIC
        = new BaseImageTokenID("public", PUBLIC_ID, KEYWORDS);

    public static final BaseImageTokenID RAISES
        = new BaseImageTokenID("raises", RAISES_ID, KEYWORDS);

    public static final BaseImageTokenID READONLY
        = new BaseImageTokenID("readonly", READONLY_ID, KEYWORDS);

    public static final BaseImageTokenID SEQUENCE
        = new BaseImageTokenID("sequence", SEQUENCE_ID, KEYWORDS);

    public static final BaseImageTokenID SHORT
        = new BaseImageTokenID("short", SHORT_ID, KEYWORDS);

    public static final BaseImageTokenID STRING
        = new BaseImageTokenID("string", STRING_ID, KEYWORDS);

    public static final BaseImageTokenID STRUCT
        = new BaseImageTokenID("struct", STRUCT_ID, KEYWORDS);

    public static final BaseImageTokenID SUPPORTS
        = new BaseImageTokenID("supports", SUPPORTS_ID, KEYWORDS);

    public static final BaseImageTokenID SWITCH
        = new BaseImageTokenID("switch", SWITCH_ID, KEYWORDS);

    public static final BaseImageTokenID TRUNCATABLE
        = new BaseImageTokenID("truncatable", TRUNCATABLE_ID, KEYWORDS);

    public static final BaseImageTokenID TYPEDEF
        = new BaseImageTokenID("typedef", TYPEDEF_ID, KEYWORDS);

    public static final BaseImageTokenID UNION
        = new BaseImageTokenID("union", UNION_ID, KEYWORDS);

    public static final BaseImageTokenID UNSIGNED
        = new BaseImageTokenID("unsigned", UNSIGNED_ID, KEYWORDS);

    public static final BaseImageTokenID VALUETYPE
        = new BaseImageTokenID("valuetype", VALUETYPE_ID, KEYWORDS);

    public static final BaseImageTokenID VOID
        = new BaseImageTokenID("void", VOID_ID, KEYWORDS);

    public static final BaseImageTokenID WCHAR
        = new BaseImageTokenID("wchar", WCHAR_ID, KEYWORDS);

    public static final BaseImageTokenID WSTRING
        = new BaseImageTokenID("wstring", WSTRING_ID, KEYWORDS);


    // Context instance declaration
    public static final IDLTokenContext context = new IDLTokenContext();

    public static final TokenContextPath contextPath = context.getContextPath();


    private IDLTokenContext() {
        super("idl-");

        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                e.printStackTrace();
            }
        }

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

