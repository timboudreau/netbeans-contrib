/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
