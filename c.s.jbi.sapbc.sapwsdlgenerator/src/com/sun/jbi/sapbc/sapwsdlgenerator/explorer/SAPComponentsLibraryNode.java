package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author nang
 */
public class SAPComponentsLibraryNode
    extends AbstractNode
    implements SAPComponentsChangeListener {
    
    /**
     * Creates a new instance of SAPComponentsLibraryNode
     */
    public SAPComponentsLibraryNode(File data) {
        super(Children.LEAF);
        this.data = data;
        setIconBaseWithExtension("com/sun/jbi/sapbapibc/explorer/SAPComponentsLibraryIcon.gif");
        try {
            setName(data.getCanonicalPath());
        } catch (IOException ex) {
            setName(data.getAbsolutePath());
        }
        setDisplayName(data.getName());
        
        SAPComponentsNotifier.addChangeListener(this);
    }
    
    public Object getDataObject() {
        return data;
    }
    
    public Node cloneNode() {
        return new SAPComponentsLibraryNode(data);
    }

    public Action[] getActions(boolean inContext) {
        return getActions();
    }

    public SystemAction[] getActions() {
        // deprecated
        SystemAction[] result = new SystemAction[] {
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
        return result;
    }

    public SystemAction[] getContextActions() {
        // deprecated
        return getActions();
    }
    
    public boolean canDestroy() {
        return true;
    }

    public void destroy() throws IOException {
        super.destroy();
        SAPComponentsNotifier.notifyLibraryRemoved(data);
    }

    public void added(SAPComponentsChangeEvent evt) {
        // NO-OP
    }

    public void removed(SAPComponentsChangeEvent evt) {
        // NO-OP
    }

    public void changed(SAPComponentsChangeEvent evt) {
        File[] args = (File[]) evt.getSubject();
        File oldValue = args[0];
        File newValue = args[1];
        if (oldValue == data) {
            data = newValue;
            setName(data.getAbsolutePath());
            setDisplayName(data.getName());
        }
    }
    
    protected Sheet createSheet() {
        Sheet propSheet = super.getSheet();
        Sheet.Set propSet = propSheet.get(Sheet.PROPERTIES);
        if (propSet == null) {
            propSet = propSheet.createPropertiesSet();
        }
        
        propSet.put(new LibraryPathProperty(data));

        return propSheet;
    }

    protected void finalize() throws Throwable {
        SAPComponentsNotifier.removeChangeListener(this);
    }
    
    private static final ResourceBundle bundle =
        NbBundle.getBundle(SAPComponentsLibraryNode.class);
    
    private File data;
}
