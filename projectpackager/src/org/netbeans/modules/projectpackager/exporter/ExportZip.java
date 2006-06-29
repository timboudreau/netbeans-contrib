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

package org.netbeans.modules.projectpackager.exporter;
import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Export zip - main class called by the action from layer.xml
 * @author Roman "Roumen" Strobl
 */
public class ExportZip extends CallableSystemAction {
    private static ExportZipDialog zpd;

    /**
     * Empty constructor
     */
    public ExportZip() {
    }
    
    /**
     * Returns action name
     * @return action name
     */
    public String getName() {
        return NbBundle.getBundle(Constants.BUNDLE).getString("NetBeans_Project(s)_as_Zip...");
    }
    
    /**
     * Returns help context
     * @return helpctx
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    /**
     * Performs the action
     */
    public void performAction() {        
        if (ExportPackageInfo.isProcessed()) {
            if (zpd.isShowing()) { 
                zpd.requestFocus();
                return;
            } else {
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(Constants.BUNDLE).getString("Another_instance_of_this_action_is_already_running._Please_wait_untill_it_finishes."), NotifyDescriptor.ERROR_MESSAGE);
                d.setTitle(NbBundle.getBundle(Constants.BUNDLE).getString("Error:_another_instance_is_running"));
                DialogDisplayer.getDefault().notify(d);            
                return;
            }
        }
        
        ExportPackageInfo.setProcessed(true);
        
        if (ExportZipUITools.getListData()!=null) {
            // XXX should rather delete OK/Cancel buttons from frame, and use NotifyDescriptor on a JPanel instead
            zpd = new ExportZipDialog();
            zpd.setVisible(true);
        } else {
            ExportPackageInfo.setProcessed(false);
        }
    }
        
    /**
     * The action is synchronous
     * @return synchronous
     */
    protected boolean asynchronous() {
        return false;
    }

    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
}
