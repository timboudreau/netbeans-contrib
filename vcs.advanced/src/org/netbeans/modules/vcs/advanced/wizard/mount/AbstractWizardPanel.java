/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.wizard.mount;

import org.openide.loaders.TemplateWizard;

/**
 *
 * @author  Tomas Zezula
 */
public abstract class AbstractWizardPanel extends javax.swing.JPanel implements org.openide.WizardDescriptor.FinishPanel {

    private java.util.ArrayList listeners_;
    
    /** Creates new AbstractWizardPanel */
    public AbstractWizardPanel() {
        this.listeners_ = new java.util.ArrayList ();
    }
    
    
    public void addNotify() {
        super.addNotify();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                java.awt.Container c = getTopLevelAncestor();
                if (c instanceof java.awt.Window) {
                    ((java.awt.Window) c).pack();
                }
            }
        });
    }
    
    public synchronized void addChangeListener(javax.swing.event.ChangeListener listener) {
        this.listeners_.add (listener);
    }

    public synchronized void removeChangeListener (javax.swing.event.ChangeListener listener) {
        this.listeners_.remove (listener);
    }
    
    public void readSettings(java.lang.Object data) {
        TemplateWizard wizard = (TemplateWizard) data;
        MountWizardIterator wizIter;
        wizIter = (MountWizardIterator) wizard.getIterator(wizard.getTemplate());
        MountWizardData mountData = wizIter.getData();
        wizard.putProperty ("WizardPanel_contentData", wizIter.names);
        readWizardSettings (mountData);
    }
    
    public void storeSettings(java.lang.Object data) {
        TemplateWizard wizard = (TemplateWizard) data;
        MountWizardIterator wizIter;
        wizIter = (MountWizardIterator) wizard.getIterator(wizard.getTemplate());
        MountWizardData mountData = wizIter.getData();
        storeWizardSettings (mountData);
    }
    
    public java.awt.Component getComponent() {
        return this;
    }    
    
    protected abstract void storeWizardSettings (MountWizardData data);
    
    protected abstract void readWizardSettings (MountWizardData data);
    
    protected void fireChange () {
        javax.swing.event.ChangeEvent event = new javax.swing.event.ChangeEvent (this);
        java.util.Iterator iterator;
        synchronized (this) {
            iterator = ((java.util.ArrayList)listeners_.clone()).iterator();
        }
        while (iterator.hasNext ()) {
            ((javax.swing.event.ChangeListener)iterator.next()).stateChanged (event);
        }
    }
}
