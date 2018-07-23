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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class ValuesNode extends AbstractNode {

    private final UseDescription from;

    public ValuesNode(UseDescription from) {
        super(Children.create(new ChildrenFactoryImpl(from), true));
        this.from = from;
    }

    private static final class ChildrenFactoryImpl extends ChildFactory<Node> {

        private final UseDescription use;
        private final Map<String, Map<UseDescription, FilteredUsages>> value2Defines = new HashMap<String, Map<UseDescription, FilteredUsages>>();
        private final List<TODOState> todo = new LinkedList<TODOState>();

        public ChildrenFactoryImpl(UseDescription use) {
            this.use = use;
            todo.add(new TODOState(Collections.singleton(use), Collections.singletonList(use)));
        }

        @Override
        protected boolean createKeys(List<Node> toPopulate) {
            while (!todo.isEmpty()) {
                TODOState current = todo.remove(0);
                UseDescription tip = current.path.get(current.path.size() - 1);

                if (tip.leaf) {
                    Map<UseDescription, FilteredUsages> defines = value2Defines.get(tip.displayName);
                    boolean nue = defines == null;

                    if (defines == null) {
                        value2Defines.put(tip.displayName, defines = new IdentityHashMap<UseDescription, FilteredUsages>());
                    }

                    FilteredUsages parent = null;

                    for (UseDescription d : current.path) {
                        FilteredUsages c = defines.get(d);

                        if (c == null) {
                            defines.put(d, c = new FilteredUsages(d));
                            if (parent != null) parent.addChild(c);
                        }

                        parent = c;
                    }

                    if (nue) {
                        toPopulate.add(new ValueNode(tip, defines.get(use)));
                        //TODO: incremental computation (but probably needs to show progress nodes also in subnodes)
//                        return false;
                    }
                } else {
                    for (UseDescription c : DataFlowToThis.findWrites(tip)) {
                        if (current.seenHandles.contains(c)) {
                            //XXX: add loop node?
                            continue;
                        }

                        Set<UseDescription> seenHandles = new HashSet<UseDescription>(current.seenHandles);
                        seenHandles.add(c);

                        List<UseDescription> path = new ArrayList<UseDescription>(current.path.size() + 1);
                        path.addAll(current.path);
                        path.add(c);

                        todo.add(new TODOState(seenHandles, path));
                    }
                }
            }

            value2Defines.clear();
            todo.clear();
            return true;
        }

        @Override
        protected Node createNodeForKey(Node key) {
            return key;
        }
        
    }

    private static final class TODOState {
        private final Set<UseDescription> seenHandles;
        private final List<UseDescription> path;
        public TODOState(Set<UseDescription> seenHandles, List<UseDescription> path) {
            this.seenHandles = seenHandles;
            this.path = path;
        }
    }

    public static class FilteredUsages {
        private final UseDescription currentUse;
        private final List<FilteredUsages> children = new LinkedList<FilteredUsages>();
        private final ChangeSupport cs = new ChangeSupport(this);

        public FilteredUsages(UseDescription currentUse) {
            this.currentUse = currentUse;
        }

        public synchronized List<FilteredUsages> getChildren() {
            return children;
        }

        public UseDescription getCurrentUse() {
            return currentUse;
        }

        private void addChild(FilteredUsages usages) {
            synchronized(this) {
                children.add(usages);
            }
            cs.fireChange();
        }

        public void addChangeListener(ChangeListener cl) {
            cs.addChangeListener(cl);
        }

        public void removeChangeListener(ChangeListener cl) {
            cs.removeChangeListener(cl);
        }
    }

    private static final class ValueNode extends AbstractNode {
        private final UseDescription value;
        private final FilteredUsages fu;

        public ValueNode(UseDescription value, FilteredUsages fu) {
            super(new ChildrenImpl(fu), Lookups.fixed(value));
            this.value = value;
            this.fu = fu;
        }

        @Override
        public String getHtmlDisplayName() {
            return value.displayName;
        }

    }

    private static final class ChildrenImpl extends Children.Keys<FilteredUsages> {

        private final FilteredUsages fu;

        public ChildrenImpl(FilteredUsages fu) {
            this.fu = fu;
        }

        @Override
        protected void addNotify() {
            setKeys(Collections.singletonList(fu));
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<FilteredUsages>emptyList());
        }

        @Override
        protected Node[] createNodes(FilteredUsages key) {
            return new Node[] {
                new UseDescriptionNode(key)
            };
        }

    }
}
