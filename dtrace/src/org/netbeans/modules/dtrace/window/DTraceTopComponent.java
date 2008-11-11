/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.

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

 * Contributor(s):

 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

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


package org.netbeans.modules.dtrace.window;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import org.netbeans.modules.dtrace.dialogs.CreateDialog;
import org.netbeans.modules.dtrace.script.BuildScripts;
import org.netbeans.modules.dtrace.data.DScriptDataNode;
import org.netbeans.modules.dtrace.dialogs.CheckOSDialog;
import org.netbeans.modules.dtrace.execution.ScriptExecutor;
import org.netbeans.modules.dtrace.script.Script;
import org.netbeans.modules.dtrace.script.ScriptLibrary;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.opensolaris.dtracex.AbstractDisplay;
import org.opensolaris.dtracex.ConsumerRecorder;
import org.opensolaris.chime.*;
import org.opensolaris.chime.resources.*;


/**
 * Top component which displays something.
 */
final class DTraceTopComponent extends TopComponent {
    
    private static DTraceTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/dtrace/resources/run.gif";
    private static final String PREFERRED_ID = "DTraceTopComponent";
    private ScriptExecutor scriptExecutor;
    private final PropertySheet propertySheet = new PropertySheet();
    static Configuration configuration;
    static StatLauncher launcher;
    
    private DTraceTopComponent() {
        initComponents();
        jScrollPane2.setViewportView(propertySheet);
        setName(NbBundle.getMessage(DTraceTopComponent.class, "CTL_DTraceTopComponent"));
        setToolTipText(NbBundle.getMessage(DTraceTopComponent.class, "HINT_DTraceTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
        
        if (categoryComboBox.getModel().getSize() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }
        
        scriptsListSelectionChanged();
        scriptExecutor = new ScriptExecutor();
       
//        try {
//            URLClassLoader urlLoader = new URLClassLoader(
//                    new URL[] {new URL("file", null, "/usr/share/lib/java/dtrace.jar")},
//                    getClass().getClassLoader());
//            Class c = Class.forName("org.netbeans.modules.dtrace.chime.StatLauncher", true, urlLoader);
//            Method launchMethod = c.getMethod("launch");
//            launchMethod.invoke(null);
//            Method getContentPaneMethod = c.getMethod("getContentPane");
//            Component component = (Component) getContentPaneMethod.invoke(null);
//            if (component != null) {
//                jScrollPane2.setViewportView(component);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        
          configuration = Configuration.getInstance();
	      configuration.setRunButtonIcon(ChimeImages.RUN);
	      configuration.setLookAndFeelSettable(false);
	      configuration.setIncludeCloseMenuItem(false);
	      configuration.setTraceDoubleClickAction(ChimeAction.DISPLAY);
	      configuration.setChimePathDisplayable(false);
	      configuration.setProgramDisplayEnabled(false);
	      launcher = new StatLauncher(configuration);
          launcher.addChimeListener(new ChimeListener() {
	          public void programDisplayed(ProgramDisplayEvent e) {
                  //System.out.println(e.getProgramText());
                  String tmpPath = File.separator + "tmp" + File.separator + "chime.d";
                  try {
                      PrintWriter out = new PrintWriter(new FileWriter(tmpPath));
                      out.write(e.getProgramText());
                      out.close();
                  } catch (IOException ex) {
                      Exceptions.printStackTrace(ex);
                  }
                  File chimeFile = new File(tmpPath);
                  FileObject fileObject = FileUtil.toFileObject(chimeFile);
                  try {
                      DataObject dataObject = DataObject.find(fileObject);
                      OpenCookie oc = (OpenCookie) dataObject.getNodeDelegate().getCookie(OpenCookie.class);
                      if (oc != null) {
                          oc.open();
                      }
                  } catch (Exception ex) {
                      ex.printStackTrace();
                  }
              }
        });
        if (launcher.getContentPane() != null) {
            jPanel3.add(launcher.getContentPane(), BorderLayout.CENTER);
            jPanel3.add(launcher.getMenuBar(), BorderLayout.NORTH);
        } 
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scriptPopupMenu1 = new javax.swing.JPopupMenu();
        editMenuItem1 = new javax.swing.JMenuItem();
        runMenuItem1 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        configLabel = new javax.swing.JLabel();
        runButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        scriptsList = new javax.swing.JList();
        scriptsLabel = new javax.swing.JLabel();
        categoryComboBox = new javax.swing.JComboBox(new BuildScripts().getDirs());
        categoryLabel = new javax.swing.JLabel();
        newScriptButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        org.openide.awt.Mnemonics.setLocalizedText(editMenuItem1, "View/Edit");
        editMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuItem1ActionPerformed(evt);
            }
        });
        scriptPopupMenu1.add(editMenuItem1);

        org.openide.awt.Mnemonics.setLocalizedText(runMenuItem1, "Run");
        runMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMenuItem1ActionPerformed(evt);
            }
        });
        scriptPopupMenu1.add(runMenuItem1);

        setAutoscrolls(true);
        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(22, 22));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(3, 3));

        jPanel1.setMinimumSize(new java.awt.Dimension(22, 22));

        configLabel.setFont(new java.awt.Font("Arial", 0, 14));
        org.openide.awt.Mnemonics.setLocalizedText(configLabel, "Configuration");

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dtrace/resources/start.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, "Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        scriptsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scriptsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                scriptsListValueChanged(evt);
            }
        });
        scriptsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scriptsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(scriptsList);

        scriptsLabel.setFont(new java.awt.Font("Arial", 0, 14));
        org.openide.awt.Mnemonics.setLocalizedText(scriptsLabel, "Scripts");

        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });

        categoryLabel.setFont(new java.awt.Font("Arial", 0, 14));
        org.openide.awt.Mnemonics.setLocalizedText(categoryLabel, "Category");

        org.openide.awt.Mnemonics.setLocalizedText(newScriptButton1, "New Script");
        newScriptButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newScriptButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, 0, 0, Short.MAX_VALUE)
                            .add(scriptsLabel)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, categoryComboBox, 0, 232, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(newScriptButton1)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .add(configLabel)))
                    .add(categoryLabel)
                    .add(runButton))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(categoryLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(categoryComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(newScriptButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(scriptsLabel)
                    .add(configLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(runButton)
                .add(13, 13, 13))
        );

        jTabbedPane1.addTab("Toolkit", jPanel1);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Chime", jPanel3);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void newScriptButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newScriptButton1ActionPerformed
// TODO add your handling code here:
        if (categoryComboBox.getSelectedIndex() == -1) {
            return;
        }
        String catName = (String)categoryComboBox.getSelectedItem();
        String fullPath = new ScriptLibrary().getPreDefScriptDir() + "/" + catName;
        FileObject fileObject = FileUtil.toFileObject(new File(fullPath));
        try {
            CreateDialog createDialog = new CreateDialog(null, true);
            createDialog.setLocationRelativeTo(scriptsList);
            String name = createDialog.showDialog(catName);
            //fileObject.createData(name,"d");
            fileObject.createData(name);
            fullPath += "/";
            fullPath += name;
            categoryChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        fileObject = FileUtil.toFileObject(new File(fullPath));
        try {
            DataObject dataObject = DataObject.find(fileObject);
            OpenCookie oc = (OpenCookie) dataObject.getNodeDelegate().getCookie(OpenCookie.class);
            if (oc != null) {
                oc.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }     
}//GEN-LAST:event_newScriptButton1ActionPerformed

    private void scriptsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_scriptsListValueChanged
// TODO add your handling code here:
        //descriptionTextArea.setText(((Script)scriptsList.getSelectedValue()).getDescription());
        scriptsListSelectionChanged();
    }//GEN-LAST:event_scriptsListValueChanged

    private void scriptsListSelectionChanged() {
        if (scriptsList.getSelectedIndex() != -1) {
            String name = ((Script)scriptsList.getSelectedValue()).getName();
            if (name.length() > 0) {
                int dot = name.lastIndexOf('.');
                if (dot != -1) {
                    if (name.charAt(dot + 1) == 't' &&
                            name.charAt(dot + 2) == 'x' &&
                            name.charAt(dot + 3) == 't') {
                        runButton.setEnabled(false);
                        return;
                    }
                }
            }
        }
       
        runButton.setEnabled(scriptsList.getSelectedIndex() != -1);
        if (scriptsList.getSelectedIndex() == -1) {
            return;
        }
        try {
            FileObject fileObject = FileUtil.toFileObject(((Script)scriptsList.getSelectedValue()).getFile());
            DataObject dataObject = DataObject.find(fileObject);
            propertySheet.setNodes(new Node[]{new DScriptDataNode(dataObject)});
            propertySheet.setDescriptionAreaVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            propertySheet.setNodes(new Node[0]);
        }
    }
    
    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryComboBoxActionPerformed
// TODO add your handling code here:
        categoryChanged();
    }//GEN-LAST:event_categoryComboBoxActionPerformed

    private void categoryChanged() {
     //   BuildScripts buildScripts = new BuildScripts();
     //   buildScripts.getDirs();
        final Vector newData = new BuildScripts().getScripts(categoryComboBox.getSelectedItem().toString());
        ListModel newListModel = new AbstractListModel() {
            public int getSize() { return newData.size(); }
            public Object getElementAt(int i) { return newData.elementAt(i); }
        };
        scriptsList.setModel(newListModel);
    }
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
// TODO add your handling code here:
        //DTraceExecutor.runScript((Script)scriptsList.getSelectedValue());
        scriptExecutor.execute((Script)scriptsList.getSelectedValue());       
}//GEN-LAST:event_runButtonActionPerformed

private void scriptsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scriptsListMouseClicked
// TODO add your handling code here:
        if (evt.getClickCount() == 1 && evt.getButton() == evt.BUTTON1) {
            if (scriptsList.getSelectedIndex() == -1) {
                return;
            }
            try {
                FileObject fileObject = FileUtil.toFileObject(((Script)scriptsList.getSelectedValue()).getFile());
                DataObject dataObject = DataObject.find(fileObject);
                propertySheet.setNodes(new Node[]{new DScriptDataNode(dataObject)});
                propertySheet.setDescriptionAreaVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
                propertySheet.setNodes(new Node[0]);
            } 
        } else if (evt.getClickCount() >= 2) {
            if (scriptsList.getSelectedIndex() == -1) {
                return;
            }
            FileObject fileObject = FileUtil.toFileObject(((Script)scriptsList.getSelectedValue()).getFile());
            try {
                DataObject dataObject = DataObject.find(fileObject);
                OpenCookie oc = (OpenCookie) dataObject.getNodeDelegate().getCookie(OpenCookie.class);
                if (oc != null) {
                    oc.open();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }  
        } else if (evt.getButton() == evt.BUTTON2 ||evt.getButton() == evt.BUTTON3) {           
             scriptPopupMenu1.show(scriptsList, evt.getX(), evt.getY());
             scriptPopupMenu1.setVisible(true);
        }
}//GEN-LAST:event_scriptsListMouseClicked

private void editMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuItem1ActionPerformed
// TODO add your handling code here:
    if (scriptsList.getSelectedIndex() == -1) {
        scriptPopupMenu1.setVisible(false);
        return;
    }
    FileObject fileObject = FileUtil.toFileObject(((Script)scriptsList.getSelectedValue()).getFile());
    try {
        DataObject dataObject = DataObject.find(fileObject);
        OpenCookie oc = (OpenCookie) dataObject.getNodeDelegate().getCookie(OpenCookie.class);
        if (oc != null) {
            oc.open();
        }
    } catch (Exception e) {
        e.printStackTrace();
    } 
    
    scriptPopupMenu1.setVisible(false);
}//GEN-LAST:event_editMenuItem1ActionPerformed

private void runMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runMenuItem1ActionPerformed
// TODO add your handling code here:
    scriptExecutor.execute((Script)scriptsList.getSelectedValue());
    scriptPopupMenu1.setVisible(false);
}//GEN-LAST:event_runMenuItem1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox categoryComboBox;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel configLabel;
    private javax.swing.JMenuItem editMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton newScriptButton1;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem runMenuItem1;
    private javax.swing.JPopupMenu scriptPopupMenu1;
    private javax.swing.JLabel scriptsLabel;
    private javax.swing.JList scriptsList;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized DTraceTopComponent getDefault() {
        if (instance == null) {
            instance = new DTraceTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the DTraceTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized DTraceTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find DTrace component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof DTraceTopComponent) {
            return (DTraceTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
        if (Utilities.getOperatingSystem() != Utilities.OS_SOLARIS) {
            CheckOSDialog checkOSDialog = new CheckOSDialog(null, true);
            checkOSDialog.setVisible(true);
        }
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
	ConsumerRecorder.stopAll();
	AbstractDisplay.closeAll();
        scriptExecutor.stopAllIoTabs();
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void open() {
        Mode m = WindowManager.getDefault().findMode ("explorer");
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return DTraceTopComponent.getDefault();
        }
    }
    
}
