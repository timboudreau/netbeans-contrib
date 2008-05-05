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
package org.netbeans.modules.javafx.editor.completion;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.javafx.source.CompilationController;

class JavaFXCompletionEnvironment {

    private int offset;
    private String prefix;
    private boolean isCamelCasePrefix;
    private CompilationController controller;
    private TreePath path;
    private SourcePositions sourcePositions;
    private boolean insideForEachExpressiion = false;
    private Set<? extends TypeMirror> smartTypes = null;

    JavaFXCompletionEnvironment(int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions) {
        super();
        this.offset = offset;
        this.prefix = prefix;
        this.isCamelCasePrefix = prefix != null && prefix.length() > 1 && JavaFXCompletionQuery.camelCasePattern.matcher(prefix).matches();
        this.controller = controller;
        this.path = path;
        this.sourcePositions = sourcePositions;
    }

    public int getOffset() {
        return offset;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isCamelCasePrefix() {
        return isCamelCasePrefix;
    }

    public CompilationController getController() {
        return controller;
    }

    public CompilationUnitTree getRoot() {
        return path.getCompilationUnit();
    }

    public TreePath getPath() {
        return path;
    }

    public SourcePositions getSourcePositions() {
        return sourcePositions;
    }

    public void insideForEachExpressiion() {
        this.insideForEachExpressiion = true;
    }

    public boolean isInsideForEachExpressiion() {
        return insideForEachExpressiion;
    }

    public Set<? extends TypeMirror> getSmartTypes() throws IOException {
        if (smartTypes == null) {
            smartTypes = JavaFXCompletionQuery.getSmartTypes(JavaFXCompletionEnvironment.this);
            if (smartTypes != null) {
                Iterator<? extends TypeMirror> it = smartTypes.iterator();
                TypeMirror err = null;
                if (it.hasNext()) {
                    err = it.next();
                    if (it.hasNext() || err.getKind() != TypeKind.ERROR) {
                        err = null;
                    }
                }
                if (err != null) {
                    HashSet<TypeMirror> st = new HashSet<TypeMirror>();
                    smartTypes = st;
                }
            }
        }
        return smartTypes;
    }
}
