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
import javax.lang.model.element.Element;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 *
 * @author Caoyuan Deng
 */
public class AstScope implements Iterable<AstScope> {

    private AstElement bindingElement;
    private AstScope parent;
    private List<AstScope> scopes;
    private List<AstElement> elements;
    private List<AstMirror> mirrors;
    private List<AstExpression> expressions;
    private boolean scopesSorted;
    private boolean elementsSorted;
    private boolean mirrorsSorted;
    private boolean expressionsSorted;
    private Token[] boundsTokens;

    public AstScope(Token[] boundsTokens) {
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

    public void setBindingDef(AstElement bindingElement) {
        this.bindingElement = bindingElement;
    }

    public AstElement getBindingElement() {
        return bindingElement;
    }

    public AstScope getParent() {
        return parent;
    }

    public List<AstScope> getScopes() {
        return scopes == null ? Collections.<AstScope>emptyList() : scopes;
    }

    public List<AstElement> getElements() {
        return elements == null ? Collections.<AstElement>emptyList() : elements;
    }

    public List<AstMirror> getMirrors() {
        return mirrors == null ? Collections.<AstMirror>emptyList() : mirrors;
    }

    public List<AstExpression> getExpressions() {
        return expressions == null ? Collections.<AstExpression>emptyList() : expressions;
    }

    void addScope(AstScope scope) {
        if (scopes == null) {
            scopes = new ArrayList<AstScope>();
        }
        scopes.add(scope);
        scopesSorted = false;
        scope.parent = this;
    }

    void addElement(AstElement element) {
        if (elements == null) {
            elements = new ArrayList<AstElement>();
        }
        elements.add(element);
        elementsSorted = false;
        element.setEnclosingScope(this);
    }

    public void addMirror(AstMirror mirror) {
        if (mirrors == null) {
            mirrors = new ArrayList<AstMirror>();
        }
        mirrors.add(mirror);
        mirrorsSorted = false;
        mirror.setEnclosingScope(this);
    }

    void addExpression(AstExpression expression) {
        if (expressions == null) {
            expressions = new ArrayList<AstExpression>();
        }
        expressions.add(expression);
        expressionsSorted = false;
        expression.setEnclosingScope(this);
    }

    public Iterator<AstScope> iterator() {
        if (scopes != null) {
            return scopes.iterator();
        } else {
            return Collections.<AstScope>emptySet().iterator();
        }
    }

    public AstNode findElementOrMirror(TokenHierarchy th, int offset) {
        // Always seach Mirror first, since Mirror can be included in Element
        if (mirrors != null) {
            if (!mirrorsSorted) {
                Collections.sort(mirrors, new MirrorComparator(th));
                mirrorsSorted = true;
            }
            int low = 0;
            int high = mirrors.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstMirror middle = mirrors.get(mid);
                if (offset < middle.getPickOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getPickEndOffset(th)) {
                    low = mid + 1;
                } else {
                    return middle;
                }
            }
        }

        if (elements != null) {
            if (!elementsSorted) {
                Collections.sort(elements, new ElementComparator(th));
                elementsSorted = true;
            }
            int low = 0;
            int high = elements.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstElement middle = elements.get(mid);
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
                    return middle.findElementOrMirror(th, offset);
                }
            }
        }

        return null;
    }

    public <T extends AstElement> T findElementAt(Class<T> clazz, TokenHierarchy th, int offset) {
        if (elements != null) {
            if (!elementsSorted) {
                Collections.sort(elements, new ElementComparator(th));
                elementsSorted = true;
            }
            int low = 0;
            int high = elements.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstElement middle = elements.get(mid);
                if (offset < middle.getPickOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getPickEndOffset(th)) {
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
                    return (T) middle.findElementAt(clazz, th, offset);
                }
            }
        }

        return null;
    }

    public <T extends AstMirror> T findMirrorAt(Class<T> clazz, TokenHierarchy th, int offset) {
        if (mirrors != null) {
            if (!mirrorsSorted) {
                Collections.sort(mirrors, new MirrorComparator(th));
                mirrorsSorted = true;
            }
            int low = 0;
            int high = mirrors.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstMirror middle = mirrors.get(mid);
                if (offset < middle.getPickOffset(th)) {
                    high = mid - 1;
                } else if (offset >= middle.getPickEndOffset(th)) {
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
                    return (T) middle.findMirrorAt(clazz, th, offset);
                }
            }
        }

        return null;
    }

    public AstExpression findExpressionAt(TokenHierarchy th, int offset) {
        if (expressions != null) {
            if (!expressionsSorted) {
                Collections.sort(expressions, new ExpressionComparator(th));
                expressionsSorted = true;
            }
            int low = 0;
            int high = expressions.size() - 1;
            while (low <= high) {
                int mid = (low + high) >> 1;
                AstExpression middle = expressions.get(mid);
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
                    return middle.findExpressionAt(th, offset);
                }
            }
        }

        return null;
    }

    public List<AstNode> findOccurrences(AstNode node) {
        AstElement element = null;

        if (node instanceof AstElement) {
            element = (AstElement) node;
        } else if (node instanceof AstMirror) {
            element = findElementOf((AstMirror) node);
        }

        if (element == null) {
            return Collections.emptyList();
        }

        List<AstNode> occurrences = new ArrayList<AstNode>();
        occurrences.add(element);
        occurrences.addAll(findMirrorsOf(element));

        return occurrences;
    }

    public AstElement findElementOf(AstNode node) {
        AstElement element = null;

        if (node instanceof AstElement) {
            element = (AstElement) node;
        } else if (node instanceof AstMirror) {
            element = findElementOf((AstMirror) node);
        }

        return element;
    }

    private AstElement findElementOf(AstMirror mirror) {
        AstScope closestScope = mirror.getEnclosingScope();
        return closestScope.findElementOfUpward(mirror);
    }

    private final AstElement findElementOfUpward(AstMirror mirror) {
        if (elements != null) {
            for (AstElement element : elements) {
                if (element.isMirroredBy(mirror)) {
                    return element;
                }
            }
        }

        /** search upward */
        if (parent != null) {
            return parent.findElementOfUpward(mirror);
        }

        return null;
    }

    public List<AstMirror> findMirrorsOf(AstElement element) {
        List<AstMirror> result = new ArrayList<AstMirror>();

        AstScope enclosingScope = element.getEnclosingScope();
        enclosingScope.findMirrorsOfDownward(element, result);

        return result;
    }

    private final void findMirrorsOfDownward(AstElement element, List<AstMirror> result) {
        // find if there is closest override Element, if so, we shoud bypass it now :
        if (elements != null) {
            for (AstElement _element : elements) {
                if (_element != element && _element.mayEqual(element)) {
                    return;
                }
            }
        }

        if (mirrors != null) {
            for (AstMirror mirror : mirrors) {
                if (element.isMirroredBy(mirror)) {
                    result.add(mirror);
                }

            }
        }

        /** search downward */
        if (scopes != null) {
            for (AstScope scope : scopes) {
                scope.findMirrorsOfDownward(element, result);
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

    public <T extends Element> List<T> getVisibleElements(Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        getVisibleElementsUpward(clazz, result);

        return result;
    }

    private final <T extends Element> void getVisibleElementsUpward(Class<T> clazz, List<T> result) {
        if (elements != null) {
            for (AstElement element : elements) {
                if (clazz.isInstance(element)) {
                    result.add((T) element);
                }
            }
        }

        if (parent != null) {
            parent.getVisibleElementsUpward(clazz, result);
        }
    }

    public <T extends AstElement> T getEnclosingElement(Class<T> clazz, TokenHierarchy th, int offset) {
        AstScope scope = getClosestScope(th, offset);
        return scope.getEnclosingElement(clazz);
    }

    public <T extends AstElement> T getEnclosingElement(Class<T> clazz) {
        if (bindingElement != null && clazz.isInstance(bindingElement)) {
            return (T) bindingElement;
        } else {
            if (parent != null) {
                return parent.getEnclosingElement(clazz);
            } else {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return "Scope(Binding=" + bindingElement + "," + ",elememts=" + getElements() + ",mirrors=" + getMirrors() + ")";
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

    private static class ExpressionComparator implements Comparator<AstExpression> {

        private TokenHierarchy th;

        public ExpressionComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstExpression o1, AstExpression o2) {
            return o1.getBoundsOffset(th) < o2.getBoundsOffset(th) ? -1 : 1;
        }
    }

    private static class ElementComparator implements Comparator<AstElement> {

        private TokenHierarchy th;

        public ElementComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstElement o1, AstElement o2) {
            return o1.getPickOffset(th) < o2.getPickOffset(th) ? -1 : 1;
        }
    }

    private static class MirrorComparator implements Comparator<AstMirror> {

        private TokenHierarchy th;

        public MirrorComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(AstMirror o1, AstMirror o2) {
            return o1.getPickOffset(th) < o2.getPickEndOffset(th) ? -1 : 1;
        }
    }
    // Sinleton EmptyScope
    private static AstScope EmptyScope;

    public static AstScope emptyScope() {
        if (EmptyScope == null) {
            EmptyScope = new AstScope(null) {

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

