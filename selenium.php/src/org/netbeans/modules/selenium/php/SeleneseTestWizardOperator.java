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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium.php;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
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

//      TODO server ULR & server port
//        Object serverPort = null;
//        if (serverPort == null){
//            serverPort = DEFAULT_SERVER_PORT;
//        }
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("server_port", serverPort);   //NOI18N

        DataObject dobj = dTemplate.createFromTemplate(df, targetName, params);
        createdFile = dobj.getPrimaryFile();

        return Collections.singleton(createdFile);
    }

    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        SeleniumPHPSupport.getSeleniumDir(Templates.getProject(wiz), true);
        panel = createPanel(wiz);
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
        Project project = Templates.getProject(wizardDescriptor);
        SourceGroup seleniumSourceGroup = SeleniumPHPSupport.getSeleniumSourceGroup(project);
        return Templates.createSimpleTargetChooser(project, new SourceGroup[]{seleniumSourceGroup});
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
}
