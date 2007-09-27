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
 * AliasDialog.java
 *
 * Created on 15.7.02 13:56
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Alias" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class AliasDialog extends JDialogOperator {

    /** Creates new AliasDialog that can handle it.
     */
    public AliasDialog(boolean customize) {
        super(customize ? "Customize" : "Create Alias");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblType;
    private JLabelOperator _lblLength;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtType;
    private JTextFieldOperator _txtLength;
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

    /** Tries to find "Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblType() {
        if (_lblType==null) {
            _lblType = new JLabelOperator(this, "Type:");
        }
        return _lblType;
    }

    /** Tries to find "Length:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLength() {
        if (_lblLength==null) {
            _lblLength = new JLabelOperator(this, "Length:");
        }
        return _lblLength;
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
    public JTextFieldOperator txtType() {
        if (_txtType==null) {
            _txtType = new JTextFieldOperator(this, 1);
        }
        return _txtType;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtLength() {
        if (_txtLength==null) {
            _txtLength = new JTextFieldOperator(this, 2);
        }
        return _txtLength;
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

    /** gets text for txtType
     * @return String text
     */
    public String getType() {
        return txtType().getText();
    }

    /** sets text for txtType
     * @param text String text
     */
    public void setType(String text) {
        txtType().setText(text);
    }

    /** types text for txtType
     * @param text String text
     */
    public void typeType(String text) {
        txtType().typeText(text);
    }

    /** gets text for txtLength
     * @return String text
     */
    public String getLength() {
        return txtLength().getText();
    }

    /** sets text for txtLength
     * @param text String text
     */
    public void setLength(String text) {
        txtLength().setText(text);
    }

    /** types text for txtLength
     * @param text String text
     */
    public void typeLength(String text) {
        txtLength().typeText(text);
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

    /** Performs verification of AliasDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblType();
        lblLength();
        txtName();
        txtType();
        txtLength();
        btOk();
        btCancel();
    }

    /** Performs simple test of AliasDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new AliasDialog(false).verify();
        System.out.println("AliasDialog verification finished.");
    }
}

