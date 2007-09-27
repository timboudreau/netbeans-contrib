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

package org.netbeans.modules.sfsbrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays System FileSystem and META-INF/services Browser
 * window.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
final class SFSBrowserTopComponent extends TopComponent {

    private static SFSBrowserTopComponent instance;

    private String platform = null;

    private BeanTreeView sfsView;

    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/sfsbrowser/sfs.gif";

    private static final String PREFERRED_ID = "SFSBrowserTopComponent";

    private SFSBrowserTopComponent() {
        setName(NbBundle.getMessage(SFSBrowserTopComponent.class, "CTL_SFSBrowserTopComponent"));
        setToolTipText(NbBundle.getMessage(SFSBrowserTopComponent.class, "HINT_SFSBrowserTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));

        setLayout(new BorderLayout());

        String netbeansHome = System.getProperty("netbeans.home");
        if (netbeansHome != null) {
            File netbeansHomeDir = new File(netbeansHome);
            if (netbeansHomeDir.exists() && netbeansHomeDir.isDirectory()) {
                platform = netbeansHomeDir.getName();
            }
        }

        ExplorerManagerPanel sfsPanel = new ExplorerManagerPanel("System FileSystem");
        sfsView = new BeanTreeView();
        sfsView.setRootVisible(false);
        try {
            sfsPanel.getExplorerManager().setRootContext(
                    new SFSNode(DataObject.find(Repository.getDefault().getDefaultFileSystem().getRoot()).getNodeDelegate(),
                    platform));
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        sfsPanel.add(sfsView, BorderLayout.CENTER);

        ExplorerManagerPanel metaInfServicesPanel = new ExplorerManagerPanel("META-INF/services"); // NOI18N
        BeanTreeView metaInfServicesView = new BeanTreeView();
        metaInfServicesView.setRootVisible(false);
        metaInfServicesPanel.getExplorerManager().setRootContext(new AbstractNode(new MetaInfServicesChildren(platform)));
        metaInfServicesPanel.add(metaInfServicesView, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, sfsPanel, metaInfServicesPanel) {
            public void addNotify() {
                super.addNotify();
                this.setDividerLocation(400);
            }
        };
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);
    }

    private class ExplorerManagerPanel extends JPanel implements ExplorerManager.Provider {
        private final ExplorerManager manager = new ExplorerManager();

        ExplorerManagerPanel(String label) {
            super(new BorderLayout());

            add(new JLabel(label), BorderLayout.NORTH);
        }

        public ExplorerManager getExplorerManager() {
            return manager;
        }
    }

    public String getPlatform() {
        return platform;
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized SFSBrowserTopComponent getDefault() {
        if (instance == null) {
            instance = new SFSBrowserTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the SFSBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized SFSBrowserTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find SFSBrowser component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof SFSBrowserTopComponent) {
            return (SFSBrowserTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public void componentOpened() {
        // TODO add custom code on component opening
    }

    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return SFSBrowserTopComponent.getDefault();
        }
    }

    private void selectNode(String path) {
        ExplorerManager explorerManager = ExplorerManager.find(sfsView);
        if (explorerManager != null) {
            try {
                Node node = NodeOp.findPath(explorerManager.getRootContext(), path.split("/"));
                try {
                    explorerManager.setSelectedNodes(new Node[] {node});
                } catch (PropertyVetoException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            } catch (NodeNotFoundException ex) {
                StatusDisplayer.getDefault().setStatusText("Could not select " + path);
            }
        }
    }

    private static void select(String path) {
        SFSBrowserTopComponent sfsBrowserTopComponent = findInstance();
        sfsBrowserTopComponent.selectNode(path);
    }

    private static class NonRecursiveFolderLookup extends FolderLookup {
        public NonRecursiveFolderLookup(DataObject.Container df) {
            super(df);
        }

        protected InstanceCookie acceptFolder(DataFolder df) {
            return null;
        }

        protected InstanceCookie acceptContainer(DataObject.Container container) {
            return null;
        }
    }

    private static void collectActions(Node node, List<Action> actions, String platform, FileObject root) {
        DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null && fileObject != root) {
                FileObject specificReleaseActionInstanceFolder =
                        root.getFileObject(
                        "org.netbeans.modules.sfsbrowser" // NOI18N
                        + "/"
                        + platform
                        + "/"
                        + fileObject.getPath());
                if (specificReleaseActionInstanceFolder != null) {
                    DataFolder dataFolder = DataFolder.findFolder(specificReleaseActionInstanceFolder);
                    FolderLookup fl = new NonRecursiveFolderLookup(dataFolder);
                    Lookup.Template template = new Lookup.Template(Action.class);
                    Lookup.Result result = fl.getLookup().lookup(template);
                    actions.addAll(result.allInstances());
                }
                FileObject genericActionInstanceFolder =
                        root.getFileObject(
                        "org.netbeans.modules.sfsbrowser" // NOI18N
                        + "/"
                        + fileObject.getPath());
                if (genericActionInstanceFolder != null) {
                    DataFolder dataFolder = DataFolder.findFolder(genericActionInstanceFolder);
                    FolderLookup fl = new NonRecursiveFolderLookup(dataFolder);
                    Lookup.Template template = new Lookup.Template(Action.class);
                    Lookup.Result result = fl.getLookup().lookup(template);
                    actions.addAll(result.allInstances());
                }
                FileObject parentFileObject = fileObject.getParent();
                if (parentFileObject != null && parentFileObject != root) {
                    DataObject parentDataObject;
                    try {
                        parentDataObject = DataObject.find(parentFileObject);
                        if (parentDataObject != null) {
                            Node parentNode = parentDataObject.getNodeDelegate();
                            if (parentNode != null) {
                                collectActions(parentNode, actions, platform, root);
                            }
                        }
                    } catch (DataObjectNotFoundException ex) {
                        // too bad
                    }
                }
            }
        }
    }

    private static Node[] EMPTY_NODE_ARRAY = new Node[0];
    private static Action[] EMPTY_ACTIONS = new Action[0];

    private static class SFSNode extends FilterNode {
        private String platform;
        private Action[] actions;

        SFSNode(Node node, String platform) {
            super(node, new SFSNodeChildren(node, platform));
            this.platform = platform;
        }

        public Action[] getActions(boolean context) {
            if (!context) {
                if (actions == null) {
                    Node node = SFSNode.this.getOriginal();
                    return getActions(node);
                }
                return actions;
            }
            return EMPTY_ACTIONS;
        }

        public Node getOriginal() {
            return super.getOriginal();
        }

        private Action[] getActions(Node node) {
            List<Action> actions = new LinkedList<Action>();
            MultiFileSystem multiFileSystem = (MultiFileSystem) Repository.getDefault().getDefaultFileSystem();
            FileObject root = multiFileSystem.getRoot();
            collectActions(node, actions, platform, root);
            DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null) {
                    URL url = null;
                    if (fileObject.getExt().equals("instance")) {
                        url = getURL("http://wiki.netbeans.org/wiki/view/DevFaqInstanceDataObject"); // NOI18N
                        if (url != null) {
                            actions.add(
                                    new ShowURLActionFactory.ShowURLAction(
                                        "FAQ on .instance files",
                                        url)); // TODO cache this
                        }
                    } else if (fileObject.getExt().equals("settings")) {
                        url = getURL("http://wiki.netbeans.org/wiki/view/DevFaqDotSettingsFiles"); // NOI18N
                        if (url != null) {
                            actions.add(
                                    new ShowURLActionFactory.ShowURLAction(
                                        "FAQ on .settings files",
                                        url)); // TODO cache this
                        }
                    } else if (fileObject.getExt().equals("shadow")) {
                        url = getURL("http://wiki.netbeans.org/wiki/view/DevFaqDotShadowFiles"); // NOI18N
                        if (url != null) {
                            actions.add(
                                    new ShowURLActionFactory.ShowURLAction(
                                        "FAQ on .settings files",
                                        url)); // TODO cache this
                        }

                        String originalFile = String.valueOf(fileObject.getAttribute("originalFile"));
                        if (originalFile != null && originalFile.endsWith(".instance")) {
                            final String originalFileSansExt = originalFile.substring(0, originalFile.lastIndexOf(".instance"));
                            actions.add(new AbstractAction("Go to original file " + originalFileSansExt) {
                                public void actionPerformed(ActionEvent e) {
                                    select(originalFileSansExt);
                                }
                            });
                        }
                    }
                    List delegates = getDelegates(multiFileSystem, fileObject);
                    if (delegates.size() > 0) {
                        FileObject delegateFileObject = (FileObject) delegates.get(0);
                        if (delegateFileObject.isValid()) {
                            try {
                                DataObject delegateDataObject = DataObject.find(delegateFileObject);
                                if (delegateDataObject != null &&
                                        (delegateDataObject.getCookie(OpenCookie.class) != null ||
                                         delegateDataObject.getCookie(EditorCookie.class) != null
                                        )) {
                                    try {
                                        actions.add(new OpenDelegateAction(delegateFileObject,
                                                delegateFileObject.getFileSystem().getDisplayName()));
                                    }
                                    catch (FileStateInvalidException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            } catch (DataObjectNotFoundException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
            }
            URL url = getURL("http://www.netbeans.org/download/dev/javadoc/org-openide-filesystems/org/openide/filesystems/doc-files/api.html");
            if (url != null) {
                actions.add(new ShowURLActionFactory.ShowURLAction("FileSystem API Details", url));
            }
            url = null;
            if ("platform6".equals(platform)) {
                url = getURL("http://www.netbeans.org/download/5_5/javadoc/org-openide-filesystems/org/openide/filesystems/XMLFileSystem.html");
            } else if ("platform7".equals(platform)) {
                url = getURL("http://www.netbeans.org/download/6_0/javadoc/org-openide-filesystems/org/openide/filesystems/XMLFileSystem.html");

            }
            if (url != null) {
                actions.add(new ShowURLActionFactory.ShowURLAction("XML FileSystem API Details", url));
            }

            return actions.toArray(EMPTY_ACTIONS);
        }
    }

    private static Comparator<Node> nodeComparator  = new Comparator<Node>() {
        public int compare(Node o1, Node o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    };

    private static class SFSNodeChildren extends Children.Keys {
        private Node node;
        private String platform;
        SFSNodeChildren(Node node, String platform) {
            this.node = node;
            this.platform = platform;
        }

        public void addNotify() {
            List<Object> childrenKeys = new LinkedList<Object>();
            DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null && fileObject != Repository.getDefault().getDefaultFileSystem().getRoot()) {
                    Enumeration attributes = fileObject.getAttributes();
                    while (attributes.hasMoreElements()) {
                        String attribute = (String) attributes.nextElement();
                        if (attribute != null) {
                            Object value = fileObject.getAttribute(attribute);
                            childrenKeys.add(
                                    new Object[] {
                                node,
                                attribute,
                                String.valueOf(value),
                            }
                            );
                        }
                    }
                }
                if (dataObject instanceof DataFolder) {
                    DataFolder dataFolder = (DataFolder)dataObject;
                    DataObject[] childrenDataObjects = dataFolder.getChildren();
                    for (DataObject childDataObject : childrenDataObjects) {
                        childrenKeys.add(childDataObject.getNodeDelegate());
                    }
                }
            }

            setKeys(childrenKeys);
        }

        protected Node[] createNodes(Object key) {
            if (key instanceof Node) {
                Node node = (Node) key;
                return new Node[] {new SFSNode(node, platform)};
            } else if (key instanceof Object[]) {
                Object[] attrDesc = (Object[])key;
                Node attributeNode = new AttributeNode(
                        (Node)attrDesc[0],
                        (String)attrDesc[1],
                        attrDesc[2],
                        platform);
                return new Node[] {attributeNode};
            }
            return EMPTY_NODE_ARRAY;
        }
    }

    private static class AttributeNode extends AbstractNode {
        private Node   of;
        private String attributeName;
        private Object attributeValue;
        private String platform;
        private Action[] actions;

        AttributeNode(Node of, String attributeName, Object attributeValue, String platform) {
            super(Children.LEAF);
            this.of = of;
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
            this.platform = platform;
            setName(attributeName + "=" + attributeValue);
        }

        public Action[] getActions(boolean context) {
            if (!context) {
                if (actions == null) {
                    return getActions(of);
                }
                return actions;
            }
            return EMPTY_ACTIONS;
        }

        private Action[] getActions(Node node) {
            List<Action> actions = new LinkedList<Action>();
            if ("instanceClass".equals(attributeName)) {
                actions.add(new GotoJavaTypeAction(String.valueOf(attributeValue)));
            } else if ("originalFile".equals(attributeName)) {
                String originalFile = String.valueOf(attributeValue);
                if (originalFile != null && originalFile.endsWith(".instance")) {
                    final String originalFileSansExt = originalFile.substring(0, originalFile.lastIndexOf(".instance"));
                    actions.add(new AbstractAction("Go to original file " + originalFileSansExt) {
                        public void actionPerformed(ActionEvent e) {
                            select(originalFileSansExt);
                        }
                    });
                }
            }
            MultiFileSystem multiFileSystem = (MultiFileSystem) Repository.getDefault().getDefaultFileSystem();
            FileObject root = multiFileSystem.getRoot();
            collectActions(node, actions, platform, root);
            return actions.toArray(EMPTY_ACTIONS);
        }
    }

    private static class MetaInfService {
        private String service;
        private List<String> providers;

        MetaInfService(String service) {
            this.service = service;
            providers = new LinkedList<String>();
        }

        String getService() {
            return service;
        }

        void addProvider(String providerInfo) {
            providers.add(providerInfo);
        }

        List<String> getProviders() {
            return Collections.<String>unmodifiableList(providers);
        }
    }
    private static Map<String, String> metaInfServicesAPI = new HashMap<String, String>();
    static {
        metaInfServicesAPI.put("org.openide.filesystems.Repository", "org-openide-filesystems/org/openide/filesystems/Repository.html");
        metaInfServicesAPI.put("org.openide.filesystems.URLMapper", "org-openide-filesystems/org/openide/filesystems/URLMapper.html");
        metaInfServicesAPI.put("org.openide.modules.InstalledFileLocator", "org-openide-modules/org/openide/modules/InstalledFileLocator.html");
        metaInfServicesAPI.put("org.openide.util.Lookup", "org-openide-util/org/openide/util/Lookup.html");
        metaInfServicesAPI.put("org.openide.nodes.NodeOperation", "org-openide-nodes/org/openide/nodes/NodeOperation.html");
        metaInfServicesAPI.put("org.openide.util.ContextGlobalProvider", "org-openide-util/org/openide/util/ContextGlobalProvider.html");
        metaInfServicesAPI.put("org.openide.xml.EntityCatalog", "org-openide-util/org/openide/xml/EntityCatalog.html");
        metaInfServicesAPI.put("org.netbeans.spi.editor.mimelookup.MimeLookupInitializer", "org-netbeans-modules-editor-mimelookup/org/netbeans/spi/editor/mimelookup/MimeLookupInitializer.html");
        metaInfServicesAPI.put("org.netbeans.spi.editor.mimelookup.Class2LayerFolder", "org-netbeans-modules-editor-mimelookup/org/netbeans/spi/editor/mimelookup/Class2LayerFolder.html");
        metaInfServicesAPI.put("org.openide.awt.StatusLineElementProvider", "org-openide-awt/org/openide/awt/StatusLineElementProvider.html");
        metaInfServicesAPI.put("org.openide.ErrorManager", "org-openide-util/org/openide/ErrorManager.html");
        metaInfServicesAPI.put("org.openide.LifecycleManager", "org-openide-util/org/openide/LifecycleManager.html");
        metaInfServicesAPI.put("org.openide.actions.ActionManager", "org-openide-actions/org/openide/actions/ActionManager.html");
        metaInfServicesAPI.put("org.openide.awt.StatusDisplayer", "org-openide-awt/org/openide/awt/StatusDisplayer.html");
        metaInfServicesAPI.put("org.openide.loaders.DataLoaderPool", "org-openide-loaders/org/openide/loaders/DataLoaderPool.html");
        metaInfServicesAPI.put("org.openide.loaders.RepositoryNodeFactory", "org-openide-loaders/org/openide/loaders/RepositoryNodeFactory.html");
        metaInfServicesAPI.put("org.openide.util.datatransfer.ExClipboard", "org-openide-util/org/openide/util/datatransfer/ExClipboard.html");
        metaInfServicesAPI.put("org.openide.windows.IOProvider", "org-openide-io/org/openide/windows/IOProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.CollocationQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/CollocationQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.FileOwnerQueryImplementation", "org-netbeans-modules-projectapi/org/netbeans/spi/project/FileOwnerQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.FileBuiltQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/FileBuiltQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.SharabilityQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/SharabilityQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.classpath.ClassPathProvider", "org-netbeans-api-java/org/netbeans/spi/java/classpath/ClassPathProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/SourceForBinaryQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.ProjectFactory", "org-netbeans-modules-projectapi/org/netbeans/spi/project/ProjectFactory.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.ant.AntArtifactQueryImplementation", "org-netbeans-modules-project-ant/org/netbeans/spi/project/ant/AntArtifactQueryImplementation.html");
        metaInfServicesAPI.put("org.openide.execution.ExecutionEngine", "org-openide-execution/org/openide/execution/ExecutionEngine.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.VisibilityQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/VisibilityQueryImplementation.html");
        metaInfServicesAPI.put("org.apache.tools.ant.module.spi.AntLogger", "org-apache-tools-ant-module/org/apache/tools/ant/module/spi/AntLogger.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.libraries.LibraryProvider", "org-netbeans-modules-project-libraries/org/netbeans/spi/project/libraries/LibraryProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.AccessibilityQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/AccessibilityQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/JavadocForBinaryQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.MultipleRootsUnitTestForSourceQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/MultipleRootsUnitTestForSourceQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.SourceLevelQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/SourceLevelQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/UnitTestForSourceQueryImplementation.html");
        metaInfServicesAPI.put("org.apache.tools.ant.module.spi.AutomaticExtraClasspathProvider", "org-apache-tools-ant-module/org/apache/tools/ant/module/spi/AutomaticExtraClasspathProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.project.support.ui.PackageRenameHandler", "org-netbeans-modules-java-project/org/netbeans/spi/java/project/support/ui/PackageRenameHandler.html");
        metaInfServicesAPI.put("org.openide.loaders.FolderRenameHandler", "org-openide-loaders/org/openide/loaders/FolderRenameHandler.html");
        metaInfServicesAPI.put("org.openide.text.AnnotationProvider", "org-openide-text/org/openide/text/AnnotationProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.support.ant.AntBasedProjectType", "org-netbeans-modules-project-ant/org/netbeans/spi/project/support/ant/AntBasedProjectType.html");
        metaInfServicesAPI.put("org.netbeans.modules.masterfs.providers.AnnotationProvider", "org-netbeans-modules-masterfs/org/netbeans/modules/masterfs/providers/AnnotationProvider.html");
        metaInfServicesAPI.put("org.openide.DialogDisplayer", "org-openide-dialogs/org/openide/DialogDisplayer.html");
        metaInfServicesAPI.put("org.openide.windows.WindowManager", "org-openide-windows/org/openide/windows/WindowManager.html");
    }

    private static Map<String, URL> urlCache = new HashMap<String, URL>();

    static URL getURL(String urlString) {
        if (urlCache.containsKey(urlString)) {
            return urlCache.get(urlString);
        }
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        urlCache.put(urlString, url);
        return url;
    }

    private static class MetaInfServicesChildren extends Children.Keys {
        private List<String> services;
        private String platform;

        MetaInfServicesChildren(String platform) {
            this.platform = platform;
        }

        protected void addNotify() {
            ClassLoader systemClassLoader = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
            if (systemClassLoader != null) {
                java.util.Map<String, MetaInfService> servicesMap = new LinkedHashMap<String, MetaInfService>();
                try {
                    Enumeration<URL> services = systemClassLoader.getResources("META-INF/services"); // NOI18N
                    while (services.hasMoreElements()) {
                        URL service = services.nextElement();
                        URLConnection urlConnection = service.openConnection();
                        if (urlConnection instanceof JarURLConnection) {
                            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                            JarEntry jarEntry = jarURLConnection.getJarEntry();
                            if (jarEntry != null) {
                                JarFile jarFile = jarURLConnection.getJarFile();
                                Enumeration entries = jarFile.entries();
                                while (entries.hasMoreElements()) {
                                    JarEntry entry = (JarEntry) entries.nextElement();
                                    if (entry.getName().startsWith("META-INF/services/") && !entry.isDirectory()) { // NOI18N
                                        //sb.append(entry.getName().substring("META-INF/services/".length()) + "\n");
                                        String serviceClassName = entry.getName().substring("META-INF/services/".length());
                                        MetaInfService metaInfService = servicesMap.get(serviceClassName);
                                        if (metaInfService == null) {
                                            metaInfService = new MetaInfService(serviceClassName);
                                            servicesMap.put(serviceClassName, metaInfService);
                                        }
                                        InputStream inputStream = jarFile.getInputStream(entry);
                                        if (inputStream != null) {
                                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                            String aLine;
                                            while ((aLine = bufferedReader.readLine()) != null) {
                                                if (aLine.trim().length() != 0) {
                                                    if (aLine.startsWith("#")) {
                                                        if (!aLine.startsWith("#position=")) {
                                                            continue;
                                                        }
                                                    }
                                                    metaInfService.addProvider(aLine.trim());
                                                }
                                            }
                                            bufferedReader.close();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                }
                setKeys(servicesMap.values());
            }
        }

        protected Node[] createNodes(Object key) {
            return new Node[] { new MetaInfServiceNode((MetaInfService) key, platform)};
        }
    }

    private static class MetaInfServiceNode extends AbstractNode {
        private String platform;
        private String service;
        private Action[] actions;

        MetaInfServiceNode(MetaInfService metaInfService, String platform) {
            super(new MetaInfServiceNodeChildren(metaInfService.getProviders()));
            this.platform = platform;
            this.service = metaInfService.getService();
            setDisplayName(service);
            setIconBaseWithExtension("org/netbeans/modules/sfsbrowser/service.gif");
        }

        public Action[] getActions(boolean context) {
            if (metaInfServicesAPI.containsKey(service)) {
                if (!context) {
                    if (actions == null) {
                        List<Action> actionsList = new LinkedList<Action>();
                        actionsList.add(new GotoJavaTypeAction(service));
                        String urlString = metaInfServicesAPI.get(service);
                        if (platform.equals("platform6")) {
                            urlString = "http://www.netbeans.org/download/5_5/javadoc/"+ urlString;
                        } else if (platform.equals("platform7")) {
                            urlString = "http://www.netbeans.org/download/6_0/javadoc/"+ urlString;
                        }
                        URL url = getURL(urlString);
                        if (url != null) {
                            actionsList.add(new ShowURLActionFactory.ShowURLAction(service + " API", url));
                        }
                        actions = actionsList.toArray(EMPTY_ACTIONS);
                    }
                    return actions;
                }
            }
            return EMPTY_ACTIONS;
        }
    }

    private static class MetaInfServiceNodeChildren extends Children.Keys {
        private List<String> providers;

        MetaInfServiceNodeChildren(List<String> providers) {
            this.providers = providers;
        }

        protected void addNotify() {
            setKeys(providers);
        }

        protected Node[] createNodes(Object key) {
            Node node = new AbstractNode(Children.LEAF);
            String displayName = String.valueOf(key);
            if (displayName.startsWith("#position=")) {
                displayName = displayName.substring("#position=".length());
                node = new PositionNode(displayName);
            } else {
                node = new ServiceImplNode(displayName);
            }
            return new Node[] { node };
        }
    }

    private static class ServiceImplNode extends AbstractNode {
        private Action[] actions;

        ServiceImplNode(String serviceImpl) {
            super(Children.LEAF);
            setDisplayName(serviceImpl);
            setIconBaseWithExtension("org/netbeans/modules/sfsbrowser/provider.gif");
        }

        public Action[] getActions(boolean context) {
            if (!context) {
                if (actions == null) {
                    actions = new Action[] {new GotoJavaTypeAction(getDisplayName())};
                }
                return actions;
            }
            return EMPTY_ACTIONS;
        }
    }

    private static class PositionNode extends AbstractNode {
        PositionNode(String position) {
            super(Children.LEAF);
            setDisplayName(position);
            setIconBaseWithExtension("org/netbeans/modules/sfsbrowser/position.gif");
        }

        public Action[] getActions(boolean context) {
            return EMPTY_ACTIONS;
        }
    }

    private static final ClassPath EMPTY_CLASSPATH = ClassPathSupport.createClassPath( new FileObject[0] );

    private static class GotoJavaTypeAction extends AbstractAction {
        private String typeName;

        GotoJavaTypeAction(String typeName) {
            super("Go to " + typeName);
            this.typeName = typeName;
        }

        public void actionPerformed(ActionEvent e) {
            Set<FileObject> roots = GlobalPathRegistry.getDefault().getSourceRoots();
            FileObject rootArray[] = new FileObject[1];
            for (FileObject root: roots) {
                FileObject sourceFileObject = root.getFileObject(typeName.replace(".", "/")+".java"); // NOI18N
                if (sourceFileObject != null && sourceFileObject.isValid()) {
                    DataObject dataObject;
                    try {
                        dataObject = DataObject.find(sourceFileObject);
                        if (dataObject != null) {
                            EditorCookie editorCookie = (EditorCookie) dataObject.getCookie(EditorCookie.class);
                            if (editorCookie != null) {
                                editorCookie.open();
                            }
                        }
                    } catch (DataObjectNotFoundException ex) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                    }
                }
            }
        }
    }

    private static class OpenDelegateAction extends AbstractAction {
        private FileObject fileObject;

        OpenDelegateAction(FileObject fileObject, String fileSystemName) {
            super("Open " + fileObject.getPath() + " in " + fileSystemName); // TODO I18N
            this.fileObject = fileObject;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                org.openide.loaders.DataObject dataObject = org.openide.loaders.DataObject.find(fileObject);

                if (dataObject != null) {
                    org.openide.cookies.OpenCookie openCookie = dataObject.getCookie(org.openide.cookies.OpenCookie.class);

                    if (openCookie == null) {
                        org.openide.cookies.EditorCookie editorCookie = dataObject.getCookie(org.openide.cookies.EditorCookie.class);

                        if (editorCookie != null) {
                            editorCookie.open();
                        }
                    } else {
                        openCookie.open();
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static List getDelegates(MultiFileSystem multiFileSystem, FileObject fileObject) {
        List delegates = new LinkedList();
        getDelegates(multiFileSystem, fileObject, delegates);
        Collections.reverse(delegates);
        return delegates;
    }

    private static Method method;
    static {
        try {
            method = MultiFileSystem.class.getDeclaredMethod("delegates", String.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException nsme) {
            // ignore
        }
    }

    private static void getDelegates(MultiFileSystem multiFileSystem, FileObject fileObject, List delegatesSet) {
        if (method != null) {
            try         {
                java.util.Enumeration<org.openide.filesystems.FileObject> delegates = (java.util.Enumeration<org.openide.filesystems.FileObject>) method.invoke(multiFileSystem,
                        fileObject.getPath());

                while (delegates.hasMoreElements()) {
                    org.openide.filesystems.FileObject delegate = delegates.nextElement();

                    if (delegate.isValid()) {
                        delegatesSet.add(delegate);
                        org.openide.filesystems.FileSystem fileSystem = delegate.getFileSystem();

                        if (fileSystem instanceof org.openide.filesystems.MultiFileSystem) {
                            getDelegates((org.openide.filesystems.MultiFileSystem) fileSystem,
                                    delegate);
                        }
                    }
                }
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                // ignore
            } catch (IllegalArgumentException ex) {
                // ignore
            } catch (InvocationTargetException ex) {
                // ignore
            }
        }
    }
}
