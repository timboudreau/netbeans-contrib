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

import java.awt.Toolkit;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ShellOnPathsAction extends CookieAction {
  
    private static final Class<?>[] COOKIE_CLASSES = new Class<?>[] {
        Project.class,
        DataObject.class
    };

    public ShellOnPathsAction() {
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    protected Class<?>[] cookieClasses() {
        return COOKIE_CLASSES;
    }

    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        Set<FileObject> files = new LinkedHashSet<FileObject>();
        for (Node node:activatedNodes) {
            Lookup context = node.getLookup();
            Project project = (Project) context.lookup(Project.class);
            if (project != null) {
                files.add(project.getProjectDirectory());
            }
            DataObject dataObject = (DataObject) context.lookup(DataObject.class);
            if (dataObject != null) {
                FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null) {
                    files.add(fileObject);
                }
            }
        }

        if (files.size() == 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        for (FileObject fileObject : files) {
            String filePath = null;
            File file = FileUtil.toFile(fileObject);
            if (file != null) {                
                filePath = file.getAbsolutePath();
            }
            if (filePath != null && filePath.trim().length() > 0) {
                String commandFormat = fileObject.isFolder() ? 
                    Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                        PathtoolsOptionsPanelController.PROP_FOLDER_SHELL_COMMAND,
                        PathtoolsOptionsPanelController.getDEFAULT_FOLDER_SHELL_COMMAND())
                    : 
                    Preferences.userNodeForPackage(PathtoolsPanel.class).get(
                        PathtoolsOptionsPanelController.PROP_FILE_SHELL_COMMAND,
                        PathtoolsOptionsPanelController.getDEFAULT_FILE_SHELL_COMMAND());
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
    }

    public String getName() {
        return NbBundle.getMessage(EditPathsAction.class, "CTL_ShellOnPathsAction"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/pathtools/shellonpaths.gif"; // NOI18N
    }

    protected boolean asynchronous() {
        return false;
    }

    public HelpCtx getHelpCtx() {
        return null;
    }
}
