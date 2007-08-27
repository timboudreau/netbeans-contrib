package com.sun.jbi.sapbc.sapwsdlgenerator.wizard;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.modules.compapp.projects.wizard.ProgressController;
import org.netbeans.modules.compapp.projects.wizard.ProgressDescriptor;
import org.netbeans.modules.compapp.projects.wizard.ProgressDialogFactory;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;

import com.sun.jbi.sapbc.sapwsdlgenerator.SAPConnectParams;
import com.sun.jbi.sapbc.sapwsdlgenerator.SAPObjectBrowser;

/**
 * First Wizard panel for the SAP BC Wizard.
 * Handles connection parameters.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class bapiwizardWizardPanel1
        implements WizardDescriptor.Panel,
                   PropertyChangeListener,
                   VetoableStep {
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (mComponent == null) {
            mComponent = mVisualPanel = new bapiwizardVisualPanel1();
            mVisualPanel.addPropertyChangeListener(
                    bapiwizardVisualPanel1.PROPERTY_INPUT_UPDATE,
                    this);
            mVisualPanel.addPropertyChangeListener(
                    bapiwizardVisualPanel1.PROPERTY_OBJECTTYPE_BAPI,
                    this);
            mVisualPanel.addPropertyChangeListener(
                    bapiwizardVisualPanel1.PROPERTY_OBJECTTYPE_RFC,
                    this);
            mVisualPanel.addPropertyChangeListener(
                    bapiwizardVisualPanel1.PROPERTY_TRACERFC,
                    this);
        }
        return mComponent;
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

    public boolean transpire() {
        final ProgressDescriptor progress;
        final ProgressController tracker;
        final Component view;
        final Thread browsing;
        
        // Progress display for SAP repository browsing process.
        progress = ProgressDialogFactory.createProgressDialog("Retrieving BAPI/RFC objects", true);
        tracker = progress.getController();
        view = progress.getGUIComponent();
        
        // The browsing process will be asynchronous, running off its own
        // thread -- keep in mind that this codepath is being executed in
        // the Swing dispatch thread, and I don't want to block that.
        browsing = new Thread(new Runnable() {
            public void run() {
                boolean browsed = false;
                
                tracker.start(3);
                
                // Fetch BAPIs, RFCs from the SAP Repository
                tracker.progress("Retrieving BAPI/RFC objects...", 1);
                mBrowser.setConnectionParams(mConnectInfo);
                mBrowser.setBrowseType(mObjectType);
                try{
                browsed = mBrowser.browse();
                } catch (Exception e){
                	
                }
                delay(2000);
                if (tracker.isCanceled()) {
                    tracker.dispose();
                    return;
                }
                if (!browsed) {
                    String reason = mBrowser.outcomeMessage();
                    tracker.progress(reason, 0);
                    tracker.cancel();
                }
                
                // Create list for the fetched objects
                tracker.progress("Creating list...", 2);
                delay(2000);
                if (tracker.isCanceled()) {
                    tracker.dispose();
                    return;
                }
                
                tracker.finish();
            }
        });
        
        // Start the browsing process.
        browsing.start();
        
        // Display progress display; browsing thread will update the display.
        view.setVisible(true);
        
        // Getting here means the browsing thread has closed the display.
        // Get the outcome of the browsing process from the tracker.
        return !tracker.isCanceled();
    }
    
    // TODO: remove when SAP BOR browsing is operational.
    private void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ;
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        
        // Only interested in specific properties...
        if (bapiwizardVisualPanel1.PROPERTY_INPUT_UPDATE.equals(property)) {
            fireChangeEvent();
        } else if (bapiwizardVisualPanel1.PROPERTY_TRACERFC.equals(property)) {
            mTraceRfc = ((Boolean) evt.getNewValue()).booleanValue();
            fireChangeEvent();
        } else if (bapiwizardVisualPanel1.PROPERTY_OBJECTTYPE_BAPI.equals(property)) {
            mObjectType = SAPObjectBrowser.ObjectType.BAPI;
            fireChangeEvent();
        } else if (bapiwizardVisualPanel1.PROPERTY_OBJECTTYPE_RFC.equals(property)) {
            mObjectType = SAPObjectBrowser.ObjectType.RFC;
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
        mVisualPanel.loadSettings(descriptor);
    }
    
    public void storeSettings(Object settings) {
        final WizardDescriptor descriptor = (WizardDescriptor) settings;
        
        // Push visual panel's data to the WizardDescriptor.
        mVisualPanel.storeSettings(descriptor);
        
        // Push this object's data to the WizardDescriptor.
        saveParams(descriptor);
        
        // Update this object with the visual panel's data
        // TAKEN FROM the WizardDescriptor.
        loadParams(descriptor);
    }

    private void loadParams(WizardDescriptor descriptor) {
        Boolean traceRfc = (Boolean) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_TRACERFC);
        mConnectInfo.setTraceRfc(traceRfc != null ? traceRfc : false);
        
        mConnectInfo.setClientNumber((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_CLIENTNUM));
        mConnectInfo.setUserName((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_USERNAME));
        mConnectInfo.setPassword ((char[]) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_PASSWORD));
        mConnectInfo.setSystemId((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_SYSTEMID));
        mConnectInfo.setSystemNumber((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_SYSTEMNUM));
        mConnectInfo.setServerName((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_SERVERNAME));
        mConnectInfo.setRouterString((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_ROUTERSTRING));
        mConnectInfo.setLanguage((String) descriptor.getProperty(
                bapiwizardVisualPanel1.PROPERTY_LANGUAGE));
    }
        
    private void saveParams(WizardDescriptor descriptor) {
        // Passing an object reference.
        // It's intentional that there is no corresponding attempt to
        // retrieve the browser object in loadParams.
        descriptor.putProperty(PROPERTY_BROWSER, mBrowser);
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component mComponent;
    private bapiwizardVisualPanel1 mVisualPanel;
    
    /**
     * Collection for listeners interested in changes in the panel's data.
     */
    private final Set<ChangeListener> mListeners = new HashSet<ChangeListener>(1);
    
    /**
     * SAP Object browser.
     */
    private final SAPObjectBrowser mBrowser = new SAPObjectBrowser();
    
    /**
     * Object Type to browse when connected to SAP.
     */
    private SAPObjectBrowser.ObjectType mObjectType = SAPObjectBrowser.ObjectType.BAPI;
    
    /**
     * Generate RFC trace information during connection and browsing phases.
     */
    private boolean mTraceRfc = false;
    
    /**
     * SAP connnection data.
     */
    private SAPConnectParams mConnectInfo = new SAPConnectParams();
    
    /**
     * Name of property indicating the browse data for the targetted SAP system.
     */
    public static final String PROPERTY_BROWSER =
            bapiwizardWizardPanel1.class.getName().concat("/browser");
 
    
}
