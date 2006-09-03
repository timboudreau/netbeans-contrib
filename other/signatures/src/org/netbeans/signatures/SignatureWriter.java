/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.signatures;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Writes out usages of a type signature.
 * @author Jesse Glick
 */
public final class SignatureWriter {
    
    private final PrintWriter w;
    private final String prefix;
    private final Elements elements;
    private final Types types;
    
    public SignatureWriter(PrintWriter w, String prefix, Elements elements, Types types) {
        this.w = w;
        this.prefix = prefix;
        this.elements = elements;
        this.types = types;
    }
    
    private void emit(String text) {
        w.println(prefix + "{" + text + "}");
    }

    public void process(String clazz) {
        TypeElement type = elements.getTypeElement(clazz);
        if (type == null) {
            // Cannot find any such.
            return;
        }
        if (!type.getModifiers().contains(Modifier.PUBLIC)) {
            // Package-private class.
            return;
        }
        Name name = type.getQualifiedName();
        if (name.toString().length() == 0) {
            // Anonymous class.
            return;
        }
        emit("Class _ = " + name + ".class;");
        switch (type.getKind()) {
            case CLASS:
                if (!type.getModifiers().contains(Modifier.ABSTRACT)) {
                    processPublicConstructors(type);
                }
                // XXX
                break;
            case INTERFACE:
                // XXX
                break;
            case ENUM:
                // XXX
                break;
            case ANNOTATION_TYPE:
                // XXX
                break;
            default:
                assert false : type.getKind();
        }
        return;
    }
    
    private String name(TypeMirror type) {
        return type.toString().replaceAll("\\bjava\\.lang\\.([A-Z])", "$1");
    }
    
    private TypeMirror instantiateTypeParametersWithUpperBound(TypeMirror type) {
        switch (type.getKind()) {
            case DECLARED:
                DeclaredType dtype = (DeclaredType) type;
                List<TypeMirror> params = new ArrayList<TypeMirror>();
                for (TypeMirror arg : dtype.getTypeArguments()) {
                    params.add(instantiateTypeParametersWithUpperBound(arg));
                }
                return types.getDeclaredType((TypeElement) types.asElement(dtype), params.toArray(new TypeMirror[params.size()]));
            case ARRAY:
                return types.getArrayType(instantiateTypeParametersWithUpperBound(((ArrayType) type).getComponentType()));
            case TYPEVAR:
                return ((TypeVariable) type).getUpperBound();
            default:
                return type;
        }
    }
    
    private TypeMirror instantiateTypeParametersWithUpperBound(TypeElement type) {
        List<TypeMirror> params = new ArrayList<TypeMirror>();
        for (TypeParameterElement p : type.getTypeParameters()) {
            List<? extends TypeMirror> bounds = p.getBounds();
            if (bounds.isEmpty()) {
                params.add(elements.getTypeElement("java.lang.Object").asType());
            } else {
                params.add(bounds.get(0)); // XXX OK?
            }
        }
        return types.getDeclaredType(type, params.toArray(new TypeMirror[params.size()]));
    }
    
    private Iterable<TypeMirror> supertypes(TypeElement type) {
        Set<TypeMirror> supertypes = new LinkedHashSet<TypeMirror>();
        collectSupertypes(instantiateTypeParametersWithUpperBound(type), supertypes);
        return supertypes;
    }
    private void collectSupertypes(TypeMirror type, Set<TypeMirror> supertypes) {
        for (TypeMirror t : types.directSupertypes(type)) {
            collectSupertypes(t, supertypes);
        }
        supertypes.add(type);
    }
    
    private void processPublicConstructors(final TypeElement type) {
        boolean firstConstructor = true;
        for (Element e : type.getEnclosedElements()) {
            if (e.getKind() != ElementKind.CONSTRUCTOR) {
                continue;
            }
            if (!e.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            String params = parameters((ExecutableElement) e);
            if (params == null) {
                // Some parameter type is inaccessible, so we cannot call it.
                // XXX this might not be strictly true in case that parameter type has a public subtype
                continue;
            }
            String fqn = name(instantiateTypeParametersWithUpperBound(type));
            List<TypeMirror> checkedExceptions = new ArrayList<TypeMirror>();
            for (TypeMirror exc : ((ExecutableElement) e).getThrownTypes()) {
                if (types.isSubtype(exc, elements.getTypeElement("java.lang.Exception").asType()) &&
                        !types.isSubtype(exc, elements.getTypeElement("java.lang.RuntimeException").asType())) {
                    checkedExceptions.add(exc);
                }
            }
            String tryprefix;
            String catchsuffix;
            if (checkedExceptions.isEmpty()) {
                tryprefix = "";
                catchsuffix = "";
            } else {
                tryprefix = "try {";
                StringBuilder b = new StringBuilder();
                b.append("}");
                for (TypeMirror exc : checkedExceptions) {
                    b.append(" catch (");
                    b.append(name(exc));
                    b.append(" _) {}");
                }
                catchsuffix = b.toString();
            }
            if (firstConstructor) {
                for (TypeMirror t : supertypes(type)) {
                    if (accessible(t)) {
                        emit(tryprefix + name(t) + " _ = new " + fqn + "(" + params + ");" + catchsuffix);
                    }
                }
                firstConstructor = false;
            } else {
                // For other constructors, skip type checks.
                // But still need to translate element type to an instantiation with type parameters.
                emit(tryprefix + fqn + " _ = new " + fqn + "(" + params + ");" + catchsuffix);
            }
        }
    }
    
    private boolean accessible(TypeMirror type) {
        switch (type.getKind()) {
            case ERROR:
                return false;
            case DECLARED:
                return ((DeclaredType) type).asElement().getModifiers().contains(Modifier.PUBLIC);
            case WILDCARD:
                TypeMirror bound = ((WildcardType) type).getExtendsBound();
                return bound == null || accessible(bound);
            case ARRAY:
                return accessible(((ArrayType) type).getComponentType());
            case TYPEVAR:
                return accessible(((TypeVariable) type).getUpperBound());
            default:
                return true;
        }
    }

    private String parameters(ExecutableElement e) {
        StringBuilder b = new StringBuilder();
        // e.getParameters() does not work for classes read from bytecode
        for (TypeMirror type : ((ExecutableType) e.asType()).getParameterTypes()) {
            if (b.length() > 0) {
                b.append(", ");
            }
            if (!accessible(type)) {
                return null;
            }
            TypeMirror type2 = instantiateTypeParametersWithUpperBound(type);
            switch (type2.getKind()) {
                case BOOLEAN:
                    b.append("false");
                    break;
                case BYTE:
                    b.append("(byte) 0");
                    break;
                case CHAR:
                    b.append("' '");
                    break;
                case SHORT:
                    b.append("(short) 0");
                    break;
                case INT:
                    b.append("0");
                    break;
                case LONG:
                    b.append("0L");
                    break;
                case FLOAT:
                    b.append("0.0f");
                    break;
                case DOUBLE:
                    b.append("0.0d");
                    break;
                case TYPEVAR:
                    // XXX what if it is an intersection type, e.g. Number & Runnable?
                    // fallthru
                case DECLARED:
                case ARRAY:
                    String n = name(type2);
                    if (n.equals("String")) {
                        b.append("\"\"");
                    } else {
                        b.append("(");
                        b.append(n);
                        b.append(") null");
                    }
                    break;
                default:
                    assert false : type2;
            }
        }
        return b.toString();
    }
    
}
