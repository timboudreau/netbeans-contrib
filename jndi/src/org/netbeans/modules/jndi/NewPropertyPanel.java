/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
