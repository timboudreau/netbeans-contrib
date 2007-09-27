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

package org.netbeans.modules.corba.settings;

import java.awt.Image;
import java.beans.*;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;


/** BeanInfo for CORBASupportSettings - defines property editor
*
* @author Karel Gardas
* @version 0.11, March 27, 1999
*/

import org.netbeans.modules.corba.*;

public class CORBASupportSettingsBeanInfo extends SimpleBeanInfo {
    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    // initialization of the array of descriptors
    static {
        try {
            desc = new PropertyDescriptor[] {
		new PropertyDescriptor ("_M_orb_name", CORBASupportSettings.class, // NOI18N
					"getOrb", "setOrb"), // NOI18N
		new PropertyDescriptor ("_M_orb_tag", CORBASupportSettings.class, // NOI18N
					"getORBTag", "setORBTag"), // NOI18N
		new PropertyDescriptor ("namingChildren", // NOI18N
					CORBASupportSettings.class, // NOI18N
					"getNamingServiceChildren", // NOI18N
					"setNamingServiceChildren"), // NOI18N
		new PropertyDescriptor ("IRChildren", CORBASupportSettings.class, // NOI18N
					"getInterfaceRepositoryChildren", // NOI18N
					"setInterfaceRepositoryChildren"), // NOI18N
		new PropertyDescriptor ("_M_implementations", // NOI18N
					CORBASupportSettings.class, // NOI18N
					"getBeans", "setBeans") // NOI18N
		    };
	    desc[0].setDisplayName (ORBSettingsBundle.PROP_ORB);
	    desc[0].setShortDescription (ORBSettingsBundle.HINT_ORB);
	    desc[0].setPropertyEditorClass (OrbPropertyEditor.class);
	    
	    // hidden options for serialization
	    desc[1].setHidden (true);  // ORB Serialization Tag
	    desc[2].setHidden (true);  // children of persistent NamingService Browser
	    desc[3].setHidden (true);  // children of persistent IR Browser
	    desc[4].setHidden (true);  // _M_implementations
	} catch (IntrospectionException ex) {
            //throw new InternalError ();
	    org.openide.ErrorManager.getDefault().notify(ex);
        }
    }

    /**
     * loads icons
     */
    public CORBASupportSettingsBeanInfo () {
    }

    /** Returns the ExternalCompilerSettings' icon */
    public Image getIcon(int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) 
	    || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage
		    ("/org/netbeans/modules/corba/settings/orb.gif"); // NOI18N
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage
		    ("/org/netbeans/modules/corba/settings/orb32.gif"); // NOI18N
            return icon32;
        }
    }

    /** Descriptor of valid properties
     * @return array of properties
     */
    public PropertyDescriptor[] getPropertyDescriptors () {
        return desc;
    }
}

