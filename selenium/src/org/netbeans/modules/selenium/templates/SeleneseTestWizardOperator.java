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
package org.netbeans.modules.selenium.templates;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.selenium.SeleniumSupport;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Jindrich Sedek
 */
public class SeleneseTestWizardOperator implements WizardDescriptor.InstantiatingIterator {

    private ChangeSupport changeSupport = new ChangeSupport(this);
    private transient WizardDescriptor.Panel panel;
    private transient WizardDescriptor wiz;


    public static WizardDescriptor.InstantiatingIterator create(){
        return new SeleneseTestWizardOperator();
    }

    public Set<FileObject> instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wiz);
        String targetName = Templates.getTargetName(wiz);

        DataFolder df = DataFolder.findFolder(dir);
        FileObject template = Templates.getTemplate(wiz);

        FileObject createdFile = null;
        DataObject dTemplate = DataObject.find(template);
        DataObject dobj = dTemplate.createFromTemplate(df, targetName);
        createdFile = dobj.getPrimaryFile();

        return Collections.singleton(createdFile);
    }

    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        Project proj = Templates.getProject(wiz);
        SeleniumSupport.getSelenimDir(proj);
        panel = createPanel(wiz);
        panel.getComponent();
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz = null;
        panel = null;
    }

    public String name() {
        return NbBundle.getMessage(SeleneseTestWizardOperator.class, "SELENESE_TEMPLATE_WIZARD_TITLE");
    }

    public boolean hasNext() {
        return false;
    }

    public boolean hasPrevious() {
        return false;
    }

    public void nextPanel() {
          assert(false);
    }

    public void previousPanel() {
          assert(false);
    }

    public WizardDescriptor.Panel current() {
        return panel;
    }

    private WizardDescriptor.Panel createPanel(WizardDescriptor wizardDescriptor) {
        // Ask for Java folders
        Project project = Templates.getProject(wizardDescriptor);
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        assert groups != null : "Cannot return null from Sources.getSourceGroups: " + sources;
        if (groups.length == 0) {
            groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
            return Templates.createSimpleTargetChooser(project, groups);
        } else {
            FileObject seleniumDir = SeleniumSupport.getSelenimDir(project);
            for (SourceGroup selGroup : groups) {
                if (selGroup.getRootFolder().equals(seleniumDir)){
                    return JavaTemplates.createPackageChooser(project, new SourceGroup[]{selGroup});
                }
            }
            return JavaTemplates.createPackageChooser(project, groups);
        }

    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
}
