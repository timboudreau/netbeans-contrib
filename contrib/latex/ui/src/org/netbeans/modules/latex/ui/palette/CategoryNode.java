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

import java.util.Collections;
import org.netbeans.modules.latex.model.IconsStorage;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class CategoryNode extends AbstractNode {
    
    private String cathegory;
    
    /**
     * Creates a new instance of CategoryNode
     */
    public CategoryNode(String cathegory) {
        super(new CathegoryChildren(cathegory));
        this.cathegory = cathegory;
    }
    
    public String getDisplayName() {
        return IconsStorage.getDefault().getCathegoryDisplayName(cathegory);
    }
    
    private static final class CathegoryChildren extends Children.Keys {

        private String cathegory;
        
        public CathegoryChildren(String cathegory) {
            this.cathegory = cathegory;
        }
        protected void addNotify() {
            setKeys(IconsStorage.getDefault().getIconNamesForCathegory(cathegory));
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] {new IconNode((String) key)};
        }
        
    }
    
}
