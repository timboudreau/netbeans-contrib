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
package org.netbeans.modules.latex.model.structural.figtable;

import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class FigtableStructuralElement extends StructuralElement {

    public static final String NAME = "name";
    
    private BlockNode   node;
    private int         type;
    
    public static final int UNKNOWN = 0;
    public static final int FIGURE = 1;
    public static final int TABLE  = 2;
    
    public FigtableStructuralElement(BlockNode node, int type) {
        this.node = node;
        this.type = type;
    }
    
    public int getPriority() {
        return 40000;
    }
    
    public String getName() {
        String caption = Utilities.getDefault().findCaptionForNode(node);
        
        if (caption != null)
            return caption;
        else
            return "";
    }
    
    public int getType() {
        return type;
    }
    
    public BlockNode getNode() {
        return node;
    }
    
    private static final String[] names = {
        "", //NOI18N
        org.openide.util.NbBundle.getBundle(FigtableStructuralElement.class).getString("LBL_Figure"),
        org.openide.util.NbBundle.getBundle(FigtableStructuralElement.class).getString("LBL_Table")
    };
    
    public String getTypeName() {
        return names[getType()];
    }

    /*package private*/ void fireNameChanged() {
        pcs.firePropertyChange(NAME, null, null);
    }
}
