package org.netbeans.modules.erlang.console;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Erlang Console component.
 */
public class ErlangConsoleAction extends AbstractAction {
    
    public ErlangConsoleAction() {
        super(NbBundle.getMessage(ErlangConsoleAction.class, "CTL_ErlangConsoleAction"));
        putValue(SMALL_ICON,new ImageIcon(Utilities.loadImage(ErlangConsoleTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = ErlangConsoleTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
