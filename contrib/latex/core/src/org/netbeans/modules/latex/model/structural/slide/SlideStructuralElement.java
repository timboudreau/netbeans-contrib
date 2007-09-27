/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
