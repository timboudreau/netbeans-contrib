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

import org.openide.filesystems.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

import org.netbeans.modules.zeroadmin.*;
import org.netbeans.core.projects.TrivialProjectManager;

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
        try {
            // force the core to save pending stuff:
            org.netbeans.core.windows.PersistenceManager.getDefault ().writeXMLWaiting ();
            org.netbeans.core.projects.XMLSettingsHandler.saveOptions();
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
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
        });
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(RefreshConfigAction.class).getString("Refresh");
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }    
}
