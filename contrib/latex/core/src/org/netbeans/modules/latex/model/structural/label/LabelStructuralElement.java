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

import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public final class LabelStructuralElement extends StructuralElement implements LabelInfo, Comparable {
    
    private CommandNode node;
    private String      label;
    private String      caption;
    
    public LabelStructuralElement(CommandNode labelCommand, String label, String caption) {
        this.node = labelCommand;
        this.label = label;
        this.caption = caption;
    }
    
    public int getPriority() {
        return 100000;
    }
    
    public String getName() {
        return getLabel() + " (" + getCaption() + ")";
    }
    
    public CommandNode getNode() {
        return node;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public SourcePosition getStartingPosition() {
        return getNode().getStartingPosition();
    }
    
    public SourcePosition getEndingPosition() {
        return getNode().getEndingPosition();
    }
    
    public int compareTo(Object o) {
        LabelStructuralElement el = (LabelStructuralElement) o;
        
        return getLabel().compareTo(el.getLabel());
    }
    
    public String toString() {
        return getName();
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("latex.ssec.label.node");
    }
    
}
