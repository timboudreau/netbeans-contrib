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

package org.netbeans.modules.j2ee.blueprints;

import java.util.Iterator;
import java.util.Set;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;

/**
 * Show the BluePrints screen.
 * @author  Ludo
 */
public class ShowBluePrintsAction extends CallableSystemAction {
    
    public ShowBluePrintsAction () {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    public void performAction() {        
        BluePrintsComponent topComp = null;
        Set/*<TopComponent>*/ tcs = TopComponent.getRegistry().getOpened();
        Iterator it = tcs.iterator();
        while (it.hasNext()) {
            TopComponent tc = (TopComponent)it.next();
            if (tc instanceof BluePrintsComponent) {                
                topComp = (BluePrintsComponent) tc;               
                break;
            }
        }
        if(topComp == null){            
            topComp = BluePrintsComponent.findComp();
        }
       
        topComp.open();
        topComp.requestActive();
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowBluePrintsAction.class, "LBL_Action");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous(){
        return false;
    }

}
