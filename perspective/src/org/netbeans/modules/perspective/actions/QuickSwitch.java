package org.netbeans.modules.perspective.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public final class QuickSwitch extends AbstractAction {

    private static final long serialVersionUID = 1l;

    public QuickSwitch() {
        putValue( NAME, getName() );
        putValue("noIconInMenu", Boolean.TRUE);//NOI18n
    }


    public void performAction() {
        JButton button=PerspectiveManager.getInstance().getToolbarButton();
        Point point = button.getLocationOnScreen();
        PerspectiveManager.getInstance().getMenu().setLocation(point.x, point.y + button.getHeight());
        PerspectiveManager.getInstance().getMenu().setInvoker(button);
        PerspectiveManager.getInstance().getMenu().setVisible(true);
    }

    public String getName() {
        return NbBundle.getMessage(QuickSwitch.class, "CTL_QuickSwitch");
    }

    

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void actionPerformed(ActionEvent e) {
        performAction();
    }

   
}