/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;

public final class LookupNode extends AbstractNode {
    
    private static Class[] GLOBAL_CLAZZES;
    private static Class[] COOKIE_CLAZZES;
    
    // See #24609.
    // This is just compiled from a list of META-INF/services/ dirs in sources.
    // (for i in */{,*/,*/*/}src/META-INF/services/*; do if [ -f $i ]; then echo "        "'"'"`basename $i`"'"'", // NOI18N"; fi; done) | sort | uniq
    // trimmed to deprecated, private, or experimental interfaces.
    // Plus some custom additions like ServiceType, SystemOption, HelpSet.
    private static final String[] GLOBAL_SERVICE_NAMES = {
        "java.awt.datatransfer.Clipboard", // NOI18N
        "java.net.URLStreamHandlerFactory", // NOI18N
        "javax.help.HelpSet", // NOI18N
        "javax.naming.spi.InitialContextFactoryBuilder", // NOI18N
        "javax.swing.text.Keymap", // NOI18N
        "org.netbeans.CLIHandler", // NOI18N
        "org.netbeans.core.modules.TestModuleDeployer", // NOI18N
        "org.netbeans.spi.registry.RootContext", // NOI18N
        "org.openide.awt.HtmlBrowser$URLDisplayer", // NOI18N
        "org.openide.awt.StatusDisplayer", // NOI18N
        "org.openide.compiler.CompilationEngine", // NOI18N
        "org.openide.DialogDisplayer", // NOI18N
        "org.openide.ErrorManager", // NOI18N
        "org.openide.execution.ExecutionEngine", // NOI18N
        "org.openide.filesystems.Repository", // NOI18N
        "org.openide.LifecycleManager", // NOI18N
        "org.openide.loaders.DataLoaderPool", // NOI18N
        "org.openide.loaders.Environment$Provider", // NOI18N
        "org.openide.loaders.RepositoryNodeFactory", // NOI18N
        "org.openide.modules.InstalledFileLocator", // NOI18N
        "org.openide.nodes.NodeOperation", // NOI18N
        "org.openide.options.SystemOption", // NOI18N
        "org.openide.ServiceType", // NOI18N
        "org.openide.ServiceType$Registry", // NOI18N
        "org.openide.util.ContextGlobalProvider", // NOI18N
        "org.openide.util.datatransfer.ExClipboard", // NOI18N
        "org.openide.util.Lookup", // NOI18N
        "org.openide.windows.IOProvider", // NOI18N
        "org.openide.windows.TopComponent$Registry", // NOI18N
        "org.openide.windows.WindowManager", // NOI18N
        "org.openide.xml.EntityCatalog", // NOI18N
    };
    
    private static Class[] globalClazzes() {
        if (GLOBAL_CLAZZES == null) {
            Set clazzes = new HashSet();
            ClassLoader l = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            for (int i = 0; i < GLOBAL_SERVICE_NAMES.length; i++) {
                try {
                    clazzes.add(Class.forName(GLOBAL_SERVICE_NAMES[i], false, l));
                } catch (ClassNotFoundException e) {
                    // OK, maybe missing for some reason. Ignore.
                }
            }
            GLOBAL_CLAZZES = (Class[])clazzes.toArray(new Class[clazzes.size()]);
        }
        return GLOBAL_CLAZZES;
    }
    
    private static Class[] cookieClazzes() {
        if (COOKIE_CLAZZES == null) {
            // #29851: just Object may not be enough, because the query may
            // not return everything! Everything in the CookieSet is probably
            // there, but certainly not accurate if you override getCookie.
            Class[] clazzes = CookieClassList.getCookieClasses();
            COOKIE_CLAZZES = new Class[clazzes.length + 1];
            COOKIE_CLAZZES[0] = Object.class;
            System.arraycopy(clazzes, 0, COOKIE_CLAZZES, 1, clazzes.length);
        }
        return COOKIE_CLAZZES;
    }
    
    /**
     * Create a node displaying default lookup.
     * Will start off showing standard singletons.
     */
    public LookupNode() {
        this(Lookup.getDefault(), globalClazzes());
        setDisplayName("Global Lookup");
        setShortDescription("The contents of Lookup.getDefault().");
    }
    
    /**
     * Create a node displaying the specified lookup.
     * Will start off showing an Object query, i.e. all items, and probe for common cookies.
     */
    public LookupNode(Lookup l) {
        this(l, cookieClazzes());
        setDisplayName("Local Lookup");
        setShortDescription("The contents of a local Lookup.");
    }
    
    private LookupNode(Lookup l, Class[] initClazzes) {
        this(l, Object.class, new ClassSet(initClazzes));
    }
    
    private final Lookup l;
    private final Class clazz;
    private final ClassSet clazzes;
    
    LookupNode(Lookup l, Class clazz, ClassSet clazzes) {
        super(new LookupChildren(l, clazz, clazzes));
        this.l = l;
        this.clazz = clazz;
        this.clazzes = clazzes;
        setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
        setName("LookupNode:" + clazz.getName()); // NOI18N
        setDisplayName((clazz.isInterface() ? "interface " : "class ") + clazz.getName().replace('$', '.'));
    }
    
    public Action[] getActions(boolean context) {
        if (clazz == Object.class) {
            return new Action[] {
                new AddClassAction(),
            };
        } else {
            return new Action[0];
        }
    }
    
    private final class AddClassAction extends AbstractAction {
        public AddClassAction() {
            super("Add Superclass/interface");
        }
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Superclass or interface:", "Add New Lookup Class");
            if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                try {
                    Class clazz = ((ClassLoader) Lookup.getDefault().lookup(ClassLoader.class)).loadClass(desc.getInputText());
                    clazzes.add(clazz);
                } catch (ClassNotFoundException cnfe) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, cnfe);
                }
            }
        }
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser.lookup");
    }
    
    private static final class ClassSet {
        
        private final Set/*<Class>*/ clazzes;
        private final List/*<ChangeListener>*/ listeners;
        
        public ClassSet(Class[] initClazzes) {
            clazzes = new HashSet(Arrays.asList(initClazzes));
            listeners = new ArrayList();
        }
        
        public void addChangeListener(ChangeListener l) {
            listeners.add(l);
        }
        
        public void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
        }
        
        private void fireChange() {
            ChangeEvent ev = new ChangeEvent(this);
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                ((ChangeListener)it.next()).stateChanged(ev);
            }
        }
        
        public synchronized void add(Class c) {
            if (clazzes.add(c)) {
                fireChange();
            }
        }
        
        public synchronized void remove(Class c) {
            if (clazzes.remove(c)) {
                fireChange();
            }
        }
        
        public synchronized void addAll(Class c) {
            if (addAll0(c)) {
                fireChange();
            }
        }
        
        private boolean addAll0(Class clazz) {
            boolean success = clazzes.add(clazz);
            Class s = clazz.getSuperclass();
            if (s != null) {
                success |= addAll0(s);
            }
            Class[] is = clazz.getInterfaces();
            for (int i = 0; i < is.length; i++) {
                success |= addAll0(is[i]);
            }
            return success;
        }
        
        public synchronized Collection/*<Class>*/ getSubtypes(Class c) {
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    Class c1 = (Class)o1;
                    Class c2 = (Class)o2;
                    if (c1.isInterface() && !c2.isInterface()) {
                        return 1;
                    } else if (!c1.isInterface() && c2.isInterface()) {
                        return -1;
                    } else {
                        return c1.getName().compareTo(c2.getName());
                    }
                }
            };
            SortedSet/*<Class>*/ s = new TreeSet(comp);
            Iterator it = clazzes.iterator();
            while (it.hasNext()) {
                Class c2 = (Class)it.next();
                if (c2 == c) {
                    continue;
                }
                if (c == Object.class) {
                    // All top-level classes and interfaces.
                    if (c2.isInterface()) {
                        if (c2.getInterfaces().length == 0) {
                            s.add(c2);
                        }
                    } else {
                        if (c2.getSuperclass() == c) {
                            s.add(c2);
                        }
                    }
                } else if (c.isInterface()) {
                    // Direct subinterfaces and directly implementing classes.
                    if (Arrays.asList(c2.getInterfaces()).contains(c)) {
                        s.add(c2);
                    }
                } else {
                    // Direct subclasses.
                    if (c2.getSuperclass() == c) {
                        s.add(c2);
                    }
                }
            }
            return s;
        }
        
    }
    
    private static final class LookupChildren extends Children.Keys/*<Class|Lookup.Item>*/ implements ChangeListener, LookupListener {
        
        private static final Object KEY_PLEASE_WAIT = "wait"; // NOI18N
        private static final RequestProcessor RP = new RequestProcessor(LookupChildren.class.getName());
        
        private final Lookup l;
        private final Class clazz;
        private final ClassSet clazzes;
        private Lookup.Result result;
        
        public LookupChildren(Lookup l, Class clazz, ClassSet clazzes) {
            this.l = l;
            this.clazz = clazz;
            this.clazzes = clazzes;
        }
        
        private void updateKeys() {
            RP.post(new Runnable() {
                public void run() {
                    List keys = new ArrayList();
                    Iterator it = result.allItems().iterator();
                    while (it.hasNext()) {
                        Lookup.Item item = (Item) it.next();
                        Object o = item.getInstance();
                        if (o == null) { // dead item, rare but possible
                            continue;
                        }
                        Class c = o.getClass();
                        if (c == clazz) {
                            keys.add(item);
                        }
                        clazzes.addAll(c);
                    }
                    it = clazzes.getSubtypes(clazz).iterator();
                    while (it.hasNext()) {
                        Class c = (Class) it.next();
                        clazzes.addAll(c);
                        if (!l.lookup(new Lookup.Template(c)).allItems().isEmpty()) {
                            keys.add(c);
                        }
                    }
                    setKeys(keys);
                }
            });
        }
        
        protected void addNotify() {
            result = l.lookup(new Lookup.Template(clazz));
            result.addLookupListener(this);
            clazzes.addChangeListener(this);
            setKeys(Collections.singleton(KEY_PLEASE_WAIT));
            updateKeys();
        }
        
        protected void removeNotify() {
            clazzes.removeChangeListener(this);
            result.removeLookupListener(this);
            setKeys(Collections.EMPTY_SET);
        }
        
        protected Node[] createNodes(Object key) {
            if (key == KEY_PLEASE_WAIT) {
                return new Node[] {PropSetKids.makePlainNode("Please wait...")};
            } else if (key instanceof Class) {
                return new Node[] {new LookupNode(l, (Class) key, clazzes)};
            } else {
                Lookup.Item item = (Item) key;
                Node n = PropSetKids.makeObjectNode(item.getInstance());
                n.setShortDescription("Lookup item ID: " + item.getId());
                return new Node[] {n};
            }
        }
        
        public void stateChanged(ChangeEvent e) {
            updateKeys();
        }
        
        public void resultChanged(LookupEvent ev) {
            updateKeys();
        }
        
    }
    
}
