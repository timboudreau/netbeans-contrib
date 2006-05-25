/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ShowKeyBindingsAction extends CallableSystemAction {
    private Rectangle bounds = new Rectangle(100, 100, 900, 700);
    
    public void performAction() {
        List masterList = KeyBindingsHelper.getKeyBindings();
        
        final JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage( ShowKeyBindingsAction.class, "TITLE_KeyBindings"), false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new BindingsPanel(masterList, 0));
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
        return NbBundle.getMessage( ShowKeyBindingsAction.class, "LBL_ShowKeyBindingsAction" ); //NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/keybindings/ui/KeyBindings.gif"; //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean asynchronous() {
        return false;
    }
}
