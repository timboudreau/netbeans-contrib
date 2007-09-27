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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.jackpot.cmds;

import com.sun.source.util.Trees;
import java.util.List;
import javax.lang.model.type.TypeKind;
import com.sun.source.tree.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Set;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;

/**
 * Find any "dangerous" method calls made by an incompletely initialized
 * object.  These are defined as:
 *
 * 1.  any non-final public or protected methods of the defining class 
 *     or its superclasses; or
 * 2.  any method call where "this" is passed as a parameter.
 */
public class ConstPublicCalls extends TreePathQuery<Void,Object> {
    private TypeMirror objectType;
    private TypeMirror voidType;
    private TypeMirror objectInputStreamType;
    private Trees trees;
    private ElementUtilities elementUtils;

    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        objectType = info.getElements().getTypeElement("java.lang.Object").asType();
        voidType = info.getTypes().getNoType(TypeKind.VOID);
        objectInputStreamType = info.getElements().getTypeElement("java.io.ObjectInputStream").asType();
        trees = info.getTrees();
        elementUtils = info.getElementUtilities();
    }
    
    @Override
    public void release() {
        super.release();
        objectType = null;
        voidType = null;
        objectInputStreamType = null;
        trees = null;
    }

    @Override
    public Void visitMethod(MethodTree tree, Object p) {
	ExecutableElement sym = (ExecutableElement)trees.getElement(getCurrentPath());
        final TypeElement cls = elementUtils.enclosingTypeElement(sym);
	if (isFinal(cls))
	    return null;
	if (isConstructor(sym) || isClone(sym) || isReadObject(sym)) {
            /* FIXME
	    CallGraph cg = new CallGraph(sym, env) {
                public boolean acceptCall(Element e, Tree t) {
                    // accept if a class member...
                    if (cls.equals(elements.enclosingTypeElement(e)))
                        return true;

                    // ... or if "this" is used as a parameter.
                    List<? extends ExpressionTree> args = (t instanceof MethodInvocationTree) ?
                        ((MethodInvocationTree)t).getArguments() : ((NewClassTree)t).getArguments();
                    for (ExpressionTree arg : args)
                        if (isThisArg(arg))
                            return true;
                    return false;
                }
            };
	    cg.buildGraph();
	    CallGraph.Node[] calls = cg.getNodes();
	    int n = calls.length;
	    String note = "";
	    for (int i = 1; i < n; i++) {
		Element e = calls[i].getElement();
		if (isMemberOf(e, cls) && 
		    !isStatic(e) && 
		    e.getKind() != ElementKind.CONSTRUCTOR &&
 		    isOverridable(e))
		        note += cg.getPathString(e) + ' ';
	    }
	    if (note.length() > 0)
		addResult(sym, note);
             */
	}
        return null;
    }

    private boolean isThisArg(Tree arg) {
	if (arg instanceof IdentifierTree) {
	    String name = ((IdentifierTree)arg).getName().toString();
	    if ("this".equals(name))
		return true;
	}
	return false;
    }

    private boolean isOverridable(Element e) {
	if (isFinal(e) || isFinal(e.getEnclosingElement()))
	    return false;
        Set<Modifier> mods = e.getModifiers();
        return mods.contains(Modifier.PUBLIC) || mods.contains(Modifier.PROTECTED);
    }

    private boolean isFinal(Element e) {
        return e.getModifiers().contains(Modifier.FINAL);
    }

    private boolean isStatic(Element e) {
        return e.getModifiers().contains(Modifier.STATIC);
    }

    private boolean isClone(ExecutableElement s) {
	return s.getSimpleName().toString().equals("clone") && 
                s.getParameters().isEmpty() && 
                s.getReturnType().equals(objectType);
    }

    private boolean isReadObject(ExecutableElement s) {
        String name = s.getSimpleName().toString();
        List<? extends VariableElement> params = s.getParameters();
        TypeMirror returnType = s.getReturnType();
	return name.equals("readObject") && 
                params.size() == 1 && 
                params.get(0).asType().equals(objectInputStreamType) &&
                returnType == voidType;
    }
    
    private boolean isConstructor(ExecutableElement e) {
        return e.getSimpleName().toString().equals("<init>");
    }
}
