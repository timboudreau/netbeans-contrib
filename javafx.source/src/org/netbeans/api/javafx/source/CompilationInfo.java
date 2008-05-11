/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.api.javafx.source;

import com.sun.javafx.api.JavafxcTask;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javafx.api.JavafxcTrees;
import com.sun.tools.javafx.code.JavafxTypes;
import com.sun.tools.javafx.comp.JavafxEnter;
import com.sun.tools.javafx.comp.JavafxEnv;
import com.sun.tools.javafx.tree.JFXClassDeclaration;
import com.sun.tools.javafx.tree.JFXFunctionDefinition;
import com.sun.tools.javafx.tree.JFXVar;
import com.sun.tools.javafx.tree.JavafxTreeScanner;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.netbeans.api.lexer.TokenHierarchy;

/**
 *
 * @author nenik
 */
public class CompilationInfo {
    final CompilationInfoImpl impl;

    public CompilationInfo(JavaFXSource source) {
        impl = new CompilationInfoImpl(source);
    }

    CompilationInfo(CompilationInfoImpl impl) {
        this.impl = impl;
    }

    public JavaFXSource.Phase getPhase() {
        return impl.getPhase();
    }

    /**
     * Return the {@link com.sun.tools.javafx.api.JavafxcTrees} service of the javafxc represented by this {@link CompilationInfo}.
     * @return javafxc Trees service
     */
    public JavafxcTrees getTrees() {
        return JavafxcTrees.instance(impl.getJavafxcTask());
    }
    
        // XXX: hack around lack of support in compiler
    public TreePath getPath(Element e) {
        Symbol sym = (Symbol)e;
        JavafxEnter enter = JavafxEnter.instance(impl.getContext());
        JavafxEnv env = enter.getEnv(sym.enclClass());
        if (env == null) return null;
        JCTree tree = declarationFor(sym, env.tree);
        return tree == null ? null : getTrees().getPath(getCompilationUnit(), tree);
    }
    
    private static JCTree declarationFor(final Symbol sym, final JCTree tree) {

        class DeclScanner extends JavafxTreeScanner {
            JCTree result = null;
            public void scan(JCTree tree) {
                if (tree!=null && result==null)
                    tree.accept(this);
            }
	    public @Override void visitTopLevel(JCCompilationUnit that) {
		if (that.packge == sym) result = that;
		else super.visitTopLevel(that);
	    }
            public @Override void visitClassDeclaration(JFXClassDeclaration that) {
		if (that.sym == sym) result = that;
		else super.visitClassDeclaration(that);
            }

	    public @Override void visitClassDef(JCClassDecl that) {
		if (that.sym == sym) result = that;
		else super.visitClassDef(that);
	    }

            public @Override void visitFunctionDefinition(JFXFunctionDefinition that) {
		if (that.sym == sym) result = that;
                else super.visitFunctionDefinition(that);
            }
            
	    public @Override void visitMethodDef(JCMethodDecl that) {
		if (that.sym == sym) result = that;
		else super.visitMethodDef(that);
	    }

            public @Override void visitVar(JFXVar that) {
		if (that.sym == sym) result = that;
		else super.visitVarDef(that);
            }
            
	    public @Override void visitVarDef(JCVariableDecl that) {
		if (that.sym == sym) result = that;
		else super.visitVarDef(that);
	    }
	}
	DeclScanner s = new DeclScanner();
	tree.accept(s);
	return s.result;
    }


    public Types getTypes() {
        return impl.getJavafxcTask().getTypes();
    }
    
    public JavafxTypes getJavafxTypes() {
        return JavafxTypes.instance(impl.getContext());
    }
    
    public Elements getElements() {
	return impl.getJavafxcTask().getElements();
    }
    
    /**
     * Returns {@link JavaFXSource} for which this {@link CompilationInfo} was created.
     * @return JavaFXSource
     */
    public JavaFXSource getJavaFXSource() {
        return impl.getJavaFXSource();
    }
    
    /**
     * Returns the javafxc tree representing the source file.
     * @return {@link CompilationUnitTree} the compilation unit cantaining the top level classes contained in the,
     * javafx source file.
     * 
     * @throws java.lang.IllegalStateException  when the phase is less than {@link JavaFXSource.Phase#PARSED}
     */
    public CompilationUnitTree getCompilationUnit() {
        return impl.getCompilationUnit();

    }

    public TokenHierarchy getTokenHierarchy() {
        return impl.getTokenHierarchy();
    }
    
    public List<Diagnostic> getDiagnostics() {
        return this.impl.getDiagnostics();
    }
}
