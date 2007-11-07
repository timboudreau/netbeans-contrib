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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.jsr168;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class PortletApplicationWizardPanel extends WebModuleExtender {
    
    private WizardDescriptor wizardDescriptor;
    private PortletApplicationCustomPanel component;
    private JSR168WebFrameworkProvider framework;
    private ExtenderController controller;
    private boolean customizer;
    private WebModule wm;
    
    /** Creates a new instance of templateWizardPanel */
    public PortletApplicationWizardPanel(JSR168WebFrameworkProvider framework, WebModule wm, ExtenderController controller, boolean customizer) {
        this.framework = framework;
        this.controller = controller;
        this.customizer = customizer;
        this.wm = wm;
    }
    
    public JComponent getComponent() {
        if(customizer) return new JPanel();
        if (component == null) {
            component = new PortletApplicationCustomPanel(this,wm);
            component.setName(NbBundle.getMessage(PortletApplicationWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(PortletApplicationWizardPanel.class);
    }
    
    public boolean isValid() {
        if(customizer) return true;
        getComponent();
        return component.valid();
    }
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);
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
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    public boolean getCustomizer()
    {
        return customizer;
    }
    public Map getData()
    {
        getComponent();
        return component.getData();
    }


    @Override
    public void update() {
        if(customizer) return;
        getComponent();
        component.update();
    }

    @Override
    public Set<FileObject> extend(WebModule webModule) {
        return framework.extendImpl(webModule);
    }
    
    public ExtenderController getController() {
        return controller;
    }
}
