/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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
    
    protected static class LabelsNodeChildren extends StructuralNodeChildren<MainStructuralElement> {
        
        public LabelsNodeChildren(MainStructuralElement el) throws IntrospectionException {
            super(el);
        }
        
        protected void doSetKeys() {
            setKeys(getElement().getLabels());
        }
        
        @Override
        public Node[] createNodes(StructuralElement key) {
            Node node = StructuralNodeFactory.createNode(key);
            
            return new Node[] {new FilterNode(node)};
        }
        
    }
}
