/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

