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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.keybindings.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
*/
public class ShowActionBindingsAction extends CallableSystemAction {
    private Rectangle bounds = new Rectangle(100, 100, 900, 700);
    
    public void performAction() {
        List masterList = KeyBindingsHelper.getKeyBindings();
        
        final JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage( ShowActionBindingsAction.class, "TITLE_ActionBindings"), false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new BindingsPanel(masterList, 2));
        dialog.setBounds(bounds);
        dialog.setVisible(true);
        
//        dialog.addWindowListener(new WindowAdapter() {
//            public void windowDeactivated(WindowEvent e) {
//                bounds = dialog.getBounds();
//                dialog.setVisible(false);
//                dialog.dispose();
//            }
//        });
        
        dialog.getRootPane().registerKeyboardAction(
                new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.setVisible(false);
            }
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    
    public String getName() {
        return NbBundle.getMessage( ShowActionBindingsAction.class, "LBL_ShowActionBindingsAction" ); //NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/keybindings/ui/ActionBindings.gif"; //NOI18N
    }
    
    public boolean asynchronous() {
        return false;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
}
