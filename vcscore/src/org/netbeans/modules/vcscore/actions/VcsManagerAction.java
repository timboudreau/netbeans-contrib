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

package org.netbeans.modules.vcscore.actions;

import org.netbeans.modules.vcscore.ui.fsmanager.*;

import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.*;

import javax.swing.SwingUtilities;
import java.awt.*;
/**
* VCS Manager Action.
*
* @author   Richard Gregor
*/
public class VcsManagerAction extends CallableSystemAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -3240759228704433330L;
    
    private VcsManager manager = null;

    /** Human presentable name of the action. This should be
     * presented as an item in a menu.
     * @return the name of the action
     */
    public String getName () {
        return NbBundle.getBundle (VcsManagerAction.class).getString ("CTL_VcsManagerActionName");
    }

    /** The action's icon location.
     * @return the action's icon location
     */
    protected String iconResource () {        
        return "org/netbeans/modules/vcscore/actions/VcsManagerActionIcon.gif"; // NOI18N
    }

    /** Help context where to find more about the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (VcsManagerAction.class);
    }

    /** This method is called by one of the "invokers" as a result of
     * some user's action that should lead to actual "performing" of the action.
     * This default implementation calls the assigned actionPerformer if it
     * is not null otherwise the action is ignored.
     */
    public void performAction () {               
        Object[] options = new Object[]{ NbBundle.getMessage(VcsManagerAction.class, "LBL_VcsManagerClose") };     
        DialogDescriptor desc = new DialogDescriptor(getVcsManager(),NbBundle.getBundle (VcsManagerAction.class).getString ("CTL_VcsManagerTitle"));        
        desc.setOptions(options);
        desc.setValue(options[0]);
        desc.setClosingOptions(options);        
        desc.setHelpCtx(getHelpCtx());
        desc.setModal(false);
                                                     
        final Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.show ();                    
     
    }
    
    private VcsManager getVcsManager(){
        if(manager == null)
            manager = new VcsManager();
        return manager;
    }
    
    protected boolean asynchronous(){
        return false;
    }
}
