/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelConfigureSourcesVisual.java
 *
 * Created on Sep 12, 2008, 7:51:33 AM
 */

package org.netbeans.modules.python.project.templates;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
public class PanelConfigureSourcesVisual extends javax.swing.JPanel {
    
    private final FolderModel sourcesModel;
    private final FolderModel testsModel;
    private File lastUsed;
    private WizardDescriptor descriptor;

    /** Creates new form PanelConfigureSourcesVisual */
    public PanelConfigureSourcesVisual() {
        initComponents();
        this.sourcesModel = new FolderModel();
        this.testsModel = new FolderModel();
        this.sources.setModel(sourcesModel);
        this.tests.setModel(testsModel);
        this.sources.setCellRenderer(new FolderRenderer());
        this.tests.setCellRenderer(new FolderRenderer());
    }        
    
    private class TableColumnSizeComponentAdapter extends ComponentAdapter {
        private JTable table = null;
        
        public TableColumnSizeComponentAdapter(JTable table){
            this.table = table;
        }
        
        @Override
        public void componentResized(ComponentEvent evt){
            double pw = table.getParent().getParent().getSize().getWidth();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setWidth( ((int)pw/2) - 1 );
            column.setPreferredWidth( ((int)pw/2) - 1 );
            column = table.getColumnModel().getColumn(1);
            column.setWidth( ((int)pw/2) - 1 );
            column.setPreferredWidth( ((int)pw/2) - 1 );
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sources = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tests = new javax.swing.JList();
        addSource = new javax.swing.JButton();
        removeSource = new javax.swing.JButton();
        addTest = new javax.swing.JButton();
        removeTest = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.jLabel1.text")); // NOI18N
        jLabel1.setFocusable(false);

        jLabel2.setLabelFor(sources);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.jLabel2.text")); // NOI18N

        sources.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(sources);

        jLabel3.setLabelFor(tests);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.jLabel3.text")); // NOI18N

        tests.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(tests);

        org.openide.awt.Mnemonics.setLocalizedText(addSource, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.addSource.text")); // NOI18N
        addSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSourceActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeSource, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.removeSource.text")); // NOI18N
        removeSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSourceActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addTest, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.addTest.text")); // NOI18N
        addTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTestActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeTest, org.openide.util.NbBundle.getMessage(PanelConfigureSourcesVisual.class, "PanelConfigureSourcesVisual.removeTest.text")); // NOI18N
        removeTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(removeTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addSource, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(removeSource, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addSource)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeSource)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeTest))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void addSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSourceActionPerformed
    File[] toAdd = browse (NbBundle.getMessage(PanelConfigureSourcesVisual.class, "LBL_SelectSources"),this.testsModel);
    for (File f : toAdd) {
        this.sourcesModel.addElement(FileUtil.normalizeFile(f));
    }
}//GEN-LAST:event_addSourceActionPerformed

private void removeSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSourceActionPerformed
    Object[] selected = this.sources.getSelectedValues();
    for (Object d : selected) {
        this.sourcesModel.removeElement(d);
    }
}//GEN-LAST:event_removeSourceActionPerformed

private void addTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTestActionPerformed
    File[] toAdd = browse (NbBundle.getMessage(PanelConfigureSourcesVisual.class, "LBL_SelectTests"),this.sourcesModel);
    for (File f : toAdd) {
        this.testsModel.addElement(FileUtil.normalizeFile(f));
    }
}//GEN-LAST:event_addTestActionPerformed

private void removeTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTestActionPerformed
    Object[] selected = this.tests.getSelectedValues();
    for (Object d : selected) {
        this.testsModel.removeElement(d);
    }
}//GEN-LAST:event_removeTestActionPerformed


    private File[] browse (String label, FolderModel other) {
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(label);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        
        chooser.setMultiSelectionEnabled(true);
        chooser.setCurrentDirectory(getCwd());
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {            
            File[] files = chooser.getSelectedFiles();
            this.lastUsed = chooser.getCurrentDirectory();
            return files;
        }
        return new File[0];
    }   

    private File getCwd () {
        if (lastUsed == null) {
            File projectLocation = (File) descriptor.getProperty(NewPythonProjectWizardIterator.PROP_PROJECT_LOCATION);
            if (projectLocation != null && projectLocation.exists()) {
                lastUsed = projectLocation;
            }
            else {
                lastUsed = ProjectChooser.getProjectsFolder();
            }
        }
        return lastUsed;
    }
    
    void store (final WizardDescriptor descriptor) {
        descriptor.putProperty(NewPythonProjectWizardIterator.SOURCE_ROOTS,sourcesModel.getData());
        descriptor.putProperty(NewPythonProjectWizardIterator.TEST_ROOTS,testsModel.getData());
    }

    void read (final WizardDescriptor descriptor) {
        this.descriptor = descriptor;
        File projectDirectory = (File) descriptor.getProperty(NewPythonProjectWizardIterator.PROP_PROJECT_LOCATION);
        ((FolderRenderer)sources.getCellRenderer()).setProjectFolder(projectDirectory);
        ((FolderRenderer)tests.getCellRenderer()).setProjectFolder(projectDirectory);
        this.sourcesModel.removeAllElements();
        final File[] src = (File[]) descriptor.getProperty(NewPythonProjectWizardIterator.SOURCE_ROOTS);
        for (File f : src) {
            this.sourcesModel.addElement(f);
        }
        this.testsModel.removeAllElements();
        final File[] tst = (File[]) descriptor.getProperty(NewPythonProjectWizardIterator.TEST_ROOTS);
        for (File f : tst) {
            this.testsModel.addElement(f);
        }
    }

    public void validate (final WizardDescriptor descriptor) {        
    }

    boolean valid (final WizardDescriptor descriptor) {
        return true;
    }
    
    private class FolderModel extends DefaultListModel {
        public boolean contains (File root) {
            Object[] data = toArray();
            for (Object o : data) {
                if (root.equals(o)) {
                    return true;
                }
            }
            return false;
        }
        
        public File[] getData () {
            Object[] data = toArray();
            File[] res = new File[data.length];
            for (int i=0; i< data.length; i++) {
                res[i] = (File) data[i];
            }
            return res;
        }
    }
    
    private class FolderRenderer extends DefaultListCellRenderer {
        private File projectFolder;
        
        public FolderRenderer () {
        }
        
        public void setProjectFolder (File folder) {
            this.projectFolder = folder;
        }
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (projectFolder != null && value instanceof File) {
                String path = ((File)value).getAbsolutePath();
                String projectPath = projectFolder.getAbsolutePath();
                if (!projectPath.endsWith(File.separator)) {
                    projectPath+=File.separator;
                }
                if (path.startsWith(projectPath)) {
                    value = path.substring(projectPath.length(), path.length());
                }
                else {
                    value = path;
                }
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSource;
    private javax.swing.JButton addTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeSource;
    private javax.swing.JButton removeTest;
    private javax.swing.JList sources;
    private javax.swing.JList tests;
    // End of variables declaration//GEN-END:variables

}
