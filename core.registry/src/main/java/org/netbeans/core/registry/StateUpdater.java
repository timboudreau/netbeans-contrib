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

package org.netbeans.core.registry;

import org.netbeans.api.convertor.Convertors;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakListeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * This class listens on installed modules and installed convertors
 * and whenever there is a change it notifies all registered root contexts.
 * Root contexts should in turn notify all their existing instances of ContextImpl
 * that installed modules/convertors has changed and that some bindings might
 * need to be enabled or disable. The root contexts are weakly referenced so 
 * that they can be GCed.
 *
 * There is only one instance of this class.
 *
 */
final class StateUpdater {

    private Lookup.Result query;
    private ArrayList enabledModules;
    
    private static StateUpdater DEFAULT;
    
    private WeakHashMap list = new WeakHashMap();
    private final ModuleInfoLkpListener lookupListener = new ModuleInfoLkpListener();
    private final ConvertorsPropertyChangeListener propertyChangeListener = new ConvertorsPropertyChangeListener();

    private StateUpdater() {
        init();
    }
    
    static synchronized StateUpdater getDefault() {
        if (DEFAULT == null) {
            DEFAULT= new StateUpdater();
        }
        return DEFAULT;
    }
    
    private void init() {
        query = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class));
        enabledModules = getModules();
        // Weak listeners might not be needed, because StateUpdater is static:
        query.addLookupListener((LookupListener)WeakListeners.create(LookupListener.class, lookupListener, query));
        Convertors convertors = Convertors.getDefault();
        convertors.addPropertyChangeListener(WeakListeners.propertyChange(propertyChangeListener, convertors));
    }
    
    private ArrayList getModules() {
        Iterator it = query.allInstances().iterator();
        ArrayList m = new ArrayList();
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo)it.next();
            if (mi.isEnabled()) {
                m.add(cutOffVersion(mi.getCodeName()));
            }
        }
        return m;
    }

    boolean isModuleEnabled(String moduleCodeName) {
        return enabledModules.contains(cutOffVersion(moduleCodeName));
    }
    
    // some .setting files are using incorect version number
    // or are not using module version number at all.
    // cut off version and ignore it
    static String cutOffVersion(String moduleCodeName) {
        int index = moduleCodeName.lastIndexOf('/');
        if (index != -1) {
            moduleCodeName = moduleCodeName.substring(0, index);
        }
        return moduleCodeName;
    }
    
    /** Root context will be weakly referenced so that it can be GCed. */
    void registerRootContext(ContextImpl root) {
        list.put(root, null);
    }

    private class ModuleInfoLkpListener implements LookupListener {
        public void resultChanged(LookupEvent ev) {
            // Called whenever the list of modules is changed.
            // CLARIFY: will module reload be fired as module
            // disable and enable? If not this will not work.
            Set roots = list.keySet();
            if (roots.size() == 0) {
                return;
            }
            ArrayList modules = getModules();
            ArrayList added = new ArrayList(modules);
            added.removeAll(enabledModules);
            ArrayList removed = new ArrayList(enabledModules);
            removed.removeAll(modules);
            enabledModules = modules;
        
            Iterator all = roots.iterator();
            while (all.hasNext()) {
                ContextImpl root = (ContextImpl)all.next();
                root.getContextCache().modulesChanged(added, removed);
            }
        }
    }

    private class ConvertorsPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Set roots = list.keySet();
            if (roots.size() == 0) {
                return;
            }
            if (evt.getPropertyName().equals(Convertors.CONVERTOR_DESCRIPTORS)) {
                Set oldConvertors = (Set)evt.getOldValue();
                Set newConvertors = (Set)evt.getNewValue();
                // current impl of convertors calculates diff and sends
                // correct old and new value
                assert oldConvertors != null && newConvertors != null;

                ArrayList added = new ArrayList(newConvertors);
                added.removeAll(oldConvertors);
                ArrayList removed = new ArrayList(oldConvertors);
                removed.removeAll(newConvertors);
            
                Iterator all = roots.iterator();
                while (all.hasNext()) {
                    ContextImpl root = (ContextImpl)all.next();                
                    root.getContextCache().modulesChanged(added, removed);
                }
            }
        }
    }
    
/*
    public void unregisterRootContext(ContextImpl root) {
        list.remove(root);
    }
*/
    
    
}
    
