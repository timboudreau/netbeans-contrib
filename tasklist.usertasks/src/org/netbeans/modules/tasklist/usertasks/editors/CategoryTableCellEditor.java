/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.AbstractCellEditor;
import org.openide.util.NbBundle;

/**
 * TableCellEditor for the category
 */
public class CategoryTableCellEditor extends DefaultCellEditor {
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public CategoryTableCellEditor() {
        super(new JComboBox(new String[] {
            NbBundle.getMessage(CategoryTableCellEditor.class, "Bug"), // NOI18N
            NbBundle.getMessage(CategoryTableCellEditor.class, "Enhancement"), // NOI18N
            NbBundle.getMessage(CategoryTableCellEditor.class, "Feature") // NOI18N
        }));
        ((JComboBox) editorComponent).setEditable(true);
    }
}
