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
package org.netbeans.modules.latex.model.structural.figtable;

import java.beans.IntrospectionException;
import org.netbeans.modules.latex.model.structural.NodeProvider;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class FigtableNodeProvider extends NodeProvider {
    
    /** Creates a new instance of SectionNodeProvider */
    public FigtableNodeProvider() {
    }
    
    public Node createNode(StructuralElement element) throws IntrospectionException {
        if (element instanceof FigtableStructuralElement) {
            return new FigtableStructuralElementNode((FigtableStructuralElement) element);
        }
        
        return null;
    }
    
}
