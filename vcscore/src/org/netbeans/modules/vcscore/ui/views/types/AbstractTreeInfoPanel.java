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

package org.netbeans.modules.vcscore.ui.views.types;

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

import org.openide.explorer.view.*;


import org.netbeans.modules.vcscore.ui.views.*;
import org.netbeans.modules.vcscore.util.table.*;


/**
 *
 * @author  mkleint
 * @version
 */
public abstract class AbstractTreeInfoPanel extends javax.swing.JPanel {

    protected  String DEFAULT_FOLDER = "/org/openide/loaders/defaultFolder.gif"; // NOI18N
    protected  String DEFAULT_OPEN_FOLDER = "/org/openide/loaders/defaultFolderOpen.gif"; // NOI18N
    protected  String DEFAULT_FILE = "/org/openide/nodes/defaultNode.gif"; // NOI18N
    
//    private ArrayList files;
//    private ArrayList filesBackup;
    protected TreeCellRenderer insideTreeRenderer;
//    private boolean treeDisabled;
    
  private javax.swing.JPanel pnlStatus;
  private javax.swing.JPanel pnlTree;
  private javax.swing.JPanel pnlButtons;
  private javax.swing.JTabbedPane jTabbedPane1;
//  private javax.swing.JScrollPane jScrollPane1;
  private org.openide.explorer.view.TreeView trDirStructure;
//  private javax.swing.JScrollPane jScrollPane2;
  private SplittedPanel split;
  protected javax.swing.JComponent tblTable;

    /** the same as AbstractTreeInfoPanel(File topDir) but it disables the tree.
     */
    public AbstractTreeInfoPanel() {
        initComponents ();
        split = new SplittedPanel();
        split.add(initTree(),SplittedPanel.ADD_LEFT);
        split.add(initRightPanel(),SplittedPanel.ADD_RIGHT);
        add(split, BorderLayout.CENTER);
/*        tblTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() { 
            public void valueChanged(ListSelectionEvent e) {
                tblTableValueChanged(e);
            }
        });
        treeDisabled = true;
 */
    } 
    
    public void postInit() {
        pnlStatus.add(initPanel(), BorderLayout.CENTER);
//        split.setSplitPosition();
 //       split.setSplitType(SplittedPanel.RAISED_SPLITTER);
        split.setSplitDragable(true);
        split.setSplitAbsolute(false);
        split.setSplitTypeChangeEnabled(true);
        split.setSplitPosition(30);
    }
    
    
    protected TableInfoModel getTableModel() {
      TableInfoModel model = new TableInfoModel();
      Class classa = FileVcsInfo.class;
      String  column1 = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.columnName"); // NOI18N
      try {
          Method method1 = classa.getMethod("getFile", null); // NOI18N
          model.setColumnDefinition(0, column1, method1, null, true, new FileComparator());
      } catch (NoSuchMethodException exc) {
          Thread.dumpStack();
      } catch (SecurityException exc2) {
          Thread.dumpStack();
      }
      return model;
    }
    
    /**
     * to be overidden to have a fiter toggle box..
     */
    protected boolean hasFilter() {
        return false;
    }
    
    /**
     * in case of hasFilter == true, provide the filter panel component here..
     */
    protected JComponent createFilterComponent() {
        return null;
    }
    
    private JComponent initTree() {
        pnlTree = new javax.swing.JPanel();
        pnlButtons = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
//        jScrollPane1 = new javax.swing.JScrollPane();
        trDirStructure = new BeanTreeView();
        trDirStructure.setPopupAllowed(true);
//        jScrollPane2 = new javax.swing.JScrollPane();
        tblTable = new NodesTableView(getTableModel());
        
        pnlTree.setLayout(new java.awt.BorderLayout());
        pnlTree.setPreferredSize(new java.awt.Dimension(200, 300));
        pnlTree.setMinimumSize(new java.awt.Dimension(200, 300));

        if (hasFilter()) {
            final JPanel pnlFilterMain = new javax.swing.JPanel();
            pnlFilterMain.setLayout(new BorderLayout());
            final JToggleButton btnToggle = new JToggleButton(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.btnToggle.showFilterText"));
            btnToggle.setSelected(false);
            final JComponent innerComp = createFilterComponent();
            btnToggle.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent event) {
                  if (btnToggle.isSelected()) {
                      btnToggle.setText(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.btnToggle.hideFilterText"));
                      innerComp.setVisible(true);
                  } else {
                      btnToggle.setText(NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.btnToggle.showFilterText"));
                      innerComp.setVisible(false);
                  }
               }
            });
            pnlFilterMain.add(btnToggle, BorderLayout.NORTH);
            pnlFilterMain.add(innerComp, java.awt.BorderLayout.CENTER);
            innerComp.setVisible(false);
            pnlTree.add(pnlFilterMain, java.awt.BorderLayout.NORTH);
        }
        jTabbedPane1.setTabPlacement(javax.swing.SwingConstants.BOTTOM);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(500, 400));
/*        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 70));
        jScrollPane1.setVerticalScrollBar(new JScrollBar(Adjustable.VERTICAL));
        jScrollPane1.setHorizontalScrollBar(new JScrollBar(Adjustable.HORIZONTAL));
 */
/*        trDirStructure.setShowsRootHandles(true);
        trDirStructure.setMinimumSize(new java.awt.Dimension(80, 60));
        trDirStructure.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                trDirStructureValueChanged(evt);
            }
        });
 */
//        jScrollPane1.setViewportView(trDirStructure);
        String treeTitle = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.treeTitle"); //NOI18N
        jTabbedPane1.addTab(treeTitle, trDirStructure); 
        String tableTitle = NbBundle.getBundle(AbstractTreeInfoPanel.class).getString("AbstractTreeInfoPanel.tableTitle"); //NOI18N
        jTabbedPane1.addTab(tableTitle, tblTable);
        pnlTree.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        return pnlTree;
    }
    
    public JComponent initRightPanel() {
        pnlStatus = new javax.swing.JPanel();
        pnlStatus.setLayout(new java.awt.BorderLayout());
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
/*      int index = tblTable.getSelectedRow();
      TableInfoModel model = (TableInfoModel)tblTable.getModel();
      FileInfoContainer info = (FileInfoContainer) model.getElementAt(index);
      if (info != null) {
         setPanel(info);
      } else {
          setClearPanel();
      }    
 */
  }    
  
  
  private void trDirStructureValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_trDirStructureValueChanged
      // Add your handling code here:
/*      DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
      Object userObj = node.getUserObject();
      if (userObj instanceof File) {
          setClearPanel();
      } else {
          setPanel(userObj);
      }
 */
  }//GEN-LAST:event_trDirStructureValueChanged
  
  private void dirStructureValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_dirStructureValueChanged
      
  }//GEN-LAST:event_dirStructureValueChanged
  
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
  
  /** Use for command that don't have a common root. Ex. commit MessageChooser
   */
  protected void disableTree() {
//      treeDisabled = true;
      //TODO.. remove the tree tab.
//      jTabbedPane1.remove(jScrollPane1);
      this.repaint();
  }    

  protected boolean isTreeDisabled() {
      return false;
//      return treeDisabled;
  }
  
  protected JPanel getButtonPanel() {
      return pnlButtons;
  }
  
  
  protected abstract JComponent initPanel();

}
