/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.perspective.menus;

import org.netbeans.modules.perspective.ui.OpenPerspective;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class OpenPerspectivesAction extends CallableSystemAction {

    public void performAction() {
        OpenPerspective.createOpenUI();
    }

    public String getName() {
        return NbBundle.getMessage(OpenPerspectivesAction.class, "CTL_OpenPerspectivesAction");
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
    protected boolean asynchronous() {
        return false;
    }
}
