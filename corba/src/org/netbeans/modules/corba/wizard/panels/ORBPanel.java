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


import java.util.*;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import org.netbeans.modules.corba.wizard.CorbaWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBBindingDescriptor;
import org.netbeans.modules.corba.settings.WizardSettings;
import org.netbeans.modules.corba.settings.WizardRequirement;
import org.netbeans.modules.corba.settings.ORBSettings;
//import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  tzezula
 * @version
 */

public class ORBPanel extends AbstractCORBAWizardPanel {
    
    
    
    private boolean initialized;
    private CORBASupportSettings css;
    private CorbaWizardData data;
    
    /** Creates new form ORBPanel */
    public ORBPanel() {
        this.initialized = false;
        this.css = null;
        initComponents ();
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(2));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_ImplementationsBindings"));
    }
    
    
    
    /** Sets the panel data from CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void readCorbaSettings (CorbaWizardData data){
        if (!initialized){
            this.data = data;
            this.css = (CORBASupportSettings) data.getSettings();
            
            Vector names = this.css.getNames();
            for (int i=0; i< names.size(); i++){
                this.orbs.addItem (names.elementAt(i));
            }

            List list = this.css.getActiveSetting ().getServerBindings ();
            this.bindings.setRenderer(new LocalizedRenderer(this.css.getActiveSetting()));
            if (list != null) {
                ListIterator li = list.listIterator();
                while (li.hasNext()) {
                    ORBBindingDescriptor bd = (ORBBindingDescriptor)li.next();
                    WizardSettings ws = bd.getWizardSettings();
                    if (ws != null && ws.isSupported())
                        this.bindings.addItem (bd.getName());
                }
            }
            
            String value = this.css.getActiveSetting ().getName ();
            
            if (value != null){
                this.orbs.setSelectedItem (value);
                data.setDefaultOrbValue (value);
            }
            value = this.css.getActiveSetting ().getClientBinding().getValue ();
            data.setDefaultClientBindingValue (value);
            value = this.css.getActiveSetting ().getServerBinding().getValue ();
            data.setDefaultServerBindingValue (value);
            this.orbs.addActionListener ( new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    ORBPanel.this.orbChanged (event);
                }
            });
            this.bindings.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    ORBPanel.this.bindingChanged (event);
                }
            });
            this.bindings.setSelectedItem (data.getDefaultServerBindingValue());
            this.initialized = true;
        }
        
        Object value = data.getCORBAImpl();
        if (value != null){
            this.orbs.setSelectedItem (value);
        }
        value = data.getBindMethod ();
        if (value != null){
            this.bindings.setSelectedItem (value);
        }
        
        int mask = data.getGenerate(); 
        this.bindings.setEnabled (((mask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER)
                                     || ((mask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT)
                                     || ((mask & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT));
    }
    
    
    
    /** Sets the settings from panel to CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    
    public void storeCorbaSettings (CorbaWizardData data){
        data.setCORBAImpl((String)this.orbs.getSelectedItem());
        data.setBindMethod((String)this.bindings.getSelectedItem());
    }
    
    
    
    public boolean isValid () {
        return true;
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        orbs = new javax.swing.JComboBox();
        bindings = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize(new java.awt.Dimension(500, 340));
        
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        jLabel1.setText(bundle.getString("TXT_OrbImplementations"));
        jLabel1.setLabelFor(orbs);
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new java.awt.Insets(12, 12, 6, 6);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel1, gridBagConstraints2);
        
        
        jLabel2.setText(bundle.getString("TXT_BindingMethod"));
        jLabel2.setLabelFor(bindings);
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.insets = new java.awt.Insets(6, 12, 12, 6);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel2, gridBagConstraints2);
        
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(12, 6, 6, 12);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        jPanel1.add(orbs, gridBagConstraints2);
        
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(6, 6, 12, 12);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(bindings, gridBagConstraints2);
        
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        add(jPanel1, gridBagConstraints1);
        
        
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.gridheight = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jPanel3, gridBagConstraints1);
        
    }//GEN-END:initComponents
      
  
  
    private void bindingChanged (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bindingChanged
        // Add your handling code here:
        
        String serverBind = (String)this.bindings.getSelectedItem();
        if (serverBind == null)
            return;   //dirty hack
        this.css.getActiveSetting ().setServerBindingFromString (serverBind);
        String clientBind = CorbaWizardData.getClientBindMethod(serverBind);
        this.css.getActiveSetting ().setClientBindingFromString (clientBind);
        
    }//GEN-LAST:event_bindingChanged
    
    
    
    private void orbChanged (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orbChanged
        // Add your handling code here:
        
        if (this.data != null && this.data.getDefaultClientBindingValue() != null)
            this.css.getActiveSetting ().setClientBindingFromString (this.data.getDefaultClientBindingValue());
        if (this.data != null && this.data.getDefaultServerBindingValue() != null)
            this.css.getActiveSetting ().setServerBindingFromString (this.data.getDefaultServerBindingValue());
        this.css.setOrb ((String) this.orbs.getSelectedItem ());
        if (this.data != null){
            this.data.setDefaultClientBindingValue(this.css.getActiveSetting().getClientBinding().getValue());
            this.data.setDefaultServerBindingValue(this.css.getActiveSetting().getServerBinding().getValue());
        }
        List list = this.css.getActiveSetting ().getServerBindings ();
        this.bindings.removeAllItems();
        this.bindings.setRenderer(new LocalizedRenderer(this.css.getActiveSetting()));
        if (list != null) {
            ListIterator li = list.listIterator();
            while (li.hasNext()) {
                ORBBindingDescriptor bd = (ORBBindingDescriptor)li.next();
                WizardSettings ws = bd.getWizardSettings();
                if (ws != null && ws.isSupported())
                    this.bindings.addItem (bd.getName());
            }
        }
    }//GEN-LAST:event_orbChanged
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox orbs;
    private javax.swing.JComboBox bindings;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    
    
    
    
}

class LocalizedRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer {
    
    protected ORBSettings _os = null;
    
    LocalizedRenderer(ORBSettings os) {
        _os = os;
    };
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (_os != null)
            setText(_os.getLocalizedString(getText()));
        return this;
    }

}

