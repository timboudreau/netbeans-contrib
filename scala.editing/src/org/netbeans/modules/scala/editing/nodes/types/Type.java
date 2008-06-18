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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstMirror;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.Importing;

/**
 *
 * @author Caoyuan Deng
 */
public class Type extends AstMirror implements TypeMirror {

    // ----- Prededined Name
    protected static final Name SCALA_ANY = new AstName("scala.Any");
    protected static final Name SCALA_ANYREF = new AstName("scala.AnyRef");
    protected static final Name SCALA_ANYVAL = new AstName("scala.AnyVal");
    protected static final Name SCALA_DOUBLE = new AstName("scala.Double");
    protected static final Name SCALA_FLOAT = new AstName("scala.Float");
    protected static final Name SCALA_LONG = new AstName("scala.Long");
    protected static final Name SCALA_INT = new AstName("scala.Int");
    protected static final Name SCALA_SHORT = new AstName("scala.Short");
    protected static final Name SCALA_BYTE = new AstName("scala.Byte");
    protected static final Name SCALA_BOOLEAN = new AstName("scala.Boolean");
    protected static final Name SCALA_UNIT = new AstName("scala.Unit");
    protected static final Name SCALA_CHAR = new AstName("scala.Char");
    protected static final Name JAVA_LANG_STRING = new AstName("java.lang.String");
    protected static final Name SCALA_SYMBOL = new AstName("scala.AnyRef");
    // ----- Predefined Type
    public static final Type Any = new Type("Any", null, TypeKind.NONE) {

        @Override
        public Name getQualifiedName() {
            return SCALA_ANY;
        }
    };
    public static final Type AnyRef = new Type("AnyRef", null, TypeKind.NONE) {

        @Override
        public Name getQualifiedName() {
            return SCALA_ANYREF;
        }
    };
    public static final Type AnyVal = new Type("AnyVal", null, TypeKind.NONE) {

        @Override
        public Name getQualifiedName() {
            return SCALA_ANYVAL;
        }
    };
    public static final Type Double = new Type("Double", null, TypeKind.DOUBLE) {

        @Override
        public Name getQualifiedName() {
            return SCALA_DOUBLE;
        }
    };
    public static final Type Float = new Type("Float", null, TypeKind.FLOAT) {

        @Override
        public Name getQualifiedName() {
            return SCALA_FLOAT;
        }
    };
    public static final Type Long = new Type("Long", null, TypeKind.LONG) {

        @Override
        public Name getQualifiedName() {
            return SCALA_LONG;
        }
    };
    public static final Type Int = new Type("Int", null, TypeKind.INT) {

        @Override
        public Name getQualifiedName() {
            return SCALA_INT;
        }
    };
    public static final Type Short = new Type("Short", null, TypeKind.SHORT) {

        @Override
        public Name getQualifiedName() {
            return SCALA_SHORT;
        }
    };
    public static final Type Byte = new Type("Byte", null, TypeKind.BYTE) {

        @Override
        public Name getQualifiedName() {
            return SCALA_BYTE;
        }
    };
    public static final Type Boolean = new Type("Boolean", null, TypeKind.BOOLEAN) {

        @Override
        public Name getQualifiedName() {
            return SCALA_BOOLEAN;
        }
    };
    public static final Type Null = new Type("Unit", null, TypeKind.NULL) {

        @Override
        public Name getQualifiedName() {
            return SCALA_UNIT;
        }
    };
    public static final Type Char = new Type("Char", null, TypeKind.CHAR) {

        @Override
        public Name getQualifiedName() {
            return SCALA_CHAR;
        }
    };
    public static final Type String = new Type("String", null, TypeKind.DECLARED) {

        @Override
        public Name getQualifiedName() {
            return JAVA_LANG_STRING;
        }
    };
    public static final Type Symbol = new Type("Symbol", null, TypeKind.OTHER) {

        @Override
        public Name getQualifiedName() {
            return SCALA_ANYREF;
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
    private static final Name UNRESOLVED = new AstName("-1");
    private List<String> annotations;
    private List<Type> typeArgs;
    private TypeKind kind;

    public Type(CharSequence name, Token pickToken, TypeKind kind) {
        super(name, pickToken);
        this.kind = kind;
    }

    public <R, P> R accept(TypeVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeKind getKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setTypeArgs(List<Type> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public List<Type> getTypeArgs() {
        return typeArgs == null ? Collections.<Type>emptyList() : typeArgs;
    }

    public String getTypeArgsName() {
        if (!getTypeArgs().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Iterator<Type> itr = getTypeArgs().iterator(); itr.hasNext();) {
                sb.append(itr.next().getSimpleName());
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
        return !getQualifiedName().toString().equals(UNRESOLVED.toString());
    }

    @Override
    public Name getQualifiedName() {
        if (qualifiedName != null) {
            return qualifiedName;
        }

        Type predType = PRED_TYPES.get(getSimpleName().toString());
        if (predType != null) {
            qualifiedName = predType.getQualifiedName();
            return qualifiedName;
        }

        AstElement element = getEnclosingScope().findElementOf(this);
        if (element != null) {
            if (element instanceof TypeDef) {
                Type value = ((TypeDef) element).getValue();
                if (value != null) {
                    qualifiedName = value.getQualifiedName();
                    return qualifiedName;
                } else {
                    return UNRESOLVED;
                }
            } else {
                // should be Template
                qualifiedName = element.getQualifiedName();
                return qualifiedName;
            }

        }

        List<Importing> importings = getEnclosingScope().getVisibleElements(Importing.class);
        for (Importing importing : importings) {
            for (Type importedType : importing.getImportedTypes()) {
                if (importedType.getSimpleName().toString().equals(getSimpleName().toString())) {
                    qualifiedName = new AstName(importing.getPackageName() + "." + importedType.getSimpleName());
                    return qualifiedName;
                }
            }
        }

        return UNRESOLVED;
    }

    @Override
    public Type asType() {
        return this;
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
                for (Iterator<Type> itr = typeArgs.iterator(); itr.hasNext();) {
                    itr.next().htmlFormat(formatter);
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
        } else {
            return type.getKind() == TypeKind.DECLARED
                    ? ((DeclaredType) type).asElement().getSimpleName().toString()
                    : type.getKind().name();
        }
    }

    /**
     * Used for remote type, which has qualifiedName field only, without AstNode information
     * 
     */
    public static class PseudoTypeRef extends Type {

        public PseudoTypeRef() {
            super(null, null, TypeKind.DECLARED);
            setEnclosingScope(AstScope.emptyScope());
        }

        public PseudoTypeRef(String qName) {
            this();
            setQualifiedName(qName);
        }

        @Override
        public Name getSimpleName() {
            if (isResolved()) {
                String qName = getQualifiedName().toString();
                int lastDot = qName.lastIndexOf('.');
                if (lastDot > 0) {
                    String sName = qName.substring(lastDot + 1, qName.length());
                    setSimpleName(sName);
                }
            }

            return super.getSimpleName();
        }

        @Override
        public Name getQualifiedName() {
            return qualifiedName == null ? UNRESOLVED : qualifiedName;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getSimpleName());
            sb.append(getTypeArgsName());

            return sb.toString();
        }
    }
}
