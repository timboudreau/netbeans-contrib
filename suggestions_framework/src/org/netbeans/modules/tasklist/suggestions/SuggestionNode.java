/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.util.List;
import org.netbeans.modules.tasklist.core.AutoFixAction;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.FilterAction;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;


class SuggestionNode extends TaskNode {
    
    // Leaf
    SuggestionNode(SuggestionImpl item) {
        super(item);
        init(item);
    } 

    // Non-leaf/parent
    SuggestionNode(SuggestionImpl item, List subtasks) {
        super(item, subtasks);
        init(item);
    }

    private void init(SuggestionImpl item) {
        this.item = item;
        //setDefaultAction(SystemAction.get(GoToTaskAction.class));
        setDefaultAction(SystemAction.get(AutoFixAction.class));
    }

    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
	SuggestionImpl eitem = (SuggestionImpl)item;
        if (eitem.hasSubtasks()) {
            return new SuggestionNode(eitem, eitem.getSubtasks());
        } else {
            return new SuggestionNode(eitem);
        }
    }

   /**
       @todo Should "task has associated filepos" and "task is sourcescan task"
         have separate icons?
    */
    protected void updateIcon() {
        setIconBase("org/netbeans/modules/tasklist/suggestions/suggTask"); // NOI18N
    }
    
    protected SystemAction[] createActions() {
	
	// TODO Perform lookup here to compute an aggregate
	// menu from other modules as well. But how do we determine
	// order? I think NetBeans 4.0's actions re-work will have
	// some better support for integrating context menus so I won't
	// try to be too clever here...

	// XXX look up and locate actions

        if (item.getParent() == null) {
            // Create actions shown on an empty tasklist (e.g. only root
            // is there)
            return new SystemAction[] {
                SystemAction.get(FilterAction.class),
                null
            };
        } else {
            return new SystemAction[] {
                //SystemAction.get(GoToTaskAction.class),
                //null,
                SystemAction.get(AutoFixAction.class),
                null,
                SystemAction.get(FilterAction.class),
                null,
                SystemAction.get(ExportAction.class),
                //null,
                //SystemAction.get(ScanTasksAction.class),
                null,
                SystemAction.get(PropertiesAction.class),
            };
        }
    }

    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);
        
        Set sse = Sheet.createExpertSet();
        s.put(sse);
        
        try {
            Property p;
            p = new Reflection(item, String.class, "getSummary", null /* Don't allow users to edit this "setDescription" */); // NOI18N
            p.setName(TaskListView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(TaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(TaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, Integer.TYPE,
                                               "getPriorityNumber", null); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_PRIO);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "Priority")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "PriorityHint")); // NOI18N
            ss.put(p);

            
            p = new Reflection(item, String.class, "getFileBaseName", null /* Don't allow users to edit this! "setFileBaseName" */); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_FILE);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "File")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "FileHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getLineNumber", null /* Don't allow users to edit this! "setLineNumber" */); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_LINE);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "Line")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "LineHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, String.class, "getCategory",
                                               null); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_CAT);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "Category")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "CategoryHint")); // NOI18N
            ss.put(p);


        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }
    
    public boolean canRename() {
        return false;
    }

    public boolean canDestroy() {
        // No point since it gets recreated after every edit
        return false;
    }

    /** Can this node be copied?
    * @return <code>true</code>
    */
    public boolean canCopy () {
        // Can't copy the root node:
        return (item.getParent() != null);
    }

    /** Can this node be cut?
    * @return <code>false</code>
    */
    public boolean canCut () {
        // No point since it gets recreated after every edit
        return false;
    }        
}

