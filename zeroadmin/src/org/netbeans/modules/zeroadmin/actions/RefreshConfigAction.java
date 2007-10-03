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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.zeroadmin.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import org.openide.filesystems.*;
import org.openide.util.SharedClassObject;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
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
                                Logger.getLogger(RefreshConfigAction.class.getName()).log(
                                    Level.SEVERE, "refreshOperatorData failed", ex);
                            }
                        }
                    });
                    ResetConfigAction.updateWindowManager2();
                } catch (Exception re) {
                    Logger.getLogger(RefreshConfigAction.class.getName()).log(
                        Level.SEVERE, "updateWindowManager2 failed", re);
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
