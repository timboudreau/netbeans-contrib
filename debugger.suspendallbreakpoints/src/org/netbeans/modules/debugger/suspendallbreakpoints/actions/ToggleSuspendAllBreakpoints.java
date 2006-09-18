/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Micro//S ystems, Inc. Portions Copyright 1997-2001 Sun
 * Micro//S ystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.suspendallbreakpoints.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ToggleSuspendAllBreakpoints extends BooleanStateAction implements PropertyChangeListener {    
    public ToggleSuspendAllBreakpoints() {
        addPropertyChangeListener(this);
    }
    
    protected void initialize() {
        super.initialize();
        setBooleanState(false);
    }
    
    public void setBooleanState(boolean value) {
        super.setBooleanState(value);
        putValue(NAME, getName());
        putValue(SMALL_ICON, null);
        putValue(SMALL_ICON, getIcon());
        
        TopComponent view = WindowManager.getDefault().findTopComponent("breakpointsView");       
        if (view != null) {
            String breakpointsViewTitle = view.getName();
            if (breakpointsViewTitle != null) {
                if (getBooleanState()) {
                    view.setDisplayName(breakpointsViewTitle + NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Suspended"));
                } else {
                    view.setDisplayName(breakpointsViewTitle);
                }
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
            BreakpointsStateMementoManager.getDefault().setAllSuspended(getBooleanState());
        }
    }

    public boolean isEnabled() {
        return BreakpointsStateMementoManager.getDefault() != null;
    }    
    
    public String getName() {
        if (getBooleanState()) {
            return NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_ResumeAllBreakpoints");
        } else {
            return NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_SuspendAllBreakpoints");
        }
    }
    
    protected String iconResource() {
        if (getBooleanState()) {
            return "org/netbeans/modules/debugger/suspendallbreakpoints/actions/resumeallbreakpoints.gif";
        } else {
            return "org/netbeans/modules/debugger/suspendallbreakpoints/actions/suspendallbreakpoints.gif";
        }
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
