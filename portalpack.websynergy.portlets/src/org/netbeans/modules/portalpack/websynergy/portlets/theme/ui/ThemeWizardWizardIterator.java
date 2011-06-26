/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.theme.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.netbeans.modules.portalpack.servers.websynergy.common.WebSpacePropertiesUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle;

public final class ThemeWizardWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            Project project = null;
            if(wizard != null) {
                project = Templates.getProject(wizard);
            }
            panels = new WizardDescriptor.Panel[]{
                        new ThemeWizardWizardPanel(project)
                    };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    public Set instantiate() throws IOException {
        Project project = Templates.getProject(wizard);
        Set result = new HashSet();

        ThemeDetailsHandler tdHandler = new ThemeDetailsHandler(project, wizard);
        String themeFolder = tdHandler.createThemeConfigurationFiles(result);
        
        createFiles(project, wizard, result, themeFolder);

        return result;
    }


    public void createFiles(Project project, WizardDescriptor desc, Set result, String themeFolder) {
        final ProgressHandle handle =
                ProgressHandleFactory.createHandle(NbBundle.getMessage(ThemeWizardWizardIterator.class, "THEME_PROGRESS_MESSAGE")); // NOI18N
        handle.start();

        try {

            FileObject webInf = PortletProjectUtils.getWebInf(project);

            PSConfigObject psConfig =
                    WebSpacePropertiesUtil.getSelectedServerProperties(project);

            String classicThemePath = null;
            if(psConfig != null) {
                classicThemePath = psConfig.getProperty(LiferayConstants.LR_PORTAL_DEPLOY_DIR) +
                        File.separatorChar + "html" +
                        File.separatorChar + "themes" +
                        File.separatorChar + "classic";
            }

            HashMap selectedDirectoriesMap = new HashMap();
            FileObject classicTheme = FileUtil.toFileObject(new File(classicThemePath));
            FileObject[] children = classicTheme.getChildren();

            for(FileObject child:children) {
                  if(child != null &&
                          child.getName().equals("_diffs")) {
                      continue;
                  }
                  String path = FileUtil.getRelativePath(classicTheme, child);
                  selectedDirectoriesMap.put(path,child);
            }


            WebModule wm = PortletProjectUtils.getWebModule(project);

            FileObject themeFolderFO = null;
            if(themeFolder == null || themeFolder.trim().length() == 0) {
                themeFolderFO = wm.getDocumentBase();
            } else {

                FileObject docBaseFO = wm.getDocumentBase();
                themeFolderFO = docBaseFO.getFileObject(themeFolder);
                if(themeFolderFO == null) {
                    docBaseFO.createFolder(themeFolder);
                    themeFolderFO = docBaseFO.getFileObject(themeFolder);
                }
            }
            final CreateFilesAndFolders createFilesAndFolders = new CreateFilesAndFolders(
                    wm, selectedDirectoriesMap,
                    themeFolderFO);
            if (webInf != null) {
                final FileSystem fs = webInf.getFileSystem();
                RequestProcessor.getDefault().post(new Runnable(){

                    public void run() {

                        try {
                            fs.runAtomicAction(createFilesAndFolders);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } finally {
                            handle.finish();
                        }
                    }
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
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

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }

    private class CreateFilesAndFolders implements FileSystem.AtomicAction {
        private WebModule webModule;
        private HashMap selectedDirsMap;
        private FileObject themeFolder;
        
        public CreateFilesAndFolders(WebModule webModule,
                HashMap selectedDirsMap, FileObject themeFolder) {

            this.webModule = webModule;
            this.selectedDirsMap = selectedDirsMap;
            this.themeFolder = themeFolder;
        }

        public void run(){
            FileObject fo = null;
            String path = null;
            for (Object key : selectedDirsMap.keySet()) {
                path = (String) key;
                fo = (FileObject) selectedDirsMap.get(path);
                File jspFolderFile = new File(FileUtil.toFile(themeFolder),path);
                jspFolderFile.mkdirs();
                
                try {
                    copyDirectory(fo, themeFolder);
                } catch (IOException ex) {
                }
            }

			try{
				FileUtil.refreshFor(FileUtil.toFile(themeFolder));
			}catch(Exception e) {

			}
        }

        public void copyDirectory(FileObject sourceLocation , FileObject destParent/*, String file*/)
        throws IOException {

            if (sourceLocation.isFolder()) {

                FileObject destFO = destParent.getFileObject(sourceLocation.getName());
                if (destFO == null) {
                    try{
                        destFO = destParent.createFolder(sourceLocation.getName());
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                FileObject[] children = sourceLocation.getChildren();
                for (int i=0; i<children.length; i++) {
                    copyDirectory(children[i],destFO);
                }
            } else {
                try {
                    FileUtil.copyFile(sourceLocation, destParent,sourceLocation.getName());
                }catch(Exception e) {
                }
            }
        }
    }
}
