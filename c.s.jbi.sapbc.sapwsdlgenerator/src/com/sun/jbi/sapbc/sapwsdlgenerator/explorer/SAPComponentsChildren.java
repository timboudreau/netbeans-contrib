package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * This class manages the list of nodes placed beneath the SAP Components node.
 */
public class SAPComponentsChildren extends Children.Keys {
    
    public SAPComponentsChildren() {
    }

    protected Node[] createNodes(Object key) {
        Collection<Node> nodes = new ArrayList<Node>();
        
        if (LIBRARY_FOLDER.equals(key)) {
            nodes.add(new SAPComponentsLibrariesNode());
        }
        
        return nodes.size() > 0 ? nodes.toArray(new Node[nodes.size()]) : null;
    }

    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.EMPTY_SET);
    }

    protected void addNotify() {
        super.addNotify();
        
        Collection<Object> keys = new ArrayList<Object>();
        
        // Generate "Libraries" subtree
        keys.add(LIBRARY_FOLDER);
        
        setKeys(keys);
    }
    
    private static final Object LIBRARY_FOLDER = new Object();
}
