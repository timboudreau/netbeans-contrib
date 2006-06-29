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
package org.netbeans.modules.uidiagnostics;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import javax.swing.Action;
import java.lang.ref.WeakReference;


/** Action to toggle focus logging
 *
 * @author Tim Boudreau
 */
public class ToggleFocusLoggingAction extends BooleanStateAction {
    transient static Focus instance = null;
    public void performAction() {
//        getFocus().toggleState();
        setBooleanState (!(getBooleanState()));
    }

    public String getName() {
        return NbBundle.getMessage(ToggleFocusLoggingAction.class, "LBL_ToggleAction");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/uidiagnostics/ToggleFocusLoggingActionIcon.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
     protected void initialize() {
        super.initialize();
        putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ConfigureFocusLoggingAction.class, "HINT_ToggleAction"));
     }
     
     static Focus getFocus () {
         if (instance == null) instance = new Focus();
         return instance;
     }
     
     public boolean getBooleanState () {
         if (instance == null) return false;
         return instance.isListening();
     }
     
     public void setBooleanState (boolean b) {
         if (b) {
             getFocus().startListening();
         } else {
             getFocus().stopListening();
         }
     }
    
}
