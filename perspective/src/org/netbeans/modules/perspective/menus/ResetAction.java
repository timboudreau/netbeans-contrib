package org.netbeans.modules.perspective.menus;

import java.io.IOException;
import org.netbeans.modules.perspective.persistence.MainPaser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ResetAction extends CallableSystemAction {

    private static final long serialVersionUID = 1l;

    public void performAction() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you want to reset Perspectives ?", "Reset Perspectives", NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            try {
                MainPaser.getInstance().reset();
                NotifyDescriptor information = new NotifyDescriptor.Message("Please invoke \"Window ->Reset Windows\" to compleate Reset", NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(information);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(ResetAction.class, "CTL_ResetAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}