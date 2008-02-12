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
package org.netbeans.modules.xtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.awt.Component;
import javax.swing.event.ChangeListener;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.java.j2seproject.J2SEProject;
import org.netbeans.spi.project.ui.templates.support.Templates;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.NbModuleProjectType;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.netbeans.modules.apisupport.project.Util;
import org.netbeans.modules.java.api.common.SourceRoots;
import org.w3c.dom.Element;

/** Wizard iterator to create XTest testing infrastructure in J2SE project. It
 * was inspired by JavaWizardIterator.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class XTestWizardIterator implements TemplateWizard.Iterator {
    private static final long serialVersionUID = -1987345873459L;

    /** Array of panels that form the Wizard.
     */
    private transient WizardDescriptor.Panel[] panels;

    /**
     * Names for panels used in the Wizard.
     */
    private static String[] panelNames;
    
    /** Index of the current panel. Panels are numbered from 0 to PANEL_COUNT - 1.
     */
    private transient int panelIndex = 0;

    /**
     * Singleton instance of XTestWizardIterator, should it be ever needed.
     * 
     */
    private static XTestWizardIterator instance;
    
    /**
     * Holds a reference to the instance of TemplateWizard we are communicating with.
     */
    private transient TemplateWizard wizardInstance;

    public XTestWizardIterator() {
    }
    
    /**
     * Returns XTestWizardIterator singleton instance. This method is used
     * for constructing the instance from filesystem.attributes.
     * 
     */
    public static synchronized XTestWizardIterator singleton() {
        if (instance == null) {
            instance = new XTestWizardIterator();
        }
        return instance;
    }
    // ========================= TemplateWizard.Iterator ============================

    /** Instantiates the template using informations provided by the wizard.
     *
     * @param wiz the wizard
     * @return set of data objects that has been created (should contain at least one
     * @exception IOException if the instantiation fails
    */
    public java.util.Set instantiate(TemplateWizard wiz) throws IOException, IllegalArgumentException {
        HashSet createdObjects = new HashSet(5);
        Project project = Templates.getProject(wiz);
        FileObject projectDirFO = Templates.getProject(wiz).getProjectDirectory();
        FileObject templateFolderFO = wiz.getTemplatesFolder().getPrimaryFile();
        
        Hashtable templates = new Hashtable();
        templates.put("cfg-unit.xml", "cfg-unit.xml");  //NOI18N
        if(project instanceof NbModuleProject) {
            templates.put("build.xml", "nb.build.xml");  //NOI18N
            templates.put("build-unit.xml", "nb.build-unit.xml");  //NOI18N
            templates.put("build-qa-functional.xml", "nb.build-qa-functional.xml");  //NOI18N
            templates.put("cfg-qa-functional.xml", "nb.cfg-qa-functional.xml");  //NOI18N
        } else {
            templates.put("build.xml", "build.xml");  //NOI18N
            templates.put("build-unit.xml", "build-unit.xml");  //NOI18N
            templates.put("build-qa-functional.xml", "build-qa-functional.xml");  //NOI18N
            templates.put("cfg-qa-functional.xml", "cfg-qa-functional.xml");  //NOI18N
        }

        Map map = getReplaceMap(project);
        FileObject testFO = FileUtil.createFolder(projectDirFO, "test");  // NOI18N
        FileObject buildFO = templateFolderFO.getFileObject("TestingTools/"+templates.get("build.xml"));  // NOI18N
        //DataObject buildDO = DataObject.find(buildFO.copy(testFO, "build", "xml"));  // NOI18N
        DataObject buildDO = DataObject.find(createFromTemplate(buildFO, testFO, "build.xml", map));  // NOI18N
        // or also works
        //DataObject obj = DataObject.find(buildFO).createFromTemplate(DataFolder.findFolder(testFO), "build");
        createdObjects.add(buildDO);
        FileObject buildUnitFO = templateFolderFO.getFileObject("TestingTools/"+templates.get("build-unit.xml"));  // NOI18N
        DataObject buildUnitDO = DataObject.find(createFromTemplate(buildUnitFO, testFO, "build-unit.xml", map));  // NOI18N
        createdObjects.add(buildUnitDO);
        FileObject buildFunctionalFO = templateFolderFO.getFileObject("TestingTools/"+templates.get("build-qa-functional.xml"));  // NOI18N
        DataObject buildFunctionalDO = DataObject.find(createFromTemplate(buildFunctionalFO, testFO, "build-qa-functional.xml", map));  // NOI18N
        createdObjects.add(buildFunctionalDO);
        FileObject cfgUnitFO = templateFolderFO.getFileObject("TestingTools/"+templates.get("cfg-unit.xml"));  // NOI18N
        DataObject cfgUnitDO = DataObject.find(cfgUnitFO.copy(testFO, "cfg-unit", "xml"));  // NOI18N
        createdObjects.add(cfgUnitDO);
        FileObject cfgFunctionalFO = templateFolderFO.getFileObject("TestingTools/"+templates.get("cfg-qa-functional.xml"));  // NOI18N
        DataObject cfgFunctionalDO = DataObject.find(cfgFunctionalFO.copy(testFO, "cfg-qa-functional", "xml"));  // NOI18N
        createdObjects.add(cfgFunctionalDO);

        addXTestTestRoots(project);
        return createdObjects;
    }
    
    /** Returns type of project. It was copied from NbModuleProject. */
    private static NbModuleProvider.NbModuleType getModuleType(NbModuleProject nbProject) {
        Element data = nbProject.getPrimaryConfigurationData();
        if (Util.findElement(data, "suite-component", NbModuleProjectType.NAMESPACE_SHARED) != null) { // NOI18N
            return NbModuleProvider.SUITE_COMPONENT;
        } else if (Util.findElement(data, "standalone", NbModuleProjectType.NAMESPACE_SHARED) != null) { // NOI18N
            return NbModuleProvider.STANDALONE;
        } else {
            return NbModuleProvider.NETBEANS_ORG;
        }
    }
    
    public WizardDescriptor.Panel current() {
        return panels[panelIndex];
    }
    
    public String name() {
        return ""; // NOI18N
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public boolean hasPrevious() {
        return false;
    }
    
    public void nextPanel() {
        throw new NoSuchElementException();
    }
    
    public void previousPanel() {
        throw new NoSuchElementException();
    }
    
    /** Add a listener to changes of the current panel.
     * The listener is notified when the possibility to move forward/backward changes.
     * @param l the listener to add
    */
    public void addChangeListener(ChangeListener l) {
    }
    
    /** Remove a listener to changes of the current panel.
     * @param l the listener to remove
    */
    public void removeChangeListener(ChangeListener l) {
    }

    public void initialize(TemplateWizard wizard) {
        this.wizardInstance = wizard;
	if (panels == null) {
            WizardDescriptor.Panel panel = new XTestWizardPanel(Templates.getProject(wizard));
            Component panelComponent = panel.getComponent();
            panelNames = new String[]{
                NbBundle.getBundle("org.netbeans.modules.project.ui.Bundle").  // NOI18N
                         getString("LBL_TemplateChooserPanelGUI_Name"),         // NOI18N
                panelComponent.getName()
            };
            if (panelComponent instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent)panelComponent).putClientProperty(
                    "WizardPanel_contentData", panelNames); // NOI18N
            }
            panels = new WizardDescriptor.Panel[] {
                panel
            };
	}

    }
    
    public void uninitialize(TemplateWizard wiz) {
	panels = null;
        wizardInstance = null;
    }

    // ========================= IMPLEMENTATION ============================
    
    private Object readResolve() {
        return singleton();
    }
    
    /** Adds test/unit/src ("Unit Test Packages") and test/functional/src
     * ("Functional Test Packages") test roots to specified J2SE project.
     * Directories are created if needed.
     */
    private static void addXTestTestRoots(Project project) throws IOException, IllegalArgumentException {
        FileObject unitSrcFO = FileUtil.createFolder(project.getProjectDirectory(), "test/unit/src"); // NOI18N
        FileObject functionalSrcFO = FileUtil.createFolder(project.getProjectDirectory(), "test/qa-functional/src"); // NOI18N
        if(!(project instanceof J2SEProject)) {
            // add new source roots for J2SEProject only. NbModuleProject should add new sources automatically.
            return;
        }
        SourceRoots testRoots = ((J2SEProject)project).getTestSourceRoots();
        URL[] oldRoots = testRoots.getRootURLs();
        FileObject[] oldRootsFO = testRoots.getRoots();
        String[] oldNames = testRoots.getRootNames();
        String[] oldProperties = testRoots.getRootProperties();
        ArrayList/*<URL>*/ newRoots = new ArrayList();
        ArrayList/*<String>*/ newRootsLabels = new ArrayList();
        for(int i=0;i<oldRoots.length;i++) {
            // if old test root is empty, don't add it to new ones
            boolean notEmpty = false;
            if(oldRootsFO[i].getChildren().length != 0) {
                Enumeration children = oldRootsFO[i].getChildren(true);
                while(children.hasMoreElements()) {
                    if(((FileObject)children.nextElement()).hasExt("java")) { // NOI18N
                        notEmpty = true;
                        break;
                    }
                }
            }
            if(notEmpty) {
                newRoots.add(oldRoots[i]);
                newRootsLabels.add(testRoots.getRootDisplayName(oldNames[i], oldProperties[i]));
            }
        }
        newRoots.add(unitSrcFO.getURL());
        newRootsLabels.add("Unit Test Packages"); //NOI18N
        newRoots.add(functionalSrcFO.getURL());
        newRootsLabels.add("Functional Test Packages"); //NOI18N
        testRoots.putRoots((URL[])newRoots.toArray(new URL[0]), (String[])newRootsLabels.toArray(new String[0]));
        ProjectManager.getDefault().saveProject(project);
    }
    
    private Map getReplaceMap(Project project) {
        Map replaceMap = new HashMap();
        File xtestHome = InstalledFileLocator.getDefault().
                locate("xtest-distribution", "org.netbeans.modules.xtest", false);  // NOI18N
        replaceMap.put("__XTEST_HOME__", xtestHome.getAbsolutePath()); // NOI18N
        String projectName = ProjectUtils.getInformation(project).getName();
        replaceMap.put("__XTEST_MODULE__", projectName); // NOI18N
        File netbeansDestDir = InstalledFileLocator.getDefault().
                locate("core/core.jar", null, false);  // NOI18N
        // expects __NETBEANS_DEST_DIR__/platformX/core/core.jar
        replaceMap.put("__NETBEANS_DEST_DIR__", 
                netbeansDestDir.getAbsolutePath().replaceFirst(".platform.*core.jar", "")); // NOI18N
        File jemmyJar = InstalledFileLocator.getDefault().
                locate("modules/ext/jemmy.jar", "org.netbeans.modules.jemmy", false);  // NOI18N
        if(jemmyJar != null) {
            replaceMap.put("__JEMMY_JAR__", jemmyJar.getAbsolutePath()); // NOI18N
        } else {
            replaceMap.put("__JEMMY_JAR__", "jemmy.jar"); // NOI18N
        }
        if(project instanceof NbModuleProject) {
            NbModuleProject nbProject = (NbModuleProject)project;
            if(getModuleType(nbProject).equals(NbModuleProvider.NETBEANS_ORG)) {
                String relativePath = nbProject.getPathWithinNetBeansOrg();
                String nbAllRelativePath = "../..";
                int fromIndex = 0;
                while((fromIndex = 1+relativePath.indexOf('/', fromIndex)) > 0) {
                    nbAllRelativePath += "/..";
                }
                replaceMap.put("__XTEST_XML_TEMPLATE__", 
                        nbAllRelativePath+"/nbbuild/templates/xtest.xml");  //NOI18N
                replaceMap.put("__XTEST_UNIT_XML_TEMPLATE__", 
                        nbAllRelativePath+"/nbbuild/templates/xtest-unit.xml");  //NOI18N
                replaceMap.put("__XTEST_QA_FUNCTIONAL_XML_TEMPLATE__", 
                        nbAllRelativePath+"/nbbuild/templates/xtest-qa-functional.xml");  //NOI18N
            } else {
                replaceMap.put("__XTEST_XML_TEMPLATE__", 
                        "${xtest.home}/lib/templates/xtest.xml");  //NOI18N
                replaceMap.put("__XTEST_UNIT_XML_TEMPLATE__", 
                        "${xtest.home}/lib/templates/xtest-unit.xml");  //NOI18N
                replaceMap.put("__XTEST_QA_FUNCTIONAL_XML_TEMPLATE__", 
                        "${xtest.home}/lib/templates/xtest-qa-functional.xml");  //NOI18N
            }
        }
        return replaceMap;
    }
    
    /** Creates a new file object with given name in destination directory. 
     * It copies input file object and replaces keys according to the map. */
    private static FileObject createFromTemplate(FileObject inputFO, FileObject destDirFO, String name, Map map) throws IOException {
        FileObject outputFO = FileUtil.createData(destDirFO, name);
        InputStream is = inputFO.getInputStream();
        Reader reader = new InputStreamReader(is);
        BufferedReader r = new BufferedReader(reader);
        try {
            FileLock lock = outputFO.lock ();
            try {
                OutputStream os = outputFO.getOutputStream(lock);
                OutputStreamWriter w = new OutputStreamWriter(os);
                String line;
                while ((line = r.readLine ()) != null) {
                    Iterator iter = map.keySet().iterator();
                    Object key;
                    while(iter.hasNext()) {
                        key = iter.next();
                        // Need to handle backslahes otherwise they are swallowed
                        // and also to escape $ character.
                        line = line.replaceAll(
                                    key.toString(),
                                    map.get(key).toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$"));
                    }
                    w.write(line+'\n');
                }
                w.close ();
            } finally {
                lock.releaseLock ();
            }
        } finally {
            r.close ();
        }
        return outputFO;
    }

    private void notifyError(String msg) {
        this.wizardInstance.putProperty("WizardPanel_errorMessage", msg); //NOI18N
        IllegalStateException ex = new IllegalStateException(msg);
        ErrorManager.getDefault().annotate(ex, ErrorManager.USER, null, msg, null, null);
        throw ex;
    }
}
