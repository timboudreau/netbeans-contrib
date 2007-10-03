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

package org.netbeans.modules.j2ee.ejbfreeform.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.ant.freeform.spi.support.NewFreeformProjectSupport;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.java.freeform.spi.support.NewJavaFreeformProjectSupport;
import org.netbeans.modules.j2ee.ejbfreeform.EJBProjectGenerator;
import org.netbeans.modules.j2ee.ejbfreeform.EJBProjectNature;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


/**
 * @author  David Konecny, Radko Najman
 */
public class NewEJBFreeformProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

    // EJB sources
    public static final String PROP_EJB_EJBMODULES = "ejbModules"; // <List> NOI18N
    public static final String PROP_EJB_SOURCE_FOLDERS = "ejbSourceFolders"; // <List> NOI18N
    public static final String PROP_EJB_RESOURCE_FOLDERS = "ejbResourceFolders"; // <List> NOI18N
    public static final String SERVER_ID = "serverID"; //NOI18N
    public static final String J2EE_LEVEL = "j2eeLevel"; //NOI18N
    public static final String J2EE_SERVER_TYPE = "j2eeServerType"; //NOI18N
    
    private static final long serialVersionUID = 1L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
    public NewEJBFreeformProjectWizardIterator() {
    }
    
    private WizardDescriptor.Panel[] createPanels () {
        List l = new ArrayList();
        List extraTargets = new ArrayList();
        extraTargets.add(EJBProjectNature.getExtraTarget());
        l.add(NewFreeformProjectSupport.createBasicProjectInfoWizardPanel());
        l.add(NewFreeformProjectSupport.createTargetMappingWizardPanel(extraTargets));
        l.add(new EJBLocationsWizardPanel());
        l.addAll(Arrays.asList(NewJavaFreeformProjectSupport.createJavaPanels()));
        return (WizardDescriptor.Panel[])l.toArray(new WizardDescriptor.Panel[l.size()]);
    }
    
    public Set/*<FileObject>*/ instantiate () throws IOException {
        final WizardDescriptor wiz = this.wiz;
        final IOException[] ioe = new IOException[1];
        ProjectManager.mutex().writeAccess(new Runnable() {
            public void run() {
                try {
                    AntProjectHelper helper = NewFreeformProjectSupport.instantiateBasicProjectInfoWizardPanel(wiz);
                    NewFreeformProjectSupport.instantiateTargetMappingWizardPanel(helper, wiz);
                    NewJavaFreeformProjectSupport.instantiateJavaPanels(helper, wiz);

                    List ejbSources = (List)wiz.getProperty(PROP_EJB_SOURCE_FOLDERS);
                    AuxiliaryConfiguration aux = Util.getAuxiliaryConfiguration(helper);
                    EJBProjectGenerator.putEJBSourceFolder(helper, ejbSources);
                    
                    List resources = (List) wiz.getProperty(PROP_EJB_RESOURCE_FOLDERS);
                    EJBProjectGenerator.putResourceFolder(helper, resources);
        
                    String j2eeLevel = (String) wiz.getProperty(J2EE_LEVEL);
                    EJBProjectGenerator.putJ2EELevel(helper, j2eeLevel);
                    
                    String serverID = (String) wiz.getProperty(SERVER_ID);
                    EJBProjectGenerator.putServerID(helper, serverID);
                    
                    List ejbModules = (List) wiz.getProperty(PROP_EJB_EJBMODULES);
                    if (ejbModules != null) {
                        EJBProjectGenerator.putEJBModules (helper, aux, ejbModules);
                    }
                    
                    Project p = ProjectManager.getDefault().findProject(helper.getProjectDirectory());
                    
                    org.netbeans.modules.j2ee.api.ejbjar.EjbJar ejbModule = org.netbeans.modules.j2ee.api.ejbjar.EjbJar.getEjbJars(p)[0];
                    if (ejbModule != null) {
                        FileObject ejbJarFile = ejbModule.getDeploymentDescriptor();
                        EjbJar dd = DDProvider.getDefault().getDDRoot(ejbJarFile);
                        // #82897: putEJBNodeView() is called here because Enterprise Beans
                        // node is supposed to be shown only for non EJB 3.0 projects
                        if (!new BigDecimal(EjbJar.VERSION_3_0).equals(dd.getVersion())) {
                            EJBProjectGenerator.putEJBNodeView(helper, ejbSources);
                        }
                        boolean write = false;
                        // set the DD display name if there is none
                        String dispName = dd.getDefaultDisplayName();
                        if ((null == dispName || dispName.trim().length() == 0) && (ejbJarFile != null))  {
                            dd.setDisplayName(helper.getProjectDirectory().getName());
                            write = true;
                        }
                        // update the DD to 2.1 if it is 2.0 and the user chose J2EE 1.4
                        if (j2eeLevel.equals("1.4") && !new BigDecimal(EjbJar.VERSION_2_1).equals(dd.getVersion())) { // NOI18N
                            dd.setVersion(new BigDecimal(EjbJar.VERSION_2_1));
                            write = true;
                        }
                        // probably update the DD to 3.0 if it's 1.5 and no DD specified - XXX after #82231 is fixed!
                        // if (j2eeLevel.equals("1.5") && ejbJarFile != null) { // NOI18N
                        //     dd.setVersion(new BigDecimal(EjbJar.VERSION_3_0));
                        //     write = true;
                        // }
                        // ejbJarFile can be null for Java EE 5
                        if (write && ejbJarFile != null) {
                            dd.write(ejbJarFile);
                        }
                    }
                    
                    ProjectManager.getDefault().saveProject(p);
                } catch (IOException e) {
                    ioe[0] = e;
                    return;
                }
            }});
        if (ioe[0] != null) {
            throw ioe[0];
        }
        File nbProjectFolder = (File)wiz.getProperty(NewFreeformProjectSupport.PROP_PROJECT_FOLDER);
        Set resultSet = new HashSet();
        resultSet.add(FileUtil.toFileObject(nbProjectFolder));
        File f = nbProjectFolder.getParentFile();
        if (f != null) {
            ProjectChooser.setProjectsFolder(f);
        }
        return resultSet;
    }
    
        
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        
        List l = new ArrayList();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            assert c instanceof JComponent;
            JComponent jc = (JComponent)c;
            l.add(jc.getName());
        }
        String[] steps = (String[])l.toArray(new String[l.size()]);
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            assert c instanceof JComponent;
            JComponent jc = (JComponent)c;
            // Step #.
            jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
            // Step name (actually the whole list for reference).
            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            // set title
            jc.putClientProperty ("NewProjectWizard_Title", NbBundle.getMessage (NewEJBFreeformProjectWizardIterator.class, "TXT_NewEJBFreeformProjectWizardIterator_NewProjectWizardTitle")); // NOI18N
        }
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        NewFreeformProjectSupport.uninitializeBasicProjectInfoWizardPanel(wiz);
        NewFreeformProjectSupport.uninitializeTargetMappingWizardPanel(wiz);
        NewJavaFreeformProjectSupport.uninitializeJavaPanels(wiz);
        wiz.putProperty(PROP_EJB_SOURCE_FOLDERS, null);
        wiz.putProperty(PROP_EJB_RESOURCE_FOLDERS, null);
        wiz.putProperty(PROP_EJB_EJBMODULES, null);
        wiz.putProperty(SERVER_ID, null);
        wiz.putProperty(J2EE_LEVEL, null);
        wiz.putProperty(J2EE_SERVER_TYPE, null);
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return NbBundle.getMessage(NewEJBFreeformProjectWizardIterator.class, "TXT_NewEJBFreeformProjectWizardIterator_TitleFormat",
            new Object[] {new Integer (index + 1), new Integer (panels.length)} );
    }
    
    public boolean hasNext() {
        if (!NewJavaFreeformProjectSupport.enableNextButton(current())) {
            return false;
        }
        return index < panels.length - 1;
    }
    public boolean hasPrevious() {
        return index > 0;
    }
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    public void previousPanel() {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    public WizardDescriptor.Panel current () {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}

    
}
