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

import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.ada.project.AdaMimeResolver;
import org.netbeans.modules.ada.project.AdaProject;
import org.netbeans.modules.ada.project.ui.Utils;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 *
 * @author Andrea Lucarelli
 */
public final class NewFileWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel<WizardDescriptor>[] wizardPanels;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel<WizardDescriptor>[] getPanels() {
        Project p = Templates.getProject(wizard);
        final SourceGroup[] groups = Utils.getSourceGroups(p);
        WizardDescriptor.Panel<WizardDescriptor> simpleTargetChooserPanel = Templates.createSimpleTargetChooser(p, groups);
        FileObject template = Templates.getTemplate(wizard);

        return new WizardDescriptor.Panel[]{
                    simpleTargetChooserPanel
                };
    }

    public Set instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        FileObject template = Templates.getTemplate(wizard);

        Map<String, Object> wizardProps = new HashMap<String, Object>();

        DataFolder dataFolder = DataFolder.findFolder(dir);
        DataObject dataTemplate = DataObject.find(template);
        String fname = Templates.getTargetName(wizard);
        String ext = FileUtil.getExtension(fname);

        FileObject foo = FileUtil.createData(FileUtil.createMemoryFileSystem().getRoot(), fname);
        if (foo == null || !AdaMimeResolver.MIME_TYPE.equals(FileUtil.getMIMEType(foo))) {
            if (ext == null || ext.length() == 0) {
                // TODO: get naming by options
                Templates.setTargetName(this.wizard, fname); //NOI18N
                fname = Templates.getTargetName(wizard);
                ext = FileUtil.getExtension(fname);
            }
        }

        // Remove the extension on File Name text field
        if (ext != null && ext.length() > 0) {
            wizardProps.put("name", fname.substring(0, fname.length() - ext.length() - 1));//NOI18N
        }

        DataObject createdFile = dataTemplate.createFromTemplate(dataFolder, Templates.getTargetName(wizard), wizardProps);

        return Collections.<FileObject>singleton(createdFile.getPrimaryFile());
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        FileObject targetFolder = Templates.getTargetFolder(wizard);
        if (targetFolder == null) {
            Project project = Templates.getProject(wizard);
            assert project instanceof AdaProject;
            AdaProject adaProject = (AdaProject) project;
            // XXX - sources directory is remembered in project
            FileObject srcDir = getFileObject(adaProject.getHelper(), AdaProjectProperties.SRC_DIR);
            if (srcDir != null) {
                targetFolder = srcDir;
                Templates.setTargetFolder(wizard, srcDir);
            }
        }
        FileObject template = Templates.getTemplate(this.wizard);
        String targetName = (targetFolder != null) ? FileUtil.findFreeFileName(targetFolder, template.getName(), template.getExt()) : template.getName();//NOI18N
        // TODO: get naming by options
        Templates.setTargetName(this.wizard, targetName);//NOI18N
        wizardPanels = getPanels();

        // Make sure list of steps is accurate.
        String[] beforeSteps = (String[]) wizard.getProperty(WizardDescriptor.PROP_CONTENT_DATA);
        int beforeStepLength = beforeSteps.length - 1;
        String[] steps = createSteps(beforeSteps);
        for (int i = 0; i < wizardPanels.length; i++) {
            Component c = wizardPanels[i].getComponent();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i + beforeStepLength - 1)); // NOI18N
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps); // NOI18N
            }
        }
    }

    private String[] createSteps(String[] beforeSteps) {
        int beforeStepLength = beforeSteps.length - 1;
        String[] res = new String[beforeStepLength + wizardPanels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeStepLength)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = wizardPanels[i - beforeStepLength].getComponent().getName();
            }
        }
        return res;
    }

    public void uninitialize(WizardDescriptor wizard) {
        wizardPanels = null;
    }

    public WizardDescriptor.Panel current() {
        return wizardPanels[index];
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


    private FileObject getFileObject(AntProjectHelper helper, String propname) {
        String prop = helper.getStandardPropertyEvaluator().getProperty(propname);
        if (prop != null) {
            return helper.resolveFileObject(prop);
        }
        return null;
    }
}
