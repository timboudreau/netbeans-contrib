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
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.openide.modules.ModuleInfo;
import org.openide.util.Mutex;

/**
 *
 * @author Jirka Rechtacek (jrechtacek@netbeans.org)
 */
public interface ModuleManager {

    public static final java.lang.String PROP_CLASS_LOADER = "classLoader";

    public static final java.lang.String PROP_ENABLED_MODULES = "enabledModules";

    public static final java.lang.String PROP_MODULES = "modules";

    public void addPropertyChangeListener (java.beans.PropertyChangeListener l);

    public ModuleInfo create (java.io.File jar, java.lang.Object history, boolean reloadable, boolean autoload, boolean eager) throws Exception;

//    public ModuleInfo createFixed (java.util.jar.Manifest mani, java.lang.Object history, java.lang.ClassLoader loader) throws Exception;

    public void delete (ModuleInfo m) throws IllegalArgumentException;

//    public void disable (ModuleInfo m) throws IllegalArgumentException;

    public void disable (java.util.Set<ModuleInfo> modules) throws IllegalArgumentException;

//    public void enable (ModuleInfo m) throws Exception;

    public void enable (java.util.Set<ModuleInfo> modules) throws Exception;

    public ModuleInfo get (java.lang.String codeNameBase);

//    public ClassLoader getClassLoader ();

//    public Set<ModuleInfo> getEnabledModules ();

    public Set getModuleInterdependencies (ModuleInfo m, boolean reverse, boolean transitive);

//    public Lookup getModuleLookup ();

    public Set<ModuleInfo> getModules ();

//    public boolean isSpecialResource (java.lang.String pkg);

    public Mutex mutex ();

//    public Mutex.Privileged mutexPrivileged ();

//    public void refineClassLoader (ModuleInfo m, java.util.List parents);

    public void reload (ModuleInfo m) throws IllegalArgumentException, IOException;

    public void removePropertyChangeListener (PropertyChangeListener l);

//    public boolean shouldDelegateResource (ModuleInfo m, ModuleInfo parent, java.lang.String pkg);

//    public boolean shutDown ();

//    public boolean shutDown (java.lang.Runnable midHook);

    public List<ModuleInfo> simulateDisable (Set<ModuleInfo> modules) throws IllegalArgumentException;

    public List<ModuleInfo> simulateEnable (Set<ModuleInfo> modules) throws IllegalArgumentException;

}
