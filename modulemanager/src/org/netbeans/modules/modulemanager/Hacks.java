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
package org.netbeans.modules.modulemanager;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.modules.ModuleInfo;
import org.openide.util.Mutex;

/**
 *
 * @author Jirka Rechtacek
 */
@SuppressWarnings ("unchecked")
public class Hacks {

    private static final Logger err = Logger.getLogger (Hacks.class.getName ()); // NOI18N
    private static ModuleManager mgr = null;
    
    private Hacks() {}
    
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
    
    public static File getJarFile (ModuleInfo info) {
        return (File) getSomething (info, "getJarFile", File.class);
    }
    
    public static boolean isReloadable (ModuleInfo info) {
        return isSomething (info, "isReloadable");
    }
    
    public static void setReloadable (ModuleInfo info, boolean state) {
        setSomething (info, "setReloadable", Boolean.class, state);
    }
    
    public static boolean isAutoload (ModuleInfo info) {
        return isSomething (info, "isAutoload");
    }
    
    public static boolean isEager (ModuleInfo info) {
        return isSomething (info, "isEager");
    }
    
    public static Set<Object> getProblems (ModuleInfo info) {
        return (Set<Object>) getSomething (info, "getProblems", Set.class);
    }
    
    // XXX: missing generics
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
    
    // XXX: missing generics
    private static Object setSomething (ModuleInfo info, String something, Class type, Object... args) {
        Object res = null;
        try {
            
            Object module = getModule (info);
            Class clazz = module.getClass ();
            Method setSomethingM = clazz.getMethod (something, type);
            assert setSomethingM != null : "Method " + something + "(" + type + ") found.";
            setSomethingM.setAccessible (true);
            
            res = setSomethingM.invoke (module, args);
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "Module " + info.getCodeName () + " " + something + "() returns " + res);
        return res;
    }
    
    private static boolean isSomething (ModuleInfo info, String something) {
        if (getModule (info) == null) {
            return false;
        }
        return (Boolean) getSomething (info, something, Boolean.class);
    }
    
    ///////////////////////////////////////////////
    
    public static Object createModuleHistory (String jar) {
        // new ModuleHistory (String jar);
        Object moduleHistory = null;
        try {
            Class clazz = Class.forName ("org.netbeans.core.startup.ModuleHistory",
                    false,
                    Thread.currentThread().getContextClassLoader ());
            assert clazz != null : "Class org.netbeans.core.startup.ModuleHistory found.";
            
            Constructor newModuleHistory = clazz.getConstructor (String.class);
            moduleHistory = newModuleHistory.newInstance (jar);
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "Return new ModuleHistory (" + jar + "): " + moduleHistory);
        return moduleHistory;
    }
    
    public static String messageForProblem (ModuleInfo info, Object problem) {
        // NbProblemDisplayer.messageForProblem (Module m, Object problem)
        String res = null;
        try {
            Class clazz = Class.forName ("org.netbeans.core.startup.NbProblemDisplayer",
                    false,
                    Thread.currentThread ().getContextClassLoader ());
            assert clazz != null : "Class org.netbeans.core.startup.NbProblemDisplayer found.";
            
            Method messageForProblemM = clazz.getMethod ("messageForProblem", Class.forName ("org.netbeans.Module"), Object.class);
            messageForProblemM.setAccessible (true);
            assert getModule (info) != null;
            Object resO = messageForProblemM.invoke (null, getModule (info), problem);
            
            assert resO == null || resO instanceof String;
            
            res = resO == null ? null : (String) resO;
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "messageForProblem (" + info + ", " + problem + ") returns " + res);
        return res;
    }
    
    public static ModuleManager getModuleManager () {
        // mgr = Main.getModuleSystem ().getManager ();
        if (mgr == null) {
            mgr = new ModuleManagerImpl ();
        }
        return mgr;
    }
    
    public static String getEffectiveClasspath (ModuleInfo info) {
        // ModuleSystem.getEffectiveClasspath (Module m)
        String res = null;
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
            
            Method getEffectiveClasspathM = clazz.getMethod ("getEffectiveClasspath", Class.forName ("org.netbeans.Module"));
            assert getEffectiveClasspathM != null : "ModuleSystem.getEffectiveClasspath () found.";
            
            getEffectiveClasspathM.setAccessible (true);
            assert getModule (info) != null;
            Object resO = getEffectiveClasspathM.invoke (moduleSystem, getModule (info));
            
            assert resO == null || resO instanceof String;
            
            res = resO == null ? null : (String) resO;
            
        } catch (Exception x) {
            err.log (Level.INFO, x.getMessage (), x);
        }
        
        err.log (Level.FINE, "getEffectiveClasspathM (" + info + ") returns " + res);
        return res;
    }
    
    private static class ModuleManagerImpl implements ModuleManager {
        private Object mgr = null;
        
        public ModuleManagerImpl () {
            mgr = getModuleManager ();
        }
        
        public void addPropertyChangeListener (PropertyChangeListener l) {
            doSomethingSimple ("addPropertyChangeListener", PropertyChangeListener.class, Object.class, l);
        }

        public ModuleInfo create (File jar, Object history, boolean reloadable, boolean autoload, boolean eager) throws Exception {
            Object res = doSomething ("create",
                    new Class [] { File.class, Object.class, boolean.class, boolean.class, boolean.class },
                    ModuleInfo.class,
                    jar, history, reloadable, autoload, eager);
            return (ModuleInfo) res;
        }

        public void delete (ModuleInfo m) throws IllegalArgumentException {
            doSomethingSimple ("delete", ModuleInfo.class, Object.class, m);
        }

        public void disable (Set<ModuleInfo> modules) throws IllegalArgumentException {
            doSomethingSimple ("disable", Set.class, Object.class, modules);
        }

        public void enable (Set<ModuleInfo> modules) throws Exception {
            doSomethingSimple ("enable", Set.class, Object.class, modules);
        }

        public ModuleInfo get (String codeNameBase) {
            return (ModuleInfo) doSomethingSimple ("get", String.class, ModuleInfo.class, codeNameBase);
        }

        public Set getModuleInterdependencies (ModuleInfo m, boolean reverse, boolean transitive) {
            Class moduleClass = null;
            try {
                moduleClass = Class.forName ("org.netbeans.Module");
            } catch (ClassNotFoundException x) {
                err.log (Level.INFO, x.getMessage (), x);
                return null;
            }
            Object res = doSomething ("getModuleInterdependencies",
                    new Class [] { moduleClass, boolean.class, boolean.class },
                    Set.class,
                    getModule (m), reverse, transitive);
            return (Set) res;
        }

        public Set<ModuleInfo> getModules () {
            return (Set<ModuleInfo>) doSomething ("getModules", new Class[0], Set.class, new Object [0]);
        }

        public Mutex mutex () {
            return (Mutex) doSomething ("mutex", new Class[0], Mutex.class, new Object [0]);
        }

        public void reload (ModuleInfo m) throws IllegalArgumentException, IOException {
            doSomethingSimple ("reload", ModuleInfo.class, Object.class, m);
        }

        public void removePropertyChangeListener (PropertyChangeListener l) {
            doSomethingSimple ("removePropertyChangeListener", PropertyChangeListener.class, Object.class, l);
        }

        public List<ModuleInfo> simulateDisable (Set<ModuleInfo> modules) throws IllegalArgumentException {
            return (List<ModuleInfo>) doSomethingSimple ("simulateDisable", Set.class, List.class, modules);
        }

        public List<ModuleInfo> simulateEnable (Set<ModuleInfo> modules) throws IllegalArgumentException {
            return (List<ModuleInfo>) doSomethingSimple ("simulateEnable", Set.class, List.class, modules);
        }
        
        private Object getModuleManager () {
            Object mgr = null;
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

                mgr = getManager.invoke (moduleSystem);
                assert mgr != null : "Method getManager() returns ModuleManager.";

            } catch (Exception x) {
                err.log (Level.INFO, x.getMessage (), x);
            }

            err.log (Level.FINE, "org.netbeans.ModuleManager found.");
            return mgr;
        }
        
        // XXX: missing generics
        private Object doSomethingSimple (String something, Class type, Class resultType, Object... args) {
            return doSomething (something, new Class [] { type }, resultType, args);
        }
        
        // XXX: missing generics
        private Object doSomething (String something, Class[] parameterTypes, Class resultType, Object... args) {
            Object res = null;
            try {

                Class clazz = mgr.getClass ();
                Method doSomethingM = clazz.getMethod (something, parameterTypes);
                assert doSomethingM != null : "Method " + something + "(" + Arrays.asList(parameterTypes) + ") found.";
                doSomethingM.setAccessible (true);

                res = doSomethingM.invoke (mgr, args);
                assert res == null || resultType.isInstance (res) : "Method ModuleManager." + something + "() returns " + resultType + " or null " + res;
                
            } catch (NoSuchMethodException nsme) {
                err.log (Level.INFO, nsme.getMessage (), nsme);
            } catch (IllegalAccessException iae) {
                err.log (Level.INFO, iae.getMessage (), iae);
            } catch (InvocationTargetException ite) {
                throw new IllegalArgumentException (ite.getCause ());
            } finally {
                
            }

            err.log (Level.FINER, "ModuleManager." + something + "(" + args + ") returns " + res);
            return res;
        }

    }

}
