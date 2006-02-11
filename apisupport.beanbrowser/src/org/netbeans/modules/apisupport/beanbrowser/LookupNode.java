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

import java.awt.EventQueue;
import java.io.IOException;
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
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Mutex;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

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
        // XXX why doesn't the override work?
        setDisplayName("Global Lookup");
        setShortDescription("The contents of Lookup.getDefault().");
    }
    
    /**
     * Create a node displaying the specified lookup.
     * Will start off showing an Object query, i.e. all items, and probe for common cookies.
     */
    public LookupNode(Lookup l) {
        this(l, new Class[] {Object.class});
        Class[] cookies = cookieClazzes();
        for (int i = 0; i < cookies.length; i++) {
            // Just probing the cookies should get them to appear in the Object query.
            l.lookup(cookies[i]);
        }
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
        setDisplayName("Lookup: " + clazz.getName().replace('$', '.'));
        setShortDescription("The contents of a Lookup restricted to some subtype.");
    }
    
    public Node cloneNode() {
        return new LookupNode(l, clazz, clazzes);
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(NewAction.class),
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(OpenLocalExplorerAction.class),
            null,
            SystemAction.get(ToolsAction.class),
        };
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser.lookup");
    }
    
    public NewType[] getNewTypes() {
        return new NewType[] {new NewType() {
            public String getName() {
                return "Superclass/interface";
            }
            public HelpCtx getHelpCtx() {
                return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser.lookup");
            }
            public void create() throws IOException {
                NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Superclass or interface:", "Add New Lookup Class");
                if (DialogDisplayer.getDefault().notify(desc) == NotifyDescriptor.OK_OPTION) {
                    try {
                        Class clazz = ((ClassLoader)Lookup.getDefault().lookup(ClassLoader.class)).loadClass(desc.getInputText());
                        clazzes.add(clazz);
                    } catch (ClassNotFoundException cnfe) {
                        IOException ioe = new IOException(cnfe.toString());
                        ErrorManager.getDefault().annotate(ioe, cnfe);
                        throw ioe;
                    }
                }
            }
        }};
    }
    
    public boolean canDestroy() {
        return clazz != Object.class;
    }
    public void destroy() throws IOException {
        clazzes.remove(clazz);
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
        
        public void add(Class c) {
            assert EventQueue.isDispatchThread();
            if (clazzes.add(c)) {
                fireChange();
            }
        }
        
        public void remove(Class c) {
            assert EventQueue.isDispatchThread();
            if (clazzes.remove(c)) {
                fireChange();
            }
        }
        
        public void addAll(Class c) {
            assert EventQueue.isDispatchThread();
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
        
        public Collection/*<Class>*/ getSubtypes(Class c) {
            assert EventQueue.isDispatchThread();
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    Class c1 = (Class)o1;
                    Class c2 = (Class)o2;
                    return c1.getName().compareTo(c2.getName());
                }
            };
            SortedSet/*<Class>*/ s = new TreeSet(comp);
            Iterator it = clazzes.iterator();
            while (it.hasNext()) {
                Class c2 = (Class)it.next();
                if (c2 == c) {
                    continue;
                }
                /*
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
                    // Direct subinterfaces, and classes directly implementing it.
                    if (Arrays.asList(c2.getInterfaces()).contains(c)) {
                        s.add(c2);
                    }
                } else {
                    // Direct subclasses.
                    if (c2.getSuperclass() == c) {
                        s.add(c2);
                    }
                }
                 */
                if (c.isAssignableFrom(c2)) {
                    s.add(c2);
                }
            }
            return s;
        }
        
    }
    
    private static final class LookupChildren extends Children.Keys/*<Class|INSTANCES_KEY>*/ implements ChangeListener {
        
        private static final Object INSTANCES_KEY = "instances"; // NOI18N
        
        private final Lookup l;
        private final Class clazz;
        private final ClassSet clazzes;
        
        public LookupChildren(Lookup l, Class clazz, ClassSet clazzes) {
            this.l = l;
            this.clazz = clazz;
            this.clazzes = clazzes;
        }
        
        private void updateKeys() {
            List l = new ArrayList();
            l.add(INSTANCES_KEY);
            l.addAll(clazzes.getSubtypes(clazz));
            setKeys(l);
        }
        
        protected void addNotify() {
            Mutex.EVENT.writeAccess(new Runnable() {
                public void run() {
                    updateKeys();
                }
            });
            clazzes.addChangeListener(this);
        }
        
        protected void removeNotify() {
            clazzes.removeChangeListener(this);
            setKeys(Collections.EMPTY_SET);
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof Class) {
                return new Node[] {new LookupNode(l, (Class)key, clazzes)};
            } else {
                return new Node[] {new LookupResultNode(l, clazz, clazzes)};
            }
        }
        
        public void stateChanged(ChangeEvent e) {
            updateKeys();
        }
        
    }
    
    private static final class LookupResultNode extends AbstractNode {
        
        private final Lookup l;
        private final Class clazz;
        private final ClassSet clazzes;
        
        public LookupResultNode(Lookup l, Class clazz, ClassSet clazzes) {
            super(new LookupResultChildren(l, clazz, clazzes));
            this.l = l;
            this.clazz = clazz;
            this.clazzes = clazzes;
            setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
            setName(clazz.getName());
            setDisplayName("All instances...");
            setShortDescription("A lookup query on " + clazz.getName().replace('$', '.'));
        }
        
        public Action[] getActions(boolean context) {
            return new Action[] {
                SystemAction.get(OpenLocalExplorerAction.class),
                null,
                SystemAction.get(ToolsAction.class),
            };
        }
        
        public HelpCtx getHelpCtx() {
            return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser.lookup");
        }
        
        public Node cloneNode() {
            return new LookupResultNode(l, clazz, clazzes);
        }
        
        private static final class LookupResultHandle implements Node.Handle {
            private static final long serialVersionUID = 45626587265263L;
            private final Class clazz;
            public LookupResultHandle(Class clazz) {
                this.clazz = clazz;
            }
            public Node getNode() throws IOException {
                return new LookupResultNode(Lookup.getDefault(), clazz, new ClassSet(new Class[] {clazz}));
            }
        }
        public Node.Handle getHandle() {
            if (l == Lookup.getDefault()) {
                return new LookupResultHandle(clazz);
            } else {
                return super.getHandle();
            }
        }
        
        protected Sheet createSheet() {
            Sheet s = super.createSheet();
            if (clazz == Object.class) {
                // Too slow, and useless anyway.
                return s;
            }
            Sheet.Set ss = Sheet.createPropertiesSet();
            class SampleProp extends PropertySupport.ReadWrite {
                Object value = null;
                public SampleProp() {
                    super("prop", Object.class, // NOI18N
                            "Sample Property",
                            "An example property of type " + clazz.getName());
                    this.setValue("superClass", clazz); // NOI18N
                    this.setValue("nullValue", "<null>");
                    this.setValue("lookup", l); // NOI18N
                }
                public Object getValue() {
                    return value;
                }
                public void setValue(Object value) {
                    this.value = value;
                    LookupResultNode.this.firePropertyChange("id", null, null); // NOI18N
                }
            }
            final SampleProp prop = new SampleProp();
            ss.put(prop);
            class IDProp extends PropertySupport.ReadOnly {
                public IDProp() {
                    super("id", String.class, // NOI18N
                            "Lookup ID",
                            "The ID of the lookup item (if any) matching the current property selection.");
                }
                public Object getValue() {
                    Object value = prop.getValue();
                    if (value != null) {
                        Iterator it = l.lookup(new Lookup.Template(clazz, null, value)).
                                allItems().iterator();
                        if (it.hasNext()) {
                            return ((Lookup.Item)it.next()).getId();
                        } else {
                            return "<no ID>";
                        }
                    } else {
                        return "<null>";
                    }
                }
            }
            ss.put(new IDProp());
            s.put(ss);
            return s;
        }
        
    }
    
    // key class: Lookup.Item
    private static final class LookupResultChildren extends Children.Keys/*<Lookup.Item|KEY_PLEASE_WAIT>*/ implements LookupListener {
        
        private static final Object KEY_PLEASE_WAIT = "wait"; // NOI18N
        
        private final Lookup l;
        private final Class clazz;
        private final ClassSet clazzes;
        private Lookup.Result result;
        
        public LookupResultChildren(Lookup l, Class clazz, ClassSet clazzes) {
            this.l = l;
            this.clazz = clazz;
            this.clazzes = clazzes;
        }
        
        private void updateKeys() {
            // Do not run in EQ, too slow.
            setKeys(Collections.singleton(KEY_PLEASE_WAIT));
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    setKeys(result.allItems());
                }
            });
        }
        
        protected void addNotify() {
            result = l.lookup(new Lookup.Template(clazz));
            result.addLookupListener(this);
            updateKeys();
        }
        
        protected void removeNotify() {
            result.removeLookupListener(this);
            result = null;
            setKeys(Collections.EMPTY_SET);
        }
        
        protected Node[] createNodes(Object key) {
            if (key == KEY_PLEASE_WAIT) {
                return new Node[] {PropSetKids.makePlainNode("Please wait...")};
            }
            Lookup.Item item = (Lookup.Item)key;
            Object o = item.getInstance();
            if (o != null) {
                Node n = PropSetKids.makeObjectNode(o);
                n.setShortDescription("ID='" + item.getId() + "' " + n.getShortDescription());
                // Also try to list lookup results for anything it implements.
                final Class c = o.getClass();
                // Calling setKeys from inside createNodes seems to be bad.
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        clazzes.addAll(c);
                    }
                });
                return new Node[] {n};
            } else {
                return new Node[] {PropSetKids.makePlainNode("<cancelled lookup item>")};
            }
        }
        
        public void resultChanged(LookupEvent ev) {
            updateKeys();
        }
        
    }
    
}
