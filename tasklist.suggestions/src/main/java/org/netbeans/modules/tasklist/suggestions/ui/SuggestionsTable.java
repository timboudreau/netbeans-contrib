package org.netbeans.modules.tasklist.suggestions.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.suggestions.SuggestionImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionNode;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.Utilities;

/**
 * A table for suggestions.
 *
 * @author tl
 */
public class SuggestionsTable extends JTable {
    /**
     * Creates a new instance of SuggestionsTable
     */
    public SuggestionsTable() {
        super(new SuggestionsTableModel());
        
        addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                Action[] actions;
                if (row < 0 || col < 0)
                    return;

                if (!getSelectionModel().isSelectedIndex(row)) {
                    setRowSelectionInterval(row, row);
                }
                Node n = createNode(row);

                actions = n.getActions(false);

                JPopupMenu pm = Utilities.actionsToPopup(actions,
                    SuggestionsTable.this);
                if(pm != null)
                    pm.show(SuggestionsTable.this, e.getX(), e.getY());
            }
        });
        
        getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                    int row = getSelectedRow();
                    ExplorerManager em = ExplorerManager.find(
                        SuggestionsTable.this);
                    if (em == null)
                        return;
                    try {
                        if (row < 0)
                            em.setSelectedNodes(new Node[0]);
                        else {
                            Node sn = createNode(row);
                            em.setRootContext(sn);
                            em.setSelectedNodes(new Node[] {sn});
                        }
                    } catch (PropertyVetoException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        );
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2)
                    return;
                JTable tt = (JTable) e.getSource();
                int row = tt.rowAtPoint(e.getPoint());
                if (row < 0)
                    return;
                Suggestion s = 
                    ((SuggestionsTableModel) tt.getModel()).getSuggestion(row);
                Line l = s.getLine();
                if (l != null)
                    l.show(Line.SHOW_GOTO);
            }
        });
        
        setAutoResizeMode(AUTO_RESIZE_OFF);
        
        TableColumn tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.TYPE.ordinal());
        tc.setCellRenderer(new CategoryTableCellRenderer());
        tc.setPreferredWidth(100);
        
        tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.PRIORITY.ordinal());
        tc.setPreferredWidth(60);
        tc.setCellRenderer(new PriorityTableCellRenderer());

        tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.SUMMARY.ordinal());
        tc.setPreferredWidth(400);
        
        tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.DETAILS.ordinal());
        tc.setPreferredWidth(300);
        
        tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.FILE.ordinal());
        tc.setPreferredWidth(200);
        
        tc = getColumnModel().getColumn(
                SuggestionsTableModel.Columns.LINE.ordinal());
        tc.setPreferredWidth(40);
    }
    
    /**
     * Creates a Node for the specified row.
     *
     * @param row row number
     * @return Node for this row
     */
    private Node createNode(int row) {
        Suggestion s = 
            ((SuggestionsTableModel) getModel()).getSuggestion(row);
        return new SuggestionNode((SuggestionImpl) s);
    }
}
