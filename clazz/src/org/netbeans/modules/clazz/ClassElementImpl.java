/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.netbeans.modules.classfile.*;
import org.openide.filesystems.FileObject;
import org.openide.src.*;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.*;

/** The implementation of the class element for
* class objects. Presents data about the class -
* uses java reflection to obtain needed information.
*
* @author Dafe Simonek
*/
public final class ClassElementImpl extends MemberElementImpl implements ClassElement.Impl {
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
    
    /** One JavaDoc empty implementation for all objects */
    private static final ClassJavaDocImpl.Class CLASS_JAVADOC_IMPL = new ClassJavaDocImpl.Class ();

    static final long serialVersionUID =-8717988834353784544L;
    /** Default constructor.
    */
    public ClassElementImpl (final ClassFile data) {
        super(data);
    }

    private ClassElementImpl (final ClassFile data,InnerClass inner) {
        this(data);
        innerclass=inner;
    }
    
    /** Not supported. Throws source exception */
    public void setSuperclass(Identifier superClass) throws SourceException {
        throwReadOnlyException();
    }

    public int getModifiers () {
        int access;
        
        if (innerclass!=null)
            access=innerclass.getAccess();
        else
            access=((ClassFile)data).getAccess();
        return access & (~Access.INTERFACE & ~Access.SYNCHRONIZED);
    }
        
    public Identifier getSuperclass() {
        if (superClass == null) {
            ClassName cn = ((ClassFile)data).getSuperClass();
            String sc;
            
            if (cn  == null)
                sc = "";
            else
                sc = cn.getExternalName();
            superClass = Identifier.create(sc); // NOI18N
        }
        return superClass;
    }
    
    protected Identifier createName(Object data) {
        String simpleName;
        String outerName;
        int len;
        
        if (innerclass!=null) {
            simpleName=innerclass.getSimpleName();
            outerName=((ClassElement)element).getDeclaringClass().getName().getFullName();
            len=outerName.length();
        } else {
            ClassName cname=((ClassFile)data).getName();
            String iname=cname.getInternalName();
            
            outerName=cname.getPackage();
            len=outerName.length();
            if (len==0)
                simpleName=iname;
            else
                simpleName=iname.substring(len+1);
        }
	return len==0 ? 
	    Identifier.create(simpleName) :
	    Identifier.create(outerName+"."+simpleName, simpleName);
    }

    /** Not supported. Throws Source Exception */
    public void setClassOrInterface(boolean isClass) throws SourceException {
        throwReadOnlyException();
    }

    public boolean isClassOrInterface() {
        return (((ClassFile)data).getAccess() & Access.INTERFACE) == 0;        
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
            // create identifier array for interfaces
            Collection reflIntfs = ((ClassFile)data).getInterfaces();            
            interfaces = new Identifier[reflIntfs.size()];
            Iterator it = reflIntfs.iterator();
            for (int i = 0; it.hasNext(); i++) {
                ClassName cn = (ClassName)it.next();
                interfaces[i] = Identifier.create(cn.getExternalName());
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
        // obtain field array
        org.netbeans.modules.classfile.Field[] reflFields = null;
        try {
            reflFields = (org.netbeans.modules.classfile.Field[])((ClassFile)data).getVariables().toArray(new org.netbeans.modules.classfile.Field[0]);
        } catch (Throwable exc) {
            // rethrow only ThreadDeath, ignore otherwise
            if (exc instanceof ThreadDeath)
                throw (ThreadDeath)exc;
            reflFields = new org.netbeans.modules.classfile.Field[0];
        }
        // create map
        FieldElement curFE = null;
        Map result = new HashMap(reflFields.length);
        for (int i = 0; i < reflFields.length; i++) {
            // filter out methods added by compiler
            if (!addedByCompiler(reflFields[i])) {
                curFE = new FieldElement(new FieldElementImpl(reflFields[i]),
                                         (ClassElement)element);
                result.put(curFE.getName(), curFE);
            }
        }
        return result;
    }

    /** Creates map for inner classes of this class,
    * consisting of identifier - class element entries */
    private Map createInnersMap () {
        // obtain array of interfaces and inner classes
        InnerClass[] reflInners = null;
        String name = null;
        try {
            reflInners = (InnerClass[])((ClassFile)data).getInnerClasses().toArray(new InnerClass[0]);
            name = ((ClassFile)data).getName().getSimpleName();
        } catch (Throwable exc) {
            // rethrow only ThreadDeath, ignore otherwise
            if (exc instanceof ThreadDeath)
                throw (ThreadDeath)exc;
            reflInners = new InnerClass[0];
        }
        // create map
        ClassElement curCE = null;
        Map result = new HashMap(reflInners.length);
        for (int i = 0; i < reflInners.length; i++) {
            InnerClass reflInner=reflInners[i];
            String iname = reflInner.getSimpleName();
            if (iname != null) {
                StringBuffer sb = new StringBuffer(name.length() + iname.length() + 7);
                sb.append(name); sb.append('$'); sb.append(iname);
                try {
                    java.io.InputStream istm = findSourceImpl().findStreamForClass(sb.toString());
                    if (istm != null) {
                            curCE = new ClassElement(new ClassElementImpl(new ClassFile(istm),reflInner),
                                                     (ClassElement)element);
                            result.put(iname, curCE);
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault().
                        annotate(ex, ErrorManager.INFORMATIONAL, "Invalid class file", null, // NOI18N
                        null, null);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                    ErrorManager.getDefault().
                        annotate(ex, ErrorManager.INFORMATIONAL, "Invalid class file", null, // NOI18N
                        null, null);
                }
            }
        }
        return result;
    }

    SourceElementImpl findSourceImpl() {
        return (SourceElementImpl)((ClassElement)element).getSource().getCookie(SourceElement.Impl.class);
    }

    /** Creates map for constructors of this class,
    * consisting of constructor key - constructor element entries */
    private Map createConstructorsMap () {
        // obtain constructors array
        org.netbeans.modules.classfile.Method[] reflCons = null;
        try {
            reflCons = (org.netbeans.modules.classfile.Method[])((ClassFile)data).getMethods().toArray(new org.netbeans.modules.classfile.Method[0]);
        } catch (Throwable exc) {
            // rethrow only ThreadDeath, ignore otherwise
            if (exc instanceof ThreadDeath)
                throw (ThreadDeath)exc;
            reflCons = new org.netbeans.modules.classfile.Method[0];
        }
        // create map
        ConstructorElement curCE = null;
        Map result = new HashMap(reflCons.length);
        for (int i = 0; i < reflCons.length; i++) {
            if( isConstructor(reflCons[i]) ){
                curCE = new ConstructorElement(new ConstructorElementImpl(reflCons[i]),
                                               (ClassElement)element);
                result.put(new ConstructorElement.Key(curCE), curCE);
            }
        }
        return result;
    }

    /** Creates map for methods of this class,
    * consisting of method key - method element entries */
    private Map createMethodsMap () {
        // obtain methods array
        org.netbeans.modules.classfile.Method[] reflMethods = null;
        try {
            reflMethods = (org.netbeans.modules.classfile.Method[])((ClassFile)data).getMethods().toArray(new org.netbeans.modules.classfile.Method[0]);
        } catch (Throwable exc) {
            // rethrow only ThreadDeath, ignore otherwise
            if (exc instanceof ThreadDeath)
                throw (ThreadDeath)exc;
            reflMethods = new org.netbeans.modules.classfile.Method[0];
        }
        // create map
        MethodElement curME = null;
        Map result = new HashMap(reflMethods.length);
        for (int i = 0; i < reflMethods.length; i++) {
            // filter out methods added by compiler
            org.netbeans.modules.classfile.Method m = reflMethods[i];
            if (!addedByCompiler(m) && !isStaticInicializer(m) && !isConstructor(m)) {
                curME = new MethodElement(new MethodElementImpl(reflMethods[i]),
                                          (ClassElement)element);
                result.put(new MethodElement.Key(curME, true), curME);
            }
        }
        return result;
    }

    public Object readResolve() {
        return new ClassElement(this, (SourceElement)null);
    }

    /** @return true if given member was generated automatically by compiler,
    * false otherwise. Decision is made by inspecting the name of the member.
    */
    private static boolean addedByCompiler(Field field) {
        String name = field.getName();
        return name.indexOf('$') >= 0;
    }
    
    /** Determines whether the class is an anonymous one.
     */
    private static boolean isAnonymousClass (String name) {        
        int last = name.lastIndexOf('$');
        return !(last == -1 || name.length() == last + 1 || Character.isJavaIdentifierStart(name.charAt(last + 1)));
    }

    /** Determines whether the method is static inicializer.
     */
    private static boolean isStaticInicializer(org.netbeans.modules.classfile.Method method) {                
        return ( method.getName().indexOf("<clinit>") != -1 ); // NOI18N
    }
    
    /** Determines whether the method is constructor.
     */
    private static boolean isConstructor(org.netbeans.modules.classfile.Method method) {                
        return ( method.getName().indexOf("<init>") != -1 ); // NOI18N
    }    
}
