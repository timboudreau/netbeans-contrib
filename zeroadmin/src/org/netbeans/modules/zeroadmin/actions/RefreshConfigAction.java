/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin.actions;

import java.io.IOException;
import javax.swing.SwingUtilities;

import org.openide.filesystems.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

import org.netbeans.modules.zeroadmin.*;
import org.netbeans.core.projects.TrivialProjectManager;
import org.netbeans.core.NbTopManager;

/**
 * Refresh the operator configuration.
 * @author David Strupl
 */
public class RefreshConfigAction extends CallableSystemAction {
    
    public void performAction() {
        final ZeroAdminProjectManager z = (ZeroAdminProjectManager)Lookup.getDefault()
                .lookup(TrivialProjectManager.class);
        if (z == null || z.writableLayer == null) {
            throw new IllegalStateException("ZeroAdminProjectManager not initialized");
        }
        Runnable r = new Runnable() {
            public void run() {
                try {
                    // force the core to save pending stuff:
                    NbTopManager.WindowSystem windowSystem = (NbTopManager.WindowSystem)Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
                    windowSystem.save();
                    org.netbeans.core.projects.XMLSettingsHandler.saveOptions();
                    z.writableLayer.runAtomicAction(new FileSystem.AtomicAction() {
                        // atomic action --> should be faster???
                        public void run() throws IOException {
                            try {
                                z.refreshOperatorData();
                            } catch (Exception ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    });
                    ResetConfigAction.updateWindowManager2();
                } catch (Exception re) {
                    ErrorManager.getDefault().notify(re);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(RefreshConfigAction.class).getString("Refresh");
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }    
}
