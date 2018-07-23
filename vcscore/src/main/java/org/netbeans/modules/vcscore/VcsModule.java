/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore;

import java.lang.reflect.InvocationTargetException;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.vcscore.actions.VcsManagerAction;
//import org.netbeans.modules.vcscore.registry.VcsFSProvider;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.grouping.VcsGroupMenuAction;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.openide.ErrorManager;
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
        /*
        final Lookup.Result providerResult =
                Lookup.getDefault().lookup(new Lookup.Template(FileSystemProvider.class));
        for (Iterator it = providerResult.allInstances().iterator(); it.hasNext(); ) {
            FileSystemProvider provider = (FileSystemProvider) it.next();
            if (provider instanceof VcsFSProvider) {
                ((VcsFSProvider) provider).shutdown();
            }
        }
         */
    }

    private void close(TopComponent component) {
        try {
            component.close();
        } catch (Exception e) {
            // ignore exceptions from dead components
        }
    }

}
