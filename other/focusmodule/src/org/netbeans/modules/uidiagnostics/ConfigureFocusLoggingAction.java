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
import org.openide.util.actions.CallableSystemAction;
import javax.swing.Action;
import java.lang.ref.WeakReference;
import java.awt.event.*;
import org.openide.*;

/** Action to bring up the configuration dialog for focus logging
 *
 * @author Tim Boudreau
 */
public class ConfigureFocusLoggingAction extends CallableSystemAction {
    private transient WeakReference ref = null;
    public void performAction() {
       final FilterConfigPanel fcp = getPanel();
       DialogDescriptor dd = new DialogDescriptor (fcp, NbBundle.getMessage (ConfigureFocusLoggingAction.class, "DLG_Config"), true, new ActionListener () {
           public void actionPerformed (ActionEvent ae) {
               if (ae.getActionCommand().equals ("OK")) { //NOI18N
                   ToggleFocusLoggingAction.getFocus().setFilters (fcp.getFilters());
               }
           }
       });
       java.awt.Dialog d = DialogDisplayer.getDefault().createDialog(dd);
//       d.setSize (700,500);
       d.pack();
       d.show();
    }
    
    public String getName() {
        return NbBundle.getMessage(ConfigureFocusLoggingAction.class, "LBL_ConfigAction");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/uidiagnostics/ConfigureFocusLoggingActionIcon.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
     protected void initialize() {
        super.initialize();
        putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ConfigureFocusLoggingAction.class, "HINT_ConfigAction"));
     }

     private static EventFilter[] filters;
     public static EventFilter[] getFilters () {
         if (filters == null) filters = new EventFilter[] { new EventFilter() };
         return filters;
     }
     
     private FilterConfigPanel getPanel () {
         FilterConfigPanel result;
         if (ref == null) {
             result = new FilterConfigPanel();
             result.setFilters (getFilters());
             ref = new WeakReference (result);
         } else {
             result = (FilterConfigPanel) ref.get();
             if (result == null) {
                 result = new FilterConfigPanel();
                 result.setFilters (getFilters());
                 ref = new WeakReference (result);
             }
         }
         return result;
     }
    
}
