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
package org.netbeans.modules.latex.model.structural.section;

import java.util.List;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class SectionStructuralElement extends StructuralElement {
    
    public static final String NAME = "name";
    
    private CommandNode node;
    private int         priority;
    private int         type;
    
    public SectionStructuralElement(CommandNode node, int priority, int type) {
        this.node = node;
        this.priority = priority;
        this.type = type;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getName() {
        if (node.getArgumentCount() > 0)
            return node.getArgument(0).getText().toString(); //!!!there is no assurance that there will be argument number 0!
        else
            return "";
    }
    
    public int getType() {
        return type;
    }
    
    public CommandNode getNode() {
        return node;
    }
    
    /*package private*/ void fireNameChanged() {
        pcs.firePropertyChange(NAME, null, null);
    }
}
