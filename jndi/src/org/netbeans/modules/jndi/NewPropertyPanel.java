/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi;

import javax.swing.JTextField;
import javax.swing.JLabel;

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
        name = new JTextField(20);
        value= new JTextField(20);
        add(new JLabel("Property Name:"),1,1,1,1,8,8,8,8);
        add(this.name,2,1,2,1,8,0,8,8);
        add(new JLabel("Property Value:"),1,2,1,1,0,8,8,8);
        add(this.value,2,2,2,1,0,0,8,8);
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
