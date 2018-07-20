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
 * RootInterfaceStep.java
 *
 * Created on 15.7.02 14:52
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class RootInterfaceStep extends WizardOperator {

    /** Creates new RootInterfaceStep that can handle it.
     */
    public RootInterfaceStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Root Interface(s)");
    }

    private JListOperator _lstAvailableInterfaces;
    public static final String ITEM_SIMPLE = "Simple"; 
    private JLabelOperator _lblSelectedServerInterface;
    private JTextFieldOperator _txtSelectedServerInterface;
    private JLabelOperator _lblAvailableInterfaces;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null JList in this dialog.
     * @return JListOperator
     */
    public JListOperator lstAvailableInterfaces() {
        if (_lstAvailableInterfaces==null) {
            _lstAvailableInterfaces = new JListOperator(this, 1);
        }
        return _lstAvailableInterfaces;
    }

    /** Tries to find "Selected Server Interface:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSelectedServerInterface() {
        if (_lblSelectedServerInterface==null) {
            _lblSelectedServerInterface = new JLabelOperator(this, "Selected Server Interface:");
        }
        return _lblSelectedServerInterface;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSelectedServerInterface() {
        if (_txtSelectedServerInterface==null) {
            _txtSelectedServerInterface = new JTextFieldOperator(this);
        }
        return _txtSelectedServerInterface;
    }

    /** Tries to find "Available Interfaces:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAvailableInterfaces() {
        if (_lblAvailableInterfaces==null) {
            _lblAvailableInterfaces = new JLabelOperator(this, "Available Interfaces:");
        }
        return _lblAvailableInterfaces;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtSelectedServerInterface
     * @return String text
     */
    public String getSelectedServerInterface() {
        return txtSelectedServerInterface().getText();
    }

    /** sets text for txtSelectedServerInterface
     * @param text String text
     */
    public void setSelectedServerInterface(String text) {
        txtSelectedServerInterface().setText(text);
    }

    /** types text for txtSelectedServerInterface
     * @param text String text
     */
    public void typeSelectedServerInterface(String text) {
        txtSelectedServerInterface().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of RootInterfaceStep by accessing all its components.
     */
    public void verify() {
        lstAvailableInterfaces();
        lblSelectedServerInterface();
        txtSelectedServerInterface();
        lblAvailableInterfaces();
    }

    /** Performs simple test of RootInterfaceStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RootInterfaceStep().verify();
        System.out.println("RootInterfaceStep verification finished.");
    }
}

