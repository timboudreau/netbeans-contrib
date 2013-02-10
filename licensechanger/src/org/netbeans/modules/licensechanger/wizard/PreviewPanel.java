/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.wizard;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.netbeans.modules.licensechanger.wizard.utils.CheckableNodeCapability;
import org.netbeans.modules.licensechanger.wizard.utils.FileChildren;
import org.netbeans.modules.licensechanger.wizard.utils.FileChildren.FileItem;
import org.netbeans.modules.licensechanger.wizard.utils.FileLoader;
import org.netbeans.modules.licensechanger.wizard.utils.WizardProperties;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.CheckableNode;
import org.openide.explorer.view.OutlineView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.WeakListeners;

/**
 * Displays selected files before and after application of the new license
 * header template. Users can manually deselect files to exclude them from being
 * processed.
 *
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring, Diff API inclusion)
 */
public class PreviewPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener {

    private final ExplorerManager mgr = new ExplorerManager();
    private ItemLoader loader;
    private String licenseText;
    private Diff diff;
    private Map<String, Object> properties;
    private WeakReference<Node> currentNodeReference;

    public PreviewPanel() {
        diff = Diff.getDefault();
        initComponents();
        mgr.addPropertyChangeListener(this);
        jLabel3.setText("  ");
        updateView();
    }

    private void updateView() {
        OutlineView ov = (OutlineView) fileList;
        ov.getOutline().setRootVisible(false);
        ov.setPopupAllowed(false);
        ov.setTreeSortable(false);
        ov.getOutline().setShowGrid(false);
        String headerName = org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.nodesLabel.text"); // NOI18N
        ((DefaultOutlineModel) ov.getOutline().getOutlineModel()).setNodesColumnLabel(headerName);
        setName("Preview Changes");
    }

    public void setFolders(Set<FileObject> folders, Set<FileHandler> fileHandler) {
        Children kids = Children.create(new FileChildren(folders, fileHandler), true);
        mgr.setRootContext(new AbstractNode(kids));
        updateView();
        updateItems();
    }

    public Set<FileItem> getSelectedItems() {
        Set<FileItem> s = new HashSet<FileItem>();
        for (Node n : mgr.getRootContext().getChildren().getNodes(true)) {
            CheckableNode cn = n.getLookup().lookup(CheckableNode.class);
            if (cn != null && cn.isSelected()) {
                s.addAll(n.getLookup().lookupAll(FileItem.class));
            }
        }
        return s;
    }

    private void updateItems() {
        Set<FileItem> s = getSelectedItems();
        firePropertyChange(WizardProperties.KEY_ITEMS, null, s);
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
        firePropertyChange(WizardProperties.KEY_LICENSE_TEXT, null, this.licenseText);
    }

    void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        fileList = new OutlineView();
        diffPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setTopComponent(fileList);

        javax.swing.GroupLayout diffPanelLayout = new javax.swing.GroupLayout(diffPanel);
        diffPanel.setLayout(diffPanelLayout);
        diffPanelLayout.setHorizontalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        diffPanelLayout.setVerticalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 178, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(diffPanel);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel3.text")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel diffPanel;
    private javax.swing.JScrollPane fileList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            if (n.length > 0) {
                FileItem item = n[0].getLookup().lookup(FileItem.class);
                CheckableNodeCapability cnc = n[0].getLookup().lookup(CheckableNodeCapability.class);
                boolean transform = true;
                if (cnc != null) {
                    transform = cnc.isSelected();
                    if (currentNodeReference != null) {
                        Node currentNode = currentNodeReference.get();
                        if (currentNode != null && !n[0].equals(currentNode)) {
                            //remove property change listener 
                            CheckableNodeCapability cncCurrent = currentNode.getLookup().lookup(CheckableNodeCapability.class);
                            if (cncCurrent != null) {
                                removePropertyChangeListener(this);
                            }
                        }
                    }
                    cnc.addPropertyChangeListener(WeakListeners.propertyChange(this, cnc));
                    currentNodeReference = new WeakReference<Node>(n[0]);
                }

                if (item != null) {
                    setFileItem(item, transform);
                    // XXX use FileUtil.getFileDisplayName rather than FileObject.getPath

                    jLabel3.setText(FileUtil.getFileDisplayName(item.getFile()));
                } else {
                    jLabel3.setText("  ");
                }
            } else {
                jLabel3.setText("  ");
            }
        } else if (CheckableNodeCapability.PROP_SELECTED.equals(evt.getPropertyName())) {
            if (currentNodeReference != null) {
                Node currentNode = currentNodeReference.get();
                if (currentNode != null) {
                    //recreate preview for currentNode node
                    CheckableNodeCapability cncCurrent = currentNode.getLookup().lookup(CheckableNodeCapability.class);
                    boolean transform = true;
                    if (cncCurrent != null) {
                        transform = cncCurrent.isSelected();
                        FileItem item = currentNode.getLookup().lookup(FileItem.class);
                        if (item != null) {
                            setFileItem(item, transform);
                            // XXX use FileUtil.getFileDisplayName rather than FileObject.getPath

                            jLabel3.setText(FileUtil.getFileDisplayName(item.getFile()));
                        } else {
                            jLabel3.setText("  ");
                        }
                    }
                }
            }

        }
    }

    private void setFileItem(FileItem item, boolean isSelected) {
        if (item.getFile().isValid()) {
            ItemLoader ldr = new ItemLoader(item, isSelected);
            setLoader(ldr);
        }
    }

    private synchronized void setLoader(ItemLoader ldr) {
        if (loader != null) {
            loader.cancel();
        }
        this.loader = ldr;
        loader.start();
    }

    private class ItemLoader implements Runnable {

        private final Task task;
        private volatile boolean cancelled;
        private final FileItem item;
        private volatile String beforeText = "Cancelled";
        private volatile String afterText = "Cancelled";
        private final boolean transform;

        public ItemLoader(FileItem item, boolean transform) {
            this.item = item;
            this.transform = transform;
            task = RequestProcessor.getDefault().create(this);
        }

        void start() {
            task.schedule(200);
        }

        public void cancel() {
            cancelled = true;
            task.cancel();
        }

        private void loadFile() throws IOException {
            beforeText = FileLoader.loadFile(item.getFile());
        }

        @Override
        public void run() {
            if (cancelled) {
                return;
            }
            if (!EventQueue.isDispatchThread()) {
                try {
                    if (cancelled) {
                        return;
                    }
                    if (!item.getFile().isValid()) {
                        beforeText = "Invalid file";
                        afterText = "Invalid file";
                        return;
                    }
                    if (!item.getFile().canRead()) {
                        beforeText = "Cannot read " + item.getFile().getPath();
                        afterText = "Cannot read " + item.getFile().getPath();
                        return;
                    }
                    if (item.getFile().getSize() >= Integer.MAX_VALUE) {
                        beforeText = "File too long: " + item.getFile().getPath();
                        afterText = "File too long: " + item.getFile().getPath();
                        return;
                    }
                    if (item.getFile().getSize() == 0) {
                        beforeText = "Empty file";
                        afterText = "Empty file";
                        return;
                    }
                    if (cancelled) {
                        return;
                    }
                    try {
                        loadFile();
                    } catch (IOException ex) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        PrintWriter w = new PrintWriter(out);
                        ex.printStackTrace(w);
                        beforeText = new String(out.toByteArray());
                        afterText = "Error";
                        return;
                    }
                    if (cancelled) {
                        return;
                    }
                    if (beforeText.length() == 0) {
                        afterText = "";
                        return;
                    }
                    if (transform) {
                        afterText = transform(beforeText, item.getHandler());
                    } else {
                        afterText = beforeText;
                    }
                } finally {
                    if (!cancelled) {
                        EventQueue.invokeLater(this);
                    }
                }
            } else {
                String contentType = item.getFile().getMIMEType();
                DiffView view;
                try {
                    view = diff.createDiff(StreamSource.createSource("Before", "Before", contentType, new StringReader(beforeText)),
                            StreamSource.createSource("After", "After", contentType, new StringReader(afterText)));
                    int dloc = jSplitPane1.getDividerLocation();
                    jSplitPane1.setBottomComponent(view.getComponent());
                    jSplitPane1.setDividerLocation(dloc);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    int dloc = jSplitPane1.getDividerLocation();
                    jSplitPane1.setBottomComponent(diffPanel);
                    jSplitPane1.setDividerLocation(dloc);
                }

            }
        }

        private String transform(String beforeText, FileHandler handler) {
            return handler.transform(beforeText, licenseText,
                    properties == null ? Collections.<String, Object>emptyMap()
                    : properties);
        }
    }
}
