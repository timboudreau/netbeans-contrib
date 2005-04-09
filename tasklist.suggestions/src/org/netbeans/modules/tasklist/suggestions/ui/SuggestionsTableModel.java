package org.netbeans.modules.tasklist.suggestions.ui;

import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.tasklist.client.*;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * A table model for suggestions.
 */
public class SuggestionsTableModel extends AbstractTableModel implements
ChangeListener {
    private static final String[] COLUMNS = {
        NbBundle.getMessage(SuggestionsTableModel.class, "SummaryCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "PriorityCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "IconCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "LineCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "FileCol") // NOI18N
    };
    
    private SuggestionManager sm;
    private Suggestion[] s;
    
    /**
     * Creates a new instance of SuggestionsTableMode
     */
    public SuggestionsTableModel() {
        sm = SuggestionManager.getDefault();
        sm.addChangeListener(this);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (s == null)
            s = sm.getSuggestions();
        Suggestion sug = s[rowIndex];
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
                FileObject fo = s[rowIndex].getFileObject();
                if (fo == null)
                    return null;
                return fo.getNameExt();
            default:
                return null;
        }
    }

    public int getRowCount() {
        if (s == null)
            s = sm.getSuggestions();
        return s.length;
    }

    public int getColumnCount() {
        return 5;
    }

    public void stateChanged(javax.swing.event.ChangeEvent e) {
        s = null;
        fireTableDataChanged();
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
        if (s == null)
            s = sm.getSuggestions();
        return s[row];
    }
}
