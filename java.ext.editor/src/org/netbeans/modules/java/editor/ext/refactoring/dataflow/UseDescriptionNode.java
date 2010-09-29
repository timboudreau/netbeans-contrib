/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring.dataflow;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.source.UiUtils;
import org.netbeans.api.java.source.ui.ElementIcons;
import org.netbeans.modules.java.editor.ext.refactoring.dataflow.ValuesNode.FilteredUsages;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Children.Keys;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class UseDescriptionNode extends AbstractNode {

    private final UseDescription use;

    public UseDescriptionNode(UseDescription use) {
        super(createChildren(use), Lookups.fixed(use));
        this.use = use;
//        setDisplayName("<html>" + use.displayName);
    }

    public UseDescriptionNode(FilteredUsages key) {
        super(createChildren(key), Lookups.fixed(key.getCurrentUse()));
        this.use = key.getCurrentUse();
    }

    @Override
    public String getHtmlDisplayName() {
        return use.displayName;
    }

    @Override
    public Image getIcon(int type) {
        if (use.referencedKind != null) {
            Icon icon = ElementIcons.getElementIcon(use.referencedKind, EnumSet.noneOf(Modifier.class));

            if (icon != null) {
                return ImageUtilities.icon2Image(icon);
            }
        }
        
        return super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[] {
            new GoToSourceAction(use),
        };
    }

    private static Children createChildren(UseDescription use) {
        if (use.leaf) {
            return Children.LEAF;
        }

        return Children.create(new ChildrenFactoryImpl(use), true);
    }

    private static Children createChildren(final FilteredUsages key) {
        if (key.getCurrentUse().leaf) {
            return Children.LEAF;
        }

        return new FilteredChildren(key);
    }

    private static final class ChildrenFactoryImpl extends ChildFactory<UseDescription> {

        private final UseDescription use;

        public ChildrenFactoryImpl(UseDescription use) {
            this.use = use;
        }

        @Override
        protected boolean createKeys(List<UseDescription> toPopulate) {
            toPopulate.addAll(DataFlowToThis.findWrites(use));

            return true;
        }

        @Override
        protected Node createNodeForKey(UseDescription key) {
            return new UseDescriptionNode(key);
        }

    }

    private static final class GoToSourceAction extends AbstractAction {

        private final UseDescription use;

        public GoToSourceAction(UseDescription use) {
            this.use = use;
            putValue(NAME, "Go to Source");
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UiUtils.open(use.tph.getFileObject(), use.offset);
        }

    }

    private static class FilteredChildren extends Keys<FilteredUsages> implements ChangeListener {

        private final FilteredUsages key;

        public FilteredChildren(FilteredUsages key) {
            this.key = key;
        }

        @Override
        protected synchronized void addNotify() {
            setKeys(key.getChildren());
            key.addChangeListener(this);
        }

        @Override
        protected synchronized void removeNotify() {
            key.removeChangeListener(this);
            setKeys(Collections.<FilteredUsages>emptyList());
        }

        @Override
        protected Node[] createNodes(FilteredUsages key) {
            return new Node[] {
                new UseDescriptionNode(key)
            };
        }

        @Override
        public synchronized void stateChanged(ChangeEvent e) {
            setKeys(key.getChildren());
        }
    }
}
