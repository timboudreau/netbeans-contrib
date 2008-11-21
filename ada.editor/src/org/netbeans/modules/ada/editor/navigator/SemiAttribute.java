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
package org.netbeans.modules.ada.editor.navigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.indexer.AdaIndex;
import org.netbeans.modules.ada.editor.indexer.IndexedElement;
import org.netbeans.modules.ada.editor.indexer.IndexedPackageBody;
import org.netbeans.modules.ada.editor.indexer.IndexedPackageSpec;
import org.netbeans.modules.ada.editor.indexer.IndexedVariable;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement.Kind;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.annotations.CheckForNull;
import org.openide.util.Union2;

/**
 *
 * @author Jan Lahoda, Radek Matous
 */
public class SemiAttribute extends DefaultVisitor {

    public DefinitionScope global;
    private Stack<DefinitionScope> scopes = new Stack<DefinitionScope>();
    private Map<ASTNode, AttributedElement> node2Element = new HashMap<ASTNode, AttributedElement>();
    private int offset;
    private CompilationInfo info;
    private Stack<ASTNode> nodes = new Stack<ASTNode>();

    public SemiAttribute(CompilationInfo info) {
        this(info, -1);
    }

    public SemiAttribute(CompilationInfo info, int o) {
        this.offset = o;
        this.info = info;
        scopes.push(global = new DefinitionScope());
    }

    @Override
    public void scan(ASTNode node) {
        if (node == null) {
            return;
        }

        if ((offset != (-1) && offset <= node.getStartOffset())) {
            throw new Stop();
        }

        nodes.push(node);

        super.scan(node);

        nodes.pop();

        if ((offset != (-1) && offset <= node.getEndOffset())) {
            throw new Stop();
        }
    }

    @Override
    public void visit(Program program) {
        //functions defined on top-level of the current file are visible before declared:
        performEnterPass(global, program.getStatements());
        //enterAllIndexedClasses();
        super.visit(program);
    }

    @Override
    public void visit(Variable node) {
        if (!node2Element.containsKey(node)) {
            String name = extractVariableName(node);
            if (name != null) {
                node2Element.put(node, lookup(name, Kind.VARIABLE));
            }
        }

        super.visit(node);
    }

    @Override
    public void visit(PackageSpecification node) {
        String name = node.getName().getName();
        PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_SPEC, node);

        node2Element.put(node, ce);

        scopes.push(ce.enclosedElements);

        if (node.getBody() != null) {
            performEnterPass(ce.enclosedElements, node.getBody().getStatements());
        }

        super.visit(node);

        scopes.pop();
    }

    @Override
    public void visit(PackageBody node) {
        String name = node.getName().getName();
        PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_BODY, node);

        node2Element.put(node, ce);

        scopes.push(ce.enclosedElements);

        if (node.getBody() != null) {
            performEnterPass(ce.enclosedElements, node.getBody().getStatements());
        }

        super.visit(node);

        scopes.pop();
    }

    private PackageElement getCurrentClassElement() {
        PackageElement c = null;
        for (int i = scopes.size() - 1; i >= 0; i--) {
            DefinitionScope scope = scopes.get(i);
            if (scope != null && scope.enclosingClass != null) {
                c = scope.enclosingClass;
                break;
            }
        }
        return c;
    }

    private AttributedElement enterGlobalVariable(String name) {
        AttributedElement g = global.lookup(name, Kind.VARIABLE);

        if (g == null) {
            //XXX: untested:
            g = global.enterWrite(name, Kind.VARIABLE, (ASTNode) null);
        }

        scopes.peek().enter(name, Kind.VARIABLE, g);

        return g;
    }

    private String getContextClassName() {
        String contextClassName = null;
        Enumeration<DefinitionScope> elements = scopes.elements();
        while (elements.hasMoreElements()) {
            DefinitionScope nextElement = elements.nextElement();
            if (nextElement.enclosingClass != null) {
                contextClassName = nextElement.enclosingClass.getName();
            }
        }
        return contextClassName;
    }

    private CompilationInfo getInfo() {
        return info;
    }

    private AttributedElement lookup(String name, Kind k) {
        DefinitionScope ds = scopes.peek();

        AttributedElement e;

        switch (k) {
            case FUNCTION:
            case PACKAGE_BODY:
            case PACKAGE_SPEC:
                e = global.lookup(name, k);
                break;
            default:
                e = ds.lookup(name, k);
                break;
        }

        if (e != null) {
            return e;
        }

        switch (k) {
            case FUNCTION:
            case PACKAGE_BODY:
            case PACKAGE_SPEC:
                return global.enterWrite(name, k, (ASTNode) null);
            default:
                return ds.enterWrite(name, k, (ASTNode) null);
        }
    }

    public Collection<AttributedElement> getGlobalElements(Kind k) {
        return global.getElements(k);
    }

    public Collection<AttributedElement> getNamedGlobalElements(Kind k, String... filterNames) {
        Map<String, AttributedElement> name2El = global.name2Writes.get(k);

        List<AttributedElement> retval = new ArrayList<AttributedElement>();
        for (String fName : filterNames) {
            if (fName.equals("self")) {//NOI18N
                String ctxName = getContextClassName();
                if (ctxName != null) {
                    fName = ctxName;
                }
            }
            if (Kind.PACKAGE_SPEC.equals(k) && fName.equals("parent")) {//NOI18N
                Collection<AttributedElement> values = name2El.values();
                if (name2El != null) {
                    for (AttributedElement ael : values) {
                        if (ael instanceof PackageElement) {
                            PackageElement ce = (PackageElement) ael;
                            PackageElement superClass = ce.getSuperClass();
                            if (superClass != null) {
                                retval.add(superClass);
                            }
                        }
                    }
                }
            } else {
                AttributedElement el = (name2El != null) ? name2El.get(fName) : null;
                if (el != null) {
                    retval.add(el);
                } else {
                    Index i = getInfo().getIndex(AdaMimeResolver.ADA_MIME_TYPE);
                    AdaIndex index = AdaIndex.get(i);
                    for (IndexedPackageSpec m : index.getPackageSpec(null, fName, NameKind.PREFIX)) {
                        String idxName = m.getName();
                        el = global.enterWrite(idxName, Kind.PACKAGE_SPEC, m);
                        retval.add(el);
                    }
                }
            }
        }
        return retval;
    }

    public AttributedElement getElement(ASTNode n) {
        return node2Element.get(n);
    }
    private Collection<IndexedElement> name2ElementCache;

    public void enterAllIndexedClasses() {
        if (name2ElementCache == null) {
            Index i = getInfo().getIndex(AdaMimeResolver.ADA_MIME_TYPE);
            AdaIndex index = AdaIndex.get(i);
            name2ElementCache = new LinkedList<IndexedElement>();
            name2ElementCache.addAll(index.getPackageSpec(null, "", NameKind.PREFIX));
            name2ElementCache.addAll(index.getPackageBody(null, "", NameKind.PREFIX));
        }

        for (IndexedElement f : name2ElementCache) {
            if (f instanceof IndexedPackageSpec) {
                global.enterWrite(f.getName(), Kind.PACKAGE_SPEC, f);
            } else if (f instanceof IndexedPackageBody) {
                global.enterWrite(f.getName(), Kind.PACKAGE_BODY, f);
            }
        }
    }

    private void performEnterPass(DefinitionScope scope, Collection<? extends ASTNode> nodes) {
        for (ASTNode n : nodes) {
            if (n instanceof FieldsDeclaration) {
                for (SingleFieldDeclaration f : ((FieldsDeclaration) n).getFields()) {
                    String name = extractVariableName(f.getName());

                    if (name != null) {
                        node2Element.put(n, scope.enterWrite(name, Kind.VARIABLE, n));
                    }
                }
            }

            if (n instanceof PackageSpecification) {
                PackageSpecification node = (PackageSpecification) n;
                String name = node.getName().getName();
                PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_SPEC, node);
                node2Element.put(node, ce);
                if (node.getBody() != null) {
                    performEnterPass(ce.enclosedElements, node.getBody().getStatements());
                }
            }

            if (n instanceof PackageBody) {
                PackageBody node = (PackageBody) n;
                String name = node.getName().getName();
                PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_BODY, node);
                node2Element.put(node, ce);
                if (node.getBody() != null) {
                    performEnterPass(ce.enclosedElements, node.getBody().getStatements());
                }
            }

        }
    }
    private static Map<CompilationInfo, SemiAttribute> info2Attr = new WeakHashMap<CompilationInfo, SemiAttribute>();

    public static SemiAttribute semiAttribute(CompilationInfo info) {
        SemiAttribute a = info2Attr.get(info);

        if (a == null) {
            long startTime = System.currentTimeMillis();

            a = new SemiAttribute(info);
            a.scan(ASTUtils.getRoot(info));

            a.info = null;

            info2Attr.put(info, a);

            long endTime = System.currentTimeMillis();

            Logger.getLogger("TIMER").log(Level.FINE, "SemiAttribute global instance", new Object[]{info.getFileObject(), a});
            Logger.getLogger("TIMER").log(Level.FINE, "SemiAttribute global time", new Object[]{info.getFileObject(), (endTime - startTime)});
        }

        return a;
    }

    public static SemiAttribute semiAttribute(CompilationInfo info, int stopOffset) {
        SemiAttribute a = new SemiAttribute(info, stopOffset);

        try {
            a.scan(ASTUtils.getRoot(info));
        } catch (Stop s) {
        }

        return a;
    }

    private static String name(ASTNode n) {
        if (n instanceof Identifier) {
            return ((Identifier) n).getName();
        }

        return null;
    }

    @CheckForNull
    //TODO converge this method with CodeUtils.extractVariableName()
    public static String extractVariableName(Variable var) {
        String varName = CodeUtils.extractVariableName(var);

        return varName;
    }

    public Collection<PackageElement> getPackagesSpec() {
        Collection<PackageElement> retval = null;
        if (global != null) {
            retval = global.getPackagesSpec();
        } else {
            retval = Collections.emptyList();
        }
        return retval;
    }

    public boolean hasGlobalVisibility(AttributedElement elem) {
        if (elem.isClassMember()) {
            PackageMemberElement cme = (PackageMemberElement) elem;
            boolean isGlobal = hasGlobalVisibility(cme.getPackageElement());
            return isGlobal;
        }
        return (global != null) ? global.getElements(elem.getKind()).contains(elem) : false;
    }

    public static class AttributedElement {

        private List<Union2<ASTNode, IndexedElement>> writes; //aka declarations
        private List<AttributedType> writesTypes;
        private String name;
        private Kind k;

        public AttributedElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            this(n, name, k, null);
        }

        public AttributedElement(Union2<ASTNode, IndexedElement> n, String name, Kind k, AttributedType type) {
            this.writes = new LinkedList<Union2<ASTNode, IndexedElement>>();
            this.writesTypes = new LinkedList<AttributedType>();
            this.writes.add(n);

            this.writesTypes.add(type);
            this.name = name;
            this.k = k;
        }

        public boolean isClassMember() {
            return false;
        }

        public List<Union2<ASTNode, IndexedElement>> getWrites() {
            return writes;
        }

        public Kind getKind() {
            return k;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AttributedElement)) {
                return false;
            }
            AttributedElement element = (AttributedElement) obj;
            return this.name.equals(element.name) && this.k.equals(element.k);
        }

        void addWrite(Union2<ASTNode, IndexedElement> node, AttributedType type) {
            writes.add(node);
            writesTypes.add(type);
        }

        Types getTypes() {
            return new Types(this);
        }

        public String getScopeName() {
            String retval = "";//NOI18N
            Types types = getTypes();
            for (int i = 0; i < types.size(); i++) {
                AttributedType type = types.getType(i);
                if (type != null) {
                    retval = type.getTypeName();
                    break;
                }
            }
            return retval;
        }

        public enum Kind {

            VARIABLE, PROCEDURE, FUNCTION, PACKAGE_SPEC, PACKAGE_BODY, CONST;
        }
    }

    private static class Types {

        private AttributedElement el;

        Types(AttributedElement el) {
            this.el = el;
        }

        int size() {
            return el.writesTypes.size();
        }

        AttributedType getType(int idx) {
            return el.writesTypes.get(idx);
        }
    }

    public static class PackageMemberElement extends AttributedElement {

        private PackageElement classElement;
        int modifier = -1;

        public PackageMemberElement(Union2<ASTNode, IndexedElement> n, PackageElement classElement, String name, Kind k) {
            super(n, name, k);
            this.classElement = classElement;
            assert classElement != null;
        }

        public String getClassName() {
            return getPackageElement().getName();
        }

        @Override
        public String getScopeName() {
            return getClassName();
        }

        public PackageElement getPackageElement() {
            return classElement;
        }

        @Override
        public boolean isClassMember() {
            return true;
        }

        public PackageMemberKind getClassMemberKind() {
            PackageMemberKind retval = null;
            switch (getKind()) {
                case CONST:
                    retval = PackageMemberKind.CONST;
                    break;
                case FUNCTION:
                    retval = PackageMemberKind.METHOD;
                    break;
                case VARIABLE:
                    retval = PackageMemberKind.FIELD;
                    break;
                default:
                    assert false;

            }
            assert retval != null;
            return retval;
        }

        public enum PackageMemberKind {

            FIELD, METHOD, CONST;
        }
    }

    public class PackageElement extends AttributedElement {

        private final DefinitionScope enclosedElements;
        private PackageElement superClass;
        private Set<PackageElement> ifaces = new HashSet<PackageElement>();
        private boolean initialized;

        public PackageElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            super(n, name, k);
            enclosedElements = new DefinitionScope(this);
        }

        public AttributedElement lookup(String name, Kind k) {
            AttributedElement el = enclosedElements.lookup(name, k);
            if (el != null) {
                return el;
            }
            Index i = getInfo().getIndex(AdaMimeResolver.ADA_MIME_TYPE);
            AdaIndex index = AdaIndex.get(i);
            int attrs = AdaIndex.ANY_ATTR;

            switch (k) {
                case VARIABLE:
                    for (IndexedVariable m : index.getAllFields(null, getName(), name, NameKind.PREFIX, attrs)) {
                        String idxName = m.getName();
                        idxName = (idxName.startsWith("$")) ? idxName.substring(1) : idxName;
                        enclosedElements.enterWrite(idxName, Kind.VARIABLE, m);
                    }
                    break;

            }
            return enclosedElements.lookup(name, k);
        }

        public Collection<AttributedElement> getElements(Kind k) {
            List<AttributedElement> elements = new ArrayList<AttributedElement>();

            getElements0(elements, k);

            return Collections.unmodifiableList(elements);
        }

        public Collection<AttributedElement> getNamedElements(Kind k, String... filterNames) {
            Collection<AttributedElement> elements = getElements(k);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (String fName : filterNames) {
                for (AttributedElement el : elements) {
                    if (el.getName().equals(fName)) {
                        retval.add(el);
                    }
                }
            }
            return retval;
        }

        public Collection<AttributedElement> getMethods() {
            return getElements(Kind.FUNCTION);
        }

        public Collection<AttributedElement> getFields() {
            Collection<AttributedElement> elems = getElements(Kind.VARIABLE);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (AttributedElement elm : elems) {
                if (!elm.getName().equals("this")) {
                    retval.add(elm);
                }
            }
            return retval;
        }

        public PackageElement getSuperClass() {
            return superClass;
        }

        private void getElements0(List<AttributedElement> elements, Kind k) {
            elements.addAll(enclosedElements.getElements(k));

            if (superClass != null) {
                superClass.getElements0(elements, k);
            }
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialized() {
            initialized = true;
        }
    }

    public class FunctionElement extends AttributedElement {

        private final DefinitionScope enclosedElements;
        private boolean initialized;

        public FunctionElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            super(n, name, k);
            enclosedElements = new DefinitionScope(this);
        }

        public AttributedElement lookup(String name, Kind k) {
            return enclosedElements.lookup(name, k);
        }

        public Collection<AttributedElement> getElements(Kind k) {
            List<AttributedElement> elements = new ArrayList<AttributedElement>();

            getElements0(elements, k);

            return Collections.unmodifiableList(elements);
        }

        public Collection<AttributedElement> getNamedElements(Kind k, String... filterNames) {
            Collection<AttributedElement> elements = getElements(k);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (String fName : filterNames) {
                for (AttributedElement el : elements) {
                    if (el.getName().equals(fName)) {
                        retval.add(el);
                    }
                }
            }
            return retval;
        }

        public Collection<AttributedElement> getVariables() {
            return getElements(Kind.VARIABLE);
        }

        private void getElements0(List<AttributedElement> elements, Kind k) {
            elements.addAll(enclosedElements.getElements(k));
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialized() {
            initialized = true;
        }
    }

    public class DefinitionScope {

        private final Map<Kind, Map<String, AttributedElement>> name2Writes = new HashMap<Kind, Map<String, AttributedElement>>();
//        private final Map<AttributedElement, ASTNode> reads = new HashMap<AttributedElement, ASTNode>();
        private boolean classScope;
        private boolean functionScope;
        private AttributedElement thisVar;
        private PackageElement enclosingClass;
        private FunctionElement enclosingFunction;

        public DefinitionScope() {
        }

        public DefinitionScope(PackageElement enclosingClass) {
            this.enclosingClass = enclosingClass;
            this.classScope = enclosingClass != null;


            if (classScope) {
                thisVar = enterWrite("this", Kind.VARIABLE, (ASTNode) null, new ClassType(enclosingClass));
            }
        }

        public DefinitionScope(FunctionElement enclosingFunction) {
            this.enclosingFunction = enclosingFunction;
            this.functionScope = enclosingFunction != null;
        }

        public AttributedElement enterWrite(String name, Kind k, ASTNode node) {
            return enterWrite(name, k, node, null);
        }

        public AttributedElement enterWrite(String name, Kind k, ASTNode node, AttributedType type) {
            return enterWrite(name, k, Union2.<ASTNode, IndexedElement>createFirst(node), type);
        }

        public AttributedElement enterWrite(String name, Kind k, IndexedElement el) {
            return enterWrite(name, k, Union2.<ASTNode, IndexedElement>createSecond(el), null);
        }

        private AttributedElement enterWrite(String name, Kind k, Union2<ASTNode, IndexedElement> node, AttributedType type) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);

            if (name2El == null) {
                name2Writes.put(k, name2El = new HashMap<String, AttributedElement>());
            }

            AttributedElement el = name2El.get(name);

            if (el == null) {
                if (k == Kind.PACKAGE_SPEC || k == Kind.PACKAGE_BODY) {
                    el = new PackageElement(node, name, k);
                } else {
                    if (classScope && !Arrays.asList(new String[]{"this"}).contains(name)) {
                        switch (k) {
                            case CONST:
                            case FUNCTION:
                            case VARIABLE:
                                el = new PackageMemberElement(node, enclosingClass, name, k);
                                break;
                            default:
                                assert false;
                        }
                    } else {
                        if (k == Kind.FUNCTION) {
                            el = new FunctionElement(node, name, k);
                        } else if (k == Kind.VARIABLE) {
                            if (type == null && functionScope && enclosingFunction != null) {
                                type = new FunctionType(enclosingFunction);
                            }
                            el = new AttributedElement(node, name, k, type);
                        } else {
                            el = new AttributedElement(node, name, k, type);
                        }
                    }
                }

                name2El.put(name, el);
            } else {
                el.addWrite(node, type);
            }

            return el;
        }

        public AttributedElement enter(String name, Kind k, AttributedElement el) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El == null) {
                name2Writes.put(k, name2El = new HashMap<String, AttributedElement>());
            }
            name2El.put(name, el);
            return el;
        }

        public AttributedElement lookup(String name, Kind k) {
            AttributedElement el = null;
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El != null) {
                el = name2El.get(name);
            }
            if (el == null) {
                Index i = getInfo().getIndex(AdaMimeResolver.ADA_MIME_TYPE);
                AdaIndex index = AdaIndex.get(i);
                switch (k) {
                    case CONST:
                        //for (IndexedConstant m : index.getConstants(null, name, NameKind.PREFIX)) {
                        //    String idxName = m.getName();
                        //    el = enterWrite(idxName, Kind.CONST, m);
                        //}
                        break;
                }
            }
            return el;
        }

        public Collection<AttributedElement> getElements(Kind k) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El != null) {
                return Collections.unmodifiableCollection(name2El.values());
            }
            return Collections.emptyList();
        }

        public Collection<AttributedElement> getFunctions() {
            return getElements(Kind.FUNCTION);
        }

        public Collection<AttributedElement> getVariables() {
            return getElements(Kind.VARIABLE);
        }

        private Collection<AttributedElement> getConstants() {
            return getElements(Kind.CONST);
        }

        public Collection<PackageElement> getPackagesSpec() {
            Collection<PackageElement> retval = new LinkedHashSet<PackageElement>();
            Collection<AttributedElement> elements = getElements(Kind.PACKAGE_SPEC);
            for (AttributedElement el : elements) {
                assert el instanceof PackageElement;
                retval.add((PackageElement) el);
            }
            return retval;
        }
    }

    private static final class Stop extends Error {
    }

    public static abstract class AttributedType {

        public abstract String getTypeName();
    }

    public static class ClassType extends AttributedType {

        private PackageElement element;

        public ClassType(PackageElement element) {
            this.element = element;
        }

        public PackageElement getElement() {
            return element;
        }

        @Override
        public String getTypeName() {
            return getElement().getName();
        }
    }

    public static class FunctionType extends AttributedType {

        private FunctionElement element;

        public FunctionType(FunctionElement element) {
            this.element = element;
        }

        public FunctionElement getElement() {
            return element;
        }

        @Override
        public String getTypeName() {
            return getElement().getName();
        }
    }
}
