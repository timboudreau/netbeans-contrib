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

import javax.swing.SwingUtilities;

import org.openide.filesystems.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;
import org.openide.ErrorManager;

import org.netbeans.modules.zeroadmin.*;
import org.netbeans.core.projects.TrivialProjectManager;
import org.netbeans.core.NbTopManager;

/**
 * Saves the configuration to the remote storage. Depends
 * on core to enforce the winsys and options saving.
 * @author David Strupl
 */
public class SaveOperatorConfigAction extends CallableSystemAction {
    
    /**
     * Saves the writableLayer from ZeroAdminProjectManager to the server.
     */
    public void performAction() {
        try {
            final ZeroAdminProjectManager z = (ZeroAdminProjectManager)Lookup.getDefault()
                .lookup(TrivialProjectManager.class);

            if ((z == null) || (z.writableLayer == null) || (z.storage == null)) {
                throw new IllegalStateException("ZeroAdminProjectManager not initialized");
            }
            Runnable r = new Runnable() {
                public void run() {
                    try {
            
                        // force the core to save pending stuff:
                        NbTopManager.WindowSystem windowSystem = (NbTopManager.WindowSystem)Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
                        windowSystem.save();
                        org.netbeans.core.projects.XMLSettingsHandler.saveOptions();

                        XMLBufferFileSystem bufFs = new XMLBufferFileSystem();
                        ZeroAdminProjectManager.copy(z.writableLayer.getRoot(), bufFs.getRoot(), true);
                        bufFs.waitFinished();
                        z.storage.saveOperatorData(bufFs.getBuffer());
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
        } catch (Exception re) {
            ErrorManager.getDefault().notify(re);
        }
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(SaveOperatorConfigAction.class).getString("SaveOperator");
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }
}
