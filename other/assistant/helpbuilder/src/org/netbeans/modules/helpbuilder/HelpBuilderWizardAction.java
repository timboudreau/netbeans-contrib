/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 *
 */

package org.netbeans.modules.helpbuilder;

import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

 /**
 *
 * @author  Richard Gregor
 */
public class HelpBuilderWizardAction extends CallableSystemAction {
    public static ErrorManager err;

    static{
        Integer eger = new Integer(ErrorManager.UNKNOWN);
        System.setProperty("HelpBuilderWizardAction",eger.toString());
        err = ErrorManager.getDefault().getInstance("HelpBuilderWizardAction");
    }

    public void performAction () {
        
        Integer eger = new Integer(ErrorManager.WARNING);
        err.log(eger.toString());
    
        WizardDescriptor desc = new HelpBuilderWizardDescriptor();
        final Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.show ();
        
        if (desc.getValue () == WizardDescriptor.FINISH_OPTION) {
            //System.out.println ("User finished the wizard"); // NOI18N
        } else {
            //System.out.println ("User cancelled the wizard"); // NOI18N
        }
        
        
    }

    public String getName () {
        return NbBundle.getMessage(HelpBuilderWizardAction.class, "LBL_Action");
    }

    protected String iconResource () {
        return "org/netbeans/modules/helpbuilder/images/HelpBuilderWizardActionIcon.gif";
    }

    protected boolean asynchronous(){
        return false;
    }
    
    public HelpCtx getHelpCtx () {  
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (HelpBuilderWizardAction.class);
    }

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
    protected void initialize () {
	super.initialize ();
     * putProperty (Action.SHORT_DESCRIPTION, NbBundle.getMessage (HelpBuilderWizardAction.class, "HINT_Action"));
    }
    */

}
