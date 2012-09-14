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

package org.netbeans.modules.licensechanger.spi.wizard;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.netbeans.modules.licensechanger.spi.wizard.utils.CheckboxListView;
import org.netbeans.modules.licensechanger.spi.wizard.utils.FileChildren;
import org.netbeans.modules.licensechanger.spi.wizard.utils.FileChildren.FileItem;
import org.netbeans.modules.licensechanger.spi.wizard.utils.FileLoader;
import org.netbeans.modules.licensechanger.spi.wizard.utils.NodeCheckObserver;
import org.netbeans.modules.licensechanger.spi.wizard.utils.WizardProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.CheckableNode;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring, Diff API inclusion)
 */
public class PreviewPanel extends javax.swing.JPanel implements ExplorerManager.Provider, PropertyChangeListener, NodeCheckObserver {

    private final ExplorerManager mgr = new ExplorerManager();
    private ItemLoader loader;
    private String licenseText;
    private Diff diff;

    public PreviewPanel() {
        diff = Diff.getDefault();
        initComponents();
        mgr.addPropertyChangeListener(this);
        jLabel3.setText ("  ");
        view().setNodeCheckObserver(this);
        setName("Preview Changes");
    }
    
    public void setFolders(Set<FileObject> folders, Set<FileHandler> fileHandler) {
        Children kids = Children.create(new FileChildren(folders,fileHandler), true);
        mgr.setRootContext(new AbstractNode(kids));
        view().setCheckboxesVisible(true);
        view().setCheckboxesEnabled(true);
        view().setListEnabled(true);
        updateItems();
    }

    public Set<FileItem> getSelectedItems() {
        Set<FileItem> s = new HashSet<FileItem>();
        for (Node n : mgr.getRootContext().getChildren().getNodes(true)) {
            CheckableNode cn = n.getLookup().lookup(CheckableNode.class);
            if (cn!= null && cn.isSelected()) {
                s.addAll(n.getLookup().lookupAll(FileItem.class));
            }
        }
        return s;
    }
    
    private void updateItems() {
        Set<FileItem> s = getSelectedItems();
        firePropertyChange(WizardProperties.KEY_ITEMS, null, s);
    }

    private CheckboxListView view() {
        return (CheckboxListView) fileList;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
        firePropertyChange(WizardProperties.KEY_LICENSE_TEXT, null, this.licenseText);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        fileList = new CheckboxListView();
        diffPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        fileList.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.fileList.border.title"))); // NOI18N
        jSplitPane1.setTopComponent(fileList);

        javax.swing.GroupLayout diffPanelLayout = new javax.swing.GroupLayout(diffPanel);
        diffPanel.setLayout(diffPanelLayout);
        diffPanelLayout.setHorizontalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        diffPanelLayout.setVerticalGroup(
            diffPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(diffPanel);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PreviewPanel.class, "PreviewPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel diffPanel;
    private javax.swing.JScrollPane fileList;
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
                if (item != null) {
                    setFileItem(item);
                    // XXX use FileUtil.getFileDisplayName rather than FileObject.getPath
                    jLabel3.setText (item.getFile().getPath());
                } else {
//                    before.setText("");
                    jLabel3.setText ("  ");
                }
            } else {
//                before.setText("");
//                after.setText("");
                jLabel3.setText("  ");
            }
        }
    }

    private void setFileItem(FileItem item) {
        if (item.getFile().isValid()) {
            // XXX should use Diff API instead (like refactoring preview)
//            before.setContentType("text/plain");
//            after.setContentType("text/plain");
//            before.setText("Loading " + item.getFile().getPath());
//            after.setText("Loading " + item.getFile().getPath());
            ItemLoader ldr = new ItemLoader(item);
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

    @Override
    public void onNodeChecked(Node node) {
        updateItems();
    }

    @Override
    public void onNodeUnchecked(Node node) {
        updateItems();
    }

    private class ItemLoader implements Runnable {

        private final Task task;
        private volatile boolean cancelled;
        private final FileItem item;
        private volatile String beforeText = "Cancelled";
        private volatile String afterText = "Cancelled";

        public ItemLoader(FileItem item) {
            this.item = item;
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
                    afterText = transform(beforeText, item.getHandler());
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
            return handler.transform(beforeText, licenseText);
        }
    }
}
