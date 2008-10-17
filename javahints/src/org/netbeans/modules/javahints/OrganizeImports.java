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

package org.netbeans.modules.javahints;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class OrganizeImports extends AbstractHint {

    public OrganizeImports() {
        super(false, false, HintSeverity.WARNING);
    }

    @Override
    public String getDescription() {
        return "Organize Imports";
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.COMPILATION_UNIT);
    }

    public List<ErrorDescription> run(CompilationInfo info, TreePath treePath) {
        try {
            if (GuardedSectionManager.getInstance((StyledDocument) info.getDocument()) != null) {
                return null;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        Fix f = new FixImpl(TreePathHandle.create(new TreePath(info.getCompilationUnit()), info));
        List<Fix> l = Collections.singletonList(f);
        ErrorDescription ed = ErrorDescriptionFactory.createErrorDescription(Severity.WARNING, "Organize Imports", l, info.getFileObject(), 0, 1);

        return Collections.singletonList(ed);
    }

    public String getId() {
        return OrganizeImports.class.getName();
    }

    public String getDisplayName() {
        return "Organize Imports";
    }

    public void cancel() {
    }

    private static final class FixImpl implements Fix {

        private final TreePathHandle h;

        public FixImpl(TreePathHandle h) {
            this.h = h;
        }

        public String getText() {
            return "Organize Imports";
        }

        public ChangeInfo implement() throws Exception {
            JavaSource js = JavaSource.forFileObject(h.getFileObject());

            js.runModificationTask(new Task<WorkingCopy>() {

                public void run(WorkingCopy parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    organizeImports(parameter);
                }
            }).commit();

            return null;
        }
    }

    private static void organizeImports(WorkingCopy copy) {
        CompilationUnitTree old = copy.getCompilationUnit();

        new ReplaceQualifiedNames(copy).scan(old, null);

        CompilationUnitTree nue = copy.getTreeMaker().CompilationUnit(old.getPackageName(), Collections.<ImportTree>emptyList(), old.getTypeDecls(), old.getSourceFile());

        copy.rewrite(old, nue);
    }

    private static final class ReplaceQualifiedNames extends TreePathScanner<Void, Void> {

        private final WorkingCopy copy;

        public ReplaceQualifiedNames(WorkingCopy copy) {
            this.copy = copy;
        }

        private static final Set<String> PROHIBITED_NAMES = new HashSet<String>(Arrays.asList("super", "this", "<init>"));
        
        private static final Set<ElementKind> SITES_GOOD_FOR_IMPORTS = EnumSet.of(ElementKind.ANNOTATION_TYPE, ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.PACKAGE);
        
        @Override
        public Void visitMemberSelect(MemberSelectTree node, Void p) {
            if (PROHIBITED_NAMES.contains(node.getIdentifier().toString())) {
                return super.visitMemberSelect(node, p);
            }

            Element e = copy.getTrees().getElement(getCurrentPath());
            Element site = copy.getTrees().getElement(new TreePath(getCurrentPath(), node.getExpression()));

            if (e != null && site != null && SITES_GOOD_FOR_IMPORTS.contains(site.getKind())) {
                copy.rewrite(node, copy.getTreeMaker().QualIdent(e));
                return null;
            }
            
            return super.visitMemberSelect(node, p);
        }

        private static final Set<ElementKind> LOCAL_VARIABLES = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.LOCAL_VARIABLE, ElementKind.PARAMETER);

        @Override
        public Void visitIdentifier(IdentifierTree node, Void p) {
            if (PROHIBITED_NAMES.contains(node.getName().toString())) {
                return super.visitIdentifier(node, p);
            }

            Element e = copy.getTrees().getElement(getCurrentPath());

            if (e != null && !LOCAL_VARIABLES.contains(e.getKind())) {
                if (e.getKind().isClass() || e.getKind().isInterface()) {
                    copy.rewrite(node, copy.getTreeMaker().QualIdent(e));
                    return null;
                }

                if (!classes.contains(e.getEnclosingElement())) {
                    copy.rewrite(node, copy.getTreeMaker().QualIdent(e));
                    return null;
                }
            }

            return super.visitIdentifier(node, p);
        }

        @Override
        public Void visitCompilationUnit(CompilationUnitTree node, Void p) {
            scan(node.getPackageAnnotations(), p);
            scan(node.getImports(), p);
            scan(node.getTypeDecls(), p);
            return null;
        }

        private final Set<Element> classes = new HashSet<Element>();

        @Override
        public Void visitClass(ClassTree node, Void p) {
            classes.add(copy.getTrees().getElement(getCurrentPath()));
            return super.visitClass(node, p);
        }

    }
}
