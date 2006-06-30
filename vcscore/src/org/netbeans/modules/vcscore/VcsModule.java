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

package org.netbeans.modules.vcscore;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.masterfs.providers.FileSystemProvider;
import org.netbeans.modules.vcscore.actions.VcsManagerAction;
import org.netbeans.modules.vcscore.registry.VcsFSProvider;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.grouping.VcsGroupMenuAction;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.openide.ErrorManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * Redistributes notifications about module lifetime.
 * Warning: it's registered using module manifest,
 * no usages can be found.
 *
 * @author Petr Kuzel
 */
public final class VcsModule extends ModuleInstall {
    
    private static EventListenerList restoredListeners;
    private static boolean restored = false;

    public void close() {
        Turbo.shutdown();
    }

    public void uninstalled() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    close(VersioningExplorer.getRevisionExplorer());
                    close(VcsGroupMenuAction.GroupExplorerPanel.getDefault());
                    close(CommandOutputTopComponent.getDefault());
                    VcsManagerAction managerAction = (VcsManagerAction)
                            VcsManagerAction.findObject(VcsManagerAction.class);
                    if (managerAction != null) {
                        managerAction.closeVcsManager();
                    }
                }
            });
        } catch (InterruptedException iex) {
            // Interrupted - ignored.
        } catch (InvocationTargetException itex) {
            ErrorManager.getDefault().notify(itex);
        }
        FileAttributeQuery.getDefault().cancel();
        // Unmount FS
        final Lookup.Result providerResult =
                Lookup.getDefault().lookup(new Lookup.Template(FileSystemProvider.class));
        for (Iterator it = providerResult.allInstances().iterator(); it.hasNext(); ) {
            FileSystemProvider provider = (FileSystemProvider) it.next();
            if (provider instanceof VcsFSProvider) {
                ((VcsFSProvider) provider).shutdown();
            }
        }
    }

    private void close(TopComponent component) {
        try {
            component.close();
        } catch (Exception e) {
            // ignore exceptions from dead components
        }
    }

}
