package org.netbeans.modules.perspective.menus;

import org.netbeans.modules.perspective.ui.SaveAsUI;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SaveAsAction extends CallableSystemAction {
private static final long serialVersionUID = 1l;
    public void performAction() {
        SaveAsUI.createSaveAsUI();
    }

    public String getName() {
        return NbBundle.getMessage(SaveAsAction.class, "CTL_SaveAsAction");
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
