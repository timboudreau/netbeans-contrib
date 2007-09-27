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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.features2views;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Jirka Rechtacek
 */
public class InstalledModuleProvider {
    private static final Logger err = Logger.getLogger (InstalledModuleProvider.class.getName ()); // NOI18N
    private static InstalledModuleProvider DEFAULT;
    private Set<ModuleInfo> moduleInfos;
    private Lookup.Result<ModuleInfo> result;
    private LookupListener  lkpListener;
    
    private InstalledModuleProvider () {
        result = Lookup.getDefault ().lookup (new Lookup.Template<ModuleInfo> (ModuleInfo.class));
        lkpListener = new LookupListener () {
            public void resultChanged (LookupEvent ev) {
                moduleInfos = null;
            }
        };
        result.addLookupListener (lkpListener);
    }

    public static InstalledModuleProvider getDefault () {
        if (DEFAULT == null) {
            DEFAULT = new InstalledModuleProvider ();
        }
        return DEFAULT;
    }
        
    public Set<ModuleInfo> getModuleInfos (boolean force) {
        if (moduleInfos == null || force) {
            moduleInfos = new HashSet<ModuleInfo> ();
            Collection<? extends ModuleInfo> infos = result.allInstances ();
            for (ModuleInfo info: infos) {
                moduleInfos.add (info);
            }
            
        }
        assert moduleInfos != null;
        return moduleInfos;
    }
    
    public static File getCluster (ModuleInfo info) {
        File cluster = null;
        File jarFile = getModuleJarFile (info);
        if (jarFile != null) {
            File p = jarFile.getParentFile ();
            if (p != null && p.getName ().matches ("eager|autoload")) { // NOI18N
                p = p.getParentFile ();
            }
            if (p != null && p.getName ().matches ("modules|lib|core")) { // NOI18N
                cluster = p.getParentFile ();
            }
        }
        return cluster;
    }
    
    @SuppressWarnings ("unchecked")
    private static File getModuleJarFile (ModuleInfo info) {
        File jarFile = null;
        try {
            
            Class clazz = Class.forName ("org.netbeans.core.startup.Main",
                    false,
                    Thread.currentThread().getContextClassLoader ());
            assert clazz != null : "Class org.netbeans.core.startup.Main found.";
            
            Method getModuleSystemMethod = clazz.getMethod ("getModuleSystem");
            assert getModuleSystemMethod != null : "Method getModuleSystem() found.";
            getModuleSystemMethod.setAccessible (true);
            
            Object moduleSystem = getModuleSystemMethod.invoke (null);
            assert moduleSystem != null : "Method getModuleSystem() returns ModuleSystem.";
            
            clazz = moduleSystem.getClass ();
            Method getManager = clazz.getMethod ("getManager");
            assert getManager != null : "Method getManager() found.";
            getManager.setAccessible (true);
            
            Object mgr = getManager.invoke (moduleSystem);
            assert mgr != null : "Method getManager() returns ModuleManager.";
            
            clazz = mgr.getClass ();
            Method getModule = clazz.getMethod ("get", String.class);
            assert getModule != null : "Method get(String codeName) found.";
            getModule.setAccessible (true);
            
            Object module = getModule.invoke (mgr, info.getCodeNameBase ());
            assert module != null : "Method ModuleManager.get(" + info.getCodeNameBase () + ") returns Module.";
            
            clazz = module.getClass ();
            Method getJarFile = clazz.getMethod ("getJarFile");
            assert getJarFile != null : "Method getJarFile() found.";
            getJarFile.setAccessible (true);
            
            Object file = getJarFile.invoke (module);
            assert file == null || file instanceof File : "Method Module.getJarFile() returns File or null for module " + module;
            
            jarFile = file == null ? null : (File) file;
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "Module " + info.getCodeName () + " has jarFile " + jarFile);
        return jarFile;
    }

    public static String getCategory (ModuleInfo info) {
        String category = (String) info.getLocalizedAttribute ("OpenIDE-Module-Display-Category");
        category = category == null ? "" : category;
        if (isEager (info) || isAutoload (info) || isFixed (info)) {
            category = null;
        }
        return category;
    }
    
    @SuppressWarnings ("unchecked")
    private static Object getSomething (ModuleInfo info, String something, Class type) {
        Object getSomething = null;
        try {
            
            Object module = getModule (info);
            Class clazz = module.getClass ();
            Method getSomethingM = clazz.getMethod (something);
            assert getSomethingM != null : "Method " + something + "() found.";
            getSomethingM.setAccessible (true);
            
            Object res = getSomethingM.invoke (module);
            assert res == null || type.isInstance (res) : "Method Module." + something + "() returns " + type + " or null for module " + module;
            
            getSomething = res;
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "Module " + info.getCodeName () + " " + something + "() returns " + getSomething);
        return getSomething;
    }
    
    private static boolean isSomething (ModuleInfo info, String something) {
        if (getModule (info) == null) {
            return false;
        }
        return (Boolean) getSomething (info, something, Boolean.class);
    }
    
    @SuppressWarnings ("unchecked")
    public static Object getModule (ModuleInfo info) {
        Object module = null;
        try {
            Class clazz = Class.forName ("org.netbeans.core.startup.Main",
                    false,
                    Thread.currentThread().getContextClassLoader ());
            assert clazz != null : "Class org.netbeans.core.startup.Main found.";
            
            Method getModuleSystemMethod = clazz.getMethod ("getModuleSystem");
            assert getModuleSystemMethod != null : "Method getModuleSystem() found.";
            getModuleSystemMethod.setAccessible (true);
            
            Object moduleSystem = getModuleSystemMethod.invoke (null);
            assert moduleSystem != null : "Method getModuleSystem() returns ModuleSystem.";
            
            clazz = moduleSystem.getClass ();
            Method getManager = clazz.getMethod ("getManager");
            assert getManager != null : "Method getManager() found.";
            getManager.setAccessible (true);
            
            Object mgr = getManager.invoke (moduleSystem);
            assert mgr != null : "Method getManager() returns ModuleManager.";
            
            clazz = mgr.getClass ();
            Method getModule = clazz.getMethod ("get", String.class);
            assert getModule != null : "Method get(String codeName) found.";
            getModule.setAccessible (true);
            
            module = getModule.invoke (mgr, info.getCodeNameBase ());
            //assert module != null : "Method ModuleManager.get(" + info.getCodeNameBase () + ") returns Module.";
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "ModuleInfo [" + info.getCodeName () + "/" + info.getSpecificationVersion() + " has module " + module);
        return module;
    }
    
    public static boolean isValid (ModuleInfo info) {
        return isSomething (info, "isValid");
    }
    
    public static boolean isFixed (ModuleInfo info) {
        return isSomething (info, "isFixed");
    }
    
    public static boolean isAutoload (ModuleInfo info) {
        return isSomething (info, "isAutoload");
    }
    
    public static boolean isEager (ModuleInfo info) {
        return isSomething (info, "isEager");
    }
    
}
