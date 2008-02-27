package org.netbeans.modules.graphicclassview.actions;

import org.netbeans.modules.graphicclassview.JavaViewComponent;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class GraphicViewAction extends CookieAction {

    public GraphicViewAction() {
    }

    protected void performAction(Node activatedNodes[]) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
            if ((tc instanceof JavaViewComponent) && dataObject.equals(tc.getLookup().lookup(DataObject.class))) {
                tc.open();
                tc.requestActive();
                return;
            }
        }

        JavaViewComponent nue = new JavaViewComponent(dataObject);
        nue.open();
        nue.requestActive();
    }

    protected int mode() {
        return 4;
    }

    public String getName() {
        return NbBundle.getMessage(GraphicViewAction.class, "CTL_GraphicViewAction");
    }

    protected Class[] cookieClasses() {
        return (new Class[]{
            DataObject.class
        });
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
