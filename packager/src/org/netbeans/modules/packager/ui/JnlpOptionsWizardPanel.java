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
 * JnlpOptionsWizardPanel.java
 *
 * Created on May 29, 2004, 10:45 PM
 */

package org.netbeans.modules.packager.ui;

import org.openide.WizardDescriptor;
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
public class JnlpOptionsWizardPanel implements WizardDescriptor.Panel {
    private WizardDescriptor wizardDescriptor = null;
    /** Creates a new instance of JnlpOptionsWizardPanel */
    public JnlpOptionsWizardPanel() {
    }
    
    private JnlpOptionsPanel comp = new JnlpOptionsPanel(this);
    public java.awt.Component getComponent() {
        return comp;
    }
    
    private JnlpOptionsPanel cust() {
        return comp;
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
