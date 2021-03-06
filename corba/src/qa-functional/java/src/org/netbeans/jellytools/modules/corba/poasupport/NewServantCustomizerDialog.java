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

package org.netbeans.jellytools.modules.corba.poasupport;

import org.netbeans.jemmy.operators.*;

public class NewServantCustomizerDialog extends JDialogOperator {

    /** Creates new NewServantCustomizerDialog that can handle it.
     */
    public NewServantCustomizerDialog() {
        super("Customizer Dialog");
    }

    private JLabelOperator _lblVariable;
    private JTextFieldOperator _txtVariable;
    private JLabelOperator _lblIDVariable;
    private JTextFieldOperator _txtIDVariable;
    private JCheckBoxOperator _cbGenerateServantInstantiationCode;
    private JLabelOperator _lblType;
    private JComboBoxOperator _cboType;
    private JLabelOperator _lblConstructor;
    private JComboBoxOperator _cboConstructor;
    private JButtonOperator _btClose;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Variable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblVariable() {
        if (_lblVariable==null) {
            _lblVariable = new JLabelOperator(this, "Variable:");
        }
        return _lblVariable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtVariable() {
        if (_txtVariable==null) {
            _txtVariable = new JTextFieldOperator(this);
        }
        return _txtVariable;
    }

    /** Tries to find "ID variable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIDVariable() {
        if (_lblIDVariable==null) {
            _lblIDVariable = new JLabelOperator(this, "ID variable:");
        }
        return _lblIDVariable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIDVariable() {
        if (_txtIDVariable==null) {
            _txtIDVariable = new JTextFieldOperator(this, 1);
        }
        return _txtIDVariable;
    }

    /** Tries to find "Generate servant instantiation code" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbGenerateServantInstantiationCode() {
        if (_cbGenerateServantInstantiationCode==null) {
            _cbGenerateServantInstantiationCode = new JCheckBoxOperator(this, "Generate servant instantiation code");
        }
        return _cbGenerateServantInstantiationCode;
    }

    /** Tries to find "Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblType() {
        if (_lblType==null) {
            _lblType = new JLabelOperator(this, "Type:");
        }
        return _lblType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboType() {
        if (_cboType==null) {
            _cboType = new JComboBoxOperator(this);
        }
        return _cboType;
    }

    /** Tries to find "Constructor:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblConstructor() {
        if (_lblConstructor==null) {
            _lblConstructor = new JLabelOperator(this, "Constructor:");
        }
        return _lblConstructor;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboConstructor() {
        if (_cboConstructor==null) {
            _cboConstructor = new JComboBoxOperator(this, 1);
        }
        return _cboConstructor;
    }

    /** Tries to find "Close" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btClose() {
        if (_btClose==null) {
            _btClose = new JButtonOperator(this, "Close");
        }
        return _btClose;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtVariable
     * @return String text
     */
    public String getVariable() {
        return txtVariable().getText();
    }

    /** sets text for txtVariable
     * @param text String text
     */
    public void setVariable(String text) {
        txtVariable().setText(text);
    }

    /** types text for txtVariable
     * @param text String text
     */
    public void typeVariable(String text) {
        txtVariable().typeText(text);
    }

    /** gets text for txtIDVariable
     * @return String text
     */
    public String getIDVariable() {
        return txtIDVariable().getText();
    }

    /** sets text for txtIDVariable
     * @param text String text
     */
    public void setIDVariable(String text) {
        txtIDVariable().setText(text);
    }

    /** types text for txtIDVariable
     * @param text String text
     */
    public void typeIDVariable(String text) {
        txtIDVariable().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkGenerateServantInstantiationCode(boolean state) {
        if (cbGenerateServantInstantiationCode().isSelected()!=state) {
            cbGenerateServantInstantiationCode().push();
        }
    }

    /** returns selected item for cboType
     * @return String item
     */
    public String getSelectedType() {
        return cboType().getSelectedItem().toString();
    }

    /** selects item for cboType
     * @param item String item
     */
    public void selectType(String item) {
        cboType().selectItem(item);
    }

    /** types text for cboType
     * @param text String text
     */
    public void typeType(String text) {
        cboType().typeText(text);
    }

    /** returns selected item for cboConstructor
     * @return String item
     */
    public String getSelectedConstructor() {
        return cboConstructor().getSelectedItem().toString();
    }

    /** selects item for cboConstructor
     * @param item String item
     */
    public void selectConstructor(String item) {
        cboConstructor().selectItem(item);
    }

    /** types text for cboConstructor
     * @param text String text
     */
    public void typeConstructor(String text) {
        cboConstructor().typeText(text);
    }

    /** clicks on "Close" JButton
     */
    public void close() {
        btClose().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NewServantCustomizerDialog by accessing all its components.
     */
    public void verify() {
        lblVariable();
        txtVariable();
        lblIDVariable();
        txtIDVariable();
        cbGenerateServantInstantiationCode();
        lblType();
        cboType();
        lblConstructor();
        cboConstructor();
        btClose();
    }

    /** Performs simple test of NewServantCustomizerDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NewServantCustomizerDialog().verify();
        System.out.println("NewServantCustomizerDialog verification finished.");
    }
}

