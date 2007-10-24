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
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.wizard.InstructionsPanel;
import org.netbeans.spi.wizard.*;
import org.netbeans.modules.wizard.MergeMap;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
/**
 *
 * @author Tim Boudreau
 */
class WrapperIterator implements WizardDescriptor.InstantiatingIterator, WizardObserver {
    
    static class ProgressWrapperIterator extends WrapperIterator implements WizardDescriptor.ProgressInstantiatingIterator {
        ProgressWrapperIterator (Wizard w, Set <String> vpanels, Set <String> asynchVpanels) {
            super (w, vpanels, asynchVpanels);
        }
    }
    
    static class AsynchWrapperIterator extends WrapperIterator implements WizardDescriptor.AsynchronousInstantiatingIterator {
        AsynchWrapperIterator (Wizard w, Set <String> vpanels, Set <String> asynchVpanels) {
            super (w, vpanels, asynchVpanels);
        }
    }
    
    private final Wizard wizard;
    private MergeMap settings;
    private final Map <String, WrapperPanel> 
            steps2panels = new HashMap <String, WrapperPanel> ();
    private int currStep = 0;
    private WizardDescriptor descriptor;
    private final Set <String> vpanels;
    private final Set <String> asynchVpanels;
            
    WrapperIterator(Wizard wizard, Set <String> vpanels, Set <String> asynchVpanels) {
        assert nonBuggyWizard (wizard);
        this.wizard = wizard;
        this.vpanels = vpanels;
        this.asynchVpanels = asynchVpanels;
        wizard.addWizardObserver(this);
    }
    
    public Set instantiate() throws IOException {
        return finishWithSet();
    }
    
    static String[] createSteps(Wizard wizard) {
        String[] stepIds = wizard.getAllSteps();
        String[] result = new String[stepIds.length];
        for (int i = 0; i < result.length; i++) {
            if (stepIds[i].equals(Wizard.UNDETERMINED_STEP)) {
                result[i] = "..."; //XXX I18N
            } else {
                result[i] = wizard.getStepDescription(stepIds[i]);
            }
        }
        return result;
    }
    
    public void initialize(WizardDescriptor desc) {
        this.descriptor = desc;
        Map m = fetchLegacySettingsMap(desc);
        if (settings == null) {
            String first = wizard.getAllSteps()[0];
            settings = new MergeMap (first, m);
        }
        descriptor.putProperty("WizardPanel_errorMessage", wizard.getProblem()); //NOI18N
        descriptor.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); //NOI18N
        String title = wizard.getTitle();
        if (desc instanceof TemplateWizard) {
            DataFolder targetFolder = null;
            try {
                targetFolder = ((TemplateWizard)desc).getTargetFolder();
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
            String targetName = ((TemplateWizard)desc).getTargetName();
            DataObject template = ((TemplateWizard)desc).getTemplate();
            DataFolder templateFolder = ((TemplateWizard)desc).getTemplatesFolder();
            if (targetName != null) {
                m.put (WizardFactory.KEY_TARGET_NAME, targetName);
            }
            if (targetFolder != null) {
                m.put (WizardFactory.KEY_TARGET_FOLDER, targetFolder.getPrimaryFile());
            }
            if (template != null) {
                m.put (WizardFactory.KEY_TEMPLATE, template.getPrimaryFile());
            }
            if (templateFolder != null) {
                m.put (WizardFactory.KEY_TEMPLATE_FOLDER, 
                        templateFolder.getPrimaryFile());
            }
        }
        if (title != null) {
            desc.setTitle(title);
        }
    }
    
    private static final Map fetchLegacySettingsMap (WizardDescriptor d) {
        //Look up the original settings by reflection.  We could do this
        //by wrapping WizardDescriptor.get/setProperty() in an implementation
        //of Map, but we can't iterate keys that way
        try {
            Field f = WizardDescriptor.class.getDeclaredField("properties"); //NOI18N
            f.setAccessible(true);
            Map m = (Map) f.get(d);
            return m;
        } catch (ClassCastException ex) {
            java.util.logging.Logger.getLogger("global").log( //NOI18N
                    java.util.logging.Level.SEVERE,
                     ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger("global").log( //NOI18N
                    java.util.logging.Level.SEVERE,
                     ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger("global").log( //NOI18N
                    java.util.logging.Level.SEVERE,
                     ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            java.util.logging.Logger.getLogger("global").log( //NOI18N
                    java.util.logging.Level.SEVERE,
                     ex.getMessage(), ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger("global").log( //NOI18N
                    java.util.logging.Level.SEVERE,
                    ex.getMessage(), ex);
        };
        return new HashMap();
    }
    
    public void uninitialize(WizardDescriptor wizard) {
        //do nothing for now
    }
    
    private WrapperPanel  createPanelForStep (String stepId) {
        WrapperPanel result;
        if (asynchVpanels.contains (stepId)) {
            result = new WrapperPanel.AsynchValidatingWrapperPanel (wizard, 
                    stepId, settings);
        } else if (vpanels.contains(stepId)) {
            result = new WrapperPanel.ValidatingWrapperPanel (wizard, stepId, 
                    settings);
        } else {
            result = new WrapperPanel (wizard, stepId, settings);
        }
        return result;
    }
    
    public Panel current() {
        String stepId = wizard.getCurrentStep();    
        WrapperPanel result = steps2panels.get (stepId);
        if (stepId == null) {
            //maybe just 0?
            stepId = wizard.getAllSteps()[currStep];
        }
        if (result == null) {
            result = createPanelForStep (stepId);
            steps2panels.put (stepId, result);
        }
        return result;
    }
    
    public String name() {
        return wizard.getTitle();
    }
    
    public boolean hasNext() {
        if (wizard.isBusy()) {
            return false;
        }
        String id = wizard.getNextStep();
        boolean result = id != null;
        if (result) {
            result = !Wizard.UNDETERMINED_STEP.equals(id);
        }
        return result;
    }
    
    public boolean hasPrevious() {
        if (wizard.isBusy()) {
            return false;
        }
        return wizard.getPreviousStep() != null;
    }
    
    public void nextPanel() {
        currStep++;
        String next = wizard.getNextStep();
        wizard.navigatingTo(next, settings);
        WrapperPanel wp = (WrapperPanel) current();
        wp.forwardInto(settings, settings);
    }
    
    public void previousPanel() {
        currStep--;
        if (currStep < 0) {
            currStep = 0;
            throw new IllegalStateException ("Current step cannot be < 0");
        }
        String prev = wizard.getPreviousStep();
        wizard.navigatingTo(prev, settings);
        WrapperPanel wp = (WrapperPanel) current();
        wp.backInto(settings, settings);
    }
    
    private boolean firing = false;
    void fire() {
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
    
    Set finishWithSet() throws IOException{
        Object result = finish();
        if (result instanceof Summary) {
            Summary summary = (Summary) result;
            result = summary.getResult();
        }
        if (result instanceof Set) {
            return (Set) result;
        } else {
            return Collections.singleton(result);
        }
    }
    
    Object finish() throws IOException {
        try {
            Object result = wizard.finish(settings);
            return result;
        }
        catch (WizardException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove (l);
    }

    public void stepsChanged(Wizard w) {
        fire();
    }

    public void navigabilityChanged(Wizard w) {
        descriptor.putProperty("WizardPanel_errorMessage", wizard.getProblem()); //NOI18N
        fire();
    }

    public void selectionChanged(Wizard w) {
        fire();
    }
    
    public Set instantiate(ProgressHandle handle) throws IOException {
        Object result = finish();
        if (result instanceof DeferredWizardResult) {
            DeferredWizardResult def = (DeferredWizardResult) result;
            WrapperHandle wh;
            def.start(settings, wh = new WrapperHandle (handle));
            result = wh.result;
            //XXX handle summary
            if (result instanceof Set) {
                return (Set) result;
            } else {
                return Collections.singleton(result);
            }
        } else {
            handle.start();
            handle.finish();
            if (result instanceof Set) {
                return (Set) result;
            } else {
                return Collections.singleton (result);
            }
        }
    }
    
    private static final class WrapperHandle implements ResultProgressHandle {
        private final ProgressHandle h;
        private boolean started = false;
        WrapperHandle (ProgressHandle h) {
            this.h = h;
        }
        
        private boolean indeterminate = false;
        private void setTotal(int val) {
            if (val != -1 && indeterminate) {
                if (!started) {
                    h.start (val);
                    started = true;
                }
                indeterminate = false;
            } else if (val == -1 && !indeterminate) {
                indeterminate = true;
                h.switchToIndeterminate();
                if (!started) {
                    h.start();
                    started = true;
                }
            }
        }
        
        public void setProgress(int step, int total) {
            setTotal (total);
            h.progress(step);
        }

        public void setProgress(String msg, int step, int total) {
            setTotal (total);
            h.progress(msg, total);
        }

        Object result;
        public void finished(Object o) {
            result = o;
            if (started) {
                h.finish();
            } else {
                h.start();;
                h.finish();
            }
            started = false;
        }

        public void failed(String msg, boolean foo) {
            h.setDisplayName(msg);
            if (!started) {
                h.start();
                h.finish();
            } else {
                h.finish();
            }
            started = false;
        }
        
        public boolean isRunning() {
            return started;
        }
    
        public void setBusy(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addProgressComponents(InstructionsPanel arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static boolean nonBuggyWizard (Wizard wizard) {
        String[] s = wizard.getAllSteps();
        if (s.length == 0) {
            return false;
        }
        assert new HashSet <String> (Arrays.<String>asList(s)).size() == s.length;
        if (s.length == 1 && Wizard.UNDETERMINED_STEP.equals(s[0])) {
            assert false : "If a wizard has only one step id, it may not " + //NOI18N
                    "be UNDETERMINED_STEP"; //NOI18N //NOI18N
        }
        for (int i=0; i < s.length; i++) {
            if (Wizard.UNDETERMINED_STEP.equals(s[i]) && i != s.length - 1) {
               assert false :  "UNDETERMINED_ID may only be last element in" + //NOI18N
                       " ids array " + Arrays.asList(s); //NOI18N
            }
        }
        return true;
    }
    
}
