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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
