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

import com.sun.source.tree.CatchTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.lang.model.util.Types;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;

/**
 * Reports all public and protected variables which are not referenced by 
 * code other than the variable's initialization expression.  
 * <p>
 * Note:  this query can only find references within the set of open
 * projects, so it is important to run this query with all client code available
 * before deciding to delete an unused variable.  Another restriction is that
 * this query currently doesn't test for access via the Java Reflection API.
 * 
 * @author Tom Ball
 */
public class FindUnusedVariables extends TreePathQuery<Void,Object> {
    private static final String serialVersionUIDName = "serialVersionUID";
    PrimitiveType longType;
    TypeMirror serializableType;
    Types types;
    Trees trees;
    QueryOperations ops;
    ElementUtilities elementUtils;
    
    /** user-set property */
    public boolean ignoreStatics;
    
    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        longType = info.getTypes().getPrimitiveType(TypeKind.LONG);
	serializableType = 
            info.getElements().getTypeElement("java.io.Serializable").asType();
        types = info.getTypes();
        trees = info.getTrees();
        ops = new QueryOperations(info);
        elementUtils = info.getElementUtilities();
    }
    
    @Override
    public void release() {
        super.release();
        longType = null;
        serializableType = null;
        types = null;
    }

    /**
     * Check all variable declarations for references.
     * 
     * @param tree the variable declaration
     * @param p unused
     * @return null
     */
    @Override
    public Void visitVariable(VariableTree tree, Object p) {
        super.visitVariable(tree, p);
        if (shouldTest(tree))
            if (noPublicUses(tree))
                addResult();
        return null;
    }
    /**
     * Ignore method parameters when searching variable declarations.
     * 
     * @param tree the method tree
     * @param p unused
     * @return void
     */
    @Override
    public Void visitMethod(MethodTree tree, Object p) {
        // ignore parameters
        return scan(tree.getBody(), p);
    }
    
    /**
     * Ignore expression declarations in catch blocks.
     * 
     * @param tree the catch tree
     * @param p unused
     * @return void
     */
    @Override
    public Void visitCatch(CatchTree tree, Object p) {
        // ignore parameters
        return scan(tree.getBlock(), p);
    }
    
    private boolean noPublicUses(VariableTree tree) {
        return false; //FIXME
    }

    private boolean shouldTest(VariableTree tree) {
        boolean ignore = ignoreStatics && ops.isConstant(getCurrentPath());
        return !ignore && !isSerialVersionUID(tree) && !isEnum(tree);
    }
    
    private boolean isEnum(VariableTree tree) {
        TreePath path = getCurrentPath();
	return trees.getElement(path).getKind() == ElementKind.ENUM_CONSTANT;
    }

    private boolean isSerialVersionUID(VariableTree tree) {
        Element sym = trees.getElement(getCurrentPath());
        return serialVersionUIDName.equals(tree.getName().toString()) && 
            sym.asType().equals(longType) && 
            isConstantField(tree) &&
	    isClassSerializable(sym.getEnclosingElement());
    }
    
    private boolean isClassSerializable(Element sym) {
	if (sym instanceof TypeElement)
            return types.isAssignable(sym.asType(), serializableType);
	return false;
    }

    private boolean isConstantField(VariableTree tree) {
        Set<Modifier> flags = tree.getModifiers().getFlags();
        boolean isStaticFinal = 
            flags.contains(Modifier.STATIC) && flags.contains(Modifier.FINAL);
        Element owner = elementUtils.enclosingTypeElement(trees.getElement(getCurrentPath()));
        boolean isInterfaceField = owner.getKind() == ElementKind.INTERFACE;
        return isStaticFinal || isInterfaceField;
    }
}
