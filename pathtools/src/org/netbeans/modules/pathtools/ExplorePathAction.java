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

package org.netbeans.modules.pathtools;

import java.io.File;
import java.text.MessageFormat;
import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ExplorePathAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        FileObject fileObject = null;
        DataObject dataObject = (DataObject) activatedNodes[0].getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            fileObject = dataObject.getPrimaryFile();
        } else {
            Project project = (Project) activatedNodes[0].getLookup().lookup(Project.class);
            if (project != null) {
                fileObject = project.getProjectDirectory();
            }
        }
        
        // is this a physical file on the disk
        if (fileObject != null && FileUtil.toFile(fileObject) != null) {
            String commandFormat = fileObject.isFolder() ? 
                Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                    PathtoolsOptionsPanelController.PROP_FOLDER_EXPLORE_COMMAND,
                    PathtoolsOptionsPanelController.getDEFAULT_FOLDER_EXPLORE_COMMAND())
                : 
                Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                    PathtoolsOptionsPanelController.PROP_FILE_EXPLORE_COMMAND,
                    PathtoolsOptionsPanelController.getDEFAULT_FILE_EXPLORE_COMMAND());
            String command = MessageFormat.format(CommandLauncher.convertParameters(commandFormat),
                    new Object[] {
                fileObject.getPath().replace('/', File.separatorChar).replace('\\', File.separatorChar),
                fileObject.getParent().getPath().replace('/', File.separatorChar).replace('\\', File.separatorChar),
                fileObject.getPath().replace('\\', '/'),
                fileObject.getParent().getPath().replace('\\', '/'),
                fileObject.getPath().replace('/', '\\'),
                fileObject.getParent().getPath().replace('/', '\\'),
            });
            CommandLauncher.launch(command);
        }    
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ExplorePathAction.class, "CTL_ExplorePathAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class,
            Project.class
        };
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/pathtools/explore.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}

