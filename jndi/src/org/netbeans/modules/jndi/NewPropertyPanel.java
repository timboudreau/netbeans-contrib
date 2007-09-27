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

package org.netbeans.modules.jndi;

import javax.swing.JTextField;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/** Property panel for specifying additional properties
 *
 *  @author Tomas Zezula
 */
final class NewPropertyPanel extends GridBagPanel {

    private JTextField name;
    private JTextField value;

    /** Constructor
     */
    public NewPropertyPanel() {
        this.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_NewPropertyPanel"));
        name = new JTextField(20);
        name.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString ("AD_PropertyName"));
        value= new JTextField(20);
        value.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("AD_PropertyValue"));
        JLabel label = new JLabel(NbBundle.getBundle(NewPropertyPanel.class).getString("TXT_PropertyName"));
        label.setLabelFor (name);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_PropertyName_MNEM").charAt(0));
        add(label,1,1,1,1,8,8,8,8);
        add(this.name,2,1,2,1,8,0,8,8);
        label = new JLabel(NbBundle.getBundle(NewPropertyPanel.class).getString("TXT_PropertyValue"));
        label.setLabelFor (value);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_PropertyValue_MNEM").charAt(0));
        add(label,1,2,1,1,0,8,8,8);
        add(this.value,2,2,2,1,0,0,8,8);
        this.name.requestFocus ();
    }

    /** Accessor for name of property
     *  @return String name of property
     */
    public String getName() {
        return name.getText();
    }

    /** Accessor for value of property
     * @return String value
     */
    public String getValue() {
        return value.getText();
    }

    /** Sets the name of property
     *  @param name name of property
     */
    public void setName(String name) {
        this.name.setText(name);
    }

    /** Sets the value of property
     *  @param value value of property
     */  
    public void setValue(String value) {
        this.value.setText(value);
    }
}
