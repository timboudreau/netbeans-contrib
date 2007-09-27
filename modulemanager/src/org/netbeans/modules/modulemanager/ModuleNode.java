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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/** Node representing modules.
 * @author Jesse Glick, Jaroslav Tulach, Rostislav Levy (?), et al.
 */
public class ModuleNode extends AbstractNode {

    enum SortMode {
        BY_CATEGORY,
        BY_DISPLAY_NAME,
        BY_CLUSTER
        // old modes: unsorted, by code name, by enablement, by URL
    }

    private static final String MODULE_ITEM_ICON_BASE = "org/netbeans/modules/modulemanager/resources/moduleItem.gif"; // NOI18N
    private static final String MODULE_ITEM_DISABLED_BASE = "org/netbeans/modules/modulemanager/resources/moduleItemDisabled.gif"; // NOI18N
    private static final String MODULE_ITEM_ERROR_BASE = "org/netbeans/modules/modulemanager/resources/moduleItemError.gif"; // NOI18N
    private static final String MODULE_TEST_ITEM_ICON_BASE = "org/netbeans/modules/modulemanager/resources/testModuleItem.gif"; // NOI18N
    private static final String MODULES_ICON_BASE = "org/netbeans/modules/modulemanager/resources/modules.gif"; // NOI18N

    private static ModuleDeleter deleter;

    private static Preferences getPreferences() {
        return NbPreferences.forModule(ModuleNode.class);
    }

    private static final String KEY_SORT_MODE = "moduleSortMode"; // NOI18N

    static SortMode getModuleSortMode() {
        return SortMode.valueOf(getPreferences().get(KEY_SORT_MODE, SortMode.BY_CATEGORY.name()));
    }

    static void setModuleSortMode(SortMode sortMode) {
        getPreferences().put(KEY_SORT_MODE, sortMode.name());
    }

    public ModuleNode() {
        // #14553: try to keep code name consistent for node path compatibility
        super(new Modules());
        setName("Modules"); // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(ModuleNode.class);
    }

    /** Class representing node of one standard module
     */
    static class Item extends AbstractNode implements PropertyChangeListener {

        private ModuleBean item;
        /** true if usedBy prop has been displayed, which can affect all else */
        private boolean listeningToAllModules = false;

        public Item(ModuleBean item) {
            super(Children.LEAF, Lookups.singleton(item));
            this.item = item;

            setName(item.getCodeNameBase());
            item.addPropertyChangeListener(WeakListeners.propertyChange(this, item));
            updateDisplayStuff();
        }

        ModuleBean getItem() {
            assert item != null : "Cannot be called untill item is null.";
            return item;
        }

        void uninstall() {
            ModuleDeleter deleter = getModuleDeleter();
            assert deleter != null : "ModuleDeleter must be available.";
            boolean canUninstall = deleter.canDelete(item.getModuleInfo());
            if (canUninstall) {
                try {
                    deleter.delete(item.getModuleInfo());
                } catch (IOException ioe) {
                    DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Exception(ioe));
                }
            } else if (canDestroy()) {
                item.delete();
            }
        }

        private void updateDisplayStuff() {
            setDisplayName(item.getDisplayName());
            setShortDescription(item.getShortDescription());
            setIconBaseWithExtension(item.isEnabled() ?
                (item.isReloadable() ? MODULE_TEST_ITEM_ICON_BASE : MODULE_ITEM_ICON_BASE) :
                    (item.isProblematic() ? MODULE_ITEM_ERROR_BASE : MODULE_ITEM_DISABLED_BASE));
        }

        public void propertyChange(PropertyChangeEvent evt) {
            // Some aspects of the module may have changed. Redisplay everything.
            updateDisplayStuff();
            firePropertyChange(null, null, null);
        }

        public HelpCtx getHelpCtx() {
            return new HelpCtx(Item.class);
        }

        public Action[] getActions(boolean context) {
            return new Action[] {
                SystemAction.get(ModuleNodeActions.EnableDisableAction.class),
                SystemAction.get(ModuleNodeActions.EnableAllAction.class),
                null,
                SystemAction.get(ModuleNodeActions.UninstallAction.class),
                null,
                SystemAction.get(ModuleNodeActions.SortAction.class),
                null,
                SystemAction.get(PropertiesAction.class),
            };
        }

        public Action getPreferredAction() {
            return SystemAction.get(PropertiesAction.class);
        }

        public void destroy() {
            if (ModuleNodeUtils.confirmUninstall(new Node [] { this })) {
                uninstall();
            }
        }

        public boolean canDestroy() {
            boolean canUninstall = getModuleDeleter().canDelete(item.getModuleInfo());
            if (! canUninstall) {
                // #65568: allow delete an standalone module
                return item.getJar() != null && ModuleNodeUtils.isUninstallAllowed(item.getModuleInfo());
            }
            return true;
        }

        /** Creates properties.
         */
        protected Sheet createSheet() {
            Sheet s = Sheet.createDefault();
            Sheet.Set ss = s.get(Sheet.PROPERTIES);

            Sheet.Set sse = Sheet.createExpertSet();
            s.put(sse);

            try {
                Node.Property<?> p;
                p = new PropertySupport.Reflection<String>(item, String.class, "getDisplayName", null); // NOI18N
                p.setName("displayName"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_name"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_name"));
                ss.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getSpecificationVersion", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("specificationVersion"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_specversion"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_specversion"));
                ss.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getImplementationVersion", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("implementationVersion"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_implversion"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_implversion"));
                sse.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getBuildVersion", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("buildVersion"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_buildversion"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_buildversion"));
                sse.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getShortDescription", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("shortDescription"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_shortDescription"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_shortDescription"));
                ss.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getLongDescription", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("longDescription"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_longDescription"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_longDescription"));
                ss.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getCategory", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("category"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_category"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_category"));
                sse.put(p);
                class EnabledOrReloadableProp extends PropertySupport.Reflection<Boolean> {
                    public EnabledOrReloadableProp(String getter, String setter) throws NoSuchMethodException {
                        super(item, Boolean.TYPE, getter, setter);
                    }
                    @Override
                    public boolean canWrite() {
                        if (! super.canWrite()) return false; // not really necessary
                        if (this.getName().equals("enabled")) { // NOI18N
                            return /*! item.isFixed()*/ item.getJar() != null && !item.isProblematic() && !item.isAutoload() && !item.isEager();
                        } else if (this.getName().equals("reloadable")) { // NOI18N
                            // Need to be able to write to the containing directory to use this feature.
                            return item.getJar() != null &&
                                    (item.isReloadable() || item.getJar().getParentFile().canWrite());
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                }
                p = new EnabledOrReloadableProp("isEnabled", "setEnabled"); // NOI18N
                p.setName("enabled"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_enabled"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_enabled"));
                ss.put(p);
                p = new EnabledOrReloadableProp("isReloadable", "setReloadable"); // NOI18N
                p.setName("reloadable"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_reloadable"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_reloadable"));
                sse.put(p);
                p = new PropertySupport.Reflection<String>(item, String.class, "getCodeName", null); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setName("codeName"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_codename"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_codename"));
                sse.put(p);
                if (item.getJar() != null) {
                    p = new PropertySupport.Reflection<File>(item, File.class, "getJar", null); // NOI18N
                    p.setName("jar"); // NOI18N
                    p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_jar"));
                    p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_jar"));
                    sse.put(p);
                }
                @SuppressWarnings("unchecked") // actual property type determined at runtime
                class ClasspathProperty extends PropertySupport.ReadOnly {
                    // See #22466
                    public ClasspathProperty() {
                        super("classpath", getNbClassPathOrStringClass(), // NOI18N
                                NbBundle.getMessage(ModuleNode.class, "PROP_modules_classpath"),
                                NbBundle.getMessage(ModuleNode.class, "HINT_modules_classpath"));
                    }
                    public Object getValue() throws InvocationTargetException {
                        String cp = item.getEffectiveClasspath();
                        if (getValueType() == String.class) {
                            return cp;
                        } else {
                            try {
                                Constructor c = getValueType().getConstructor(new Class[] {String.class});
                                return c.newInstance(new Object[] {cp});
                            } catch (Exception e) {
                                throw new InvocationTargetException(e);
                            }
                        }
                    }
                }
                sse.put(new ClasspathProperty());
                // Though normally in a separate category, still potentially
                // useful to leave here (e.g. if sorting differently):
                p = new PropertySupport.Reflection<Boolean>(item, Boolean.TYPE, "isAutoload", null); // NOI18N
                p.setName("autoload"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_autoload"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_autoload"));
                sse.put(p);
                p = new PropertySupport.Reflection<Boolean>(item, Boolean.TYPE, "isEager", null); // NOI18N
                p.setName("eager"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_eager"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_eager"));
                sse.put(p);
                p = new PropertySupport.Reflection<String[]>(item, String[].class, "getProvides", null); // NOI18N
                p.setName("provides"); // NOI18N
                p.setValue("suppressCustomEditor",true);
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_provides"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_provides"));
                sse.put(p);
                // #16636:
                p = new PropertySupport.Reflection<String[]>(item, String[].class, "getProblemDescriptions", null); // NOI18N
                p.setName("problemDescriptions"); // NOI18N
                p.setDisplayName(NbBundle.getMessage(ModuleNode.class, "PROP_modules_problemDescriptions"));
                p.setShortDescription(NbBundle.getMessage(ModuleNode.class, "HINT_modules_problemDescriptions"));
                sse.put(p);
            } catch (NoSuchMethodException nsme) {
                Exceptions.printStackTrace(nsme);
            }
            sse.put(new UsedByProp());

            return s;
        }

        private static Class getNbClassPathOrStringClass() {
            ClassLoader l = Lookup.getDefault().lookup(ClassLoader.class);
            try {
                return l.loadClass("org.openide.execution.NbClassPath"); // NOI18N
            } catch (ClassNotFoundException cnfe) {
                return String.class;
            }
        }

        /** For enabled modules, shows other modules which are currently
         * using it, i.e. depending on it to stay enabled.
         * @see #22504
         */
        private final class UsedByProp extends PropertySupport.ReadOnly<String[]> implements ModuleBean.AllModulesBean.RelationCallback, PropertyChangeListener {
            private String[] value = null;
            public UsedByProp() {
                super("usedBy", String[].class, // NOI18N
                        NbBundle.getMessage(ModuleNode.class, "PROP_modules_usedby"),
                        NbBundle.getMessage(ModuleNode.class, "HINT_modules_usedby"));
            }
            public String[] getValue() {
                if (value != null) {
                    return value;
                } else {
                    ModuleBean.AllModulesBean amb = ModuleBean.AllModulesBean.getDefault();
                    amb.getRelations(item, ModuleBean.AllModulesBean.RELATION_TRANSITIVELY_NEEDED_BY, this);
                    // All sorts of changes can now affect this!
                    if (!listeningToAllModules) {
                        listeningToAllModules = true;
                        amb.addPropertyChangeListener(WeakListeners.propertyChange(this, amb));
                        ModuleBean[] mods = amb.getModules();
                        for (int i = 0; i < mods.length; i++) {
                            if (mods[i] != item) {
                                mods[i].addPropertyChangeListener(WeakListeners.propertyChange(this, mods[i]));
                            }
                        }
                    }
                    return new String[] {NbBundle.getMessage(ModuleNode.class, "LBL_please_wait_modules_usedby")};
                }
            }
            public void result(Set<ModuleBean> modules) {
                List<String> l = new ArrayList<String>(Math.max(modules.size(), 1));
                // Only display enabled users, and only display anything if this is enabled:
                if (item.isEnabled()) {
                    for (ModuleBean mb : modules) {
                        if (mb.isEnabled()) {
                            l.add(mb.getDisplayName());
                        }
                    }
                    Collections.sort(l, Collator.getInstance());
                }
                String[] old = value;
                value = l.toArray(new String[l.size()]);
                Item.this.firePropertyChange("usedBy", old, value); // NOI18N
            }
            public void propertyChange(PropertyChangeEvent ev) {
                value = null;
                Item.this.firePropertyChange("usedBy", null, null); // NOI18N
            }
        }

    }

    /** Children that contains modules installed it has to
     * dissingushg between modules and test modules.
     * Keys are of type ModuleBean, *_KEY, String category name, or File cluster.
     */
    private static class Modules extends Children.Keys<Object> implements PropertyChangeListener {
        /** Key for fixed/autoload modules, rather than a category per se. */
        private static final Object LIBRARY_KEY = new Object();
        /** Key for eager modules. */
        private static final Object BRIDGE_KEY = new Object();
        /** Key for modules with no defined cluster. */
        private static final Object NO_CLUSTER_KEY = new Object();

        private PreferenceChangeListener prefsListener;

        public Modules() {}

        /** Refreshed list of nodes acc. to current sorting and contents. */
        private void refreshKeys() {
            if (prefsListener == null) {
                addNotify();
            }
            ModuleBean[] items = getAllModules().getModules();
            switch (getModuleSortMode()) {
                case BY_CATEGORY:
                    final Collator collator = Collator.getInstance();
                    class CategoryComparator implements Comparator<Object/*LIBRARY_KEY|BRIDGE_KEY|ModuleBean|String*/> {
                        public int compare(Object o1, Object o2) {
                            // Libraries always put in the last place.
                            if (o1 == LIBRARY_KEY) {
                                if (o2 == LIBRARY_KEY) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if (o2 == LIBRARY_KEY) {
                                    return -1;
                                } else {
                                    // Eager modules come between categories and libraries.
                                    if (o1 == BRIDGE_KEY) {
                                        if (o2 == BRIDGE_KEY) {
                                            return 0;
                                        } else {
                                            return 1;
                                        }
                                    } else if (o2 == BRIDGE_KEY) {
                                        return -1;
                                    }
                                    // #13078: sort modules and categories inline.
                                    String name1;
                                    if (o1 instanceof ModuleBean) {
                                        name1 = ((ModuleBean) o1).getDisplayName();
                                    } else {
                                        name1 = (String) o1;
                                    }
                                    String name2;
                                    if (o2 instanceof ModuleBean) {
                                        name2 = ((ModuleBean) o2).getDisplayName();
                                    } else {
                                        name2 = (String) o2;
                                    }
                                    return collator.compare(name1, name2);
                                }
                            }
                        }
                    }
                    SortedSet<Object> categories = new TreeSet<Object>(new CategoryComparator());
                    for (ModuleBean item : items) {
                        if (item.getJar() == null || item.isAutoload()) {
                            categories.add(LIBRARY_KEY);
                        } else if (item.isEager()) {
                            categories.add(BRIDGE_KEY);
                        } else {
                            String category = item.getCategory();
                            if (category != null) {
                                categories.add(category);
                            } else {
                                categories.add(item);
                            }
                        }
                    }
                    setKeys(categories);
                    break;
                case BY_DISPLAY_NAME:
                    List<ModuleBean> itemsL = new ArrayList<ModuleBean>(Arrays.asList(items));
                    Collections.sort(itemsL, new Comparator<ModuleBean>() {
                        Collator coll = Collator.getInstance();
                        public int compare(ModuleBean m1, ModuleBean m2) {
                            return coll.compare(m1.getDisplayName(), m2.getDisplayName());
                        }
                    });
                    setKeys(itemsL);
                    break;
                case BY_CLUSTER:
                    class ClusterComparator implements Comparator<Object/*NO_CLUSTER_KEY|File*/> {
                        public int compare(Object o1, Object o2) {
                            if (o1 == NO_CLUSTER_KEY) {
                                if (o2 == NO_CLUSTER_KEY) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            } else {
                                if (o2 == NO_CLUSTER_KEY) {
                                    return -1;
                                } else {
                                    return ((File) o1).compareTo((File) o2);
                                }
                            }
                        }
                    }
                    SortedSet<Object> clusters = new TreeSet<Object>(new ClusterComparator());
                    for (ModuleBean item : items) {
                        File cluster = item.getCluster();
                        if (cluster == null) {
                            clusters.add(NO_CLUSTER_KEY);
                        } else {
                            clusters.add(cluster);
                        }
                    }
                    setKeys(clusters);
                    break;
                default:
                    assert false;
            }
        }

        public void addNotify() {
            prefsListener = new PreferenceChangeListener(){
                public void preferenceChange(PreferenceChangeEvent evt) {
                    refreshKeys();
                }
            };
            getPreferences().addPreferenceChangeListener(prefsListener);
            getAllModules().addPropertyChangeListener(this);
            refreshKeys();
        }

        public void removeNotify() {
            if (prefsListener != null) {
                getPreferences().removePreferenceChangeListener(prefsListener);
            }
            getAllModules().removePropertyChangeListener(this);
            ModuleBean[] items = getAllModules().getModules();
            for (int i = 0; i < items.length; i++) {
                items[i].removePropertyChangeListener(this);
            }
        }

        /** Make sure hierarchy lookups get the proper module list first. */
        public Node findChild(String name) {
            getAllModules().waitForModules().waitFinished();
            refreshKeys();
            return super.findChild(name);
        }

        /** Reacts to changes */
        public void propertyChange(PropertyChangeEvent ev) {
            refreshKeys();
        }

        /** Generates node for the ModuleBean key */
        protected Node[] createNodes(Object key) {
            if (key == LIBRARY_KEY) {
                return new Node[] {new LibraryNode()};
            } else if (key == BRIDGE_KEY) {
                return new Node[] {new BridgeNode()};
            } else if (key == NO_CLUSTER_KEY) {
                return new Node[] {new NoClusterNode()};
            } else if (key instanceof String) {
                return new Node[] {new CategoryNode((String) key)};
            } else if (key instanceof File) {
                return new Node[] {new ClusterNode((File) key)};
            } else {
                return new Node[] {new Item((ModuleBean) key)};
            }
        }
    }

    private static abstract class ModuleFolderNode extends AbstractNode implements PropertyChangeListener {

        protected ModuleFolderNode(DirectModuleChildren ch) {
            super(ch);
        }

        public boolean canDestroy() {
            return ModuleNodeUtils.canUninstall(new Node [] { this });
        }

        public void destroy() {
            ModuleNodeUtils.doUninstall(new Node [] { this });
        }

        public boolean canRename() {
            return false;
        }

        public boolean canCut() {
            return false;
        }

        public HelpCtx getHelpCtx() {
            return new HelpCtx(ModuleNode.class);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())) {
                updateDisplayStuff();
            }
        }

        protected void updateDisplayStuff() {
            Boolean b = ModuleNodeUtils.isEnableCandidate(getChildren().getNodes());
            setIconBaseWithExtension(b != null && b.booleanValue() ? MODULE_ITEM_DISABLED_BASE : MODULES_ICON_BASE);
        }

        public Action[] getActions(boolean context) {
            return new Action[] {
                SystemAction.get(ModuleNodeActions.EnableDisableAction.class),
                SystemAction.get(ModuleNodeActions.EnableAllAction.class),
                null,
                SystemAction.get(ModuleNodeActions.UninstallAction.class),
                null,
                SystemAction.get(ModuleNodeActions.SortAction.class),
                null,
                SystemAction.get(PropertiesAction.class),
            };
        }

        protected abstract boolean supportsEnabledProperty();

        protected abstract boolean contains(ModuleBean module);

        /** Creates property.
         */
        protected Sheet createSheet() {
            if (!supportsEnabledProperty()) {
                return super.createSheet();
            }
            Sheet s = Sheet.createDefault();
            Sheet.Set ss = s.get(Sheet.PROPERTIES);

            class EnabledProp extends PropertySupport.ReadWrite<Boolean> implements PropertyChangeListener {
                /** modules in this category */
                private Set<ModuleBean> modules = new HashSet<ModuleBean>();
                public EnabledProp() {
                    super("enabled", Boolean.TYPE, // NOI18N
                            NbBundle.getMessage(ModuleNode.class, "PROP_modules_enabled"),
                            NbBundle.getMessage(ModuleNode.class, "HINT_modules_enabled"));
                    calculateModules();
                    getAllModules().addPropertyChangeListener(WeakListeners.propertyChange(this, getAllModules()));
                }
                public Boolean getValue() {
                    Boolean val = null;
                    for (ModuleBean m : modules) {
                        if (m.isEnabled()) {
                            if (val == null) {
                                val = true;
                            } else if (!val) {
                                // Mixed results.
                                return null;
                            }
                        } else {
                            if (val == null) {
                                val = false;
                            } else if (val) {
                                // Mixed results.
                                return null;
                            }
                        }
                    }
                    return val;
                }
                public void setValue(Boolean val) {
                    getAllModules().pause();
                    for (ModuleBean m : modules) {
                        m.setEnabled(val);
                    }
                    getAllModules().resume();
                }
                public boolean canWrite() {
                    for (ModuleBean m : modules) {
                        if (m.isAutoload() || m.getJar() == null || m.isEager() || m.isProblematic()) {
                            return false;
                        }
                    }
                    return true;
                }
                private void calculateModules() {
                    Set<ModuleBean> modules2 = new HashSet<ModuleBean>();
                    for (ModuleBean testing : getAllModules().getModules()) {
                        if (contains(testing)) {
                            modules2.add(testing);
                            if (!modules.contains(testing)) {
                                testing.addPropertyChangeListener(WeakListeners.propertyChange(this, testing));
                            }
                        }
                    }
                    modules = modules2;
                }
                public void propertyChange(PropertyChangeEvent evt) {
                    // Something changed--list of modules, enabled status of some, etc.
                    ModuleFolderNode.this.firePropertyChange("enabled", null, null); // NOI18N
                    calculateModules();
                }
                private PropertyEditor editor=null;
                public PropertyEditor getPropertyEditor() {
                    //issue 38019, cache the property editor for TTV performance
                    if (editor == null) {
                        editor = super.getPropertyEditor();
                    }
                    return editor;
                }
            }

            ss.put(new EnabledProp());
            return s;
        }

    }

    private static final class LibraryNode extends ModuleFolderNode {
        LibraryNode() {
            super(new LibraryChildren());
            setName("libraries"); // NOI18N
            setDisplayName(NbBundle.getMessage(ModuleNode.class, "LBL_ModuleNode_libraries"));
            setIconBaseWithExtension(MODULES_ICON_BASE);
        }
        protected boolean supportsEnabledProperty() {
            return false;
        }
        protected boolean contains(ModuleBean module) {
            throw new AssertionError();
        }
    }

    private static final class BridgeNode extends ModuleFolderNode {
        BridgeNode() {
            super(new BridgeChildren());
            setName("bridges"); // NOI18N
            setDisplayName(NbBundle.getMessage(ModuleNode.class, "LBL_ModuleNode_bridges"));
            setIconBaseWithExtension(MODULES_ICON_BASE);
        }
        protected boolean supportsEnabledProperty() {
            return false;
        }
        protected boolean contains(ModuleBean module) {
            throw new AssertionError();
        }
    }

    private static final class CategoryNode extends ModuleFolderNode {
        private final String category;
        CategoryNode(String category) {
            super(new CategoryChildren(category));
            this.category = category;
            setName(category); // displayName should be the same
            // #20448: use disabled icon for disabled category
            updateDisplayStuff();
            addPropertyChangeListener(WeakListeners.propertyChange(this, null));
        }
        protected boolean supportsEnabledProperty() {
            return true;
        }
        protected boolean contains(ModuleBean module) {
            return module.getJar() != null &&
                    !module.isAutoload() &&
                    !module.isEager() &&
                    category.equals(module.getCategory());
        }
    }

    private static final class ClusterNode extends ModuleFolderNode {
        private final File cluster;
        ClusterNode(File cluster) {
            super(new ClusterChildren(cluster));
            this.cluster = cluster;
            setName(cluster.getAbsolutePath());
            setDisplayName(cluster.getName());
            updateDisplayStuff();
            addPropertyChangeListener(WeakListeners.propertyChange(this, null));
        }
        protected boolean supportsEnabledProperty() {
            return true;
        }
        protected boolean contains(ModuleBean module) {
            return cluster.equals(module.getCluster());
        }
    }

    private static final class NoClusterNode extends ModuleFolderNode {
        NoClusterNode() {
            super(new NoClusterChildren());
            setName("noCluster"); // NOI18N
            setDisplayName(NbBundle.getMessage(ModuleNode.class, "LBL_ModuleNode_no_cluster"));
            updateDisplayStuff();
            addPropertyChangeListener(WeakListeners.propertyChange(this, null));
        }
        protected boolean supportsEnabledProperty() {
            return true;
        }
        protected boolean contains(ModuleBean module) {
            return module.getCluster() == null;
        }
    }

    private static abstract class DirectModuleChildren extends Children.Keys<ModuleBean> implements PropertyChangeListener {

        protected void addNotify() {
            refreshKeys();
            getAllModules().addPropertyChangeListener(this);
        }

        protected void removeNotify() {
            getAllModules().removePropertyChangeListener(this);
            setKeys(Collections.<ModuleBean>emptySet());
        }

        /** Make sure hierarchy lookups get the proper module list first. */
        public Node findChild(String name) {
            getAllModules().waitForModules().waitFinished();
            refreshKeys();
            return super.findChild(name);
        }

        protected abstract boolean contains(ModuleBean bean);

        private void refreshKeys() {
            List<ModuleBean> keys = new ArrayList<ModuleBean>();
            for (ModuleBean module : getAllModules().getModules()) {
                if (contains(module)) {
                    keys.add(module);
                }
            }
            final Collator collator = Collator.getInstance();
            Collections.sort(keys, new Comparator<ModuleBean>() {
                public int compare(ModuleBean m1, ModuleBean m2) {
                    return collator.compare(m1.getDisplayName(), m2.getDisplayName());
                }
            });
            setKeys(keys);
        }

        protected Node[] createNodes(ModuleBean key) {
            return new Node[] {new Item(key)};
        }

        public void propertyChange(PropertyChangeEvent ev) {
            refreshKeys();
        }

    }

    private static final class CategoryChildren extends DirectModuleChildren {
        private final String category;
        public CategoryChildren(String category) {
            this.category = category;
        }
        protected boolean contains(ModuleBean module) {
            return category.equals(module.getCategory());
        }
    }

    private static final class ClusterChildren extends DirectModuleChildren {
        private final File cluster;
        public ClusterChildren(File cluster) {
            this.cluster = cluster;
        }
        protected boolean contains(ModuleBean module) {
            return cluster.equals(module.getCluster());
        }
    }

    private static final class NoClusterChildren extends DirectModuleChildren {
        public NoClusterChildren() {}
        protected boolean contains(ModuleBean module) {
            return module.getCluster() == null;
        }
    }

    private static final class LibraryChildren extends DirectModuleChildren {
        protected boolean contains(ModuleBean module) {
            return module.getJar() == null || module.isAutoload();
        }
    }

    private static final class BridgeChildren extends DirectModuleChildren {
        protected boolean contains(ModuleBean module) {
            return module.isEager();
        }
    }

    /** Getter for all modules.
     */
    private static ModuleBean.AllModulesBean getAllModules() {
        return ModuleBean.AllModulesBean.getDefault();
    }

    private static ModuleDeleter getModuleDeleter() {
        if (deleter == null) {
            deleter = new ModuleDeleterImpl (); //org.netbeans.modules.modulemanager.ModuleDeleterImpl ();
        }
        return deleter;
    }

}
