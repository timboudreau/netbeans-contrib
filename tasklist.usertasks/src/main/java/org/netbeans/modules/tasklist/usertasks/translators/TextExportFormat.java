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
        try {
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
        SaveFilePanel panel = (SaveFilePanel)
                wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        super.doExportImport(provider, wd);
        File dir = panel.getFile().getParentFile();
        Settings.getDefault().setLastUsedExportFolder(dir);
        if (panel.getOpenExportedFile()) {
            openFileInIde(panel.getFile());
        }
    }
    
}
