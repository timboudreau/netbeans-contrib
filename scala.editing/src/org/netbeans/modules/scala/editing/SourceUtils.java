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
package org.netbeans.modules.scala.editing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstScope;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import scala.tools.nsc.symtab.Symbols.Symbol;

/**
 *
 * @author Caoyuan Deng
 */
public class SourceUtils {

    /**
     * Returns classes declared in the given source file which have the main method.
     * @param fo source file
     * @return the classes containing main method
     * @throws IllegalArgumentException when file does not exist or is not a java source file.
     */
    public static Collection<AstDef> getMainClasses(final FileObject fo) {
        if (fo == null || !fo.isValid() || fo.isVirtual()) {
            throw new IllegalArgumentException();
        }
        final Source source = Source.create(fo);
        if (source == null) {
            throw new IllegalArgumentException();
        }
        try {
            final List<AstDef> result = new ArrayList<AstDef>();
            ParserManager.parse(Collections.singleton(source), new UserTask() {

                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ScalaParserResult pResult = (ScalaParserResult) resultIterator.getParserResult();
                    AstRootScope rootScope = pResult.rootScope();
                    if (rootScope == null) {
                        return;
                    }
                    // Get all defs will return all visible packages from the root and down
                    final List<AstDef> visibleDefs = getAllDefs(rootScope, ElementKind.PACKAGE);
                    for (AstDef packaging : visibleDefs) {
                        // Only go through the defs for each package scope.
                        // Sub-packages are handled by the fact that
                        // getAllDefs will find them.
                        List<AstDef> objs = packaging.getBindingScope().getDefs();
                        for (AstDef obj : objs) {
                            if (isMainMethodPresent(obj)) {
                                result.add(obj);
                            }
                        }
                    }
                    for (AstDef obj : rootScope.getVisibleDefs(ElementKind.MODULE)) {
                        if (isMainMethodPresent(obj)) {
                            result.add(obj);
                        }
                    }
                }

                public List<AstDef> getAllDefs(AstScope rootScope, ElementKind kind) {
                    List<AstDef> result = new ArrayList<AstDef>();
                    getAllDefs(rootScope, kind, result);

                    return result;
                }

                private final void getAllDefs(AstScope astScope, ElementKind kind, List<AstDef> result) {
                    for (AstDef def : astScope.getDefs()) {
                        if (def.getKind() == kind) {
                            result.add(def);
                        }
                    }
                    for (AstScope childScope : astScope.getSubScopes()) {
                        getAllDefs(childScope, kind, result);
                    }
                }
            });
            
            return result;
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
            return Collections.<AstDef>emptySet();
        }
    }

    public static boolean isMainMethodPresent(AstDef obj) {
        final scala.List<Symbol> members = obj.getType().members();
        for (int j = 0; j < members.length(); j++) {
            Symbol methodCandidate = members.apply(j);
            if (methodCandidate.isMethod() && isMainMethod(methodCandidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the method is a main method
     * @param method to be checked
     * @return true when the method is a main method
     */
    public static boolean isMainMethod(final Symbol method) {
        if (!method.nameString().equals("main")) {                //NOI18N
            return false;
        }
        method.tpe().paramTypes();
        scala.List params = method.tpe().paramTypes();
        if (params != null && params.size() != 1) {
            return false;
        }
        return true;
    }

    /**
     * Returns classes declared under the given source roots which have the main method.
     * @param sourceRoots the source roots
     * @return the classes containing the main methods
     * Currently this method is not optimized and may be slow
     */
    public static Collection<AstDef> getMainClasses(final FileObject[] sourceRoots) {
        final List<AstDef> result = new LinkedList<AstDef>();
        for (FileObject root : sourceRoots) {
            result.addAll(getMainClasses(root));
            try {
                ClassPath bootPath = ClassPath.getClassPath(root, ClassPath.BOOT);
                ClassPath compilePath = ClassPath.getClassPath(root, ClassPath.COMPILE);
                ClassPath srcPath = ClassPathSupport.createClassPath(new FileObject[]{root});
                ClasspathInfo cpInfo = ClasspathInfo.create(bootPath, compilePath, srcPath);
//                final Set<AstElement> classes = cpInfo.getClassIndex().getDeclaredTypes("", ClassIndex.NameKind.PREFIX, EnumSet.of(ClassIndex.SearchScope.SOURCE));
//                Source js = Source.create(cpInfo);
//                js.runUserActionTask(new CancellableTask<CompilationController>() {
//
//                    public void cancel() {
//                    }
//
//                    public void run(CompilationController control) throws Exception {
//                        for (AstElement cls : classes) {
//                            TypeElement te = cls.resolve(control);
//                            if (te != null) {
//                                Iterable<? extends ExecutableElement> methods = ElementFilter.methodsIn(te.getEnclosedElements());
//                                for (ExecutableElement method : methods) {
//                                    if (isMainMethod(method)) {
//                                        if (isIncluded(cls, control.getClasspathInfo())) {
//                                            result.add(cls);
//                                        }
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }, false);
            } catch (Exception ioe) {
                Exceptions.printStackTrace(ioe);
                return Collections.<AstDef>emptySet();
            }
        }
        return result;
    }
}
