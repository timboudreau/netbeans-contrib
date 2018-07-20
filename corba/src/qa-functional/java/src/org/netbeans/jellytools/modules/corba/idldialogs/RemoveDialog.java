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
 * RemoveDialog.java
 *
 * Created on 15.7.02 13:55
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Question" NbPresenter.
 *
 * @author dave
 * @version 1.0
 */
public class RemoveDialog extends JDialogOperator {

    /** Creates new RemoveDialog that can handle it.
     */
    public RemoveDialog() {
        super("Question");
    }

    private JLabelOperator _lblAreYouSureYouWantToDeleteFf;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Are you sure, you want to delete ff?" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAreYouSureYouWantToDeleteFf() {
        if (_lblAreYouSureYouWantToDeleteFf==null) {
            _lblAreYouSureYouWantToDeleteFf = new JLabelOperator(this, "Are you sure, you want to delete ff?");
        }
        return _lblAreYouSureYouWantToDeleteFf;
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

    /** Performs verification of RemoveDialog by accessing all its components.
     */
    public void verify() {
        lblAreYouSureYouWantToDeleteFf();
        btOK();
        btCancel();
    }

    /** Performs simple test of RemoveDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RemoveDialog().verify();
        System.out.println("RemoveDialog verification finished.");
    }
}

