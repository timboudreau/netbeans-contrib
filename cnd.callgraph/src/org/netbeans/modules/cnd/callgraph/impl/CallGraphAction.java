/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.cnd.callgraph.impl;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows CallGraph component.
 */
public class CallGraphAction extends AbstractAction {

    public CallGraphAction() {
        super(NbBundle.getMessage(CallGraphAction.class, "CTL_CallGraphAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(CallGraphTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = CallGraphTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
