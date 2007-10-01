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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.clazz;

import org.netbeans.modules.classfile.ClassFile;
import org.netbeans.modules.classfile.InnerClass;
import org.openide.src.*;
import java.lang.ref.SoftReference;
import java.util.*;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.util.RequestProcessor;

/** The implementation of the class element for
* class objects. Presents data about the class -
* uses java reflection to obtain needed information.
*
* @author Dafe Simonek
*/
public final class ClassElementImpl extends MemberElementImpl implements ClassElement.Impl, ElementProperties {
    /** Empty array of initializers - constant to return from getInitializers() */
    private static final InitializerElement[] EMPTY_INITIALIZERS =
        new InitializerElement[0];
    private static final Object[] EMPTY_ARRAY = new Object[0];

    /** Identifier of superclass of this class element,
    * can be empty */
    private Identifier superClass;
    /** Array of identifiers for interfaces */
    private Identifier[] interfaces;

    /** Fields of this class element */
    private SoftReference fields;
    /** Inner classes of this class element */
    private SoftReference inners;
    /** Contructors of this class element */
    private SoftReference constructors;
    /** Methods of this class element */
    private SoftReference methods;
    
    /* InnerClass attribute of a associeted with this classfile */ 
    private InnerClass innerclass;
    
    private boolean isClassInited = false;
    private boolean isClass;
    
    /** One JavaDoc empty implementation for all objects */
    private static final ClassJavaDocImpl.Class CLASS_JAVADOC_IMPL = new ClassJavaDocImpl.Class ();
    
    private static final Identifier NO_SUPERCLASS = Identifier.create(""); // NOI18N

    static final long serialVersionUID =-8717988834353784544L;
    /** Default constructor.
    */
    public ClassElementImpl (final ClassFile data) {
        super(data);
    }
    
    ClassElementImpl(JavaClass data) {
        super(data);
    }
    
    private ClassElementImpl (final ClassFile data,InnerClass inner) {
        this(data);
        innerclass=inner;
    }
    
    public void initializeData() {
        super.initializeData();
        getSuperclass();
        isClassOrInterface();
        getInterfaces();
        ClassElement[] classes = getClasses();
        for (int x = 0; x < classes.length; x++) {
            ClassElementImpl impl = (ClassElementImpl) classes[x].getCookie(org.openide.src.Element.Impl.class);
            impl.initializeData();
        }
        FieldElement[] fields = getFields();
        for (int x = 0; x < fields.length; x++) {
            FieldElementImpl impl = (FieldElementImpl) fields[x].getCookie(org.openide.src.Element.Impl.class);
            impl.initializeData();
        }
        MethodElement[] methods = getMethods();
        for (int x = 0; x < methods.length; x++) {
            MethodElementImpl impl = (MethodElementImpl) methods[x].getCookie(org.openide.src.Element.Impl.class);
            impl.initializeData();
        }
        ConstructorElement[] cons = getConstructors();
        for (int x = 0; x < cons.length; x++) {
            ConstructorElementImpl impl = (ConstructorElementImpl) cons[x].getCookie(org.openide.src.Element.Impl.class);
            impl.initializeData();
        }
    }
    
    /** Not supported. Throws source exception */
    public void setSuperclass(Identifier superClass) throws SourceException {
        throwReadOnlyException();
    }
    
    JavaClass getClazz() {
        return (JavaClass)data;
    }

    public Identifier getSuperclass() {
        if (superClass == NO_SUPERCLASS)
            return null;
        if (superClass != null)
            return superClass;
        
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Identifier.create(""); // NOI18N
            }
            if (superClass == null) {
                JavaClass c = getClazz();
                JavaClass tid = c.getSuperClass();
                if (tid == null)
                    superClass = NO_SUPERCLASS;
                else
                    superClass = Identifier.create(Util.createClassName(tid.getName()));
            }
            if (superClass == NO_SUPERCLASS)
                return null;
            else
                return superClass;
        } finally {
            repo.endTrans();
        }
    }
    
    protected Identifier createName(Object data) {
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Identifier.create(""); // NOI18N
            }
            String fqn=getClazz().getName();
            String simpleName;
            int lastDot=fqn.lastIndexOf('.');

            if (lastDot!=-1)
                simpleName=fqn.substring(lastDot+1);
            else
                simpleName=fqn;
            return Identifier.create(fqn, simpleName);
        } finally {
            repo.endTrans();
        }
    }

    /** Not supported. Throws Source Exception */
    public void setClassOrInterface(boolean isClass) throws SourceException {
        throwReadOnlyException();
    }

    public boolean isClassOrInterface() {
        if (isClassInited)
            return isClass;
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return true;
            }
            isClassInited = true;
            return isClass = !getClazz().isInterface();
        } finally {
            repo.endTrans();
        }
    }

    /** Not supported. Throws SourceException.
    */
    public void changeInitializers (InitializerElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    /** Not available. Always returns empty array */
    public InitializerElement[] getInitializers () {
        return EMPTY_INITIALIZERS;
    }

    /** Changes set of elements.
    * @param elems elements to change
    * @exception SourceException if the action cannot be handled
    */
    public void changeFields (FieldElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    public FieldElement[] getFields () {
        Map fieldsMap = (fields == null) ? null : (Map)fields.get();
        if (fieldsMap == null) {
            // soft ref null, we must recreate
            fieldsMap = createFieldsMap();
            fields = new SoftReference(fieldsMap);
        }
        return (FieldElement[])fieldsMap.values().toArray(new FieldElement[0]);
    }

    /** Finds a field with given name.
    * @param name the name of field to look for
    * @return the element or null if field with such name does not exist
    */
    public FieldElement getField (Identifier name) {
        Map fieldsMap = (fields == null) ? null : (Map)fields.get();
        if (fieldsMap == null) {
            // soft ref null, we must recreate
            fieldsMap = createFieldsMap();
            fields = new SoftReference(fieldsMap);
        }
        return (FieldElement)fieldsMap.get(name);
    }

    /** Changes set of elements.
    * @param elems elements to change
    * @exception SourceException if the action cannot be handled
    */
    public void changeMethods (MethodElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    public MethodElement[] getMethods () {
        Map methodsMap = (methods == null) ? null : (Map)methods.get();
        if (methodsMap == null) {
            // soft ref null, we must recreate
            methodsMap = createMethodsMap();
            methods = new SoftReference(methodsMap);
        }
        return (MethodElement[])methodsMap.values().toArray(new MethodElement[0]);
    }

    /** Finds a method with given name and argument types.
    * @param name the name of field to look for
    * @param arguments for the method
    * @return the element or null if such method does not exist
    */
    public MethodElement getMethod (Identifier name, Type[] arguments) {
        Map methodsMap = (methods == null) ? null : (Map)methods.get();
        if (methodsMap == null) {
            // soft ref null, we must recreate
            methodsMap = createMethodsMap();
            methods = new SoftReference(methodsMap);
        }
        return (MethodElement)
               methodsMap.get(new MethodElement.Key(name, arguments));
    }

    /** Changes set of elements.
    * @param elems elements to change
    * @exception SourceException if the action cannot be handled
    */
    public void changeConstructors (ConstructorElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    public ConstructorElement[] getConstructors () {
        Map constructorsMap =
            (constructors == null) ? null :(Map)constructors.get();
        if (constructorsMap == null) {
            // soft ref null, we must recreate
            constructorsMap = createConstructorsMap();
            constructors = new SoftReference(constructorsMap);
        }
        return (ConstructorElement[])constructorsMap.values().
               toArray(new ConstructorElement[0]);
    }

    /** Finds a constructor with argument types.
    * @param arguments for the method
    * @return the element or null if such method does not exist
    */
    public ConstructorElement getConstructor (Type[] arguments) {
        Map constructorsMap =
            (constructors == null) ? null :(Map)constructors.get();
        if (constructorsMap == null) {
            // soft ref null, we must recreate
            constructorsMap = createConstructorsMap();
            constructors = new SoftReference(constructorsMap);
        }
        return (ConstructorElement)
               constructorsMap.get(new ConstructorElement.Key(arguments));
    }

    /** Changes set of elements.
    * @param elems elements to change
    * @exception SourceException if the action cannot be handled
    */
    public void changeClasses (ClassElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    public ClassElement[] getClasses () {
        Map innersMap = (inners == null) ? null : (Map)inners.get();
        if (innersMap == null) {
            // soft ref null, we must recreate
            innersMap = createInnersMap();
            inners = new SoftReference(innersMap);
        }
        return (ClassElement[])innersMap.values().toArray(new ClassElement[0]);
    }

    /** Finds an inner class with given name.
    * @param name the name to look for
    * @return the element or null if such class does not exist
    */
    public ClassElement getClass (Identifier n) {
        Map innersMap = (inners == null) ? null : (Map)inners.get();
        if (innersMap == null) {
            // soft ref null, we must recreate
            innersMap = createInnersMap();
            inners = new SoftReference(innersMap);
        }
        String key = n.getSourceName();
        ClassElement el = (ClassElement)innersMap.get(key);
        if (el != null) {
            if (!key.equals(n.getFullName()) &&
                !n.getFullName().equals(el.getName().getFullName()))
            el = null;
        }
        return el;
    }

    /** Changes interfaces this class implements (or extends).
    * @param ids identifiers to change
    * @exception SourceException if the action cannot be handled
    */
    public void changeInterfaces (Identifier[] ids, int action) throws SourceException {
        throwReadOnlyException();
    }

    /** @return all interfaces which the class implements or interface extends.
    */
    public Identifier[] getInterfaces () {
        if (interfaces == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    interfaces = new Identifier[0];
                } else {
                    JavaClass[] reflIntfs = (JavaClass[])getClazz().getInterfaces().toArray(new JavaClass[0]);
                    interfaces = new Identifier[reflIntfs.length];
                    for (int i = 0; i < reflIntfs.length; i++) {
                        interfaces[i] = Identifier.create(Util.createClassName(reflIntfs[i].getName()));
                    }
                }
            } finally {
                repo.endTrans();
            }
        }
        return interfaces;
    }

    /** @return class documentation.
    */
    public JavaDoc.Class getJavaDoc() {
        return CLASS_JAVADOC_IMPL;
    }

    /******** non public methods ***********/

    /** Creates map for fields consisting of identifier - field entries */
    private Map createFieldsMap () {
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Collections.EMPTY_MAP;
            }
            Feature[] reflFields = (Feature[])getClazz().getFeatures().toArray(new Feature[0]);
            Map result = new HashMap(reflFields.length);
            for (int i = 0; i < reflFields.length; i++) {
                Feature f=reflFields[i];

                if (f instanceof Field) {
                    Field field=(Field)f;
                    // filter out methods added by compiler
                    if (!addedByCompiler(field)) {
                        FieldElement curFE = new FieldElement(new FieldElementImpl(field),
                                                 (ClassElement)element);
                        result.put(curFE.getName(), curFE);
                    }
                }
            }
            return result;
        } finally {
            repo.endTrans();
        }
    }

    /** Creates map for inner classes of this class,
    * consisting of identifier - class element entries */
    private Map createInnersMap () {
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Collections.EMPTY_MAP;
            }
            Feature[] reflInners = (Feature[])getClazz().getFeatures().toArray(new Feature[0]);

            // create map
            ClassElement curCE = null;
            Map result = new HashMap(reflInners.length);
            for (int i = 0; i < reflInners.length; i++) {
                Feature f=reflInners[i];

                if (f instanceof JavaClass) {
                    JavaClass jcls=(JavaClass)f;
                    String iname = jcls.getName();
                    curCE = new ClassElement(new ClassElementImpl(jcls),
                                             (ClassElement)element);
                    result.put(iname, curCE);
                }
            }
            return result;
        } finally {
            repo.endTrans();
        }
    }

    SourceElementImpl findSourceImpl() {
        return (SourceElementImpl)((ClassElement)element).getSource().getCookie(SourceElement.Impl.class);
    }

    /** Creates map for constructors of this class,
    * consisting of constructor key - constructor element entries */
    private Map createConstructorsMap () {
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Collections.EMPTY_MAP;
            }
            Feature[] reflCons = (Feature[])getClazz().getFeatures().toArray(new Feature[0]);
            Map result = new HashMap(reflCons.length);

            for (int i = 0; i < reflCons.length; i++) {
                Feature f=reflCons[i];

                if (f instanceof Constructor) {
                    ConstructorElement curCE = new ConstructorElement(new ConstructorElementImpl((Constructor)f),
                                                   (ClassElement)element);
                    result.put(new ConstructorElement.Key(curCE), curCE);
                }
            }
            return result;
        } finally {
            repo.endTrans();
        }
    }

    /** Creates map for methods of this class,
    * consisting of method key - method element entries */
    private Map createMethodsMap () {
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Collections.EMPTY_MAP;
            }
            Feature[] reflMethods = (Feature[])getClazz().getFeatures().toArray(new Feature[0]);
            Map result = new HashMap(reflMethods.length);

            for (int i = 0; i < reflMethods.length; i++) {
                Feature f=reflMethods[i];

                if (f instanceof Method) {                
                    // filter out methods added by compiler
                    Method m = (Method)f;
                    if (!addedByCompiler(m)) {
                        MethodElement curME = new MethodElement(new MethodElementImpl(m),
                                                  (ClassElement)element);
                        result.put(new MethodElement.Key(curME, true), curME);
                    }
                }
            }
            return result;
        } finally {
            repo.endTrans();
        }
    }

    public Object readResolve() {
        return new ClassElement(this, (SourceElement)null);
    }

    public void refreshData() {
        // System.out.println("refreshing class ...");
        fields = null;
        inners = null;
        constructors = null;
        methods = null;
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                firePropertyChange(PROP_MEMBERS, null, null);
                firePropertyChange(PROP_METHODS, null, null);
                firePropertyChange(PROP_FIELDS, null, null);
                firePropertyChange(PROP_CONSTRUCTORS, null, null);
                firePropertyChange(PROP_CLASSES, null, null);
            }
        });
    }
    
    /** @return true if given member was generated automatically by compiler,
    * false otherwise. Decision is made by inspecting the name of the member.
    */
    private static boolean addedByCompiler(NamedElement field) {
        String name = field.getName();
        return name.indexOf('$') >= 0;
    }   
}
