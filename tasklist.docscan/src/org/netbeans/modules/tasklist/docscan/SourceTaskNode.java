/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.util.List;
import java.util.ArrayList;
import java.awt.datatransfer.Transferable;

import javax.swing.*;

import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Sheet;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.actions.PropertiesAction;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.DataEditorSupport;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.client.Suggestion;

import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.core.editors.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.core.editors.LineNumberPropertyEditor;

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

