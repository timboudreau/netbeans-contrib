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
package org.netbeans.modules.corba.idl.editor.indent;

/**
 *
 * @author  tzezula
 */
import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.MethodDescriptor;
import java.beans.BeanDescriptor;
import org.openide.util.NbBundle;

public class IDLIndentEngineBeanInfo extends SimpleBeanInfo {

    private BeanDescriptor beanDescriptor;
    private PropertyDescriptor[] propertyDescriptors;
    private java.awt.Image icon;
    private java.util.ResourceBundle bundle;

    /** Creates new IDLindentEngineBeanInfo */
    public IDLIndentEngineBeanInfo() {
    }
    
    public BeanDescriptor getBeanDescriptor () {
        if (this.beanDescriptor == null) {
            this.beanDescriptor = new BeanDescriptor (IDLIndentEngine.class);
        }
        return this.beanDescriptor;
    }
    
    public PropertyDescriptor[] getPropertyDescriptors () {
        if (this.propertyDescriptors == null) {
            try {
                this.propertyDescriptors = new PropertyDescriptor[] {
                    new PropertyDescriptor("tabWidth",IDLIndentEngine.class)    // No I18N
                };
                this.propertyDescriptors[0].setBound(true);
                this.propertyDescriptors[0].setDisplayName(getLocalizedString("TXT_tabWidthDisplayName"));  // No I18N
                this.propertyDescriptors[0].setShortDescription(getLocalizedString("TXT_tabWidthShortDescription")); // No I18N
            }catch (java.beans.IntrospectionException introspectionException) {
                this.propertyDescriptors = new PropertyDescriptor[0];
            }
        }
        return this.propertyDescriptors;
    }
    
    public int getDefaultPropertyIndex () {
        return 0;
    }
    
    public java.awt.Image getIcon (int kind) {
        if (this.icon == null)
            this.icon = this.loadImage ("/org/netbeans/modules/editor/resources/indentEngine.gif"); // No I18N
        return icon;
    }
    
    protected String getLocalizedString (String message) {
        if (this.bundle == null)
            this.bundle = NbBundle.getBundle (IDLIndentEngineBeanInfo.class);
        return this.bundle.getString (message);
    }

}
