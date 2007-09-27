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
 * MergeIORsDialog.java
 *
 * Created on 16.7.02 16:19
 */
package org.netbeans.jellytools.modules.corba.dialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New IOR File" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class MergeIORsDialog extends JDialogOperator {

    /** Creates new MergeIORsDialog that can handle it.
     */
    public MergeIORsDialog() {
        super("New IOR File");
    }

    private JLabelOperator _lblIORFileName;
    private JTextFieldOperator _txtIORFileName;
    private JTreeOperator _tree;
    private JLabelOperator _lblPackage;
    private JTextFieldOperator _txtPackage;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "IOR File Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIORFileName() {
        if (_lblIORFileName==null) {
            _lblIORFileName = new JLabelOperator(this, "IOR File Name:");
        }
        return _lblIORFileName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIORFileName() {
        if (_txtIORFileName==null) {
            _txtIORFileName = new JTextFieldOperator(this);
        }
        return _txtIORFileName;
    }

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator tree() {
        if (_tree==null) {
            _tree = new JTreeOperator(this);
        }
        return _tree;
    }

    /** Tries to find "Package:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPackage() {
        if (_lblPackage==null) {
            _lblPackage = new JLabelOperator(this, "Package:");
        }
        return _lblPackage;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPackage() {
        if (_txtPackage==null) {
            _txtPackage = new JTextFieldOperator(this, 1);
        }
        return _txtPackage;
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

    /** gets text for txtIORFileName
     * @return String text
     */
    public String getIORFileName() {
        return txtIORFileName().getText();
    }

    /** sets text for txtIORFileName
     * @param text String text
     */
    public void setIORFileName(String text) {
        txtIORFileName().setText(text);
    }

    /** types text for txtIORFileName
     * @param text String text
     */
    public void typeIORFileName(String text) {
        txtIORFileName().typeText(text);
    }

    /** gets text for txtPackage
     * @return String text
     */
    public String getPackage() {
        return txtPackage().getText();
    }

    /** sets text for txtPackage
     * @param text String text
     */
    public void setPackage(String text) {
        txtPackage().setText(text);
    }

    /** types text for txtPackage
     * @param text String text
     */
    public void typePackage(String text) {
        txtPackage().typeText(text);
    }

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

    /** Performs verification of MergeIORsDialog by accessing all its components.
     */
    public void verify() {
        lblIORFileName();
        txtIORFileName();
        tree();
        lblPackage();
        txtPackage();
        btOK();
        btCancel();
    }

    /** Performs simple test of MergeIORsDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new MergeIORsDialog().verify();
        System.out.println("MergeIORsDialog verification finished.");
    }
}

