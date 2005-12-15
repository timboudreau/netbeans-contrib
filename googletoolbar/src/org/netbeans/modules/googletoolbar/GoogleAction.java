/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.googletoolbar;

import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;




/** Google search action in the netbeans toolbar
*
* @author   Ludovic Champenois
*/
public final class GoogleAction extends CallableSystemAction {

    /*
     * this panel is what is displayed in the NetBeans toolbar
     *
     */
    GooglePanel retValue=  new GooglePanel();
    
    public void performAction() {
        // Nothing to do, the perform is done insie the GooglePanel
	
    }

    public String getName() {
        return "Google Toolbar";
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/googletoolbar/Servers.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    /* this is the way you can fine tune what is displayed in the NetBeans toolbar
     *
     **/
    public java.awt.Component getToolbarPresenter() {        
        return retValue;
    }

}
