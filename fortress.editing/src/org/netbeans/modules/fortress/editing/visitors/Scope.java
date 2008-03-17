/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.fortress.editing.visitors;

import com.sun.fortress.nodes.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Caoyuan Deng
 */
public class Scope implements Iterable<Scope> {

    private final Node node;
    private Scope parent;
    private List<Scope> scopes;
    private List<Element> definitions;
    private List<Element> usages;

    public Scope(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Scope getParent() {
        return parent;
    }

    public List<Scope> getScopes() {
        if (scopes == null) {
            return Collections.emptyList();
        }
        return scopes;
    }

    public List<Element> getDefinitions() {
        if (definitions == null) {
            return Collections.emptyList();
        }
        return definitions;
    }

    public List<Element> getUsages() {
        if (usages == null) {
            return Collections.emptyList();
        }
        return usages;
    }

    void addScope(Scope scope) {
        if (scopes == null) {
            scopes = new ArrayList<Scope>();
        }
        scopes.add(scope);
        scope.parent = this;
    }

    void addDefinition(Element signature) {
        if (definitions == null) {
            definitions = new ArrayList<Element>();
        }
        definitions.add(signature);
    }

    void addUsage(Element signature) {
        if (usages == null) {
            usages = new ArrayList<Element>();
            ;
        }
        usages.add(signature);
    }

    public Iterator<Scope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<Scope>emptySet().iterator();
        }
    }

    private List<Node> findVarNodes(String name) {
        List<Node> nodes = new ArrayList<Node>();
        addNodes(node, name, nodes);

        return nodes;
    }

    // Iterate over a scope and mark the given unused locals and globals in the highlights map
    private void addNodes(Node node, String name, List<Node> result) {
//            switch (node.getType()) {
//                case Token.NAME:
//                case Token.PARAMETER:
//                case Token.BINDNAME: {
//                    String s = node.getString();
//                    if (s.equals(name)) {
//                        result.add(node);
//                    }
//                    break;
//                }
//            }
//
//            if (node.hasChildren()) {
//                Node child = node.getFirstChild();
//
//                for (; child != null; child = child.getNext()) {
//                    int type = child.getType();
//                    if (type == Token.FUNCTION || type == Token.SCRIPT) {
//                        // It's another scope - skip
//                        continue;
//                    }
//                    addNodes(child, name, result);
//                }
//            }
        }

    private List<Node> findVarNodes(Set<String> names) {
        List<Node> nodes = new ArrayList<Node>();
        addNodes(node, names, nodes);

        return nodes;
    }

    // Iterate over a scope and mark the given unused locals and globals in the highlights map
    private void addNodes(Node node, Set<String> names, List<Node> result) {
//            switch (node.getType()) {
//                case Token.NAME:
//                case Token.PARAMETER:
//                case Token.BINDNAME: {
//                    String s = node.getString();
//                    if (names.contains(s)) {
//                        result.add(node);
//                    }
//                    break;
//                }
//            }
//
//            if (node.hasChildren()) {
//                Node child = node.getFirstChild();
//
//                for (; child != null; child = child.getNext()) {
//                    int type = child.getType();
//                    if (type == Token.FUNCTION || type == Token.SCRIPT) {
//                        // It's another scope - skip
//                        continue;
//                    }
//                    addNodes(child, names, result);
//                }
//            }
        }

    @Override
    public String toString() {
        return "Scope(node=" + node + ",locals=" + getDefinitions() + ",read=" + getUsages() + ")";
    }
}

