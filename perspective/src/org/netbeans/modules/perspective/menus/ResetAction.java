package org.netbeans.modules.perspective.menus;

import java.io.IOException;
import org.netbeans.modules.perspective.persistence.MainParser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ResetAction extends CallableSystemAction {

    private static final long serialVersionUID = 1l;

    public void performAction() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(NbBundle.getMessage(ResetAction.class,"Reset_ALL_Comfrom_Massage"), NbBundle.getMessage(ResetAction.class,"Reset_Perspectives_H"), NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            try {
                MainParser.getInstance().reset();
                NotifyDescriptor information = new NotifyDescriptor.Message(NbBundle.getMessage(ResetAction.class,"Window_Reset_Massage"), NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(information);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(ResetAction.class,"Reset_Perspectives");
    }

    @Override
    protected void initialize() {
        super.initialize();
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