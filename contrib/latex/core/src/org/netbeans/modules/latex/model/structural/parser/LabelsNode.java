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
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.modules.latex.model.structural.StructuralNode.StructuralNodeChildren;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class LabelsNode extends AbstractNode {
    
    public LabelsNode(MainStructuralElement el) throws IntrospectionException {
        super(new LabelsNodeChildren(el));
        
        setName("All Labels");
        setIconBase("org/netbeans/modules/latex/model/structural/impl/resources/label-icon");
    }

    protected SystemAction[] createActions() {
        return new SystemAction[0];
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("latex.ssec.all.labels.node");
    }
    
    protected static class LabelsNodeChildren extends StructuralNodeChildren {
        
        public LabelsNodeChildren(MainStructuralElement el) throws IntrospectionException {
            super(el);
        }
        
        protected void doSetKeys() {
            setKeys(((MainStructuralElement) getElement()).getLabels());
        }
        
        public Node[] createNodes(Object key) {
            Node node = StructuralNodeFactory.createNode((StructuralElement) key);
            
            return new Node[] {new FilterNode(node)};
        }
        
    }
}
