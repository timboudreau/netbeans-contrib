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

    /**
     * GeneralCommandAction asks the supporter during the enabled(Node[] nodes) method.
     * The supporter should return if the specified fileobjects are supported for the specified action.
     * @param action The action that initiated this request.
     * @param fileObjects Array of fileObjects extracted from the Activated nodes.
     */
    public abstract boolean isEnabled(GeneralCommandAction action, FileObject[] fileObjects);
    
    /**
     * GeneralCommandAction tells the supporter during the performsAction(Node[] nodes) method to perform the action on the specified FileObjects.
     * @param action The action that initiated this request.
     * @param fileObjects Array of fileObjects extracted from the Activated nodes.
     */
    
    public abstract void performAction(GeneralCommandAction action, FileObject[] fileObjects);
    
    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the toolbar tooltip. (use with caution.) <B>Experimental</B>
     */
    public String getToolBarDisplayName(GeneralCommandAction action) {
        return action.getName();
    }

    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the menu name. (use with caution.) <B>Experimental</B>
     */
    
    public String getMenuDisplayName(GeneralCommandAction action) {
        return action.getName();
    }

    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the popup menu name. (use with caution.) <B>Experimental</B>
     */
    
    public String getPopupDisplayName(GeneralCommandAction action) {
        return action.getName();
    }
    
    
}
