/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.clearcase.ui.add;

import org.openide.util.*;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.netbeans.api.project.*;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.modules.clearcase.ui.wizard.AbstractStep;
import org.netbeans.modules.clearcase.ui.wizard.RepositoryStep;
import org.netbeans.modules.clearcase.ui.selectors.ModuleSelector;
import org.netbeans.modules.clearcase.client.WizardStepProgressSupport;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.FileStatusCache;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Imports folder into Clearcase repository. It's enabled on Nodes that represent:
 * <ul>
 * <li>project root directory, parent of all necessary
 * project data and metadata.
 * <li>folders that are not a part on any project
 * </ul>
 * It's minimalitics attempt to assure
 * that the project can be reopend after checkout.
 * It also simplifies implemenattion avoiding huge
 * import mapping wizard for projects with external
 * data folders.
 *
 *
 * @author Petr Kuzel, Ramin Moazeni
 */
public final class AddToRepositoryAction extends AbstractAction implements ChangeListener {

    private WizardDescriptor wizard;
    public static boolean  canBeCheckedOut = false;
    private WizardDescriptor.Iterator wizardIterator;

    private RepositoryStep repositoryStep;
    private ImportStep importStep;
    private WizardStepProgressSupport support;
    
    private final VCSContext context;

    public AddToRepositoryAction(String name, VCSContext context) {
        super(name);
        this.context = context;
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    public String getName() {
        return NbBundle.getMessage(AddToRepositoryAction.class, "BK0006");
    }

    public void actionPerformed(ActionEvent e) {
        // TODO: only import one folder? seems most reasonable
        final File importDirectory = context.getRootFiles().iterator().next();
        
        if (importDirectory != null) {

            String prefRoot = NbBundle.getMessage(AddToRepositoryAction.class, "BK0008");
            String prefModule = importDirectory.getName();
            
            wizardIterator = panelIterator(prefRoot, prefModule, importDirectory.getAbsolutePath());
            wizard = new WizardDescriptor(wizardIterator);
            wizard.putProperty("WizardPanel_contentData",  // NOI18N
                    new String[] {
                        NbBundle.getMessage(AddToRepositoryAction.class, "BK0015"),
                        NbBundle.getMessage(AddToRepositoryAction.class, "BK0014")
                    }
            );
            wizard.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);  // NOI18N
            wizard.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);  // NOI18N
            wizard.putProperty("WizardPanel_contentNumbered", Boolean.TRUE);  // NOI18N
            wizard.setTitleFormat(new MessageFormat("{0}"));  // NOI18N
            String title = NbBundle.getMessage(AddToRepositoryAction.class, "BK0007");
            wizard.setTitle(title);

            Object result = DialogDisplayer.getDefault().notify(wizard);
            if (result == DialogDescriptor.OK_OPTION) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        async(importDirectory);
                    }
                });
            }
            
                FileObject projectFolder = FileUtil.toFileObject(importDirectory);
                if (projectFolder != null) {
                    try {
                        Project p = ProjectManager.getDefault().findProject(projectFolder);
                        if (p != null) {
                            closeProject(p);
                        }
                    } catch (IOException e1) {
                        ErrorManager err = ErrorManager.getDefault();
                        err.annotate(e1, NbBundle.getMessage(AddToRepositoryAction.class, "BK1014", projectFolder));
                        err.notify(e1);
                    }
                }
  
        }
    }

    private void async(File importDirectory) {
  
        String logMessage = importStep.getMessage();    //import message
        String module = importStep.getModule();         //repository module
        String folder = importStep.getFolder();         //folder to import
        String selectedRoot = repositoryStep.getRepositoryFile().getFileUrl();

        File dir = new File(folder);
        
        Clearcase cc = Clearcase.getInstance();
        File [] files = context.getRootFiles().toArray(new File[context.getRootFiles().size()]);
        if (files == null || files.length == 0) {
            ErrorManager.getDefault().notify(new ClearcaseException("No files selected. Please select a file for checkin."));
            return;
        }
        FileStatusCache cache = cc.getInstance().getFileStatusCache();
        try {
            
            List<String> list = new LinkedList<String>();
            // TODO: MERGE
/*
            for (int i=0; i < files.length; i++) {
                list = ClearcaseCommand.doRecursiveCheckin(selectedRoot, module, logMessage, dir);
                ClearcaseOutput.getInstance().println("Import to Clearcase Repository Completed...");
            }
*/
            canBeCheckedOut = true;
            File newProject = new File(module + "/" + files[0].getName());
             FileObject projectFolder = FileUtil.toFileObject(newProject);
                    if (projectFolder != null) {
                        try {
                            Project p = ProjectManager.getDefault().findProject(projectFolder);
                            if (p != null) {
                                openProject(p);
                            }
                        } catch (IOException e1) {
                            ErrorManager err = ErrorManager.getDefault();
                            err.annotate(e1, NbBundle.getMessage(AddToRepositoryAction.class, "BK1014", projectFolder));
                            err.notify(e1);
                        }
                    }
        } catch (Exception iox) {
            NotifyDescriptor.Exception e = new NotifyDescriptor.Exception(iox);
            DialogDisplayer.getDefault().notifyLater(e);
        }
        for(int i = 0; i < files.length; i++){
        
            // update cached file status with new repository file status
            // TODO: MERGE
//            Clearcase.getInstance().getFileStatusCache().refresh(files[i], FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_ADDED);
        }
    }

    public boolean cancel() {

        return true;
    }

    private File lookupImportDirectory(Node node) {
        File importDirectory = null;
        Project project = (Project) node.getLookup().lookup(Project.class);
        if (project != null) {
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
            if (groups.length == 1) {
                FileObject root = groups[0].getRootFolder();
                importDirectory = FileUtil.toFile(root);
            } else {
                importDirectory = FileUtil.toFile(project.getProjectDirectory());
            }
        } else {
            FileObject fo = null;
            Collection fileObjects = node.getLookup().lookup(new Lookup.Template(FileObject.class)).allInstances();
            if (fileObjects.size() > 0) {
                fo = (FileObject) fileObjects.iterator().next();
            } else {
                DataObject dataObject = (DataObject) node.getCookie(DataObject.class);
                if (dataObject instanceof DataShadow) {
                    dataObject = ((DataShadow) dataObject).getOriginal();
                }
                if (dataObject != null) {
                    fo = dataObject.getPrimaryFile();
                }
            }

            if (fo != null) {
                File f = FileUtil.toFile(fo);
                if (f != null && f.isDirectory()) {
                    importDirectory = f;
                }
            }
        }
        return importDirectory;
    }

    private WizardDescriptor.Iterator panelIterator(String root, String module, String folder) {
        repositoryStep = new RepositoryStep(RepositoryStep.IMPORT_HELP_ID);
        repositoryStep.addChangeListener(this);
        importStep = new ImportStep(module, folder);
        importStep.addChangeListener(this);

        final WizardDescriptor.Panel[] panels = new WizardDescriptor.Panel[2];
        panels[0] = repositoryStep;
        panels[1] = importStep;

        WizardDescriptor.ArrayIterator ret = new WizardDescriptor.ArrayIterator(panels) {
            public WizardDescriptor.Panel current() {
                WizardDescriptor.Panel ret = super.current();
                for (int i = 0; i<panels.length; i++) {
                    if (panels[i] == ret) {
                        wizard.putProperty("WizardPanel_contentSelectedIndex", new Integer(i));  // NOI18N
                    }
                }
                return ret;
            }
        };
        return ret;
    }

    private void setErrorMessage(String msg) {
        if (wizard != null) {
            wizard.putProperty("WizardPanel_errorMessage", msg); // NOI18N
        }
    }

    public void stateChanged(ChangeEvent e) {
        AbstractStep step = (AbstractStep) wizardIterator.current();
        setErrorMessage(step.getErrorMessage());
    }

    class ImportStep extends AbstractStep implements ActionListener {
        private final String module;
        private final String folder;
        private ImportPanel importPanel;

        public ImportStep(String module, String folder) {
            this.module = module;
            this.folder = folder;
        }

        public HelpCtx getHelp() {
            return new HelpCtx(ImportStep.class);
        }

        protected JComponent createComponent() {
            importPanel = new ImportPanel();
            importPanel.folderTextField.setText(folder);

            // user input validation
            DocumentListener validation = new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                }
                public void insertUpdate(DocumentEvent e) {
                    String s = checkInput(importPanel);
                    if (s == null) {
                        valid();
                    } else {
                        invalid(s);
                    }
                }
                public void removeUpdate(DocumentEvent e) {
                    String s = checkInput(importPanel);
                    if (s == null) {
                        valid();
                    } else {
                        invalid(s);
                    }
                }
            };
            importPanel.moduleTextField.getDocument().addDocumentListener(validation);
            importPanel.commentTextArea.getDocument().addDocumentListener(validation);
            importPanel.folderTextField.getDocument().addDocumentListener(validation);
            importPanel.folderButton.addActionListener(this);
            importPanel.moduleButton.addActionListener(this);

            String s = checkInput(importPanel);
            if (s == null) {
                valid();
            } else {
                invalid(s);
            }
    
            return importPanel;
        }

        protected void validateBeforeNext() {
            try {
                support =  new ImportProgressSupport(importPanel.progressPanel, importPanel.progressLabel);        
                RequestProcessor rp = Clearcase.getInstance().getRequestProcessor();    
                RequestProcessor.Task task = support.start(rp,org.openide.util.NbBundle.getMessage(ImportStep.class, "CTL_Import_Progress"));
                task.waitFinished();
            } finally {
                support = null;
            }
        }

       
       

        public String getMessage() {
            return importPanel.commentTextArea.getText();
        }

        public String getModule() {
            return importPanel.moduleTextField.getText();
        }

        public String getFolder() {
            return importPanel.folderTextField.getText();
        }
        
        public String getImportMessage() {
            return importPanel.commentTextArea.getText();
        }
        /**
         * Returns file to be initaly used.
         * <ul>
         * <li>first is takes text in workTextField
         * <li>then recent project folder
         * <li>finally <tt>user.home</tt>
         * <ul>
         */
        private File defaultWorkingDirectory() {
            File defaultDir = null;
            String current = importPanel.folderTextField.getText();
            if (current != null && !(current.trim().equals(""))) {  // NOI18N
                File currentFile = new File(current);
                while (currentFile != null && currentFile.exists() == false) {
                    currentFile = currentFile.getParentFile();
                }
                if (currentFile != null) {
                    if (currentFile.isFile()) {
                        defaultDir = currentFile.getParentFile();
                    } else {
                        defaultDir = currentFile;
                    }
                }
            }

            if (defaultDir == null) {
                File projectFolder = ProjectChooser.getProjectsFolder();
                if (projectFolder.exists() && projectFolder.isDirectory()) {
                    defaultDir = projectFolder;
                }
            }

            if (defaultDir == null) {
                defaultDir = new File(System.getProperty("user.home"));  // NOI18N
            }

            return defaultDir;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == importPanel.folderButton) {
                File defaultDir = defaultWorkingDirectory();
                JFileChooser fileChooser = new JFileChooser(defaultDir);
                fileChooser.setDialogTitle(NbBundle.getMessage(AddToRepositoryAction.class, "BK1017"));
                fileChooser.setMultiSelectionEnabled(false);
                javax.swing.filechooser.FileFilter[] old = fileChooser.getChoosableFileFilters();
                for (int i = 0; i < old.length; i++) {
                    javax.swing.filechooser.FileFilter fileFilter = old[i];
                    fileChooser.removeChoosableFileFilter(fileFilter);

                }
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }
                    public String getDescription() {
                        return NbBundle.getMessage(AddToRepositoryAction.class, "BK1018");
                    }
                });
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showDialog(importPanel, NbBundle.getMessage(AddToRepositoryAction.class, "BK1019"));
                File f = fileChooser.getSelectedFile();
                if (f != null) {
                    importPanel.folderTextField.setText(f.getAbsolutePath());
                }
            } else if (e.getSource() == importPanel.moduleButton) {
                ModuleSelector selector = new ModuleSelector();
                String root = repositoryStep.getRepositoryFile().getFileUrl();
                
                String path = selector.selectRepositoryPath(root);
                if (path != null) {
                    /*if (!path.endsWith(module)) {
                        path += "/" + module;
                    }*/
                    importPanel.moduleTextField.setText(path);
                }
            }
        }
        
        public boolean validateUserInput() {
            invalid(null);
            
            String text = importPanel.moduleTextField.getText().trim();
            if (text.length() == 0) {
                invalid(org.openide.util.NbBundle.getMessage(ImportStep.class, "BK2014")); // NOI18N
                return false;
            }
            
            text = importPanel.commentTextArea.getText().trim();
            boolean valid = text.length() > 0;
            if(valid) {
                valid();
            } else {
                invalid(org.openide.util.NbBundle.getMessage(ImportStep.class, "CTL_Import_MessageRequired")); // NOI18N
            }
            
            return valid;
        }
        private class ImportProgressSupport extends WizardStepProgressSupport {
        public ImportProgressSupport(JPanel panel, JLabel label) {
            super(panel);
        }
        public void perform() {
            String invalidMsg = null;
            String logMessage = importStep.getMessage();    //import message
            String module = importStep.getModule();         //repository module
            String folder = importStep.getFolder();         //folder to import
            String selectedRoot = repositoryStep.getRepositoryFile().getFileUrl();
            
            try {
                if(!validateUserInput()) {
                    return;
                }
                
                invalid(null);
                              
                File dir = new File(folder);    //import directory
          
                Clearcase cc = Clearcase.getInstance();
                File [] files = context.getRootFiles().toArray(new File[context.getRootFiles().size()]);
                if (files == null || files.length == 0) {
                    ErrorManager.getDefault().notify(new ClearcaseException("No files selected. Please select a file for checkin."));
                    return;
                }
                FileStatusCache cache = cc.getInstance().getFileStatusCache();
                
                try {
                    
                    List<String> list = new LinkedList<String>();
                    // TODO: MERGE
/*
                    for (int i=0; i < files.length; i++) {
                        list = ClearcaseCommand.doRecursiveCheckin(selectedRoot, module, logMessage, dir);
                        
                        ClearcaseOutput.getInstance().println("Import to Clearcase Repository Completed...");
                        
                    }
*/
                    canBeCheckedOut = true;
                } catch (Exception ex) {
                }
                for(int i = 0; i < files.length; i++){
                    
                    // update cached file status with new repository file status
                    // TODO: MERGE
//                    Clearcase.getInstance().getFileStatusCache().refresh(files[i], FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_ADDED);
                }
                //}
                if(isCanceled()) {
                    return;
                }
               
                // TODO: MERGE
/*
                Clearcase.getInstance().versionedFilesChanged();
                ClearcaseUtils.refreshParents(dir);
*/
                // XXX this is ugly and expensive! the client should notify (onNotify()) the cache. find out why it doesn't work...
                forceStatusRefresh(dir);  
                if(isCanceled()) {
                    return;
                }
            } finally {
                // TODO: MERGE
//                Clearcase.getInstance().versionedFilesChanged();
                if(isCanceled()) {
                    valid(org.openide.util.NbBundle.getMessage(ImportStep.class, "MSG_Import_ActionCanceled")); // NOI18N
                } else if(invalidMsg != null) {
                    valid(invalidMsg);
                } else {
                    valid();
                }
            }
        }

            public void setEditable(boolean bl) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        }
        private void forceStatusRefresh(File file) {
            // TODO: MERGE
//            Clearcase.getInstance().getStatusCache().refresh(file, FileStatus.RepositoryStatus.REPOSITORY_STATUS_UNKNOWN);
            if(!file.isFile()) {
                File[] files = file.listFiles();
                if(files == null) {
                    return;
                }
                for (int i = 0; i < files.length; i++) {
                    forceStatusRefresh(files[i]);
                }
            }
        }
    }

    private static String checkInput(ImportPanel importPanel) {
        boolean valid = true;

        File file = new File(importPanel.folderTextField.getText());
        valid &= file.isDirectory();
        if (!valid) return NbBundle.getMessage(AddToRepositoryAction.class, "BK0022");
        try {
            // TODO: MERGE
//            valid &= ClearcaseCommand.getClearcaseManagedStatus (file) == false;
        } catch (Exception iox) {
            NotifyDescriptor.Exception e = new NotifyDescriptor.Exception(iox);
            DialogDisplayer.getDefault().notifyLater(e);
        }
        
        if (!valid) return NbBundle.getMessage(AddToRepositoryAction.class, "BK0023");

        valid &= importPanel.commentTextArea.getText().trim().length() > 0;
        if (!valid) return NbBundle.getMessage(AddToRepositoryAction.class, "BK0024");

        String module = importPanel.moduleTextField.getText().trim();
        valid &= module.length() > 0;
        if (!valid) return NbBundle.getMessage(AddToRepositoryAction.class, "BK0025");
        valid &= module.indexOf(" ") == -1;  // NOI18N // NOI18N
        valid &= ".".equals(module.trim()) == false;  // NOI18N
        if (!valid) return NbBundle.getMessage(AddToRepositoryAction.class, "BK0026");

        return null;
    }

    /**
     * @return false on Thread.interrupted i.e. user cancel.
     */
    private boolean prepareIgnore(File dir) throws IOException {
        File[] projectMeta = dir.listFiles();
        Set ignored = new HashSet();
        for (int i = 0; i < projectMeta.length; i++) {
            if (Thread.interrupted()) {
                return false;
            }
            File file = projectMeta[i];
            String name = file.getName();
            int sharability = SharabilityQuery.getSharability(file);
            if (sharability == SharabilityQuery.MIXED) {
                assert file.isDirectory() : file;
                prepareIgnore(file);
            }
        }
        return true;
    }

    protected boolean asynchronous() {
        return false;
    }
    
    public void setEditable(boolean bl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void closeProject(Project p) {
        Project[] projects = new Project[]{p};
        OpenProjects.getDefault().close(projects);
    }
    private void openProject(Project p) {
        Project[] projects = new Project[]{p};
        OpenProjects.getDefault().open(projects, false);

        // set as main project and expand
        OpenProjects.getDefault().setMainProject(p);        
        //ProjectUtilities.selectAndExpandProject(p);
    }
    
}
