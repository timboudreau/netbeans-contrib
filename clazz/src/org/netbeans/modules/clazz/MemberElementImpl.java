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

import org.openide.src.MemberElement;
import org.openide.src.SourceException;
import org.openide.src.Identifier;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.util.Utilities;
import org.openide.nodes.Node;

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

static final long serialVersionUID =-6841890195552268874L;
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
      // and moreover we must throw away interface modifier if present
      try {
        return ((Class)data).getModifiers() & (~Modifier.INTERFACE);
      } catch (Exception exc) {
        return 0;
      }
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
      String fullName = (data instanceof Class) ?
        Utilities.getClassName((Class)data) :
        ((Member)data).getName();
      
      int lastDot = fullName.lastIndexOf("."); // NOI18N
      name = (lastDot == -1) ?
        Identifier.create(fullName) :
        Identifier.create(fullName, fullName.substring(lastDot + 1));
    }
    return name;
  }

  /** Unsupported. Throws SourceException.
  */
  public void setName (Identifier name) throws SourceException {
    throw new SourceException();
  }
  
  /** Delegates to source element implementation class,
  * if it's possible.
  */
  public Node.Cookie getCookie (Class type) {
    ClassElement ce = ((MemberElement)element).getDeclaringClass();
    if ((ce == null) && (element instanceof ClassElement)) {
      ce = (ClassElement)element;  
    }
    if (ce != null) {
      SourceElement se = ce.getSource();
      if (se != null) {
        return se.getCookie(type);
      }
    }
    return null;
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
*  13   src-jtulach1.12        1/20/00  David Simonek   #2119 bugfix
*  12   src-jtulach1.11        1/13/00  David Simonek   i18n
*  11   src-jtulach1.10        1/13/00  David Simonek   i18n
*  10   src-jtulach1.9         1/10/00  Petr Hamernik   Identifier creating 
*       improved.
*  9    src-jtulach1.8         1/5/00   David Simonek   #2564
*  8    src-jtulach1.7         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  7    src-jtulach1.6         8/9/99   Ian Formanek    Generated Serial Version 
*       UID
*  6    src-jtulach1.5         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  5    src-jtulach1.4         5/12/99  Petr Hamernik   ide.src.Identifier 
*       updated
*  4    src-jtulach1.3         3/26/99  David Simonek   properties, actions 
*       completed, more robust now
*  3    src-jtulach1.2         2/17/99  Petr Hamernik   serialization changed.
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
