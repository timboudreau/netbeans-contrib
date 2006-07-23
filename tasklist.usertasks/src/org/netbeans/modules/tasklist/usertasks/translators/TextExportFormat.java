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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.translators;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.SaveFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;   

/**
 * Creates plain text file using XSL transformation.
 *
 * @author tl
 */
public class TextExportFormat extends XmlExportFormat {
    private String res = "usertasks-effort-text.xsl"; // NOI18N
    
    /** 
     * Creates a new instance of HTMLTranslator 
     */
    public TextExportFormat() {
    }
    
    public String getName() {
        return NbBundle.getMessage(TextExportFormat.class, "Text"); // NOI18N
    }
    
    public org.openide.WizardDescriptor getWizard() {
        SaveFilePanel chooseFilePanel = new SaveFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(
                NbBundle.getMessage(XmlExportFormat.class, 
                    "TextFilter"), // NOI18N
                new String[] {".txt"})); // NOI18N
        chooseFilePanel.setFile(new File(
                Settings.getDefault().getLastUsedExportFolder(),
                "tasklist.txt")); // NOI18N
        chooseFileWP.setContentHighlightedIndex(0);
        chooseFilePanel.setOpenFileCheckBoxVisible(true);

        // create wizard descriptor
        WizardDescriptor.Iterator iterator = 
            new WizardDescriptor.ArrayIterator(
                new WizardDescriptor.Panel[] {chooseFileWP});
        WizardDescriptor wd = new WizardDescriptor(iterator);
        wd.putProperty("WizardPanel_contentData", // NOI18N
            new String[] {
                NbBundle.getMessage(
                    TextExportFormat.class, "TextChooseDestination"), // NOI18N
            }
        ); // NOI18N 
        wd.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        wd.setTitle(NbBundle.getMessage(TextExportFormat.class,
            "ExportText")); // NOI18N
        wd.putProperty(CHOOSE_FILE_PANEL_PROP, chooseFilePanel);
        wd.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N
        
        return wd;
    }
    
    /**
     * Opens the specified file in the IDE.
     *
     * @param file file to be opened
     */
    private static void openFileInIde(File file) {
        try
        {
            FileObject fo = FileUtil.toFileObject(file);
            if (fo != null) {
                DataObject do_ = DataObject.find(fo);
                OpenCookie oc = (OpenCookie) do_.getCookie(OpenCookie.class);
                if (oc != null) {
                    oc.open();
                } else {
                    String msg = NbBundle.getMessage(TextExportFormat.class, 
                            "CannotOpenFile"); // NOI18N
                    NotifyDescriptor nd = new NotifyDescriptor.Message(
                            msg, NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
            } else {
                String msg = NbBundle.getMessage(TextExportFormat.class, 
                        "CannotFindFile"); // NOI18N
                NotifyDescriptor nd = new NotifyDescriptor.Message(
                        msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        }
    }
   
    protected Transformer createTransformer() {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            InputStream xsl = TextExportFormat.class.
                getResourceAsStream(res);
            return tf.newTransformer(new StreamSource(xsl));
        } catch (TransformerConfigurationException e) {
            UTUtils.LOGGER.log(Level.WARNING, 
                    "XSL-Transformer not found", e); // NOI18N
            return null;
        } catch (TransformerException e) {
            UTUtils.LOGGER.log(Level.WARNING, 
                    "XSL-Transformer cannot be created", e); // NOI18N
            return null;
        }
   }
    
    public void doExportImport(ExportImportProvider provider, 
    WizardDescriptor wd) {
        SaveFilePanel chooseFilePanel = (SaveFilePanel)
            wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        super.doExportImport(provider, wd);
        if (chooseFilePanel.getOpenExportedFile()) {
            openFileInIde(chooseFilePanel.getFile());
        }
    }
    
}
