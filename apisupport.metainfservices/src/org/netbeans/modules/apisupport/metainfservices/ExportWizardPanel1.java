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
package org.netbeans.modules.apisupport.metainfservices;

import java.awt.Component;
import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.filesystems.FileObject;

public class ExportWizardPanel1 implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ExportVisualPanel1 component;

    private boolean valid;
    private FileObject target;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new ExportVisualPanel1(this);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    final FileObject getTarget() {
        return target;
    }
    
    public boolean isValid() {
        return valid;
    }

    final void setValid(boolean b) {
        this.valid = b;
        fireChangeEvent();
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(11);
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
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void readSettings(Object settings) {
        WizardDescriptor wd = (WizardDescriptor)settings;
        String impl = (String)wd.getProperty("implName"); // NOI18N
        Collection<String> interfaces = (Collection<String>)wd.getProperty("interfaceNames"); // NOI18N
        target = (FileObject)wd.getProperty("target"); // NOI18N

        if (component != null) {
            component.fillTable(interfaces);
        }
    }
    public void storeSettings(Object settings) {
        WizardDescriptor wd = (WizardDescriptor)settings;
        wd.putProperty("files", component.generatedFiles());
    }
    
}

