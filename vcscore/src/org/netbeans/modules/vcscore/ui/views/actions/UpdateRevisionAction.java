/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views.actions;

import org.netbeans.modules.vcscore.actions.*;
import org.openide.util.NbBundle;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.filesystems.*;
import org.netbeans.modules.vcscore.ui.views.*;
import java.util.*;

/**
 * 
 * @author  Milos Kleint
 */
public class UpdateRevisionAction extends FileVcsInfoAction {

    /** Creates new UpdateCommandAction */
    public UpdateRevisionAction() {
    }

    protected String iconResource() {
       return null; //NOI18N
    }
    
    public String getName() {
        return NbBundle.getMessage(UpdateRevisionAction.class, "LBL_UpdateRevisionAction"); //NOI18N
    }
     
}
