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

/**
 *
 * @author nenik
 */
public class CompilationInfo {
    final JavaFXSource source;
    private JavafxcTask cTask;
    private CompilationUnitTree compilationUnit;    
    
    JavaFXSource.Phase phase = JavaFXSource.Phase.MODIFIED;

    public CompilationInfo(JavaFXSource source) {
        this.source = source;
    }

    public JavaFXSource.Phase getPhase() {
        return phase;
    }

    JavafxcTask getJavafxcTask() {
        if (cTask == null) {
            cTask = source.createJavafxcTask();
        }
        return cTask;
    }

    /**
     * Returns the javafxc tree representing the source file.
     * @return {@link CompilationUnitTree} the compilation unit cantaining the top level classes contained in the,
     * javafx source file.
     * 
     * @throws java.lang.IllegalStateException  when the phase is less than {@link JavaFXSource.Phase#PARSED}
     */
    public CompilationUnitTree getCompilationUnit() {
//        if (this.jfo == null) {
//            throw new IllegalStateException ();
//        }
        if (phase.lessThan(JavaFXSource.Phase.PARSED))
            throw new IllegalStateException("Cannot call getCompilationInfo() if current phase < JavaFXSource.Phase.PARSED. You must call toPhase(Phase.PARSED) first.");//NOI18N
        return compilationUnit;
    }
    
    void setCompilationUnit(CompilationUnitTree compilationUnit) {
        assert this.compilationUnit == null;
        this.compilationUnit = compilationUnit;
    }

}
