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
 * MacOptionsWizardPanel.java
 *
 * Created on May 29, 2004, 10:44 PM
 */

package org.netbeans.modules.packager.ui;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author  Tim Boudreau
 */
public class MacOptionsWizardPanel implements WizardDescriptor.Panel {
    private WizardDescriptor wizardDescriptor = null;

    /** Creates a new instance of MacOptionsWizardPanel */
    public MacOptionsWizardPanel() {
    }
    
    private Component comp = null;
    public Component getComponent() {
        if (comp == null) {
            comp = new MacOptionsPanel(this);
        }
        return comp;
    }
    
    private MacOptionsPanel cust() {
        return (MacOptionsPanel) getComponent();
    }
    
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isValid() {
        return cust().valid(wizardDescriptor);
    }
    
    public synchronized void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor)settings;        
        cust().read (wizardDescriptor);
    }
    
    public synchronized void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor)settings;
        cust().store(d);
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
    protected final void fire() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
}
