/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural.slide;

import java.beans.IntrospectionException;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNode;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class SlideStructuralElementNode extends StructuralNode {
    
    /** Creates a new instance of SectionStructuralElementNode */
    public SlideStructuralElementNode(SlideStructuralElement el) throws IntrospectionException {
        super(el);
        
        this.setIconBase("org/netbeans/modules/latex/model/structural/impl/resources/slide-icon");
    }
    
    public SourcePosition getOpeningPosition() {
        return ((SlideStructuralElement) getBean()).getNode().getStartingPosition();
    }
}
