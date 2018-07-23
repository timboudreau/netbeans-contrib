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
 * SelectSourceIDLStep.java
 *
 * Created on 15.7.02 14:41
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class SelectSourceIDLStep extends WizardOperator {

    /** Creates new SelectSourceIDLStep that can handle it.
     */
    public SelectSourceIDLStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Source");
    }

    private JTreeOperator _tree;
    private JLabelOperator _lblIDLFileName;
    private JTextFieldOperator _txtIDLFileName;
    private JLabelOperator _lblPleaseSelectIDLFileToImplement;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator tree() {
        if (_tree==null) {
            _tree = new JTreeOperator(this);
        }
        return _tree;
    }

    /** Tries to find "IDL File Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIDLFileName() {
        if (_lblIDLFileName==null) {
            _lblIDLFileName = new JLabelOperator(this, "IDL File Name:");
        }
        return _lblIDLFileName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIDLFileName() {
        if (_txtIDLFileName==null) {
            _txtIDLFileName = new JTextFieldOperator(this);
        }
        return _txtIDLFileName;
    }

    /** Tries to find "Please select IDL file to implement:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPleaseSelectIDLFileToImplement() {
        if (_lblPleaseSelectIDLFileToImplement==null) {
            _lblPleaseSelectIDLFileToImplement = new JLabelOperator(this, "Please select IDL file to implement:");
        }
        return _lblPleaseSelectIDLFileToImplement;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtIDLFileName
     * @return String text
     */
    public String getIDLFileName() {
        return txtIDLFileName().getText();
    }

    /** sets text for txtIDLFileName
     * @param text String text
     */
    public void setIDLFileName(String text) {
        txtIDLFileName().setText(text);
    }

    /** types text for txtIDLFileName
     * @param text String text
     */
    public void typeIDLFileName(String text) {
        txtIDLFileName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of SelectSourceIDLStep by accessing all its components.
     */
    public void verify() {
        tree();
        lblIDLFileName();
        txtIDLFileName();
        lblPleaseSelectIDLFileToImplement();
    }

    /** Performs simple test of SelectSourceIDLStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new SelectSourceIDLStep().verify();
        System.out.println("SelectSourceIDLStep verification finished.");
    }
}

