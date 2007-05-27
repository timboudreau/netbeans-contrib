package org.netbeans.modules.syntaxtreebrowser;

import java.util.Set;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class SyntaxTreeBrowserAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = (DataObject) activatedNodes[0].getLookup().lookup(DataObject.class);
        SyntaxBrowserTopComponent stc = null;
        Set<TopComponent> tcs = TopComponent.getRegistry().getOpened();
        for (TopComponent tc : tcs) {
            if (tc instanceof SyntaxBrowserTopComponent) {
                SyntaxBrowserTopComponent check  = (SyntaxBrowserTopComponent) tc;
                if (check.getDataObject() == dataObject) {
                    stc = check;
                }
            }
        }
        if (stc == null) {
            stc = new SyntaxBrowserTopComponent (dataObject);
        }
        stc.open();
        stc.requestActive();
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SyntaxTreeBrowserAction.class, "CTL_SyntaxTreeBrowserAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}

