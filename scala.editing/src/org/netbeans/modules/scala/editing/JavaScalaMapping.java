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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Caoyuan Deng
 */
public class JavaScalaMapping {

    private static final String SCALA_OBJECT = "scala.ScalaObject";
    private static final String TAG_METHOD = "$tag";

    public static boolean isScala(TypeElement te) {
        for (Element e : te.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) e;
                ExecutableType methodType = (ExecutableType) method.asType();

                if (e.getSimpleName().toString().equals(TAG_METHOD) && method.getParameters().isEmpty()) {
                    if (methodType.getReturnType().getKind() == TypeKind.INT) {
                        Element enclosingElement = e.getEnclosingElement();
                        if (enclosingElement.getKind() == ElementKind.INTERFACE) {
                            TypeMirror superTm = enclosingElement.asType();
                            TypeElement superTe = superTm.getKind() == TypeKind.DECLARED
                                    ? (TypeElement) ((DeclaredType) superTm).asElement()
                                    : null;

                            if (superTe != null && superTe.getQualifiedName().toString().equals(SCALA_OBJECT)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
