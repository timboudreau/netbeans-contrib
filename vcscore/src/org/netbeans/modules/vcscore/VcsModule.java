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

package org.netbeans.modules.vcscore;

import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.grouping.VcsGroupMenuAction;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;

/**
 * Redistributes notifications about module lifetime.
 * Warning: it's registered using module manifest,
 * no usages can be found.
 *
 * @author Petr Kuzel
 */
public final class VcsModule extends ModuleInstall {

    public void close() {
        Turbo.shutdown();
    }

    public void uninstalled() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                close(VersioningExplorer.getRevisionExplorer());
                close(VcsGroupMenuAction.GroupExplorerPanel.getDefault());
                close(CommandOutputTopComponent.getDefault());
            }
        });
    }

    private void close(TopComponent component) {
        try {
            component.close();
        } catch (Exception e) {
            // ignore exceptions from dead components
        }
    }
}
