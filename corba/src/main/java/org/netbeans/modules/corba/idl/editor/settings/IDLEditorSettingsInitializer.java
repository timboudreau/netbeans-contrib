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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
                        // #48502 - changed comment coloring to have non-italic font style
                        return new Coloring(null, Color.gray, null);

                    case IDLTokenContext.BLOCK_COMMENT_ID:
                        // #48502 - changed comment coloring to have non-italic font style
                        return new Coloring(null, Color.gray, null);

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
