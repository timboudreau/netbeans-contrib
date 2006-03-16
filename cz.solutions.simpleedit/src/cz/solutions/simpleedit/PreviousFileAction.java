/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Simple Edit Module.
 * The Initial Developer of the Original Code is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
 * 
 * Contributor(s): David Strupl.
 */
package cz.solutions.simpleedit;

import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * @author David Strupl
 */
public final class PreviousFileAction extends CallableSystemAction {
    
    public String getName() {
        return NbBundle.getMessage(PreviousFileAction.class, "CTL_PreviousFileAction");
    }
    
    protected String iconResource() {
        return "cz/solutions/simpleedit/back.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public boolean isEnabled() {
        return FilesHistory.getDefault().canNavigateBack();
    }
    
    public void performAction() {
        DataObject dobj = FilesHistory.getDefault().navigateBack();
        OpenCookie oc = (OpenCookie)dobj.getCookie(OpenCookie.class);
        if (oc != null) {
            oc.open();
        }
    }
    
    public void fire() {
        firePropertyChange(PROP_ENABLED, null, null);
    }
}
