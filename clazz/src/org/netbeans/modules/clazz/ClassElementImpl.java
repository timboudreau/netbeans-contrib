/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;

import com.netbeans.ide.src.*;

/** The implementation of the class element for
* class objects. Presents data about the class -
* uses java reflection to obtain needed information.
*
* @author Dafe Simonek
*/
final class ClassElementImpl extends MemberElementImpl
                             implements ClassElement.Impl {
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

  /** Default constructor.
  */
  public ClassElementImpl (final Class data) {
    super(data);
  }

  /** Not supported. Throws source exception */
  public void setSuperclass(Identifier superClass) throws SourceException {
    throw new SourceException();
  }

  public Identifier getSuperclass() {
    if (superClass == null) {
      Class sc = ((Class)data).getSuperclass();
      superClass = Identifier.create(sc == null ? "" : sc.getName());
    }
    return superClass;
  }

  /** Not supported. Throws Unsupported exception */
  public void setClassOrInterface(boolean isClass) {
    throw new UnsupportedOperationException();
  }

  public boolean isClassOrInterface() {
    return !((Class)data).isInterface();
  }

  /** Not supported. Throws SourceException.
  */
  public void changeInitializers (InitializerElement[] elems, int action) throws SourceException {
    throw new SourceException();
  }

  /** Not supported. Throws Unsupported exception */
  public InitializerElement[] getInitializers () {
    throw new UnsupportedOperationException();
  }

  /** Changes set of elements.
  * @param elems elements to change
  * @exception SourceException if the action cannot be handled
  */
  public void changeFields (FieldElement[] elems, int action) throws SourceException {
    throw new SourceException();
  }

  public FieldElement[] getFields () {
    Map fieldsMap = (Map)fields.get();
    if (fieldsMap == null) {
      // soft ref null, we must recreate
      fieldsMap = createFieldsMap();
      fields = new SoftReference(fieldsMap);
    }
    return (FieldElement[])fieldsMap.values().toArray();
  }

  /** Finds a field with given name.
  * @param name the name of field to look for
  * @return the element or null if field with such name does not exist
  */
  public FieldElement getField (Identifier name) {
    Map fieldsMap = (Map)fields.get();
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
    throw new SourceException();
  }

  public MethodElement[] getMethods () {
    Map methodsMap = (Map)methods.get();
    if (methodsMap == null) {
      // soft ref null, we must recreate
      methodsMap = createMethodsMap();
      methods = new SoftReference(methodsMap);
    }
    return (MethodElement[])methodsMap.values().toArray();
  }

  /** Finds a method with given name and argument types.
  * @param name the name of field to look for
  * @param arguments for the method
  * @return the element or null if such method does not exist
  */
  public MethodElement getMethod (Identifier name, Type[] arguments) {
    Map methodsMap = (Map)methods.get();
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
    throw new SourceException();
  }

  public ConstructorElement[] getConstructors () {
    Map constructorsMap = (Map)constructors.get();
    if (constructorsMap == null) {
      // soft ref null, we must recreate
      constructorsMap = createConstructorsMap();
      constructors = new SoftReference(constructorsMap);
    }
    return (ConstructorElement[])constructorsMap.values().toArray();
  }

  /** Finds a constructor with argument types.
  * @param arguments for the method
  * @return the element or null if such method does not exist
  */
  public ConstructorElement getConstructor (Type[] arguments) {
    Map constructorsMap = (Map)constructors.get();
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
    throw new SourceException();
  }

  public ClassElement[] getClasses () {
    Map innersMap = (Map)inners.get();
    if (innersMap == null) {
      // soft ref null, we must recreate
      innersMap = createInnersMap();
      inners = new SoftReference(innersMap);
    }
    return (ClassElement[])innersMap.values().toArray();
  }

  /** Finds an inner class with given name.
  * @param name the name to look for
  * @return the element or null if such class does not exist
  */
  public ClassElement getClass (Identifier name) {
    Map innersMap = (Map)inners.get();
    if (innersMap == null) {
      // soft ref null, we must recreate
      innersMap = createInnersMap();
      inners = new SoftReference(innersMap);
    }
    return (ClassElement)innersMap.get(name);
  }

  /** Changes interfaces this class implements (or extends).
  * @param ids identifiers to change
  * @exception SourceException if the action cannot be handled
  */
  public void changeInterfaces (Identifier[] ids, int action) throws SourceException {
    throw new SourceException();
  }

  /** @return all interfaces which the class implements or interface extends.
  */
  public Identifier[] getInterfaces () {
    if (interfaces == null) {
      // create identifier array for interfaces
      Class[] reflIntfs = ((Class)data).getInterfaces();
      interfaces = new Identifier[reflIntfs.length];
      for (int i = 0; i < reflIntfs.length; i++) {
        interfaces[i] = Identifier.create(reflIntfs[i].getName());
      }
    }
    return interfaces;
  }

  /** @return class documentation.
  */
  public JavaDoc.Class getJavaDoc() {
    throw new UnsupportedOperationException();
  }

  /******** non public methods ***********/

  /** Creates map for fields consisting of identifier - field entries */
  private Map createFieldsMap () {
    // obtain field array
    Field[] reflFields = ((Class)data).getDeclaredFields();
    // create map
    FieldElement curFE = null;
    Map result = new HashMap();
    for (int i = 0; i < reflFields.length; i++) {
      curFE = new FieldElement(new FieldElementImpl(reflFields[i]),
                               (ClassElement)element);
      result.put(curFE.getName(), curFE);
    }
    return result;
  }

  /** Creates map for inner classes of this class,
  * consisting of identifier - class element entries */
  private Map createInnersMap () {
    // obtain array of interfaces and inner classes
    Class[] reflInners = ((Class)data).getClasses();
    // create map
    ClassElement curCE = null;
    Map result = new HashMap();
    for (int i = 0; i < reflInners.length; i++) {
      if (reflInners[i].isInterface()) continue;
      curCE = new ClassElement(new ClassElementImpl(reflInners[i]),
                               (ClassElement)element);
      result.put(curCE.getName(), curCE);
    }
    return result;
  }

  /** Creates map for constructors of this class,
  * consisting of constructor key - constructor element entries */
  private Map createConstructorsMap () {
    // obtain constructors array
    Constructor[] reflCons = ((Class)data).getDeclaredConstructors();
    // create map
    ConstructorElement curCE = null;
    Map result = new HashMap();
    for (int i = 0; i < reflCons.length; i++) {
      curCE = new ConstructorElement(new ConstructorElementImpl(reflCons[i]),
                               (ClassElement)element);
      result.put(new ConstructorElement.Key(curCE), curCE);
    }
    return result;
  }

  /** Creates map for methods of this class,
  * consisting of method key - method element entries */
  private Map createMethodsMap () {
    // obtain constructors array
    Method[] reflMethods = ((Class)data).getDeclaredMethods();
    // create map
    MethodElement curME = null;
    Map result = new HashMap();
    for (int i = 0; i < reflMethods.length; i++) {
      curME = new MethodElement(new MethodElementImpl(reflMethods[i]),
                                (ClassElement)element);
      result.put(new MethodElement.Key(curME), curME);
    }
    return result;
  }


}

/*
* Log
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
