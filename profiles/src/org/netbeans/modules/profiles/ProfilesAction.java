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

package org.netbeans.modules.profiles;


import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;


/** Opens Profiles Manager dialog.
 *
 * @author Jaroslav Tulach
 */
public class ProfilesAction extends CallableSystemAction {

    public ProfilesAction() {
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    

    public org.openide.util.HelpCtx getHelpCtx() {
        return org.openide.util.HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return org.openide.util.NbBundle.getMessage (ProfilesAction.class, "ACT_ProfilesAction");
    }

    public void performAction() {
        ProfilesManager.showProfilesManager();
    }

    protected boolean asynchronous () {
        return false;
    }
}

