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
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which will blink all tabs which share the same project as the 
 * current editor tab.
 *
 * @author Tim Boudreau
 */
public class CloseUnrelatedAction extends WhichProjectAction {
    protected boolean processTc (TopComponent tc, boolean val) {
        if (!val) {
            tc.close();
        }
        return val;
    }

    public String getName () {
        return NbBundle.getMessage ( WhichProjectAction.class, "LBL_CloseAction" ); //NOI18N
    }
}

