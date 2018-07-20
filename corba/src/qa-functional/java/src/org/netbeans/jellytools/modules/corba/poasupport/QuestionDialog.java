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

public class QuestionDialog extends JDialogOperator {

    /** Creates new QuestionDialog that can handle it.
     */
    public QuestionDialog() {
        super("Question");
    }

    private JLabelOperator _lblLabel;
    private JLabelOperator _lblDefaultServantRegisteredWithThePOA;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Setting the Request Processing policy to the value " JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLabel() {
        if (_lblLabel==null) {
            _lblLabel = new JLabelOperator(this, "Setting the Request Processing policy to the value ");
        }
        return _lblLabel;
    }

    /** Tries to find "default servant registered with the POA." JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblDefaultServantRegisteredWithThePOA() {
        if (_lblDefaultServantRegisteredWithThePOA==null) {
            _lblDefaultServantRegisteredWithThePOA = new JLabelOperator(this, "default servant registered with the POA.");
        }
        return _lblDefaultServantRegisteredWithThePOA;
    }

    /** Tries to find "OK" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOK() {
        if (_btOK==null) {
            _btOK = new JButtonOperator(this, "OK");
        }
        return _btOK;
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

    /** clicks on "OK" JButton
     */
    public void oK() {
        btOK().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of QuestionDialog by accessing all its components.
     */
    public void verify() {
        lblLabel();
        lblDefaultServantRegisteredWithThePOA();
        btOK();
        btCancel();
    }

    /** Performs simple test of QuestionDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new QuestionDialog().verify();
        System.out.println("QuestionDialog verification finished.");
    }
}

