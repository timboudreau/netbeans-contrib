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
package org.netbeans.modules.scala.editing.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 *
 * @author Caoyuan Deng
 */
public class AstScope implements Iterable<AstScope> {

    private AstDefinition bindingDefinition;
    private AstScope parent;
    private List<AstScope> scopes;
    private List<AstDefinition> definitions;
    private List<AstUsage> usages;
    private boolean scopesSorted;
    private boolean definitionsSorted;
    private boolean usagesSorted;
    private OffsetRange range;

    public AstScope(OffsetRange range) {
        this.range = range;
    }

    public void setBindingDefinition(AstDefinition bindingDefinition) {
        this.bindingDefinition = bindingDefinition;        
    }
    
    public AstDefinition getBindingDefinition() {
        return bindingDefinition;
    }

    public OffsetRange getRange() {
        return range;
    }

    public AstScope getParent() {
        return parent;
    }

    public List<AstScope> getScopes() {
        if (scopes == null) {
            return Collections.emptyList();
        }
        return scopes;
    }

    public List<AstDefinition> getDefinitions() {
        if (definitions == null) {
            return Collections.emptyList();
        }
        return definitions;
    }

    public List<AstUsage> getUsages() {
        if (usages == null) {
            return Collections.emptyList();
        }
        return usages;
    }

    void addScope(AstScope scope) {
        if (scopes == null) {
            scopes = new ArrayList<AstScope>();
        }
        scopes.add(scope);
        scope.parent = this;
    }

    void addDefinition(AstDefinition definition) {
        if (definitions == null) {
            definitions = new ArrayList<AstDefinition>();
        }
        definitions.add(definition);
        definition.setEnclosingScope(this);
    }

    void addUsage(AstUsage usage) {
        if (usages == null) {
            usages = new ArrayList<AstUsage>();
        }
        usages.add(usage);
        usage.setEnclosingScope(this);
    }

    public Iterator<AstScope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<AstScope>emptySet().iterator();
        }
    }

    public AstElement getElement(int offset) {
        if (definitions != null) {
            if (!definitionsSorted) {
                Collections.sort(definitions, new SignatureComparator());
                definitionsSorted = true;
            }
            int low = 0;
            int high = definitions.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstDefinition middle = definitions.get(mid);
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
                AstUsage middle = usages.get(mid);
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
                AstScope middle = scopes.get(mid);
                if (offset < middle.getRange().getStart()) {
                    high = mid - 1;
                } else if (offset >= middle.getRange().getEnd()) {
                    low = mid + 1;
                } else {
                    return middle.getElement(offset);
                }
            }
        }

        return null;
    }

    public List<AstElement> findOccurrences(AstElement element) {
        AstDefinition definition = null;
        if (element instanceof AstDefinition) {
            definition = (AstDefinition) element;
        } else if (element instanceof AstUsage) {
            definition = findDefinition((AstUsage) element);
        }

        if (definition == null) {
            return Collections.emptyList();
        }

        List<AstElement> occurrences = new ArrayList<AstElement>();
        occurrences.add(definition);

        findUsages(definition, occurrences);

        return occurrences;
    }

    public AstDefinition findDefinition(AstUsage usage) {
        AstScope closestScope = usage.getEnclosingScope();
        return findDefinitionInScope(closestScope, usage);
    }

    private AstDefinition findDefinitionInScope(AstScope scope, AstUsage usage) {
        for (AstDefinition definition : scope.getDefinitions()) {
            /** @todo also compare arity etc */
            if (definition.getName().equals(usage.getName())) {
                return definition;
            }
        }

        AstScope parentScope = scope.getParent();
        if (parentScope != null) {
            return parentScope.findDefinitionInScope(parentScope, usage);
        }

        return null;
    }

    public void findUsages(AstDefinition definition, List<AstElement> usages) {
        AstScope enclosingScope = definition.getEnclosingScope();
        findUsagesInScope(enclosingScope, definition, usages);
    }

    private void findUsagesInScope(AstScope scope, AstDefinition definition, List<AstElement> usages) {
        for (AstUsage usage : scope.getUsages()) {
            if (definition.getName().equals(usage.getName())) {
                usages.add(usage);
            }
        }

        for (AstScope child : scope.getScopes()) {
            findUsagesInScope(child, definition, usages);
        }
    }
    
    
    private boolean contains(int offset) {
        return offset >= range.getStart() && offset < range.getEnd();
    }
    
    public AstScope getClosestScope(int offset) {
        AstScope result = null;
        
        if (scopes != null) {
            /** search children first */
            for (AstScope child : scopes) {
                if (child.contains(offset)) {
                    result = child.getClosestScope(offset);
		    break;
		}
	    }  
	}
	if (result != null) {
            return result;
	} else {
            if (this.contains(offset)) {
                return this;
	    } else {
                /* we should return null here, since it may under a parent context's call, 
		 * we shall tell the parent there is none in this and children of this
		 */
                return null; 
	    } 
	}
    }
    
    
    public AstDefinition getEnclosingDefinition(ElementKind kind, int offset) {
        AstScope context = getClosestScope(offset);
        return context.getEnclosingDefinitionRecursively(kind);
    }
    
    private AstDefinition getEnclosingDefinitionRecursively(ElementKind kind) {
        AstDefinition binding = getBindingDefinition();
        if (binding != null && binding.getKind() == kind) {
            return binding;
        } else {
            AstScope parentScope = getParent();
            if (parentScope != null) {
                return parentScope.getEnclosingDefinitionRecursively(kind);
            } else {
                return null;
            }
        }        
    }
       

    @Override
    public String toString() {
        return "Scope(Binding=" + bindingDefinition + "," + getRange() + ",defs=" + getDefinitions() + ",usages=" + getUsages() + ")";
    }

    private static class ScopeComparator implements Comparator<AstScope> {

        public int compare(AstScope o1, AstScope o2) {
            return o1.getRange().getStart() < o2.getRange().getStart() ? -1 : 1;
        }
    }

    private static class SignatureComparator implements Comparator<AstElement> {

        public int compare(AstElement o1, AstElement o2) {
            return o1.getNameRange().getStart() < o2.getNameRange().getStart() ? -1 : 1;
        }
    }
}

