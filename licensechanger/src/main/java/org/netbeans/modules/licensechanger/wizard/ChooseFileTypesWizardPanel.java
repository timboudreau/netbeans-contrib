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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.licensechanger.wizard.utils.WizardProperties;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

/**
 *
 * @author Nils Hoffmann
 */
public class ChooseFileTypesWizardPanel implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ChooseFileTypesPanel component;
    private WizardDescriptor wiz;
    private boolean valid = true;
    private ChangeSupport cs = new ChangeSupport(this);

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public ChooseFileTypesPanel getComponent() {
        if (component == null) {
            component = new ChooseFileTypesPanel();
            component.addPropertyChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        cs.fireChange();
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        Boolean storeInUserProperties = (Boolean) wiz.getProperty(WizardProperties.KEY_STORE_IN_USER_PROPERTIES);
        ChooseFileTypesPanel panel = getComponent();
        if (storeInUserProperties != null) {
            panel.setStoreInUserProperties(storeInUserProperties);
        }
        String copyrightHolder = (String) wiz.getProperty(WizardProperties.KEY_COPYRIGHT_HOLDER);
        if (copyrightHolder == null) {
            copyrightHolder = NbPreferences.forModule(ChooseFileTypesWizardPanel.class).get(WizardProperties.KEY_COPYRIGHT_HOLDER, "");
        }
        panel.setCopyrightHolder(copyrightHolder);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(WizardProperties.KEY_FILE_HANDLERS, getComponent().getFileHandlers());
        String copyrightHolder = getComponent().getCopyrightHolder();
        if (copyrightHolder != null && !copyrightHolder.isEmpty()) {
            wiz.putProperty(WizardProperties.KEY_COPYRIGHT_HOLDER, copyrightHolder);
            NbPreferences.forModule(ChooseFileTypesWizardPanel.class).put(WizardProperties.KEY_COPYRIGHT_HOLDER, copyrightHolder);
        }
        wiz.putProperty(WizardProperties.KEY_STORE_IN_USER_PROPERTIES, Boolean.valueOf(getComponent().isStoreInUserProperties()));
    }

    @Override
    public void validate() throws WizardValidationException {
        valid = true;
    }
}
