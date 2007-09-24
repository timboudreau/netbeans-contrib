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
/*
 * CustomizerAction.java
 *
 * Created on March 8, 2007, 10:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.saw.palette;

import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author root
 */
public class CustomizerAction extends CallableSystemAction{
    private static String name;
    
    /** Creates a new instance of TestDDPaletteCustomizerAction */
    public CustomizerAction() {
        putValue("noIconInMenu",Boolean.TRUE);
    }

    public boolean asynchronous(){
        return false;
    }
    
    public void performAction() {
        try{
            Factory.getPalette().showCustomizer();
        }catch(IOException ioe){
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ioe);
        }
    }

    public String getName() {
        if(name == null){
            name = NbBundle.getBundle(CustomizerAction.class).getString("ACT_OpenTestDDCustomizer");
        }
        return name;
    }

    public HelpCtx getHelpCtx() {
        return null;
    }
    
}
