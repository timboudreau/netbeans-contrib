/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.perspective.menus;

import java.util.List;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.persistence.MainParser;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class RemovePerspectiveAction extends CallableSystemAction {

    public void performAction() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(NbBundle.getMessage(RemovePerspectiveAction.class, "Remove_Comfrom_Massage",
                PerspectiveManagerImpl.getInstance().getSelected().getAlias()),
                NbBundle.getMessage(ResetAction.class, "Remove_Perspective_H"),
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            PerspectiveManagerImpl managerImpl = PerspectiveManagerImpl.getInstance();
            PerspectiveImpl selected = managerImpl.getSelected();

            PerspectiveImpl next = null;


            int index = selected.getIndex();
            List<PerspectiveImpl> perspectives = managerImpl.getPerspectives();
            if (index < (perspectives.size() - 1)) {
                next = perspectives.get(++index);
            } else if (index!=0&&perspectives.size() > 0) {
                next = perspectives.get(0);
            }
            managerImpl.deregisterPerspective(selected, true);

            managerImpl.setSelected(next, true);


            ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();

            MainParser.getInstance().hidePerspective(selected);
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemovePerspectiveAction.class, "CTL_RemovePerspectiveAction");
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

        return PerspectiveManager.getDefault().getSelected() != null;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
