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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;

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
    private ClassIndex index;
    
    /** user-set property */
    public boolean ignoreStatics;
    
    @Override
    public void init(QueryContext context, JavaSource javaSource) {
        super.init(context, javaSource);
        index = javaSource.getClasspathInfo().getClassIndex();
    }
    
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
    
    @Override
    public void destroy() {
        super.destroy();
        index = null;
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
        scan(tree.getBody(), p);
        return null;
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
        scan(tree.getBlock(), p);
        return null;
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
