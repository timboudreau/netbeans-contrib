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

package org.netbeans.modules.tasklist.bugs;

import java.util.List;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.FilterAction;
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


class BugNode extends TaskNode {
    
    // Leaf
    BugNode(Bug item) {
        super(item);
        init(item);
    } 

    // Non-leaf/parent
    BugNode(Bug item, List subtasks) {
        super(item, subtasks);
        init(item);
    }

    private void init(Bug item) {
        this.item = item;
	setDefaultAction(SystemAction.get(ViewBugAction.class));
	setIconBase("org/netbeans/modules/tasklist/bugs/bug"); // NOI18N
    }

    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
	Bug eitem = (Bug)item;
        if (eitem.hasSubtasks()) {
            return new BugNode(eitem, eitem.getSubtasks());
        } else {
            return new BugNode(eitem);
        }
    }

    protected void updateIcon() {
	// Override so we don't reset it to task.gif here
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
                SystemAction.get(RefreshAction.class)
            };
        } else {
            return new SystemAction[] {
                SystemAction.get(ViewBugAction.class),
                null,
                SystemAction.get(RefreshAction.class),
                null,
                SystemAction.get(FilterAction.class),
                null,
                /*
                  SystemAction.get(CutAction.class),
                  SystemAction.get(CopyAction.class),
                  SystemAction.get(PasteAction.class),
                  null,
                  SystemAction.get(DeleteAction.class),
                  null,
                */
                SystemAction.get(ExportAction.class),
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
            p = new Reflection(item, String.class, "getSummary", null); // NOI18N
            p.setName(BugsView.PROP_BUG_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Summary")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "SummaryHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, String.class, "getId", null); // NOI18N
            p.setName(BugsView.PROP_BUG_ID);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "BugId")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "BugIdHint")); // NOI18N
            ss.put(p);
            
            p = new Reflection(item, String.class, "getSynopsis", null); // NOI18N
            p.setName(BugsView.PROP_BUG_SYNOPSIS);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Synopsis")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "SynopsisHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getPriorityNumber", null); // NOI18N
            p.setName(BugsView.PROP_BUG_PRIO);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Priority")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "PriorityHint")); // NOI18N
            ss.put(p);






            p = new Reflection(item, String.class, "getType", null); // NOI18N
            p.setName(BugsView.PROP_BUG_TYPE);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Type")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "TypeHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getComponent", null); // NOI18N
            p.setName(BugsView.PROP_BUG_COMP);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Component")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "ComponentHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getSubComponent", null); // NOI18N
            p.setName(BugsView.PROP_BUG_SUBCOMP);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "SubComponent")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "SubComponentHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getCreated", null); // NOI18N
            p.setName(BugsView.PROP_BUG_CREATED);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Created")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "CreatedHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getKeywords", null); // NOI18N
            p.setName(BugsView.PROP_BUG_KEYWORDS);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Keywords")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "KeywordsHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getAssignedTo", null); // NOI18N
            p.setName(BugsView.PROP_BUG_ASSIGNED);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Assigned")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "AssignedHint")); // NOI18N
            ss.put(p);




            p = new Reflection(item, String.class, "getReportedBy", null); // NOI18N
            p.setName(BugsView.PROP_BUG_REPORTEDBY);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "ReportedBy")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "ReportedByHint")); // NOI18N
            ss.put(p);


            p = new Reflection(item, String.class, "getStatus", null); // NOI18N
            p.setName(BugsView.PROP_BUG_STATUS);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Status")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "StatusHint")); // NOI18N
            ss.put(p);


            p = new Reflection(item, String.class, "getTarget", null); // NOI18N
            p.setName(BugsView.PROP_BUG_TARGET);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Target")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "TargetHint")); // NOI18N
            ss.put(p);



            p = new Reflection(item, Integer.TYPE, "getVotes", null); // NOI18N
            p.setName(BugsView.PROP_BUG_VOTES);
            p.setDisplayName(NbBundle.getMessage(BugNode.class, "Votes")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(BugNode.class, "VotesHint")); // NOI18N
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
        return false;
    }

    public boolean canCopy () {
        return false;
    }

    public boolean canCut () {
        return false;
    }    
}

