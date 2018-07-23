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
 * ProprietaryBindingStep.java
 *
 * Created on 15.7.02 15:17
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ProprietaryBindingStep extends WizardOperator {

    /** Creates new ProprietaryBindingStep that can handle it.
     */
    public ProprietaryBindingStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Binding Details");
    }

    private JLabelOperator _lblServerName;
    private JTextFieldOperator _txtServerName;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Server name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblServerName() {
        if (_lblServerName==null) {
            _lblServerName = new JLabelOperator(this, "Server name:");
        }
        return _lblServerName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtServerName() {
        if (_txtServerName==null) {
            _txtServerName = new JTextFieldOperator(this);
        }
        return _txtServerName;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtServerName
     * @return String text
     */
    public String getServerName() {
        return txtServerName().getText();
    }

    /** sets text for txtServerName
     * @param text String text
     */
    public void setServerName(String text) {
        txtServerName().setText(text);
    }

    /** types text for txtServerName
     * @param text String text
     */
    public void typeServerName(String text) {
        txtServerName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ProprietaryBindingStep by accessing all its components.
     */
    public void verify() {
        lblServerName();
        txtServerName();
    }

    /** Performs simple test of ProprietaryBindingStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ProprietaryBindingStep().verify();
        System.out.println("ProprietaryBindingStep verification finished.");
    }
}

