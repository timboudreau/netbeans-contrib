/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Leon Chiver. All Rights Reserved.
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
 */

package org.netbeans.modules.codeinfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jmi.reflect.RefObject;
import org.netbeans.jmi.javamodel.Array;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Expression;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.jmi.javamodel.PrimitiveType;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.ThisExpression;
import org.netbeans.jmi.javamodel.Type;
import org.openide.ErrorManager;


/**
 * @author leon chiver
 */
final class DeclarationInfo {
    
    private static ErrorManager ERR = ErrorManager.getDefault().getInstance("org.netbeans.modules.java.hints"); // NOI18N
    
    private static Map primitiveTypeWeights;
    
    static {
        primitiveTypeWeights = new HashMap();
        primitiveTypeWeights.put("byte", new Integer(0));
        primitiveTypeWeights.put("char", new Integer(1));
        primitiveTypeWeights.put("short", new Integer(2));
        primitiveTypeWeights.put("int", new Integer(3));
        primitiveTypeWeights.put("long", new Integer(4));
        primitiveTypeWeights.put("float", new Integer(5));
        primitiveTypeWeights.put("double", new Integer(6));
    }

    // TODO - send a patch for JMIUtils to handle names of inner classes right, 
    // then use directly JMIUtils 
    public static String getSimpleName(Type type) {
        if (type instanceof JavaClass) {
            JavaClass clazz = (JavaClass) type;
            String name = clazz.getSimpleName();
            ClassDefinition declaring = clazz.getDeclaringClass();
            String declaringName = getSimpleName(declaring);
            if (declaringName != null) {
                return declaringName + "." + name;
            } else {
                return name;
            }
        } else if (type instanceof Array) {
            return getSimpleName(((Array) type).getType()) + "[]";
        } else if (type != null) {
            return type.getName();
        } else {
            return null;
        }
    }
    
    private static String getPackageName(ClassDefinition jcls) {
        return jcls.getResource().getPackageName();
    }
        
    private static boolean methodsEqual(Method m1, Method m2) {
        // Check names
        String name1 = m1.getName();
        String name2 = m2.getName();
        if (!name1.equals(name2)) {
            return false;
        }
        List params1 = m1.getParameters();
        List params2 = m2.getParameters();
        if (params1.size() != params2.size()) {
            return false;
        }
        int sz = params1.size();
        for (int i = 0; i < sz; i++) {
            Parameter p1 = (Parameter) params1.get(i);
            Parameter p2 = (Parameter) params2.get(i);
            if (!p1.getType().getName().equals(p2.getType().getName())) {
                return false;
            }
        }
        return true;
    }

    private static void collectMethods(ClassDefinition clazz, MethodMatcher matcher, 
            List/*<Method>*/ l, Set visited) {
        String n = clazz.getName();
        if (visited.contains(n)) {
            return;
        }
        visited.add(n);
        Feature[] f = (Feature[]) clazz.getFeatures().toArray(new Feature[0]);
        for (int i = 0; i < f.length; i++) {
            if (f[i] instanceof Method) {
                Method m = (Method) f[i];
                if (!containsMethod(l, m) && matcher.matches(m)) {
                    l.add(m);
                }
            }
        }
        // Get the methods of all interfaces
        List intfList = clazz.getInterfaces();
        JavaClass[] intf = (JavaClass[]) intfList.toArray(new JavaClass[0]);
        for (int i = 0; i < intf.length; i++) {
            collectMethods(intf[i], matcher, l, visited);
        }
        ClassDefinition supr = clazz.getSuperClass();
        if (supr != null) {
            collectMethods(supr, matcher, l, visited);
        }
    }

    public static boolean isAssignableFrom(Type t1, Type t2) {
        if (t1 instanceof PrimitiveType && t2 instanceof PrimitiveType) {
            String n1 = ((PrimitiveType) t1).getName();
            String n2 = ((PrimitiveType) t2).getName();
            if (n1.equals("void") || n2.equals("void")) {
                return false;
            }
            if (n1.equals(n2)) {
                return true;
            }
            if (n1.equals("boolean") || n2.equals("boolean")) {
                return false;
            }
            int n1w = ((Integer) primitiveTypeWeights.get(n1)).intValue();
            int n2w = ((Integer) primitiveTypeWeights.get(n2)).intValue();
            return n1w >= n2w;
        } else if (t1 instanceof ClassDefinition && t2 instanceof ClassDefinition) {
            ClassDefinition d1 = (ClassDefinition) t1;
            ClassDefinition d2 = (ClassDefinition) t2;
            return d1.getName().equals(d2.getName()) || d2.isSubTypeOf(d1);
        }
        return false;
    }
    
    private static boolean containsMethod(List l, Method m) {
        for (Iterator it = l.iterator(); it.hasNext();) {
            if (methodsEqual((Method) it.next(), m)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isFeatureAccessibleFrom(Feature f, ClassDefinition clazz, Expression featureUser) {
        int featureModif = f.getModifiers();
        // Public
        if ((Modifier.PUBLIC & featureModif) != 0) {
            return true;
        }
        // Private
        ClassDefinition featureClass = f.getDeclaringClass();
        if ((Modifier.PRIVATE & featureModif) != 0) {
            // Is the feature inside a inner class of the accessing class?
            return isSameOrInnerClassOf(f.getDeclaringClass(), clazz);
        }
        // Protected
        // Here we have to check the feature user. For protected access it has to be
        // this or null. For super it's also null
        if ((Modifier.PROTECTED & featureModif) != 0 && 
                (featureUser == null || featureUser instanceof ThisExpression)) {
            return getPackageName(featureClass).equals(getPackageName(clazz)) ||
                    clazz.isSubTypeOf(featureClass);
        }
        // Package access
        return getPackageName(featureClass).equals(getPackageName(clazz));
    }
    
    public static boolean isSameOrInnerClassOf(ClassDefinition inner, ClassDefinition clazz) {
        ClassDefinition current = inner;
        while (inner != null) {
            if (inner.getName().equals(clazz.getName())) {
                return true;
            }
            if (inner instanceof JavaClass) {
                inner = ((JavaClass) inner).getDeclaringClass();
            } else {
                inner = null;
            }
        }
        return false;
    }
    
    public static ClassDefinition getContainingClass(Element element) {
        Object o = element;
        while (o != null && o instanceof RefObject && !(o instanceof ClassDefinition)) {
            o = ((RefObject) o).refImmediateComposite();
        }
        return (o instanceof ClassDefinition) ? (ClassDefinition) o : null;
    }
    
    public static Method getMethodByNameAndParamTypeNames(ClassDefinition def, String name, String[] paramTypeNames) {
        Feature[] f = (Feature[]) def.getFeatures().toArray(new Feature[0]);
        for (int i = 0; i < f.length; i++) {
            if (f[i] instanceof Method) {
                Method m = (Method) f[i];
                if (!m.getName().equals(name)) {
                    continue;
                }
                List paramList = m.getParameters();
                if (paramList.size() != paramTypeNames.length) {
                    continue;
                }
                Parameter[] p = (Parameter[]) m.getParameters().toArray(new Parameter[0]);
                for (int j = 0; j < p.length; j++) {
                    if (!p[j].getType().getName().equals(paramTypeNames[j])) {
                        continue;
                    }
                }
                return m;
            }
        }
        return null;
    }
    
    public static List/*<Method>*/ getMethodsByNameAndParamCount(ClassDefinition def, String name, int paramCount) {
        List l = new ArrayList();
        Set visited = new HashSet();
        collectMethods(def, new MethodNameAndParamCountMatcher(name, paramCount), l, visited);
        return l;
    }

    public static List/*<Constructor>*/ getConstructorsByParamCount(ClassDefinition def, int paramCount) {
        Feature[] f = (Feature[]) def.getFeatures().toArray(new Feature[0]);
        List matches = new ArrayList();
        for (int i = 0; i < f.length; i++) {
            Feature feat = f[i];
            if (feat instanceof Constructor) {
                Constructor c = (Constructor) feat;
                if (c.getParameters().size() == paramCount) {
                    matches.add(c);
                }
            }
        }
        return matches;
    }
    
    public static CallableFeature findCallableFeatureAtPosition(Element currentElement, int position) {
        return (CallableFeature) findSubElementOfTypeAtPosition(currentElement, CallableFeature.class, position, false);
    }
    
    public static List/*<Method>*/ getAbstractMethods(ClassDefinition def) {
        Set visitedClasses = new HashSet();
        List abstractMethods = new ArrayList();
        List visitedMethods = new ArrayList();
        collectMethods(def, new AbstractMethodMatcher(visitedMethods), abstractMethods, visitedClasses);
        return abstractMethods;
    }

    public static Element findSubElementOfTypeAtPosition(Element currentElement, Class type, int position, boolean mostInner) {
        boolean cont = true;
        Element found = null;
        while (cont) {
            if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                ERR.log(ErrorManager.INFORMATIONAL, "currentElement=" + currentElement);
            }
            
            cont = false;
            
            for (Iterator i = currentElement.getChildren().iterator(); i.hasNext(); ) {
                Element el = (Element) i.next();
                
                if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                    ERR.log(ErrorManager.INFORMATIONAL, "child element=" + el);
                }

                if (el.getStartOffset() <= position && el.getEndOffset() >= position) {
                    if (type.isAssignableFrom(el.getClass())) {
                        if (mostInner) {
                            // Found a element, but do not stop here; check for nested elements if needed
                            found = el;
                            currentElement = found;
                            cont = true;
                        } else {
                            return el;
                        }
                    } else {
                        currentElement = el;
                        cont = true;
                        break;
                    }
                }
            }
        }
        return found;
    }
    
    private static interface MethodMatcher {
        boolean matches(Method m);
    }
    
    private static class MethodNameAndParamCountMatcher implements MethodMatcher {
        
        String name;
        
        int paramCount;
        
        MethodNameAndParamCountMatcher(String name, int paramCount) {
            this.name = name;
            this.paramCount = paramCount;
        }
        
        public boolean matches(Method m) {
            return m.getName().equals(name) && m.getParameters().size() == paramCount;
        }
    }
    
    private static class AbstractMethodMatcher implements MethodMatcher {
        
        List checkedMethods;
        
        AbstractMethodMatcher(List checkedMethods) {
            this.checkedMethods = checkedMethods;
        }
        
        public boolean matches(Method m) {
            boolean abstr = Modifier.isAbstract(m.getModifiers());
            if (abstr && containsMethod(checkedMethods, m)) {
                abstr = false;
            }
            checkedMethods.add(m);
            return abstr;
        }
    }
    
    
    private static class ReturnTypeMatcher implements MethodMatcher {
        
        Type returnType;

        ReturnTypeMatcher(Type returnType) {
            this.returnType = returnType;
        }
        
        public boolean matches(Method m) {
            Type actMethodType = m.getType();
            boolean matches = false;
            if (actMethodType instanceof PrimitiveType && returnType instanceof PrimitiveType) {
                String pActMethodType = ((PrimitiveType) actMethodType).getName();
                String pSearchedMethodType = ((PrimitiveType) returnType).getName();
                if (pSearchedMethodType.equals("boolean") && pActMethodType.equals("boolean")) {
                    return true;
                } else if (!pActMethodType.equals("void") && !pActMethodType.equals("boolean") &&
                        !pSearchedMethodType.equals("boolean")) {
                    int actWeight = ((Integer) primitiveTypeWeights.get(pActMethodType)).intValue();
                    int searchedWeight = ((Integer) primitiveTypeWeights.get(pSearchedMethodType)).intValue();
                    if (searchedWeight >= actWeight) {
                        return true;
                    }
                }
            } else if (actMethodType instanceof ClassDefinition && returnType instanceof ClassDefinition) {
                ClassDefinition cdActMethodType = (ClassDefinition) actMethodType;
                ClassDefinition cdSearchedMethodType = (ClassDefinition) returnType;
                if (isAssignableFrom(cdSearchedMethodType, cdActMethodType)) {
                    return true;
                }
            }
            return false;
        }
    }

}
