/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.profiler.data.FunctionContainer;
import org.netbeans.modules.cnd.profiler.models.FunctionNode;
import org.netbeans.modules.cnd.profiler.models.FunctionNodeModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.Node;

/**
 *
 * @author eu155513
 */
public abstract class FunctionViewPanel extends JPanel implements ExplorerManager.Provider {
    private final TreeTableView treeTable = new TreeTableView();
    private final ExplorerManager explorerManager = new ExplorerManager();
    
    private final FunctionNodeModel nodeModel;
    
    private final Column[] columns = new Column[] {Column.createNameColumn(), Column.createTimeColumn(), Column.createSelfTimeColumn()};
    
    protected FunctionViewPanel(FunctionNodeModel nodeModel) {
        this.nodeModel = nodeModel;
        
        setLayout(new BorderLayout());
        add(treeTable, BorderLayout.CENTER);

        //getExplorerManager().setRootContext(new FunctionNode.RootNode(new FunctionContainer[0], columns, nodeModel));
        treeTable.setProperties(columns);
        treeTable.setRootVisible(false);
    }
    
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public boolean requestFocusInWindow () {
        super.requestFocusInWindow ();
        return treeTable.requestFocusInWindow ();
    }
    
    @Override
    public void addNotify () {
        super.addNotify ();
        //TopComponent.getRegistry().addPropertyChangeListener (this);
        //ExplorerUtils.activateActions(getExplorerManager(), true);
        //getExplorerManager().addPropertyChangeListener(this);
    }
    
    @Override
    public void removeNotify () {
        //TopComponent.getRegistry().removePropertyChangeListener (this);
        //ExplorerUtils.activateActions(getExplorerManager(), false);
        //getExplorerManager().removePropertyChangeListener (this);
        super.removeNotify();
    }
    
    public void setRoot(FunctionContainer[] functions) {
        final Node root = new FunctionNode.RootNode(functions, columns, nodeModel);
        getExplorerManager().setRootContext(root);
        // somehow root node may not expand automatically
        // TODO: find the reason, here is a workaround:
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                treeTable.expandNode(root);
            }
        });
    }
}
