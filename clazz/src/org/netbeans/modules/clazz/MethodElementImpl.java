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

import java.lang.reflect.Method;
import java.lang.reflect.Member;

import org.openide.src.*;

/** Implementation of method element for class objects.
*
* @author Dafe Simonek
*/
final class MethodElementImpl extends ConstructorElementImpl
                              implements MethodElement.Impl {
  /** Return type of the method */
  private Type returnType;

  /** Default constructor, asociates with given
  * java reflection Method element.
  */
  public MethodElementImpl(final Method data) {
    super(data);
  }

  /** @return returns teh Type representing return type of this method.
  */
  public Type getReturn () {
    if (returnType == null)
      returnType = Type.createFromClass(((Method)data).getReturnType());
    return returnType;
  }

  /** Unsupported. Throws an Source exception. */
  public void setReturn (Type ret) throws SourceException {
    throw new SourceException();
  }

  public Object readResolve() {
    return new MethodElement(this, null);
  }

}

/*
* Log
*  3    src-jtulach1.2         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  2    src-jtulach1.1         2/17/99  Petr Hamernik   serialization changed.
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
