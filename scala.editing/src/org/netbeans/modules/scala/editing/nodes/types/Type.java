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

import org.netbeans.modules.scala.editing.nodes.BasicType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.netbeans.modules.scala.editing.nodes.BasicTypeElement;
import org.netbeans.modules.scala.editing.nodes.Importing;

/**
 *
 * @author Caoyuan Deng
 */
public class Type extends AstMirror implements DeclaredType {

    // ----- Predefined Type
    public static final Type Any = new Type("Any", null, TypeKind.NONE) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Any;
        }
    };
    public static final Type AnyRef = new Type("AnyRef", null, TypeKind.NONE) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.AnyRef;
        }
    };
    public static final Type AnyVal = new Type("AnyVal", null, TypeKind.NONE) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.AnyVal;
        }
    };
    public static final Type Double = new Type("Double", null, TypeKind.DOUBLE) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Double;
        }
    };
    public static final Type Float = new Type("Float", null, TypeKind.FLOAT) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Float;
        }
    };
    public static final Type Long = new Type("Long", null, TypeKind.LONG) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Long;
        }
    };
    public static final Type Int = new Type("Int", null, TypeKind.INT) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Int;
        }
    };
    public static final Type Short = new Type("Short", null, TypeKind.SHORT) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Short;
        }
    };
    public static final Type Byte = new Type("Byte", null, TypeKind.BYTE) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Byte;
        }
    };
    public static final Type Boolean = new Type("Boolean", null, TypeKind.BOOLEAN) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Boolean;
        }
    };
    public static final Type Null = new Type("Unit", null, TypeKind.NULL) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Null;
        }
    };
    public static final Type Char = new Type("Char", null, TypeKind.CHAR) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Char;
        }
    };
    public static final Type String = new Type("String", null, TypeKind.DECLARED) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.String;
        }
    };
    public static final Type Symbol = new Type("Symbol", null, TypeKind.OTHER) {

        @Override
        public TypeElement asElement() {
            return BasicTypeElement.Symbol;
        }
    };
    public static Map<String, Type> PRED_TYPES = new HashMap<String, Type>();
    

    static {
        PRED_TYPES.put("Any", Any);
        PRED_TYPES.put("AnyRef", AnyRef);
        PRED_TYPES.put("AnyVal", AnyVal);
        PRED_TYPES.put("Double", Double);
        PRED_TYPES.put("double", Double);
        PRED_TYPES.put("Float", Float);
        PRED_TYPES.put("float", Float);
        PRED_TYPES.put("Long", Long);
        PRED_TYPES.put("long", Long);
        PRED_TYPES.put("Int", Int);
        PRED_TYPES.put("int", Int);
        PRED_TYPES.put("Short", Short);
        PRED_TYPES.put("short", Short);
        PRED_TYPES.put("Byte", Byte);
        PRED_TYPES.put("byte", Byte);
        PRED_TYPES.put("Boolean", Boolean);
        PRED_TYPES.put("boolean", Boolean);
        PRED_TYPES.put("Unit", Null);
        PRED_TYPES.put("unit", Null);
        PRED_TYPES.put("Char", Char);
        PRED_TYPES.put("char", Char);
        PRED_TYPES.put("String", String);
    }
    private List<String> annotations;
    private List<? extends TypeMirror> typeArgs;
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

    public void setTypeArgs(List<? extends TypeMirror> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public List<? extends TypeMirror> getTypeArguments() {
        return typeArgs == null ? Collections.<Type>emptyList() : typeArgs;
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

        Type predType = PRED_TYPES.get(getSimpleName().toString());
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
        if (typeArgs != null) {
            formatter.appendText("[");
            if (typeArgs.isEmpty()) {
                // wildcard
                formatter.appendText("_");
            } else {
                for (Iterator<? extends TypeMirror> itr = typeArgs.iterator(); itr.hasNext();) {
                    TypeMirror typeArg = itr.next();
                    if (typeArgs instanceof Type) {
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
            return ((BasicType) type).getSimpleName().toString();
        } else {
            return type.getKind() == TypeKind.DECLARED
                    ? ((DeclaredType) type).asElement().getSimpleName().toString()
                    : type.getKind().name();
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
            return (TypeElement) PRED_TYPES.get(typeSName.toLowerCase()).asElement();
        }
    }

    @Override
    public String toString() {
        return "TypeMirror(sName=" + getSimpleName() + ")";
    }
}
