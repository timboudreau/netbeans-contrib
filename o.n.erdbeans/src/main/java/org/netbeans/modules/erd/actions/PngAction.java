package org.netbeans.modules.erd.actions;

import org.netbeans.modules.erd.io.ERDDataObject;
import org.openide.cookies.EditCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class PngAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        ERDDataObject editorCookie = (ERDDataObject) activatedNodes[0].getLookup().lookup(ERDDataObject.class);
        // TODO use editorCookie
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(PngAction.class, "CTL_PngAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditCookie.class
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

