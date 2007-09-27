
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.ui.framework;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassesManager;
import com.sun.tthub.gde.logic.GDEController;
import com.sun.tthub.gde.logic.GDEInitParamKeys;
import com.sun.tthub.gde.ui.MainWizardUI;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class GDEWizardMenuAction extends CallableSystemAction {
    
    public void performAction() {
        GDEController controller = new GDEController();
        HashMap map = new HashMap();
        try {
            controller.initializeAppContext(map);
            // Create and initialize the GUI
            GDEWizardMainDlg dlg = new GDEWizardMainDlg();        
            MainWizardUI ui = new MainWizardUI(dlg);            
            GDEAppContext.getInstance().setWizardUI(ui);        

            dlg.setSize(709, 756);
            dlg.setLocationRelativeTo(null);                
            dlg.setVisible(true);
            dlg.toFront();            
        } catch(Exception ex) {
           ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Exception occured  " +
                    "while loading. Failed to initialize " +
                    "the context", "Context Initialization Error", 
                    JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public String getName() {
        return NbBundle.getMessage(GDEWizardMenuAction.class, "CTL_GDEWizardMenuAction");
    }
    
    protected String iconResource() {
        return "com/sun/tthub/gde/ui/wizard.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
