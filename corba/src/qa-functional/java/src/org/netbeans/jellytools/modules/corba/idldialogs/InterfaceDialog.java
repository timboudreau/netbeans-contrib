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

/*
 * InterfaceDialog.java
 *
 * Created on 15.7.02 13:59
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Interface" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class InterfaceDialog extends JDialogOperator {

    /** Creates new InterfaceDialog that can handle it.
     */
    public InterfaceDialog(boolean customize) {
        super(customize ? "Customize" : "Create Interface");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblBaseInterfaces;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtBaseInterfaces;
    private JCheckBoxOperator _cbAbstract;
    private JButtonOperator _btOk;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblName() {
        if (_lblName==null) {
            _lblName = new JLabelOperator(this, "Name:");
        }
        return _lblName;
    }

    /** Tries to find "Base Interfaces:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblBaseInterfaces() {
        if (_lblBaseInterfaces==null) {
            _lblBaseInterfaces = new JLabelOperator(this, "Base Interfaces:");
        }
        return _lblBaseInterfaces;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this);
        }
        return _txtName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtBaseInterfaces() {
        if (_txtBaseInterfaces==null) {
            _txtBaseInterfaces = new JTextFieldOperator(this, 1);
        }
        return _txtBaseInterfaces;
    }

    /** Tries to find "Abstract" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbAbstract() {
        if (_cbAbstract==null) {
            _cbAbstract = new JCheckBoxOperator(this, "Abstract");
        }
        return _cbAbstract;
    }

    /** Tries to find "Ok" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOk() {
        if (_btOk==null) {
            _btOk = new JButtonOperator(this, "Ok");
        }
        return _btOk;
    }

    /** Tries to find "Cancel" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCancel() {
        if (_btCancel==null) {
            _btCancel = new JButtonOperator(this, "Cancel");
        }
        return _btCancel;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtName
     * @return String text
     */
    public String getName() {
        return txtName().getText();
    }

    /** sets text for txtName
     * @param text String text
     */
    public void setName(String text) {
        txtName().setText(text);
    }

    /** types text for txtName
     * @param text String text
     */
    public void typeName(String text) {
        txtName().typeText(text);
    }

    /** gets text for txtBaseInterfaces
     * @return String text
     */
    public String getBaseInterfaces() {
        return txtBaseInterfaces().getText();
    }

    /** sets text for txtBaseInterfaces
     * @param text String text
     */
    public void setBaseInterfaces(String text) {
        txtBaseInterfaces().setText(text);
    }

    /** types text for txtBaseInterfaces
     * @param text String text
     */
    public void typeBaseInterfaces(String text) {
        txtBaseInterfaces().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkAbstract(boolean state) {
        if (cbAbstract().isSelected()!=state) {
            cbAbstract().push();
        }
    }

    /** clicks on "Ok" JButton
     */
    public void ok() {
        btOk().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of InterfaceDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblBaseInterfaces();
        txtName();
        txtBaseInterfaces();
        cbAbstract();
        btOk();
        btCancel();
    }

    /** Performs simple test of InterfaceDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new InterfaceDialog(false).verify();
        System.out.println("InterfaceDialog verification finished.");
    }
}

