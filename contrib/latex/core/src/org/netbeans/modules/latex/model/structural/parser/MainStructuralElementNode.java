/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural.parser;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.structural.StructuralNode;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class MainStructuralElementNode extends StructuralNode {
    
    /** Creates a new instance of MainStructuralElementNode */
    public MainStructuralElementNode(MainStructuralElement el) throws IntrospectionException {
        super(el, new MainStructuralElementNodeChildren(el));
    }
    
    protected static class MainStructuralElementNodeChildren extends StructuralNodeChildren {
        
        private LabelsNode labelsNode = null;
        
        public MainStructuralElementNodeChildren(MainStructuralElement el) {
            super(el);
        }

        protected synchronized void doSetKeys() {
            List l = new ArrayList();
            
            try {
                if (labelsNode == null)
                    labelsNode = new LabelsNode((MainStructuralElement) getElement());
                
                l.add(labelsNode);
            } catch (IntrospectionException e) {
                ErrorManager.getDefault().notify(e);
            }
            
            l.addAll(getElement().getSubElements());
            
            setKeys(l);
        }
        
        public void removeNotify() {
            super.removeNotify();
            labelsNode = null;
        }
    
        protected Node[] createNodes(Object key) {
            if (key instanceof Node) {
                return new Node[] {(Node) key};
            } else
                return super.createNodes(key);
        }

    }
    
}
