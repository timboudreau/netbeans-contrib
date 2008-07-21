package org.netbeans.modules.gsf.tools.lucene;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.Index.SearchResult;
import org.netbeans.modules.gsf.Language;
import org.netbeans.modules.gsf.LanguageRegistry;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedClass;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedElement;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedField;
import org.netbeans.modules.gsf.tools.IndexUtils.IndexedMethod;
import org.netbeans.modules.gsfret.source.usages.ClassIndexImpl;
import org.netbeans.modules.gsfret.source.usages.ClassIndexManager;
import org.netbeans.modules.gsfret.source.usages.Index;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 * Window for browsing a GSF index
 * 
 * TODO: Query the set of available keys from Lucene
 * @todo Set column model to get proportional widths (auto fit?)
 * @todo Instead of showing the segment+docid, show the require-value in the match list
 * 
 * @author Tor Norbye
 */
final class IndexBrowserTopComponent extends TopComponent {
    
    private static IndexBrowserTopComponent instance;
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "IndexBrowserTopComponent";
    
    private File segmentFile;
    
    private IndexBrowserTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(IndexBrowserTopComponent.class, "CTL_IndexBrowserTopComponent"));
        setToolTipText(NbBundle.getMessage(IndexBrowserTopComponent.class, "HINT_IndexBrowserTopComponent"));
        //        setIcon(Utilities.loadImage(ICON_PATH, true));


        Vector<String> languageNames = new Vector<String>();
        for (Language language : LanguageRegistry.getInstance()) {
            if (language.getIndexer() == null) {
                continue;
            }
            languageNames.add(language.getDisplayName());
        }
        languageCombo.setModel(new DefaultComboBoxModel(languageNames));
        
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = matchTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                try {
                    ListSelectionModel lsm =
                        (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        if (matchTable.getModel() instanceof SearchMatchModel) {
                            SearchMatchModel model = (SearchMatchModel)matchTable.getModel();
                            SearchResult result = model.getSearchResultAt(selectedRow);
                            show(result);
                        }
                    }
                } catch (Exception ex) {
                    ;
                }
            }
        });
        
        updateIndices();
        
        indexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        indexList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent ev) {
                if (!ev.getValueIsAdjusting()) {
                    //int row = indexList.getSelectedIndex();
                    int row = ev.getFirstIndex();
                    if (row == -1) {
                        indexLabel.setText("");
                        segmentLabel.setText("");
                        setSegment(null);
                        return;
                    }
                    if (row >= indexList.getModel().getSize()) {
                        return;
                    }
                    IndexEntry entry = ((IndexListModel)indexList.getModel()).getEntry(row);
                    setSegment(entry.segment);
                    String path = getSegmentLabel(entry.segment);
                    segmentLabel.setText(path);
                    indexLabel.setText(getUrlLabel(entry.url));
                }
            }

            
        });
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    private Language getLanguage() {
        String name = languageCombo.getSelectedItem().toString();
        for (Language language : LanguageRegistry.getInstance()) {
            if (language.getDisplayName().equals(name)) {
                return language;
            }
        }
        
        return null;
    }
    
    private void updateIndices() {
        List<IndexEntry> list = new ArrayList<IndexEntry>();
//        if (allIndexButton.isSelected()) {
            // Show all indices
            // Initialize indices
            Language language = getLanguage();
            Map<URL, ClassIndexImpl> map = ClassIndexManager.get(language).getAllIndices();
            for (URL url : map.keySet()) {
                ClassIndexImpl index = map.get(url);
                File segment = index.getSegment();
                list.add(new IndexEntry(url, index, segment));
            }
//        } else {
//            assert indexForButton.isSelected();
//            
//            File file = getCurrentFile();
//            if (file == null) {
//                Toolkit.getDefaultToolkit().beep();
//                return;
//            }
//            
//            FileObject fo = FileUtil.toFileObject(file);
//            if (fo == null) {
//                Toolkit.getDefaultToolkit().beep();
//                return;
//            }
//            
////            Project p = FileOwnerQuery.getOwner(fo);
////            if (p == null) {
////                Toolkit.getDefaultToolkit().beep();
////                return;
////            }
//            
//            final ClassPath cp = ClassPath.getClassPath(fo, ClassPath.SOURCE);
//            Set<ClassIndexImpl> indices = new HashSet<ClassIndexImpl>();
//            ClassIndex.createQueriesForRoots(cp, true, indices);
//            for (ClassIndexImpl index : indices) {
//                URL url = index.getRoot();
//                File segment = index.getSegment();
//                list.add(new IndexEntry(url, index, segment));
//            }
//        }
        indexList.setModel(new IndexListModel(list));
    }
    
    private String getUrlLabel(URL url) {
        String path = url.toExternalForm();
        int index = path.indexOf("jruby-1.1.3");
        if (index != -1) {
            path = path.substring(index+12);
        }
        return path;
    }
    
    private String getSegmentLabel(File segment) {
        String path = "";
        if (segment != null) {
            path = segment.getPath();
            int index = path.indexOf("gsf-index");
            if (index != -1) {
                path = path.substring(index+10);
            }
        }
        
        return path;
    }
    
    private void setSegment(File segment) {
        this.segmentFile = segment;
        lukeButton.setEnabled(segment != null);
    }
    
    private void show(SearchResult result) {
        File segment = (File)result.getSegment();
        setSegment(segment);
        segmentLabel.setText(segment.getPath());
        ClassIndexImpl index = (ClassIndexImpl)result.getIndex();
        URL root = index.getRoot();
        if (root != null) {
            indexLabel.setText(getUrlLabel(root));
        } else {
            indexLabel.setText("");
        }
        docIdField.setText(Integer.toString(result.getDocumentNumber()));
        
        final TableModel model = new SearchDocumentTableModel(result);
        documentTable.setModel(model);
        documentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                //int index = evt.getFirstIndex();
                if (evt.getValueIsAdjusting()) {
                    return;
                }
                int index = documentTable.getSelectedRow();
                String s = (String)model.getValueAt(index, 1);
                String key = (String)model.getValueAt(index, 0);
                if ("method".equals(key)) {
                    // Decode the attributes
                    int attributeIndex = s.indexOf(';');
                    if (attributeIndex != -1) {
                        int flags = IndexedElement.stringToFlag(s, attributeIndex+1);
                        if (flags != 0) {
                            String desc = IndexedMethod.decodeFlags(flags);
                            s = s.substring(0, attributeIndex) + desc + s.substring(attributeIndex+3);
                        }
                    }
                } else if ("attrs".equals(key)) {
                    // Decode the attributes
                    int flags = IndexedElement.stringToFlag(s, 0);
                    if (flags != 0) {
                        String desc = IndexedClass.decodeFlags(flags);
                        s = desc + s.substring(2);
                    }
                } else if ("field".equals(key)) {
                    // Decode the attributes
                    int attributeIndex = s.indexOf(';');
                    if (attributeIndex != -1) {
                        int flags = IndexedElement.stringToFlag(s, attributeIndex+1);
                        if (flags != 0) {
                            String desc = IndexedField.decodeFlags(flags);
                            s = s.substring(0, attributeIndex) + desc + s.substring(attributeIndex+3);
                        }
                    }
                } // TODO: attribute
                selectedElementField.setText(s);
            }
        });
        
        // Temporary hack because the other search model doesn't work right -- duplicate entries
        showDocument(result.getDocumentNumber());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton3 = new javax.swing.JRadioButton();
        indexGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        keyCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        typeCombo = new javax.swing.JComboBox();
        searchButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        matchTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        documentTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        docIdField = new javax.swing.JTextField();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        indexList = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        languageCombo = new javax.swing.JComboBox();
        indexLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        segmentLabel = new javax.swing.JLabel();
        lukeButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        selectedElementField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton3, "jRadioButton3");
        jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Key:");

        keyCombo.setEditable(true);
        keyCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "base", "fqn", "class", "method", "field", "attribute", "constant", "fqn", "file", "module", "extends", "require", "dbtable", "clz", "lcbase" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Name:");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Type:");

        typeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Prefix", "Prefix (IC)", "Exact", "Regexp", "CamelCase", "Regexp (IC)" }));

        org.openide.awt.Mnemonics.setLocalizedText(searchButton, "Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search(evt);
            }
        });

        jSplitPane1.setBorder(null);
        jSplitPane1.setResizeWeight(0.5);

        matchTable.setModel(getEmptyMatchTableModel());
        jScrollPane1.setViewportView(matchTable);

        jSplitPane1.setLeftComponent(jScrollPane1);

        documentTable.setModel(getEmptyDocumentTableModel());
        jScrollPane2.setViewportView(documentTable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Document:");

        docIdField.setColumns(6);
        docIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docIdFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(prevButton, "Previous");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(nextButton, "Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(docIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(prevButton)
                .add(6, 6, 6)
                .add(nextButton)
                .addContainerGap(209, Short.MAX_VALUE))
            .add(jPanel2Layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(docIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(prevButton)
                    .add(nextButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        indexList.setModel(getIndexList());
        jScrollPane3.setViewportView(indexList);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "Update");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateIndices(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, "Language:");

        languageCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        languageCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateIndexList(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8)
                    .add(languageCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(languageCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButton1))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Index:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "Segment:");

        org.openide.awt.Mnemonics.setLocalizedText(lukeButton, "Open in Luke");
        lukeButton.setEnabled(false);
        lukeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openInLuke(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "Element:");

        selectedElementField.setEditable(false);
        selectedElementField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedElementFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jLabel1)
                                            .add(jLabel2)
                                            .add(jLabel3))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(typeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(keyCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(nameField)))
                                    .add(searchButton))
                                .add(13, 13, 13)
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(indexLabel)
                                    .add(layout.createSequentialGroup()
                                        .add(segmentLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 591, Short.MAX_VALUE)
                                        .add(lukeButton))))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4))
                        .add(20, 20, 20))
                    .add(layout.createSequentialGroup()
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(selectedElementField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(26, 26, 26)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(keyCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(typeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchButton))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 404, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(selectedElementField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(indexLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(segmentLabel)
                    .add(lukeButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void openInLuke(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openInLuke
    File f = null;
    String l = System.getProperty("luke.dir");
    if (l != null) {
        f = new File(l);
    } else {
        f = InstalledFileLocator.getDefault().locate("modules/org-netbeans-modules-gsf-tools.jar", null, false);

        if (f != null) {
            f = new File(f.getParentFile().getAbsolutePath() +  File.separator + "..");
            try {
                f = f.getCanonicalFile();
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }

            f = new File(f, "lukeall.jar");
        }
    }
    
    try {
        if (f == null || !f.exists()) {
           StatusDisplayer.getDefault().setStatusText(f.getPath() + " not found. Install from http://www.getopt.org/luke/");
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        Runtime.getRuntime().exec(new java.lang.String[]{ System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
           "-jar", f.getPath(), "-index", segmentFile.getPath()+File.separator + "gsf"});
    }
    catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }
}//GEN-LAST:event_openInLuke

private void updateIndices(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateIndices
    updateIndices();
}//GEN-LAST:event_updateIndices


//    private File getCurrentFile() {
//        String name = indexFileField.getText();
//
//        File f = new File(name);
//
//        if (f.exists()) {
//            return f;
//        }
//
//        return null;
//    }
    
private void docIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docIdFieldActionPerformed
    int doc = Integer.parseInt(docIdField.getText().trim());
    showDocument(doc);
}//GEN-LAST:event_docIdFieldActionPerformed

private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
    String text = docIdField.getText().trim();
    try {
        int num = Integer.parseInt(text);
        num++;
        docIdField.setText(Integer.toString(num));
        showDocument(num);
    } catch (NumberFormatException nfe) {
        return;
    }    
}//GEN-LAST:event_nextButtonActionPerformed

private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
    String text = docIdField.getText().trim();
    try {
        int num = Integer.parseInt(text);
        if (num == 0) {
            return;
        }
        num--;
        docIdField.setText(Integer.toString(num));
        showDocument(num);
    } catch (NumberFormatException nfe) {
        return;
    }
}//GEN-LAST:event_prevButtonActionPerformed

private void showDocument(int id) {
    SearchDocumentTableModel model = (SearchDocumentTableModel)documentTable.getModel();
    IndexReader reader = model.getIndexReader();
    if (reader != null) {
        if (id >= reader.maxDoc()) {
            docIdField.setText(Integer.toString(reader.maxDoc()-1));
            return;
        }
        try {
            Document luceneDoc = reader.document(id);
            if (luceneDoc != null) {
                documentTable.setModel(new SearchDocumentTableModel(luceneDoc, reader, id));
            }
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }
}

private void search(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search
    // Perform the search
    String name = nameField.getText().trim();
    String type = typeCombo.getSelectedItem().toString();
    String field = keyCombo.getSelectedItem().toString();
    Set<Index.SearchScope> scope = EnumSet.allOf(Index.SearchScope.class);
    
    NameKind kind = NameKind.EXACT_NAME;
    if (type.equals("Prefix")) {
        kind = NameKind.PREFIX;
    }
    if (type.equals("Prefix (IC)")) {
        kind = NameKind.CASE_INSENSITIVE_PREFIX;
    }
    if (type.equals("Regexp")) {
        kind = NameKind.REGEXP;
    } else if (type.equals("CamelCase")) {
        kind = NameKind.CAMEL_CASE;
    } else if (type.equals("Regexp (IC)")) {
        kind = NameKind.CASE_INSENSITIVE_REGEXP;
    }
    Language language = getLanguage();
    Map<URL, ClassIndexImpl> map = ClassIndexManager.get(language).getAllIndices();
    Set<SearchResult> result = new HashSet<SearchResult>();

    try {
        for (ClassIndexImpl index : map.values()) {
            index.search(field, name, kind, scope, result, null);
        }
        
        TableModel model = new SearchMatchModel(result, field);
        matchTable.setModel(model);
        
        // TODO: Clear out the document table
        
    } catch (IOException ioe) {
        Exceptions.printStackTrace(ioe);
    }
}//GEN-LAST:event_search

private void selectedElementFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedElementFieldActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_selectedElementFieldActionPerformed

private void updateIndexList(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateIndexList
    updateIndices();
}//GEN-LAST:event_updateIndexList

private IndexReader indexReader;

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField docIdField;
    private javax.swing.JTable documentTable;
    private javax.swing.ButtonGroup indexGroup;
    private javax.swing.JLabel indexLabel;
    private javax.swing.JList indexList;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox keyCombo;
    private javax.swing.JComboBox languageCombo;
    private javax.swing.JButton lukeButton;
    private javax.swing.JTable matchTable;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel segmentLabel;
    private javax.swing.JTextField selectedElementField;
    private javax.swing.JComboBox typeCombo;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized IndexBrowserTopComponent getDefault() {
        if (instance == null) {
            instance = new IndexBrowserTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the IndexBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized IndexBrowserTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                    "Cannot find MyWindow component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof IndexBrowserTopComponent) {
            return (IndexBrowserTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
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
    
    private ListModel getIndexList() {
        return new IndexListModel();
    }
    
    private TableModel getEmptyDocumentTableModel() {
        return new SearchDocumentTableModel();
    }

    private TableModel getEmptyMatchTableModel() {
        return new SearchMatchModel();
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return IndexBrowserTopComponent.getDefault();
        }
    }
    
    class SearchMatchModel implements TableModel {
        private List<Map<String, SearchResult>> data;
        private IndexReader indexReader;

        SearchMatchModel() {
            // Empty
        }
        
        SearchMatchModel(Set<SearchResult> results, String key) {
            data = new ArrayList<Map<String, SearchResult>>();
            for (SearchResult result : results) {
                String[] matches = result.getValues(key);
                if (matches != null) {
                    for (String match : matches) {
                        Map<String,SearchResult> map = new HashMap<String,SearchResult>();
                        map.put(match, result);
                        data.add(map);
                    }
                }

                IndexReader indexReader = (IndexReader)result.getIndexReader();
                if (indexReader != null) {
                    this.indexReader = indexReader;
                }
            }
            
            // Sort the data to be helpful
            //final List<Map<String, SearchResult>> foo = new ArrayList<Map<String,SearchResult>>();
            //Collections.sort(foo, new Comparator<Map<String,SearchResult>>() {
            Collections.sort(data, new Comparator<Map<String,SearchResult>>() {
                public int compare(Map<String, SearchResult> m1,
                                   Map<String, SearchResult> m2) {
                    String key1 = m1.keySet().iterator().next();
                    String key2 = m2.keySet().iterator().next();
                    return key1.compareTo(key2);
                }
            });
        }
        
        public IndexReader getIndexReader() {
            return indexReader;
        }
    
        public int getRowCount() {
            return data == null ? 0 : data.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int col) {
            switch (col) {
            case 0: return "Match";
            case 1: return "Document";
            default: throw new IllegalArgumentException();
            }
        }

        public Class<?> getColumnClass(int arg0) {
            return String.class;
        }

        public boolean isCellEditable(int arg0, int arg1) {
            return false;
        }

        public Object getValueAt(int row, int col) {
            Map<String,SearchResult> entry = data.get(row);
            switch (col) {
            case 0:
                return entry.keySet().iterator().next();
            case 1: {
                SearchResult map = entry.values().iterator().next();
                //return getSegmentLabel((File)map.getSegment()) + ":" + Integer.toString(map.getDocumentNumber());
                String value = map.getValue("require");
                if (value != null) {
                    return value;
                } else {
                    return "";
                }
            }
            default: throw new IllegalArgumentException();
            }
        }
        
        public SearchResult getSearchResultAt(int row) {
            Map<String,SearchResult> entry = data.get(row);
            return entry.values().iterator().next();
        }

        public void setValueAt(Object arg0, int arg1, int arg2) {
            throw new IllegalArgumentException();
        }

        public void addTableModelListener(TableModelListener arg0) {
            // For now, no changes occur - we just replace the whole model
        }

        public void removeTableModelListener(TableModelListener arg0) {
        }
    }
 
    class SearchDocumentTableModel implements TableModel {
        private IndexReader indexReader;

        private class Match {
            private String key;
            private String value;

            Match(String key, String value) {
                this.key = key;
                this.value = value;
            }
        }

        private String[] fields;
        private List<Match> data;
        
        
        SearchDocumentTableModel(Document luceneDoc, IndexReader reader, int docId) {
            this.indexReader = reader;
            data = new ArrayList<Match>();
            initFromLuceneDoc(luceneDoc);
        }
        
        SearchDocumentTableModel(SearchResult doc) {
            data = new ArrayList<Match>();
            Document luceneDoc = (Document)doc.getDocument();
            IndexReader indexReader = (IndexReader)doc.getIndexReader();
            if (indexReader != null) {
                this.indexReader = indexReader;
            }
            initFromLuceneDoc(luceneDoc);
        }
        
        private void initFromLuceneDoc(Document luceneDoc) {
            @SuppressWarnings("unchecked")
            Enumeration<Field> en = luceneDoc.fields();
            while (en.hasMoreElements()) {
                Field f = en.nextElement();
                String key = f.name();
                String value = f.stringValue();
                data.add(new Match(key, value));
            }

            // Sort the data to be helpful
            Collections.sort(data, new Comparator<Match>() {
                public int compare(Match m1,
                                   Match m2) {
                    // Sort by key, then by value - except the "method" and "attribute" keys should go to the end
                    if (m1.key.equals(m2.key)) {
                        return m1.value.compareTo(m2.value);
                    }
                    if (m1.key.equals("method")) {
                        return 1;
                    }
                    if (m2.key.equals("method")) {
                        return -1;
                    }
                    if (m1.key.equals("attribute")) {
                        return 1;
                    }
                    if (m2.key.equals("attribute")) {
                        return -1;
                    }
                    return m1.key.compareTo(m2.key);
                }
            });
        }

        SearchDocumentTableModel() {
            // Empty
        }
    
        public IndexReader getIndexReader() {
            return indexReader;
        }
    
        public int getRowCount() {
            return data == null ? 0 : data.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int col) {
            switch (col) {
            case 0: return "Key";
            case 1: return "Value";
            default: throw new IllegalArgumentException();
            }
        }

        public Class<?> getColumnClass(int col) {
            return String.class;
        }

        public boolean isCellEditable(int arg0, int arg1) {
            return false;
        }

        public Object getValueAt(int row, int col) {
            Match match = data.get(row);
            
            switch (col) {
            case 0: return match.key;
            case 1: { 
                if ("source".equals(match.key)) {
                    // Reformat URLs since they are too long
                    String name = match.value;
                    int index = name.lastIndexOf('/');
                    if (index != -1) {
                        return name.substring(index+1) + " : " + name;
                    }
                }
                return match.value;
            }
            default: throw new IllegalArgumentException();
            }
        }

        public void setValueAt(Object arg0, int arg1, int arg2) {
            throw new IllegalArgumentException();
        }

        public void addTableModelListener(TableModelListener arg0) {
            // For now, no changes occur - we just replace the whole model
        }

        public void removeTableModelListener(TableModelListener arg0) {
        }
    }
    
    class IndexListModel implements ListModel {
        private List<IndexEntry> data;
        
        IndexListModel(List<IndexEntry> entries) {
            this.data = entries;
        }
        
        IndexListModel() {
            // Empty
        }
        
        IndexEntry getEntry(int row) {
            return data != null ? data.get(row) : null;
        }
    
        public int getSize() {
            return data != null ? data.size() : 0;
        }

        public Object getElementAt(int row) {
            return getUrlLabel(data.get(row).url);
        }

        public void addListDataListener(ListDataListener arg0) {
            // The list is fixed - we'll be replacing the model whenever
            // we change it
        }

        public void removeListDataListener(ListDataListener arg0) {
        }
    }
    
    class IndexEntry {
        IndexEntry(URL url, ClassIndexImpl index, File segment) {
            this.url = url;
            this.index = index;
            this.segment = segment;
        }
        private URL url;
        private ClassIndexImpl index;
        private File segment;
    }
}
