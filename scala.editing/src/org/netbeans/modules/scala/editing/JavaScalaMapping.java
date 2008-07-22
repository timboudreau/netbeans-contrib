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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.scala.editing.nodes.types.Type;

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
    private static Map<Character, String> ScalaToJavaOpName = new HashMap<Character, String>();
    private static Map<String, Character> JavaToScalaOpName = new HashMap<String, Character>();
    private static Map<String, String> ScalaTypeToJavaType = new HashMap<String, String>();

    static {
        ScalaToJavaOpName.put('$', "$");
        ScalaToJavaOpName.put('~', "$tilde");
        ScalaToJavaOpName.put('=', "$eq");
        ScalaToJavaOpName.put('<', "$less");
        ScalaToJavaOpName.put('>', "$greater");
        ScalaToJavaOpName.put('!', "$bang");
        ScalaToJavaOpName.put('#', "$hash");
        ScalaToJavaOpName.put('%', "$percent");
        ScalaToJavaOpName.put('^', "$up");
        ScalaToJavaOpName.put('&', "$amp");
        ScalaToJavaOpName.put('|', "$bar");
        ScalaToJavaOpName.put('*', "$times");
        ScalaToJavaOpName.put('/', "$div");
        ScalaToJavaOpName.put('\\', "$bslash");
        ScalaToJavaOpName.put('+', "$plus");
        ScalaToJavaOpName.put('-', "$minus");
        ScalaToJavaOpName.put(':', "$colon");

        JavaToScalaOpName.put("$", '$');
        JavaToScalaOpName.put("$tilde", '~');
        JavaToScalaOpName.put("$eq", '=');
        JavaToScalaOpName.put("$less", '<');
        JavaToScalaOpName.put("$greater", '>');
        JavaToScalaOpName.put("$bang", '!');
        JavaToScalaOpName.put("$hash", '#');
        JavaToScalaOpName.put("$percent", '%');
        JavaToScalaOpName.put("$up", '^');
        JavaToScalaOpName.put("$amp", '&');
        JavaToScalaOpName.put("$bar", '|');
        JavaToScalaOpName.put("$times", '*');
        JavaToScalaOpName.put("$div", '/');
        JavaToScalaOpName.put("$bslash", '\\');
        JavaToScalaOpName.put("$plus", '+');
        JavaToScalaOpName.put("$minus", '-');
        JavaToScalaOpName.put("$colon", ':');
        
        ScalaTypeToJavaType.put("scala.Any", "scala.ScalaObject");
        ScalaTypeToJavaType.put("scala.AnyRef", "scala.ScalaObject");
        ScalaTypeToJavaType.put("scala.AnyVal", "scala.ScalaObject");
        ScalaTypeToJavaType.put("scala.Double", "double");
        ScalaTypeToJavaType.put("scala.Float", "float");
        ScalaTypeToJavaType.put("scala.Long", "long");
        ScalaTypeToJavaType.put("scala.Int", "int");
        ScalaTypeToJavaType.put("scala.Short", "short");
        ScalaTypeToJavaType.put("scala.Byte", "byte");
        ScalaTypeToJavaType.put("scala.Boolean", "boolean");
        ScalaTypeToJavaType.put("scala.Unit", "void");
        ScalaTypeToJavaType.put("scala.Char", "char");
        ScalaTypeToJavaType.put("String", "String");
        ScalaTypeToJavaType.put("<error>", "Object");        
        ScalaTypeToJavaType.put("<none>", "void");        
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

    public static String toJavaOpName(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            String s = ScalaToJavaOpName.get(c);
            if (s != null) {
                sb.append(s);
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String toScalaOpName(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '$') {
                int j;
                for (j = i + 1; j < name.length(); j++) {
                    if (name.charAt(j) == '$') {
                        break;
                    }
                }

                if (j > i) {
                    String seg = name.substring(i, j);
                    Character c1 = JavaToScalaOpName.get(seg);
                    if (c1 != null) {
                        sb.append(c1);
                    } else {
                        sb.append(seg);
                    }
                } else {
                    sb.append(c);
                }

                i = j - 1;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String toJavaType(String qName) {
        String javaQName = ScalaTypeToJavaType.get(qName);
        return javaQName != null ? javaQName : qName;
    }

    public static int isFunctionType(TypeMirror functionType) {
        int paramNum = -1;
        String funQName = Type.qualifiedNameOf(functionType);
        int lastDot = funQName.lastIndexOf('.');
        if (lastDot != -1) {
            String pkgName = funQName.substring(0, lastDot);
            String funSName = funQName.substring(lastDot + 1, funQName.length());
            if (pkgName.equals("scala")) {
                if (funSName.startsWith("Function")) {
                    String paramNumStr = funSName.substring(8, funSName.length());
                    try {
                        paramNum = Integer.parseInt(paramNumStr);
                    } catch (Exception ex) {
                        paramNum = -1;
                    }
                }
            }
        }

        return paramNum;
    }

    public static String classFunctionToScalaSName(TypeMirror type, int paramNum, List<VariableElement> params) {
        StringBuilder sb = new StringBuilder();

        if (paramNum != -1) {
            // last one is return type
            //assert paramNum == params.size();
            DeclaredType declType = (DeclaredType) type;
            if (paramNum == 0) {
            } else if (paramNum == 1) {
                sb.append("=> ");
                sb.append(Type.simpleNameOf(params.get(0).asType()));
            } else {
                sb.append("(");
                for (int i = 0; i < params.size() - 1; i++) {
                    sb.append(Type.simpleNameOf(params.get(i).asType()));
                    if (i < params.size() - 2) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
                sb.append(" => _");
                //sb.append(Type.simpleNameOf(params.get(params.size() - 1).asType()));
            }
        }

        return sb.toString();
    }
}
