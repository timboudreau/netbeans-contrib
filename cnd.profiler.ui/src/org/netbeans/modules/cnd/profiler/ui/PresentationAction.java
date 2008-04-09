/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.cnd.profiler.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Presentation component.
 */
public class PresentationAction extends AbstractAction {

    public PresentationAction() {
        super(NbBundle.getMessage(PresentationAction.class, "CTL_PresentationAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PresentationTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PresentationTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
