/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.timerwin;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Shows "running task" view.
 *
 * @author tl
 */
public final class ShowViewAction extends CallableSystemAction {
    private static JDialog win;
    
    public void performAction() {
        if (win == null) {
            win = new JDialog();
            win.setLocation(TimerWindowModuleInstall.WINDOW_POSITION);
            win.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    TimerWindowModuleInstall.WINDOW_POSITION.setLocation(
                            e.getWindow().getLocation());
                    TimerWindowModuleInstall.writeSettings();
                }
            });
            win.setUndecorated(true);
            new MoveWindowMouseListener(win.getContentPane());
            win.getContentPane().add(new TimeAccPanel(), BorderLayout.CENTER);
            win.pack();
        }
        win.setVisible(true);
        System.out.println(AlwaysOnTop.class.getName());
        AlwaysOnTop.setAlwaysOnTop(win);
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowViewAction.class, 
                "CTL_ShowViewAction"); // NOI18N
    }
    
    protected void initialize() {
        super.initialize();
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/timerwin/view.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }   
}
