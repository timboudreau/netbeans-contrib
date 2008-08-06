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

// <RAVE> Copy from projects/projectui/src/org/netbeans/modules/project/ui
package org.netbeans.modules.portalpack.servers.websynergy.portlets.php;

import org.netbeans.modules.portalpack.servers.websynergy.portlets.php.util.PortletProjectUtils;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

// <RAVE>
import java.io.File;
import java.util.Enumeration;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.api.java.classpath.ClassPath;

// XXX org.netbeans.modules.visualweb.project.jsf is not accessible under NetBeans 6.0; needs friend-package
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
// Use local copy now
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectConstants;
// </RAVE>

/**
 *
 * @author  Po-Ting Wu, Petr Hrebejk
 */
final class SimpleTargetChooserPanel implements WizardDescriptor.Panel, ChangeListener {

    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    private SimpleTargetChooserPanelGUI gui;

    private Project project;
    private SourceGroup[] folders;
    private WizardDescriptor.Panel bottomPanel;
    private WizardDescriptor wizard;
    private boolean isFolder;

    SimpleTargetChooserPanel( Project project, SourceGroup[] folders, WizardDescriptor.Panel bottomPanel, boolean isFolder ) {
        this.folders = folders;
        this.project = project;
        this.bottomPanel = bottomPanel;
        if ( bottomPanel != null ) {
            bottomPanel.addChangeListener( this );
        }
        this.isFolder = isFolder;
        this.gui = null;
    }

    public Component getComponent() {
        if (gui == null) {
            gui = new SimpleTargetChooserPanelGUI( project, folders, bottomPanel == null ? null : bottomPanel.getComponent(), isFolder );
            gui.addChangeListener(this);
        }
        return gui;
    }

    public HelpCtx getHelp() {
        if ( bottomPanel != null ) {
            HelpCtx bottomHelp = bottomPanel.getHelp();
            if ( bottomHelp != null ) {
                return bottomHelp;
            }
        }

        //XXX
        return null;

    }

    public boolean isValid() {
        boolean ok = ( gui != null && gui.getTargetName() != null &&
               ( bottomPanel == null || bottomPanel.isValid() ) );

        if (!ok) {
            return false;
        }

        // check if the file name can be created
        FileObject template = Templates.getTemplate( wizard );

        String errorMessage = canUseFileName (gui.getTargetGroup().getRootFolder(), gui.getTargetFolder(), gui.getTargetName(), template.getExt (), isFolder);
        wizard.putProperty ("WizardPanel_errorMessage", errorMessage); // NOI18N

        // <RAVE>
        // return errorMessage == null;
        if (errorMessage != null) {
            return false;
        }

        // no support for non-web project
        if (!PortletProjectUtils.isWebProject(project)) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_NotInWebProject")); // NOI18N
            return false;
        }
        
        FileObject webInf = PortletProjectUtils.getWebInf(project);
        
        // Check to make sure that the target name is not illegal
        String targetName = gui.getTargetName();
        if (!PortletProjectUtils.isValidJavaFileName(targetName)) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_InvalidJavaFileName", targetName)); // NOI18N
            return false;
        }

        // Check to make sure there is valid Source Package Folder
        if (PortletProjectUtils.getSourceRoot(project) == null) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_NoSourceRoot")); // NOI18N
            return false;
        }


        FileObject docRoot = PortletProjectUtils.getDocumentRoot(project);
        String folderPath = getFolderPath(FileUtil.getFileDisplayName(docRoot));
        if (folderPath == null) {
            return false;
        }
        
        if (folderPath.indexOf("WEB-INF") != -1) {  // NOI18N
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_InvalidTargetFolder", folderPath));
            return false;
        }

        // Check to make sure that the backing file doesn't already exist.
        String jspName = targetName + ".php";
        String javaName = targetName + ".java";
        FileObject javaDir = PortletProjectUtils.getPageBeanRoot(project);
        String javaPath = folderPath + javaName;
        if (javaPath.startsWith("/")) {
            javaPath = javaPath.substring(1);
        }
        if (javaDir.getFileObject(javaPath) != null) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_PageBeanNameConflict", javaName, jspName)); // NOI18N
            return false;
        }

        // Bug 5058134: Warn if page or bean file name differs from existing file by letter case
        FileObject folderDir = docRoot.getFileObject(folderPath);
        FileObject srcDir = javaDir.getFileObject(folderPath);
        if (((folderDir != null) && checkCaseInsensitiveName(folderDir, targetName, "jsp")) ||
            ((srcDir != null) && checkCaseInsensitiveName(srcDir, targetName, "java"))) {  // NOI18N
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_FileDifferentByCase", targetName));
        }
        
        return true;
    }

    private String getFolderPath(String targetPath) {
        // Get the path of the target folder relative to the target root
        FileObject rootDir = gui.getTargetGroup().getRootFolder();
        String rootPath = FileUtil.getFileDisplayName(rootDir).replace(File.separatorChar, '/');
        String folderName = gui.getTargetFolder();
        String folderPath = folderName != null ? (rootPath + (folderName.startsWith("/") ? "" : "/") + folderName) : rootPath; // NOI18N
        boolean isUnderTargetRoot = false;
        String relativePath = null;
        targetPath = targetPath.replace(File.separatorChar, '/');
        if (folderPath.startsWith(targetPath)) {
            relativePath = folderPath.substring(targetPath.length());
            if (relativePath.equals("") || relativePath.equals("/")) {
                isUnderTargetRoot = true;
            } else if (relativePath.startsWith("/")) {
                relativePath += "/";  // NOI18N
                isUnderTargetRoot = true;
            }
        }

        if (!isUnderTargetRoot) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_NotUnderTargetFolder",
                               folderPath.length() > rootPath.length() ? folderPath.substring(rootPath.length()+1) : folderPath,
                               targetPath.length() > rootPath.length() ? targetPath.substring(rootPath.length()+1) : targetPath)); // NOI18N
            return null;
        }

        // 5087626 Don't allow pages to be created under illegal subfolder
        String[] folderTokens = relativePath.split("/");
        for (int i = 0; i < folderTokens.length; i++) {
            String token = folderTokens[i];
            if (!"".equals(token) && !PortletProjectUtils.isValidJavaFileName(token)) {
                wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_InvalidJavaFolderName", token)); // NOI18N
                return null;
            }
        }

        return relativePath;
    }

    static boolean checkCaseInsensitiveName(FileObject folder, String targetName, String extension) {
        // bugfix #41277, check only direct children
        Enumeration children = folder.getChildren(false);
        FileObject fo;
        while (children.hasMoreElements()) {
            fo = (FileObject) children.nextElement();
            if (extension.equalsIgnoreCase(fo.getExt()) && targetName.equalsIgnoreCase(fo.getName())) {
                return true;
            }
        }
        return false;
    }
    // </RAVE>

    // <RAVE> Copy from projects/projectui/src/org/netbeans/modules/project/ui/ProjectUtilities
    /** Checks if the given file name can be created in the target folder.
     *
     * @param targetFolder target folder (e.g. source group)
     * @param folderName name of the folder relative to target folder (null or /-separated)
     * @param newObjectName name of created file
     * @param extension extension of created file
     * @param allowFileSeparator if '/' (and possibly other file separator, see {@link FileUtil#createFolder FileUtil#createFolder})
     *                           is allowed in the newObjectName
     * @return localized error message or null if all right
     */    
    public static String canUseFileName (FileObject targetFolder, String folderName, String newObjectName, String extension, boolean allowFileSeparator) {
        assert newObjectName != null; // SimpleTargetChooserPanel.isValid returns false if it is... XXX should it use an error label instead?

        boolean allowSlash = false;
        boolean allowBackslash = false;
        int errorVariant = 0;
        
        if (allowFileSeparator) {
            if (File.separatorChar == '\\') {
                errorVariant = 3;
                allowSlash = allowBackslash = true;
            } else {
                errorVariant = 1;
                allowSlash = true;
            }
        }
        
        if ((!allowSlash && newObjectName.indexOf('/') != -1) || (!allowBackslash && newObjectName.indexOf('\\') != -1)) {
            //if errorVariant == 3, the test above should never be true:
            assert errorVariant == 0 || errorVariant == 1 : "Invalid error variant: " + errorVariant;
            
            return NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_not_valid_filename", newObjectName, new Integer(errorVariant));
        }
        
        // test whether the selected folder on selected filesystem already exists
        if (targetFolder == null) {
            return NbBundle.getMessage (SimpleTargetChooserPanel.class, "MSG_fs_or_folder_does_not_exist"); // NOI18N
        }
        
        // target filesystem should be writable
        if (!targetFolder.canWrite ()) {
            return NbBundle.getMessage (SimpleTargetChooserPanel.class, "MSG_fs_is_readonly"); // NOI18N
        }

        // file should not already exist
        StringBuffer relFileName = new StringBuffer();
        if (folderName != null) {
            if (!allowBackslash && folderName.indexOf('\\') != -1) {
                return NbBundle.getMessage(SimpleTargetChooserPanel.class, "MSG_not_valid_folder", folderName, new Integer(1));
            }
            relFileName.append(folderName);
            relFileName.append('/');
        }
        relFileName.append(newObjectName);
        if (extension != null) {
            relFileName.append('.');
            relFileName.append(extension);
        }
        if (targetFolder.getFileObject(relFileName.toString()) != null) {
            return NbBundle.getMessage (SimpleTargetChooserPanel.class, "MSG_file_already_exist", newObjectName); // NOI18N
        }
        
        // all ok
        return null;
    }
    // </RAVE>
    
    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        List templist;
        synchronized (this) {
            templist = new ArrayList (listeners);
        }
        Iterator it = templist.iterator();
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(e);
        }
    }

    public void readSettings( Object settings ) {
                
        wizard = (WizardDescriptor)settings;
                
        if ( gui == null ) {
            getComponent();
        }
        
        // Try to preselect a folder            
        FileObject preselectedTarget = Templates.getTargetFolder( wizard );
        // Try to preserve the already entered target name
        String targetName = Templates.getTargetName( wizard );
        // Init values
        gui.initValues( Templates.getTemplate( wizard ), preselectedTarget, targetName );
        
        // XXX hack, TemplateWizard in final setTemplateImpl() forces new wizard's title
        // this name is used in NewFileWizard to modify the title
        Object substitute = gui.getClientProperty ("NewFileWizard_Title"); // NOI18N
        if (substitute != null) {
            wizard.putProperty ("NewFileWizard_Title", substitute); // NOI18N
        }
        
        wizard.putProperty ("WizardPanel_contentData", new String[] { // NOI18N
            NbBundle.getBundle (SimpleTargetChooserPanel.class).getString ("LBL_TemplatesPanel_Name"), // NOI18N
            NbBundle.getBundle (SimpleTargetChooserPanel.class).getString ("LBL_SimpleTargetChooserPanel_Name")}); // NOI18N
            
        if ( bottomPanel != null ) {
            bottomPanel.readSettings( settings );
        }
    }
    
    public void storeSettings(Object settings) { 
        if ( WizardDescriptor.PREVIOUS_OPTION.equals( ((WizardDescriptor)settings).getValue() ) ) {
            return;
        }
        if( isValid() ) {
            if ( bottomPanel != null ) {
                bottomPanel.storeSettings( settings );
            }
            
            FileObject template = Templates.getTemplate( wizard );
            
            String name = gui.getTargetName ();
            if (name.indexOf ('/') > 0) { // NOI18N
                name = name.substring (name.lastIndexOf ('/') + 1);
            }
            
            Templates.setTargetFolder( (WizardDescriptor)settings, getTargetFolderFromGUI () );
            Templates.setTargetName( (WizardDescriptor)settings, name );
        }
        ((WizardDescriptor)settings).putProperty ("NewFileWizard_Title", null); // NOI18N
    }

    public void stateChanged(ChangeEvent e) {        
        fireChange();
    }
    
    private FileObject getTargetFolderFromGUI () {
        FileObject rootFolder = gui.getTargetGroup().getRootFolder();
        String folderName = gui.getTargetFolder();
        String newObject = gui.getTargetName ();
        
        if (newObject.indexOf ('/') > 0) { // NOI18N
            String path = newObject.substring (0, newObject.lastIndexOf ('/')); // NOI18N
            folderName = folderName == null || "".equals (folderName) ? path : folderName + '/' + path; // NOI18N
        }

        FileObject targetFolder;
        if ( folderName == null ) {
            targetFolder = rootFolder;
        }
        else {            
            targetFolder = rootFolder.getFileObject( folderName );
        }

        if ( targetFolder == null ) {
            // XXX add deletion of the file in uninitalize ow the wizard
            try {
                targetFolder = FileUtil.createFolder( rootFolder, folderName );
            } catch (IOException ioe) {
                // XXX
                // Can't create the folder
            }
        }
        
        return targetFolder;
    }
}
