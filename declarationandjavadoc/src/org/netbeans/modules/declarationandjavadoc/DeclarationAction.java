package org.netbeans.modules.declarationandjavadoc;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Declaration component.
 */
public class DeclarationAction extends AbstractAction {
    
    public DeclarationAction() {
        super(NbBundle.getMessage(DeclarationAction.class, "CTL_DeclarationAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(DeclarationTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = DeclarationTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
