package org.netbeans.modules.tasklist.suggestions.ui;

import java.awt.Image;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.tasklist.client.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * A table model for suggestions.
 *
 * @author tl
 */
public class SuggestionsTableModel extends AbstractTableModel 
        implements ListDataListener {
    /**
     * Columns
     */
    public static enum Columns {
        SUMMARY, PRIORITY, DETAILS, FILE, LINE, TYPE
    };
    
    private static final String[] COLUMNS = {
        NbBundle.getMessage(SuggestionsTableModel.class, "SummaryCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "PriorityCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "DetailsCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "FileCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "LineCol"), // NOI18N
        NbBundle.getMessage(SuggestionsTableModel.class, "TypeCol") // NOI18N
    };
    
    private Suggestion[] list;

    private Class<?>[] COLUMN_CLASSES = new Class[] {
        String.class,
        SuggestionPriority.class,
        String.class,
        String.class,
        Integer.class,
        Image.class,
    };
    
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
                return sug.getPriority();
            case 2:
                return sug.getDetails();
            case 3:
                FileObject fo = sug.getFileObject();
                if (fo == null) {
                    Line l = sug.getLine();
                    DataObject dobj = l.getLookup().lookup(DataObject.class);
                    if (dobj == null)
                        return null;
                    else
                        return dobj.getPrimaryFile().getNameExt();
                }
                return fo.getNameExt();
            case 4:
                return sug.getLine().getLineNumber() + 1;
            case 5:
                return sug;
            default:
                return null;
        }
    }

    public int getRowCount() {
        return list.length;
    }

    public int getColumnCount() {
        return 6;
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
    }

    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    public void intervalRemoved(ListDataEvent e) {
        list = StaticSuggestions.getDefault().getAll();
        fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
    }

    public void intervalAdded(ListDataEvent e) {
        list = StaticSuggestions.getDefault().getAll();
        fireTableRowsInserted(e.getIndex0(), e.getIndex1());
    }

    public void contentsChanged(ListDataEvent e) {
        list = StaticSuggestions.getDefault().getAll();
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }
}
