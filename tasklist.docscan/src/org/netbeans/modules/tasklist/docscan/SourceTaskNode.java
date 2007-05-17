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

package org.netbeans.modules.tasklist.docscan;


import javax.swing.*;

import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.openide.nodes.Children;


import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.modules.tasklist.core.*;

/**
 * Represents one scanned source task as a Node with
 * actions, cookies, properties, clipboard operations and
 * children (at root represents all tasks in list).
 *
 * @author Petr Kuzel
 */
class SourceTaskNode extends SuggestionNode {

    public SourceTaskNode(SuggestionImpl rootItem) {
      super(rootItem, Children.LEAF);
    }


    public SourceTaskNode(SuggestionImpl rootItem, Children children) {
      super(rootItem, children);
    }

    public Node cloneNode () {
      SourceTaskNode clon = new SourceTaskNode((SuggestionImpl)this.item);
      if (!clon.isLeaf()) 
	clon.setChildren((Children)getTaskChildren().clone());
      return clon;
    }

    protected TaskChildren createChildren() {
      return new SourceTaskChildren((SuggestionImpl)this.item);
    }

    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(ShowSuggestionAction.class)
        };
    }

//     public String getDisplayName() {
//         return Util.getString("task-col");  // see TreeTableModelAdapter.getColumnName(int column)
//     }

//     public String getShortDescription() {
//         return Util.getString("TODOHint");
//     }

    
}

