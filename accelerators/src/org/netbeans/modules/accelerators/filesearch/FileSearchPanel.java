/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.accelerators.AcceleratorsOptions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;

/**
 *
 * @author  Andrei Badea
 */
public class FileSearchPanel extends javax.swing.JPanel {
    
    private FileSearch search;
    private File projectDir;
    private ResultListModel resultModel;
    
    public FileSearchPanel(FileSearch search, FileObject projectDir) {
        assert search != null;
        assert projectDir != null;
        this.search = search;
        this.projectDir = FileUtil.toFile(projectDir);
        
        initComponents();
        
        resultModel = new ResultListModel();
        resultList.setModel(resultModel);
        resultList.setCellRenderer(new ResultListCellRenderer());
        
        fileNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateFileName();
            }
            
            public void insertUpdate(DocumentEvent e) {
                updateFileName();
            }
            
            public void removeUpdate(DocumentEvent e) {
                updateFileName();
            }
        });
        
        search.getResult().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateResult();
            }
        });
    }
    
    public FileObject getSelectedFile() {
        Object selected = resultList.getSelectedValue();
        return selected != null ? ((FileSearchResult.Item)selected).getFileObject() : null;
    }
    
    private boolean isCaseSensitive() {
        return caseSensitiveCheckBox.isSelected();
    }
    
    private void updateFileName() {
        search.search(fileNameTextField.getText(), caseSensitiveCheckBox.isSelected());
    }
    
    private void updateResult() {
        Mutex.EVENT.readAccess(new Runnable() {
            public void run() {
                FileSearchResult.Item[] items = search.getResult().getItems();
                Arrays.sort(items, new ResultItemComparator());
                resultModel.setItems(items);
                if (resultModel.getSize() > 0) {
                    resultList.setSelectedIndex(0);
                    resultList.scrollRectToVisible(resultList.getCellBounds(0, 0));
                } else {
                    resultList.clearSelection();
                }
            }
        });
    }
    
    /**
     * Copied from org.netbeans.spi.project.support.ant.PropertyUtils. Could
     * have just added the dependency on ant/project, but 
     * 1) it's an SPI support class, not API; 2) let's not add a new
     * dependency just because of a single method.
     */
    private static String relativizeFile(File basedir, File file) {
        if (basedir.isFile()) {
            throw new IllegalArgumentException("Cannot relative w.r.t. a data file " + basedir); // NOI18N
        }
        if (basedir.equals(file)) {
            return "."; // NOI18N
        }
        StringBuffer b = new StringBuffer();
        File base = basedir;
        String filepath = file.getAbsolutePath();
        while (!filepath.startsWith(slashify(base.getAbsolutePath()))) {
            base = base.getParentFile();
            if (base == null) {
                return null;
            }
            if (base.equals(file)) {
                // #61687: file is a parent of basedir
                b.append(".."); // NOI18N
                return b.toString();
            }
            b.append("../"); // NOI18N
        }
        URI u = base.toURI().relativize(file.toURI());
        assert !u.isAbsolute() : u + " from " + basedir + " and " + file + " with common root " + base;
        b.append(u.getPath());
        if (b.charAt(b.length() - 1) == '/') {
            // file is an existing directory and file.toURI ends in /
            // we do not want the trailing slash
            b.setLength(b.length() - 1);
        }
        return b.toString();
    }

    private static String slashify(String path) {
        if (path.endsWith(File.separator)) {
            return path;
        } else {
            return path + File.separatorChar;
        }
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        resultScrollPane = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList();
        resultLabel = new javax.swing.JLabel();
        caseSensitiveCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(540, 280));
        fileNameLabel.setLabelFor(fileNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(fileNameLabel, org.openide.util.NbBundle.getMessage(FileSearchPanel.class, "CTL_FileName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(fileNameLabel, gridBagConstraints);

        fileNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fileNameTextFieldKeyPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 11);
        add(fileNameTextField, gridBagConstraints);

        resultScrollPane.setViewportView(resultList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 0, 11);
        add(resultScrollPane, gridBagConstraints);

        resultLabel.setLabelFor(resultList);
        org.openide.awt.Mnemonics.setLocalizedText(resultLabel, org.openide.util.NbBundle.getMessage(FileSearchPanel.class, "CTL_MatchingFiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(resultLabel, gridBagConstraints);

        caseSensitiveCheckBox.setSelected(AcceleratorsOptions.getInstance().getFileSearchCaseSensitive());
        org.openide.awt.Mnemonics.setLocalizedText(caseSensitiveCheckBox, org.openide.util.NbBundle.getMessage(FileSearchPanel.class, "CTL_CaseSensitive"));
        caseSensitiveCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                caseSensitiveCheckBoxItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 11);
        add(caseSensitiveCheckBox, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void caseSensitiveCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_caseSensitiveCheckBoxItemStateChanged
        AcceleratorsOptions.getInstance().setFileSearchCaseSensitive(isCaseSensitive());
        updateFileName();
    }//GEN-LAST:event_caseSensitiveCheckBoxItemStateChanged

    private void fileNameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fileNameTextFieldKeyPressed
        Object actionKey = resultList.getInputMap().get(KeyStroke.getKeyStrokeForEvent(evt));
        
        // see JavaFastOpen.boundScrollingKey()
        boolean isListScrollAction = "selectPreviousRow".equals(actionKey) || // NOI18N
                "selectNextRow".equals(actionKey) || // NOI18N
                //"selectFirstRow".equals(actionKey) || // NOI18N
                //"selectLastRow".equals(actionKey) || // NOI18N
                "scrollUp".equals(actionKey) || // NOI18N
                "scrollDown".equals(actionKey); // NOI18N
        
        if (isListScrollAction) {
            Action action = resultList.getActionMap().get(actionKey);
            action.actionPerformed(new ActionEvent(resultList, 0, (String)actionKey));
            evt.consume();
        }
    }//GEN-LAST:event_fileNameTextFieldKeyPressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox caseSensitiveCheckBox;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JList resultList;
    private javax.swing.JScrollPane resultScrollPane;
    // End of variables declaration//GEN-END:variables
    
    private static final class ResultListModel extends AbstractListModel {

        private FileSearchResult.Item[] items = null;
        
        public void setItems(FileSearchResult.Item[] items) {
            assert items != null;
            this.items = items;
            fireContentsChanged(this, 0, items.length);
        }
        
        public Object getElementAt(int index) {
            return items[index];
        }

        public int getSize() {
            return items != null ? items.length : 0;
        }
    }
    
    private final class ResultListCellRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Object newValue = value;
  
            if (value instanceof FileSearchResult.Item) {
                FileSearchResult.Item item = (FileSearchResult.Item)value;
                File file = item.getFile();
                String name = file.getName();
                String parent = relativizeFile(projectDir, file);
                newValue = name + " (" + parent + ")"; // NOI18N
            }
                
            return super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
        }
    }
    
    private static final class ResultItemComparator implements Comparator {
        
        private boolean caseSensitive;
        
        public ResultItemComparator() {
            this.caseSensitive = caseSensitive;
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof ResultItemComparator)) {
                return false;
            }
            
            ResultItemComparator that = (ResultItemComparator)obj;
            return this.caseSensitive == that.caseSensitive;
        }
        
        public int compare(Object o1, Object o2) {
            FileSearchResult.Item item1 = (FileSearchResult.Item)o1;
            FileSearchResult.Item item2 = (FileSearchResult.Item)o2;
            
            File file1 = item1.getFile();
            File file2 = item2.getFile();
            
            int result = String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
            if (result == 0) {
                result = String.CASE_INSENSITIVE_ORDER.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
            }
            
            return result;
        }
    }
}
