/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.regexplugin;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ToolMenuAction extends CallableSystemAction {

    public void performAction() {
        new regexTopComponent().open();
    }

    public String getName() {
        return NbBundle.getMessage(ToolMenuAction.class, "CTL_ToolMenuAction");
    }

    @Override
    protected String iconResource() {
        return "org/netbeans/modules/regexplugin/plugin_go.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
