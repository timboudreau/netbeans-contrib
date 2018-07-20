package org.netbeans.modules.gsf.tools.lexing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows TokenSpy component.
 */
public class TokenSpyAction extends AbstractAction {
    
    public TokenSpyAction() {
        super(NbBundle.getMessage(TokenSpyAction.class, "CTL_TokenSpyAction"));
        //        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(TokenSpyTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TokenSpyTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
