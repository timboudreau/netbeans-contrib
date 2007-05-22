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
import org.openide.util.datatransfer.NewType;

/**
 * Specifies what children will be under the Library subnode of the
 * SAP Components node.
 */
public class SAPComponentsLibrariesNode extends AbstractNode {
    
    public SAPComponentsLibrariesNode() {
        super(new SAPComponentsLibrariesChildren());
        setIconBaseWithExtension("com/sun/jbi/sapbapibc/explorer/SAPComponentsLibrariesIcon.gif");
        setName(bundle.getString("SAPComponentsLibrariesNode.name"));
        setDisplayName(bundle.getString("SAPComponentsLibrariesNode.display_name"));
        setShortDescription(bundle.getString("SAPComponentsLibrariesNode.description"));
    }
    
    public NewType[] getNewTypes() {
        NewType[] newTypes = new NewType[] {
            new SAPLibraryNewType(),
        };
        return newTypes;
    }

    public Node cloneNode() {
        return new SAPComponentsLibrariesNode();
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
            null,
            SystemAction.get(NewAction.class),
            SystemAction.get(ToolsAction.class),
        };
        return result;
    }

    public SystemAction[] getContextActions() {
        // deprecated
        return getActions();
    }

    private static ResourceBundle bundle = NbBundle.getBundle(SAPComponentsLibrariesNode.class);
}
