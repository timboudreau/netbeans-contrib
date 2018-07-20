package org.netbeans.modules.toolsintegration;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ExternalToolsManagerAction extends CallableSystemAction {
    
    public void performAction () {
        final ExternalToolsManager externalToolsManager = new ExternalToolsManager ();
        final DialogDescriptor dialogDescriptor = new DialogDescriptor (
            externalToolsManager,
            "External Tools Manager"
        );
        final Dialog dialog = DialogDisplayer.getDefault ().
            createDialog (dialogDescriptor);
        dialog.setVisible (true);
        if (dialogDescriptor.getValue () != dialogDescriptor.OK_OPTION)
            return;
        Model.getDefault ().setTools (
            externalToolsManager.getTools ()
        );
    }
    
    public String getName () {
        return NbBundle.getMessage (ExternalToolsManagerAction.class, "CTL_ExternalTools");
    }
    
    protected void initialize () {
        super.initialize ();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue ("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous () {
        return false;
    }
}
