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

package org.netbeans.modules.portalpack.portlets.ruby;

import java.awt.Component;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.ruby.util.PortletDDHelper;
import org.netbeans.modules.portalpack.portlets.ruby.util.PortletSupportException;
import org.netbeans.modules.portalpack.portlets.ruby.util.PortletSupportImpl;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class PortletDetailsPanel implements WizardDescriptor.Panel,ChangeListener {
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private PortletDetailsPanelGUI component;
    private WizardDescriptor wizardDescriptor;
    private List availablePortlets;
    private Project project;
    public PortletDetailsPanel(WizardDescriptor wizard, List availablePortlets,Project project)
    {
        super();
        this.availablePortlets = availablePortlets;
        this.wizardDescriptor = wizard;
        this.project = project;
    }
    
    public List getAvailablePortlets()
    {
        return availablePortlets;
    }
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new PortletDetailsPanelGUI(this);
        }
        return component;
    }
    
    public Project getProject()
    {
        return project;
    }
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    
    public boolean isValid() {
        getComponent();
        // If it is always OK to press Next or Finish, then:
        return component.valid(wizardDescriptor);
        //return component.valid();
        //return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
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
    
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.readSettings((WizardDescriptor)settings);
        
    }
    public void storeSettings(Object settings) {
        
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }

    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }
    
    public boolean isPhpPortletAlreadyExists()
    {
        PortletSupportImpl impl = new PortletSupportImpl(project);
        File filePortlet = null;
        
        try {
            filePortlet = impl.getPortletDD();
        } catch (PortletSupportException ex) {
            ex.printStackTrace();
            return false;
        }
        PortletApp portletApp = NetbeansUtil.getPortletApp(filePortlet);
        if (portletApp == null) {
            return false;
        }
        
        return PortletDDHelper.isRubyPortletEntryPresent(portletApp);
    }
    
}

