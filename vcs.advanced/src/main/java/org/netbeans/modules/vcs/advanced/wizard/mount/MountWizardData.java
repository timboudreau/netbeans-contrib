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

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.util.VariableInputValidator;
import org.openide.util.NbBundle;

/**
 * The data set in the Generic VCS wizard.
 *
 * @author  Martin Entlicher
 */
public class MountWizardData {
    
    private String workingDir;
    private String[] mountPoints;
    private int refreshRate;
    
    private CommandLineVcsFileSystem fileSystem;
    private VcsCustomizer customizer;

    /** Creates new MountWizardData */
    public MountWizardData(Object instance) {
        if (instance instanceof CommandLineVcsFileSystem) {
            this.fileSystem = (CommandLineVcsFileSystem) instance;
            if (fileSystem.getConfig() == null) {
                GeneralVcsSettings gvs = (GeneralVcsSettings) GeneralVcsSettings.findObject(GeneralVcsSettings.class, true);
                String profile = gvs.getDefaultProfile();
                if (profile != null) {
                    if (fileSystem.readConfiguration(profile)) {
                        fileSystem.setConfigFileName(profile);
                    }
                }
                
            }
            this.customizer = new VcsCustomizer();
            customizer.setResetEqualFSVars(true);
            customizer.setUseWizardDescriptors(true);
            customizer.setRapidVariablesAssignment(true);
            customizer.setObject(fileSystem);
        } else throw new IllegalArgumentException("Bad instance "+instance);
    }
    
    javax.swing.JPanel getProfilePanel(int index) {
        return (index == 0) ? customizer.getConfigPanel()
                            : customizer.getAdditionalConfigPanels()[index - 1];
    }
    
    String getProfileLabel(int index) {
        return customizer.getConfigPanelName(index);
    }

    javax.swing.JPanel getAdvancedPanel() {
        return customizer.getAdvancedPanel();
    }

    javax.swing.JPanel getEnvironmentPanel() {
        return customizer.getEnvironmentPanel();
    }
    
    boolean isNoneProfileSelected() {
        return customizer.isNoneProfileSelected();
    }
    
    VcsCustomizer getCustomizer(){
        return customizer;
    }
    
    void addPropertyChangeListener(java.beans.PropertyChangeListener l){       
        customizer.addPropertyChangeListener(l);
        fileSystem.addPropertyChangeListener(l);
    }
    
    void removePropertyChangeListener(java.beans.PropertyChangeListener l) {    
        customizer.removePropertyChangeListener(l);
        fileSystem.removePropertyChangeListener(l);
    }
    
    CommandLineVcsFileSystem getFileSystem() {
        return fileSystem;
    }

    /** Getter for property workingDir.
     * @return Value of property workingDir.
     */
    public String getWorkingDir() {
        return workingDir;
    }
    
    /** Setter for property workingDir.
     * @param workingDir New value of property workingDir.
     */
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
    
    public String[] getMountPoints () {
        return this.mountPoints;
    }
    
    public void setMountPoints (String[] mountPoints) {
        this.mountPoints = mountPoints;
    }
    
    /** Getter for property refreshRate.
     * @return Value of property refreshRate.
     */
    public int getRefreshRate() {
        return refreshRate;
    }
    
    /** Setter for property refreshRate.
     * @param refreshRate New value of property refreshRate.
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }
    
    VariableInputValidator validateData() {
        return new MountValidator(fileSystem.getRootDirectory());
    }
    
    private static class MountValidator extends VariableInputValidator {
        
        public MountValidator(java.io.File rootDir) {
            super(null, "");
            //this.fileSystem = fileSystem;
            try {
                rootDir = rootDir.getCanonicalFile();
            } catch (java.io.IOException ioex) {}
            boolean valid = true;
            FSInfo[] fsInfos = FSRegistry.getDefault().getRegistered();
            for (int i = 0; i < fsInfos.length; i++) {
                if (rootDir.equals(fsInfos[i].getFSRoot())) {
                    valid = false;
                    break;
                }
            }
            setValid(valid);
            if (!valid) {
                setMessage(NbBundle.getMessage(MountWizardData.class, "MSG_DirAlreadyMounted", rootDir));
                setVariable("ROOTDIR");
            }
        }
        
    }
    
}
