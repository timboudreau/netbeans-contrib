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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.netbeans.modules.properties.rbe.model.TreeItem;
import org.netbeans.modules.properties.rbe.model.visitor.TreeVisitor;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
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
    public void destroy() throws IOException {
        treeItem.accept(new TreeVisitor<BundleProperty>() {

            public void visit(TreeItem<BundleProperty> t) {
                for (TreeItem<BundleProperty> tree : t.getChildren()) {
                    tree.accept(this);
                    tree.getValue().deleteProperty();
                }
            }
        });
        treeItem.getValue().deleteProperty();
        treeItem.getParent().removeChild(treeItem);
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
