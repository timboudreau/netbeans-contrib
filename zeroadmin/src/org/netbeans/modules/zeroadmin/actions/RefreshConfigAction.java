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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin.actions;

import java.io.IOException;
import javax.swing.SwingUtilities;

import org.openide.filesystems.*;
import org.openide.util.SharedClassObject;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

import org.netbeans.modules.zeroadmin.*;
import org.netbeans.core.NbTopManager;

/**
 * Refresh the operator configuration.
 * @author David Strupl
 */
public class RefreshConfigAction extends CallableSystemAction {

    public void performAction() {
        final ZeroAdminInstall z = (ZeroAdminInstall)SharedClassObject.findObject(ZeroAdminInstall.class);
        if (z == null || z.writableLayer == null) {
            throw new IllegalStateException("ZeroAdminProjectManager not initialized");
        }
        Runnable r = new Runnable() {
            public void run() {
                try {
                    // force the core to save pending stuff:
                    NbTopManager.WindowSystem windowSystem = (NbTopManager.WindowSystem)Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
                    windowSystem.save();
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
