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

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.netbeans.modules.editor.options.BaseOptionsBeanInfo;
import org.openide.util.NbBundle;

/** BeanInfo for plain options
*
* @author Miloslav Metelka, Ales Novak
*/
public class TexOptionsBeanInfo extends BaseOptionsBeanInfo {

    private ResourceBundle bundle = NbBundle.getBundle(TexOptions.class);
    
    public TexOptionsBeanInfo() {
        super();//"/org/netbeans/modules/editor/resources/plainOptions"); // NOI18N
    }

    public TexOptionsBeanInfo(String iconPrefix) {
        super(iconPrefix);
    }

    protected Class getBeanClass() {
        return TexOptions.class;
    }

    private String[] getLocalPropNames() {
        return new String[] {
            TexOptions.PROP_FULL_SYNTACTIC_COLORING,
            TexOptions.PROP_LOCAL_CONNECTS_ONLY,
            TexOptions.PROP_REMOTE_HOST,
        };
    }
    
    static final String[] BASE_PROP_NAMES = {
/*        BaseOptions.ABBREV_MAP_PROP,
        BaseOptions.CARET_BLINK_RATE_PROP,
        BaseOptions.CARET_COLOR_INSERT_MODE_PROP,
        BaseOptions.CARET_COLOR_OVERWRITE_MODE_PROP,
        BaseOptions.CARET_ITALIC_INSERT_MODE_PROP,
        BaseOptions.CARET_ITALIC_OVERWRITE_MODE_PROP,
        BaseOptions.CARET_TYPE_INSERT_MODE_PROP,
        BaseOptions.CARET_TYPE_OVERWRITE_MODE_PROP,
        BaseOptions.COLORING_MAP_PROP,
        BaseOptions.EXPAND_TABS_PROP,
        BaseOptions.FONT_SIZE_PROP,
        BaseOptions.HIGHLIGHT_CARET_ROW_PROP,
        BaseOptions.HIGHLIGHT_MATCHING_BRACKET_PROP,
        BaseOptions.INDENT_ENGINE_PROP,
        BaseOptions.KEY_BINDING_LIST_PROP,
        BaseOptions.LINE_HEIGHT_CORRECTION_PROP,
        BaseOptions.LINE_NUMBER_MARGIN_PROP_2,
        BaseOptions.LINE_NUMBER_VISIBLE_PROP,
        BaseOptions.MACRO_MAP_PROP,
        BaseOptions.MARGIN_PROP,
        BaseOptions.SCROLL_FIND_INSETS_PROP,
        BaseOptions.SCROLL_JUMP_INSETS_PROP,
        BaseOptions.SPACES_PER_TAB_PROP,
        BaseOptions.STATUS_BAR_CARET_DELAY_PROP,
        BaseOptions.STATUS_BAR_VISIBLE_PROP,
        BaseOptions.TAB_SIZE_PROP,
        BaseOptions.TEXT_LIMIT_LINE_COLOR_PROP,
        BaseOptions.TEXT_LIMIT_LINE_VISIBLE_PROP,
        BaseOptions.TEXT_LIMIT_WIDTH_PROP,*/
//        BaseOptions.OPTIONS_VERSION_PROP
    };

//    protected String[] getPropNames() {
//        return BASE_PROP_NAMES;
//    }
    
    protected String[] getPropNames() {
        String[] s = super.getPropNames();
        String[] l = getLocalPropNames();
        String[] result = new String[s.length + l.length];
        
        System.arraycopy(s, 0, result, 0, s.length);
        System.arraycopy(l, 0, result, s.length, l.length);
        
        return result;
    }

    protected String getString(String s) {
        try {
            return super.getString(s);
        } catch (MissingResourceException e) {
            return bundle.getString(s);
        }
    }

}
