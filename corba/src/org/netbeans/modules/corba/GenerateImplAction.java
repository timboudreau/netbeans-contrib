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

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.nodes.Node;

/** Actions for IDL Node.
*
* @author Karel Gardas
* @version 0.01, May 21, 1999
*/

public class GenerateImplAction extends CookieAction {

  /** @return set of needed cookies */
  protected Class[] cookieClasses () {
    return new Class[] { IDLNodeCookie.class };
  }

  /** @return false */
  protected boolean surviveFocusChange () {
    return false;
  }

  /** @return exactly one */
  protected int mode () {
    return MODE_EXACTLY_ONE;
  }

  /** Human presentable name of the action. This should be
  * presented as an item in a menu.
  * @return the name of the action
  */
  public String getName() {
    return NbBundle.getBundle (CORBASupport.class).getString ("CTL_GenerateImpl");
  }

  /** Help context where to find more about the action.
  * @return the help context for this action
  */
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP; // [PENDING]
  }

  /** Resource name for the icon.
  * @return resource name
  */
  protected String iconResource () {
    return "/org/openide/resources/actions/empty.gif"; // no icon
  }

  /**
  * Standart perform action extended by actually activated nodes.
  * @see CallableSystemAction#performAction
  *
  * @param activatedNodes gives array of actually activated nodes.
  */
  protected void performAction (final Node[] activatedNodes) {
    IDLNodeCookie unc = (IDLNodeCookie)activatedNodes[0].getCookie(IDLNodeCookie.class);
    if (unc != null) {
      unc.GenerateImpl((IDLDataObject)activatedNodes[0].getCookie (IDLDataObject.class));
    }
  }
}

/*
 * <<Log>>
 *  8    Gandalf   1.7         10/1/99  Karel Gardas    updates from CVS
 *  7    Gandalf   1.6         8/3/99   Karel Gardas    
 *  6    Gandalf   1.5         7/10/99  Karel Gardas    
 *  5    Gandalf   1.4         6/9/99   Ian Formanek    Fixed resources for 
 *       package change
 *  4    Gandalf   1.3         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  3    Gandalf   1.2         5/28/99  Karel Gardas    
 *  2    Gandalf   1.1         5/28/99  Karel Gardas    
 *  1    Gandalf   1.0         5/22/99  Karel Gardas    
 * $
 */
