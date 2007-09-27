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
