/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber.ui;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.packet.Packet;
import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.Result;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author  nenik
 */
public class WaitForResultUI extends javax.swing.JPanel {

    
    public static void performQuery(Manager man, Packet packet, String title, String message) {
        new WaitForResultUI(man).doQuery(packet, title, message);
    }
    
    
    private Manager manager;
    private Result result;
    Dialog d;

    
    /** Creates new form RemoveContactUI */
    private WaitForResultUI(Manager man) {
        manager = man;
        
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        cancelButton = new javax.swing.JButton();
        list = new javax.swing.JLabel();

        cancelButton.setText("Cancel");
        cancelButton.setMinimumSize(new java.awt.Dimension(200, 100));

        setLayout(new java.awt.BorderLayout());

        add(list, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel list;
    // End of variables declaration//GEN-END:variables


    
    private void doQuery(Packet packet, String title, String message) {
        // prepare listener
        Lst listener = new Lst();
        
        list.setText(message);
        
        DialogDescriptor desc = new DialogDescriptor(this, title, true,
            new Object[] {cancelButton}, cancelButton,
            DialogDescriptor.BOTTOM_ALIGN, null, listener);
        
        d = DialogDisplayer.getDefault().createDialog(desc);
        d.addWindowListener(listener);

        result = manager.sendPacketWithResult(packet, listener);
        d.show();
    }

    private class Lst extends WindowAdapter implements ActionListener, Result.Callback, Runnable {
        boolean done;
        
        // cancel
        public void actionPerformed(java.awt.event.ActionEvent e) {
            result.cancel();
        }
        
        // result came, replan to AWT
        public void resultFinished(Result result) {
            done = true;
            SwingUtilities.invokeLater(this);
        }    
        
        // either finished removing or cancelled
        public void run() {
            d.dispose();
        }
        
        public void windowClosed(java.awt.event.WindowEvent e) {
            if (!done) {
                done = true;
                result.cancel();
                d.removeWindowListener(this); // necessary?
            }
        }
    }
}
