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

import java.lang.reflect.Field;

import com.netbeans.ide.src.*;

/** The implementation of the field element for class objects.
*
* @author Dafe Simonek
*/
final class FieldElementImpl extends MemberElementImpl
                             implements FieldElement.Impl {
  /** Cached type of this field */
  private Type type;

  /** Default constructor. Asociates with given
  * reflection Field data.
  */
  public FieldElementImpl (final Field data) {
    super(data);
  }

  /** Type of the variable.
  * @return the type
  */
  public Type getType () {
    if (type == null)
      type = Type.createFromClass(((Field)data).getType());
    return type;
  }

  /** Not supported. Throws SourceException.
  */
  public void setType (Type type) throws SourceException {
    throw new SourceException();
  }

  /** PENDING - don't know how to implement...
  */
  public String getInitValue () {
    return "";
  }

  /** Not supported. Throws SourceException.
  */
  public void setInitValue (String value) throws SourceException {
    throw new SourceException();
  }

  /** @return java doc for the field
  */
  public JavaDoc.Field getJavaDoc () {
    throw new UnsupportedOperationException();
  }

  public Object readResolve() {
    return new FieldElement(this, null);
  }
  
}

/*
* Log
*  2    src-jtulach1.1         2/17/99  Petr Hamernik   serialization changed.
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
