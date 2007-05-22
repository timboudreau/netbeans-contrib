package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * Specifies what children will be under the SAP Components node.
 */
public class SAPComponentsNode extends AbstractNode {
    
    public SAPComponentsNode() {
        super(new SAPComponentsChildren());
        setIconBaseWithExtension("com/sun/jbi/sapbapibc/explorer/SAPComponentsIcon.gif");
        setName(bundle.getString("SAPComponentsNode.name"));
        setDisplayName(bundle.getString("SAPComponentsNode.display_name"));
        setShortDescription(bundle.getString("SAPComponentsNode.description"));
    }
    
    public Node cloneNode() {
        return new SAPComponentsNode();
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx("com.sun.jbi.sapbapibc.explorer");
    }
    
    public Action[] getActions(boolean inContext) {
        return getActions();
    }

    public SystemAction[] getActions() {
        // deprecated
        SystemAction[] result = new SystemAction[] {
            SystemAction.get(OpenLocalExplorerAction.class),
            SystemAction.get(ToolsAction.class),
        };
        return result;
    }

    public SystemAction[] getContextActions() {
        // deprecated
        return getActions();
    }

    private static final ResourceBundle bundle =
        NbBundle.getBundle(SAPComponentsNode.class);
}
