/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007-2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.javahints.epi.JavaFix;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

/**
 *
 * @author Jaroslav Tulach
 */
public class ImageUtilitiesDeprecation extends AbstractHint {

    public ImageUtilitiesDeprecation() {
        super(true, false, HintSeverity.WARNING);
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ImageUtilitiesDeprecation.class, "DESC_ImageUtilities");
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.METHOD_INVOCATION);
    }

    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        return run(compilationInfo, treePath, CaretAwareJavaSourceTaskFactory.getLastPosition(compilationInfo.getFileObject()));
    }

    protected List<ErrorDescription> run(CompilationInfo info, TreePath treePath, int offset) {
        if (!Kind.METHOD_INVOCATION.equals(treePath.getLeaf().getKind())) {
            return null;
        }
        MethodInvocationTree mit = (MethodInvocationTree) treePath.getLeaf();
        TreePath ms = new TreePath(treePath, mit.getMethodSelect());
        Element e = info.getTrees().getElement(ms);
        if (e != null && (
                e.getSimpleName().contentEquals("loadImage") ||
                e.getSimpleName().contentEquals("mergeImages") ||
                e.getSimpleName().contentEquals("icon2Image")
            ) && info.getElementUtilities().enclosingTypeElement(e).getQualifiedName().contentEquals("org.openide.util.Utilities")
        ) {
            int[] span;

            switch (mit.getMethodSelect().getKind()) {
                case MEMBER_SELECT: span = info.getTreeUtilities().findNameSpan((MemberSelectTree) mit.getMethodSelect()); break;
                case IDENTIFIER:
                    span = new int[2];
                    span[0] = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), mit.getMethodSelect());
                    span[1] = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), mit.getMethodSelect());
                    break;
                default:
                    span = null;
            }

            if (span == null) {
                return null;
            }

            String fixDN = NbBundle.getMessage(ImageUtilitiesDeprecation.class, "FIX_ImageUtilities");
            List<Fix> fix = Collections.<Fix>singletonList(JavaFix.rewriteFix(info, fixDN, ms, "org.openide.util.ImageUtilities." + e.getSimpleName().toString()));
            ErrorDescription w = ErrorDescriptionFactory.createErrorDescription(getSeverity().toEditorSeverity(), "Use of Utilities." + e.getSimpleName().toString(), fix, info.getFileObject(), span[0], span[1]);
            return Collections.singletonList(w);
        }
        return null;
    }

    public String getId() {
        return ImageUtilitiesDeprecation.class.getName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(ImageUtilitiesDeprecation.class, "DN_ImageUtilities");
    }

    public void cancel() {
    }

}
