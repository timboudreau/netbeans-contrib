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
 * SimpleWizard.java
 *
 * Created on February 22, 2005, 2:33 PM
 */

package org.netbeans.spi.wizard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.wizard.Wizard.WizardListener;

/**
 * A simple implementation of Wizard for use in wizards which have a 
 * straightforward set of steps with no branching.  To use, implement the
 * simplified interface SimpleWizard.Info and pass that to the constructor.
 *
 * @see SimpleWizard.Info
 * @author Tim Boudreau
 */
final class SimpleWizard implements Wizard {
    final SimpleWizardInfo info;
    private final List listeners = new ArrayList(1);
    private final Map ids2panels = new HashMap();

    private String currID = null;
    
    public SimpleWizard (WizardPanelProvider prov) {
        this (new SimpleWizardInfo (prov));
    }
    
    /** Creates a new instance of SimpleWizard */
    public SimpleWizard(SimpleWizardInfo info) {
        this.info = info;
        info.setWizard (this);
    }

    public void addWizardListener(WizardListener listener) {
        listeners.add (listener);
    }
    
    public void removeWizardListener(WizardListener listener) {
        listeners.remove (listener);
    }    

    public boolean canFinish() {
        return info.canFinish() || 
            (info.isValid() && currentIndex() == info.getSteps().length - 1);
    }

    public String[] getAllSteps() {
        String[] result = new String[info.getSteps().length];
        //Defensive copy
        System.arraycopy(info.getSteps(), 0, result, 0, info.getSteps().length);
        return result;
    }

    public String getStepDescription(String id) {
        int idx = Arrays.asList(info.getSteps()).indexOf (id);
        if (idx == -1) {
            throw new IllegalArgumentException ("Undefined id: " + id);
        }
        return info.getDescriptions()[idx];
    }
    
    public JComponent navigatingTo(String id, Map settings) {
//        assert SwingUtilities.isEventDispatchThread();
        assert Arrays.asList (info.getSteps()).contains(id);
        JComponent result = (JComponent) ids2panels.get(id);
        currID = id;
        if (result == null) {
            result = info.createPanel(id, settings);
            ids2panels.put (id, result);
        } else {
            info.update();
            info.recycleExistingPanel(id, settings, result);
        }
        return result;
    }

    public String getNextStep() {
        if (!info.isValid()) {
            return null;
        }
            
        int idx = currentIndex();
        if (idx < info.getSteps().length - 1) {
            return info.getSteps() [idx + 1];
        } else {
            return null;
        }
    }

    public String getPreviousStep() {
        int idx = currentIndex();
        if (idx < info.getSteps().length && idx > 0) {
            return info.getSteps() [idx - 1];
        } else {
            return null;
        }
    }
    
    int currentIndex() {
        int idx = 0;
        if (currID != null) {
            idx = Arrays.asList(info.getSteps()).indexOf (currID);
        }
        return idx;
    }
    
    void fireNavigability() {
        for (Iterator i=listeners.iterator(); i.hasNext();) {
            WizardListener l = (WizardListener) i.next();
            l.navigabilityChanged(this);
        }
    }
    
    public Object finish(Map settings) throws WizardException {
        return info.finish(settings);
    }
    
    public String getTitle() {
        return info.getTitle();
    }
    
    public String getProblem() {
        return info.getProblem();
    }
    
    public int hashCode() {
        return info.hashCode() ^ 17;
    }
    
    public boolean equals (Object o) {
        if (o instanceof SimpleWizard) {
            return ((SimpleWizard) o).info.equals (info);
        } else {
            return false;
        }
    }
    
    public String toString() {
        return "SimpleWizard for " + info;
    }
}
