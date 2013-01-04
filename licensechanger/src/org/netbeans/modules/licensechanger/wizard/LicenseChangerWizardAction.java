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
package org.netbeans.modules.licensechanger.wizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import org.netbeans.api.project.*;
import org.netbeans.modules.licensechanger.wizard.utils.LicenseChangerRunnable;
import org.netbeans.modules.licensechanger.wizard.utils.WizardProperties;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.*;

/**
 * Context-sensitive action to launch the license header change wizard.
 *
 * @author Nils Hoffmann
 */
@ActionID(
    category = "Tools",
id = "org.netbeans.modules.licensechanger.wizard.LicenseChangerWizardAction")
@ActionRegistration(
    displayName = "#CTL_LicenseChangerWizardAction")
@ActionReferences(
//    @ActionReference(path = "Loaders/folder/any/Actions", position = 951)
@ActionReference(path = "UI/ToolActions/Files", position = 951))
@NbBundle.Messages("CTL_LicenseChangerWizardAction=Change License Header")
public final class LicenseChangerWizardAction implements ActionListener {

    private List<DataObject> context;

    public LicenseChangerWizardAction(List<DataObject> context) {
        this.context = context;
    }

    /**
     * Used to return the parent folder of a file or the folder itself.
     *
     * @param fo
     * @return
     */
    private FileObject addFileObject(FileObject fo) {
        if (fo.isFolder()) {
            return fo;
        }
        return fo.getParent();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Set<FileObject> files = new HashSet<FileObject>();
        Project owningProject = null;
        for (DataObject ob : context) {
            FileObject fo = ob.getPrimaryFile();
            Project proj = FileOwnerQuery.getOwner(fo);
            if (owningProject == null) {
                owningProject = proj;
                //TODO implement better exclusion of build directories 
                files.add(addFileObject(fo));
            } else {
                if (owningProject.equals(proj)) {
                    files.add(addFileObject(fo));
                } else {
                    Exceptions.printStackTrace(new IllegalStateException("Can only handle folders below one project!"));
                    return;
                }
            }
        }

        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new ChooseFileTypesWizardPanel());
        panels.add(new LicenseChooserWizardPanel());
        panels.add(new SelectFoldersWizardPanel());
        panels.add(new PreviewWizardPanel());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Change License Headers");
        wiz.putProperty(WizardProperties.KEY_ROOT_FILES, files);
        wiz.putProperty(WizardProperties.KEY_PROJECT, owningProject);
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            RequestProcessor.getDefault().post(new LicenseChangerRunnable(wiz));
        }
    }
}
