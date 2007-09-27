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

package org.netbeans.modules.regextester.editor;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

/*
 * Based on sqleditor
 *
 * @author Martin Adamek
 */
public class RegexSettingsInitializer extends Settings.AbstractInitializer {

    public static final String NAME = "regex-settings-initializer"; // NOI18N
    
    /**
     * Constructor
     */
    public RegexSettingsInitializer() {
        super(NAME);
    }

   /** 
    * Update map filled with the settings.
    * @param kitClass kit class for which the settings are being updated.
    *   It is always non-null value.
    * @param settingsMap map holding [setting-name, setting-value] pairs.
    *   The map can be empty if this is the first initializer
    *   that updates it or if no previous initializers updated it.
    */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
        if (kitClass == BaseKit.class) {
            new SQLTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);
        }

        if (kitClass == RegexEditorKit.class) {
            SettingsUtil.updateListSetting(
                    settingsMap, 
                    SettingsNames.TOKEN_CONTEXT_LIST,
                    new TokenContext[] { RegexTokenContext.context }
            );
        }
    }
    
    /**
     * Class for adding syntax coloring to the editor
     */
    static class SQLTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer {

        Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        Settings.Evaluator lightGraySubst = 
                new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

        /**
         * Constructor
         */
        public SQLTokenColoringInitializer() {
            super(RegexTokenContext.context);
        }

        /**
         * Get colors for the SQL tokens
         */
        public Object getTokenColoring(
                TokenContextPath tokenContextPath,
                TokenCategory tokenIDOrCategory, 
                boolean printingSet) {
            // get the ones for non printing selecting some sensible defaults
            if (!printingSet) {
                int id = tokenIDOrCategory.getNumericID();
                switch (id) {
                    case RegexTokenContext.WHITESPACE_ID:
                        return SettingsDefaults.emptyColoring;
                    case RegexTokenContext.LINE_COMMENT_ID:
                        return new Coloring(
                                null,
                                Color.gray, 
                                null);
                    case RegexTokenContext.BLOCK_COMMENT_ID:
                        return new Coloring(
                                null,
                                Color.gray, 
                                null);
                    case RegexTokenContext.STRING_ID:
                        return new Coloring(
                                null, 
                                Color.red.darker(), 
                                null);
                    case RegexTokenContext.INCOMPLETE_STRING_ID:
                        return new Coloring(
                                null, 
                                Color.white, 
                                Color.red);
                    case RegexTokenContext.IDENTIFIER_ID:
                        return new Coloring(
                                null, 
                                Color.red, 
                                null);
                    case RegexTokenContext.INVALID_CHARACTER_ID:
                        return new Coloring(
                                null, 
                                Color.white, 
                                Color.red);
                    case RegexTokenContext.OPERATOR_ID:
                        return new Coloring(
                                null, 
                                Color.blue, 
                                null);
                    case RegexTokenContext.INVALID_COMMENT_END_ID:
                        return new Coloring(
                                null, 
                                Color.white, 
                                Color.red);
                    case RegexTokenContext.INT_LITERAL_ID:
                    case RegexTokenContext.DOUBLE_LITERAL_ID:
                        return new Coloring(
                                null, 
                                Color.red.darker(), 
                                null);
                    case RegexTokenContext.KEYWORD_ID:
                        return new Coloring(
                                boldFont, 
                                Coloring.FONT_MODE_APPLY_STYLE, 
                                Color.blue.darker().darker(), 
                                null);
                }

            } else { 
                // get the set for printing (no color)
                switch (tokenIDOrCategory.getNumericID()) {
                    case RegexTokenContext.BLOCK_COMMENT_ID:
                    case RegexTokenContext.LINE_COMMENT_ID:
                        return lightGraySubst;

                    default:
                         return SettingsUtil.defaultPrintColoringEvaluator;
                }
            }
            
            return null;
        }
    }
}
