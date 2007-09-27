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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner6;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import static javax.lang.model.element.ElementKind.*;

/**
 * Queries instance methods to determine whether they can be made static.  
 * Reported methods do not reference any instance data, are not already
 * static, abstract or native, do not override a superclass method, nor 
 * are overridden by a subclass method.
 * <p>
 * Note:  the last test for overridden methods is as accurate as the 
 * set of projects the IDE has scanned, which includes all projects which
 * are currently open, or have been previously opened and have valid cache
 * values.  It is recommended that all projects be first converted using
 * the AddOverrides transformer, so the compiler will catch any cases where
 * an overridden method is made static.
 * 
 * @author tball
 */
public class CouldBeStatic extends TreePathQuery<Void,Object> {
    Trees trees;
    ClassIndex index;
    ElementUtilities elementUtils;
    
    @Override
    public void init(QueryContext context, JavaSource javaSource) {
        super.init(context, javaSource);
        index = javaSource.getClasspathInfo().getClassIndex();
    }
    
    @Override
    public void attach(CompilationInfo info) {
	super.attach(info);
        trees = info.getTrees();
        elementUtils = info.getElementUtilities();
    }
    
    @Override
    public void release() {
        elementUtils = null;
        trees = null;
        super.release();
    }
    
    @Override 
    public void destroy() {
        index = null;
        super.destroy();
    }
    
    /**
     * Check each method for possible static use.
     * @param meth the method being checked
     * @param p not used
     * @return null
     */
    @Override
    public Void visitMethod(MethodTree meth, Object p) {
	if (meth != null) {
            Set<Modifier> flags = meth.getModifiers().getFlags();
            if (!flags.contains(Modifier.ABSTRACT) && 
                !flags.contains(Modifier.STATIC) &&
                !flags.contains(Modifier.NATIVE)) {
                TreePath path = getCurrentPath();
                ExecutableElement e = (ExecutableElement)trees.getElement(path);
                if(!elementUtils.overridesMethod(e) && getInstanceReferenceCount(path)==0 && !isOverridden(e)) 
                    addResult(path, "Could be Static");
            }
        }
        return null;
    }
    
    /**
     * Skip all interfaces when looking for possible static methods.
     * @param clazz the class being checked
     * @param p not used
     * @return null
     */
    @Override
    public Void visitClass(ClassTree clazz, Object p) {
        if (trees.getElement(getCurrentPath()).getKind() == ElementKind.INTERFACE)
	    super.visitClass(clazz, p);
        return null;
    }
    
    private boolean isOverridden(final ExecutableElement meth) {
        TypeElement owner = (TypeElement)meth.getEnclosingElement();
        Set<ElementHandle<TypeElement>> classes = 
            index.getElements(ElementHandle.create(owner), 
                              EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS), 
                              EnumSet.of(ClassIndex.SearchScope.SOURCE));
        for (ElementHandle<TypeElement> handle : classes) {
            TypeElement cls = handle.resolve(getCompilationInfo());
            Boolean found = new ElementScanner6<Boolean,Boolean>() {
                @Override
                public Boolean scan(Element e, Boolean found) {
                    return !found ? e.accept(this, false) : true;
                }
                @Override
                public Boolean visitExecutable(ExecutableElement e, Boolean found) {
                    if (!found) {
                        if (elementUtils.getOverriddenMethod(e) == meth)
                            return true;
                        return scan(e.getParameters(), false);
                    }
                    return false;
                }
            }.scan(cls, false);
            if (found)
                return true;
        }
        return false;
    }

    private int getInstanceReferenceCount(TreePath path) {
        final EnumSet<ElementKind> validKinds = 
              EnumSet.<ElementKind>of(CONSTRUCTOR, FIELD, INSTANCE_INIT, METHOD);
        final HashMap<Object,Counter> map = new HashMap<Object,Counter>();
        final int thisUseCount[] = new int[1];
	new TreeScanner<Void,Tree>() {
	    public Void visitIdentifier(IdentifierTree tree, Tree parent) {
                Element e = trees.getElement(getCurrentPath());
		checkBoth(tree.getName(), e);
                if(isThis(tree.getName()) || 
                        e != null && !validKinds.contains(e.getKind()) && !elementUtils.isLocal(e))
		    thisUseCount[0]++;
                return null;
	    }
	    public Void visitMemberSelect(MemberSelectTree tree, Tree parent) {
		checkBoth(tree.getIdentifier(), trees.getElement(getCurrentPath()));
		if(isThis(tree.getIdentifier())) thisUseCount[0]++;  // T.this
		super.visitMemberSelect(tree, parent);
                return null;
	    }
	    public Void visitNewClass(NewClassTree tree, Tree parent) {
		checkBoth(null, trees.getElement(getCurrentPath()));
		super.visitNewClass(tree, parent);
                return null;
	    }
            private boolean isThis(Name name) {
                return name.toString().equals("this");
            }
	    private void checkBoth(Name name,Element e) {
		if(name!=null) 
                    checkOne(name);
		if(e!=null)
                    checkOne(e);
	    }
	    private boolean checkOne(Object key) {
		Counter c = map.get(key);
		if(c==null) {
		    map.put(key,c=new Counter());
		    c.useCount++;
		    return true;
		}
		c.useCount++;
		return false;
	    }
	}.scan(path, null);
        return thisUseCount[0];
    }
    
    private static class Counter {
	int useCount = 0;
    }
}
