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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 *
 * @author Caoyuan Deng
 */
public class Scope implements Iterable<Scope> {

    private final Definition bindingDefinition;
    private Scope parent;
    private List<Scope> scopes;
    private List<Definition> definitions;
    private List<Usage> usages;
    private boolean scopesSorted;
    private boolean definitionsSorted;
    private boolean usagesSorted;
    private OffsetRange range;

    public Scope(Definition bindingDefinition, OffsetRange range) {
        if (bindingDefinition != null) {
            bindingDefinition.setBindingScope(this);
        }
        
        this.bindingDefinition = bindingDefinition;
        this.range = range;
    }

    public Definition getBindingDefinition() {
        return bindingDefinition;
    }

    public OffsetRange getRange() {
        return range;
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

    public List<Definition> getDefinitions() {
        if (definitions == null) {
            return Collections.emptyList();
        }
        return definitions;
    }

    public List<Usage> getUsages() {
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

    void addDefinition(Definition definition) {
        if (definitions == null) {
            definitions = new ArrayList<Definition>();
        }
        definitions.add(definition);
        definition.setEnclosingScope(this);
    }

    void addUsage(Usage usage) {
        if (usages == null) {
            usages = new ArrayList<Usage>();
        }
        usages.add(usage);
        usage.setEnclosingScope(this);
    }

    public Iterator<Scope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<Scope>emptySet().iterator();
        }
    }

    public Signature getSignature(int offset) {
        if (definitions != null) {
            if (!definitionsSorted) {
                Collections.sort(definitions, new SignatureComparator());
                definitionsSorted = true;
            }
            int low = 0;
            int high = definitions.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                Definition middle = definitions.get(mid);
                if (offset < middle.getNameRange().getStart()) {
                    high = mid - 1;
                } else if (offset >= middle.getNameRange().getEnd()) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (usages != null) {
            if (!usagesSorted) {
                Collections.sort(usages, new SignatureComparator());
                usagesSorted = true;
            }
            int low = 0;
            int high = usages.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                Usage middle = usages.get(mid);
                if (offset < middle.getNameRange().getStart()) {
                    high = mid - 1;
                } else if (offset >= middle.getNameRange().getEnd()) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (scopes != null) {
            if (!scopesSorted) {
                Collections.sort(scopes, new ScopeComparator());
                scopesSorted = true;
            }
            int low = 0;
            int high = scopes.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                Scope middle = scopes.get(mid);
                if (offset < middle.getRange().getStart()) {
                    high = mid - 1;
                } else if (offset >= middle.getRange().getEnd()) {
                    low = mid + 1;
                } else {
                    return middle.getSignature(offset);
                }
            }
        }

        return null;
    }

    public List<Signature> findOccurrences(Signature signature) {
        Definition definition = null;
        if (signature instanceof Definition) {
            definition = (Definition) signature;
        } else if (signature instanceof Usage) {
            definition = findDefinition((Usage) signature);
        }

        if (definition == null) {
            return Collections.emptyList();
        }

        List<Signature> occurrences = new ArrayList<Signature>();
        occurrences.add(definition);

        findUsages(definition, occurrences);

        return occurrences;
    }

    public Definition findDefinition(Usage usage) {
        Scope closestScope = usage.getEnclosingScope();
        return findDefinitionInScope(closestScope, usage);
    }

    private Definition findDefinitionInScope(Scope scope, Usage usage) {
        for (Definition definition : scope.getDefinitions()) {
            /** @todo also compare arity etc */
            if (definition.getName().equals(usage.getName())) {
                return definition;
            }
        }

        Scope parentScope = scope.getParent();
        if (parentScope != null) {
            return parentScope.findDefinitionInScope(parentScope, usage);
        }

        return null;
    }

    public void findUsages(Definition definition, List<Signature> usages) {
        Scope enclosingScope = definition.getEnclosingScope();
        findUsagesInScope(enclosingScope, definition, usages);
    }

    private void findUsagesInScope(Scope scope, Definition definition, List<Signature> usages) {
        for (Usage usage : scope.getUsages()) {
            if (definition.getName().equals(usage.getName())) {
                usages.add(usage);
            }
        }

        for (Scope child : scope.getScopes()) {
            findUsagesInScope(child, definition, usages);
        }
    }

    @Override
    public String toString() {
        return "Scope(Binding=" + bindingDefinition + "," + getRange() + ",defs=" + getDefinitions() + ",usages=" + getUsages() + ")";
    }

    private static class ScopeComparator implements Comparator<Scope> {

        public int compare(Scope o1, Scope o2) {
            return o1.getRange().getStart() < o2.getRange().getStart() ? -1 : 1;
        }
    }

    private static class SignatureComparator implements Comparator<Signature> {

        public int compare(Signature o1, Signature o2) {
            return o1.getNameRange().getStart() < o2.getNameRange().getStart() ? -1 : 1;
        }
    }
}

