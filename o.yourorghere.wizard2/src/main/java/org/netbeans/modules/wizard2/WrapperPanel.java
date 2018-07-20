/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.wizard2;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.netbeans.spi.wizard.*;
import org.netbeans.modules.wizard.*;
import org.openide.WizardValidationException;

/**
 *
 * @author Tim Boudreau
 */
class WrapperPanel implements WizardDescriptor.Panel, WizardObserver, WizardDescriptor.FinishablePanel{
    static final class AsynchValidatingWrapperPanel extends WrapperPanel implements WizardDescriptor.AsynchronousValidatingPanel {
        public AsynchValidatingWrapperPanel(Wizard wizard, String id, Map settings) {
            super (wizard, id, settings);
        }
    }
    
    static final class ValidatingWrapperPanel extends WrapperPanel implements WizardDescriptor.ValidatingPanel {
        public ValidatingWrapperPanel(Wizard wizard, String id, Map settings) {
            super (wizard, id, settings);
        }
    }
    private final Wizard wizard;
    private final String id;
    JComponent comp;
    final Map settings;
    /** Creates a new instance of WrapperPanel */
    public WrapperPanel(Wizard wizard, String id, Map settings) {
        this.wizard = wizard;
        this.id = id;
        this.settings = settings;
        assert wizard != null;
        assert settings != null;
        if (id == null) {
            throw new NullPointerException();
        }
        if (Wizard.UNDETERMINED_STEP.equals(id)) {
            throw new IllegalArgumentException ("Creating a panel for an " +
                    "undetermined step is not possible");
        }
        System.err.println("MY ID IS " + id);
        wizard.addWizardObserver(this);
    }
    
    private int stepNumber() {
        return Arrays.asList (wizard.getAllSteps()).indexOf(id);
    }
    
    public Component getComponent() {
        if (comp == null) {
            comp = wizard.navigatingTo(id, settings);
            comp.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
            comp.putClientProperty("WizardPanel_contentSelectedIndex", 
                    new Integer(stepNumber() - 1));
            
            comp.putClientProperty("WizardPanel_contentData", 
                    WrapperIterator.createSteps(wizard));
            comp.setName (wizard.getStepDescription(id));
        }
        return comp;
    }
    
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP; //XXX get from FileObject?
    }
    
    public void readSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
    }
    
    void backInto (MergeMap settings, Map initialSettings) {
        settings.popAndCalve();
        if (!id.equals(wizard.getCurrentStep())) {
            wizard.navigatingTo (id, initialSettings);
        }
    }
    
    void forwardInto (MergeMap settings, Map initialSettings) {
        settings.push(id);
        if (!id.equals(wizard.getCurrentStep())) {
            wizard.navigatingTo (id, initialSettings);
        }
    }
    
    public boolean isValid() {
        if (wizard.isBusy()) {
            return false;
        }
        boolean result = wizard.getProblem() == null;
        if (result) {
            int nav = wizard.getForwardNavigationMode();
            result = nav == Wizard.MODE_CAN_CONTINUE ||
                nav == Wizard.MODE_CAN_CONTINUE_OR_FINISH ||
                nav == Wizard.MODE_CAN_FINISH;
        }
        return result;
    }
    
    private boolean firing = false;
    private void fire() {
        if (firing == true) return;
        firing = true;
        try {
            ChangeListener[] l = listeners.toArray(new ChangeListener[0]);
            for (int i = 0; i < l.length; i++) {
                l[i].stateChanged(new ChangeEvent(this));
            }
        } finally {
            firing = false;
        }
    }
    
    private final List <ChangeListener> listeners = 
            Collections.<ChangeListener>synchronizedList (new LinkedList <ChangeListener>());
    public void addChangeListener(ChangeListener l) {
        listeners.add (l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove (l);
    }
    
    public void stepsChanged(Wizard wizard) {
        fire();
    }
    
    public void navigabilityChanged(Wizard wizard) {
        fire();
    }

    public void selectionChanged(Wizard wizard) {
        fire();
    }

    public void validate() throws WizardValidationException {
        String problem = wizard.getProblem();
        if (problem != null) {
            if (comp != null && comp instanceof JComponent) {
                throw new WizardValidationException (comp, problem, problem);
            }
        }
    }

    public boolean isFinishPanel() {
        int nav = wizard.getForwardNavigationMode();
        return nav ==
                Wizard.MODE_CAN_CONTINUE_OR_FINISH || 
                nav == Wizard.MODE_CAN_FINISH;
    }
    
    public void prepareValidation() {
        //XXX pending implementation in wizard
    }    
}
