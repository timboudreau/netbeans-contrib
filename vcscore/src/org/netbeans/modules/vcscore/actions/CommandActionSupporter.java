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

package org.netbeans.modules.vcscore.actions;

import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint
 */
public abstract class CommandActionSupporter {

    /** Creates new CommandActionSupporter */
    public CommandActionSupporter() {
    }

    public abstract boolean isEnabled(GeneralCommandAction action, FileObject[] fileObjects);
    
    public abstract void performAction(GeneralCommandAction action, FileObject[] fileObjects);
    
    public String getToolBarDisplayName(GeneralCommandAction action) {
        return action.getName();
    }
    
    public String getMenuDisplayName(GeneralCommandAction action) {
        return action.getName();
    }
    
    public String getPopupDisplayName(GeneralCommandAction action) {
        return action.getName();
    }
    
    
}
