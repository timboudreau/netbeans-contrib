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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.visualweb.project.importpage;

import org.netbeans.modules.visualweb.api.insync.InSyncService;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.w3c.tidy.Configuration;

import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
// <move>
//import com.sun.rave.css2.FacesSupport;
//import org.netbeans.modules.visualweb.css2.FormComponentBox;
// </move>


/**
 * XXX Originaly in desinger module
 * (designer/src/org/netbeans/modules/visualweb/designer/ImportPagePanel), moved away from there.
 *
 * Provide the ability to import a web page
 *
 * @todo Test that I properly handle comments, doctypes, entities, etc.
 * @todo Try to remove the Cancel button on the progress bar
 * @todo We should convert &lt;% references to &lt;jsp:scriptlet !
 * @todo If you go and put a bad file name for the input file, then make
 *     the output file bad, then make the output file good - the OK button
 *     is enabled even though the input is still bad. I should refactor
 *     such that both fields are checked before enabling the OK button!
 * @todo Instead of node-duplicating the Tidy dom, have xerces parse tidy
 *    output (which is generated during a parseDOM as it turns out so no
 *    performance penalty). It would also take away the need for the
 *    cleanEntities() hack
 * @todo How do I handle the file upload tag? And how do I create the
 *     radiobutton and checkbox lists?
 * @todo Can I convert from table to grid panel or data table?
 * @todo Do I convert img to graphic image etc. ? What about span to
 *    output text? I should only do the span thing  if it only contains
 *    text (or text and style elements)
 * @todo Make sure that the Import action frees up the page panels when
 *    done with them... or at least free up data! Perhaps have a cleanup()
 *    method called after import?  @todo Initialize textfield to homedir!
 *    and don't allow dir to be selected as filename!  Offer completion?
 * @todo The JSP to JSPX tag converter needs to handle the "page" attribute
 *    better; right now it only looks for imports.
 * @todo Clean up context handling; move manipulation of the various lists
 *    etc. into add(), get() etc. methods on the context.
 * @todo Convert meta, title, link in html to braveheart components
 * @todo Copy style attribute on from import body to our body
 *
 * @author  Tor Norbye
 */
public class ImportPagePanel extends JPanel implements DocumentListener, ActionListener {
    private ImportContext context;
    private Project project;
    private DialogDescriptor descriptor;
    
    // XXX Should these puppies be localizable?
    private String[] encodingNames = { "UTF-8", "Latin 1", "ASCII", "ISO 2022", "MacRoman" };
    private int[] encodingValues =
    {
        Configuration.UTF8, Configuration.LATIN1, Configuration.ASCII, Configuration.ISO2022,
        Configuration.MACROMAN
    };
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox convertCheckBox;
    private javax.swing.JComboBox encodingCombo;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTextField fileField;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JCheckBox formCheckBox;
    private javax.swing.JCheckBox fragmentCheckBox;
    private javax.swing.JCheckBox imageCheckBox;
    private javax.swing.JCheckBox includeCheckBox;
    private javax.swing.JTextField pageNameField;
    private javax.swing.JLabel pageNameLabel;
    // End of variables declaration//GEN-END:variables
    
    
    public ImportPagePanel() {
        context = ImportContext.getInstance();
    }
    
    /** Set the current project the dialog should be attached to */
    void setProject(Project project) {
        this.project = project;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileLabel = new javax.swing.JLabel();
        fileField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        encodingLabel = new javax.swing.JLabel();
        encodingCombo = new javax.swing.JComboBox();
        includeCheckBox = new javax.swing.JCheckBox();
        pageNameLabel = new javax.swing.JLabel();
        pageNameField = new javax.swing.JTextField();
        convertCheckBox = new javax.swing.JCheckBox();
        imageCheckBox = new javax.swing.JCheckBox();
        formCheckBox = new javax.swing.JCheckBox();
        fragmentCheckBox = new javax.swing.JCheckBox();
        errorLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(600, 300));
        fileLabel.setLabelFor(fileField);
        fileLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "FileOrURL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(fileLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(fileField, gridBagConstraints);

        browseButton.setText(NbBundle.getMessage(ImportPagePanel.class, "Browse"));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(browseButton, gridBagConstraints);

        encodingLabel.setLabelFor(encodingCombo);
        encodingLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "Encoding"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        add(encodingLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        add(encodingCombo, gridBagConstraints);

        includeCheckBox.setSelected(true);
        includeCheckBox.setText(NbBundle.getMessage(ImportPagePanel.class, "IncludeFiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(includeCheckBox, gridBagConstraints);

        pageNameLabel.setLabelFor(pageNameField);
        pageNameLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "PageName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(pageNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(pageNameField, gridBagConstraints);

        convertCheckBox.setSelected(true);
        convertCheckBox.setText(NbBundle.getMessage(ImportPagePanel.class, "ConvertHtml"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(convertCheckBox, gridBagConstraints);

        imageCheckBox.setSelected(true);
        imageCheckBox.setText(NbBundle.getMessage(ImportPagePanel.class, "ConvertImages"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 36, 0, 11);
        add(imageCheckBox, gridBagConstraints);

        formCheckBox.setSelected(true);
        formCheckBox.setText(NbBundle.getMessage(ImportPagePanel.class, "ConvertForms"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 36, 12, 11);
        add(formCheckBox, gridBagConstraints);

        fragmentCheckBox.setText(org.openide.util.NbBundle.getBundle(ImportPagePanel.class).getString("ImportFragment"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        add(fragmentCheckBox, gridBagConstraints);

        errorLabel.setForeground(java.awt.Color.red);
        errorLabel.setText("   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(errorLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    /** Invoked when the user presses the browse button */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        
        //JFileChooser chooser = new JFileChooser(experimentName);
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(ImportPagePanel.class, "BrowseTitle"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        File currentFile = getCurrentFile();
        
        if (currentFile != null) {
            if (!currentFile.isDirectory()) {
                currentFile = currentFile.getParentFile();
            }
            
            if (currentFile.exists()) {
                chooser.setCurrentDirectory(currentFile);
            }
        }
        
        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getParent() + File.separator +
                    chooser.getSelectedFile().getName());
        }
    }//GEN-LAST:event_browseButtonActionPerformed
    
    private void initAccessibility() {
        ResourceBundle bundle = NbBundle.getBundle(ImportPagePanel.class);
        
        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AddExistingPagePanel"));
        
        browseButton.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_BrowseButton"));
        convertCheckBox.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_ConvertHtml"));
        formCheckBox.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_ConvertForms"));
        imageCheckBox.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_ConvertImages"));
        fragmentCheckBox.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_ImportFragment"));
        includeCheckBox.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_IncludeFiles"));
        encodingCombo.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_Encoding"));
        fileField.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_FileOrURL"));
        pageNameField.getAccessibleContext().setAccessibleDescription(
                bundle.getString("ACSD_PageName"));
        
        fileLabel.setDisplayedMnemonic(bundle.getString("LBL_FileOrURL_Mnem").charAt(0));
        encodingLabel.setDisplayedMnemonic(bundle.getString("LBL_Encoding_Mnem").charAt(0));
        pageNameLabel.setDisplayedMnemonic(bundle.getString("LBL_PageName_Mnem").charAt(0));
        
        browseButton.setMnemonic(bundle.getString("LBL_BrowseButton_Mnem").charAt(0));
        includeCheckBox.setMnemonic(bundle.getString("LBL_IncludeFiles_Mnem").charAt(0));
        convertCheckBox.setMnemonic(bundle.getString("LBL_ConvertHtml_Mnem").charAt(0));
        imageCheckBox.setMnemonic(bundle.getString("LBL_ConvertImages_Mnem").charAt(0));
        formCheckBox.setMnemonic(bundle.getString("LBL_ConvertForms_Mnem").charAt(0));
        fragmentCheckBox.setMnemonic(bundle.getString("LBL_ImportFragment_Mnem").charAt(0));
    }
    
    /** Additional initialization of the form, no done by the NetBeans form designer */
    private void postInitComponents(File initialFile) {
        if (initialFile != null) {
            fileField.setText(initialFile.getPath());
            updateNameSuggestion(fileField.getDocument());
        } else {
            fileField.setText(System.getProperty("user.home")); // NOI18N
        }
        fileField.getDocument().addDocumentListener(this);
        pageNameField.getDocument().addDocumentListener(this);
        convertCheckBox.setEnabled(true);
        convertCheckBox.setSelected(true);
        convertCheckBox.addActionListener(this);
        
        DefaultComboBoxModel df = new DefaultComboBoxModel(encodingNames);
        encodingCombo.setModel(df);
        
        if (project != null) {
            String srcenc = JsfProjectUtils.getSourceEncoding(project);
            int encoding = Configuration.UTF8;
            
            if (srcenc.startsWith("mac") || srcenc.startsWith("Mac")) { // NOI18N
            } else if (srcenc.equalsIgnoreCase("UTF-8")) { // NOI18N
                encoding = Configuration.UTF8;
            } else if ((srcenc.indexOf("latin") != -1) || // NOI18N
                    (srcenc.indexOf("8859") != -1)) { // NOI18N
                encoding = Configuration.LATIN1;
            } else if (srcenc.indexOf("2022") != -1) { // NOI18N
                encoding = Configuration.ISO2022;
            }
            
            for (int i = 0; i < encodingValues.length; i++) {
                if (encodingValues[i] == encoding) {
                    encodingCombo.setSelectedIndex(i);
                    
                    break;
                }
            }
        }
    }
    
    public void showImportDialog(File initialFile) {
        boolean checkInitialPath = fileField == null;
        
        if (fileField == null) {
            initComponents();
            initAccessibility();
            postInitComponents(initialFile);
        } else {
            errorLabel.setText("");
        }
        
        if (initialFile != null) {
            checkInitialPath = false;
            pageNameField.requestFocus();
        }
        
        String title = NbBundle.getMessage(ImportPagePanel.class, "AddExistingPage"); // NOI18N
        DialogDescriptor dlg =
                new DialogDescriptor(this, title, true, DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION, DialogDescriptor.DEFAULT_ALIGN,
                // DialogDescriptor.BOTTOM_ALIGN,
                null, //new HelpCtx("import_web_page"), // NOI18N
                null);
        
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
        setDescriptor(dlg);
        
        //descriptor.setValid(false);
        if (checkInitialPath) {
            descriptor.setValid(false);
        } else {
            checkEditedName(pageNameField.getDocument());
            checkFileName(fileField.getText());
        }
        
        dialog.show();
        
        if (!dlg.getValue().equals(DialogDescriptor.OK_OPTION)) {
            // Cancel, or Esc: do nothing
            return;
        }
        
        URL url = null;
        final String name = pageNameField.getText();
        String fileName = fileField.getText();
        
        // URL constructor below doesn't handle this case well so convert it to filename first
        if (fileName.startsWith("file:")) { // NOI18N
            // <markup_separation>
            //            fileName = MarkupUnit.fromURL(fileName);
            // ====
            fileName = InSyncService.getProvider().fromURL(fileName);
            // </markup_separation>
        } else {
            try {
                url = new URL(fileName);
            } catch (MalformedURLException ex) {
                // We don't know if the String passed in is a URL or not!! It could be
                // a file ("C:\myfile.htm"), and it could be a URL ("http://www.sun.com").
                // We're doing a parse to see if it looks like a URL. A MalformedURLException
                // is NOT an error we should log; it just means that our test to see if it
                // was a URL has failed. (It would be better if there was a URL API to test
                // if a String will pass without relying on an exception to catch the case
                // where it is not, but alas there is no such method.)
            }
        }
        
        if ((url == null) || ((url.getProtocol() != null) && url.getProtocol().equals("file"))) {
            File file = new File(fileName);
            
            try {
                //url = file.toURL();
                url = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
                
                return;
            }
        }
        
        final boolean includeResources = includeCheckBox.isSelected();
        final boolean convert = convertCheckBox.isSelected();
        final URL furl = url;
        
        title = NbBundle.getMessage(ImportPagePanel.class, "PageImportProgress"); // NOI18N
        
        JPanel panel = new JPanel();
        java.awt.GridBagConstraints gridBagConstraints;
        JLabel jLabel1 = new javax.swing.JLabel();
        JProgressBar jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setStringPainted(false);
        panel.setLayout(new java.awt.GridBagLayout());
        
        jLabel1.setText(title);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        panel.add(jLabel1, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        panel.add(jProgressBar1, gridBagConstraints);
        
        dlg = new DialogDescriptor(panel, title, false, DialogDescriptor.DEFAULT_OPTION,
                DialogDescriptor.OK_OPTION, DialogDescriptor.DEFAULT_ALIGN,
                // DialogDescriptor.BOTTOM_ALIGN,
                null, //new HelpCtx("import_web_page"), // NOI18N
                null);
        
        final Dialog progressDialog = DialogDisplayer.getDefault().createDialog(dlg);
        setDescriptor(dlg);
        progressDialog.show();
        
        RequestProcessor.getDefault().post(new Runnable() { // separate thread
            public void run() {
                try {
                    context = new ImportContext();
                    context.project = project;
                    context.fragment = fragmentCheckBox.isSelected();
                    Import importUtil = new Import();
                    importUtil.doImport(name, furl, includeResources, convert, getEncoding(), isCopyImage(), isConvertForm(), context);
                } finally {
                    progressDialog.hide();
                }
            }
        });
    }
    
    private boolean isCopyImage() {
        return imageCheckBox.isSelected();
    }
    
    private boolean isConvertForm() {
        return formCheckBox.isEnabled();
    }
    
    private int getEncoding() {
        String sel = encodingCombo.getSelectedItem().toString();
        
        for (int i = 0; i < encodingNames.length; i++) {
            if (encodingNames[i].equals(sel)) {
                return encodingValues[i];
            }
        }
        
        return -1;
    }
    
    public void setDescriptor(DialogDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    // ---- Implements document listener
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        if (e.getDocument() == fileField.getDocument()) {
            updateNameSuggestion(e.getDocument());
        } else {
            checkEditedName(e.getDocument());
        }
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        if (e.getDocument() == fileField.getDocument()) {
            updateNameSuggestion(e.getDocument());
        } else {
            checkEditedName(e.getDocument());
        }
    }
    
    private void checkEditedName(javax.swing.text.Document doc) {
        // The user has edited the name: make sure it's valid
        try {
            String text = doc.getText(0, doc.getLength());
            validateName(text);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    private void updateNameSuggestion(javax.swing.text.Document doc) {
        // The user has changed the filename: make sure it's valid
        try {
            String text = doc.getText(0, doc.getLength());
            StringBuffer sb = new StringBuffer(text.length());
            
            int start = 0;
            
            // Ensure that we skip over paths, http://, file:, etc.
            int idx = text.lastIndexOf(File.separatorChar) + 1;
            
            if (idx > start) {
                start = idx;
            }
            
            idx = text.lastIndexOf('/');
            
            if (idx > start) {
                start = idx;
            }
            
            idx = text.lastIndexOf(':');
            
            if (idx > start) {
                start = idx;
            }
            
            for (int i = start, n = text.length(); i < n; i++) {
                char c = text.charAt(i);
                
                if (sb.length() == 0) {
                    if (Character.isJavaIdentifierStart(c)) {
                        sb.append(c);
                    }
                } else if ((c == '.') || Character.isJavaIdentifierPart(c)) {
                    sb.append(c);
                }
            }
            
            // Strip out the extension
            int dot = sb.indexOf(".");
            
            if (dot != -1) {
                sb.setLength(dot);
            }
            
            String name = sb.toString();
            pageNameField.setText(name);
            
            if (!checkFileName(text)) {
                return;
            }
            
            validateName(name);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    // TODO - check to make sure that the file doesn't already exist in the project!
    private void validateName(String name) {
        if (name.length() == 0) {
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "NoEmptyName")); // NOI18N
            
            if (descriptor != null) {
                descriptor.setValid(false);
            }
            
            return;
        }
        
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "WrongFirstLetter")); // NOI18N
            
            if (descriptor != null) {
                descriptor.setValid(false);
            }
            
            return;
        }
        
        for (int i = 1, n = name.length(); i < n; i++) {
            char c = name.charAt(i);
            
            if (!Character.isJavaIdentifierPart(c)) {
                errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "WrongPageName")); // NOI18N
            if (descriptor != null) {
                descriptor.setValid(false);
            }
                
                return;
            }
        }
        
        if ((project != null) && isUsedName(name, project)) {
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "NameUsed", name)); // NOI18N
            if (descriptor != null) {
                descriptor.setValid(false);
            }
            
            return;
        }
        
        errorLabel.setText("   "); // not empty - that will shrink the height to 0
        
        if (descriptor != null) {
            descriptor.setValid(true);
        }
    }
    
    private File getCurrentFile() {
        String name = fileField.getText();
        
        if (name.startsWith("file:")) { // NOI18N
            // <markup_separation>
            //            name = MarkupUnit.fromURL(name);
            // ====
            name = InSyncService.getProvider().fromURL(name);
            // </markup_separation>
        }
        
        File f = new File(name);
        
        if (f.exists()) {
            return f;
        }
        
        return null;
    }
    
    private boolean checkFileName(String name) {
        try {
            URL url = new URL(name);
            
            if ((url.getProtocol() != null) && !url.getProtocol().equals("file")) {
                // XXX but I could try to validate the URL, e.g. look for spaces and such!
                return true; // we don't actually try to connect
            }
        } catch (MalformedURLException ex) {
            // We don't know if the String passed in is a URL or not!! It could be
            // a file ("C:\myfile.htm"), and it could be a URL ("http://www.sun.com").
            // We're doing a parse to see if it looks like a URL. A MalformedURLException
            // is NOT an error we should log; it just means that our test to see if it
            // was a URL has failed. (It would be better if there was a URL API to test
            // if a String will pass without relying on an exception to catch the case
            // where it is not, but alas there is no such method.)
        }
        
        int lastDot = name.lastIndexOf(".");
        if (lastDot != -1) {
            String suffix = name.substring(lastDot+1);
            if (!(suffix.equalsIgnoreCase("html") || suffix.equalsIgnoreCase("htm") ||
                    suffix.equalsIgnoreCase("jsp") || suffix.equalsIgnoreCase("jspx"))) {
                errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "WrongExtension")); // NOI18N
                descriptor.setValid(false);
                
                return false;
            }
        }
        
        if (name.startsWith("file:")) { // NOI18N
            // <markup_separation>
            //            name = MarkupUnit.fromURL(name);
            // ====
            name = InSyncService.getProvider().fromURL(name);
            // </markup_separation>
        }
        
        /* I -do- support URLs now
         * NoUrlsYet=URLs not supported yet
        if (name.indexOf("http:") != -1) {
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "NoUrlsYet"));
            descriptor.setValid(false);
            return false;
        }
         */
        File f = new File(name);
        
        if (f.exists()) {
            if (f.isDirectory()) {
                errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "IsDirectory")); // NOI18N
                descriptor.setValid(false);
                
                return false;
            }
            
            return true;
        }
        
        try {
            File f2 = f.getCanonicalFile();
            
            if (f2.exists()) {
                if (f2.isDirectory()) {
                    errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "IsDirectory")); // NOI18N
                    descriptor.setValid(false);
                    
                    return false;
                }
                
                return true;
            }
            
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "NoSuchFile")); // NOI18N
            descriptor.setValid(false);
            
            return false;
        } catch (java.io.IOException ex) {
            String msg = ex.getLocalizedMessage();
            
            if (msg != null) {
                errorLabel.setText(msg);
            } else {
                errorLabel.setText(ex.toString());
            }
            
            descriptor.setValid(false);
            
            return false;
        }
    }
    
    
    private boolean isUsedName(String name, Project project) {
        FileObject webroot = JsfProjectUtils.getDocumentRoot(project);
        FileObject beanroot = JsfProjectUtils.getPageBeanRoot(project);
        
        // But what about other capitalizations of java and jsp?
        return (webroot.getFileObject(name, "jsp") != null) || // NOI18N
                (beanroot.getFileObject(name, "java") != null); // NOI18N
    }
    
    //    /**
    //     * Gets an array of urlStrings.
    //     * Adds the rules matching the element/pseudo-element of given style
    //     * declaration to the list
    //     */
    //    private static String[] getStyleResourcesFromStyleDeclaration(StyleDeclaration sd) {
    ////        Map rewrite = new HashMap();
    //        List urlStrings = new ArrayList();
    //        for (int j = 0, m = sd.size(); j < m; j++) {
    //            int idx = sd.getIndex(j);
    //
    //            if ((idx == XhtmlCss.BACKGROUND_IMAGE_INDEX) ||
    //                    (idx == XhtmlCss.LIST_STYLE_IMAGE_INDEX)) {
    //                // If I support audio: cue-before, cure-after,
    //                // play-during as well
    //                Value v = sd.getValue(j);
    //
    //                if (v instanceof URIValue) {
    //                    URIValue uv = (URIValue)v;
    //                    String urlString = uv.getRawCssText();
    //
    ////                    if (rewrite.get(urlString) == null) {
    ////                        // Import the image, as newUrl
    ////                        String projectUrl = copyResource(urlString);
    ////
    ////                        if (projectUrl != null) {
    ////                            rewrite.put(urlString, projectUrl);
    ////                        }
    ////                    }
    //                    if (urlStrings.contains(urlString)) {
    //                        continue;
    //                    }
    //
    //                    urlStrings.add(urlString);
    //                }
    //            }
    //        }
    //        return (String[])urlStrings.toArray(new String[urlStrings.size()]);
    //    }
    
    
    //    /** Gets map of urlString to relPath.
    //     * Adds the rules matching the element/pseudo-element of given style
    //     * sheet to the list.
    //     */
    //    private static ResourceData[] getStyleResourcesFromStyleSheet(Document doc, StyleSheet ss) {
    ////        Map rewrite = new HashMap();
    //        List resourceData = new ArrayList();
    //
    //        int len = ss.getSize();
    //
    //        for (int i = 0; i < len; i++) {
    //            Rule r = ss.getRule(i);
    //
    //            switch (r.getType()) {
    //            case StyleRule.TYPE:
    //
    //                StyleRule style = (StyleRule)r;
    //                StyleDeclaration sd = style.getStyleDeclaration();
    ////                rewrite.putAll(importStyleResourcesFromStyleDeclaration(depth, sd));
    //                String[] urlStrings = getStyleResourcesFromStyleDeclaration(sd);
    ////                rewrite.putAll(importStyleResources(urlStrings));
    //                resourceData.add(new UrlStringsResourceData(urlStrings));
    //                break;
    //
    //            /*case MediaRule.TYPE:*/
    //            case Impor87tRule.TYPE:
    //
    //                ImportRule mr = (ImportRule)r;
    //
    ////                XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
    ////                if (ces.mediaMatch(mr.getMediaList())) { // XXX todo
    //                if (CssProvider.getEngineService().isMediaMatchingForDocument(doc, mr.getMediaList())) {
    //
    //                    URL url = mr.getURI();
    ////                    String parent = new File(url.getPath()).getParent() + "/";
    ////                    URL oldBase = context.base;
    ////
    ////                    try {
    ////                        context.base =
    ////                            new URL(url.getProtocol(), url.getHost(), url.getPort(), parent);
    ////                    } catch (MalformedURLException mfu) {
    ////                        // XXX shouldn't happen
    ////                        ErrorManager.getDefault().notify(mfu);
    ////
    ////                        return rewrite;
    ////                    }
    ////
    ////                    String urlString = mr.getRelativeUri();
    ////                    String relPath = handleStyleSheet(depth + 1, urlString, url);
    ////                    context.base = oldBase;
    //                    String urlString = mr.getRelativeUri();
    ////                    String relPath;
    ////                    try {
    ////                        relPath = importStyleSheetResource(depth, url, urlString);
    ////                    } catch (MalformedURLException mfu) {
    ////                        // XXX shouldn't happen
    ////                        ErrorManager.getDefault().notify(mfu);
    ////                        return rewrite;
    ////                    }
    ////
    ////                    if (relPath != null) {
    ////                        rewrite.put(urlString, relPath);
    ////                    }
    //                    resourceData.add(new UrlResourceData(url, urlString));
    //                }
    //
    //                break;
    //            }
    //        }
    //
    //        return (ResourceData[])resourceData.toArray(new ResourceData[resourceData.size()]);
    //    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == convertCheckBox) {
            //boolean enabled = convertCheckBox.isEnabled();
            //if (imageCheckBox.isEnabled() != enabled) {
            // We haven't seen the change yet, so just toggle our own state instead
            boolean enabled = !imageCheckBox.isEnabled();
            imageCheckBox.setEnabled(enabled);
            formCheckBox.setEnabled(enabled);
        }
    }
    
    
    // XXX Why is this here? Is it supposed to be API? Revise.
    // Grr... tidy uses input/output stream instead of input/output writer
    public static class StringBufferOutputStream extends OutputStream {
        private StringBuffer sb;
        
        public StringBufferOutputStream(StringBuffer sb) {
            this.sb = sb;
        }
        
        public void write(int b) {
            sb.append((char)b);
        }
    }
    
}
