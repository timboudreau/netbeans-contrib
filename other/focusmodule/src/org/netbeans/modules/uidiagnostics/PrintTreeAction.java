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
 * PrintTreeAction.java
 *
 * Created on February 20, 2003, 4:14 PM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.util.actions.*;
import org.openide.util.*;
import javax.swing.Action;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
/** Action to print out the component tree.
 *
 * @author  Tim Boudreau
 */
public class PrintTreeAction extends BooleanStateAction {

    /** Creates a new instance of PrintTreeAction */
    public PrintTreeAction() {
    }

    public void actionPerformed () {
       if (!getBooleanState()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message (
                NbBundle.getMessage (PrintTreeAction.class, "MSG_ClickToPrintTree"));
            DialogDisplayer.getDefault().notify (nd);
            setBooleanState (true);
        } else {
            getInstance().disarm();
            setBooleanState (false);
        }
    }
    
    public boolean getBooleanState () {
        return getInstance().armed;
    }
    
    public void setBooleanState (boolean val) {
        if (val) {
            getInstance().arm();
        } else {
            getInstance().disarm();
        }
        super.setBooleanState (val);
    }
    
    private Decipherer instance = null;
    
    private Decipherer getInstance () {
        if (instance == null) instance = new Decipherer();
        return instance;
    }
    
    public String getName() {
        return NbBundle.getMessage(ConfigureFocusLoggingAction.class, "LBL_PrintAction");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/uidiagnostics/PrintTreeActionIcon.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
     protected void initialize() {
        super.initialize();
        putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ConfigureFocusLoggingAction.class, "HINT_PrintAction"));
     }    
    
}
