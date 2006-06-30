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
