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
        storeSettings(wizardDescriptor);
        
        System.err.println("Customizer as wizard panel firing");
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
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor)settings;
        cust().store(d);
    }
}
