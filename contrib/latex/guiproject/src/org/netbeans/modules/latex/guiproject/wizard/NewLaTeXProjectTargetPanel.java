/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject.wizard;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;


import javax.swing.event.ChangeListener;


import org.openide.WizardDescriptor;

import org.openide.util.HelpCtx;

/**
 *
 * @author Jan Lahoda
 */
public class NewLaTeXProjectTargetPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel {
    
    private NewLaTeXProjectTargetPanelImpl impl = null;
    private WizardDescriptor wizard;
    
    /** Creates a new instance of NewLaTeXProjectTargetPanel */
    public NewLaTeXProjectTargetPanel(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void addChangeListener(ChangeListener l) {
    }

    public Component getComponent() {
        return getPanelImpl();
    }
    
    private synchronized NewLaTeXProjectTargetPanelImpl getPanelImpl() {
        if (impl == null) {
            impl = new NewLaTeXProjectTargetPanelImpl();
        }
        return impl;
    }

    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid() {
        return true;
    }

    public void readSettings(Object settings) {
        getPanelImpl().load(wizard);
    }

    public void removeChangeListener(ChangeListener l) {
    }

    public void storeSettings(Object settings) {
        getPanelImpl().store(wizard);
    }

    public boolean isFinishPanel() {
        return true;
    }
    
}
