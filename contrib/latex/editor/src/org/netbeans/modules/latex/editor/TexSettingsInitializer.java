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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.latex.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Map;
import javax.swing.KeyStroke;
import org.netbeans.editor.Acceptor;

import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.plain.PlainTokenContext;

/**
* Settings for plain kit
*
* @author Miloslav Metelka
* @version 1.00
*/

public class TexSettingsInitializer extends Settings.AbstractInitializer {

    public static final String NAME = "nb-plain-settings-initializer";

    public TexSettingsInitializer() {
        super(NAME);
    }

    /** Update map filled with the settings.
    * @param kitClass kit class for which the settings are being updated.
    *   It is always non-null value.
    * @param settingsMap map holding [setting-name, setting-value] pairs.
    *   The map can be empty if this is the first initializer
    *   that updates it or if no previous initializers updated it.
    */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
        if (kitClass == TexKit.class) {
            settingsMap.put(SettingsNames.CODE_FOLDING_ENABLE, Boolean.TRUE);

//            //Coloring:
//            settingsMap.put(TexColoringNames.COMMAND_GENERAL   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, null));
//            settingsMap.put(TexColoringNames.COMMENT           + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.gray, null));
//            settingsMap.put(TexColoringNames.UNKNOWN_CHARACTER + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.black, null));
//            settingsMap.put(TexColoringNames.WHITESPACE        + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.white, null));
//            settingsMap.put(TexColoringNames.WORD              + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.black, null));
//            settingsMap.put(TexColoringNames.PARAGRAPH_END     + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, null));
//            settingsMap.put(TexColoringNames.MATH              + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, new Color(200, 218, 255)));
//            settingsMap.put(TexColoringNames.COMMAND_INCORRECT + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(204, 0, 0), null, null, null, Color.RED));
//            settingsMap.put(TexColoringNames.COMMAND_CORRECT   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(204, 0, 0), null));
//            settingsMap.put(TexColoringNames.DEFINITION        + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(PLAIN, Coloring.FONT_MODE_APPLY_STYLE, new Color(128, 128, 0), null));
//            settingsMap.put(TexColoringNames.ENUM_ARG_INCORRECT + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(0, 0, 245), null, null, null, Color.RED));
//            settingsMap.put(TexColoringNames.ENUM_ARG_CORRECT   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(0, 0, 245), null));
//            settingsMap.put(TexColoringNames.ARG_INCORRECT      + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.RED));
//            settingsMap.put(TexColoringNames.WORD_BAD           + SettingsNames.COLORING_NAME_SUFFIX, new Coloring());
//            settingsMap.put(TexColoringNames.WORD_INCORRECT     + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.RED));
//            settingsMap.put(TexColoringNames.WORD_INCOMPLETE    + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.GRAY));
// 
//            settingsMap.put(SettingsNames.COLORING_NAME_LIST, Arrays.asList(new String[] {
//                TexColoringNames.COMMAND_GENERAL   ,
//                TexColoringNames.COMMENT           ,
//                TexColoringNames.UNKNOWN_CHARACTER ,
//                TexColoringNames.WHITESPACE        ,
//                TexColoringNames.WORD              ,
//                TexColoringNames.PARAGRAPH_END     ,
//                TexColoringNames.MATH              ,
//                TexColoringNames.COMMAND_INCORRECT ,
//                TexColoringNames.COMMAND_CORRECT   ,
//                TexColoringNames.DEFINITION        ,
//                TexColoringNames.ENUM_ARG_INCORRECT ,
//                TexColoringNames.ENUM_ARG_CORRECT   ,
//                TexColoringNames.WORD_BAD           ,
//                TexColoringNames.WORD_INCORRECT     ,
//                TexColoringNames.WORD_INCOMPLETE    ,
//            }));
        }

    }

    private static final Font ITALIC = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
    private static final Font PLAIN  = SettingsDefaults.defaultFont;

}
