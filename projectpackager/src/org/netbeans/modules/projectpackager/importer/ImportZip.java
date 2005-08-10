/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.projectpackager.importer;

import org.netbeans.modules.projectpackager.tools.Constants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 * Main class of the import zip part, executed by an action registered in layer.xml
 * @author roman
 */
public class ImportZip extends CallableSystemAction {
    private static ImportZipDialog izd;
    
    /**
     * Constructs new ImportZip
     */
    public ImportZip() {
    }
    
    /**
     * Return action name
     * @return action name
     */
    public String getName() {
        return java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("NetBeans_Project_from_Zip...");
    }
    
    /**
     * Return action help context
     * @return help context
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    /**
     * Perform the action - open the main dialog
     */
    public void performAction() {
        if (ImportPackageInfo.isProcessed()) {
            if (izd.isShowing()) { 
                izd.requestFocus();
                return;
            } else {
                NotifyDescriptor d = new NotifyDescriptor.Message(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Another_instance_of_this_action_is_already_running._Please_wait_untill_it_finishes."), NotifyDescriptor.ERROR_MESSAGE);
                d.setTitle(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Error:_another_instance_is_running"));
                DialogDisplayer.getDefault().notify(d);            
                return;
            }
        }
        
        ImportPackageInfo.setProcessed(true);
        
        izd = new ImportZipDialog();
        izd.setVisible(true);
    }
        
    /**
     * Sychronous
     * @return false as synchronous
     */
    protected boolean asynchronous() {
        return false;
    }
}

