package org.netbeans.modules.scala.project.ui.wizards;

import javax.swing.JPanel;
import org.openide.WizardDescriptor;

/**
 * Basic visual panel for Scala wizard panels.
 *
 * @author Martin Krauskopf
 */
public abstract class BasicVisualPanel extends JPanel {
    
    private final WizardDescriptor settings;
    
    protected BasicVisualPanel(final WizardDescriptor settings) {
        this.settings = settings;
    }
    
    public final WizardDescriptor getSettings() {
        return settings;
    }
    
    protected void storeData() { /* default implementation does nothing */ }
    
    /**
     * Set an error message and mark the panel as invalid.
     */
    protected final void setError(String message) {
        if (message == null) {
            throw new IllegalArgumentException("\"message\" argument cannot be null"); // NOI18N
        }
        setMessage(message);
        setValid(false);
    }
    
    /**
     * Set an warning message but mark the panel as valid.
     */
    protected final void setWarning(String message) {
        if (message == null) {
            throw new IllegalArgumentException("\"message\" argument cannot be null"); // NOI18N
        }
        setMessage(message);
        setValid(true);
    }
    
    /**
     * Mark the panel as invalid without any message.
     * Use with restraint; generally {@link #setError} is better.
     */
    protected final void markInvalid() {
        setMessage(null);
        setValid(false);
    }
    
    /**
     * Mark the panel as valid and clear any error or warning message.
     */
    protected final void markValid() {
        setMessage(null);
        setValid(true);
    }
    
    private final void setMessage(String message) {
        settings.putProperty("WizardPanel_errorMessage", message); // NOI18N
    }
    
    /**
     * Sets this panel's validity and fires event to it's wrapper wizard panel.
     * See {@link BasicWizardPanel#propertyChange} for what happens further.
     */
    private final void setValid(boolean valid) {
        firePropertyChange("valid", null, Boolean.valueOf(valid)); // NOI18N
    }
    
}
