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
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.java.additional.refactorings.Utils;
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
//        System.err.println("Visit member named " + memberName);
        if (ctx.mods.isNewOrChangedParameterName(memberName)) {
//            System.err.println("POSSIBLE MATCH: " + memberName);
            TreePath pathToMemberSelect = getCurrentPath();
            assert pathToMemberSelect != null : "Path to member select is null";
            Element element = info.getTrees().getElement(pathToMemberSelect);
            
            TypeElement ownerOfThingSelected = info.getElementUtilities().enclosingTypeElement(element);
//            System.err.println("Member select owned by " + ownerOfThingSelected.getQualifiedName());
            
            Element ownerOfMemberSelect = info.getTrees().getElement(getCurrentPath());
//            System.err.println("Owner of meber select is " + ownerOfMemberSelect);
            if (!(ownerOfMemberSelect instanceof TypeElement)) {
                ownerOfMemberSelect = info.getElementUtilities().enclosingTypeElement(ownerOfMemberSelect);
            }
            if (isSelfOrAncestorOf(ownerOfThingSelected, ownerOfMemberSelect)) {
                ElementHandle <ExecutableElement> methodHandle = 
                        getCurrentMethodHandle(ctx.changeData.getCompilationInfo());
                if (methodHandle != null) {
                    TreePathHandle memberSelectHandle = TreePathHandle.create(pathToMemberSelect, info);
                    String qualification = Utils.getQualification (pathToMemberSelect, (TypeElement) 
                            ownerOfMemberSelect, element, ctx);
                    if (qualification != null) {
                        System.err.println("Adding member select that needs requalifying: " + tree + " qualification " + qualification);
                        data.addMemberSelectThatNeedsRequalifying(memberSelectHandle, methodHandle, ctx.changeData.getCompilationInfo(), qualification);
                    }
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
