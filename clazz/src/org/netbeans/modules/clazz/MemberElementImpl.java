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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import com.netbeans.ide.src.MemberElement;
import com.netbeans.ide.src.SourceException;
import com.netbeans.ide.src.Identifier;
import com.netbeans.ide.util.Utilities;

/** Implementation of the MemberElement.Impl for the class objects.
*
* @author Dafe Simonek
*/
public abstract class MemberElementImpl extends ElementImpl
                               implements MemberElement.Impl {
  /** Asociated java reflection data */
  protected Object data;
  /** Cached name identifier */
  private transient Identifier name;

  /** Constructor, asociates this impl with java reflection
  * Member element, which acts as data source.
  */
  public MemberElementImpl (final Object data) {
    super();
    this.data = data;
  }

  /** @return Modifiers for this element.
  */
  public int getModifiers () {
    if (data instanceof Class) {
      // Class doesn't implement Member interface...
      // and moreover we must throw away "interface" modifier if present
      return ((Class)data).getModifiers() & (~Modifier.INTERFACE);
    }
    return ((Member)data).getModifiers();
  }

  /** Unsupported. Throws SourceException
  */
  public void setModifiers (int mod) throws SourceException {
    throw new SourceException();
  }

  /** Getter for name of the field.
  * @return the name
  */
  public Identifier getName () {
    if (name == null) {
      String s = (data instanceof Class) ?
        Utilities.getClassName((Class)data) :
        ((Member)data).getName();
      
      name = Identifier.create(s);
    }
    return name;
  }

  /** Unsupported. Throws SourceException.
  */
  public void setName (Identifier name) throws SourceException {
    throw new SourceException();
  }

  public void writeExternal (ObjectOutput oi) throws IOException {
    oi.writeObject(data);
  }

  public void readExternal (ObjectInput oi) throws IOException, ClassNotFoundException {
    data = oi.readObject();
  }
}

/*
* Log
*  5    src-jtulach1.4         5/12/99  Petr Hamernik   ide.src.Identifier 
*       updated
*  4    src-jtulach1.3         3/26/99  David Simonek   properties, actions 
*       completed, more robust now
*  3    src-jtulach1.2         2/17/99  Petr Hamernik   serialization changed.
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
