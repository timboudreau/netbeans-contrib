/*
 * OptionPanel.java
 *
 * Created on November 7, 2005, 1:08 PM
 */

package org.netbeans.modules.importcruncher;

import java.awt.Color;
import javax.swing.JPanel;
import org.openide.util.NbBundle;

/**
 * GUI presentation of preferences.
 * @author Jesse Glick
 */
final class OptionPanel extends JPanel {
    
    public OptionPanel() {
        initComponents();
        // XXX #68242: why did Hanz make everything white?
        breakup.setBackground(Color.white);
        eliminateFqns.setBackground(Color.white);
        eliminateWildcards.setBackground(Color.white);
        importNestedClasses.setBackground(Color.white);
        sort.setBackground(Color.white);
    }
    
    private void initComponents() {//GEN-BEGIN:initComponents
        breakup = new javax.swing.JCheckBox();
        eliminateFqns = new javax.swing.JCheckBox();
        sort = new javax.swing.JCheckBox();
        importNestedClasses = new javax.swing.JCheckBox();
        eliminateWildcards = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(breakup, NbBundle.getMessage(OptionPanel.class, "breakup"));
        breakup.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        breakup.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(eliminateFqns, NbBundle.getMessage(OptionPanel.class, "eliminateFqns"));
        eliminateFqns.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        eliminateFqns.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(sort, NbBundle.getMessage(OptionPanel.class, "sort"));
        sort.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sort.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(importNestedClasses, NbBundle.getMessage(OptionPanel.class, "importNestedClasses"));
        importNestedClasses.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        importNestedClasses.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(eliminateWildcards, NbBundle.getMessage(OptionPanel.class, "eliminateWildcards"));
        eliminateWildcards.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        eliminateWildcards.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(breakup)
                    .add(eliminateFqns)
                    .add(sort)
                    .add(importNestedClasses)
                    .add(eliminateWildcards))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(breakup)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(eliminateFqns)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sort)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importNestedClasses)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(eliminateWildcards)
                .addContainerGap(189, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox breakup;
    public javax.swing.JCheckBox eliminateFqns;
    public javax.swing.JCheckBox eliminateWildcards;
    public javax.swing.JCheckBox importNestedClasses;
    public javax.swing.JCheckBox sort;
    // End of variables declaration//GEN-END:variables
    
}
