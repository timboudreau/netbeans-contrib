/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
