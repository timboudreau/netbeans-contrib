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
package org.netbeans.modules.websvc.axis2.actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.websvc.axis2.AxisUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;
import org.openide.util.NbPreferences;
import org.openide.util.actions.NodeAction;

public class AxisConfigurationAction extends NodeAction  {
    
    public String getName() {
        return NbBundle.getMessage(AxisConfigurationAction.class, "LBL_AxisConfigAction");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
        
    @Override
    protected boolean asynchronous() {
        return true;
    }
    
    protected boolean enable(Node[] activatedNodes) {
        return activatedNodes!=null && activatedNodes.length == 1;
    }
    
    protected void performAction(Node[] activatedNodes) {
        Preferences preferences = NbPreferences.forModule(AxisConfigurationAction.class);
        String oldAxisHome = preferences.get("AXIS_HOME",""); //NOI18N
        String oldAxisDeploy = preferences.get("AXIS_DEPLOY",""); //NOI18N
        ConfigurationPanel configPanel = new ConfigurationPanel(oldAxisHome, oldAxisDeploy);
        DialogDescriptor dialog = new DialogDescriptor(configPanel, "Configuration");
        DialogDisplayer.getDefault().notify(dialog);   
        String axisHome = configPanel.getAxisHome();
        if (!axisHome.equals(oldAxisHome)) {
            preferences.put("AXIS_HOME", axisHome);
        }
        String axisDeploy = configPanel.getAxisDeploy();
        if (!axisDeploy.equals(oldAxisDeploy)) {
            preferences.put("AXIS_DEPLOY", axisDeploy);
        }
        
        Project prj = activatedNodes[0].getLookup().lookup(Project.class);
        
        // adding Axis libraries to project
        File file = new File(configPanel.getAxisHome());
        if (file.exists()) {
            try {
                
                
                // PENDING : Need to add all jars
                final URL[] roots = new URL[2];
                File f = new File(file,"lib/axis2-saaj-1.3.jar");
                if (f.exists()) roots[0] = FileUtil.getArchiveRoot(f.toURL());
                f = new File(file,"lib/axis2-saaj-api-1.3.jar");
                if (f.exists()) roots[1] = FileUtil.getArchiveRoot(f.toURL());
                
                final SourceGroup[] srcGroup = ProjectUtils.getSources(prj).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                ProjectManager.mutex().writeAccess(new Runnable(){

                    public void run() {
                        try {
                            ProjectClassPathModifier.addRoots(roots, srcGroup[0].getRootFolder(), ClassPath.COMPILE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                });
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            EditableProperties ep = AxisUtils.getEditableProperties(prj, AntProjectHelper.PRIVATE_PROPERTIES_PATH);
            if (ep != null) {
                ep.setProperty("axis2.home",axisHome); //NOI18N
                if (axisDeploy.length() > 0)
                    ep.setProperty("axis2.deploy",axisDeploy); //NOI18N
            }
            AxisUtils.storeEditableProperties(prj, AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        
    }
}

