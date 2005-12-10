/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.whichproject;

import java.awt.Toolkit;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which will blink all tabs which share the same project as the 
 * current editor tab.
 *
 * @author Tim Boudreau
 */
public class WhichProjectAction extends CallableSystemAction {
    
    public void performAction () {
        Mode editorMode = WindowManager.getDefault().findMode("editor");
        boolean didSomething = false;
        if (editorMode != null) {
            TopComponent selTC = editorMode.getSelectedTopComponent();
            if (selTC != null) {
                Project project = projectFor(selTC);
                if (project != null) {
                    TopComponent[] tc = editorMode.getTopComponents();
                    for (int i=0; i < tc.length; i++) {
                        Project p = projectFor(tc[i]);
                        didSomething |= processTc(tc[i], project.equals(p));
                    }
                }
            }
        }
        if (!didSomething) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    protected boolean processTc (TopComponent tc, boolean val) {
        if (val) {
            tc.requestAttention(true);
        }
        return val;
    }

    public String getName () {
        return NbBundle.getMessage ( WhichProjectAction.class, "LBL_Action" ); //NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean asynchronous () {
        return false;
    }
    
    private Project projectFor (TopComponent tc) {
        Node[] nodes = tc.getActivatedNodes();
        if (nodes == null) {
            return null;
        }
        DataObject obj = null;
        for (int i=0; i < nodes.length; i++) {
            obj = (DataObject) nodes[i].getCookie(DataObject.class);
            if (obj != null) {
                FileObject file = obj.getPrimaryFile();
                if (obj != null) {
                    Project p = FileOwnerQuery.getOwner(file);
                    if (p != null) {
                        return p;
                    }
                }
            }
        }
        return null;
    }
}

