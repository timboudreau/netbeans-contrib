/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Simple Edit Module.
 * The Initial Developer of the Original Code is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
 * 
 * Contributor(s): David Strupl.
 */
package cz.solutions.simpleedit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * @author David Strupl
 */
final class ClosingTabsOptionsPanelController extends OptionsPanelController {
    
    private ClosingTabsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    
    public void update() {
        getPanel().load();
        changed = false;
    }
    
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }
    
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }
    
    public boolean isValid() {
        return getPanel().valid();
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    private ClosingTabsPanel getPanel() {
        if (panel == null) {
            panel = new ClosingTabsPanel(this);
        }
        return panel;
    }
    
    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
    
}
