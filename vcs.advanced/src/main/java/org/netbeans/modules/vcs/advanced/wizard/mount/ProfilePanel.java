/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcs.advanced.wizard.mount;

import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcscore.util.VariableInputValidator;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 *
 * @author  Martin Entlicher
 */
public class ProfilePanel extends AbstractWizardPanel {

    private int index;
    private ProfilePanelUI panelUI;
    private boolean initialized = false;
    private boolean isFinish = true;
    private java.beans.PropertyChangeListener propL;

    MountWizardData data;
    
    private static final long serialVersionUID = 1184058637535734526L;
    
    /** Creates new form ProfilePanel */
    public ProfilePanel(int index) {
        this.index = index;
    }


    public org.openide.util.HelpCtx getHelp() {
        return null;
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
            propL = new java.beans.PropertyChangeListener() {                
                public void propertyChange(java.beans.PropertyChangeEvent evt) {                    
                    if(evt.getPropertyName().equals(VcsCustomizer.PROP_PROFILE_SELECTION_CHANGED)){
                        fireChange();
                    }
                }
            };
            data.addPropertyChangeListener(propL);
        }
    }

    protected void storeWizardSettings(MountWizardData data) {
        if (propL != null) {
            data.removePropertyChangeListener(propL);
            propL = null;
        }
    }
    
    public boolean isValid() {
        if (data.isNoneProfileSelected()) {
            getWizard().putProperty("WizardPanel_errorMessage", NbBundle.getMessage(ProfilePanel.class, "MSG_NoProfileSelected")); //NOI18N
            return false;
        }
        VariableInputValidator validator = data.validateData();
        if (validator.isValid()) {
            validator = data.getCustomizer().validateConfigPanel(index);
        }
        if (validator != null && !validator.isValid()) {
            getWizard().putProperty("WizardPanel_errorMessage", validator.getMessage()); //NOI18N
            return false;
        } else {
            getWizard().putProperty("WizardPanel_errorMessage", ""); //NOI18N
            return true;
        }
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
