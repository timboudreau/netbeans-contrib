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
package org.netbeans.modules.latex.model.structural.label;

import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;

/**
 *
 * @author Jan Lahoda
 */
public class RefStructuralElement extends StructuralElement {
    
    private CommandNode node;
    private String label;
    
    /** Creates a new instance of RefStructuralElement */
    public RefStructuralElement(CommandNode node, String label) {
        this.node = node;
        this.label = label;
    }
    
    public String getName() {
        SourcePosition position = getStartingPosition();
        
        return Utilities.getDefault().getHumanReadableDescription(position);
    }
    
    public String getLabel() {
        return label;
    }
    
    public CommandNode getNode() {
        return node;
    }
    
    public SourcePosition getStartingPosition() {
        return getNode().getStartingPosition();
    }
    
    public int getPriority() {
        return -1;
    }
    
}
