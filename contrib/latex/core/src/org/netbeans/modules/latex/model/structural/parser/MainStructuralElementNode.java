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
