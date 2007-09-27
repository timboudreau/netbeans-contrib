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
 * TypeAplicationStep.java
 *
 * Created on 15.7.02 14:48
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class TypeAplicationStep extends WizardOperator {

    public static final String STEP_NAME = "Application Components";

    /** Creates new TypeAplicationStep that can handle it.
     */
    public TypeAplicationStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue (STEP_NAME);
    }

    private JCheckBoxOperator _cbCreateImplementation;
    private JCheckBoxOperator _cbTieBased;
    private JCheckBoxOperator _cbCreateClient;
    private JCheckBoxOperator _cbCreateServer;
    private JCheckBoxOperator _cbCreateCallBackClient;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Create Implementation" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateImplementation() {
        if (_cbCreateImplementation==null) {
            _cbCreateImplementation = new JCheckBoxOperator(this, "Create Implementation");
        }
        return _cbCreateImplementation;
    }

    /** Tries to find "Tie Based" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbTieBased() {
        if (_cbTieBased==null) {
            _cbTieBased = new JCheckBoxOperator(this, "Tie Based");
        }
        return _cbTieBased;
    }

    /** Tries to find "Create Client" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateClient() {
        if (_cbCreateClient==null) {
            _cbCreateClient = new JCheckBoxOperator(this, "Create Client");
        }
        return _cbCreateClient;
    }

    /** Tries to find "Create Server" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateServer() {
        if (_cbCreateServer==null) {
            _cbCreateServer = new JCheckBoxOperator(this, "Create Server");
        }
        return _cbCreateServer;
    }

    /** Tries to find "Create Call-back Client" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateCallBackClient() {
        if (_cbCreateCallBackClient==null) {
            _cbCreateCallBackClient = new JCheckBoxOperator(this, "Create Call-back Client");
        }
        return _cbCreateCallBackClient;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateImplementation(boolean state) {
        if (cbCreateImplementation().isSelected()!=state) {
            cbCreateImplementation().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkTieBased(boolean state) {
        if (cbTieBased().isSelected()!=state) {
            cbTieBased().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateClient(boolean state) {
        if (cbCreateClient().isSelected()!=state) {
            cbCreateClient().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateServer(boolean state) {
        if (cbCreateServer().isSelected()!=state) {
            cbCreateServer().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateCallBackClient(boolean state) {
        if (cbCreateCallBackClient().isSelected()!=state) {
            cbCreateCallBackClient().push();
        }
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of TypeAplicationStep by accessing all its components.
     */
    public void verify() {
        cbCreateImplementation();
        cbTieBased();
        cbCreateClient();
        cbCreateServer();
        cbCreateCallBackClient();
    }

    /** Performs simple test of TypeAplicationStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new TypeAplicationStep().verify();
        System.out.println("TypeAplicationStep verification finished.");
    }
}

