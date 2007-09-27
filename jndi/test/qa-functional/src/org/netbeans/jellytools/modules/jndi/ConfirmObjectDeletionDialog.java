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

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Confirm Object Deletion" NbPresenter.
 *
 * @author dave
 * @version 1.0
 */
public class ConfirmObjectDeletionDialog extends JDialogOperator {

    /** Creates new ConfirmObjectDeletionDialog that can handle it.
     */
    public ConfirmObjectDeletionDialog() {
        super("Confirm Object Deletion");
    }

    private JLabelOperator _lblAreYouSureYouWantToDelete;
    private JButtonOperator _btYes;
    private JButtonOperator _btNo;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Are you sure you want to delete?" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAreYouSureYouWantToDelete() {
        if (_lblAreYouSureYouWantToDelete==null) {
            _lblAreYouSureYouWantToDelete = new JLabelOperator(this, "Are you sure you want to delete ");
        }
        return _lblAreYouSureYouWantToDelete;
    }

    /** Tries to find "Yes" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btYes() {
        if (_btYes==null) {
            _btYes = new JButtonOperator(this, "Yes");
        }
        return _btYes;
    }

    /** Tries to find "No" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btNo() {
        if (_btNo==null) {
            _btNo = new JButtonOperator(this, "No");
        }
        return _btNo;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "Yes" JButton
     */
    public void yes() {
        btYes().push();
    }

    /** clicks on "No" JButton
     */
    public void no() {
        btNo().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ConfirmObjectDeletionDialog by accessing all its components.
     */
    public void verify() {
        lblAreYouSureYouWantToDelete();
        btYes();
        btNo();
    }

    /** Performs simple test of ConfirmObjectDeletionDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ConfirmObjectDeletionDialog().verify();
        System.out.println("ConfirmObjectDeletionDialog verification finished.");
    }
}

