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

import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.GroupNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.ParagraphNode;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class SlideStructuralElement extends StructuralElement {

    public static final String NAME = "name";
    
    private BlockNode   node;
    
    public SlideStructuralElement(BlockNode node) {
        this.node = node;
    }
    
    public int getPriority() {
        return 40000;
    }
    
    public String getName() {
        //TODO: this is only a workaround, not very exact:
        if (node.getContent().getChildrenCount() > 0) {
            Node argumentParagraph = node.getContent().getChild(0);
            
            if (argumentParagraph instanceof ParagraphNode && ((ParagraphNode) argumentParagraph).getChildrenCount() > 0) {
                Node argumentGroup = ((ParagraphNode) argumentParagraph).getChild(0);
                
                if (argumentGroup instanceof GroupNode && ((GroupNode) argumentGroup).getChildrenCount() > 0) {
                    CharSequence name = ((GroupNode) argumentGroup).getChild(0).getText();
                    
                    return name.toString();
                }
            }
        }
        
        return "Unnamed Slide";
    }
    
    public BlockNode getNode() {
        return node;
    }
    
    public String getTypeName() {
        return org.openide.util.NbBundle.getBundle(SlideStructuralElement.class).getString("LBL_Slide");
    }

    /*package private*/ void fireNameChanged() {
        pcs.firePropertyChange(NAME, null, null);
    }
}
