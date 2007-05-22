package com.sun.jbi.sapbc.sapwsdlgenerator.wizard;

import com.sun.jbi.sapbc.sapwsdlgenerator.SAPObjectBrowser;
import com.sun.jbi.sapbc.sapwsdlgenerator.SAPObjectTreeModelAdapter;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeModel;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * Second Wizard panel for the SAP BC Wizard.
 * Handles BAPI/RFC object selection.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class bapiwizardWizardPanel2
        implements WizardDescriptor.Panel,
                   PropertyChangeListener {
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = mVisualPanel = new bapiwizardVisualPanel2();
            mVisualPanel.addPropertyChangeListener(
                    bapiwizardVisualPanel2.PROPERTY_INPUT_UPDATE,
                    this);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    
    public boolean isValid() {
        return mVisualPanel.ready();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        
        // Only interested in specific properties...
        if (bapiwizardVisualPanel2.PROPERTY_INPUT_UPDATE.equals(property)) {
            fireChangeEvent();
        }
    }
        
    public final void addChangeListener(ChangeListener l) {
        synchronized (mListeners) {
            mListeners.add(l);
        }
    }
    
    public final void removeChangeListener(ChangeListener l) {
        synchronized (mListeners) {
            mListeners.remove(l);
        }
    }
    
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (mListeners) {
            it = new HashSet<ChangeListener>(mListeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        final WizardDescriptor descriptor = (WizardDescriptor) settings;
        
        SAPObjectBrowser browser =
                (SAPObjectBrowser) descriptor.getProperty(
                    bapiwizardWizardPanel1.PROPERTY_BROWSER);
        
        TreeModel model = new SAPObjectTreeModelAdapter(browser.iterator());
        mVisualPanel.loadSettings(model);
    }
    
    public void storeSettings(Object settings) {
        final WizardDescriptor descriptor = (WizardDescriptor) settings;
        descriptor.putProperty(PROPERTY_SELECTED_BUSOBJ, mVisualPanel.getSelectionPath());
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private bapiwizardVisualPanel2 mVisualPanel;
    
    private final Set<ChangeListener> mListeners = new HashSet<ChangeListener>(1);
    
    /**
     * Name of property indicating the BAPI/RFC selected from the browsed SAP
     * object catalog.
     */
    private static final String PROPERTY_SELECTED_BUSOBJ =
            bapiwizardWizardPanel2.class.getName().concat("/selected_busobj");
}
