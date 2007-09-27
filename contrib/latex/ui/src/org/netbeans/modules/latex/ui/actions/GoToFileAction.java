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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui.actions;

import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.latex.ui.GoToFile;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/** Action that can always be invoked and work procedurally.
 *
 * @author Jan Lahoda
 */
public class GoToFileAction extends CallableSystemAction {
    
    public void performAction() {
        final GoToFile panel = new GoToFile();
        final DialogDescriptor dd = new DialogDescriptor(panel, "Go To File");
        
        dd.setValid(false);
        
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        
        panel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                dd.setValid(panel.getSelectedFile() != null);
            }
        });
        
        dialog.show();
        
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            try {
                DataObject od = DataObject.find(panel.getSelectedFile());
                
                ((OpenCookie) od.getCookie(OpenCookie.class)).open();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(GoToFileAction.class, "LBL_GoToFileAction");
//        return NbBundle.getBundle("org/netbeans/modules/latex/ui/actions/Bundle").getString("LBL_GoToFileAction");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/latex/ui/resources/GoToFileActionIcon.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(GoToFileAction.class);
    }
    
    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
     * protected void initialize() {
     * super.initialize();
     * putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(GoToFileAction.class, "HINT_Action"));
     * }
     */
    
}
