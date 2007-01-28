/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visualweb.project.importpage;

import org.netbeans.modules.visualweb.api.designer.cssengine.CssProvider;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData.UrlResourceData;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData.UrlStringsResourceData;
import org.netbeans.modules.visualweb.api.insync.InSyncService;
import org.netbeans.modules.visualweb.insync.InSyncServiceProvider;
import org.netbeans.modules.visualweb.insync.beans.Bean;
import org.netbeans.modules.visualweb.insync.live.BeansDesignBean;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.faces.component.UIForm;
import javax.faces.component.UIGraphic;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilder;

import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.DOMDocumentImpl;
import org.w3c.tidy.Tidy;

import com.sun.rave.faces.data.DefaultSelectItemsArray;
import org.netbeans.modules.visualweb.api.designerapi.DesignerServiceHack;
import org.netbeans.modules.visualweb.api.designer.markup.MarkupService;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
import org.netbeans.modules.visualweb.project.jsf.api.AddResourceOverwriteDialog;
// <move>
//import com.sun.rave.css2.FacesSupport;
//import org.netbeans.modules.visualweb.css2.FormComponentBox;
// </move>
import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.DesignContext;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.markup.MarkupDesignBean;
import com.sun.rave.designtime.markup.MarkupPosition;
import org.netbeans.modules.visualweb.insync.UndoEvent;
import org.netbeans.modules.visualweb.insync.faces.FacesBean;
import org.netbeans.modules.visualweb.insync.faces.FacesPageUnit;
import org.netbeans.modules.visualweb.insync.faces.FacesUnit;
import org.netbeans.modules.visualweb.insync.faces.MarkupBean;
import org.netbeans.modules.visualweb.insync.live.LiveUnit;
import org.netbeans.modules.visualweb.insync.markup.MarkupUnit;
import org.netbeans.modules.visualweb.insync.models.FacesModel;
import org.netbeans.modules.visualweb.api.designer.cssengine.XhtmlCss;
import com.sun.rave.designer.html.HtmlAttribute;
import com.sun.rave.designer.html.HtmlTag;


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
                        doImport(name, furl, includeResources, convert);
                    } finally {
                        progressDialog.hide();
                    }
                }
            });
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
            descriptor.setValid(false);

            return;
        }

        for (int i = 1, n = name.length(); i < n; i++) {
            char c = name.charAt(i);

            if (!Character.isJavaIdentifierPart(c)) {
                errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "WrongPageName")); // NOI18N
                descriptor.setValid(false);

                return;
            }
        }

        if ((project != null) && isUsedName(name, project)) {
            errorLabel.setText(NbBundle.getMessage(ImportPagePanel.class, "NameUsed", name)); // NOI18N
            descriptor.setValid(false);

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

    /** The actual import method. Will create the web forms etc. */
    private void doImport(String name, URL url, boolean includeResources, boolean convert) {
        context.project = project;
        context.fragment = fragmentCheckBox.isSelected();

        if (context.project == null) {
            return;
        }

        // Create files
        try {
            DataObject webroot = null;
            context.webformFile = DesignerServiceHack.getDefault().getCurrentFile();

//            if (context.webformFile == null) {
//                // XXX TODO I should ensure that I can pass in a null parent here
//                // (to JsfProjectUtils.addResource) to place resources at the document
//                // root!
//            }

            try {
                webroot = DataObject.find(JsfProjectUtils.getDocumentRoot(context.project));
            } catch (DataObjectNotFoundException dnfe) {
                ErrorManager.getDefault().notify(dnfe);
            }

            context.copyResources = includeResources;

            DataFolder folderObj = (DataFolder)webroot;
            FileSystem fs = Repository.getDefault().getDefaultFileSystem();

            String tmpl;
            if (context.fragment) {
                tmpl = "Templates/JsfApps/PageFragment.jspf"; // NOI18N
            } else {
                tmpl = "Templates/JsfApps/JsfPage.jsp"; // NOI18N
                tmpl = "Templates/JsfApps/Page.jsp"; // NOI18N
            }
            FileObject fo = fs.findResource(tmpl);

            if (fo == null) {
                throw new IOException("Can't find template FileObject for " + tmpl); // NOI18N
            }

            DataObject webformTemplate = DataObject.find(fo);
            DataObject webformDobj = webformTemplate.createFromTemplate(folderObj, name);
            context.webformDobj = webformDobj;
            context.webformFile = webformDobj.getPrimaryFile();

            // Now go and edit the heck out of it
            org.netbeans.modules.visualweb.insync.Util.retrieveDocument(webformDobj.getPrimaryFile(), true);

            // XXX TODO grab atomic lock
            Document cleanDoc = cleanup(url);
            context.parsedDocument = cleanDoc;

            if (cleanDoc == null) {
                return;
            }
            
            MarkupService.markJspxSource(cleanDoc);

            FacesModel model = FacesModel.getInstance(context.webformFile);
            if(model == null) {
                return;
            }
//            WebForm webform = DesignerUtils.getWebForm(webformDobj, true);
//
//            if (webform == null) {
//                return;
//            }
//
//            context.webform = webform;

            // The rest of the import deals with insync models and has to happen 
            // on the AWT thread (insync requirement)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        // The cursor change doesn't seem to have any effect:
                        //setCursor(org.openide.util.Utilities.createProgressCursor(ImportPagePanel.this));

                        finishImportSafely();
                    } finally {
                        //setCursor(null);
                        context = null;
                    }
                }
            });
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);

            String message = NbBundle.getMessage(ImportPagePanel.class, "ImportFailed"); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }
            
    /** The actual import method. Will create the web forms etc. */
    private void finishImportSafely() {
        assert SwingUtilities.isEventDispatchThread();
        
        try {
            FileObject webformFile = context.webformFile;
            FacesModel model = getModel(webformFile);

            if (model == null) {
                return;
            }

            // Ensure that we start off in a clean state
            model.sync();

            UndoEvent undoEvent = null;

            try {
                undoEvent = model.writeLock(null);

                // Copy pieces from the body and from the head into the corresponding places in the document
                insertPortions(context, context.parsedDocument);

                /*
                if (doc instanceof BaseDocument)
                    ((BaseDocument)doc).atomicLock();
                try {

                    String cleanedup = cleanup(url);
                    if (cleanedup == null) {
                        return;
                    }

                    doc.remove(0, doc.getLength());
                    doc.insertString(0, cleanedup, null);
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(e);
                } finally {
                    if (doc instanceof BaseDocument)
                        ((BaseDocument)doc).atomicUnlock();
                }
                 */
                if (convertCheckBox.isSelected()) {
                    convertCompsToJsf(context);
                }
            } finally {
                model.writeUnlock(undoEvent);
            }
            
            // Force a global element rebuild. This is necessary to ensure that
            // all the source<->render references are correct since we've been
            // doing surgery on the DOM. Without this, you might for example
            // import the Sun homepage, but you can't click on text nodes and
            // have the caret position itself inside the word until you hit Refresh.
            // This MAY be related to
            //  http://jupiter.czech.sun.com/wiki/view/Creator/InSyncAi1067
// <move> XXX Bad API.
//            webform.getActions().refresh(true);
// ====
//            DesignerServiceHack.getDefault().refresh(null, context.webformDobj, true);
//            DesignerServiceHack.getDefault().refreshDataObject(context.webformDobj, true);
            // There is needed to refresh only the insync part, designer is not opened yet.
            model.refresh(true);
// </move>
            

            OpenCookie open = (OpenCookie)context.webformDobj.getCookie(OpenCookie.class);

            if (open != null) {
                open.open();
            }

            if (context.haveOldJsp) {
//                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
//                        "JspConversionWarning")); // NOI18N
                IllegalStateException ex = new IllegalStateException("Old jsp"); // NOI18N
                Throwable th = ErrorManager.getDefault().annotate(ex, NbBundle.getMessage(ImportPagePanel.class, "JspConversionWarning"));
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, th);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);

            String message = NbBundle.getMessage(ImportPagePanel.class, "ImportFailed"); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    private boolean isUsedName(String name, Project project) {
        FileObject webroot = JsfProjectUtils.getDocumentRoot(project);
        FileObject beanroot = JsfProjectUtils.getPageBeanRoot(project);

        // But what about other capitalizations of java and jsp?
        return (webroot.getFileObject(name, "jsp") != null) || // NOI18N
        (beanroot.getFileObject(name, "java") != null); // NOI18N
    }

    /** Given a URL to an online document or a file, read the file, parse it,
     * translate resource strings (and import the resources into the project in the process)
     * and returned the cleaned up dom contents.
     */
    private Document cleanup(URL resourceURL) {
        InputStream is = null;

        try {
            is = resourceURL.openStream();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);

            String message =
                NbBundle.getMessage(ImportPagePanel.class, "URLAccessFailed", resourceURL.toString()); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);

            return null;
        }

        try {
            String name = resourceURL.toString().toLowerCase();
            boolean isJsp =
                name.endsWith(".jsp") || name.endsWith(".jspx") || // NOI18N
                name.endsWith(".jspf"); // NOI18N
            Tidy tidy = getTidy(isJsp, getEncoding());
            Document dom = rewrite(tidy, is);

            if (dom == null) {
                return null;
            }

            Document target = null;

            try {
                org.xml.sax.InputSource is2 =
                    new org.xml.sax.InputSource(new StringReader(
                            "<jsp:root version=\"1.2\" xmlns:f=\"http://java.sun.com/jsf/core\" " +
                            "xmlns:h=\"http://java.sun.com/jsf/html\" xmlns:jsp=\"http://java.sun.com/JSP/Page\"><jsp:directive.page contentType=\"text/html;charset=UTF-8\"/><f:view/></jsp:root>"));

                // I do need CSS handling now that I'm parsing style elements
                boolean css = true;
                DocumentBuilder parser = MarkupService.createRaveSourceDocumentBuilder(css);
                target = parser.parse(is2);
            } catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);

                return null;
            } catch (org.xml.sax.SAXException e) {
                ErrorManager.getDefault().notify(e);

                return null;
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                ErrorManager.getDefault().notify(e);

                return null;
            }

            /*
            Node html = null;
            NodeList nl = dom.getChildNodes();
            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    html = node;
                    break;
                }
            }
            if (html == null) {
                return null;
            }
             */

            //Node html = dom.getDocumentElement();
            NodeList children = dom.getElementsByTagName(HtmlTag.HTML.name);

            // jsp:root serves the same role when importing JSP documents:
            // contains name space lists etc.
            if (children.getLength() < 1) {
                children = dom.getElementsByTagName("jsp:root");
            }

            if (children.getLength() < 1) {
                String message = NbBundle.getMessage(ImportPagePanel.class, "NoHtmlElement"); // NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(message);
                d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);

                return null;
            }

            Node html = children.item(0);

            // Strip xmlns stuff off the html element since it causes insync to fail
            // (Can't insert default form beans etc.)
            if (html.getNodeType() == Node.ELEMENT_NODE) {
                Element he = (Element)html;
                he.removeAttribute("xmlns"); // NOI18N
                
                NamedNodeMap nnm = he.getAttributes();
                int num = nnm.getLength();
                Map map = new HashMap();
                context.nameSpaces = map;
                
                // Look for other namespace attributes to duplicate to the
                // jsp root
                for (int i = 0; i < num; i++) {
                    Node a = nnm.item(i); // XXX move element.getAttributes out of loop

                    String attribute = a.getNodeName();
                    if (attribute.startsWith("xmlns:")) { // NOI18N
                        map.put(attribute, a.getNodeValue());
                    }
                }
                
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    String attribute = (String)it.next();
                    he.removeAttribute(attribute);
                }
            }

            // Do surgery to insert our own stuff
            children = target.getElementsByTagName("f:view"); // NOI18N

            if (children.getLength() < 1) {
                return null;
            }

            Element fview = (Element)children.item(0);
            Node copy = importNode(target, html);
            fview.appendChild(copy);

            // Hack
            Tidy.cleanEntities(copy, !isJsp);

            // Add taglibs from the top of the document
            if (dom instanceof DOMDocumentImpl) {
                List nodes = ((DOMDocumentImpl)dom).getJspStartNodes();

                if (nodes != null) {
                    Iterator it = nodes.iterator();

                    while (it.hasNext()) {
                        Node node = (Node)it.next();
                        String data = node.getNodeValue();
                        getJspxElementFromJsp(target, data);

                        // We don't use the nodes here since they are outside
                        // the body, but as a side effect, the tagLibs list
                        // may be modified
                    }
                }
            }

            if (context.tagLibs != null) {
                int i = 0;
                Element jspRoot = target.getDocumentElement();

                while (i < context.tagLibs.size()) {
                    String xmlns = (String)context.tagLibs.get(i++);
                    String uri = (String)context.tagLibs.get(i++);

                    if (!jspRoot.hasAttribute(xmlns)) {
                        jspRoot.setAttribute(xmlns, uri);
                    }
                }
            }

            context.fullUrl = resourceURL;
            context.base = computeBase(target, resourceURL);

            if (context.copyResources) {
                context.resources = new HashMap(50);
                int oldMode = AddResourceOverwriteDialog.getMode();
                try {
                    // Let users respond with "yes to all" or "no to all" on file conflicts
                    AddResourceOverwriteDialog.setMode(AddResourceOverwriteDialog.CONFLICT_ASK_MANY);
                    
                    copyResources(target.getDocumentElement());
                } finally {
                    AddResourceOverwriteDialog.setMode(oldMode);
                }
            }

            //String result = FacesSupport.getHtmlStream(target);
            //return result;
            return target;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
    }

    /** Copy the resources from the given URL to the given project */
    private void copyResources(Node node) {
        // TODO: iterate over the DOM, looking for resources I should copy,
        // and rewrite the URLs if necessary and copy the resource
        if (!(node instanceof Element)) {
            return;
        }

        Element element = (Element)node;
        String tag = element.getTagName();

        // What about jsp include??
        if (HtmlTag.IMG.name.equals(tag)) {
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.SCRIPT.name.equals(tag) &&
                (element.getAttribute(HtmlAttribute.SRC).length() > 0)) {
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.STYLE.name.equals(tag)) {
            handleStyleSheet(0, element, null);
        } else if (HtmlTag.LINK.name.equals(tag) &&
                "stylesheet".equalsIgnoreCase(element.getAttribute(HtmlAttribute.REL))) { // NOI18N
            handleStyleSheet(0, element, HtmlAttribute.HREF);
        } else if (HtmlTag.INPUT.name.equals(tag) &&
                "image".equals(element.getAttribute(HtmlAttribute.TYPE))) { // NOI18N
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.OBJECT.name.equals(tag) &&
                (element.getAttribute(HtmlAttribute.SRC).length() > 0)) {
            copyResource(element, HtmlAttribute.SRC);
        }

        if (element.getAttribute(HtmlAttribute.STYLE).length() > 0) {
            handleStyleSheet(0, element, HtmlAttribute.STYLE);
        }

        // XXX what about <frame> and <iframe> -- should I copy these too?
        // More likely I should recursively import them, not just copy, such
        // that their images are also copied. I should use the same name map
        // to make sure I don't duplicate copies of shared resources, and
        // I can use this to make sure that I don't get in infinite recursion
        // as well by putting my own url in there so that other pages referring
        // to me simply use the new url value returned by the resources map!
        NodeList nl = element.getChildNodes();

        for (int i = 0, n = nl.getLength(); i < n; i++) {
            copyResources(nl.item(i));
        }
    }

    /** Rewrite the given url attribute for the given element to
     * point to a local copy of the resource, and create that local
     * copy. Take the URL from the given element and attribute,
     * and store the new project relative uri in there when done. */
    private void copyResource(Element element, String urlAttribute) {
        // Look up the url
        String urlString = element.getAttribute(urlAttribute);

        if (urlString.length() == 0) {
            return;
        }

        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            // Rewrite html url
            element.setAttribute(urlAttribute, (String)context.resources.get(urlString));

            return;
        }

        String projectUrl = copyResource(urlString);

        if (projectUrl != null) {
            element.setAttribute(urlAttribute, projectUrl);
        }
    }

    /** Rewrite the given url attribute for the given element to
     * point to a local copy of the resource, and create that local
     * copy. */
    private String copyResource(String urlString) {
        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            return (String)context.resources.get(urlString);
        }

        URL url;

        try {
            url = new URL(context.base, urlString);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);

            return null;
        }

        // Copy resource into project
        try {
            String projectPath = null;

            // XXX Shouldn't JsfProjectUtils take a project parameter?
            projectPath = JsfProjectUtils.addResource(context.webformFile, url, true);

            if (projectPath == null) {
                // XXX what do we do?  The user has cancelled when
                // there was a conflict warning in the PM - but the
                // PM returned null so we don't know exactly what the
                // conflict path was -- I'd like to use it here
                // Does this also happen if we pass in a bogus url (e.g. a src to an image
                // where the image doesn't actually exist?)
                return null;
            }

            String projectUrl = MarkupUnit.toURL(projectPath);
            context.resources.put(urlString, projectUrl);

            return projectUrl;
        } catch (java.io.FileNotFoundException fnfe) {
            // Try with full url as base instead; see comment in computeBase() for why
            // this is necessary
            URL fullUrl = context.fullUrl;

            try {
                //url = new URL(fullUrl, urlString);
                url = new URL(fullUrl.getProtocol(), fullUrl.getHost(), fullUrl.getPort(),
                        fullUrl.getFile() + "/" + urlString);
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);

                return null;
            }

            // Copy resource into project
            try {
                String projectPath = null;

                // XXX Shouldn't JsfProjectUtils take a project parameter?
                projectPath = JsfProjectUtils.addResource(context.webformFile, url, true);

                if (projectPath == null) {
                    // XXX what do we do?  The user has cancelled when
                    // there was a conflict warning in the PM - but the
                    // PM returned null so we don't know exactly what the
                    // conflict path was -- I'd like to use it here
                    return null;
                }

                String projectUrl = MarkupUnit.toURL(projectPath);

                // Rewrite html url
                context.resources.put(urlString, projectUrl);

                return projectUrl;
            } catch (java.net.UnknownHostException uhe) {
                // Open the output window instead and tell the user that the resource couldn't
                // be imported
                if (context.warnMissingFile) {
//                    InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
//                            "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString)); // NOI18N
                    IllegalStateException ise = new IllegalStateException(uhe);
                    Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString));
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, uhe);
                }
            } catch (java.io.FileNotFoundException fnfe2) {
                if (context.warnMissingFile) {
//                    InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
//                            "NoSuchResource", urlString)); // NOI18N
                    IllegalStateException ise = new IllegalStateException(fnfe2);
                    Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", urlString));
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, fnfe2);
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } catch (java.net.UnknownHostException uhe) {
            // Open the output window instead and tell the user that the resource couldn't
            // be imported
            if (context.warnMissingFile) {
//                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
//                        "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString)); // NOI18N
                IllegalStateException ise = new IllegalStateException(uhe);
                Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString));
                ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
            } else {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, uhe);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }

        return null;
    }

    /**
     * Returns the location to resolve relative URLs against.  By
     * default this will be the document's URL if the document
     * was loaded from a URL.  If a base tag is found and
     * can be parsed, it will be used as the base location.
     *
     * @return the base location
     */
    public URL computeBase(Document document, URL fullUrl) {
        // First see if we have a <base> tag within the <head>
        URL base = null;

        // TODO - gather ALL <base> elements within the head
        // and process them
        Element root = document.getDocumentElement();
        Element html = findHtmlTag(root);

        if (html != null) {
            Element head = findElement(HtmlTag.HEAD.name, html);

            if (head != null) {
                Element baseElement = findElement(HtmlTag.BASE.name, head);

                if (baseElement != null) {
                    String href = baseElement.getAttribute(HtmlAttribute.HREF);

                    if ((href != null) && (href.length() > 0)) {
                        try {
                            base = new URL(href);

                            return base;
                        } catch (MalformedURLException ex) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                        }
                    }
                }
            }
        }

        // No <base>, so use the URL of the document file itself
        // and use that to resolve relative URLs.
        // However, we can't simply strip off the basename and use that
        // as the base, because of "ambiguous" urls, such as
        // 'http://wwws.sun.com/software/products/jscreator'. The REAL
        // URL to the resource is
        // 'http://wwws.sun.com/software/products/jscreator/index.html'
        // but we don't know that -- the HTTP server on the receiving end
        // will check if the pointed to URL is really a directory, and if
        // so pick a default file (typically index.html) within it.
        // Unfortunately it's hard for me to detect if this is the case.
        // I thought I could just try to add "/index.html" to the given
        // URL and see if that "exists", but that won't work either --
        // the web server will redirect this request to some other page
        // (perhaps an error page) and serve that back, and I can't tell
        // that content from regular content. So instead the caller will
        // simply have to use the fullURL as a "backup" url to try
        // when copying a resource fails.
        try {
            String file = new File(fullUrl.getFile()).getParent() + "/";
            base = new URL(fullUrl.getProtocol(), fullUrl.getHost(), fullUrl.getPort(), file);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }

        return base;
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
    
    private Map importStyleResources(String[] urlStrings) {
        Map rewrite = new HashMap();
        
        for(int i = 0; i < urlStrings.length; i++) {
            String urlString = urlStrings[i];
            // Import the image, as newUrl
            String projectUrl = copyResource(urlString);
            if (projectUrl != null) {
                rewrite.put(urlString, projectUrl);
            }
        }
        return rewrite;
    }
    
    private Map importStyleResources(int depth, ResourceData[] resourceData) {
        Map rewrite = new HashMap();
        for (int i = 0; i < resourceData.length; i++) {
            ResourceData rd = resourceData[i];
            if (rd instanceof UrlStringsResourceData) {
                UrlStringsResourceData urlStringsResourceData = (UrlStringsResourceData)rd;
                rewrite.putAll(importStyleResources(urlStringsResourceData.getUrlStrings()));
            } else if (rd instanceof UrlResourceData) {
                UrlResourceData urlResourceData = (UrlResourceData)rd;
                String relPath;
                try {
                    relPath = importStyleSheetResource(depth, urlResourceData.getUrl(), urlResourceData.getUrlString());
                } catch (MalformedURLException mfu) {
                    // XXX shouldn't happen
                    ErrorManager.getDefault().notify(mfu);
                    return rewrite;
                }

                if (relPath != null) {
                    rewrite.put(urlResourceData.getUrlString(), relPath);
                }
            } else {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        new IllegalStateException("Unexpected resourceData=" + rd)); // NOI18N
            }
        }
        return rewrite;
    }

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
//            case ImportRule.TYPE:
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
    
    
    private String importStyleSheetResource(int depth, URL url, String urlString) throws MalformedURLException {
        String parent = new File(url.getPath()).getParent() + "/";
        URL oldBase = context.base;

        context.base = new URL(url.getProtocol(), url.getHost(), url.getPort(), parent);

//        String urlString = mr.getRelativeUri();
        String relPath = handleStyleSheet(depth + 1, urlString, url);
        context.base = oldBase;
        return relPath;
    }

    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private void handleStyleSheet(int depth, Element element, String urlAttribute) {
        if (HtmlAttribute.STYLE.equals(urlAttribute)) {
            String rules = element.getAttribute(HtmlAttribute.STYLE);

            if (rules.length() == 0) {
                return;
            }

////            Document doc = MarkupUnit.createEmptyDocument(true);
////
////            // XXX I should share the engine for all the stylesheets
//////            doc.setUrl(context.base);
////            InSyncService.getProvider().setUrl(doc, context.base);
////
////            // <markup_separation>
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
////            // ====
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
//////            // </markup_separation>
//////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
//////            if (doc != null) {
////////                doc.setCssEngine(ces);
//////                CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
//////            }
////            CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
//////            XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
////// </moved from engine impl>
////
//////            StyleDeclaration sd = ces.parseStyleDeclaration((RaveElement)element, rules);
////            StyleDeclaration sd = CssProvider.getEngineService().parseStyleDeclarationForElement(element, rules);
////                    
////            Map rewrite = importStyleResources(depth, sd);
            Map rewrite = getStyleResourcesForElement(element, rules);

            if (rewrite.size() > 0) {
                String newStylesheet = replaceStrings(rewrite, rules);
                element.setAttribute(HtmlAttribute.STYLE, newStylesheet);
            }

            return;
        } else if (element.getTagName().equals(HtmlTag.STYLE.name)) {
            String rules = MarkupService.getStyleText(element);

            if (rules.length() == 0) {
                return;
            }

////            Document doc = MarkupUnit.createEmptyDocument(true);
////
////            // XXX I should share the engine for all the stylesheets
//////            doc.setUrl(context.base);
////            InSyncService.getProvider().setUrl(doc, context.base);
////
////            // <markup_separation>
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
////            // ====
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
//////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
//////            // </markup_separation>
//////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
//////            if (doc != null) {
////////                doc.setCssEngine(ces);
//////                CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
//////            }
////            CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
//////            XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
////// </moved from engine impl>
////            InputSource is = new InputSource(new StringReader(rules));
//////            StyleSheet ss = ces.parseStyleSheet(is, context.base, "all", context.base);
////            StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, context.base, "all", context.base); // NOI18N
////
////            Map rewrite = importStyleResources(depth, doc, ss);
            Map rewrite = getStyleResources(rules, depth);

            if (rewrite.size() > 0) {
                String newStylesheet = replaceStrings(rewrite, rules);

                // Replace child nodes of the style element
                while (element.getChildNodes().getLength() > 0) {
                    element.removeChild(element.getFirstChild());
                }

                element.appendChild(element.getOwnerDocument().createComment(newStylesheet));
            }

            return;
        }

        String urlString = element.getAttribute(urlAttribute);

        if (urlString.length() == 0) {
            return;
        }

        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            // Rewrite html url
            element.setAttribute(urlAttribute, (String)context.resources.get(urlString));

            return;
        }

        String relPath = handleStyleSheet(depth, urlString);

        if (relPath != null) {
            element.setAttribute(urlAttribute, relPath);
        }
    }

    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private String handleStyleSheet(int depth, String urlString) {
        URL url;

        try {
            url = new URL(context.base, urlString);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);

            return null;
        }

        return handleStyleSheet(depth, urlString, url);
    }

    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private String handleStyleSheet(int depth, String urlString, URL url) {
        if (depth > 15) {
            // Probably circular references!
            // XXX Probably???
            return null;
        }

////        // XXX do I have to do the context.fullUrl trick here too, as in copyResource????
////        // Parse the stylesheet
////        Document doc = MarkupUnit.createEmptyDocument(true);
//////        doc.setUrl(context.base);
////        InSyncService.getProvider().setUrl(doc, context.base);
////
////        // XXX I should share the engine for all the stylesheets
////        // <markup_separation>
//////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
////        // ====
//////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
//////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
//////        // </markup_separation>
//////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
//////        if (doc != null) {
////////            doc.setCssEngine(ces);
//////            CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
//////        }
////        CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
//////        XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
////// </moved from engine impl>
////        InputSource is = new InputSource(url.toString());
//////        StyleSheet ss = ces.parseStyleSheet(is, url, "all", url);
////        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, url, "all", url); // NOI18N
////
////        Map rewrite = importStyleResources(depth, doc, ss);
        Map rewrite = getStyleResources(url, depth);
        
        URL oldBase = context.base;
        context.base = url;
        context.base = oldBase;

        try {
            String newStylesheet = replaceStrings(rewrite, read(url));

            String name = urlString.substring(urlString.lastIndexOf('/') + 1);

            if (name.indexOf('.') == -1) {
                name = name + ".css"; // NOI18N
            }

            // TODO - store in the resources folder instead
            FileObject webFolder = null;
            webFolder = JsfProjectUtils.getDocumentRoot(context.project);

            File targetFile = new File(FileUtil.toFile(webFolder), name);

            if (targetFile.exists()) {
                AddResourceOverwriteDialog d = new AddResourceOverwriteDialog(targetFile);
                d.showDialog();

                File newTarget = d.getFile();

                if (newTarget == null) {
                    // User pressed cancel - just use the original name
                    return name;
                }
                
                if (newTarget.exists()) {
                    //return linkRef;
                    return name;
                }

                name = newTarget.getName();

                /*
                if (mimeDir != null) {
                    linkRef = mimeDir + "/" + fileName;  // NOI18N
                } else {
                    linkRef = fileName;
                }
                */
            }

            DataObject ssdo = addSource(webFolder, newStylesheet, name);
            String s = ssdo.getPrimaryFile().getNameExt();

            // XXX get full name
            String relPath = s;

            return relPath;
        } catch (IOException ex) {
            if (context.warnMissingFile) {
//                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
//                        "NoSuchResource", ex.getLocalizedMessage() + ": " + urlString)); // NOI18N
                IllegalStateException ise = new IllegalStateException(ex);
                Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", ex.getLocalizedMessage() + ": " + urlString));
                ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
            } else {
                ErrorManager.getDefault().notify(ex);
            }
        }

        /*
        // Now I just need to change the resourceURL to something temporary
        // that I can copy from
        try {
            URL resourceURL = url;
            String projectUrl = WebAppProject.addResource(webform, resourceURL, true);
            element.setAttribute(urlAttribute, projectUrl);
            return projectUrl;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
        */
        return null;
    }

    /** Replace the keys in oldStylesheet with values from the hashmap. */
    private String replaceStrings(Map rewrite, String oldStylesheet) {
        Set keySet = rewrite.keySet();
        String newStylesheet = oldStylesheet;

        if (keySet.size() > 0) {
            String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
            String[] values = new String[keys.length];

            for (int i = 0; i < keys.length; i++) {
                values[i] = (String)rewrite.get(keys[i]);
            }

            StringBuffer sb = new StringBuffer(oldStylesheet.length() + 200);
            int m = keys.length;
outer: 
            for (int i = 0, n = oldStylesheet.length(); i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (oldStylesheet.startsWith(keys[j], i)) {
                        // Replace here!
                        sb.append(values[j]);
                        i += (keys[j].length() - 1); // -1: since we're going to add in for loop

                        continue outer;
                    }
                }

                sb.append(oldStylesheet.charAt(i));
            }

            newStylesheet = sb.toString();
        }

        return newStylesheet;
    }

    private String read(URL url) throws IOException {
        StringBuffer sb = new StringBuffer(1000); // XXX is there a utility method for this?
        InputStream is = null;
        BufferedInputStream in = null;

        try {
            is = url.openStream();
            in = new BufferedInputStream(is);

            int c;

            while ((c = in.read()) != -1)
                sb.append((char)c);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }

        return sb.toString();
    }

    /** Locate an element of the given tag name as a direct child of
     * the given parent. If no element of that tag name is found it
     * will either return null if the create flag is false, or if the
     * create flag is true, the element will be created and inserted
     * before it is returned.
     * @todo Move to Utilities?
     * @param tag The tag name of the tag to be found or created
     * @param parent The element parent under which we want to search
     * @param create If true, create the element if it doesn't exist,
     *    otherwise return null if the tag is not found.
     */
    private Element findElement(String tag, Node parent) {
        NodeList list = parent.getChildNodes();
        int len = list.getLength();

        for (int i = 0; i < len; i++) {
            org.w3c.dom.Node child = list.item(i);

            if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element)child;

                if (tag.equals(element.getTagName())) {
                    return element;
                }
            }
        }

        return null;
    }

    /** Locate the &lt;html&gt; tag. In a normal xhtml/html document, it's
     * the same as the root tag for the DOM, but in our JSF files, it
     * might be nested within &lt;jsp:root&gt;, &lt;f:view&gt;, etc.
     * @param parent The root tag
     * @todo Just pass in the Document node instead?
     * @todo Move to Utilities?
     * @return The html tag Element
     */
    private Element findHtmlTag(Node root) {
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)root;

            if (HtmlTag.HTML.name.equals(element.getTagName())) { // Don't allow "HTML"

                return element;
            }
        }

        NodeList list = root.getChildNodes();
        int len = list.getLength();

        for (int i = 0; i < len; i++) {
            Node child = list.item(i);
            Element match = findHtmlTag(child);

            if (match != null) {
                return match;
            }
        }

        return null;
    }

    /** Import the given node into this document (but don't parent it).
     * For some freakin' reason, Xerces throws NPEs when I try to import nodes
     * from JTidy's DOM. Not sure why; perhaps its the namespace stuff that's
     * improperly implemented in JTidy's DOM implementation.
     */
    private Node importNode(Document target, Node node) {
        // Can't just use DOM's own method - xerces barfs:
        //return target.getDocumentElement().appendChild(target.importNode(node, true));
        Node curr = null;

        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:

            Element e = (Element)node;
            Element ec = target.createElement(e.getTagName());
            curr = ec;

            // Copy attributes
            int num = e.getAttributes().getLength();

            for (int i = 0; i < num; i++) {
                Node a = e.getAttributes().item(i);
                ec.setAttribute(a.getNodeName(), a.getNodeValue());
            }

            break;

        case Node.TEXT_NODE:
            curr = target.createTextNode(node.getNodeValue());

            break;

        case Node.ENTITY_REFERENCE_NODE:
            curr = target.createEntityReference(node.getNodeValue());

            break;

        case Node.ENTITY_NODE:

            // We won't copy the entity into our document
            break;

        case Node.COMMENT_NODE:
            curr = target.createComment(node.getNodeValue());

            break;

        case Node.CDATA_SECTION_NODE:
            curr = target.createCDATASection(node.getNodeValue());

            break;

        // XXX what to do, what to do?
        //break;
        case 115: // XXX special hack. See  org.w3c.tidy.DOMNodeImpl's getNodeType()

            String data = node.getNodeValue();
            curr = getJspxElementFromJsp(target, data);

            break;

        case Node.ATTRIBUTE_NODE:
        case Node.PROCESSING_INSTRUCTION_NODE:
        case Node.DOCUMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.NOTATION_NODE:default:

            // TODO - check for other character data, ... others?
            ErrorManager.getDefault().log("Not processing node " + node);
        }

        NodeList nl = node.getChildNodes();

        for (int i = 0, n = nl.getLength(); i < n; i++) {
            Node child = importNode(target, nl.item(i));

            if (child != null) {
                curr.appendChild(child);
            }
        }

        return curr;
    }

    /*
     * Convert:
     * <pre>
        Regular JSP page          JSP Document
        <%@ page attribute list %>         <jsp:directive.page attribute list />
        <%@ include file="path" %>         <jsp:directive.include file="path" />
        <%! declaration %>         <jsp:declaration>declaration</jsp:declaration>
        <%= expression %>         <jsp:expression>expression</jsp:expression>
        <% scriptlet %>         <jsp:scriptlet>scriptlet</jsp:scriptlet>
     </pre>
     *
     */
    private Node getJspxElementFromJsp(Document document, String data) {
        context.haveOldJsp = true;

        if (data.startsWith("@")) {
            // Look for taglib
            String s = data;

            if (s.startsWith("@ taglib ")) {
                // taglib
                // Find prefix and uri
                int n = s.length();
                int prefix = s.indexOf(" prefix=\"");
                int uri = s.indexOf(" uri=\"");

                if ((prefix != -1) && (uri != -1)) {
                    StringBuffer p = new StringBuffer();

                    for (int i = prefix + 9; i < n; i++) {
                        char c = s.charAt(i);

                        if ((c == '"') || !Character.isLetter(c)) {
                            break;
                        }

                        p.append(c);
                    }

                    StringBuffer u = new StringBuffer();

                    for (int i = uri + 6; i < n; i++) {
                        if (s.charAt(i) == '"') {
                            break;
                        }

                        u.append(s.charAt(i));
                    }

                    if ((p.length() > 0) && (u.length() > 0)) {
                        String xmlns = "xmlns:" + p.toString(); // NOI18N

                        if (context.tagLibs == null) {
                            context.tagLibs = new ArrayList();
                        }

                        context.tagLibs.add(xmlns);
                        context.tagLibs.add(u.toString());

                        /*
                        if (!jspRoot.hasAttribute(xmlns)) {
                            jspRoot.setAttribute(xmlns, u.toString());
                        }
                         */
                    }
                }

                // TODO: }  else if (s.startsWith("@ include ")) {
            } else if (s.startsWith("@ page ")) {
                // Find the attribute list
                // XXX. this is wrong, I should change this to do arbitrary attribute list conversion
                int n = s.length();
                int imp = s.indexOf(" import=\"");

                if (imp != -1) {
                    StringBuffer p = new StringBuffer();

                    for (int i = imp + 9; i < n; i++) {
                        char c = s.charAt(i);

                        if ((c == '"') || !Character.isLetter(c)) {
                            break;
                        }

                        p.append(c);
                    }

                    if (p.length() > 0) {
                        Element e = document.createElement("jsp:directive.page");
                        e.setAttribute("import", p.toString());

                        return e;
                    }
                }
            }
        } else if (data.startsWith("!")) {
            Element e = document.createElement("jsp:declaration");
            e.appendChild(document.createTextNode(data.substring(1))); // skip !

            return e;
        } else if (data.startsWith("=")) {
            Element e = document.createElement("jsp:expression");
            e.appendChild(document.createTextNode(data.substring(1))); // skip !

            return e;
        } else {
            // Just a scriptlet
            Element e = document.createElement("jsp:scriptlet");
            e.appendChild(document.createTextNode(data));

            return e;
        }

        return null;
    }

    /** Based closely on WebAppProject.addResource, but adds to the source folder instead. (The "source" folder can be anything, not necessary just the form folder. I should change the name -- TODO). */
    private DataObject addSource(FileObject formFolderFO, String contents, String name)
        throws IOException {
        //GenericFolder formFolder = (GenericFolder)webForm.getParent();
        //DataObject formFolderDO = formFolder.getDataObject();
        //FileObject formFolderFO = formFolderDO.getPrimaryFile();
        InputStream is = null;
        OutputStream os = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        FileLock lock = null;

        try {
            is = new StringBufferInputStream(contents);

            FileObject resourceFO = null;
            resourceFO = formFolderFO.getFileObject(name);

            if (resourceFO == null) {
                resourceFO = formFolderFO.createData(name);
            }

            lock = resourceFO.lock();
            os = resourceFO.getOutputStream(lock);
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(os);

            int c;

            while ((c = in.read()) != -1)
                out.write(c);

            DataObject dobj = DataObject.find(resourceFO);

            return dobj;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (lock != null) {
                    lock.releaseLock();
                }

                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
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

    private static Tidy getTidy(boolean isJsp, int encoding) {
        // Set configuration settings
        Tidy tidy = new Tidy();
        tidy.setOnlyErrors(false);
        tidy.setShowWarnings(true);
        tidy.setQuiet(false);
        tidy.getConfiguration().outputJspMode = true;
        tidy.getConfiguration().inputJspMode = isJsp;

//        int encoding = getEncoding();

        if (encoding != -1) {
            tidy.setCharEncoding(encoding);
        }

        // XXX Apparently JSP pages (at least those involving
        // JSF) need XML handling in order for JTidy not to choke on them
        tidy.setXmlTags(false);

        tidy.setXHTML(true); // XXX ?

        //tidy.setMakeClean(panel.getReplace());
        //tidy.setIndentContent(panel.getIndent());
        //tidy.setSmartIndent(panel.getIndent());
        //tidy.setUpperCaseTags(panel.getUpper());
        //tidy.setHideEndTags(panel.getOmit());
        //tidy.setWraplen(panel.getWrapCol());
        return tidy;
    }

    private static org.w3c.dom.Document rewrite(Tidy tidy, InputStream input) {
        StringBuffer sb = new StringBuffer(4000);
        OutputStream output = new StringBufferOutputStream(sb);

        //tidy.parse(input, output);
        StringWriter sw = new StringWriter();
        tidy.setErrout(new PrintWriter(sw));

        /* Show NetBeans output window with the errors? But how do I get an
         * output stream from the output writer?
                InputOutput io = IOProvider.getDefault().getStdOut();
                OutputWriter out = io.getOut();
                OutputStreamWriter
        */
        boolean escape =
            tidy.getConfiguration().outputJspMode && !tidy.getConfiguration().inputJspMode;
        org.w3c.dom.Document document =
            tidy.parseDOM(new Tidy.EntityWrapperInputStream(input),
                new Tidy.EntityWrapperOutputStream(output, escape));

        /* The Tidy DOM implementation sucks - it throws not implemented
           exceptions for CharacterData methods etc., so we call this on
           the duplicated node tree instead
        if (document != null) {
            tidy.cleanEntities(document, escape);
        }
        */

        //return sb.toString();
        if (document == null) {
            String message =
                NbBundle.getMessage(ImportPagePanel.class, "ParsingFailed",
                    sb.toString() + "\n" + sw.getBuffer().toString()); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }

        return document;
    }

    private void insertPortions(ImportContext context, Document document) {
        FileObject webformFile = context.webformFile;
        LiveUnit unit = getModel(webformFile).getLiveUnit();

        // Locate <body> in document. Look for <ui:body> too?
        NodeList importBodys = document.getElementsByTagName(HtmlTag.BODY.name);

        if (importBodys.getLength() == 0) {
            importBodys = document.getElementsByTagName("ui:body"); // NOI18N
        }

        if (importBodys.getLength() > 0) {
            Node importbody = importBodys.item(0);
            MarkupDesignBean body;
            if (context.fragment) {
                MarkupBean bean = getModel(webformFile).getFacesUnit().getDefaultParent();
//                RaveElement element = (RaveElement)bean.getElement();
//                body = element.getDesignBean();
                Element element = bean.getElement();
                body = InSyncService.getProvider().getMarkupDesignBeanForElement(element);
                for (int i = 0; i < body.getChildBeanCount(); i++) {
                    DesignBean child = body.getChildBean(i);
                    if (child.getInstance() instanceof org.netbeans.modules.visualweb.xhtml.P) {
                        unit.deleteBean(child);
                        break;
                    }
                }
            } else {
                DesignBean[] bodys;
                if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                    // XXX Woodstock
                    bodys = unit.getBeansOfType(com.sun.webui.jsf.component.Body.class);
                } else {
                    // XXX Braveheart
                    bodys = unit.getBeansOfType(com.sun.rave.web.ui.component.Body.class);
                }
                
                if ((bodys == null) || (bodys.length == 0)) {
                    bodys = unit.getBeansOfType(org.netbeans.modules.visualweb.xhtml.Body.class);
                    
                    if ((bodys == null) || (bodys.length == 0)) {
                        // XXX ERROR
                        ErrorManager.getDefault().log("No body found");
                        
                        return;
                    }
                }
                
                body = (MarkupDesignBean)bodys[0];
            }

            //Duplicate attributes on the body... when applicable
            //        like bgcolor etc.
            //        TODO: bgcolor !
            Element importBodyElement = (Element)importbody;
            String style = importBodyElement.getAttribute(HtmlAttribute.STYLE);
            String bgcolor = importBodyElement.getAttribute(HtmlAttribute.BGCOLOR);
            if (bgcolor.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";background-color:"+bgcolor; // NONI18N
            }
            String background = importBodyElement.getAttribute(HtmlAttribute.BACKGROUND);
            if (background.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";background-image:url("+background+")"; // NOI18N
            }
            String text = importBodyElement.getAttribute(HtmlAttribute.TEXT);
            if (text.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";color"+text; // NOI18N
            }

            DesignProperty styleProp = null;
            if (context.fragment && body.getBeanParent() != null) {
                styleProp = body.getBeanParent().getProperty("style");
            } else {
               styleProp = body.getProperty("style");
            }
            if (styleProp != null) {
                styleProp.setValue(style);
            }
            
            String onload = importBodyElement.getAttribute(HtmlAttribute.ONLOAD);
            if (onload.length() > 0) {
                DesignProperty onLoadProp = body.getProperty("onLoad");
                if (onLoadProp != null) {
                    onLoadProp.setValue(onload);
                }
            }
            
            //            String onunload = importBodyElement.getAttribute(HtmlAttribute.ONUNLOAD);
            //            if (onunload.length() > 0) {
            //                body.getProperty("onUnload").setValue(onunload);
            //
            //            }
            
            String cls = importBodyElement.getAttribute(HtmlAttribute.CLASS);
            if (cls.length() > 0) {
                DesignProperty styleClassProp = body.getProperty("styleClass");
                if (styleClassProp != null) {
                    styleClassProp.setValue(cls); // NOI18N
                }
            }
            
            NodeList nl = importbody.getChildNodes();
            Element parent = body.getElement();

            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node copied = getDom(webformFile).importNode(nl.item(i), true);
                parent.appendChild(copied);
            }

            // Eliminate duplicate <title> ?
            // Eliminate duplicate <meta> ?
        }

        // Locate <head> in document. Look for <ui:head> too?
        NodeList importHeads = document.getElementsByTagName(HtmlTag.HEAD.name);

        if (importHeads.getLength() == 0) {
            importHeads = document.getElementsByTagName("ui:head"); // NOI18N
        }

        if (importHeads.getLength() > 0) {
            Node importHead = importHeads.item(0);
            DesignBean[] heads;
            if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                // Woodstock
                heads = unit.getBeansOfType(com.sun.webui.jsf.component.Head.class);
            } else {
                // XXX Braveheart
                heads = unit.getBeansOfType(com.sun.rave.web.ui.component.Head.class);
            }

            if ((heads == null) || (heads.length == 0)) {
                heads = unit.getBeansOfType(org.netbeans.modules.visualweb.xhtml.Head.class);

                if ((heads == null) || (heads.length == 0)) {
                    // XXX ERROR
                    ErrorManager.getDefault().log("No head found");

                    return;
                }
            }

            MarkupDesignBean head = (MarkupDesignBean)heads[0];
            NodeList nl = importHead.getChildNodes();
            Element parent = head.getElement();

            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node copied = getDom(webformFile).importNode(nl.item(i), true);
                parent.appendChild(copied);
            }

            // Eliminate duplicate <title> ?
            // Eliminate duplicate <meta> ?
        }
        
        // Duplicate name spaces
        if (context.nameSpaces != null) {
            Element root = getDom(webformFile).getDocumentElement();
            Iterator it = context.nameSpaces.keySet().iterator();
            while (it.hasNext()) {
                String attribute = (String)it.next();
                if (!root.hasAttribute(attribute)) {
                    root.setAttribute(attribute,  (String)context.nameSpaces.get(attribute));
                }
            }
        }
    }

    /** Convert the input components to JSF components */
    private void convertCompsToJsf(ImportContext context) {
        FileObject webformFile = context.webformFile;
        FacesModel model = getModel(webformFile);

        if (model == null) {
            return;
        }

        LiveUnit lunit = model.getLiveUnit();

        if (lunit == null) {
            return;
        }

        // Iterate over the DOM, change <input>, <select> etc. tags
        //MarkupUnit munit = webform.getMarkup();
        Document document = getDom(webformFile);

        convertCompsToJsf(document.getDocumentElement(), webformFile, imageCheckBox.isSelected(),
            formCheckBox.isEnabled());

        // Clean up forms
        if (context.formsAdded != null) {
            removeBogusForms(context.formsAdded, webformFile);
        }

        // Get rid of old element references etc.
        //        model.getMarkupUnit().setSourceDirty(); // we've been messing with the DOM
        //        model.sync();
        model.getMarkupUnit().setModelDirty();
        model.getJavaUnit().setModelDirty();
        model.flush();
        LifecycleManager.getDefault().saveAll();
    }

    /** Remove form beans in the document except for the given set of forms to keep */
    private void removeBogusForms(List keepForms, FileObject webformFile) {
        // Remove all form beans that are "bogus" (empty)
        // Also make sure that the FacesPageUnit has the correct default parent
        // after this (since it may currently be using the bogus)
        // Strategy: locate all form tags in the hierarchy: those we didn't add
        // via import should be yanked. 
        MarkupBean defaultParent = getModel(webformFile).getFacesUnit().getDefaultParent();
        ArrayList allForms = new ArrayList();
        findForms(allForms, getModel(webformFile).getRootBean());

        Iterator it = allForms.iterator();

        while (it.hasNext()) {
            DesignBean bean = (DesignBean)it.next();

            if (!keepForms.contains(bean)) {
// <move>
//                if (FacesSupport.getMarkupBean(bean) == defaultParent) {
// ====
                if (getMarkupBean(bean) == defaultParent) {
// </move>
                    defaultParent = null;
                }

                getModel(webformFile).getLiveUnit().deleteBean(bean);
            } else {
// <move>
//                Element e = FacesSupport.getElement(bean);
// ====
                Element e = getElement(bean);
// </move>

                if (e != null) {
                    e.setAttribute(HtmlAttribute.ID, bean.getInstanceName());
                }
            }
        }

        if (defaultParent == null) {
            // We've deleted the bean that used to be the parent so set a new parent
            // Pick a suitable candidate.... let's pick the bean with the largest number
            // of children (recursively). The rationale is that this probably includes
            // the most content... so if we have a main form and a smaller form for
            // say search, we pick the main form. Another possible selection criterion
            // would be hierarchy depth; pick form tags closer to body than deeply 
            // nested forms.
            int fewestChildren = Integer.MAX_VALUE;
            it = keepForms.iterator();

            while (it.hasNext()) {
                DesignBean bean = (DesignBean)it.next();
// <move>
//                MarkupBean mb = FacesSupport.getMarkupBean(bean);
// ====
                MarkupBean mb = getMarkupBean(bean);
// </move>

                if (mb != null) {
                    int children = getChildCount(mb.getElement());

                    if (children < fewestChildren) {
                        defaultParent = mb;
                    }
                }
            }

            getModel(webformFile).getFacesUnit().setDefaultParent(defaultParent);
        }
    }

    private int getChildCount(Node n) {
        int count = 1; // me
        NodeList nl = n.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            count += getChildCount(nl.item(i));
        }

        return count;
    }

    /** Locate all UIForm beans in the component tree */
    private void findForms(List list, DesignBean bean) {
        if (bean.getInstance() instanceof UIForm) {
            list.add(bean);
        }

        for (int i = 0, n = bean.getChildBeanCount(); i < n; i++) {
            findForms(list, bean.getChildBean(i));
        }
    }

    private void convertCompsToJsf(Element element, FileObject webformFile, boolean images,
        boolean formComps) {
        String tagName = element.getTagName();
        HtmlTag tag = HtmlTag.getTag(tagName);
        DesignBean bean = null;

        // Declare early so specific components can prune their children
        // by nulling out
        boolean skipChildren = false;

        if (tag != null) {
            char c = tagName.charAt(0);

            switch (c) {
            case 'l':

                if (tag == HtmlTag.LABEL) {
                    bean =
                        replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlOutputLabel.class.getName()); // NOI18N

                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.FOR, "for"); // NOI18N
                    }
                }

                break;

            case 'f':

                if (formComps && (tag == HtmlTag.FORM)) {
                    //bean = replaceComponent(webform, element, "javax.faces.component.html.HtmlForm"); // NOI18N
                    if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                        // Woodstock
                        bean = replaceComponent(webformFile, element, com.sun.webui.jsf.component.Form.class.getName());
                    } else {
                        // Braveheart
                        bean = replaceComponent(webformFile, element, com.sun.rave.web.ui.component.Form.class.getName());
                    }

                    if (bean != null) {
                        if (context.formsAdded == null) {
                            context.formsAdded = new ArrayList();
                        }

                        context.formsAdded.add(bean);
                        // TODO - check these
                        //convertProperty(bean, element, HtmlAttribute.TARGET, "target"); // NOI18N
                        //convertProperty(bean, element, "accept", "accept"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.ENCTYPE, "enctype"); // NOI18N
                        convertProperty(bean, element, "onsubmit", "onSubmit"); // NOI18N
                        convertProperty(bean, element, "onreset", "onReset"); // NOI18N
                        //convertProperty(bean, element, "accept-charset", "acceptcharset"); // NOI18N
                    }
                }

                break;

            /*
            case 'a':
            if (tag == HtmlTag.A) {
                bean = replaceComponent(webform, element, "javax.faces.component.html.HtmlOutputLink"); // NOI18N
                if (bean != null) {
                    convertProperty(bean, element, HtmlAttribute.HREF, "value"); // NOI18N
                }


                // Also, HtmlOutputLink does not render its markup children,
                // only its component children, so I've gotta insert an
                // output text to contain its link text!
            }
            break;
            */
            case 'i':

                if (formComps && (tag == HtmlTag.INPUT)) {
                    String type = element.getAttribute(HtmlAttribute.TYPE);

                    if ((type == null) || (type.length() == 0) || type.equals("text")) { // NOI18N

                        // Text Field
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputText.class.getName());

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.MAXLENGTH, "maxlength"); // NOI18N
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                            convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                        }
                    } else if (type.equals("submit") // NOI18N
                             ||type.equals("reset") // NOI18N
                             ||type.equals("button")) { // NOI18N
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlCommandButton.class.getName());
                    } else if (type.equals("checkbox")) {
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectBooleanCheckbox.class.getName());

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.CHECKED, "value"); // NOI18N
                        }
                    } else if (type.equals("file")) { // NOI18N

                        // XXX no file upload component!!
                    } else if (type.equals("hidden")) { // NOI18N
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputHidden.class.getName());
                    } else if (type.equals("password")) { // NOI18N
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputSecret.class.getName());

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.MAXLENGTH, "maxlength"); // NOI18N
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                            convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                        }
                    } else if (type.equals("radio")) {
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectManyCheckbox.class.getName());

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.CHECKED, "value"); // NOI18N
                        }
                    } else if ("image".equals(type)) {
                        // HtmlCommandButton with image attribute
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlCommandButton.class.getName());

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.SRC, "image"); // NOI18N
                        }
                    }

                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N

                        // Not a JSF prop: convertProperty(bean, element, "accept", "accept"); // NOI18N
                    }
                }
                // CONTROVERSIAL:
                else if (images && (tag == HtmlTag.IMG)) {
                    bean =
                        replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlGraphicImage.class.getName());

                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.SRC, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.WIDTH, "width"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.HEIGHT, "height"); // NOI18N
                        convertProperty(bean, element, "longdesc", "longdesc"); // NOI18N

                        // TODO: usemap, ismap
                    }
                }

                break;

            // CONTROVERSIAL:
            case 's':

                if (formComps && (tag == HtmlTag.SELECT)) {
                    int size =
                        HtmlAttribute.getIntegerAttributeValue(element, HtmlAttribute.SIZE, 1);
                    boolean multiple = element.hasAttribute(HtmlAttribute.MULTIPLE);

                    if ((size > 1) || multiple) {
                        // javax.faces.component.html.HtmlSelectOneListbox
                        // javax.faces.component.html.HtmlSelectManyListbox
                        // XXX Todo: look up selection attribute and do the right thing!
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectOneListbox.class.getName(), false);

                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                        }
                    } else {
                        bean =
                            replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectOneMenu.class.getName(), false);
                    }

                    if (bean != null) {
                        skipChildren = true; // Option children should not be included!

                        // XXX will this conflict with checkbox/radiobox
                        // setting value from the "checked" attribute?
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.CLASS, "styleClass"); // NOI18N

                        // Create a select items!
                        // XXX I've gotta get the id set first... so
                        // that I pick a decent select items name!
                        String[] optionList = extractOptions(element);

                        try {
                            DesignContext context = bean.getDesignContext();

                            // create and setup a default items array
                            DesignBean items =
                                context.createBean(DefaultSelectItemsArray.class.getName(), null,
                                    null);
                            items.setInstanceName(bean.getInstanceName() + "DefaultItems", true); //NOI18N

                            DesignProperty itp = items.getProperty("items"); // NOI18N

                            if (itp != null) {
                                itp.setValue(optionList);
                            }

                            // create a selectitems child
                            if (context.canCreateBean(UISelectItems.class.getName(), bean, null)) {
                                DesignBean si =
                                    context.createBean(UISelectItems.class.getName(), bean, null);

                                if (si != null) {
                                    si.setInstanceName(bean.getInstanceName() + "SelectItems", true); //NOI18N

                                    String outer =
                                        bean.getDesignContext().getRootContainer().getInstanceName();
                                    si.getProperty("value").setValueSource("#{" + outer + "." + //NOI18N
                                        items.getInstanceName() + "}"); //NOI18N
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Todo: nuke out all children; possibly convert the
                    // <option> tags to a SelectItems object and populate it,
                    // then nuke the <option> tags!

                    /*
                    } else if (tag == HtmlTag.SPAN) {
                    // CONTROVERSIAL!
                    // javax.faces.component.html.HtmlOutputText
                    // javax.faces.component.html.HtmlPanelGroup
                    */
                }

                break;

            case 't':

                if (formComps && (tag == HtmlTag.TEXTAREA)) {
                    bean =
                        replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlInputTextarea.class.getName());

                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.ROWS, "rows"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.COLS, "cols"); // NOI18N
                        convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                    }

                    /*
                    } else if (tag == HtmlTag.TABLE) {
                    // CONTROVERSIAL:
                    // javax.faces.component.html.HtmlDataTable
                    // javax.faces.component.html.HtmlPanelGrid
                    */
                }

                break;

            case 'o':

//                if (tag == HtmlTag.OPTION) {
//                    // Should convert these as part of <select>'s children.
//                    // When they appear outside of a select, wipe them out.
//                    // TODO
//                }

                break;
            }

            // XXX How do we convert to checkbox lists and radiobutton lists?
        } else {
            // Use reverse map from tags to class names to instantiate
            // a component behind the tag
            initPaletteComponents();

            int colon = tagName.indexOf(':');

            if (colon != -1) {
                tagName = tagName.substring(colon + 1);
            }

            String cls = (String)context.paletteComponents.get(tagName);

            if (cls != null) {
                // I may already have some existing DesignBeans in the document
                // I'm mutating -- the default parent form for example; don't
                // do anything with these.
//                if (((RaveElement)element).getDesignBean() == null) {
                if (InSyncService.getProvider().getMarkupDesignBeanForElement(element) == null) {
                    // we don't set "bean" -- don't want html treatment below
                    DesignBean newBean = replaceTag(webformFile, element, cls);

                    // If I've replaced the Element, ensure that I recurse into
                    // the new element instead
                    if (newBean != null) {
                        if (tagName.equals(HtmlTag.FORM.name)) {
                            if (context.formsAdded == null) {
                                context.formsAdded = new ArrayList();
                            }

                            context.formsAdded.add(newBean);
                        }

// <move>
//                        Element e = FacesSupport.getElement(newBean);
// ====
                        Element e = getElement(newBean);
// </move>

                        if (e != null) {
                            element = e;

                            // See if we should import some additional resources
                            // I don't actually know which component properties represent URLs
                            // Is there metadata for this?
                            // Should I try to simply iterate over all properties, looking to
                            // see if they are importable resources?? Seems like this could
                            // have some accidental effects
                            String propName = null;

                            if (newBean.getInstance() instanceof com.sun.rave.web.ui.component.ImageComponent
                            || newBean.getInstance() instanceof com.sun.webui.jsf.component.ImageComponent) { // XXX Woodstock
                                propName = "url"; // NOI18N
                            } else if (newBean.getInstance() instanceof UIGraphic) {
                                propName = "value"; // NOI18N
                            } else if (newBean.getInstance() instanceof HtmlCommandButton) {
                                propName = "image"; // NOI18N
                            } // XXX are there any known other ones?

                            if (context.copyResources && (propName != null)) {
                                DesignProperty prop = newBean.getProperty(propName);

                                if (prop != null) {
                                    String value = prop.getValueSource();

                                    if ((value != null) && (value.length() > 0) &&
// <move>
//                                            !FacesSupport.isValueBindingExpression(value, false)) {
// ====
                                            !isValueBindingExpression(value, false)) {
// </move>
                                        context.warnMissingFile = false;
                                        copyResource(element, propName);

                                        String copied = element.getAttribute(propName);

                                        if ((copied != value) && (copied != null) &&
                                                (copied.length() > 0)) {
                                            prop.setValue(copied);
                                        }

                                        context.warnMissingFile = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bean != null) {
            // XXX Can I preserve the name somehow?
            String id = null;

            if (element.getAttribute(HtmlAttribute.ID).length() > 0) {
                id = element.getAttribute(HtmlAttribute.ID);
            } else if (element.getAttribute(HtmlAttribute.NAME).length() > 0) {
                id = element.getAttribute(HtmlAttribute.NAME);
            }

            if (id != null) {
                // Make sure id is unique
                if (context.names == null) {
                    context.names = new HashSet();
                    context.names.add(id);
                } else {
                    while (context.names.contains(id)) {
                        id = id + "_";
                    }

                    context.names.add(id);
                }

                bean.setInstanceName(id);
            }

            convertProperty(bean, element, HtmlAttribute.CLASS, "styleClass"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.STYLE, "style"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.ALT, "alt"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.TITLE, "title"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.DIR, "dir"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.LANG, "lang"); // NOI18N
            convertProperty(bean, element, "disabled", "disabled"); // NOI18N
            convertProperty(bean, element, "accesskey", "accesskey"); // NOI18N
            convertProperty(bean, element, "tabindex", "tabindex"); // NOI18N

            // Copy all the JavaScript properties... "onblur", "onchange", ...
            convertProperty(bean, element, "onblur", "onblur"); // NOI18N
            convertProperty(bean, element, "onchange", "onchange"); // NOI18N
            convertProperty(bean, element, "onclick", "onclick"); // NOI18N
            convertProperty(bean, element, "ondblclick", "ondblclick"); // NOI18N
            convertProperty(bean, element, "onfocus", "onfocus"); // NOI18N
            convertProperty(bean, element, "okeydownn", "oneydownn"); // NOI18N
            convertProperty(bean, element, "onkeypress", "onkeypress"); // NOI18N
            convertProperty(bean, element, "onkeyup", "onkeyup"); // NOI18N
            convertProperty(bean, element, "onmousedown", "onmousedown"); // NOI18N
            convertProperty(bean, element, "onmousemove", "onmousemove"); // NOI18N
            convertProperty(bean, element, "onmouseout", "onmouseout"); // NOI18N
            convertProperty(bean, element, "onmouseover", "onmouseover"); // NOI18N
            convertProperty(bean, element, "onmouseup", "onmouseup"); // NOI18N
            convertProperty(bean, element, "onselect", "onselect"); // NOI18N

            // If I've replaced the Element, ensure that I recurse into
            // the new element instead
// <move>
//            Element e = FacesSupport.getElement(bean);
// ====
            Element e = getElement(bean);
// </move>

            if (e != null) {
                element = e;
            }
        }

        if (skipChildren) {
            return;
        }

        NodeList nl = element.getChildNodes();

        if ((nl == null) || (nl.getLength() == 0)) {
            return;
        }

        // Copy children list first since we're mutating while we fly
        ArrayList children = new ArrayList(nl.getLength());

        for (int i = 0, n = nl.getLength(); i < n; i++) {
            Node child = nl.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
        }

        for (int i = 0, n = children.size(); i < n; i++) {
            Element e = (Element)children.get(i);
            convertCompsToJsf(e, webformFile, images, formComps);
        }
    }

    private DesignBean replaceComponent(FileObject webformFile, Element element, String className) {
        return replaceComponent(webformFile, element, className, true);
    }

    private DesignBean replaceComponent(FileObject webformFile, Element element, String className,
        boolean copyChildren) {
        LiveUnit unit = getModel(webformFile).getLiveUnit();
        Node under = element.getParentNode();
        Node before = element;
        MarkupPosition pos = new MarkupPosition(under, before);

        DesignBean parent = null;
        Node n = under;

//        while (n instanceof RaveElement) {
//            RaveElement xel = (RaveElement)n;
        while (n instanceof Element) {
            Element xel = (Element)n;

//            if (xel.getDesignBean() != null) {
//                DesignBean lbean = (DesignBean)xel.getDesignBean();
            DesignBean lbean = InSyncService.getProvider().getMarkupDesignBeanForElement(xel);
            if (lbean != null) {
                if (lbean.isContainer()) {
                    parent = lbean;

                    break;
                }
            }

            n = n.getParentNode();
        }

        DesignBean bean = unit.createBean(className, parent, pos);

        // Move the element's children to the new bean
        if (bean != null) {
            if (copyChildren) {
// <move>
//                Element e = FacesSupport.getElement(bean);
// ====
                Element e = getElement(bean);
// </move>

                if (e != null) {
                    NodeList nl = element.getChildNodes();
                    int num = nl.getLength();

                    if (num > 0) {
                        // Copy list first since nodelist object changes
                        // under us when we move the nodes
                        ArrayList children = new ArrayList(num);

                        for (int i = 0; i < num; i++) {
                            children.add(nl.item(i));
                        }

                        for (int i = 0; i < num; i++) {
                            e.appendChild((Node)children.get(i));
                        }
                    }
                }
            }

            // Get rid of the old element that we've replaced!
            /*
            // This doesn't work as expected because deleteBean on the old
            // element will see that a component of id "foo" is being removed
            // and it will therefore remove references to "foo" in other components
            // like the "for" property of associated labels. Instead we rely on a
            // sync when the import is done.
            DesignBean oldBean = null;
            if (element instanceof XhtmlElement) {
                oldBean = ((XhtmlElement)element).getDesignBean();
            }
            if (oldBean != null) {
                unit.deleteBean(oldBean);
            } else {
                under.removeChild(element);
            }
            */
            under.removeChild(element);
        }

        // TODO: remove the element since it now has a sibling!
        return bean;
    }

    private DesignBean replaceTag(FileObject webformFile, Element element, String className) {
        boolean copyChildren = true;
        LiveUnit unit = getModel(webformFile).getLiveUnit();
        Node under = element.getParentNode();
        Node before = element;
        MarkupPosition pos = new MarkupPosition(under, before);

        DesignBean parent = null;
        Node n = under;

//        while (n instanceof RaveElement) {
//            RaveElement xel = (RaveElement)n;
        while (n instanceof Element) {
            Element xel = (Element)n;

//            if (xel.getDesignBean() != null) {
//                DesignBean lbean = (DesignBean)xel.getDesignBean();
            DesignBean lbean = InSyncService.getProvider().getMarkupDesignBeanForElement(xel);
            if (lbean != null) {
                if (lbean.isContainer()) {
                    parent = lbean;

                    break;
                }
            }

            n = n.getParentNode();
        }

        DesignBean bean = unit.createBean(className, parent, pos);

        // Move the element's children to the new bean
        if (bean != null) {
            // Copy all the attributes!!
// <move>
//            Element e = FacesSupport.getElement(bean);
// ====
            Element e = getElement(bean);
// </move>

            if (e != null) {
                int numAttr = element.getAttributes().getLength();

                for (int i = 0; i < numAttr; i++) {
                    Node a = element.getAttributes().item(i); // XXX move element.getAttributes out of loop
                    String attribute = a.getNodeName();

                    if (attribute.equals(FacesBean.BINDING_ATTR)) {
                        // Don't mess with component binding!!!
                        continue;
                    }

                    String value = a.getNodeValue();
                    e.setAttribute(a.getNodeName(), a.getNodeValue());

                    DesignProperty prop = bean.getProperty(attribute);

                    try {
                        if (prop != null) {
                            prop.setValue(value);
                        }
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }

                if (copyChildren) {
                    NodeList nl = element.getChildNodes();
                    int num = nl.getLength();

                    if (num > 0) {
                        // Copy list first since nodelist object changes
                        // under us when we move the nodes
                        ArrayList children = new ArrayList(num);

                        for (int i = 0; i < num; i++) {
                            children.add(nl.item(i));
                        }

                        for (int i = 0; i < num; i++) {
                            e.appendChild((Node)children.get(i));
                        }
                    }
                }
            }

            // Get rid of the old element that we've replaced!
            under.removeChild(element);
        }

        // TODO: remove the element since it now has a sibling!
        return bean;
    }

    private void convertProperty(DesignBean bean, Element element, String htmlAttr, String jsfProp) {
        if ((bean == null) || (element == null)) {
            return;
        }

        if (!element.hasAttribute(htmlAttr)) {
            return;
        }

        String value = element.getAttribute(htmlAttr);
        DesignProperty prop = bean.getProperty(jsfProp);

        if (prop == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                "No such jsf property alias " + jsfProp + " in bean " + bean.getInstanceName() + " for html attribute " + htmlAttr); // NOI18N

            return;
        }

        // XXX Do any kind of to-Object conversion here?? Do we have any
        // cases where we have to convert from e.g. a string color description
        // in HTML to an actual Color object (for example) in the JSF
        // component?
        try {
            PropertyDescriptor pd = prop.getPropertyDescriptor();

            if (pd != null) {
                if (pd.getPropertyType() == Integer.TYPE) {
                    try {
                        int f = Integer.parseInt(value);
                        prop.setValue(new Integer(f));

                        return;
                    } catch (NumberFormatException nfe) {
                        ErrorManager.getDefault().notify(nfe);
                    }
                } else if (pd.getPropertyType() == Boolean.TYPE) {
                    // True if "yes", "on", "true", "1", or <name>
                    // (e.g.  checked="checked" is considered true)
                    if (value.equalsIgnoreCase("yes") || // NOI18N
                            value.equalsIgnoreCase("on") || // NOI18N
                            value.equalsIgnoreCase("true") || // NOI18N
                            value.equalsIgnoreCase("1") || // NOI18N
                            value.equalsIgnoreCase(jsfProp)) {
                        prop.setValue(Boolean.TRUE);
                    } else {
                        assert value.equalsIgnoreCase("no") || // NOI18N
                        value.equalsIgnoreCase("off") || // NOI18N
                        value.equalsIgnoreCase("false") || // NOI18N
                        value.equalsIgnoreCase("0") || // NOI18N
                        value.equalsIgnoreCase(""); // NOI18N
                        prop.setValue(Boolean.FALSE);
                    }

                    return;
                }
            }

            prop.setValue(value);
        } catch (Exception ex) { // Don't let one-value assignment stop whole conversion
            ErrorManager.getDefault().notify(ex);
        }
    }

    /** For the given select element, find all option children and
        return as an array of strings */
    private String[] extractOptions(Element element) {
        Vector v = new Vector();
// <move>
//        int[] selected = FormComponentBox.populateOptions(element, v, context.webform.getMarkup());
// ====
        int[] selected = populateOptions(element, v, getMarkupUnit(context.webformFile));
// </move>

        // XXX Can I somehow preserve the selection into the data?
        if (v.size() == 0) {
            return new String[0];
        }

        return (String[])v.toArray(new String[v.size()]);
    }

    private void initPaletteComponents() {
        if (context.paletteComponents != null) {
            return;
        }

        context.paletteComponents = new HashMap(200);
        
//        // Iterate over the palette contents and locate all available components
//        // such that I can create a reverse map, from tag name to component name.
//        // This is used in component import to create binding attributes
//        // to concrete components, since JSPs being imported may not contain these
//        // (page import doesn't study java code, and besides, JSF doesn't require
//        // component binding but we always need it.)
//        Palette[] palettes = PaletteComponentModel.getInstance().getPalettes();
//        for (int i = 0; palettes != null && i < palettes.length; i++) {
//            PaletteSection[] paletteSections = palettes[i].getPaletteSections();
//            for (int j = 0; paletteSections != null && j < paletteSections.length; j++) {
//                PaletteItem[] paletteItems = paletteSections[j].getItems();
//                for (int k = 0; paletteItems != null && k < paletteItems.length; k++) {
//                    PaletteItem pi = paletteItems[k];
//
//                    if (pi instanceof BeanPaletteItem) {
//                        BeanPaletteItem bpi = (BeanPaletteItem)pi;
//
//                        // getBeanClassName() seems to do more work than it should!!
//                        String className = bpi.getName();
//                        BeanInfo beanInfo = BeanPaletteItem.loadBeanInfo(className);
//
//                        if (beanInfo != null) {
//                            String tagName = FacesUnit.getBeanTagName(beanInfo);
//
//                            if ((tagName != null) && (tagName.length() > 0)) {
//                                context.paletteComponents.put(tagName, className);
//                            }
//                        }
//                    }
//                }
//            }
//        }
        // TODO The old way is not working now, there needs to be new API made.
//        context.paletteComponents = Complib.getTagName2ClassNameMap(context.project);
        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                new IllegalStateException("Missing API (from complib?) to provide map with tag names to class names for project, project=" + project)); // TEMP
        context.paletteComponents = Collections.EMPTY_MAP;
    }

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

    
// <move-first>
    private static FacesModel getModel(FileObject fo) {
        return FacesModel.getInstance(fo);
    }
    
    private static MarkupUnit getMarkupUnit(FileObject fo) {
        FacesModel model = getModel(fo);
        if(model == null) {
            return null;
        }
        
        return model.getMarkupUnit();
    }

    private static Document getDom(FileObject fo) {
        MarkupUnit unit = getMarkupUnit(fo);

        if (unit == null) { // possible when project has closed

            return null;
        }

        return unit.getSourceDom();
    }
// </move-first>
    
    
    private static class ImportContext {
        boolean fragment;
        Document parsedDocument;
        Project project;
        FileObject webformFile;
        DataObject webformDobj;
//        WebForm webform;
        Map paletteComponents;
        ArrayList formsAdded;
        Set names;
        ArrayList tagLibs;
        boolean haveOldJsp;
        URL base;
        URL fullUrl;
        boolean warnMissingFile = true;
        HashMap resources;
        boolean copyResources;
        Map nameSpaces;
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
    
    
// <move>
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static MarkupBean getMarkupBean(DesignBean lb) {
        if (!(lb instanceof BeansDesignBean)) {
            return null;
        }

        Bean b = ((BeansDesignBean)lb).getBean();

        if (b instanceof MarkupBean) {
            return (MarkupBean)b;
        }

        return null;
    }
    
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static Element getElement(DesignBean lb) {
        if (lb instanceof MarkupDesignBean) {
            return ((MarkupDesignBean)lb).getElement();
        } else {
            return null;
        }
    }
    
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static boolean isValueBindingExpression(String s, boolean containsOk) {
        assert s != null;

        // TODO: Use
        //  ((FacesDesignProperty)designProperty).isBound()
        // instead - so change to passing in a DesignProperty etc.
        if (containsOk) {
            return s.indexOf("#{") != -1; // NOI18N
        } else {
            return s.startsWith("#{"); // NOI18N
        }
    }
    
    /** XXX Copy from designer FormComponentBox, it shouldn't be neither in designer not here. */
    private static int[] populateOptions(Element element, Vector v, MarkupUnit markup) {
        // <markup_separation>
//        MarkupService markupService = MarkupServiceProvider.getDefault();
        // </markup_separation>
        ArrayList selected = new ArrayList();
        NodeList list = element.getChildNodes();
        int len = list.getLength();

        for (int i = 0; i < len; i++) {
            Node child = list.item(i);

            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element option = (Element)child;

            if (!option.getTagName().equals(HtmlTag.OPTION.getTagName())) {
                continue;
            }

            // Found an option
            NodeList list2 = option.getChildNodes();
            int len2 = list2.getLength();
            StringBuffer sb = new StringBuffer();

            for (int j = 0; j < len2; j++) {
                Node child2 = list2.item(j);

                if ((child2.getNodeType() != Node.TEXT_NODE) &&
                        (child2.getNodeType() != Node.CDATA_SECTION_NODE)) {
                    continue;
                }

                String nodeVal = child2.getNodeValue();

                if (nodeVal != null) {
                    nodeVal = nodeVal.trim();

//                    RaveText textNode = (child2 instanceof RaveText) ? (RaveText)child2 : null;
                    Text textNode = (child2 instanceof Text) ? (Text)child2 : null;

//                    if ((textNode != null) && textNode.isJspx()) {
                    if (textNode != null && MarkupService.isJspxNode(textNode)) {
                        // <markup_separation>
//                        nodeVal = markupService.expandHtmlEntities(nodeVal, true, element);
                        // ====
                        nodeVal = InSyncService.getProvider().expandHtmlEntities(nodeVal, true, element);
                        // </markup_separation>
                    } // ELSE: regular entity fixing?

                    sb.append(nodeVal);

                    // XXX I should be able to bail here - for combo
                    // boxes I only show the first item! (There's no
                    // way for the user to open the menu). However,
                    // for things like a multi select, you need to
                    // show possibly multiple choices, so perhaps pass
                    // in a max count?
                }
            }

            if (sb.length() > 0) {
                // Is this item selected too?
                Attr attr = option.getAttributeNode(HtmlAttribute.SELECTED);

                if (attr != null) {
                    selected.add(new Integer(v.size()));
                }

                v.addElement(sb.toString());
            }
        }

        if (selected != null) {
            int[] result = new int[selected.size()];

            for (int i = 0, n = selected.size(); i < n; i++) {
                result[i] = ((Integer)selected.get(i)).intValue();
            }

            return result;
        }

        return null;
    }
// </move>
    
    private Map getStyleResourcesForElement(Element element, String rules) {
//        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);
        
//        URL docUrl = InSyncService.getProvider().getUrl(doc);
//        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
//        StyleDeclaration sd = CssProvider.getEngineService().parseStyleDeclarationForElement(element, rules);
//        String[] urlStrings = getStyleResourcesFromStyleDeclaration(sd);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        String[] urlStrings = CssProvider.getEngineService().getStyleResourcesForElement(
                element,
                rules,
                doc,
                docUrl,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(urlStrings);
    }
    
    private Map getStyleResources(String rules, int depth) {
//        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);

//        URL docUrl = InSyncService.getProvider().getUrl(doc);
//        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
//        InputSource is = new InputSource(new StringReader(rules));
//        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, context.base, "all", context.base); // NOI18N
//        ResourceData[] resourceData = getStyleResourcesFromStyleSheet(doc, ss);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        ResourceData[] resourceData = CssProvider.getEngineService().getStyleResourcesForRules(
                rules,
                doc,
                docUrl,
                context.base,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(depth, resourceData);
    }
    
    private Map getStyleResources(URL url, int depth) {
//        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);

//        URL docUrl = InSyncService.getProvider().getUrl(doc);
//        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
//        InputSource is = new InputSource(url.toString());
//        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, url, "all", url); // NOI18N
//        ResourceData[] resourceData = getStyleResourcesFromStyleSheet(doc, ss);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        ResourceData[] resourceData = CssProvider.getEngineService().getStyleResourcesForUrl(
                url,
                doc,
                docUrl,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(depth, resourceData);
    }
    
    // Moved from insync/MarkupUnit.
    private static Document createEmptyDocument() {
        try {
            org.xml.sax.InputSource is =
                new org.xml.sax.InputSource(new StringReader("<html><body><p/></body></html>"));
            DocumentBuilder parser = MarkupService.createRaveSourceDocumentBuilder(true);
            Document doc = parser.parse(is);
            return doc;
        }
        catch (java.io.IOException e) {
            // should not happen reading from a string!
//            Trace.trace("insync.markup", "Error in createEmptyDocument");
//            Trace.trace("insync.markup", e);
            e.printStackTrace();
        }
        catch (org.xml.sax.SAXException e) {
//            Trace.trace("insync.markup", "Error in createEmptyDocument");
//            Trace.trace("insync.markup", e);
            e.printStackTrace();
        }
        catch (javax.xml.parsers.ParserConfigurationException e) {
//            Trace.trace("insync.markup", "Error in createEmptyDocument");
//            Trace.trace("insync.markup", e);
            e.printStackTrace();
        }
        return null;
    }
    
}
