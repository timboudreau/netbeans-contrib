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
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.java.hints.spi.support.FixFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author lahvac
 */
public class NPECheck extends AbstractHint {

    public NPECheck() {
        super(false, false, HintSeverity.WARNING, "null");
    }

    @Override
    public String getDescription() {
        return "NPE";
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.METHOD);
    }

    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        VisitorImpl v = new VisitorImpl(compilationInfo);
        
        v.scan(treePath, null);
        
        return v.warnings;
    }

    public String getId() {
        return NPECheck.class.getName();
    }

    public String getDisplayName() {
        return "NPE";
    }

    public void cancel() {
    }

    private final class VisitorImpl extends TreePathScanner<State, Void> {
        
        private CompilationInfo info;
        private List<ErrorDescription> warnings;
        
        private Map<VariableElement, State> variable2State = new HashMap<VariableElement, NPECheck.State>();
        private Map<VariableElement, State> testedTo = new HashMap<VariableElement, NPECheck.State>();

        public VisitorImpl(CompilationInfo info) {
            this.info = info;
            this.warnings = new LinkedList<ErrorDescription>();
        }

        @Override
        public State visitAssignment(AssignmentTree node, Void p) {
            Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable()));
            
            if (e == null || !LOCAL_VARIABLES.contains(e.getKind())) {
                return super.visitAssignment(node, p);
            }
            
            State r = scan(node.getExpression(), p);
            
            variable2State.put((VariableElement) e, r);
            
            scan(node.getVariable(), p);
            
            return r;
        }

        @Override
        public State visitVariable(VariableTree node, Void p) {
            Element e = info.getTrees().getElement(getCurrentPath());
            
            if (e == null || !LOCAL_VARIABLES.contains(e.getKind())) {
                return super.visitVariable(node, p);
            }
            
            State r = scan(node.getInitializer(), p);
            
            variable2State.put((VariableElement) e, r);
            
            return r;
        }

        @Override
        public State visitMemberSelect(MemberSelectTree node, Void p) {
            State expr = scan(node.getExpression(), p);
            
            long start = info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), node);
            long end = info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), node);

            if (expr == State.NULL) {
                String displayName = NbBundle.getMessage(NPECheck.class, "ERR_DereferencingNull");
                List<Fix> fixes = Collections.singletonList(FixFactory.createSuppressWarnings(info, getCurrentPath(), "null"));
                warnings.add(ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), displayName, fixes, info.getFileObject(), (int) start, (int) end));
            }

            if (expr == State.POSSIBLE_NULL_REPORT) {
                String displayName = NbBundle.getMessage(NPECheck.class, "ERR_PossiblyDereferencingNull");
                List<Fix> fixes = Collections.singletonList(FixFactory.createSuppressWarnings(info, getCurrentPath(), "null"));
                warnings.add(ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), displayName, fixes, info.getFileObject(), (int) start, (int) end));
            }
            
            return super.visitMemberSelect(node, p);
        }

        @Override
        public State visitLiteral(LiteralTree node, Void p) {
            if (node.getValue() == null) {
                return State.NULL;
            } else {
                return State.NOT_NULL;
            }
        }

        @Override
        public State visitIf(IfTree node, Void p) {
            Map<VariableElement, State> oldTestedTo = testedTo;
            
            testedTo = new HashMap<VariableElement, NPECheck.State>();
            
            scan(node.getCondition(), p);
            
            Map<VariableElement, State> oldVariable2State = variable2State;
            
            variable2State = new HashMap<VariableElement, NPECheck.State>(oldVariable2State);
            variable2State.putAll(testedTo);
            
            scan(node.getThenStatement(), null);
            
            Map<VariableElement, State> variableStatesAfterThen = new HashMap<VariableElement, NPECheck.State>(variable2State);
            
            variable2State = new HashMap<VariableElement, NPECheck.State>(oldVariable2State);
            
            for (Map.Entry<VariableElement, State> entry : testedTo.entrySet()) {
                variable2State.put(entry.getKey(), entry.getValue().reverse());
            }

            scan(node.getElseStatement(), null);
            
            Map<VariableElement, State> variableStatesAfterElse = new HashMap<VariableElement, NPECheck.State>(variable2State);
            
            variable2State = oldVariable2State;
            
            if (node.getElseStatement() == null) {
                for (Entry<VariableElement, State> e : variableStatesAfterThen.entrySet()) {
                    if (testedTo.get(e.getKey()) == State.NULL && e.getValue() == State.NOT_NULL) {
                        variable2State.put(e.getKey(), State.NOT_NULL);
                    }
                }
            } else {
                for (Entry<VariableElement, State> e : variableStatesAfterThen.entrySet()) {
                    State t = e.getValue();
                    State el = variableStatesAfterElse.get(e.getKey());
                    
                    if (t == el) {
                        variable2State.put(e.getKey(), t);
                    } else {
                        if (t == State.NULL && el == State.NOT_NULL) {
                            variable2State.put(e.getKey(), State.POSSIBLE_NULL_REPORT);
                        }
                        if (el == State.NULL && t == State.NOT_NULL) {
                            variable2State.put(e.getKey(), State.POSSIBLE_NULL_REPORT);
                        }
                    }
                }
            }
            
            testedTo = oldTestedTo;
            
            return null;
        }

        @Override
        public State visitBinary(BinaryTree node, Void p) {
            State left = scan(node.getLeftOperand(), p);
            State right = scan(node.getRightOperand(), p);
            
            if (node.getKind() == Kind.EQUAL_TO) {
                if (right == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getLeftOperand()));
                    
                    if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                        testedTo.put((VariableElement) e, State.NULL);
                        
                        return null;
                    }
                }
                if (left == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getRightOperand()));
                    
                    if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                        testedTo.put((VariableElement) e, State.NULL);
                        
                        return null;
                    }
                }
            }
            
            if (node.getKind() == Kind.NOT_EQUAL_TO) {
                if (right == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getLeftOperand()));
                    
                    if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                        testedTo.put((VariableElement) e, State.NOT_NULL);
                        
                        return null;
                    }
                }
                if (left == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getRightOperand()));
                    
                    if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                        testedTo.put((VariableElement) e, State.NOT_NULL);
                        
                        return null;
                    }
                }
            }
            
            return null;
        }

        @Override
        public State visitConditionalExpression(ConditionalExpressionTree node, Void p) {
            //TODO: handle the condition similarly to visitIf
            scan(node.getCondition(), p);
            State thenSection = scan(node.getTrueExpression(), p);
            State elseSection = scan(node.getTrueExpression(), p);
            
            State result;
            
            if (thenSection == elseSection) {
                result = thenSection;
            } else {
                result = NPECheck.State.NOT_NULL.POSSIBLE_NULL;
            }
            
            return result;
        }

        @Override
        public State visitNewClass(NewClassTree node, Void p) {
            super.visitNewClass(node, p);
            
            return State.NOT_NULL;
        }

        @Override
        public State visitMethodInvocation(MethodInvocationTree node, Void p) {
            super.visitMethodInvocation(node, p);
            
            Element e = info.getTrees().getElement(getCurrentPath());
            
            if (e == null || e.getKind() != ElementKind.METHOD) {
                return State.POSSIBLE_NULL;
            }
            
            return getStateFromAnnotations(e);
        }

        @Override
        public State visitIdentifier(IdentifierTree node, Void p) {
            super.visitIdentifier(node, p);
            
            Element e = info.getTrees().getElement(getCurrentPath());

            if (e == null) {
                return State.POSSIBLE_NULL;
            }
            
            if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                State s = variable2State.get(e);
                
                if (s != null) {
                    return s;
                }
            }
            
            return getStateFromAnnotations(e);
        }
        
        private State getStateFromAnnotations(Element e) {
            return getStateFromAnnotations(e, State.POSSIBLE_NULL);
        }
        
        private State getStateFromAnnotations(Element e, State def) {
            for (AnnotationMirror am : e.getAnnotationMirrors()) {
                String simpleName = ((TypeElement) am.getAnnotationType().asElement()).getSimpleName().toString();

                if ("Nullable".equals(simpleName)) {
                    return State.POSSIBLE_NULL_REPORT;
                }

                if ("CheckForNull".equals(simpleName)) {
                    return State.POSSIBLE_NULL_REPORT;
                }

                if ("NotNull".equals(simpleName)) {
                    return State.NOT_NULL;
                }
            }
            
            if (e.getKind() == ElementKind.METHOD) {
                String fqn = getFQN((ExecutableElement) e);
                FileObject source = SourceUtils.getFile(e, info.getClasspathInfo());
                
                if (source != null) {
                    Project owner = FileOwnerQuery.getOwner(source);
                    AuxiliaryConfiguration ac = owner != null ? owner.getLookup().lookup(AuxiliaryConfiguration.class) : null;
                    
                    if (ac != null) {
                        org.w3c.dom.Element configurationFragment = ac.getConfigurationFragment("npe-check-hints", "http://www.netbeans.org/ns/npe-check-hints/1", true);
                        
                        if (configurationFragment != null) {
                            NodeList nl = configurationFragment.getElementsByTagName("check-for-null");

                            for (int cntr = 0; cntr < nl.getLength(); cntr++) {
                                Node n = nl.item(cntr);
                                String text = n.getTextContent();

                                if (fqn.equals(text)) {
                                    return State.POSSIBLE_NULL_REPORT;
                                }
                            }
                        }
                    }
                    
                }
            }

            return def;
        }
    }
    
    private String getFQN(ExecutableElement ee) {
        TypeElement te = (TypeElement) ee.getEnclosingElement();
        
        return te.getQualifiedName().toString() + "." + ee.getSimpleName().toString();
    }
    
    private static final Set<ElementKind> LOCAL_VARIABLES = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.LOCAL_VARIABLE, ElementKind.PARAMETER);
    
    private static enum State {
        NULL,
        POSSIBLE_NULL,
        POSSIBLE_NULL_REPORT,
        NOT_NULL;
        
        public State reverse() {
            switch (this) {
                case NULL:
                    return NOT_NULL;
                case POSSIBLE_NULL:
                case POSSIBLE_NULL_REPORT:
                    return this;
                case NOT_NULL:
                    return NULL;
                default: throw new IllegalStateException();
            }
        }
    }
    
}
