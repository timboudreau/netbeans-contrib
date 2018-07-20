/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.cms;

import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;

public class CMSFrameworkWizardPanel extends WebModuleExtender{
    private WizardDescriptor wizardDescriptor;
    private CMSFrameworkProvider framework;
    private WebModule module;
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CMSFrameworkVisualPanel component;
    
    public CMSFrameworkWizardPanel(CMSFrameworkProvider framework,WebModule module,
                                                        ExtenderController controller){
       this.framework = framework;
       this.module = module;
    }
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = new CMSFrameworkVisualPanel();
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    
    public boolean isValid() {
        return true;
    }
    
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
   
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
            wizardDescriptor = (WizardDescriptor) settings;
         
    }
    public void storeSettings(Object settings) {
           ((WizardDescriptor) settings).putProperty(CMSFrameworkVisualPanel.SELECTED_VALUE, getSelectedValueFromVisualPanel());
    }
    
    public void enableComponents(boolean enable) {
      //this.enableComponents(enable);
    }
     public  String getSelectedValueFromVisualPanel() {
         return ((CMSFrameworkVisualPanel) component).getSelectedValue();
     }


    @Override
    public void update() {
        getComponent();
        
    }

    @Override
    public Set<FileObject> extend(WebModule webModule) {
        return framework.extendImpl(webModule);
    }
   
    
}

