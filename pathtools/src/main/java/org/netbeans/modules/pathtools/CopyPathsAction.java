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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class CopyPathsAction extends CookieAction {
    private static Clipboard clipboard;
    static {
        clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
    }
   
    private static final Class<?>[] COOKIE_CLASSES = new Class<?>[] {
        Project.class,
        DataObject.class
    };

    public CopyPathsAction() {
    }

    protected int mode() {
        return MODE_ANY;
    }

    protected Class<?>[] cookieClasses() {
        return COOKIE_CLASSES;
    }

    protected void performAction(Node[] activatedNodes) {
        // no clipboard available - this is unlikely to happen
        if (clipboard == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, NbBundle.getMessage(CopyPathsAction.class, "MSG_NoClipboard")); // NOI18N
            Toolkit.getDefaultToolkit().beep();
            return;
        }

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

        StringBuilder stringBuilder = new StringBuilder();
        for (FileObject fileObject : files) {
            String filePath = null;
            File file = FileUtil.toFile(fileObject);
            if (file == null) {
                 filePath = fileObject.getPath();
            } else {
                filePath = file.getAbsolutePath();
            }
            if (filePath != null && filePath.trim().length() > 0) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(System.getProperty("line.separator")); // NOI18N
                }
                stringBuilder.append(filePath);
            }
        }

        if (stringBuilder.length() == 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        StringSelection pathSelection = new StringSelection(stringBuilder.toString());
        clipboard.setContents(pathSelection, null);
    }

    public String getName() {
        return NbBundle.getMessage(CopyPathsAction.class, "CTL_CopyPathsAction"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/pathtools/copypaths.gif"; // NOI18N
    }

    protected boolean asynchronous() {
        return false;
    }

    public HelpCtx getHelpCtx() {
        return null;
    }
}
