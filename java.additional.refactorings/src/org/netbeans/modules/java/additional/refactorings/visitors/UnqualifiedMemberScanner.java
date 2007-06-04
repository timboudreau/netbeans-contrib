/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ChangeData;

/**
 * Scans for unqualified references to member names which match parameter names
 * that will be used in the refactored method.
 *
 * @author Tim Boudreau
 */
public class UnqualifiedMemberScanner extends TreePathScanner <Void, ParameterChangeContext> {

    public UnqualifiedMemberScanner() {
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree tree, ParameterChangeContext ctx) {
        handle (tree, ctx);
        return super.visitMemberSelect(tree, ctx);
    }
    
    @Override
    public Void visitIdentifier(IdentifierTree tree, ParameterChangeContext ctx) {
        handle (tree, ctx);
        return super.visitIdentifier(tree, ctx);
    }
    
    private void handle (Tree tree, ParameterChangeContext ctx) {
        ChangeData data = ctx.changeData;
        CompilationInfo info = data.getCompilationInfo();
        String memberName = tree instanceof MemberSelectTree ? ((MemberSelectTree)tree).getIdentifier().toString() :
            tree instanceof IdentifierTree ? ((IdentifierTree) tree).getName().toString() : null;
        assert memberName != null;
        System.err.println("Visit member named " + memberName);
        if (ctx.mods.isNewOrChangedParameterName(memberName)) {
            System.err.println("POSSIBLE MATCH: " + memberName);
            TreePath pathToMemberSelect = getCurrentPath();
            assert pathToMemberSelect != null : "Path to member select is null";
            Element element = info.getTrees().getElement(pathToMemberSelect);
            
            TypeElement ownerOfThingSelected = info.getElementUtilities().enclosingTypeElement(element);
            System.err.println("Member select owned by " + ownerOfThingSelected.getQualifiedName());
            
            Element ownerOfMemberSelect = info.getTrees().getElement(getCurrentPath());
            System.err.println("Owner of meber select is " + ownerOfMemberSelect);
            if (!(ownerOfMemberSelect instanceof TypeElement)) {
                ownerOfMemberSelect = info.getElementUtilities().enclosingTypeElement(ownerOfMemberSelect);
            }
            if (isSelfOrAncestorOf(ownerOfThingSelected, ownerOfMemberSelect)) {
                ElementHandle <ExecutableElement> methodHandle = 
                        getCurrentMethodHandle(ctx.changeData.getCompilationInfo());
                if (methodHandle != null) {
                    TreePathHandle memberSelectHandle = TreePathHandle.create(pathToMemberSelect, info);
                    System.err.println("Adding member select that needs requalifying: " + tree);
                    data.addMemberSelectThatNeedsRequalifying(memberSelectHandle, methodHandle, ctx.changeData.getCompilationInfo());
                } else {
                    System.err.println("Method handle null for " + ownerOfMemberSelect);
                }
            }
        }
    }
    
    private ElementHandle <ExecutableElement> getCurrentMethodHandle (CompilationInfo info) {
        TreePath path = getCurrentPath();
        Tree leaf = null;
        while (path != null) {
            if ((path = path.getParentPath()) == null) {
                leaf = null;
                break;
            }
            leaf = path.getLeaf();
            if (leaf.getKind() == Kind.METHOD) {
                break;
            }
        }
        if (leaf != null) {
            Element el = info.getTrees().getElement(path);
            return ElementHandle.<ExecutableElement>create((ExecutableElement) el);
        }
        return null;
    }
    
    private boolean isSelfOrAncestorOf (Element target, Element test) {
        if (target == test) {
            return true;
        }
        while (test != null && !test.equals(target)) {
            System.err.println("Check " + test + " with " + target + " result " + test.equals(target));
            test = test.getEnclosingElement();
        }
        return test != null && test.equals(target);
    }
}
