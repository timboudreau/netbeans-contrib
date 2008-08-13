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

package org.netbeans.modules.portalpack.servers.core.ui;

import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Satya
 */
public class InstallPanel implements WizardDescriptor.Panel,WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel,ChangeListener{
    
    public ConfigPanel component;
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private WizardDescriptor wizardDescriptor;
    private String className;
    private boolean isFinishPanel = false;
    
    public InstallPanel(String className) {
        this.className = className;
        
    }
    
    public InstallPanel(String className,boolean isFinishPanel) {
        this.className = className;
        this.isFinishPanel = isFinishPanel;
        
    }
    public InstallPanel(ConfigPanel component)
    {
        this.component = component;
        component.addChangeListener(this);
    }
    public InstallPanel(ConfigPanel component,boolean isFinishPanel)
    {
        this.component = component;
        this.isFinishPanel = isFinishPanel;
        component.addChangeListener(this);
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
    public final void fireChangeEvent() {
        logger.fine("Inside fireChangeEvent of Install Panel..................&&&&&&&&&&&&&&");
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings) {
        getComponent();
        wizardDescriptor = (WizardDescriptor) settings;
        component.readSettings(wizardDescriptor);
    }
    
    public void storeSettings(Object settings) {
        getComponent();
        wizardDescriptor = (WizardDescriptor) settings;
        component.store(wizardDescriptor);
    }
    
    public boolean isFinishPanel() {
        return isFinishPanel;
    }
    
    public void validate() throws WizardValidationException {
        getComponent();
        if(!component.validate(wizardDescriptor))
        {
            Object ob = ((WizardDescriptor)wizardDescriptor).getProperty("WizardPanel_errorMessage");
            String errMsg = null;
            if(ob == null)
                errMsg = "";
            else
                errMsg = ob.toString();               
            throw new WizardValidationException(component,errMsg,errMsg);                                              
        }
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(InstallPanel.class);
    }
    
    public Component getComponent() {
        if(component == null)
            component = createConfigPanel();
        if(component instanceof Component)
            return (Component)component;
        return null;        
    }
    
    public boolean isValid() {
        try{
            validate();          
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    public ConfigPanel createConfigPanel() {
        
        if(component != null)
            return component;
        
        if(className == null)
            return null;
        Class clazz;
        try {
            clazz = Class.forName(className);
            Object obj;
            
            obj = clazz.newInstance();
            
            if(obj instanceof ConfigPanel) {
               
                ((ConfigPanel)obj).addChangeListener(this);
                return (ConfigPanel) obj;
            } else {
                return null;
            }
        }catch(Exception e){
            logger.log(Level.SEVERE,"error initializing panel",e);
            return null;
        }
    }

    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }
}
