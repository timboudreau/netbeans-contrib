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

package org.netbeans.modules.corba.idl.editor.settings;

import java.util.Map;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Font;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.modules.corba.idl.editor.coloring.IDLKit;
import org.netbeans.modules.corba.idl.editor.coloring.IDLSyntax;
import org.netbeans.modules.corba.idl.editor.coloring.IDLTokenContext;
import org.netbeans.modules.corba.idl.editor.indent.IDLIndentAcceptor;

public class IDLEditorSettingsInitializer extends Settings.AbstractInitializer {

    /** Name assigned to initializer */
    public static final String NAME = "idl-editor-settings-initializer";

    public IDLEditorSettingsInitializer() {
        super(NAME);
    }

    public void updateSettingsMap (Class kitClass, Map settingsMap) {

        // IDL Colorings
        if (kitClass == BaseKit.class) {

            new IDLTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);
        }


        if (kitClass == IDLKit.class) {

            settingsMap.put (SettingsNames.ABBREV_MAP, getIDLAbbrevMap());
            settingsMap.put (ExtSettingsNames.INDENT_HOT_CHARS_ACCEPTOR, new IDLIndentAcceptor());
            SettingsUtil.updateListSetting(settingsMap, SettingsNames.TOKEN_CONTEXT_LIST,
                   new TokenContext[] { IDLTokenContext.context });

        }
    }

    Map getIDLAbbrevMap() {
        Map idlAbbrevMap = new HashMap();
        idlAbbrevMap.put("#d", "#define ");
        idlAbbrevMap.put("#i", "#include ");
        idlAbbrevMap.put("#if", "#ifdef ");
        idlAbbrevMap.put("#ifn", "#ifndef ");
        idlAbbrevMap.put("#e", "#endif");
        idlAbbrevMap.put("#p", "#pragma ");

        idlAbbrevMap.put("at", "attribute ");
        idlAbbrevMap.put("bo", "boolean ");
        idlAbbrevMap.put("ca", "case ");
        idlAbbrevMap.put("co", "const ");
        idlAbbrevMap.put("de", "default");
        idlAbbrevMap.put("do", "double ");
        idlAbbrevMap.put("en", "enum ");
        idlAbbrevMap.put("ex", "exception ");
        idlAbbrevMap.put("FA", "FALSE");
        idlAbbrevMap.put("fa", "FALSE");
        idlAbbrevMap.put("fi", "fixed");
        idlAbbrevMap.put("fl", "float ");
        idlAbbrevMap.put("int", "interface ");
        idlAbbrevMap.put("lo", "long ");
        idlAbbrevMap.put("mo", "module ");
        idlAbbrevMap.put("Ob", "Object");
        idlAbbrevMap.put("ob", "Object");
        idlAbbrevMap.put("oc", "octet ");
        idlAbbrevMap.put("on", "oneway ");
        idlAbbrevMap.put("ra", "raises (");
        idlAbbrevMap.put("re", "readonly ");
        idlAbbrevMap.put("se", "sequence ");
        idlAbbrevMap.put("sh", "short ");
        idlAbbrevMap.put("stu", "struct ");
        idlAbbrevMap.put("str", "string ");
        idlAbbrevMap.put("sw", "switch ");
        idlAbbrevMap.put("TR", "TRUE");
        idlAbbrevMap.put("tr", "TRUE");
        idlAbbrevMap.put("ty", "typedef ");
        idlAbbrevMap.put("uns", "unsigned ");
        idlAbbrevMap.put("uni", "union ");
        idlAbbrevMap.put("wc", "wchar ");
        idlAbbrevMap.put("ws", "wstring ");

        return idlAbbrevMap;
    }

    static class IDLTokenColoringInitializer
    extends SettingsUtil.TokenColoringInitializer {

        Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
        Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
        Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
        Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

        Coloring emptyColoring = new Coloring(null, null, null);

        Coloring numberColoring = new Coloring(null, Color.red, null);

        public IDLTokenColoringInitializer() {
            super(IDLTokenContext.context);
        }

        public Object getTokenColoring(TokenContextPath tokenContextPath,
        TokenCategory tokenIDOrCategory, boolean printingSet) {
            if (!printingSet) {
                switch (tokenIDOrCategory.getNumericID()) {
                    case IDLTokenContext.TEXT_ID:
                        return emptyColoring;

                    case IDLTokenContext.ERROR_ID:
                        return new Coloring(null, Color.white, Color.red);

                    case IDLTokenContext.KEYWORDS_ID:
                        return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                                                  Color.blue, null);

                    case IDLTokenContext.IDENTIFIER_ID:
                        return new Coloring(null, null, null);

                    case IDLTokenContext.OPERATOR_ID:
                        return new Coloring(null, null, null);

                    case IDLTokenContext.LINE_COMMENT_ID:
                        return new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE,
                                                  Color.gray, null);

                    case IDLTokenContext.BLOCK_COMMENT_ID:
                        return new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE,
                                                  Color.gray, null);

                    case IDLTokenContext.CHAR_LITERAL_ID:
                        return new Coloring(null, Color.green.darker(), null);

                    case IDLTokenContext.STRING_LITERAL_ID:
                        return new Coloring(null, Color.magenta, null);

                    case IDLTokenContext.INT_LITERAL_ID:
                    case IDLTokenContext.HEX_LITERAL_ID:
                    case IDLTokenContext.OCTAL_LITERAL_ID:
                    case IDLTokenContext.LONG_LITERAL_ID:
                    case IDLTokenContext.FLOAT_LITERAL_ID:
                        return numberColoring;

                    case IDLTokenContext.DIRECTIVE_ID:
                        return new Coloring(null, Color.green.darker().darker(), null);

                }

            } else { // printing set
                switch (tokenIDOrCategory.getNumericID()) {
                    case IDLTokenContext.BLOCK_COMMENT_ID:
                    case IDLTokenContext.LINE_COMMENT_ID:
                        return lightGraySubst;

                    default:
                         return SettingsUtil.defaultPrintColoringEvaluator;
                }

            }

            return null;

        }

    }

}

/*
 * <<Log>>
 *  4    Jaga      1.3         4/13/00  Miloslav Metelka token colorings in 
 *       base-kit settings
 *  3    Jaga      1.2         3/24/00  Miloslav Metelka renaming
 *  2    Jaga      1.1         3/22/00  Miloslav Metelka fix
 *  1    Jaga      1.0         3/15/00  Miloslav Metelka 
 * $
 */
