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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.netbeans.modules.properties.rbe.model.Bundle;
import org.netbeans.modules.properties.rbe.model.visitor.AbstractTraversalTreeVisitor;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.netbeans.modules.properties.rbe.model.TreeItem;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 * The Bundle property node factory
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class BundlePropertyNodeFactory extends ChildFactory<TreeItem<BundleProperty>> implements PropertyChangeListener {

    protected RBE rbe;

    public BundlePropertyNodeFactory(RBE rbe) {
        this.rbe = rbe;
    }

    @Override
    protected boolean createKeys(final List<TreeItem<BundleProperty>> toPopulate) {
        switch (rbe.getMode()) {
            case FLAT:
                rbe.getBundle().getPropertyTree().accept(new AbstractTraversalTreeVisitor<BundleProperty>() {

                    public void preVisit(TreeItem<BundleProperty> tree) {
                        if (tree.getValue() != null && tree.getValue().isExists()) {
                            toPopulate.add(tree);
                        }
                    }

                    public void postVisit(TreeItem<BundleProperty> tree) {
                    }
                });
                rbe.getBundle().addPropertyChangeListener(this);
                break;
            case TREE:
                toPopulate.addAll(rbe.getBundle().getPropertyTree().getChildren());
                rbe.getBundle().getPropertyTree().addPropertyChangeListener(this);
                break;
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(TreeItem<BundleProperty> key) {
        switch (rbe.getMode()) {
            case FLAT:
                return new FlatPropertyNode(key);
            case TREE:
                return new TreeItemPropertyNode(key);
        }
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (TreeItem.PROPERTY_CHILDREN.equals(evt.getPropertyName()) || Bundle.PROPERTY_PROPERTIES.equals(evt.getPropertyName())) {
            refresh(false);
        }
    }
}
