/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.panels;

import java.util.List;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.HashSet;

import org.netbeans.modules.corba.wizard.*;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.ORBBindingDescriptor;
import org.netbeans.modules.corba.settings.WizardSettings;
import org.netbeans.modules.corba.settings.WizardRequirement;

/**
 *
 * @author  Dusan Balek
 */
public class BindingDetailsPanel extends AbstractCORBAWizardPanel implements javax.swing.event.ChangeListener {
    
    private static final String STRING = "string";
    private static final String NAMING = "ns_code";
    private static final String FILE = "file_name";
    
    private CorbaWizardData wizardData;
    private ORBSettings orbSettings;
    private HashMap bindingDetails;
    private boolean maximizeReminder;
    
    /** Creates new form ProprietalPanel */
    public BindingDetailsPanel() {
        initComponents();
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(4));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_BindingMethodDetails"));
    }
    
    public void readCorbaSettings (CorbaWizardData data) {
        wizardData = data;
        orbSettings = data.getSettings().getActiveSetting ();
        bindingDetails = (HashMap)data.getBindingDetails();
        if (bindingDetails == null)
            bindingDetails = new HashMap();
        maximizeReminder = true;
        HashSet alreadyDisplayed = new HashSet();
        int mode = data.getGenerate ();
        if ((mode & CorbaWizardData.SERVER) == CorbaWizardData.SERVER) {
            ListIterator bindings = orbSettings.getServerBindings ().listIterator();
            ORBBindingDescriptor bd = null;
            while (bindings.hasNext()) {
                bd = (ORBBindingDescriptor)bindings.next();
                if (bd.getName().equals(data.getBindMethod())) {
                    WizardSettings ws = bd.getWizardSettings();
                    if (ws != null && ws.isSupported()) {
                        ListIterator wri = ws.getRequirements().listIterator();
                        while (wri.hasNext()) {
                            WizardRequirement wreq = (WizardRequirement)wri.next();
                            if (!alreadyDisplayed.contains(wreq.getValue())) {
                                alreadyDisplayed.add(wreq.getValue());
                                displayItem (wreq);
                            }
                        }
                    }
                }
            }
        }
        if ((mode & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT ||
            (mode & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) {
            ListIterator bindings = orbSettings.getClientBindings ().listIterator();
            ORBBindingDescriptor bd = null;
            while (bindings.hasNext()) {
                bd = (ORBBindingDescriptor)bindings.next();
                if (bd.getName().equals(data.getClientBindMethod())) {
                    WizardSettings ws = bd.getWizardSettings();
                    if (ws != null && ws.isSupported()) {
                        ListIterator wri = ws.getRequirements().listIterator();
                        while (wri.hasNext()) {
                            WizardRequirement wreq = (WizardRequirement)wri.next();
                            if (!alreadyDisplayed.contains(wreq.getValue())) {
                                alreadyDisplayed.add(wreq.getValue());
                                displayItem (wreq);
                            }
                        }
                    }
                }
            }
        }
        if (alreadyDisplayed.size() == 0)
            displayMessage();
        fillReminder();
    }
    
    public void storeCorbaSettings (CorbaWizardData data) {
        java.awt.Component[] details = this.getComponents();
        for (int i = 0; i < details.length; i++) {
            if (details[i] instanceof BindingDetail) {
                BindingDetail detail = (BindingDetail)details[i];
                detail.removeChangeListener(this);
                bindingDetails.put(detail.getValue(), detail.getData());
            }
        }
        removeAll();
        data.setBindingDetails(bindingDetails);
    }
    
    public boolean isValid () {
        java.awt.Component[] details = this.getComponents();
        for (int i = 0; i < details.length; i++) {
            if (details[i] instanceof BindingDetail && !((BindingDetail)details[i]).isValid())
                return false;
        }
        return true;
    }
    
    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());
        setPreferredSize(new java.awt.Dimension(500, 340));
    }
    
    private void displayItem(WizardRequirement wr) {
        String type = wr.getType();
        BindingDetail detail = null;
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        if (type.equals(STRING))
            detail = new StringPanel();
        else if (type.equals(FILE))
            detail = new FilePanel();
        else if (type.equals(NAMING)) {
            detail = new NSPanel((wizardData.getGenerate() & CorbaWizardData.SERVER) == CorbaWizardData.SERVER);
            gridBagConstraints.weighty = 1.0;
            maximizeReminder = false;
        }
        detail.setTitle(orbSettings.getLocalizedString(wr.getTitle()));
        detail.setValue(wr.getValue());
        detail.setData(bindingDetails.get(wr.getValue()));
        detail.addChangeListener(this);
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(detail, gridBagConstraints);
    }
    
    private void displayMessage() {
        javax.swing.JTextArea text = new javax.swing.JTextArea();
        text.setText(bundle.getString("TXT_NoBindingDetails"));
        text.setPreferredSize(new java.awt.Dimension(400, 50));
        text.setMinimumSize(new java.awt.Dimension(400, 50));
        text.setBackground ( this.getBackground ());
        text.setEditable (false);
        text.setLineWrap (true);
        text.setWrapStyleWord (true);
        text.setEditable (false);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(text, gridBagConstraints);
    }
    
    private void fillReminder() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        if (maximizeReminder)
            gridBagConstraints.weighty = 1.0;
        add(panel, gridBagConstraints);
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        fireChange(this);
    }
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
}
