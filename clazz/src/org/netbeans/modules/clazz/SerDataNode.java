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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;

import com.netbeans.ide.util.datatransfer.TransferableOwner;

/** Exetends ClassDataNode, overrides one method
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jaroslav Tulach, Dafe Simonek
*/
final class SerDataNode extends ClassDataNode {
  /** generated Serialized Version UID */
  static final long serialVersionUID = -2645179282674800246L;

  private static final String SER_BASE =
    "com/netbeans/developer/modules/resources/class/ser";
  private static final String SER_MAIN_BASE =
    "com/netbeans/developer/modules/resources/class/serMain";
  private static final String SER_ERROR_BASE =
    "com/netbeans/developer/modules/resources/class/serError";

  /** Constructs bean data node with asociated data object.
  */
  public SerDataNode(final SerDataObject obj) {
    super(obj);
  }

// ----------------------------------------------------------------------------------
// methods

  /** Returns initial icon base string for ser node.
  */
  protected String initialIconBase () {
    return SER_BASE;
  }

  protected void resolveIcons () {
    try {
      ClassDataObject dataObj = (ClassDataObject)getDataObject();
      dataObj.getBeanClass(); // check exception
      if (dataObj.getHasMainMethod()) {
        setIconBase(SER_MAIN_BASE);
      } else {
        setIconBase(SER_BASE);
      }
    } catch (ThreadDeath td) {
      throw td;
    } catch (Throwable t) {
      setIconBase(SER_ERROR_BASE);
    }
    iconResolved = true;
  }

}

/*
 * Log
 *  2    src-jtulach1.1         1/19/99  David Simonek   
 *  1    src-jtulach1.0         1/15/99  David Simonek   
 * $
 */
