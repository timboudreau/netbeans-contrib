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
package org.netbeans.modules.docbook.project.wizard;

import java.awt.Component;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectInfoPanelPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel, ChangeListener {

    public ProjectInfoPanelPanel() {
    }

    private ProjectInfoPanel panel;
    public Component getComponent() {
        boolean wasNull = panel == null;
        Component result = wasNull ? (panel = new ProjectInfoPanel()) : panel;
        if (wasNull) {
            panel.cl = this;
        }
        return result;
    }

    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void readSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        if (panel != null) {
            panel.load (d);
        }
    }

    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        if (panel != null) {
            panel.save (d);
        }
    }

    public boolean isValid() {
        return panel != null && !panel.hasProblem();
    }

    private List listeners = Collections.synchronizedList (new LinkedList());
    public void addChangeListener(ChangeListener l) {
        listeners.add (l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove (l);
    }

    void fire() {
        ChangeListener[] l = (ChangeListener[])
                listeners.toArray (new ChangeListener[0]);
        for (int i=0; i < l.length; i++) {
            l[i].stateChanged(new ChangeEvent (this));
        }
    }

    public void validate() throws WizardValidationException {
        if (panel != null && panel.hasProblem()) {
            throw new WizardValidationException (panel,
                    panel.getProblem(), panel.getProblem());
        }
    }

    public boolean isFinishPanel() {
        return true;
    }

    public void stateChanged(ChangeEvent e) {
        fire();
    }
}
