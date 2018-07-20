package org.netbeans.modules.gsf.tools.lucene;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows IndexBrowser component.
 */
public class IndexBrowserAction extends AbstractAction {
    
    public IndexBrowserAction() {
        super(NbBundle.getMessage(IndexBrowserAction.class, "CTL_IndexBrowserAction"));
        //        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(IndexBrowserTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = IndexBrowserTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
