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
package org.netbeans.modules.codetemplatetools.ui.view;

import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.openide.ErrorManager;
import org.openide.windows.WindowManager;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CodeTemplatesPanel extends javax.swing.JPanel {

    public static void promptAndInsertCodeTemplate(JEditorPane editorPane) {
        JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(),
        "Templates",
        true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new CodeTemplatesPanel(editorPane));
        dialog.setBounds(200,200, 600, 450);
        dialog.setVisible(true);

    }

    private JEditorPane editorPane;

    /** Creates new form CodeTemplatesPanel */
    public CodeTemplatesPanel(JEditorPane editorPane) {
        initComponents();
        this.editorPane = editorPane;
        loadModel();
        mimeTypeLabel.setText(editorPane.getContentType());
        templatesList.setCellRenderer(new CodeTemplateListCellRenderer());

        templateTextEditorPane.setContentType(editorPane.getContentType());

        newButton.setIcon(Icons.NEW_TEMPLATE_ICON);

        adjustButtonState();
    }

    public void addNotify() {
        super.addNotify();

        SwingUtilities.getRootPane(this).setDefaultButton(insertButon);
    }

    private void loadModel() {
        Document doc = editorPane.getDocument();
        CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(doc);
        Collection codeTemplatesCollection = codeTemplateManager.getCodeTemplates();
        CodeTemplate[] codeTemplates = (CodeTemplate[]) codeTemplatesCollection.toArray(new CodeTemplate[0]);
        CodeTemplateListModel codeTemplateListModel = new CodeTemplateListModel(codeTemplates);
        templatesList.setModel(codeTemplateListModel);
    }

    private void insertTemplate() {
        CodeTemplate selectedCodeTemplate = (CodeTemplate) templatesList.getSelectedValue();
        if (selectedCodeTemplate != null) {
            selectedCodeTemplate.insert(editorPane);
        }
        done();
    }

    private void deleteTemplates(CodeTemplate[] templates) {
        if (templates == null) {
            return;
        }
        CodeTemplateUtils.deleteTemplates(editorPane, templates);
        loadModel();
    }

    private void close() {
        done();
    }

    private void done() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
    }

    private void adjustButtonState() {
        insertButon.setEnabled(editorPane.isEditable() && templatesList.getSelectedIndices().length == 1);
        deleteButton.setEnabled(templatesList.getSelectedIndices().length > 0);
        modifyButton.setEnabled(templatesList.getSelectedIndices().length == 1);
        templateTextEditorPane.setEnabled(templatesList.getSelectedIndices().length == 1);
    }

    private void showCodeTemplate(CodeTemplate codeTemplate) {
        if (codeTemplate == null) {
            templateTextEditorPane.setText("");
        } else {
            templateTextEditorPane.setText(codeTemplate.getParametrizedText());
        }
    }

    /** Elements */
    private static final String TAG_CODE_TEMPLATES = "codetemplates"; //NOI18N
    private static final String TAG_CODE_TEMPLATE = "codetemplate"; //NOI18N
    private static final String TAG_CODE = "code"; //NOI18N

    /** Attributes */
    private static final String ATTR_ABBREVIATION = "abbreviation"; //NOI18N
    private static final String ATTR_ACTION = "action"; //NOI18N
    private static final String ATTR_REMOVE = "remove"; //NOI18N
    private static final String ATTR_XML_SPACE = "xml:space"; //NOI18N
    private static final String VALUE_XML_SPACE = "preserve"; //NOI18N

    private void importTemplates() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (!f.isDirectory()) {
                    return false;
                }
                if (f.getName().toLowerCase().endsWith(".tmbundle")) { // NOI18N
                    return true;
                }
                return false;
            }
            public String getDescription() {
                return "TextMate Bundles (*.tmbundle)";
            }
        });
        jFileChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                if (f.getName().toLowerCase().endsWith(".xml")) { // NOI18N
                    return true;
                }
                return false;
            }
            public String getDescription() {
                return "NetBeans Abbrev files (*.xml)";
            }
        });

        if (jFileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                Element rootElement = null;
                if (file.isDirectory() && file.getName().endsWith(".tmbundle")) { // NOI18N
                    // TODO for tor
//                    // No generics - still must be compilable on 5.5
//                    String defaultMimeType = editorPane.getEditorKit().getContentType();
//                    Map/*<String,Map<String,String>>*/ propsByMime = new TmBundleImport().importBundle(file, defaultMimeType);
//                    Iterator/*<Map.Entry>*/ it = propsByMime.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry entry = (Map.Entry)it.next();
//                        String mimeType = (String)entry.getKey();
//                        Map/*<String,String>*/ props = (Map)entry.getValue();
//
//                        Lookup lookup = MimeLookup.getLookup(MimePath.get(mimeType));
//                        BaseOptions baseOptions = lookup.lookup(BaseOptions.class);
//                        Map abbreviationsMap = baseOptions.getAbbrevMap();
//                        if (abbreviationsMap == null) {
//                            abbreviationsMap = props;
//                        } else {
//                            abbreviationsMap.putAll(props);
//                        }
//                        CodeTemplateUtils.saveTemplates(editorPane, abbreviationsMap);
//                        loadModel();
//                    }
                    return;
                } else {
                    InputSource inputSource = new InputSource(new FileReader(file));
                    org.w3c.dom.Document doc = XMLUtil.parse(inputSource, false, false, null, null);
                    rootElement = doc.getDocumentElement();
                }

                if (!TAG_CODE_TEMPLATES.equals(rootElement.getTagName())) {
                    // Wrong root element
                    return;
                }
                Map properties = new HashMap();
                Map mapa = new HashMap();

                NodeList codeTemplates = rootElement.getElementsByTagName(TAG_CODE_TEMPLATE);
                int len = codeTemplates.getLength();
                for (int i=0; i < len; i++){
                    Node node = codeTemplates.item(i);
                    Element element = (Element)node;

                    if (element == null){
                        continue;
                    }

                    String abbbreviation = element.getAttribute(ATTR_ABBREVIATION);
                    String remove    = element.getAttribute(ATTR_REMOVE);
                    String expanded  = "";
                    // Skip removed
                    if (! Boolean.valueOf(remove).booleanValue()){
                        NodeList codeList = element.getElementsByTagName(TAG_CODE);
                        if (codeList.getLength() > 0) {
                            Node codeNode = codeList.item(0);
                            Node codeText = codeNode.getFirstChild();
                            if (codeText instanceof Text) {
                                expanded = ((Text) codeText).getData();
                            }
                        } else {
                            continue;
                        }
                    }

                    properties.put(abbbreviation, expanded);
                }

                if (properties.size()>0){
                    // create updated map
                    mapa.putAll(properties);

                    // remove all deleted values
                    for( Iterator i = properties.keySet().iterator(); i.hasNext(); ) {
                        String key = (String)i.next();
                        if(((String)properties.get(key)).length() == 0){
                            mapa.remove(key);
                        }
                    }
                }

                // Write templates out
                // Don't iterate and save each one, since every individual save
                // fires changes that cause a lot of work.
                //for( Iterator i = mapa.keySet().iterator(); i.hasNext(); ) {
                //    String key = (String)i.next();
                //    String value = (String) mapa.get(key);
                //    CreateCodeTemplatePanel.saveTemplate(editorPane, key, value, true);
                //}
                // Do a single batch save
                CodeTemplateUtils.saveTemplates(editorPane, mapa);
                loadModel();
            } catch (FileNotFoundException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (SAXException ex) {
                ErrorManager.getDefault().notify(ex);
            }
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

        templatesLabel = new javax.swing.JLabel();
        mimeTypeLabelLabel = new javax.swing.JLabel();
        mimeTypeLabel = new javax.swing.JLabel();
        templatesScrollPane = new javax.swing.JScrollPane();
        templatesList = new javax.swing.JList() {
            public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
                ListModel listModel = getModel();
                if (listModel instanceof CodeTemplateListModel) {
                    prefix = prefix.toLowerCase();
                    CodeTemplateListModel codeTemplateListModel = (CodeTemplateListModel) listModel;
                    int size = codeTemplateListModel.getSize();
                    int select = -1;
                    for (int i = startIndex; i < size; i++) {
                        CodeTemplate codeTemplate = (CodeTemplate) codeTemplateListModel.getElementAt(i);
                        if (codeTemplate.getAbbreviation().toLowerCase().startsWith(prefix)) {
                            select = i;
                            break;
                        }
                    }
                    if (select == -1) {
                        for (int i = 0; i < startIndex; i++) {
                            CodeTemplate codeTemplate = (CodeTemplate) codeTemplateListModel.getElementAt(i);
                            if (codeTemplate.getAbbreviation().toLowerCase().startsWith(prefix)) {
                                select = i;
                                break;
                            }
                        }
                    }
                    if (select != -1) {
                        return select;
                    }
                }
                Toolkit.getDefaultToolkit().beep();
                return -1;
            }
        };
        newButton = new javax.swing.JButton();
        modifyButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        templateTextLabel = new javax.swing.JLabel();
        templateTextScrollPane = new javax.swing.JScrollPane();
        templateTextEditorPane = new javax.swing.JEditorPane();
        buttonsPanel = new javax.swing.JPanel();
        insertButon = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        templatesLabel.setDisplayedMnemonic('s');
        templatesLabel.setText("Templates");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templatesLabel, gridBagConstraints);

        mimeTypeLabelLabel.setDisplayedMnemonic('y');
        mimeTypeLabelLabel.setText("Mime Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(mimeTypeLabelLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(mimeTypeLabel, gridBagConstraints);

        templatesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                templatesListValueChanged(evt);
            }
        });

        templatesScrollPane.setViewportView(templatesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(templatesScrollPane, gridBagConstraints);

        newButton.setMnemonic('N');
        newButton.setText("New...");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(newButton, gridBagConstraints);

        modifyButton.setMnemonic('M');
        modifyButton.setText("Modify...");
        modifyButton.setToolTipText("Modify selected template");
        modifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(modifyButton, gridBagConstraints);

        deleteButton.setMnemonic('D');
        deleteButton.setText("Delete...");
        deleteButton.setToolTipText("Delete selected template");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(deleteButton, gridBagConstraints);

        importButton.setMnemonic('p');
        importButton.setText("Import...");
        importButton.setToolTipText("IMport Code Templates");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(importButton, gridBagConstraints);

        templateTextLabel.setDisplayedMnemonic('T');
        templateTextLabel.setText("Template Text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextLabel, gridBagConstraints);

        templateTextEditorPane.setEditable(false);
        templateTextScrollPane.setViewportView(templateTextEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextScrollPane, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        insertButon.setMnemonic('I');
        insertButon.setText("Insert");
        insertButon.setToolTipText("Insert selected template");
        insertButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButonActionPerformed(evt);
            }
        });

        buttonsPanel.add(insertButon);

        closeButton.setMnemonic('C');
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        buttonsPanel.add(closeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(buttonsPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void templatesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_templatesListValueChanged
        CodeTemplate selectedCodeTemplate = null;
        if (templatesList.getSelectedIndices().length == 1) {
            selectedCodeTemplate = (CodeTemplate) templatesList.getSelectedValue();
        }
        showCodeTemplate(selectedCodeTemplate);
        adjustButtonState();
    }//GEN-LAST:event_templatesListValueChanged

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        close();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void insertButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertButonActionPerformed
        insertTemplate();
    }//GEN-LAST:event_insertButonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        importTemplates();
    }//GEN-LAST:event_importButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int templateCount = templatesList.getSelectedIndices().length;
        switch (templateCount) {
            case 0:
                return;
            case 1:
                String templateName = ((CodeTemplate) templatesList.getSelectedValue()).getAbbreviation();
                if  (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                    "Delete Code Template : " + templateName + " ?",
                    "Delete Code Template",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                    return;
                }
                break;
            default:
                if  (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                    "Delete " + templateCount + " Code Templates ?",
                    "Delete Code Templates",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                    return;
                }
                break;
        }
        Object[] selectedObjects =  templatesList.getSelectedValues();
        CodeTemplate[] selectedTemplates = new CodeTemplate[selectedObjects.length];
        System.arraycopy(selectedObjects, 0, selectedTemplates, 0, selectedObjects.length);
        deleteTemplates(selectedTemplates);
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void modifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyButtonActionPerformed
        CreateCodeTemplatePanel.modifyCodeTemplate(CodeTemplatesPanel.this.editorPane, (CodeTemplate) templatesList.getSelectedValue());
        // Templates may have been modified.
        loadModel();
    }//GEN-LAST:event_modifyButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        CreateCodeTemplatePanel.createCodeTemplate(CodeTemplatesPanel.this.editorPane);
        // New templates may have been added.
        loadModel();
    }//GEN-LAST:event_newButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton importButton;
    private javax.swing.JButton insertButon;
    private javax.swing.JLabel mimeTypeLabel;
    private javax.swing.JLabel mimeTypeLabelLabel;
    private javax.swing.JButton modifyButton;
    private javax.swing.JButton newButton;
    private javax.swing.JEditorPane templateTextEditorPane;
    private javax.swing.JLabel templateTextLabel;
    private javax.swing.JScrollPane templateTextScrollPane;
    private javax.swing.JLabel templatesLabel;
    private javax.swing.JList templatesList;
    private javax.swing.JScrollPane templatesScrollPane;
    // End of variables declaration//GEN-END:variables

}
