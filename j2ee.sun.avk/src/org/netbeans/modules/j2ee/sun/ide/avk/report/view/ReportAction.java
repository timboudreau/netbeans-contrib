package org.netbeans.modules.j2ee.sun.ide.avk.report.view;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Report component.
 * @author bshankar@sun.com
 */
public class ReportAction extends AbstractAction {
    
    
    TopComponent win;
    
    public ReportAction() {
        super(NbBundle.getMessage(ReportAction.class, "CTL_ReportAction"));
        win = ReportTopComponent.findInstance();
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ReportTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        win.open();
        win.requestActive();
    }
    
    public void repaint() {
        win.repaint();
    }
    
    public boolean isUIClosed() {
        return !win.isOpened();
    }
}
