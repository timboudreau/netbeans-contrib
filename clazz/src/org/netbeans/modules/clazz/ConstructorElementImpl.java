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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.netbeans.ide.src.*;

/** Implementation of the constructor element for class objects.
* It's simple bridge to the java reflection Constructor, delegates
* all tasks to it.
*
* @author Dafe Simonek
*/
class ConstructorElementImpl extends MemberElementImpl
                             implements ConstructorElement.Impl {

  /** The array of parameters of this constructor (or method)*/
  private MethodParameter[] parameters;
  /** The array of exceptions which can be thrown */
  private Type[] exceptions;

  /** Default constructor, asocitates this object
  * with java reflection Constructor instance.
  */
  public ConstructorElementImpl (final Member data) {
    super(data);
  }

  /** @return the array specifying the parameters
  */
  public MethodParameter[] getParameters () {
    if (parameters == null) {
      // build method params
      Class[] reflPars = null;
      if (data instanceof Method)
        reflPars = ((Method)data).getParameterTypes();
      else
        reflPars = ((Constructor)data).getParameterTypes();
      parameters = new MethodParameter[reflPars.length];
      // helper variables
      Class curPar = null;
      Type curType = null;
      String curName = null;
      for (int i = 0; i < reflPars.length; i++) {
        curPar = reflPars[i];
        // create method parameter
        parameters[i] = new MethodParameter(
          curPar.getName(), Type.createFromClass(curPar),
          (curPar.getModifiers() & Modifier.FINAL) == 0
        );
      }
    }
    return parameters;
  }

  /** Unsupported, throws SourceException
  */
  public void setParameters (MethodParameter[] params) throws SourceException {
    throw new SourceException();
  }

  /** @return the array of the exceptions throwed by the method.
  */
  public Type[] getExceptions () {
    if (exceptions == null) {
      // obtain via reflection
      Class[] reflEx = ((Constructor)data).getExceptionTypes();
      exceptions = new Type[reflEx.length];
      // build our exception types
      for (int i = 0; i < reflEx.length; i++) {
        exceptions[i] = Type.createFromClass(reflEx[i]);
      }
    }
    return exceptions;
  }

  /** Unsupported, throws SourceException
  */
  public void setExceptions (Type[] exceptions) throws SourceException {
    throw new SourceException();
  }

  /** Unsupported, throws SourceException
  */
  public void setBody (String s) throws SourceException {
    throw new SourceException();
  }

  /** Unsupported, throws UnsupportedOperationException
  */
  public String getBody () {
    throw new UnsupportedOperationException();
  }

  /** PENDING - ???
  */
  public JavaDoc.Method getJavaDoc () {
    throw new UnsupportedOperationException();
  }

}

/*
* Log
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
