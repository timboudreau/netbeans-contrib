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

package org.netbeans.modules.clazz;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;
import java.io.IOException;

import org.openide.TopManager;
import org.openide.util.datatransfer.ExTransferable;

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
    "/org/netbeans/modules/clazz/resources/ser"; // NOI18N
  private static final String SER_MAIN_BASE =
    "/org/netbeans/modules/clazz/resources/serMain"; // NOI18N
  private static final String SER_ERROR_BASE =
    "/org/netbeans/modules/clazz/resources/serError"; // NOI18N

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
      if (dataObj.isExecutable()) {
        setIconBase(SER_MAIN_BASE);
      } else {
        setIconBase(SER_BASE);
      }
    } catch (IOException ex) {
      TopManager.getDefault ().notifyException (ex);
      setIconBase(SER_ERROR_BASE);
    } catch (ClassNotFoundException ex) {
      TopManager.getDefault ().notifyException (ex);
      setIconBase(SER_ERROR_BASE);
    }
    iconResolved = true;
  }

}

/*
 * Log
 *  10   src-jtulach1.9         1/13/00  David Simonek   i18n
 *  9    src-jtulach1.8         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  8    src-jtulach1.7         6/11/99  Jaroslav Tulach System.out commented
 *  7    src-jtulach1.6         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  6    src-jtulach1.5         3/22/99  Ian Formanek    Icons location fixed
 *  5    src-jtulach1.4         3/22/99  Ian Formanek    Icons moved from 
 *       modules/resources to this package
 *  4    src-jtulach1.3         2/25/99  Jaroslav Tulach Change of clipboard 
 *       management  
 *  3    src-jtulach1.2         1/20/99  David Simonek   icon managing repaired
 *  2    src-jtulach1.1         1/19/99  David Simonek   
 *  1    src-jtulach1.0         1/15/99  David Simonek   
 * $
 */
