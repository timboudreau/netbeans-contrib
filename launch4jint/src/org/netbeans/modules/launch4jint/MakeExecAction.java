/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

