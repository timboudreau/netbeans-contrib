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
 * IDLSourceStep.java
 *
 * Created on 15.7.02 13:51
 */
package org.netbeans.jellytools.modules.corba.idlwizard;

import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Wizard - Empty" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class IDLSourceStep extends NewWizardOperator {

    /** Creates new IDLSourceStep that can handle it.
     */
    public IDLSourceStep() {
        stepsWaitSelectedValue ("IDL Source");
    }

    private JRadioButtonOperator _rbIDLWizard;
    private JRadioButtonOperator _rbInterfaceRepository;
    private JLabelOperator _lblGenerateIDLFrom;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "IDL Wizard" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbIDLWizard() {
        if (_rbIDLWizard==null) {
            _rbIDLWizard = new JRadioButtonOperator(this, "IDL Wizard");
        }
        return _rbIDLWizard;
    }

    /** Tries to find "Interface Repository" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbInterfaceRepository() {
        if (_rbInterfaceRepository==null) {
            _rbInterfaceRepository = new JRadioButtonOperator(this, "Interface Repository");
        }
        return _rbInterfaceRepository;
    }

    /** Tries to find "Generate IDL from:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblGenerateIDLFrom() {
        if (_lblGenerateIDLFrom==null) {
            _lblGenerateIDLFrom = new JLabelOperator(this, "Generate IDL from:");
        }
        return _lblGenerateIDLFrom;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "IDL Wizard" JRadioButton
     */
    public void iDLWizard() {
        rbIDLWizard().push();
    }

    /** clicks on "Interface Repository" JRadioButton
     */
    public void interfaceRepository() {
        rbInterfaceRepository().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of IDLSourceStep by accessing all its components.
     */
    public void verify() {
        rbIDLWizard();
        rbInterfaceRepository();
        lblGenerateIDLFrom();
    }

    /** Performs simple test of IDLSourceStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new IDLSourceStep().verify();
        System.out.println("IDLSourceStep verification finished.");
    }
}

