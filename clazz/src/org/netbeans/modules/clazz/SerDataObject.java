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

import org.openide.filesystems.*;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/** DataObject which represents JavaBeans (".ser" files).
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class SerDataObject extends ClassDataObject {
  /** generated Serialized Version UID */
  static final long serialVersionUID = 8229229209013849842L;

  /** Constructs a new BeanDataObject */
  public SerDataObject(FileObject fo, ClassDataLoader loader) throws DataObjectExistsException {
    super (fo, loader);
  }

  /** Getter for move action.
  * @return true if the object can be moved
  */
  public boolean isMoveAllowed () {
    return !getPrimaryFile ().isReadOnly ();
  }

  /** Getter for rename action.
  * @return true if the object can be renamed
  */
  public boolean isRenameAllowed () {
    return !getPrimaryFile ().isReadOnly ();
  }

  /** Creates another delegate.
  */
  protected Node createNodeDelegate () {
    return new SerDataNode (this);
  }

  public HelpCtx getHelpCtx () {
    HelpCtx test = InstanceSupport.findHelp (instanceSupport);
    if (test != null)
      return test;
    else
      return new HelpCtx (SerDataObject.class);
  }

}

/*
 * Log
 *  6    src-jtulach1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    src-jtulach1.4         6/25/99  Jesse Glick     Instance context help.
 *  4    src-jtulach1.3         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  3    src-jtulach1.2         2/25/99  Jaroslav Tulach Change of clipboard 
 *       management  
 *  2    src-jtulach1.1         2/1/99   David Simonek   
 *  1    src-jtulach1.0         1/15/99  David Simonek   
 * $
 * Beta Change History:
 *  0    Tuborg    0.20        --/--/98 Jan Formanek    SWITCHED TO NODES
 *  0    Tuborg    0.21        --/--/98 Jan Formanek    icons tweak
 */
