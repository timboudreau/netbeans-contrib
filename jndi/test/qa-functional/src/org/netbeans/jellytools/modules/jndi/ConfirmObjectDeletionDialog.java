/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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

