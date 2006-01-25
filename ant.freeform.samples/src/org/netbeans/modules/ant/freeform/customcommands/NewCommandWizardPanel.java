/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.ant.freeform.customcommands;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class NewCommandWizardPanel implements WizardDescriptor.Panel {
    
    private NewCommandVisualPanel component;
    private String[] likelyCommandNames;
    
    public NewCommandWizardPanel(String[] likelyCommandNames) {
        this.likelyCommandNames = likelyCommandNames;
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new NewCommandVisualPanel(this, likelyCommandNames);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isValid() {
        return component.getCommand().length() > 0 &&
                component.getDisplayName().length() > 0 &&
                component.getMenu().length() > 0 &&
                component.getPosition() >= 0;
    }
    
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
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
    final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new ArrayList(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings) {}
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        d.putProperty("command", component.getCommand()); // NOI18N
        d.putProperty("displayName", component.getDisplayName() + " {0,choice,0#File|1#\"{1}\"|1<Files}"); // XXX I18N
        d.putProperty("menu", component.getMenu()); // NOI18N
        d.putProperty("position", new Integer(component.getPosition())); // NOI18N
    }
    
}

