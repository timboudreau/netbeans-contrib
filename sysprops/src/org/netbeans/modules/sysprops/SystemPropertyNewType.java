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
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.NewType;

/** NewType that can create a new SystemProperty.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
class SystemPropertyNewType extends NewType {

    /** Name of the Property. May be null. */
    private String propertyName = null;

    /**
     * Creates a new SystemPropertyNewType with a propertyName.
     */
    public SystemPropertyNewType(String propertyName) {
        this.propertyName = propertyName;
    }
    
    /**
     * Returns the Name of this NewType.
     * @return a localized display name
     */
    public String getName() {
        return NbBundle.getMessage(SystemPropertyNewType.class, "LBL_NewProp");
    }
    
    /**
     * Creates a new SystemProperty and refreshs the SystemProperties.
     * @throws IOException doesn't, actually
     */
    public void create() throws IOException {
        // create a new Dialog to ask the Name of the Property
        String title = NbBundle.getMessage(SystemPropertyNewType.class, "LBL_NewProp_dialog");
        String msg = NbBundle.getMessage(SystemPropertyNewType.class, "MSG_NewProp_dialog_key");
        
        NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine(msg, title);
        if (propertyName != null) {
            desc.setInputText(propertyName + ".");
        }
        
        if (!DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
            // User cancelled.
            return;
        }
        
        String key = desc.getInputText();
        
        // create a new Dialog to ast the Value of the Propertry
        msg = NbBundle.getMessage(SystemPropertyNewType.class, "MSG_NewProp_dialog_value");
        desc = new NotifyDescriptor.InputLine(msg, title);
        if (!DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
            return;
        }
        String value = desc.getInputText();
        
        // add the Property to the SystemProperties
        System.setProperty(key, value);
        
        // refresh the SystemProperties-Node(s)
        PropertiesNotifier.getDefault().changed();
    }
    
}
