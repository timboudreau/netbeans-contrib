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

package org.netbeans.jellytools.modules.jndi;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Add Property" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class AddPropertyDialog extends NbDialogOperator {

    /** Creates new AddPropertyDialog that can handle it.
     */
    public AddPropertyDialog() {
        super("Add Property");
    }

    private JLabelOperator _lblPropertyName;
    private JTextFieldOperator _txtPropertyName;
    private JLabelOperator _lblPropertyValue;
    private JTextFieldOperator _txtPropertyValue;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Property Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPropertyName() {
        if (_lblPropertyName==null) {
            _lblPropertyName = new JLabelOperator(this, "Property Name:");
        }
        return _lblPropertyName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPropertyName() {
        if (_txtPropertyName==null) {
            _txtPropertyName = new JTextFieldOperator(this);
        }
        return _txtPropertyName;
    }

    /** Tries to find "Property Value:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPropertyValue() {
        if (_lblPropertyValue==null) {
            _lblPropertyValue = new JLabelOperator(this, "Property Value:");
        }
        return _lblPropertyValue;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPropertyValue() {
        if (_txtPropertyValue==null) {
            _txtPropertyValue = new JTextFieldOperator(this, 1);
        }
        return _txtPropertyValue;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtPropertyName
     * @return String text
     */
    public String getPropertyName() {
        return txtPropertyName().getText();
    }

    /** sets text for txtPropertyName
     * @param text String text
     */
    public void setPropertyName(String text) {
        txtPropertyName().setText(text);
    }

    /** types text for txtPropertyName
     * @param text String text
     */
    public void typePropertyName(String text) {
        txtPropertyName().typeText(text);
    }

    /** gets text for txtPropertyValue
     * @return String text
     */
    public String getPropertyValue() {
        return txtPropertyValue().getText();
    }

    /** sets text for txtPropertyValue
     * @param text String text
     */
    public void setPropertyValue(String text) {
        txtPropertyValue().setText(text);
    }

    /** types text for txtPropertyValue
     * @param text String text
     */
    public void typePropertyValue(String text) {
        txtPropertyValue().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of AddPropertyDialog by accessing all its components.
     */
    public void verify() {
        lblPropertyName();
        txtPropertyName();
        lblPropertyValue();
        txtPropertyValue();
    }

    /** Performs simple test of AddPropertyDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new AddPropertyDialog().verify();
        System.out.println("AddPropertyDialog verification finished.");
    }
}

