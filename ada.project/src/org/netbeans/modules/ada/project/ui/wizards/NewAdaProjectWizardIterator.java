/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.project.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.ada.project.AdaProjectType;
import org.netbeans.modules.ada.project.SourceRoots;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NewAdaProjectWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {    
    
    static final String SET_AS_MAIN = "setAsMain";  //NOI18N
    static final String MAIN_FILE ="mainFile";      //NOI18N
    static final String PROP_PROJECT_NAME = "projectName";  //NOI18N
    static final String PROP_PROJECT_LOCATION = "pojectLocation";   //NOI18N
    static final String PROP_PLATFORM_ID = "platform";              //NOI18N
    static final String SOURCE_ROOTS = "sources";                   //NOI18N
    static final String TEST_ROOTS = "tests";                       //NOI18N

    public static enum WizardType {
        NEW,
        EXISTING,
    }

    private final WizardType wizardType;
    private WizardDescriptor descriptor;
    private WizardDescriptor.Panel[] panels;
    private int index;

    public NewAdaProjectWizardIterator() {
        this(WizardType.NEW);
    }

    private NewAdaProjectWizardIterator(WizardType wizardType) {
        this.wizardType = wizardType;
    }   
    
    public static NewAdaProjectWizardIterator createApplication () {
        return new NewAdaProjectWizardIterator();
    }

    public static NewAdaProjectWizardIterator createExistingProject () {        
        return new NewAdaProjectWizardIterator (WizardType.EXISTING);
    }

    public void initialize(WizardDescriptor wizard) {
        descriptor = wizard;
        index = 0;
        panels = createPanels();
        // normally we would do it in uninitialize but we have listener on ide options (=> NPE)
        initDescriptor(wizard);
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
        descriptor = null;
    }

    public Set instantiate() throws IOException {
        assert false : "Cannot call this method if implements WizardDescriptor.ProgressInstantiatingIterator.";
        return null;
    }

    public Set instantiate(ProgressHandle handle) throws IOException {
        final Set<FileObject> resultSet = new HashSet<FileObject>();

        handle.start(5);

        String msg = NbBundle.getMessage(
                NewAdaProjectWizardIterator.class, "LBL_NewAdaProjectWizardIterator_WizardProgress_CreatingProject");
        handle.progress(msg, 3);

        // project
        File projectDirectory = (File) descriptor.getProperty(PROP_PROJECT_LOCATION);
        ProjectChooser.setProjectsFolder(projectDirectory.getParentFile());
        
        String projectName = (String) descriptor.getProperty(PROP_PROJECT_NAME);
        AntProjectHelper helper = createProject(projectDirectory, projectName);
        resultSet.add(helper.getProjectDirectory());

        if (wizardType == WizardType.NEW) {
            // sources
            FileObject sourceDir = createSourceRoot();
            resultSet.add(sourceDir);

            // tests
            FileObject testDir = createTestRoot();
            resultSet.add(testDir);

            // main file
            final String mainName = (String) descriptor.getProperty(NewAdaProjectWizardIterator.MAIN_FILE);        
            if (mainName != null) {            
                resultSet.add(createMainFile(Repository.getDefault().getDefaultFileSystem().findResource("Templates/Ada/NewAdaMain.adb"),
                        sourceDir,mainName).getPrimaryFile());
            }
        }
        
        msg = NbBundle.getMessage(
                NewAdaProjectWizardIterator.class, "LBL_NewAdaProjectWizardIterator_WizardProgress_PreparingToOpen");
        handle.progress(msg, 5);

        return resultSet;
    }

    public String name() {
        return NbBundle.getMessage(NewAdaProjectWizardIterator.class, "LBL_IteratorName", index + 1, panels.length);
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }
    public boolean hasPrevious() {
        return index > 0;
    }
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        // wizard title
        String title = NbBundle.getMessage(NewAdaProjectWizardIterator.class, wizardType == WizardType.NEW ? "TXT_AdaProject" : "TXT_ExistingAdaProject");
        descriptor.putProperty("NewProjectWizard_Title", title); // NOI18N
        return panels[index];
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }
    
    static String getFreeFolderName (final File owner, final String proposal) {
        assert owner != null;
        assert proposal != null;
        String freeName = proposal;
        File f = new File (owner, freeName);
        int counter = 1;        
        while (f.exists()) {
            counter++;
            freeName = proposal+counter;
            f = new File (owner,freeName);
        }
       return freeName;
    }

    private WizardDescriptor.Panel[] createPanels() {
        switch (wizardType) {
            case NEW:
            {
                String[] steps = new String[] {
                    NbBundle.getBundle(NewAdaProjectWizardIterator.class).getString("LBL_ProjectNameLocation"),
                };

                PanelConfigureProject configureProjectPanel = new PanelConfigureProject(wizardType, steps);
                return new WizardDescriptor.Panel[] {
                    configureProjectPanel,
                };
            }
            case EXISTING:
            {
                String[] steps = new String[] {
                    NbBundle.getBundle(NewAdaProjectWizardIterator.class).getString("LBL_ProjectNameLocation"),
                    NbBundle.getMessage(NewAdaProjectWizardIterator.class, "LBL_ProjectSources"),
                };

                PanelConfigureProject configureProjectPanel = new PanelConfigureProject(wizardType, steps);
                PanelConfigureSources configureSourcesPanel = new PanelConfigureSources(wizardType, steps);
                return new WizardDescriptor.Panel[] {
                    configureProjectPanel,
                    configureSourcesPanel,
                };
            }
            default:
                throw new IllegalStateException(wizardType.toString());
        }
    }

    // prevent incorrect default values (empty project => back => existing project)
    private void initDescriptor(WizardDescriptor settings) {
        settings.putProperty(PROP_PROJECT_NAME, null);
        settings.putProperty(PROP_PROJECT_LOCATION, null);
        settings.putProperty(SOURCE_ROOTS, new File[0]);            
        settings.putProperty(TEST_ROOTS, new File[0]);
    }    
    
    private AntProjectHelper createProject(final File dir, final String name) throws IOException {
        FileObject projectFO = FileUtil.createFolder(dir);
        final AntProjectHelper helper = ProjectGenerator.createProject(projectFO, AdaProjectType.TYPE);
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run () throws MutexException {
                    try {
                    // configure
                    final Element data = helper.getPrimaryConfigurationData(true);
                    Document doc = data.getOwnerDocument();
                    Element nameEl = doc.createElementNS(AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                    nameEl.appendChild(doc.createTextNode(name));
                    data.appendChild(nameEl);


                    EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);

                    configureSources(helper, data, properties);
                    configureRuntime(properties);
                    configureMainFile(properties);        
                    helper.putPrimaryConfigurationData(data, true);
                    helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);

                    Project project = ProjectManager.getDefault().findProject(helper.getProjectDirectory());
                    ProjectManager.getDefault().saveProject(project);
                    return null;
                    } catch (IOException ioe) {
                       throw new MutexException(ioe); 
                    }
                }
            });
        } catch (MutexException e) {
            Exception ie = e.getException();
            if (ie instanceof IOException) {
                throw (IOException)ie;
            }
            Exceptions.printStackTrace(e);
        }
        return helper;
    }    

    private void configureSources(final AntProjectHelper helper, final Element data, final EditableProperties properties) {
        final List<? extends File> srcDirs = getSources();
        final Document doc = data.getOwnerDocument();
        final Element sourceRoots = doc.createElementNS(AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE,SourceRoots.E_SOURCES);
        final File projectDirectory = FileUtil.toFile(helper.getProjectDirectory());
        appendRoots (sourceRoots, properties, srcDirs, projectDirectory, doc);        
        data.appendChild (sourceRoots);
        final List<? extends File> testDirs = getTests();
        final Element testRoots = doc.createElementNS(AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE,SourceRoots.E_TESTS);
        appendRoots (testRoots, properties, testDirs, projectDirectory, doc);
        data.appendChild (testRoots);
    }    
    
    private void appendRoots (Element node, EditableProperties properties, List<? extends File> roots, File projectDirectory, Document doc) { 
        for (File srcDir : roots) {
            String srcPath = PropertyUtils.relativizeFile(projectDirectory, srcDir);
            // # 132319
            if (srcPath == null || srcPath.startsWith("../")) { // NOI18N
                // relative path, change to absolute
                srcPath = srcDir.getAbsolutePath();
            }
            Element root = doc.createElementNS (AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE,"root");   //NOI18N
            String propName;
            String name = srcDir.getName();
            propName = name + ".dir";    //NOI18N
            int rootIndex = 1;                            
            while (properties.containsKey(propName)) {
                rootIndex++;
                propName = name + rootIndex + ".dir";   //NOI18N
            }
            root.setAttribute ("id",propName);   //NOI18N
            node.appendChild(root);
            properties.setProperty(propName, srcPath);            
        }
    }

    private void configureRuntime (final EditableProperties properties) {
        String platformId = (String) descriptor.getProperty(PROP_PLATFORM_ID);
        assert platformId != null;
        properties.setProperty(AdaProjectProperties.ACTIVE_PLATFORM, platformId);
        properties.setProperty(AdaProjectProperties.ADA_LIB_PATH, "");    //NOI18N
    }
    
    private void configureMainFile(EditableProperties properties) {
        String mainFile = (String) descriptor.getProperty(NewAdaProjectWizardIterator.MAIN_FILE);
        if (mainFile != null) {
            properties.setProperty(AdaProjectProperties.MAIN_FILE, mainFile);
        }
    }   

    private FileObject createSourceRoot() throws IOException {
        return FileUtil.createFolder(getSources().get(0));
    }

    private FileObject createTestRoot() throws IOException {
        return FileUtil.createFolder(getTests().get(0));
    }

    private List<? extends File> getSources () {
        if (wizardType == WizardType.NEW) {
            return Collections.singletonList(new File ((File) descriptor.getProperty(PROP_PROJECT_LOCATION),"src"));   //NOI18N
        }
        else if (wizardType == WizardType.EXISTING) {
            return Arrays.asList((File[])descriptor.getProperty(SOURCE_ROOTS));            
        }
        else {
            throw new UnsupportedOperationException();
        }
    }
        
    private List<? extends File> getTests () {
        if (wizardType == WizardType.NEW) {
            return Collections.singletonList(new File ((File) descriptor.getProperty(PROP_PROJECT_LOCATION),"test"));   //NOI18N
        }
        else if (wizardType == WizardType.EXISTING) {
            return Arrays.asList((File[])descriptor.getProperty(TEST_ROOTS));            
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private DataObject createMainFile(FileObject template, FileObject sourceDir, String name) throws IOException {
        DataFolder dataFolder = DataFolder.findFolder(sourceDir);
        DataObject dataTemplate = DataObject.find(template);
        //Strip extension when needed
        int idx = name.lastIndexOf('.');
        if (idx >0 && idx<name.length()-1 && "adb".equalsIgnoreCase(name.substring(idx+1))) {
            name = name.substring(0, idx);
        }
        return dataTemplate.createFromTemplate(dataFolder, name);
    }
    
}
