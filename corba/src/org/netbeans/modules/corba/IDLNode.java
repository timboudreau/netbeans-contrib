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


import org.openide.loaders.*;
import org.openide.actions.*;
import org.openide.util.actions.*;
import org.openide.nodes.*;
import org.openide.cookies.OpenCookie;
import org.openide.actions.OpenAction;

// my
import org.openide.filesystems.FileUtil;


import com.netbeans.enterprise.modules.corba.idl.node.*;
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

  //public static final boolean DEBUG = true;
  public static final boolean DEBUG = false;

  /** Icon base for the IDLNode node */
  public static final String IDL_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/settings/idl";
  public static final String IDL_ERROR_ICON =
    "com/netbeans/enterprise/modules/corba/settings/idl-error";

  IDLDocumentChildren children;
  IDLDataObject ido;

  /** Default constructor, constructs node */
  public IDLNode (DataObject dataObject) throws Exception {
    //super(dataObject, Children.LEAF);
    //try {
    super (dataObject, new IDLDocumentChildren ((IDLDataObject) dataObject));
    ido = (IDLDataObject)dataObject;
    setIconBase (IDL_ICON_BASE);
    children = (IDLDocumentChildren) getChildren ();
    children.setNode (this);
    //children.startParsing ();
    //} catch (ParseException e) {
    //setIconBase(IDL_ERROR_ICON);
    //}
    if (DEBUG)
      System.out.println ("IDLNode constructor!!!");
  }
  
  /** Overrides default action from DataNode.
   * Instantiate a template, if isTemplate() returns true.
   * Opens otherwise.
   */
  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  public boolean canRename () {
    return true;
  }

  protected IDLDataObject getIDLDataObject () {
    return (IDLDataObject) getDataObject ();
  }
   
  public void update () {
    children.setSrc (ido.getSources ());
    children.createKeys ();
  }

  // for better debuging
  /*
    public Node.Cookie getCookie (java.lang.Class clazz) {
    System.out.println ("IDLNode::getCookie (" + clazz + ");");
    return super.getCookie (clazz);
    }
  */
}


/*
 * <<Log>>
 *  14   Gandalf   1.13        11/4/99  Karel Gardas    update from CVS
 *  13   Gandalf   1.12        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  12   Gandalf   1.11        10/5/99  Karel Gardas    update from CVS
 *  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */



