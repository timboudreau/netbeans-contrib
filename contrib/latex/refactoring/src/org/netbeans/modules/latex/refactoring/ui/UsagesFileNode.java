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
package org.netbeans.modules.latex.refactoring.ui;

import java.util.Collections;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class UsagesFileNode extends FilterNode {
    
    /** Creates a new instance of UsagesRootNode */
    public UsagesFileNode(Node delegate, List<? extends org.netbeans.modules.latex.model.command.Node> usages) {
        super(delegate, new UsagesChildren(usages));
    }
    
    private static final class UsagesChildren extends Children.Keys {
        
        private List<? extends org.netbeans.modules.latex.model.command.Node> usages;
        
        public UsagesChildren(List<? extends org.netbeans.modules.latex.model.command.Node> usages) {
            this.usages = usages;
        }
        
        @Override
        public void addNotify() {
            setKeys(usages);
        }
        
        @Override
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }

        protected Node[] createNodes(Object key) {
            return new Node[] {new UseNode((org.netbeans.modules.latex.model.command.Node) key)};
        }
    }
}
