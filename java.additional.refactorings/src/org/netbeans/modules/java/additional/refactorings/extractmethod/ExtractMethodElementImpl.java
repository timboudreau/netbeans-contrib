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
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import org.netbeans.modules.java.additional.refactorings.*;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.visitors.ParamDesc;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class ExtractMethodElementImpl extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy> {
    final List <TreePathHandle> pathsToStatementsToMove; 
    final Set <ParamDesc> forParams; 
    final TreePathHandle refactoredMethodHandle;
    final Set <? extends TypeMirrorHandle> exceptions;
    final TypeMirrorHandle returnTypeHandle;
    final boolean lastLineReturns;
    final String returnVariableName;
    final FileObject fob;
    final String methodToCreate;
    final Set <Modifier> newMethodFlags;
    ExtractMethodElementImpl (String methodToCreate, FileObject fob, TreePathHandle refactoredMethod, Set <ParamDesc> forParams, Set <? extends TypeMirrorHandle> exceptions, List <TreePathHandle> pathsToStatementsToMove, TypeMirrorHandle returnTypeHandle, String returnVariableName, boolean lastLineReturns, Set <Modifier> modifiers) {
        this.fob = fob;
        this.methodToCreate = methodToCreate;
        this.returnVariableName = returnVariableName;
        this.lastLineReturns = lastLineReturns;
        this.pathsToStatementsToMove = pathsToStatementsToMove;
        this.exceptions = exceptions;
        this.forParams = forParams;
        this.refactoredMethodHandle = refactoredMethod;
        this.returnTypeHandle = returnTypeHandle;
        this.newMethodFlags = modifiers;
    }

    public String getText() {
        return getDisplayText(); //???
    }

    public String getDisplayText() {
        return ExtractMethodPlugin.getS("LBL_EXTRACT_METHOD", methodToCreate);
    }

    public void performChange() {
        JavaSource js = JavaSource.forFileObject (fob);
        try {
            js.runModificationTask(this).commit();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    public Lookup getLookup() {
        return Lookups.singleton (fob);
    }

    public FileObject getParentFile() {
        return fob;
    }

    public PositionBounds getPosition() {
        return null;
    }

    volatile boolean cancelled = false;
    public void cancel() {
        cancelled = true;
    }

    public void run(WorkingCopy copy) throws Exception {
        copy.toPhase (Phase.RESOLVED);
        TreeMaker maker = copy.getTreeMaker();
        Trees trees = copy.getTrees();
        super.setStatus(10);

        Element el = refactoredMethodHandle.resolveElement(copy);
        Element containingClass = copy.getElementUtilities().enclosingTypeElement(el);
        TreePath path = trees.getPath(el);
        Tree     tree = path.getLeaf();
        boolean statik = Utils.isStatic(el, trees.getScope(path), copy);
        if (statik) {
            newMethodFlags.add (Modifier.STATIC);
        }
        ModifiersTree modifiers = maker.Modifiers(newMethodFlags);

        List <StatementTree> toMove = Utils.<StatementTree>toTrees(
                pathsToStatementsToMove, 
                copy);
        
        if (returnVariableName != null) {
            StatementTree ret = maker.Return(maker.Identifier(returnVariableName));
            toMove.add (ret);
        }

        BlockTree rewrittenMethodBlock;
        if (tree instanceof BlockTree) {
            rewrittenMethodBlock = (BlockTree) tree;
        } else if (tree instanceof MethodTree) {
            rewrittenMethodBlock = ((MethodTree) tree).getBody();
        } else {
            rewrittenMethodBlock = null;
        }
        BlockTree originalMethodBlock = rewrittenMethodBlock;
        super.setStatus (20);

        int insertPoint = 0;
        if (rewrittenMethodBlock != null) {
            insertPoint = rewrittenMethodBlock.getStatements().indexOf (toMove.get(0));
            for (StatementTree st : toMove) {
                rewrittenMethodBlock = maker.removeBlockStatement(rewrittenMethodBlock, st);
            }
        }
        super.setStatus (30);

        BlockTree newMethodBlock = maker.Block(toMove, false);
        List <VariableTree> vars = new ArrayList <VariableTree> (forParams.size());

        List <IdentifierTree> methodArgs = new ArrayList <IdentifierTree> ();
        for (ParamDesc desc : forParams) {
            if (cancelled) return;
            methodArgs.add (maker.Identifier(desc.getName()));
            ModifiersTree mt = maker.Modifiers(EnumSet.noneOf(Modifier.class));
            VariableTree t = maker.Variable(mt, desc.getName(), 
                    maker.Type(desc.getType().resolve(copy)), null);
            vars.add (t);
        }
        if (cancelled) return;

        super.setStatus (40);

        if (cancelled) return;
        TreePath refactoredMethodPath = refactoredMethodHandle.resolve(copy);
        Element refactoredMethodElement = trees.getElement(refactoredMethodPath);
        TypeElement parentClass = copy.getElementUtilities().enclosingTypeElement(refactoredMethodElement);
        int methodIndex = parentClass.getEnclosedElements().indexOf(refactoredMethodElement);
        TreePath enclosingType = refactoredMethodPath.getParentPath();
        ClassTree currClassTree = (ClassTree) enclosingType.getLeaf();

        assert parentClass != null;
        assert currClassTree != null;
        assert methodIndex >= 0;

        List <ExpressionTree> throwsList = new ArrayList <ExpressionTree>();

        for (TypeMirrorHandle typeHandle : exceptions) {
            TypeMirror type = typeHandle.resolve(copy);
            System.err.println("resolved " + type);
            Tree exc = maker.Type(type);
            if (exc instanceof ExpressionTree) { //PENDING should it be?
                throwsList.add ((ExpressionTree) exc);
            } else {
                System.err.println("Type class " + type);
            }
        }
        super.setStatus (60);
        if (cancelled) return;

        TypeMirror returnType;
        Tree returnTypeTree;
        if (this.returnTypeHandle != null) {
            returnType = returnTypeHandle.resolve(copy);
        } else {
            returnType = returnTypeHandle != null ? returnTypeHandle.resolve(copy) :
                copy.getTypes().getNoType(TypeKind.VOID);
        }
        returnTypeTree = maker.Type(returnType);

        if (cancelled) return;
        MethodTree newMethod = maker.Method(modifiers, methodToCreate, 
                returnTypeTree, Collections.<TypeParameterTree>emptyList(), 
                vars, throwsList, newMethodBlock, null);

        super.setStatus (70);
        assert newMethod != null;            

        ClassTree newClassTree = maker.insertClassMember(currClassTree, 
                methodIndex, newMethod);
        super.setStatus (80);

        copy.rewrite(currClassTree, newClassTree);
        super.setStatus (90);

        if (rewrittenMethodBlock != null) {
            ExpressionTree id = statik ? maker.QualIdent(containingClass)
                    : maker.Identifier("this"); //NOI18N                    
            MemberSelectTree select = maker.MemberSelect(id, methodToCreate);
            MethodInvocationTree callToNewMethod = maker.MethodInvocation(Collections.<ExpressionTree>emptyList(), select, methodArgs);
            ExpressionStatementTree statement;
            if (returnVariableName != null) {
                AssignmentTree assig = maker.Assignment(maker.Identifier(returnVariableName), callToNewMethod);
                statement = maker.ExpressionStatement (assig);
            } else {
                statement = maker.ExpressionStatement(callToNewMethod);
            }
            rewrittenMethodBlock = maker.insertBlockStatement(rewrittenMethodBlock, insertPoint, statement);
            if (!lastLineReturns && !returnType.equals(copy.getTypes().getNoType(TypeKind.VOID))) {

            }
            copy.rewrite (originalMethodBlock, rewrittenMethodBlock);
        }
        super.setStatus (100);            
    }
}
