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

package org.netbeans.modules.vcs.advanced;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 * Ensures that new versioning modules (New CVS, etc.) are disabled when this module installs.
 * 
 * @author Maros Sandor, Martin Entlicher
 */
public class ModuleLifecycleManager extends ModuleInstall {
    
    private static final Set newModules = new HashSet(Arrays.asList(new String[] {
        "org.netbeans.modules.versioning.system.cvss",
        "org.netbeans.modules.subversion",
        "org.netbeans.modules.mercurial",
        "org.netbeans.modules.localhistory",
    }));
    
    public void restored() {
        disableNewModules();
    }

    private void disableNewModules() {
        Runnable runnable = new Runnable() {
            public void run() {
                Set modulesToDisable = new HashSet();
                Collection modules = Lookup.getDefault().lookupAll(ModuleInfo.class);
                for (Iterator it = modules.iterator(); it.hasNext(); ) {
                    ModuleInfo module = (ModuleInfo) it.next();
                    if (newModules.contains(module.getCodeNameBase()) && module.isEnabled()) {
                        modulesToDisable.add(module);
                    }
                }
                if (modulesToDisable.size() > 0) {
                    JOptionPane.showMessageDialog(null, 
                                                  NbBundle.getBundle(ModuleLifecycleManager.class).getString("MSG_Install_Warning"), 
                                                  NbBundle.getBundle(ModuleLifecycleManager.class).getString("MSG_Install_Warning_Title"), 
                                                  JOptionPane.WARNING_MESSAGE);
                    disableModules(modulesToDisable);
                }
            }
        };
        RequestProcessor.getDefault().post(runnable);
    }

    public void uninstalled() {
        ProfilesFactory.getDefault().shutdown();
    }

    // Module hacks:
    // Inspired by contrib/modulemanager/src/org/netbeans/modules/modulemanager/Hacks

    private Object moduleManager;

    private synchronized Object getModuleManager () {
        if (moduleManager != null) return moduleManager;
        Object mgr = null;
        try {
            Class clazz = Class.forName ("org.netbeans.core.startup.Main",
                    false,
                    Thread.currentThread().getContextClassLoader ());
            assert clazz != null : "Class org.netbeans.core.startup.Main found.";

            Method getModuleSystemMethod = (Method) clazz.getMethod ("getModuleSystem", new Class[0]);
            assert getModuleSystemMethod != null : "Method getModuleSystem() found.";
            getModuleSystemMethod.setAccessible (true);

            Object moduleSystem = getModuleSystemMethod.invoke (null, new Object[0]);
            assert moduleSystem != null : "Method getModuleSystem() returns ModuleSystem.";

            clazz = moduleSystem.getClass ();
            Method getManager = clazz.getMethod ("getManager", new Class[0]);
            assert getManager != null : "Method getManager() found.";
            getManager.setAccessible (true);

            mgr = getManager.invoke (moduleSystem, new Object[0]);
            assert mgr != null : "Method getManager() returns ModuleManager.";

        } catch (Exception x) {
            Logger.getLogger(getClass().getName()).log (Level.INFO, x.getMessage (), x);
        }

        //err.log (Level.FINE, "org.netbeans.ModuleManager found.");
        moduleManager = mgr;
        return mgr;
    }

    private Object disableModules (Set modules) {
        Object res = null;
        try {
            Object mgr = getModuleManager();
            Class clazz = mgr.getClass ();
            Method doSomethingM = (Method) clazz.getMethod ("disable", new Class[] { Set.class });
            //assert doSomethingM != null : "Method " + something + "(" + Arrays.asList(parameterTypes) + ") found.";
            doSomethingM.setAccessible (true);

            res = doSomethingM.invoke (mgr, new Object[] { modules });
            //assert res == null || resultType.isInstance (res) : "Method ModuleManager." + something + "() returns " + resultType + " or null " + res;

        } catch (NoSuchMethodException nsme) {
            Logger.getLogger(getClass().getName()).log (Level.INFO, nsme.getMessage (), nsme);
        } catch (IllegalAccessException iae) {
            Logger.getLogger(getClass().getName()).log (Level.INFO, iae.getMessage (), iae);
        } catch (InvocationTargetException ite) {
            throw new IllegalArgumentException (ite.getCause ());
        } finally {

        }

        //err.log (Level.FINER, "ModuleManager." + something + "(" + args + ") returns " + res);
        return res;
    }

}
