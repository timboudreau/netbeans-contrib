/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.suggestions.SuggestionsView;
import org.netbeans.modules.tasklist.suggestions.SuggestionImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionNode;
import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.api.tasklist.SuggestionPriority;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

/**
 * View containing only source tasks (TODOs).
 *
 * @author Petr Kuzel
 */
final class SourceTasksView extends TaskListView {

    final static String CATEGORY = "sourcetasks"; // NOI18N

    private final int MAIN_COLUMN_UID = 2352;
    private final int PRIORITY_COLUMN_UID = 7896;
    private final int DETAILS_COLUMN_UID = 1098;
    private final int FILE_COLUMN_UID = 8902;
    private final int LINE_COLUMN_UID = 6646;

    //XXX keep with sync with SuggestionNode, hidden dependency
    static final String PROP_SUGG_DETAILS = "suggDetails"; // NOI18N
    static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
    static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
    static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
    static final String PROP_SUGG_CAT = "suggCat"; // NOI18N

    /**
     * Construct a Scaned tasks view with the given window title, and the given
     * list to show the contents in
     * @param name The name of the window
     * @param list The tasklist to store the scanned tasks in
     */
    public SourceTasksView(String name, TaskList list, String icon) {
        super(
                CATEGORY,
                name,
                Utilities.loadImage(icon),
                false,
                list
        );

        assert list instanceof SourceTasksList;

        // When the tab is alone in a container, don't show a tab;
        // the category nodes provide enough feedback.
        putClientProperty("TabPolicy", "HideWhenAlone");
    }

    protected TaskNode createRootNode() {
        SourceTask root = (SourceTask) tasklist.getRoot();
        return new SourceTaskNode(root, root.getSubtasks());
    }

    protected ColumnProperty[] createColumns() {
        // No point allowing other attributes of the task since that's
        // all we support for scan items (they are not created by
        // the user - and they are not persisted.
        return new ColumnProperty[]{
            createMainColumn(800),
            createPriorityColumn(false, 100),
            createDetailsColumn(false, 800),
            createFileColumn(true, 150),
            createLineColumn(true, 50)
        };

    }

    private ColumnProperty createMainColumn(int width) {
        // Tree column
        // NOTE: Task.getDisplayName() must also be kept in sync here
        return new ColumnProperty(
                MAIN_COLUMN_UID, // UID -- never change (part of serialization
                PROP_TASK_SUMMARY,
                NbBundle.getMessage(SuggestionsView.class, "SuggestionsRoot"), // NOI18N
                NbBundle.getMessage(SuggestionsView.class, "SuggestionsRoot"), // NOI18N
                true,
                width
        );
    }


    private ColumnProperty createPriorityColumn(boolean visible, int width) {
        return new ColumnProperty(
                PRIORITY_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_PRIO,
                SuggestionPriority.class,
                NbBundle.getMessage(SuggestionsView.class, "Priority"), // NOI18N
                NbBundle.getMessage(SuggestionsView.class, "PriorityHint"), // NOI18N
                true,
                visible,
                width
        );
    }

    private ColumnProperty createFileColumn(boolean visible, int width) {
        return new ColumnProperty(
                FILE_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_FILE,
                String.class,
                NbBundle.getMessage(SuggestionsView.class, "File"), // NOI18N
                NbBundle.getMessage(SuggestionsView.class, "FileHint"), // NOI18N
                true,
                visible,
                width
        );
    }

    private ColumnProperty createLineColumn(boolean visible, int width) {
        return new ColumnProperty(
                LINE_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_LINE,
                Integer.TYPE,
                NbBundle.getMessage(SuggestionsView.class, "Line"), // NOI18N
                NbBundle.getMessage(SuggestionsView.class, "LineHint"), // NOI18N
                true,
                visible,
                width
        );
    }


    private ColumnProperty createDetailsColumn(boolean visible, int width) {
        return new ColumnProperty(
                DETAILS_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_DETAILS,
                String.class,
                NbBundle.getMessage(SuggestionsView.class, "Details"), // NOI18N
                NbBundle.getMessage(SuggestionsView.class, "DetailsHint"), // NOI18N
                true,
                visible,
                width
        );
    }


    protected void componentOpened() {
        super.componentOpened();
        setNorthComponentVisible(true);
    }

//    protected Component createNorthComponent() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new FlowLayout());
//        panel.add(new JButton("A"));
//        panel.add(new JButton("B"));
//        panel.add(new JButton("C"));
//        panel.add(new JLabel("Status"));
//        return panel;
//    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        // TODO super.writeExternal(objectOutput);
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        // TODO super.readExternal(objectInput);
    }
}
