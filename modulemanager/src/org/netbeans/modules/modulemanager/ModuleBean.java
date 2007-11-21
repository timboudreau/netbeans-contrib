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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.modulemanager;

import java.awt.Component;
import java.util.logging.Logger;
import org.netbeans.*;
import java.util.*;
import javax.swing.SwingUtilities;
import org.openide.modules.SpecificationVersion;
import java.io.File;
import java.beans.*;
import org.openide.*;
import java.io.IOException;
import java.util.logging.Level;
import org.openide.modules.ModuleInfo;
import org.openide.util.*;

/** Bean representing a module.
 * Mirrors its properties but provides safe access from the event thread.
 * Also permits delayed write access, again safe to initiate from the event thread.
 * These changes are batched and auto-validating.
 * @author Jesse Glick
 */
public final class ModuleBean implements Runnable, PropertyChangeListener {
    
    private static final Logger err = Logger.getLogger (ModuleBean.class.getName ());
    
    private final ModuleInfo moduleInfo;
    
    private String codeName;
    private String codeNameBase;
    private String specVers;
    private String implVers;
    private String buildVers;
    private String[] provides;
    private File jar;
    private boolean enabled;
    private boolean reloadable;
    private boolean autoload;
    private boolean eager;
    private boolean problematic;
    private String[] problemDescriptions;
    private String displayName;
    private String shortDescription;
    private String longDescription;
    private String category;
    private String classpath;
    
    /** Must be created within mutex. */
    private ModuleBean(ModuleInfo m) {
        moduleInfo = m;
        loadProps();
        moduleInfo.addPropertyChangeListener(WeakListeners.propertyChange(this, moduleInfo));
    }
    
    /** If necessary, get the underlying module. */
    public ModuleInfo getModuleInfo () {
        return moduleInfo;
    }
    
    private void loadProps() {
        if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
        err.log(Level.FINE, "loadProps: module=" + moduleInfo);
        if (! Hacks.isValid (moduleInfo)) {
            err.log(Level.FINE, "invalid, forget it...");
            return;
        }
        // Set fields. Called inside read mutex.
        codeName = moduleInfo.getCodeName();
        codeNameBase = moduleInfo.getCodeNameBase();
        SpecificationVersion sv = moduleInfo.getSpecificationVersion();
        specVers = (sv == null ? null : sv.toString());
        implVers = moduleInfo.getImplementationVersion ();
        buildVers = moduleInfo.getBuildVersion ();
        provides = moduleInfo.getProvides();
        jar = Hacks.getJarFile (moduleInfo);
        enabled = moduleInfo.isEnabled();
        reloadable = Hacks.isReloadable (moduleInfo);
        autoload = Hacks.isAutoload (moduleInfo);
        eager = Hacks.isEager (moduleInfo);
        Set problems = Hacks.getProblems (moduleInfo);
        problematic = !problems.isEmpty();
        if (problematic) {
            problemDescriptions = new String[problems.size()];
            Iterator it = problems.iterator();
            int i = 0;
            while (it.hasNext()) {
                problemDescriptions[i++] = Hacks.messageForProblem(moduleInfo, it.next());
            }
        } else {
            problemDescriptions = null;
        }
        err.log (Level.FINE, "IZ #82480: Module.getJarFile() " + Hacks.getJarFile (moduleInfo) + // NOI18N
                (Hacks.getJarFile (moduleInfo) != null ? " exists " + Boolean.toString (Hacks.getJarFile (moduleInfo).exists ()) : "")); // NOI18N
        displayName = moduleInfo.getDisplayName();
        shortDescription = (String)moduleInfo.getLocalizedAttribute("OpenIDE-Module-Short-Description"); // NOI18N
        longDescription = (String)moduleInfo.getLocalizedAttribute("OpenIDE-Module-Long-Description"); // NOI18N
        category = (String)moduleInfo.getLocalizedAttribute("OpenIDE-Module-Display-Category"); // NOI18N
        classpath = Hacks.getEffectiveClasspath (moduleInfo);
    }
    
    /** Get the code name. */
    public String getCodeName() {
        return codeName;
    }
    
    /** Get the code name base. */
    public String getCodeNameBase() {
        return codeNameBase;
    }
    
    /** Get the specification version, or null. */
    public String getSpecificationVersion() {
        return specVers;
    }
    
    /** Get the implementation version, or null. */
    public String getImplementationVersion() {
        return implVers;
    }
    
    /** Get the build version, or null. */
    public String getBuildVersion() {
        return buildVers;
    }
    
    /** Get a list of provided tokens (never null, maybe empty). */
    public String[] getProvides() {
        return provides;
    }
    
    /** Get the module JAR file, or null. */
    public File getJar() {
        return jar;
    }
    
    /** Get the module JAR cluster, or null if unknown or inapplicable. */
    public File getCluster() {
        if (jar != null) {
            File p = jar.getParentFile();
            if (p != null && p.getName().matches("eager|autoload")) { // NOI18N
                p = p.getParentFile();
            }
            if (p != null && p.getName().matches("modules|lib|core")) { // NOI18N
                return p.getParentFile();
            }
        }
        return null;
    }
    
    /** Test whether the module is enabled. */
    public boolean isEnabled() {
        return enabled;
    }
    
    /** Enable/disable the module. */
    public void setEnabled(boolean e) {
        if (enabled == e) return;
        if (jar == null || autoload || eager || problematic) throw new IllegalStateException();
        err.log(Level.FINE, "setEnabled: module=" + moduleInfo + " enabled=" + e);
        enabled = e; // optimistic change
        supp.firePropertyChange("enabled", null, null); // NOI18N
        Update u = new Update(e ? "enable" : "disable", moduleInfo); // NOI18N
        AllModulesBean.getDefault().update(u);
    }
    
    /** Test whether the module is a library module. */
    public boolean isAutoload() {
        return autoload;
    }
    
    /** Test whether the module is a bridge module. */
    public boolean isEager() {
        return eager;
    }
    
    /** Test whether the module is reloadable. */
    public boolean isReloadable() {
        return reloadable;
    }
    
    /** Set whether the module is reloadable. */
    public void setReloadable(boolean r) {
        // XXX sanity-check
        if (reloadable == r) return;
        err.log(Level.FINE,
                "setReloadable: module=" + moduleInfo + " reloadable=" + r);
        reloadable = r; // optimistic change
        supp.firePropertyChange("reloadable", null, null); // NOI18N
        Update u = new Update(r ? "makeReloadable" : "makeUnreloadable", moduleInfo); // NOI18N
        AllModulesBean.getDefault().update(u);
    }
    
    /** Delete the module. */
    public void delete() {
        if (jar == null) throw new IllegalStateException();
        err.log(Level.FINE, "delete: module=" + moduleInfo);
        Update u = new Update("delete", moduleInfo); // NOI18N
        AllModulesBean.getDefault().update(u);
    }
    
    /** Test whether the module has problems with installation. */
    public boolean isProblematic() {
        return problematic;
    }
    
    /**
     * Get a list of descriptions of each problem in the module (if it has any).
     * Each item will be a localized phrase.
     * @return a nonempty array of explanations if {@link #isProblematic}, null otherwise
     * @see NbProblemDisplayer#messageForProblem
     * @see "#16636"
     */
    public String[] getProblemDescriptions() {
        return problemDescriptions;
    }
    
    /** Get the display name. */
    public String getDisplayName() {
        return displayName;
    }
    
    /** Get the short description, or null. */
    public String getShortDescription() {
        return shortDescription;
    }
    
    /** Get the long description, or null. */
    public String getLongDescription() {
        return longDescription;
    }
    
    /** Get the display category, or null. */
    public String getCategory() {
        return category;
    }
    
    /** Get the effective classpath for this module.
     * May be the empty string for a disabled module.
     * @see ModuleSystem#getEffectiveClasspath
     * @see "#22466"
     */
    public String getEffectiveClasspath() {
        return classpath;
    }
    
    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);
    
    /** Listen to changes in bean properties. */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
        supp.addPropertyChangeListener(l);
    }
    
    /** Stop listening to changes in bean properties. */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
        // Something on the module changed. Inside read mutex.
        err.log(Level.FINE, "got changes: module=" + moduleInfo + " evt=" + evt);
         if (/* #13834 */ evt != null && "classLoader".equals(evt.getPropertyName())) {
            err.log(Level.FINE, "ignoring PROP_CLASS_LOADER");
            // Speed optimization.
            return;
        }
        loadProps();
        SwingUtilities.invokeLater(this);
    }
    
    public void run() {
        if (! SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
        // Inside event thread after a change.
        err.log(Level.FINE, "firing changes: module=" + moduleInfo);
        supp.firePropertyChange(null, null, null);
        ModuleSelectionPanel.getGUI (false).setWaitingState (false, false);
    }
    
    // ModuleNode uses these as keys, so make sure even if recreated after change
    // in list of modules that the node selection is retained. Cf. #23757 however:
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof ModuleBean) &&
            codeNameBase.equals(((ModuleBean)o).codeNameBase);
    }
    
    @Override
    public int hashCode() {
        return 35632846 ^ codeNameBase.hashCode();
    }
    
    @Override
    public String toString() {
        return "ModuleBean[" + codeNameBase + "]"; // NOI8N
    }
    
    public static final class AllModulesBean implements Runnable, PropertyChangeListener, Comparator<ModuleInfo> {
        
        private static AllModulesBean deflt = null;
        /** Get the bean representing all modules. */
        public static synchronized AllModulesBean getDefault() {
            if (deflt == null) deflt = new AllModulesBean();
            return deflt;
        }
        private AllModulesBean() {}
        
        private final ModuleManager mgr = Hacks.getModuleManager ();
        
        private final PropertyChangeSupport supp = new PropertyChangeSupport(this);
        
        /** Listen to changes in the list of modules or whether there are pending updates. */
        public void addPropertyChangeListener(PropertyChangeListener l) {
            supp.removePropertyChangeListener(l);
            supp.addPropertyChangeListener(l);
        }
        
        /** Stop listening to changes in the list of modules and whether there are pending updates. */
        public void removePropertyChangeListener(PropertyChangeListener l) {
            supp.removePropertyChangeListener(l);
        }
        
        private ModuleBean[] modules = null;
        
        private Task recalcTask = null;
        
        /** Get the list of all modules. */
        public synchronized ModuleBean[] getModules() {
            err.log(Level.FINE,
                    "getModules: modules count=" +
                    (modules == null ? "null"
                                     : String.valueOf(modules.length)));
            if (modules == null) {
                recalcTask = RequestProcessor.getDefault().post(new Reader());
                modules = new ModuleBean[0];
                return modules;
            } else {
                return modules/*.clone()*/;
            }
        }
        
        /** Get a task representing the need to get all modules.
         * When it is finished (if it is not already), they will be ready.
         * It is <em>not</em> guaranteed that changes will have been fired to
         * listeners by the time this task finishes.
         */
        public synchronized Task waitForModules() {
            getModules();
            if (recalcTask != null) {
                return recalcTask;
            } else {
                return Task.EMPTY;
            }
        }
        
        /** Create a new module from JAR file, perhaps reloadable. */
        public void create(File jar, boolean reloadable) {
            err.log(Level.FINE, "create: jar=" + jar);
            Update u = new Update(reloadable ? "createReloadable" : "create", jar); // NOI18N
            update(u);
        }
        
        private class Reader implements Runnable {
            private boolean theother = false;
            Reader() {}
            /** Called first in request processor, then pushed to read mutex,
             * to read list of modules.
             */
            public void run() {
                if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
                if (! theother) {
                    err.log(Level.FINE, "will load modules in read mutex...");
                    Reader r = new Reader();
                    r.theother = true;
                    mgr.mutex().readAccess(r);
                    return;
                }
                err.log(Level.FINE, "first time, finding module list");
                // First time. We are in read mutex and need to find out what is here.
                Set modulesSet = mgr.getModules();
                ModuleBean[] _modules = new ModuleBean[modulesSet.size()];
                Iterator it = modulesSet.iterator();
                int i = 0;
                while (it.hasNext()) {
                    ModuleInfo m = (ModuleInfo)it.next();
                    _modules[i++] = new ModuleBean(m);
                }
                synchronized (AllModulesBean.this) {
                    modules = _modules;
                    recalcTask = null;
                }
                // Listen for further changes.
                mgr.addPropertyChangeListener(org.openide.util.WeakListeners.propertyChange(AllModulesBean.this, mgr));
                // Relative to the initial list of zero modules, something 'has changed'.
                SwingUtilities.invokeLater(AllModulesBean.this);
            }
        }
        
        public void run() {
            if (! SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
            err.log(Level.FINE, "in event thread, will fire changes");
            // Something changed and now we are in the event thread.
            // (Either list of modules or pending changes or both.)
            supp.firePropertyChange(null, null, null);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
            err.log(Level.FINE, "got changes: evt=" + evt);
            ModuleSelectionPanel.getGUI (false).setWaitingState (true, true);
            if (ModuleManager.PROP_MODULES.equals(evt.getPropertyName())) {
                // Later on. Something changed. Again in read mutex.
                Map<ModuleInfo, ModuleBean> modules2Beans = new HashMap<ModuleInfo, ModuleBean> (modules.length * 4 / 3 + 1);
                for (int i = 0; i < modules.length; i++) {
                    modules2Beans.put(modules[i].getModuleInfo(), modules[i]);
                }
                Set<ModuleInfo> modulesSet = mgr.getModules();
                ModuleBean[] themodules = new ModuleBean[modulesSet.size()];
                int i = 0;
                for (ModuleInfo m : modulesSet) {
                    ModuleBean existing = modules2Beans.get(m);
                    if (existing == null) existing = new ModuleBean(m);
                    themodules[i++] = existing;
                }
                synchronized (this) {
                    modules = themodules;
                }
                // Fire changes later.
                SwingUtilities.invokeLater(this);
            }
        }
        
        private final List<Update> updates = new LinkedList<Update>();
        
        private boolean paused = false;
        
        private Runnable updater = new Updater();
        
        private RequestProcessor.Task updaterTask = new RequestProcessor ("module-bean-updater").create (updater);
        
        /** Pause any pending updates for later. */
        public void pause() {
            err.log(Level.FINE, "pause");
            paused = true;
        }
        
        /** Resume any previously paused updates. */
        public void resume() {
            err.log(Level.FINE, "resume");
            paused = false;
            updaterTask.schedule (0);
        }
        
        /** Cancel any previously posted updates (whether paused or not). */
        public void cancel() {
            err.log(Level.FINE, "cancel");
            synchronized (updates) {
                updates.clear();
            }
            paused = false;
            supp.firePropertyChange("pending", null, null); // NOI18N
        }
        
        /** Test whether there are any pending updates (whether paused or not). */
        public boolean isPending() {
            synchronized (updates) {
                return ! updates.isEmpty();
            }
        }
        
        /** Called from event thread.
         * Access only from within this class or from ModuleBean.
         */
        void update(Update u) {
            synchronized (updates) {
                // If nonempty, we are already waiting for it...
                boolean runme = updates.isEmpty();
                updates.add(u);
                err.log(Level.FINE, "pending updates: " + updates);
                if (runme) {
                    updaterTask.schedule (0);
                }
            }
            supp.firePropertyChange("pending", null, null); // NOI18N
        }
        
        private class Updater implements Runnable {
            Updater() {}
            /** Called from request processor to actually perform updates.
             * Or from the write mutex if isWriteAccess().
             */
            public void run() {
                if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException();
                if (! mgr.mutex ().isWriteAccess ()) {
                    err.log(Level.FINE, "saving all documents...");
                    org.openide.LifecycleManager.getDefault ().saveAll ();
                    err.log(Level.FINE, "will run updates in write mutex...");
                    mgr.mutex().writeAccess(this);
                    return;
                }
                try {
                    if (paused) {
                        err.log(Level.FINE, "run updates, but paused");
                        return;
                    }
                    ModuleSelectionPanel.getGUI (false).setWaitingState (true, true);
                    Set<Union2<ModuleInfo, File>> toEnable = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toDisable = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toMakeReloadable = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toMakeUnreloadable = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toDelete = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toCreate = new HashSet<Union2<ModuleInfo, File>> ();
                    Set<Union2<ModuleInfo, File>> toCreateReloable = new HashSet<Union2<ModuleInfo, File>> ();
                    List<Update> updatesL = null;
                    synchronized (updates) {
                        if (updates.isEmpty()) {
                            err.log(Level.FINE, "run updates, but empty");
                            return;
                        }
                        err.log(Level.FINE, "run updates: " + updates);
                        updatesL = new LinkedList<Update> (updates);
                        updates.clear();
                    }
                    for (Update u : updatesL) {
                        if (u.command.equals("enable")) { // NOI18N
                            if (toDelete.contains(u.arg)) throw new IllegalStateException();
                            toDisable.remove(u.arg);
                            toEnable.add(u.arg);
                        } else if (u.command.equals("disable")) { // NOI18N
                            if (toDelete.contains(u.arg)) throw new IllegalStateException();
                            toEnable.remove(u.arg);
                            toDisable.add(u.arg);
                        } else if (u.command.equals("makeReloadable")) { // NOI18N
                            if (toDelete.contains(u.arg)) throw new IllegalStateException();
                            toMakeUnreloadable.remove(u.arg);
                            toMakeReloadable.add(u.arg);
                        } else if (u.command.equals("makeUnreloadable")) { // NOI18N
                            if (toDelete.contains(u.arg)) throw new IllegalStateException();
                            toMakeReloadable.remove(u.arg);
                            toMakeUnreloadable.add(u.arg);
                        } else if (u.command.equals("delete")) { // NOI18N
                            toEnable.remove(u.arg);
                            toDisable.remove(u.arg); // will always be disabled anyway
                            toMakeReloadable.remove(u.arg);
                            toMakeUnreloadable.remove(u.arg);
                            toDelete.add(u.arg);
                        } else if (u.command.equals("create")) { // NOI18N
                            toCreateReloable.remove(u.arg);
                            toCreate.add(u.arg);
                        } else if (u.command.equals("createReloadable")) { // NOI18N
                            toCreate.remove(u.arg);
                            toCreateReloable.add(u.arg);
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                    doDelete(takeModuleInfos (toDelete));
                    doDisable(takeModuleInfos (toDisable), true);
                    for (Union2<ModuleInfo, File> infoOrJar : toMakeReloadable) {
                        if (infoOrJar.hasFirst ()) {
                            Hacks.setReloadable (infoOrJar.first (), true);
                        }
                    }
                    for (Union2<ModuleInfo, File> infoOrJar : toMakeUnreloadable) {
                        if (infoOrJar.hasFirst ()) {
                            Hacks.setReloadable (infoOrJar.first (), false);
                        }
                    }
                    doEnable(takeModuleInfos (toEnable));
                    doCreate(toCreate, false);
                    doCreate(toCreateReloable, true);
                } catch (RuntimeException re) {
                    Exceptions.printStackTrace(re);
                    // Never know. Revert everything to real state just in case.
                    // #17873: if not inited, no need...
                    ModuleBean[] _modules = modules;
                    if (_modules != null) {
                        for (int i = 0; i < _modules.length; i++) {
                            _modules[i].propertyChange(null);
                        }
                    }
                } finally {
                    ModuleSelectionPanel.getGUI (false).setWaitingState (false, true);
                }
                // Fire a change in pending property.
                SwingUtilities.invokeLater(AllModulesBean.this);
            }
            
        }
        
        private static Set<ModuleInfo> takeModuleInfos (Collection<Union2<ModuleInfo, File>> infoOrJars) {
            Set<ModuleInfo> res = new HashSet<ModuleInfo> ();
            for (Union2<ModuleInfo, File> u : infoOrJars) {
                if (u.hasFirst ()) {
                    res.add (u.first ());
                } else {
                    err.log (Level.INFO, "Union2 with jar by mistake: " + u);
                }
            }
            return res;
        }
        
        // Actual command to do certain kinds of updates, called within
        // write mutex and should take care of their own UI. Note that it
        // is OK to block on the event thread here indirectly, by means of
        // calling TopManager.notify and waiting for the result.
        
        private void doDelete(Set<ModuleInfo> modules) {
            if (modules.isEmpty()) return;
            err.log(Level.FINE, "doDelete: " + modules);
            // Have to be turned off first:
            doDisable(modules, false);
            Iterator it = modules.iterator();
            while (it.hasNext()) {
                ModuleInfo m = (ModuleInfo)it.next();
                if (Hacks.isFixed (m)) {
                    // Hmm, ignore.
                    continue;
                }
                mgr.delete(m);
            }
        }
        
        public int compare(ModuleInfo m1, ModuleInfo m2) {
            int i = m1.getDisplayName().compareTo(m2.getDisplayName());
            if (i != 0) {
                return i;
            } else {
                return m1.getCodeNameBase().compareTo(m2.getCodeNameBase());
            }
        }

        private void doDisable(Set<ModuleInfo> modules, boolean cancelable) {
            if (modules.isEmpty()) return;
            err.log(Level.FINE, "doDisable: " + modules);
            SortedSet<ModuleInfo> realModules = new TreeSet<ModuleInfo> (this);
            for (ModuleInfo m : modules) {
                if (! m.isEnabled () || Hacks.isAutoload (m) || Hacks.isEager (m) || Hacks.isFixed (m)) {
                    // In here by mistake, ignore.
                } else {
                    realModules.add (m);
                }
            }
            // Check if there are any non-autoloads/eagers added.
            SortedSet<ModuleInfo> others = new TreeSet<ModuleInfo> (this); // SortedSet<Module>
            for (ModuleInfo m : mgr.simulateDisable (realModules)) {
                if (! Hacks.isAutoload (m) && ! Hacks.isEager (m) && !realModules.contains(m)) {
                    others.add(m);
                }
            }
            if (! others.isEmpty()) {
                Component c = new ModuleEnableDisablePanel(false, realModules, others);
                // component's accessibility will be used for Dialog accessibility
                c.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ModuleBean.class, "ACSN_TITLE_disabling"));
                c.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ModuleBean.class, "ACSD_TITLE_disabling"));
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(c,
                    NbBundle.getMessage(ModuleBean.class, "MB_TITLE_disabling"),
                    cancelable ? NotifyDescriptor.YES_NO_OPTION : NotifyDescriptor.DEFAULT_OPTION);
                if (org.openide.DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                    // User refused.
                    // Fire changes again since modules are now wrong & need recalc.
                    ModuleBean[] _modules = this.modules;
                    if (_modules != null) {
                        for (int i = 0; i < _modules.length; i++) {
                            if (realModules.contains(_modules[i].moduleInfo)) {
                                _modules[i].propertyChange(null);
                            }
                        }
                    }
                    return;
                }
                realModules.addAll (others);
            }
            // Ready to go.
            mgr.disable(realModules);
        }
        
        private void doEnable(Set<ModuleInfo> modules) {
            if (modules.isEmpty()) return;
            err.log(Level.FINE, "doEnable: " + modules);
            SortedSet<ModuleInfo> realModules = new TreeSet<ModuleInfo> (this);
            for (ModuleInfo m : modules) {
                if (m.isEnabled() || Hacks.isAutoload (m) || Hacks.isEager (m) || Hacks.isFixed (m) || ! Hacks.getProblems (m).isEmpty ()) {
                    // In here by mistake, ignore.
                } else {
                    realModules.add (m);
                }
            }
            // Check if there are any non-autoloads/eagers added.
            SortedSet<ModuleInfo> others = new TreeSet<ModuleInfo> (this);
            for (ModuleInfo m : mgr.simulateEnable(realModules)) {
                if (! Hacks.isAutoload (m) && ! Hacks.isEager (m) && ! realModules.contains (m)) {
                    others.add(m);
                }
            }
            if (! others.isEmpty()) {
                Component c = new ModuleEnableDisablePanel(true, realModules, others);
                // component's accessibility will be used for Dialog accessibility
                c.getAccessibleContext().setAccessibleName(NbBundle.getMessage(ModuleBean.class, "ACSN_TITLE_disabling"));
                c.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ModuleBean.class, "ACSD_TITLE_disabling"));
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(c,
                    NbBundle.getMessage(ModuleBean.class, "MB_TITLE_enabling"),
                    NotifyDescriptor.YES_NO_OPTION);
                if (org.openide.DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                    // User refused.
                    // Fire changes again since modules are now wrong & need recalc.
                    ModuleBean[] _modules = this.modules;
                    if (_modules != null) {
                        for (int i = 0; i < _modules.length; i++) {
                            if (realModules.contains(_modules[i].moduleInfo)) {
                                _modules[i].propertyChange(null);
                            }
                        }
                    }
                    return;
                }
                realModules.addAll(others);
            }
            // Ready to go. First reload any test modules.
            for (ModuleInfo m : mgr.simulateEnable(realModules)) {
                if (Hacks.isReloadable (m)) {
                    try {
                        mgr.reload(m);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                        // Refresh all.
                        ModuleBean[] _modules = this.modules;
                        if (_modules != null) {
                            for (int i = 0; i < _modules.length; i++) {
                                if (realModules.contains(_modules[i].moduleInfo)) {
                                    _modules[i].propertyChange(null);
                                }
                            }
                        }
                        return;
                    }
                }
            }
            try {
                mgr.enable(realModules);
            } catch (Exception ie) {
                err.log(Level.WARNING, null, ie);
                if (realModules == null) throw new IllegalStateException();
                err.log(Level.INFO, "EVENT -> FAILED_INSTALL_NEW_UNEXPECTED " + Arrays.asList (new Object[] { realModules, ie }));
                
                // Refresh it.
                ModuleBean[] _modules = this.modules;
                if (_modules != null) {
                    for (int i = 0; i < _modules.length; i++) {
                        if (realModules.contains (_modules[i].moduleInfo)) {
                            _modules[i].propertyChange(null);
                        }
                    }
                }
                // Try to enable a subset this time.
                realModules.remove(realModules);
                doEnable(realModules);
            }
        }
        
        private void doCreate(Set files, boolean reloadable) {
            if (files.isEmpty()) return;
            err.log(Level.FINE,
                    "doCreate: " + files + " reloadable=" + reloadable);
            Iterator it = files.iterator();
            while (it.hasNext()) {
                File jar = (File)it.next();
                ModuleInfo nue;
                try {
                    nue = mgr.create (jar, Hacks.createModuleHistory (jar.getAbsolutePath()), reloadable, false, false);
                } catch (IOException ioe) {
                    DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Exception(ioe));
                    continue;
                } catch (Exception dupe) {
                    // Cannot install, make sure user knows about it!
                    err.log(Level.INFO, "EVENT -> FAILED_INSTALL_NEW " + dupe);
                }
            }
        }
        
        // Inter-module dependencies; see #22504.
        // Cumbersome to do with the property + change model used for simple
        // properties of modules, because these properties may be expensive
        // to compute and might never be needed. It is also harder to know for
        // sure when they might have changed.

        /**
         * Modules directly needed by this module.
         */
        public static final int RELATION_DIRECTLY_NEEDS = 0;
        /**
         * All modules needed by this module.
         */
        public static final int RELATION_TRANSITIVELY_NEEDS = 1;
        /**
         * Modules which directly need this module.
         */
        public static final int RELATION_DIRECTLY_NEEDED_BY = 2;
        /**
         * All modules which need this module.
         */
        public static final int RELATION_TRANSITIVELY_NEEDED_BY = 3;
        
        /** A callback used to supply the result of a relationship computation
         * asynchronously.
         */
        public interface RelationCallback {
            /** Called when a computation is done.
             * @param modules a set of module beans
             */
            void result(Set<ModuleBean> modules);
        }
        
        private static final RequestProcessor RELATION_COMPUTER_RP = new RequestProcessor("RelationComputer"); // NOI18N
        /**
         * Compute the relations of other modules to this one.
         * <p>Provide-require dependencies are treated just like direct dependencies,
         * where there is in fact a provider for a requirement. Note that this could
         * lead to slightly misleading results in some cases: for example, consider
         * if A (enabled) requires X, and B (enabled) and C (enabled) both provide X.
         * A's reported dependency list will include both B and C, despite the fact
         * that it is possible to disable either one by itself without disabling A.
         * <p>This computation is done asynchronously. Call this method
         * from the event thread; the callback will be called later
         * from the event thread.
         * @param mb the module to start with
         * @param type one of the RELATION_* constants
         * @param callback will be called when the computation is done
         */
        public void getRelations(ModuleBean mb, int type, RelationCallback callback) {
            if (type != RELATION_DIRECTLY_NEEDS && type != RELATION_TRANSITIVELY_NEEDS &&
                    type != RELATION_DIRECTLY_NEEDED_BY && type != RELATION_TRANSITIVELY_NEEDED_BY) {
                throw new IllegalArgumentException("bad type: " + type); // NOI18N
            }
            RELATION_COMPUTER_RP.post(new RelationComputer(mb.moduleInfo, type, callback));
        }
        
        private class RelationComputer implements Runnable {
            private int stage;
            private final ModuleInfo m;
            private final int type;
            private final RelationCallback callback;
            private Set result; // Set<Module>
            public RelationComputer(ModuleInfo m, int type, RelationCallback callback) {
                this.stage = 0;
                this.m = m;
                this.type = type;
                this.callback = callback;
            }
            public void run() {
                switch (stage) {
                    case 0:
                        stage = 1;
                        mgr.mutex().readAccess(this);
                        break;
                    case 1:
                        compute();
                        stage = 2;
                        SwingUtilities.invokeLater(this);
                        break;
                    default:
                        // Convert Module -> ModuleBean and return it.
                        Set<ModuleBean> mbresult = new HashSet<ModuleBean> (result.size() * 2 + 1);
                        ModuleBean[] _modules = getModules();
                        for (int i = 0; i < _modules.length; i++) {
                            if (result.contains(_modules[i].moduleInfo)) {
                                mbresult.add(_modules[i]);
                            }
                        }
                        callback.result(mbresult);
                        break;
                }
            }
            /** Called from within module system read mutex.
             * Should do the calculations and store them.
             */
            private void compute() {
                switch (type) {
                case RELATION_DIRECTLY_NEEDS:
                    result = mgr.getModuleInterdependencies(m, false, false);
                    break;
                case RELATION_DIRECTLY_NEEDED_BY:
                    result = mgr.getModuleInterdependencies(m, true, false);
                    break;
                case RELATION_TRANSITIVELY_NEEDS:
                    result = mgr.getModuleInterdependencies(m, false, true);
                    break;
                case RELATION_TRANSITIVELY_NEEDED_BY:
                    result = mgr.getModuleInterdependencies(m, true, true);
                    break;
                default:
                    assert false : type;
                }
            }
        }
        
    }

    /** One update to run. */
    private static final class Update {
        public final String command;
        public final Union2<ModuleInfo, File> arg;
        public Update(String command, ModuleInfo info) {
            this.command = command;
            arg = org.openide.util.Union2.createFirst (info);
            assert arg != null : "Union2<ModuleInfo, File> cannot be null when create Update for command " + command;
        }
        public Update(String command, File jar) {
            this.command = command;
            arg = org.openide.util.Union2.createSecond (jar);
            assert arg != null : "Union2<ModuleInfo, File> cannot be null when create Update for command " + command;
        }
        @Override
        public boolean equals(Object o) {
            if (! (o instanceof Update)) return false;
            Update u = (Update)o;
            return command.equals(u.command) && arg.equals(u.arg);
        }
        @Override
        public int hashCode() {
            return command.hashCode() ^ arg.hashCode();
        }
        @Override
        public String toString() {
            return "Update[" + command + "," + arg + "]"; // NOI18N
        }
    }
    
}
