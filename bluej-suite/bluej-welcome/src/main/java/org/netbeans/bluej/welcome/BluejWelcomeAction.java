package org.netbeans.bluej.welcome;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows BluejWelcome component.
 */
public class BluejWelcomeAction extends AbstractAction {
    
    public BluejWelcomeAction() {
        super(NbBundle.getMessage(BluejWelcomeAction.class, "CTL_BluejWelcomeAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(BluejWelcomeTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = BluejWelcomeTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
