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

import com.netbeans.ide.util.HelpCtx;
import com.netbeans.ide.util.NbBundle;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.nodes.Node;

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
    return "/com/netbeans/ide/resources/actions/empty.gif"; // no icon
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
      unc.GenerateImpl();
    }
  }
}

/*
 * <<Log>>
 *  1    Gandalf   1.0         5/22/99  Karel Gardas    
 * $
 */
