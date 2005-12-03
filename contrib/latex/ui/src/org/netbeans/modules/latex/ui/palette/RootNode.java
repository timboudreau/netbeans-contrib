/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.latex.model.IconsStorage;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class RootNode extends AbstractNode {
    
    /** Creates a new instance of RootNode */
    public RootNode() {
        super(new RootChildren());
    }
    
    private static final class RootChildren extends Children.Keys {
        
        protected void addNotify() {
            List cath = new ArrayList(IconsStorage.getDefault().getCathegories());
            
            cath.remove("greek");
            cath.add(0, "greek");
            
            setKeys(cath);
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] {new CategoryNode((String) key)};
        }
        
    }
    
}
