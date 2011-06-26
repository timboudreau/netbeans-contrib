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

package org.netbeans.modules.portalpack.websynergy.portlets.hook.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookType;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookTypeFactory;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookTypeHandler;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;

/**
 *
 * @author satyaranjan
 * @author Santh Chetan Chadalavada
 */
 
public class HookPluginWizardIterator implements WizardDescriptor.InstantiatingIterator{
    private static final long serialVersionUID = 1L;
    private int index;
    private transient WizardDescriptor.Panel[] panels;
    
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private WizardDescriptor wizard;
    
    public Set instantiate() throws IOException {
        Project project = Templates.getProject(wizard);
        HookType hookType = (HookType) wizard.getProperty("hook-type");
        Set result = new HashSet();
        HookTypeHandler eventHandler =
                HookTypeFactory.getHookTypeHandler(project, hookType);

        PortletProjectUtils.addPortletLibraryToProject(project, NetbeanConstants.PORTLET_2_0);
        eventHandler.addHook(PortletProjectUtils.getWebModule(project), wizard, result);
        
        return result;
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        Project project = Templates.getProject(wizard);
        WizardDescriptor.Panel hookDtlPanel = new HookDetailsPanel(project);

        panels = new WizardDescriptor.Panel[]{hookDtlPanel};

        // Creating steps.
        Object prop = wizard.getProperty("WizardPanel_contentData"); // NOI18N

        // Creating steps.
        
        String[] beforeSteps = null;
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        String[] steps = createSteps(beforeSteps, panels);

        for (int i = 0; i < panels.length; i++) {
            JComponent jc = (JComponent) panels[i].getComponent();
            if (steps[i] == null) {
                steps[i] = jc.getName();
            }
            jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N

            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N

        }
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
         return panels[index];
    }

    public String name() {
        return "Hook Config";
        //return NbBundle.getMessage(HookPluginWizardIterator.class, "TITLE_x_of_y", new Integer(index + 1), new Integer(panels.length));
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

    public void addChangeListener(ChangeListener l) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChangeListener(ChangeListener l) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String[] createSteps(String[] before, WizardDescriptor.Panel[] panels) {
        int diff = 0;
        if (before == null) {
            before = new String[0];
        } else if (before.length > 0) {
            diff = ("...".equals(before[before.length - 1])) ? 1 : 0; // NOI18N

        }
        String[] res = new String[(before.length - diff) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (before.length - diff)) {
                res[i] = before[i];
            } else {
                res[i] = panels[i - before.length + diff].getComponent().getName();
            }
        }
        return res;
    }
}
