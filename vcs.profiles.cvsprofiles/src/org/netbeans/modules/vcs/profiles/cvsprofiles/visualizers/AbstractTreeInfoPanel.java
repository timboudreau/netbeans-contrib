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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.lang.reflect.Method;
import org.openide.util.NbBundle;
import org.openide.awt.SplittedPanel;
import javax.accessibility.*;

import org.netbeans.modules.vcscore.util.table.*;


/** 
 * Richard Gregor
 *
 */
public abstract class AbstractTreeInfoPanel extends javax.swing.JPanel implements TreeCellRenderer {    
    protected  String DEFAULT_FOLDER = "/org/openide/loaders/defaultFolder.gif"; // NOI18N
    protected  String DEFAULT_OPEN_FOLDER = "/org/openide/loaders/defaultFolderOpen.gif"; // NOI18N
    protected  String DEFAULT_FILE = "/org/openide/resources/defaultNode.gif"; // NOI18N
    
    protected File topDirectory;
    private ArrayList files;
    private ArrayList filesBackup;
    protected TreeCellRenderer insideTreeRenderer;
    private boolean treeDisabled;
    
  private javax.swing.JPanel pnlStatus;
  private javax.swing.JPanel pnlTree;
  private javax.swing.JPanel pnlButtons;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTree trDirStructure;
  private javax.swing.JScrollPane jScrollPane2;
  private SplittedPanel split;
  private JComponent component;
  protected javax.swing.JTable tblTable;
    
    /** Creates new form StatusTreeInfoPanel */
    public AbstractTreeInfoPanel(File topDir) {
        this();        
        topDirectory = topDir;
        insideTreeRenderer = new DefaultTreeCellRenderer();
        trDirStructure.setCellRenderer(this);
        trDirStructure.putClientProperty("JTree.lineStyle", "Angled"); // NOI18N
        treeDisabled = false;
    }
    
    public void postInit() {
        component = initPanel();
        pnlStatus.add(component, BorderLayout.CENTER);
        split.setSplitDragable(true);
        split.setSplitAbsolute(false);
        split.setSplitTypeChangeEnabled(true);
        split.setSplitPosition(30);
    }
    
    public void resetPanel(JComponent comp){
        pnlStatus.remove(component);
        pnlStatus.add(comp,BorderLayout.CENTER);
        split.setSplitDragable(true);
        split.setSplitAbsolute(false);
        split.setSplitTypeChangeEnabled(true);
        split.setSplitPosition(30);
    }
    
    /** the same as AbstractTreeInfoPanel(File topDir) but it disables the tree.
     */
    public AbstractTreeInfoPanel() {
        initComponents ();
        split = new SplittedPanel();
        split.add(initTree(),SplittedPanel.ADD_LEFT);
        split.add(initRightPanel(),SplittedPanel.ADD_RIGHT);
        add(split, BorderLayout.CENTER);
        tblTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() { 
            public void valueChanged(ListSelectionEvent e) {
                tblTableValueChanged(e);
            }
        });
        treeDisabled = true;
        getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel"));//NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel"));//NOI18N
        tblTable.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.table"));//NOI18N
        tblTable.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.table"));//NOI18N
    } 
    
    private JComponent initTree() {
        pnlTree = new javax.swing.JPanel();
        pnlTree.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.pnlTree"));//NOI18N
        pnlTree.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.pnlTree"));//NOI18N
        pnlButtons = new javax.swing.JPanel();
        pnlButtons.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.pnlButtons"));//NOI18N
        pnlButtons.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.pnlButtons"));//NOI18N
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane1.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.tab"));//NOI18N
        jTabbedPane1.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.tab"));//NOI18N
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.scroll"));//NOI18N
        jScrollPane1.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.scroll"));//NOI18N
        trDirStructure = new javax.swing.JTree();
        trDirStructure.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.tree"));//NOI18N
        trDirStructure.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.tree"));//NOI18N
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane2.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.scroll"));//NOI18N
        jScrollPane2.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.scroll"));//NOI18N
        tblTable = new javax.swing.JTable();
        tblTable.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.table"));//NOI18N
        tblTable.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.table"));//NOI18N
        pnlTree.setLayout(new java.awt.BorderLayout());
        pnlTree.setPreferredSize(new java.awt.Dimension(200, 300));
        pnlTree.setMinimumSize(new java.awt.Dimension(200, 300));
        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.X_AXIS));
        pnlTree.add(pnlButtons, java.awt.BorderLayout.NORTH);
        jTabbedPane1.setTabPlacement(javax.swing.SwingConstants.BOTTOM);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(500, 400));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 70));
        trDirStructure.setShowsRootHandles(true);
        trDirStructure.setMinimumSize(new java.awt.Dimension(80, 60));
        trDirStructure.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                trDirStructureValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(trDirStructure);
        String treeTitle = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.treeTitle"); //NOI18N
        jTabbedPane1.addTab(treeTitle, jScrollPane1); 
        tblTable.setModel(new javax.swing.table.DefaultTableModel (
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4" // NOI18N
        }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            
            public Class getColumnClass (int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblTable);
        String tableTitle = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.tableTitle"); //NOI18N
        jTabbedPane1.addTab(tableTitle, jScrollPane2);
        pnlTree.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        return pnlTree;
    }
    
    public JComponent initRightPanel() {
        pnlStatus = new javax.swing.JPanel();
        pnlStatus.setLayout(new java.awt.BorderLayout());
        pnlStatus.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.statusPanel")); //NOI18N
        pnlStatus.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.statusPanel")); //NOI18N
        return pnlStatus;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents

  private void lstTableValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstTableValueChanged
// Add your handling code here:
/*      int index = evt.getFirstIndex();
 //     System.out.println("selected index=" + index);
      FileInfoContainer info = (FileInfoContainer) lstTable.getModel().getElementAt(index);
      if (info != null) {
          setPanel(info);
      }
 */
  }//GEN-LAST:event_lstTableValueChanged

  private void tblTableValueChanged(javax.swing.event.ListSelectionEvent evt) {
//      int index = evt.getFirstIndex();
      int index = tblTable.getSelectedRow();
      TableInfoModel model = (TableInfoModel)tblTable.getModel();
      FileInfoContainer info = (FileInfoContainer) model.getElementAt(index);
      if (info != null) {
         setPanel(info);
      } else {
          setClearPanel();
      }    
  }    
  
  
  private void trDirStructureValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_trDirStructureValueChanged
      // Add your handling code here:
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
      Object userObj = node.getUserObject();
      if (userObj instanceof File) {
          setClearPanel();
      } else {
          setPanel(userObj);
      }
  }//GEN-LAST:event_trDirStructureValueChanged
  
  private void dirStructureValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_dirStructureValueChanged
      
  }//GEN-LAST:event_dirStructureValueChanged
  
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
  
  /** Use for command that don't have a common root. Ex. commit MessageChooser
   */
  protected void disableTree() {
      treeDisabled = true;
      //TODO.. remove the tree tab.
      jTabbedPane1.remove(jScrollPane1);
      this.repaint();
  }    
  protected boolean isTreeDisabled() {
      return treeDisabled;
  }
  
  protected JPanel getButtonPanel() {
      return pnlButtons;
  }
  
  protected void recreateModel() {
      files = new ArrayList(filesBackup);
      tblTable.clearSelection();
      TableInfoModel model = (TableInfoModel)createTable(); 
      tblTable.setModel(model);
      if (!isTreeDisabled()) {
          // !!!needs to be after table setup, because it deletes the list
          trDirStructure.setModel(new DefaultTreeModel(createTree(topDirectory)));
      }    
      
  }
  
  protected abstract void setPanel(Object infoData);
  protected abstract void setClearPanel();
  protected abstract JComponent initPanel();
  
  /** to be overidden in case more than the filemane is to be displaed in the Table
   *  it needs to be a tablemodel implementing the CommandTableModel methods
   */
  protected TableInfoModel createTableModel() {
      TableInfoModel model = new TableInfoModel();
      Class classa = FileInfoContainer.class;
      String  column1 = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("TableInfoModel.fileName"); // NOI18N
        try {
            Method method1 = classa.getMethod("getFile", null);     // NOI18N
//            model.setColumnDefinition(0, column1, method1, true, new FileComparator(topDirectory.getAbsolutePath()));
            model.setColumnDefinition(0, column1, method1, true, new FileComparator());            
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
      return model;
  }    
  
  private DefaultMutableTreeNode createTree(File root) {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
      recursiveTreeNodes(rootNode); // created directories
      addFiles(rootNode);
      return rootNode;
  }
  
  private TableModel createTable() {
      TableInfoModel model = createTableModel();
      Iterator it = files.iterator();
      while (it.hasNext()) {
          FileInfoContainer info = (FileInfoContainer)it.next();
          if (addToList(info)) {
              model.addElement(info);
          }
      }
      return (TableModel)model;
  }
  
    /** creates subdirectory structure for the directory returned in the status command
     */
  private void recursiveTreeNodes(DefaultMutableTreeNode parent) {
      File parentFile = (File)parent.getUserObject();
      DefaultMutableTreeNode child;
      File childFile;
      boolean hasChild = false;
      File[] list = parentFile.listFiles();
      for (int index = 0; index < list.length; index++) {
          if (list[index].isDirectory()) {
              childFile = list[index];
              if (!childFile.getName().equals("CVS")) { //CVS dirs go out..  // NOI18N
                  hasChild = true;
                  child = new DefaultMutableTreeNode(new File(childFile.getAbsolutePath()));
                  parent.add(child);
                  recursiveTreeNodes(child);
              }
          }
      }
  }
  
  private void addFiles(DefaultMutableTreeNode parent) {
      if (parent.getChildCount() > 0) { // first do recursively for all children -> bottom-up filling
          Enumeration enum = parent.children();
          while (enum.hasMoreElements()) {
              DefaultMutableTreeNode childDir = (DefaultMutableTreeNode)enum.nextElement();
              addFiles(childDir);
          }
      }
      // now add all statuses for this directory
      if (files == null) return;
      Iterator it = files.iterator();
      File parFile = (File)parent.getUserObject();
      String parPath = parFile.getAbsolutePath();
      while (it.hasNext()) {
          FileInfoContainer info = (FileInfoContainer)it.next();
          String path = info.getFile().getParentFile().getAbsolutePath();
          if (path.equals(parPath)) {
              // this method can be overriden by children for tree  structure filtering
              addFileNode(info, parent);
              it.remove();
          }
      }
  }
  
  /** defines which InfoContainer instances will be added to the tree
   *  By default adds all, can be overriden by subclasses to define filtering
   */
  protected void addFileNode(FileInfoContainer info, DefaultMutableTreeNode parent) {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(info);
      parent.add(child);
  }
  
  /** defines which InfoContainer instances will be added to the table
   *  By default adds all, can be overriden by subclasses to define filtering
   */
  
  protected boolean addToList(FileInfoContainer info) {
      return true;
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, 
                                                    boolean sel, boolean expanded, 
                                                    boolean leaf, int row, boolean hasFocus) {
      Component comp = insideTreeRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (comp instanceof JLabel) {
          JLabel label = (JLabel) comp;
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
          if (node != null) {
              Object userObj = node.getUserObject();
              if (userObj != null) {
                  if (userObj instanceof File) { // it is a directory
                      label.setText(((File)userObj).getName());
                      if (!expanded) {
                          java.net.URL url1 = this.getClass().getResource(DEFAULT_FOLDER);
                          label.setIcon(new ImageIcon(url1));
                      } else {
                          java.net.URL url2 = this.getClass().getResource(DEFAULT_OPEN_FOLDER);
                          label.setIcon(new ImageIcon(url2));
                      }
                  } else if (userObj instanceof FileInfoContainer) { //is File
                      FileInfoContainer info = (FileInfoContainer)userObj;
                      label.setText(info.getFile().getName());
                      java.net.URL url3 = this.getClass().getResource(DEFAULT_FILE);
                      label.setIcon(new ImageIcon(url3));
                  }
              }
          }
          
      }
      return comp;
  }
  
  /** in this method the displayer should use the data returned by the command to
 * produce it's own data structures/ fill in UI components
 * @param resultList - the data from the command. It is assumed the Displayer 
 * knows what command the data comes from and most important in what format. 
 * (which FileInfoContainer class is used).
 */
  public void setDataToDisplay(Collection resultList) {
      filesBackup = new ArrayList(resultList);
      recreateModel();
        // sets up the sorting listener
      JTableHeader head = tblTable.getTableHeader();
      head.setUpdateTableInRealTime(true);
      ColumnSortListener listen = new ColumnSortListener(tblTable);
      head.addMouseListener(listen);
  }
  
  /** Does the actual display - docking into the javacvs Mode, 
 *  displaying as single Dialog.. whatever.
 */
  public void displayOutputData(int moment,Object data) {
      //TODO in subclasses.
  }
  
  public void closeNotify() {
      this.files = null;
      this.filesBackup = null;
      if (tblTable.getModel() instanceof TableInfoModel) {
          TableInfoModel model = (TableInfoModel)tblTable.getModel();
          model.clear();
      }
      // now it's needed to reset all user objects in the tree since I don't know how to get rid of the nodes generally.. they seem to hang around.. 
      // so at least we discard the fileinfo containers..
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)trDirStructure.getModel().getRoot();
      root.setUserObject(null);
      Enumeration enum = root.depthFirstEnumeration();
      while (enum.hasMoreElements()) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)enum.nextElement();
          node.setUserObject(null);
      }
 //     trDirStructure.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
  }  

}
