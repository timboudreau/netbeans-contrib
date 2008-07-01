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
package org.netbeans.modules.scala.editing.nodes.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstMirror;
import org.netbeans.modules.scala.editing.nodes.BasicName;
import org.netbeans.modules.scala.editing.nodes.BasicType;
import org.netbeans.modules.scala.editing.nodes.BasicTypeElement;
import org.netbeans.modules.scala.editing.nodes.Importing;

/**
 *
 * @author Caoyuan Deng
 */
public class Type extends AstMirror implements DeclaredType {

    private List<String> annotations;
    private List<? extends TypeMirror> typeArguments;
    private TypeKind kind;
    private TypeElement element;

    public Type(CharSequence name, Token pickToken, TypeKind kind) {
        super(name, pickToken);
        this.kind = kind;
    }

    public <R, P> R accept(TypeVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeKind getKind() {
        return kind;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setTypeArguments(List<? extends TypeMirror> typeArguments) {
        this.typeArguments = typeArguments;
    }

    public List<? extends TypeMirror> getTypeArguments() {
        return typeArguments == null ? Collections.<Type>emptyList() : typeArguments;
    }

    public String getTypeArgsName() {
        if (!getTypeArguments().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Iterator<? extends TypeMirror> itr = getTypeArguments().iterator(); itr.hasNext();) {
                sb.append(simpleNameOf(itr.next()));
                if (itr.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
        return "";
    }

    public boolean isResolved() {
        return asElement() != null;
    //return !getQualifiedName().toString().equals(UNRESOLVED.toString());
    }

    @Override
    public Type asType() {
        return this;
    }

    public TypeMirror getEnclosingType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setElement(TypeElement element) {
        this.element = element;
    }

    public TypeElement asElement() {
        if (element != null) {
            return element;
        }

        DeclaredType predType = PredefinedTypes.PRED_TYPES.get(getSimpleName().toString());
        if (predType != null) {
            element = (TypeElement) predType.asElement();
        }

        AstElement defElement = getEnclosingScope().findElementOf(this);
        if (defElement != null) {
            if (defElement instanceof TypeDef) {
                Type value = ((TypeDef) defElement).getValue();
                if (value != null) {
                    element = (TypeElement) value.asElement();
                }
            } else {
                // should be TypeElement
                assert defElement instanceof TypeElement;
                element = (TypeElement) defElement;
            }

        }

        if (element != null) {
            return element;
        }

        /** @Todo global infer/checking of imported types */
        List<Importing> importings = getEnclosingScope().getVisibleElements(Importing.class);
        for (Importing importing : importings) {
            for (Type importedType : importing.getImportedTypes()) {
                if (importedType.element == null) { // don't call importedType.asElement() here, which causes cycled calling
                    String sName = importedType.getSimpleName().toString();
                    String qName = importing.getPackageName() + "." + sName;
                    importedType.setElement(new BasicTypeElement(sName, new BasicName(qName)));
                }

                if (importedType.getSimpleName().toString().equals(getSimpleName().toString())) {
                    element = (TypeElement) importedType.asElement();
                    break;
                }
            }
        }

        return element;
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        if (getSimpleName() != null) {
            formatter.appendText(getSimpleName().toString());
            htmlFormatTypeArgs(formatter);
        }
    }

    public void htmlFormatTypeArgs(HtmlFormatter formatter) {
        if (typeArguments != null) {
            formatter.appendText("[");
            if (typeArguments.isEmpty()) {
                // wildcard
                formatter.appendText("_");
            } else {
                for (Iterator<? extends TypeMirror> itr = typeArguments.iterator(); itr.hasNext();) {
                    TypeMirror typeArg = itr.next();
                    if (typeArguments instanceof Type) {
                        ((Type) typeArg).htmlFormat(formatter);
                    }
                    if (itr.hasNext()) {
                        formatter.appendText(", ");
                    }
                }
            }
            formatter.appendText("]");
        }
    }

    public static String simpleNameOf(TypeMirror type) {
        if (type instanceof Type) {
            return ((Type) type).getSimpleName().toString();
        } else if (type instanceof BasicType) {
            Name sName = ((BasicType) type).getSimpleName();
            if (sName == null) {
                return null;
            } else {
                return sName.toString();
            }
        } else {
            if (type.getKind() == TypeKind.DECLARED) {
                return ((DeclaredType) type).asElement().getSimpleName().toString();
            } else {
                if (type.getKind() == TypeKind.WILDCARD) {
                    return "_";
                } else {
                    return type.getKind().name().toLowerCase();
                }
            }
        }
    }

    public static String qualifiedNameOf(TypeMirror type) {
        if (type.getKind() == TypeKind.DECLARED) {
            TypeElement te = (TypeElement) ((DeclaredType) type).asElement();
            if (te != null) {
                return te.getQualifiedName().toString();
            } else {
                return null;
            }
        } else {
            return type.getKind().name();
        }
    }

    public static boolean isResolved(TypeMirror type) {
        if (type instanceof Type) {
            return ((Type) type).isResolved();
        } else if (type instanceof BasicType) {
            return ((BasicType) type).isResolved();
        } else {
            if (type.getKind() == TypeKind.DECLARED) {
                return ((DeclaredType) type).asElement() != null;
            } else {
                return true;
            }
        }
    }

    public static TypeElement asElement(TypeMirror type) {
        if (type.getKind() == TypeKind.DECLARED) {
            return (TypeElement) ((DeclaredType) type).asElement();
        } else {
            String typeSName = type.getKind().name();
            // convert to Scala's declared type
            BasicType predType = PredefinedTypes.PRED_TYPES.get(typeSName.toLowerCase());
            if (predType == null) {
                System.out.println("typeSName: " + typeSName + " is not predefined");
                return null;
            } else {
                return (TypeElement) predType.asElement();
            }
        }
    }

    public static void htmlFormat(HtmlFormatter formatter, TypeMirror type, boolean usingSimpleName) {
        if (usingSimpleName) {
            formatter.appendText(simpleNameOf(type));
        } else {
            formatter.appendText(qualifiedNameOf(type));
        }
        if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType dclType = (DeclaredType) type;
            List<? extends TypeMirror> typeArgs = dclType.getTypeArguments();
            if (!typeArgs.isEmpty()) {
                formatter.appendText("[");
                for (Iterator<? extends TypeMirror> itr = typeArgs.iterator(); itr.hasNext();) {
                    TypeMirror typeArg = itr.next();
                    htmlFormat(formatter, typeArg, usingSimpleName);
                    if (itr.hasNext()) {
                        formatter.appendHtml(",");
                    }
                }
                formatter.appendText("]");
            }
        }
    }

    @Override
    public String toString() {
        return "TypeMirror(sName=" + getSimpleName() + ")";
    }
}
