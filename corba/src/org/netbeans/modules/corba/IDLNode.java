/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba;

import java.util.*;


import com.netbeans.ide.loaders.*;
import com.netbeans.ide.actions.*;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.nodes.*;
import com.netbeans.ide.cookies.OpenCookie;
import com.netbeans.ide.actions.OpenAction;

// my
import com.netbeans.ide.filesystems.FileUtil;

/**
*
*
* @author Karel Gardas
*/

 /** IDL Node implementation.
 * Leaf node, default action opens editor or instantiates template.
 * Icons redefined.
 */

public class IDLNode extends DataNode {
  /** Icon base for the IDLNode node */
  private static final String IDL_ICON_BASE =
  "com/netbeans/enterprise/modules/corba/settings/idl";
  
  /** Default constructor, constructs node */
  public IDLNode (DataObject dataObject) {
    super(dataObject, Children.LEAF);
    setIconBase(IDL_ICON_BASE);
  }
  
  /** Overrides default action from DataNode.
   * Instantiate a template, if isTemplate() returns true.
   * Opens otherwise.
   */
  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  protected IDLDataObject getIDLDataObject () {
    return (IDLDataObject) getDataObject ();
  }


}


/*
* <<Log>>
*  6    Gandalf   1.5         5/28/99  Karel Gardas    
*  5    Gandalf   1.4         5/22/99  Karel Gardas    
*  4    Gandalf   1.3         5/15/99  Karel Gardas    
*  3    Gandalf   1.2         5/8/99   Karel Gardas    
*  2    Gandalf   1.1         4/24/99  Karel Gardas    
*  1    Gandalf   1.0         4/23/99  Karel Gardas    
* $
*/
