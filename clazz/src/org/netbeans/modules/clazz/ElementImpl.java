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
import java.beans.PropertyChangeListener;

import com.netbeans.ide.src.Element;

/** Implementation of Element for classes.
*
* @author Dafe Simonek
*/
public class ElementImpl extends Object implements Element.Impl {

  /** The element we aare asociated to. We provide an implementation
  * to that element */
  protected Element element;

  /** Default constructor
  */
  public ElementImpl () {
  }

  /** Attaches this implementation to the element.
  *
  * @param element the element we are attached to
  */
  public void attachedToElement (Element element) {
    this.element = element;
  }

  /** We don't support property changes - does nothing */
  public void addPropertyChangeListener (PropertyChangeListener l) {
  }

  /** We don't support property changes - does nothing */
  public void removePropertyChangeListener (PropertyChangeListener l) {
  }

  public void writeExternal(ObjectOutput oi) throws IOException {
  }

  public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
  }

}

/*
* Log
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
