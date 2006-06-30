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

package org.netbeans.modules.vcscore.actions;

import org.openide.filesystems.FileObject;

/**
 *  OBject that needs to be return as an attribute of all fileobjects that
 * support the Abstract/GeneralCommandAction framework and all the actions that subclass these two classes.
 * The name of the attribute is in AbstractCommandAction.VCS_ACTION_ATTRIBUTE.
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
     * This is called from GeneralCommandAction in AWT thread.
     * RequestProcessor needs to be used to leave the AWT thread if necessary.
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
