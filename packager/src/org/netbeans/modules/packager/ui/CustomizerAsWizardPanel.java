/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * CustomizerAsWizardPanel.java
 *
 * Created on May 26, 2004, 4:55 PM
 */

package org.netbeans.modules.packager.ui;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.packager.ui.PackagerCustomizer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * Implementation of WizardDescriptor.Panel that proxies a customizer
 * panel.
 *
 * @author  Tim Boudreau
 */
public class CustomizerAsWizardPanel implements WizardDescriptor.Panel {
    private WizardDescriptor wizardDescriptor;
    
    /** Creates a new instance of CustomizerAsWizardPanel */
    public CustomizerAsWizardPanel() {
    }

    private Component comp = null;
    public Component getComponent() {
        if (comp == null) {
            comp = new PackagerCustomizer(this);
        }
        return comp;
    }
    
    private PackagerCustomizer cust() {
        return (PackagerCustomizer) getComponent();
    }
    
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isValid() {
        return cust().valid(wizardDescriptor);
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
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor)settings;        
        cust().read (wizardDescriptor);
        
        // XXX hack, TemplateWizard in final setTemplateImpl() forces new wizard's title
        // this name is used in NewProjectWizard to modify the title
/*        Object substitute = cust().getClientProperty ("NewProjectWizard_Title"); // NOI18N
        if (substitute != null) {
            wizardDescriptor.putProperty ("NewProjectWizard_Title", substitute); // NOI18N
        }
 */
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor)settings;
        cust().store(d);
//        ((WizardDescriptor)d).putProperty ("NewProjectWizard_Title", null); // NOI18N
    }
}
