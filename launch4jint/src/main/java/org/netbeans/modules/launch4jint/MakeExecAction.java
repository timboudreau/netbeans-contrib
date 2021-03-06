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


package org.netbeans.modules.launch4jint;

import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.Action;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/** Action which is sensitive to currently selected project,
 * configures and runs Launch4j through ant task
 */
public final class MakeExecAction implements ProjectActionPerformer {

    public static final Action createAction () {
        return ProjectSensitiveActions.projectSensitiveAction(
                new MakeExecAction(), 
                NbBundle.getBundle(MakeExecAction.class).getString("CTL_MakeExecAction"), 
                null);
    }

    /** Instantiated only through createAction */
    private MakeExecAction () {
    }
    
    public boolean enable (Project project) {
        return project != null;
    }

    public void perform (Project project) {
        ConfigHandler ch = new ConfigHandler(project);
        ProjectInformation pi = ProjectUtils.getInformation(project);
        if (!ch.isTypicalProject()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                MessageFormat.format(NbBundle.getBundle(MakeExecAction.class).
                    getString("FMT_NotSupported"), 
                    new Object[] { pi.getDisplayName() })
            );
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        
        FileObject projJar = ch.getProjectJar();
        // XXX - try to call build project first 
        if (projJar == null) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                NbBundle.getBundle(MakeExecAction.class).getString("MSG_NotFound")
            );
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        
        FileObject script = ch.prepareAllConfig();
        if (script == null) {
            return;
        }
        
        try {
            ActionUtils.runTarget(script, null, null);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}

