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
 * Contributor(s): Denis Stepanov
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
package org.netbeans.modules.properties.rbe.ui;

import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import org.netbeans.modules.properties.rbe.ResourceBundleEditorOptions;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.netbeans.modules.properties.rbe.model.TreeItem;
import org.netbeans.modules.properties.rbe.model.visitor.AbstractTraversalTreeVisitor;
import org.netbeans.modules.properties.rbe.model.visitor.TreeVisitor;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 * The Bundle property node
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class TreeItemPropertyNode extends BundlePropertyNode implements PropertyChangeListener, Comparable<TreeItemPropertyNode> {

    private final TreeItem<BundleProperty> treeItem;

    public TreeItemPropertyNode(TreeItem<BundleProperty> treeItem) {
        super(treeItem.isLeaf() ? Children.LEAF : new ChildrenProperties(treeItem), Lookups.singleton(treeItem.getValue()));
        this.treeItem = treeItem;
        treeItem.addPropertyChangeListener(this);
    }

    public BundleProperty getProperty() {
        return treeItem.getValue();
    }

    public TreeItem<BundleProperty> getTreeItem() {
        return treeItem;
    }

    @Override
    public String getName() {
        return treeItem.getValue().getKey();
    }

    @Override
    public String getDisplayName() {
        return treeItem.getValue().getName();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (TreeItem.PROPERTY_CHILDREN.equals(evt.getPropertyName())) {
            setChildren(treeItem.getChildren().isEmpty() ? Children.LEAF : new ChildrenProperties(treeItem));
        }
    }

    public int compareTo(TreeItemPropertyNode o) {
        return treeItem.compareTo(o.treeItem);
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public boolean canCut() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        treeItem.getParent().removeChild(treeItem);
        treeItem.accept(new TreeVisitor<BundleProperty>() {

            public void visit(TreeItem<BundleProperty> t) {
                for (TreeItem<BundleProperty> tree : t.getChildren()) {
                    tree.accept(this);
                    tree.getValue().delete();
                }
            }
        });
        treeItem.getValue().delete();
    }

    @Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        super.createPasteTypes(t, s);
        Node node = NodeTransfer.node(t, NodeTransfer.CLIPBOARD_COPY);
        if (node != null && node instanceof BundlePropertyNode) {
            s.add(new TransferAction(((TreeItemPropertyNode) node).getTreeItem(), NodeTransfer.CLIPBOARD_COPY));
        } else {
            node = NodeTransfer.node(t, NodeTransfer.CLIPBOARD_CUT);
            if (node != null && node instanceof TreeItemPropertyNode) {
                s.add(new TransferAction(((TreeItemPropertyNode) node).getTreeItem(), NodeTransfer.CLIPBOARD_CUT));
            }
        }
    }

    /**
     * Transfer action
     * @param tree
     * @param action
     * @return true if paste type should be removed from the memory
     */
    protected boolean transferAction(TreeItem<BundleProperty> tree, final int action) {
        final boolean isMoveAction = (action & NodeTransfer.MOVE) > 0;
        if (tree.isSubtree(treeItem)) { // Don't move to the current tree
            return false;
        }
        if (isMoveAction) {
            // Remove reference from the root node to prevent CME
            tree.getParent().removeChild(tree);
        }
        if (!tree.getParent().equals(treeItem)) { //Don't do anything when copy/cut to the same node
            tree.accept(new AbstractTraversalTreeVisitor<BundleProperty>() {

                private StringBuilder nodeKey = new StringBuilder(treeItem.getValue().getKey());

                @Override
                protected void preVisit(TreeItem<BundleProperty> t) {
                    nodeKey.append(ResourceBundleEditorOptions.getSeparator());
                    nodeKey.append(t.getValue().getName());
                    treeItem.getValue().getBundle().createPropertyFromExisting(nodeKey.toString(), t.getValue(), true);
                }

                @Override
                protected void postVisit(TreeItem<BundleProperty> t) {
                    if (isMoveAction) {
                        t.getValue().delete();
                    }
                    nodeKey.setLength(nodeKey.length() - ResourceBundleEditorOptions.getSeparator().length() - t.getValue().getName().length());
                }
            });
        }
        return isMoveAction;
    }

    private class TransferAction extends PasteType {

        private final TreeItem<BundleProperty> tree;
        private final int type;

        public TransferAction(TreeItem<BundleProperty> tree, int type) {
            this.tree = tree;
            this.type = type;
        }

        @Override
        public Transferable paste() throws IOException {
            return transferAction(tree, type) ? ExTransferable.EMPTY : null;
        }
    }

    private static class ChildrenProperties extends Children.Keys<TreeItem<BundleProperty>> implements PropertyChangeListener {

        private TreeItem<BundleProperty> treeItem;

        public ChildrenProperties(TreeItem<BundleProperty> treeItem) {
            this.treeItem = treeItem;
            treeItem.addPropertyChangeListener(this);
        }

        @Override
        protected void addNotify() {
            setKeys(treeItem.getChildren());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (TreeItem.PROPERTY_CHILDREN.equals(evt.getPropertyName())) {
                addNotify();
            }
        }

        @Override
        protected Node[] createNodes(TreeItem<BundleProperty> key) {
            return new Node[]{new TreeItemPropertyNode(key)};
        }
    }
}
