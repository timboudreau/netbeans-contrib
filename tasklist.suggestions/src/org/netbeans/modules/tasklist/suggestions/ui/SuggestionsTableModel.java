package org.netbeans.modules.tasklist.suggestions.ui;

import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.tasklist.client.*;
import org.netbeans.modules.tasklist.suggestions.SuggestionManagerImpl;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;

import java.util.List;

/**
 * A table model for suggestions.
 */
public class SuggestionsTableModel extends AbstractTableModel implements TaskListener {
    private static final String[] COLUMNS = {
        NbBundle.getMessage(SuggestionsTableModel.class, "SummaryCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "PriorityCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "IconCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "LineCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "FileCol") // NOI18N
    };
    
    private TaskList list;
    private List suggestions;
    
    /**
     * Creates a new instance of SuggestionsTableMode
     */
    public SuggestionsTableModel() {
        SuggestionManagerImpl sm = (SuggestionManagerImpl) SuggestionManager.getDefault();
        list = sm.getList();
        list.addTaskListener(this);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Suggestion sug = getSuggestion(rowIndex);
        switch (columnIndex) {
            case 0:
                return sug.getSummary();
            case 1:
                return sug.getPriority();
            case 2:
                return sug.getIcon();
            case 3:
                Line line = sug.getLine();
                if (line == null)
                    return null;
                else
                    return new Integer(line.getLineNumber());
            case 4:
                FileObject fo = sug.getFileObject();
                if (fo == null)
                    return null;
                return fo.getNameExt();
            default:
                return null;
        }
    }

    public int getRowCount() {
        return TLUtils.recursiveCount(list.getTasks().iterator());
    }

    public int getColumnCount() {
        return 5;
    }

    public String getColumnName(int column) {
       return COLUMNS[column];
    }
    
    /**
     * Returns the suggestion object for the specified row.
     *
     * @param row row number
     * @return Suggestion
     */
    public Suggestion getSuggestion(int row) {
        if (suggestions == null) {
            suggestions = list.getTasks();
        }
        return (Suggestion) suggestions.get(row);
    }

    public void selectedTask(Task t) {
    }

    public void warpedTask(Task t) {
    }

    public void addedTask(Task t) {
        suggestions = null;
        fireTableDataChanged();
    }

    public void removedTask(Task pt, Task t, int index) {
        suggestions = null;
        fireTableDataChanged();
    }

    public void structureChanged(Task t) {
        suggestions = null;
        fireTableDataChanged();
    }
}
