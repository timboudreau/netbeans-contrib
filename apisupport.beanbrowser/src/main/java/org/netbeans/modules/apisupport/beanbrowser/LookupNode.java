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
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

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
        "javax.jmi.xmi.XmiReader", // NOI18N
        "javax.jmi.xmi.XmiWriter", // NOI18N
        "javax.naming.spi.InitialContextFactoryBuilder", // NOI18N
        "javax.swing.LookAndFeel", // NOI18N
        "javax.swing.PopupFactory", // NOI18N
        "javax.swing.text.Keymap", // NOI18N
        "org.apache.tools.ant.module.spi.AntLogger", // NOI18N
        "org.apache.tools.ant.module.spi.AutomaticExtraClasspathProvider", // NOI18N
        "org.netbeans.CLIHandler", // NOI18N
        "org.netbeans.ModuleFactory", // NOI18N
        "org.netbeans.api.mdr.DTDProducer", // NOI18N
        "org.netbeans.api.mdr.JMIMapper", // NOI18N
        "org.netbeans.api.mdr.MDRManager", // NOI18N
        "org.netbeans.api.xmi.XMIReaderFactory", // NOI18N
        "org.netbeans.api.xmi.XMIWriterFactory", // NOI18N
        "org.netbeans.api.xmi.sax.XMIConsumerFactory", // NOI18N
        "org.netbeans.api.xmi.sax.XMIProducerFactory", // NOI18N
        "org.netbeans.core.NbTopManager$WindowSystem", // NOI18N
        "org.netbeans.core.modules.TestModuleDeployer", // NOI18N
        "org.netbeans.core.startup.CoreBridge", // NOI18N
        "org.netbeans.core.startup.RunLevel", // NOI18N
        "org.netbeans.lib.editor.hyperlink.HyperlinkProviderManager", // NOI18N
        "org.netbeans.modules.ant.freeform.spi.ProjectNature", // NOI18N
        "org.netbeans.modules.db.spi.sql.editor.SQLEditorProvider", // NOI18N
        "org.netbeans.modules.j2ee.spi.ejbjar.EarProvider", // NOI18N
        "org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider", // NOI18N
        "org.netbeans.modules.j2ee.spi.ejbjar.EjbNodesFactory", // NOI18N
        "org.netbeans.modules.masterfs.providers.AnnotationProvider", // NOI18N
        "org.netbeans.modules.refactoring.spi.ReadOnlyFilesHandler", // NOI18N
        "org.netbeans.modules.refactoring.spi.RefactoringPluginFactory", // NOI18N
        "org.netbeans.modules.versioning.spi.VersioningSystem", // NOI18N
        "org.netbeans.modules.web.spi.webmodule.RequestParametersProvider", // NOI18N
        "org.netbeans.modules.web.spi.webmodule.WebModuleProvider", // NOI18N
        "org.netbeans.modules.websvc.api.client.WsCompileClientEditorSupport", // NOI18N
        "org.netbeans.modules.websvc.api.registry.WebServicesRegistryView", // NOI18N
        "org.netbeans.modules.websvc.api.webservices.WsCompileEditorSupport", // NOI18N
        "org.netbeans.modules.websvc.spi.client.WebServicesClientSupportProvider", // NOI18N
        "org.netbeans.modules.websvc.spi.client.WebServicesClientViewProvider", // NOI18N
        "org.netbeans.modules.websvc.spi.webservices.WebServicesSupportProvider", // NOI18N
        "org.netbeans.modules.websvc.spi.webservices.WebServicesViewProvider", // NOI18N
        "org.netbeans.modules.xml.wsdl.model.spi.ElementFactoryProvider", // NOI18N
        "org.netbeans.spi.editor.mimelookup.Class2LayerFolder", // NOI18N
        "org.netbeans.spi.editor.mimelookup.MimeLookupInitializer", // NOI18N
        "org.netbeans.spi.java.classpath.ClassPathProvider", // NOI18N
        "org.netbeans.spi.java.project.support.ui.PackageRenameHandler", // NOI18N
        "org.netbeans.spi.java.queries.AccessibilityQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.MultipleRootsUnitTestForSourceQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.SourceLevelQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation", // NOI18N
        "org.netbeans.spi.project.FileOwnerQueryImplementation", // NOI18N
        "org.netbeans.spi.project.ProjectFactory", // NOI18N
        "org.netbeans.spi.project.ant.AntArtifactQueryImplementation", // NOI18N
        "org.netbeans.spi.project.libraries.LibraryProvider", // NOI18N
        "org.netbeans.spi.project.support.ant.AntBasedProjectType", // NOI18N
        "org.netbeans.spi.queries.CollocationQueryImplementation", // NOI18N
        "org.netbeans.spi.queries.FileBuiltQueryImplementation", // NOI18N
        "org.netbeans.spi.queries.SharabilityQueryImplementation", // NOI18N
        "org.netbeans.spi.queries.VisibilityQueryImplementation", // NOI18N
        "org.netbeans.swing.menus.spi.MenuTreeModel", // NOI18N
        "org.openide.DialogDisplayer", // NOI18N
        "org.openide.ErrorManager", // NOI18N
        "org.openide.LifecycleManager", // NOI18N
        "org.openide.ServiceType", // NOI18N
        "org.openide.ServiceType$Registry", // NOI18N
        "org.openide.actions.ActionManager", // NOI18N
        "org.openide.awt.HtmlBrowser$URLDisplayer", // NOI18N
        "org.openide.awt.StatusDisplayer", // NOI18N
        "org.openide.awt.StatusLineElementProvider", // NOI18N
        "org.openide.execution.ExecutionEngine", // NOI18N
        "org.openide.execution.ScriptType", // NOI18N
        "org.openide.filesystems.MIMEResolver", // NOI18N
        "org.openide.filesystems.Repository", // NOI18N
        "org.openide.filesystems.URLMapper", // NOI18N
        "org.openide.loaders.DataLoaderPool", // NOI18N
        "org.openide.loaders.Environment$Provider", // NOI18N
        "org.openide.loaders.FolderRenameHandler", // NOI18N
        "org.openide.loaders.RepositoryNodeFactory", // NOI18N
        "org.openide.modules.InstalledFileLocator", // NOI18N
        "org.openide.nodes.NodeOperation", // NOI18N
        "org.openide.options.SystemOption", // NOI18N
        "org.openide.text.AnnotationProvider", // NOI18N
        "org.openide.util.ContextGlobalProvider", // NOI18N
        "org.openide.util.Lookup", // NOI18N
        "org.openide.util.datatransfer.ExClipboard$Convertor", // NOI18N
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
    public static Node globalLookupNode() {
        Node n = new LookupNode(Lookup.getDefault(), globalClazzes());
        n.setDisplayName("Global Lookup");
        n.setShortDescription("The contents of Lookup.getDefault().");
        return n;
    }

    /**
     * Create a node displaying default action lookup.
     */
    public static Node actionsGlobalContextLookupNode() {
        Node n = new LookupNode(ignoreBbNodes(Utilities.actionsGlobalContext()), cookieClazzes());
        n.setDisplayName("Action Lookup");
        n.setShortDescription("The contents of Utilities.actionsGlobalContext().");
        return n;
    }
    
    /**
     * Create a node displaying the specified lookup.
     * Will start off showing an Object query, i.e. all items, and probe for common cookies.
     */
    public static Node localLookupNode(Lookup l) {
        Node n = new LookupNode(l, cookieClazzes());
        n.setDisplayName("Local Lookup");
        n.setShortDescription("The contents of a local Lookup.");
        return n;
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
    
    /** Hack to exclude BB-related nodes from lookup view. */
    static final class BbMarker implements Node.Cookie {}
    private static boolean containsBbNode(Lookup context) {
        Iterator/*<Node>*/ nodes = context.lookup(new Lookup.Template(Node.class)).allInstances().iterator();
        while (nodes.hasNext()) {
            for (Node n = (Node) nodes.next(); n != null; n = n.getParentNode()) {
                if (n.getCookie(BbMarker.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    private static Lookup ignoreBbNodes(final Lookup orig) {
        class Proxy extends AbstractLookup implements LookupListener {
            private final InstanceContent content;
            private Collection copy = Collections.EMPTY_SET;
            private final Lookup.Result master = orig.lookup(new Lookup.Template(Object.class));
            public Proxy() {
                this(new InstanceContent());
            }
            private Proxy(InstanceContent content) {
                super(content);
                this.content = content;
                Lookup.Result r = orig.lookup(new Lookup.Template(Node.class));
                r.addLookupListener((LookupListener) WeakListeners.create(LookupListener.class, this, r));
                resultChanged(null);
            }
            public void resultChanged(LookupEvent ignore) {
                if (!containsBbNode(orig)) {
                    copy = master.allInstances();
                }
                content.set(copy, null);
            }
        }
        return new Proxy();
    }

}
