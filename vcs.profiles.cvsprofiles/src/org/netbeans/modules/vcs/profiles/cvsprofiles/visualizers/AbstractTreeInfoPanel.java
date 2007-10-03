/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import javax.accessibility.*;

import org.netbeans.modules.vcscore.util.table.*;
import org.openide.util.Utilities;


/**
 * Richard Gregor
 *
 */
public abstract class AbstractTreeInfoPanel extends javax.swing.JPanel implements TreeCellRenderer {    
    
    private static final String DEFAULT_FOLDER = "org/openide/loaders/defaultFolder.gif"; // NOI18N
    private static final String DEFAULT_OPEN_FOLDER = "org/openide/loaders/defaultFolderOpen.gif"; // NOI18N
    private static final String DEFAULT_FILE = "org/openide/nodes/defaultNode.gif"; // NOI18N
    
    private static final Image FOLDER_ICON = (Image) UIManager.get("Nb.Explorer.Folder.icon"); // NOI18N
    private static final Image OPEN_FOLDER_ICON = (Image) UIManager.get("Nb.Explorer.Folder.openedIcon"); // NOI18N
    
    private static final Object PLEASE_WAIT = new Object();

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
  private JSplitPane split;
  private JComponent component;
  protected javax.swing.JTable tblTable;
    
    /** Creates new form StatusTreeInfoPanel */
    public AbstractTreeInfoPanel(File topDir) {
        this();        
        topDirectory = topDir;
        setWaitModel();
        insideTreeRenderer = new DefaultTreeCellRenderer();
        trDirStructure.setCellRenderer(this);
        trDirStructure.putClientProperty("JTree.lineStyle", "Angled"); // NOI18N
        treeDisabled = false;
    }
    
    public void postInit() {
        component = initPanel();
        pnlStatus.add(component, BorderLayout.CENTER);
        split.setResizeWeight(0);
        split.setDividerLocation(split.getMinimumDividerLocation());
    }
    
    public void resetPanel(JComponent comp){
        pnlStatus.remove(component);
        pnlStatus.add(comp,BorderLayout.CENTER);
    }
    
    /** the same as AbstractTreeInfoPanel(File topDir) but it disables the tree.
     */
    public AbstractTreeInfoPanel() {
        initComponents ();
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, initTree(), initRightPanel());
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
        tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblTable.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACS_AbstractTreeInfoPanel.table"));//NOI18N
        tblTable.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("ACSD_AbstractTreeInfoPanel.table"));//NOI18N
        pnlTree.setLayout(new java.awt.BorderLayout());
        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.X_AXIS));
        pnlTree.add(pnlButtons, java.awt.BorderLayout.NORTH);
        jTabbedPane1.setTabPlacement(javax.swing.SwingConstants.BOTTOM);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(80, 60));
        trDirStructure.setShowsRootHandles(true);
        trDirStructure.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                trDirStructureValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(trDirStructure);
        String treeTitle = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.treeTitle"); //NOI18N
        jTabbedPane1.addTab(treeTitle, jScrollPane1); 
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
  
  private void setWaitModel() {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(topDirectory);
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(PLEASE_WAIT);
      rootNode.add(child);
      trDirStructure.setModel(new DefaultTreeModel(rootNode));
  }
  
  protected void recreateModel() {
      files = new ArrayList(filesBackup);
      tblTable.clearSelection();
      TableInfoModel model = createTable(); 
      tblTable.setModel(model);
      initRenderers(tblTable, model);
      initColumnSizes(tblTable);
      if (!isTreeDisabled()) {
          // !!!needs to be after table setup, because it deletes the list
          trDirStructure.setModel(new DefaultTreeModel(createTree(topDirectory)));
      }
  }
  
  /**
   * Initialize the cell renderers.
   */
  private static void initRenderers(JTable table, final TableInfoModel model) {
      
      class ToolTipCellRenderer implements TableCellRenderer {
          
          int column;
          private TableCellRenderer parent;
          
          public ToolTipCellRenderer(int column, TableCellRenderer parent) {
              this.column = column;
              if (parent == null) {
                  parent = new DefaultTableCellRenderer();
              }
              this.parent = parent;
          }
          
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
              Component c = parent.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
              if (c instanceof JComponent) {
                  String toolTipText = model.getTooltipTextAt(row, column);
                  if (toolTipText != null) {
                      ((JComponent) c).setToolTipText(toolTipText);
                  }
              }
              return c;
          }
      }
      
      int n = table.getColumnCount();
      for (int i = 0; i < n; i++) {
          TableColumn column = table.getColumnModel().getColumn(i);
          column.setCellRenderer(new ToolTipCellRenderer(i, column.getCellRenderer()));
      }
  }
  
  /**
   * Calculates the suitable preferred column sizes with respect to width of
   * individual cells.
   */
  private static void initColumnSizes(JTable table) {
      TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
      int nc = table.getColumnCount();
      int nr = table.getRowCount();
      for (int i = 0; i < nc; i++) {
          TableColumn column = table.getColumnModel().getColumn(i);
          Component comp = headerRenderer.getTableCellRendererComponent(
                              null, column.getHeaderValue(),
                              false, false, 0, 0);
          int maxWidth = comp.getPreferredSize().width;

          for (int j = 0; j < nr; j++) {
              comp = table.getDefaultRenderer(table.getColumnClass(i)).
                                  getTableCellRendererComponent(
                                      table, table.getValueAt(j, i),
                                      false, false, j, i);
              int cellWidth = comp.getPreferredSize().width;
              if (cellWidth > maxWidth) {
                  maxWidth = cellWidth;
              }
          }
          column.setPreferredWidth(maxWidth + 10); // Add a small inset so that it looks better
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
            Method methodT = classa.getMethod("getToolTipText", null);     // NOI18N
            model.setColumnDefinition(0, column1, method1, true, new FileComparator());            
            model.setColumnToolTipGetter(0, methodT);
        } catch (NoSuchMethodException exc) {
            ErrorManager.getDefault().notify(exc);
        } catch (SecurityException exc2) {
            ErrorManager.getDefault().notify(exc2);
        }
      return model;
  }    
  
  private DefaultMutableTreeNode createTree(File root) {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
      recursiveTreeNodes(rootNode); // created directories
      addFiles(rootNode);
      return rootNode;
  }
  
  private TableInfoModel createTable() {
      TableInfoModel model = createTableModel();
      Iterator it = files.iterator();
      while (it.hasNext()) {
          FileInfoContainer info = (FileInfoContainer)it.next();
          if (addToList(info)) {
              model.addElement(info);
          }
      }
      return model;
  }
  
    /** creates subdirectory structure for the directory returned in the status command
     */
  private void recursiveTreeNodes(DefaultMutableTreeNode parent) {
      File parentFile = (File)parent.getUserObject();
      DefaultMutableTreeNode child;
      File childFile;
      boolean hasChild = false;
      File[] list = parentFile.listFiles();
      java.util.Arrays.sort(list);
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
                  if (userObj == PLEASE_WAIT) {
                      String pleaseWaitText = NbBundle.getMessage(org.netbeans.modules.vcscore.versioning.RevisionChildren.class, "WaitNodeTooltip");
                      label.setText(pleaseWaitText);
                      label.setIcon(new ImageIcon(org.netbeans.modules.vcscore.versioning.RevisionChildren.class.getResource("/org/netbeans/modules/vcscore/versioning/wait.gif")));
                  } else if (userObj instanceof File) { // it is a directory
                      label.setText(((File)userObj).getName());
                      if (!expanded) {
                          if (FOLDER_ICON != null) {
                              label.setIcon(new ImageIcon(FOLDER_ICON));
                          } else {
                              label.setIcon(new ImageIcon(Utilities.loadImage(DEFAULT_FOLDER)));
                          }
                      } else {
                          if (OPEN_FOLDER_ICON != null) {
                              label.setIcon(new ImageIcon(OPEN_FOLDER_ICON));
                          } else {
                              label.setIcon(new ImageIcon(Utilities.loadImage(DEFAULT_OPEN_FOLDER)));
                          }
                      }
                  } else if (userObj instanceof FileInfoContainer) { //is File
                      FileInfoContainer info = (FileInfoContainer)userObj;
                      label.setText(info.getFile().getName());
                      label.setIcon(new ImageIcon(Utilities.loadImage(DEFAULT_FILE)));
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
