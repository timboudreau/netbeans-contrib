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

import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ChangeData;

/**
 * Scans method bodies for variable names used within them and stores the list
 * of used names in a ParameterChangeContext.ChangeData for use later to check
 * if there are name conflicts.
 *
 * Also used to scan parameter lists to ensure against any name conflicts there;
 * only stores parameter names that do not match the parameter name of the
 * current parameter, so we don't see false conflicts by including the list
 * of parameters that should be there anyway.
 *
 * @author Tim Boudreau
 */
public final class VariableNameScanner extends TreeScanner <Void, ParameterChangeContext> {
    private final Mode mode;

    public enum Mode {
        SCAN_METHOD_BODIES, SCAN_PARAMETERS
    }

    public VariableNameScanner (Mode mode) {
        this.mode = mode;
    }

    @Override
    public Void visitVariable(VariableTree tree, ParameterChangeContext ctx) {
        RequestedParameterChanges mods = ctx.mods;
        ChangeData data = ctx.changeData;
        ExecutableElement method = data.getCurrentMethodElement();
        String paramName = tree.getName().toString();
        switch (mode) {
            case SCAN_METHOD_BODIES : {
                //In this mode, we are scanning the body of the method and collecting
                //all variable names defined in it, to see if they conflict with
                //the parameter names
                ElementHandle<ExecutableElement> handle = ElementHandle.<ExecutableElement>create(method);
                data.addUsedVariableName(handle, paramName);
            }
            break;
            case SCAN_PARAMETERS : {
                //In this mode we are scanning the variable tree of each parameter to
                //a method, and collecting any parameter names that are different in
                //this implementation of the method than in their original canonical
                //definition
                int ix = data.getParameterIndex();
                if (!mods.matchesOriginalParameterName(ix, tree.getName().toString())) {
                    ElementHandle<ExecutableElement> handle =
                            ElementHandle.<ExecutableElement>create(method);
                    data.addUsedVariableName(handle, paramName);
                }
            }
            break;
        }
        return super.visitVariable(tree, ctx);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree tree, ParameterChangeContext ctx) {
        ChangeData data = ctx.changeData;
        CompilationInfo info = data.getCompilationInfo();
        String memberName = tree.getIdentifier().toString();
        if (ctx.mods.isNewOrChangedParameterName(memberName)) {
            //XXX how to determine if the memberselect is on this or this.getClass()?
            TreePath pathToMemberSelect = TreePath.getPath(ctx.changeData.getCompilationUnit(), tree);
            assert pathToMemberSelect != null : "Path to member select is null";
            Element element = info.getTrees().getElement(pathToMemberSelect);
            
            //This will be the class whose member is being selected.  We will
            //use it to see if the member with the conflicting name is a class
            //member of the class containing the method we are refactoring.  If
            //so then we will need to change uses of the variable to be qualified
            //with "this" or the type name.  In other words, if someone has
            
            // int x = 0;
            // @Override
            // public void foo (int y) {
            //     x += y;
            // }
            
            //and they want to rename the parameter y to x, then the existing
            //reference to x in foo() must become "this.x"
            
            TypeElement owner = info.getElementUtilities().enclosingTypeElement(element);
            
            TreePath path = pathToMemberSelect;
            Tree leaf = path.getLeaf();
            while (leaf != null && path != null && leaf.getKind() != Kind.CLASS) {
                path = path.getParentPath();
                leaf = path.getLeaf();
            }
            if (leaf != null) {
                Element el = info.getTrees().getElement(path);
                if (el instanceof TypeElement) {
                    if (owner == el) {
                        ElementHandle <ExecutableElement> methodHandle = data.getHandleToCurrentMethodElement();
                        TreePathHandle memberSelectHandle = TreePathHandle.create(pathToMemberSelect, info);
                        data.addMemberSelectThatNeedsRequalifying(memberSelectHandle, methodHandle);
                    }
                }
            } else {
                throw new IllegalStateException("??? Found no enclosing class for " + tree);
            }
        }
        return super.visitMemberSelect(tree, ctx);
    }
}
