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

import javax.swing.SwingUtilities;

import org.openide.filesystems.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;
import org.openide.ErrorManager;

import org.netbeans.modules.zeroadmin.*;
import org.netbeans.core.NbTopManager;

/**
 * Saves the configuration to the remote storage. Depends
 * on core to enforce the winsys and options saving.
 * @author David Strupl
 */
public class SaveOperatorConfigAction extends CallableSystemAction {
    
    /**
     * Saves the writableLayer from ZeroAdminInstall to the server.
     */
    public void performAction() {
        try {
            final ZeroAdminInstall z = (ZeroAdminInstall)SharedClassObject.findObject(ZeroAdminInstall.class);

            if ((z == null) || (z.writableLayer == null) || (z.cfgProxy == null)) {
                throw new IllegalStateException("ZeroAdminProjectManager not initialized");
            }
            Runnable r = new Runnable() {
                public void run() {
                    try {
            
                        // force the core to save pending stuff:
                        NbTopManager.WindowSystem windowSystem = (NbTopManager.WindowSystem)Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
                        windowSystem.save();

                        XMLBufferFileSystem bufFs = new XMLBufferFileSystem();
                        ZeroAdminInstall.copy(z.writableLayer.getRoot(), bufFs.getRoot(), true);
                        bufFs.waitFinished();
                        z.cfgProxy.saveOperatorData(bufFs.getBuffer());
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
