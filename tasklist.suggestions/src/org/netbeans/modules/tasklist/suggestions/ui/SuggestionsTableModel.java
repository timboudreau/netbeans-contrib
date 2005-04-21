package org.netbeans.modules.tasklist.suggestions.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.tasklist.client.*;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;


/**
 * A table model for suggestions.
 */
public class SuggestionsTableModel extends AbstractTableModel implements ChangeListener {
    private static final String[] COLUMNS = {
        NbBundle.getMessage(SuggestionsTableModel.class, "SummaryCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "FileCol") // NOI18N
    };
    
    private Suggestion[] list;
    
    /**
     * Creates a new instance of SuggestionsTableMode
     */
    public SuggestionsTableModel() {
        StaticSuggestions sm = StaticSuggestions.getDefault();
        list = sm.getAll();
        sm.addListener(this);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Suggestion sug = getSuggestion(rowIndex);
        switch (columnIndex) {
            case 0:
                return sug.getSummary();
            case 1:
                FileObject fo = sug.getFileObject();
                if (fo == null)
                    return null;
                return fo.getNameExt();
            default:
                return null;
        }
    }

    public int getRowCount() {
        return list.length;
    }

    public int getColumnCount() {
        return 2;
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
        return list[row];
    }

    public void stateChanged(ChangeEvent e) {
        list = StaticSuggestions.getDefault().getAll();
        fireTableDataChanged();
    }
}
