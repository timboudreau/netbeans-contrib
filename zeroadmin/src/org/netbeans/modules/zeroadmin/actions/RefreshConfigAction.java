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

// semi deprecated things
import org.openide.util.SharedClassObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.netbeans.modules.zeroadmin.*;

/**
 * Refresh the operator configuration.
 * @author David Strupl
 */
public class RefreshConfigAction extends CallableSystemAction {
    
    public void performAction() {
        final ZeroAdminModule z = (ZeroAdminModule)SharedClassObject.findObject(ZeroAdminModule.class);
        if (z.writableLayer == null) {
            throw new IllegalStateException("ZeroAdminModule not initialized");
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
        return org.openide.util.NbBundle.getBundle(ResetConfigAction.class).getString("Refresh");
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }    
}
