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
package org.netbeans.modules.scala.editing.nodes;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.scala.editing.nodes.AstNode.AstName;

/**
 *
 * @author Caoyuan Deng
 */
public class BasicTypeElement implements TypeElement {
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
    private Name simpleName;
    private Name qualifiedName;

    public BasicTypeElement(String sName, Name qualifiedName) {
        this.simpleName = new AstName(sName);
        this.qualifiedName = qualifiedName;
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name getQualifiedName() {
        return qualifiedName;
    }

    public TypeMirror getSuperclass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends TypeMirror> getInterfaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends TypeParameterElement> getTypeParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeMirror asType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ElementKind getKind() {
        return ElementKind.CLASS;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <A extends Annotation> A getAnnotation(Class<A> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Modifier> getModifiers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public Element getEnclosingElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends Element> getEnclosedElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <R, P> R accept(ElementVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
