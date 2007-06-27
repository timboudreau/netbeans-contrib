/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.sfsexplorer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
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
    static final String ICON_PATH = "org/netbeans/modules/sfsexplorer/sfs.gif";

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

        ExplorerManagerPanel sfsPanel = new ExplorerManagerPanel(NbBundle.getMessage(SFSBrowserTopComponent.class, "System_FileSystem"));
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
        associateLookup(sfsPanel.getLookup());
    }

    private class ExplorerManagerPanel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {
        private final ExplorerManager manager = new ExplorerManager();
        private Lookup lookup;

        /**
         * 
         * @param label 
         */
        ExplorerManagerPanel(String label) {
            super(new BorderLayout());
            ActionMap map = getActionMap();
//            map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
//            map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
//            map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
//            map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false

            // ...but add e.g.:
//            InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//            keys.put(KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction);
//            keys.put(KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction);
//            keys.put(KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction);
//            keys.put(KeyStroke.getKeyStroke("DELETE"), "delete");

            // ...and initialization of lookup variable
            lookup = ExplorerUtils.createLookup (manager, map);

            add(new JLabel(label), BorderLayout.NORTH);
        }

        /**
         * 
         * @return 
         */
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        
        /**
         * 
         * @return 
         */
        public Lookup getLookup() {
            return lookup;
        }
        public void addNotify() {
            super.addNotify();
            ExplorerUtils.activateActions(manager, true);
        }
        public void removeNotify() {
            ExplorerUtils.activateActions(manager, false);
            super.removeNotify();
        }
    }
        
    /**
     * 
     * @return 
     */
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
            ErrorManager.getDefault().log(ErrorManager.WARNING, NbBundle.getMessage(SFSBrowserTopComponent.class, "Cannot_find_SFSBrowser_component._It_will_not_be_located_properly_in_the_window_system."));
            return getDefault();
        }
        if (win instanceof SFSBrowserTopComponent) {
            return (SFSBrowserTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, NbBundle.getMessage(SFSBrowserTopComponent.class, "There_seem_to_be_multiple_components_with_the_'") + PREFERRED_ID + NbBundle.getMessage(SFSBrowserTopComponent.class, "'_ID._That_is_a_potential_source_of_errors_and_unexpected_behavior."));
        return getDefault();
    }

    /**
     * 
     * @return 
     */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public void componentOpened() {
        // TODO add custom code on component opening
    }

    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /**
     * replaces this in object stream
     * @return 
     */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    /**
     * 
     * @return 
     */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 
         * @return 
         */
        public Object readResolve() {
            return SFSBrowserTopComponent.getDefault();
        }
    }

    /**
     * 
     * @param path 
     */
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
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(SFSBrowserTopComponent.class, "Could_not_select_") + path);
            }
        }
    }

    /**
     * 
     * @param path 
     */
    static void select(String path) {
        SFSBrowserTopComponent sfsBrowserTopComponent = findInstance();
        sfsBrowserTopComponent.selectNode(path);
    }

    private static class NonRecursiveFolderLookup extends FolderLookup {
        /**
         * 
         * @param df 
         */
        public NonRecursiveFolderLookup(DataObject.Container df) {
            super(df);
        }

        /**
         * 
         * @param df 
         * @return 
         */
        protected InstanceCookie acceptFolder(DataFolder df) {
            return null;
        }

        /**
         * 
         * @param container 
         * @return 
         */
        protected InstanceCookie acceptContainer(DataObject.Container container) {
            return null;
        }
    }

    /**
     * 
     * @param node 
     * @param actions 
     * @param platform 
     * @param root 
     */
    static void collectActions(Node node, List/*<Action>*/ actions, String platform, FileObject root) {
        DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null && fileObject != root) {
                FileObject specificReleaseActionInstanceFolder =
                        root.getFileObject(
                        "org.netbeans.modules.sfsexplorer" // NOI18N
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
                        "org.netbeans.modules.sfsexplorer" // NOI18N
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

    static Node[] EMPTY_NODE_ARRAY = new Node[0];
    static Action[] EMPTY_ACTIONS = new Action[0];

    private static Map<String, URL> urlCache = new HashMap<String, URL>();

    /**
     * 
     * @param urlString 
     * @return 
     */
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

    static class OpenDelegateAction extends AbstractAction {
        private FileObject fileObject;

        /**
         * 
         * @param fileObject 
         * @param fileSystemName 
         */
        OpenDelegateAction(FileObject fileObject, String fileSystemName) {
            super(NbBundle.getMessage(SFSBrowserTopComponent.class, "Open_") + fileObject.getPath() + NbBundle.getMessage(SFSBrowserTopComponent.class, "_in_") + fileSystemName); // TODO I18N
            this.fileObject = fileObject;
        }

        /**
         * 
         * @param e 
         */
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

}
