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

package org.netbeans.modules.tasklist.usertasks.translators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.filechooser.FileSystemView;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.SaveFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.NbBundle;

/**
 * Creates HTML using XSL transformation
 */
public class HtmlExportFormat extends XmlExportFormat {
    private static String[] LAYOUTS = {
        "usertasks-table-html.xsl", // NOI18N
        "usertasks-tree-html.xsl" // NOI18N
    };
    private String res = "usertasks-table-html.xsl"; // NOI18N
    
    /** 
     * Creates a new instance of HTMLTranslator 
     */
    public HtmlExportFormat() {
    }
    
    public String getName() {
        return NbBundle.getMessage(HtmlExportFormat.class, "HTML"); // NOI18N
    }
    
    public org.openide.WizardDescriptor getWizard() {
        SaveFilePanel chooseFilePanel = new SaveFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(
                NbBundle.getMessage(XmlExportFormat.class, 
                    "HtmlFilter"), // NOI18N
                new String[] {".html"})); // NOI18N
        chooseFilePanel.setFile(new File(
            FileSystemView.getFileSystemView().
            getDefaultDirectory(), "tasklist.html")); // NOI18N
        chooseFileWP.setContentHighlightedIndex(0);

        XslTemplatesPanel templatesPanel = new XslTemplatesPanel();
        templatesPanel.setAvailableLayouts(new String[] {
            NbBundle.getMessage(
                XmlExportFormat.class, "Table"), // NOI18N
            NbBundle.getMessage(
                XmlExportFormat.class, "Tree") // NOI18N
        });
        SimpleWizardPanel templatesWP = new SimpleWizardPanel(templatesPanel);
        templatesWP.setFinishPanel(true);
        templatesWP.setContentHighlightedIndex(1);

        // create wizard descriptor
        WizardDescriptor.Iterator iterator = 
            new WizardDescriptor.ArrayIterator(
                new WizardDescriptor.Panel[] {chooseFileWP, templatesWP});
        WizardDescriptor wd = new WizardDescriptor(iterator);
        wd.putProperty("WizardPanel_contentData", // NOI18N
            new String[] {
                NbBundle.getMessage(
                    XmlExportFormat.class, "ChooseDestination"), // NOI18N
                NbBundle.getMessage(
                    XmlExportFormat.class, "ChooseLayout") // NOI18N
            }
        ); // NOI18N
        wd.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        wd.setTitle(NbBundle.getMessage(XmlExportFormat.class,
            "ExportHTML")); // NOI18N
        wd.putProperty(getClass().getName() + 
            ".TemplatesPanel", templatesPanel); // NOI18N
        wd.putProperty(CHOOSE_FILE_PANEL_PROP, chooseFilePanel);
        wd.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N todo
        
        return wd;
    }
    
    /**
     * Opens the specified file in browser
     *
     * @param file file to be opened
     */
    private static void showFileInBrowser(File file) {
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            // Can't show URL
            ErrorManager.getDefault().notify(e);
            return;
        }

        HtmlBrowser.URLDisplayer.getDefault().showURL(url);
    }
   
    protected Transformer createTransformer() {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            InputStream xsl = HtmlExportFormat.class.
                getResourceAsStream(res);
            return tf.newTransformer(new StreamSource(xsl));
        } catch (TransformerConfigurationException e) {
            ErrorManager.getDefault().notify(e);
            return null;
        } catch (TransformerException e) {
            ErrorManager.getDefault().notify(e);
            return null;
        }
   }
    
    public void doExportImport(ExportImportProvider provider, 
    WizardDescriptor wd) {
        SaveFilePanel chooseFilePanel = (SaveFilePanel)
            wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        XslTemplatesPanel templatesPanel = (XslTemplatesPanel)
            wd.getProperty(getClass().getName() + 
                ".TemplatesPanel"); // NOI18N
        this.res = LAYOUTS[templatesPanel.getLayoutIndex()];
        super.doExportImport(provider, wd);
        if (templatesPanel.getOpenFile()) {
            showFileInBrowser(chooseFilePanel.getFile());
        }
    }
    
}
