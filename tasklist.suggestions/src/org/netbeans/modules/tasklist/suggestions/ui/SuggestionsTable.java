package org.netbeans.modules.tasklist.suggestions.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.suggestions.SuggestionImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionNode;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.text.Line;

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
                            Suggestion s = 
                                ((SuggestionsTableModel) getModel()).getSuggestion(row);
                            SuggestionNode sn = new SuggestionNode(
                                (SuggestionImpl) s);
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
    }
}
