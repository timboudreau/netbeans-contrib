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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.support.ClassPathSupport;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.tmpls.ObjectTemplate;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.napi.gsfret.source.ClasspathInfo;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

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
    public static Collection<TypeElement> getMainClasses(final FileObject fo) {
        if (fo == null || !fo.isValid() || fo.isVirtual()) {
            throw new IllegalArgumentException();
        }
        final Source js = Source.forFileObject(fo);
        if (js == null) {
            throw new IllegalArgumentException();
        }
        try {
            final List<TypeElement> result = new LinkedList<TypeElement>();
            js.runUserActionTask(new CancellableTask<CompilationController>() {

                public void cancel() {
                }

                public void run(final CompilationController control) throws Exception {
                    if (control.toPhase(Phase.ELEMENTS_RESOLVED).compareTo(Phase.ELEMENTS_RESOLVED) >= 0) {
                        ScalaParserResult pResult = (ScalaParserResult) control.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
                        AstScope rootScope = pResult.getRootScope();
                        if (rootScope == null) {
                            return;
                        }

                        List<ObjectTemplate> objs = null;
                        for (Packaging packaging : rootScope.getVisibleElements(Packaging.class)) {
                            objs = packaging.getBindingScope().getVisibleElements(ObjectTemplate.class);
                            break;
                        }
                        if (objs == null) {
                            objs = rootScope.getVisibleElements(ObjectTemplate.class);
                        }

                        for (ObjectTemplate obj : objs) {
                            List<ExecutableElement> methods = obj.getBindingScope().getVisibleElements(ExecutableElement.class);
                            for (ExecutableElement method : methods) {
                                if (isMainMethod(method)) {
                                    result.add(obj);
                                    break;
                                }
                            }
                        }
                    }
                }
            }, true);
            return result;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
            return Collections.<TypeElement>emptySet();
        }
    }

    /**
     * Returns true when the class contains main method.
     * @param qualifiedName the fully qualified name of class
     * @param cpInfo the classpath used to resolve the class
     * @return true when the class contains a main method
     */
    public static boolean isMainClass(final String qualifiedName, ClasspathInfo cpInfo) {
        if (qualifiedName == null || cpInfo == null) {
            throw new IllegalArgumentException();
        }
        final boolean[] result = new boolean[]{false};
        Source js = Source.create(cpInfo);
        try {
            js.runUserActionTask(new CancellableTask<CompilationController>() {

                public void cancel() {
                }

                public void run(CompilationController control) throws Exception {
                    if (control.toPhase(Phase.ELEMENTS_RESOLVED).compareTo(Phase.ELEMENTS_RESOLVED) >= 0) {
                        ScalaParserResult pResult = (ScalaParserResult) control.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
                        AstScope rootScope = pResult.getRootScope();
                        if (rootScope == null) {
                            return;
                        }
                        
                        List<ObjectTemplate> objs = null;
                        for (Packaging packaging : rootScope.getVisibleElements(Packaging.class)) {
                            objs = packaging.getBindingScope().getVisibleElements(ObjectTemplate.class);
                            break;
                        }
                        if (objs == null) {
                            objs = rootScope.getVisibleElements(ObjectTemplate.class);
                        }

                        for (ObjectTemplate obj : objs) {
                            if (obj.getSimpleName().toString().equals(qualifiedName)) {
                                List<ExecutableElement> methods = obj.getBindingScope().getVisibleElements(ExecutableElement.class);
                                for (ExecutableElement method : methods) {
                                    if (isMainMethod(method)) {
                                        result[0] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }, true);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return result[0];
    }

    /**
     * Returns true if the method is a main method
     * @param method to be checked
     * @return true when the method is a main method
     */
    public static boolean isMainMethod(final ExecutableElement method) {
        if (!method.getSimpleName().toString().equals("main")) {                //NOI18N

            return false;
        }
        List<? extends VariableElement> params = method.getParameters();
        if (params != null && params.size() != 1) {
            return false;
        }
//        TypeMirror param = params.get(0).asType();
//        if (param.getKind() != TypeKind.ARRAY) {
//            return false;
//        }
//        ArrayType array = (ArrayType) param;
//        TypeMirror compound = array.getComponentType();
//        if (compound.getKind() != TypeKind.DECLARED) {
//            return false;
//        }
//        if (!"java.lang.String".contentEquals(((TypeElement) ((DeclaredType) compound).asElement()).getQualifiedName())) {   //NOI18N
//
//            return false;
//        }
        return true;
    }

    /**
     * Returns classes declared under the given source roots which have the main method.
     * @param sourceRoots the source roots
     * @return the classes containing the main methods
     * Currently this method is not optimized and may be slow
     */
    public static Collection<TypeElement> getMainClasses(final FileObject[] sourceRoots) {
        final List<TypeElement> result = new LinkedList<TypeElement>();
        for (FileObject root : sourceRoots) {
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
                return Collections.<TypeElement>emptySet();
            }
        }
        return result;
    }
}
