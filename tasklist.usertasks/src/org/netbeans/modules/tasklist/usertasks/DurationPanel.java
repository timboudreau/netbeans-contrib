/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import java.beans.PropertyEditor;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.netbeans.modules.tasklist.usertasks.model.Duration;

/**
 * Panel for duration
 *
 * @author tl
 */
public class DurationPanel extends javax.swing.JPanel implements
ChangeListener {
    private static final long serialVersionUID = 1;

    private PropertyEditor pe;
    
    /**
     * Creates new form DurationPanel
     */
    public DurationPanel() {
        initComponents();
        
        SpinnerNumberModel snm = (SpinnerNumberModel) jSpinnerDays.getModel();
        snm.setMinimum(new Integer(0));
        snm.setMaximum(new Integer(1000));
        snm.addChangeListener(this);
        snm = (SpinnerNumberModel) jSpinnerHours.getModel();
        snm.setMinimum(new Integer(0));
        snm.setMaximum(new Integer(23));
        snm.addChangeListener(this);
        snm = (SpinnerNumberModel) jSpinnerMinutes.getModel();
        snm.setMinimum(new Integer(0));
        snm.setMaximum(new Integer(59));
        snm.addChangeListener(this);
        
        ((JSpinner.NumberEditor) jSpinnerDays.getEditor()).getTextField().setColumns(2);
        ((JSpinner.NumberEditor) jSpinnerHours.getEditor()).getTextField().setColumns(2);
        ((JSpinner.NumberEditor) jSpinnerMinutes.getEditor()).getTextField().setColumns(2);
        
        setOpaque(false);
        jPanelPlaceholder.setOpaque(false);
    }
    
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        jSpinnerDays.setEnabled(b);
        jSpinnerHours.setEnabled(b);
        jSpinnerMinutes.setEnabled(b);
    }    
    
    /**
     * Sets new property editor.
     *
     * @param pe a property editor or null
     */
    public void setPropertyEditor(PropertyEditor pe) {
        this.pe = pe;
        if (pe != null) {
            Integer v = (Integer) pe.getValue();
            setDuration(v == null ? 0 : v.intValue());
        }
    }
    
    /**
     * Sets the duration shown in this panel
     *
     * @param minutes new duration in minutes
     */
    public void setDuration(int minutes) {
        Duration d = new Duration(minutes,
            Settings.getDefault().getHoursPerDay(), 
            Integer.MAX_VALUE);
        
        jSpinnerDays.setValue(new Integer(d.days));
        jSpinnerHours.setValue(new Integer(d.hours));
        jSpinnerMinutes.setValue(new Integer(d.minutes));
    }
    
    /**
     * Returns choosed duration in minutes
     *
     * @return duration in minutes
     */
    public int getDuration() {
        int days = ((Integer) jSpinnerDays.getValue()).intValue();
        int hours = ((Integer) jSpinnerHours.getValue()).intValue();
        int minutes = ((Integer) jSpinnerMinutes.getValue()).intValue();
        
        return (days * Settings.getDefault().getHoursPerDay() + hours) * 60 + 
            minutes;
    }

    public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (pe != null) {
            pe.setValue(new Integer(getDuration()));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jSpinnerDays = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jSpinnerHours = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jSpinnerMinutes = new javax.swing.JSpinner();
        jPanelPlaceholder = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DurationPanel.class, "Days")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jLabel1, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jSpinnerDays, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DurationPanel.class, "Hours")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jLabel2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jSpinnerHours, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DurationPanel.class, "Minutes")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jLabel3, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    add(jSpinnerMinutes, gridBagConstraints);

    jPanelPlaceholder.setPreferredSize(new java.awt.Dimension(0, 0));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(jPanelPlaceholder, gridBagConstraints);

    }//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanelPlaceholder;
    private javax.swing.JSpinner jSpinnerDays;
    private javax.swing.JSpinner jSpinnerHours;
    private javax.swing.JSpinner jSpinnerMinutes;
    // End of variables declaration//GEN-END:variables
    
}
