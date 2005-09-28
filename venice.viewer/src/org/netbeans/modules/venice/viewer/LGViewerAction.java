package org.netbeans.modules.venice.viewer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which shows LGViewer component.
 */
public class LGViewerAction extends AbstractAction {

    public LGViewerAction() {
        putValue(NAME, NbBundle.getMessage(LGViewerAction.class, "CTL_LGViewerAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage("org/netbeans/modules/venice/viewer/setting-inherited.gif", true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = WindowManager.getDefault().findTopComponent("LGViewerTopComponent");
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find LGViewer component.");
            win = LGViewerTopComponent.getDefault();
        }
        win.open();
        win.requestActive();
    }

}
