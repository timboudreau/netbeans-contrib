package org.netbeans.modules.themebuilder.wizard;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public final class ThemeBuilderProjectWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {
    
    private WizardDescriptor wizardDescriptor;
    private ThemeBuilderProjectPanelVisual component;
    
    /** Creates a new instance of templateWizardPanel */
    public ThemeBuilderProjectWizardPanel() {
    }
    
    /**
     * 
     * @return 
     */
    public Component getComponent() {
        if (component == null) {
            component = new ThemeBuilderProjectPanelVisual(this);
            component.setName(NbBundle.getMessage(ThemeBuilderProjectWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    /**
     * 
     * @return 
     */
    public HelpCtx getHelp() {
        return new HelpCtx(ThemeBuilderProjectWizardPanel.class);
    }
    
    /**
     * 
     * @return 
     */
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    /**
     * 
     * @param l 
     */
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    /**
     * 
     * @param l 
     */
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    /**
     * 
     */
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    /**
     * 
     * @param settings 
     */
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }
    
    /**
     * 
     * @param settings 
     */
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }
    
    /**
     * 
     * @return 
     */
    public boolean isFinishPanel() {
        return true;
    }
    
    /**
     * 
     * @throws org.openide.WizardValidationException 
     */
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
    
}
