/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */


package org.netbeans.modules.tasklist.checkstyle.options;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.ErrorManager;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *  Bean info for Checkstyle settings.
 * @author hair
 * @version $Id$
 */
public class CheckstyleSettingsBeanInfo extends SimpleBeanInfo {

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bdesc = new BeanDescriptor(CheckstyleSettings.class);
        bdesc.setDisplayName(NbBundle.getMessage(CheckstyleSettingsBeanInfo.class,
                "OPTION_CHECKSTYLE_SETTINGS_NAME"));   //NOI18N
        bdesc.setShortDescription(NbBundle.getMessage(CheckstyleSettingsBeanInfo.class,
                "HINT_CHECKSTYLE_SETTINGS_NAME"));   //NOI18N
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
                new PropertyDescriptor(CheckstyleSettings.PROP_SCAN_CHECKSTYLE,
                        CheckstyleSettings.class)
            };

            desc[i].setDisplayName(NbBundle.getMessage(
                    CheckstyleSettingsBeanInfo.class,
                    "PROP_SCAN_CHECKSTYLE")); //NOI18N
            desc[i++].setShortDescription(NbBundle.getMessage(
                    CheckstyleSettingsBeanInfo.class,
                    "HINT_SCAN_CHECKSTYLE")); //NOI18N
        } catch (IntrospectionException ex) {
            
            ErrorManager.getDefault().notify(ex);
            throw new InternalError();
        }
        return desc;
    }

    /** (Placeholder) icon in options window */
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/tasklist/checkstyle/checkstyle.gif"); //NOI18N
    }
}
