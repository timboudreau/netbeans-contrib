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

package org.netbeans.modules.tasklist.suggestions;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.netbeans.api.tasklist.Suggestion;

import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.FilterAction;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.ExpandAllAction;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.PriorityPropertyEditor;

import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import java.awt.datatransfer.Transferable;
import org.netbeans.api.tasklist.SuggestionPriority;
import org.netbeans.modules.tasklist.core.LineNumberPropertyEditor;
import org.openide.text.Line;


/**
 * A node in the Suggestions View, representing a Suggestion
 *
 * @author Tor Norbye
 */

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
        if (item.getAction() == null) {
            //setDefaultAction(SystemAction.get(GoToTaskAction.class));
            setDefaultAction(SystemAction.get(ShowSuggestionAction.class));
        } else if (item.getAction() != null) {
            setDefaultAction(SystemAction.get(FixAction.class));
        } else {
            setDefaultAction(null);
        }
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

    protected void updateIcon() {
        setIconBase("org/netbeans/modules/tasklist/suggestions/suggTask"); // NOI18N
    }
    
    protected SystemAction[] createActions() {
        if (item.getParent() == null) {
            // Create actions shown on an empty tasklist (e.g. only root
            // is there)
            return new SystemAction[] {
                SystemAction.get(ShowCategoryAction.class),
                SystemAction.get(FilterAction.class),
                SystemAction.get(EditTypesAction.class)
            };
        } else {
            ArrayList actions = new ArrayList(20);
            if (item.getAction() != null) {
                actions.add(SystemAction.get(FixAction.class));
            }
            //actions.add(SystemAction.get(GoToTaskAction.class);
            actions.add(SystemAction.get(ShowSuggestionAction.class));
            List typeActions = 
                ((SuggestionImpl)item).getSType().getActions();
            if ((typeActions != null) && (typeActions.size() > 0)) {
                actions.add(null);
                Iterator it = typeActions.iterator();
                while (it.hasNext()) {
                    actions.add(it.next());
                }
            }
            actions.add(null);
            actions.add(SystemAction.get(DisableAction.class));

            // "Global" (not node specific) actions
            actions.add(null);
            actions.add(SystemAction.get(ShowCategoryAction.class));
            actions.add(SystemAction.get(EditTypesAction.class));
            actions.add(SystemAction.get(OptionsAction.class));
            actions.add(null);
            actions.add(SystemAction.get(FilterAction.class));
            actions.add(SystemAction.get(ExpandAllAction.class));
            actions.add(null);
            actions.add(SystemAction.get(ExportAction.class));

            // Property: node specific, but by convention last in menu
            actions.add(null);
            actions.add(SystemAction.get(PropertiesAction.class));

            return (SystemAction[])actions.toArray(
                 new SystemAction[actions.size()]);
        }
    }

    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        if (item.getParent() == null)
            return s;
        Set ss = s.get(Sheet.PROPERTIES);
        
        Set sse = Sheet.createExpertSet();
        s.put(sse);
        
        try {
            PropertySupport.Reflection p;
            p = new Reflection(item, String.class, "getSummary", null /* Don't allow users to edit this "setDescription" */); // NOI18N
            p.setName(TaskListView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(SuggestionsView.class, "SuggestionsRoot")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionsView.class, "SuggestionsRootHint")); // NOI18N
            ss.put(p);


            p = new Reflection(item, String.class, "getDetails", null); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_DETAILS);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "Details")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "DetailsHint")); // NOI18N
            ss.put(p);
            

            p = new Reflection(item, SuggestionPriority.class, 
                "getPriority", null); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_PRIO);
            p.setPropertyEditorClass(PriorityPropertyEditor.class);
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
            p.setPropertyEditorClass(LineNumberPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(SuggestionNode.class, "Line")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "LineHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, String.class, "getCategory",
                                               null); // NOI18N
            p.setName(SuggestionsView.PROP_SUGG_CAT);
            p.setDisplayName(getCategoryLabel()); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SuggestionNode.class, "CategoryHint")); // NOI18N
            ss.put(p);


        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }

    static String getCategoryLabel() {
        return NbBundle.getMessage(SuggestionNode.class, "Category"); // NOI18N
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

    /** Don't allow pastes */
    protected void createPasteTypes(Transferable t, List s) {
    }


    /** Get a cookie. Call super first, but if null, also
     * check the data object associated with the line number
     * if any.
     * @todo Should this be done in TaskNode (for all tasklist
     * tasks) or just here?
     */
    public Node.Cookie getCookie(Class cl) {
        Node.Cookie c = super.getCookie(cl);
        if (c != null) {
            return c;
        }
        if (cl.isAssignableFrom(Suggestion.class)) {
            return (SuggestionImpl)item;
        }
        Line l = item.getLine();
        if (l != null) {
            if (l.getDataObject() != null)
                return l.getDataObject().getCookie(cl);
            else
                return null;
        }
        return null;
    }
}

