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
final class SourceTaskNode extends AbstractNode {

    private Node item;
    private String summary;

    // cached leaf
    SourceTaskNode(String summary, String file, int line, int prio) {
        super(Children.LEAF);
        this.summary = summary;
        item = this;
        setIconBase("org/netbeans/modules/tasklist/docscan/scanned-task"); // NOI18N
    }

//    // Leaf
//    SourceTaskNode(SourceTask item) {
//        super(item);
//        init(item);
//    }
//
//    /**
//     * Entry point
//     * @param item
//     * @param subtasks
//     */
//    SourceTaskNode(SourceTask item, List subtasks) {
//        super(item, subtasks);
//        init(item);
//    }
//
//    private void init(SourceTask item) {
//        this.item = item;
//        setIconBase("org/netbeans/modules/tasklist/docscan/scanned-task"); // NOI18N
//    }

    public Action getPreferredAction() {
        return SystemAction.get(GoToTaskAction.class);
    }

    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
//    public Node cloneNode() {
//        SourceTask eitem = (SourceTask) item;
//        if (eitem.hasSubtasks()) {
//            return new SourceTaskNode(eitem, eitem.getSubtasks());
//        } else {
//            return new SourceTaskNode(eitem);
//        }
//    }

    public Action[] getActions(boolean context) {
        if (context == true) {
            // root node actions accestible on blank pane
            return new SystemAction[]{
                SystemAction.get(FilterAction.class),
            };
        } else {
            return super.getActions(context);
        }
    }

    protected SystemAction[] createActions() {
        ArrayList actions = new ArrayList(3);
        actions.add(SystemAction.get(GoToTaskAction.class));

        // No global actions should be on node, move them to toolbar
        // however contextual filter action should be added etc.
        // "Global" (not node specific) actions

//            actions.add(null);
//            actions.add(SystemAction.get(ShowCategoryAction.class));
//            actions.add(SystemAction.get(EditTypesAction.class));
//            actions.add(SystemAction.get(DisableAction.class));
//            actions.add(null);
//            actions.add(SystemAction.get(FilterAction.class));
//            actions.add(SystemAction.get(ExpandAllAction.class));
//            actions.add(null);
//            actions.add(SystemAction.get(ExportAction.class));

        // Property: node specific, but by convention last in menu
        actions.add(null);
        actions.add(SystemAction.get(PropertiesAction.class));

        return (SystemAction[]) actions.toArray(
                new SystemAction[actions.size()]);
    }

    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        if (item.getParentNode() == null)
            return s;
        Sheet.Set ss = s.get(Sheet.PROPERTIES);

        try {
            PropertySupport.Reflection p;
            p = new PropertySupport.Reflection(item, String.class, "getSummary", null /* Don't allow users to edit this "setDescription" */); // NOI18N
            p.setName(TaskListView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(SourceTaskNode.class, "SuggestionsRoot")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SourceTaskNode.class, "SuggestionsRootHint")); // NOI18N
            ss.put(p);


            p = new PropertySupport.Reflection(item, SuggestionPriority.class,
                    "getPriority", null); // NOI18N
            p.setName(SourceTasksView.PROP_SUGG_PRIO);
            p.setPropertyEditorClass(PriorityPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(SourceTaskNode.class, "Priority")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SourceTaskNode.class, "PriorityHint")); // NOI18N
            ss.put(p);


            p = new PropertySupport.Reflection(item, String.class, "getFileBaseName", null /* Don't allow users to edit this! "setFileBaseName" */); // NOI18N
            p.setName(SourceTasksView.PROP_SUGG_FILE);
            p.setDisplayName(NbBundle.getMessage(SourceTaskNode.class, "File")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SourceTaskNode.class, "FileHint")); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getLineNumber", null /* Don't allow users to edit this! "setLineNumber" */); // NOI18N
            p.setName(SourceTasksView.PROP_SUGG_LINE);
            p.setPropertyEditorClass(LineNumberPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(SourceTaskNode.class, "Line")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(SourceTaskNode.class, "LineHint")); // NOI18N
            ss.put(p);

        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }

    // experiment how does it work when restoring visualization
    // layer from cache

    public String getFileBaseName() {
        return "<from cache>";
    }

    public int getLineNumber() {
        return -1;
    }

    public int getPriority() {
        return 1;
    }

    public String getSummary() {
        return summary;
    }

    // /experiment

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
    public boolean canCopy() {
        // Can't copy the root node:
        return (item.getParentNode() != null);
    }

    /** Can this node be cut?
     * @return <code>false</code>
     */
    public boolean canCut() {
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
//    public Node.Cookie getCookie(Class cl) {
//        Node.Cookie c = super.getCookie(cl);
//        if (c != null) {
//            return c;
//        }
//        if (cl.isAssignableFrom(Suggestion.class)) {
//            return (SuggestionImpl) item;  // FIXME wrong dependency, you can use lookup instead
//        }
//        Line l = item.getLine();
//        if (l != null) {
//            DataObject dao = DataEditorSupport.findDataObject(l);
//            if (dao != null)
//                return dao.getCookie(cl);
//            else
//                return null;
//        }
//        return null;
//    }

}
