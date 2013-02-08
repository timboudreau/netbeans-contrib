/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 */
package org.netbeans.modules.javahints;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import javax.lang.model.element.Element;
import static org.netbeans.spi.editor.hints.ErrorDescriptionFactory.createErrorDescription;
import com.sun.source.tree.Tree;
import static org.netbeans.modules.editor.java.Utilities.isExcludeMethods;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.editor.java.Utilities;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

/**
 * Provides warnings when the Java Excluder is violated. The Import hint catches imports
 * of excluded classes, so we don't show warnings for assignations, casts, etc.
 *
 * @author Samuel Halliday
 * @see <a href="http://www.netbeans.org/issues/show_bug.cgi?id=125060">RFE 125060</a>
 * @see org.netbeans.modules.editor.java.Utilities#isExcludeMethods()
 * @see org.netbeans.modules.editor.java.Utilities#isExcluded(CharSequence)
 */
public class Excluded extends AbstractHint {

    private final AtomicBoolean cancel = new AtomicBoolean();
    private static final List<Fix> NO_FIXES = Collections.<Fix>emptyList();

    public Excluded() {
        super(false, false, HintSeverity.WARNING);
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(getClass(), "DSC_Excluded"); //NOI18N
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.METHOD_INVOCATION, Kind.NEW_CLASS);
    }

    public String getId() {
        return getClass().getName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "DN_Excluded"); //NOI18N
    }

    public void cancel() {
        cancel.set(true);
    }

    public List<ErrorDescription> run(CompilationInfo ci, TreePath tp) {
        if (tp == null) {
            return null;
        }
        Tree t = tp.getLeaf();
        CharSequence fqn;
        Trees trees = ci.getTrees();
        switch (t.getKind()) {
            case METHOD_INVOCATION:
                Element e = trees.getElement(tp);
                if (e == null) {
                    fqn = null;
                    break;
                }
                Element encl = e.getEnclosingElement();
                if (isExcludeMethods()) {
                    fqn = Utilities.getElementName(encl, true) + "." + e.getSimpleName(); //NOI18N
                } else {
                    fqn = Utilities.getElementName(encl, true);
                }
                break;
            case NEW_CLASS:
                TreePath tpid = TreePath.getPath(tp, ((NewClassTree)t).getIdentifier());
                fqn = Utilities.getElementName(trees.getElement(tpid), true);
                break;
//            case PARAMETERIZED_TYPE:
//                TreePath tt = TreePath.getPath(tp, ((ParameterizedTypeTree)t).getType());
//                fqn = Utilities.getElementName(trees.getElement(tt), true);
//                break;
            default:
                return null;
        }
        if (fqn == null || fqn.length() == 0 || !Utilities.isExcluded(fqn)) {
            return null;
        }
        CompilationUnitTree cu = ci.getCompilationUnit();
        SourcePositions sp = ci.getTrees().getSourcePositions();
        ErrorDescription desc = createErrorDescription(
                getSeverity().toEditorSeverity(),
                NbBundle.getMessage(getClass(), "HINT_Excluded", fqn),
                NO_FIXES,
                ci.getFileObject(),
                (int) sp.getStartPosition(cu, t),
                (int) sp.getEndPosition(cu, t));
        return Collections.singletonList(desc);
    }
}
