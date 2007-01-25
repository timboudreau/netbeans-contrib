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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.rules;

import org.netbeans.modules.jackpot.ui.QueryAndRefactorAction;
import org.openide.actions.*;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import java.awt.Image;
import javax.swing.Action;

/**
 * RulesDataNode: a node delegate for Jackpot a rules file.
 */
public class RulesDataNode extends DataNode {
   
    public RulesDataNode(RulesDataObject obj) {
        super(obj, Children.LEAF);
    }
    
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/jackpot/rules/resources/Rule_file_16.png");
    }
    
    public Action getPreferredAction() {
        return SystemAction.get(EditAction.class);
    } 
    
    public Action[] getActions(boolean context) {
        return new Action [] {
            SystemAction.get(EditAction.class),
            SystemAction.get(QueryAndRefactorAction.class),
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(RenameAction.class),
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }
}
