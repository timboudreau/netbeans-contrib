package org.netbeans.modules.perspective.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public final class QuickSwitch extends AbstractAction {

    private static final long serialVersionUID = 1l;

    public QuickSwitch() {
        putValue( NAME, getName() );
        putValue("noIconInMenu", Boolean.TRUE);//NOI18N
    }


    public void performAction() {
        ToolbarStyleSwitchUI.getInstance().showPerspectiveList();
    }

    public String getName() {
        return NbBundle.getMessage(QuickSwitch.class,"Quick_Switch");
    }

    

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void actionPerformed(ActionEvent e) {
        performAction();
    }

   
}
