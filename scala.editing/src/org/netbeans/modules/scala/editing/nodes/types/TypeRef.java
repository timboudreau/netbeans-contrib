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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstRef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.Importing;

/**
 *
 * @author Caoyuan Deng
 */
public class TypeRef extends AstRef implements TypeMirror {

    public static final TypeRef Any = new TypeRef("Any", null, TypeKind.NONE) {

        @Override
        public String getQualifiedName() {
            return "scala.Any";
        }
    };
    public static final TypeRef AnyRef = new TypeRef("AnyRef", null, TypeKind.NONE) {

        @Override
        public String getQualifiedName() {
            return "scala.AnyRef";
        }
    };
    public static final TypeRef AnyVal = new TypeRef("AnyVal", null, TypeKind.NONE) {

        @Override
        public String getQualifiedName() {
            return "scala.AnyVal";
        }
    };
    public static final TypeRef Double = new TypeRef("Double", null, TypeKind.DOUBLE) {

        @Override
        public String getQualifiedName() {
            return "scala.Double";
        }
    };
    public static final TypeRef Float = new TypeRef("Float", null, TypeKind.FLOAT) {

        @Override
        public String getQualifiedName() {
            return "scala.Float";
        }
    };
    public static final TypeRef Long = new TypeRef("Long", null, TypeKind.LONG) {

        @Override
        public String getQualifiedName() {
            return "scala.Long";
        }
    };
    public static final TypeRef Int = new TypeRef("Int", null, TypeKind.INT) {

        @Override
        public String getQualifiedName() {
            return "scala.Int";
        }
    };
    public static final TypeRef Short = new TypeRef("Short", null, TypeKind.SHORT) {

        @Override
        public String getQualifiedName() {
            return "scala.Short";
        }
    };
    public static final TypeRef Byte = new TypeRef("Byte", null, TypeKind.BYTE) {

        @Override
        public String getQualifiedName() {
            return "scala.Byte";
        }
    };
    public static final TypeRef Boolean = new TypeRef("Boolean", null, TypeKind.BOOLEAN) {

        @Override
        public String getQualifiedName() {
            return "scala.Boolean";
        }
    };
    public static final TypeRef Null = new TypeRef("Unit", null, TypeKind.NULL) {

        @Override
        public String getQualifiedName() {
            return "scala.Unit";
        }
    };
    public static final TypeRef Char = new TypeRef("Char", null, TypeKind.CHAR) {

        @Override
        public String getQualifiedName() {
            return "scala.Char";
        }
    };
    public static final TypeRef String = new TypeRef("String", null, TypeKind.DECLARED) {

        @Override
        public String getQualifiedName() {
            return "java.lang.String";
        }
    };
    public static final TypeRef Symbol = new TypeRef("Symbol", null, TypeKind.OTHER) {

        @Override
        public String getQualifiedName() {
            return "scala.AnyRef";
        }
    };
    public static Map<String, TypeRef> PRED_TYPES = new HashMap<String, TypeRef>();
    

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
    private static final String UNRESOLVED = "-1";
    private List<String> annotations;
    private List<TypeRef> typeArgs;
    private TypeKind kind;

    public TypeRef(CharSequence name, Token pickToken, TypeKind kind) {
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

    public void setTypeArgs(List<TypeRef> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public List<TypeRef> getTypeArgs() {
        return typeArgs == null ? Collections.<TypeRef>emptyList() : typeArgs;
    }

    public String getTypeArgsName() {
        if (!getTypeArgs().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Iterator<TypeRef> itr = getTypeArgs().iterator(); itr.hasNext();) {
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
        return !getQualifiedName().equals(UNRESOLVED);
    }

    @Override
    public String getQualifiedName() {
        if (qualifiedName != null) {
            return qualifiedName;
        }

        TypeRef predType = PRED_TYPES.get(getSimpleName().toString());
        if (predType != null) {
            qualifiedName = predType.getQualifiedName();
            return qualifiedName;
        }

        AstDef def = getEnclosingScope().findDef(this);
        if (def != null) {
            if (def instanceof TypeDef) {
                TypeRef value = ((TypeDef) def).getValue();
                if (value != null) {
                    qualifiedName = value.getQualifiedName();
                    return qualifiedName;
                } else {
                    return UNRESOLVED;
                }
            } else {
                // should be Template
                qualifiedName = def.getQualifiedName();
                return qualifiedName;
            }

        }

        List<Importing> importings = getEnclosingScope().getDefsInScope(Importing.class);
        for (Importing importing : importings) {
            for (TypeRef importedType : importing.getImportedTypes()) {
                if (importedType.getSimpleName().equals(getSimpleName())) {
                    qualifiedName = importing.getPackageName() + "." + importedType.getSimpleName();
                    return qualifiedName;
                }
            }
        }

        return UNRESOLVED;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public TypeRef getType() {
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
                for (Iterator<TypeRef> itr = typeArgs.iterator(); itr.hasNext();) {
                    itr.next().htmlFormat(formatter);
                    if (itr.hasNext()) {
                        formatter.appendText(", ");
                    }
                }
            }
            formatter.appendText("]");
        }
    }

    /**
     * Used to ref remote type, which has qualifiedName field only
     * 
     */
    public static class PseudoTypeRef extends TypeRef {

        public PseudoTypeRef() {
            super(null, null, TypeKind.DECLARED);
            setEnclosingScope(AstScope.emptyScope());
        }

        public PseudoTypeRef(String qualifiedName) {
            this();
            setQualifiedName(qualifiedName);
        }

        @Override
        public Name getSimpleName() {
            if (isResolved()) {
                String qName = getQualifiedName();
                int lastDot = qName.lastIndexOf('.');
                if (lastDot > 0) {
                    String sName = qName.substring(lastDot + 1, qName.length());
                    setSimpleName(sName);
                }
            }

            return super.getSimpleName();
        }

        @Override
        public String getQualifiedName() {
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
