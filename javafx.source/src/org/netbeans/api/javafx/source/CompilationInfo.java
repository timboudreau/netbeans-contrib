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
import com.sun.tools.javafx.api.JavafxcTrees;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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

    public Types getTypes() {
        return impl.getJavafxcTask().getTypes();
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
}
