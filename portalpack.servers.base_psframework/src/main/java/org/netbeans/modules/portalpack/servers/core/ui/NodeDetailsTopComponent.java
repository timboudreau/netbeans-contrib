/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.servers.core.ui;

import java.awt.BorderLayout;
import java.io.Serializable;
import javax.swing.JLabel;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.ChoiceView;
import org.openide.explorer.view.ContextTreeView;
import org.openide.explorer.view.IconView;
import org.openide.explorer.view.ListView;
import org.openide.explorer.view.MenuView;
import org.openide.explorer.view.NodeTableModel;
import org.openide.explorer.view.NodeTreeModel;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
public final class NodeDetailsTopComponent extends TopComponent implements ExplorerManager.Provider {
    
    private transient ExplorerManager explorerManager = new ExplorerManager();
    private TreeTableView treeTableView;
    private MenuView menuView;
    private IconView iconView;
    private ChoiceView choiceView;
    private ListView listView;
    private BeanTreeView beanTreeView;
    private ContextTreeView contextTreeView;
    private NodeTableModel nodeTableModel;
    private NodeTreeModel nodeTreeModel;
    
    private static NodeDetailsTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "NodeDetailsTopComponent";
    
    private NodeDetailsTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(NodeDetailsTopComponent.class, "CTL_NodeDetailsTopComponent"));
        setToolTipText(NbBundle.getMessage(NodeDetailsTopComponent.class, "HINT_NodeDetailsTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
    }
    
    public void setRootNode(Node rootNode) {
        this.removeAll();
        
        if (rootNode != null) {
            
            explorerManager.setRootContext(rootNode);
            explorerManager.getRootContext().setDisplayName("Marilyn Monroe's Movies");
            
            //Use the following four lines for Class TreeTableView
            //and then add "treeTableView" to the "add" method that follows:
            nodeTableModel = new NodeTableModel();
            nodeTableModel.setNodes(rootNode.getChildren().getNodes());
            treeTableView = new TreeTableView(nodeTableModel);
            treeTableView.setRootVisible(false);
            
            //Use this line for Class MenuView 
            //and then add "menuView" to the "add" method that follows:
            //menuView = new MenuView();
            
            //Use this line for Class IconView 
            //and then add "iconView" to the "add" method that follows:
            //iconView = new IconView();
            
            //Use this line for Class ChoiceView 
            //and then add "choiceView" to the "add" method that follows:
            //choiceView = new ChoiceView();
            
            //Use this line for Class ListView 
            //and then add "listView" to the "add" method that follows:
            //listView = new ListView();
            
            //Use this line for Class ContextTreeView 
            //and then add "contextTreeView" to the "add" method that follows:
            //contextTreeView = new ContextTreeView();
            
            //Use the following 2 lines for Class BeanTreeView 
            //and then add "beanTreeView" to the "add" method that follows:
            //beanTreeView = new BeanTreeView();
            //beanTreeView.setRootVisible(false);
            
            add(treeTableView, BorderLayout.CENTER);
            
            
        } else {
            add(new JLabel("Invalid Item Data"), BorderLayout.CENTER);
        }
    }
    
    
    
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized NodeDetailsTopComponent getDefault() {
        if (instance == null) {
            instance = new NodeDetailsTopComponent();
        }
        return instance;
    }
    
    public static synchronized NodeDetailsTopComponent findInstance(Node root)
    {
        NodeDetailsTopComponent component = findInstance();
        component.setRootNode(root);
        return component;
    }
    
    /**
     * Obtain the NodeDetailsTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized NodeDetailsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find NodeDetails component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof NodeDetailsTopComponent) {
            return (NodeDetailsTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
         return explorerManager;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return NodeDetailsTopComponent.getDefault();
        }
    }
    
}
