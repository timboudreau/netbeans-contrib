/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
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

            //Coloring:
            settingsMap.put(TexColoringNames.COMMAND_GENERAL   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, null));
            settingsMap.put(TexColoringNames.COMMENT           + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.gray, null));
            settingsMap.put(TexColoringNames.UNKNOWN_CHARACTER + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.black, null));
            settingsMap.put(TexColoringNames.WHITESPACE        + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.white, null));
            settingsMap.put(TexColoringNames.WORD              + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Color.black, null));
            settingsMap.put(TexColoringNames.PARAGRAPH_END     + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, null));
            settingsMap.put(TexColoringNames.MATH              + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, null, new Color(200, 218, 255)));
            settingsMap.put(TexColoringNames.COMMAND_INCORRECT + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(204, 0, 0), null, null, null, Color.RED));
            settingsMap.put(TexColoringNames.COMMAND_CORRECT   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(204, 0, 0), null));
            settingsMap.put(TexColoringNames.DEFINITION        + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(PLAIN, Coloring.FONT_MODE_APPLY_STYLE, new Color(128, 128, 0), null));
            settingsMap.put(TexColoringNames.ENUM_ARG_INCORRECT + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(0, 0, 245), null, null, null, Color.RED));
            settingsMap.put(TexColoringNames.ENUM_ARG_CORRECT   + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, new Color(0, 0, 245), null));
            settingsMap.put(TexColoringNames.ARG_INCORRECT      + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.RED));
            settingsMap.put(TexColoringNames.WORD_BAD           + SettingsNames.COLORING_NAME_SUFFIX, new Coloring());
            settingsMap.put(TexColoringNames.WORD_INCORRECT     + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.RED));
            settingsMap.put(TexColoringNames.WORD_INCOMPLETE    + SettingsNames.COLORING_NAME_SUFFIX, new Coloring(null, Coloring.FONT_MODE_DEFAULT, null, null, null, null, Color.GRAY));
 
            settingsMap.put(SettingsNames.COLORING_NAME_LIST, Arrays.asList(new String[] {
                TexColoringNames.COMMAND_GENERAL   ,
                TexColoringNames.COMMENT           ,
                TexColoringNames.UNKNOWN_CHARACTER ,
                TexColoringNames.WHITESPACE        ,
                TexColoringNames.WORD              ,
                TexColoringNames.PARAGRAPH_END     ,
                TexColoringNames.MATH              ,
                TexColoringNames.COMMAND_INCORRECT ,
                TexColoringNames.COMMAND_CORRECT   ,
                TexColoringNames.DEFINITION        ,
                TexColoringNames.ENUM_ARG_INCORRECT ,
                TexColoringNames.ENUM_ARG_CORRECT   ,
                TexColoringNames.WORD_BAD           ,
                TexColoringNames.WORD_INCORRECT     ,
                TexColoringNames.WORD_INCOMPLETE    ,
            }));
            SettingsUtil.updateListSetting(settingsMap, SettingsNames.KEY_BINDING_LIST, new MultiKeyBinding[] {
                new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), ActionsFactory.WORD_COUNT_ACTION),
                new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.ALT_DOWN_MASK), ActionsFactory.GO_TO_DECLARATION_ACTION),
                new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), ExtKit.commentAction),
                new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), ExtKit.uncommentAction),
            });
        }

    }

    private static final Font ITALIC = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
    private static final Font PLAIN  = SettingsDefaults.defaultFont;

}
