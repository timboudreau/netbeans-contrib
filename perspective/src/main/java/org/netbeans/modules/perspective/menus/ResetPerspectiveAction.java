/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.perspective.menus;

import org.netbeans.modules.perspective.persistence.MainParser;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ResetPerspectiveAction extends CallableSystemAction {

    public void performAction() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(ResetAction.class, "Reset_Comfrom_Massage",
                PerspectiveManagerImpl.getInstance().getSelected().getAlias()),
                NbBundle.getMessage(ResetAction.class, "Reset_Perspective_H"),
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            MainParser.getInstance().resetPerspective(PerspectiveManagerImpl.getInstance().getSelected());
        }
    }

    public String getName() {
        return NbBundle.getMessage(ResetPerspectiveAction.class, "CTL_ResetPerspectiveAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isEnabled() {
        PerspectiveImpl selected = PerspectiveManagerImpl.getInstance().getSelected();
        if (selected != null && !selected.getName().startsWith("custom_")) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
