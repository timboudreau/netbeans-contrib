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

package org.netbeans.modules.vcs.advanced.wizard.mount;

import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.openide.loaders.TemplateWizard;

/**
 *
 * @author  Martin Entlicher
 */
public class ProfilePanel extends AbstractWizardPanel {

    private int index;
    private ProfilePanelUI panelUI;
    private boolean initialized = false;
    private boolean isFinish = true;
    
    MountWizardData data;
    
    private static final long serialVersionUID = 1184058637535734526L;
    
    /** Creates new form ProfilePanel */
    public ProfilePanel(int index) {
        this.index = index;
    }


    public org.openide.util.HelpCtx getHelp() {
        return new org.openide.util.HelpCtx(ProfilePanel.class);
    }

    protected void readWizardSettings(MountWizardData data) {
        this.data = data;
        getPanelUI().putClientProperty ("WizardPanel_contentSelectedIndex", new Integer (index)); // NOI18N
        if (!initialized) {
            javax.swing.JPanel profilePanel = data.getProfilePanel (index);
            profilePanel.setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets (0, 0, 0, 0)));
            getPanelUI().add (profilePanel);
            initialized = true;
        }
        if (index == 0 && data.isNoneProfileSelected()) {            
            data.addPropertyChangeListener(new java.beans.PropertyChangeListener() {                
                public void propertyChange(java.beans.PropertyChangeEvent evt) {                    
                    if(evt.getPropertyName().equals(VcsCustomizer.PROP_PROFILE_SELECTION_CHANGED)){
                        fireChange();
                    }
                }
            });
        }
    }

    protected void storeWizardSettings(MountWizardData data) {
    }
    
    public boolean isValid() {
        return index > 0 || !data.isNoneProfileSelected();
    }
    
    
    /** Get the component displayed in this panel.
     *
     * Note; method can be called from any thread, but not concurrently
     * with other methods of this interface.
     *
     * @return the UI component of this wizard panel
     *
     */
    public java.awt.Component getComponent(){
        return getPanelUI();
    }
    
    private javax.swing.JPanel getPanelUI(){        
        if(panelUI == null)
            panelUI = new ProfilePanelUI(index, data);
        return panelUI;
    }
    
    public boolean isFinishPanel() {               
        return isFinish;
    }
    
    public void setFinish(boolean isFinish){
        this.isFinish = isFinish;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
