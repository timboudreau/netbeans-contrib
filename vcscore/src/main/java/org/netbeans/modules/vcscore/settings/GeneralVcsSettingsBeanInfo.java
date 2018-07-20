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

package org.netbeans.modules.vcscore.settings;

import java.beans.*;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class GeneralVcsSettingsBeanInfo extends SimpleBeanInfo {

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = null;
        try {
            properties = new PropertyDescriptor[] {
                new PropertyDescriptor(GeneralVcsSettings.PROP_USE_GLOBAL, GeneralVcsSettings.class),  // [0]
                new PropertyDescriptor(GeneralVcsSettings.PROP_OFFLINE, GeneralVcsSettings.class),    // [1]
                new PropertyDescriptor(GeneralVcsSettings.PROP_AUTO_REFRESH, GeneralVcsSettings.class), // [2]
                new PropertyDescriptor(GeneralVcsSettings.PROP_HOME, GeneralVcsSettings.class, "getHome", null), // [3]
                new PropertyDescriptor(GeneralVcsSettings.PROP_DEFAULT_PROFILE, GeneralVcsSettings.class),// [4]
                new PropertyDescriptor(GeneralVcsSettings.PROP_RECOGNIZED_FS, GeneralVcsSettings.class), // [5]
                new PropertyDescriptor(GeneralVcsSettings.PROP_ADVANCED_NOTIFICATION, GeneralVcsSettings.class), // [6])
                new PropertyDescriptor(GeneralVcsSettings.PROP_FILE_ANNOTATION, GeneralVcsSettings.class), // [6])
            };
            properties[0].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_useGlobal"));
            properties[0].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_useGlobal"));
            properties[1].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_offline"));
            properties[1].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_offline"));
            properties[2].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_autoRefresh"));
            properties[2].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_autoRefresh"));
            properties[2].setPropertyEditorClass(RefreshModePropertyEditor.class);
            properties[3].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_home"));
            properties[3].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_home"));            
            properties[4].setHidden(true);            
            //properties[5].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_recognizedFS"));
            //properties[5].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_recognizedFS"));
            properties[5].setHidden(true);
            properties[6].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_advancedNotification"));
            properties[6].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_advancedNotification"));            
            properties[7].setDisplayName(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("PROP_fileAnnotation"));
            properties[7].setShortDescription(NbBundle.getBundle(GeneralVcsSettingsBeanInfo.class).getString("HINT_fileAnnotation"));            
            properties[7].setPropertyEditorClass(AnnotationModePropertyEditor.class);
            
        } catch (java.beans.IntrospectionException intrexc) {
            org.openide.ErrorManager.getDefault().notify(intrexc);
        }
        return properties;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
            case ICON_COLOR_16x16:
                return Utilities.loadImage("org/netbeans/modules/vcscore/settings/vcsSettings.gif"); // NOI18N
            case ICON_COLOR_32x32:
                return Utilities.loadImage("org/netbeans/modules/vcscore/settings/vcsSettings32.gif"); // NOI18N
        }
        return null;
    }

}

