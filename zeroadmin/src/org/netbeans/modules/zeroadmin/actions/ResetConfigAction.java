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
import org.openide.windows.WindowManager;

// semi deprecated things
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.netbeans.modules.zeroadmin.*;

// core dependency
import org.netbeans.core.windows.PersistenceManager;
import org.netbeans.core.windows.WorkspaceImpl;
import org.netbeans.core.projects.TrivialProjectManager;

/**
 * Reset to operator configuration.
 * @author David Strupl
 */
public class ResetConfigAction extends CallableSystemAction {
    
    public void performAction() {
        final ZeroAdminProjectManager z = (ZeroAdminProjectManager)Lookup.getDefault()
                .lookup(TrivialProjectManager.class);
        if (z == null || z.writableLayer == null) {
            throw new IllegalStateException("ZeroAdminProjectManager not initialized");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // force the core to save pending stuff:
                    org.netbeans.core.windows.PersistenceManager.getDefault ().writeXMLWaiting ();
                    org.netbeans.core.projects.XMLSettingsHandler.saveOptions();

                    final FileObject[] ch = z.writableLayer.getRoot().getChildren();
                    z.writableLayer.runAtomicAction(new FileSystem.AtomicAction() {
                        // atomic action --> should be faster???
                        public void run() throws IOException {
                            for (int i = 0; i < ch.length; i++) {
                                if ("Modules".equals(ch[i].getName())) {
                                    // don't touch modules directory!
                                    continue;
                                }
                                ch[i].delete();
                            }
                            try {
                                z.installOperatorData();
                            } catch (Exception ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    });
                    updateWindowManager2();
                } catch (Exception re) {
                    ErrorManager.getDefault().notify(re);
                }
            }
        });
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(ResetConfigAction.class).getString("Reset");
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }    

    /**
     * Refresh the window system with the new data.
     * Copied from core from WindowManagerImpl.
     */
    static void updateWindowManager2() {
        PersistenceManager pm = PersistenceManager.getDefault();
        
        FileObject f = pm.getWindowManagerFolder();
        DataFolder d = DataFolder.findFolder(f);
        
        // tricky, this piece of code forces WindowManagerData to fire
        // PROP_CHILDREN property change, which in turn refreshes
        // whole hierarchy of loaded winsys objects
        DataObject ch [] = d.getChildren();
        try {
            d.setOrder(ch);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        WorkspaceImpl wi = (WorkspaceImpl)WindowManager.getDefault().getCurrentWorkspace();
        if (wi != null) {
            wi.setVisible(true);
        }
    }
}
