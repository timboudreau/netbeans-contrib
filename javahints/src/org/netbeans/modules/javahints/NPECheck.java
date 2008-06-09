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
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
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
import org.netbeans.api.project.ProjectUtils;
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

import static org.netbeans.modules.javahints.NPECheck.State.*;

/**
 *
 * @author lahvac
 */
public class NPECheck extends AbstractHint {

    public NPECheck() {
        super(false, true, HintSeverity.WARNING, "null");
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

        private void report(TreePath path, String key) {
            long start = info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), path.getLeaf());
            long end = info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), path.getLeaf());

            String displayName = NbBundle.getMessage(NPECheck.class, key);
            List<Fix> fixes = Collections.singletonList(FixFactory.createSuppressWarnings(info, path, "null"));
            warnings.add(ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), displayName, fixes, info.getFileObject(), (int) start, (int) end));
        }
        
        @Override
        public State visitAssignment(AssignmentTree node, Void p) {
            Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable()));
            
            if (e == null || !VARIABLE_ELEMENT.contains(e.getKind())) {
                return super.visitAssignment(node, p);
            }
            
            State r = scan(node.getExpression(), p);
            
            variable2State.put((VariableElement) e, r);
            
            scan(node.getVariable(), p);
            
            State elementState = getStateFromAnnotations(e);
            
            if (elementState != null && elementState.isNotNull()) {
                String key = null;

                if (r == NULL) {
                    key = "ERR_AssigningNullToNotNull";
                }

                if (r == POSSIBLE_NULL_REPORT) {
                    key = "ERR_PossibleAssigingNullToNotNull";
                }
            
                if (key != null) {
                    report(getCurrentPath(), key);
                }
            }
            
            return r;
        }

        @Override
        public State visitVariable(VariableTree node, Void p) {
            Element e = info.getTrees().getElement(getCurrentPath());
            
            if (e == null) {
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

            boolean wasNPE = false;
            
            if (expr == State.NULL) {
                String displayName = NbBundle.getMessage(NPECheck.class, "ERR_DereferencingNull");
                List<Fix> fixes = Collections.singletonList(FixFactory.createSuppressWarnings(info, getCurrentPath(), "null"));
                warnings.add(ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), displayName, fixes, info.getFileObject(), (int) start, (int) end));
                
                wasNPE = true;
            }

            if (expr == State.POSSIBLE_NULL_REPORT) {
                String displayName = NbBundle.getMessage(NPECheck.class, "ERR_PossiblyDereferencingNull");
                List<Fix> fixes = Collections.singletonList(FixFactory.createSuppressWarnings(info, getCurrentPath(), "null"));
                warnings.add(ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), displayName, fixes, info.getFileObject(), (int) start, (int) end));
                
                wasNPE = true;
            }
            
            if (wasNPE) {
                Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getExpression()));
                
                if (isVariableElement(e)) {
                    variable2State.put((VariableElement) e, NOT_NULL_BE_NPE);
                }
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
            
            Set<VariableElement> uncertain = new HashSet<VariableElement>();
            
            if (node.getElseStatement() == null) {
                for (Entry<VariableElement, State> e : variableStatesAfterThen.entrySet()) {
                    if (testedTo.get(e.getKey()) == State.NULL) {
                        if (e.getValue() != null) {
                            switch (e.getValue()) {
                                case NOT_NULL:
                                case NOT_NULL_BE_NPE:
                                    variable2State.put(e.getKey(), e.getValue());
                                    break;
                                case POSSIBLE_NULL:
                                    variable2State.put(e.getKey(), POSSIBLE_NULL);
                                    uncertain.add(e.getKey());
                                    break;
                            }
                        }
                    } else {
                        State c = collect(variable2State.get(e.getKey()), e.getValue());
                        
                        variable2State.put(e.getKey(), c);
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
            
            boolean thenExitsFromAllBranches = new ExitsFromAllBranches(info).scan(new TreePath(getCurrentPath(), node.getThenStatement()), null) == Boolean.TRUE;
            
            if (!thenExitsFromAllBranches) {
                for (Entry<VariableElement, State> test : testedTo.entrySet()) {
                    if ((variable2State.get(test.getKey()) == POSSIBLE_NULL || variable2State.get(test.getKey()) == null) && !uncertain.contains(test.getKey())) {
                        variable2State.put(test.getKey(), POSSIBLE_NULL_REPORT);
                    }
                }
            } else {
                for (Entry<VariableElement, State> test : testedTo.entrySet()) {
                    variable2State.put(test.getKey(), test.getValue().reverse());
                }
            }
            
            testedTo = oldTestedTo;
            
            return null;
        }

        @Override
        public State visitBinary(BinaryTree node, Void p) {
            State left = scan(node.getLeftOperand(), p);
            boolean rightAlreadyProcessed = false;
            
            if (node.getKind() == Kind.CONDITIONAL_AND) {
                boolean isParentAnd = getCurrentPath().getLeaf().getKind() == Kind.CONDITIONAL_AND;
                Map<VariableElement, State> oldVariable2State = variable2State;
                Map<VariableElement, State> oldTestedTo = testedTo;
                
                variable2State = new HashMap<VariableElement, NPECheck.State>(variable2State);
                variable2State.putAll(testedTo);
                
                testedTo = new HashMap<VariableElement, State>();
                
                scan(node.getRightOperand(), p);
                variable2State = oldVariable2State;
                
                if (isParentAnd) {
                    Map<VariableElement, State> o = new HashMap<VariableElement, NPECheck.State>(oldTestedTo);
                    
                    o.putAll(testedTo);
                    
                    testedTo = o;
                }
                
                rightAlreadyProcessed = true;
            }
            
            if (node.getKind() == Kind.CONDITIONAL_OR) {
                boolean isParentOr = getCurrentPath().getLeaf().getKind() == Kind.CONDITIONAL_OR;
                Map<VariableElement, State> oldVariable2State = variable2State;
                Map<VariableElement, State> oldTestedTo = testedTo;
                
                variable2State = new HashMap<VariableElement, NPECheck.State>(variable2State);
                
                for (Entry<VariableElement, State> e : testedTo.entrySet()) {
                    variable2State.put(e.getKey(), e.getValue().reverse());
                }
                
                testedTo = new HashMap<VariableElement, State>();
                
                scan(node.getRightOperand(), p);
                variable2State = oldVariable2State;
                
                if (isParentOr) {
                    Map<VariableElement, State> o = new HashMap<VariableElement, NPECheck.State>(oldTestedTo);
                    
                    o.putAll(testedTo);
                    
                    testedTo = o;
                }
                
                rightAlreadyProcessed = true;
            }
            
            State right = null;
            
            if (!rightAlreadyProcessed) {
                right = scan(node.getRightOperand(), p);
            }
            
            if (node.getKind() == Kind.EQUAL_TO) {
                if (right == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getLeftOperand()));
                    
                    if (isVariableElement(e)) {
                        testedTo.put((VariableElement) e, State.NULL);
                        
                        return null;
                    }
                }
                if (left == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getRightOperand()));
                    
                    if (isVariableElement(e)) {
                        testedTo.put((VariableElement) e, State.NULL);
                        
                        return null;
                    }
                }
            }
            
            if (node.getKind() == Kind.NOT_EQUAL_TO) {
                if (right == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getLeftOperand()));
                    
                    if (isVariableElement(e)) {
                        testedTo.put((VariableElement) e, State.NOT_NULL);
                        
                        return null;
                    }
                }
                if (left == State.NULL) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getRightOperand()));
                    
                    if (isVariableElement(e)) {
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
                
            Map<VariableElement, State> oldVariable2State = variable2State;

            variable2State = new HashMap<VariableElement, State>(variable2State);
            
            variable2State.putAll(testedTo);
                
            State thenSection = scan(node.getTrueExpression(), p);
            
            variable2State = new HashMap<VariableElement, State>(variable2State);
            
            for (Entry<VariableElement, State> e : testedTo.entrySet()) {
                variable2State.put(e.getKey(), e.getValue().reverse());
            }
            
            State elseSection = scan(node.getFalseExpression(), p);
            
            variable2State = oldVariable2State;
                
            State result;
            
            if (thenSection == elseSection) {
                result = thenSection;
            } else {
                result = NPECheck.State.NOT_NULL.POSSIBLE_NULL;
            }
            
            for (Entry<VariableElement, State> test : testedTo.entrySet()) {
                if (variable2State.get(test.getKey()) == POSSIBLE_NULL || variable2State.get(test.getKey()) == null) {
                    variable2State.put(test.getKey(), POSSIBLE_NULL_REPORT);
                }
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
            scan(node.getTypeArguments(), p);
            
            State methodSelectState = scan(node.getMethodSelect(), p);
            List<State> paramStates = new ArrayList<State>(node.getArguments().size());
            
            for (Tree param : node.getArguments()) {
                paramStates.add(scan(param, p));
            }
            
            Element e = info.getTrees().getElement(getCurrentPath());
            
            if (e == null || e.getKind() != ElementKind.METHOD) {
                return State.POSSIBLE_NULL;
            }
            
            ExecutableElement ee = (ExecutableElement) e;
            int index = 0;
            
            for (VariableElement param : ee.getParameters()) {
                if (getStateFromAnnotations(param) == NOT_NULL) {
                    switch (paramStates.get(index)) {
                        case NULL:
                            report(new TreePath(getCurrentPath(), node.getArguments().get(index)), "ERR_NULL_TO_NON_NULL_ARG");
                            break;
                        case POSSIBLE_NULL_REPORT:
                            report(new TreePath(getCurrentPath(), node.getArguments().get(index)), "ERR_POSSIBLENULL_TO_NON_NULL_ARG");
                            break;
                    }
                }
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
            
            if (e != null) {
                State s = variable2State.get(e);
                
                if (s != null) {
                    return s;
                }
            }
            
            return getStateFromAnnotations(e);
        }

        @Override
        public State visitWhileLoop(WhileLoopTree node, Void p) {
            Map<VariableElement, State> oldTestedTo = testedTo;
            
            testedTo = new HashMap<VariableElement, State>();
            
            Map<VariableElement, State> oldVariable2State = variable2State;

            variable2State = new HashMap<VariableElement, State>(variable2State);
            
            scan(node.getCondition(), p);
            
            variable2State.putAll(testedTo);
            
            scan(node.getStatement(), p);
            
            variable2State = new HashMap<VariableElement, State>(oldVariable2State);
            
            for (Entry<VariableElement, State> e : testedTo.entrySet()) {
                State o = variable2State.get(e.getKey());
                
                if (e.getValue() == NOT_NULL && (o == POSSIBLE_NULL || o == null)) {
                    variable2State.put(e.getKey(), POSSIBLE_NULL_REPORT);
                }
            }
            
            testedTo = oldTestedTo;
            
            return null;
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
                Project owner = findProject(info, e);
                
                if (owner != null && findCheckForNullNames(owner).contains(fqn)) {
                    return State.POSSIBLE_NULL_REPORT;
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
    
    static enum State {
        NULL,
        POSSIBLE_NULL,
        POSSIBLE_NULL_REPORT,
        NOT_NULL,
        NOT_NULL_BE_NPE;
        
        public State reverse() {
            switch (this) {
                case NULL:
                    return NOT_NULL;
                case POSSIBLE_NULL:
                case POSSIBLE_NULL_REPORT:
                    return this;
                case NOT_NULL:
                case NOT_NULL_BE_NPE:
                    return NULL;
                default: throw new IllegalStateException();
            }
        }
        
        public boolean isNotNull() {
            return this == NOT_NULL || this == NOT_NULL_BE_NPE;
        }
        
        public static State collect(State s1, State s2) {
            if (s1 == s2) return s1;
            if (s1 == NULL && s2 != null && s2.isNotNull()) return POSSIBLE_NULL_REPORT;
            if (s1 != null && s1.isNotNull() && s2 == NULL) return POSSIBLE_NULL_REPORT;
            if (s1 == POSSIBLE_NULL_REPORT && s2 != POSSIBLE_NULL) return POSSIBLE_NULL_REPORT;
            if (s1 != POSSIBLE_NULL && s2 == POSSIBLE_NULL_REPORT) return POSSIBLE_NULL_REPORT;
            
            return POSSIBLE_NULL;
        }
    }
    
    //XXX copied from IntroduceHint:
    private static final class ExitsFromAllBranches extends TreePathScanner<Boolean, Void> {
        
        private CompilationInfo info;
        private Set<Tree> seenTrees = new HashSet<Tree>();

        public ExitsFromAllBranches(CompilationInfo info) {
            this.info = info;
        }

        @Override
        public Boolean scan(Tree tree, Void p) {
            seenTrees.add(tree);
            return super.scan(tree, p);
        }

        @Override
        public Boolean visitIf(IfTree node, Void p) {
            return scan(node.getThenStatement(), null) == Boolean.TRUE && scan(node.getElseStatement(), null) == Boolean.TRUE;
        }

        @Override
        public Boolean visitReturn(ReturnTree node, Void p) {
            return true;
        }

        @Override
        public Boolean visitBreak(BreakTree node, Void p) {
            return !seenTrees.contains(info.getTreeUtilities().getBreakContinueTarget(getCurrentPath()));
        }

        @Override
        public Boolean visitContinue(ContinueTree node, Void p) {
            return !seenTrees.contains(info.getTreeUtilities().getBreakContinueTarget(getCurrentPath()));
        }

        @Override
        public Boolean visitClass(ClassTree node, Void p) {
            return false;
        }

        @Override
        public Boolean visitThrow(ThrowTree node, Void p) {
            return true; //XXX: simplification
        }
        
    }
    
    private static final Map<TypeElement, Project> class2Project = new WeakHashMap<TypeElement, Project>();
    
    private static Project findProject(CompilationInfo info, Element e) {
        TypeElement owner = info.getElementUtilities().outermostTypeElement(e);
        
        if (owner == null) return null;
        
        Project p = class2Project.get(owner);
        
        if (p == null) {
            FileObject source = SourceUtils.getFile(e, info.getClasspathInfo());

            if (source != null) {
                p = FileOwnerQuery.getOwner(source);
                
                if (p != null) {
                    class2Project.put(owner, p);
                }
            }
        }
        
        return p;
    }
    
    private static final Map<Project, Set<String>> project2CheckForNullNames = new WeakHashMap<Project, Set<String>>();
    
    private static Set<String> findCheckForNullNames(Project p) {
        Set<String> set = project2CheckForNullNames.get(p);
        
        if (set == null) {
            project2CheckForNullNames.put(p, set = new HashSet<String>());
            AuxiliaryConfiguration ac = ProjectUtils.getAuxiliaryConfiguration(p);

            if (ac != null) {
                org.w3c.dom.Element configurationFragment = ac.getConfigurationFragment("npe-check-hints", "http://www.netbeans.org/ns/npe-check-hints/1", true);

                if (configurationFragment != null) {
                    NodeList nl = configurationFragment.getElementsByTagName("check-for-null");

                    for (int cntr = 0; cntr < nl.getLength(); cntr++) {
                        Node n = nl.item(cntr);
                        set.add(n.getTextContent());
                    }
                }
            }
        }
        
        return set;
    }
    
    private static boolean isVariableElement(Element ve) {
        return ve != null && VARIABLE_ELEMENT.contains(ve.getKind());
    }
    
    private static final Set<ElementKind> VARIABLE_ELEMENT = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.FIELD, ElementKind.LOCAL_VARIABLE, ElementKind.PARAMETER);
    
}
