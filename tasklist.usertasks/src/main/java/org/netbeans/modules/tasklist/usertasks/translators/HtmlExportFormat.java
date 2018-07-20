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

package org.netbeans.modules.tasklist.usertasks.translators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.SaveFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;


import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 * Creates HTML using XSL transformation
 */
public class HtmlExportFormat extends XmlExportFormat {
    private static String[] LAYOUTS = {
        "usertasks-simple-html.xsl", // NOI18N
        "usertasks-effort-html.xsl", // NOI18N
        "usertasks-planning-html.xsl", // NOI18N
        "usertasks-table-html.xsl", // NOI18N
        "usertasks-tree-html.xsl" // NOI18N
    };

    /**
     * Copies a resource to a file.
     * 
     * @param from see Class.getResourceAsStream
     * @param to destination
     */
    private static void copyResourceToFile(String from, File to) 
            throws IOException {
        InputStream is = TextExportFormat.class.getResourceAsStream(from);
        try {
            OutputStream os = new FileOutputStream(to);
            try {
                UTUtils.copyStream(is, os);
            } finally {
                os.close();
            }
        } finally {
            is.close();
        }
    }
    
    private String res = "usertasks-effort-html.xsl"; // NOI18N
    
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
                Settings.getDefault().getLastUsedExportFolder(), 
                "tasklist.html")); // NOI18N
        chooseFileWP.setContentHighlightedIndex(0);

        XslTemplatesPanel templatesPanel = new XslTemplatesPanel();
        templatesPanel.setAvailableLayouts(new String[] {
            NbBundle.getMessage(
                XmlExportFormat.class, "Simple"), // NOI18N
            NbBundle.getMessage(
                XmlExportFormat.class, "Effort"), // NOI18N
            NbBundle.getMessage(
                XmlExportFormat.class, "Planning"), // NOI18N
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
            UTUtils.LOGGER.log(Level.SEVERE, "", e);
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
            UTUtils.LOGGER.log(Level.WARNING, "", e);
            return null;
        } catch (TransformerException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e);
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
        File dir = chooseFilePanel.getFile().getParentFile();
        super.doExportImport(provider, wd);
        try {
            copyResourceToFile(
                    "/org/netbeans/modules/tasklist/core/task.gif", // NOI18N
                    new File(dir, "undone.gif")); // NOI18N
            copyResourceToFile(
                    "/org/netbeans/modules/tasklist/core/doneItem.gif", // NOI18N
                    new File(dir, "done.gif")); // NOI18N
        } catch (IOException ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(),
                    NotifyDescriptor.Message.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        if (templatesPanel.getOpenFile()) {
            showFileInBrowser(chooseFilePanel.getFile());
        }
    }
}
