/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

/*
 * ComponentsEditorPanel.java
 *
 * Created on March 12, 2002, 11:16 AM
 */
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.openide.DialogDescriptor;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import org.openide.nodes.Node;
import org.openide.nodes.BeanNode;
import java.beans.IntrospectionException;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import java.io.InputStream;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.event.KeyEvent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import java.util.Collection;
import java.awt.Component;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeNode;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** class with panel used for edit found components before generation
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class ComponentsEditorPanel extends javax.swing.JPanel implements ChangeListener {
    
    static ImageIcon rootIcon;
    static ImageIcon nodeIcon;
    
    static {
        try {
            rootIcon = new ImageIcon(Utilities.loadImage("org/openide/resources/propertysheet/customize.gif")); // NOI18N
            nodeIcon = new ImageIcon(Utilities.loadImage("org/openide/src/resources/sourceOptions.gif")); // NOI18N
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    Collection nodes;
    JPopupMenu popup;

    static class MyCellRenderer extends DefaultTreeCellRenderer {
        public MyCellRenderer() {
            super();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,hasFocus);
            try {
                Icon icon = ((ComponentGenerator.ComponentRecord)((DefaultMutableTreeNode)value).getUserObject()).getIcon();
                if (icon!=null) 
                    setIcon(icon);
            } catch (Exception e) {};
            return this;
        }

    }
    
    /** Creates new form ComponentsEditorPanel
     * @param gen ComponentGenerator instance */
    public ComponentsEditorPanel(ComponentGenerator gen) {
        TreeNode rootNode=gen.getRootNode();
        this.nodes = gen.getNodes();
        gen.addChangeListener(this);
        initComponents();
        if ((rootIcon!=null)&&(nodeIcon!=null)) {
            MyCellRenderer rend = new MyCellRenderer();
            rend.setClosedIcon(rootIcon);
            rend.setOpenIcon(rootIcon);
            rend.setLeafIcon(nodeIcon);
            tree.setCellRenderer(rend);
        }
        tree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
        tree.setModel(new DefaultTreeModel(rootNode));
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                nodeChanged(tree.getSelectionPaths());
            }
        });
    }
    
    void nodeChanged(TreePath paths[]) {
        if (paths==null) {
            propertySheet.setNodes(new Node[0]);
        } else try {
            Node nodes[]=new Node[paths.length];
            for (int i=0; i<paths.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                nodes[i]=new BeanNode(node.getUserObject());
            }
            propertySheet.setNodes(nodes);
        } catch (IntrospectionException ex) {
            propertySheet.setNodes(new Node[0]);
        }
    }        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        propertySheet = new org.openide.explorer.propertysheet.PropertySheet();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(415);
        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.5);
        splitPane.setPreferredSize(new java.awt.Dimension(800, 400));
        scrollPane.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentsEditorPanel.class, "TTT_ComponentsTree", new Object[] {}));
        tree.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentsEditorPanel.class, "TTT_ComponentsTree", new Object[] {}));
        tree.setShowsRootHandles(true);
        tree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                treeKeyReleased(evt);
            }
        });

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });

        scrollPane.setViewportView(tree);
        tree.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComponentsEditorPanel.class, "LBL_ComponentTree", new Object[] {}));

        splitPane.setLeftComponent(scrollPane);

        propertySheet.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentsEditorPanel.class, "TTT_Properties", new Object[] {}));
        propertySheet.setDisplayWritableOnly(true);
        splitPane.setRightComponent(propertySheet);
        propertySheet.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComponentsEditorPanel.class, "LBL_Properties", new Object[] {}));

        add(splitPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void treeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeKeyReleased
        if ((evt.getKeyCode()==KeyEvent.VK_DELETE)&&(evt.getModifiers()==0)&&(tree.getSelectionCount()>0)&&!tree.isRowSelected(0)) {
            DeleteActionPerformed();
        }
    }//GEN-LAST:event_treeKeyReleased

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        if ((evt.getModifiers()==evt.BUTTON3_MASK)&&(tree.getSelectionCount()>0)&&!tree.isRowSelected(0)) {
            if (popup==null) {
                popup=new JPopupMenu();
                JMenuItem del=new JMenuItem(NbBundle.getMessage(ComponentsEditorPanel.class, "CTL_Delete"));
                del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                del.addActionListener(new java.awt.event.ActionListener() { // NOI18N
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        DeleteActionPerformed();
                    }
                });
                popup.add(del);
            }
            popup.show(tree,evt.getX(),evt.getY());
        } else if (popup!=null) {
            popup.setVisible(false);
        }
    }//GEN-LAST:event_treeMouseClicked
    
    void DeleteActionPerformed() {
        TreePath paths[]=tree.getSelectionPaths();
        for (int i=0; paths!=null&&i<paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
            Enumeration enum=node.postorderEnumeration();
            while (enum.hasMoreElements()) {
                nodes.remove(((DefaultMutableTreeNode)enum.nextElement()).getUserObject());
            }
            ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(node);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    private javax.swing.JScrollPane scrollPane;
    private org.openide.explorer.propertysheet.PropertySheet propertySheet;
    // End of variables declaration//GEN-END:variables
    
    /** shows Component Editor modal dialog
     * @param gen ComponentGenerator instance
     * @return boolean false when operation canceled */    
    public static boolean showDialog(ComponentGenerator gen) {
        DialogDescriptor desc = new DialogDescriptor(new ComponentsEditorPanel(gen), NbBundle.getMessage(ComponentsEditorPanel.class, "ComponentsEditor_Title"), true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null); // NOI18N
        desc.setHelpCtx(new HelpCtx(ComponentsEditorPanel.class));
        org.openide.DialogDisplayer.getDefault().createDialog(desc).show();
        return desc.getValue()==DialogDescriptor.OK_OPTION;
    }
    
    /** implementation of StateListener
     * @param changeEvent ChangeEvent */    
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        DefaultTreeModel model=(DefaultTreeModel)tree.getModel();
        model.nodeChanged(((ComponentGenerator.ComponentRecord)changeEvent.getSource()).getNode());
        nodeChanged(tree.getSelectionPaths());
    }
    
}
