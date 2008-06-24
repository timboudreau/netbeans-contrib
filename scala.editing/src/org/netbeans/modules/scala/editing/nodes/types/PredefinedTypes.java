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

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import org.netbeans.modules.scala.editing.nodes.BasicName;
import org.netbeans.modules.scala.editing.nodes.BasicType;
import org.netbeans.modules.scala.editing.nodes.BasicTypeElement;

/**
 *
 * @author dcaoyuan
 */
public class PredefinedTypes {
    // ----- Prededined Name
    protected static final Name SCALA_ANY = new BasicName("scala.Any");
    protected static final Name SCALA_ANYREF = new BasicName("scala.AnyRef");
    protected static final Name SCALA_ANYVAL = new BasicName("scala.AnyVal");
    protected static final Name SCALA_DOUBLE = new BasicName("scala.Double");
    protected static final Name SCALA_FLOAT = new BasicName("scala.Float");
    protected static final Name SCALA_LONG = new BasicName("scala.Long");
    protected static final Name SCALA_INT = new BasicName("scala.Int");
    protected static final Name SCALA_SHORT = new BasicName("scala.Short");
    protected static final Name SCALA_BYTE = new BasicName("scala.Byte");
    protected static final Name SCALA_BOOLEAN = new BasicName("scala.Boolean");
    protected static final Name SCALA_UNIT = new BasicName("scala.Unit");
    protected static final Name SCALA_CHAR = new BasicName("scala.Char");
    protected static final Name JAVA_LANG_STRING = new BasicName("java.lang.String");
    protected static final Name SCALA_SYMBOL = new BasicName("scala.AnyRef");
    // ----- Predefined TypeElement
    public static final TypeElement Any = new BasicTypeElement("Any", SCALA_ANY);
    public static final TypeElement AnyRef = new BasicTypeElement("AnyRef", SCALA_ANYREF);
    public static final TypeElement AnyVal = new BasicTypeElement("AnyVal", SCALA_ANYVAL);
    public static final TypeElement Double = new BasicTypeElement("Double", SCALA_DOUBLE);
    public static final TypeElement Float = new BasicTypeElement("Float", SCALA_FLOAT);
    public static final TypeElement Long = new BasicTypeElement("Long", SCALA_LONG);
    public static final TypeElement Int = new BasicTypeElement("Int", SCALA_INT);
    public static final TypeElement Short = new BasicTypeElement("Short", SCALA_SHORT);
    public static final TypeElement Byte = new BasicTypeElement("Byte", SCALA_BYTE);
    public static final TypeElement Boolean = new BasicTypeElement("Boolean", SCALA_BOOLEAN);
    public static final TypeElement Null = new BasicTypeElement("Unit", SCALA_UNIT);
    public static final TypeElement Char = new BasicTypeElement("Char", SCALA_CHAR);
    public static final TypeElement String = new BasicTypeElement("String", JAVA_LANG_STRING);
    public static final TypeElement Symbol = new BasicTypeElement("Symbol", SCALA_ANYREF);
    // ----- Predefined TypeMirror
    public static final BasicType AnyType = new BasicType(Any, TypeKind.NONE);
    public static final BasicType AnyRefType = new BasicType(AnyRef, TypeKind.NONE);
    public static final BasicType AnyValType = new BasicType(AnyVal, TypeKind.NONE);
    public static final BasicType DoubleType = new BasicType(Double, TypeKind.DOUBLE);
    public static final BasicType FloatType = new BasicType(Float, TypeKind.FLOAT);
    public static final BasicType LongType = new BasicType(Long, TypeKind.LONG);
    public static final BasicType IntType = new BasicType(Int, TypeKind.INT);
    public static final BasicType ShortType = new BasicType(Short, TypeKind.SHORT);
    public static final BasicType ByteType = new BasicType(Byte, TypeKind.BYTE);
    public static final BasicType BooleanType = new BasicType(Boolean, TypeKind.BOOLEAN);
    public static final BasicType NullType = new BasicType(Null, TypeKind.NULL);
    public static final BasicType CharType = new BasicType(Char, TypeKind.CHAR);
    public static final BasicType StringType = new BasicType(String, TypeKind.DECLARED);
    public static final BasicType SymbolType = new BasicType(Symbol, TypeKind.OTHER);
    public static Map<String, BasicType> PRED_TYPES = new HashMap<String, BasicType>();


    static {
        PRED_TYPES.put("Any", AnyType);
        PRED_TYPES.put("AnyRef", AnyRefType);
        PRED_TYPES.put("AnyVal", AnyValType);
        PRED_TYPES.put("Double", DoubleType);
        PRED_TYPES.put("double", DoubleType);
        PRED_TYPES.put("Float", FloatType);
        PRED_TYPES.put("float", FloatType);
        PRED_TYPES.put("Long", LongType);
        PRED_TYPES.put("long", LongType);
        PRED_TYPES.put("Int", IntType);
        PRED_TYPES.put("int", IntType);
        PRED_TYPES.put("Short", ShortType);
        PRED_TYPES.put("short", ShortType);
        PRED_TYPES.put("Byte", ByteType);
        PRED_TYPES.put("byte", ByteType);
        PRED_TYPES.put("Boolean", BooleanType);
        PRED_TYPES.put("boolean", BooleanType);
        PRED_TYPES.put("Unit", NullType);
        PRED_TYPES.put("unit", NullType);
        PRED_TYPES.put("Char", CharType);
        PRED_TYPES.put("char", CharType);
        PRED_TYPES.put("String", StringType);
    }

}
