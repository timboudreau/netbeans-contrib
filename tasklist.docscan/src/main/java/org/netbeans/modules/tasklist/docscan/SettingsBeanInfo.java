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


package org.netbeans.modules.tasklist.docscan;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Bean info for Settings. It's used for SystemOption serialization.
 *
 * @author Tor Norbye
 */
public final class SettingsBeanInfo extends SimpleBeanInfo {

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bdesc = new BeanDescriptor(Settings.class);
        bdesc.setDisplayName(NbBundle.getMessage(SettingsBeanInfo.class,
                "OPTION_TASK_SETTINGS_NAME"));   //NOI18N
        bdesc.setShortDescription(NbBundle.getMessage(SettingsBeanInfo.class,
                "HINT_TASK_SETTINGS_NAME"));   //NOI18N
        return bdesc;
    }

    /**
     *  Descriptor of valid properties.
     *
     *  @return array of properties
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        int i = 0;
        PropertyDescriptor[] desc = null;
        try {
            desc = new PropertyDescriptor[]{
                new PropertyDescriptor(Settings.PROP_SCAN_SKIP,
                        Settings.class),
                new PropertyDescriptor(Settings.PROP_SCAN_TAGS,
                        Settings.class),
                new PropertyDescriptor(Settings.PROP_MODIFICATION_TIME, Settings.class),
                new PropertyDescriptor(Settings.PROP_USABILITY_LIMIT, Settings.class),
            };

            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "PROP_SCAN_SKIP"));	    //NOI18N
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "HINT_SCAN_SKIP"));	    //NOI18N
            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "PROP_SCAN_TAGS"));	    //NOI18N
            desc[i].setPropertyEditorClass(TaskTagEditor.class);
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "HINT_SCAN_TAGS"));	    //NOI18N
            desc[i++].setHidden(true);

            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,"BK0002")); // NOI18N
            desc[i].setExpert(true);
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,"BK0003")); // NOI18N
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            throw new InternalError();
        }
        return desc;
    }

    /** (Placeholder) icon in options window */
    public Image getIcon(int type) {
        // XXX this icon is wrong
        return Utilities.loadImage("org/netbeans/modules/tasklist/docscan/settings.gif"); //NOI18N
    }
}
