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
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 *
 * @author Caoyuan Deng
 */
public class AstScope implements Iterable<AstScope> {

    private AstDef bindingDef;
    private AstScope parent;
    private List<AstScope> scopes;
    private List<AstDef> defs;
    private List<AstRef> refs;
    private List<AstExpr> exprs;
    private boolean scopesSorted;
    private boolean defsSorted;
    private boolean refsSorted;
    private boolean exprsSorted;
    private Token[] boundsTokens;

    public AstScope(Token[] boundsTokens) {
        assert boundsTokens.length == 2;
        this.boundsTokens = boundsTokens;
    }

    public Token[] getBoundsTokens() {
        return boundsTokens;
    }

    public OffsetRange getRange(TokenHierarchy th) {
        return new OffsetRange(getBoundsOffset(th), getBoundsEndOffset(th));
    }

    public int getBoundsOffset(TokenHierarchy th) {
        return boundsTokens[0].offset(th);
    }

    public int getBoundsEndOffset(TokenHierarchy th) {
        return boundsTokens[1].offset(th) + boundsTokens[1].length();
    }

    public void setBindingDef(AstDef bindingDef) {
        this.bindingDef = bindingDef;
    }

    public AstDef getBindingDef() {
        return bindingDef;
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

    public List<AstDef> getDefs() {
        if (defs == null) {
            return Collections.emptyList();
        }
        return defs;
    }

    public List<AstRef> getRefs() {
        if (refs == null) {
            return Collections.emptyList();
        }
        return refs;
    }

    public List<AstExpr> getExprs() {
        if (exprs == null) {
            return Collections.emptyList();
        }
        return exprs;
    }

    void addScope(AstScope scope) {
        if (scopes == null) {
            scopes = new ArrayList<AstScope>();
        }
        scopes.add(scope);
        scope.parent = this;
    }

    void addDef(AstDef def) {
        if (defs == null) {
            defs = new ArrayList<AstDef>();
        }
        defs.add(def);
        def.setEnclosingScope(this);
    }

    public void addRef(AstRef ref) {
        if (refs == null) {
            refs = new ArrayList<AstRef>();
        }
        refs.add(ref);
        ref.setEnclosingScope(this);
    }

    void addExpr(AstExpr expr) {
        if (exprs == null) {
            exprs = new ArrayList<AstExpr>();
        }
        exprs.add(expr);
        expr.setEnclosingScope(this);
    }

    public Iterator<AstScope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<AstScope>emptySet().iterator();
        }
    }

    public AstElement getDefRef(TokenHierarchy th, int offset) {
        // Always seach refs first, since ref can be included in def
        if (refs != null) {
            if (!refsSorted) {
                Collections.sort(refs, new RefComparator(th));
                refsSorted = true;
            }
            int low = 0;
            int high = refs.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstRef middle = refs.get(mid);
                if (offset < middle.getPickOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getPickEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (defs != null) {
            if (!defsSorted) {
                Collections.sort(defs, new DefComparator(th));
                defsSorted = true;
            }
            int low = 0;
            int high = defs.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstDef middle = defs.get(mid);
                if (offset < middle.getPickOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getPickEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (scopes != null) {
            if (!scopesSorted) {
                Collections.sort(scopes, new ScopeComparator(th));
                scopesSorted = true;
            }
            int low = 0;
            int high = scopes.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstScope middle = scopes.get(mid);
                if (offset < middle.getBoundsOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getBoundsEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle.getDefRef(th, offset);
                }
            }
        }

        return null;
    }

    public AstExpr getExpr(TokenHierarchy th, int offset) {
        if (exprs != null) {
            if (!exprsSorted) {
                Collections.sort(exprs, new ExprComparator(th));
                exprsSorted = true;
            }
            int low = 0;
            int high = exprs.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstExpr middle = exprs.get(mid);
                if (offset < middle.getBoundsOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getBoundsEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (scopes != null) {
            if (!scopesSorted) {
                Collections.sort(scopes, new ScopeComparator(th));
                scopesSorted = true;
            }
            int low = 0;
            int high = scopes.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstScope middle = scopes.get(mid);
                if (offset < middle.getBoundsOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getBoundsEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle.getExpr(th, offset);
                }
            }
        }

        return null;
    }

    public List<AstElement> findOccurrences(AstElement element) {
        AstDef def = null;
        if (element instanceof AstDef) {
            def = (AstDef) element;
        } else if (element instanceof AstRef) {
            def = findDef((AstRef) element);
        }

        if (def == null) {
            return Collections.emptyList();
        }

        List<AstElement> occurrences = new ArrayList<AstElement>();
        occurrences.add(def);

        findRefs(def, occurrences);

        return occurrences;
    }

    public AstDef findDef(AstElement element) {
        AstDef def = null;

        if (element instanceof AstDef) {
            def = (AstDef) element;
        } else if (element instanceof AstRef) {
            def = findDef((AstRef) element);
        }

        return def;
    }

    public AstDef findDef(AstRef ref) {
        AstScope closestScope = ref.getEnclosingScope();
        return findDefInScope(closestScope, ref);
    }

    private AstDef findDefInScope(AstScope scope, AstRef ref) {
        for (AstDef def : scope.getDefs()) {
            if (def.referredBy(ref)) {
                return def;
            }
        }

        AstScope parentScope = scope.getParent();
        if (parentScope != null) {
            return parentScope.findDefInScope(parentScope, ref);
        }

        return null;
    }

    public void findRefs(AstDef def, List<AstElement> refs) {
        AstScope enclosingScope = def.getEnclosingScope();
        findRefsInScope(enclosingScope, def, refs);
    }

    private void findRefsInScope(AstScope scope, AstDef def, List<AstElement> refs) {
        // find if there is closest override def, if so, we shoud bypass now :
        for (AstDef _def : scope.getDefs()) {
            if (_def != def && _def.mayEqual(def)) {
                return;
            }
        }

        for (AstRef ref : scope.getRefs()) {
            if (def.referredBy(ref)) {
                refs.add(ref);
            }
        }

        for (AstScope _scope : scope.getScopes()) {
            findRefsInScope(_scope, def, refs);
        }
    }

    private boolean contains(TokenHierarchy th, int offset) {
        return offset >= getBoundsOffset(th) && offset < getBoundsEndOffset(th);
    }

    public AstScope getClosestScope(TokenHierarchy th, int offset) {
        AstScope result = null;

        if (scopes != null) {
            /** search children first */
            for (AstScope child : scopes) {
                if (child.contains(th, offset)) {
                    result = child.getClosestScope(th, offset);
                    break;
                }
            }
        }
        if (result != null) {
            return result;
        } else {
            if (this.contains(th, offset)) {
                return this;
            } else {
                /* we should return null here, since it may under a parent context's call, 
                 * we shall tell the parent there is none in this and children of this
                 */
                return null;
            }
        }
    }

    public <T extends AstDef> List<T> getDefsInScope(Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        getDefsInScopeRecursively(clazz, result);

        return result;
    }

    private <T extends AstDef> void getDefsInScopeRecursively(Class<T> clazz, List<T> result) {
        for (AstDef def : getDefs()) {
            if (clazz.isInstance(def)) {
                result.add((T) def);
            }
        }

        AstScope parentScope = getParent();
        if (parentScope != null) {
            parentScope.getDefsInScopeRecursively(clazz, result);
        }
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz, TokenHierarchy th, int offset) {
        AstScope scope = getClosestScope(th, offset);
        return scope.getEnclosingDef(clazz);
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz) {
        AstDef binding = getBindingDef();
        if (binding != null && clazz.isInstance(binding)) {
            return (T) binding;
        } else {
            AstScope parentScope = getParent();
            if (parentScope != null) {
                return parentScope.getEnclosingDef(clazz);
            } else {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return "Scope(Binding=" + bindingDef + "," + ",defs=" + getDefs() + ",refs=" + getRefs() + ")";
    }

    private static class ScopeComparator implements Comparator<AstScope> {

        private TokenHierarchy th;

        public ScopeComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstScope o1, AstScope o2) {
            return o1.getBoundsOffset(th) < o2.getBoundsOffset(th) ? -1 : 1;
        }
    }

    private static class ExprComparator implements Comparator<AstExpr> {

        private TokenHierarchy th;

        public ExprComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstExpr o1, AstExpr o2) {
            return o1.getBoundsOffset(th) < o2.getBoundsOffset(th) ? -1 : 1;
        }
    }

    private static class DefComparator implements Comparator<AstDef> {

        private TokenHierarchy th;

        public DefComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstDef o1, AstDef o2) {
            return o1.getPickOffset(th) < o2.getPickOffset(th) ? -1 : 1;
        }
    }

    private static class RefComparator implements Comparator<AstRef> {

        private TokenHierarchy th;

        public RefComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstRef o1, AstRef o2) {
            return o1.getPickOffset(th) < o2.getPickEndOffset(th) ? -1 : 1;
        }
    }
}

