/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * BranchingWizard.java
 *
 * Created on March 4, 2005, 10:56 PM
 */

package org.netbeans.spi.wizard;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import org.netbeans.spi.wizard.Wizard.WizardListener;

/**
 * A Wizard with indeterminate branches.  The actual branch decision-making
 * is done by the WizardBranchController passed to the constructor.
 * <p>
 * Wizards with arbitrary numbers of branches can be handled by a 
 * WizardBranchController by returning wizards created by 
 * another WizardBranchController's <code>createWizard()</code> method.
 * <p>
 * One important point: There should be no duplicate IDs between steps of 
 * this wizard.
 *
 * @author Tim Boudreau
 */
final class BranchingWizard implements Wizard {
    final Wizard base;
    private Wizard secondary;
    private Wizard curr;
    private final WizardBranchController iter;
    private WL wl = null;
    
    public BranchingWizard (WizardBranchController iterator) {
        this.iter = iterator;
        base = new SimpleWizard (iterator.getBase());
        setCurrent (base);
    }
    
    private SimpleWizardInfo lastInfo = null;
    
    protected final Wizard createSecondary (Map settings) {
        return iter.getWizardForStep(currStep, settings);
    }
    
    private void checkForSecondary() {
        if (settings == null) {
            return;
        }
        Wizard newSecondary = createSecondary (settings);
        if (((secondary == null) != (newSecondary == null)) || (secondary != null && !secondary.equals (newSecondary))) {
            secondary = newSecondary;
            fireStepsChanged();
        }
    }
    
    private void setCurrent (Wizard curr) {
        if (this.curr == curr) {
            return;
        }
        if (curr == null) {
            throw new NullPointerException ("Can't set current wizard to null");
        }
        if (this.curr != null) {
            this.curr.removeWizardListener (wl);
        }
        this.curr = curr;
        if (wl == null) {
            wl = new WL();
        }
        curr.addWizardListener (wl);
    }

    public final boolean canFinish() {
        return curr != base && curr.canFinish();
    }

    public final Object finish(Map settings) throws WizardException {
        try {
            Object result = curr.finish (settings);
            base.removeWizardListener (wl);
            secondary.removeWizardListener (wl);
            return result;
        } catch (WizardException we) {
            if (we.getStepToReturnTo() != null) {
                base.addWizardListener (wl);
                secondary.addWizardListener (wl);
            }
            throw we;
        }
    }

    public final String[] getAllSteps() {
        String[] result;
        if (secondary == null) {
            String[] bsteps = base.getAllSteps();
            result = new String[bsteps.length + 1];
            System.arraycopy (bsteps, 0, result, 0, bsteps.length);
            result[result.length-1] = UNDETERMINED_STEP;
        } else {
            String[] bsteps = base.getAllSteps();
            String[] csteps = secondary.getAllSteps();
            result = new String[bsteps.length + csteps.length];
            System.arraycopy (bsteps, 0, result, 0, bsteps.length);
            System.arraycopy (csteps, 0, result, bsteps.length,  csteps.length);
        }
        return result;
    }

    public final String getNextStep() {
        String result;
        if (currStep == null) {
            result = getAllSteps() [0];
        } else {
            String[] steps = getAllSteps();
            int idx = Arrays.asList(steps).indexOf(currStep);
            if (idx == -1) {
                throw new IllegalStateException ("Current step not in" +
                        " available steps:  " + currStep + " not in " +
                        Arrays.asList(steps));
            } else {
                if (idx == steps.length - 1) {
                    if (secondary == null) {
                        result = UNDETERMINED_STEP;
                    } else {
                        result = secondary.getNextStep();
                    }
                } else {
                    Wizard w = ownerOf (currStep);
                    if (w == base && w.canFinish() && idx == base.getAllSteps().length -1) {
                        checkForSecondary();
                        if (secondary != null) {
                            result = secondary.getAllSteps()[0];
                        } else {
                            result = UNDETERMINED_STEP;
                        }
                    } else {
                        result = w.getNextStep();
                    }
                }
            }
        }
        return result;
    }

    public final String getPreviousStep() {
        if (curr == secondary && secondary.getAllSteps() [0].equals(currStep)) {
            return base.getAllSteps()[base.getAllSteps().length-1];
        } else {
            return curr.getPreviousStep();
        }
    }

    public final String getProblem() {
        return curr.getProblem();
    }

    public final String getStepDescription(String id) {
        Wizard w = ownerOf (id);
        if (w == null) {
            return null;
        }
        return w.getStepDescription(id);
    }
    
    private Wizard ownerOf (String id) {
        if (UNDETERMINED_STEP.equals(id)) {
            checkForSecondary();
            return secondary;
        }
        if (Arrays.asList(base.getAllSteps()).contains(id)) {
            return base;
        } else {
            checkForSecondary();
            return secondary;
        }
    }

    public final String getTitle() {
        return curr.getTitle();
    }

    private Map settings;
    private String currStep = null;
    public final JComponent navigatingTo(String id, Map settings) {
        this.settings = settings;
        currStep = id;
        setCurrent (ownerOf (id));
        return curr.navigatingTo(id, settings);
    }

    private Set listeners = Collections.synchronizedSet (new HashSet());
    public final void removeWizardListener(WizardListener listener) {
        listeners.remove (listener);
    }

    public final void addWizardListener(WizardListener listener) {
        listeners.add (listener);
    }
    
    private void fireNavChanged() {
        checkForSecondary();
        WizardListener[] l = (WizardListener[]) listeners.toArray (
                new WizardListener[listeners.size()]);
        for (int i=0; i < l.length; i++) {
            l[i].navigabilityChanged (BranchingWizard.this);
        }
    }
    
    private void fireStepsChanged() {
        WizardListener[] l = (WizardListener[]) listeners.toArray (
                new WizardListener[listeners.size()]);
        for (int i=0; i < l.length; i++) {
            l[i].stepsChanged (BranchingWizard.this);
        }
    }
    
    private class WL implements WizardListener {
        public void stepsChanged(Wizard wizard) {
            fireStepsChanged();
        }
        
        public void navigabilityChanged(Wizard wizard) {
            fireNavChanged();
        }
    }
}
