package org.netbeans.modules.declarationandjavadoc;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Javadoc component.
 */
public class JavadocAction extends AbstractAction {
    
    public JavadocAction() {
        super(NbBundle.getMessage(JavadocAction.class, "CTL_JavadocAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(JavadocTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = JavadocTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
