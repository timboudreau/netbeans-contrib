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
package org.netbeans.modules.scala.editing.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.OffsetRange;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.Type;

/**
 *
 * @author Caoyuan Deng
 */
public class AstScope implements Iterable<AstScope> {

    private AstDef bindinDef;
    private AstScope parent;
    private List<AstScope> scopes;
    private List<AstDef> defs;
    private List<AstRef> refs;
    private boolean scopesSorted;
    private boolean defsSorted;
    private boolean refsSorted;
    private Token[] boundsTokens;

    public AstScope(Token... boundsTokens) {
        if (boundsTokens != null) {
            assert boundsTokens.length <= 2;
            if (boundsTokens.length == 1) {
                setBoundsToken(boundsTokens[0]);
            } else if (boundsTokens.length == 2) {
                setBoundsToken(boundsTokens[0]);
                setBoundsEndToken(boundsTokens[1]);
            }
        }
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public Token getBoundsToken() {
        if (boundsTokens != null) {
            assert boundsTokens.length == 2;
            return boundsTokens[0];
        }
        return null;
    }

    public Token getBoundsEndToken() {
        if (boundsTokens != null) {
            assert boundsTokens.length == 2;
            return boundsTokens[1];
        }
        return null;
    }

    public void setBoundsToken(Token token) {
        if (boundsTokens == null) {
            boundsTokens = new Token[2];
        }
        boundsTokens[0] = token;
    }

    public void setBoundsEndToken(Token token) {
        if (boundsTokens == null) {
            boundsTokens = new Token[2];
        }
        boundsTokens[1] = token;
    }

    public boolean isScopesSorted() {
        return scopesSorted;
    }

    public OffsetRange getRange(TokenHierarchy th) {
        return new OffsetRange(getBoundsOffset(th), getBoundsEndOffset(th));
    }

    public int getBoundsOffset(TokenHierarchy th) {
        Token token = getBoundsToken();
        if (token != null) {
            return token.offset(th);
        }
        return -1;
    }

    public int getBoundsEndOffset(TokenHierarchy th) {
        Token token = getBoundsEndToken();
        if (token != null) {
            return token.offset(th) + token.length();
        }
        return -1;
    }

    public void setBindingDef(AstDef bindingDef) {
        this.bindinDef = bindingDef;
    }

    public AstDef getBindingElement() {
        return bindinDef;
    }

    public AstScope getParent() {
        return parent;
    }

    public List<AstScope> getScopes() {
        return scopes == null ? Collections.<AstScope>emptyList() : scopes;
    }

    public List<AstDef> getDefs() {
        return defs == null ? Collections.<AstDef>emptyList() : defs;
    }

    public List<AstRef> getRefs() {
        return refs == null ? Collections.<AstRef>emptyList() : refs;
    }

    void addScope(AstScope scope) {
        if (scopes == null) {
            scopes = new ArrayList<AstScope>();
        }
        scopes.add(scope);
        scopesSorted = false;
        scope.parent = this;
    }

    /**
     * @param def to be added
     * @retrun added successfully or not
     */
    boolean addDef(AstDef def) {
        Token idToken = def.getIdToken();
        if (idToken == null) {
            return false;
        }
        
        /** a def will always be added */
        getRoot().tryPut(idToken, def);
        if (defs == null) {
            defs = new ArrayList<AstDef>();
        }
        defs.add(def);
        defsSorted = false;
        def.setEnclosingScope(this);
        return true;
    }

    /**
     * @param ref to be added
     * @retrun added successfully or not
     */
    boolean addRef(AstRef ref) {
        Token idToken = ref.getIdToken();
        if (idToken == null) {
            return false;
        }

        /** if a def or ref that corresponds to thi idToekn has been added, this ref won't be added */
        if (getRoot().contains(idToken)) {
            return false;
        }

        getRoot().tryPut(idToken, ref);
        if (refs == null) {
            refs = new ArrayList<AstRef>();
        }
        refs.add(ref);
        refsSorted = false;
        ref.setEnclosingScope(this);
        return true;
    }

    public Iterator<AstScope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<AstScope>emptySet().iterator();
        }
    }

    public AstItem findItemAt(TokenHierarchy th, int offset) {
        // Always seach Ref first, since Ref can be included in Def's range
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
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
                    return middle.findItemAt(th, offset);
                }
            }
        }

        return null;
    }

    public AstItem findItemAt(TokenHierarchy th, Token token) {
        int offset = token.offset(th);
        // Always seach Ref first, since Ref can be included in Def's range
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
                    low = mid + 1;
                } else {
                    Token idToken = middle.getIdToken();
                    if (idToken != null && idToken == token) {
                        return middle;
                    }
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
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
                    return middle.findItemAt(th, offset);
                }
            }
        }

        return null;
    }

    public <T extends AstDef> T findDefAt(Class<T> clazz, TokenHierarchy th, int offset) {
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return clazz.isInstance(middle) ? (T) middle : null;
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
                    return (T) middle.findDefAt(clazz, th, offset);
                }
            }
        }

        return null;
    }

    public <T extends AstRef> T findRefAt(Class<T> clazz, TokenHierarchy th, int offset) {
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
                if (offset < middle.getIdOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getIdEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return clazz.isInstance(middle) ? (T) middle : null;
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
                    return (T) middle.findRefAt(clazz, th, offset);
                }
            }
        }

        return null;
    }

    public List<? extends AstItem> findOccurrences(AstItem item) {
        AstDef def = null;

        if (item instanceof AstDef) {
            def = (AstDef) item;
        } else if (item instanceof AstRef) {
            def = findDefOf((AstRef) item);
        }

        if (def == null) {
            // def maybe remote one, just try to find all same refs
            return findAllRefsSameAs((AstRef) item);
        }

        List<AstItem> occurrences = new ArrayList<AstItem>();
        occurrences.add(def);
        occurrences.addAll(findRefsOf(def));

        return occurrences;
    }

    public AstDef findDefOf(AstItem item) {
        AstDef def = null;

        if (item instanceof AstDef) {
            def = (AstDef) item;
        } else if (item instanceof AstRef) {
            def = findDefOf((AstRef) item);
        }

        return def;
    }

    private AstDef findDefOf(AstRef ref) {
        AstScope closestScope = ref.getEnclosingScope();
        return closestScope.findDefOfUpward(ref);
    }

    private final AstDef findDefOfUpward(AstRef ref) {
        if (defs != null) {
            for (AstDef def : defs) {
                if (def.isReferredBy(ref)) {
                    return def;
                }
            }
        }

        /** search upward */
        if (parent != null) {
            return parent.findDefOfUpward(ref);
        }

        return null;
    }

    public List<AstRef> findRefsOf(AstDef def) {
        List<AstRef> result = new ArrayList<AstRef>();

        AstScope enclosingScope = def.getEnclosingScope();
        enclosingScope.findRefsOfDownward(def, result);

        return result;
    }

    private final void findRefsOfDownward(AstDef def, List<AstRef> result) {
        // find if there is closest override Def, if so, we shoud bypass it now:
        if (defs != null) {
            for (AstDef _def : defs) {
                if (_def != def && _def.mayEqual(def)) {
                    return;
                }
            }
        }

        if (refs != null) {
            for (AstRef ref : refs) {
                if (def.isReferredBy(ref)) {
                    result.add(ref);
                }

            }
        }

        /** search downward */
        if (scopes != null) {
            for (AstScope scope : scopes) {
                scope.findRefsOfDownward(def, result);
            }
        }
    }

    public final AstRootScope getRoot() {
        return parent == null ? (AstRootScope) this : parent.getRoot();
    }

    private List<AstRef> findAllRefsSameAs(AstRef ref) {
        List<AstRef> result = new ArrayList<AstRef>();

        result.add(ref);
        getRoot().findAllRefsSameAsDownward(ref, result);

        return result;
    }

    protected final void findAllRefsSameAsDownward(AstRef ref, List<AstRef> result) {
        if (refs != null) {
            for (AstRef _ref : refs) {
                if (ref.isOccurence(_ref)) {
                    result.add(_ref);
                }

            }
        }

        /** search downward */
        if (scopes != null) {
            for (AstScope scope : scopes) {
                scope.findAllRefsSameAsDownward(ref, result);
            }
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

    public List<AstDef> getVisibleDefs(ElementKind kind) {
        List<AstDef> result = new ArrayList<AstDef>();

        getVisibleDefsUpward(kind, result);

        return result;
    }

    private final void getVisibleDefsUpward(ElementKind kind, List<AstDef> result) {
        if (defs != null) {
            for (AstDef def : defs) {
                if (def.getKind() == kind) {
                    result.add(def);
                }
            }
        }

        if (parent != null) {
            parent.getVisibleDefsUpward(kind, result);
        }
    }

    public AstDef getEnclosinDef(ElementKind kind, TokenHierarchy th, int offset) {
        AstScope scope = getClosestScope(th, offset);
        return scope.getEnclosingDef(kind);
    }

    public AstDef getEnclosingDef(ElementKind kind) {
        if (bindinDef != null && bindinDef.getKind() == kind) {
            return bindinDef;
        } else {
            if (parent != null) {
                return parent.getEnclosingDef(kind);
            } else {
                return null;
            }
        }
    }

    public <T extends AstDef> List<T> getVisibleDefs(Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        getVisibleDefsUpward(clazz, result);

        return result;
    }

    private final <T extends AstDef> void getVisibleDefsUpward(Class<T> clazz, List<T> result) {
        if (defs != null) {
            for (AstDef def : defs) {
                if (clazz.isInstance(def)) {
                    result.add((T) def);
                }
            }
        }

        if (parent != null) {
            parent.getVisibleDefsUpward(clazz, result);
        }
    }

    public <T extends AstDef> T getEnclosinDef(Class<T> clazz, TokenHierarchy th, int offset) {
        AstScope scope = getClosestScope(th, offset);
        return scope.getEnclosingDef(clazz);
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz) {
        if (bindinDef != null && clazz.isInstance(bindinDef)) {
            return (T) bindinDef;
        } else {
            if (parent != null) {
                return parent.getEnclosingDef(clazz);
            } else {
                return null;
            }
        }
    }

    public int findOffetOfDefEqualsTo(Symbol toMatch, TokenHierarchy th) {
        String name = toMatch.nameString();
        Type toMatchType = toMatch.tpe();
        for (AstDef def : getDefs()) {
            Symbol symbol = def.getSymbol();
            if (symbol != null && symbol.nameString().equals(name)) {
                if (symbol.tpe().$eq$colon$eq(toMatchType)) {
                    return def.getIdOffset(th);
                }
            }
        }

        for (AstScope child : getScopes()) {
            return child.findOffetOfDefEqualsTo(toMatch, th);
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Scope(Binding=" + bindinDef + "," + ",defs=" + getDefs() + ",refs=" + getRefs() + ")";
    }
    // ----- inner classes

    private static class ScopeComparator implements Comparator<AstScope> {

        private TokenHierarchy th;

        public ScopeComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstScope o1, AstScope o2) {
            return o1.getBoundsOffset(th) < o2.getBoundsOffset(th) ? -1 : 1;
        }
    }

    private static class DefComparator implements Comparator<AstDef> {

        private TokenHierarchy th;

        public DefComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstDef o1, AstDef o2) {
            return o1.getIdOffset(th) < o2.getIdOffset(th) ? -1 : 1;
        }
    }

    private static class RefComparator implements Comparator<AstRef> {

        private TokenHierarchy th;

        public RefComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstRef o1, AstRef o2) {
            return o1.getIdOffset(th) < o2.getIdEndOffset(th) ? -1 : 1;
        }
    }
    // Sinleton EmptyScope
    private static AstScope EmptyScope;

    public static AstScope emptyScope() {
        if (EmptyScope == null) {
            EmptyScope = new AstScope() {

                @Override
                public int getBoundsOffset(TokenHierarchy th) {
                    return -1;
                }

                @Override
                public int getBoundsEndOffset(TokenHierarchy th) {
                    return -1;
                }
            };
        }

        return EmptyScope;
    }
}

