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

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.portalpack.servers.websynergy.common.WebSpacePropertiesUtil;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookType;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookTypeFactory;
import org.netbeans.modules.portalpack.websynergy.portlets.hook.api.HookTypeHandler;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Santh Chetan Chadalavada
 */

public class HookDetailsPanel implements WizardDescriptor.Panel, ChangeListener {

    private HookDetailsPanelGUI component;
    private WizardDescriptor wizard;
    private Project project;
    
    public HookDetailsPanel() {
        
    }
    public HookDetailsPanel(Project project) {
        this.project = project;
    }
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    //private Component component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new HookDetailsPanelGUI();
            registerHandlers();
            component.addChangeListener(this);
        }
        return component;
    }
    
    private void registerHandlers() {
      
        HookType eventType = new HookType(HookType.EVENT_HANDLER_HOOK);
        HookTypeHandler eventHandler =
                HookTypeFactory.getHookTypeHandler(project, eventType);
        component.registerHandlers(eventType, eventHandler);
        
        HookType modelType = new HookType(HookType.MODEL_LISTENERS_HOOK);
        HookTypeHandler modelHandler =
                HookTypeFactory.getHookTypeHandler(project, modelType);
        component.registerHandlers(modelType, modelHandler);
        
        HookType ppType = new HookType(HookType.PORTAL_PROPERTIES_HOOK);
        HookTypeHandler ppHandler =
                HookTypeFactory.getHookTypeHandler(project, ppType);
        component.registerHandlers(ppType, ppHandler);
        
        HookType jspType = new HookType(HookType.JSP_HOOK);
        HookTypeHandler jspHandler =
                HookTypeFactory.getHookTypeHandler(project, jspType);
        component.registerHandlers(jspType, jspHandler);
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        if(!WebSpacePropertiesUtil.isWebSynergyServer(project)) {
            if(wizard != null) {
                wizard.putProperty("WizardPanel_errorMessage",
                            NbBundle.getMessage(
                            HookDetailsPanel.class, "NOT_ALLOWED_FOR_NON_WEBSYNERGY_RUNTIME")); // NOI18N
                return false;
            }
        } else {
            String prjName = ProjectUtils.getInformation(project).getName();
            if(prjName != null && !prjName.endsWith("-hook")) {
                   if(wizard != null) {
                        wizard.putProperty("WizardPanel_errorMessage",
                                NbBundle.getMessage(
                                HookDetailsPanel.class, "HOOK_NOT_ALLOWED_WHEN_APP_NAME_NOT_ENDS_WITH_HOOK")); // NOI18N
                        return false;
                   }
            }
            
        }
        // If it is always OK to press Next or Finish, then:
        return component.valid(wizard);
    // If it depends on some condition (form filled out...), then:
    // return someCondition();
    // and when this condition changes (last form field filled in...) then:
    // fireChangeEvent();
    // and uncomment the complicated stuff below.
    }

    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

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
     

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        wizard = (WizardDescriptor) settings;
        component.readSettings(wizard);
    }

    public void storeSettings(Object settings) {
         component.storeSettings((WizardDescriptor)settings);
    }

    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }
}

