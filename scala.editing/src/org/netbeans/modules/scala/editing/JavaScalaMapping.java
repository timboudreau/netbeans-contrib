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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Caoyuan Deng
 */
public class JavaScalaMapping {

    public enum ScalaKind {

        Class,
        Object,
        Trait
    }

    private static final String SCALA_OBJECT = "scala.ScalaObject";
    private static final String SCALA_OBJECT_MODULE = "MODULE$";

    public static ScalaKind getScalaKind(TypeElement te) {
        if (!isScala(te)) {
            return null;
        }

        ScalaKind kind = ScalaKind.Class;

        String sName = te.getSimpleName().toString();

        if (te.getKind() == ElementKind.INTERFACE) {
            kind = ScalaKind.Trait;
        } else {
            for (Element e : te.getEnclosedElements()) {
                if (e.getSimpleName().toString().equals(SCALA_OBJECT_MODULE)) {
                    TypeMirror tm1 = e.asType();
                    TypeElement te1 = tm1.getKind() == TypeKind.DECLARED
                            ? (TypeElement) ((DeclaredType) tm1).asElement()
                            : null;

                    if (te1.getSimpleName().toString().equals(sName)) {
                        kind = ScalaKind.Object;
                        break;
                    }

                }
            }
        }

        return kind;
    }

    public static boolean isScala(TypeElement te) {
        //List<? extends AnnotationMirror> annots = te.getAnnotationMirrors();

        if (te.getQualifiedName().toString().equals(SCALA_OBJECT)) {
            return true;
        }

        TypeMirror superTm = te.getSuperclass();
        TypeElement superTe = superTm.getKind() == TypeKind.DECLARED
                ? (TypeElement) ((DeclaredType) superTm).asElement()
                : null;

        if (superTe != null) {
            String superName = superTe.getQualifiedName().toString();
            if (!(superName.startsWith("java.") || superName.startsWith("javax.")) && isScala(superTe)) {
                return true;
            }
        }

        for (TypeMirror interfaceTm : te.getInterfaces()) {
            TypeElement interfaceTe = interfaceTm.getKind() == TypeKind.DECLARED
                    ? (TypeElement) ((DeclaredType) interfaceTm).asElement()
                    : null;

            if (interfaceTe != null && isScala(interfaceTe)) {
                return true;
            }
        }

        return false;
    }
}
